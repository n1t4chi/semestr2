/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
     * Class for text line. Contains text line and its ID.
 * @author n1t4chi
 */
public class TextLine {
    
   //##########################fields###########################################
    /**
     * Text line.
     */
    private final String text;
    /**
     * ID.
     */
    private final String ID;

    
    
    
   //#########################methods###########################################
    /**
     * Returns text line.
     * @return text line.
     */
    public String getText() {
        return text;
    }
    /**
     * Returns ID.
     * @return ID.
     */
    public String getID() {
        return ID;
    }
    /**
     * Checks if ID matches this object ID. 
     * @param ID ID to check.
     * @return True if IDs are equal.
     */
    public boolean isIt(String ID){
        return (this.ID.equalsIgnoreCase(ID));
    }
    
    
   //###########################constructor#####################################
    
    
    /**
     * Constructor
     * @param text Text line. 
     * @param ID ID of text line.
     */
    public TextLine(String text, String ID) {
        this.text = text;
        this.ID = ID;
    }       
}
