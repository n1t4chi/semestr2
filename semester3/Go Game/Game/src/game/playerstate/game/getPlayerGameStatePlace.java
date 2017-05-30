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
class getPlayerGameStatePlace extends PlayerGameState {
    
    private static volatile getPlayerGameStatePlace instance;
    
    private getPlayerGameStatePlace(){
    }
    
    /**
     * Returns the instance of this class
     * @return instane of this class
     */
    public static getPlayerGameStatePlace getInstance() {
        getPlayerGameStatePlace instance = getPlayerGameStatePlace.instance;
        if (instance == null) {
            synchronized(getPlayerGameStatePlace.class){
                instance = getPlayerGameStatePlace.instance;
		if (instance == null) 
                    getPlayerGameStatePlace.instance = instance = new getPlayerGameStatePlace();
            }
        }
	return instance;
    }

    @Override
    public GameState getState() {
        return GameState.PLACE;
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
        return true;
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
