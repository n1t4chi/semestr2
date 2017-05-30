/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package figures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.PopupMenuEvent;
import main.Text;
import window.Configuration;
import window.FigurePanel;
import static window.Window.translateME;

/**
 * Class of drawable and editable oval.
 * @author n1t4chi
 */
public class Ellipse extends Figure{
    
   //##########################static context###################################
          
    /**
     * Makes 2 element array out of one variable and difference between first and second.
     * @param x 1. Variable
     * @param dx Difference between elements.
     * @return 2 element array.
     */
    private static double[] makeConstructorTab(double x,double dx){ 
        double[] rtrn = {x,x-dx};
        return rtrn;
    };
    
    
    
   //##########################fields###########################################
    
    /**
     * Policy of changing location of Forming Point in such way that ellipse will stay being a circle.
     */   
    private boolean force_circle;
    
    /**
     * Popup menu item. Locks figure to circle.
     */
    protected final JCheckBoxMenuItem menu_Lock_Circle;
       
     
    
    
    
    
   //#########################methods###########################################
    /**
     * Returns whether this ellipse is a circle.
     * @return True if this ellipse is a circle, False otherwise.
     */
    public boolean isCircle(){
        return (  Math.abs(getXRadius()-getYRadius())<0.1 );
    }    
    
    
 
    /**
     * Changes shape of this ellipse to circle. It will reshape to the smallest size.
     */
    private void validateCircle(){
        if(!isCircle()){
            FPoint p = getCenterPoint().coordinates;
            double d = Math.min(getXRadius(), getYRadius());
            double sig_x = (isFormingPointToTheLeft())?-1:1;
            double sig_y = (isFormingPointBelowCenter())?-1:1;
            getFormingPoint().coordinates.setLocation(p.getX() + sig_x*d   , p.getY() + sig_y*d);
        }
    }
    
    /**
     * Translates figure.
     * @param dx X difference
     * @param dy Y difference.
     */
    private void circleTranslation(int dx, int dy) {
        validateCircle();
        
        boolean strong_x = (dx!=0)&&(Math.abs(dx)>=Math.abs(dy));
        //double scale;
        //System.out.println("figures.Ellipse.circleTranslation() d("+dx+","+dy+")  strong_x "+strong_x);
        double d;
        if(strong_x){
            //if(isFormingPointToTheLeft()){
                //scale = 1.0- 3*(double)dx/getXRadius();
                d = dx;
            //}else{
                //scale = 1.0+ 3*(double)dx/getXRadius();
            //}    
        }else{
            //if(isFormingPointBelowCenter()){
                //scale = 1.0- 3*(double)dy/getXRadius();
                d = dy;
            //}else{
                //scale = 1.0+ 3*(double)dy/getXRadius();
            //}
        }
        int sig_y;
        if( isFormingPointToTheLeft()!=isFormingPointBelowCenter() ){
            sig_y=-1;
        }else{
            sig_y=1;
        }
        getFormingPoint().coordinates.Translate(d, d*sig_y);
        //System.out.println("figures.Ellipse.circleTranslation() scale:"+scale);
        //scaleFigure(scale, scale, SCALE_CENTRE);      
        validateCircle();
    }

    
    
    
    
    /**
     * Returns center point.
     * @return Center pooint.
     */
    public FigurePoint getCenterPoint(){
        return (FigurePoint)points.get(0);
    }
    /**
     * Returns point that defines X and Y radius.
     * @return Forming point.
     */
    public FigurePoint getFormingPoint(){
        return (FigurePoint)points.get(1);
    }
    /**
     * Returns whether forming point is below center point.
     * @return True if yes, No otherwise [including at same level].
     */  
    public boolean isFormingPointBelowCenter(){
        return (getCenterPoint().coordinates.getY()>getFormingPoint().coordinates.getY());
    }    
    /**
     * Returns whether forming point is below center point.
     * @return True if yes, No otherwise [including at same level].
     */  
    public boolean isFormingPointToTheLeft(){
        return (getCenterPoint().coordinates.getX()>getFormingPoint().coordinates.getX());
    }    
    /**
     * Returns radius on X axis.
     * @return Radius on X axis.
     */
    public double getXRadius(){
        return Math.abs(getCenterPoint().coordinates.getX() - getFormingPoint().coordinates.getX());
    }
    /**
     * Returns radius on Y axis.
     * @return Radius on Y axis.
     */
    public double getYRadius(){
        return Math.abs(getCenterPoint().coordinates.getY() - getFormingPoint().coordinates.getY());
    }  

    
    
