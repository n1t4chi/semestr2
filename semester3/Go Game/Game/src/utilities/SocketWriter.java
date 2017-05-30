/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Observable;
import utilities.messages.Message;
import utilities.messages.MessageType;
import utilities.messages.SocketCloseError;
import utilities.messages.SocketInputException;

/**
 * Class used for maintaining input of socket.
 */
class SocketWriter extends Observable implements Runnable{
    /**
     * Deck for messages to send.
     */
    private final ArrayDeque<Message> messagesToSend = new ArrayDeque();
    /**
     * Output stream
     */
    ObjectOutputStream oos;
    /**
     * Stops writing.
     */
    public void close() throws IOException{

        oos.close();
        oos = null;
        messagesToSend.removeAll(messagesToSend);
    }
    /**
     * Resets messages to send.
     */
    public void reset(){
        if(oos!=null){
            try {
                oos.flush();
            } catch (IOException ex) {
                consecutive_error_counter++;
            }
        }
        messagesToSend.removeAll(messagesToSend);
    }

    /**
     * Adds message to be send by socket.
     * @param msg Message to send.
     */
    public synchronized void addMessage(Message msg){
        messagesToSend.add(msg);
    }
    /**
     * Default client constructor.
     * @param parent Parent
     * @param socket socket to get writer stream
     * @throws IOException When there was error on getting writer stream
     */
    public SocketWriter(Socket parent,java.net.Socket socket) throws IOException {
        if((parent==null)||(socket==null)){
            throw new NullPointerException("null argument");
        }
        addObserver(parent);
        oos = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * Default server constructor.
     * @param parent Parent
     * @param os output stream
     * @throws IOException When there was error on getting writer stream
     * @throws NullPointerException when one of arguments is null
     */
    public SocketWriter(Socket parent,OutputStream os) throws IOException {
        if((parent==null)||(os==null)){
            throw new NullPointerException("null argument");
        }
        addObserver(parent);
        oos = new ObjectOutputStream(os);
        if(oos!=null){
            oos.flush();
        }else{
            throw new IOException("Failed to create ObjectOutputStream");
        }
    }

    int consecutive_error_counter=0;
    @Override
    public void run(){
        if(oos!=null){
            try {
                while(!messagesToSend.isEmpty()){
                    Message msg = messagesToSend.pollFirst();
                    if(msg!=null){
                        oos.writeUnshared(msg);
                        //if(msg.getMessageType()!=MessageType.PING)
                        //    System.out.println("["+new SimpleDateFormat("HH:mm:ss:SSS").format(new Date())+"] sending message:"+msg.getMessageType());
                        oos.flush();
                        consecutive_error_counter=0;
                    }
                }
            } catch (EOFException ex) {
                //System.err.println("Writer EOF:"+ex);
                setChanged();
                notifyObservers(new SocketCloseError(ex));
            } catch (IOException ex) {
                consecutive_error_counter++;
                if(consecutive_error_counter>1){
                    setChanged();
                    notifyObservers(new SocketInputException(ex));
                }
            }
        }
    }   
}
   
    
