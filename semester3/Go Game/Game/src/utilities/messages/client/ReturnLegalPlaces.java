/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.client;

import java.io.Serializable;
import java.util.ArrayList;
import utilities.messages.Message;
import utilities.messages.MessageType;
import utilities.messages.data.FieldInfo;

/**
 * Returned value of legal places to place stones on board  by GameHandler to subcomponent
 * @author n1t4chi
 */
public class ReturnLegalPlaces implements Message{
    /**
     * List of legal places.
     */
    private final ArrayList<FieldInfo> list;
    /**
     * Additional constructor, creates empty list ready to add elements to
     */
    public ReturnLegalPlaces() {
        this(new ArrayList<>());
    }
    /**
     * Adds legal place to list.
     * @param fi legal place
     * @throws NullPointerException if argument is null
     */
   public void addLegalPlace(FieldInfo fi){
        if(fi==null)
           throw new NullPointerException("Null argument");
        list.add(fi);
   } 
    /**
     * Default constructor.
     * @param list List of legal fields
     * @throws NullPointerException if argument is null
     */
    public ReturnLegalPlaces(ArrayList<FieldInfo> list) {
        if(list==null)
           throw new NullPointerException("Null argument");
        this.list = list;
    }
    /**
     * returns legal places on board to place stone on.
     * @return legal places.
     */
    public ArrayList<FieldInfo> getLegalPlaces() {
        return list;
    }
    
    
    
    @Override
    public Serializable getMessage() {
        return getLegalPlaces();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.RETURN_LEGAL_PLACES;
    }
    
}
