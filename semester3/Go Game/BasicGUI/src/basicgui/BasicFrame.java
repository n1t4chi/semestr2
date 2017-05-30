/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicgui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import javax.swing.*;
import util.ConfigWindow;

/**
 * Basic frame. Provides default menu bar with simple ability to add own menus.
 * Content that will be displayed by this frame should be placed into desiganted 
 * container by invoking {@link BasicFrame#getContainer() }. Default one is JPanel with BorderLayout activated.
 * Container can be changed with {@link BasicFrame#setContainer(javax.swing.JComponent)  }
 * Basic frame default container uses BorderLayout with Center position taken by designated container.
 * You can still place some components like status bar into frame itself.
 * <br>
 * BasicFrame provides methods that should be overridden by subclasses<br>
 * 
 * -{@link BasicFrame#shouldExit() } which is called after user wants to exit application,
 * either form menu or by clicking exit button on window. 
 * Normally it returns true at all times. It is advised to override this method if some things should be performed beforehand<br>
 * 
 * -{@link BasicFrame#getAbout() } returns text displayed under Help-About menu item.
 * Normally gives program name and creators.<br>
 * 
 * -{@link BasicFrame#getHelp() }  is performed when user clicks Help-Help menu item.
 * Normally opens popup and provides simple info about use of menu. It is advised to override this method and provide real help to user.<br>
 * 
 * Menu Item keys are of format [menu#1].[submenu#1]. ... .[menu item]
 * @author n1t4chi
 */
public class BasicFrame extends JFrame{
    /**
     * Menu bar.
     */
    private final JMenuBar menu_bar;
    /**
     * List of menus.
     */
    private final HashMap<String,JMenuItem> menu_list;
    /**
     * Container on which content should be put to.
     */
    private JComponent container;
    /**
     * Window configuration.
     */
    private final ConfigWindow config;
    /**
     * Special suffix for window configuration file.
     */
    private final String WindowConfigFileSuffix;
    
