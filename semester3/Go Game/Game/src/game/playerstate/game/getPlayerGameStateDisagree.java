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
class getPlayerGameStateDisagree extends PlayerGameState {
    
    private static getPlayerGameStateDisagree instance;
    
    private getPlayerGameStateDisagree(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerGameStateDisagree getInstance() {
        if (instance == null) {
            synchronized(getPlayerGameStateDisagree.class){
		if (instance == null) 
                    instance = instance = new getPlayerGameStateDisagree();
            }
        }
	return instance;
    }

    @Override
    public GameState getState() {
        return GameState.DISAGREE;
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
        return true;
    }

    @Override
    public boolean canSetGameAgree() {
        return false;
    }

    @Override
    public boolean canSetGameDisagree() {
        return true;
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
