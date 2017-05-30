/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import game.Board;
import game.MoveNotifier;
import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.game.PlayerGameState;
import java.awt.Point;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import utilities.messages.*;
import utilities.messages.client.*;
import utilities.messages.move.*;
import utilities.messages.move.Move;

/**
 *
 * @author n1t4chi
 */
public class SocketReadWriteTest {
    
    
    @Test
    public void TestRequestMessage() {
        ms = new MoveNotifier() {
            @Override
            public void setMasterObserver(MoveNotifier observer) {}
            @Override
            public void addSlaveObserver(MoveNotifier observer) {}
            @Override
            public void notifyMasterObserverAboutMove(Move move) {}
            @Override
            public void notifySlaveObserversAboutMove(Move move) {}
            @Override
            public void notifyMasterObserverAboutMessage(Message msg) {}
            @Override
            public void notifySlaveObserversAboutMessage(Message msg) {}
            @Override
            public void updateMove(MoveNotifier src, Move move) {}

            @Override
            public void updateMessage(MoveNotifier src, Message msg) {
                if((src == ss)&&(msg.getMessageType() == MessageType.ADD_AI))
                    ss.updateMessage(ms, new RequestAI());
            }
        };
    
        try {
            soc  = new ServerSocket(0);
        } catch (IOException ex) {
            fail("Couldn't initialise server socket");
        }
        Executors.newSingleThreadExecutor().execute(()->{
            try {
                ss = new Socket(ms,soc.accept(),10);
            } catch (IOException ex) {
                fail("Couldn't initialise socket for server");
            }
        });
        cs = new Socket(ms,"127.0.0.1",soc.getLocalPort(),10);
        cs.updateMessage(ms, new RequestAI());
        Message msg = cs.requestMessage(MessageType.ADD_AI);
        assertNotNull(msg);
        
    }    
    @Test
    public void TestCheckPing() {
        ms = new MoveNotifier() {
            @Override
            public void setMasterObserver(MoveNotifier observer) {}
            @Override
            public void addSlaveObserver(MoveNotifier observer) {}
            @Override
            public void notifyMasterObserverAboutMove(Move move) {}
            @Override
            public void notifySlaveObserversAboutMove(Move move) {}
            @Override
            public void notifyMasterObserverAboutMessage(Message msg) {}
            @Override
            public void notifySlaveObserversAboutMessage(Message msg) {}
            @Override
            public void updateMove(MoveNotifier src, Move move) {}

            @Override
            public void updateMessage(MoveNotifier src, Message msg) {
                if((msg instanceof Ping)||(msg instanceof Pong))
                    fail("Failed");
            }
        };
    
        try {
            soc  = new ServerSocket(0);
        } catch (IOException ex) {
            fail("Couldn't initialise server socket");
        }
        Executors.newSingleThreadExecutor().execute(()->{
            try {
                ss = new Socket(ms,soc.accept(),10);
            } catch (IOException ex) {
                fail("Couldn't initialise socket for server");
            }
        });
        cs = new Socket(ms,"127.0.0.1",soc.getLocalPort(),10);
        long ping = cs.checkPing();
        System.out.println("Ping:"+ping);
        assertNotEquals(-1,ping);
        
    }
    
    
    ServerSocket soc;
    Socket ss;
    Socket cs;
    MoveNotifier ms;
    
    int move_it=0;
    int msg_it = 0;

    @After
    public void after(){
        try {
            cs.Disconnect();
            ss.Disconnect();
            cs = null;
            ss = null;
            soc.close();
            soc = null;
        } catch (IOException | SocketException ex) {
            fail("Erro on closing sockets");
        }
        
    }
    
