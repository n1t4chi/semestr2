/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.Room;

import client.room.RoomGUI;
import game.Board;
import game.MoveNotifier;
import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.game.PlayerGameState;
import javax.swing.SwingUtilities;
import utilities.messages.Message;
import utilities.messages.PlayerConnected;
import utilities.messages.ReturnOpponentName;
import utilities.messages.client.ReturnConnectionState;
import utilities.messages.client.ReturnGameState;
import utilities.messages.client.ReturnPing;
import utilities.messages.client.ReturnScore;
import utilities.messages.move.Move;

/**
 * This class provides basic possibility to test RoomGUI. It must be run with Run File since it is not a JUnit test class.
 * @author n1t4chi
 */
public class RoomGUITest {
    
    RoomGUI gui;
    MoveNotifier master;
    public RoomGUITest() {
        
        master = new MoveNotifier() {
            @Override
            public void setMasterObserver(MoveNotifier observer) {}
            @Override
            public void addSlaveObserver(MoveNotifier observer) {}
            @Override
            public void notifyMasterObserverAboutMove(Move move) {}
            @Override
            public void notifyMasterObserverAboutMessage(Message msg) {}
            @Override
            public void updateMove(MoveNotifier src, Move move) {
                
            }
            @Override
            public void updateMessage(MoveNotifier src, Message msg) {
                System.err.println("Got message: ["+msg.getMessageType()+"]"+msg.getMessage());
                switch(msg.getMessageType()){
                    case QUIT_GAME:
                            gui.dispose();
                        break;
                    case REQUEST_PING:
                            gui.updateMessage(this, new ReturnPing(10));
                        break;
                    case REQUEST_CONNECTION_STATE:
                            gui.updateMessage(this, new ReturnConnectionState(PlayerConnectionState.ConnectionState.CONNECTED));
                        break;
                    case REQUEST_GAME_STATE:
                            ReturnGameState gs=null;
                            gs = new ReturnGameState(PlayerGameState.GameState.PLACE);
                            gui.updateMessage(this, gs);
                        break;
                    case REQUEST_OPPONENT_NAME:
                            gui.updateMessage(this, new ReturnOpponentName("test opponent"));
                        break;
                    case REQUEST_SCORE:
                            gui.updateMessage(this, new ReturnScore(1337, 9001));
                        break;
                }
            }

            @Override
            public void notifySlaveObserversAboutMove(Move move) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void notifySlaveObserversAboutMessage(Message msg) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        Board board = new Board(Board.Size.SMALL);
        for(int i=0; i<5; i++){
            board.changeField(6, i, Board.BoardField.WHITE);
            board.changeField(5-i, i, Board.BoardField.BLACK);
        }
        
        this.gui = new RoomGUI(master,"test user",board,true, Board.Size.SMALL);
        this.gui.setVisible(true);
        this.gui.setEnabled(true);
        this.gui.pack();
        this.gui.repaint();
        
        gui.updateMessage(master, new PlayerConnected("Test opponent"));
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RoomGUITest t = new RoomGUITest();
        });
    }
}
