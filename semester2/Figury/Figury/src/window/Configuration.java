package window;

import static main.Text.isProperLanguage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import main.Text;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * This class is used for storing some properties of {@link Window} class like colours and fonts.
 * @author n1t4chi
 * @see Window
 */
public class Configuration implements Serializable{
    
   //##########################static context###################################
    /**
     * Default value of {@link #drawGrid}.
     */
    public static boolean DEFAULT_VALUE_SHOW_GRID = false;
    /**
     * Default value of {@link #drawBorder}.
     */
    public static boolean DEFAULT_VALUE_DRAW_BORDER = false;
    
    /**
     * Default value for {@link #minimumWindowSize}.
     */
    public static final Dimension DEFAULT_VALUE_MINIMUM_WINDOW_SIZE = new Dimension(600,400);
    
    /**
     * Default value for {@link #preferredWindowSize}.
     */
    public static final Dimension DEFAULT_VALUE_PREFERRED_WINDOW_SIZE = new Dimension(1200,700);
    
    /**
     * Default value for {@link #windowLocation}.
     */
    public static final Point DEFAULT_VALUE_WINDOW_LOCATION = new Point(0,0);
    
    /**
     * Default value for {@link #language}.
     */
    public static final int DEFAULT_VALUE_LANGUAGE = Text.LANGUAGE_ENGLISH;
    //public static final int DEFAULT_VALUE_LANGUAGE = Text.LANGUAGE_POLISH;
    /**
     * Default value for {@link #defaultFigureFillColour}.
     */
    public static final Color DEFAULT_VALUE_DEFAULT_FIGURE_FILL_COLOUR=Color.WHITE;
    /**
     * Default value for {@link #defaultFigureBorderThickness}.
     */
    public static final int DEFAULT_VALUE_DEFAULT_FIGURE_BORDER_THICKNESS=1;
    /**
     * Default value for {@link #defaultFigureBorderColour}.
     */
    public static final Color DEFAULT_VALUE_DEFAULT_FIGURE_BORDER_COLOUR=Color.BLACK;
    
   
    /**
     * Default value for {@link #font}.
     */
    public static final Font DEFAULT_FONT = new Font(Font.SERIF, Font.PLAIN, 12);
    /**
     * Default value for {@link #fontColour}.
     */
    public static final Color DEFAULT_FONT_COLOUR= Color.BLACK;
    /**
     * Default value for {@link #backgroundColour}.
     */
    public static final Color DEFAULT_BACKGROUND_COLOUR = Color.white;



   //##########################fields###########################################
    
    /**
     * Indicates if helping grid lines should be displayed. 
     */
    private boolean drawGrid;
    /**
     * Indicates if components on window should have border drawn. 
     */
    private boolean drawBorder;
    /**
     * Indicates window minimum size.
     */
    private Dimension minimumWindowSize;
    
    /**
     * Indicates window location.
     */
    Point windowLocation;
            
    /**
     * Indicates window optimal size.
     */
    Dimension preferredWindowSize;  
    
    /**
     * Default colour of filling of newly made figures without specified colour pattern.
     */
    Color defaultFigureFillColour;
    /**
     * Value of default border thickness of newly made figures if it is not specified upon creation.
     */
    int defaultFigureBorderThickness;
    /**
     * Default colour of border for newly made figures without specified colour pattern.
     */
    Color defaultFigureBorderColour;
    /**
     * Current language.
     */
    private int language;
    
    /**
     * Font object.
     */
    public Font font;
    /**
     * Font colour.
     */
    public Color fontColour;
    /**
     * Background colour;
     */
    public Color backgroundColour;
    
    


    
    

   //#########################methods###########################################
    
    /**
     * Sets value of {@link #drawGrid}
     * @param drawGrid value representing if grid lines should be displayed.
     */
    public void setDrawGrid(boolean drawGrid){
        this.drawGrid =  drawGrid;     
    }
    
    /**
     * Returns value of {@link #drawGrid}.
     * @return value representing if grid lines should be displayed.
     */
    public boolean getShowGrid(){
        return this.drawGrid;
    }
    
    /**
     * Sets value of {@link #drawBorder}
     * @param draw_border value representing if border should be drawn onto components after execution of this method.
     */
    public void setDrawBorder(boolean draw_border){
        this.drawBorder =  draw_border;     
    }
    
    /**
     * Returns value of {@link #drawBorder}.
     * @return value representing if border should be drawn onto components.
     */
    public boolean getDrawBorder(){
        return this.drawBorder;
    }
    
