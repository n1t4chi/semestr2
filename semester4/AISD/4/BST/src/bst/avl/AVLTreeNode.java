/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst.avl;

import bst.BinaryTreeNode;

/**
 *
 * @author n1t4chi
 * @param <Key> Key type
 */
public class AVLTreeNode<Key extends Comparable<Key>> extends BinaryTreeNode<Key>{

    public AVLTreeNode(AVLTreeNode<Key> parent, Key value) {
        super(parent, value);
    }

    @Override
    public AVLTreeNode<Key> getGreater() {
        return (AVLTreeNode)super.getGreater(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AVLTreeNode<Key> getLesser() {
        return (AVLTreeNode)super.getLesser(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AVLTreeNode<Key> getParent() {
        return (AVLTreeNode)super.getParent(); //To change body of generated methods, choose Tools | Templates.
    }

    
    public int getBalanceFactor(){
        int l=0,g=0;
        if(hasGreater())
            g = getGreater().getHeight();
        if(hasLesser())
            l = getLesser().getHeight();
        return l-g;
    }
}
