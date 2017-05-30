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
class getPlayerGameStatePass extends PlayerGameState {
    
    private static volatile getPlayerGameStatePass instance;
    
    private getPlayerGameStatePass(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerGameStatePass getInstance() {
        getPlayerGameStatePass instance = getPlayerGameStatePass.instance;
        if (instance == null) {
            synchronized(getPlayerGameStatePass.class){
                instance = getPlayerGameStatePass.instance;
		if (instance == null) 
                    getPlayerGameStatePass.instance = instance = new getPlayerGameStatePass();
            }
        }
	return instance;
    }

    @Override
    public GameState getState() {
        return GameState.PASS;
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
        return false;
    }

    @Override
    public boolean canSetGamePass() {
        return true;
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
