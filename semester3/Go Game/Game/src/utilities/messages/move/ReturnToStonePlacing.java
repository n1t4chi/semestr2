/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Message that client wants to go back to placing stones after they started choosing territories.
 * @author n1t4chi
 */
public class ReturnToStonePlacing extends Move{

    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.RETURN_TO_STONE_PLACING; 
    }

    @Override
    public Serializable getMessage() {
        return "Nah let's go back to placing stones.";
    }
    
}
