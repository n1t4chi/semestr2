/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst.avl;


/**
 *
 * @author n1t4chi
 * @param <Key>
 */
public class OSAVLTreeNode<Key extends Comparable<Key>> extends AVLTreeNode<Key>{
    
    public OSAVLTreeNode(OSAVLTreeNode<Key> parent,Key value) {
        super(parent, value);
    }
    
    int size=0;

    
    public void updateSize() {
        int nsize = 1;
        if(hasLesser())
            nsize += getLesser().getSize();
        if(hasGreater())
            nsize += getGreater().getSize();
        this.size = nsize;
    }
    public void updateRecursivelySize() {
        int nsize = 1;
        if(hasLesser()){
            getLesser().updateRecursivelySize();
            nsize += getLesser().getSize();
        }
        if(hasGreater()){
            getGreater().updateRecursivelySize();
            nsize += getGreater().getSize();
        }
        this.size = nsize;
    }

    public int getSize() {
        return size;
    }

    
    
    
    @Override
    public OSAVLTreeNode<Key> getGreater() {
        return (OSAVLTreeNode<Key>) super.getGreater(); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public OSAVLTreeNode<Key> getParent() {
        return (OSAVLTreeNode<Key>) super.getParent(); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public OSAVLTreeNode<Key> getLesser() {
        return (OSAVLTreeNode<Key>) super.getLesser(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return super.toString()+"#"+getSize();
    }
    
    
    public OSAVLTreeNode<Key> Select(int position){
        int k = 1;
        if(hasLesser())
            k+=getLesser().getSize();
        if(position==k){
            return this;
        }else if (position<k) {
            if(hasLesser())
                return getLesser().Select(position);
            else
                return null;
        }else{
            if(hasGreater())
                return getGreater().Select(position-k);
            else
                return null;
        }
    }
    
}
