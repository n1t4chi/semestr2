/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * OutputStream used for copying standard/error output into given text panel.
 * @author n1t4chi
 */
public class Output extends PrintStream{
  //  PipedOutputStream out;
  //  BufferedReader reader; 
  //  PipedInputStream in;
    
    
    private final PrintStream original;
    
    /**
     * JTextPanel where messages are written to.
     */
    private final JEditorPane output;
    /**
     * HTML editor kit
     */
    private final HTMLEditorKit kit;
    
    /**
     * colour of the text
     */
    private final String textColour;
    
    
    private static String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    /**
     * Adds given text at the end of the chat output.
     * @param text text to print
     */
    @Override
    public void print(String text){
       // super.print("["+getDate()+"]"+text);
        try {
            String font_col,source;

            text = "<font color =\""+textColour+"\">["+getDate()+"]"+text+"</font>";
            HTMLDocument doc = (HTMLDocument)output.getDocument();
            kit.insertHTML(doc,doc.getLength(), text, 0,0,null);
            //kit.write(out, doc, ERROR, WIDTH);
            //doc.insertString(doc.getLength()  ,text, null);
        } catch (BadLocationException | IOException ex) {
            System.err.println("Failed to insert message: "+ex.getLocalizedMessage());
        }
    }
    
    
    
    /**
     * constructor.
     * @param original
     * @param output Text panel where output should be written.
     * @param error Whether this output is error output or not.
     */
    public Output(PrintStream original,JEditorPane output,boolean error){
        super(original,true);
        this.original = original;
        kit = (HTMLEditorKit)output.getEditorKit();
        this.output=output;
        if(error){
            textColour="#ff0000";
        }else{
            textColour ="#000000";
        }
    }

    
    
    
    
    
 /*   @Override
    public synchronized void write(byte[] b) {
        //System.err.print(Arrays.toString(b));
        try{
            super.write(b);
        }catch(IOException ex){}
        
        String txt = new String(b);
        if(txt.equals(""+(char)13+(char)10)){
            //print("");
        }else{
            print(""+txt);
        }
    }*/

  /*  @Override
    public synchronized void write(int b) {
        //super.write(b);
        //print(""+(char)b);
    }*/

  /*  @Override
    public void print(char c) {
        super.print(c);
        print(""+c);
    }

    @Override
    public void println(char x) {
        super.println(x);
        print(""+x);
    }*/
    
    
    
    
    @Override
    public synchronized void write(byte[] bytes, int i, int i1) {
        super.write(bytes, i, i1);
        //System.err.print(Arrays.toString(bytes));
        String txt = new String(bytes,i,i1);
        if(txt.equals(""+(char)13+(char)10)){
            //print("");
        }else{
            print(txt);
        }
    }

}
