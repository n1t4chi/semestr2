package tree;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Binary tree node class.
 * @param <T> Data type that is stored inside.
 * @author n1t4chi
*/
public final class BinaryTreeNode<T> extends DefaultMutableTreeNode implements Serializable{


    //#########################Static Context###############################
    /**
     * Constant for bigger value when using {@link #compare(java.lang.Object)}.
     * It is returned only when:<br>
     * - classes are derived from {@link Comparable} and value.compareTo(objectToCompare){@literal >0}<br>
     * - or when Type is derived from {@link Number} and value.doubleValue(){@literal >}objectToCompare.doubleValue() ]<br>
     * - or when value.hashCode() {@literal >} objectToCompare.hashCode()
     */
    public static final int VALUE_BIGGER = 10;
    /**
     * Constant for lesser value when using {@link #compare(java.lang.Object)}.
     * It is returned only when:<br>
     * - classes are derived from {@link Comparable} and value.compareTo(objectToCompare){@literal <0}<br>
     * - or when Type is derived from {@link Number} and value.doubleValue(){@literal <}objectToCompare.doubleValue()<br>
     * - or when value.hashCode() {@literal <} objectToCompare.hashCode();     
     */
    public static final int VALUE_LESSER = 5;
    /**
     * Constant for same value when using {@link #compare(java.lang.Object)}. 
     * It's returned when:<br>
     * - value.equals(object)==true and (value == objectToCompare)==false [returned value is {@link #VALUE_SAME_OBJECT}]<br>
     * - or when Type is derived from {@link Comparable} and value.compareTo(objectToCompare) returns 0<br>
     * - or when Type is derived from {@link Number} and absolute difference is lesser than {@link #margin}<br>
     * - or when value.hashCode() == objectToCompare.hashCode()<br>
     * For same value look {@link #VALUE_SAME_VALUE}, you can catch both values with {@literal <=}VALUE_SAME_VALUE
     */
    public static final int VALUE_SAME_VALUE = 1;

    /**
     * Constant for same object when using {@link #compare(java.lang.Object)}.
     * It's returned when value==objectToCompare. 
     * For same value look {@link #VALUE_SAME_VALUE}, you can catch both values with {@literal <=}VALUE_SAME_VALUE
     */
    public static final int VALUE_SAME_OBJECT = 0; 

   /* /**
     * Constant when {@link #compare(java.lang.Object)} cannot determine whether objects are the same of lesser/bigger.
     * Such cases are when:<br>
     * - value.equals(objectToCompare)==false and (value == objectToCompare)==false <br>
     * - and either: <br>
     *   + one of them is of type that is not derived from {@link Comparable} interface.<br>
     *   + or both are of different types that are not comparable with each other.<br>
     * You can catch both {@link #VALUE_UNDEFINED} and {@link #VALUE_NULL} with {@literal >=} VALUE_UNDEFINED
     */
    //public static final int VALUE_UNDEFINED = 666;
   //  * You can catch both {@link #VALUE_UNDEFINED} and {@link #VALUE_NULL} with {@literal >=} VALUE_UNDEFINED
    
    
    /**
     * Constant for case when one of pointers is null while using {@link #compare(java.lang.Object)}.
     */
    public static final int VALUE_NULL = 1337;
   
