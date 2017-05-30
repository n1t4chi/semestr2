/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import utilities.messages.client.ReturnDefaultTerritories;
import utilities.messages.data.FieldInfo;
import utilities.messages.move.PlaceStone;

/**
 * Board class.
 * @author n1t4chi
 */
public class Board implements Serializable{

    /**
     * Enum representing state of a single field.
     */
    public enum BoardField{BLACK,WHITE,EMPTY};
    /**
     * Enum representing size of a board.
     */
    public enum Size{SMALL,MODERATE,BIG}
    private final Size size;
    /**
     * Board. ROWS Row COLUMNS
     */   
    private BoardField[][] board;
    
    /** Number of captured white stones */
    private int capturedWhite = 0;
    /** Number of captured black stones */
    private int capturedBlack = 0;
    
    /** Field that can possibly be Ko */
    private Point possibleKo = null;
    
    /**
     * Default constructor.
     * @param size Size of board.
     */
    public Board(Size size) {
        this.size = size;
        int siz=(size==Size.SMALL)?9:((size==Size.MODERATE)?13:19  );
        board = new BoardField[siz][siz];
        for(int i=0; i<siz; i++)
            for(int j=0; j<siz; j++)
                changeField(i,j,BoardField.EMPTY);
    }
    /**
     * Returns size of this board.
     * @return size of this board.
     */
    public Size getSize(){
        return size;
    }

    /**
     * Returns not editable board
     * @return 
     */
    public BoardField[][] getBoard() {
        return board.clone();
    }
    /**
     * Swaps BoardFields to given ones
     * @param board Board Fields
     * @throws NullPointerException if board is null
     */
    public void setBoard(BoardField[][] board) {
        if(board == null)
            throw new NullPointerException("Null argument");
        this.board=board;
    }    
    
    /**
     * Returns current state of given field 
     * @param x Row
     * @param y Column
     * @return state of Field
     * @throws IllegalArgumentException if either Row or Column are outside boundaries.
     */
    public BoardField getField(int x,int y) {
        if((x<0)||(y<0)||(x>=board.length)||(x>=board.length)){
            throw new IllegalArgumentException("Position outside board boundaries.");
        }
        return board[x][y];
    }
    
    /**
     * Changes the type of board field
     * @param x x
     * @param y y
     * @param boardField type of board field that specific field will change into
     */
    public void changeField(int x, int y, BoardField boardField){
        board[x][y] = boardField;
    }
    
    /**
     * Changes the board adequately to performed move returning number of captured stones
     * @param pc message about placing stones
     * @return number of captured stones
     */
    public int changeField(PlaceStone pc){
        int rtrn = 0;
        if(!isLegalMove(pc.getColourOfStones(), pc.getX(), pc.getY()))  //not legal move
            return -1;
        else if(!canCaptureStones(pc.getColourOfStones(), pc.getX(), pc.getY())){   //legal move but no capturing stones
            changeField(pc.getX(), pc.getY(), pc.getColourOfStones());
            possibleKo = null;
            return 0;
        }
        else{   //legal move capturing stones
            int[] columns = { pc.getX()+1, pc.getX(), pc.getX()-1, pc.getX() }; //finding the chains that should be deleted after placing the stone
            int[] rows = { pc.getY(), pc.getY()-1, pc.getY(), pc.getY()+1 };
            BoardField opposite;
            if(pc.getColourOfStones() == BoardField.WHITE)
                opposite = BoardField.BLACK;
            else
                opposite = BoardField.WHITE;

            for(int i=0; i<4; i++){
                if(columns[i]>=0 && rows[i]>=0 && columns[i]<board.length && rows[i]<board.length ){
                    if(getField(columns[i], rows[i]) == opposite){
                        if(countBreaths(opposite, columns[i], rows[i], new HashSet(), new HashSet()) == 1){
                            if(chain(columns[i], rows[i], new ArrayList<>()).size() == 1)   //if there is only one stone to capture save it as possibleKo
                                possibleKo = new Point(columns[i], rows[i]);
                            rtrn += deleteStones(columns[i], rows[i]);  //deleting from board the chain that was captured by the stone
                        }
                    }
                }
            }
            changeField(pc.getX(), pc.getY(), pc.getColourOfStones());
        }
        return rtrn;
    }
    
