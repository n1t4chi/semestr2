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
 * Request for opponent name.
 * @author n1t4chi
 */
public class RequestOpponentName implements Message{

    @Override
    public Serializable getMessage() {
        return "gib name pls";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.REQUEST_OPPONENT_NAME;
    }
    
}
