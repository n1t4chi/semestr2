/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import java.io.Serializable;

/**
 * Message received back to check latency between creating Ping message and receiving Pong.
 * Returns difference between current time upon reading getPing() and creation time of paired Ping message,
 * so for proper results the message should be handled as fast as possible.
 * @author n1t4chi
 */
public class Pong extends Ping{
    /**
     * Default constructor. Pairs this Pong with Ping.
     * @param ping 
     */
    public Pong(Ping ping) {
        super(ping);
    }
    /**
     * Returns difference between current time upon reading getPing() and creation time of paired Ping message
     * @return latency
     */
    public long getLatency() {
        return System.currentTimeMillis()-sendTime;
    }
    
    
    
    
    @Override
    public Serializable getMessage() {
        return getLatency()+"ms";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PONG;
    }

}
