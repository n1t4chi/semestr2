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
class getPlayerGameStateWait extends PlayerGameState {
    
    private static volatile getPlayerGameStateWait instance;
    
    private getPlayerGameStateWait(){
    }
    
    /**
     * Returns the instance of this class
     * @return instamce of this class
     */
    public static getPlayerGameStateWait getInstance() {
        getPlayerGameStateWait instance = getPlayerGameStateWait.instance;
        if (instance == null) {
            synchronized(getPlayerGameStateWait.class){
                instance = getPlayerGameStateWait.instance;
		if (instance == null) 
                    getPlayerGameStateWait.instance = instance = new getPlayerGameStateWait();
            }
        }
	return instance;
    }

    @Override
    public GameState getState() {
        return GameState.WAIT;
    }

    @Override
    public boolean canSetGameStopped() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSetGamePlace() {
        return true;
    }

    @Override
    public boolean canSetGameWait() {
        return true;
    }

    @Override
    public boolean canSetGamePass() {
        return false;
    }

    @Override
    public boolean canSetGameChose() {
        return false;
    }

    @Override
    public boolean canSetGameAgree() {
        return false;
    }

    @Override
    public boolean canSetGameDisagree() {
        return false;
    }

    @Override
    public boolean canSetGameWon() {
        return true;
    }

    @Override
    public boolean canSetGameLost() {
        return true;
    }
    
}
