/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import game.MoveNotifier;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.Socket;
import utilities.SocketException;
import utilities.messages.Message;
import utilities.messages.ReturnRoomList;
import utilities.messages.RoomCreate;
import utilities.messages.ServerShutdown;
import utilities.messages.data.RoomInfo;
import utilities.messages.move.Move;
/**
 *
 * @author n1t4chi
 */
public class LobbyServer extends Server implements MoveNotifier{
    /**
     * Executor for threads.
     */
    private ScheduledExecutorService ses;

    /**
     * List of rooms.
     */
    private final List<RoomServer> room_list;
    /**
     * List of lobby clients
     */
    private final List<Socket> lobby_clients;
    
    
    /**
     * Server socket.
     */
    private ServerSocket socket;
        /**
     * Configuration object.
     */
    private final ServerConfig config;
    /**
     * Configuration filename suffix
     */
    private final String config_suffix;
    
    /**
     * Scheduled Future of thread which connects with other hosts.
     */
    private ScheduledFuture host_connector;

    
    /**
     * Returns server config
     * @return server config
     */
    public final ServerConfig getConfig() {
        return config;
    }
    /**
     * Waits for new host to connect and creates Socket for him. After new Socket is setup it repeats the steps.
     */
    private void connectWithNewHost(){
        int f_ups=0;
        while((socket!=null)&&(!socket.isClosed())){
            try{
                Socket soc = new Socket(this,socket.accept(), 50);
                lobby_clients.add(soc);
                f_ups=0;
            }catch(Exception ex){//all exception since better safe and not working after few tries that 'working' with broken scheduler.
                f_ups++;
                printErr("Failed to connect with host. "+(f_ups)+". fail out of 10\n ERROR:"+ex);
                //ex.printStackTrace();
                if(f_ups>9){
                    printErr("Trying to restart server");
                    for(;f_ups<13;f_ups++){
                        try{
                            restart();
                            f_ups=0;
                            break;
                        }catch(Exception ex1){
                            printErr("Cannot restart server "+(f_ups-10)+". fail out of 3\n ERROR:"+ex);
                            if(f_ups>12){
                                printErr("Closing server");
                                try {
                                    dispose();
                                } catch (Exception ex2) {
                                    printErr("Cannot close server.");
                                    System.exit(-1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    /**
     * Returns list of rooms.
     * @return list of rooms.
     */
    public synchronized ArrayList<RoomInfo> getRoomList(){
        ArrayList<RoomInfo> list = new ArrayList<>();
        room_list.stream().forEach((rs) -> {
            RoomInfo ri =rs.getRoomInfo();
            if(ri!=null){
                list.add(ri);
            }
        });
        return list;
    }

    
    /**
     * Default constructor.
     * @param config_suffix config filename suffix
     */
    public LobbyServer(String config_suffix){
        super("SYSTEM[Lobby]","ERROR[Lobby]");
        config = ServerConfig.getDefaultServerConfig();
        this.config_suffix = config_suffix;
        config.load(config_suffix);
        room_list = Collections.synchronizedList(new ArrayList<>());
        lobby_clients = Collections.synchronizedList(new ArrayList<>());
    }
    
    
    @Override
    protected void initSubclass() throws IOException{ 
        if(!isWorking()){
            ses = new ScheduledThreadPoolExecutor(2);
            room_list.clear();
            socket = new ServerSocket(getConfig().getServerPort());
            host_connector = ses.schedule(()->{
                connectWithNewHost();
            }, 1, TimeUnit.MILLISECONDS);
        }
    }
    @Override
    protected void disposeSubclass() throws Exception{
        notifySlaveObserversAboutMessage(new ServerShutdown());
        for (Socket cl : lobby_clients) {
            cl.Disconnect();
        }
        
        try{
            Thread.sleep(1000);
        }catch(InterruptedException ex){
            printErr("Will not send message to  Room Servers that Lobby is shutting down.");
            for (RoomServer roomServer : room_list) {
                roomServer.dispose();
            }
        }
        if(isWorking()){
            room_list.clear();
            if(host_connector!=null)
                host_connector.cancel(true);
            host_connector=null;
            ses.shutdownNow();
            room_list.stream().filter((rs) -> (rs!=null)).forEach((rs) -> {
                try {
                    rs.dispose();
                } catch (Exception ex) {
                    printErr("Couldn't dispose room");
                }
            });
            room_list.clear();
            if(socket!=null)
                try{
                    socket.close();
                }catch(IOException ex){
                    printErr("Problem while closing socket: "+ex.getLocalizedMessage());
                }
            socket = null;
        }
        config.save(config_suffix);
    }

    @Override
    protected void restartSubclass() throws IOException{}

    @Override
    public void interpret(String command, String... parameters) {}

    @Override
    public void interpret(String input) {}

    @Override
    public boolean isWorking() {
        return  socket!=null&&
                host_connector!=null&&
                !host_connector.isDone()&&
                !socket.isClosed();
    }


    

    @Override
    public synchronized void notifySlaveObserversAboutMessage(Message msg) {
        try{
            RoomServer[] copy = room_list.toArray(new RoomServer[room_list.size()]);
            for(RoomServer rs : copy){
                if(rs!=null)
                    rs.updateMessage(this, msg);
            }
        }catch(java.util.ConcurrentModificationException ex){
            
        }
     /*   lobby_clients.stream().forEach((lc) -> {
            if(lc!=null)
                lc.updateMessage(this, msg);
        });*/
    }


    @Override
    public void updateMessage(MoveNotifier src, Message msg) {
        //printErr("got message from "+src+" msg:"+msg);
        if(msg!=null){
            if(src instanceof Socket){ //Lobby clients
                switch(msg.getMessageType()){
                    case SOCKET_CLOSE_ERROR:
                    case SOCKET_INPUT_ERROR:
                    case SOCKET_OUTPUT_ERROR:
                    case SERVER_SHUTDOWN:
                            printErr("Client disconnected.");
                            try {
                                ((Socket) src).stopIO();
                                ((Socket) src).Disconnect();
                                lobby_clients.remove((Socket)src);
                            } catch (SocketException ex) {
                                printErr("Exception while disconnecting from Lobby client");
                            }
                        break;
                    case REQUEST_LIST_ROOM:
                            ((Socket) src).updateMessage(this,new ReturnRoomList(getRoomList()));
                        break;
                    case CREATE_ROOM:
                            if(msg instanceof RoomCreate){
                                RoomServer rs=null;
                                try{
                                    rs = new RoomServer(this,
                                            ((RoomCreate) msg).getSize(),
                                            ((RoomCreate) msg).isBlackAI(),
                                            ((RoomCreate) msg).isWhiteAI(),
                                            (((((RoomCreate) msg).getRoomName()).trim().isEmpty())?
                                                ("ROOM #"+room_list.size()):
                                                ((RoomCreate) msg).getRoomName()
                                            )
                                    );
                                    printSys("Created room:"+rs.getRoomInfo());
                                    room_list.add(rs);
                                }catch(Exception ex){
                                    printErr("Couldn't create room:"+ex);
                                    if(rs!=null){
                                        try{
                                            rs.dispose();
                                        }catch(Exception ex1){
                                            printErr("Error while disposing room:"+ex);
                                        }
                                        room_list.remove(rs);
                                    }
                                }
                            }else{
                                printErr("Received illegal instance for RoomCreate type:"+msg);
                            }
                        break;
                }  
            }else if(src instanceof RoomServer){ //Room servers
                switch(msg.getMessageType()){
                    case SOCKET_CLOSE_ERROR:
                    case SOCKET_INPUT_ERROR:
                    case SOCKET_OUTPUT_ERROR:
                            printErr("Server Room connection failed.");
                    case SERVER_SHUTDOWN:
                           /* try {
                                ((RoomServer) src).dispose();
                            } catch (Exception ex) {
                                printErr("Exception while disposing Room Server");
                                //System.exit(-1);
                            }*/
                            printSys("Game in room "+src+" ends. Room Server will be disposed soon.");
                            room_list.remove((RoomServer)src);
                        break;
                    default:
                            printErr("Received illegal message type ["+msg.getMessageType()+"] from RoomServer");
                        break;
                }  
            }else{
                printErr("Received message from illegal source:"+src);
            }
        }else{
            printErr("Received null message from:"+src);
        }
    }
    
    


    @Override
    /**
     * Does nothing
     */
    public void notifyMasterObserverAboutMessage(Message msg) {}
    @Override
    /**
     * Does nothing
     */
    public void notifySlaveObserversAboutMove(Move move) {}
    @Override
    /**
     * Does nothing
     */
    public void updateMove(MoveNotifier src, Move move) {}
    @Override
    /**
     * Does nothing
     */
    public void setMasterObserver(MoveNotifier observer) {}

    @Override
    /**
     * Does nothing
     */
    public void addSlaveObserver(MoveNotifier observer) {}

    @Override
    /**
     * Does nothing
     */
    public void notifyMasterObserverAboutMove(Move move) {}
}
