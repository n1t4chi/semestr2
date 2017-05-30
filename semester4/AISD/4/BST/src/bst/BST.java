/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Tree class.
 * Tree should not store null values although methods will be null pointer exception proof.
 * Also null pointer must be considered the lowest value so they will always be in lesser subtree
 * Lesser subtree should contain lesser or equal elements if multiple equal values are allowed.
 * @author n1t4chi
 * @param <NodeT> - Class of nodes. At least must allow for BST properties.
 * @param <KeyT> -  Class of values held by nodes.
 * 
 */
public abstract class BST<NodeT extends Node<KeyT>,KeyT extends Comparable<KeyT>> {

    final Constructor<NodeT> cons;
    
    /**
     * Creates new node with given parameters. If this instance was initialised with parameterless constructor then this method will throw NullPointerException.
     * @param params
     * @return
     * @throws Exception 
     */
    public NodeT getNewNode(Object... params) throws Exception{
        return cons.newInstance(params);
    }
    /**
     * 
     */
    public BST() {
        this.cons = null;
    }
    
    /**
     * Creates new Binary Search Tree. This constructor must be used if Insert needs to use {@link #getNewNode(java.lang.Object...)} method.
     * Otherwise use parameterless constructor.
     * @param cl
     * @param parameterTypes
     * @throws java.lang.Exception on any error when trying to get constructor of given class with given parameters.
     */
    public BST(Class cl,Class<?>... parameterTypes) throws Exception{
        Constructor constructor = null;
        for(Constructor c : cl.getConstructors() ){
            boolean found;
            Class[] par = c.getParameterTypes();
            if(found = (par.length == parameterTypes.length)){
                for(int i=0 ; i< par.length ; i++){
                    if(!par[i].isAssignableFrom(parameterTypes[i])){
                        found = false;
                        break;
                    }
                }
            }
            if(found){
                constructor = c;
            }
        }
        if(constructor != null){
            this.cons = constructor;
        }else{
            throw new IllegalArgumentException("No constructor with given parameter types found.");
        }
    }
    
    
    
    /**
     * Inserts given value into the tree. Insert must keep BST properties.
     * @param value to insert
     * @return added node
     */
    public abstract NodeT insert(KeyT value);
    /**
     * Deletes given value from the tree. Delete must keep BST properties.
     * @param value to delete
     * @return node that took deleted key place or parent of deleted key if none was swaped.
     */
    public abstract NodeT delete(KeyT value);
    
    abstract NodeT getRoot();
    
    
    /**
     * Returns minimal value
     * @return minimal value
     */
    public KeyT min(){
        Node<KeyT> node = getRoot();
        if(node!=null){
            while(node.getLesser()!=null){
                node = node.getLesser();
            }
            return node.getValue();
        }else{
            return null;
        }
    }
    /**
     * Returns list of tree elements in inorder and descending.
     * @return list
     */
    public List<NodeT> inorder(){
        List<NodeT> rtrn = new ArrayList<>();
        inorder(getRoot(),rtrn);        
        return rtrn;
    }
    private void inorder(NodeT node,List<NodeT> list){
        if(node!=null){
            inorder((NodeT)node.getGreater(), list);
            list.add(node);
            inorder((NodeT)node.getLesser(), list);
        }
    }
    /**
     * Returns maximal value
     * @return maximal value
     */
    public KeyT max(){
        Node<KeyT> node = getRoot();
        if(node!=null){
            while(node.getGreater()!=null){
                node = node.getGreater();
            }
            return node.getValue();
        }else{
            return null;
        }
    }
    
    /**
     * Compares v1 and v2 and returns values just like v1.compareTo(v2) but is null proof.<br>
     * 0 -> v1 == v2<br>
     * -1 => v1 < v2<br>
     * 1 => v1 > v2<br>
     * null is considered the lowest value.
     * @param v1
     * @param v2
     * @return 
     */
    public int compare(KeyT v1,KeyT v2){
        if(v1 == null || v2 == null){
            if(v1 == v2)
                return 0;
            else if(v1 == null)
                return -1;
            else 
                return 1;
        }else{
            return v1.compareTo(v2);
        }
    }
    
    
    private int Counter = 0;
    public int getCounter(){
        return Counter;
    }
    /**
     * Returns whether the tree contains given value.
     * @param value
     * @return 
     */
    public NodeT find(KeyT value){
        NodeT node = getRoot();
        Counter = 0;
        while(node!=null){     
            Counter++;
            int comp = compare( value, node.getValue());  
            if(comp == 0){
                return node;
            }else if(comp>0){
                node = (NodeT)node.getGreater();
            }else /*comp<0*/{
                node = (NodeT)node.getLesser();
            }
            
                
        }
        return null;
    }
    
    private static final char CHAR_LEFT_DOWN=(char)0x2510;
    private static final char CHAR_LEFT_DOWN_HEAVY=(char)0x2513;
    private static final char CHAR_SPACE=(char)0x00A0;
    private static final char CHAR_SPACE_HEAVY=(char)0x2003;
    private static final char CHAR_UP_DOWN=(char)0x2503;
    private static final char CHAR_UP_DOWN_RIGHT = (char)0x2520;
    private static final char CHAR_UP_RIGHT= (char)0x2514;
    private static final char CHAR_UP_RIGHT_HEAVY = (char)0x2517;
            
    private String makeLine(spaces curr,List<spaces> open_pos,boolean lesser){
        String rtrn="";
        for(int i=0 ; i<open_pos.size();i++){
            spaces s = open_pos.get(i);
                for(int x=0;x<s.spaces;x++){
                    rtrn+=CHAR_SPACE;
                }
            if(s!=curr){
                if(s.draw){
                    rtrn+=CHAR_UP_DOWN;
                }else{
                    rtrn+=CHAR_SPACE_HEAVY; //double space
                }
            }else{
                if(curr.draw==true){
                    rtrn+=CHAR_UP_DOWN_RIGHT; //2523;
                }else{
                    rtrn+=(lesser)?CHAR_UP_RIGHT:CHAR_UP_RIGHT_HEAVY; //2514;
                }             
                break;
            }
        }
        return rtrn;
    }
    private String makeTextTree(Node<KeyT> node,int curr_level,List<spaces> open_pos){
        if(node!=null){
            String rtrn = ""+node;
            spaces curr = new spaces(curr_level, rtrn.length(),node.hasGreater()&&node.hasLesser());
            open_pos.add(curr);
            if(node.hasSubtrees()){     
                rtrn+=((node.hasGreater())?CHAR_LEFT_DOWN_HEAVY:CHAR_LEFT_DOWN)+"\n";
                if(node.hasLesser()){
                    rtrn+=makeLine(curr,open_pos,true);
                    rtrn+=makeTextTree(node.getLesser(),curr_level+1,open_pos);
                    curr.draw=false;
                    if(!node.hasGreater()){     
                        open_pos.remove(curr);
                    }else{
                        rtrn+="\n";
                    }
                }
                if(node.hasGreater()){
                    rtrn+=makeLine(curr,open_pos,false);
                    rtrn+=makeTextTree(node.getGreater(),curr_level+1,open_pos.subList(0, curr_level+1)); 
                }
            }    
            return rtrn;
        }else{
            return "null";
        }
    }
    private class spaces{
        int level;
        int spaces;
        boolean draw;
        public spaces(int level, int spaces, boolean draw) {
            this.level = level;
            this.spaces = spaces;
            this.draw = draw;
        }
    }
    
    
    @Override
    public String toString() {
        return makeTextTree(getRoot(),0,new ArrayList<>());
    }
    
}
