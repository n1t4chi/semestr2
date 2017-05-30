package window;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import figures.Ellipse;
import figures.Figure;
import figures.FigureException;
import figures.Polygon;
import figures.Quadrilateral;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import main.IconLoader;
import main.Text;

/**
 * Class of GUI window that allows user to draw figures and modify them after.
 * @author n1t4chi
 */
public class Window extends JFrame{  
    
   //##########################static context###################################
    /**
     * Translates MouseEvent so {@link MouseEvent#getX() } and {@link MouseEvent#getY() } will be returning XY position moved by vector(dx,dy)
     * @param e Mouse event to translate.
     * @param dx X parameter
     * @param dy Y parameter
     * @return Mouse event with moved XY coordinates.
     */
    public static MouseEvent translateME(MouseEvent e,int dx, int dy){
        return new MouseEvent(e.getComponent(),e.getID(),e.getWhen(),e.getModifiers(),dx+e.getX(),dy+e.getY(),e.getXOnScreen(),e.getYOnScreen(),e.getClickCount(),e.isPopupTrigger(),e.getButton());      
    }
    /**
     * Extension of save files containing figures.
     */
    public static final String SAVE_FILE_EXTENSION = "fig_save";
    /**
     * For point tab when creating figures. It gives X coordinates.
     */
    public static final int X = 0;
    /**
     * For point tab when creating figures. It gives T coordinates.
     */
    public static final int Y = 1;




   //##########################fields###########################################
    
    
    
    
    /**
     * Bar that displays some basic information.
     */
    private final StatusPanel status_bar;
    /**
     * Scroll for {@link #container}.
     */
    private final javax.swing.JScrollPane container_scroll;
    /**
     * Container for figures.
     */
    public final FigurePanel container;
    /**
     * Configuration of window.
     */
    public final Configuration config;
    /**
     * List of menu items and submenus.
     */
    public final ArrayList menu_list;
    /**
     * List of figures in container.
     */
    private final ArrayList figure_list;
    /**
     * List of components within window.
     */
    private final ArrayList comp_list;
    /**
     * Menu bar
     */
    private final javax.swing.JMenuBar menu_bar;
    /**
     * Main menu for window and figure commands.
     */   
    private final javax.swing.JMenu menu_file;
    /**
     * Menu item for clearing screen.
     */
    private final javax.swing.JMenuItem menu_file_clear;
    /**
     * Menu item for saving figures.
     */
    private final javax.swing.JMenuItem menu_file_save;
    /**
     * Menu item for loading figures.
     */
    private final javax.swing.JMenuItem menu_file_load;
    /**
     * Menu item for adding new figure.
     */
    private final javax.swing.JMenuItem menu_file_new;
    /**
     * Menu item for exiting the program
     */
    private final javax.swing.JMenuItem menu_file_exit;
    
    /**
     * Menu for options and configuration
     */   
    private final javax.swing.JMenu menu_config;
    /**
     * Submenu of configuration menu, responsible for figures
     */   
    private final javax.swing.JMenu menu_config_figure;
    /**
     * Menu item for changing default figure fill colour.
     */
    private final javax.swing.JMenuItem menu_config_figure_fillColour;
    /**
     * Menu item for changing default figure border colour.
     */
    private final javax.swing.JMenuItem menu_config_figure_borderColour;
    /**
     * Menu item for changing default figure border thickness.
     */
    private final javax.swing.JMenuItem menu_config_figure_borderThickness;
    
    /**
     * Submenu of configuration menu, responsible for window and components options.
     */   
    private final javax.swing.JMenu menu_config_window;
    /**
     * Menu item for changing colour of background.
     */
    private final javax.swing.JMenuItem menu_config_window_background;
    /**
     * Menu item for changing grid lines policy.
     */
    private final javax.swing.JCheckBoxMenuItem menu_config_window_grid;
    /**
     * Menu item for changing border of components policy.
     */
    private final javax.swing.JCheckBoxMenuItem menu_config_window_border;
    /**
     * Menu item for resetting current visible rectangle.
     */
    private final javax.swing.JMenuItem menu_config_window_reset;
    /**
     * Menu item for resetting configuration file.
     */
    private final javax.swing.JMenuItem menu_config_window_config_reset;
    /**
     * Menu item changing language.
     */
    private final javax.swing.JMenuItem menu_config_window_language;
    
    /**
     * Submenu of configuration menu, responsible for font options.
     */   
    private final javax.swing.JMenu menu_config_font;
    
