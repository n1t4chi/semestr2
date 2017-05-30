/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import basicgui.BasicFrame;
import basicgui.PortInput;
import java.awt.BorderLayout;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * GUI wrapper for LobbyServer
 * @author n1t4chi
 */
public class GUIServer extends LobbyServer {

    /**
     * GUI
     */
    private final BasicFrame gui;
    /**
     * Output part of console
     */
    private final JEditorPane output;
    /**
     * Input part of console
     */
    private final JTextField input;
    
    /**
     * Menu item for server initialisation.
     */
    private final JMenuItem initialise;
    /**
     * Menu item for server restart.
     */
    private final JMenuItem restart;
    /**
     * Menu item for changing port.
     */
    private final JMenuItem change_port;
   
    /**
     * Method called on clicking initialise in menu bar or as command through console input
     */
    private void commandInitialise(){
        if(!isWorking()){
            try {
                init();
            } catch (Exception ex) {}
        }else{
            printSys("Server already initialised");
        }
    }
    /**
     * Method called on clicking Change Port in menu bar.
     */
    private void commandChangePort(){
        PortInput pi = new PortInput(getConfig().getServerPort());
        Object[] obj = {"Port:",pi};
        int opt = JOptionPane.showConfirmDialog(gui,obj,"Switching server port",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(opt == JOptionPane.OK_OPTION){
            commandChangePort(pi.getPort());
        }
    }
    /**
     * Method called as command through console input
     */
    private void commandChangePort(int param){
        switchPort(param);
    }
    
    /**
     * Method called on clicking restart in menu bar or as command through console input
     */
    private void commandRestart(){
        try{
            restart();
        }catch(Exception ex){}
    }
    
    private static final String COMMAND_INITIALISE = "initialise";
    private static final String COMMAND_PORT = "port";
    private static final String COMMAND_RESTART = "restart";
    private static final String COMMAND_SHUTDOWN = "shutdown";
    private static final String COMMAND_EXIT = "exit";
    private static final String COMMAND_QUIT = "quit";
    
    /**
     * Returns help message for server.
     * @param simple Whether the returned message is short and simple or detailed.
     * @return help message
     */
    private String getHelp(boolean simple){
        String rtrn = "";
        if(!simple)
            rtrn += "All commands can be typed into console or chosen from File menu.\n"
                    + "If command is available only through console then #console#\n"
                    + " tag is added before command name, similarly with Menu Bar commands\n."
                    + "Commands with different names but same result are divided with | in one line.\n"
                    + "Console commands need special parameters only when its specified\n"
                    + "with [name::type] tag where type is data type of argument \n"
                    + "(could be number[ie. 10, 15 etc.], text[ie. Aaa, Y etc.] or logical[TRUE or FALSE].\n"
                    + "only group of parameters within ?(  )? block are noncompulsory,\n"
                    + "but either all or none must be given from one block.\n"
                    + "For example with given command: \n"
                    + "CMD [s::text] ?( [a1::text] [a2::text] )? ?( [l1::number] [l2::number] )? \n"
                    + "You can only invoke it correctly with these calls:\n"
                    + "CMD compulsory_text\n"
                    + "CMD compulsory_text Text#1 Text#2 100 200\n"
                    + "CMD compulsory_text Text#1 Text#2\n"
                    + "CMD compulsory_text 100 200\n";
        rtrn +="Available commands: ";
        rtrn +="\nInitialise";
        if(!simple)
            rtrn+=" - Initialises lobby server. After it's successfuly done, server is ready to accept hosts.";
        rtrn +="\n#Console# port [port number::number] | #Menu Bar# Change port";
        if(!simple)
            rtrn+=" - Changes port number to given number. On console you need to specify number of port after command. Through menu bar ";
        rtrn +="\nRestart";
        if(!simple)
            rtrn+=" - Tries to restart server. After it's successfuly done, server is ready to accept hosts.";
        rtrn +="\n#Console# Shutdown | Exit | #Console# Quit";
        if(!simple)
            rtrn+=" - Disconnects server and exits the program."; 
        rtrn +="\n#Console# AddRoom ?([room name::text])? ?([AI as black::logical] [AI as white::logical])?";
        if(!simple)
            rtrn+=" - Creates new game room with specified name ";      
        return rtrn;
        
    }
    /**
     * Method called on clicking shutdown in menu bar or as command through console input
     */
    private void commandShutdown(){
        try{
            dispose();
            gui.dispose();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(gui, "Error while closing server:"+ex.getLocalizedMessage(), "ERROR",JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }
    
    
    
    
    
    /**
     * Default constructor. Server needs initiated after.
     */
    public GUIServer(){
        super("");
         gui = new BasicFrame("GoServer",null,"Server"){
            @Override
            public void getHelp() {
                String str = GUIServer.this.getHelp(false);
                JOptionPane.showMessageDialog(this, str);
            }
             
            @Override
            public boolean shouldExit() {
                commandShutdown();
                return true;
            }
        };
        input = new JTextField();
        input.addActionListener(new AbstractAction("") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!input.getText().trim().isEmpty()){
                    interpret(input.getText());
                }
                input.setText("");
            }
        });
        input.setEditable(true);
        output = new JEditorPane("text/html","");
        JScrollPane output_scroll = new JScrollPane(output);
        output.setEditable(false);
        System.setOut(new PrintStream(new Output(System.out,output,false),true));
        System.setErr(new PrintStream(new Output(System.err,output,true),true));
        
        gui.getContainer().add(output_scroll,BorderLayout.CENTER);
        gui.getContainer().add(input,BorderLayout.SOUTH);
        gui.setVisible(true);
        gui.setEnabled(true);
        
        initialise = new JMenuItem(new AbstractAction("Initialise") {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandInitialise();
            }
        });
        change_port = new JMenuItem(new AbstractAction("Change port") {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandChangePort();
            }
        });
        restart = new JMenuItem(new AbstractAction("Restart") {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandRestart();
            }
        });
        
        gui.addMenuItem("File", change_port, 0);
        gui.addMenuItem("File", restart, 0);
        gui.addMenuItem("File", initialise, 0);
        
    }
    
    /**
     * Changes port on which server works. Restarts server in the process.
     * @param port new port to change to. must be within [0,65535] bounds
     * @throws IllegalArgumentException when port is outside legal bound.
     */
    public void switchPort(int port){
        if((port>=0)&&(port<=65535)){
            int old = getConfig().getServerPort();
            getConfig().setServerPort(port);
            if(isWorking()){
                try {
                    restart();
                } catch (Exception ex) {
                    if(isWorking()){
                        printErr("Coudln't switch ports. Server works on old one.");
                        getConfig().setServerPort(old);
                    }else{
                        printErr("Coudln't switch ports. Server stopped working.");
                    }
                }
            }
        }
    }
    

    @Override
    public void interpret(String command, String... parameters) {
        String s = command;
        for (String parameter : parameters) {
            if(!parameter.trim().isEmpty())
                s+=" [" + parameter + "]";
        }
            
        printInput(s);
    }

    @Override
    public void interpret(String input) {
        input=input.trim();
        String cmd = "";
        if(!input.isEmpty()){
            String[] s = input.split(" ");
            if(s.length>1){
                ArrayList<String> list = new ArrayList<>();
                for(int i=1;i<s.length;i++){
                    int old = i;
                    if(s[i].startsWith("\"")){
                        s[old] = s[old].substring(1);
                        i++;
                        for(;i<s.length;i++){
                            s[old]+= " "+s[i];
                            if(s[i].endsWith("\"")){
                                s[old] = s[old].substring(0,s[old].length()-1);
                                break;
                            }
                        }
                        
                    }
                    list.add(s[old]);
                }
                interpret(s[0], list.toArray(new String[list.size()]));
            }else if (s.length==1){
                interpret(s[0],"");
            }
        }
    }
    
    
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            try {
                GUIServer s = new GUIServer();
                s.commandInitialise();
            } catch (Exception ex) {
                System.err.println("Exception:"+ex.getLocalizedMessage());
                //System.exit(-1);
            }
        
        });
        
    }


}
