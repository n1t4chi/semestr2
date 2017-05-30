/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layer;

import java.awt.Color;

/**
 * Used for polygon data type layers
 * @author n1t4chi
 */
public class StyleConfigPolygon implements StyleConfig{

    
    private Color fillColor;
    private StrokeType borderStrokeType;
    private Color borderColour;
    private double borderThickness;

    /**
     * Default constructor, sets colours and strokes as null types!
     */
    public StyleConfigPolygon() {
        this.fillColor = null;
        this.borderStrokeType = null;
        this.borderColour = null;
        this.borderThickness = -1;
    }

    /**
     * Constructor
     * @param fillColor
     * @param borderStrokeType
     * @param borderColour
     * @param borderThickness 
     */
    public StyleConfigPolygon(Color fillColor, StrokeType borderStrokeType, Color borderColour, double borderThickness) {
        this.fillColor = fillColor;
        this.borderStrokeType = borderStrokeType;
        this.borderColour = borderColour;
        this.borderThickness = borderThickness;
    }
    
    
   
    /**
     * Changes type of stroke
     * @param borderStrokeType 
     */
    public void setBorderStrokeType(StrokeType borderStrokeType) {
        this.borderStrokeType = borderStrokeType;
    }
    /**
     * Returns type of stroke
     * @return 
     */
    public StrokeType getBorderStrokeType() {
        return borderStrokeType;
    }
    /**
     * Returns border thickness of polygon
     * @return 
     */
    public double getBorderThickness() {
        return borderThickness;
    }
    /**
     * Changes border thickness of polygon
     * @param borderThickness
     */
    public void setBorderThickness(double borderThickness) {
        
        this.borderThickness = borderThickness;
    }
    
    /**
     * Changes border colour of polygon
     * @param borderColour 
     */
    public void setBorderColour(Color borderColour) {
        this.borderColour = borderColour;
    }
    /**
     * Returns border colour
     * @return 
     */
    public Color getBorderColour() {
        return borderColour;
    }
    
    /**
     * Changes fill colour of polygon
     * @param fillColor 
     */
    public void setColour(Color fillColor) {
        this.fillColor = fillColor;
    }
    /**
     * Returns fill colour
     * @return 
     */
    public Color getColour() {
        return fillColor;
    }
    
    @Override
    public StyleConfigType getType() {
        return StyleConfigType.POLYGON;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getFillColor() {
        return fillColor;
    }
    
}
