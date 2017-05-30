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
class getPlayerConnectionStateDisconnected extends PlayerConnectionState {

    private static getPlayerConnectionStateDisconnected instance;
    
    private getPlayerConnectionStateDisconnected(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerConnectionStateDisconnected getInstance() {
        if (instance == null) {
            synchronized(getPlayerConnectionStateDisconnected.class){
		if (instance == null) 
                    instance = instance = new getPlayerConnectionStateDisconnected();
            }
        }
	return instance;
    }

    @Override
    public ConnectionState getState() {
        return ConnectionState.DISCONNECTED;
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
        return false;
    }

    @Override
    public boolean canSetConnectionReconnected() {
        return true;
    }
    
}
