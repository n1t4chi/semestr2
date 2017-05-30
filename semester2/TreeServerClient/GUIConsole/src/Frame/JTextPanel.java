/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author n1t4chi
 */
public class JTextPanel extends JTextPane{
    final DateFormat df;
    String text="";
   // HTMLEditorKit kit;
   // HTMLDocument doc;
    void newLine(){
        text+="<br>";
        this.setText("<html>"+this.text+"</html>");   
    }
    void insert(String text){
        text=text.replaceAll("\n", "<br>");
        try{
            String add = "";
            if(df!=null)
                add = "["+df.format(new Date())+"]";
            this.text +=add+text;
        }catch(Exception ex){
            System.out.println("server.JTextPanel.<init>() Something wrong happened");
        }
        this.setText("<html>"+this.text+"</html>");  
    }
    /**
     * Constructor.
     */
    public JTextPanel() {
        DateFormat d;
        try{
            d = new SimpleDateFormat("h:mm:ss:SSS a" );
        }catch(Exception ex){
            d=null;
            System.out.println("server.JTextPanel.<init>() Something wrong happened");
        }
        
        DefaultCaret caret = (DefaultCaret)getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        df = d;
        setEditable(false);
        setContentType("text/html");
    }
    
}
