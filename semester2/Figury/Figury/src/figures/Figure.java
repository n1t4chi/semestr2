/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package figures;

import static figures.Polygon.SCALE_ABSOLUTE;
import static figures.Polygon.SCALE_CENTRE;
import static figures.Polygon.SCALE_MOUSE_CONTAINER;
import static figures.Polygon.SCALE_MOUSE_FIGURE;
import static figures.Polygon.SCALE_POINT;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import main.Text;
import window.Configuration;
import window.FigurePanel;
import static window.Window.translateME;

/**
 * Class of general figure, Not drawable.
 * @author n1t4chi
 */
public abstract class Figure extends JComponent{
    
    
   //##########################static context###################################
    
    /**
     * Returns whether given string is save format from this class.
     * @param c class to check from
     * @param s String to check.
     * @return True if yes, false otherwise.
     */
    public static boolean isSaveFormatFromClass( Class c, String s){ 
        return s.startsWith(c.getCanonicalName());
    }    
    /**
     * Returns class prefix used when saving objects of this class.
     * @param c class to add prefix from
     * @return Class prefix.
     */
    public static String getClassSaveFormatPrefix(Class c){
        return c.getCanonicalName();
    }    
    
    
    
    
    /**
     * Distance buffer for searching objects.
     */
    public static final double SEARCH_BUFFER = 8;
    
    /**
     * For {@link #extremes()} purposes. X coordinate.
     */
    protected final static int X = 0;
    /**
     * For {@link #extremes()} purposes. Y coordinate.
     */
    protected final static int Y = 1;
    /**
     * For {@link #extremes()} purposes. Minimal value.
     */
    protected final static int MIN = 0;
    /**
     * For {@link #extremes()} purposes. Maximal value.
     */
    protected final static int MAX = 1;
    /**
     * Free space on each side of polygon.
     */
    protected final static int FREE_XY_SPACE = 5+FigurePoint.POLYGON_POINT_SIZE;
    /**
     * Constant for scaling type. Scales absolutely from point (0,0)
     */
    public static final int SCALE_ABSOLUTE=0;
    /**
     * Constant for scaling type. Scales relatively from most top left point of this figure.
     */
    public static final int SCALE_POINT=1;
    /**
     * Constant for scaling type. Scales relatively from centre of polygon.
     */
    public static final int SCALE_CENTRE=2;
    /**
     * Constant for scaling type. Scales relatively from position where popup menu was popped up. Relatively to figure.
     */
    public static final int SCALE_MOUSE_FIGURE=3;    
    /**
     * Constant for scaling type. Scales relatively from position where popup menu was popped up. Relatively to point (0,0).
     */
    public static final int SCALE_MOUSE_CONTAINER=4;   
    
    
    
    
    
    
   //##########################fields###########################################
    
    
    //##########################menus###########################################
    /**
     * Popup menu for figure.
     */
    protected final JPopupMenu menu;  
    /**
     * Popup menu item. Scales this polygon.
     */
    protected final JMenuItem menu_Scale;
    /**
     * Popup menu item. Locks size of polygon.
     */
    protected final JCheckBoxMenuItem menu_Lock_Size;
    /**
     * Popup menu item. Locks position of polygon.
     */
    protected final JCheckBoxMenuItem menu_Lock_Position;
    /**
     * Popup menu item. Removes this polygon.
     */
    protected final JMenuItem menu_Remove;
    /**
     * Popup submenu. Polygon looks subemnu.
     */
    protected final JMenu menu_Figure;
    /**
     * Popup menu item. Changes border colour.
     */
    protected final JMenuItem menu_Figure_Border_Colour;
    /**
     * Popup menu item. Changes border thickness.
     */
    protected final JMenuItem menu_Figure_Border_Thickness;
    /**
     * Popup menu item. Changes fill colour.
     */
    protected final JMenuItem menu_Figure_Fill_Colour;
    
    
    
    
    
    
    
    
    
