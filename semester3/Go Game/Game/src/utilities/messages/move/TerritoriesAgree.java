/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Move for player agreeing with opponent territories placement
 * @author n1t4chi
 */
public class TerritoriesAgree extends Move{
    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.TERRITORIES_AGREE;
    }

    @Override
    public Serializable getMessage() {
        return "gut placement bro";
    }
    
}
