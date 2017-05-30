/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client class.
 * @author n1t4chi
 */
public class Client{

    /**
     * client socket.
     */
    final Socket socket;
    /**
     * client input.
     */
    private ObjectInputStream OIS;
    /**
     * Whether input was taken already or not
     */   
    private boolean gotInput;
    /**
     * Whether output end was achieved.
     */   
    public boolean finishedInput;

    /**
     * client output.
     */
    private ObjectOutputStream OOS;   
    /**
     * Whether output was taken already or not
     */   
    private boolean gotOutput; 
    /**
     * Whether output end was achieved.
     */   
    public boolean finishedOutput;
    /**
     * Returns whether this client should be moved from setting up list to working client list.
     * @return True if this client should be moved/
     */
    public boolean shouldBeMoved(){
        return gotInput&&gotOutput;
    }
    /**
     * Returns whether this client should be removed from clent list.
     * @return True if this client should be removed.
     */
    public boolean shouldBeRemoved(){
        return finishedInput&&finishedOutput;
    }
    /**
     * Returns input stream if it was not taken before.
     * @return Input stream if it was not taken before, null otherwise.
     * @throws IOException When could not get input stream.
     */
    public ObjectInputStream getInput() throws IOException {
        if(!gotInput){
            OIS = new ObjectInputStream(this.socket.getInputStream());
            gotInput=true;
            return OIS;
        }else{
            return OIS;
        }
    }
    /**
     * Returns output stream if it was not taken before.
     * @return Output stream if it was not taken before, null otherwise.
     * @throws IOException When could not get  output stream.
     */
    public ObjectOutputStream getOutput() throws IOException {
        if(!gotOutput){
            OOS = new ObjectOutputStream(this.socket.getOutputStream());
            gotOutput=true;
            return OOS;
        }else{
            return OOS;
        }
    }

    /**
     * constructor
     * @param socket Client socket
     */
    public Client(Socket socket) {
        this.socket = socket;
        gotInput = false;
        finishedInput = false;
        gotOutput = false;
        finishedOutput = false;
    }    

    @Override
    public String toString() {
        return "["+socket.getInetAddress()+"]:["+socket.getPort()+"]";
    }
    
    
}
