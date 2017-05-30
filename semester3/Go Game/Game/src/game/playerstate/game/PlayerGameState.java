/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.playerstate.game;

/**
 * Abstract class for game state handling.
 * @author n1t4chi
 */
public abstract class PlayerGameState {
    
    
    /**
     * Returns instance of a class corresponding with param GameState
     * @param state GameState
     * @return instance of a class corresponding with param GameState
     */
    public static PlayerGameState getPlayerGameState(GameState state){
        
        PlayerGameState rtrn = null;
        switch(state){
            case STOPPED:
                    rtrn = getPlayerGameStateStopped.getInstance();
                break;
            case PLACE:
                    rtrn = getPlayerGameStatePlace.getInstance();
                break;
            case WAIT:
                    rtrn = getPlayerGameStateWait.getInstance();
                break;
            case PASS:
                    rtrn = getPlayerGameStatePass.getInstance();
                break;
            case CHOOSE:
                    rtrn = getPlayerGameStateChoose.getInstance();
                break;
            case AGREE:
                    rtrn = getPlayerGameStateAgree.getInstance();
                break;
            case DISAGREE:
                    rtrn = getPlayerGameStateDisagree.getInstance();
                break;
            case WON:
                    rtrn = getPlayerGameStateWon.getInstance();
                break;
            case LOST:
                    rtrn = getPlayerGameStateLost.getInstance();
                break;
        }
        return rtrn;
        
          
    }
    
    
    
    /**
     * Represents connection status of a player.
     */
    public enum GameState{
        STOPPED,
        PLACE,
        WAIT,
        PASS,
        CHOOSE,
        AGREE,
        DISAGREE,
        WON,
        LOST
    };
    
    /**
     * Returns current state.
     * @return current state.
     */
    public abstract GameState getState();
    /**
     * Changes state.
     * @param state state to change to.
     * @return new PlayerGameState.
     */
    public PlayerGameState setState(GameState state){
        PlayerGameState rtrn = this;
        switch(state){
            case AGREE: rtrn=(canSetGameAgree())?getPlayerGameState(GameState.AGREE):rtrn; break;
            case CHOOSE: rtrn=(canSetGameChose())?getPlayerGameState(GameState.CHOOSE):rtrn; break;
            case DISAGREE: rtrn=(canSetGameDisagree())?getPlayerGameState(GameState.DISAGREE):rtrn; break;
            case LOST: rtrn=(canSetGameLost())?getPlayerGameState(GameState.LOST):rtrn; break;
            case PASS: rtrn=(canSetGamePass())?getPlayerGameState(GameState.PASS):rtrn; break;
            case PLACE: rtrn=(canSetGamePlace())?getPlayerGameState(GameState.PLACE):rtrn; break;
            case STOPPED: rtrn=(canSetGameStopped())?getPlayerGameState(GameState.STOPPED):rtrn; break;
            case WAIT: rtrn=(canSetGameWait())?getPlayerGameState(GameState.WAIT):rtrn; break;
            case WON: rtrn=(canSetGameWon())?getPlayerGameState(GameState.WON):rtrn; break;
        }
        return rtrn;
    }
    
    /**
     * Check if it is possible to change state to stopped.
     * @return whether it is possible.
     */
    public abstract boolean canSetGameStopped();
    /**
     * Check if it is possible to change state to move.
     * @return whether it is possible.
     */
    public abstract boolean canSetGamePlace();
    /**
     * Check if it is possible to change state to wait.
     * @return whether it is possible.
     */
    public abstract boolean canSetGameWait();
    /**
     * Check if it is possible to change state to pass.
     * @return whether it is possible.
     */
    public abstract boolean canSetGamePass();
    /**
     * Check if it is possible to change state to chose.
     * @return whether it is possible.
     */
    public abstract boolean canSetGameChose();
    /**
     * Check if it is possible to change state to agree.
     * @return whether it is possible.
     */
    public abstract boolean canSetGameAgree();
    /**
     * Check if it is possible to change state to disagree.
     * @return whether it is possible.
     */
    public abstract boolean canSetGameDisagree();
    /**
     * Check if it is possible to change state to won.
     * @return whether it is possible.
     */
    public abstract boolean canSetGameWon();
    /**
     * Check if it is possible to change state to lost.
     * @return whether it is possible.
     */
    public abstract boolean canSetGameLost();
}
