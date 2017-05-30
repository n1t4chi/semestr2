/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zad3;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author n1t4chi
 */
public class EditDistance {
    
    public static int ED(String A, String B){
        final int Al = A.length(), Bl = B.length();
        if(Al>Bl)
            return ED(B,A);
        A = A.toLowerCase();
        B = B.toLowerCase();
        byte[][] E = new byte[2][Bl+1];
       // System.out.print("\t");
        for(int j = 1 ; j<E[0].length ; j++){
       //     System.out.print("\t"+B.charAt(j-1));
            E[0][j] = (byte) j;
        }
       // System.out.println();
        for(int i = 0 ; i<Al ; i++){
            E[1][0] = (byte) (i+1);
            for(int j = 0 ; j<Bl ; j++){
                E[1][j+1] = (byte)
                        Math.min( 
                                E[0][j]+ ((A.charAt(i) == B.charAt(j))?0:1) ,
                                Math.min( 
                                        E[0][j+1]+1,
                                        E[1][j]+1 
                                )
                        );
            }
          /*  if( i >0)
                System.out.print( A.charAt(i-1) );
            for(int it : E[0])
                System.out.print("\t"+it);
            System.out.println("");*/
            E[0] = E[1];
            E[1] = new byte[Bl+1];
        }
      /*  System.out.print( A.charAt(A.length()-1) );
        for(int it : E[0])
            System.out.print("\t"+it);
        System.out.println("");*/
        
        return E[0][Bl];
    }
    
    public static ArrayList<String> readFile(String filename) {
        ArrayList<String> al = new ArrayList(354_985);
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.lines()
                .forEach((String s) -> al.add(s)) 
            ;
        } catch (FileNotFoundException ex) {
            System.err.println("File not found:"+ex);
        } catch (IOException ex) {
            System.err.println("Error on reading from file:"+ex);
        }
        //System.err.println(sb.toString());
        return al;
    }
    
    private static class AA extends AbstractAction{
        final JButton b ;
        final JTextField tf;
        public AA(JButton b, JTextField tf) {
            this.b = b;
            this.tf = tf;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String s = b.getText();
            if(s!=null && !s.isEmpty()){
                tf.setText(b.getText());
            }
        }
        
    }
    
    public static void main(String[] args) {
        //System.out.println("result: "+ED("snowy","sunny"));
        //System.out.println("result: "+ED("exponential","polynomial"));

        ArrayList<String> words = readFile("words.txt");
        HashMap<String,Integer> words_s = new HashMap<>(words.size());
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setLayout(new GridLayout(2, 1));
            
            JPanel jp = new JPanel(new GridLayout(1, 3));
            final JButton[] b = new JButton[3];
            JTextField tf = new JTextField();
            tf.addKeyListener(new KeyAdapter() {
              // volatile boolean work = false;
                @Override
                public void keyReleased(KeyEvent e) {
                    if(tf.getText().isEmpty()){
                        for (JButton b1 : b) {
                            b1.setText("");
                        }
                    }else{
                        for(String t : words){
                            words_s.put(t,ED(tf.getText(), t));
                        }
                        Stream<String> s = words.stream()
                            .parallel()
                            .sorted((o1, o2) -> Integer.compare(words_s.get(o1),words_s.get(o2)));
                        Iterator<String > si = s.iterator();
                        for (JButton b1 : b) {
                            if(si.hasNext())
                                b1.setText(si.next());
                        }
                    }
                    //frame.repaint();
                }
                
            });
            frame.add(jp);
            frame.add(tf);
            for(int i=0 ; i<b.length ; i++){
                b[i] = new JButton("");
                b[i].setAction(new AA(b[i],tf));
                jp.add(b[i]);
            }
            
            frame.pack();
            frame.setMinimumSize(frame.getPreferredSize());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        });
        
        
    }
    
}
