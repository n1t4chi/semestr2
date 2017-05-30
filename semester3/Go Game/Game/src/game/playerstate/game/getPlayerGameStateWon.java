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
class getPlayerGameStateWon extends PlayerGameState {
    
    private static volatile getPlayerGameStateWon instance;
    
    private getPlayerGameStateWon(){
    }
    
    /**
     * Returns the instance of this class
     * @return instance of this class
     */
    public static getPlayerGameStateWon getInstance() {
        getPlayerGameStateWon instance = getPlayerGameStateWon.instance;
        if (instance == null) {
            synchronized(getPlayerGameStateWon.class){
                instance = getPlayerGameStateWon.instance;
		if (instance == null) 
                    getPlayerGameStateWon.instance = instance = new getPlayerGameStateWon();
            }
        }
	return instance;
    }

    @Override
    public GameState getState() {
        return GameState.WON;
    }

    @Override
    public boolean canSetGameStopped() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return true;
    }

    @Override
    public boolean canSetGameLost() {
        return false;
    }
    
}
