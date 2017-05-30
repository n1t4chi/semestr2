/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.playerstate.game;

/**
 *
 * @author Kingaa
 */
class getPlayerGameStateStopped extends PlayerGameState {
    
    private static volatile getPlayerGameStateStopped instance;
    
    private getPlayerGameStateStopped(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerGameStateStopped getInstance() {
        getPlayerGameStateStopped instance = getPlayerGameStateStopped.instance;
        if (instance == null) {
            synchronized(getPlayerGameStateStopped.class){
                instance = getPlayerGameStateStopped.instance;
		if (instance == null) 
                    getPlayerGameStateStopped.instance = instance = new getPlayerGameStateStopped();
            }
        }
	return instance;
    }

    @Override
    public GameState getState() {
        return GameState.STOPPED;
    }
    
    @Override
    public boolean canSetGameStopped() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSetGamePlace() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSetGameWait() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSetGamePass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSetGameChose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSetGameAgree() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSetGameDisagree() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSetGameWon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSetGameLost() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlayerGameState setState(GameState state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
