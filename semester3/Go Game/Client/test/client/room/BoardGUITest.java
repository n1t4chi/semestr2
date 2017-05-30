/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.room;

import game.Board;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This class provides basic possibility to test BoardGUI. It must be run with Run File since it is not a JUnit test class.
 * @author n1t4chi
 */
public class BoardGUITest {
    BoardGUI gui;
    public BoardGUITest() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Board board = new Board(Board.Size.SMALL);
        /*for(int i=0;i<5;i++){
            board.changeField(i, i, Board.BoardField.BLACK);
            board.changeField(6, i, Board.BoardField.WHITE);
        }*/
        gui = new BoardGUI(null, true, Board.Size.SMALL);
        frame.add(gui);
        
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setMinimumSize(new Dimension(500, 500));
        
        gui.setBoardGUIstate(BoardGUI.BoardGUIstate.PLAY);
        
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            BoardGUITest t = new BoardGUITest();
        });
        
    }
}
