/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Move for player disagreeing with opponent territories placement
 * @author n1t4chi
 */
public class TerritoriesDisagree extends Move{

    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.TERRITORIES_DISAGREE;
    }

    @Override
    public Serializable getMessage() {
        return "your territories are bad and you should feel bad";
    }
    
}
