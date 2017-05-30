/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.data;

import game.Board;
import java.io.Serializable;

/**
 * Room info provided by Server and received by clients.
 * @author n1t4chi
 */
public class RoomInfo implements Serializable{
    
    private final String room_name;
    private final String white_player;
    private final int room_port_white;
    private final String black_player;
    private final int room_port_black;
    private final Board.Size room_size;
    /**
     * Default constructor.
     * @param room_port_black room port for black player, negative value indicates an AI player.
     * @param room_port_white room port, negative value indicates an AI player.
     * @param room_size room size
     * @param room_name room name
     * @param black_player Black player name
     * @param white_player White player name
     * @throws NullPointerException if any pointer is null
     */
    public RoomInfo(int room_port_black, int room_port_white, Board.Size room_size, String room_name, String black_player, String white_player) {
        if(
            (room_size==null)||
            (room_name==null)||
            (black_player==null)||
            (white_player==null)
        )
            throw new NullPointerException("null argument");
        this.room_port_black = room_port_black;
        this.room_port_white = room_port_white;
        this.room_name = room_name;
        this.white_player = white_player;
        this.black_player = black_player;
        this.room_size = room_size;
    }

    /**
     * Returns room size
     * @return room size
     */
    public Board.Size getRoomSize() {
        return room_size;
    }
    
    
    /**
     * Returns Black player name
     * @return Black player name
     */
    public String getBlackPlayer() {
        return black_player;
    }
    /**
     * Returns room name
     * @return room name
     */
    public String getRoomName() {
        return room_name;
    }
    /**
     * Returns room port on which server provides service for black player.
     * @return room port
     */
    public int getRoomPortBlack() {
        return room_port_black;
    }
    /**
     * Returns room port on which server provides service for white player.
     * @return room port
     */
    public int getRoomPortWhite() {
        return room_port_white;
    }
    /**
     * Returns White player name
     * @return White player name
     */
    public String getWhitePlayer() {
        return white_player;
    }

    @Override
    public String toString() {
        return "["+room_name+"]"
                + "["+((room_port_black<0)?"AI":room_port_black)+"]"
                + "["+((room_port_white<0)?"AI":room_port_white)+"]"
                + "["+((black_player.trim().isEmpty())?"Empty":black_player )+"]"
                + "["+((white_player.trim().isEmpty())?"Empty":white_player )+"]";
    }
    
}
