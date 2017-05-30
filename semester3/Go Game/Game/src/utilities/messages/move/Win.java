/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Message send by server to winner
 * @author n1t4chi
 */
public class Win extends Move{

    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.WIN;
    }

    @Override
    public Serializable getMessage() {
        return "Hail to the king, baby";
    }
    
}