    /**
     * Constant for {@link #insert(java.lang.Object, int) } method.
     * Adds everything to a tree.<br>
     * Objects will be added do bigger subtree only when value.compareTo(objectToAdd) result is {@link #VALUE_LESSER}.
     * Otherwise they are placed onto lesser subtree.<br>
     * Constant value is bigger than {@link #INSERT_TYPE_ALL_VALUES}
     */
    public static final int INSERT_TYPE_ALL = 3;
    /**
     * Constant for {@link #insert(java.lang.Object, int) } method.
     * Forces to add new node even if value objects are the same. <br>
     * Null pointers are not added.<br>
     * Objects will be added do bigger subtree only when value.compareTo(objectToAdd) result is {@link #VALUE_LESSER}.
     * Otherwise they are placed onto lesser subtree.<br>
     * If current node have null value it will be overwritten.<br>
     * Constant value is bigger than {@link #INSERT_TYPE_ALL_OBJECTS}
     */
    public static final int INSERT_TYPE_ALL_OBJECTS = 2;
    /**
     * Constant for {@link #insert(java.lang.Object, int) } method.
     * Forces to add new node even if values are the same. Stops when objects are the same.<br>
     * Increments count of current node when object is not added to one of the subtrees.<br>
     * Objects will be added do bigger subtree only when value.compareTo(objectToAdd) result is {@link #VALUE_LESSER}.
     * Otherwise they are placed onto less subtree.<br>
     * Null pointers are not added.<br>
     * If current node have null value it will be overwritten.<br>
     * Constant value is bigger than {@link #INSERT_TYPE_SELECTIVE}
     */
    public static final int INSERT_TYPE_ALL_VALUES = 1;
    /**
     * Constant for {@link #insert(java.lang.Object, int) } method.
     * Adds new node only when values are bigger/lesser.<br>
     * Increments count of current node when object is not added to one of the subtrees.<br>
     * Null pointers are not added.<br>
     * If current node have null value it will be overwritten.<br>
     */
    public static final int INSERT_TYPE_SELECTIVE = 0;

    //#########################Fields#######################################
    /**
     * Counter for same value/objects.
     */
    private long count;
    /**
     * Value that is stored by this node.
     */
    private T value;
    /**
     * Parent of current node.
     */
    private BinaryTreeNode<T> parent;
    /**
     * Subtree that contains lesser values.
     */
    private BinaryTreeNode<T> lesser;
    /**
     * Subtree that contains bigger [and same in some cases] values.
     */
    private BinaryTreeNode<T>  bigger;

    /**
     * Margin error when comparing values. Usable only when value type is derived from {@link Number} class.
     */
    private double margin;
    /**
     * ID of this node
     */
    final private String nodeID;




    //#########################Methods######################################
    
    /**
     * Returns node ID.
     * @return node ID.
     */
    public String getNodeID(){
        return nodeID;
    }

    /**
     * Changes margin error for comparison purposes. If given value is negative then zero will be placed instead.
     * @param margin New value.
     */
    public void setMargin(double margin){
        this.margin=(margin<0)?0:margin;
    }     
    /**
     * Returns current counter value of this node.
     * @return Current counter value of this node.
     */
    public long getCount(){
        return this.count;
    }
    /**
     * Returns value that is stored inside this node.
     * @return Value that is stored inside.
     */
    public T getValue(){
        return this.value;
    }
    /**
     * Changes value that is stored inside this node.
     * @param value New value.
     */
    public void setValue(T value){
        this.value=value;
        this.userObject = value;
    }     
    /**
     * Returns parent subtree node.
     * @return Parent node.
     */
    public BinaryTreeNode<T> getParent(){
        return parent;
    }
    /**
     * Changes parent of this node.
     * @param parent New parent.
     */
    public void setParent( BinaryTreeNode<T>  parent){
        this.parent = parent;
    }
    /**
     * Returns lesser subtree node.
     * @return Lesser subtree node.
     */
    public BinaryTreeNode<T> getLesser(){
        return lesser;
    }
    /**
     * Returns bigger subtree node.
     * @return Bigger subtree node.
     */
    public BinaryTreeNode<T> getBigger(){
        return bigger;
    }
    /**
     * Changes lesser subtree node.
     * @param lesser New node.
     */
    public void setLesser( BinaryTreeNode<T>  lesser){
        this.lesser = lesser;
        if(lesser!=null)
            lesser.setParent(this);
    }
    /**
     * Changes bigger subtree node.
     * @param bigger New node.
     */
    public void setBigger( BinaryTreeNode<T>  bigger){
        this.bigger = bigger;
        if(bigger!=null)
            bigger.setParent(this);
    }
    
    
    /**
     * Returns whether this node has lesser subtree.
     * @return True if this node has lesser subtree, false otherwise.
     */
    public boolean hasLesser(){
        return (lesser!=null);
    }
    /**
     * Returns whether this node has bigger subtree.
     * @return True if this node has bigger subtree, false otherwise.
     */
    public boolean hasBigger(){
        return (bigger!=null);
    }
    /**
     * Returns whether this node has subtrees.
     * @return True if this node has at least one subtree, false otherwise.
     */
    public boolean hasSubtrees(){
        return hasLesser()||hasBigger();
    }
    
    
    
    
    
