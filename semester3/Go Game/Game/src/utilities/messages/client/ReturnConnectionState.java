/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.client;

import game.playerstate.connection.PlayerConnectionState;
import java.io.Serializable;
import utilities.messages.Message;
import utilities.messages.MessageType;

/**
 * Returned value of x by GameHandler to subcomponent
 * @author n1t4chi
 */
public class ReturnConnectionState implements Message{
    /**
     * Connection state 
     */
    private final PlayerConnectionState.ConnectionState state;
    
    @Override
    public Serializable getMessage() {
        return state;
    }
    /**
     * Default constructor.
     * @param state 
     */
    public ReturnConnectionState(PlayerConnectionState.ConnectionState state) {
        this.state = state;
    }

    /**
     * Returns state, same as getMessage() but returns ConnectionState
     * @return state
     */
    public PlayerConnectionState.ConnectionState getState() {
        return getState();
    }
   
    @Override
    public MessageType getMessageType() {
        return  MessageType.RETURN_CONNECTION_STATE;
    }
    
}
