/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import game.Board;
import java.io.Serializable;

/**
 * Creates new room on server.
 * @author n1t4chi
 */
public class RoomCreate implements Message{
    final String room_name;
    final Board.Size size;
    final boolean BlackAI;
    final boolean WhiteAI;

    /**
     * Default constructor
     * @param room_name
     * @param size 
     * @param BlackAI 
     * @param WhiteAI 
     */
    public RoomCreate(String room_name, Board.Size size, boolean BlackAI, boolean WhiteAI) {
        this.room_name = room_name;
        this.size = size;
        this.BlackAI = BlackAI;
        this.WhiteAI = WhiteAI;
    }

    /**
     * Returns whether to create AI as black player
     * @return whether to create or not
     */
    public boolean isBlackAI() {
        return BlackAI;
    }
    /**
     * Returns whether to create AI as white player
     * @return whether to create or not
     */
    public boolean isWhiteAI() {
        return WhiteAI;
    }
    
    /**
     * Return room name
     * @return room name
     */
    public String getRoomName() {
        return room_name;
    }

    /**
     * Returns size of board;
     * @return size of board;
     */
    public Board.Size getSize() {
        return size;
    }
    
    @Override
    public Serializable getMessage() {
        return getRoomName()+":"+getSize();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CREATE_ROOM;
    }
    
}
