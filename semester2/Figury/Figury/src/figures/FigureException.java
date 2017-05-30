/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package figures;

/**
 * Exceptions thrown by Polygon and its subclasses.
 * @author n1t4chi
 */
public class FigureException extends Exception{
    
   //##########################static context###################################
    /**
     * Constant for error code. FigureException thrown with this code should indicate that given list of coordinates are not equal in POLYGON_POINT_SIZE.
     */     
    public static final int FIGURE_EXCEPTION_POINT_ARRAYS_NOT_EQUAL_SIZE=-666;
    /**
     * Constant for error code. FigureException thrown with this code means that one of objects passed as arguments are null pointers.
     */     
    public static final int FIGURE_EXCEPTION_NULL_POINTER=-1337;
    /**
     * Constant for error code. FigureException thrown with this code means that argument for border thickness was negative.
     */     
    public static final int FIGURE_EXCEPTION_NEGATIVE_BORDER_THICKNESS=-1;
    /**
     * Constant for error code. FigureException thrown with this code means that zero points were passed as arguments
     */     
    public static final int FIGURE_EXCEPTION_ZERO_COORDINATES=0;
    /**
     * Constant for error code. FigureException thrown with this code means that string given to recreate figure from is invalid.
     */     
    public static final int FIGURE_EXCEPTION_BAD_SAVE_STRING_FORMAT=666;
    /**
     * Constant for error code. FigureException thrown with this code means that arrays of coordinates are with wrong size.
     */     
    public static final int FIGURE_EXCEPTION_WRONG_COORD_TAB_SIZE=1337;
    
    


   //##########################fields###########################################
    
    
    /**
     * Error message printed by {@link#getMessage()};
     */
    final String err_message;              
    /**
     * Error code.
     */
    final int err_code;
    
    


   //#########################methods###########################################
    
    /**
     * Returns error code.
     * @return error code.
     */ 
    public int getCode(){
        return err_code;
    }


    
    
    
    
    
   //#########################constructors######################################
    
    
    /**
     * Default constructor.
     * @param Cause Error code for why exception was thrown. Should be one of Error Constants that start with FIGURE_EXCEPTION, otherwise generic info will be displayed in message.
     * @see #FIGURE_EXCEPTION_POINT_ARRAYS_NOT_EQUAL_SIZE
     */
    public FigureException(int Cause) {
        err_code = Cause;
        switch(Cause){        
            case FIGURE_EXCEPTION_POINT_ARRAYS_NOT_EQUAL_SIZE:
                    err_message="Arrays of X and Y coordinates are not egual in size.";
                break;
            case FIGURE_EXCEPTION_NULL_POINTER:
                    err_message="One of arguments is null pointer.";
                break;
            case FIGURE_EXCEPTION_NEGATIVE_BORDER_THICKNESS:
                    err_message="Negative border thickness was passed as argument.";
                break;
            case FIGURE_EXCEPTION_ZERO_COORDINATES:
                    err_message="Zero points were given to make polygon from";
                break;
            case FIGURE_EXCEPTION_BAD_SAVE_STRING_FORMAT:
                    err_message="Invalid string for recreating figure";
                break;
            default:
                    err_message="Something went wrong.";
                break;
        }
    }     
    
  



   //#########################overriden methods#################################  
    
    @Override
    public String getMessage() {
        return err_message;
    }
}
