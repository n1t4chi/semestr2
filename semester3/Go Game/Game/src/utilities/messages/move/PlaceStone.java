/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import game.Board.BoardField;
import java.io.Serializable;

/**
 * Message of MOVE type with information about location of placing stone and its colour
 * @author Kingaa
 */
public class PlaceStone extends Move{

    /** colour of stones played by player */
    private final BoardField colourOfStones;
    
    
    /** x coordinate*/
    private final int x;
    /** y coordinate*/
    private final int y;
    
    /**
     * Constructor
     * @param black whether player plays with black stones
     * @param x x
     * @param y y
     */
    public PlaceStone(boolean black, int x, int y) {
        if(black)
            colourOfStones = BoardField.BLACK;
        else
            colourOfStones = BoardField.WHITE;
        this.x=x;
        this.y=y;
    }
    
    /**
     * Returns x coordinate of played move
     * @return x coordinate
     */
    public int getX(){
        return x;
    }
    
    /**
     * Returns y coordinate of played move
     * @return y coordinate
     */
    public int getY(){
        return y;
    }

    @Override
    public Serializable getMessage() {
        return getColourOfStones()+":"+getX()+":"+getY();
                
    }

    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.PLACE_STONE; 
    }
    
    
    /**
     * Returns colour of stones played by player
     * @return colour of stones played by player
     */
    public BoardField getColourOfStones(){
        return colourOfStones;
    }
    
}
