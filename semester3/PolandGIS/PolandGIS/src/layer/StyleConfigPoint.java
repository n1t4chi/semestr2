/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layer;

import java.awt.Color;

/**
 * Used for point data type layers
 * @author n1t4chi
 */
public class StyleConfigPoint implements StyleConfig{

    
    public enum figureType{
        CIRLCE,SQUARE
    }
    
    private double size;
    
    private figureType figureType;
    
    private Color colour;
    
    private boolean geographic;

    /**
     * Default constructor. Figure type and colour are null types by default!
     */
    public StyleConfigPoint() {
        size = -1;
        figureType = null;
        colour = null;
        geographic = false;
    }

    /**
     * constructor
     * @param size size of figure
     * @param figureType type of figure
     * @param colour colour of figure
     * @param geographic is figure projection size a geographic or not
     */
    public StyleConfigPoint(double size, figureType figureType, Color colour, boolean geographic) {
        this.size = size;
        this.figureType = figureType;
        this.colour = colour;
        this.geographic = geographic;
    }
    /**
     * Changes colour of figure
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
    
    /**
     * Returns whether the figure projection is geographic or not
     * @return 
     */
    public boolean isGeographic() {
        return geographic;
    }
    /**
     * Changes whether the projection of figure is on geographic scale or not
     * @param geographic 
     */
    public void setGeographic(boolean geographic) {
        this.geographic = geographic;
    }

    /**
     * Changes figure type
     * @param figureType 
     */
    public void setFigureType(figureType figureType) {
        this.figureType = figureType;
    }
    /**
     * Returns figure type
     * @return 
     */
    public figureType getFigureType() {
        return figureType;
    }
    /**
     * Changes figure size
     * @param size 
     */
    public void setSize(double size) {
        this.size = size;
    }
    /**
     * Returns figure size
     * @return 
     */
    public double getSize() {
        return size;
    }
    
    
    @Override
    public StyleConfigType getType() {
        return StyleConfigType.POINT; 
    }
}