    /**
     * Returns value of {@link #drawBorder}.
     * Same as {@link #getDrawBorder()}
     * @return value representing if border should be drawn onto components.
     */
    public boolean shouldDrawBorder(){
        return getDrawBorder();
    }
    
    
    /**
     * Returns value of {@link #windowLocation}.
     * @return Current window location.
     */
    public Point getWindowLocation(){
        return this.windowLocation;
    }
    /**
     * Sets new value of {@link #windowLocation}.
     * @param curr_loc Current locatiom
     * @return  True if changing value was successful.
     */
    public boolean setWindowLocation(Point curr_loc){
        try{
            return setWindowLocation(curr_loc.x , curr_loc.y);
        }catch(NullPointerException ex){
            return false;
        }   
    }
    /**
     * Sets new value of {@link #windowLocation}.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return  True if changing value was successful.
     */
    public boolean setWindowLocation(int x, int y){
        //System.out.println("method save saving location:("+x+","+y+")");
        windowLocation = new Point(x, y);
        return true;
    }
    
    /**
     * Returns value of {@link #minimumWindowSize}.
     * @return Minimum window size.
     */
    public Dimension getMinimumWindowSize(){
        return this.minimumWindowSize;
    }
    /**
     * Sets new value of {@link #minimumWindowSize}.
     * @param minimum_size new minimum size.
     * @return  True if changing value was successful.
     */
    public boolean setMinimumWindowSize(Dimension minimum_size){
        try{
            return setMinimumWindowSize(minimum_size.width,minimum_size.height);
        }catch(NullPointerException ex){
            return false;
        }   
    }
    
    
    /**
     * Sets new value of {@link #minimumWindowSize}.
     * @param width new minimum width.
     * @param height new minimum height.
     * @return  True if changing value was successful.
     */
    public boolean setMinimumWindowSize(int width,int height){
        if((width>0)&&(height>0)){
            this.minimumWindowSize = new Dimension(width,height);
            return true;
        }else{
            return false;
        }
    }
    /**
     * Returns preferred window size.
     * @return Preferred window size.
     */
    public Dimension getPreferredWindowSize() {
        return preferredWindowSize;
    }

