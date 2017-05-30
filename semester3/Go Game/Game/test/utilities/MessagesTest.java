/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import utilities.messages.PlayerConnected;
import utilities.messages.ReturnOpponentName;
import utilities.messages.RequestOpponentName;
import utilities.messages.PlayerDisconnected;
import game.Board;
import game.Board.BoardField;
import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.connection.PlayerConnectionState.ConnectionState;
import game.playerstate.game.PlayerGameState;
import game.playerstate.game.PlayerGameState.GameState;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;
import utilities.messages.*;
import utilities.messages.client.*;
import utilities.messages.data.FieldInfo;
import utilities.messages.data.RoomInfo;
import utilities.messages.move.ChooseTerritories;
import utilities.messages.move.InvalidMove;
import utilities.messages.move.Lose;
import utilities.messages.move.MakeMove;
import utilities.messages.move.MessageMoveType;
import utilities.messages.move.Move;
import utilities.messages.move.PlaceStone;
import utilities.messages.move.ReturnToStonePlacing;
import utilities.messages.move.Surrender;
import utilities.messages.move.TerritoriesAgree;
import utilities.messages.move.TerritoriesChosen;
import utilities.messages.move.TerritoriesDisagree;
import utilities.messages.move.ValidMove;
import utilities.messages.move.Win;

/**
 * Test class for all messages
 * @author n1t4chi
 */
public class MessagesTest {
    
