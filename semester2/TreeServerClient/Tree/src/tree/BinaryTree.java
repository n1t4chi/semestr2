package tree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Binary tree class. Allows all classes to be used as values although there are certain limitations.
 * If class isn't derived from {@link Comparable} interface then tree will be made as a list since there is 
 * @author n1t4chi
 * @param <T> Data type that is stored inside.
 */
public class BinaryTree<T> implements TreeModel,Serializable{
    //#########################Static Context###############################
    /**
     * Delete mode in which whole tree is deleted.
     */
    public static final boolean DELETE_ALL = true;
    /**
     * Delete mode in which only node is deleted.
     */
    public static final boolean DELETE_NODE = false;
    
    /**
     * Returns random Integer tree.
     * @return random Integer tree.
     */
    public static final BinaryTree randomIntegerTree(){
        return randomIntegerTree(3,40,0,150);
    }
    /**
     * Returns random Integer tree.
     * @param min_node Minimal amount of nodes.
     * @param max_node Maximal amount of nodes.
     * @param min_range Minimal values.
     * @param max_range Maximal values
     * @return random Integer tree.
     * @throws IllegalArgumentException if minimum values are bigger than maximum.
     */
    public static final BinaryTree randomIntegerTree(int min_node,int max_node,int min_range,int max_range){
        if((min_node>max_node)||(min_range>max_range)){
            throw new IllegalArgumentException("Minimal range is bigger than maximal");
        }
        BinaryTree<Integer> rtrn = new BinaryTree<>(Integer.class);
        Random rand = new Random();     
        for(int i=0; i<rand.nextInt(max_node-min_node+1)+min_node;i++){
            rtrn.insert(rand.nextInt(max_range-min_range+1)+min_range);
        }
        return rtrn;
    }
    /**
     * Returns random Double tree.
     * @param min_node Minimal amount of nodes.
     * @param max_node Maximal amount of nodes.
     * @param min_range Minimal values.
     * @param max_range Maximal values
     * @return random Double tree.
     * @throws IllegalArgumentException if minimum values are bigger than maximum.
     */
    public static final BinaryTree randomDoubleTree(int min_node,int max_node,double min_range,double max_range){
        if((min_node>max_node)||(min_range>max_range)){
            throw new IllegalArgumentException("Minimal range is bigger than maximal");
        }
        BinaryTree<Double> rtrn = new BinaryTree<>(Double.class);
        Random rand = new Random();      
        for(int i=0; i<rand.nextInt(max_node-min_node+1)+min_node;i++){            
            double val = min_range;
            if(max_range-min_range>=0)
                val += rand.nextInt((int)Math.round(max_range-min_range));
            if(max_range-val<1&&max_range-val>0){
                val += (rand.nextDouble() / (max_range-val));
            }else{
                if(max_range<val){
                    val = max_range;
                }else{
                    val +=rand.nextDouble();
                }
            }   
            rtrn.insert(val);
        }
        return rtrn;
    }
    /**
     * Returns random Double tree.
     * @return random Double tree.
     */
    public static final BinaryTree randomDoubleTree(){
        return randomDoubleTree(3,40,0,1);
    }
    /**
     * Returns random String tree;
     * @return random Double tree.
     */
    public static final BinaryTree randomStringTree(){
        return randomStringTree(3,40,3,10);
    }
    /**
     * Returns random String tree. Values are randomly generated letters.
     * @param min_node Minimal amount of nodes.
     * @param max_node Maximal amount of nodes.
     * @param min_length Minimal length.
     * @param max_length Maximal length.
     * @return random Double tree.
     */
    public static final BinaryTree randomStringTree(int min_node,int max_node,int min_length,int max_length){
        if((min_node>max_node)||(min_length>max_length)){
            throw new IllegalArgumentException("Minimal range is bigger than maximal");
        }
        BinaryTree<String> rtrn = new BinaryTree<>(String.class);
        Random rand = new Random();      
        char[] alphabet = "qwertyuiopasdfghjklzxcvbnm".toCharArray();
        for(int i=0; i<rand.nextInt(max_node-min_node+1)+min_node;i++){     
            String t="";
            for( int a=0; a<(min_length + rand.nextInt(max_length-min_length+1)) ;a++  ){
                char c = alphabet[rand.nextInt(alphabet.length)];
                if(rand.nextBoolean()){
                    c = Character.toUpperCase(c);
                }
                t+=c;       
            }
            rtrn.insert(t);
        }
        return rtrn;
    }

