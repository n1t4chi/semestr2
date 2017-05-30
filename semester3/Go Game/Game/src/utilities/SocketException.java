/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

/**
 * Class used by {@link Socket} for communication errors.
 * @author n1t4chi
 */
public class SocketException extends RuntimeException{
    /**
     * Enum describing type of a thrown SocketException.
     */
    public enum Type { DEFAULT,CANNOT_CONNECT,CANNOT_INITIALISE,CANNOT_SEND,CANNOT_RECEIVE,LOST_CONNECTION,  }
    /**
     * Type of a exception.
     */
    private final Type exception_type;
    /**
     * Additional message provided with {@link SocketException#getMessage()} method.
     */
    private final String additional_info;
    
    /**
     * Simple constructor. Sets type to {@link Type#DEFAULT} and that's all.
     */
    public SocketException() {
        this(Type.DEFAULT,"");
    }
    /**
     * Additional constructor. Does not provide additional information for {@link SocketException#getMessage()}.
     * @param exception_type Type of an exception.
     */
    public SocketException(Type exception_type) {
        this(exception_type,"");
    }
    /**
     * Additional constructor. With given IP and port it provides "host[IP:port]" info for {@link SocketException#getMessage()}.
     * @param exception_type Type of an exception.
     * @param ip IP of a host related to this exception.
     * @param port Port of a host related to this exception.
     */
    public SocketException(Type exception_type,String ip, int port) {
        this(exception_type,"host["+ip+":"+port+"]");
    }
    /**
     * Additional constructor. Provides room for additional info before Host credentials.
     * With given IP and port it provides "msg [IP:port]" info for {@link SocketException#getMessage()}. 
     * @param exception_type Type of an exception.
     * @param msg Message to print in addition to host credentials.
     * @param ip IP of a host related to this exception.
     * @param port Port of a host related to this exception.
     */
    public SocketException(Type exception_type,String msg,String ip, int port) {
        this(exception_type,msg+" ["+ip+":"+port+"]");
    }
    /**
     * Default constructor. Additional info should provide info about host or in case of {@link Type#DEFAULT} type some information what exactly happened
     * @param exception_type Type of an exception.
     * @param s Additional message displayed with {@link SocketException#getMessage()}
     */
    public SocketException(Type exception_type,String s) {
        if(exception_type!=null)
            this.exception_type = exception_type;
        else
           this.exception_type = Type.DEFAULT;
        additional_info=s;
    }
    /**
     * Returns type of an exception.
     * @return 
     */
    public Type getType() {
        return exception_type;
    }

    @Override
    public String getMessage() {
        String rtrn;
        switch(exception_type){
            case CANNOT_CONNECT: 
                    rtrn= "Could not connect to host. "+additional_info+"."; 
                break;
            case CANNOT_RECEIVE: rtrn= "Could not receive message from host. "+additional_info+"."; break;
            case CANNOT_SEND: rtrn= "Could not send message to host. "+additional_info+"."; break;
            case LOST_CONNECTION: rtrn= "Lost connection with host. "+additional_info+(((additional_info!=null)&&(additional_info.isEmpty()))?"":"."); break;
            case CANNOT_INITIALISE: rtrn = "Couldn't initialise socket. "+additional_info; break;
            default: rtrn= "Problem with socket. "+additional_info+""; break;
        }
        return rtrn;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }
    
    
}
