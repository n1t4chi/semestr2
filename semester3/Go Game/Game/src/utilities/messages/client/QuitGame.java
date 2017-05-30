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
 * Message send by sub component to GameHandler
 * @author n1t4chi
 */
public class QuitGame implements Message{

    @Override
    public Serializable getMessage() {
        return "End my suffering please";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.QUIT_GAME;
    }
    
}
