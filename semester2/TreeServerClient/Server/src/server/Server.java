/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Frame.Window;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import packet.Message;
import static packet.Message.COMMANDS;
import static packet.Message.COMMANDS_TOOLTIP_SERVER;
import static packet.Message.COMMANDS_TOOLTIP_SERVER_USAGE;
import static packet.Message.COMMAND_DELETE;
import static packet.Message.COMMAND_DRAW;
import static packet.Message.COMMAND_INSERT;
import static packet.Message.COMMAND_SEARCH;
import static packet.Message.COMMAND_TREE_GET;
import static packet.Message.COMMAND_TREE_LIST;
import static packet.Message.COMMAND_TREE_SET;
import tree.BinaryTree;
import tree.BinaryTreeNode;

/**
 * Main server class.
 * @author n1t4chi
 */
public class Server extends Window{
    
    /**
     * Addition to server commands.
     */
    final private static int COMMANDS_SERVER_ADD = 100;
    /**
     * Command for making random tree.
     */
    final static int COMMANDS_SERVER_RANDOM = COMMANDS_SERVER_ADD+0;
    /**
     * Command for saving trees.
     */
    final static int COMMANDS_SERVER_SAVE = COMMANDS_SERVER_ADD+1;
    /**
     * Command for loading trees.
     */
    final static int COMMANDS_SERVER_LOAD = COMMANDS_SERVER_ADD+2;
    /**
     * Command for resetting server.
     */
    final static int COMMANDS_SERVER_RESET = COMMANDS_SERVER_ADD+3;
    /**
     * Command for changing server port.
     */
    final static int COMMANDS_SERVER_PORT = COMMANDS_SERVER_ADD+4;
    /**
     * Command for changing tree options.
     */
    final static int COMMANDS_SERVER_TREE = COMMANDS_SERVER_ADD+5;
    /**
     * Server specific commands.
     */
    final static String[] COMMANDS_SERVER = {"RANDOM","SAVE","LOAD","RESET","PORT","TREE"};
    /**
     * Usages for server commands.
     */
    final static String[] COMMANDS_SERVER_USAGE = {
            "[nothing]/[INTEGER]/[STRING]/[DOUBLE]"  //"RANDOM"
            , "[nothing]/[ALL]]/[treeID]"   //"SAVE"
            , "[ALL]]/[treeID] *([NO]/[YES]/[nothing])"   //"LOAD"
            , "[YES]"   //"RESET"
            , "[port] [YES]"   //"PORT"
            , "[treeID] [POLICY]/[TYPE]  *([ALL]/[ALL_VALUES]/[SELECTIVE]/[STRING]/[INTEGER]/[DOUBLE])" //TREE
    
    };
    /**
     * Tooltips for server commands.
     */
    final static String[] COMMANDS_SERVER_TOOLTIPS = {
            "Makes new random tree based on specified type,  if no option will be given then INTEGER tree will be created"  //"RANDOM"
            ,"Saves either spoecified tree or all trees, if no option will be given then all trees will be saved"   //"SAVE"
            ,"Loads either spoecified tree or all trees."
            + "\n\t - treeID - specified tree is loaded and will replace current one. "
            + "\n\t - ALL - trees from //tree folder will be loaded. If additional YES parameter is given then when loaded tree from file is currently available on server then current one will be replced. Otherwise currently available tree will remain intact."   //"LOAD"
            ,"Resets socket on which server works. For security reasons YES must be typed after command name."   //"RESET"
            ,"Changes current port on which server works. For security reasons YES must be typed after parameter."   //"PORT"
            ,"Changes specified tree parameter."
            + "\n\t - POLICY parameter and one of [ALL]/[ALL_VALUES]/[SELECTIVE] means that Tree will either accept every object as new node, all values as new nodes or only unique values as new nodes. If new object is not inserted then counter of node that matches with that object is incremented. Does not change type. ALL means that same objects or null pointers can be inserted."
            + "\n\t - TYPE parameter and one of [ALL]/[STRING]/[INTEGER]/[DOUBLE] means that tree will change new[!!!!] values inserted by clients. Old nodes will remain and new values added to tree will be limited to given type." //TREE
    
    };


    //#########################Static Context###############################
    /**
     * Main method that starts server.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Server server = new Server();
                server.setVisible(true);
                server.initConnection();
            }
        });
    }
    
    
    
    






    //#########################Fields#######################################

    /**
     * JTree containing available trees.
     */
    final JTree  tree;
    /**
     * List of currently loaded trees.
     */
    final ArrayList<BinaryTree>  treeList;
    /**
     * Config object
     */
    final Configuration config;
    
