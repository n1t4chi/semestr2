/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import game.MoveNotifier;
import utilities.messages.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import utilities.messages.MessageType;
import utilities.messages.Ping;
import utilities.messages.Pong;
import utilities.messages.ServerShutdown;
import utilities.messages.SocketError;
import utilities.messages.client.RequestPing;
import utilities.messages.client.ReturnPing;
import utilities.messages.move.Move;


/**
 * Abstract socket class that allows for communication with connected host. 
 * This class is used by both client and server.
 * On server side disconnect and reconnect shouldn't be called since it will fail to create new connection.
 * 
 * Known BUG:
 * ObjectInputStream creation might fail once in thousand times thus making Socket throw exception.
 * Did not find good solution to this problem so It is advisable to just connect again. 
 * For security reasons Socket tries to connect only twice but Server or client could try few more times.
 * 
 * 
 * @author n1t4chi
 */
public class Socket implements MoveNotifier, Observer{  
    MoveNotifier master = null;
    /**
     * 
     * Socket that is used for communication.
     */
    private volatile java.net.Socket socket;
    /**
     * Whether this socket is server or client type.
     */
    private final boolean isServer;
    /**
     * IP field for client.
     */
    private String ip;
    /**
     * Port field for client.
     */
    private int port;
    /**
     * Socket input writer.
     */
    private SocketWriter writer;
    /**
     * Socket output reader.
     */
    private SocketReader reader;
    /**
     * Interval between each successful socket reads and writes.
     */
    private int interval;
    
    /**
     * Executor of this field.
     */
    ScheduledExecutorService executor;
    
    @Override
    public void setMasterObserver(MoveNotifier observer) {
        this.master = observer;
    }

    @Override
    /**
     * Does nothing
     */
    public void addSlaveObserver(MoveNotifier observer) {}

    @Override
    public void notifyMasterObserverAboutMove(Move move) {
        if(master!=null){
            try{
                master.updateMove(this, move);
            }catch(Exception ex){
                System.err.println("Error on updating move: "+ex);
                ex.printStackTrace();
                throw ex;
            }
        }else
            System.err.println("Socket has no master");
    }

    @Override
    public void notifySlaveObserversAboutMove(Move move) {
        if(writer!=null){
            writer.addMessage(move);
            //writer.updateMove(this, move);
        }else
            System.err.println("Socket has no Socket writer");
    }

    @Override
    public void notifyMasterObserverAboutMessage(Message msg) {
        if(master!=null){
            try{
                master.updateMessage(this, msg);
            }catch(Exception ex){
                System.err.println("Error on updating message: "+ex);
                ex.printStackTrace();
                throw ex;
            }
        }else
            System.err.println("Socket has no master");
            
    }

    @Override
    public void notifySlaveObserversAboutMessage(Message msg) {
        //System.out.println("checking writer:"+writer);
        if(writer!=null){
            //System.out.println("updating :"+writer);
            writer.addMessage(msg);
            //writer.updateMessage(this, msg);
        }else
            System.err.println("Socket has no Socket writer");
    }

