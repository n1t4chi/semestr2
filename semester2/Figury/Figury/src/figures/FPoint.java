/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package figures;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Class of point coordinate.
 * @author n1t4chi
 */
public class FPoint extends Point2D{
    

   //##########################fields###########################################    
    /**
     * X coordinate.
     */
    private double coordinate_x;
    /**
     * Y coordinate
     */
    private double coordinate_y;
    
    
    
    
    

   //#########################methods###########################################
    
    /**
     * Scales this point by given XY vector.
     * @param v_X X coordinate.
     * @param v_Y Y coordinate. 
     */
    public void scale(double v_X, double v_Y ){
        coordinate_x*=v_X;
        coordinate_y*=v_Y;
    }
    /**
     * Translates this point by given XY vector.
     * @param dx X coordinate.
     * @param dy Y coordinate.
     */
    public void Translate(double dx, double dy){
        coordinate_x+=dx;
        coordinate_y+=dy;
    }
    /**
     * Returns location as a Point object.
     * @return Point
     */
    public Point getPoint(){
        return new Point((int)coordinate_x,(int)coordinate_y);
    }

  
    /**
     * Sets X coordinate.
     * @param x new X coordinate.
     */
    public void setX(double x) {
        this.coordinate_x = x;
    }
    /**
     * Sets Y coordinate.
     * @param y new Y coordinate.
     */
    public void setY(double y) {
        this.coordinate_y = y;
    }
    /**
     * Sets new coordinates.
     * @param x new X coordinate.
     * @param y new Y coordinate.
     */
    public void setPoint(double x, double y){
        this.coordinate_x = x;
        this.coordinate_y = y;
    }     
    /**
     * Sets new coordinates.
     * @param p new coordinates.
     */
    public void setPoint(FPoint p){
        this.coordinate_x = p.coordinate_x;
        this.coordinate_y = p.coordinate_y;
    }     
    
    
    
   //#########################constructors######################################
    
    
    /**
     * Default constructor, 
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public FPoint(double x, double y) {
        setPoint(x,y);
    }     

    
    
    
    
    
   //#########################overriden methods#################################
    
    
    @Override
    public void setLocation(double x, double y) {
        setPoint(x, y);
    }
    @Override
    public String toString() {
        return "X["+coordinate_x+"] Y["+coordinate_y+"]";
    }
      /**
     * Returns X coordinate.
     * @return X coordinate.
     */
    @Override
    public double getX() {
        return coordinate_x;
    }
    /**
     * Returns Y coordinate.
     * @return Y coordinate.
     */
    @Override
    public double getY() {
        return coordinate_y;
    }
    
    
}
