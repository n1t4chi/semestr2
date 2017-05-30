/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Configuration file for GUI
 * @author n1t4chi
 */
public class ConfigWindow extends Config{
    public enum LanguageType{POLISH,ENGLISH};
    
    public static final Dimension DEFAULT_WINDOW_SIZE_MINIMUM = new Dimension(400,200);
    public static final Dimension DEFAULT_WINDOW_SIZE_PREVIOUS = new Dimension(600,400);
    public static final Point DEFAULT_WINDOW_LOCATION_PREVIOUS = new Point(0,0);
    public static final LanguageType DEFAULT_LANGUAGE=LanguageType.ENGLISH;

   
    /**
     * Returns default configuration.
     * @return default configuration.
     */
    public static ConfigWindow getDefaultConfig() {
        return new ConfigWindow(DEFAULT_WINDOW_SIZE_MINIMUM,DEFAULT_WINDOW_SIZE_PREVIOUS,DEFAULT_WINDOW_LOCATION_PREVIOUS,DEFAULT_LANGUAGE);
    }
    
    
    
    private Dimension WindowMinimumSize=null;
    private Dimension WindowPreviousSize=null;
    private Point WindowPreviousLocation=null;
    private LanguageType Language=null;
    
    
    
    /**
     * Copies all field from given object.
     * @param model Object to copy fields from. For null it sets default values.
     * @return If model is null then false, true otherwise.
     */
    public final boolean copy(ConfigWindow model) {    
        if(model!=null){
            set(model.WindowMinimumSize, model.WindowPreviousSize, model.WindowPreviousLocation, model.Language);
            return true;
        }else{
            set(null,null,null,null);
            return false;
        }
    }
    
    /**
     * Changes all fields.
     * @param WindowMinimumSize
     * @param WindowPreviousSize
     * @param WindowPreviousLocation
     * @param Language 
     */
    public final void set(Dimension WindowMinimumSize, Dimension WindowPreviousSize, Point WindowPreviousLocation, LanguageType Language) {    
        setLanguage(Language);
        setWindowMinimumSize(WindowMinimumSize);
        setWindowPreviousLocation(WindowPreviousLocation);
        setWindowPreviousSize(WindowPreviousSize);

    }
    /**
     * Initialising constructor.
     * @param WindowMinimumSize
     * @param WindowPreviousSize
     * @param WindowPreviousLocation
     * @param Language 
     */
    public ConfigWindow(Dimension WindowMinimumSize, Dimension WindowPreviousSize, Point WindowPreviousLocation, LanguageType Language) {  
        set(WindowMinimumSize, WindowPreviousSize, WindowPreviousLocation, Language);
    }
    /**
     * Default constructor.
     */
    public ConfigWindow() {
    }
     
    
   
    public void setWindowPreviousSize(Dimension WindowPreviousSize) {
        this.WindowPreviousSize = (WindowPreviousSize==null)?DEFAULT_WINDOW_SIZE_PREVIOUS:WindowPreviousSize;
    }

    public void setWindowPreviousLocation(Point WindowPreviousLocation) {
        this.WindowPreviousLocation = (WindowPreviousLocation==null)?DEFAULT_WINDOW_LOCATION_PREVIOUS:WindowPreviousLocation;
    }

    public void setWindowMinimumSize(Dimension WindowMinimumSize) {
        this.WindowMinimumSize = (WindowMinimumSize==null)?DEFAULT_WINDOW_SIZE_MINIMUM:WindowMinimumSize;
    }

    public void setLanguage(LanguageType Language) {
        this.Language = (Language==null)?DEFAULT_LANGUAGE:Language;
    }

    public Dimension getWindowPreviousSize() {
        return WindowPreviousSize;
    }

    public Dimension getWindowMinimumSize() {
        return WindowMinimumSize;
    }

    public Point getWindowPreviousLocation() {
        return WindowPreviousLocation;
    }

    public LanguageType getLanguage() {
        return Language;
    }
    
    
    
    @Override
    public final boolean copy(Config model) {
        if(model instanceof ConfigWindow){
            return copy((ConfigWindow)model);
        }else{
            copy(null);
            return false;
        }
    }    
    
    
    
    @Override
    public String getFileName() {
        return "config_window";
    }

    @Override
    public String toString() {
        return ""+ this.getWindowMinimumSize()+"\n"
            + this.getWindowPreviousSize()+"\n"
            + this.getLanguage()+"\n"
            + this.getWindowPreviousLocation()+"\n";
    }
    
    
    
}