    /**
     * Mouse position. For popup menu purposes.
     */
    protected Point mouse_position;
    /**
     * Nearby point. For popup menu purposes.
     */
    protected FigurePoint nearby_point; 
    /**
     * list of XY coordinates
     */
    protected final ArrayList points;
    /**
     * Variable indicating border colour.
     */
    protected Color borderColour;
    /**
     * Variable indicating border thickness.
     */
    protected int borderThickness;
    /**
     * Variable indicating fill colour.
     */
    protected Color fillColour;     
    /**
     * Configuration.
     */
    public final Configuration config;
    /**
     * Text object.
     */
    protected final Text t;
    /**
     * Ultimate policy if polygon should move when dragged.
     */
    protected boolean moveable_Super=true;
    /**
     * Policy if polygon should move when dragged.
     */
    protected volatile boolean moveable=true;
    /**
     * Parent container of this polygon.
     */   
    public final FigurePanel container;
    
    /**
     * Ultimate policy if polygon should be resizeable. Includes locking points within him.
     */
    protected boolean resizeable_Super = true;   
    /**
     * Relative location of most top left point to (0,0) point of figure size.
     */
    public final Point relativeLocation=new Point(FREE_XY_SPACE,FREE_XY_SPACE);
    
    
    
    
    
    
    
   //#########################methods###########################################
    /**
     * Checks if mouse is near some FigurePoint
     * @param e mouse event
     * @return Point that mouse is nearby.
     */
    public FigurePoint isMouseNearPoint(MouseEvent e){
        FigurePoint rtrn = null;      
        for (Object o : points) {
            if(o instanceof FigurePoint){
                if (SEARCH_BUFFER>((FigurePoint) o).coordinates.distance(e.getX(), e.getY())){
                    rtrn = ((FigurePoint) o);
                    //System.out.println("Found point:"+rtrn);
                    break;
                }
            }
        }
        return rtrn;
    }
    /**
     * Checks if mouse is near some other FigurePoint
     * @param e mouse event
     * @param current FigurePoint that should be ignored. Can be null
     * @return Point that mouse is nearby.
     */
    public FPoint isMouseNearPoint(MouseEvent e,FigurePoint current){
        FPoint rtrn = null;      
        for (Object o : points) {
            if(!o.equals(current)){
                if(o instanceof FigurePoint){
                    if (SEARCH_BUFFER>((FigurePoint) o).coordinates.distance(e.getX(), e.getY())){
                        rtrn = ((FigurePoint) o).coordinates;
                        //System.out.println("Found point:"+rtrn);
                        break;
                    }
                }
            }
        }
        return rtrn;
    } 
    /**
     * Asks container is there a point that is nearby current mouse position.
     * @param e mouse event
     * @param current FigurePoint that should be ignored. Can be null
     * @return Point that mouse is nearby.
     */
    public FPoint askParentIsMouseNearPoint(MouseEvent e,FigurePoint current){
        //System.out.println(container);
        return container.isMouseNearOtherPoint(e,current);
    }  
   /**
    * Returns 2 dimensional array of coordinates. Array is POLYGON_POINT_SIZE of [2][n] where n is number of coordinates.
    * Array[0] is list of X coorinates and array[1] is Y coordinates.
    * @return 2 dimensional array of coordinates.
    */
    protected int[][] getIntegerArrayOfCoordinates(){
        return getRelativeIntegerArrayOfCoordinates(0,0);
    }
     
    /**
     * Returns relative to given point 2 dimensional array of coordinates. Array is POLYGON_POINT_SIZE of [2][n] where n is number of coordinates. 
     * Array[0] is list of X coorinates and array[1] is Y coordinates.
     * @param x X coordinate for relative point.
     * @param y T coordinate for relative point.
    * @return 2 dimensional array of coordinates.
     */
    protected int[][] getRelativeIntegerArrayOfCoordinates(int x,int y){
        int[][] rtrn = new int[2][0];
        for (Object object : points) {
            if(object instanceof FigurePoint){
                
                rtrn[0] = Arrays.copyOf(rtrn[0], rtrn[0].length+1);
                rtrn[1] = Arrays.copyOf(rtrn[1], rtrn[1].length+1);
                rtrn[0][rtrn[0].length-1] = (int)((FigurePoint) object).coordinates.getX()+x;
                rtrn[1][rtrn[1].length-1] = (int)((FigurePoint) object).coordinates.getY()+y;
            }    
        }
        return rtrn;    
    }
    
    
        /**
     * Adds new point.
     * @param p point.
     * @return True if point was added to list. False otherwise [for example if point object is already on the list].
     */
    public boolean addPoint(FigurePoint p){  
        try{
            if(!points.contains(p)){
                points.add(p);
                add(p);
                repaint();
                return true;
            }else{
                return false;
            }
        }catch(NullPointerException ex){
            return false;
        }
    }
    