    /**
     * Finds node by its ID.
     * @param ID ID of a node to find;
     * @return Node with given value. Might be null if objects is not in the tree.
     */
    public BinaryTreeNode<T>  findID(String ID){
        if(nodeID.equalsIgnoreCase(ID)){
            return this;
        }else{
            BinaryTreeNode rtrn = null;
            if(hasLesser())
                rtrn = lesser.findID(ID);
            if((rtrn==null)&&(hasBigger()))
                rtrn = bigger.findID(ID);
            return rtrn;
        }
    }
    
    /**
     * Finds first node that contains value of given object.
     * Returned node fulfils these conditions: <br>
     * - {@link #compare(java.lang.Object) } returns {@link #VALUE_SAME_OBJECT} or {@link #VALUE_SAME_VALUE}   <br>
     * - both values of the node and object are null<br>
     * @param value Value to find;
     * @return Node with given value. Might be null if objects is not in the tree.
     */
    public BinaryTreeNode<T>  findValue(T value){    
        //System.out.println("findValue() Searching value"+value);
        int compare = compare(value);
        //System.out.println("findValue() Comparison:"+compare);
        if(compare<=VALUE_SAME_VALUE){
            return this;
        }else{
            if(compare!=VALUE_LESSER){
                if(hasLesser())
                    return lesser.findValue(value);
                else{
                    return null;
                }
            }else{
                if(hasBigger()){
                    return bigger.findValue(value);
                }else{
                    return null;
                }
            }        
        }          
    } 
    
    
    /**
     * Deletes whole tree
     */
    public void deleteAll(){
        if(hasLesser()){
            lesser.deleteAll();
        }
        if(hasBigger()){
            bigger.deleteAll();
        }
        bigger = null;
        lesser = null;
        value = null;
        if(parent!=null){
            if(parent.getLesser()==this){
                parent.setLesser(null);
            }else{
                if(parent.getBigger()==this){
                    parent.setLesser(null);
                }
            }
        }
        parent = null;
        count=1;
    }
    

    
    /**
     * Deletes given node. Works if node was taken directly from this tree.
     * @param node node to delete.
     * @return New address of root if node to delete is root node. Null otherwise.
     */
    public BinaryTreeNode<T> deleteNode(BinaryTreeNode<T> node){
        BinaryTreeNode<T> rtrn = null;
        if(node!=null){
            BinaryTreeNode<T> parent = node.getParent();
            BinaryTreeNode<T> new_node;
            if(node.hasLesser()){
                new_node = node.getLesser(); //new node to set
            }else{
                new_node = node.getBigger();
            }
            if(node.hasBigger()&&node.hasLesser()){ //case when there are 2 children
                BinaryTreeNode<T> prev = null; //new node previous parent
                while(new_node.hasBigger()){
                    prev = new_node;
                    new_node = new_node.getBigger();
                }
                if(prev!=null){ //switching subtrees. 
                    prev.setBigger(new_node.getLesser());    
                 //   System.out.println(prev+" has new bigger:"+new_node.getLesser());
                }    

                if(new_node!=node.getBigger())
                    new_node.setBigger(node.getBigger());           
                if(new_node!=node.getLesser())
                    new_node.setLesser(node.getLesser()); 
            }else{
            }
            //System.out.println(" new node:"+new_node);



            if(parent!=null){
                if(parent.getBigger()==node){
                   // System.out.println(parent +" has new bigger:"+new_node);
                    parent.setBigger(new_node);
                }else{
                   // System.out.println(parent +" has new lesser:"+new_node);
                    parent.setLesser(new_node);
                }    
            }else{
                new_node.setParent(null);
                rtrn = new_node;
            }


            node.setParent(null);
            node.setLesser(null);
            node.setBigger(null);  
        }
        return rtrn;
        
    }
    
    
    /**
     * Inserts new value to one of subtrees based on {@link #compare(java.lang.Object) } result.
     * It is {@link #INSERT_TYPE_SELECTIVE} version of {@link #insert(java.lang.Object, int)}.
     * @param value Value to be added.
     */
    public void insert(T value){
        insert(value, INSERT_TYPE_SELECTIVE);
    }
    /**
     * Inserts new value to one of subtrees based on {@link #compare(java.lang.Object) } result.
     * @param value Value to be added.
     * @param type Type of adding see: {@link #INSERT_TYPE_SELECTIVE} {@link #INSERT_TYPE_ALL_VALUES} {@link #INSERT_TYPE_ALL_OBJECTS} {@link #INSERT_TYPE_ALL}
     */
    public void insert(T value,int type){     
        if(type == INSERT_TYPE_ALL){
            if(compare(value)==VALUE_LESSER){
                addToBigger(value, type);
            }else{
                addToLesser(value, type);
            }
        }else{         
            if(this.value == null){
                this.value = value;
            }else{
                int compare =  compare(value);
                if(compare != VALUE_NULL){
                    if(
                            ((compare == VALUE_SAME_OBJECT )&&(type < INSERT_TYPE_ALL_OBJECTS ))||                   
                            ((compare == VALUE_SAME_VALUE )&&(type < INSERT_TYPE_ALL_VALUES ))
                        ){
                        count++;               
                    }else{
                        if(compare == VALUE_LESSER){
                            addToBigger(value, type);
                        }else{              
                            addToLesser(value, type);                   
                        }
                    }
                }
            }
        }
    }
    /**
     * Adds value to specified subtree. If subtree is null, then new one is created.
     * @param value Value to be added.
     * @param lesser If true then new value will be added to lesser subtree. otherwise to bigger.
     * @param type Type of adding see: {@link #INSERT_TYPE_SELECTIVE} {@link #INSERT_TYPE_ALL_VALUES} {@link #INSERT_TYPE_ALL_OBJECTS} {@link #INSERT_TYPE_ALL}
     */
    public void addTo(T value, boolean lesser,int type){
        if(lesser){
            if(this.lesser==null){
                this.lesser = new BinaryTreeNode<T>(value, margin,this);
            }else{
                this.lesser.insert(value, type);
            }
        }else{
            if(this.bigger==null){
                this.bigger = new BinaryTreeNode<T>(value, margin,this);
            }else{
                this.bigger.insert(value, type);
            }
        }         
    }
    
