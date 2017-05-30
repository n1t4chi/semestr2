/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import java.io.Serializable;

/**
 *
 * @author n1t4chi
 */
public class ServerShutdown implements Message{

    @Override
    public Serializable getMessage() {
        return "I quit";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SERVER_SHUTDOWN;
    }
    
}
