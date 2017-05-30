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
 * Message from server that opponent disconnected.
 * @author n1t4chi
 */
public class PlayerDisconnected implements Message{

    @Override
    public Serializable getMessage() {
        return "Damned cowards.";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PLAYER_DISCONNECTED;
    }
    
}
