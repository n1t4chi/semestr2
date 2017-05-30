/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import java.io.Serializable;

/**
 * Message send to check latency between creating Ping message and receiving Pong
 * @author n1t4chi
 */
public class Ping  implements Message{
    protected final long sendTime;

    /**
     * Default constructor.
     */
    public Ping() {
        this.sendTime = System.currentTimeMillis();
    }
    /**
     * Constructor for pong.
     * @param ping ping message to pair
     */
    protected Ping(Ping ping) {
        if(ping!=null)
            this.sendTime = ping.sendTime;
        else{
            this.sendTime=0;
        }
    }
    
    
    
    @Override
    public Serializable getMessage() {
        return "ping";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PING;
    }

}
