/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frame;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Window_Configuration class.
 * @author n1t4chi
 */
public class Window_Configuration extends Program_Configuration{



//#########################Static Context#######################################
    /**
     * Default {@link #windowSize} value.
     */
    public static Dimension DEFAULT_WINDOW_SIZE = new Dimension(600,400);
    /**
     * Default minimum windows size.
     */
    public static Dimension MINIMUM_WINDOW_SIZE = new Dimension(500,300);
    /**
     * Default {@link #windowLocation} value.
     */
    public static Point DEFAULT_WINDOW_LOCATION = new Point(0,0);
    /**
     * Default extension of configuration file.
     */
    public static String DEFAULT_EXTENSION = "win_config";
    /**
     * Default filename of configuration file.
     */
    public static String DEFAULT_FILE_NAME = "config";






//#########################Fields###############################################
    
    /**
     * Size of a window.
     */
    public Dimension windowSize;   
    /**
     * Location of a window.
     */
    public Point windowLocation;
    /**
     * Location of a window.
     */
    public String extension;





//#########################Constructors#########################################
    
    /**
     * Default constructor. Used default values.
     */
    public Window_Configuration() {
        this(DEFAULT_WINDOW_SIZE,DEFAULT_WINDOW_LOCATION,DEFAULT_EXTENSION);
    }
    /**
     * Constructor.
     * @param LoadFromFile Whether to load from configuration file or not.
     * @param extension Extension of a save/load file.
     */
    public Window_Configuration(boolean LoadFromFile, String extension) {
        this(DEFAULT_WINDOW_SIZE,DEFAULT_WINDOW_LOCATION,extension);
        if(LoadFromFile)
            loadFromFile();
    }

    /**
     * Constructor.
     * @param windowSize Size of a window.
     * @param windowLocation Location of a window.
     * @param extension Extension of a save/load file.
     */
    public Window_Configuration(Dimension windowSize, Point windowLocation,String extension) {
        super(DEFAULT_FILE_NAME+"."+extension);
        this.windowSize = windowSize;
        this.windowLocation = windowLocation;
        this.extension = extension;
    }

    






//#########################overriden methods####################################
    
    @Override
    public void copy(Program_Configuration copy){
        if(copy instanceof Window_Configuration){
            this.windowSize = ((Window_Configuration)copy).windowSize;
            this.windowLocation = ((Window_Configuration)copy).windowLocation;
            this.extension = ((Window_Configuration)copy).extension;
        }
    }
    
    @Override
    public boolean testConfiguration(Object o) {
        return (o instanceof Window_Configuration);
    }

    
    
    
}
