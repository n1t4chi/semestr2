/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.messages.Message;
import utilities.messages.MessageType;
import utilities.messages.SocketCloseError;
import utilities.messages.SocketInputException;

/**
 * Class used for maintaining writer of socket.
 */
class SocketReader extends Observable implements Runnable{
    /**
     * Output stream
     */
    private ObjectInputStream ois;
    /**
     * Stack for received messages.
     */
    private final LinkedList<Message> messagesReceived = new LinkedList<>();
    /**
     * Stops reading.
     */
    public void close() throws IOException{
        ois.close();
        ois=null;
        messagesReceived.removeAll(messagesReceived);
    }
    /**
     * Removes all received messages.
     */
    public void reset(){
        removeAllReceived();
        try {
            ois.skipBytes(ois.available());
        } catch (IOException ex) {
            consecutive_error_counter++;
        }
    }
    /**
     * Removes all received messages.
     */
    public void removeAllReceived(){
        messagesReceived.removeAll(messagesReceived);
    }
    /**
     * Resets received messages of given type
     * @param type Type of messages to remove.
     */
    public void removeAllReceived(MessageType type){
        if(type ==  null){
            removeAllReceived();
        }else{
            for(Message msg : messagesReceived){
                if(msg.getMessageType()==type){
                    messagesReceived.remove(msg);
                }
            }
        }
    }

    /**
     * Returns oldest message received through socket.
     * @return Message If no message are received then null is returned.
     */
    public synchronized Message getMessage(){
        return messagesReceived.poll();
    }
    int consecutive_error_counter=0;
    /**
     * Returns oldest message received through socket.
     * @return Message If no message are received then null is returned.
     */
    public synchronized Message getMessage(MessageType type){
        if(type==null){
            return getMessage();
        }else{
            Message rtrn = null;
            int i=0;
            while((i<messagesReceived.size())&&(messagesReceived.get(i).getMessageType()!=type)){
                i++;
            }
            if(i<messagesReceived.size()){
                rtrn = messagesReceived.remove(i);
            }
            return rtrn;
        }
    }

    /**
     * Default client constructor
     * @param parent 
     * @param socket socket to get input stream
     * @throws IOException When there was error on getting writer stream
     */
    public SocketReader(Socket parent,java.net.Socket socket) throws IOException {
        if((parent==null)||(socket==null)){
            throw new NullPointerException("null argument");
        }
        addObserver(parent);
        ois = new ObjectInputStream(socket.getInputStream());
    }
    /**
     * Default server constructor
     * @param parent 
     * @param is input stream
     * @throws IOException When there was error on getting writer stream
     * @throws NullPointerException when one of arguments is null
     */
    public SocketReader(Socket parent,InputStream is) throws IOException {
        if((parent==null)||(is==null)){
            throw new NullPointerException("null argument");
        }
        addObserver(parent);
        ois = new ObjectInputStream(is);
    }

    
    @Override
    public void run() {
        if(ois!=null){
            try {
                Object ob;
                while((ob=ois.readUnshared())!=null){     
                    if(ob instanceof Message){
                        messagesReceived.add((Message) ob);
                        //if(((Message) ob).getMessageType()!=MessageType.PONG)
                        //System.out.println("["+new SimpleDateFormat("HH:mm:ss:SSS").format(new Date())+"] received message "+((Message) ob).getMessageType());
                        setChanged();
                        notifyObservers(((Message) ob).getMessageType());
                    }
                    consecutive_error_counter=0;
                }
                
            } catch (EOFException ex) {
                System.err.println("Reader EOF:"+ex);
                messagesReceived.add(new SocketCloseError(ex));
                setChanged();
                notifyObservers(MessageType.SOCKET_CLOSE_ERROR);
            } catch (IOException ex) {
                System.err.println("Reader error IO:"+ex);
                consecutive_error_counter++;
                if(consecutive_error_counter>1){
                    messagesReceived.add(new SocketInputException(ex));
                    setChanged();
                    notifyObservers(MessageType.SOCKET_INPUT_ERROR);
                }
            } catch (ClassNotFoundException ex) {
                System.err.println("Received invalid class through socket:"+ex);
            }
        }
    }   
}