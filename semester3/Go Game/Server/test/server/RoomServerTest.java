/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import game.MoveNotifier;
import org.junit.Test;
import static org.junit.Assert.*;
import utilities.messages.Message;
import utilities.messages.data.RoomInfo;
import utilities.messages.move.Move;

/**
 *
 * @author n1t4chi
 */
public class RoomServerTest {
    
    public RoomServerTest() {
    }

    /**
     * Test of getRoomInfo method, of class RoomServer.
     */
    @Test
    public void testGetRoomInfo() {
        System.out.println("getRoomInfo");
        RoomServer instance = null;
        RoomInfo expResult = null;
        RoomInfo result = instance.getRoomInfo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateMove method, of class RoomServer.
     */
    @Test
    public void testUpdateMove() {
        System.out.println("updateMove");
        MoveNotifier src = null;
        Move move = null;
        RoomServer instance = null;
        instance.updateMove(src, move);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateMessage method, of class RoomServer.
     */
    @Test
    public void testUpdateMessage() {
        System.out.println("updateMessage");
        MoveNotifier src = null;
        Message msg = null;
        RoomServer instance = null;
        instance.updateMessage(src, msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserversAboutMove method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserversAboutMove() {
        System.out.println("notifySlaveObserversAboutMove");
        Move move = null;
        RoomServer instance = null;
        instance.notifySlaveObserversAboutMove(move);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserverAboutMove_SendToTarget method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserverAboutMove_SendToTarget() {
        System.out.println("notifySlaveObserverAboutMove_SendToTarget");
        MoveNotifier target = null;
        Move move = null;
        RoomServer instance = null;
        instance.notifySlaveObserverAboutMove_SendToTarget(target, move);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserverAboutMessage_SendToTarget method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserverAboutMessage_SendToTarget() {
        System.out.println("notifySlaveObserverAboutMessage_SendToTarget");
        MoveNotifier target = null;
        Message msg = null;
        RoomServer instance = null;
        instance.notifySlaveObserverAboutMessage_SendToTarget(target, msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserverAboutMove_SendToBlackPlayer method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserverAboutMove_SendToBlackPlayer() {
        System.out.println("notifySlaveObserverAboutMove_SendToBlackPlayer");
        Move move = null;
        RoomServer instance = null;
        instance.notifySlaveObserverAboutMove_SendToBlackPlayer(move);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserverAboutMessage_SendToBlackPlayer method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserverAboutMessage_SendToBlackPlayer() {
        System.out.println("notifySlaveObserverAboutMessage_SendToBlackPlayer");
        Message msg = null;
        RoomServer instance = null;
        instance.notifySlaveObserverAboutMessage_SendToBlackPlayer(msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserverAboutMove_SendToWhitePlayer method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserverAboutMove_SendToWhitePlayer() {
        System.out.println("notifySlaveObserverAboutMove_SendToWhitePlayer");
        Move move = null;
        RoomServer instance = null;
        instance.notifySlaveObserverAboutMove_SendToWhitePlayer(move);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserverAboutMessage_SendToWhitePlayer method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserverAboutMessage_SendToWhitePlayer() {
        System.out.println("notifySlaveObserverAboutMessage_SendToWhitePlayer");
        Message msg = null;
        RoomServer instance = null;
        instance.notifySlaveObserverAboutMessage_SendToWhitePlayer(msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserverAboutMove_SendToOppositePlayer method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserverAboutMove_SendToOppositePlayer() {
        System.out.println("notifySlaveObserverAboutMove_SendToOppositePlayer");
        MoveNotifier player = null;
        Move move = null;
        RoomServer instance = null;
        instance.notifySlaveObserverAboutMove_SendToOppositePlayer(player, move);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserverAboutMessage_SendToOppositePlayer method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserverAboutMessage_SendToOppositePlayer() {
        System.out.println("notifySlaveObserverAboutMessage_SendToOppositePlayer");
        MoveNotifier source = null;
        Message msg = null;
        RoomServer instance = null;
        instance.notifySlaveObserverAboutMessage_SendToOppositePlayer(source, msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifySlaveObserversAboutMessage method, of class RoomServer.
     */
    @Test
    public void testNotifySlaveObserversAboutMessage() {
        System.out.println("notifySlaveObserversAboutMessage");
        Message msg = null;
        RoomServer instance = null;
        instance.notifySlaveObserversAboutMessage(msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMasterObserver method, of class RoomServer.
     */
    @Test
    public void testSetMasterObserver() {
        System.out.println("setMasterObserver");
        MoveNotifier observer = null;
        RoomServer instance = null;
        instance.setMasterObserver(observer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addSlaveObserver method, of class RoomServer.
     */
    @Test
    public void testAddSlaveObserver() {
        System.out.println("addSlaveObserver");
        MoveNotifier observer = null;
        RoomServer instance = null;
        instance.addSlaveObserver(observer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifyMasterObserverAboutMove method, of class RoomServer.
     */
    @Test
    public void testNotifyMasterObserverAboutMove() {
        System.out.println("notifyMasterObserverAboutMove");
        Move move = null;
        RoomServer instance = null;
        instance.notifyMasterObserverAboutMove(move);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifyMasterObserverAboutMessage method, of class RoomServer.
     */
    @Test
    public void testNotifyMasterObserverAboutMessage() {
        System.out.println("notifyMasterObserverAboutMessage");
        Message msg = null;
        RoomServer instance = null;
        instance.notifyMasterObserverAboutMessage(msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isWorking method, of class RoomServer.
     */
    @Test
    public void testIsWorking() {
        System.out.println("isWorking");
        RoomServer instance = null;
        boolean expResult = false;
        boolean result = instance.isWorking();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initSubclass method, of class RoomServer.
     */
    @Test
    public void testInitSubclass() throws Exception {
        System.out.println("initSubclass");
        RoomServer instance = null;
        instance.initSubclass();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of restartSubclass method, of class RoomServer.
     */
    @Test
    public void testRestartSubclass() throws Exception {
        System.out.println("restartSubclass");
        RoomServer instance = null;
        instance.restartSubclass();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of disposeSubclass method, of class RoomServer.
     */
    @Test
    public void testDisposeSubclass() throws Exception {
        System.out.println("disposeSubclass");
        RoomServer instance = null;
        instance.disposeSubclass();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of interpret method, of class RoomServer.
     */
    @Test
    public void testInterpret_String_StringArr() {
        System.out.println("interpret");
        String command = "";
        String[] parameters = null;
        RoomServer instance = null;
        instance.interpret(command, parameters);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of interpret method, of class RoomServer.
     */
    @Test
    public void testInterpret_String() {
        System.out.println("interpret");
        String input = "";
        RoomServer instance = null;
        instance.interpret(input);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
