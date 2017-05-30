/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layer;

import java.awt.Color;

/**
 *
 * @author n1t4chi
 */
public class LabelConfig {
    private String format;
    private int Size;
    private Color fontColour;
    private Color backgroundColour;

    /**
     * Default constructor, Sets format and colours as null types!
     */
    public LabelConfig() {
        this.format = null;
        this.Size = -1;
        this.fontColour = null;
        this.backgroundColour = null;
    }
    /**
     * Constructor
     * @param format
     * @param Size
     * @param fontColour
     * @param backgroundColour 
     */
    public LabelConfig(String format, int Size, Color fontColour, Color backgroundColour) {
        this.format = format;
        this.Size = Size;
        this.fontColour = fontColour;
        this.backgroundColour = backgroundColour;
    }
    /**
     * Returns background colour
     * @return 
     */
    public Color getBackgroundColour() {
        return backgroundColour;
    }
    /**
     * Changes background colour
     * @param backgroundColour 
     */
    public void setBackgroundColour(Color backgroundColour) {
        this.backgroundColour = backgroundColour;
    }
    /**
     * Returns font colour
     * @return 
     */
    public Color getFontColour() {
        return fontColour;
    }
    /**
     * changes font colour
     * @param fontColour 
     */
    public void setFontColour(Color fontColour) {
        this.fontColour = fontColour;
    }

    /**
     * Changes label format
     * @param format 
     */
    public void setFormat(String format) {
        this.format = format;
    }
    /**
     * returns label format
     * @return 
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns font size
     * @return 
     */
    public int getSize() {
        return Size;
    }
    /**
     * Changes font size
     * @param Size 
     */
    public void setSize(int Size) {
        this.Size = Size;
    }
    
    
    
    
    
    
}