    /**
     * Inserts new point at specified index.
     * @param p Point.
     * @param index Index.
     * @return True if point was added to list. False otherwise [for example if point object is already on the list].
     */
    public boolean addPoint(FigurePoint p,int index){  
        try{
            if(!points.contains(p)){
                points.add(index,p);
                add(p);
                repaint();
                return true;
            }else{
                return false;
            }
        }catch(NullPointerException | IndexOutOfBoundsException ex){
            return false;
        }
    }
    
    /**
     * Removes point.
     * @param p point to remove
     * @return True if point was removed, False otherwise [for example if point was not on the list].
     */
    public boolean removePoint(FigurePoint p){
        try{
            remove(p);
            points.remove(p);
            repaint();
            return true;
        }catch(NullPointerException ex){
            return false;
        }
    }
    
    /**
     * Scales whole figure in specified manner: {@link #SCALE_ABSOLUTE}[default] {@link #SCALE_CENTRE} {@link #SCALE_MOUSE_CONTAINER} {@link #SCALE_MOUSE_FIGURE} {@link #SCALE_POINT}.
     * @param v_X X length of vector.
     * @param v_Y Y length of vector.
     * @param type Type of scaling. 
     */
    public void scaleFigure(double v_X,double v_Y,int type){
        int x;
        int y;
        boolean absolute;
        switch(type){
            case SCALE_POINT:
                x = 0;
                y = 0;
                absolute=false;
                break;
            case SCALE_CENTRE:
                x = getFigureSize().width/2;
                y = getFigureSize().height/2;
                absolute=false;
                break;
            case SCALE_MOUSE_FIGURE:
                Point p = mouse_position.getLocation();
                x=mouse_position.x - getFigureLocation().x;
                y=mouse_position.y - getFigureLocation().y;
                absolute=false;             
                break;                
            case SCALE_MOUSE_CONTAINER:
                x=mouse_position.x;
                y=mouse_position.y;
                absolute=true;
                break;
            case SCALE_ABSOLUTE: default:
                x=0;
                y=0;
                absolute=true;
                break;
        }
        scalePoints(v_X,v_Y,x,y,absolute);
    }
    /**
     * Scales all points belonging to this polygon by given number. Scales all coordinates with same proportions. Scales relatively to given XY point.
     * @param v scaling.
     * @param x X argument of point to scale relatively to.
     * @param y Y argument of point to scale relatively to.
     * @param absolute scaling absolutely to given point or relatively to figure.
     */
    public void scalePoints(double v,int x,int y,boolean absolute){
        scalePoints(v, v,x,y,absolute);
    }
    /**
     * Scales all points belonging to this polygon by given XY vector[X coordinates are scaled by Vx length]. Scales relatively to given XY point.
     * @param v_X X length of vector.
     * @param v_Y Y length of vector.
     * @param x X argument of point  to scale relatively to.
     * @param y Y argument of point  to scale relatively to.
     * @param absolute scaling absolutely to given point or relatively within figure.
     */
    public void scalePoints(double v_X,double v_Y,int x,int y,boolean absolute){
        
        //System.out.println("Scaling "+( (absolute)?"absolutely":"relatively"  )+" to point ("+x+","+y+") by vec=("+v_X+","+v_Y+")");
        if(
            (!
                (
                    (v_X==v_Y)
                    &&(v_Y==1)
                )
            )
            &&isResizeable()
        ){
            for (Object o : points) {
                if(o instanceof FigurePoint){
                    //System.out.print("Point "+((FigurePoint) o).toString()+" changing to ");
                    Point p = ((FigurePoint) o).coordinates.getPoint(); 
                    if(!absolute){
                        p.translate(-getFigureLocation().x, -getFigureLocation().y);
                    }      
                    ((FigurePoint) o).translate(((v_X-1)*(p.getX()-x))  , ((v_Y-1)*(p.getY()-y)));
                    //System.out.println(((FigurePoint) o).toString());
                }
            }
        }
        resize();
    }
    
    
        
