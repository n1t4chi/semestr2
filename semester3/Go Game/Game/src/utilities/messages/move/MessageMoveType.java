/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

/**
 * Enum for Message Types
 * @author n1t4chi
 */
public enum MessageMoveType{
    // Server -> Client -> BoardGUI
    /**
     * Send by server in response to sending valid move by client.
     */
    VALID_MOVE,
    /**
     * Send by server in response to sending invalid move by client.
     */
    INVALID_MOVE,
    /**
     * Send by server on
     */
    MAKE_MOVE,
    /**
     * Send by server on
     */
    CHOOSE_TERRITORIES,
    /**
     * Send by server on
     */
    WIN,
    /**
     * Send by server on
     */
    LOSE, 
    
    
    
    

    RETURN_TO_STONE_PLACING,PASS,SURRENDER, //Clinet -> Server

    PLACE_STONE,TERRITORIES_CHOOSEN,TERRITORIES_AGREE,TERRITORIES_DISAGREE // Client1 -> Server - > Client2

}
