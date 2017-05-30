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
class getPlayerConnectionStateEmpty extends PlayerConnectionState {

    private static getPlayerConnectionStateEmpty instance;
    
    private getPlayerConnectionStateEmpty(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerConnectionStateEmpty getInstance() {
        if (instance == null) {
            synchronized(getPlayerConnectionStateEmpty.class){
		if (instance == null) 
                    instance = instance = new getPlayerConnectionStateEmpty();
            }
        }
	return instance;
    }

    @Override
    public ConnectionState getState() {
        return ConnectionState.EMPTY;
    }

    @Override
    public boolean canSetConnectionEmpty() {
        return true;
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
        return false;
    }
    
}
