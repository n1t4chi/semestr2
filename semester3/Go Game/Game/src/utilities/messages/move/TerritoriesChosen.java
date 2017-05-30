/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.move;

import java.io.Serializable;
import java.util.ArrayList;
import utilities.messages.data.FieldInfo;

/**
 * Message containing chosen territories.
 * @author n1t4chi
 */
public class TerritoriesChosen extends Move{
    /**
     * List of chosen territories.
     */
    private final ArrayList<FieldInfo> list;

    /**
     * Constructor. Initialises empty container for territories which should be  added with addTerritory()
     */
    public TerritoriesChosen() {
        list = new ArrayList<>();
        
    }
    /**
     * Adds territory to list
     * @param territory territory to add
     * @throws NullPointerException if argument is null
     */
    public void addLegalPlace(FieldInfo territory){
        if(territory==null)
            throw new NullPointerException("Null argument");
        list.add(territory);
    } 
    /**
     * Returns list of territories held by this object
     * @return list of territories 
     */
    public ArrayList<FieldInfo> getTerritoriesList() {
        return list;
    }
   
   
    /**
     * Default constructor
     * @param list List of territories.
     * @throws NullPointerException if argument is null
     */
    public TerritoriesChosen(ArrayList<FieldInfo> list){
        if(list == null)
            throw new NullPointerException("Null pointer");
        this.list = list;
    }
    
    
    
    
    @Override
    public MessageMoveType getMoveType() {
        return MessageMoveType.TERRITORIES_CHOOSEN;
    }

    @Override
    public Serializable getMessage() {
            return getTerritoriesList();
    }
    
}
