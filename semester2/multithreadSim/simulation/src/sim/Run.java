package sim;

import java.awt.HeadlessException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Main class that starts whole application.
 * @author n1t4chi
 */
public class Run {
    /**
     * Prints error message if messages should be displayed
     * @param message Message to be displayed.
     */
    public static final void error(String message){
        if(messages){
            System.err.print(message);
        }
    }
    /**
     * Prints message if messages should be displayed
     * @param message Message to be displayed.
     */
    public static final void print(String message){
        if(messages){
            System.out.print(message);
        }
    }
    /**
     * Prints message if messages should be displayed. Adds line break.
     * @param message Message to be displayed.
     */
    public static final void println(String message){
        if(messages){
            System.out.println(message);
        }
    }
    
    /**
     * Whether messages should be displayed or not.
     */
    private static boolean messages;
    /**
     * Main method. Runs {@link Window}. Requires 4 [can be 5, more will be ignored] parameters as stated below: <br>
     * 1st argument: positive integer for amount of rows to be displayed. <br>
     * 2nd argument: positive integer for amount of columns to be displayed. <br>
     * 3rd argument: positive integer for speed [in miliseconds] of changing colour. <br>
     * 4th argument: double within range of [0,1] for chance of field changing colour randomly. <br>
     * 5th argument[optional]: if TRUE [case insensitive] information about colour changing will be displayed.<br>
     * @param args Arguments.
     */
    public static void main(String[] args) {
        try{
            try{
                messages = ("TRUE").equalsIgnoreCase(args[4]);
            }catch(ArrayIndexOutOfBoundsException ex){
                messages=false;
            }
            println( "(pre) parameters: rows["+args[0]+"] , columns["+args[1]+"] , speed["+args[2]+"] , chance["+args[3]+"]"+((args.length>=5)?(" , ["+args[4]+"]"):""));
            int rows = Integer.parseInt(args[0]);
            int columns = Integer.parseInt(args[1]);
            int speed = Integer.parseInt(args[2]);
            args[3] = args[3].replaceAll(",", ".");
            double chance = Double.parseDouble(args[3]);
            
            println( "(post)parameters: rows["+rows+"] , columns["+columns+"] , speed["+args[2]+"] , chance["+chance+"]"+((args.length>=5)?(" , ["+args[4]+"]"):""));
            Window w = new Window(rows,columns,speed,chance);  
        }catch( IllegalArgumentException | HeadlessException ex){
            System.out.println(ex.getMessage());
        }catch(ArrayIndexOutOfBoundsException ex){
            System.out.println("Not enough arguments");
        }
    }
}
