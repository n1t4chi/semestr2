/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Frame.Program_Configuration;

/**
 * Configuration class.
 * @author n1t4chi
 */
public class Configuration extends Program_Configuration{



//#########################Static Context#######################################
    /**
     * Default tree type class.
     */
    private static Class DEFAULT_TREE_TYPE = Object.class;
    /**
     * Server config filename.
     */
    private static String FILE_NAME = "server.config";
    /**
     * Default port on which server works. Clients have this inserted as default port on which they connect to.
     */
    public static int DEFAULT_PORT = 5000;
    /**
     * Default policy of auto loading tree files on startup.
     */
    public static boolean DEFAULT_AUTOLOAD = false;
    /**
     * Default policy for replacing trees on loading.
     */
    private static boolean DEFAULT_LOAD_REPLACE = false;
    






//#########################Fields###############################################
    /**
     * Type of a tree.
     */
    public Class treeType;
    /**
     * Port on which server/client works.
     */
    public int port;
    /**
     * Whether to autoload trees [if available] on startup.
     */
    public boolean autoLoad;
    /**
     * Whether loaded trees should replace older ones.
     */
    public boolean replaceLoad;
    







//#########################Constructors#########################################
    /**
     * Default constructor. Uses default values.
     */
    public Configuration() {
        this(DEFAULT_TREE_TYPE,DEFAULT_PORT,DEFAULT_AUTOLOAD,DEFAULT_LOAD_REPLACE);
    }
    /**
     * Constructor.
     * @param LoadFromFile Whether to load from configuration file or not.
     */
    public Configuration(boolean LoadFromFile) {
        this(DEFAULT_TREE_TYPE,DEFAULT_PORT,DEFAULT_AUTOLOAD,DEFAULT_LOAD_REPLACE);
        if(LoadFromFile)
            loadFromFile();
    }

    /**
     * Constructor
     * @param treeType Type of tree.
     * @param port Port on which communication take place.
     * @param autoLoad Whether to load available trees on startup
     * @param replaceLoad Whether to replace trees on loading or not.
     */
    public Configuration(Class treeType,int port, boolean autoLoad, boolean replaceLoad){
        super(FILE_NAME);
        this.treeType = treeType;
        this.port = port;
        this.autoLoad = autoLoad;
        this.replaceLoad = replaceLoad;
    }







//#########################overriden methods####################################
    @Override
    public boolean testConfiguration(Object o) {
        return (o instanceof Configuration);
    }

    @Override
    public void copy(Program_Configuration copy){
        if(copy instanceof Configuration){
            this.treeType = ((Configuration)copy).treeType;
            this.port = ((Configuration)copy).port;
            this.autoLoad = ((Configuration) copy).autoLoad;
            this.replaceLoad = ((Configuration) copy).replaceLoad;
        }
    }

}
