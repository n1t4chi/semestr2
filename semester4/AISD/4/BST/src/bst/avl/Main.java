/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst.avl;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author n1t4chi
 */
public class Main {
    public static void main(String[] args) {
        try {
            OSAVL<Integer> tree = new OSAVL(Integer.class);
            
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
