/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.Board.BoardField;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import utilities.messages.client.ReturnDefaultTerritories;
import utilities.messages.data.FieldInfo;
import utilities.messages.move.PlaceStone;

/**
 *
 * @author n1t4chi
 */
public class BoardTest {
    
    public BoardTest() {
    }
    public Board getBoard(){
        return new Board(Board.Size.SMALL);
    }
    /**
     * Test of getBoard method, of class Board.
     */
    @Test
    public void testGetBoard() {
        System.out.println("getBoard");
        Board instance = getBoard();
        assertNotSame("Cloned board two times is the same",instance.getBoard(), instance.getBoard());
    }

    /**
     * Test of getField method, of class Board.
     */
    @Test
    public void testGetField() {
        System.out.println("getField");
        Board instance = getBoard();
        try{
            instance.getField(5,-1);
            fail("Exception was not thrown after passing negative argument");
        }catch(IllegalArgumentException e){}
        try{
            instance.getField(35,3);
            fail("Exception was not thrown after passing argument outside upper limit bound.");
        }catch(IllegalArgumentException e){}
        try{
            instance.getField(1,1);
        }catch(Exception e){
            fail("Exception was thrown after passing valid arugments :"+e.getMessage());
        }
        
    }

    /**
     * Test of isLegalMove method, of class Board.
     */
    @Ignore
    public void testIsLegalMove() {
        System.out.println("isLegalMove");
        boolean black = true;
        int x = 0;
        int y = 0;
        Board instance = getBoard();
        instance.changeField(3,1,BoardField.WHITE);
        instance.changeField(2,2,BoardField.WHITE);
        instance.changeField(4,2,BoardField.WHITE);
        instance.changeField(3,3,BoardField.WHITE);
        
        boolean expResult = true;
        boolean result = instance.isLegalMove(BoardField.BLACK, x, y);
        assertEquals(expResult, result);
        
        x = 3;
        y = 2;
        expResult = false;
        result = instance.isLegalMove(BoardField.BLACK, x, y);
        assertEquals(expResult, result);
        
        instance.changeField(4,0,BoardField.WHITE);
        instance.changeField(2,0,BoardField.WHITE);
        
        x = 3;
        y = 0;
        expResult = false;
        result = instance.isLegalMove(BoardField.BLACK, x, y);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countBreaths method of Board class
     */
    @Test
    public void testCountBreaths(){
        System.out.println("countBreaths");
        Board instance = getBoard();
        instance.changeField(3,1,BoardField.WHITE);
        instance.changeField(3,2,BoardField.WHITE);
        instance.changeField(4,2,BoardField.BLACK);
        
        assertEquals(5, instance.countBreaths(BoardField.WHITE, 3,2,new HashSet(), new HashSet()));
       
        assertEquals(2, instance.countBreaths(BoardField.BLACK, 0,0,new HashSet(), new HashSet()));
        
        assertEquals(2, instance.countBreaths(BoardField.BLACK, 8,8,new HashSet(), new HashSet()));
        
        assertEquals(3, instance.countBreaths(BoardField.BLACK, 4,2,new HashSet(), new HashSet()));
    }
    
    /**
     * Test of canCaptureStones method of Board class
     */
    @Test
    public void testCanCaptureStones(){
        System.out.println("captureStones");
        Board instance = getBoard();
        instance.changeField(3,1,BoardField.WHITE);
        instance.changeField(3,2,BoardField.WHITE);
        instance.changeField(4,2,BoardField.BLACK);
        instance.changeField(4,1,BoardField.BLACK);
        instance.changeField(3,0,BoardField.BLACK);
        instance.changeField(2,1,BoardField.BLACK);
        instance.changeField(2,2,BoardField.BLACK);
        
        assertEquals(true, instance.canCaptureStones(BoardField.BLACK, 3, 3));
        
        assertEquals(false, instance.canCaptureStones(BoardField.WHITE, 3, 3));
        
        assertEquals(false, instance.canCaptureStones(BoardField.BLACK, 4, 0));
    }
    
    /**
     * Test of the chain method of Board class
     */
    @Test
    public void testChain(){
        System.out.println("chain");
        Board instance = getBoard();
        fillBoard(instance);
        instance.changeField(4, 0, BoardField.EMPTY);
        
        ArrayList<Point> list = instance.chain(4,1, new ArrayList<>());
        
        StringBuilder sb = new StringBuilder();
        for(Point point : list)
            sb.append("(").append(point.x).append(", ").append(point.y).append(");");
        
        String expResult = "(4, 1);(4, 2);(4, 3);";
        assertEquals(expResult, sb.toString());
        
        list = instance.chain(3,0, new ArrayList<>());
        
        sb.setLength(0);
        sb = new StringBuilder();
        for(Point point : list)
            sb.append("(").append(point.x).append(", ").append(point.y).append(");");
        
        expResult = "(3, 0);";
        assertEquals(expResult, sb.toString());
        
        list = instance.chain(0,0, new ArrayList<>());
        
        sb.setLength(0);
        sb = new StringBuilder();
        for(Point point : list)
            sb.append("(").append(point.x).append(", ").append(point.y).append(");");
        
        expResult = "";
        assertEquals(expResult, sb.toString());
        
        instance.changeField(4,0,BoardField.BLACK);
        list = instance.chain(4,1, new ArrayList<>());
        
        sb.setLength(0);
        sb = new StringBuilder();
        for(Point point : list)
            sb.append("(").append(point.x).append(", ").append(point.y).append(");");
        
        expResult = "(4, 1);(4, 0);(3, 0);(4, 2);(4, 3);";
        assertEquals(expResult, sb.toString());
        
    }
    
    /**
     * Test of the changeField method of Board class
     */
    @Test
    public void testChangeField1(){
        System.out.println("changeField(int x, int y, BoardField boardField)");
        Board instance = getBoard();
        BoardField before = instance.getField(0,0);
        instance.changeField(0, 0, BoardField.BLACK);
        BoardField after = instance.getField(0,0);
        assertNotEquals(before, after);
    }
    
    /**
     * Test of the deleteStones method of Board class
     */
    @Test
    public void testDeleteStones(){
        System.out.println("deleteStones");
        Board instance = getBoard();
        fillBoard(instance);
        
        instance.deleteStones(4, 1);
        
        assertEquals(BoardField.EMPTY, instance.getField(3, 0));
        assertEquals(BoardField.EMPTY, instance.getField(4, 0));
        assertEquals(BoardField.EMPTY, instance.getField(4, 1));
        assertEquals(BoardField.EMPTY, instance.getField(4, 2));
        assertEquals(BoardField.EMPTY, instance.getField(4, 3));
    }
    
    /**
     * Test of the changeField method of Board class
     */
    @Test
    public void testChangeField2(){
        System.out.println("changeField(placeStone pc)");
        Board instance = getBoard();
        fillBoard(instance);
        
        PlaceStone pc = new PlaceStone(true, 3, 3);
        int result = instance.changeField(pc);
        
        assertEquals(2, result);
        assertEquals(BoardField.EMPTY, instance.getField(3,1));
        assertEquals(BoardField.EMPTY, instance.getField(3,2));
        assertEquals(BoardField.BLACK, instance.getField(3,3));
        
        pc = new PlaceStone(false, 0, 0);
        result = instance.changeField(pc);
        
        assertEquals(0, result);
        assertEquals(BoardField.WHITE, instance.getField(0,0));
        
        instance = getBoard();
        fillBoard(instance);
        instance.changeField(2, 0, BoardField.WHITE);
        instance.changeField(3, 3, BoardField.WHITE);
        instance.changeField(4, 4, BoardField.WHITE);
        instance.changeField(5, 3, BoardField.WHITE);
        instance.changeField(5, 2, BoardField.WHITE);
        instance.changeField(5, 1, BoardField.WHITE);
        
        pc = new PlaceStone(false, 5, 0);
        result = instance.changeField(pc);
        
        assertEquals(5, result);
        assertEquals(BoardField.WHITE, instance.getField(5,0));
        assertEquals(BoardField.EMPTY, instance.getField(3,0));
        assertEquals(BoardField.EMPTY, instance.getField(4,0));
        assertEquals(BoardField.EMPTY, instance.getField(4,1));
        assertEquals(BoardField.EMPTY, instance.getField(4,2));
        assertEquals(BoardField.EMPTY, instance.getField(4,3));
    }
    
    /** Test of chainOfEmptyFields method */
    @Test
    public void testChainOfEmptyFields(){
        System.out.println("chainOfEmptyFields");
        Board instance = getBoard();
        fillBoard(instance);
        
        instance.changeField(0, 2, BoardField.BLACK);
        instance.changeField(1, 2, BoardField.BLACK);
        
        ArrayList <Point> empty = instance.chainOfEmptyFields(0,0, new ArrayList<>());
        
        StringBuilder sb = new StringBuilder();
        for(Point p : empty)
            sb.append("(").append(p.x).append(", ").append(p.y).append(");");
        
        String expResult = "(0, 0);(1, 0);(2, 0);(1, 1);(0, 1);";
        
        assertEquals(expResult, sb.toString());
    }
    
    /** Test of the countBlackNeighbors method */
    @Test
    public void testCountBlackNeighbors(){
        System.out.println("countBlackNeighbors");
        Board instance = getBoard();
        fillBoard(instance);
        
        instance.changeField(0, 2, BoardField.BLACK);
        instance.changeField(1, 2, BoardField.BLACK);
        
        assertEquals(4, instance.countBlackNeighbors(0, 0, new HashSet(), new HashSet()));  
        assertEquals(7, instance.countBlackNeighbors(0, 3, new HashSet(), new HashSet()));        
    }
    
    /** Test of the countWhiteNeighbors method */
    @Test
    public void testCountWhiteNeighbors(){
        System.out.println("countWhiteNeighbors");
        Board instance = getBoard();
        fillBoard(instance);
        
        instance.changeField(0, 2, BoardField.BLACK);
        instance.changeField(1, 2, BoardField.BLACK);
        
        assertEquals(0, instance.countWhiteNeighbors(0, 0, new HashSet(), new HashSet()));
        assertEquals(1, instance.countWhiteNeighbors(0, 3, new HashSet(), new HashSet()));
    }
    
    /** Test of capturedBlack method */
    @Test
    public void testCapturedBlack(){
        System.out.println("capturedBlack");
        Board instance = getBoard();
        fillBoard(instance);
        
        instance.changeField(new PlaceStone(false,2,0));
        instance.changeField(new PlaceStone(false,1,1));
        instance.changeField(new PlaceStone(false,1,2));
        instance.changeField(new PlaceStone(false,2,3));
        
        assertEquals(2, instance.capturedBlack());
    }
    
    /** Test of capturedWhite method */
    @Test
    public void testCapturedWhite(){
        System.out.println("capturedWhite");
        Board instance = getBoard();
        fillBoard(instance);
        
        instance.changeField(new PlaceStone(true,3,3));
        
        assertEquals(2, instance.capturedWhite());
    }
    
    /** Test of calculateScore method */
    @Test
    public void testCalculateScore(){
        System.out.println("calculateScore");
        Board instance = getBoard();
        fillBoard(instance);
        
        instance.changeField(new PlaceStone(true,3,3));
        
        assertEquals(2.0, instance.calculateScore(true, new ArrayList<>()),0);
        assertEquals(6.5, instance.calculateScore(false, new ArrayList<>()),0);       
    }
    
    /** test of getPossiblePlaces method */
    @Test
    public void testGetPossibleMoves(){
        System.out.println("getPossibleMoves");
        Board instance = getBoard();
        fillBoard(instance);
        
        ArrayList<FieldInfo> list = instance.getPossibleMoves(BoardField.WHITE);
        
        ArrayList<Point> expected = new ArrayList<>();
        for(int x=0; x<instance.getBoard().length; x++)
            for(int y=0; y<instance.getBoard().length; y++){
                expected.add(new Point(y,x));
            }
        expected.remove(3);
        expected.remove(4);
        expected.remove(11);
        expected.remove(12);
        expected.remove(13);
        expected.remove(20);
        expected.remove(21);
        expected.remove(22);
        expected.remove(31);        
        
        StringBuilder sb = new StringBuilder();
        for(FieldInfo field : list)
            sb.append("(").append(field.getX()).append(", ").append(field.getY()).append(");");
        
        String expResult = sb.toString();
        
        sb = new StringBuilder();
        for(FieldInfo field : list)
            sb.append("(").append(field.getX()).append(", ").append(field.getY()).append(");");
        
        assertEquals(expResult, sb.toString());
    }
    
    
    /** Test of the getDefaultTerritories method */
    @Test
    public void testGetDefaultTerritories(){
        System.out.println("getDefaultTerritories");
        Board instance = getBoard();
        fillBoard(instance);
        
        instance.changeField(0,2, BoardField.BLACK);
        instance.changeField(1,2, BoardField.BLACK);
        
        ReturnDefaultTerritories territories = instance.getDefaultTerritories();
        
        ArrayList<Point> territoriesBlack = (ArrayList<Point>) territories.getBlackTerritories();
        
        StringBuilder sb = new StringBuilder();
        for(Point point : territoriesBlack)
            sb.append("(").append(point.x).append(", ").append(point.y).append(");");
        
        String expResult = "(0, 0);(1, 0);(2, 0);(1, 1);(0, 1);";
        assertEquals(expResult, sb.toString());
        
        
        ArrayList<Point> territoriesWhite = (ArrayList<Point>) territories.getWhiteTerritories();
        
        sb = new StringBuilder();
        for(Point point : territoriesWhite)
            sb.append("(").append(point.x).append(", ").append(point.y).append(");");
        
        expResult = "";
        assertEquals(expResult, sb.toString());
                
    }
    
    /**
     * Fills the board
     * @param board board
     */
    public void fillBoard(Board board){
        board.changeField(3,1,BoardField.WHITE);
        board.changeField(3,2,BoardField.WHITE);
        board.changeField(4,2,BoardField.BLACK);
        board.changeField(4,1,BoardField.BLACK);
        board.changeField(3,0,BoardField.BLACK);
        board.changeField(2,1,BoardField.BLACK);
        board.changeField(2,2,BoardField.BLACK);
        board.changeField(4,3,BoardField.BLACK);
        board.changeField(4,0,BoardField.BLACK);
    }
}
