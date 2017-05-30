/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Message send by server to dictate who should make move now.
 * @author n1t4chi
 */
public class MakeMove extends Move{
    
    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.MAKE_MOVE;
    }

    @Override
    public Serializable getMessage() {
        return "Ur turn m8";
    }
    
}
