/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layer;

/**
 * Configuration of style of layer projection
 * @author n1t4chi
 */
public interface StyleConfig {
    
    
    /**
     * Returns type of this config.
     * @return type of config.
     */
    public abstract StyleConfigType getType();
    
    
    
}
