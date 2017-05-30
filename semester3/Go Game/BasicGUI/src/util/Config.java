/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for configuration file.
 * Allows for loading previous configuration and saving to file.
 * You need to override methods such as that copy() as they are used to load configuration from file
 * Also getFileName() needs to return constant name at all executions for loading from file to work properly.
 * getDefaultConfig() is required by subclass to return its instance as it will cause bugs on runtime.
 * @author n1t4chi
 */
public abstract class Config implements Serializable{
    /**
     * Copies fields values from given object.
     * @param model to copy values from. On null the default configuration should set.
     * @return Whether it was successful or not
     */
    public abstract boolean copy(Config model);
    
    
    /**
     * Returns name of a config file.
     * @return name of a file
     */
    public abstract String getFileName();
    
    
    
    /**
     * Returns relative path to config file.
     * @return relative path to file.
     */
    public String getPath(String suffix){
        if((!suffix.isEmpty())&&(!suffix.startsWith("_"))){
            suffix="_"+suffix;
        }
        return getDirectory()+getFileName()+suffix+".xml";
    }
    /**
     * Returns relative path to config file.
     * @return relative path to file.
     */
    public String getDirectory(){
        return "./config/";
    }

    /**
     * Saves this configuration to file.
     * @return Whether it was successful or not.
     * @param suffix Suffix of a config file
     */
    public final boolean save(String suffix){
       // System.err.println("Save path:"+getPath(suffix));
        File file = new File(getPath(suffix));
        boolean proceed = true;
        try {     
            if(!file.exists()){
                File dir = new File(getDirectory());
                if(!dir.exists())
                    proceed = dir.mkdirs();
                if(proceed)
                    proceed = file.createNewFile();
            } 
            if(proceed){
                if(file.exists()&&(!file.canWrite())){
                    proceed = file.setWritable(true);
                }
                if(proceed){
                        XMLEncoder e = new XMLEncoder(
                            new BufferedOutputStream(
                                new FileOutputStream(file))
                        );
                        e.writeObject(this);
                        e.flush();
                        e.close();
                        
                }
           }
        } catch (Exception ex) {
            System.err.println("Error on saving: "+ex);
            ex.printStackTrace();
            proceed=false;
        }
        return proceed;
    }
    /**
     * Loads configuration from file and copies its configuration to this object.
     * If configuration was not loaded then default values are set.
     * @param suffix Suffix of a config file
     * @return whether it was successful or not.
     */
    public final boolean load(String suffix){
        //System.err.println("Save path:"+getPath(suffix));
        //System.out.println("util.Config.load()");
      /*  try{
            throw new Exception();            
        }catch(Exception ex){
            ex.printStackTrace();
        }*/
        boolean proceed = false;
      /*  if(file.exists()){
            if(!file.canRead()){
                 proceed=file.setReadable(true);
            }
            if(proceed){*/
                try {       
                    
                    XMLDecoder e = new XMLDecoder(
                        new BufferedInputStream(
                            new FileInputStream(getPath(suffix)))
                    );
                    
                    Config ob = (Config)e.readObject();
                    //System.err.println("ob:"+ob);
                    proceed = copy(ob);
                    e.close();
                } catch (IOException | ClassCastException | ArrayIndexOutOfBoundsException ex) {
                    System.err.println("Error on loading: "+ex);
                    proceed = false;
                }
         /*   }
        }*/
        if(!proceed)
            copy(null);
        return proceed;
    }

    
}

