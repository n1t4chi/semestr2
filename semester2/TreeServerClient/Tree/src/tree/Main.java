/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tree;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTree;

/**
 * Main class. For test purposes.
 * @author n1t4chi
 */
public class Main {
    /**
     * Test class.
     */
    public static class test  implements Comparable<test>{
        byte x;
        byte y;
        /**
         * Test constructor
         * @param x X coordinate
         * @param y Y coordinate
         */
        public test(byte x, byte y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "("+x+","+y+")";
        }

        @Override
        public int hashCode() {
            return x*Byte.MAX_VALUE+y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final test other = (test) obj;
            if (this.x != other.x) {
                return false;
            }
            if (this.y != other.y) {
                return false;
            }
            return true;
        }
        
        @Override
        public int compareTo(test o) {
            return x-o.x+((x==o.x)?(y-o.y):0);
                    
        }  
    }
    /**
     * Test main method. Prints random tree.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Random r = new Random();
        //BinaryTree<test> tree = new BinaryTree<>();
        BinaryTree<Integer> tree = BinaryTree.randomDoubleTree();
        System.out.println(tree.draw(true));
        while(true){
            try {
                byte[] b = new byte[1024];
                int i = System.in.read(b);
                String s = new String(b,0,i-1);
                System.err.println("["+s+"]");
                Integer t = new Integer(s);
                tree.delete(t);
                System.out.println(tree.draw(true));
            } catch (IOException | NumberFormatException ex) {
                System.err.println("wrong");
            }
        }
    }
    
}
