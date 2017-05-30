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
class getPlayerGameStateAgree extends PlayerGameState {
    
    private static volatile getPlayerGameStateAgree instance;
    
    private getPlayerGameStateAgree(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerGameStateAgree getInstance() {
        getPlayerGameStateAgree instance = getPlayerGameStateAgree.instance;
        if (instance == null) {
            synchronized(getPlayerGameStateAgree.class){
                instance = getPlayerGameStateAgree.instance;
		if (instance == null) 
                     getPlayerGameStateAgree.instance = instance = new getPlayerGameStateAgree();
            }
        }
	return instance;
    }

    @Override
    public GameState getState() {
        return GameState.AGREE;
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
        return true;
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
