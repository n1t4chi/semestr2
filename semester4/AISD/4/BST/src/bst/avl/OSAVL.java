/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst.avl;

/**
 *
 * @author n1t4chi
 * @param <KeyT>
 */
public class OSAVL <KeyT extends Comparable<KeyT>> extends AVL<OSAVLTreeNode<KeyT>,KeyT>{
        
    public OSAVL(Class key_class) throws Exception {
        super(OSAVLTreeNode.class, key_class);
    }

    @Override
    public OSAVLTreeNode<KeyT> insert(KeyT value) {
        OSAVLTreeNode<KeyT> rtrn =  super.insert(value);
        OSAVLTreeNode<KeyT> node = rtrn;
        while (node != null){
            node.updateSize();
            node = node.getParent();
        }
        return rtrn;
    }

    @Override
    public OSAVLTreeNode<KeyT> delete(KeyT value) {
        OSAVLTreeNode<KeyT> rtrn =  super.delete(value);
        OSAVLTreeNode<KeyT> node = rtrn;
        while (node != null){
            node.updateSize();
            node = node.getParent();
        }
        //getRoot().updateRecursivelySize();
        return rtrn;
    }

    @Override
    protected void rightRotation(OSAVLTreeNode<KeyT> B) {
        super.rightRotation(B); //To change body of generated methods, choose Tools | Templates.
        if(B!=null){
            B.updateSize();
            if(B.hasGreater())
                B.getGreater().updateSize();
        }
    }

    @Override
    protected void leftRotation(OSAVLTreeNode<KeyT> B) {
        super.leftRotation(B); //To change body of generated methods, choose Tools | Templates.
        if(B!=null){
            B.updateSize();
            if(B.hasLesser())
                B.getLesser().updateSize();
        }
    }

    
    public OSAVLTreeNode<KeyT> Select(int position){
        if(position > 0){
            if (getRoot()!= null){
                return getRoot().Select(position);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    /**
     * 
     * @param key
     * @return -1 if no node was found 
     */
    public int Rank(KeyT key){
        OSAVLTreeNode<KeyT> node = find(key);
        if(node != null){
            int r = 1;
            if(node.hasLesser())
                r += node.getLesser().getSize();
            while(!node.equals(getRoot())){
                if( node.equals(node.getParent().getGreater()) ){
                    r += 1;
                    if(node.getParent().hasLesser())
                        r+= node.getParent().getLesser().getSize();
                }
                node = node.getParent();
            }
            return r;
        }else{
            return -1;
        }
    }
    
    
}
