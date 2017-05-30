/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst;

/**
 *
 * @author n1t4chi
 * @param <Key>
 */
public class BinaryTreeNode<Key extends Comparable<Key>> extends Node<Key>{
    private BinaryTreeNode<Key> lesser;
    private BinaryTreeNode<Key> greater;
    private BinaryTreeNode<Key> parent;
    private final Key value;

    public BinaryTreeNode(BinaryTreeNode<Key> parent,Key value) {
        this.parent = parent;
        this.lesser=null;
        this.greater = null;
        this.value = value;
    }

    public void setLesser(BinaryTreeNode<Key> lesser) {
        this.lesser = lesser;
    }

    public void setGreater(BinaryTreeNode<Key> greater) {
        this.greater = greater;
    }

    public void setParent(BinaryTreeNode<Key> parent) {
        this.parent = parent;
    }
    
    
    @Override
    public BinaryTreeNode<Key> getLesser() {
        return lesser;
    }
    @Override
    public BinaryTreeNode<Key> getGreater() {
        return greater;
    }

    public BinaryTreeNode<Key> getParent() {
        return parent;
    }
    
    
    public int getHeight(){
        int l=0,g=0;
        if(hasGreater())
            g = getGreater().getHeight();
        if(hasLesser())
            l = getLesser().getHeight();
        return 1+Math.max(l,g);
    }
    
    
    @Override
    public Key getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ""+value;
    }
    
}
