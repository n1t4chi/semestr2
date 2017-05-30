/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Server response to invalid move.
 * @author n1t4chi
 */
public class InvalidMove extends Move implements Serializable{

    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.INVALID_MOVE;
    }

    @Override
    public Serializable getMessage() {
        return "Oi u cheeky swine, m8. U though I would not notice ur cheeting.";
    }
    
}
