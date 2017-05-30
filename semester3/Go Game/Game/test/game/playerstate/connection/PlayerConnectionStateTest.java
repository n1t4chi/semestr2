/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.playerstate.connection;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author n1t4chi
 */
public class PlayerConnectionStateTest {
    
    public PlayerConnectionStateTest() {
    }

    /**
     * Test of getPlayerConnectionState method, of class PlayerConnectionState.
     */
    @Test
    public void testGetPlayerConnectionState() {
        System.out.println("getPlayerConnectionState");
        assertSame(PlayerConnectionState.getPlayerConnectionState(PlayerConnectionState.ConnectionState.CONNECTED), getPlayerConnectionStateConnected.getInstance());
        assertSame(PlayerConnectionState.getPlayerConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED), getPlayerConnectionStateDisconnected.getInstance());
        assertSame(PlayerConnectionState.getPlayerConnectionState(PlayerConnectionState.ConnectionState.EMPTY), getPlayerConnectionStateEmpty.getInstance());
        assertSame(PlayerConnectionState.getPlayerConnectionState(PlayerConnectionState.ConnectionState.RECONNECTED), getPlayerConnectionStateReconnected.getInstance());
    }

    /**
     * Test of setState method, of class PlayerConnectionState.
     */
    @Test
    public void testSetState() {
        System.out.println("setState");
        PlayerConnectionState pcs = new PlayerConnectionStateImpl();
        assertSame(pcs, pcs.setState(PlayerConnectionState.ConnectionState.CONNECTED));
        assertSame(pcs, pcs.setState(PlayerConnectionState.ConnectionState.RECONNECTED));
        assertSame(PlayerConnectionState.getPlayerConnectionState(PlayerConnectionState.ConnectionState.EMPTY), pcs.setState(PlayerConnectionState.ConnectionState.EMPTY));
        assertSame(PlayerConnectionState.getPlayerConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED), pcs.setState(PlayerConnectionState.ConnectionState.DISCONNECTED));
    }

    public class PlayerConnectionStateImpl extends PlayerConnectionState {

        @Override
        public ConnectionState getState() {
            return null;
        }

        @Override
        public boolean canSetConnectionEmpty() {
            return true;
        }

        @Override
        public boolean canSetConnectionDisconnected() {
            return true;
        }

        @Override
        public boolean canSetConnectionConnected() {
            return false;
        }

        @Override
        public boolean canSetConnectionReconnected() {
            return false;
        }
    }
    
}
