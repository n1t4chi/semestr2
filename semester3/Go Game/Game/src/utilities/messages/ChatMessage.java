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
 * Class used for sending chat messages between players.
 * @author n1t4chi
 */
public class ChatMessage implements Message{
    /**
     * Message source types used for communication. 
     */
    public enum ChatSource{
        SERVER,SYSTEM,ERROR,PLAYER,OPPONENT
        
    }
    
    /**
     * Chat message.
     */
    final String message;
    
    final ChatSource source;
    
    
    /**
     * Default constructor
     * @param source Source of message.
     * @param message Message to send.
     */
    public ChatMessage(ChatSource source, String message) {
        this.message = message;
        this.source = source;
    }
    /**
     * Returns source of this message
     * @return source of this message.
     */
    public ChatSource getSource() {
        return source;
    }
    
    
    
    
    /**
     * Works the same as getMessage() but returns string.
     * @return Message.
     */
    public String getChatMessage(){
        return message;
    }
    
    @Override
    public Serializable getMessage() {
        return getChatMessage();
    }
    
    @Override
    public MessageType getMessageType() {
        return MessageType.CHAT;
    }
    
}
