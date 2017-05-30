/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import util.Config;

/**
 *
 * @author n1t4chi
 */
public class ConfigClient extends Config{
    /**
     * Default value for client name. Upon connection server should grant unique name.
     */
    public final static String DEFAULT_CLIENT_NAME="";
    /**
     * Default value for default server port for retrieving game room list.
     */
    public final static int DEFAULT_SERVER_PORT=54321;
    /**
     * Default value for default server IP.
     */
    public final static String DEFAULT_SERVER_IP="127.0.0.1";
    /**
     * Returns default configuration.
     * @return default configuration.
     */
    public static ConfigClient getDefaultConfigClient(){
        return new ConfigClient(DEFAULT_SERVER_PORT, DEFAULT_SERVER_IP,DEFAULT_CLIENT_NAME);
    }
    /**
     * Default server port for retrieving game room list.
     */
    private int defaultServerPort=-1;
    /**
     * Default server IP.
     */
    private String defaultServerIP=null;
    
    /**
     * Client name.
     */
    private String clientName=DEFAULT_CLIENT_NAME;
    
    
    @Override
    public boolean copy(Config model) {
        boolean rtrn = (model instanceof ConfigClient);
        if(rtrn){
            set(((ConfigClient)model).getDefaultServerPort(),((ConfigClient)model).getDefaultServerIP(),((ConfigClient)model).getClientName());  
        }else{
            set(DEFAULT_SERVER_PORT, DEFAULT_SERVER_IP,DEFAULT_CLIENT_NAME);
        }
        return rtrn;
    }

    /**
     * Changes all fields.
     * @param defaultServerPort
     * @param defaultServerIP
     * @param clientName
     */
    public void set(int defaultServerPort, String defaultServerIP,String clientName){
        setDefaultServerIP(defaultServerIP);
        setDefaultServerPort(defaultServerPort);
        setClientName(clientName);
    }
    
    /**
     * Default constructor.
     */
    public ConfigClient() {
    }

    /**
     * Initialising constructor.
     * @param defaultServerPort
     * @param defaultServerIP
     * @param clientName
     */
    public ConfigClient(int defaultServerPort, String defaultServerIP,String clientName) {
        set(defaultServerPort, defaultServerIP,clientName);
    }
    /**
     * Changes default server port
     * @param defaultServerPort default server port
     */
    public void setDefaultServerPort(int defaultServerPort) {
        this.defaultServerPort = (defaultServerPort<0||defaultServerPort>65535)?DEFAULT_SERVER_PORT:defaultServerPort;
    }
    /**
     * Changes default server IP
     * @param defaultServerIP default server IP
     */
    public void setDefaultServerIP(String defaultServerIP) {
        this.defaultServerIP = (defaultServerIP==null)?DEFAULT_SERVER_IP:defaultServerIP;
    }
    /**
     * Returns default server port
     * @return default server port
     */
    public int getDefaultServerPort() {
        return defaultServerPort;
    }
    /**
     * Returns default server IP
     * @return default server IP 
     */
    public String getDefaultServerIP() {
        return defaultServerIP;
    }

    public String getFileName() {
        return "config_client";
    }
    /**
     * Changes client name.
     * @param clientName client name.
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    /**
     * Returns client name.
     * @return client name.
     */
    public String getClientName() {
        return clientName;
    }
    
    
    
    
}
