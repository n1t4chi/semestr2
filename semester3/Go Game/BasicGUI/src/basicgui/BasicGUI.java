/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicgui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import util.ConfigWindow;

/**
 * Default class for running basicGui, opens BasicFrame.
 * @author n1t4chi
 */
public class BasicGUI {
    /**
     * Main Method, opens BasicFrame
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       // String txt = "<img SRC=http://www.cs.cmu.edu/~wjh/go/rules/Japanese.2.gif WIDTH=285, HEIGHT=293";
       // System.out.println(txt.substring(0,txt.indexOf(".gif"))+".gif");
                
        
       /* String regex = "("
                    + "([0-9])"
                    + "|([1-9][0-9])"
                    + "|(1([0-9]{2}))"
                    + "|(25[0-5])"
                    + "|(2[0-4][0-9])"
                + ")"
                + "("
                    + "("
                        + "[.]("
                            + "[0-9]|"
                + "([1-9][0-9])|"
                + "(1([0-9]{2}))|"
                + "(25[0-5])|"
                + "(2[0-4][0-9])|"
                        + ")"
                    + "){0,3}"
                + ")";
        String[] corr = {"127.0.0.1","255.255.255.255","","123","123.123."};
        String[] inv = {"127.01.0.1","256.255.255.255"," ","1234","123.."};
        System.out.println("Corrrect:");
        for(String str : corr){
            System.out.println("["+str+"]? "+ ((str.matches(regex)&&!str.contains(".."))||str.isEmpty())   );
        }
        System.out.println("Invalid:");
        for(String str : inv){
            System.out.println("["+str+"]? "+((str.matches(regex)&&!str.contains(".."))||str.isEmpty()));
        }
        */
        
        /*try {
            XMLEncoder e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream("./config/test.xml"))
            );
            e.writeObject(new ConfigWindow(
                    new Dimension(25,25),
                    new Dimension(45,35),
                    new Point(2, 2), 
                    ConfigWindow.LanguageType.POLISH
            ));
            e.flush();
            e.close();
            
            XMLDecoder d = new XMLDecoder(
                    new BufferedInputStream(
                            new FileInputStream("./config/test.xml"))
            );
            ConfigWindow win = (ConfigWindow) d.readObject();
            System.out.println(""
                    + win.getWindowMinimumSize()+"\n"
                    + win.getWindowPreviousSize()+"\n"
                    + win.getLanguage()+"\n"
                    + win.getWindowPreviousLocation()+"\n"
            );
            d.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicGUI.class.getName()).log(Level.SEVERE, null, ex);
        }*/

        SwingUtilities.invokeLater(() -> {
            BasicFrame bf = new BasicFrame("BasicGui",null,"TEST_BasicGui");
            bf.setVisible(true);
            bf.setEnabled(true);
         });
    }
    
}
