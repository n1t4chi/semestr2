/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frame;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Window class. Adds input and output fields and basic menu.
 * @author n1t4chi
 */
public abstract class Window extends JFrame{  
    /**
     * Menu bar.
     */
    final public JMenuBar menu_Bar;
    /**
     * File menu
     */
    final public JMenu menu_File;
    /**
     * File menu item. Exit.
     */
    final public JMenuItem menu_File_Exit;
    
    
    /**
     * Tabbed panel for output
     */
    final public JTabbedPane outputPanel;
    /**
     * Scroll for output.
     */
    final public JScrollPane consoleOutputScroll;
    /**
     * Tree container.
     */
    final private JScrollPane treePanelScroll;
    /**
     * Tree container.
     */
    final public JPanel treePanel;
    /**
     * Text field displaying output.
     */
    final public JTextPanel consoleOutput;
    /**
     * Text field for input.
     */
    final public JTextField consoleInput;
    /**
     * Window Window_Configuration
     */
    final public Window_Configuration win_config;

    final private ArrayList<String> CommandList;




//#########################Methods##############################################
    /**
     * Exits the program.
     */
    public void Exit(){  
        win_config.saveToFile();
        doThisOnClose();
        System.exit(0);
    }
            
            
            
            
            






//#########################Constructors#########################################
            
    /**
     * Default constructor.
     * @param title Title of a window
     */
    public Window(String title){ 
        this(title,Window_Configuration.DEFAULT_EXTENSION);
    }
    /**
     * Constructor.
     * @param title Title of a window
     * @param configExtension Extension of a window configuration file
     */
    public Window(String title,String configExtension){ 
        outputPanel = new JTabbedPane();
        win_config = new Window_Configuration(true,configExtension);
        consoleInput = new JTextField();
        consoleOutput = new JTextPanel();
        consoleOutputScroll = new JScrollPane(consoleOutput);
        consoleOutputScroll.setAutoscrolls(true);
        treePanel = new JPanel(); //Scroll
        treePanel.setLayout(new BorderLayout());
        treePanelScroll = new JScrollPane(treePanel);
        treePanelScroll.getVerticalScrollBar().setUnitIncrement(16);
        CommandList = new ArrayList<>();
        
        menu_Bar = new JMenuBar();
        menu_File = new JMenu("File");
        menu_File_Exit = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Exit();
            }
        });
        menu_File.add(new JSeparator());
        menu_File.add(menu_File_Exit);
        menu_Bar.add(menu_File);
        
        
        setMinimumSize(win_config.MINIMUM_WINDOW_SIZE);
        setPreferredSize(win_config.windowSize);
        setLocation(win_config.windowLocation);     
        setLayout(new BorderLayout());
        setTitle(title);
        outputPanel.add("Console", consoleOutputScroll);
        outputPanel.add("Tree", treePanelScroll);   
        //treePanel.setLayout(null);
        add(outputPanel,BorderLayout.CENTER);
        add(consoleInput,BorderLayout.PAGE_END);
        add(menu_Bar,BorderLayout.PAGE_START);
        consoleInput.addKeyListener(new KeyAdapter() {
            int command = -1;
            @Override
            public void keyPressed(KeyEvent e) {
                //System.err.println("client.Client.Execute() -3");
                //System.err.println("client.Client.Execute() key:"+e.getKeyCode()+" == "+KeyEvent.VK_ENTER);
                switch(e.getKeyCode()){
                    case KeyEvent.VK_ENTER:
                            //System.err.println("client.Client.Execute() -2");
                            if(!consoleInput.getText().replaceAll(" ", "").equals("")){
                                //System.err.println("client.Client.Execute() -1.5");
                                CommandList.add(consoleInput.getText());
                                System.out.println(consoleInput.getText());
                                if(consoleInput.getText().equalsIgnoreCase("clear"))
                                    consoleOutput.setText("");
                                else
                                    doThisOnInput(consoleInput.getText());      
                                consoleInput.setText("");
                                command = -1;
                            }    
                        break;
                    case KeyEvent.VK_KP_UP: 
                    case KeyEvent.VK_UP:
                            if(command<CommandList.size()-1){
                                command++;
                                consoleInput.setText(CommandList.get(CommandList.size()-command-1));
                            }
                            
                        break;
                    case KeyEvent.VK_KP_DOWN: 
                    case KeyEvent.VK_DOWN:
                            if(command>-1){
                                command--;
                                if(command>-1)
                                    consoleInput.setText(CommandList.get(CommandList.size()-command-1));
                                else
                                    consoleInput.setText("");
                            }                                           
                        break;
                        
                        
                }    
                
            }
           
        });
        PrintStream o;
        try {
            o = new PrintStream(new Output( consoleOutput,false),true,"UTF-16");
            System.setOut(o);
            o = new PrintStream(new Output( consoleOutput,true),true,"UTF-16");
            System.setErr(o);
        } catch (UnsupportedEncodingException ex) {
            System.err.println(" There has been a problem on redirecting error to GUI console. Messages will be displayed in terminal.");
        }    
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                win_config.windowSize = getSize();
            }
            @Override
            public void componentMoved(ComponentEvent e) {
                win_config.windowLocation = getLocation();
            }           
        });
        
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Exit();
            }     
        });
        pack();
    }
    
    






//#########################overriden methods####################################
    
    /**
     * This method is executed when window is being closed.
     */
    public abstract void doThisOnClose();

    /**
     * This method is executed when text was typed.
     * @param input Text that was typed.
     */
    public abstract void doThisOnInput(String input);
    
}
