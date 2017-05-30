/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Server interface, provides basic server methods: init(), restart(), dispose() and command interpretation.
 * Subclasses must override xxSubclass() methods since they are called by init(),restart() or dispose() methods
 * restart() calls dispose() then init() so leaving restartSubclass() body empty will still give intended results.
 * @author n1t4chi
 */
public abstract class Server implements ServerInterpreter {

    /**
     * String which which will be written together with error messages
     */
    private final String Error_prefix;
    /**
     * String which which will be written together with standard messages
     */
    private final String Output_prefix;
    /**
     * Default Constructor. 
     * @param out_prefix String which which will be written together with standard messages
     * @param err_prefix String which which will be written together with error messages
     * @throws NullPointerException if one of parameters is null pointer
     */
    public Server(String out_prefix,String err_prefix) {
        if((out_prefix==null)||(err_prefix==null))
            throw new NullPointerException("null pointer");
        this.Output_prefix = out_prefix;
        this.Error_prefix = err_prefix;
    }

    
    
    /**
     * Starts server. 
     * There is no need for printing messages prior to calling this message, nor in case of exception.
     * @throws Exception if there was any problem while performing this task. 
     */
    public final void init() throws Exception{
        try{
            printSys("Initiating server");
            initSubclass();
        }catch(Exception ex){
            printErr("Exception on initiating server:"+ex.getLocalizedMessage());
            ex.printStackTrace();
            throw ex;
        }
    }
    
    private static String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    /**
     * Prints Error message in console with date
     * @param s message
     */
    public synchronized void printErr(String s){
        System.err.println(this.Error_prefix+s);
    }
    /**
     * Prints System message in console with date
     * @param s message
     */
    public synchronized void printSys(String s){
        print(this.Output_prefix+s);
    }
        /**
     * Prints given string with date
     * @param s string to print
     */
    private synchronized void print(String s){
        System.out.println(s);
    }
    /**
     * Prints given string as input value
     * @param s string to print
     */
    public synchronized void printInput(String s){
        print(">"+s);
    }
    /**
     * Tries to restart sever.
     * There is no need for printing messages prior to calling this message, nor in case of exception.
     * @throws Exception if there was any problem while performing this task
     */
    public final void restart() throws Exception{
        try{
            printSys("Restarting server");
            dispose();
            restartSubclass();
            init();
        }catch(Exception ex){
            printErr("Exception on restaring server:"+ex);
            ex.printStackTrace();
            throw ex;
        }
    }
    /**
     * Closes all connection on server and saves settings.
     * There is no need for printing messages prior to calling this message, nor in case of exception.
     * @throws Exception if there was any problem while performing this task
     */
    public final void dispose() throws Exception{
        try{
            printSys("Disposing server");
            disposeSubclass();
        }catch(Exception ex){
            printErr("Exception on disposing server:"+ex);
            ex.printStackTrace();
            throw ex;
        }
    }
    
    /**
     * Returns whether the server works currently or not.
     * @return Whether the server works currently or not.
     */
    public abstract boolean isWorking();
    
    /**
     * Method called when init() method is called.
     * @throws Exception if there was any problem while performing this task
     */
    protected abstract void initSubclass() throws Exception;
    
    /**
     * Method called by restart() method after dispose() method is called and before init().
     * @throws Exception if there was any problem while performing this task
     */
    protected abstract void restartSubclass() throws Exception;
    /**
     * Method called by dispose() after the config file is saved.
     * @throws Exception if there was any problem while performing this task
     */
    protected abstract void disposeSubclass() throws Exception;
}