    /**
     * Returns number of breaths particular field has
     * @param boardField type of board field
     * @param x x
     * @param y y
     * @param hs HashSet of already checked fields
     * @param breaths HashSet of already counted breaths
     * @return number of breaths particular field has
     */
    public int countBreaths(BoardField boardField, int x, int y, HashSet hs, HashSet breaths){
        String move=x+":"+y;
        int rtrn = 0;
        hs.add(move);
        
        int[] columns = { x+1, x, x-1, x };
        int[] rows = { y, y-1, y, y+1 };
        
        for(int i=0; i<4; i++){
            move=columns[i]+":"+rows[i];
            if(!hs.contains(move) && !breaths.contains(move)&& columns[i]>=0 && rows[i]>=0 && columns[i]<board.length && rows[i]<board.length ){
                if(getField(columns[i], rows[i]) == BoardField.EMPTY){
                    breaths.add(move);
                    rtrn++;
                }
                else if(getField(columns[i], rows[i]) == boardField)
                    rtrn += countBreaths(getField(columns[i], rows[i]), columns[i], rows[i], hs, breaths);
            }
        }
        return rtrn;
    }
    
    /**
     * Counts number of black stones placed next to the chain of empty fields
     * @param x x coordinate of the field
     * @param y y coordinate of the field
     * @param hs HashSet of checked fields
     * @param blackNeighbors HashSet of counted black fields
     * @return number of black stones placed next to the chain of empty fields
     */
    public int countBlackNeighbors ( int x, int y, HashSet hs, HashSet blackNeighbors ){
        String move=x+":"+y;
        int rtrn = 0;
        hs.add(move);
        
        int[] columns = { x+1, x, x-1, x };
        int[] rows = { y, y-1, y, y+1 };
        
        for(int i=0; i<4; i++){
            move=columns[i]+":"+rows[i];
            if(!hs.contains(move) && !blackNeighbors.contains(move)&& columns[i]>=0 && rows[i]>=0 && columns[i]<board.length && rows[i]<board.length ){
                if(getField(columns[i], rows[i]) == BoardField.BLACK){
                    blackNeighbors.add(move);
                    rtrn++;
                }
                else if(getField(columns[i], rows[i]) == BoardField.EMPTY)
                    rtrn += countBlackNeighbors( columns[i], rows[i], hs, blackNeighbors );
            }
        }
        return rtrn;
    }
    
    /**
     * Counts number of white stones placed next to the chain of empty fields
     * @param x x coordinate of the field
     * @param y y coordinate of the field
     * @param hs HashSet of checked fields
     * @param whiteNeighbors HashSet of counted white fields
     * @return number of black stones placed next to the chain of empty fields
     */
    public int countWhiteNeighbors ( int x, int y, HashSet hs, HashSet whiteNeighbors ){
        String move=x+":"+y;
        int rtrn = 0;
        hs.add(move);
        
        int[] columns = { x+1, x, x-1, x };
        int[] rows = { y, y-1, y, y+1 };
        
        for(int i=0; i<4; i++){
            move=columns[i]+":"+rows[i];
            if(!hs.contains(move) && !whiteNeighbors.contains(move)&& columns[i]>=0 && rows[i]>=0 && columns[i]<board.length && rows[i]<board.length ){
                if(getField(columns[i], rows[i]) == BoardField.WHITE){
                    whiteNeighbors.add(move);
                    rtrn++;
                }
                else if(getField(columns[i], rows[i]) == BoardField.EMPTY)
                    rtrn += countWhiteNeighbors( columns[i], rows[i], hs, whiteNeighbors );
            }
        }
        return rtrn;
    }
    
