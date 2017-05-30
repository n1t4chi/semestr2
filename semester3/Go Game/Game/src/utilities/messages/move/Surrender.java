/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Players surrenders the game. 
 * @author n1t4chi
 */
public class Surrender extends Move{
    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.SURRENDER;
    }

    @Override
    public Serializable getMessage() {
        return "Russians best tactic is drive enemy deep into their territory and wait for winter. in WW2 French did the same but forgot about winter part.";
    }
    
}
