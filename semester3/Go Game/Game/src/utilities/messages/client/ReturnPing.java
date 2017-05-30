/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.client;

import java.io.Serializable;
import utilities.messages.Message;
import utilities.messages.MessageType;

/**
 * Returned value of ping by GameHandler to subcomponent
 * @author n1t4chi
 */
public class ReturnPing implements Message{
    private final long ping;

    
    public ReturnPing(long ping) {
        this.ping = ping;
    }

    /**
     * Returns ping. Works same as getMessage() but returns long directly.
     * @return ping value
     */
    public long getPing() {
        return ping;
    }
    
    
    @Override
    public Serializable getMessage() {
        return getPing();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.RETURN_PING;
    }
    
}