    /**
     * Default Constructor. Loads configuration from file on start. 
     * with {@link ConfigWindow#getDefaultConfig() )}
     * 
     * @param Name Name of this frame.
     * @param icon Icon for this application;
     * @param WindowConfigFileSuffix Suffix added to configuration file.
     * @throws HeadlessException 
     */
    public BasicFrame(String Name,Image icon,String WindowConfigFileSuffix) throws HeadlessException {
        this(Name,icon,null,WindowConfigFileSuffix);
    }
    /**
     * Constructor. Set ups configuration from given object.
     * @param Name Name of this frame.
     * @param icon Icon for this application;
     * @param config Window configuration.
     * @throws HeadlessException 
     */
    public BasicFrame(String Name,Image icon,ConfigWindow config,String WindowConfigFileSuffix) throws HeadlessException {
        if(config == null){
            this.config = new ConfigWindow();
            this.config.load(WindowConfigFileSuffix);
        }else{
            this.config=config;
        }
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.WindowConfigFileSuffix = WindowConfigFileSuffix;
        this.menu_bar = new JMenuBar();
        this.menu_list = new HashMap<>();
        init(Name,icon);
    }
    
    
    /**
     * Initiates this object. Called from main constructor.
     * @param Name Name of this frame.
     * @param icon Icon for this application;
     */
    private void init(String Name,Image icon){
        this.setVisible(false);
        this.setName(Name);
        this.setTitle(Name);
        this.setIconImage(icon);
        this.setMinimumSize(config.getWindowMinimumSize());
        this.setPreferredSize(config.getWindowPreviousSize());
        this.setSize(this.getPreferredSize());
        this.setLocation(config.getWindowPreviousLocation());
        
        
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        this.setJMenuBar(menu_bar);
        
        
        
        
        //this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);  
        
        
        this.addMenu("File");
        this.addSeparator("File");
        this.addMenuItem("File", new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        }));
        this.addMenu("Help");
        this.addMenuItem("Help", new JMenuItem(new AbstractAction("Help") {
            @Override
            public void actionPerformed(ActionEvent e) {
                getHelp();
            }
        }));
        this.addSeparator("Help");
        this.addMenuItem("Help", new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Image img = BasicFrame.this.getIconImage();
                ImageIcon icon = null;
                if(img!=null)
                    icon = new ImageIcon(img);

                JOptionPane.showMessageDialog(rootPane,getAbout(), "About "+ BasicFrame.this.getName(),JOptionPane.INFORMATION_MESSAGE, icon);
            }
        }));
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
                
            }
        });   
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                config.setWindowPreviousSize(BasicFrame.this.getSize());
            }
            
            @Override
            public void componentMoved(ComponentEvent e) {
                
                config.setWindowPreviousLocation(BasicFrame.this.getLocation());
            }
            
        });
        this.setContainer(new JPanel(new BorderLayout()));
        //this.revalidate();
        //this.repaint();
    }
    /**
     * Returns String to display in Help.About menu
     * @return string to display
     */
    public String getAbout(){
        return "<html>"
                + "<font size=\"4\">"+this.getName()+"</font><br><br>"
                + ""
                + "Made by Kinga Pachla and Piotr Olejarz "
                + ""
                + "</html>";
    }
    
    public void getHelp(){
        JOptionPane.showMessageDialog(rootPane,"Menu bar provides mostly essential operations under File menu or provide some information under Help menu.\n"
                + "Actual content of this application is provided just below the menu bar.\n", "About "+ this.getName(),JOptionPane.INFORMATION_MESSAGE);   
    }
    
    /**
     * Shutdowns program.
     */
    public void exit(){
        if(shouldExit()){
            config.save(WindowConfigFileSuffix);
            this.dispose();
        }
    }
    /**
     * This method is called right before program is set to be shutdown by user.
     * Returns whether shutdown should be performed or not.
     * If program should exit then this method should perform necessary methods before the application quits.
     * @return Whether this program will exit or not.
     */
    public boolean shouldExit(){
        return true;
    }
    
    
    /**
     * Returns container on which content to display should be put to.
     * @return 
     */
    public final JComponent getContainer(){
        return this.container;
    }
    
    /**
     * Set ups new container
     * @param container new container.
     * @throws NullPointerException When container is null
     */
    public final void setContainer(JComponent container){
        if(container == null)
            throw new NullPointerException("Null container");
        //System.out.println("basicgui.BasicFrame.setContainer() adding:"+container);
        this.remove(container);
        this.add(container,BorderLayout.CENTER);
        container.setVisible(true);
        this.container = container;
    }
    
    
    /**
     * Returns menu item under given key. Can receive menu.
     * Keys are format [submenu#1].[submenu#2]. ... .[MenuItem]
     * @param key Key identifying menu. 
     * @return menu item. Null if there is no menu item under given key.
     */
    public JMenuItem getMenuItem(String key) {
        return menu_list.get(key);
    }
    
    /**
     * Adds separator to specified menu
     * @param menu 
     * @param index Non negative integer or -1.
     * @throws IllegalArgumentException when there is no such menu
     * @throws NullPointerException if menu is null pointer.
     */
    public void addSeparator(String menu,int index){
        if( (menu == null) ){
            throw new NullPointerException("Null argument");
        }
        if( (menu.isEmpty())||(!menu_list.containsKey(menu))  ){
            throw new IllegalArgumentException("No such menu");
        }
        menu_list.get(menu).add(new JSeparator(),index);
    }
    /**
     * Adds separator to specified menu
     * @param menu 
     * @throws IllegalArgumentException when there is no such menu
     * @throws NullPointerException if menu is null pointer.
     */
    public void addSeparator(String menu){
        if( (menu == null) ){
            throw new NullPointerException("Null argument");
        }
        if( (menu.isEmpty())||(!menu_list.containsKey(menu))  ){
            throw new IllegalArgumentException("No such menu");
        }
        menu_list.get(menu).add(new JSeparator());
    }
    /**
     * Adds menu to menu bar at the end.
     * @param menu Menu to add.
     * @throws IllegalArgumentException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     * @throws NullPointerException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     */
    public void addMenu(JMenu menu){
        addSubMenu("",menu, -1);
    }
    /**
     * Adds menu to menu bar with specified menu_name at the end.
     * @param menu_name Name of menu to add.
     * @throws IllegalArgumentException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     * @throws NullPointerException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     */
    public void addMenu(String menu_name){
        addSubMenu("",menu_name, -1);
    }    
    /**
     * Adds submenu to specified menu within menu bar with specified menu_name at the end.
     * @param menu Name of menu to add submenu to. If menu is empty test submenu will be added onto main bar.
     * @param menu_name Name of menu to add.
     * @throws IllegalArgumentException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     * @throws NullPointerException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     */
    public void addSubMenu(String menu,String menu_name){
        addSubMenu(menu,new JMenu(menu_name), -1);
    }
    /**
     * Adds submenu to specified menu within menu bar at the end.
     * @param menu Name of menu to add submenu to. If menu is empty test submenu will be added onto main bar.
     * @param submenu Menu to add.
     * @throws IllegalArgumentException When either submenu with same name exists, specified menu does not exist or submenu name is empty.
     * @throws NullPointerException When menu or submenu is null pointer.
     */
    public void addSubMenu(String menu,JMenu submenu){
        addSubMenu(menu, submenu, -1);
    }
    /**
     * Adds menu to menu bar.
     * @param menu Menu to add.
     * @param index Non negative integer or -1.
     * @throws IllegalArgumentException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     * @throws NullPointerException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     */
    public void addMenu(JMenu menu,int index){
        addSubMenu("",menu,index);
    }
    /**
     * Adds menu to menu bar with specified menu_name.
     * @param menu_name Name of menu to add.
     * @param index Non negative integer or -1.
     * @throws IllegalArgumentException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     * @throws NullPointerException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     */
    public void addMenu(String menu_name,int index){
        addSubMenu("",menu_name,index);
    }    
    /**
     * Adds submenu to specified menu within menu bar with specified menu_name.
     * @param menu Name of menu to add submenu to. If menu is empty test submenu will be added onto main bar.
     * @param menu_name Name of menu to add.
     * @param index Non negative integer or -1.
     * @throws IllegalArgumentException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     * @throws NullPointerException as specified by {@link BasicFrame#addSubMenu(java.lang.String, javax.swing.JMenu)}
     */
    public void addSubMenu(String menu,String menu_name,int index){
        addSubMenu(menu,new JMenu(menu_name),index);
    }
    /**
     * Adds submenu to specified menu within menu bar.
     * @param menu Name of menu to add submenu to. If menu is empty test submenu will be added onto main bar.
     * @param submenu Menu to add.
     * @param index Non negative integer or -1.
     * @throws IllegalArgumentException When either submenu with same name exists, specified menu does not exist or submenu name is empty.
     * @throws NullPointerException When menu or submenu is null pointer or index is invalid.
     */
    public void addSubMenu(String menu,JMenu submenu,int index){
 /*       if( (menu == null)||(submenu == null) ){
            throw new NullPointerException("Null arguments");
        }
        submenu.setText(submenu.getText().trim().replace("  "," "));
        if( submenu.getText().isEmpty() ){
            throw new IllegalArgumentException("Empty menu name");
        }
        if( menu_list.containsKey(submenu.getText()) ){
            throw new IllegalArgumentException("Duplicated menu name");
        }
        if( (!menu.isEmpty())&&(!menu_list.containsKey(menu))  ){
            throw new IllegalArgumentException("No such menu");
        }
        if( (!menu.isEmpty())&&(menu_list.containsKey(menu))&&(!(menu_list.get(menu) instanceof JMenu))  ){
            throw new IllegalArgumentException("Not a menu");
        }
        
        menu_list.put( menu+(("".equals(menu))?"":".")+submenu.getText(), submenu);
        if(menu.isEmpty())
            menu_bar.add(submenu,index);
        else
            menu_list.get(menu).add(submenu,index);*/
        addMenuItem(menu, submenu, index);
    }
    
    /**
     * Adds menu item to specified menu at the end. 
     * @param menu Name of menu to add item to. Can be empty and then menu item will be added to menu bar but it is not advised to do so.
     * @param item Menu item.
     * @throws NullPointerException When menu or item are null pointers.
     * @throws IllegalArgumentException When menu does not exist.
     */
    public void addMenuItem(String menu,JMenuItem item){
        addMenuItem(menu, item,-1);
    }
    /**
     * Adds menu item to specified menu. 
     * @param menu Name of menu to add item to. Can be empty and then menu item will be added to menu bar but it is not advised to do so.
     * @param item Menu item.
     * @param index Non negative integer or -1.
     * @throws NullPointerException When menu or item are null pointers.
     * @throws IllegalArgumentException When menu does not exist or index is invalid.
     */
    public void addMenuItem(String menu,JMenuItem item,int index){
        if( (menu == null)||(item == null) ){
            throw new NullPointerException("Null arguments");
        }
        item.setText(item.getText().trim().replace("  "," "));
        if( item.getText().isEmpty() ){
            throw new IllegalArgumentException("Empty menu name");
        }
        if( (!menu.isEmpty())&&(!menu_list.containsKey(menu))  ){
            throw new IllegalArgumentException("No such menu");
        }
        if( (!menu.isEmpty())&&(menu_list.containsKey(menu))&&(!(menu_list.get(menu) instanceof JMenu))  ){
            throw new IllegalArgumentException("Not a menu");
        }
        if( menu_list.containsKey(menu+(("".equals(menu))?"":".")+item.getText()) ){
            throw new IllegalArgumentException("Duplicated menu name");
        }
    /*    boolean bad=false;
        //System.err.println("\n\nChecking "+menu+"/"+item.getText());
        for (Component c : menu_obj.getMenuComponents()){
            if(c instanceof JMenuItem){   
                bad = item.getText().equals(((JMenuItem)c).getText());
                //System.err.println("Comparing "+((JMenuItem)c).getText()+" == "+item.getText()+" = "+bad );
                if(bad) break;
            }
        }
        if(bad){
            throw new IllegalArgumentException("Duplicated menu item name");
        }*/
        
        JMenu menu_obj = (JMenu) menu_list.get(menu);
        menu_list.put( menu+(("".equals(menu))?"":".")+item.getText(), item);
        if(menu.isEmpty())
            menu_bar.add(item,index);
        else
            menu_obj.add(item,index);
    }

    
    
}
