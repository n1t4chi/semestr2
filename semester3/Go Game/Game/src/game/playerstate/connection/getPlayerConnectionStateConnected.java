/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.playerstate.connection;

import game.playerstate.connection.PlayerConnectionState.ConnectionState;

/**
 *
 * @author Kingaa
 */
class getPlayerConnectionStateConnected extends PlayerConnectionState {
    
    private static getPlayerConnectionStateConnected instance;
    
    private getPlayerConnectionStateConnected(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerConnectionStateConnected getInstance() {
        if (instance == null) {
            synchronized(getPlayerConnectionStateConnected.class){
		if (instance == null) 
                    instance = instance = new getPlayerConnectionStateConnected();
            }
        }
	return instance;
    }

    @Override
    public ConnectionState getState() {
        return ConnectionState.CONNECTED;
    }

    @Override
    public boolean canSetConnectionEmpty() {
        return true;
    }

    @Override
    public boolean canSetConnectionDisconnected() {
        return true;
    }

    @Override
    public boolean canSetConnectionConnected() {
        return true;
    }

    @Override
    public boolean canSetConnectionReconnected() {
        return false;
    }
}
