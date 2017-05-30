/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages;

import java.io.Serializable;

/**
 * Socket error messages super class. Cannot be send through Socket as it is not valid for serialisation!
 * @author n1t4chi
 */
public abstract class SocketError implements Message{
    final Exception Error;
    /**
     * Default constructor.
     * @param Error Exception which lead to creation of this message.
     */
    public SocketError(Exception Error) {
        if(Error ==null)
            throw new NullPointerException("null exception");
        this.Error = Error;
    }
    /**
     * Returns exception held by this message
     * @return exception
     */
    public Exception getError() {
        return Error;
    }

    @Override
    public Serializable getMessage() {
        return Error.getLocalizedMessage();
    }

}
