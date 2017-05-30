/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package figures;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author n1t4chi
 */
public class PolygonLine extends java.awt.geom.Line2D{


   //##########################fields###########################################
    /**
     * Starting point.
     */
    private FigurePoint P1;
    /**
     * Ending point.
     */
    private FigurePoint P2;
    /**
     * Parent polygon of this line.
     */
    Polygon parent;
    
    
    

   //#########################methods###########################################

    
    /**
     * Returns first FigurePoint.
     * @return FigurePoint.
     */
    public FigurePoint getPP1() {
        return P1;
    }
    /**
     * Returns second FigurePoint.
     * @return FigurePoint.
     */
    public FigurePoint getPP2() {
        return P2;
    }
    
    /**
     * Sets points of this line.
     * @param p1 First point.
     * @param p2 Second point.
     */
    public void setLine(FigurePoint p1, FigurePoint p2) {
        P1=p1;
        P2=p2;
    }
    /**
     * Checks if this line has given FigurePoint as one end.
     * @param p point to check if it is one of end points.
     * @return True if yes, no otherwise.
     */
    public boolean has(FigurePoint p){
        return ((P1==p)||(P2==p));
    }  
    
    /**
     * Returns string representation of this object. If simple is true, then simple version will be returned.
     * @param simple is simple string should be returned.
     * @return String representation of the object.
     */
    public String toString(boolean simple) {
        if(simple)
            return P1.toString(true)+P2.toString(true);
        else    
            return toString();
    }
    
    
    
    
   //#########################constructors######################################
    /**
     * Constructor
     * @param p1 First point.
     * @param p2 Second point.
     * @param parent Parent polygon.
     */
    public PolygonLine(FigurePoint p1, FigurePoint p2,Polygon parent){
        setLine(p1, p2);
        this.parent=parent;
    }

    
    
    
    
    
   //#########################overriden methods#################################
    
    
    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle((int)Math.min(getX1(), getX2()),(int)Math.min(getY1(), getY2()),(int)Math.max(getX1(), getX2()),(int)Math.max(getY1(), getY2()));
    }

    @Override
    public String toString() {
        return "Line P1["+getX1()+"]["+getY1()+"] P2["+getX2()+"]["+getY2()+"]";
    }
    
    
    @Override
    public double getX1() {
        return P1.coordinates.getX();
    }

    @Override
    public double getY1() {
        return P1.coordinates.getY();
    }

    @Override
    public Point2D getP1() {
        return P1.coordinates.getPoint();
    }
    @Override
    public double getX2() {
        return P2.coordinates.getX();
    }

    @Override
    public double getY2() {
        return P2.coordinates.getY();
    }

    @Override
    public Point2D getP2() {
        return P2.coordinates.getPoint();
    }

    @Override
    @Deprecated
    public void setLine(double x1, double y1, double x2, double y2) {
        throw new UnsupportedOperationException("Not supported yet. And will never be."); //To change body of generated methods, choose Tools | Templates.
    }
}
