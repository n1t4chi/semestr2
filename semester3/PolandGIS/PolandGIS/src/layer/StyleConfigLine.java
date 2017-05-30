/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layer;

import java.awt.Color;

/**
 * Used for line data type layers
 * @author n1t4chi
 */
public class StyleConfigLine implements StyleConfig{

    
    
    private StrokeType strokeType;
    private Color colour;
    private double thickness;
    private boolean geographic;
    /**
     * Default constructor, sets colour and stroke as null type!
     */
    public StyleConfigLine() {
        this.strokeType = null;
        this.colour = null;
        this.thickness = -1;
        this.geographic = false;
    }
    /**
     * Constructor
     * @param strokeType
     * @param colour
     * @param thickness
     * @param geographic 
     */
    public StyleConfigLine(StrokeType strokeType, Color colour, double thickness, boolean geographic) {
        this.strokeType = strokeType;
        this.colour = colour;
        this.thickness = thickness;
        this.geographic = geographic;
    }

    /**
     * Changes type of stroke
     * @param strokeType 
     */
    public void setStrokeType(StrokeType strokeType) {
        this.strokeType = strokeType;
    }
    /**
     * Returns type of stroke
     * @return 
     */
    public StrokeType getStrokeType() {
        return strokeType;
    }
    /**
     * Returns thickness of line
     * @return 
     */
    public double getThickness() {
        return thickness;
    }
    /**
     * Changes thickness of line
     * @param thickness
     */
    public void setThickness(double thickness) {
        
        this.thickness = thickness;
    }
    
    
   
    
    /**
     * Returns whether the line projection is geographic or not
     * @return 
     */
    public boolean isGeographic() {
        return geographic;
    }

    /**
     * Changes whether the projection of line is on geographic scale or not
     * @param geographic 
     */
    public void setGeographic(boolean geographic) {
        this.geographic = geographic;
    }
    
    
    /**
     * Changes colour of line
     * @param colour 
     */
    public void setColour(Color colour) {
        this.colour = colour;
    }
    /**
     * Returns colour
     * @return 
     */
    public Color getColour() {
        return colour;
    }
    
    
    @Override
    public StyleConfigType getType() {
        return StyleConfigType.LINE;
    }
    
}
