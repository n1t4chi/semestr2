/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.launcher.LauncherGUI;
import basicgui.BasicFrame;
import java.awt.Rectangle;
import javax.swing.SwingUtilities;

/**
 * Main client class. Starts gui and loads config.
 * @author n1t4chi
 */
public class Client {
    /**
     * Configuration.
     */
    private final ConfigClient config;
    /**
     * LauncherGUI
     */
    final LauncherGUI launcher;
    
    /**
     * Returns config for client.
     * @return config.
     */
    public ConfigClient getConfig() {
        return config;
    }

    /**
     * Default constructor.
     */
    public Client() {
        config = new ConfigClient();
        config.load("");
        launcher = new LauncherGUI(config);
        launcher.setEnabled(false);
        launcher.setVisible(false);
    }
    /**
     * Starts LauncherGUI.
     */
    public void start(){
        launcher.setVisible(true);
        launcher.setEnabled(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            SwingUtilities.invokeLater(() -> {
                Client c = new Client();
                c.start();
            });
        }catch(Exception ex){
            
        }
    }
    
    
    
}