    /**
     * Returns this figure name.
     * @return Figure name.
     */
    public String getFigureName(){
        return getFigureName(config.getLanguage());
    }        
    
    /**
     * Sets this polygon as current active polygon.
     */
    void setMeAsActiveFigure(){    
        //System.out.println(container);
        container.setActiveFigure(this);
    }
    
    
    
    /**
     * Returns if this polygon is moveable at the moment.
     * @return True if it is movable, no otherwise.
     */
    public boolean isMoveable(){
        return (moveable_Super&&moveable);
    }
    /**
     * Changes policy for moving this polygon.
     * @param state New policy.
     */
    public void setMoveable(boolean state){
        moveable = state;
    }
    /**
     * Returns if this polygon is resizeable at the moment.
     * @return True if it is resizeable, no otherwise.
     */
    public boolean isResizeable(){
        return resizeable_Super;
    }
    
    
    /**
     * Method updates {@link #nearby_point} with current nearby point.
     * @param e Mouse Event
     */
    protected void updateNearbyPoint(MouseEvent e){
        //System.out.println("e location:"+e.getPoint());
        nearby_point = isMouseNearPoint(e);
    }
    
    /**
     * Sets visibility of all points belonging to this polygon. 
     * @param visible If points should be visible.
     */
    public void setPointVisibility(boolean visible){
        for (Object p : points) {
            if(p instanceof FigurePoint){
                ((FigurePoint) p).setVisible(visible);
            }
        }
    }
    
    /**
     * Translates mouse event position with relation to (0,0) point.
     * @param e MouseEvent
     * @return Translated mouse event.
     */
    public MouseEvent getRelativeMouseEvent(MouseEvent e){
        //System.out.println("figures.Polygon.getRelativeMouseEvent() e"+e.getPoint());
        return translateME(e, -container.relative_Position.x+getLocation().x, -container.relative_Position.y+getLocation().y); 
    }
    /**
     * Translates mouse event position with relation to (0,0) point and places it in {@link #mouse_position}
     * @param e MouseEvent
     */
    public void setRelativeMousePosition(MouseEvent e){
        MouseEvent m = getRelativeMouseEvent(e);
        mouse_position = m.getPoint();   
    }
    
    /**
     * Translates all points by given vector.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void translatePoints(int x, int y){
        //System.out.println("figures.Figure.translatePoints()");
        if(isMoveable()){
            for (Object o : points) {
                if(o instanceof FigurePoint){
                    //System.out.print("Changing point from "+((FigurePoint) o).toString(true));
                    ((FigurePoint) o).translate(x, y);
                    //System.out.println(" to "+((FigurePoint) o).toString(true));
                }
            }
        }
        resize();
    }  
    /**
     * Returns location of figure.
     * @return Location of figure.
     */
    public Point getFigureLocation(){
        return new Point(-container.relative_Position.x+getLocation().x+FREE_XY_SPACE, -container.relative_Position.y+getLocation().y+FREE_XY_SPACE  );
    }
    /**
     * Returns size of figure.
     * @return size of figure.
     */
    public Dimension getFigureSize(){
        return new Dimension(getSize().width-2*FREE_XY_SPACE, getSize().height-2*FREE_XY_SPACE  );
    }
            
                
    /**
     * Returns extremal XY coordinates of this polygon. 
     * @return [2][2] array where first argument is respectively for X and Y axis and second argument is for respectively MIN and MAX value on specified axis.
     */
    public int[][] extremes(){
        int[][] xy = getIntegerArrayOfCoordinates();   
        int[][] rtrn = new int[2][2];
        if(xy[0].length>0){
            rtrn[X][MIN]=xy[0][0];
            rtrn[X][MAX]=xy[0][0];
            rtrn[Y][MIN]=xy[1][0];
            rtrn[Y][MAX]=xy[1][0];
            for(int i=1; i<xy[0].length;i++){
                if(xy[0][i] > rtrn[X][MAX] ){
                    rtrn[X][MAX] = xy[0][i];
                }else{
                    if(xy[0][i] < rtrn[X][MIN] ){
                        rtrn[X][MIN] = xy[0][i];
                    }
                }
                if(xy[1][i] > rtrn[Y][MAX] ){
                    rtrn[Y][MAX] = xy[1][i];
                }else{
                    if(xy[1][i] < rtrn[Y][MIN] ){
                        rtrn[Y][MIN] = xy[1][i];
                    }
                }                   
            }    
        }else{
            rtrn = null;
        }
        return rtrn;
    }
    