    /**
     * Adds value to bigger subtree.
     * @param value Value to be added.
     * @param type Type of adding see: {@link #INSERT_TYPE_SELECTIVE} {@link #INSERT_TYPE_ALL_VALUES} {@link #INSERT_TYPE_ALL_OBJECTS} {@link #INSERT_TYPE_ALL}
     */
    public void addToBigger(T value,int type){
        addTo(value,false,type);
    }
    /**
     * Adds value to lesser subtree.
     * @param value Value to be added.
    * @param type Type of adding see: {@link #INSERT_TYPE_SELECTIVE} {@link #INSERT_TYPE_ALL_VALUES} {@link #INSERT_TYPE_ALL_OBJECTS} {@link #INSERT_TYPE_ALL}
      */
    public void addToLesser(T value,int type){
        addTo(value,true,type);
    }
    //* - {@link #VALUE_UNDEFINED} when comparison cannot be done <br>
    /**
     * Compares {@link #value} thats stored inside with given object.
     * @param object Object to compare to.
     * @return Returns:<br>
     * - {@link #VALUE_NULL} when one of the pointers is null  <br>
     * - {@link #VALUE_SAME_OBJECT} when they are the same object <br>
     * - {@link #VALUE_SAME_VALUE} when they have same value or lie within {@link #margin} <br>
     * - {@link #VALUE_LESSER} when stored value is lesser than value of given object  <br>
     * - {@link #VALUE_BIGGER} when stored value is bigger than value of given object  <br>
     */
    public int compare(T object){
        //System.out.println("tree.BinaryTreeNode.compare() comparing: "+object+" with "+this.value);
        if((object==null)||(value==null)){
            return VALUE_NULL;
        }else{
            if(value == object){
                return VALUE_SAME_OBJECT;
            }else{
                if(value.equals(object)){
                    return VALUE_SAME_VALUE;
                }else{     
                    double t;
                    if((value instanceof Number)&&(object instanceof Number)){
                        double val = ((Number)value).doubleValue();
                        double obj = ((Number)object).doubleValue();
                        //System.out.println("tree.BinaryTreeNode.compare() val"+val+" obj"+obj+" |vall-obj|"+Math.abs(val - obj)+"<"+margin);    
                        if((val == obj)||( Math.abs(val - obj)<margin)){
                            return VALUE_SAME_VALUE;
                        }else{
                            t=val-obj;
                        }
                    }else{  
                        int rtrn;
                        if((value instanceof Comparable)&&(object instanceof Comparable)){
                            try{
                                //System.out.println("tree.BinaryTreeNode.compare() ");
                                if((value instanceof String)&&(object instanceof String)){        
                                    t=((String)value).compareToIgnoreCase((String)object);
                                }else       
                                    t=((Comparable )value).compareTo(object);
                            }catch(ClassCastException ex){
                                t = value.hashCode() - object.hashCode();
                            }
                        }else{
                            t = value.hashCode() - object.hashCode();
                        }
                    }       
                    //System.out.println("tree.BinaryTreeNode.compare() t: "+t);           
                    if(t==0){
                        return VALUE_SAME_VALUE;
                    }else{
                        if(t<0){
                            return VALUE_LESSER;
                        }else{
                            return VALUE_BIGGER;
                        }
                    }          
                }
            }        
        }
    }


