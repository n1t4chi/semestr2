/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst.avl;

import bst.BinaryTree;
import bst.avl.AVLTreeNode;


/**
 *
 * @author n1t4chi
 * @param <NodeT> Node Type
 * @param <KeyT> Data type
 */
public class AVL<NodeT extends AVLTreeNode<KeyT>,KeyT extends Comparable<KeyT>> extends BinaryTree<NodeT,KeyT>{

    
    
    
    public AVL(Class node_class, Class key_class) throws Exception{
        super(node_class, key_class);
    }

    @Override
    public NodeT insert(KeyT value) {
        NodeT rtrn = super.insert(value);
        NodeT x = rtrn;
        if(x!=null){
            NodeT y = (NodeT) x.getParent();
            if(y!=null){
                NodeT z = (NodeT) y.getParent();
                while(z!=null){
                    int bf = z.getBalanceFactor();
                    if(Math.abs(bf) > 1){
                        if(z.getLesser() == y){
                            if(y.getLesser() == x){
                                rightRotation(y);
                            }else if(y.getGreater() == x){
                                left_rightRotation(x);
                            }else{
                                throw new RuntimeException("y has x as invalid child!");
                            }

                        }else if(z.getGreater() == y){
                            if(y.getLesser() == x){
                                right_leftRotation(x);
                            }else if(y.getGreater() == x){
                                leftRotation(y);
                            }else{
                                throw new RuntimeException("y has x as invalid child!");
                            }
                        }else{
                            throw new RuntimeException("z has y as invalid child!");
                        }
                        break;
                    }
                    x = y;
                    y = z;
                    z = (NodeT)z.getParent();
                }
            }
        }
        return rtrn;
    }
  
    
    @Override
    public NodeT delete(KeyT value) {
        NodeT rtrn = super.delete(value);
        NodeT x = rtrn;
        while(x!=null){
            int bal = x.getBalanceFactor();
            if(Math.abs(bal)>1){
                int bal_l = (x.hasLesser())?x.getLesser().getBalanceFactor():0;
                int bal_g = (x.hasGreater())?x.getGreater().getBalanceFactor():0;
                if(bal > 1 && bal_l >= 0){ // l l
                    rightRotation((NodeT)x.getLesser());
                }else if(bal > 1 && bal_l < 0){ //l r
                    left_rightRotation((NodeT)x.getLesser().getGreater());
                }else if(bal < -1 && bal_g <= 0){// r r
                    leftRotation((NodeT)x.getGreater());
                }else if(bal < -1 && bal_g > 0){// r l
                    right_leftRotation((NodeT)x.getGreater().getLesser());
                }
            }    
            x = (NodeT)x.getParent();
        }
        return rtrn;
    }
    
    
    
    /**
     * After inserting into C:
     *   A            (B)
     * /  \          /   \
     *x   (B)  ->   A     C 
     *    /  \     / \
     *   y    C   x   y
     * 
     * @param B B
     */
    protected void leftRotation(NodeT B){
        if(B!=null){
            NodeT A = (NodeT)B.getParent();
            NodeT C = (NodeT)B.getGreater();
            if(A!=null /* && C!=null*/){
                if(A.getGreater() != B)
                    throw new RuntimeException("Parent has given node as invalid child!");
                NodeT y = (NodeT)B.getLesser();
                B.setLesser(A);
                if(y!=null)
                    y.setParent(A);
                A.setGreater(y);
                
                if(A == getRoot())
                    setRoot(B);
                NodeT ap = (NodeT)A.getParent();
                A.setParent(B);
                B.setParent(ap);
                if(ap!=null){
                    if(ap.getLesser() == A)
                        ap.setLesser(B);
                    else if (ap.getGreater() == A)
                        ap.setGreater(B);
                    else{
                        throw new RuntimeException("Non null parent of given node parent has invalid children.");
                    }
                }   
            }else{ //:"+A+" or C:"+C+"
                System.err.println("leftRotation -> A is null pointer");
            }
        }else{
            System.err.println("leftRotation -> B is null pointer");
        }
    }    
     /**
     * After inserting into C:
     *      C              (B)
     *     / \            /   \
     *   (B)  x     ->   A     C 
     *  /   \                 /  \ 
     * A     y               y    x
     * 
     * @param node B
     */
    protected void rightRotation(NodeT B){
        if(B!=null){
            NodeT C = (NodeT)B.getParent();
            NodeT A = (NodeT)B.getLesser();
           // System.err.println("C:"+C);
           // System.err.println("C.l:"+C.getLesser());
           // System.err.println("C.g:"+C.getGreater());
           // System.err.println("B:"+B);
            if(/*A!=null &&*/ C!=null){
                if(C.getLesser() != B)
                    throw new RuntimeException("Parent has given node as invalid child!");
                    
                NodeT y = (NodeT)B.getGreater();
                B.setGreater(C);
                if(y!=null)
                    y.setParent(C);
                C.setLesser(y);
                
                if(C == getRoot())
                    setRoot(B);
                NodeT cp = (NodeT)C.getParent();
                C.setParent(B);
                B.setParent(cp);
                if(cp!=null){
                    if(cp.getLesser() == C)
                        cp.setLesser(B);
                    else if (cp.getGreater() == C)
                        cp.setGreater(B);
                    else{
                        throw new RuntimeException("Non null parent of given node parent has invalid children.");
                    }
                }   
            }else{//A:"+A+" or C:"+C+"
                System.err.println("rightRotation -> C is null pointer");
            }
        }else{
            System.err.println("rightRotation -> B is null pointer");
        }
    }
    
    /**
     * After inserting into C:
     *       C               (B)
     *     /  \            /    \
     *    A    x    ->    A      C 
     *  /   \           /  \    /  \ 
     * y    (B)        y    q   w    x
     *      /  \
     *     q    w
     * 
     * @param B
     */
    protected void left_rightRotation(NodeT B){
        if(B!=null){
            NodeT A = (NodeT)B.getParent();
            if(A!=null){
                NodeT C = (NodeT)A.getParent();
                if(C!=null){
                    leftRotation(B);
                    rightRotation(B);
                }else{
                    System.err.println("left_rightRotation -> C is null pointer");
                }
            }else{
                System.err.println("left_rightRotation -> A is null pointer");
            }
        }else{
            System.err.println("left_rightRotation -> B is null pointer");
        }
    }
    protected void right_leftRotation(NodeT B){
        if(B!=null){
            NodeT C = (NodeT)B.getParent();
            if(C!=null){
                NodeT A = (NodeT)C.getParent();
                if(A!=null){
                    rightRotation(B);
                    leftRotation(B);
                }else{
                    System.err.println("right_leftRotation -> A is null pointer");
                }
            }else{
                System.err.println("right_leftRotation -> C is null pointer");
            }
        }else{
            System.err.println("right_leftRotation -> B is null pointer");
        }
    }
    

    public int balanceFactor(){
        if(getRoot()!=null){
            return getRoot().getBalanceFactor();
        }else{
            return 0;
        }
    }
    
}
