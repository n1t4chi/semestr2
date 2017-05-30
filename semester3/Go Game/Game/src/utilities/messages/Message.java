 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import java.io.Serializable;

/**
 * Message class interface for Socket to communicate with connected host.
 * This class or just method {@link Message#getMessage() } should be overridden
 * since on its own provides only simple "ping" message for connection testing.
 * @author n1t4chi
 */
public interface Message extends Serializable{
    
    /**
     * Method used by Socket to get message to send over network.
     * It is advised to override this method.
     * @return Serialisable message.
     */
    public abstract Serializable getMessage();
    /**
     * Returns this message type.
     * @return this message type.
     */
    public abstract MessageType getMessageType();
    
}
