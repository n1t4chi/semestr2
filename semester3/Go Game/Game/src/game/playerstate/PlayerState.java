/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.playerstate;

import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.connection.PlayerConnectionState.ConnectionState;
import game.playerstate.game.PlayerGameState;
import game.playerstate.game.PlayerGameState.GameState;
import java.util.Observable;

/**
 * Interface for handling player statuses. Subclasses are required to use default constructor to properly initialise this object.
 * @author n1t4chi
 */
public class PlayerState extends Observable{
    /**
     * Represents type of a player
     */
    public enum PlayerType {HUMAN,AI};
    
    private PlayerConnectionState connection_state= null;
    private PlayerGameState game_state = null;
    private final PlayerType type;
    

    /**
     * Constructor.
     * @param connection_state 
     * @param game_state Player Game State 
     * @param type Type of a player.
     * @throws NullPointerException If either one of the arguments was null.
     */
    protected PlayerState(PlayerConnectionState connection_state, PlayerGameState game_state, PlayerType type) throws NullPointerException{
        if((connection_state==null)||(game_state==null)||(type==null))
            throw new NullPointerException("Atleast one of the parameters was null");
        this.connection_state = connection_state;
        this.game_state = game_state;
        this.type = type;
    }
    /**
     * Default constructor. Should be used by subclass to initialise this object with proper initial values.
     * @param type Type of a player.
     */
    public PlayerState(PlayerType type) {
        this.type = type;
    }
    
    
    
    
    
    
    /**
     * Returns whether this player is Human or AI.
     * @return whether this player is Human or AI.
     */
    public boolean isHuman() {
        return type==PlayerType.HUMAN;
    }
    /**
     * Returns current game state.
     * @return current game state.
     */
    public GameState getGameState() {
        return game_state.getState();
    }
    

    /**
     * Changes current game state.
     * @param state GameState to change to
     * @return Whether changing game state was successful
     */
    public boolean setGameState(GameState state) {
        if(state!=null){
            if(this.game_state == null){
                this.game_state = PlayerGameState.getPlayerGameState(state);
                this.setChanged();
                this.notifyObservers(state);
                return true;
            }else{
                PlayerGameState rtrn = this.game_state.setState(state);
                if(rtrn.getState()==state){
                    this.game_state=rtrn;
                    this.setChanged();
                    this.notifyObservers(state);
                    return true;
                }else
                    return false;
            }
        }else{
            return false;
        }
    }
    
    /**
     * Forces changing the game state
     * @param state GameState to change to
     */
    public void forceSetGameState(GameState state){
        this.game_state = PlayerGameState.getPlayerGameState(state);
    }
    /**
     * Returns current connection state.
     * @return current connection state.
     */
    public ConnectionState getConnectionState() {
        return connection_state.getState();
    }
    /**
     * Changes Connection State.
     * @param state State to change to.
     * @return Whether changing game state was successful
     */
    public boolean setConnectionState(ConnectionState state) {
        if(state!=null){
            if(this.connection_state == null){
                this.connection_state = PlayerConnectionState.getPlayerConnectionState(state);
                this.setChanged();
                this.notifyObservers(state);
                return true;
            }else{
                PlayerConnectionState rtrn = this.connection_state.setState(state);
                if(rtrn.getState()==state){
                    this.connection_state=rtrn;
                    this.setChanged();
                    this.notifyObservers(state);
                    return true;
                }else
                    return false;
            }
        }else{
            return false;
        }
    }
    
   
    
}
