/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Frame.Program_Configuration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Configuration class.
 * @author n1t4chi
 */
public class Configuration extends Program_Configuration{
    /**
     * filename for configuration file.
     */
    private static String FILE_NAME = "client.config";
    /**
     * Default port of server.
     */
    public static int DEFAULT_PORT = 5000;
    /**
     * Default ip of server.
     */
    public static String DEFAULT_IP = "127.0.0.1";
    /**
     * Default tree id.
     */
    private static String DEFAULT_TREE = null;
    /**
     * Default value for loading trees policy.
     */
    private static boolean DEFAULT_LOAD_TREE = false;
    
    /**
     * Port on which server/client works.
     */
    public int port;
    /**
     * IP of the server.
     */
    public String ip;
    /**
     * Whether last used tree should be loaded automatically after successful connection.
     */
    public boolean loadTree;
    /**
     * Last ID of a tree.
     */
    public String treeID;
    
    
    /**
      * Loads configuration file
      * @return True if loading was successful.
      */ 
    public boolean loadFromFile(){
        boolean rtrn = false;
        FileInputStream fos = null;
        try {
            fos = new FileInputStream("\\"+FILE_NAME);          
            ObjectInputStream oos = new ObjectInputStream(fos);
            Object o = oos.readObject();
            if(o instanceof Configuration){
                copy((Configuration)o);    
                //System.out.println("loading location:"+windowLocation); 
                rtrn = true;
            }    
        } catch (FileNotFoundException ex) {
            System.err.println("Could not find file.");     
            saveToFile();
        } catch (IOException ex) {
            System.err.println("Could not load configuration.");
        } catch (ClassNotFoundException ex) {
            System.err.println("Wrong class");
        } finally {
            try {
                if(fos!=null)
                    fos.close();
            } catch (IOException ex) {
                System.err.println("Could not close configuration file.");
            }
        }
        return rtrn;
    }
     /**
      * Saves to configuration file
      * @return True if saving was successful.
      */ 
    public boolean saveToFile(){
        //System.out.println("saving location:"+windowLocation);
        boolean rtrn = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("\\"+FILE_NAME);   
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);     
            rtrn = true;
        } catch (FileNotFoundException ex) {
            System.err.println("Could not find file.");     
        } catch (IOException ex) {
            System.err.println("Could not save configuration.");
        } finally {
            try {
                if(fos!=null)
                    fos.close();
            } catch (IOException ex) {
                System.err.println("Could not close configuration file.");
            }
        }
        return rtrn;
    }
    
    
    
    /**
     * Default constructor. Uses default values.
     */
    public Configuration() {
        this(DEFAULT_PORT,DEFAULT_IP,DEFAULT_TREE,DEFAULT_LOAD_TREE);
    }
    /**
     * Constructor.
     * @param LoadFromFile Whether to load from configuration file or not.
     */
    public Configuration(boolean LoadFromFile) {
        this(DEFAULT_PORT,DEFAULT_IP,DEFAULT_TREE,DEFAULT_LOAD_TREE);
        if(LoadFromFile)
            loadFromFile();
    }

    /**
     * Constructor
     * @param port Port on which communication take place.
     * @param ip  IP of a server.
     * @param treeID Name of default tree to load if auto loading is on.
     * @param auto_load Whether to trying to load tree automatically or not.
     */
    public Configuration(int port,String ip,String treeID,boolean auto_load){
        super(FILE_NAME);
        this.port = port;
        this.ip = ip;
        this.treeID = treeID;
        this.loadTree = auto_load;
    }

    
    
    @Override
    public void copy(Program_Configuration copy){
        if(copy instanceof Configuration){
            this.port = ((Configuration)copy).port;
            this.ip = ((Configuration) copy).ip;
            this.loadTree = ((Configuration) copy).loadTree;
            this.treeID = ((Configuration) copy).treeID;
        }
    }

    @Override
    public boolean testConfiguration(Object o) {
        return (o instanceof Configuration);
    }

}
