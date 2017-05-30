/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import java.io.Serializable;
import utilities.messages.Message;
import utilities.messages.MessageType;

/**
 * Returned name of opponent by Server
 * @author n1t4chi
 */
public class ReturnOpponentName implements Message{
    /**
     * Name of opponent
     */
    private final String name;
    /**
     * Default constructor
     * @param name Name of opponent
     */
    public ReturnOpponentName(String name) {
        this.name = name;
    }
    /**
     * Returns name of opponent
     * @return name of opponent
     */
    public String getName() {
        return name;
    }
    
    
    
    @Override
    public Serializable getMessage() {
        return getName();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.RETURN_OPPONENT_NAME;
    }
    
}
