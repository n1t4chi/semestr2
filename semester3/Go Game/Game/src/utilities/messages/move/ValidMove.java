/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;

/**
 * Server response to valid move.
 * @author n1t4chi
 */
public class ValidMove extends Move{

  //  private int x;
   // private int y;
    
   // public ValidMove(/*int x, int y*/){
    //    this.x=x;
    //    this.y=y;
   // }
    
    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.VALID_MOVE;
    }

    @Override
    public Serializable getMessage() {
        return "Sick moves, bro";
    }
    
 /*   public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }*/
}
