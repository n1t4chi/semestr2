/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import tree.BinaryTree;
import tree.BinaryTreeNode;

/**
 * Tree Model for server.
 * @author n1t4chi
 */
public class model implements TreeModel {
    /**
     * Root.
     */
    Server root;
    /**
     * Constructor
     * @param root Server object.
     */
    public model(Server root) {
        this.root = root;
    }
    
    @Override
    public Object getRoot() {
        return root;
    }
    @Override
    public Object getChild(Object parent, int index) {
        if(parent instanceof Server){
            //System.err.println(".getChild() server index:"+index);
            //System.err.println(".getChild() server tree:"+root.treeList.get(index));
            return root.treeList.get(index);
        }else      
            if(parent instanceof BinaryTree)
                return ((BinaryTree) parent).getChild(parent, index);
            else
                if(parent instanceof BinaryTreeNode)
                    return ((BinaryTreeNode) parent).getChildAt(index);
                else
                    return -1;
    }

    @Override
    public int getChildCount(Object parent) {
        if(parent instanceof Server){
            //System.err.println(".getChildCount() server "+root.treeList.size());
            return root.treeList.size();
        }else  
            if(parent instanceof BinaryTree)
                return ((BinaryTree) parent).getChildCount(parent);
            else
                if(parent instanceof BinaryTreeNode)
                    return ((BinaryTreeNode) parent).getChildCount();
                else
                    return 0;

    }

    @Override
    public boolean isLeaf(Object node) {
        if(node instanceof Server)
            return root.treeList.isEmpty();
        else
            if(node instanceof BinaryTree)
                return (((BinaryTree)node).getRoot()==null);
            else
                if(node instanceof BinaryTreeNode)
                    return ((BinaryTreeNode) node).isLeaf();
                else
                    return true;
    }
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if(parent instanceof Server){
            //System.err.println(".getIndexOfChild() server child:"+child);
            //System.err.println(".getIndexOfChild() server index:"+((Server)parent).treeList.indexOf(child));
            return ((Server)parent).treeList.indexOf(child);
        }else      
            if(parent instanceof BinaryTree)
                return ((BinaryTree) parent).getIndexOfChild(parent, child);
            else
                if((parent instanceof BinaryTreeNode)&&(child instanceof BinaryTreeNode))
                    return ((BinaryTreeNode) parent).getIndex((BinaryTreeNode)child);
                else
                    return -1;        
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        root.updateTree();
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }
};