    @Test
    public void TestSendReceiveAllMessages() {
        Message[] mg={
        new ChatMessage(ChatMessage.ChatSource.ERROR,"test")
        ,new PlayerConnected("test")
        ,new PlayerDisconnected()
        ,new RequestAI()
        ,new RequestBoard()
        ,new RequestOpponentName()
        ,new RequestRoomList()
        ,new ReturnBoard(new Board(Board.Size.BIG))
        ,new ReturnOpponentName("test")
        ,new ReturnRoomList()
        ,new RoomCreate("test",Board.Size.BIG,true,true)
        ,new ServerShutdown()
        ,new QuitGame()
        ,new RequestConnectionState()
        ,new RequestDefaultTerritories()
        ,new RequestGameState()
        ,new RequestLegalPlaces()
        ,new RequestScore()
        ,new ReturnConnectionState(PlayerConnectionState.ConnectionState.CONNECTED)
        ,new ReturnDefaultTerritories(new ArrayList<Point>(),new ArrayList<Point>())
        ,new ReturnGameState(PlayerGameState.GameState.AGREE)
        ,new ReturnLegalPlaces()
        ,new ReturnScore(1,1)};
        this.mg = mg;
         Move[] mv={
        new ChooseTerritories()
        ,new InvalidMove()
        ,new Lose()
        ,new MakeMove()
        ,new Pass()
        ,new PlaceStone(true,1,1)
        ,new ReturnToStonePlacing()
        ,new Surrender()
        ,new TerritoriesAgree()
        ,new TerritoriesChosen()
        ,new TerritoriesDisagree()
        ,new ValidMove()
        ,new Win()
        };
        this.mv = mv;
        ms = new MoveNotifier() {
            @Override
            public void setMasterObserver(MoveNotifier observer) {}
            @Override
            public void addSlaveObserver(MoveNotifier observer) {}
            @Override
            public void notifyMasterObserverAboutMove(Move move) {}
            @Override
            public void notifySlaveObserversAboutMove(Move move) {}
            @Override
            public void notifyMasterObserverAboutMessage(Message msg) {}
            @Override
            public void notifySlaveObserversAboutMessage(Message msg) {}
            @Override
            public void updateMove(MoveNotifier src, Move move) {
                if(move==null){
                    ss.updateMove(ms,mv[move_it]);
                    move_it++;
                }else{
                    //System.out.println("Master received:"+move);
                    if(move_it<mv.length){
                        if(src == ss)
                            cs.updateMove(ms,mv[move_it]);
                        else
                            ss.updateMove(ms,mv[move_it]);
                    }
                    move_it++;
                }
            }

            @Override
            public void updateMessage(MoveNotifier src, Message msg) {
                
                if(msg==null){
                    ss.updateMessage(ms,mg[msg_it]);
                    msg_it++;
                }else{
                    //System.out.println("Master received:"+msg);
                    if(msg_it<mg.length){
                        if(src == ss)
                            cs.updateMessage(ms,mg[msg_it]);
                        else
                            ss.updateMessage(ms,mg[msg_it]);
                    }
                    msg_it++;
                }
            }
        };
        
        
        try {
            soc  = new ServerSocket(0);
        } catch (IOException ex) {
            fail("Couldn't initialise server socket");
        }
        Executors.newSingleThreadExecutor().execute(()->{
            try {
                ss = new Socket(ms,soc.accept(),10);
            } catch (IOException ex) {
                fail("Couldn't initialise socket for server");
            }
        });
        cs = new Socket(ms,"127.0.0.1",soc.getLocalPort(),10);
        
        try{
            while(ss==null){
                Thread.sleep(10);
            }
            System.out.println("Sending messages");
            ms.updateMessage(null, null);
            while(msg_it<mg.length){
                Thread.sleep(10);
            }
            System.out.println("Sending move messages");
            ms.updateMove(null, null);
            while(move_it<mv.length){
                Thread.sleep(10);
            }
            
            
            
        }catch(Exception ex){
            System.err.println("exception:"+ex);
            ex.printStackTrace();
            fail("exception");
        }
    }
    Move[]mv;
    Message[] mg;
  
}
