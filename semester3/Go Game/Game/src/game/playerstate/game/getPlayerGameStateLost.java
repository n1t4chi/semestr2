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
class getPlayerGameStateLost extends PlayerGameState {
    
    private static volatile getPlayerGameStateLost instance;
    
    private getPlayerGameStateLost(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerGameStateLost getInstance() {
        getPlayerGameStateLost instance = getPlayerGameStateLost.instance;
        if (instance == null) {
            synchronized(getPlayerGameStateLost.class){
                instance = getPlayerGameStateLost.instance;
		if (instance == null) 
                    getPlayerGameStateLost.instance = instance = new getPlayerGameStateLost();
            }
        }
	return instance;
    }
    @Override
    public GameState getState() {
        return GameState.LOST;
    }

    @Override
    public boolean canSetGameStopped() {
        return false;
    }

    @Override
    public boolean canSetGamePlace() {
        return false;
    }

    @Override
    public boolean canSetGameWait() {
        return false;
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
        return false;
    }

    @Override
    public boolean canSetGameLost() {
        return true;
    }
    
}
