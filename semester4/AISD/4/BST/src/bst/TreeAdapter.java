/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst;

import bst.avl.AVL;
import bst.avl.AVLTreeNode;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author n1t4chi
 */
public class TreeAdapter {
    private final AVL<AVLTreeNode<Integer>,Integer> tree;

    public TreeAdapter() throws Exception {
        this.tree = new AVL(AVLTreeNode.class,Integer.class);
    }
    public void insert(int i){
        tree.insert(i);
    }
    public void delete(int i){
        tree.delete(i);
    }
    public void find(int i){
        if(tree.find(i) != null){
            System.out.println("1");
        }else{
            System.out.println("0");
        }
    }
    public void min(){
        Integer i = tree.min();
        if(i!=null)
            System.out.println(i);
        else
            System.out.println();
    }
    public void max(){
        Integer i = tree.max();
        if(i!=null)
            System.out.println(i);
        else
            System.out.println();
    }
    public void inorder(){
        List<AVLTreeNode<Integer>> l = tree.inorder();
        Collections.reverse(l);  
        l.forEach((n) -> {
            System.out.print(n+" ");
        });
        System.out.println();
            
    }
}
