/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Server message that client should choose territories now.
 * @author n1t4chi
 */
public class ChooseTerritories extends Move{

    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.CHOOSE_TERRITORIES; 
    }

    @Override
    public Serializable getMessage() {
        return "Divide this land and I will judge who wins.";
    }
    
}