    /**
     * Returns a chain of empty fields
     * @param x x coordinate
     * @param y y coordinate
     * @param list list of empty fields in the chain
     * @return chain of empty fields
     */
    public ArrayList<Point> chainOfEmptyFields(int x, int y, ArrayList<Point> list){
        if(getField(x,y) == BoardField.WHITE || getField(x,y) == BoardField.BLACK )
            return list;
        Point move = new Point(x,y);
        list.add(move);
        
        int[] columns = { x+1, x, x-1, x };
        int[] rows = { y, y-1, y, y+1 };
        
        for(int i=0; i<4; i++){
            move= new Point (columns[i], rows[i]);
            if(!list.contains(move) && columns[i]>=0 && rows[i]>=0 && columns[i]<board.length && rows[i]<board.length ){
                if(getField(columns[i], rows[i]) == BoardField.EMPTY){
                    ArrayList<Point> newlist = chainOfEmptyFields(columns[i], rows[i], list);
                    for(Point point: newlist)
                        if(!list.contains(point))
                            list.add(point);
                }
            }
        }
        return list;
    }
    
    /**
     * Returns default territories for black and white stones
     * @return message about territories for black and white stones
     */
    public ReturnDefaultTerritories getDefaultTerritories(){
        ArrayList<Point> territoriesWhite = new ArrayList<>();
        ArrayList<Point> territoriesBlack = new ArrayList<>();
        ArrayList<String> checked = new ArrayList<>();
        
        for(int x=0;x<board.length;x++)
            for(int y=0;y<board.length;y++){
                if(getField(x,y) == BoardField.EMPTY && !checked.contains(x+":"+y)){    //if field is empty and unchecked
                    ArrayList<Point> chainOfEmptyFields = chainOfEmptyFields(x, y, new ArrayList<>());
                    for(Point p: chainOfEmptyFields)    //get the chain of empty fields and add to the checked fields list
                        checked.add(p.x+":"+p.y);
                    
                    int white = countWhiteNeighbors(x, y, new HashSet(), new HashSet());    //counting white and black neighbors
                    int black = countBlackNeighbors(x, y, new HashSet(), new HashSet());
                    
                    if(white == 0 && black != 0){   //if a chain has only black neighbors is in a black territorie
                        for(Point p: chainOfEmptyFields)
                            territoriesBlack.add(p);
                    }
                        
                    if(white != 0 && black ==0 ){   //if a chain has only white neighbors is in a white territorie
                        for(Point p: chainOfEmptyFields)
                            territoriesWhite.add(p);
                    }
                }
            }
        return new ReturnDefaultTerritories(territoriesWhite, territoriesBlack);
    }
    
    /**
     * Returns if move causes capturing opponent's stones
     * @param boardField type of boardField that is going to be in this x and y
     * @param x x
     * @param y y
     * @return if move causes capturing opponent's stones
     */
    public boolean canCaptureStones(BoardField boardField, int x, int y){
        int[] columns = { x+1, x, x-1, x };
        int[] rows = { y, y-1, y, y+1 };
        BoardField opposite;
        if(boardField == BoardField.WHITE)
            opposite = BoardField.BLACK;
        else
            opposite = BoardField.WHITE;
        
        for(int i=0; i<4; i++){
            if(columns[i]>=0 && rows[i]>=0 && columns[i]<board.length && rows[i]<board.length ){
                if(getField(columns[i], rows[i]) == opposite)
                    if(countBreaths(opposite, columns[i], rows[i], new HashSet(), new HashSet()) == 1)
                        return true;
            }
        }
        return false;
    }
    
    /**
     * Returns list of coordinates of the fields in the chain
     * @param x x
     * @param y y
     * @param list list of coordinates of the fields in the chain
     * @return list of coordinates of the fields in the chain
     */
    public ArrayList<Point> chain(int x, int y, ArrayList<Point> list){
        BoardField boardField = getField(x, y);
        if(boardField == BoardField.EMPTY)
            return list;
        Point move = new Point(x,y);
        list.add(move);
        
        int[] columns = { x+1, x, x-1, x };
        int[] rows = { y, y-1, y, y+1 };
        
        for(int i=0; i<4; i++){
            move= new Point (columns[i], rows[i]);
            if(!list.contains(move) && columns[i]>=0 && rows[i]>=0 && columns[i]<board.length && rows[i]<board.length ){
                if(getField(columns[i], rows[i]) == boardField){
                    ArrayList<Point> newlist = chain(columns[i], rows[i], list);
                    for(Point point: newlist)
                        if(!list.contains(point))
                            list.add(point);
                }
            }
        }
        return list;
    }
    
