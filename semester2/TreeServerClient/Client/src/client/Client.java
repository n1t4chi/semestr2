/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Frame.Window;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import packet.Message;
import static packet.Message.*;
import tree.BinaryTree;
import tree.BinaryTreeNode;

/**
 * Main client class.
 * @author n1t4chi
 */
public class Client extends Window{


    /**
     * Node for testing purposes.
     */
    final static BinaryTreeNode testnode = new BinaryTreeNode("TEST",null);
    
//#########################Static Context#######################################

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Client client = new Client();
                client.setVisible(true);
                client.initConnection(false);
                Timer t = new Timer();
                /*t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        client.sendTestPacket();
                        //client.sendObject(testnode);
                    }
                },10);*/
                /*t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.err.println(If_I_print_that_String_out_will_you_crash.It_would_be_extremely_painful.You_are_a_big_String.For_you.What_is_next_step_of_your_master_plan(Crashing_this_plane));      
                        System.err.println(Crashing_this_plane.length());
                        System.out.println(If_I_print_that_String_out_will_you_crash.It_would_be_extremely_painful.You_are_a_big_String.For_you.What_is_next_step_of_your_master_plan(Crashing_this_plane));      
                        System.err.println("#######################\n#######################\n#######################\n#######################\n#######################\n"
                                + "#######################\n#######################\n#######################\n#######################\n#######################\n#######################\n"
                                + "#######################\n#######################\n#######################\n#######################\n#######################\n"
                                + "#######################\n#######################\n#######################\n#######################\n#######################\n");
                        System.out.println(If_I_print_that_String_out_will_you_crash.It_would_be_extremely_painful.You_are_a_big_String.For_you.What_is_next_step_of_your_master_plan(Crashing_this_plane));      
                        /*System.err.println("#######################\n#######################\n#######################\n#######################\n#######################\n"
                                     + "#######################\n#######################\n#######################\n#######################\n#######################\n#######################\n"
                                     + "#######################\n#######################\n#######################\n#######################\n#######################\n"
                                     + "#######################\n#######################\n#######################\n#######################\n#######################\n");

                         }
                    
                },5000);*/
            }
        });
    }

    
    






//#########################Fields###############################################
    /**
     * icons
     */
    final IconLoader icon;
    /**
     * Client Configuration
     */
    final Configuration config;
    
    /**
     * Binary tree object.
     */
    BinaryTree tree;
    
    /**
     * GUI tree.
     */
    final JTree jTree;
    
    /**
     * Socket.
     */
    Socket socket;
    /**
     * Input stream reader.
     */
    ObjectInputStream ois;
    /**
     * Output stream reader.
     */
    ObjectOutputStream oos;
    /**
     * Client menu. Changes IP.
     */
    final JMenuItem menu_File_Connect;
    /**
     * Client menu. Reconnects
     */
    final JMenuItem menu_File_Reconnect;
    /**
     * Client menu. Closes Connection.
     */
    final JMenuItem menu_File_Disconnect;
    /**
     * Client menu. Changes IP.
     */
    final JCheckBoxMenuItem menu_File_AutoLoad;
    /**
     * Toolbar for client options.
     */
    final JToolBar toolBar;
    /**
     * Button for insert.
     */
    final JButton toolBar_insert;
    /**
     * Button for delete.
     */
    final JButton toolBar_delete;
    /**
     * Button for select.
     */
    final JButton toolBar_select;
    /**
     * Button for new tree.
     */
    final JButton toolBar_new;
    /**
     * Combo box for new tree.
     */
    final JComboBox<String> toolBar_new_selection;
    /**
     * Combo box for new tree.
     */
    final JTextField toolBar_value;
    /**
     * Combo box for new tree.
     */
    final JLabel toolBar_current;