    /**
     * Server socket.
     */
    ServerSocket serverSocket;
    /**
     * List of clients that are not fully ready for input/output.
     */
    final java.util.ArrayList<Client> clients;    
    /**
     * List of clients that are currently working.
     */
    final java.util.ArrayList<Client> workingClients;  
    /**
     * File Menu item. Resets server
     */
    final JMenuItem menu_File_ResetServer;
    /**
     * File Menu item. Changes port.
     */
    final JMenuItem menu_File_ChangePort;
    /**
     * File Menu item. Saves trees.
     */
    final JMenuItem menu_File_Save;
    /**
     * File Menu item. Saves trees.
     */
    final JMenuItem menu_File_Load;
    /**
     * File Menu item. Indicates if trees should be load automatically.
     */
    final JCheckBoxMenuItem menu_File_AutoLoad;
    /**
     * File Menu item. Indicates if loaded trees should replace older ones.
     */
    final JCheckBoxMenuItem menu_File_LoadReplace;


//#########################Methods##############################################
    /**
     * Closes server socket.
     */
    public void closeServerSocket(){  
        if(this.serverSocket!=null){
            try {
                this.serverSocket.close();
                serverSocket=null;
            } catch (IOException ex) {
                System.err.println("Error on closing socket");
            }
        }    
    }
    /**
     * Changes port.
     * @param port Port to change
     */
    public void changePort(int port){  
        run = false;
        boolean proceed = true;
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
        } catch (IOException ex) {
            proceed =false;
            System.err.println("Wrong server port");
        }
        if(proceed){        
            closeServerSocket();
            config.port = port;
            this.serverSocket = socket;
            System.out.println("Port changed to "+port);
            run = true;
        }
    }
    
    /**
     * Opens server.
     */
    public void initConnection(){
        changePort(config.port);    
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getClient();
            }
        },10,10);
    }

    /**
     * Class for starting listening to clients.
     */
    private class ClientTask extends TimerTask{
        Client client;
        @Override
        public void run() {
            if(client!=null)
                listenToClient(client);      
        }
        public ClientTask (Client client){
            this.client = client;
        }
    }
    /**
     * Returns Tree that matches given ID.
     * @param treeID ID to check.
     * @return Tree that matches ID, might be null if not one was found.
     */
    public BinaryTree getTree(String treeID){
        BinaryTree rtrn = null;
        for (BinaryTree binaryTree : treeList) {
            if(binaryTree.getTreeID().equalsIgnoreCase(treeID)){
                rtrn = binaryTree;
                break;
            }
        }
        return rtrn;
    }
    
   /* public static final class gfgdf extends TimerTask{
        final Client cl;
        Server serv;
        gfgdf (Server serv,Client cl){
            this.serv = serv;
            this.cl = cl;
        }
        @Override
        public void run() {
            System.out.println("server.Server.gfgdf.run()");
            serv.writeObjectToClient(cl, "work");
        }
        
    }*/
    
    
    /**
     * Whether to accept new clients and listen to them.
     */
    boolean run=false;
    /**
     * Waits for new client.
     */
    public void getClient(){  
        boolean error = false;
        if(run){
            Client client = null;
            try {              
                System.out.println("Waiting for client");
                Socket new_client = serverSocket.accept();
                client = new Client(new_client);
                System.out.println("Got client "+client);
                writeObjectToClient(client, "Hello "+client+", what can I do for you?");
                clients.add(client);
                Timer t = new Timer();
                t.schedule(new ClientTask(client), 5);
                //gfgdf tsk = new gfgdf(this,client);
                //t.scheduleAtFixedRate(tsk, 5000,2000);
            } catch (java.net.SocketException ex){
                error = true;
                System.err.println(client+" There has been a problem with socket.");
                run=false;
            } catch (IOException ex) {
                error = true;
                System.err.println(client+" There has been a problem with connection.");
                run=false;
            }
            if(!error)
                getClient();
        }
    }
    /**
     * Executes given message and then returns a reply.
     * @param message message to execute.
     * @return Reply to given message.
     */
    public Message executeAndGetReply(Message message){
        Message rtrn = null;
        //System.err.println("server.Server.executeAndGetReply() [0] message:"+message );
        if(message!=null){
            if((message.getType()!=COMMAND_TREE_LIST)&&(message.getType()!=COMMAND_TREE_SET)){
                BinaryTree tree = getTree(message.getTreeID());
                //System.err.println("server.Server.executeAndGetReply() [10] tree:"+tree+ " value:"+message.getValue());
                if(tree!=null){                       
                    switch(message.getType()){
                        case COMMAND_DELETE:
                                tree.setDeletionMode(BinaryTree.DELETE_NODE);
                                if(message.getNode()!=null){
                                    tree.delete(message.getNode());
                                    System.out.println(tree+" node is deleted:"+message.getNode());
                                }else{
                                    tree.delete(message.getValue());
                                    System.out.println(tree+" value is deleted:"+message.getValue());
                                }
                                rtrn = Message.deleteReply(tree.getTreeID(), tree);
                            break;
                        case COMMAND_INSERT:
                                //System.err.println("server.Server.executeAndGetReply() [20] inserting"+message.getValue());
                                //System.err.println("server.Server.executeAndGetReply() [30] tree pre:\n"+tree.draw());
                                tree.insert(message.getValue());
                                System.out.println(tree+" value is inserted:"+message.getValue());
                                //System.err.println("server.Server.executeAndGetReply() [30] tree post:\n"+tree.draw());
                                rtrn = Message.insertReply(tree.getTreeID(), tree);
                                //System.err.println("server.Server.executeAndGetReply() [30] tree in reply:\n"+rtrn.getTree().draw());
                            break;
                        case COMMAND_SEARCH:
                                rtrn = Message.searchReply(tree.getTreeID(), tree,tree.search(message.getValue()));
                            break;
                        case COMMAND_DRAW:   
                                if(message.getValue() instanceof Boolean){                      
                                    rtrn = Message.drawReply(tree.getTreeID(), tree.draw((boolean) message.getValue()));
                                }
                            break;
                        case COMMAND_TREE_GET:                    
                                rtrn = Message.getReply(tree.getTreeID(), tree);
                            break; 
                    }       
                }      
            }else{
                if(message.getType()==COMMAND_TREE_LIST){
                    String[] list = new String[treeList.size()];
                    //System.err.println("server.Server.executeAndGetReply() [11] list is string[]?"+(list instanceof String[]));
                    int i=0;
                    for(BinaryTree tree : treeList){
                        list[i]=tree.getTreeID();
                        i++;
                    }
                    //System.err.println("server.Server.executeAndGetReply() [21] list:\n"+Arrays.toString(list)+"\n");
                    rtrn = Message.listReply(list);
                    //System.err.println("server.Server.executeAndGetReply() [11] value is string[]?"+(rtrn.getValue() instanceof String[]));
                }else{//tree set
                    BinaryTree tree = newTree(message.getValue());
                    //System.err.println("server.Server.executeAndGetReply() [12] tree:"+tree);
                    if(tree!=null){
                        rtrn = Message.setReply(tree.getTreeID(), tree);
                    }      
                }
            }
        }
       // System.err.println("server.Server.executeAndGetReply() [3] rtrn:"+rtrn);
       updateTree();
        return rtrn;
    }
    /**
     * Sends object to specified client.
     * @param client Client.
     * @param obj Object to send.
     */
    public void writeObjectToClient(Client client, Object obj){
        if((run)&&(client!=null)&&(obj!=null)){
            System.out.println("Sending ["+obj+"] to "+client);
            ObjectOutputStream oos = null;
            try {
                oos = client.getOutput();
                oos.writeUnshared(obj);
                oos.reset();
            }catch(IOException ex){
                System.err.println(client+" There was problem while sending a message.");
            }
        }
    }
    /**
     * Sends message to specified client.
     * @param client Client.
     * @param message Message to send.
     */
    public void writeToClient(Client client, Message message){
        writeObjectToClient(client,message);
    }
    
    /**
     * Listens to given client.
     * @param client Client to listen to.
     */
    public void listenToClient(Client client){
        System.out.println(client+" Starting listening");
        if((run)&&(client!=null)){
            ObjectInputStream ois = null;
            try {
                ois = client.getInput();
                if(ois!=null){
                    boolean has = true;
                    while(has){
                        try{
                            Object read = ois.readObject();
                            if(read!=null){
                                if(read instanceof String){
                                    System.out.println(client+" Client message: "+read);
                                }else{
                                    if(read instanceof BinaryTree){
                                        System.out.println(client+" Received tree: <br>"+((BinaryTree) read).draw(false));
                                    }else{
                                        if(read instanceof Message){
                                            System.out.println(client+" Received message: "+read);
                                            Message msg = executeAndGetReply((Message)read);
                                            System.out.println(client+" Replying with "+msg);
                                            writeToClient(client,msg);
                                        }else{
                                            if(read instanceof BinaryTreeNode){
                                                System.out.println(client+" Received node: "+read);
                                                writeObjectToClient(client, read);
                                            }
                                        }
                                    }
                                }
                            }
                        }catch (InvalidClassException | ClassNotFoundException ex) {
                            System.err.println(client+" Received invalid class object");
                        }
                        catch(IOException ex){
                            System.err.println(client+" Error on connection");
                            has = false;
                        }
                    }
                }
            } catch (IOException ex) {
                    System.err.println(client+" Error reading from client ");
            } finally {
                workingClients.remove(client);
                System.out.println(client+" Closing connection ");
                try {
                    if(ois!=null)
                        ois.close();                
                } catch (IOException ex) {
                    System.err.println(client+" Error on closing communication");
                }
            }
        }
    }
    
    /**
     * Inserts tree
     * @param tree tree to insert.
     * @param replace Whether to all tree replacement or not.
     */
    public void insertTree(BinaryTree tree,boolean replace){
        System.out.println("Adding tree:"+tree);
        if(tree!=null){
            BinaryTree t = getTree(tree.getTreeID());
            if(!replace&&(t!=null)){
                System.err.println("There is already such tree on server.");
            }else{
                if(t!=null){
                    System.err.println("Replacing current tree available.");
                    treeList.remove(t);
                }    
                treeList.add(tree); 
                updateTree();
                System.out.println("Tree added: "+tree+"\n"+tree.draw(false));     
            }
        }else{
            System.err.println("Trying to insert null tree");
        }
    }
    /**
     * Adds new tree based on given string:
     * INTEGER - Integer values.
     * DOUBLE - Real values.
     * STRING - Text values.
     * nothing - General values.
     * @param t String.
     * @return created Tree, might be null.
     */
    public BinaryTree newTree(Object t){
        if(t instanceof String){
            String s = (String)t;
            if(s.equalsIgnoreCase("INTEGER")){
                t = Integer.class;
            }else{
                if(s.equalsIgnoreCase("DOUBLE")){
                    t = Double.class;
                }else{
                    if(s.equalsIgnoreCase("STRING")){
                        t = String.class;
                    }else{
                        if(s.equalsIgnoreCase("")){
                            t = Object.class;
                        }else{
                            System.err.println("Wrong type. Do not give parameter if you want to create general purpose tree.");
                        }
                    }    
                }
            }
        }
        BinaryTree tree = null;
        if(t instanceof Class){
            tree = new BinaryTree(null, (Class)t);
        }
        if(tree!=null){
            insertTree(tree, false);
        }    
        return tree;
    }
    /**
     * Executes given string as a command.
     * @param t Command to execute.
     */
    public void execute(String t){
        //System.err.println("client.Client.Execute() [0] t:"+t);
        int command = -1;
        boolean wrong_command = false;
        while(t.contains("  ")){
            t = t.replaceAll("  ", " ");
        }
        if(t.endsWith(" ")){
            t = t.substring(0, t.length()-1);
        }
        if(t!=null){
            int help = ( 
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
                //System.err.println("client.Client.Execute() 1");
                String text = "Available commands:\n";
                for(int i=0; (i<COMMANDS.length)&&(i<COMMANDS_TOOLTIP_SERVER.length)&&(i<COMMANDS_TOOLTIP_SERVER_USAGE.length);i++){
                    if(i!=COMMAND_TREE_GET)
                        text+=(COMMANDS[i]+" "+COMMANDS_TOOLTIP_SERVER_USAGE[i]+" - "+COMMANDS_TOOLTIP_SERVER[i]+"\n");
                }
                for(int i=0; (i<COMMANDS_SERVER.length)&&(i<COMMANDS_SERVER_TOOLTIPS.length)&&(i<COMMANDS_SERVER_USAGE.length);i++     ){
                    text+=(COMMANDS_SERVER[i]+" "+COMMANDS_SERVER_USAGE[i]+" - "+COMMANDS_SERVER_TOOLTIPS[i]+"\n");
                }
                //System.err.println("client.Client.Execute() 2");
                System.out.print(text);
                if(  help>1   ){
                    text=(
                        "How to read:\n"
                        + "COMMAND [parameter#1]/[CONSTANT]/[nothing] [parameter#2] *([parameter#2])\n"
                        + " - Command is a type of a command.\n"
                        + " - [parameter] means that value is expected to be given with a command.\n"
                        + " - [nothing] means that no value or constant is required for it to work.\n"
                        + " - [CONSTANT] means that given word in brackets is required.\n"
                        + " - [choice#1]/[choice#2] means that either choice#1 or choice#2 is needed.\n"
                        + " - [parameter#1] [parameter#2] means that both parameter#1 and parameter#2 is needed\n"
                        + " - [parameter#1] *([parameter#2]) means that parameter#2 depends heavily on parameter#1"
                    );
                    System.out.println(text);
                    text=("Examples:\n"
                        + "LIST\n"
                        + "INSERT 1829164700###Integer 150\n"
                        + "SET INTEGER\n"
                        + "DELETE 4823423466###Integer NODE 16      //this deletes first node with value 16\n"
                        + "DELETE 3463463246###Integer NODE         //this deletes current active node\n"  
                        + "DELETE 3233474743###Integer NODE ALL     //this deletes all nodes within tree\n"
                        + "DELETE 3233474743###Integer TREE         //this removes tree from the server\n"
                        + "PORT 5000 YES\n"
                        + "LOAD ALL                                 //This loads only new trees to server from //trees folder\n"
                        + "LOAD ALL NO                              //Same as above\n"
                        + "LOAD ALL YES                             //Loads all trees and replaces old ones if they have same ID\n"
                        + "LOAD 3233474743###Integer                //Loads 3233474743###Integer tree if it exists in //trees folder\n"
                    );
                    System.out.println(text);
                }
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
                if(command<0){
                    for(int i=0; i<COMMANDS_SERVER.length;i++){
                        if(
                            (t.length()>=COMMANDS_SERVER[i].length())
                            &&(
                                t.substring(0, COMMANDS_SERVER[i].length()).equalsIgnoreCase(COMMANDS_SERVER[i])
                            )
                        ){
                            command = COMMANDS_SERVER_ADD+i;
                            break;
                        }
                    }
                } 
                if(command>-1){      
                    if(command>=COMMANDS_SERVER_ADD)
                        t = t.substring(COMMANDS_SERVER[command%COMMANDS_SERVER_ADD].length());
                    else
                        t = t.substring(COMMANDS[command].length());
                    if(t.startsWith(" ")){
                        t=t.substring(1);
                    }
                }
                if(command>=COMMANDS_SERVER_ADD){
                    switch(command){                 
                        case COMMANDS_SERVER_TREE:
                                int index= t.indexOf(" ");
                                if(t.contains("###")&&(index>0) ){
                                    String id = t.substring(0,index);     
                                    t=t.substring(index);
                                    if(t.startsWith(" ")){
                                        t=t.substring(1);
                                    }
                                    BinaryTree tree = getTree(id);
                                    if(tree!=null){
                                        if(t.length()>"POLICY".length() && (t.substring(0,"POLICY".length()).equalsIgnoreCase("POLICY"))){
                                            t = t.substring("POLICY".length());
                                            if(t.startsWith(" ")){
                                                t=t.substring(1);
                                            }
                                            if(t.equalsIgnoreCase("ALL")){
                                                System.out.println("Changing tree "+tree+" policy insertion policy to all objects");
                                                tree.setInsertionMode(BinaryTreeNode.INSERT_TYPE_ALL);
                                            }else{
                                                if(t.equalsIgnoreCase("ALL_VALUES")){
                                                    System.out.println("Changing tree "+tree+" policy insertion policy to all values");
                                                    tree.setInsertionMode(BinaryTreeNode.INSERT_TYPE_ALL_VALUES);
                                                }else{
                                                    if(t.equalsIgnoreCase("SELECTIVE")){
                                                        System.out.println("Changing tree "+tree+" policy insertion policy to selective insertion");
                                                        tree.setInsertionMode(BinaryTreeNode.INSERT_TYPE_SELECTIVE);
                                                    }else{
                                                        System.err.println("Invalid policy.");  
                                                    }    
                                                }    
                                            }
                                        }else{
                                            if(t.length()>"TYPE".length() && (t.substring(0,"TYPE".length()).equalsIgnoreCase("TYPE"))){                           
                                                t = t.substring("TYPE".length());
                                                if(t.startsWith(" ")){
                                                    t=t.substring(1);
                                                }
                                            if(t.equalsIgnoreCase("ALL")){
                                                System.out.println("Changing tree "+tree+" type to all");
                                                tree.setClassType(Object.class);
                                            }else{
                                                if(t.equalsIgnoreCase("STRING")){
                                                    System.out.println("Changing tree "+tree+" type to string");
                                                    tree.setClassType(String.class);
                                                }else{
                                                    if(t.equalsIgnoreCase("INTEGER")){
                                                        System.out.println("Changing tree "+tree+" type to integer");
                                                        tree.setClassType(Integer.class);
                                                        
                                                    }else{
                                                        if(t.equalsIgnoreCase("DOUBLE")){  
                                                            System.out.println("Changing tree "+tree+" type to double"); 
                                                            tree.setClassType(Double.class);
                                                            
                                                        }else{
                                                            System.err.println("Invalid type.");  
                                                        }  
                                                    }    
                                                }    
                                            }
                                            }else{
                                                wrong_command = true;
                                            } 
                                        }   
                                    }else{
                                        System.err.println("Invalid tree ID. See LIST for available trees.");   
                                    }
                                }else{
                                    System.err.println("No tree ID given.");
                                    wrong_command = true;
                                }
                                    
                            break;
                        case COMMANDS_SERVER_SAVE:
                                if(t.isEmpty()||t.equalsIgnoreCase("ALL")){
                                    saveTrees();
                                }else{
                                    BinaryTree tree = getTree(t);
                                    if(tree!=null){
                                        saveTree(tree);
                                    }else{
                                        System.err.println("Invalid tree ID. See LIST for available trees.");
                                    }
                                }
                            break;
                        case COMMANDS_SERVER_LOAD:
                                if((t.length()>3)&&(t.substring(0,3).equalsIgnoreCase("ALL"))){
                                    loadTrees(t.contains("YES"));
                                }else{
                                    loadTree(t,true);
                                }
                            
                        case COMMANDS_SERVER_RESET:
                        case COMMANDS_SERVER_PORT:
                                if(t.contains("YES")){
                                    t= t.replaceAll("YES","");
                                    switch(command){
                                        case COMMANDS_SERVER_LOAD:   
                                                t=t.replaceAll(" ", "");
                                                if(t.equalsIgnoreCase("ALL")){
                                                    loadTrees(true);
                                                }else{
                                                    BinaryTree tree = getTree(t);
                                                    if(tree!=null){
                                                        saveTree(tree);
                                                    }else{
                                                        System.err.println("Invalid tree ID. See LIST for available trees.");
                                                    }
                                                }
                                            break;     
                                        case COMMANDS_SERVER_RESET:
                                            reset();
                                        break;
                                        case COMMANDS_SERVER_PORT:
                                            t=t.replaceAll(" ", "");
                                            changePort(t);
                                        break;
                                    }
                                }else{
                                    System.err.println("YES keyword is required as last parameter due to security reasons.");
                                }
                            break;
                        case COMMANDS_SERVER_RANDOM:
                                BinaryTree tree = null;
                                if(t.equalsIgnoreCase("INTEGER")||t.isEmpty()){
                                    tree = BinaryTree.randomIntegerTree();          
                                }else{
                                    if(t.equalsIgnoreCase("DOUBLE")){
                                        tree = BinaryTree.randomDoubleTree();
                                    }else{
                                        if(t.equalsIgnoreCase("STRING")){
                                            tree = BinaryTree.randomStringTree();
                                        }else{
                                            System.err.println("Wrong type");
                                        }
                                    }
                                }
                                if(tree!=null){
                                    insertTree(tree, false);               
                                }
                            break;
                    }        
                }else{
                    //System.err.println("client.Client.Execute() [1] t:"+t+"  command:"+command);
                    if((command>-1)&&(command != COMMAND_TREE_GET)){
                        //System.err.println("client.Client.Execute() [2] t:"+t+"  command:"+command);
                        if(command == COMMAND_TREE_LIST){
                            if(treeList.isEmpty()){
                                System.out.println("There are currently no trees available.");
                            }else{
                                String text = "Available trees:\n";
                                for (BinaryTree binaryTree : treeList) {
                                    text+=binaryTree.getTreeID()+"\n";
                                }
                                System.out.println(text);
                            }
                        }else{
                            //System.err.println("client.Client.Execute() [3] t:"+t+"  command:"+command);
                            //System.out.println("client.Client.Execute() cut command:"+t);
                            if(command == COMMAND_TREE_SET){
                                newTree(t);
                            }else{
                                int index= t.indexOf(" ");
                                if (index==-1)
                                    index = t.length();
                                String id = t.substring(0,index);
                                //System.err.println("client.Client.Execute() [4] t:"+t+" id:"+id+" command:"+command);
                                if(id.contains("###")){
                                    BinaryTree tree = getTree(id);
                                    //System.err.println("client.Client.Execute() [5] t:"+t+" id:"+id+" command:"+command+" tree"+tree);
                                    if(tree!=null){
                                        if(index<t.length())
                                            t = t.substring(index+1);      
                                        //System.err.println("client.Client.Execute() [6] t:"+t+" id:"+id);                 
                                        switch(command){
                                            case COMMAND_DELETE: 
                                                if(t.startsWith("NODE")){
                                                    index= t.indexOf(" ");
                                                    if (index==-1)
                                                        index = t.length();
                                                    t = t.substring(index);
                                                    //System.err.println("client.Client.Execute() [7] t:"+t+" id:"+id);  
                                                    if(t.equals("")){
                                                        if(tree.getCurrentNode()!=null){
                                                            System.out.println("Deleting node:"+tree.getCurrentNode());
                                                            tree.setDeletionMode(BinaryTree.DELETE_NODE);                                                 
                                                            tree.delete(tree.getCurrentNode());
                                                        }else{
                                                            System.out.println("No current node active.");
                                                        }
                                                        break; 
                                                    }else{
                                                        if(t.equals("ALL")){
                                                            System.out.println("Deleting all nodes:");
                                                            tree.setDeletionMode(BinaryTree.DELETE_ALL);                                                 
                                                            tree.delete();
                                                            break; 
                                                        }
                                                        /*
                                                        Here this case will transfer to below cases of insert and search where code can be mostly shared.
                                                        */
                                                    }
                                                }else{
                                                    if(t.startsWith("TREE")){
                                                        System.out.println("Deleting tree:"+tree);
                                                        treeList.remove(tree);
                                                    }else{
                                                        System.out.println("For security reasons you need to specify deletion mode by giving TREE or NODE as 2nd parameter. See FULLHELP for examples");
                                                    }
                                                    break; 
                                                }
                                                //no break here.
                                            //case COMMAND_DELETE: where T string does not equal either TREE or NODE or NODE ALL
                                            case COMMAND_SEARCH: 
                                            case COMMAND_INSERT: 
                                                    Object val = null;

                                                    try{
                                                        if(tree.getClassType() == Integer.class){
                                                            val = Double.parseDouble(t);
                                                        }else{
                                                            if(tree.getClassType() == Double.class){
                                                                val = Double.parseDouble(t);
                                                            }else{
                                                                val = t;
                                                            }
                                                        }
                                                    }catch(NumberFormatException ex){ 
                                                        System.err.println("Invalid given value.");
                                                    }
                                                    switch(command){
                                                        case COMMAND_DELETE:
                                                                tree.setDeletionMode(BinaryTree.DELETE_NODE);                                                 
                                                                tree.delete(val);
                                                                System.out.println("Value deleted: "+val);
                                                            break;
                                                        case COMMAND_SEARCH: 
                                                                BinaryTreeNode node = tree.search(val);
                                                                if(node!=null){
                                                                    tree.setCurrentNode(node);
                                                                    System.out.println("Node found :"+node);
                                                                }else{
                                                                    System.out.println("No value was found in the tree");
                                                                }
                                                            break;
                                                        case COMMAND_INSERT:                                                      
                                                                tree.insert(val);
                                                                System.out.println("Value inserted: "+val);
                                                            break;
                                                    }         
                                                break;
                                            case COMMAND_DRAW:
                                                System.out.println("\n"+tree.draw(false));
                                                break;   
                                        }
                                    }else{
                                        System.err.println("There is no such tree.");  
                                    }
                                }else{
                                    System.err.println("Invalid tree ID. See LIST for available trees.");
                                    wrong_command = true;
                                }
                            }
                        }
                    }else{   
                        if(command == COMMAND_TREE_GET)
                            wrong_command = true;
                    }  
                }
            }
        }else{
            wrong_command = true;
        }
        
        if(wrong_command){
            System.err.println("Wrong command");
        }else{
            updateTree();
        }
    }

    /**
     * Loads tree from //tree folder if it exists.
     * @param TreeID ID of a tree
     * @param replace Whether to replace current ones.
     */
    public void loadTree(String TreeID,boolean replace){
        System.out.println("Trying to load tree "+TreeID);
        BinaryTree tree = BinaryTree.loadTree(TreeID);
        if(tree!=null){
            BinaryTree t = getTree(tree.getTreeID());
            if((t!=null)&&(treeList.remove(t))){
                System.err.println("This tree:"+tree+" will be overwritten");
            }
            insertTree(tree, replace);
            //System.out.println("Tree loaded:"+tree);
        }
    }
    /**
     * Loads available trees from //tree folder.
     * @param replace Whether to replace current ones.
     */
    public void loadTrees(boolean replace){
        System.out.println("Loading available trees from files. Might override previous ones.");
        File fil = new File("\\trees");
        //System.err.println("server.Server.loadTrees() file url:"+fil.getAbsolutePath());
        if(fil.exists()&&fil.isDirectory()){
            File[] list = fil.listFiles((File dir, String name1) -> (name1.contains("###") && name1.contains(".sav")));
            if((list==null)||(list.length==0)){
                System.err.println("There are no tree files.");
            }else{
                for (File file : list) {
                    if(file!=null){
                        loadTree(file.getName(),replace);
                    }
                }
            }
        }else{
            System.err.println("There is no folder with trees.");
        }
    }
    /**
     * Saves available trees to //tree folder.
     */
    public void saveTrees(){
        System.out.println("Saving available trees to files.");
        for (BinaryTree binaryTree : treeList) {
            saveTree(binaryTree);
        }    
    }
    /**
     * Saves tree to //tree folder.
     * @param binaryTree Tree to save.
     */
    public void saveTree(BinaryTree binaryTree){
        System.out.println("Trying to save "+binaryTree);
        if(BinaryTree.saveTree(binaryTree)){
            System.out.println("Successfully saved "+binaryTree);
        }
    }
    /**
     * Resets server.
     */
    public void reset(){
        closeServerSocket();
        initConnection();
    }
    
    /**
     * Changes port if string is a integer within 0-2^16-1 range.
     * @param opt String representation of port.
     */
    public void changePort(String opt){
        opt=opt.replaceAll(" ", "");
        int new_port = 0;
        try{
            new_port = Integer.parseInt(opt);
        }catch(NumberFormatException ex){
            System.err.println("Invalid port");
        }
        if(new_port != config.port){
            changePort(new_port);
        }
    }
    /**
     * Updates gui Tree
     */
    public void updateTree(){   
        //System.out.println("Updating gui tree.");
        tree.setModel(new model(this));
        for(int i =0; i< tree.getRowCount();i++)
            tree.expandRow(i);
        tree.repaint();
    }
    
    
