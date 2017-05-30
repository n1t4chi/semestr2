/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import util.Config;

/**
 * Server configuration object.
 * @author n1t4chi
 */
public class ServerConfig extends Config{
    private static final int DEFAULT_SERVER_PORT=54321;
    /**
     * Returns default instance of server config
     * @return default instance
     */
    public static final ServerConfig getDefaultServerConfig(){
        return new ServerConfig(DEFAULT_SERVER_PORT);
    }
    /**
     * Server port
     */
    private int serverPort=-1;
    
    @Override
    public boolean copy(Config model) {
        if(model instanceof ServerConfig){
            serverPort = ((ServerConfig) model).serverPort;
            return true;
        }else{
            return false;
        }
    }
    /**
     * Configuration of server port.
     * @param serverPort Port on which server will run next time.
     //* @throws IllegalArgumentException If port is outside (0,65535) bonds.
     */
    public void setServerPort(int serverPort) {
        //if((serverPort<0)||(serverPort>65535))
        //    throw new IllegalArgumentException("Illegal port number");
        this.serverPort = serverPort;
    }
    /**
     * Returns server port
     * @return server port
     */
    public int getServerPort() {
        return serverPort;
    }
    /**
     * Default constructor.
     * @param serverPort port on which server will run.
     */
    public ServerConfig(int serverPort) {
        this.serverPort = serverPort;
    }
    /**
     * Constructor. Does not initialise any variable.
     */
    public ServerConfig() {
    }

    
    
    @Override
    public String getFileName() {
        return "Server";
    }
    
}