    /**
     * Menu item for changing font colour.
     */
    private final javax.swing.JMenuItem menu_config_font_colour;
    /**
     * Menu item for changing font size.
     */
    private final javax.swing.JMenuItem menu_config_font_size;
    /**
     * Menu item for changing font family.
     */
    private final javax.swing.JMenuItem menu_config_font_type;
    /**
     * Submenu of configuration menu, responsible for help options
     */
    private final javax.swing.JMenu menu_help;
    /**
     * Menu item for showing helpful information.
     */
    private final javax.swing.JMenuItem menu_help_help;
    /**
     * Menu item for showing information about this application.
     */
    private final javax.swing.JMenuItem menu_help_about;
    
    
    /**
     * Old size of window.
     */
    private Dimension Old_Size;
    /**
     * Text object.
     */
    public final Text t;
    
    
    
    
    /**
     * Icon loader object.
     */
    IconLoader icons;
    
    
    /**
     * Toolbar for creating figures.
     */
    private final javax.swing.JToolBar toolbar;
    /**
     * Toolbar action for creating polygons.
     */
    private final javax.swing.Action toolbar_Polygon;
    /**
     * Toolbar action for creating circles.
     */
    private final javax.swing.Action toolbar_Circle;
    /**
     * Toolbar action for creating ellipses.
     */
    private final javax.swing.Action toolbar_Ellipse;
    /**
     * Toolbar action for creating quadrilaterals.
     */
    private final javax.swing.Action toolbar_Quadrilateral;
    /**
     * Toolbar action for creating parallelograms.
     */
    private final javax.swing.Action toolbar_Parallelogram;
    /**
     * Toolbar action for creating rhombs.
     */
    private final javax.swing.Action toolbar_Rhomb;
    /**
     * Toolbar action for creating rectangles.
     */
    private final javax.swing.Action toolbar_Rectangle;
    /**
     * Toolbar action for creating squares.
     */
    private final javax.swing.Action toolbar_Square;
    /**
     * Toolbar button for border colour.
     */
    private final javax.swing.JButton toolbar_Border_Colour;
    /**
     * Toolbar button for border thickness.
     */
    private final javax.swing.JSpinner toolbar_Border_Thickness;
    /**
     * Toolbar button for fill colour.
     */
    private final javax.swing.JButton toolbar_Fill_Colour;
    


   //#########################methods###########################################
    
    
    
    /**
     * Returns index of specified menu item.
     * @param comp component to find its index
     * @return  index of component, or -1 if there is no such component in array
     */
    private int indexMenu(Component comp){
        return menu_list.indexOf(comp);
    }
    /**
     * Returns index of specified figure.
     * @param comp component to find its index
     * @return index of component, or -1 if there is no such component in array
     */
    private int indexFigure(Component comp){
        return figure_list.indexOf(comp);
    }
    /**
     * Returns index of specified component.
     * @param comp  component to find its index
     * @return  index of component, or -1 if there is no such component in array
     */
    private int indexComponent(Component comp){
        return comp_list.indexOf(comp);
    }
    