    @Override
    public void updateMove(MoveNotifier src, Move move) {
        if(move!=null){
            if(src==master){
                synchronized(this){
                    notifySlaveObserversAboutMove(move);
                }
            }else if(src == this){
                notifyMasterObserverAboutMove(move);
            }else {
                System.err.println("Received Move from illegal source: "+src);
            }
        }else{
            System.err.println("Received NULL Move from "+src);
        }
    }

    
    
    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof MessageType){
            if((o==reader)&&(reader!=null))
                updateMessage(this, reader.getMessage());
            else
                System.err.println("Received illegal notification ["+arg+"] from: "+o);
        }else if((o==writer)&&(writer!=null)){
            if(arg instanceof Message){
                updateMessage(this,(Message)arg);
            }
        }else{
            System.err.println("Received illegal notification ["+arg+"] from: "+o);
        }
    }
    
    private final LinkedList<MessageType> requestedTypes = new LinkedList<>();
    private final HashMap<MessageType,LinkedList<Message>> requestedMessages = new HashMap<>();
    
    
    @Override
    public void updateMessage(MoveNotifier src, Message msg) {   
        if(msg!=null){
            switch(msg.getMessageType()){
                case SOCKET_CLOSE_ERROR:
                        notifyMasterObserverAboutMessage(new ServerShutdown());
                        /*System.err.println("End of stream, disconnecting"); 
                        try{
                            Disconnect();
                        }catch(SocketException ex){
                            System.out.println("Couldn't close Socket");
                        }*/
                    break;
                case SOCKET_INPUT_ERROR:  
                case SOCKET_OUTPUT_ERROR:  
                    System.err.println("Got error on Socket: ["+msg.getMessageType()+"]: "+msg.getMessage()); 
                    boolean success = false;
                    if((!isConnected())&&(!isServer)){
                        try{
                            success =Reconnect();
                        }catch(SocketException ex){
                            System.out.println("Couldn't restart Socket");
                        }
                    }
                    if(!success){
                        try{
                            Disconnect();
                        }catch(SocketException ex){
                            System.out.println("Couldn't close Socket");
                        }
                        notifyMasterObserverAboutMessage(msg);
                    }
                    //notifyMasterObserverAboutMessage(new ServerShutdown());
                break;
            }
            if(src==master){
                //System.out.println("Sending message :"+msg.toString());
                switch(msg.getMessageType()){
                    case REQUEST_PING:
                            notifySlaveObserversAboutMessage(new Ping());
                        break;
                    default:
                            notifySlaveObserversAboutMessage(msg);
                            //System.out.println("Sending message to all slaves"+msg.toString());
                        break;
                }
            }else if(src == this){
                int request_index= requestedTypes.indexOf(msg.getMessageType());
                if(request_index>=0){
                    //System.out.println("Inserting ["+msg.getMessageType()+"] into requested messages");
                    requestedTypes.remove(request_index);
                    LinkedList<Message> list;
                    synchronized(this){
                        if(  (list = requestedMessages.get(msg.getMessageType()))!=null  ){
                            list.add(msg);
                        }else{
                            list = new LinkedList<>();
                            list.add(msg);
                            requestedMessages.put(msg.getMessageType(), list);
                        }
                    }
                }else{
                    switch(msg.getMessageType()){
                        case PING:
                                if(msg instanceof Ping)
                                    notifySlaveObserversAboutMessage(new Pong((Ping)msg));
                                else
                                    System.err.println("Received invalid instance of message for PING message: "+msg.getMessage());
                            break;
                        case PONG:
                                if(msg instanceof Pong)
                                    notifyMasterObserverAboutMessage(new ReturnPing(((Pong)msg).getLatency()));
                                else
                                    System.err.println("Received invalid instance of message for PONG message: "+msg.getMessage());
                            break;
                        case SOCKET_CLOSE_ERROR:
                        case SOCKET_INPUT_ERROR:  
                        case SOCKET_OUTPUT_ERROR:   
                            break;
                        case MOVE:
                                if(msg instanceof Move)
                                    updateMove(this, (Move)msg);
                                else
                                    System.err.println("Received invalid instance of message for Move type:"+msg);
                            break;
                        default:
                                notifyMasterObserverAboutMessage(msg);
                            break;
                    }
                }
            }else if(src != writer){
                System.err.println("Received message from illegal source: "+src+"  this:["+this+"] M["+(master)+"]W["+(writer)+"]R["+(reader)+"]");
                (new Exception()).printStackTrace();
            }
        }else{
            System.err.println("Received NULL Message from "+src);
        }
    }

    
    /**
     * Sends given message to connected host.
     * @param msg Message to send
     * @throws SocketException on communication error or when IO was not initialised.
     */
  /*  public void send(Message msg) throws SocketException{
        if((msg!=null)&&(writer!=null)){
            writer.addMessage(msg); 
            if(!isWorking()){
                writer.run();
            }
        }else{
            throw new SocketException(SocketException.Type.DEFAULT, "IO is not initialised");
        }
    }*/
    
    /**
     * Makes information for Socket that specified message is requested and will be returned through this method instead of NotifyMasterObserverAboutXXX()
     * Waits in 10ms intervals for 5 seconds
     * @param type Type of a message to receive. Cannot be null
     * @return Message received. Null if not one was received within specified time
     * @throws SocketException on communication error or when IO was not initialised.
     * @throws IllegalArgumentException When the timeout is lesser than or equal to 0 while the wait is true
     * @throws NullPointerException If type is null
     */
    public Message requestMessage(MessageType type) throws SocketException{
        return requestMessage(5000, 10, type);
    }
    /**
     * Makes information for Socket that specified message is requested and will be returned through this method instead of NotifyMasterObserverAboutXXX()
     * @param timeout how long to wait in microseconds in total. 0 will work like wait = false
     * @param interval how long to wait in microseconds between checking if something was delivered.
     * @param type Type of a message to receive. Cannot be null
     * @return Message received. Null if not one was received within specified time
     * @throws SocketException on communication error or when IO was not initialised.
     * @throws IllegalArgumentException When the timeout is lesser than zero or interval is non positive
     * @throws NullPointerException If type is null
     */
    public Message requestMessage(int timeout,int interval,MessageType type) throws SocketException{
        if(type == null){
            throw new NullPointerException("Null pointer");
        }
        if(timeout<=0){
            throw new IllegalArgumentException("Argument is not positive");
        }
        requestedTypes.add(type);
        
        Message rtrn=null;
        synchronized(this){
            if(reader!=null){
                boolean close;
                int it=0;
                LinkedList<Message> list;
                
                if(close = !isWorking()){
                    reader.run();
                }
                
                while(( (list = requestedMessages.get(type))==null  )&&(it<timeout)){
                    try {
                        wait(interval);
                    } catch (InterruptedException ex) {}
                    it+=interval;
                }
                if(list!=null){
                    rtrn = list.poll();
                    if(list.isEmpty()){
                        requestedMessages.remove(type);
                    }
                }
                
                
            }else{
                throw new SocketException(SocketException.Type.DEFAULT, "IO is not initialised");
            }
        }
        return rtrn;
    }
    
    
    
    /**
     * Checks approximate communication latency with host.
     * @return Estimated time, -1 if response was not received within 5 seconds, most likely signifing broken connection
     */
    public synchronized long checkPing(){
        try{
            long rtrn = -1;
            updateMessage(master,new RequestPing());
            Message msg = requestMessage(5000, 5, MessageType.PONG);
            if(msg!=null){
                if(msg instanceof Pong)
                    rtrn = ((Pong) msg).getLatency();
                else{
                    System.err.println("Received Illegal message class for PONG message:"+msg);
                }
            }
            return rtrn;
        }catch(SocketException ex){
            return -1;
        }
    }
    
    /**
     * Tries to connect to specified host.
     * @param ip IP of a host to connect to.
     * @param port Port of a host to connect to.
     * @param interval Interval between each successful socket reads and writes.
     * @return Whether it was successful or not.
     * @throws SocketException on communication error.
     */
    public boolean Connect(String ip,int port,int interval) throws SocketException{
        return Connect(ip, port, false,interval);
    }
    
    /**
     * Set ups input and output on socket. Server constructor.
     * @param socket Socket to set up IO from.
     * @param interval Interval between each successful socket reads and writes.
     * @return Whether the automatic input/output read started.
     * @throws SocketException on communication error.
     * @throws NullPointerException - If one of the parameters is null
     * @throws IllegalArgumentException - If interval is non positive
     */
    private boolean Connect(java.net.Socket socket,boolean again,int interval) throws SocketException{
        if(socket==null){
            throw new NullPointerException("Null argument");
        }
        if(interval<=0){
            throw new IllegalArgumentException("Non positive interval");
        }
        if((socket.isConnected())&&(!socket.isClosed())){
            Disconnect();
            boolean rtrn = false;
            SocketWriter out = null;
            SocketReader in = null;
            String msg = "";
            try {
                msg="Exception after changing socket option";
                socket.setKeepAlive(true);
                //socket.setSoTimeout(10000);
                InputStream is = socket.getInputStream();
                msg="Exception after creating IS";
                OutputStream os = socket.getOutputStream();
                msg="Exception after creating OS";
                if((is!=null)&&(os!=null)){
                    os.flush();
                    msg="Exception after creating writer";
                    out = new SocketWriter(this,os);
                    msg="Exception after creating reader";
                    in = new SocketReader(this,is);
                    msg="";
                    this.interval = interval;
                    rtrn = true;
                }
            } catch (IOException ex) {
                rtrn = false;
            }    
            if(rtrn){
                this.socket = socket;
                this.writer = out;
                this.reader = in;
                rtrn = startIO();
                return rtrn;
            }else{
                throw new SocketException(SocketException.Type.CANNOT_INITIALISE,"SERVER SOCKET: "+msg);
            }
        }else{
            throw new SocketException(SocketException.Type.CANNOT_INITIALISE,"SERVER SOCKET");
        }
    }
    
    /**
     * Tries to connect to specified host. Client constructor.
     * @param ip IP of a host to connect to. Can be ipv4 or ipv6. Could be known host name.
     * @param port Port of a host to connect to.
     * @param again Whether this method call is a second call.
     * @param interval Interval between each successful socket reads and writes.
     * @return Whether the automatic input/output read started.
     * @throws SocketException on communication error.
     * @throws IllegalArgumentException - If port is outside [0-65535] boundaries or interval is non positive
     * @throws NullPointerException - If one of the parameters is null
     */
    private boolean Connect(String ip,int port,boolean again,int interval) throws SocketException{
        if(ip==null){
            throw new NullPointerException("Null argument");
        }
        if(port<0||port>65535){
            throw new IllegalArgumentException("Port outside boundaries");
        }
        if(interval<=0){
            throw new IllegalArgumentException("Non positive interval");
        }
        try{
            Disconnect();
            boolean rtrn = false;
            java.net.Socket soc = null;
            SocketWriter out = null;
            SocketReader in = null;
            String msg = "";
            try {
                msg="Exception after socket creation";
                soc = new java.net.Socket(InetAddress.getByName(ip),port);
                msg="Exception after changing socket option";
                soc.setKeepAlive(true);
               //soc.setSoTimeout(10000);
                msg="Exception after creating writer";
                out = new SocketWriter(this,soc);
                msg="Exception after creating reader";
                in = new SocketReader(this,soc);
                msg="";
                this.interval = interval;
                rtrn = true;
            } catch (IOException ex) {
                //ex.printStackTrace();
                rtrn = false;
            } catch (RejectedExecutionException ex){
                rtrn = false;
                msg="Could not start thread.";
            }   
            if(rtrn){
                this.ip = ip;
                this.port = port;
                this.socket = soc;
                this.writer = out;
                this.reader = in;
                rtrn = startIO();
                return rtrn;
            }else{
                throw new SocketException(SocketException.Type.CANNOT_CONNECT,"CLIENT: "+msg,ip, port);
            }
        }catch(SocketException e){
            if(!again){
                try{
                    Disconnect();
                    return Connect(ip, port, true,interval);
                }catch(SocketException ex){  
                    throw ex;
                }
            }else{
                throw e;
            }
        }
    }
    
    /**
     * Disconnects from currently connected host. 
     * If there is no active connection or it is already closed then this method does nothing.
     * @throws SocketException on communication error.
     */
    public void Disconnect() throws SocketException{
        stopIO();
        if( (socket!=null)/*&&(!socket.isClosed())*/ ){
            String msg = "";
            try{
                msg="After closing reader";
                if(reader!=null){
                    reader.close();
                    reader = null;
                }
                msg="After closing writerr";
                if(writer!=null){
                    writer.close();
                    writer = null;
                }
                msg="After closing socket";
                if(!socket.isClosed())
                    socket.close();
                socket=null;
            }catch(IOException ex){
                throw new SocketException(SocketException.Type.DEFAULT,"Problem while disconnecting from host: "+msg, socket.getInetAddress().getHostAddress(), socket.getPort());
            }
        }
    }
    /**
     * Tries to reconnect to previously connected host.
     * @return Whether it was successful or not.
     * @throws SocketException on communication error.
     */
    public boolean Reconnect() throws SocketException{
        if((socket!=null)&&(socket.isClosed())){
            Disconnect();
            return Connect(ip,port,interval);
        }else{
            return false;
        }
        
    }
    
    /**
     * A ScheduledFuture that can be used to extract result or cancel writer thread.
     */
    ScheduledFuture writer_future=null;
    /**
     * A ScheduledFuture that can be used to extract result or cancel reader thread.
     */
    ScheduledFuture reader_future=null;
    
    /**
     * Stops input and output writing/reading.
     */
    public void stopIO(){
        if(executor!=null)
            executor.shutdown();
        executor = null;
        if((writer_future!=null)/*&&(!writer_future.isDone())*/){
            writer_future.cancel(true);
        }
        if((reader_future!=null)/*&&(!reader_future.isDone())*/){
            reader_future.cancel(true);
        }
        writer_future = null;
        reader_future = null;
    }
    /**
     * Starts input and output writing/reading on socket through printer and writer at fixed intervals.
     * @return whether it was successful.
     * @throws RejectedExecutionException 
     */
    public boolean startIO() throws RejectedExecutionException{
        if(executor==null)
            executor = new ScheduledThreadPoolExecutor(4);
        if((reader!=null)&&(writer!=null)){
            ScheduledFuture wr,rd;
            if(writer_future!=null)
                writer_future.cancel(false);
            if(reader_future!=null)
                reader_future.cancel(false);
            wr=executor.scheduleWithFixedDelay(writer, 1, interval, TimeUnit.MILLISECONDS);
            rd=executor.scheduleWithFixedDelay(reader, 1, interval, TimeUnit.MILLISECONDS);
            writer_future=wr;
            reader_future=rd;
            return true;
        }else{
            return false;
        }
    }

    
    
    
    /**
     * Client constructor
     * @param master master of this component
     * @param ip IP of a host to connect to. Can be ipv4 or ipv6. Could be known host name.
     * @param port Port of a host to connect to.
     * @param interval Interval between each successful socket reads and writes.
     * @throws SocketException on any error related to socket.
     */
    public Socket(MoveNotifier master,String ip , int port,int interval) throws SocketException{
        this.master = master;
        executor = new ScheduledThreadPoolExecutor(4);
        isServer=false;
        if(!Connect(ip,port,interval)){
            System.err.println("Couldn't start automatic reading/writing through socket");
        }
    }
    /**
     * Server constructor.
     * @param master master of this component
     * @param socket Socket to set up IO from.
     * @param interval Interval between each successful socket reads and writes.
     * @throws SocketException on communication error.
     * @throws NullPointerException If any argument is null
     */
    public Socket(MoveNotifier master,java.net.Socket socket,int interval) throws SocketException{
        this.master = master;
        executor = new ScheduledThreadPoolExecutor(4);
        isServer=true;
        if(socket!=null){
            if(!Connect(socket,false,interval)){
                System.err.println("Couldn't start automatic reading/writing through socket");
            }
        }
    }
    
    
    /**
     * Checks whether the socket is connected to server.
     * @return true if yes.
     */
    public boolean isConnected(){
        return (socket!=null)&&(!socket.isClosed());
    }
    
    
    /**
     * Checks whether is Socket currently trying to send or receive messages.
     * @return true if yes.
     */
    public boolean isWorking(){
        return isConnected()
            &&(writer_future!=null)
            &&(!writer_future.isCancelled())
            &&(writer!=null)
            &&(reader_future!=null)
            &&(!reader_future.isCancelled())
            &&(reader!=null);
        
    }
    
    
    
}