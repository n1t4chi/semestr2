/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.playerstate.connection;

/**
 *
 * @author Kingaa
 */
class getPlayerConnectionStateReconnected extends PlayerConnectionState {

    private static getPlayerConnectionStateReconnected instance;
    
    private getPlayerConnectionStateReconnected(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerConnectionStateReconnected getInstance() {
        if (instance == null) {
            synchronized(getPlayerConnectionStateReconnected.class){
		if (instance == null) 
                    instance = instance = new getPlayerConnectionStateReconnected();
            }
        }
	return instance;
    }

    @Override
    public ConnectionState getState() {
        return ConnectionState.RECONNECTED;
    }

    @Override
    public boolean canSetConnectionEmpty() {
        return false;
    }

    @Override
    public boolean canSetConnectionDisconnected() {
        return false;
    }

    @Override
    public boolean canSetConnectionConnected() {
        return true;
    }

    @Override
    public boolean canSetConnectionReconnected() {
        return true;
    }
    
}
