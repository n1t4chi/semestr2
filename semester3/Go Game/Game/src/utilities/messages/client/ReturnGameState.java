/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.client;

import game.playerstate.game.PlayerGameState.GameState;
import java.io.Serializable;
import utilities.messages.Message;
import utilities.messages.MessageType;

/**
 * Returned value of x by GameHandler to subcomponent
 * @author n1t4chi
 */
public class ReturnGameState implements Message{
    /**
     * Game state
     */
    private final GameState state;
    /**
     * Default constructor.
     * @param state 
     */
    public ReturnGameState(GameState state) {
        this.state = state;
    }

    /**
     * Returns state, same as getMessage() but returns GameState
     * @return state
     */
    public GameState getState() {
        return state;
    }
   
    
    @Override
    public Serializable getMessage() {
        return getState();
    }

    @Override
    public MessageType getMessageType() {
        return  MessageType.RETURN_GAME_STATE;
    }
    
}
