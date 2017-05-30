/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import game.Board.BoardField;
import utilities.messages.Message;
import utilities.messages.MessageType;


/**
 * Abstract class for move messages.
 * @author Kingaa
 */
public abstract class Move implements Message {
    /** colour of stones played by player */
    //private final BoardField colourOfStones;

    
    @Override
    public final MessageType getMessageType() {
        return MessageType.MOVE;
    }
    
    /**
     * Return MessageMove type
     * @return type
     */
    public abstract MessageMoveType getMoveType();

    
    /**
     * Constructor
     * @param black whether player plays with black stones
     */
    /*public Move(boolean black){
        if(black)
            colourOfStones = BoardField.BLACK;
        else
            colourOfStones = BoardField.WHITE;
    }*/
    
    /**
     * Returns colour of stones played by player
     * @return colour of stones played by player
     */
    /*public BoardField getColourOfStones(){
        return colourOfStones;
    }*/
    
}
