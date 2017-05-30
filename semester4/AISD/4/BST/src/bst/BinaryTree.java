/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst;

/**
 * Binary Tree
 * @author n1t4chi
 * @param <NodeT> Node class. Constructor needs to have (NodeT parent,KeyT value) constructor.
 * @param <KeyT>
 */
public class BinaryTree<NodeT extends BinaryTreeNode<KeyT>,KeyT extends Comparable<KeyT>> extends BST<NodeT,KeyT>{
    private NodeT /*BinaryTreeNode<KeyT>*/ root;
    
    /** 
     * Creates new Binary Tree.
     * @param node_class Class of a node.
     * @param key_class Class of a value
     * @throws java.lang.Exception
     * @see BST#BST(java.lang.Class, java.lang.Class...)
     */
    public BinaryTree(Class node_class, Class key_class) throws Exception{
        super(node_class,node_class,key_class);
        root = null;
    }

    
    @Override
    public NodeT/*BinaryTreeNode<KeyT>*/ insert(KeyT value) {
        NodeT/*BinaryTreeNode<KeyT>*/ y = getRoot();
        try {
            if(y!=null){
                do{
                    NodeT x;
                    int comp = compare(value, y.getValue());
                    if(comp > 0){
                        if( (x = (NodeT)y.getGreater()) == null){
                            y.setGreater(x = getNewNode(y,value));
                            y = (NodeT) x;
                            break;
                        }
                    }else{
                        if( (x = (NodeT)y.getLesser()) == null){
                            y.setLesser(x = getNewNode(y,value));
                            y = (NodeT) x;
                            break;
                        }
                    }
                    y = (NodeT)x;
                }while(true);
                return y;
            }else{
                root = getNewNode(null,value);/*new BinaryTreeNode<>(null,value);*/
                return root;
            }
        } catch (Exception ex) {
            System.err.println("Cannot create new node: "+ex);
            return null;
        }
    }
    
    @Override
    public /*BinaryTreeNode<KeyT>*/NodeT delete(KeyT value) {
        return delete(find(value));
    }
    private NodeT/*BinaryTreeNode<KeyT>*/ delete(NodeT/*BinaryTreeNode<KeyT>*/ node) {
        if(node != null ){
            NodeT/* BinaryTreeNode<KeyT>*/ p = (NodeT) node.getParent();
            if(!node.hasSubtrees()){
                node.setParent(null);
                node.setGreater(null);
                node.setLesser(null);
                if(p == null){
                    if(node == root){
                        root = null;
                        return null;
                    }else    
                        throw new RuntimeException("Node has no parent and is not a root!");
                }else{
                    if(p.getGreater() == node)
                        p.setGreater(null);
                    else if (p.getLesser() == node)
                        p.setLesser(null);
                    else{
                        throw new RuntimeException("Non null parent of node to delete has invalid children.\np:"+p+"\np.l:"+p.getLesser()+"\np.g:"+p.getGreater()+"\nnode:"+node);
                    }
                    return p;
                }
            }else{
                NodeT/*BinaryTreeNode<KeyT>*/ n;
                NodeT/*BinaryTreeNode<KeyT>*/ rtrn;
                if(node.hasLesser() && node.hasGreater()){
                    n = predecessor(node);
                   // n = node.getLesser();
                   // while(n.hasGreater()){
                   //     n=n.getGreater();
                   // }
                    rtrn = (NodeT)n.getParent();
                    //rtrn = n;
                    if(n.getParent() != node){
                        if(n.hasLesser()){
                            n.getParent().setGreater(n.getLesser());
                            n.getLesser().setParent(n.getParent());
                            n.setLesser(null);
                        }else{
                            n.getParent().setGreater(null);
                        }
                        n.setGreater(node.getGreater());
                        n.setLesser(node.getLesser());
                    }else{
                        n.setGreater(node.getGreater());
                    }
                    node.getLesser().setParent(n);
                    node.getGreater().setParent(n);
                }else {
                    boolean greater = node.hasGreater();
                    n = (NodeT)((greater)?node.getGreater():node.getLesser());  
                    rtrn = n;
                }
                if(p != null) {
                    n.setParent(p);
                    if(p.getLesser() == node){
                        p.setLesser(n);
                    }else if (p.getGreater() == node){
                        p.setGreater(n);
                    }else{
                        throw new RuntimeException("Non null parent of node to delete has invalid children.\np:"+p+"\np.l:"+p.getLesser()+"\np.g:"+p.getGreater()+"\nnode:"+node);
                    }
                }else if(root == node){
                    root = n;
                    n.setParent(null);
                }else{
                    throw new RuntimeException("Node has no parent and is not a root!");
                } 
                node.setParent(null);
                node.setGreater(null);
                node.setLesser(null);
                return rtrn;
            }
        }else
            return null;
    }
    @Override
    public NodeT/*BinaryTreeNode<KeyT>*/ getRoot() {
        return root;
    }
    
    public void setRoot(NodeT /*BinaryTreeNode<KeyT>*/ root) {
        this.root = root;
    }
    
    public NodeT/*BinaryTreeNode<KeyT>*/ predecessor(KeyT value){
        return predecessor(find(value));
    }
    public NodeT/*BinaryTreeNode<KeyT>*/ predecessor(NodeT/*BinaryTreeNode<KeyT>*/ n){
        //System.out.print("succesor for "+n+":");
        if(n!=null){
            if(n.hasLesser()){
               n = (NodeT)n.getLesser();
               while(n!=null && n.hasGreater()){
                   n = (NodeT)n.getGreater();
               }
               return n;
            }else{
                NodeT/*BinaryTreeNode<KeyT>*/ m = (NodeT)n.getParent();
                if(m!=null){
                    if(m.getGreater() == n){//n is greater than it's parent
                        return m;
                    }else{//n is lesser than it's parent so we are going upwards till the subtree is greater than m;
                        do{
                            n = m;
                            m = (NodeT)m.getParent();
                        }while(m!=null && n == m.getLesser());
                        return m;
                    }
                }
            }
        }
        return null;
    }
    
    public int height(){
        if(getRoot()!=null){
            return getRoot().getHeight();
        }else{
            return 0;
        }
    }
}
