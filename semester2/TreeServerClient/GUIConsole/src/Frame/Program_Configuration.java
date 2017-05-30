/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Configuration class.
 * @author n1t4chi
 */
public abstract class Program_Configuration implements Serializable{
    private String file_name;

    
    /**
     * Copies all fields from given object.
     * @param copy Object to copy.
     */
    public abstract void copy(Program_Configuration copy);
    
    
    /**
     * Test whether this object is proper configuration object.
     * @param o Object to test
     * @return True if this object is a proper configuration object.
     */
    public abstract boolean testConfiguration(Object o);
    
    
    /**
      * Loads configuration file
      * @return True if loading was successful.
      */ 
    public boolean loadFromFile(){
        boolean rtrn = false;
        FileInputStream fos = null;
        try {
            fos = new FileInputStream("\\"+file_name);          
            ObjectInputStream oos = new ObjectInputStream(fos);
            Object o = oos.readObject();
            if(testConfiguration(o)){
                copy((Program_Configuration)o);    
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
            fos = new FileOutputStream("\\"+file_name);   
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
     * Default constructor.
     * @param file_name name of a configuration file
     * @throws IllegalArgumentException If filename is null
     */
    public Program_Configuration(String file_name) throws IllegalArgumentException{
        if(file_name==null)   
            throw new IllegalArgumentException("Wrong parameters");     
        String test = file_name.replace(" ", "");
        if(test.length()==0)
            throw new IllegalArgumentException("Wrong parameters");
        this.file_name=file_name;
    }


}