    //#########################Fields#######################################
    /**
     * Class type. It's object class unless specifically picked. Can be used for differentiating purposes.
     */
    Class classType;
    
    
    /**
     * ID of a tree.
     */
    final String treeID;
    /**
     * Current node pointer.
     */
    BinaryTreeNode<T> curr_node;
    /**
     * Current insert mode.
     */
    int insert_mode;
    /**
     * Current delete mode.
     * @see #DELETE_ALL
     * @see #DELETE_NODE
     */
    boolean delete_mode;

    /**
     * Main node
     */
    BinaryTreeNode<T> root;

    //#########################Methods######################################
    /**
     * Changes current insertion mode.
     * @see BinaryTreeNode#INSERT_TYPE_ALL
     * @see BinaryTreeNode#INSERT_TYPE_ALL_OBJECTS
     * @see BinaryTreeNode#INSERT_TYPE_ALL_VALUES
     * @see BinaryTreeNode#INSERT_TYPE_SELECTIVE
     * @param type New mode.
     */
    public void setClassType(Class type){
        if(type==null){           
            type= Object.class;
        }
        classType = type;
    }
    /**
     * Changes current insertion mode.
     * @see BinaryTreeNode#INSERT_TYPE_ALL
     * @see BinaryTreeNode#INSERT_TYPE_ALL_OBJECTS
     * @see BinaryTreeNode#INSERT_TYPE_ALL_VALUES
     * @see BinaryTreeNode#INSERT_TYPE_SELECTIVE
     * @param type New mode.
     */
    public void setInsertionMode(int type){
        if((type>=BinaryTreeNode.INSERT_TYPE_SELECTIVE)&&(type<=BinaryTreeNode.INSERT_TYPE_ALL)){
            insert_mode = type;
        }
    }
    /**
     * Changes current deletion mode.
     * @see #DELETE_ALL
     * @see #DELETE_NODE
     * @param ALL Whether deletion should be performed on all nodes or single node.
     */
    public void setDeletionMode(boolean ALL){
        delete_mode = ALL; 
    }
    /**
     * Changes current node.
     * @param node new current node.
     */
    public void setCurrentNode(BinaryTreeNode<T> node){
        if(root!=null)
            curr_node = root.findID(node.getNodeID());    
    }
    /**
     * Changes current node.
     * @return current node.
     */
    public BinaryTreeNode<T> getCurrentNode(){
        return curr_node;
    }
    
    
    /**
     * Returns class type.
     * @return class type.
     */
    public Class getClassType(){
        return classType;
    }
    /**
     * Returns tree ID.
     * @return tree ID.
     */
    public String getTreeID(){
        return treeID;
    }
    /**
     * Searches for first occurrence of value in tree.
     * @param value Value to search for.
     * @return First node with
     */
    public BinaryTreeNode<T> search(T value){
        //System.out.println("search() Searching value"+value);
        BinaryTreeNode nod = root.findValue(value);
        //System.out.println("search() found node"+nod);
        return nod;
    }
    /**
     * Searches for first occurrence of value in subtree made by current node.
     * @param value Value to search for.
     * @return First node with
     */
    public BinaryTreeNode<T> searchFrom(T value){
        if(curr_node!=null){
            return curr_node.findValue(value);       
        }else{
            return search(value);
        }
    }
    /**
     * Adds given value to the tree.
     * @param value Value to add.
     */    
    public void insert(T value){
        root.insert(value,insert_mode);
        //System.out.println("new value "+value);
        
    }
    /**
     * Adds given value to the the subtree made by current node.
     * @param value Value to add.
     */    
    public void insertFrom(T value){
        if(curr_node!=null){
            curr_node.insert(value,insert_mode);       
        }else{
            insert(value);
        }
    }
    /**
     * Deletes first occurrence of value.
     * @param value value to delete.
     * @return True if deletion was successful, false otherwise.
     */
    public boolean delete(T value){
        //System.out.println("delete() Deleting value"+value);
        BinaryTreeNode nod = search(value);
        //System.out.println("delete() Deleting node"+nod);
        if(nod!=null){
            return delete(nod);
        }else{
            return false;
        }
    }
    /**
     * Deletes node from the tree.
     * @param node node to delete.
     * @return True if deletion was successful, false otherwise.
     */
    public boolean delete(BinaryTreeNode node){        
        if(root!=null){
            if(node!=null){
                node = root.findID(node.getNodeID());
                if(node!=null){
                    boolean is_root = (node == this.root);
                    BinaryTreeNode<T> node_to_delete = root.findID(node.getNodeID());
                    if(node_to_delete!=null){
                        BinaryTreeNode<T> new_node =  root.deleteNode(node_to_delete);
                        if(is_root){
                            root = new_node;
                        }
                        return true;
                    }else{
                        return false;
                    }
                }else
                    return false;
            }else{
                return false;
            }
        }else{           
            return false;
        }
    }
    /**
     * Deletes current node/subtree from tree based on {@link #curr_node} and {@link #delete_mode}.
     * @return True if deletion was successful, false otherwise.
     */
    public boolean delete(){
        boolean rtrn = false;
        if(curr_node!=null){
            if(delete_mode==DELETE_ALL){
                curr_node.deleteAll();
                rtrn = true;
            }else{
                boolean is_root = (curr_node == this.root);
                BinaryTreeNode<T> new_node = curr_node.deleteNode(curr_node);
                //System.out.println("deleted node"+curr_node);
                curr_node = new_node;
                if(is_root){
                    root = new_node;
                    //System.out.println("new root node "+new_node);
                }
                rtrn = true;
            }
        }else{
            if(delete_mode==DELETE_ALL){
                root.deleteAll();
                rtrn = true;          
            }
        }
        return rtrn;
    }
    /**
     * Class used for {@link #makeTextTree(tree.BinaryTreeNode, int, java.util.List)}.
     * Is used to determine amount of spaces between depth levels.
     */
    private static class spaces{
        /**
         * Depth level of spaces.
         */
        int level;
        /**
         * Amount of spaces.
         */
        int spaces;
        /**
         * Should connecting line be drawn or not.
         */
        boolean draw;
        /**
         * default constructor
         * @param level depth level.
         * @param spaces amount of spaces.
         * @param draw  Should connecting line be drawn or not.
         */
        public spaces(int level, int spaces, boolean draw) {
            this.level = level;
            this.spaces = spaces;
            this.draw = draw;
        }
        @Override
        public String toString() {
            return "["+level+"]["+spaces+"]["+draw+"]";
        }
        
    }
    /**
     * Returns JTree object with this binary tree.
     * @return JTree representing this tree.
     */
    public JTree makeTree(){
        JTree rtrn = new JTree(this);
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Icon ic = new ImageIcon(image);
        rtrn.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer d = new DefaultTreeCellRenderer();
        d.setLeafIcon(ic);
        d.setClosedIcon(ic);
        d.setOpenIcon(ic);
        for(int i =0; i< rtrn.getRowCount();i++)
            rtrn.expandRow(i);
        rtrn.setCellRenderer(d);   
        rtrn.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                //System.out.println(".valueChanged() changing");
                Object t = rtrn.getLastSelectedPathComponent();
                if(t instanceof BinaryTreeNode){
                    //System.out.println(".valueChanged() new current node+"+t);
                    try{
                        curr_node = (BinaryTreeNode<T>) t;
                        /*counter++;
                        System.out.println(".valueChanged() counter"+counter); 
                        if(counter == 5){
                            delete_mode=DELETE_NODE;
                            delete();
                            System.out.println(draw(false));
                            rtrn.setModel(new DefaultTreeModel(root));
                            for(int i =0; i< rtrn.getRowCount();i++)
                                rtrn.expandRow(i);
                            counter = 0;
                        }*/
                    }catch(ClassCastException ex){}
                }
                
            }
        });
        return rtrn;
    }
    
    
    /**
     * Makes lane for {@link #makeTextTree(tree.BinaryTreeNode, int, java.util.List)}.
     * @param curr Current spaces.
     * @param open_pos Spaces list.
     * @param lesser Is currently drawn lesser or bigger node
     * @return String containing line.
     */
    private String makeLine(spaces curr,List<spaces> open_pos,boolean lesser){
        String rtrn="";
        for(int i=0 ; i<open_pos.size();i++){
            spaces s = open_pos.get(i);
            //System.out.println("tree.BinaryTree.makeLine() s"+s);
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
    
    
    /**
     * Makes text tree.
     * @param node Starting node.
     * @param curr_level Level of the node.
     * @param open_pos Spaces list.
     * @return String containing tree from given node.
     */
    private String makeTextTree(BinaryTreeNode<T> node,int curr_level,List<spaces> open_pos){
        if(node!=null){
            String rtrn = node.toString();
            spaces curr = new spaces(curr_level, rtrn.length(),node.hasBigger()&&node.hasLesser());
            open_pos.add(curr);
            //System.out.println("tree.BinaryTree.makeTextTree() [1]adding spaces "+curr );
            if(node.hasSubtrees()){     
                //curr.spaces++;
                rtrn+=((node.hasBigger())?CHAR_LEFT_DOWN_HEAVY:CHAR_LEFT_DOWN)+"\n";  // ┒
                if(node.hasLesser()){
                    rtrn+=makeLine(curr,open_pos,true);
                    rtrn+=makeTextTree(node.getLesser(),curr_level+1,open_pos);
                    curr.draw=false;
                    if(!node.hasBigger()){     
                        //System.out.println("tree.BinaryTree.makeTextTree() removing"+curr);
                        open_pos.remove(curr);
                    }else{
                        rtrn+="\n";
                    }
                }
                //System.out.println(rtrn);
                if(node.hasBigger()){
                    rtrn+=makeLine(curr,open_pos,false);

                    rtrn+=makeTextTree(node.getBigger(),curr_level+1,open_pos.subList(0, curr_level+1)); 
                    //System.out.println("tree.BinaryTree.makeTextTree() removing"+curr);
                }
                //System.out.println("tree.BinaryTree.makeLine()rtrn:[\n"+rtrn+"\n]");
            }    
            //System.out.println("");
            //System.out.println("");
            return rtrn;
        }else{
            return "null";
        }
    }
    
    /**
     * Returns string that represents this tree.
     * @return String that represents this tree.
     */
    public String draw(){       
        return draw(true);
    }
    private static char CHAR_LEFT_DOWN;
    private static char CHAR_LEFT_DOWN_HEAVY;
    private static char CHAR_SPACE;
    private static char CHAR_SPACE_HEAVY;
    private static char CHAR_UP_DOWN;
    private static char CHAR_UP_DOWN_RIGHT;
    private static char CHAR_UP_RIGHT;
    private static char CHAR_UP_RIGHT_HEAVY;
    
    /**
     * Returns string that represents this tree.
     * @param simple Is tree should be in UTF-7 standard. If false then Unicode charset will be used.
     * @return String that represents this tree.
     */
    public String draw(boolean simple){     
        if(simple){
            CHAR_LEFT_DOWN=(char)0x2510;    
            CHAR_LEFT_DOWN_HEAVY=CHAR_LEFT_DOWN;
            CHAR_SPACE=' ';
            CHAR_SPACE_HEAVY=CHAR_SPACE;
            CHAR_UP_DOWN='|';
            CHAR_UP_DOWN_RIGHT = (char)0x251C;
            CHAR_UP_RIGHT= (char)0x2514;
            CHAR_UP_RIGHT_HEAVY = CHAR_UP_RIGHT;
        }else{
            CHAR_LEFT_DOWN=(char)0x2510;
            CHAR_LEFT_DOWN_HEAVY=(char)0x2513;
            CHAR_SPACE=(char)0x00A0;
            CHAR_SPACE_HEAVY=(char)0x2003;
            CHAR_UP_DOWN=(char)0x2503;
            CHAR_UP_DOWN_RIGHT = (char)0x2520;
            CHAR_UP_RIGHT= (char)0x2514;
            CHAR_UP_RIGHT_HEAVY = (char)0x2517;
        }
        return makeTextTree(root,0,new ArrayList<spaces>());
    }
    /**
     * Saves given tree to \\tree folder as [treeID].sav file.
     * @param tree Tree to save.
     * @return Whether it was successful or not.
     */
    static public boolean saveTree(BinaryTree tree){
        boolean rtrn = true; 
        File fil = new File("\\trees");
        if(!fil.exists()){
            rtrn = fil.mkdir();
        }
        if(rtrn){
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream("\\trees\\"+tree.getTreeID()+".sav");   
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(tree);     
                rtrn = true;
            } catch (FileNotFoundException ex) {
                System.err.println("Could not find file.");     
            } catch (IOException ex) {
                System.err.println("Could not save tree to file.");
            } finally {
                try {
                    if(fos!=null)
                        fos.close();
                } catch (IOException ex) {
                    System.err.println("Could not close save file.");
                }
            }
        }else{
            System.err.println("Could not create folder to store trees.");
        }
        return rtrn;
    }
    /**
     * Loads given tree from \\tree folder.
     * @param treeID ID of a tree to load. Can be filename
     * @return Whether it was successful or not.
     */
    static public BinaryTree loadTree(String treeID){
        BinaryTree Tree = null;
        boolean rtrn = false;
        FileInputStream fos = null;     
        try {
            fos = new FileInputStream("\\trees\\"+treeID+(treeID.contains(".sav")?"":".sav"));          
            ObjectInputStream oos = new ObjectInputStream(fos);
            Object o = oos.readObject();
            if(o instanceof BinaryTree){
                Tree = (BinaryTree) o;
                rtrn = true;
            }    
        } catch (FileNotFoundException ex) {
            System.err.println("Could not find file.");     
        } catch (IOException ex) {
            System.err.println("Could not load tree file.");
        } catch (ClassNotFoundException ex) {
            System.err.println("Wrong class");
        } finally {
            try {
                if(fos!=null)
                    fos.close();
            } catch (IOException ex) {
                System.err.println("Could not save file.");
            }
        }
        return Tree;
    }
    
    //#########################Constructors#################################
    /**
     * Default constructor. Sets first node value to null.
     * @param t Class type. Should be the same as declared type of tree since it might provide errors.
     */
    public BinaryTree(Class t) {   
        this(null,t);
    }
    /**
     * Default constructor. Sets first node value to null.
     */
    public BinaryTree() {      
        this(null,Object.class);
    }
    /**
     * Constructor. Sets with given object first value.
     * @param value Value to set.
     */
    public BinaryTree(T value) {   
        this(value,Object.class);
    }
    
    /**
     * Constructor. Sets with given object first value. Provides data structure security.
     * @param value Value to set.
     * @param t Class type. Should be the same as declared type of tree since it might provide errors.
     */
    public BinaryTree(T value,Class t) {   
        if(t==null){           
            t= Object.class;
        }
        treeID = hashCode()+"###"+t.getSimpleName();
        //System.out.println("tree.BinaryTree.<init>() treeID:"+treeID);
        classType = t;
        root = new BinaryTreeNode<T>(value,null);
        curr_node = root;
        delete_mode = DELETE_NODE;
        insert_mode = BinaryTreeNode.INSERT_TYPE_SELECTIVE;
    }
    //#########################Overriden Methods################################

    @Override
    public String toString() {
        return ""+treeID;
    }
    
    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if(parent instanceof BinaryTree){
            return ((BinaryTree) parent).getRoot();
        }else{    
            if(parent instanceof BinaryTreeNode){
                return ((BinaryTreeNode) parent).getChildAt(index);
            }else{
                return null;
            }    
        }
    }

    @Override
    public int getChildCount(Object parent) {
        if(parent instanceof BinaryTree){
            return (root==null)?0:1;
        }else{   
            if(parent instanceof BinaryTreeNode){
                return ((BinaryTreeNode) parent).getChildCount();
            }else{
                return -1;
            }  
        }
    }

    @Override
    public boolean isLeaf(Object node) {
       if(node instanceof BinaryTreeNode){
           return ((BinaryTreeNode) node).isLeaf();
       }else{
           return true;
       }  
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
       if((parent instanceof BinaryTreeNode)&&(child instanceof TreeNode)){
           return ((BinaryTreeNode) parent).getIndex((TreeNode) child);
       }else{
           return -1;
       }  
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        
    }
    
}