    /**
     * Returns polygon bounds.
     * @return polygon bounds.
     */
    public Rectangle getFigureBounds(){
        return new Rectangle(getFigureLocation().x ,getFigureLocation().y ,getFigureSize().width,getFigureSize().height);
    }
    
    
    /**
     * Returns information about this figure for StatusBar Info.
     * @return info about this figure.
     */
    public String getInfoForStatusBar(){
        DecimalFormat f = new DecimalFormat("#0.00");
        return  " "+getFigureName()+
                " "+t.getText("AREA")+
                ":"+f.format(getArea())+
                " "+t.getText("PERIMETER")+
                ":"+f.format(getPerimeter())+
                " X:"+getFigureLocation().x+
                " Y:"+getFigureLocation().y;
    }
    
    /**
     * Returns text that allows to recreate this subclass specific parameters with {@link #recreateFromSaveSubclass(java.lang.String) }.
     * @return text.
     */
    public final String getSaveableFormat(){
        String rtrn = 
                getClassSaveFormatPrefix(this.getClass())+"->"
                +getSaveableFormatSubclass() 
                + "%"
                + "["+this.resizeable_Super+"]"
                + "["+this.moveable_Super+"]"   
                + "{"+this.borderColour.getAlpha()+","+this.borderColour.getRed()+","+this.borderColour.getGreen()+","+this.borderColour.getBlue()+"}"  
                + "{"+this.borderThickness+"}"  
                + "{"+this.fillColour.getAlpha()+","+this.fillColour.getRed()+","+this.fillColour.getGreen()+","+this.fillColour.getBlue()+"}"  
        ;
        return rtrn;
    }    
    /**
     * Recreates figure from string .
     * @param s text created with {@link #getSaveableFormat() }.
     * @throws figures.FigureException If given text is not recreatable.
     */
    public final void recreateFromSave(String s) throws FigureException{
        recreateFromSaveSubclass(  s.substring(s.indexOf(">")+1, s.lastIndexOf("%") )  );
        try{
            s = s.substring( s.lastIndexOf("%")+1 );
            //System.out.println("String: "+s);
            boolean resize = Boolean.parseBoolean( s.substring(s.indexOf("[")+1,s.indexOf("]"))   );
            s = s.substring(s.indexOf("]")+1);
            //System.out.println("String: "+s);
            boolean move = Boolean.parseBoolean( s.substring(s.indexOf("[")+1,s.indexOf("]"))   );
            s = s.substring(s.indexOf("]")+1);
            //System.out.println("String: "+s);
            
            //below variables are named in specified way [1]_[2](_[3])
            //[1] b means border f means fill 
            //[2] c means colour t means thicknes
            //[3] a/r/g/b are colour parts
            int b_c_a = Integer.parseInt( s.substring(s.indexOf("{")+1,s.indexOf(",")));
            s = s.substring(s.indexOf(",")+1);
            //System.out.println("String: "+s);
            int b_c_r = Integer.parseInt( s.substring(0,s.indexOf(",")) );
            s = s.substring(s.indexOf(",")+1);
            //System.out.println("String: "+s);
            int b_c_g = Integer.parseInt( s.substring(0,s.indexOf(",")) );
            s = s.substring(s.indexOf(",")+1);
            //System.out.println("String: "+s);
            int b_c_b = Integer.parseInt( s.substring(0,s.indexOf("}")) );
            s = s.substring(s.indexOf("}")+1);
            //System.out.println("String: "+s);
            
            
            int b_t = Integer.parseInt( s.substring(s.indexOf("{")+1,s.indexOf("}")) );
            s = s.substring(s.indexOf("}")+1);
            //System.out.println("String: "+s);
            
            
            int f_c_a = Integer.parseInt( s.substring(s.indexOf("{")+1,s.indexOf(",")) );
            s = s.substring(s.indexOf(",")+1);
            //System.out.println("String: "+s);
            int f_c_r = Integer.parseInt( s.substring(0,s.indexOf(",")) );
            s = s.substring(s.indexOf(",")+1);
            //System.out.println("String: "+s);
            int f_c_g = Integer.parseInt( s.substring(0,s.indexOf(",")) );
            s = s.substring(s.indexOf(",")+1);
            //System.out.println("String: "+s);
            int f_c_b = Integer.parseInt( s.substring(0,s.indexOf("}")) );  
            //System.out.println("String: "+s);
            
            
            
            moveable_Super = move;
            resizeable_Super = resize;
            borderColour= new Color(b_c_r , b_c_g , b_c_b , b_c_a);
            if(b_t<0) throw new IllegalArgumentException();
            borderThickness= b_t;
            fillColour= new Color(f_c_r , f_c_g , f_c_b , f_c_a);   
        }catch(IllegalArgumentException | IndexOutOfBoundsException ex){
            throw new FigureException(FigureException.FIGURE_EXCEPTION_BAD_SAVE_STRING_FORMAT);
        }
        relocate();
        resize();
        repaint();
    }
    
