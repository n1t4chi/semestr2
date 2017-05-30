/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst;

/**
 * @param <Key> Must be the same as tree
 * @author n1t4chi
 */
public abstract class Node<Key extends Comparable<Key>>{
   abstract Node<Key> getLesser();
   abstract Node<Key> getGreater();
   abstract Key getValue();
   
   public boolean hasLesser(){
       return getLesser()!=null;
   }
   public boolean hasGreater(){
       return getGreater()!=null;
   }
   public boolean hasSubtrees(){
       return hasLesser()||hasGreater();
   }
}