   //#########################constructors######################################
    
    
    /**
     * Default constructor.
     * @param x X coordinates of the centre of oval. 
     * @param y Y coordinates of the centre of oval
     * @param width_div_2 Half of height of oval.
     * @param height_div_2 Half of width of oval.
     * @param border_c Border colour.
     * @param border_t Border thickness.
     * @param fill_c Fill colour.
     * @param config Configuration for oval.
     * @param t Text object.
     * @param container Container of this oval.
     * @throws FigureException When list of coordinates are not equal in POLYGON_POINT_SIZE, or when there are not enough points.
     */
    public Ellipse(double x, double y,double width_div_2,double height_div_2, Color border_c, int border_t, Color fill_c, Configuration config, Text t, FigurePanel container) throws FigureException {
        super( makeConstructorTab(x,width_div_2),makeConstructorTab(y,height_div_2) , border_c, border_t, fill_c, config, t, container);
        
        //System.out.println("figures.Ellipse.<init>() points: "+getCenterPoint().toString(true)+", "+getFormingPoint().toString(true));
        //resizeable_Super=false;  
        //moveable_Super=false;
        menu_Lock_Circle = new JCheckBoxMenuItem(new AbstractAction(t.getText("FIGURE_LOCK_CIRCLE")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                force_circle=menu_Lock_Circle.getState();
                if(force_circle)
                    validateCircle();
                    repaint();
            }
        });
        menu.add(menu_Lock_Circle);
        setVisible(true);
        resize();
    }
    
    
    /**
     * Constructor. Uses default border and colours.
     * @param x X coordinates of the centre of oval. 
     * @param y Y coordinates of the centre of oval
     * @param width_div_2 Half of height of oval.
     * @param height_div_2 Half of width of oval.
     * @param config Configuration for oval.
     * @param t Text object.
     * @param container Container of this oval.
     * @throws FigureException When list of coordinates are not equal in POLYGON_POINT_SIZE, or when there are not enough points.
     */
    public Ellipse(double x, double y,double width_div_2,double height_div_2, Configuration config, Text t, FigurePanel container) throws FigureException {
        this( x,y,width_div_2,height_div_2, config.getDefaultFigureBorderColour(), config.getDefaultFigureBorderThickness(), config.getDefaultFigureFillColour(), config, t, container);         
    }
    
    /**
     * Constructor. Makes circle. Uses default border and colours.
     * @param x X coordinates of the centre of oval. 
     * @param y Y coordinates of the centre of oval
     * @param radius radius of circle.
     * @param config Configuration for oval.
     * @param t Text object.
     * @param container Container of this oval.
     * @throws FigureException When list of coordinates are not equal in POLYGON_POINT_SIZE, or when there are not enough points.
     */
    public Ellipse(double x, double y,double radius, Configuration config, Text t, FigurePanel container) throws FigureException {
        this( x,y, radius , config.getDefaultFigureBorderColour(), config.getDefaultFigureBorderThickness(), config.getDefaultFigureFillColour(), config, t, container);  
        force_circle=true;
    }
    
    
    /**
     * Constructor. Makes circle.
     * @param x X coordinates of the centre of oval. 
     * @param y Y coordinates of the centre of oval
     * @param radius radius of circle.
     * @param border_c Border colour.
     * @param border_t Border thickness.
     * @param fill_c Fill colour.
     * @param config Configuration for oval.
     * @param t Text object.
     * @param container Container of this oval.
     * @throws FigureException When list of coordinates are not equal in POLYGON_POINT_SIZE, or when there are not enough points.
     */
    public Ellipse(double x, double y,double radius, Color border_c, int border_t, Color fill_c, Configuration config, Text t, FigurePanel container) throws FigureException {
        this( x,y,radius,radius , border_c, border_t, fill_c, config, t, container);
    }

    @Override
    public void scalePoints(double v_X, double v_Y, int x, int y, boolean absolute) {
        super.scalePoints(v_X, v_Y, x, y, absolute); //To change body of generated methods, choose Tools | Templates.
        if(force_circle){
            validateCircle();
        }    
    }
    
    
    
    
    
   //#########################overriden methods#################################
    
    @Override
    public boolean addPoint(FigurePoint p, int index) {
        if(points.size()<2){
            return super.addPoint(p, index); //To change body of generated methods, choose Tools | Templates.
        }else{
            return false;
        }    
    }
    
    @Override
    public boolean addPoint(FigurePoint p) {
        if(points.size()<2){
            if(points.isEmpty()){
                p.setName("Center");
            }else{

                p.setName("Forming");
            }            
            return super.addPoint(p); //To change body of generated methods, choose Tools | Templates.
        }else{
            return false;
        }    
        
    }
    @Override
    public Dimension getFigureSize() {
        return new Dimension((int)(getXRadius()*2), (int)(getYRadius()*2));
    }

    @Override
    public Point getFigureLocation() {
        //System.out.println("figures.Ellipse.getFigureLocation() center:"+getCenterPoint().toString(true));
        //System.out.println("figures.Ellipse.getFigureLocation() forming:"+getFormingPoint().toString(true));
        //System.out.println("figures.Ellipse.getFigureLocation() radiuses: x:"+getXRadius()+" , y:"+getYRadius());
        return new Point( (int)(getCenterPoint().coordinates.getX()-getXRadius()) ,(int)(getCenterPoint().coordinates.getY()-getYRadius()));
    }    
    @Override
    protected void paintComponent(Graphics g) {  
        resize();
        //System.out.println("figures.Ellipse.paintComponent()");
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke((float)borderThickness ));
        g2.setColor(fillColour);
        //System.out.println("figures.Ellipse.paintComponent()  oval: ("+ 0+","+0+","+(int)getXRadius()*2+","+(int)getYRadius()*2+")"    );
        g2.fillOval(FREE_XY_SPACE,FREE_XY_SPACE, (int)getXRadius()*2, (int)getYRadius()*2 ) ;
        g2.setColor(borderColour);
        g2.drawOval(FREE_XY_SPACE,FREE_XY_SPACE, (int)getXRadius()*2, (int)getYRadius()*2 ) ;
        super.paintComponent(g);
    }

    @Override
    public double getArea() {
        return Math.PI*getXRadius()*getYRadius();
    }
    
    @Override
    public double getPerimeter() {
        return Math.PI*( 3.0/2*(getXRadius()+getYRadius()) - Math.sqrt(getXRadius()*getYRadius() )   );
    }

    @Override
    public String getFigureName(int Language) {
        return ((isCircle())?(t.getText("CIRCLE")):(t.getText("ELLIPSE")));
    }

    @Override
    public int getCornerCount() {
        return 0;
    }

    @Override
    public void relocate() {
        //setLocation(getFigureLocation());
        //System.out.println("figures.Ellipse.relocate()  Figure location: ("+this.getLocation()+")");
        //System.out.println("figures.Ellipse.relocate()  to be changed to ("+(getFigureLocation().x+container.relative_Position.x)+","+(getFigureLocation().y+container.relative_Position.y)+")");
        setLocation(getFigureLocation().x+container.relative_Position.x-FREE_XY_SPACE,getFigureLocation().y+container.relative_Position.y-FREE_XY_SPACE);
        relativeLocation.setLocation(-getFigureLocation().x+FREE_XY_SPACE,-getFigureLocation().y+FREE_XY_SPACE);
    }

    @Override
    public void resize() {
        relocate();
        setSize(getFigureSize().width+FREE_XY_SPACE*2,getFigureSize().height+FREE_XY_SPACE*2);
    }

    @Override
    protected void doThisWhenPopupMenuIsToBeShown(MouseEvent m) {
        menu_Lock_Circle.setState(force_circle);
    }

    @Override
    protected void doThisPopupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    @Override
    protected void doThisPopupMenuWillBecomeInvisible(PopupMenuEvent e) {}

    @Override
    protected void doThisPopupMenuCanceled(PopupMenuEvent e) {}

    @Override
    public void doThisWhenPointIsDragged(MouseEvent e, FigurePoint point, int dx, int dy) {
        FPoint p = askParentIsMouseNearPoint(translateME(e, -container.relative_Position.x+point.getLocation().x+getLocation().x, -container.relative_Position.y+point.getLocation().y+getLocation().y),point);  
        if(point.equals(getCenterPoint())){
            //System.out.println("figures.Ellipse.doThisWhenPointIsDragged() center d("+dx+","+dy+") is movable:"+isMoveable());
            if(p==null){
                translatePoints(dx, dy);
            }else{
                translatePoints((int)( -point.coordinates.getX()+p.getX()) ,(int)(-point.coordinates.getY()+p.getY())) ;
            }
        }else{
            if(point.equals(getFormingPoint())){
                //System.out.println("figures.Ellipse.doThisWhenPointIsDragged() forming");
                if(isResizeable()){
                    if(force_circle){
                        //System.out.println("figures.Ellipse.doThisWhenPointIsDragged() changing size as circle");
                        if((dx!=0)&&(dy!=0)){
                            circleTranslation(dx,dy);
                        }
                    }else{    
                        if(p==null){
                            point.translate(dx, dy);
                        }else{
                            point.coordinates.setLocation(p.getX(),p.getY());
                        } 
                    }
                }    
            }    
        }    
        repaint();
    }

    @Override
    public void doThisWhenIAmDragged(MouseEvent e, int dx, int dy) {
        if(isMoveable()){
            //System.out.println("figures.Ellipse.doThisWhenIAmDragged()");
            translatePoints( (Math.abs(dx)<4)?dx:(int)Math.signum(dx)*4 , (Math.abs(dy)<4)?dy:(int)Math.signum(dy)*4   );
            //System.out.println("figures.Ellipse.doThisWhenIAmDragged() center:"+getCenterPoint().toString(true));
            repaint();
        }
    }

    @Override
    protected void doThisWhenMouseIsPressedOnPoint(MouseEvent e ,FigurePoint p) {
        if(getFormingPoint().equals(p)){
            setMoveable(false);
            
        }    
    }

    @Override
    protected void doThisWhenMouseIsReleasedOnPoint(MouseEvent e ,FigurePoint p) {
        if(getFormingPoint().equals(p)){
            setMoveable(true);        
        }     
    }

    @Override
    public String getInfoForStatusBar() {
        DecimalFormat f = new DecimalFormat("#0.00");
        return super.getInfoForStatusBar() 
                + (
                    (isCircle())?
                        (" radius "+f.format(getXRadius()))
                    :
                        (" X radius " +f.format(getXRadius()) + " Y radius" + f.format(getYRadius()))
                )
                +" center:"+getCenterPoint().toString(true)
                +" location:"+getFormingPoint().toString(true)              
                ;
    }

    @Override
    protected String getSaveableFormatSubclass() {
        return    "("+this.getCenterPoint().coordinates.getX()+","+this.getCenterPoint().coordinates.getY()+")"
                + "("+this.getFormingPoint().coordinates.getX()+","+this.getFormingPoint().coordinates.getY()+")"
                + "["+this.force_circle+"]"   
                ;
    }

    @Override
    protected void recreateFromSaveSubclass(String s) throws FigureException {
        try{
            double c_x = Double.parseDouble(s.substring(s.indexOf("(")+1,s.indexOf(",")));
            double c_y = Double.parseDouble(s.substring(s.indexOf(",")+1,s.indexOf(")")));
            s = s.substring(s.indexOf(")")+1);
            double f_x = Double.parseDouble(s.substring(s.indexOf("(")+1,s.indexOf(",")));
            double f_y = Double.parseDouble(s.substring(s.indexOf(",")+1,s.indexOf(")")));
            s = s.substring(s.indexOf(")")+1);
            boolean circle = Boolean.parseBoolean( s.substring(s.indexOf("[")+1,s.indexOf("]"))   );
            
            getCenterPoint().coordinates.setLocation(c_x, c_y);
            getFormingPoint().coordinates.setLocation(f_x, f_y);            
            force_circle = circle;
            repaint();
        }catch(NumberFormatException | IndexOutOfBoundsException ex){
            throw new FigureException(FigureException.FIGURE_EXCEPTION_BAD_SAVE_STRING_FORMAT);
        }
    }

    
}
