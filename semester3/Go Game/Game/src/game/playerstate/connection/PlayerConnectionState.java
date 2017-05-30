/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.playerstate.connection;

/**
 * Abstract class for connection state handling.
 * @author n1t4chi
 */
public abstract class PlayerConnectionState {


    public static PlayerConnectionState getPlayerConnectionState(ConnectionState state){      
        PlayerConnectionState rtrn = null;
        switch(state){
            case CONNECTED:
                    rtrn = getPlayerConnectionStateConnected.getInstance();
                break;
            case RECONNECTED:
                    rtrn = getPlayerConnectionStateReconnected.getInstance();
                break;
            case DISCONNECTED:
                    rtrn = getPlayerConnectionStateDisconnected.getInstance();
                break;
            case EMPTY:
                    rtrn = getPlayerConnectionStateEmpty.getInstance();
                break;
        }
        return rtrn;
    }
    
    /**
     * Represents connection status of a player.
     */
    public enum ConnectionState{CONNECTED,RECONNECTED,DISCONNECTED,EMPTY}; 
    
    /**
     * Returns current state.
     * @return current state.
     */
    public abstract ConnectionState getState();
    /**
     * Changes state.
     * @param state state to change to.
     * @return new PlayerConnectionState.
     */
    public PlayerConnectionState setState(ConnectionState state){
        PlayerConnectionState rtrn = this;
        switch(state){
            case CONNECTED: rtrn=(canSetConnectionConnected())?getPlayerConnectionState(ConnectionState.CONNECTED):rtrn; break;
            case DISCONNECTED: rtrn=(canSetConnectionDisconnected())?getPlayerConnectionState(ConnectionState.DISCONNECTED):rtrn; break;
            case EMPTY: rtrn=(canSetConnectionEmpty())?getPlayerConnectionState(ConnectionState.EMPTY):rtrn; break;
            case RECONNECTED: rtrn=(canSetConnectionReconnected())?getPlayerConnectionState(ConnectionState.RECONNECTED):rtrn; break;
        }
        return rtrn;
    }
    
    /**
     * Check if it is possible to change state to empty.
     * @return whether it is possible.
     */
    public abstract boolean canSetConnectionEmpty();
    /**
     * Check if it is possible to change state to disconnected.
     * @return whether it is possible.
     */
    public abstract boolean canSetConnectionDisconnected();
    /**
     * Check if it is possible to change state to connected
     * @return whether it is possible.
     */
    public abstract boolean canSetConnectionConnected();
    /**
     * Check if it is possible to change state to connected
     * @return whether it is possible.
     */
    public abstract boolean canSetConnectionReconnected();
    
}
