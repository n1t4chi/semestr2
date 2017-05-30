/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import game.Board;
import java.io.Serializable;

/**
 * Message returning board fields.
 * @author n1t4chi
 */
public class ReturnBoard implements Message{
    private final Board board;
    /**
     * Default constructor.
     * @param board 
     * @throws NullPointerException If board is null
     */
    public ReturnBoard(Board board) {
        if(board==null) throw new NullPointerException("Null pointer");
        this.board = board;
    }
    /**
     * Returns board fields contained in this message.
     * @return board fields
     */
    public Board.BoardField[][] getBoard() {
        return board.getBoard();
    }
    
    @Override
    public Serializable getMessage() {
        return board.toString();
    }
    
    @Override
    public MessageType getMessageType() {
        return MessageType.RETURN_BOARD;
    }
    
}