//#########################Constructors#########################################
    /**
     * Constructor.
     */
    public Server(){ 
        super("BinaryTree server","win_server_config");
        treeList = new ArrayList<>();
        config = new Configuration(true);
        clients = new ArrayList<>();
        workingClients = new ArrayList<>(); 
        menu_File_ChangePort = new JMenuItem(new AbstractAction("Port"){
            @Override
            public void actionPerformed(ActionEvent e) {
                String opt = JOptionPane.showInputDialog(
                        Server.this
                        ,"Change port of the server.\n Remember that clients will need to change ports too!"
                        , (""+config.port)
                );
                //System.out.println("opt:"+opt);
                if(opt!=null){
                    changePort(opt);
                }
            }
        });
        menu_File_ResetServer = new JMenuItem(new AbstractAction("Reset") {     
            @Override
            public void actionPerformed(ActionEvent e) {
                int opt = JOptionPane.showConfirmDialog(
                        Server.this
                        , "Are you sure you want to do this? It will terminate all connections!"
                        , "Resetting server"
                        , JOptionPane.YES_NO_OPTION
                );
                if(opt == JOptionPane.YES_OPTION){
                    reset();
                }
            }
        });
        menu_File.add(menu_File_ChangePort,0);
        menu_File.add(menu_File_ResetServer,0);
        menu_File.add(new JSeparator(),0);
        menu_File_AutoLoad = new JCheckBoxMenuItem(new AbstractAction("Load on startup") {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.autoLoad = menu_File_AutoLoad.getState();
            }
        });
        menu_File_AutoLoad.setState(config.autoLoad);
        
        menu_File_LoadReplace = new JCheckBoxMenuItem(new AbstractAction("Replace on loading") {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.replaceLoad = menu_File_LoadReplace.getState();
            }
        });
        menu_File_LoadReplace.setState(config.replaceLoad);   
        menu_File_Load = new JMenuItem(new AbstractAction("Load") {
            @Override
            public void actionPerformed(ActionEvent e) {           
                loadTrees(config.replaceLoad);
            }
        });
        menu_File_Save = new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {           
                saveTrees();
            }
        });
        menu_File.add(menu_File_AutoLoad,0);
        menu_File.add(menu_File_LoadReplace,0);
        menu_File.add(new JSeparator(),0);
        menu_File.add(menu_File_Load,0);
        menu_File.add(menu_File_Save,0);
        tree = new JTree(new model(this));
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Icon ic = new ImageIcon(image);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer d = new DefaultTreeCellRenderer();
        d.setLeafIcon(ic);
        d.setClosedIcon(ic);
        d.setOpenIcon(ic);
        tree.setCellRenderer(d);
        treePanel.add(tree,BorderLayout.CENTER);
        System.out.println("For list of commands and short tooltip type HELP or ? and then click enter. FULL HELP or ??? will result with explanation how to use commands and examples.");
        if(config.autoLoad){
            loadTrees(true);
        }
    }

    

//#########################overriden methods####################################
    @Override
    public void doThisOnClose() {
        config.saveToFile();
        saveTrees();
        try {
            if(serverSocket!=null)
                serverSocket.close();
        } catch (IOException ex) {
            System.err.println(" Error when closing socket.");
        }
    }
    
    @Override
    public void doThisOnInput(String input) {
       execute(input);
    }
    @Override
    public String toString() {
       return "Server";
    }
    
}
