/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.client;

import java.io.Serializable;
import utilities.messages.Message;
import utilities.messages.MessageType;

/**
 * Returned value of score by GameHandler to subcomponent
 * @author n1t4chi
 */
public class ReturnScore implements Message{
    /**
     * Current score
     */
    private final double score_white;
    /**
     * Current score
     */
    private final double score_black;
    /**
     * Default constructor
     * @param score_black current score of black player.
     * @param score_white current score of white player.
     */
    public ReturnScore(double score_black,double score_white) {
        this.score_white = score_white;
        this.score_black = score_black;
    }
    /**
     * Returns current score of white player.
     * @return 
     */
    public double getScoreWhite() {
        return score_white;
    }
    /**
     * Returns current score of black player.
     * @return 
     */
    public double getScoreBlack() {
        return score_black;
    }
    
    @Override
    public Serializable getMessage() {
        return getScoreBlack()+":"+getScoreWhite();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.RETURN_SCORE;
    }
    
}