    //#########################Constructors#################################
    /**
     * Default Constructor, initiates only value, subtrees will be null pointers.
     * @param value Value to store inside this node.
     * @param parent Parent of this node.
     * @param count Starting counter value.
     */
    public BinaryTreeNode(T value,BinaryTreeNode<T>  parent,long count){
        this(value,0.0,parent,count);
    }
    /**
     * Default Constructor, initiates only value, subtrees will be null pointers.
     * @param value Value to store inside this node.
     * @param parent Parent of this node.
     */
    public BinaryTreeNode(T value,BinaryTreeNode<T>  parent){
        this(value,0.0,parent);
    }
    /**
     * Constructor initiates value of this node. Also sets error margin when comparing values.
     * @param value Value to store inside this node.
     * @param margin Error margin when comparing values. It wont have an effect if value type is not derived from {@link Number}. Will be changed to 0 when given negative number.
     * @param parent Parent of this node.
     */
    public BinaryTreeNode(T value, double margin,BinaryTreeNode<T>  parent){
        this(value,null,null,margin,parent);
    }
    /**
     * Constructor initiates value of this node. Also sets error margin when comparing values.
     * @param value Value to store inside this node.
     * @param margin Error margin when comparing values. It wont have an effect if value type is not derived from {@link Number}. Will be changed to 0 when given negative number.
     * @param parent Parent of this node.
     * @param count Starting counter value.
     */
    public BinaryTreeNode(T value, double margin,BinaryTreeNode<T>  parent,long count){
        this(value,null,null,margin,parent,count);
    }
    /**
     * Constructor initiates value of this node and subtrees with given objects.
     * @param value Value to store inside this node.
     * @param bigger Bigger subtree pointer.
     * @param lesser Lesser subtree pointer.
     * @param parent Parent of this node.
     */
    public BinaryTreeNode(T value, BinaryTreeNode<T>  bigger, BinaryTreeNode<T>  lesser,BinaryTreeNode<T>  parent){
        this(value,bigger,lesser,0.0,parent);
    }
    /**
     * Constructor initiates value of this node and subtrees with given objects.
     * @param value Value to store inside this node.
     * @param bigger Bigger subtree pointer.
     * @param lesser Lesser subtree pointer.
     * @param parent Parent of this node.
     * @param count Starting counter value.
     */
    public BinaryTreeNode(T value, BinaryTreeNode<T>  bigger, BinaryTreeNode<T>  lesser,BinaryTreeNode<T>  parent,long count){
        this(value,bigger,lesser,0.0,parent,count);
    }
    /**
     * Constructor initiates value of this node and subtrees with given objects. Also sets error margin when comparing values.
     * @param value Value to store inside this node.
     * @param bigger Bigger subtree pointer.
     * @param lesser Lesser subtree pointer.
     * @param margin Error margin when comparing values. It wont have an effect if value type is not derived from {@link Number}. Will be changed to 0 when given negative number.
     * @param parent Parent of this node.
     * 
     */
    public BinaryTreeNode(T value, BinaryTreeNode<T>  bigger, BinaryTreeNode<T>  lesser, double margin,BinaryTreeNode<T>  parent){
        this(value,bigger,lesser,margin,parent,1);
    }
    /**
     * Constructor initiates value of this node and subtrees with given objects. Also sets error margin when comparing values.
     * @param value Value to store inside this node.
     * @param bigger Bigger subtree pointer.
     * @param lesser Lesser subtree pointer.
     * @param margin Error margin when comparing values. It wont have an effect if value type is not derived from {@link Number}. Will be changed to 0 when given negative number.
     * @param parent Parent of this node.
     * @param count Starting counter value.
     * 
     */
    public BinaryTreeNode(T value, BinaryTreeNode<T>  bigger, BinaryTreeNode<T>  lesser, double margin,BinaryTreeNode<T>  parent,long count){
        nodeID = "###"+hashCode();
        setParent(parent);
        setValue(value);
        setLesser(bigger);
        setBigger(lesser);      
        setMargin(margin);
        this.count = count;
    }
    
    
    //########################0VERRIDDEN METHODS################################
    /**
     * Child index for lesser subtree, or when there is no lesser subtree for bigger subtree.
     */
    public static final int INDEX_LESSER = 0;
    /**
     * Child index for bigger subtree.
     */
    public static final int INDEX_BIGGER = 1;
    @Override
    public TreeNode getChildAt(int childIndex) {
        if(childIndex == INDEX_LESSER){
            if(!hasLesser()){
                return bigger;
            }else{
                return lesser;
            }
        }else{
            if(childIndex == INDEX_BIGGER){
                  return bigger;
            }else{
                return null;
            }  
        }
    }