    /**
     * Deletes all of the stones in the chain that the particular stones is
     * @param x x
     * @param y y
     * @return number of deleted stones
     */
    public int deleteStones(int x, int y){
        ArrayList<Point> list = chain(x, y, new ArrayList<>());
        for(Point field: list){
            if(getField(x, y) == BoardField.BLACK)
                capturedBlack += list.size();
            else if(getField(x, y) == BoardField.WHITE)
                capturedWhite += list.size();
            changeField(field.x, field.y, BoardField.EMPTY);
        }
        return list.size();
    }
    
    /**
     * Returns whether the move is legal or not.
     * @param boardField type of board field
     * @param x x
     * @param y y
     * @return Whether the move is legal.
     */
    public boolean isLegalMove(BoardField boardField,int x,int y){
        if((x<0)||(y<0)||(x>=board.length)||(x>=board.length))  //checking if in board
            return false;
            
        if(getField(x, y) == BoardField.BLACK || getField(x, y) == BoardField.WHITE)    //checking if occupied
            return false;
        
        if(countBreaths(boardField, x, y, new HashSet<>(), new HashSet<>())==0)   //checking if it is suicide
            if(!canCaptureStones(boardField, x, y))
                return false;
                
        if(possibleKo!=null)    //checking if not Ko
            if(possibleKo.x == x && possibleKo.y == y)
                return false;
                    
        return true;
    }
    
    /**
     * 
     * @param type
     * @return List of legal Moves
     */
    public ArrayList<FieldInfo> getPossibleMoves(BoardField type) {
        ArrayList<FieldInfo> legalMoves = new ArrayList<>();
        for(int x=0;x<board.length;x++)
            for(int y=0;y<board.length;y++)
                if(isLegalMove(type, x, y))
                    legalMoves.add(new FieldInfo(type, x,y));
        return legalMoves;
    }
    
    /**
     * Returns number of captured black stones in the game
     * @return number of captured black stones in the game
     */
    public int capturedBlack(){
        return capturedBlack;
    }
    
    /**
     * Returns number of captured black stones in the game
     * @return number of captured black stones in the game
     */
    public int capturedWhite(){
        return capturedWhite;
    }    

    /**
     * Returns score of player with given territories.
     * @param black whether to return black player score or not.
     * @param territories List of territories.
     * @return score of a player
     */
    public double calculateScore(boolean black, ArrayList<FieldInfo> territories){
        ArrayList<Point> territoriesBlack = new ArrayList<>();
        ArrayList<Point> territoriesWhite = new ArrayList<>();
        if(territories!=null){
            for(FieldInfo fieldInfo: territories){
                if(fieldInfo.getState() == BoardField.BLACK)
                    territoriesBlack.add(fieldInfo.getPos());
                else if (fieldInfo.getState() == BoardField.WHITE)
                    territoriesWhite.add(fieldInfo.getPos());                        
            }
        }    
        if(black)
            return territoriesBlack.size() + capturedWhite();
        return territoriesWhite.size() + capturedBlack() + 6.5;
    }

    /**
     * Returns whether the move is legal or not.
     * @param placeStone message about placing stone
     * @return whether the move is legal or not
     */
    public boolean isLegalMove(PlaceStone placeStone) {
        return isLegalMove(placeStone.getColourOfStones(), placeStone.getX(), placeStone.getY());
    }

    @Override
    public String toString() {
        String str = "\n";
        for(int x=0;x<board.length;x++){
            for(int y=0;y<board[x].length;y++){
                str+=( (board[x][y]==BoardField.BLACK)?"#":((board[x][y]==BoardField.WHITE)?"O":" ")  );
            }
            str+="\n";
        }
        return str;
    }
    
    
    
}