    /**
     * Sets new value of {@link #preferredWindowSize}.
     * @param prefferedWindowSize new preferred size.
     * @return  True if changing value was successful.
     */
    public boolean setPreferredWindowSize(Dimension prefferedWindowSize) {
        try{
            return setPreferredWindowSize(prefferedWindowSize.width,prefferedWindowSize.height);
        }catch(NullPointerException ex){
            return false;
        }  
    }
    /**
     * Sets new value of {@link #preferredWindowSize}.
     * @param width new preferred width.
     * @param height new preferred height.
     * @return  True if changing value was successful.
     */
    public boolean setPreferredWindowSize(int width, int height){
        if((width>0)&&(height>0)){
            this.preferredWindowSize = new Dimension(width,height);
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Returns default colour of filling of newly made figures without specified colour pattern.
     * @return Default colour of filling;
     */
    public Color getDefaultFigureFillColour() {
        return defaultFigureFillColour;
    }
    
    /**
     * Sets default colour of filling of newly made figures without specified colour pattern.
     * @param defaultFigureFillColour New colour value.
     * @return  True if changing value was successful.
     */
    public boolean setDefaultFigureFillColour(Color defaultFigureFillColour) {
        try{
            return setDefaultFigureFillColour(defaultFigureFillColour.getRed(), defaultFigureFillColour.getGreen(), defaultFigureFillColour.getBlue(), defaultFigureFillColour.getAlpha());
        }catch(NullPointerException ex){
            return false;
        }
    }
    /**
     * Sets default colour of filling of newly made figures without specified colour pattern.
     * @param R Red colour value.
     * @param G Green colour value.
     * @param B Blue colour value.
     * @return  True if changing value was successful.
     */
    public boolean setDefaultFigureFillColour(int R,int G,int B) {
        return setDefaultFigureFillColour(R, G, B, 255);
    }
    /**
     * Sets default colour of filling of newly made figures without specified colour pattern.
     * @param R Red colour value.
     * @param G Green colour value.
     * @param B Blue colour value.
     * @param A Alpha channel value.
     * @return  True if changing value was successful.
     */
    public boolean setDefaultFigureFillColour(int R,int G,int B,int A) {
        if((R>=0)&&(R<=255)&&(G>=0)&&(G<=255)&&(B>=0)&&(B<=255)&&(A>=0)&&(A<=255)){
            this.defaultFigureFillColour = new Color(R, G, B, A);
            return true;
        }else{
            return false;  
        }
    }
    
    /**
     * Sets value of default border thickness of newly made figures if it is not specified upon creation.
     * @param defaultFigureBorderThickness new border thickness.
     * @return  True if changing value was successful.
     */
    public boolean setDefaultFigureBorderThickness(int defaultFigureBorderThickness) {
        if(defaultFigureBorderThickness>=0){
            this.defaultFigureBorderThickness = defaultFigureBorderThickness;
            return true;
        }else{
            return false;
        }
    }
    /**
     * Returns value of default border thickness of newly made figures if it is not specified upon creation.
     * @return Border thickness.
     */
    public int getDefaultFigureBorderThickness() {
        return defaultFigureBorderThickness;
    }
    
    
    
    /**
     * Returns default colour of border for newly made figures without specified colour pattern.
     * @return Default figure border colour.
     */
    public Color getDefaultFigureBorderColour() {
        return defaultFigureBorderColour;
    }

    /**
     * Sets default colour of border for newly made figures without specified colour pattern
     * @param defaultFigureBorderColour New colour value.
     * @return  True if changing value was successful.
     */
    public boolean setDefaultFigureBorderColour(Color defaultFigureBorderColour) {
        try{
            return setDefaultFigureBorderColour(defaultFigureBorderColour.getRed(), defaultFigureBorderColour.getGreen(), defaultFigureBorderColour.getBlue(), defaultFigureBorderColour.getAlpha());
        }catch(NullPointerException ex){
            return false;
        }
    }
    /**
     * Sets default colour of border for newly made figures without specified colour pattern
     * @param R New red colour value.
     * @param G New green colour value.
     * @param B New blue colour value.
     * @return  True if changing value was successful.
     */
    public boolean setDefaultFigureBorderColour(int R,int G,int B) {
        return setDefaultFigureBorderColour(R, G, B, 255);
    }
    /**
     * Sets default colour of border for newly made figures without specified colour pattern
     * @param R New red colour value.
     * @param G New green colour value.
     * @param B New blue colour value.
     * @param A New alpha channel value.
     * @return  True if changing value was successful.
     */
    public boolean setDefaultFigureBorderColour(int R,int G,int B,int A) {
        if((R>=0)&&(R<=255)&&(G>=0)&&(G<=255)&&(B>=0)&&(B<=255)&&(A>=0)&&(A<=255)){
            this.defaultFigureBorderColour = new Color(R, G, B, A);
            return true;
        }else{
            return false;  
        }
    }
    /**
     * Sets current language.
     * @param language new language.
     * @return  True if changing value was successful.
     */
    public boolean setLanguage(int language) {
        if(isProperLanguage(language)){
            this.language = language;
            return true;
        }else{
            return false;
        }
    }
    /**
     * Returns current language.
     * @return current language.
     */
    public int getLanguage() {
        return language;
    } 
    
    /**
     * Sets font size.
     * @param size Size of font.
     */
    public void setFontSize(int size){
        font=font.deriveFont((float)size);
    }
    /**
     * Sets font type.
     * @param type Font type.
     */
    public void setFontType(int type){
        font=font.deriveFont(type);
    }
    /**
     * Sets font.
     * @param f Font.
     */
    public void setFont(Font f){
        if(f!=null)
            this.font = f;
    }
    /**
     * Returns font.
     * @return Font.
     */
    public Font getFont(){
        return font;
    }
    /**
     * Sets font colour.
     * @param f_c Colour.
     */
    public void setFontColour(Color f_c){
        if(f_c!=null)
            this.fontColour = f_c;
    }
    /**
     * Returns font colour.
     * @return Font colour.
     */
    public Color getFontColour(){
        return fontColour;
    }
    /**
     * Sets background colour.
     * @param bg_c Colour.
     */
    public void setBackGroundColour(Color bg_c){
        if(bg_c!=null)
            this.backgroundColour = bg_c;
    }
    /**
     * Returns background colour.
     * @return colour.
     */
    public Color getBackGroundColour(){
        return backgroundColour;
    }
    /**
     * Copies all setting from given configuration object.
     * @param c configuration object
     */
    public void copy(Configuration c){
        this.font = c.font;
        this.fontColour = c.fontColour;
        this.backgroundColour = c.backgroundColour;
        this.drawBorder = c.drawBorder;
        this.minimumWindowSize = c.minimumWindowSize;
        this.preferredWindowSize = c.preferredWindowSize;
        this.windowLocation = c.windowLocation;
        this.defaultFigureFillColour = c.defaultFigureFillColour;
        this.defaultFigureBorderColour = c.defaultFigureBorderColour;
        this.defaultFigureBorderThickness = c.defaultFigureBorderThickness;
        this.language = c.language;
        this.drawGrid = c.drawGrid;
    }
    
     /**
      * Loads configuration file
      * @return True if loading was successful.
      */ 
    public boolean loadFromFile(){
        boolean rtrn = false;
        FileInputStream fos = null;
        try {
            fos = new FileInputStream("\\config");          
            ObjectInputStream oos = new ObjectInputStream(fos);
            Object o = oos.readObject();
            if(o instanceof Configuration){
                copy((Configuration)o);    
                //System.out.println("loading location:"+windowLocation); 
                rtrn = true;
            }    
        } catch (FileNotFoundException ex) {
            System.err.println("Could not find file.");     
            saveToFile();
        } catch (IOException ex) {
            System.err.println("Could not load configuration.");
        } catch (ClassNotFoundException ex) {
            System.err.println("Wrong class");
        } finally {
            try {
                if(fos!=null)
                    fos.close();
            } catch (IOException ex) {
                System.err.println("Could not close configuration file.");
            }
        }
        return rtrn;
    }
     /**
      * Saves to configuration file
      * @return True if saving was successful.
      */ 
    public boolean saveToFile(){
        //System.out.println("saving location:"+windowLocation);
        boolean rtrn = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("\\config");
            
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);     
            rtrn = true;
        } catch (FileNotFoundException ex) {
            System.err.println("Could not find file.");     
        } catch (IOException ex) {
            System.err.println("Could not save configuration.");
        } finally {
            try {
                if(fos!=null)
                    fos.close();
            } catch (IOException ex) {
                System.err.println("Could not close configuration file.");
            }
        }
        return rtrn;
    }
    
    
   //#########################constructors######################################
    
