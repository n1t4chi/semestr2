/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.playerstate.game;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author n1t4chi
 */
public class PlayerGameStateTest {
    
    public PlayerGameStateTest() {
    }

    /**
     * Test of getPlayerGameState method, of class PlayerGameState.
     */
    @Test
    public void testGetPlayerGameState() {
        System.out.println("getPlayerGameState");
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.STOPPED), getPlayerGameStateStopped.getInstance());
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.CHOOSE), getPlayerGameStateChoose.getInstance());
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.AGREE), getPlayerGameStateAgree.getInstance());
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.DISAGREE), getPlayerGameStateDisagree.getInstance());
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.LOST), getPlayerGameStateLost.getInstance());
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.PASS), getPlayerGameStatePass.getInstance());
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.PLACE), getPlayerGameStatePlace.getInstance());
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.WAIT), getPlayerGameStateWait.getInstance());
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.WON), getPlayerGameStateWon.getInstance());
    }

    /**
     * Test of setState method, of class PlayerGameState.
     */
    @Test
    public void testSetState() {
        System.out.println("setState");
        PlayerGameState pgs = new PlayerGameStateImpl();
        
        assertSame(pgs, pgs.setState(PlayerGameState.GameState.WON));
        assertSame(pgs, pgs.setState(PlayerGameState.GameState.AGREE));
        assertSame(pgs, pgs.setState(PlayerGameState.GameState.PASS));
        assertSame(pgs, pgs.setState(PlayerGameState.GameState.PLACE));
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.LOST), pgs.setState(PlayerGameState.GameState.LOST));
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.DISAGREE), pgs.setState(PlayerGameState.GameState.DISAGREE));
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.CHOOSE), pgs.setState(PlayerGameState.GameState.CHOOSE));
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.WAIT), pgs.setState(PlayerGameState.GameState.WAIT));
        assertSame(PlayerGameState.getPlayerGameState(PlayerGameState.GameState.STOPPED), pgs.setState(PlayerGameState.GameState.STOPPED));
        
    }
    public class PlayerGameStateImpl extends PlayerGameState {

        @Override
        public GameState getState() {
            return null;
        }

        @Override
        public boolean canSetGameStopped() {
            return true;
        }

        @Override
        public boolean canSetGamePlace() {
            return false;
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
            return false;
        }

        @Override
        public boolean canSetGameLost() {
            return true;
        }
    }
    
}