    /**
     * This method sets size of components added directly to window with same proportions.
     * Respects minimum and maximum sizes.
     */
    public void initSize(){       
        Dimension new_size = this.getAccessibleContext().getAccessibleComponent().getSize();
        for (Object object : comp_list) {
            if(object instanceof JComponent){
                Dimension old_cmp_size = ((JComponent) object).getPreferredSize();
                Dimension min_size = ((JComponent) object).getMinimumSize();
                Dimension max_size = ((JComponent) object).getMaximumSize();
                int new_width=(old_cmp_size.width==Old_Size.width)?new_size.width:((int)(((double)old_cmp_size.width/Old_Size.width)*new_size.width));
                int new_height=(old_cmp_size.height==Old_Size.height)?new_size.height:((int)(((double)old_cmp_size.height/Old_Size.height)*new_size.height));
                new_width = (new_width<min_size.width)?min_size.width:((new_width>max_size.width)?max_size.width:new_width);
                new_height = (new_width<min_size.height)?min_size.height:((new_width>max_size.height)?max_size.height:new_height);
                ((JComponent) object).setPreferredSize(new Dimension(new_width,new_height));              
            }
        }
        Old_Size = new_size;
    }
    /**
     * Initilizes or reinitializes fonts.
     */
    public void initFont(){
        for (Object o : menu_list) {
            if( o instanceof JMenu ){
                updateMenu((JMenu) o);
            }  
            if( o instanceof JMenuItem){
                ((JMenuItem) o).setFont(config.getFont());
                ((JMenuItem) o).setForeground(config.getFontColour());
            }
            
        }
        status_bar.updateFontStyle();
    }
    /**
     * Initilizes or reinitializes background colour.
     */
    public void initBackGroundColour(){
        container.setBackground(config.getBackGroundColour());
    }
    /**
     * Initilizes or reinitializes borders of components.
     */
    public void initBorder(){    
       /* for (Object object : comp_list) {
            if(object instanceof JComponent){
                if(config.shouldDrawBorder()){       
                    ((JComponent) object).setBorder(new javax.swing.border.LineBorder(new Color(new Random().nextInt()),2));
                }else{
                    ((JComponent) object).setBorder(null);
                }
            }
        }  */  
        for (Object object : figure_list) {
            if(object instanceof JComponent){
                if(config.shouldDrawBorder()){       
                    ((JComponent) object).setBorder(new javax.swing.border.LineBorder(new Color(new Random().nextInt()),1));
                }else{
                    ((JComponent) object).setBorder(null);
                }
            }      
        }    
        status_bar.updateBorderStyle();
    }
    /**
     * Method that closes whole application
     */
    private void exit(){
        int i = JOptionPane.showConfirmDialog(this,t.getText("EXIT_ASK"),t.getText("CONFIRM_TITLE"),JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
        if(i == JOptionPane.YES_OPTION){
            //JOptionPane.showMessageDialog(container, t.getText("EXIT_YES"), t.getText("ANSWER_YES_TITLE"),JOptionPane.INFORMATION_MESSAGE);
            save();
        }
        config.saveToFile();
        if( i != JOptionPane.CANCEL_OPTION ){        
            System.exit(0);
            showCancelDialog();
        }else{
            showCancelDialog();
        }
    }
    /**
     * Method that clears figure container.
     * @param silent Suppress warnings.
     * @return Whether clear was performed successfully or not.
     */
    private boolean clear(boolean silent){
        if(!silent){
            int i = JOptionPane.showConfirmDialog(this,t.getText("CLEAR_ASK"),t.getText("CONFIRM_TITLE"),JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(i == JOptionPane.YES_OPTION){
                container.removeAllFigures();
                JOptionPane.showMessageDialog(this, t.getText("CLEAR_YES"), t.getText("ANSWER_YES_TITLE"),JOptionPane.INFORMATION_MESSAGE);
                return true;
            }else{
                showCancelDialog();
                return false;
            }
        }else{
            container.removeAllFigures();
            return true;
        }
    }
    
    
    /**
     * Method that loads figures from file.
     */
    private void load() {
        int i = JOptionPane.showConfirmDialog(this, t.getText("FILE_LOAD_ASK_TO_SAVE"), t.getText("FILE_LOAD_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(i == JOptionPane.YES_OPTION){
            save();
        }
        
        if(i != JOptionPane.CANCEL_OPTION){
            if(clear(true)){
                JFileChooser c = new JFileChooser();
                c.setFileFilter(new FileNameExtensionFilter( t.getText("SAVE_FILE_DESC"),SAVE_FILE_EXTENSION ));
                c.setDialogType(JFileChooser.OPEN_DIALOG);
                c.setFileSelectionMode(JFileChooser.FILES_ONLY);
                File fil=null;
                if (c.showOpenDialog(this)==JFileChooser.APPROVE_OPTION ){
                    boolean proceed = true;
                    fil= c.getSelectedFile();  
                    if(!fil.canRead()){
                        proceed = !fil.setReadable(true);
                    }    
                    if(proceed){
                        try {
                            boolean show_error = false;
                            BufferedReader br = new BufferedReader(new FileReader(fil));
                            String lane;
                            while( (lane = br.readLine() )!=null ){
                                Figure f=null;
                                try {
                                    if(Figure.isSaveFormatFromClass( Ellipse.class , lane)){
                                        f = new Ellipse(1, 1, 1, 1, config, t, container);
                                            f.recreateFromSave(lane);
                                    }else{
                                        if(Figure.isSaveFormatFromClass( Polygon.class , lane)){
                                            double[] x={0};
                                            double[] y={0}; 
                                            f = new Polygon(x,y , config, t, container);
                                            f.recreateFromSave(lane);
                                        }else{
                                            if(Figure.isSaveFormatFromClass( Quadrilateral.class , lane)){
                                                double[] x={0,1,2,4};
                                                double[] y={0,1,2,4};
                                                f = new Quadrilateral(x,y , config, t, container);
                                                f.recreateFromSave(lane);
                                            }    
                                        }
                                    }
                                } catch (FigureException ex) {
                                    f=null;
                                    show_error = true;
                                }
                                if(f!=null){
                                    container.addFigure(f);
                                }
                            }
                            br.close();
                            if(show_error){
                                showErrorDialog(t.getText("FILE_LOAD_BAD_FIGURE"));
                            }
                            container.resize();
                            repaint();
                        } catch (IOException ex) {
                            showErrorDialog(t.getText("FILE_CANNOT_LOAD"));
                        }    
                    }else{
                        showErrorDialog(t.getText("FILE_CANNOT_LOAD"));
                    }                     
                }        
            }
        }else{
            showCancelDialog();
        }
    }
    
    /**
     * Method that saves figures to file.
     */
    private void save(){   
        JFileChooser c = new JFileChooser();
        c.setFileSelectionMode(JFileChooser.FILES_ONLY);
        c.setFileFilter(new FileNameExtensionFilter( t.getText("SAVE_FILE_DESC"),SAVE_FILE_EXTENSION ));
        File fil=null;
        if (c.showSaveDialog(this)==JFileChooser.APPROVE_OPTION ){
            fil= c.getSelectedFile();
            if(!fil.getName().endsWith(SAVE_FILE_EXTENSION)){
                fil = new File(fil.toString()+"."+SAVE_FILE_EXTENSION);
            }
            if(!fil.exists()){
                try {
                    fil.createNewFile();
                } catch (IOException ex) {
                    showErrorDialog(t.getText("FILE_CANNOT_SAVE"));
                }
            }
            boolean proceed = true;
            if(!fil.canWrite()){
                proceed=fil.setWritable(true);
            }
            if(proceed){
                PrintWriter pw;
                try {
                    pw = new PrintWriter(fil);

                    for (Object o : figure_list) {
                        if(o instanceof Figure){
                            pw.println(((Figure) o).getSaveableFormat());
                        } 
                    }
                    
                    showMessageDialog(t.getText("FILE_SAVE_SUCCESS"),t.getText("FILE_SAVE_SUCCESS_TITLE"));
                    pw.close();
                } catch (FileNotFoundException ex) {
                    showErrorDialog(t.getText("FILE_CANNOT_SAVE"));
                }  
            }else{
                showErrorDialog(t.getText("FILE_CANNOT_SAVE"));
            }
        }           
    }
    
    /**
     * Shows dialog for input.
     * @param Message Message to be displayed.
     * @param title Title to be displayed.
     * @return Picked value.
     */
    public String showInputDialog(String Message,String title){
        String rtrn = JOptionPane.showInputDialog(this, Message, title, JOptionPane.QUESTION_MESSAGE);
        if(rtrn ==null){
            showCancelDialog();
            return null;
        }else{
            return rtrn;
        }
    }
    
    
    /**
     * Shows message dialog.
     * @param message Message to be displayed.
     * @param title Title of message.
     * 
     */
    public void showMessageDialog(String message,String title){
        JOptionPane.showMessageDialog(this, message, title,JOptionPane.PLAIN_MESSAGE);
    }    
    
    /**
     * Shows dialog for input.
     * @param Message Message to be displayed.
     * @param default_value Default value.
     * @return Picked value.
     */
    public String showInputDialog(String Message,Object default_value){
        String rtrn = JOptionPane.showInputDialog(this, Message,default_value);
        if(rtrn ==null){
            showCancelDialog();
            return null;
        }else{
            return rtrn;
        }
    }
    /**
     * Shows error message
     * @param Message Message to be displayed.
     * @param OptionType Type of options available, see {@link JOptionPane#showConfirmDialog(java.awt.Component, java.lang.Object, java.lang.String, int, int) }
     * @return Picked value.
     */
    public final int showErrorDialog(String Message, int OptionType){
        return JOptionPane.showConfirmDialog(this, Message, t.getText("ERROR_TITLE"), OptionType,JOptionPane.ERROR_MESSAGE);
    }
    /**
     * Shows error message.
     * @param Message Message to be displayed.
     */
    public final void showErrorDialog(String Message){
        JOptionPane.showMessageDialog(this, Message, t.getText("ERROR_TITLE"),JOptionPane.ERROR_MESSAGE);
    }
    /**
     * Shows dialog for positive integer input.
     * @param Message Message to be displayed.
     * @param title Title to be displayed.
     * @param Default_Value Default value.
     * @return Picked value.
     */
    public final int showPositiveIntegerDialog(String Message,String title,int Default_Value){
        return showFixedIntegerDialog(Message, title, Default_Value,1,Integer.MAX_VALUE);
    }
    /**
     * Shows dialog for non negative integer input.
     * @param Message Message to be displayed.
     * @param title Title to be displayed.
     * @param Default_Value Default value.
     * @return Picked value.
     */
    public final int showNonNegativeIntegerDialog(String Message,String title,int Default_Value){
        return showFixedIntegerDialog(Message, title, Default_Value,0,Integer.MAX_VALUE);      
    }
    /**
     * Shows dialog for integer input.
     * @param Message Message to be displayed.
     * @param title Title to be displayed.
     * @param Default_Value Default value.
     * @param Minimum Minimum value.
     * @param Maximum Maximum value.
     * @return Picked value.
     */
    public final int showFixedIntegerDialog(String Message,String title,int Default_Value,int Minimum,int Maximum){
        int rtrn = showIntegerDialog(Message, title, Default_Value);
        if((rtrn<Minimum)||(rtrn>Maximum)){
            int i = showErrorDialog(t.getText("WRONG_INPUT_MESSAGE"),JOptionPane.YES_NO_OPTION );
            if(i==JOptionPane.YES_OPTION){
                rtrn = showFixedIntegerDialog(Message,title,Default_Value,Minimum,Maximum);
            }else{
                showCancelDialog();
                rtrn = Default_Value;
            }
        }
        
        return rtrn;
    }
    /**
     * Shows dialog for integer input.
     * @param Message Message to be displayed.
     * @param title Title to be displayed.
     * @param Default_Value Default value.
     * @return Picked value.
     */
    public final int showIntegerDialog(String Message,String title,int Default_Value){
        int rtrn=0;
        try{
            String s =showInputDialog(Message,Default_Value);
            if(s!=null){
                rtrn = Integer.parseInt(s);
            }else{
                rtrn = Default_Value;
            }        
        }catch(NumberFormatException ex){
            int i = showErrorDialog(t.getText("WRONG_INPUT_MESSAGE"),JOptionPane.YES_NO_OPTION );
            if(i==JOptionPane.YES_OPTION){
                rtrn = showIntegerDialog(Message,title,Default_Value);
            }else{
                showCancelDialog();
                rtrn = Default_Value;
            }
        }     
        return rtrn;
    }
    /**
     * Shows dialog for double input.
     * @param Message Message to be displayed.
     * @param title Title to be displayed.
     * @param Default_Value Default value.
     * @return Picked value.
     */
    public final double showDoubleDialog(String Message,String title,double Default_Value){
        double rtrn=0;
        try{
            String s =showInputDialog(Message,Default_Value);
            if(s!=null){
                s=s.replaceAll(",", ".");
                rtrn = Double.parseDouble(s);
            }else{
                rtrn = Default_Value;
            }        
        }catch(NumberFormatException ex){
            int i = showErrorDialog(t.getText("WRONG_INPUT_MESSAGE"),JOptionPane.YES_NO_OPTION );
            if(i==JOptionPane.YES_OPTION){
                rtrn = showDoubleDialog(Message,title,Default_Value);
            }else{
                showCancelDialog();
                rtrn = Default_Value;
            }
        }     
        return rtrn;
    }
    /**
     * Shows dialog for boolean input and returns picked value.
     * @param Message Message to be displayed.
     * @param def default value.
     * @return new value.
     */
    public final boolean showBooleanDialog(String Message,boolean def){
        boolean rtrn;
        int i = JOptionPane.showConfirmDialog(this, Message, t.getText("BOOLEAN_INPUT_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
        switch(i){
            case JOptionPane.YES_OPTION:
                    rtrn=true;
                break;
            case JOptionPane.NO_OPTION:
                    rtrn=false;
                break;
            default:
                   rtrn=def;
                break;    
        }
        return rtrn;
    }
    /**
     * Shows dialog for cancel message.
     */
    public final void showCancelDialog(){
        JOptionPane.showMessageDialog(this, t.getText("ANSWER_CANCEL_MESSAGE"), t.getText("ANSWER_CANCEL_TITLE"),JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Shows dialog for colour pick and returns picked value.
     * @param current_color Current colour for default return value.
     * @return Chosen colour.
     */
    public final Color showPickColor(Color current_color){
        return showPickColor(current_color, t.getText("COLOUR_PICK_TITLE"));
    }
    /**
     * Shows dialog for colour pick and returns picked value.
     * @param current_color Current colour for default return value.
     * @param title Title of dialog.
     * @return Chosen colour.
     */
    public final Color showPickColor(Color current_color, String title){
        Color rtrn = JColorChooser.showDialog(this,title, current_color);
        if(rtrn==null){
            rtrn = current_color;
            showCancelDialog();
        }else{   
           /* JOptionPane.showMessageDialog(container, 
                    t.getText("COLOUR_PICK_SUCCESS")+" RGBA:("
                    +rtrn.getRed()+","
                    +rtrn.getGreen()+","
                    +rtrn.getBlue()+","
                    +rtrn.getAlpha()+")"
            , t.getText("SUCCESS_TITLE"),JOptionPane.INFORMATION_MESSAGE);*/
        }
        return rtrn;
    }

    
    
    /**
     * Shows dialog for font picking and returns picked value
     * @param Default Default font
     * @return new Font
     */
    public final Font showPickFont(Font Default){     
        Font rtrn = null;
        try{         
            String s = (String)(JOptionPane.showInputDialog(this, t.getText("FONT_PICK_TYPE_MESSAGE"), t.getText("FONT_PICK_TYPE_TITLE"), JOptionPane.QUESTION_MESSAGE, null,javafx.scene.text.Font.getFontNames().toArray(),0));  
            if(s!= null){
                rtrn = (new Font(s, Default.getStyle(), Default.getSize()));              
                JOptionPane.showMessageDialog(this,t.getText("FONT_PICK_TYPE_SUCCESS")+" "+rtrn.getFontName(),t.getText("SUCCESS_TITL"),JOptionPane.INFORMATION_MESSAGE);
            }else{
                rtrn = Default;
                showCancelDialog();
            }
        }catch(NumberFormatException ex){
            int i = showErrorDialog(t.getText("WRONG_INPUT_MESSAGE"),JOptionPane.YES_NO_OPTION);
            if(i==JOptionPane.YES_OPTION){
                rtrn = showPickFont(Default);
            }else{
                rtrn = Default;
            }
        }
        return rtrn;
    }
    /**
     * Updates menu items within menu. Works recursively.
     * @param menu Menu to update.
     */
    public void updateMenu(JMenu menu){
        for( int i=0; i<menu.getItemCount();i++ ){
            JMenuItem it = menu.getItem(i);
            if(it!=null){
                if(it instanceof JMenu){
                    updateMenu((JMenu) it);
                }   
                it.setFont(config.getFont());
                it.setForeground(config.getFontColour());
            }
        }
    }
    /**
     * Updates components so they would suit new configuration of style.
     */
    public void updateStyle(){
        initBorder();
        initBackGroundColour();
        initFont();
        repaint();
    }
    
    /**
     * Changes default figure border colour.
     */
    public void setFigureDefaultBorderColour(){
        config.setDefaultFigureBorderColour(showPickColor(config.getFontColour(),t.getText("PICK_BORDER_COLOUR") ));   
        updateColourButton(false);       
    }
    /**
     * Changes default figure fill colour.
     */
    public void setFigureDefaultFillColour(){
        config.setDefaultFigureFillColour(showPickColor(config.getFontColour(),t.getText("PICK_FILL_COLOUR")));   
        updateColourButton(true);       
    }
    /**
     * Updates specified colour button on toolbar.
     * @param fill If true then fill colour will be updated, otherwise border colour.
     */
    public void updateColourButton(boolean fill){       
        BufferedImage im = new BufferedImage(IconLoader.ICON_SIZE, IconLoader.ICON_SIZE,  BufferedImage.TYPE_INT_ARGB);
        Graphics g = im.getGraphics();
        if(fill)
            g.setColor(config.getDefaultFigureFillColour());
        else
            g.setColor(config.getDefaultFigureBorderColour());
        g.fillRect(0, 0, IconLoader.ICON_SIZE, IconLoader.ICON_SIZE);
        g.finalize();
        if(fill)
            toolbar_Fill_Colour.setIcon( new ImageIcon(im));
        else
            toolbar_Border_Colour.setIcon( new ImageIcon(im));
        
    }
    
    
    
    

   //#########################constructors######################################
    
    /**
     * Constructor for window class.
     * @throws HeadlessException if GUI is not supported.
     */   
    public Window() throws HeadlessException {
        config = new Configuration();
        config.loadFromFile();
        t = new Text(config);
        menu_list = new ArrayList();
        comp_list = new ArrayList();
        figure_list = new ArrayList();
        // System.err.println("old size:"+config.getPreferredWindowSize());
         //System.err.println("old location:"+config.getWindowLocation());
        this.setTitle(Text.APPLICATION_NAME);
        this.setEnabled(true);
        this.setPreferredSize(config.getPreferredWindowSize());
        this.setSize(config.getPreferredWindowSize());
        this.setMinimumSize(config.getMinimumWindowSize());
        this.setLocation(config.getWindowLocation());
        Old_Size = this.getAccessibleContext().getAccessibleComponent().getSize();
        this.setResizable(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);       
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }                  
        });
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                //System.err.println("new location:"+getLocation());
                config.setWindowLocation(getLocation());
                config.saveToFile();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                //System.err.println("new size:"+getSize());
                config.setPreferredWindowSize(getSize());
                config.saveToFile();
            }
            
        });
        
        this.setLayout(new BorderLayout());
        icons = new IconLoader();
        setIconImage(icons.getIcon(IconLoader.ICON_ID_LOGO).getImage());
        //this.addMouseListener(ma);
        //this.addMouseMotionListener(ma);
        //this.addMouseWheelListener(ma);
        
        
        status_bar = new StatusPanel(t,config);
        
        container  = new FigurePanel(figure_list, config,this,status_bar,t);
        container.setOpaque(true);
        container.setVisible(true);
        container.setEnabled(true);
        container.setAutoscrolls(true);
       // container.addMouseListener(ma);
       // container.addMouseMotionListener(ma);
        container_scroll = new JScrollPane(container,ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        container_scroll.setPreferredSize(this.getAccessibleContext().getAccessibleComponent().getSize());      
        container_scroll.setAutoscrolls(true);
        container_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        container_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(container_scroll,BorderLayout.CENTER);
        
        this.add(status_bar,BorderLayout.PAGE_END);             
        menu_file_exit = new JMenuItem(new AbstractAction(t.getText("MENU_FILE_EXIT")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        menu_file_clear = new JMenuItem(new AbstractAction(t.getText("MENU_FILE_CLEAR")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear(false);
            }
        });
        menu_file_new = new JMenuItem(new AbstractAction(t.getText("MENU_FILE_NEW")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                //                              0                   1                   2                         3                         4                       5                       6               7
                Object[] opt = { t.getText("POLYGON"),t.getText("ELLIPSE"),t.getText("CIRCLE"),t.getText("QUADRILATERAL"),t.getText("PARALLELOGRAM"),t.getText("RHOMB"),t.getText("RECTANGLE"),t.getText("SQUARE")  };
                Object pick = JOptionPane.showInputDialog(Window.this, t.getText("PICK_FIGURE_TYPE"), t.getText("PICK_FIGURE_TITLE"), JOptionPane.PLAIN_MESSAGE, null, opt, opt[0]);
                if(pick!=null){
                    int i;
                    for(i=0 ; i<opt.length;i++){
                        if(pick.equals(opt[i])){
                            break;
                        }
                    }
                    int type;
                    switch(i){
                        case 7: type = FigurePanel.FIGURE_TYPE_QUADRILATERAL_SQUARE; break; 
                        case 6: type = FigurePanel.FIGURE_TYPE_QUADRILATERAL_RECTANGLE; break; 
                        case 5: type = FigurePanel.FIGURE_TYPE_QUADRILATERAL_RHOMB; break; 
                        case 4: type = FigurePanel.FIGURE_TYPE_QUADRILATERAL_PARALLELOGRAM; break; 
                        case 3: type = FigurePanel.FIGURE_TYPE_QUADRILATERAL; break; 
                        case 2: type = FigurePanel.FIGURE_TYPE_ELLIPSE_CIRCLE; break; 
                        case 1: type = FigurePanel.FIGURE_TYPE_ELLIPSE; break; 
                        case 0: default: type = FigurePanel.FIGURE_TYPE_POLYGON; break; 
                            
                    }                 
                    container.createFigure(type);
                }else{
                    showCancelDialog();
                }
            }
        });
        menu_file_load = new JMenuItem(new AbstractAction(t.getText("MENU_FILE_LOAD")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });
        menu_file_save= new JMenuItem(new AbstractAction(t.getText("MENU_FILE_SAVE")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });       
        menu_file = new JMenu(t.getText("MENU_FILE"));
        menu_file.add(menu_file_new);
        menu_file.add(new JSeparator());
        menu_file.add(menu_file_load);
        menu_file.add(menu_file_save);
        menu_file.add(new JSeparator());
        menu_file.add(menu_file_clear);
        menu_file.add(menu_file_exit);
        
        
        menu_config_figure_borderColour= new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_FIGURE_BORDER_COLOUR")) {  
            @Override
            public void actionPerformed(ActionEvent e) {
                setFigureDefaultBorderColour();
            }
        });      
        menu_config_figure_borderThickness= new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_FIGURE_BORDER_THICK")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setDefaultFigureBorderThickness(showNonNegativeIntegerDialog(t.getText("FIGURE_BORDER_PICK_THICKNESS"),t.getText("FIGURE_BORDER_PICK_THICKNESS_TITLE"),config.defaultFigureBorderThickness));         
                if(toolbar_Border_Thickness!=null)toolbar_Border_Thickness.setValue(config.defaultFigureBorderThickness);
            }
        });      
        menu_config_figure_fillColour= new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_FIGURE_FILL_COLOUR")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFigureDefaultFillColour();
            }
        });            
        menu_config_figure = new JMenu(t.getText("MENU_CONFIG_FIGURE"));
        menu_config_figure.add(menu_config_figure_borderThickness);
        menu_config_figure.add(menu_config_figure_borderColour);
        menu_config_figure.add(menu_config_figure_fillColour);   
        menu_config_font_colour= new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_FONT_COLOUR")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setFontColour(showPickColor(config.getFontColour()));  
                initFont();
                //updateStyle();
            }
        });    
        menu_config_font_size= new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_FONT_SIZE")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setFontSize(showNonNegativeIntegerDialog(t.getText("FONT_PICK_SIZE_MESSAGE"), t.getText("FONT_PICK_SIZE_TITLE"), config.getFont().getSize()));
            
                initFont();
                //updateStyle();
            }    
        });    
        menu_config_font_type= new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_FONT_TYPE")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setFont(showPickFont(config.getFont()));
                initFont();
                //updateStyle();
            }
        });            
        menu_config_font = new JMenu(t.getText("MENU_CONFIG_FONT"));
        menu_config_font.add(menu_config_font_type);
        menu_config_font.add(menu_config_font_size);
        menu_config_font.add(menu_config_font_colour);
        
        menu_config_window_background= new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_WINDOW_BACKGROUND")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setBackGroundColour(showPickColor(config.getBackGroundColour()));
                initBackGroundColour();
            }
        });     
        
        menu_config_window_grid= new JCheckBoxMenuItem(t.getText("MENU_CONFIG_WINDOW_GRID"));
        menu_config_window_grid.setState(config.getShowGrid());
        menu_config_window_grid.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {             
                config.setDrawGrid(menu_config_window_grid.getState());
                repaint();
            }
        });
        menu_config_window_border = new JCheckBoxMenuItem(t.getText("MENU_CONFIG_WINDOW_BORDER"));
        menu_config_window_border.setState(config.getDrawBorder());
        menu_config_window_border.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {             
                config.setDrawBorder(menu_config_window_border.getState());
                updateStyle();
            }
        });
        
        menu_config_window_reset = new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_WINDOW_RESET")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.scrollRectToVisible(new Rectangle(container.relative_Position.x- FigurePanel.FREE_XY_SPACE ,container.relative_Position.y- FigurePanel.FREE_XY_SPACE,1,1));
            }
        });
        menu_config_window_language = new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_WINDOW_LANGUAGE")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] opt = { "English" , "Polski" };
                Object p = JOptionPane.showInputDialog(Window.this, t.getText("PICK_LANGUAGE"), t.getText("PICK_LANGUAGE_TITLE"), JOptionPane.QUESTION_MESSAGE, null, opt, opt[0]);
                if(opt[1].equals(p)){
                    config.setLanguage(Text.LANGUAGE_POLISH);
                }else{
                    config.setLanguage(Text.LANGUAGE_ENGLISH);
                }
                config.saveToFile();  
            }
        });
        
        menu_config_window_config_reset= new JMenuItem(new AbstractAction(t.getText("MENU_CONFIG_RESET_CONFIG")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Configuration reset = new Configuration();
                config.copy(reset);
                config.saveToFile();
                showMessageDialog(t.getText("RESET_CONFIG"), t.getText("RESET_CONFIG_TITLE"));
                updateColourButton(true);
                updateColourButton(false);
                toolbar_Border_Thickness.setValue(config.defaultFigureBorderThickness);
                setPreferredSize(config.getPreferredWindowSize());
                setMinimumSize(config.getMinimumWindowSize());
                setLocation(config.getWindowLocation());
                updateStyle();
            }
        });
        
        
        menu_config_window = new JMenu(t.getText("MENU_CONFIG_WINDOW"));
        menu_config_window.add(menu_config_window_background);
        menu_config_window.add(menu_config_window_grid);
        menu_config_window.add(menu_config_window_border);
        menu_config_window.add(menu_config_window_reset);
        menu_config_window.add(menu_config_window_language);
        menu_config_window.add(menu_config_window_config_reset);
        
        menu_config = new JMenu(t.getText("MENU_CONFIG"));
        menu_config.add(menu_config_figure);
        menu_config.add(menu_config_font);
        menu_config.add(menu_config_window);
        
        menu_help_about = new JMenuItem(new AbstractAction(t.getText("MENU_HELP_ABOUT")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println(t.getInfo());
                JOptionPane.showMessageDialog(Window.this,t.getInfo(), t.getText("MENU_HELP_ABOUT"),JOptionPane.INFORMATION_MESSAGE );
            }
        });  
        menu_help_help = new JMenuItem(new AbstractAction(t.getText("MENU_HELP_HELP")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Window.this,t.getHelp(), t.getText("MENU_HELP_HELP"),JOptionPane.INFORMATION_MESSAGE );
            }
        });  
        menu_help = new JMenu(t.getText("MENU_HELP"));
        menu_help.add(menu_help_help);
        menu_help.add(menu_help_about);
        
        menu_bar = new JMenuBar(){        
            @Override
            public JMenu add(JMenu menu) {
                container.window.menu_list.add(menu);
                return super.add(menu); //To change body of generated methods, choose Tools | Templates.
            }
            
        };    ;            
        menu_bar.add(menu_file);       
        menu_bar.add(menu_config);   
        menu_bar.add(menu_help);
        this.setJMenuBar(menu_bar);
        
        toolbar = new JToolBar(t.getText("FIGURES"), JToolBar.HORIZONTAL );
        
        toolbar_Circle = new AbstractAction(t.getText("CIRCLE"), icons.getIcon(IconLoader.ICON_ID_CIRCLE)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.createFigure(FigurePanel.FIGURE_TYPE_ELLIPSE_CIRCLE);
            }
        };
        toolbar_Circle.putValue(Action.SHORT_DESCRIPTION, t.getText("CIRCLE"));
        
        toolbar_Ellipse = new AbstractAction(t.getText("ELLIPSE"), icons.getIcon(IconLoader.ICON_ID_ELLIPSE)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.createFigure(FigurePanel.FIGURE_TYPE_ELLIPSE);
            }
        };
        toolbar_Ellipse.putValue(Action.SHORT_DESCRIPTION, t.getText("ELLIPSE"));
        
        
        toolbar_Polygon = new AbstractAction(t.getText("POLYGON"), icons.getIcon(IconLoader.ICON_ID_POLYGON)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.createFigure(FigurePanel.FIGURE_TYPE_POLYGON);
            }
        };
        toolbar_Polygon.putValue(Action.SHORT_DESCRIPTION, t.getText("POLYGON"));
        
        toolbar_Quadrilateral = new AbstractAction(t.getText("QUADRILATERAL"), icons.getIcon(IconLoader.ICON_ID_QUADRILATERAL)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.createFigure(FigurePanel.FIGURE_TYPE_QUADRILATERAL);
            }
        };
        toolbar_Quadrilateral.putValue(Action.SHORT_DESCRIPTION, t.getText("QUADRILATERAL"));
        
        toolbar_Parallelogram = new AbstractAction(t.getText("PARALLELOGRAM"), icons.getIcon(IconLoader.ICON_ID_PARALLELOGRAM)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.createFigure(FigurePanel.FIGURE_TYPE_QUADRILATERAL_PARALLELOGRAM);
            }
        };
        toolbar_Parallelogram.putValue(Action.SHORT_DESCRIPTION, t.getText("PARALLELOGRAM"));
        
        
        toolbar_Rhomb = new AbstractAction(t.getText("RHOMB"), icons.getIcon(IconLoader.ICON_ID_RHOMB)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.createFigure(FigurePanel.FIGURE_TYPE_QUADRILATERAL_RHOMB);
            }
        };
        toolbar_Rhomb.putValue(Action.SHORT_DESCRIPTION, t.getText("RHOMB"));
        
        toolbar_Square = new AbstractAction(t.getText("SQUARE"), icons.getIcon(IconLoader.ICON_ID_SQUARE)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.createFigure(FigurePanel.FIGURE_TYPE_QUADRILATERAL_SQUARE);
            }
        };
        toolbar_Square.putValue(Action.SHORT_DESCRIPTION, t.getText("SQUARE"));
        
        toolbar_Rectangle = new AbstractAction(t.getText("RECTANGLE"), icons.getIcon(IconLoader.ICON_ID_RECTANGLE)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.createFigure(FigurePanel.FIGURE_TYPE_QUADRILATERAL_RECTANGLE);
            }
        };
        toolbar_Rectangle.putValue(Action.SHORT_DESCRIPTION, t.getText("RECTANGLE"));
        
        toolbar_Border_Colour = new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFigureDefaultBorderColour();      
            }
        });
        toolbar_Border_Colour.setToolTipText(t.getText("BORDER_COLOUR"));       
        toolbar_Fill_Colour = new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFigureDefaultFillColour();
            }
        });
        toolbar_Fill_Colour.setToolTipText(t.getText("FILL_COLOUR"));
        updateColourButton(true);
        updateColourButton(false);
        
        toolbar_Border_Thickness = new JSpinner(new SpinnerNumberModel(config.defaultFigureBorderThickness, 1, IconLoader.ICON_SIZE, 1));
        toolbar_Border_Thickness.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                config.setDefaultFigureBorderThickness((Integer)toolbar_Border_Thickness.getValue());
            }
        });
        toolbar_Border_Thickness.setToolTipText(t.getText("BORDER_THICKNESS"));
        toolbar_Border_Thickness.setMaximumSize(new Dimension(70,Integer.MAX_VALUE));
        toolbar.add(toolbar_Polygon);
        toolbar.add(toolbar_Ellipse);
        toolbar.add(toolbar_Circle);
        toolbar.add(toolbar_Quadrilateral);
        toolbar.add(toolbar_Parallelogram);
        toolbar.add(toolbar_Rhomb);
        toolbar.add(toolbar_Rectangle);
        toolbar.add(toolbar_Square);
        toolbar.add(toolbar_Border_Thickness);
        toolbar.add(toolbar_Border_Colour);
        toolbar.add(toolbar_Fill_Colour);   
        this.add(toolbar,BorderLayout.NORTH);
        updateStyle();
        pack();
    } 
    
    
    
    
    
    


   //#########################overriden methods#################################
    @Override
    public Component add(Component comp) {
        Component rtrn = super.add(comp);
        comp_list.add(rtrn);
        return rtrn;
    }

    @Override
    public void remove(Component comp) {
        super.remove(comp);
        comp_list.remove(comp);
    }
}