    /**
     * Default Constructor, does not require variables. It will set default configuration.
     */
    public Configuration() {
        font = DEFAULT_FONT;
        fontColour = DEFAULT_FONT_COLOUR;
        backgroundColour = DEFAULT_BACKGROUND_COLOUR;
        drawBorder = DEFAULT_VALUE_DRAW_BORDER;
        minimumWindowSize = DEFAULT_VALUE_MINIMUM_WINDOW_SIZE;
        preferredWindowSize =DEFAULT_VALUE_PREFERRED_WINDOW_SIZE;
        defaultFigureFillColour = DEFAULT_VALUE_DEFAULT_FIGURE_FILL_COLOUR;
        defaultFigureBorderColour = DEFAULT_VALUE_DEFAULT_FIGURE_BORDER_COLOUR;
        defaultFigureBorderThickness = DEFAULT_VALUE_DEFAULT_FIGURE_BORDER_THICKNESS;
        language=DEFAULT_VALUE_LANGUAGE;
        windowLocation = DEFAULT_VALUE_WINDOW_LOCATION;
        this.drawGrid = DEFAULT_VALUE_SHOW_GRID;
    }
    /**
     * Constructor, sets specified values for all fields.
     * @param draw_border Policy of border drawing of components within window.
     * @param show_grid Policy of drawing grid lines.
     * @param minimum_size Minimum size of window.
     * @param preffered_size Default size of window.
     * @param location Location of window.
     * @param default_figure_inner_color Default fill colour of figures.
     * @param default_figure_border_color Default border colour of figures.
     * @param f Font.
     * @param f_c Font colour.
     * @param bg_c Background colour.
     * @param defaultFigureBorderThickness Default border thickness of figures
     * @param language Language.
     */
    public Configuration(boolean draw_border,boolean show_grid, Dimension minimum_size, Dimension preffered_size,Point location, Color default_figure_inner_color, Color default_figure_border_color, Font f, Color f_c, Color bg_c,int defaultFigureBorderThickness,int language) {
        this.font = f;
        this.fontColour = f_c;
        this.backgroundColour = bg_c;
        this.drawBorder = draw_border;
        this.minimumWindowSize = minimum_size;
        this.preferredWindowSize = preffered_size;
        this.defaultFigureFillColour = default_figure_inner_color;
        this.defaultFigureBorderColour = default_figure_border_color;
        this.defaultFigureBorderThickness = defaultFigureBorderThickness;
        this.language = language;
        this.windowLocation = location;
        this.drawGrid = show_grid;
    }
    
    
    
    
}
