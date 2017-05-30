/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 * Interpreter interface for server.
 * @author n1t4chi
 */
public interface ServerInterpreter {
    
    /**
     * Interprets given command and parameters and executes specified tasks if interpretation was successful
     * @param command Command
     * @param parameters parameters for given command
     */
    public abstract void interpret(String command,String... parameters);
    
    /**
     * Interprets given command with parameters and executes specified tasks is interpretation was successful
     * @param input Command with parameters joined within one string
     */
    public abstract void interpret(String input);
    
    
    
    
}