    @Override
    public int getChildCount() {
        int rtrn = 0;
        if(hasBigger()) rtrn++;
        if(hasLesser()) rtrn++;
        return rtrn;
    }

    @Override
    public int getIndex(TreeNode node) {
        if((hasLesser())&&(lesser.equals(node))){
            return INDEX_LESSER ;
        }else{
            if((hasBigger())&&(bigger).equals(node)){
                if(!hasLesser()){
                    return INDEX_LESSER;
                }else{
                    return INDEX_BIGGER;
                }
            }else{
                return -1;
            }          
        }
    }

    @Override
    public boolean getAllowsChildren() {
        return !(hasBigger()&&hasLesser());
    }

    @Override
    public boolean isLeaf() {
        return !hasSubtrees();
    }

    @Override
    public Enumeration children() {
        return new Enumeration<BinaryTreeNode>() {
            boolean lesser_given=!hasLesser();
            boolean bigger_given=!hasBigger();
            @Override
            public boolean hasMoreElements() {
                return !(lesser_given&&bigger_given);
            }

            @Override
            public BinaryTreeNode nextElement(){
                if(!lesser_given){
                    return lesser;
                }else{
                    if(!bigger_given){
                        return bigger;
                    }else{
                        return null;
                    }
                }
            }
        };
        
    }

    @Override
    public void setUserObject(Object userObject) {
        if(userObject!=null){
            if(value instanceof String){
                setValue( (T) this.userObject.toString());
            }else{
                if((value instanceof Integer)){
                    if(userObject instanceof Number){
                        setValue((T) new Integer(((Number) userObject).intValue()));
                    }else{
                        if(userObject instanceof String){
                            try{
                                setValue((T) new Integer(Integer.parseInt((String)userObject)));
                            }catch(NumberFormatException ex){}
                        }
                    }
                }else{             
                    if((value instanceof Double)){
                        if(userObject instanceof Number){
                            setValue((T) new Double(((Number) userObject).intValue()));
                        }else{
                            if(userObject instanceof String){
                                try{
                                    setValue((T) new Double (Double.parseDouble((String)userObject)));
                                }catch(NumberFormatException ex){}
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        String val =""+getValue();
        if(value instanceof Number){
            NumberFormat formatter = new DecimalFormat("0.####");   
            val = formatter.format(value);
        }
        return "["+val+"]"+((getCount()>1)?"["+getCount()+"]":"");
    }

    
}
