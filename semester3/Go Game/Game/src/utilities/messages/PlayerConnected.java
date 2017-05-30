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
 * Message from Server that another player connected to game. Contains his name
 * @author n1t4chi
 */
public class PlayerConnected extends ReturnOpponentName{
    /**
     * Default constructor
     * @param name Name of opponent
     */
    public PlayerConnected(String name) {
        super(name);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PLAYER_CONNECTED;
    }
    
}
