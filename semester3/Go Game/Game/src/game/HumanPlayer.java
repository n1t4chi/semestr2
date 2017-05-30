/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.playerstate.PlayerState;
import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.game.PlayerGameState;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import utilities.Socket;
import utilities.messages.Message;
import utilities.messages.PlayerConnected;
import utilities.messages.PlayerDisconnected;
import utilities.messages.RequestBoard;
import utilities.messages.ReturnOpponentName;
import utilities.messages.client.RequestGameState;
import utilities.messages.client.RequestPing;
import utilities.messages.client.ReturnGameState;
import utilities.messages.move.ChooseTerritories;
import utilities.messages.move.MakeMove;
import utilities.messages.move.MessageMoveType;
import utilities.messages.move.Move;

/**
 * Human player class used for game communication with connected host.
 * Uses Update() method to get notified about new messages on socket and receives messages to send from RoomServer through updateMove/Message()
 * @author Kingaa
 */
public class HumanPlayer extends Player {
    /**
     * Socket for communication with paired player.
     */
    private Socket socket;
    
    /**
     * Closes socket and nullifies it. New object must be created in order to set up new player.
     */
    public synchronized void close(){
        if(socket!=null)
            socket.Disconnect();
        socket = null;
        if(remainder_future!=null)
            remainder_future.cancel(true);
        remainder_future=null;
        if(executor!=null)
            executor.shutdown();
        executor = null;
    }
    ScheduledExecutorService executor = null;
    /**
     * Default constructor
     * @param master owner of this component
     * @param name Name of player
     * @param socket socket
     * @param state State pointer
     */
    public HumanPlayer(MoveNotifier master, String name, Socket socket,PlayerState state){
        super(master, name,state);
        this.socket=socket;
        socket.setMasterObserver(this);
        addSlaveObserver(socket);
        
        //System.out.println("M["+this.master+"]"+" S ["+this.slave+"]");
        //this.slave= socket;
        
        
        
        executor = Executors.newScheduledThreadPool(2);
        remainder_future = executor.scheduleWithFixedDelay(REMAINDER,1,1, TimeUnit.SECONDS);
        //notifyMasterObserverAboutMessage(new RequestBoard());
        //notifyMasterObserverAboutMessage(new RequestGameState());
    }
    
    ScheduledFuture remainder_future  = null;
    private volatile int remainder_counter = 0;
    private PlayerGameState.GameState prev_state = null;
    private final Runnable REMAINDER = () -> {
        notifySlaveObserversAboutMessage(new RequestPing());
        if(getPlayerGameState()!=prev_state){
            prev_state = getPlayerGameState();
            remainder_counter=0;
        }
        switch(getPlayerGameState()){
            case PLACE:
                    remainder_counter++;
                    if(remainder_counter>2*60){
                        remainder_counter=0;
                        notifySlaveObserversAboutMove(new MakeMove());
                    }
                break;
            case CHOOSE:
                    remainder_counter++;
                    if(remainder_counter>5*60){
                        remainder_counter=0;
                        notifySlaveObserversAboutMove(new ChooseTerritories());
                    }
                break;
            default:
                remainder_counter=0;
                break;
        }
    };
    

    @Override
    public void updateMove_Subclass(MoveNotifier src, Move move) {
        if(move!=null){
            if(src == getMaster() ){
                notifySlaveObserversAboutMove(move);
                if(move.getMoveType()==MessageMoveType.INVALID_MOVE){
                    notifyMasterObserverAboutMessage(new RequestBoard());
                    notifyMasterObserverAboutMessage(new RequestGameState());
                }
            }else if(src == socket){
                notifyMasterObserverAboutMove(move);
            }else{
                System.err.println("Received move from illegal source: "+src);
            }
        }else{
            System.err.println("Received NULL move from"+src);
        }
    }

    @Override
    public void updateMessage(MoveNotifier src, Message msg) {
        if(msg!=null){
            switch(msg.getMessageType()){
                case SERVER_SHUTDOWN:
                case SOCKET_CLOSE_ERROR:
                case SOCKET_INPUT_ERROR:
                case SOCKET_OUTPUT_ERROR:
                        notifyMasterObserverAboutMessage(new PlayerDisconnected());
                        try{
                            Thread.sleep(10);
                        }catch(InterruptedException ex){
                            System.err.println("Human player cannot wait for RoomServer to be notified about disconnected player");
                        }
                        this.setPlayerConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED);
                        close();
                    break;
                
                //case PLAYER_CONNECTED:
                /*case SOCKET_INPUT_ERROR:
                case SOCKET_OUTPUT_ERROR:    
                        if(!checkConnection()){//5* -1 ping means the connection is dead for sure.
                            this.setPlayerConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED);
                            notifyMasterObserverAboutMessage(new PlayerDisconnected());
                        }
                    break;*/
                /*case RETURN_GAME_STATE:
                        notifySlaveObserversAboutMessage(new ReturnGameState(getPlayerGameState()));
                    break;*/
                case RETURN_PING:
                    break;
                default:
                        if(src == getMaster() )
                            notifySlaveObserversAboutMessage(msg);
                        else if(src == socket){
                            if(msg instanceof ReturnOpponentName){
                                String new_name = ((ReturnOpponentName) msg).getName();
                                Name = (new_name.trim().isEmpty())?("Player#"+(new Random()).nextInt()):new_name;
                                if(Name.equalsIgnoreCase(new_name))
                                    notifyMasterObserverAboutMessage(msg);
                                else{    
                                    if(msg instanceof PlayerConnected){
                                        notifyMasterObserverAboutMessage(new PlayerConnected(Name));
                                    }else{
                                        notifyMasterObserverAboutMessage(new ReturnOpponentName(Name));
                                    }
                                }
                            }else    
                                notifyMasterObserverAboutMessage(msg);
                        }else{
                            System.err.println("Received message from illegal source: "+src);
                        }
                     break;
            }
        }else{
            System.err.println("Received NULL message from"+src);
        }
    }
    
    
    /**
     * method used for receiving messages.
     */
  /*  private Message receive(MessageType type){
        Message msg=null;
        if(socket!=null)
            try{
                msg = socket.receive(10000, type);
            }catch(SocketException ex){
                if(!checkConnection()){
                    System.err.println("Exception on receiving:"+ex);
                    if(!checkConnection()){//5* -1 ping means the connection is dead for sure.
                        this.setPlayerConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED);
                        notifyMasterObserverAboutMessage(new PlayerDisconnected());
                    }
                }else{
                    return this.receive(type);
                }
            }
        return msg;
    }*/
    
    /**
     * Returns whether there is even slight chance of connection with player.
     * @return true if there is, false otherwise
     */
    private boolean checkConnection(){
        int err=0;
        if(socket!=null){
            for(int i=0;i<5;i++)
                err+=socket.checkPing();
        }else
            err = -5;
        
        return err>-5;
    }
    

    @Override
    public void notifySlaveObserversAboutMove(Move move) {
        if(socket!=null)
            socket.updateMove(this, move);
        else{
            notifyMasterObserverAboutMessage(new PlayerDisconnected());
            System.err.println("Socket is NULL");
        }    
    }

    @Override
    public void notifySlaveObserversAboutMessage(Message msg) {
        if(socket!=null)
            socket.updateMessage(this, msg);
        else{
            notifyMasterObserverAboutMessage(new PlayerDisconnected());
            System.err.println("Socket is NULL");
        }
    }


}
