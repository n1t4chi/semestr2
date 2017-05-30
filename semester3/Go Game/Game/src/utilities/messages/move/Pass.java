/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Message send by client after he passes.
 * @author n1t4chi
 */
public class Pass extends Move{

    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.PASS;
    }

    @Override
    public Serializable getMessage() {
        return "I came here to place stones and kick ass, and I am all out of stones";
    }
    
}