//#########################Methods##############################################
    /**
     * Updates tree.
     * @param tree new Tree.
     */
    private void updateTree(BinaryTree tree){    
        this.tree = tree;
        if(tree!=null){
            jTree.setVisible(true);
            jTree.setModel(tree);
            for(int i =0; i< jTree.getRowCount();i++)
                jTree.expandRow(i);
            updateNode(tree.getCurrentNode());
        }else{
            jTree.setVisible(false);
            updateNode(null);
        }
    }
    /**
     * Updates current node
     * @param node New node.
     */
    private void updateNode(BinaryTreeNode node){    
        if(tree!=null){
            tree.setCurrentNode(node);     
            toolBar_current.setText(""+node);
        }else{
            toolBar_current.setText(""+null);
        }
    }
    /**
     * Executes given message.
     * @param t Message to execute.
     */
    public void execute(Message t){
        if((t!=null)&&(t.isReply())){ 
            switch(t.getType()){
                case Message.COMMAND_TREE_GET:
                        if(t.getTree()==null){
                            System.out.println("No tree was found.");
                        }else{               
                            updateTree(t.getTree());
                            System.out.println("Received tree:"+tree);
                            System.out.println("\n"+tree.draw(false));
                        }    
                    break;
                case Message.COMMAND_INSERT:
                case Message.COMMAND_DELETE:
                        if(tree!=null){
                            if(!tree.getTreeID().equalsIgnoreCase(t.getTreeID())){
                                System.err.println("Invalid tree was returned by the server.");
                            }else{
                                updateTree(t.getTree());
                                //System.err.println(t.getTree().draw(false));
                                System.out.println("\n"+tree.draw(false));
                            }    
                        }
                    break;
                case Message.COMMAND_TREE_SET:
                        if(t.getTree()==null)
                            System.out.println("Server did not respond with new tree.");
                        else{
                            updateTree(t.getTree());
                            System.out.println("Received tree:"+tree);
                            System.out.println("\n"+tree.draw(false));
                        }   
                    break;
                case Message.COMMAND_DRAW:
                        System.out.println("\n"+t.getValue());
                    break;
                case Message.COMMAND_SEARCH:
                        if(tree!=null){
                            if(!tree.getTreeID().equalsIgnoreCase(t.getTreeID())){
                                System.err.println("Invalid tree was returned by the server.");
                                break;
                            }else{
                                BinaryTreeNode node = t.getNode();
                                if(node!=null){
                                    updateNode(node);
                                    System.out.println("Node was found: "+node);
                                }else
                                    System.out.println("No node containig given value was found by server");
                            }
                        }
                    break;
                case Message.COMMAND_TREE_LIST:
                        if(t.getValue() instanceof String[]){
                            String[] o = (String[])t.getValue();       
                            if(o.length>0){
                                System.out.println("List of trees available");
                                for(int i=0; i<o.length;i++){
                                    System.out.println("["+i+"] "+o[i]);
                                }
                            }else{
                                System.out.println("There are no trees on the server.");
                            }
                        }else{
                            System.out.println("Invalid list from the server.");
                        }
                    break;
            } 
        }
    }
    /**
     * Executes given string as a command.
     * @param t Command to execute.
     */
    public void execute(String t){
        //System.out.println("client.Client.execute() 1");
        //System.out.println("client.Client.execute() command:"+t);
        boolean wrong_command = false;
        Message msg = null;
        int command = -1;
        Serializable value = null;
        if(t!=null){
            //System.err.print("client.Client.execute() 0");
            int help =  ( 
                    (t.equalsIgnoreCase("help")||(t.equals("?")))?
                        1 //help or ?
                    :
                        (
                            (t.equalsIgnoreCase("full help")||(t.equals("???")))?
                                2 //full help or ???
                            :
                                0 //everything else
                        )
            );  
            if(  help>0   ){
                //System.err.println("client.Client.execute() 1");
                String text = "Available commands:\n";
                for(int i=0; (i<COMMANDS.length)&&(i<COMMANDS_TOOLTIP_CLIENT.length)&&(i<COMMANDS_TOOLTIP_CLIENT_USAGE.length);i++){
                    text+=(COMMANDS[i]+" "+COMMANDS_TOOLTIP_CLIENT_USAGE[i]+" -  "+COMMANDS_TOOLTIP_CLIENT[i]+"\n");
                }
                //System.err.println("client.Client.execute() 2");
                System.out.print(text);
                if(  help>1   ){
                    text=(
                        "How to read:\n"
                        + "COMMAND [parameter]/[CONSTANT]/[nothing]\n"
                        + " - Command is a type of a command.\n"
                        + " - [parameter] means that value is expected to be given with a command.\n"
                        + " - [nothing] means that no value or constant is required for it to work.\n"
                        + " - [CONSTANT] means that given word in brackets is required.\n"
                        + " - [choice#1]/[choice#2] means that either choice#1 or choice#2 is needed."
                    );
                    System.out.println(text);
                    text=("Examples:\n"
                        + "LIST\n"
                        + "GET 1829164700###Integer\n"
                        + "INSERT 150\n"
                        + "SET INTEGER\n"
                        + "DELETE 16\n"
                        + "DELETE"
                    );
                    System.out.println(text);
                }
                //System.err.println("client.Client.execute()3");
                //System.err.println(text);
                //System.err.println("client.Client.execute()4");
            }else{
                for(int i=0; i<COMMANDS.length;i++){
                    if(
                        (t.length()>=COMMANDS[i].length())
                        &&(
                            t.substring(0, COMMANDS[i].length()).equalsIgnoreCase(COMMANDS[i])
                        )
                    ){
                        command = i;
                        break;
                    }
                }
                if(command>-1){
                    t = t.substring(COMMANDS[command].length());
                    if(t.startsWith(" ")){
                        t=t.substring(1);
                    }
                    //System.out.println("client.Client.execute() cut command:"+t);
                    switch(command){
                        case COMMAND_DELETE: 
                            if((tree!=null)&&(t.equals(""))){
                                if(tree.getCurrentNode()!=null){
                                    value = tree.getCurrentNode();
                                }else{
                                    System.out.println("No current node active.");       
                                    command = -1;
                                }
                                break;
                            }
                        case COMMAND_SEARCH: 
                        case COMMAND_INSERT: 
                                if(tree!=null){
                                    value = t;
                                    if(tree.getClassType().equals(Integer.class)){
                                        try{
                                            value = Integer.parseInt(t);
                                        }catch(NumberFormatException ex){
                                            wrong_command = true;
                                        }
                                    }else{
                                        if(tree.getClassType().equals(Double.class)){
                                            try{
                                                value = Double.parseDouble(t.replaceAll(",", "."));
                                            }catch(NumberFormatException ex){
                                                wrong_command = true;
                                            }
                                        }else{
                                            value = t;
                                        }
                                    }                                       
                                }else{    
                                    System.err.println("Currently there is no active tree. Please load one from the server first.");            
                                    command = -1;
                                }    
                            break;
                        case COMMAND_TREE_LIST:         
                            break;
                        case COMMAND_TREE_GET: 
                                value = t;
                            break;
                        case COMMAND_TREE_SET: 
                                if(t.equalsIgnoreCase("INTEGER")){
                                    value = Integer.class;
                                }else{
                                    if(t.equalsIgnoreCase("DOUBLE")){
                                        value = Double.class;
                                    }else{
                                        if(t.equalsIgnoreCase("STRING")){
                                            value = String.class;
                                        }else{
                                            if(t.equalsIgnoreCase("")){
                                                value = Object.class;
                                            }else{
                                                System.err.println("Wrong type. Do not give parameter if you want to create general purpose tree.");
                                            }
                                        }       
                                    }
                                }
                            break;
                        case COMMAND_DRAW:
                            if(tree!=null)
                                value = !t.equalsIgnoreCase("FALSE");
                            else
                                wrong_command = true;
                            break;        
                        default:                 
                                wrong_command = true;
                            break;
                    }
                }else{          
                    wrong_command = true;
                }  
            }
        }else{
            wrong_command = true;
        }
        
        if(wrong_command){
            System.err.println("Wrong command");
        }else{
            switch(command){
                case COMMAND_SEARCH:
                        msg = search(tree.getTreeID(), value);
                    break;
                case COMMAND_DELETE:
                        if(value instanceof BinaryTreeNode)
                            msg = delete(tree.getTreeID(),(BinaryTreeNode)value);
                        else
                            msg = delete(tree.getTreeID(), value);
                    break;
                case COMMAND_INSERT:
                        msg = insert(tree.getTreeID(), value);
                    break;
                case COMMAND_DRAW:
                        msg = draw(tree.getTreeID(), (Boolean)value);
                    break;
                case COMMAND_TREE_GET:
                        msg = get((String) value);
                    break;
                case COMMAND_TREE_SET:
                        msg = set((Class) value);
                    break;
                case COMMAND_TREE_LIST:                  
                        msg = Message.list();
                    break;
            }           
            if(msg!=null){
                sendMessage(msg);
            }
        }
    }
    
    
    /**
     * Reconnects to current server.
     */
    public void reconnect(){   
        System.out.println("Reconnecting to server.");
        initConnection(false);
    }
    /**
     * Disconnects from current server.
     */
    public void disconnect(){   
        if(socket!=null){
            if(!socket.isClosed()){
                System.out.println("Disconnecting from the server.");
                try {
                    socket.close();
                    socket=null;
                    ois = null;
                    oos = null;
                } catch (IOException ex) {
                    System.err.println("Error on closing socket");
                }
            }
        }  
    }
    
    
    /**
     * Connects to given IP on given port.
     * @param ip IP to change
     * @param port Port to change
     * @return True if successfully established connection.
     */
    public boolean connect(String ip,int port){   
        boolean proceed = true;
        Socket socket = null;
        System.out.println("Connecting to "+ip+":"+port);
        try {
            socket = new Socket(ip,port);
        } catch (IOException ex) {
            proceed =false;
            System.err.println("Could not connect");
        }
        if(proceed){      
            disconnect();
            ois = null;
            oos = null;
            config.port = port;
            this.socket = socket;
            
            sendTestPacket();
            if(config.loadTree&&(config.treeID!=null)){
                System.out.println("Trying to get last used tree.");
                sendMessage(Message.get(config.treeID));
            }
            
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    listenToServer();
                }
            }, 200);
            System.out.println("Connected");
        }
        return proceed;
    }
    /**
     * Used for IP input.
     */
    private final JFormattedTextField ip;
    /**
     * Used for port input.
     */
    private final JFormattedTextField port;
    /**
     * Used for changing port address.
     */
    private final Object[] opt;
    /**
     * Initiates connection to server.
     * @param tryAgain Whether to try again with ability to change ip and port of a server.
     */
    public void initConnection(boolean tryAgain){
        disconnect();
        boolean succeed = connect(config.ip,config.port);
        if(!succeed&&tryAgain){
            ip.setText(config.ip);
            port.setValue(config.port);
            int opt = JOptionPane.showConfirmDialog(this, this.opt,"Progrem connecting to server.",JOptionPane.OK_CANCEL_OPTION);
            if(opt==JOptionPane.OK_OPTION){        
                connect(ip.getText(), (Integer) port.getValue());      
            }    
        }
    }
    /**
     * Sends message to server
     * @param msg message to send.
     */
    public void sendMessage(Message msg){
        if(socket!=null){
            try {
                if(oos==null)
                    oos = new ObjectOutputStream(socket.getOutputStream());
                //System.out.println("Sending Message:"+msg);
                oos.writeObject(msg);
            } catch (IOException ex) {
                System.out.println("Error on getting output stream or sending message");
            }    
        }
    }
    
    /**
     * Sends object to server
     * @param ob object to send.
     */
    public void sendObject(Object ob){
        if(socket!=null){
            try {
                if(oos==null)
                    oos = new ObjectOutputStream(socket.getOutputStream());
                //System.out.println("Sending object:"+ob);
                oos.writeObject(ob);
            } catch (IOException ex) {
                System.out.println("Error on getting output stream or sending message");
            }    
        }
    }
    /**
     * Sends random tree to server.
     */
    public void sendRandomTree(){
        if(socket!=null){
            try {
                if(oos==null)
                    oos = new ObjectOutputStream(socket.getOutputStream());
                //System.out.println("Sending tree");
                oos.writeObject(BinaryTree.randomIntegerTree());
            } catch (IOException ex) {
                System.out.println("Error on getting output stream or sending message");
            }    
        }
    }
    /**
     * Sends test packet to server.
     */
    public void sendTestPacket(){
        if(socket!=null){
            try {
                if(oos==null)
                    oos = new ObjectOutputStream(socket.getOutputStream());
                //System.out.println("Sending test packet");
                oos.writeObject("Hello. I'm your new client.");
            } catch (IOException ex) {
                System.out.println("Error on getting output stream or sending message");
            }    
        }
    }
    /**
     * Starts listening to server messages.
     */
    public void listenToServer() {
        if(socket!=null){
            boolean has = true;
            try{
                ois = new ObjectInputStream(socket.getInputStream());        
            }catch(IOException ex){
                has = false;
                System.out.println("Error on getting input stream or receiving message");
            }   
            try{
                while(has){
                    try{
                        Object read = null;
                        read = ois.readUnshared();
                        //System.out.println("Server message: "+read);
                        if(read!=null){
                            if(read instanceof String){
                                System.out.println("Server message: "+read);
                            }else{
                                if(read instanceof BinaryTree){
                                    System.out.println("Received tree from server: <br>"+((BinaryTree) read).draw(false));
                                }else{
                                    if(read instanceof Message){
                                        //System.out.println("Received message from server: "+read);
                                        if(((Message) read).getTree() == tree){
                                            System.out.println("Received same tree");
                                        }
                                        execute((Message)read);
                                    }else{
                                        if(read instanceof BinaryTreeNode){
                                            System.out.println("read node:"+read+" testnode+"+testnode);
                                            if(read == testnode){
                                                System.out.println("Received same test node");
                                            }else{
                                                System.out.println("Received different test node");
                                            }
                                            if(((BinaryTreeNode) read).getValue() == testnode.getValue()){
                                                System.out.println("Received same value");
                                            }else{
                                                System.out.println("Received different value");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }catch (InvalidClassException | ClassNotFoundException ex) {
                        System.err.println(" Received invalid class object from server");
                    }
                }       
            }catch(IOException ex){
                System.out.println("Error on reading from server. Reconnect if error will still persist");
            }finally{
                System.out.println("Server disconnected");
                disconnect();
            }
        }
        
        
    }



//#########################Constructors#########################################
    /**
     * Constructor.
     */
    public Client(){    
        super("BinaryTree client","win_client_config");
        config = new Configuration(true);
        menu_File_Connect = new JMenuItem(new AbstractAction("Connect") {
            @Override
            public void actionPerformed(ActionEvent e) {
                initConnection(true);
            }
        });
        menu_File_Reconnect = new JMenuItem(new AbstractAction("Reconnect") {
            @Override
            public void actionPerformed(ActionEvent e) {
                reconnect();
            }
        });
        menu_File_Disconnect = new JMenuItem(new AbstractAction("Disconnect") {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect();
            }
        });
        menu_File_AutoLoad = new JCheckBoxMenuItem(new AbstractAction("Autoload tree") {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.loadTree = menu_File_AutoLoad.getState();
            }
        });
        menu_File_AutoLoad.setState(config.loadTree);
        menu_File.add(menu_File_AutoLoad,0);
        menu_File.add(new JSeparator(),0);
        menu_File.add(menu_File_Disconnect,0);
        menu_File.add(menu_File_Reconnect,0);
        menu_File.add(menu_File_Connect,0);
        AbstractFormatter af = new AbstractFormatter() {
            public boolean correct(String text){
                try{
                    boolean correct = true;
                    String t = text;
                    System.err.println("text:"+t);
                    for (int i = 0; i < 4; i++) {
                        int val;
                        if(i<3){
                            System.err.println("sub >"+t.substring(0,t.indexOf(".")));
                            val= Integer.parseInt(t.substring(0,t.indexOf(".")));
                        }else{
                            System.err.println("sub >"+t);
                            val= Integer.parseInt(t);
                        }
                        System.err.println("val >"+val);
                        if((val<0)||(val>255)){
                            return false;
                        }
                        if(i<3){
                            System.err.println("cut sub >"+t.substring(t.indexOf(".")+1));
                            t=t.substring(t.indexOf(".")+1);
                        }
                    }
                    return correct;
                }catch(NumberFormatException | ArrayIndexOutOfBoundsException | NullPointerException ex){
                    return false;
                }  
            }
            @Override
            public Object stringToValue(String text) throws ParseException {    
                if(correct(text)){
                   return text; 
                }else{
                    throw new ParseException("Not an IP",0);
                }
            }
            @Override
            public String valueToString(Object value) throws ParseException {
                if(value instanceof String){
                    if(correct((String) value)){
                        return (String) value; 
                    }else{
                        throw new ParseException("Not an IP",0);
                    }   
                }else{
                    throw new ParseException("Not a String",0);
                }
            }       
        };
        ip = new JFormattedTextField(af);
        af = new AbstractFormatter() {
            @Override
            public Object stringToValue(String text) throws ParseException {
                try{
                    int i = Integer.parseInt(text);
                    if((i>0)&&(i<Math.pow(2, 16))){
                        return i;
                    }else{
                        throw new ParseException("Not within <0 , 2^16-1> range",0);
                    }
                }catch(NumberFormatException ex){
                    throw new ParseException("Not a number",0);
                }            
            }
            @Override
            public String valueToString(Object value) throws ParseException {
                if(value instanceof Number){
                    int i = ((Number) value).intValue();
                    if((i>0)&&(i<Math.pow(2, 16))){
                        return ""+i;
                    }else{
                        throw new ParseException("Not within <0 , 2^16-1> range",0);
                    }
                }else{
                    throw new ParseException("Not a number",0);
                }
            }
        };
        
        jTree = new JTree(tree);
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Icon ic = new ImageIcon(image);
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer d = new DefaultTreeCellRenderer();
        d.setLeafIcon(ic);
        d.setClosedIcon(ic);
        d.setOpenIcon(ic);
        jTree.setCellRenderer(d);   
        jTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                //System.out.println(".valueChanged() changing");
                if(tree!=null){
                    Object t = jTree.getLastSelectedPathComponent();
                    if(t instanceof BinaryTreeNode){
                        //System.out.println(".valueChanged() new current node+"+t);
                        try{
                            tree.setCurrentNode((BinaryTreeNode) t);
                            updateNode(tree.getCurrentNode());
                        }catch(ClassCastException ex){}
                    }
                }
                
            }
        });
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        icon = new IconLoader();
        
        
        toolBar_value = new JTextField(5);  
        toolBar_insert = new JButton(new AbstractAction("Insert", icon.getIcon(IconLoader.ICON_INSERT)){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!toolBar_value.getText().equals("")){
                    execute("INSERT "+toolBar_value.getText());
                }
                toolBar_value.setText("");
            }
        });
        toolBar_insert.setText("");
        toolBar_insert.setToolTipText("Insert");
        toolBar_select = new JButton(new AbstractAction("Select", icon.getIcon(IconLoader.ICON_SELECT)){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!toolBar_value.getText().equals("")){
                    execute("SELECT "+toolBar_value.getText());
                }
                toolBar_value.setText("");
            }
        });
       toolBar_select.setText("");
        toolBar_select.setToolTipText("Select");
        toolBar_delete = new JButton(new AbstractAction("Delete", icon.getIcon(IconLoader.ICON_DELETE)){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!toolBar_value.getText().equals("")){
                    execute("DELETE "+toolBar_value.getText());
                }else{
                    if((tree!=null)&&(tree.getCurrentNode()!=null)){
                        sendMessage(delete(tree.getTreeID(), tree.getCurrentNode()));
                    }
                }
                toolBar_value.setText("");
                
            }
        });
        toolBar_delete.setText("");
        toolBar_delete.setToolTipText("Delete");
        String[] options = {"General","Integer","Double","String"};
        toolBar_new_selection = new JComboBox<String>(options);
        toolBar_new = new JButton(new AbstractAction("New tree", icon.getIcon(IconLoader.ICON_NEW)){
            @Override
            public void actionPerformed(ActionEvent e) {
                String opt = toolBar_new_selection.getItemAt(toolBar_new_selection.getSelectedIndex());
                if(opt!=null){     
                    execute("SET"+(opt.equalsIgnoreCase("General")?"":(" "+opt)  ));
                }
            }
        });        
        toolBar_new.setText("");
        toolBar_new.setToolTipText("New tree");
        toolBar_current = new JLabel("none"){
            @Override
            public void setText(String text) {
                super.setText("Current node: "+text);
            }
  
        };
        
        toolBar.add(toolBar_value);
        toolBar.add(toolBar_insert);
        toolBar.add(toolBar_delete);
        toolBar.add(toolBar_select);
        toolBar.add(new JSeparator(SwingConstants.VERTICAL));
        toolBar.add(toolBar_new_selection);
        toolBar.add(toolBar_new);
        toolBar.add(new JSeparator(SwingConstants.VERTICAL));
        toolBar.add(toolBar_current);
        treePanel.add(toolBar,BorderLayout.PAGE_START);
        treePanel.add(jTree,BorderLayout.CENTER);
        updateTree(tree);
        port = new JFormattedTextField(af);
        
        Object[] op = {"There was a problem while connecting to previous or default server."
                    + "\nCheck connection and try again.\nIf error will show up again try again later or contact the System Administrator for help. "
                    + "\nAlso check IP and port of the Server below and correct eventual mistakes:"
                    ,"IP:",ip,"Port:",port};
        opt = op;
        System.out.println("For list of commands and short tooltip type HELP or ? and then click enter. FULL HELP or ??? will result with explanation how to use commands and examples.");
        //System.out.println(BinaryTree.randomIntegerTree().draw(false));
    }





//#########################overriden methods####################################

    @Override
    public void doThisOnClose() {
        if(tree!=null)
            config.treeID =tree.getTreeID();
        config.saveToFile();
        try {
            if(socket!=null)
                socket.close();
        } catch (IOException ex) {
            System.err.println("Error on closing socket.");
        }
    }

    @Override
    public void doThisOnInput(String input) {
        //System.out.println("client.Client.execute() -1");
        execute(input);
    }
    
}
