/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.client;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import utilities.messages.Message;
import utilities.messages.MessageType;

/**
 * Returned value of default territory placement by GameHandler to subcomponent
 * @author n1t4chi
 */
public class ReturnDefaultTerritories implements Message{
    /**
     * 
     */
    //private final T x;
    
    /** List of white territories*/
    private final ArrayList<Point> territoriesWhite;
    /** List of black territories*/
    private final ArrayList<Point> territoriesBlack;
    
    /**
     * Constructor
     * @param territoriesWhite list of white territories
     * @param territoriesBlack list of black territories
     */
    public ReturnDefaultTerritories(ArrayList<Point> territoriesWhite, ArrayList<Point> territoriesBlack){
        this.territoriesWhite=territoriesWhite;
        this.territoriesBlack=territoriesBlack;
    }
    
    @Override
    public Serializable getMessage() {
        return "u have to ask for one at a time";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.RETURN_DEFAULT_TERRITORIES;
    }
    
    /**
     * returns list of white territories
     * @return list of white territories
     */
    public Serializable getWhiteTerritories(){
        return territoriesWhite;
    }
    
    /**
     * returns list of black territories
     * @return list of white territories
     */
    public Serializable getBlackTerritories(){
        return territoriesBlack;
    }
}