    @Test
    public void testChatMessage(){
        Message msg = new ChatMessage(ChatMessage.ChatSource.ERROR,"Test");
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.CHAT);
        Serializable rtrn_msg = msg.getMessage();
        assertEquals(rtrn_msg,"Test");
        
    }

    @Test
    public void testPing(){
        Message msg = new Ping();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.PING);
    }
    @Test
    public void testPong(){
        Message msg = new Pong(new Ping());
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.PONG);
    }
    @Test
    public void testRequestBoard(){
        Message msg = new RequestBoard();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.REQUEST_BOARD);
        /*
        Serializable rtrn_msg = msg.getMessage();
        assertEquals(rtrn_msg,);
        */
    }
    @Test
    public void testRequestRoomList(){
        Message msg = new RequestRoomList();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.REQUEST_LIST_ROOM);
        /*
        Serializable rtrn_msg = msg.getMessage();
        assertEquals(rtrn_msg,);
        */
    }
    @Test
    public void testReturnBoard(){
        Board board = new Board(Board.Size.BIG);
        board.changeField(6, 7, Board.BoardField.BLACK);
        board.changeField(5, 5, Board.BoardField.WHITE);
        Message msg = new ReturnBoard(board);
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.RETURN_BOARD);
        
        Serializable rtrn_msg = msg.getMessage();
        try{
            assertEquals(((BoardField[][])rtrn_msg)[6][7],Board.BoardField.BLACK);
            assertEquals(((BoardField[][])rtrn_msg)[5][5],Board.BoardField.WHITE);
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
    }
    @Test
    public void testReturnRoomList(){
        ArrayList<RoomInfo> al = new ArrayList();
        RoomInfo r1 = new RoomInfo(1,2,Board.Size.BIG,"test","b1","b2");
        RoomInfo r2 = new RoomInfo(3,4,Board.Size.MODERATE,"test2","b3","b4");
        al.add(r1);
        al.add(r2);
        Message msg = new ReturnRoomList(al);
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.RETURN_LIST_ROOMS);
        Serializable rtrn_msg = msg.getMessage();
        try{
            assertTrue(((ArrayList<RoomInfo>)rtrn_msg).contains(r2));
            assertTrue(((ArrayList<RoomInfo>)rtrn_msg).contains(r1));
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
        
        msg = new ReturnRoomList();
        ((ReturnRoomList)msg).addRoomInfo(r1);
        ((ReturnRoomList)msg).addRoomInfo(r2);
        rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.RETURN_LIST_ROOMS);
        rtrn_msg = msg.getMessage();
        try{
            assertTrue(((ArrayList<RoomInfo>)rtrn_msg).contains(r2));
            assertTrue(((ArrayList<RoomInfo>)rtrn_msg).contains(r1));
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
        
        
        
    }
    @Test
    public void testRoomCreate(){
        Message msg = new RoomCreate("Test", Board.Size.BIG,false,false);
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.CREATE_ROOM);
        
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertEquals(rtrn_msg,"Test:BIG");
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
        
    }
    @Test
    public void testPlayerConnected(){
        Message msg = new PlayerConnected("test");
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.PLAYER_CONNECTED);
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertEquals(rtrn_msg,"test");
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
    }
    @Test
    public void testPlayerDisconnected(){
        Message msg = new PlayerDisconnected();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.PLAYER_DISCONNECTED);
    }
    @Test
    public void testRequestOpponentName(){
        Message msg = new RequestOpponentName();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.REQUEST_OPPONENT_NAME);
    }
    @Test
    public void testReturnOpponentName(){
        Message msg = new ReturnOpponentName("test");
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.RETURN_OPPONENT_NAME);
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertEquals(rtrn_msg,"test");
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
    }
    @Test
    public void testRequestConnectionState(){
        Message msg = new RequestConnectionState();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.REQUEST_CONNECTION_STATE);
    }
    @Test
    public void testRequestGameState(){
        Message msg = new RequestGameState();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.REQUEST_GAME_STATE);
    }
    @Test
    public void testRequestDefaultTerritories(){
        Message msg = new RequestDefaultTerritories();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.REQUEST_DEFAULT_TERRITORIES);
    }
    @Test
    public void testRequestPing(){
        Message msg = new RequestPing();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.REQUEST_PING);
    }
    @Test
    public void testRequestScore(){
        Message msg = new RequestScore();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.REQUEST_SCORE);
    }
   
    @Test
    public void testReturnConnectionState(){
        for(ConnectionState s : ConnectionState.values()){
            Message msg = new ReturnConnectionState(s);
            MessageType rtrn_type = msg.getMessageType();
            assertEquals(rtrn_type,MessageType.RETURN_CONNECTION_STATE);
            try{
                Serializable rtrn_msg = msg.getMessage();
                assertEquals(rtrn_msg,s);
            }catch(Exception ex){
                fail("Exception was thrown:"+ex);
            }
        }
    }
    @Test
    public void testReturnGameState(){
        for(GameState s : GameState.values()){
            Message msg = new ReturnGameState(s);
            MessageType rtrn_type = msg.getMessageType();
            assertEquals(rtrn_type,MessageType.RETURN_GAME_STATE);
            try{
                Serializable rtrn_msg = msg.getMessage();
                assertEquals(rtrn_msg,s);
            }catch(Exception ex){
                fail("Exception was thrown:"+ex);
            }
        }
    }
    
    
    @Test
    public void testReturnDefaultTerritories(){
        Message msg = new ReturnDefaultTerritories(new ArrayList(), new ArrayList());
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.RETURN_DEFAULT_TERRITORIES);
        
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertNotNull(rtrn_msg);
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
        
    }
    @Test
    public void testReturnLegalPlaces(){
        Message msg = new ReturnLegalPlaces();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.RETURN_LEGAL_PLACES);
        FieldInfo f1 = new FieldInfo(BoardField.BLACK,new Point(5, 4));
        FieldInfo f2 = new FieldInfo(BoardField.WHITE,3,3);
        ((ReturnLegalPlaces)msg).addLegalPlace(f1);
        ((ReturnLegalPlaces)msg).addLegalPlace(f2);
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertTrue(((ArrayList<FieldInfo>)rtrn_msg).contains(f1));
            assertTrue(((ArrayList<FieldInfo>)rtrn_msg).contains(f2));
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
        
    }
    @Test
    public void testReturnPing(){
        Message msg = new ReturnPing(10);
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.RETURN_PING);
        
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertEquals((Long)(long)10,rtrn_msg);
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
        
    }
    @Test
    public void testReturnScore(){
        Message msg = new ReturnScore(10,10);
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.RETURN_SCORE);
        
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertEquals(rtrn_msg,"10.0:10.0");
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        }
    }
    
    @Test
    public void testMoveChooseTerritories(){
        Message msg = new ChooseTerritories();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.CHOOSE_TERRITORIES);
    }
    @Test
    public void testMoveInvalidMove(){
        Message msg = new InvalidMove();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.INVALID_MOVE);
    }
    @Test
    public void testMoveValidMove(){
        Message msg = new ValidMove();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.VALID_MOVE);
    }
    @Test
    public void testMoveLose(){
        Message msg = new Lose();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.LOSE);
    }
    @Test
    public void testMoveWin(){
        Message msg = new Win();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.WIN);
    }
    @Test
    public void testMoveSurrender(){
        Message msg = new Surrender();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.SURRENDER);
    }
    @Test
    public void testMoveReturnToStonePlacing(){
        Message msg = new ReturnToStonePlacing();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.RETURN_TO_STONE_PLACING);
    }
    @Test
    public void testMoveTerritoriesAgree(){
        Message msg = new TerritoriesAgree();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.TERRITORIES_AGREE);
    }
    @Test
    public void testMoveTerritoriesDisagree(){
        Message msg = new TerritoriesDisagree();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.TERRITORIES_DISAGREE);
    }
    @Test
    public void testMoveMakeMove(){
        Message msg = new MakeMove();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.MAKE_MOVE);
    }
    
    
    
    @Test
    public void testMovePlaceStone(){
        Message msg = new PlaceStone(false, 10, 5);
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.PLACE_STONE);
        
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertEquals(rtrn_msg,"WHITE:10:5");
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        } 
    }
    
    
    @Test
    public void testMoveTerritoriesChosen(){
        Message msg = new TerritoriesChosen();
        MessageType rtrn_type = msg.getMessageType();
        MessageMoveType rtrn_type_move = ((Move)msg).getMoveType();
        assertEquals(rtrn_type,MessageType.MOVE);
        assertEquals(rtrn_type_move,MessageMoveType.TERRITORIES_CHOOSEN);
        fail("TODO");
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertEquals(rtrn_msg,"WHITE:10:5");
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        } 
    }
    @Test
    public void testRequestAI(){
        Message msg = new RequestAI();
        MessageType rtrn_type = msg.getMessageType();
        assertEquals(rtrn_type,MessageType.ADD_AI);
        fail("TODO");
        try{
            Serializable rtrn_msg = msg.getMessage();
            assertEquals(rtrn_msg,"WHITE:10:5");
        }catch(Exception ex){
            fail("Exception was thrown:"+ex);
        } 
    }
    
    
    
    
    
}
