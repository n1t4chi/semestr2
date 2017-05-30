/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package figures;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import static window.Window.translateME;

/**
 * Class of drawable and controlable point.
 * @author n1t4chi
 */
public class FigurePoint extends javax.swing.JComponent{
    
   //##########################static context###################################
    /**
     * resize of a point.
     */    
    public static final int POLYGON_POINT_SIZE = 3;
    

   //##########################fields###########################################
    /**
     * Coordinates of FigurePoint.
     */  
    final public FPoint coordinates;    
    /**
     * Figure that this points belongs to.
     */    
    private final Figure parent;

   //#########################methods###########################################
    
    @Override
    public void setLocation(int x, int y) {
        //System.out.println("figures.FigurePoint.setLocation() "+this.getName()+"  coord:"+this.toString(true)+"  location ("+(x-POLYGON_POINT_SIZE+parent.relativeLocation.x)+","+(y-POLYGON_POINT_SIZE+parent.relativeLocation.y)+")");
        super.setLocation(x-POLYGON_POINT_SIZE+parent.relativeLocation.x, y-POLYGON_POINT_SIZE+parent.relativeLocation.y); //To change body of generated methods, choose Tools | Templates.
    }
    /**
     * Scales this point by given XY vector.
     * @param v_X X coordinate.
     * @param v_Y Y coordinate. 
     */
    public void scale(double v_X, double v_Y ){
        coordinates.scale(v_X, v_Y);
    }
    /**
     * Adds given XY point coordinates to this point.
     * @param x X coordinate.
     * @param y Y coordinate. 
     */
    public void translate(int x, int y){
        coordinates.Translate(x, y);
        setLocation(coordinates.getPoint());
    }  
    /**
     * Adds given XY point coordinates to this point.
     * @param x X coordinate.
     * @param y Y coordinate. 
     */
    public void translate(double x, double y){
        coordinates.Translate(x, y);
        setLocation(coordinates.getPoint());
    }  
    /**
     * Returns string representation of this component. If simple is set as true, it returns only XY coordinates in (X,Y) format
     * @param simple Whether return simple version of string or no.
     * @return String representation of this object.
     */
    public String toString(boolean simple) {
        return (simple)?("("+(int)coordinates.getX()+","+(int)coordinates.getY()+")"):toString();
    }
    
    
    
    
    
    
    
   //#########################constructors######################################
    
    
    /**
     * Constructor for polygon point.
     * @param coord Coordinates.
     * @param parent Figure that this points belongs to.
     */
    public FigurePoint(FPoint coord,Figure parent) {
        this.coordinates = coord;
        this.parent=parent;
        this.setPreferredSize(new Dimension(POLYGON_POINT_SIZE*2+1,POLYGON_POINT_SIZE*2+1));
        this.setSize(this.getPreferredSize());       
        this.setVisible(false);
        this.setLocation(coord.getPoint());
        
        
        
        MouseAdapter m = new MouseAdapter() {
            Point PreviousPoint;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                parent.dispatchEvent(e);
            }
            
            
            
            @Override
            public void mousePressed(MouseEvent e) {
                parent.dispatchEvent(translateME(e, getLocation().x, getLocation().y));
                parent.doThisWhenMouseIsPressedOnPoint(e,FigurePoint.this);
                //System.out.println("pos"+coordinates.toString());
                PreviousPoint = e.getLocationOnScreen();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                parent.dispatchEvent(translateME(e, getLocation().x, getLocation().y));
                parent.doThisWhenMouseIsReleasedOnPoint(e,FigurePoint.this);
                parent.setMoveable(true);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                parent.dispatchEvent(translateME(e, getLocation().x, getLocation().y));
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                parent.dispatchEvent(translateME(e, getLocation().x, getLocation().y));
                //System.out.println("Mouse pos:"+absolute_e.getPoint());
                Point current = e.getLocationOnScreen();  
                parent.doThisWhenPointIsDragged(e,FigurePoint.this,-PreviousPoint.x+current.x, -PreviousPoint.y+current.y);  
                PreviousPoint = current;
            }    
        };
        addMouseMotionListener(m);      
        addMouseListener(m);
        addMouseWheelListener(m);    
    }
    
    
    
   //#########################overriden methods#################################
    
    @Override
    protected void paintComponent(Graphics g) {
        setLocation(coordinates.getPoint());
        g.setColor(new Color(this.hashCode()));
        g.fillOval(0,0, POLYGON_POINT_SIZE*2+1, POLYGON_POINT_SIZE*2+1);
    }
    @Override
    public String toString() {
        return "Point ["+coordinates.getX()+"]["+coordinates.getY()+"]";
    }

    
}
