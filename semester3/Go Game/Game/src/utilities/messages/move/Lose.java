/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Message send to loser.
 * @author n1t4chi
 */
public class Lose extends Move{

    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.LOSE;
    }

    @Override
    public Serializable getMessage() {
        return "Ha ur a looser";
    }
    
}
