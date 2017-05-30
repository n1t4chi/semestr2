/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import java.io.Serializable;

/**
 * Request for AI
 * @author n1t4chi
 */
public class RequestAI implements Message{
    /**
     * 
     */
    //private final T x;
    
    @Override
    public Serializable getMessage() {
        throw new UnsupportedOperationException("todo");
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ADD_AI;
    }
    
}
