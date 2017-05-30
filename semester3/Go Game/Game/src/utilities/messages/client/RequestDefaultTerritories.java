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
 * Request for default territory placement by subcomponent of GameHandler
 * @author n1t4chi
 */
public class RequestDefaultTerritories implements Message{

    @Override
    public Serializable getMessage() {
        return "gib default territories pls";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.REQUEST_DEFAULT_TERRITORIES;
    }
    
}
