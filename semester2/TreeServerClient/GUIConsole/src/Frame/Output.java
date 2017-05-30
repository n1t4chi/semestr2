/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frame;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 *
 * @author n1t4chi
 */
public class Output extends OutputStream{
  //  PipedOutputStream out;
  //  BufferedReader reader; 
  //  PipedInputStream in;
    
    /**
     * JTextPanel where messages are written to.
     */
    private final JTextPanel output;
    /**
     * Charset object.
     */
    final Charset set;
  //  private PrintStream printStream;
    //OutputStreamWriter osw;
    /**
     * Whether it is error output.
     */
    boolean error;
    /**
     * Prefix for messages.
     */
    final String pre;
    /**
     * Suffix for messages.
     */
    final String suf;
    /**
     * constructor.
     * @param output Text panel where output should be written.
     * @param error Whether this output is error output or not.
     */
    public Output(JEditorPanel output,boolean error){
        set = Charset.forName("UTF-16");
        this.output=output;
        this.error=error;
        String col;
        if(error){
            col="255,0,0";
        }else{
            col ="0,0,0";
        }
        pre = "<font face=monospaced color=rgb("+col+")>";
        suf = "</font>";
    }
    
    @Override
    public synchronized void write(byte[] b) {
        //System.err.println(Arrays.toString(b));
        String txt = new String(b,set);
        if(txt.equals(""+(char)13+(char)10))
            output.newLine();
        else
            output.insert(pre+txt+suf);
    }

    @Override
    public synchronized void write(int b) {
        //System.err.println(""+(char)b);
        output.insert(pre+(""+(char)b)+suf);
    }
    
    
    @Override
    public synchronized void write(byte[] bytes, int i, int i1) {
        //System.err.println(Arrays.toString(bytes));
        String txt = new String(bytes,i,i1,set);
        if(txt.equals(""+(char)13+(char)10))
            output.newLine();
        else
            output.insert(pre+txt+suf);
    }

}