   //#########################constructors######################################
    
    
    /**
     * Default constructor.
     * @param x X coordinates.
     * @param y Y coordinates.
     * @param border_c Border colour.
     * @param border_t Border thickness.
     * @param fill_c Fill colour.
     * @param config Configuration for figures.
     * @param t Text object.
     * @param container Container of this figures
     * @throws FigureException When:
     * <br> - list coordinates are not equal in POLYGON_POINT_SIZE
     * <br> - one of pointers is null
     */
    public Figure(double[] x, double[] y, Color border_c, int border_t, Color fill_c, Configuration config, Text t,FigurePanel container) throws FigureException {  
        if((container == null)||(x == null)||(y == null) || (border_c==null)|| (fill_c==null)|| (config==null)|| (t==null)  ) {
            throw new FigureException(FigureException.FIGURE_EXCEPTION_NULL_POINTER);
        }
        if(border_t<0){
            throw new FigureException(FigureException.FIGURE_EXCEPTION_NEGATIVE_BORDER_THICKNESS);
        }
        if((x.length==0)||(y.length==0)){
            throw new FigureException(FigureException.FIGURE_EXCEPTION_ZERO_COORDINATES);
        }
        this.borderColour = border_c;
        this.borderThickness = border_t;
        this.fillColour = fill_c;
        this.config = config;
        this.t = t;
        this.container = container;
        setVisible(true);
        setEnabled(true);
        setFocusable(false);
        setOpaque(false);
        setBackground(null);
        setDoubleBuffered(true);
        if(config.getDrawBorder()){
            setBorder(new LineBorder(new Color(hashCode())));
        }
        if(x.length==y.length){
            points = new ArrayList();
            for(int i=0; ((i<x.length)&&(i<y.length));i++){           
                addPoint(new FigurePoint(new FPoint(x[i],y[i]),this));          
            }
        }else{
            throw new FigureException(FigureException.FIGURE_EXCEPTION_POINT_ARRAYS_NOT_EQUAL_SIZE);
        }     
    
        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Show(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                Show(e);
            }
            protected void Show(MouseEvent e){
                
                //System.out.println(".mouseClicked() mouse button:"+e.getButton() +" is it my trigger?"+e.isPopupTrigger());
                if(e.isPopupTrigger()){            
                    MouseEvent m = getRelativeMouseEvent(e);
                    doThisWhenPopupMenuIsToBeShown(m);
                    mouse_position = m.getPoint();
                    updateNearbyPoint(m);
                    //System.out.println("m location"+m.getPoint());
                    menu_Lock_Size.setState(!resizeable_Super);
                    menu_Lock_Position.setState(!moveable_Super);
                    menu.show(container, e.getX()+getLocation().x, e.getY()+getLocation().y);
                    setMeAsActiveFigure();
                }
            }
        });        
        menu = new JPopupMenu(){
            @Override
            public JMenuItem add(JMenuItem menuItem) {
                container.window.menu_list.add(menuItem);
                return super.add(menuItem); //To change body of generated methods, choose Tools | Templates.
            }
            
        };
        menu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {    
                doThisPopupMenuWillBecomeVisible(e);
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { 
                doThisPopupMenuWillBecomeInvisible(e);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) { 
                doThisPopupMenuCanceled(e);
            }
        });
        menu.setEnabled(true);
        add(menu);      
        menu_Scale = new JMenuItem(new AbstractAction(t.getText("FIGURE_SCALE")) {
            @Override
            public void actionPerformed(ActionEvent e) {      
                Object[] opt = {t.getText("SCALE_ABSOLUTE"),t.getText("SCALE_POINT"),t.getText("SCALE_CENTRE")/*,t.getText("SCALE_MOUSE_FIGURE"),t.getText("SCALE_MOUSE_CONTAINER")*/};
                Object pick = JOptionPane.showInputDialog(container.window,t.getText("SCALE_TYPE"),t.getText("SCALE_INPUT_TITLE"), JOptionPane.QUESTION_MESSAGE, null, opt,opt[0]);
                if(pick !=null){
                    int Scale_Type=0;
                    for(int i=0;i<opt.length;i++){
                        if(opt[i].equals(pick)){
                            Scale_Type = i;
                            break;
                        }
                    }
                    double v_x=container.window.showDoubleDialog(t.getText("SCALE_INPUT_X"),t.getText("SCALE_INPUT_TITLE"),1);
                    double v_y=container.window.showDoubleDialog(t.getText("SCALE_INPUT_Y"),t.getText("SCALE_INPUT_TITLE"),v_x);
                    scaleFigure(v_x,v_y,Scale_Type);
                }
            }
        });    
        menu.add(menu_Scale);       
        menu_Lock_Position = new JCheckBoxMenuItem(new AbstractAction(t.getText("FIGURE_LOCK_POSITION")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveable_Super = !menu_Lock_Position.isSelected();             
            }
        });
        menu.add(menu_Lock_Position);
        menu_Lock_Size = new JCheckBoxMenuItem(new AbstractAction(t.getText("FIGURE_LOCK_SIZE")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                resizeable_Super = !menu_Lock_Size.isSelected();             
            }
        });
    
        menu.add(menu_Lock_Size);
        
        menu_Figure = new JMenu(t.getText("FIGURE_POLYGON_LOOK"));    
        menu.add(menu_Figure);
        menu_Figure_Border_Colour = new JMenuItem(new AbstractAction(t.getText("FIGURE_BORDER_COLOUR")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                borderColour = container.window.showPickColor(borderColour);
                repaint();
            }
        });     
        menu_Figure.add(menu_Figure_Border_Colour);
        menu_Figure_Border_Thickness= new JMenuItem(new AbstractAction(t.getText("FIGURE_BORDER_THICKNESS")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                borderThickness = container.window.showNonNegativeIntegerDialog(t.getText("FIGURE_SPECIFIC_BORDER_PICK_THICKNESS"), t.getText("FIGURE_BORDER_PICK_THICKNESS_TITLE"), borderThickness);
                repaint();
            }
        });     
        menu_Figure.add(menu_Figure_Border_Thickness);
        menu_Figure_Fill_Colour = new JMenuItem(new AbstractAction(t.getText("FIGURE_FILL_COLOUR")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillColour = container.window.showPickColor(fillColour);
                repaint();
            }
        });    
        menu_Figure.add(menu_Figure_Fill_Colour);
        menu_Remove = new JMenuItem(new AbstractAction(t.getText("FIGURE_REMOVE")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.removeFigure(Figure.this);
            }
        });    
        menu.add(menu_Remove);
        
        MouseAdapter m = new MouseAdapter(){
            Point PreviousPoint;
            boolean drag = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                setMeAsActiveFigure();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                drag = true;
                //getParent().dispatchEvent(translateME(e, getLocation().x, getLocation().y));
                PreviousPoint = e.getLocationOnScreen();
                setMeAsActiveFigure();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                drag = true;
                super.mouseEntered(e); //To change body of generated methods, choose Tools | Templates.
                setMeAsActiveFigure();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                drag = false;
                super.mouseExited(e); //To change body of generated methods, choose Tools | Templates.
            }
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if((isResizeable())&&(mouse_position!=null)){
                    //System.out.println(""+e.getWheelRotation());                 
                    scaleFigure(1-((double)e.getWheelRotation()/20), 1-((double)e.getWheelRotation()/20), SCALE_CENTRE);
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                setRelativeMousePosition(e);
                getParent().dispatchEvent(translateME(e, getLocation().x, getLocation().y));
                setMeAsActiveFigure();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                setMeAsActiveFigure();
                //getParent().dispatchEvent(translateME(e, getLocation().x, getLocation().y));
                container.status_bar.updateMouseInfo(-container.relative_Position.x+container.getLocation().x +getLocation().x+e.getX(),-container.relative_Position.y+getLocation().y+container.getLocation().y+e.getY());
                Point current = e.getLocationOnScreen();  
                int dx = -PreviousPoint.x+current.x;
                int dy = -PreviousPoint.y+current.y;
                if(drag){
                    doThisWhenIAmDragged(e, dx, dy);
                }    
                PreviousPoint = current;
            }       
        };
        addMouseListener(m);
        addMouseMotionListener(m);
        addMouseWheelListener(m);
        //System.out.println("figures.Figure.<init>() border thickness:"+borderThickness);
        setPointVisibility(false);
    }
    
    

    
    
    
    
    
   //#########################abstract methods##################################
    
    
    /**
     * Returns amount of corners.
     * @return Amount of corners.
     */
    public abstract int getCornerCount();
    /**
     * Relocates polygon.
     */
    public abstract void relocate();
    /**
     * Changes size (and location) of polygon so it accommodates perfectly with points within him.
     */
    public abstract void resize();
    
    /**
     * This method will be executed before when popup menu is going to be shown.
     * @param m Mouse event, have translated mouse position relatively to container.
     */
    protected abstract void doThisWhenPopupMenuIsToBeShown(MouseEvent m);
    /**
     * This method will be executed when popup menu is being shown.
     * @param e Popup Menu Event.
     */    
    protected abstract void doThisPopupMenuWillBecomeVisible(PopupMenuEvent e);
    /**
     * This method will be executed when popup menu is being hidden.
     * @param e Popup Menu Event.
     */    
    protected abstract void doThisPopupMenuWillBecomeInvisible(PopupMenuEvent e);
    /**
     * This method will be executed when popup menu is canceled
     * @param e Popup Menu Event.
     */    
    protected abstract void doThisPopupMenuCanceled(PopupMenuEvent e);
    
    /**
     * This is done when mouse is dragged over point.
     * @param e Mouse Event.
     * @param point Point that is being dragged.
     * @param dx How much mouse was dragged on X axis.
     * @param dy How much mouse was dragged on Y axis.
     */
    public abstract void doThisWhenPointIsDragged(MouseEvent e,FigurePoint point,int dx, int dy);
    /**
     * This is done when mouse is dragged over this figure.
     * @param e Mouse Event.
     * @param dx How much mouse was dragged on X axis.
     * @param dy How much mouse was dragged on Y axis.
     */
    public abstract void doThisWhenIAmDragged(MouseEvent e,int dx, int dy);

    /**
     * This is done when mouse is pressed while being on figure point.
     * @param e Mouse Event
     * @param point Point that is being dragged.
     */
    protected abstract void doThisWhenMouseIsPressedOnPoint(MouseEvent e,FigurePoint point);

    /**
     * This is done when mouse is realeased while being on figure point.
     * @param e Mouse Event
     * @param point Point that is being dragged.
     */
    protected abstract void  doThisWhenMouseIsReleasedOnPoint(MouseEvent e,FigurePoint point);
    
    
    /**
     * Returns this figure name.
     * @param Language Language in which name will be returned.
     * @return Figure name.
     */
    public abstract String getFigureName(int Language);
    /**
     * Returns area of this polygon only if it is simple one, otherwise it returns -1.
     * @return Area of polygon, -1 if it is complex Polygon.
     */
    public abstract double getArea();
    
    /**
     * Returns perimeter of this polygon only if it is simple one, otherwise it returns -1.
     * @return Perimeter of polygon, -1 if it is complex Polygon.
     */
    public abstract double getPerimeter();
    
    
    
    
    /**
     * Returns text that allows to recreate this subclass specific parameters with {@link #recreateFromSaveSubclass(java.lang.String) }.
     * @return text.
     */
    protected abstract String getSaveableFormatSubclass();
  
    /**
     * Recreates subclass specific parameters from string  .
     * @param s text created with {@link #getSaveableFormat() }.
     * @throws figures.FigureException If given text is not recreatable.
     */
    protected abstract void recreateFromSaveSubclass(String s) throws FigureException;
    
    
}

