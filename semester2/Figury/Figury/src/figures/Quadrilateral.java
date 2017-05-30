/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package figures;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import main.Text;
import window.Configuration;
import window.FigurePanel;

/**
 *
 * @author n1t4chi
 */
public class Quadrilateral extends Polygon{

    
   //##########################static context###################################

    /**
     * Checks given tab of coordinates whether it has right size. Used for constructor.
     * @param t Array to check
     * @return Same array.
     * @throws FigureException When size is wrong.
     */
    private static double[] checkTab(double[] t) throws FigureException{
        if(t.length!=4){
            throw new FigureException( FigureException.FIGURE_EXCEPTION_WRONG_COORD_TAB_SIZE );
        }
        return t;
    }
    /**
     * Resizes array by one element and adds 0 at the end . Used for constructor.
     * @param t Array to remake.
     * @return Resized array.
     * @throws FigureException When tab is null.
     */
    private static double[] make4Tab(double[] t) throws FigureException{
        if(t!=null){
            if(t.length<4){
                t = Arrays.copyOf(t, t.length+1);
                t[t.length-1] = 0;
            }
            return t;
        }else{
            throw new FigureException( FigureException.FIGURE_EXCEPTION_NULL_POINTER );
        }
    }
    
    /**
     * Not restricted type of quadrilateral. 
     */
    public static final int QUADRILATERAL_OTHER = 0;
    /**
     * Parallelogram type of quadrilateral. 
     */
    public static final int QUADRILATERAL_PARALLELOGRAM = 1;
    /**
     * Rhomb type of quadrilateral. 
     */
    public static final int QUADRILATERAL_RHOMB = 2;
    /**
     * Rectangle type of quadrilateral. 
     */
    public static final int QUADRILATERAL_RECTANGLE = 3;
    /**
     * Square type of quadrilateral. 
     */
    public static final int QUADRILATERAL_SQUARE = 4;




   //##########################fields###########################################
    /**
     * Policy of locking opposite lines in parallel position.
     */
    private boolean lock_Parallel;
    /**
     * Policy of locking adjacent lines at right angle.
     */
    private boolean lock_Right;
    /**
     * Policy of locking all lines at same size.
     */
    private boolean lock_Equilateral;
    /**
     * Policy of locking all dragging points.
     */
    private boolean lock_Point_Drag;
    
    
    
    /**
     * Popup menu item. Locks opposite lines to be in parallel position.
     */
    protected final JCheckBoxMenuItem menu_Lock_Parallel;
    
    /**
     * Popup menu item. Locks adjacent lines to be perpendicular position.
     */
    protected final JCheckBoxMenuItem menu_Lock_Perpendicular;
    
    /**
     * Popup menu item. Locks all lines to be the same size.
     */
    protected final JCheckBoxMenuItem menu_Lock_Equilateral;
    
    
    
    
   //#########################methods###########################################
    
    /**
     * Checks if 2 lanes are parallel.
     * @param l1 First line.
     * @param l2 Second Line.
     * @return True if yes, False otherwise.
     */
    private boolean areLanesParallel(PolygonLine l1, PolygonLine l2){
        double dx1 = l1.getX1() - l1.getX2();
        double dx2 = l2.getX1() - l2.getX2();
        double dy1 = l1.getY1() - l1.getY2();
        double dy2 = l2.getY1() - l2.getY2();     
        
        //
        //System.err.println("l1:"+l1.toString(true)+"  l2:"+l2.toString(true)+"  "+(dx1/dy1) +" "+(dx2/dy2) );
        if((dy1==0)&&(dy2==0)){
            return true;
        }else{
           if((dy1==0)||(dy2==0))  {
               return false;
           }else{
                return (Math.abs((dx1/dy1)-(dx2/dy2))<0.001);  
           }       
        }
    }
    /**
     * Returns whether this figure is Parallelogram.
     * @return true if yes, no otherwise.
     */
    private boolean isParallelogram(){
        generateLines(false);
        //System.err.println(" one:"+areLanesParallel((PolygonLine)lines.get(0), (PolygonLine)lines.get(2))+" two:"+areLanesParallel((PolygonLine)lines.get(1), (PolygonLine)lines.get(3)));
        return areLanesParallel((PolygonLine)lines.get(0), (PolygonLine)lines.get(2))&&
               areLanesParallel((PolygonLine)lines.get(1), (PolygonLine)lines.get(3));
    }
    
    /**
     * Checks if 2 adjacent lanes are perpendicular.
     * @param l1 First line.
     * @param l2 Second Line.
     * @return True if yes, False otherwise.
     */
    private boolean areLanesPerpendicular(PolygonLine l1, PolygonLine l2){
        if( l1.has(l2.getPP1())||l1.has(l2.getPP2()) ){
            //point from L1 that does not belong to l2
            FigurePoint A = ( l2.has( l1.getPP1() ) )?l1.getPP2():l1.getPP1();
            //point from L2 that does not belong to l1
            FigurePoint B = ( l1.has( l2.getPP1() ) )?l2.getPP2():l2.getPP1();
            
            double AB = A.coordinates.distanceSq(B.coordinates);
            double L1 = l1.getPP1().coordinates.distanceSq(l1.getPP2().coordinates);
            double L2 = l2.getPP1().coordinates.distanceSq(l2.getPP2().coordinates);        
            
            return (Math.abs(AB-L1-L2)<0.001);
        }else{
            return false;
        }
    }
    /**
     * Returns whether this figure have all right angles.
     * @return true if yes, no otherwise.
     */
    private boolean isRight(){
        generateLines(false);
        return  areLanesPerpendicular((PolygonLine)lines.get(0), (PolygonLine)lines.get(1))&&
                areLanesPerpendicular((PolygonLine)lines.get(1), (PolygonLine)lines.get(2))&&
                areLanesPerpendicular((PolygonLine)lines.get(2), (PolygonLine)lines.get(3));
    }
    
    /**
     * Returns what type of quadrilateral this figure is.
     * @return type of quadrilateral.
     */
    private int whatQuadrilateralThisIs(){
        boolean r = isRight();
        boolean e = isEquilateral();
        boolean p = isParallelogram();
        if(p){
            if(r){
                if(e){
                    return QUADRILATERAL_SQUARE;
                }else{
                    return QUADRILATERAL_RECTANGLE;
                }
            }else{
                if(e){
                    return QUADRILATERAL_RHOMB;
                }else{
                    return QUADRILATERAL_PARALLELOGRAM;
                }
            }
        }else{
            return QUADRILATERAL_OTHER;
        }
    }
    /**
     * Validates this figure to current locks.
     */
    private void validateFigure(){
        lock_Point_Drag=lock_Right||lock_Parallel||lock_Equilateral;
        if(lock_Equilateral)
            validateEquilateral();
        if(lock_Right){
            lock_Parallel = lock_Right;
            validatePerpendicular();
        }else
            if(lock_Parallel)
                validateParallel();
        repaint();
    }
    /**
     * Validates this figure so its sides will be same size.
     */
    private void validateEquilateral(){
        //System.out.println("figures.Quadrilateral.validateEquilateral()");
        if(!isEquilateral()){
            generateLines(false);
            FigurePoint A = (FigurePoint)points.get(0);
            FigurePoint B = (FigurePoint)points.get(1);
            FigurePoint C = (FigurePoint)points.get(2);
            FigurePoint D = (FigurePoint)points.get(3);          
            double a = A.coordinates.distance(B.coordinates);  //new length of all sides
            double b = B.coordinates.distance(C.coordinates); //distance between B and C
            double Ax = A.coordinates.getX();
            double Ay = A.coordinates.getY();
            double Bx = B.coordinates.getX();
            double By = B.coordinates.getY();
            double Cx = C.coordinates.getX();
            double Cy = C.coordinates.getY();     
                    
            //System.out.println(" A("+Ax+","+Ay+") B("+B.coordinates.getX()+","+B.coordinates.getY()+")   C("+Cx+","+Cy+")  a="+a+" b="+b); 
            
            
            //System.out.println("d:"+a+"   d 2-3: "+b+" should do something?:"+(Math.abs(a-b)>0.001));
            //System.out.println("prev  point "+C.toString(true));
            //if(Math.abs(a-b)>0.001){
            if(Math.abs(a-b)>0.001){
                double dx = Cx-Bx; //difference on X axis between B and C
                double dy = Cy-By; //same as above on Y axis
                //System.out.println("dx:"+dx+"   dy: "+dy);
                if((dy==0)||(dx==0)){
                    if(dy==0){
                        //System.out.println("translate [11]  ("+(a-dx)+",0)");
                        //System.out.println("translate [12]  ("+(a+dx)+",0)");
                        double tx = (Math.abs(a-dx)<Math.abs(a+dx))?(a-dx):(a+dx);
                        //System.out.println("translate [13] tx ("+tx+",0)");
                        C.translate( tx ,0);
                    }else{
                        //System.out.println("translate [21]  (0,"+(a-dy)+")");
                        //System.out.println("translate [22]  (0,"+(a+dy)+")");
                        double ty = (Math.abs(a-dy)<Math.abs(a+dy))?(a-dy):(a+dy);
                        //System.out.println("translate [23] ty ("+ty+",0)");
                        C.translate( 0,ty);
                    }    
                }else{
                    double angle = Math.atan(dy/dx) + ((dx>0)?0:(Math.PI))  ; //forming angle of line between B and C   
                    
                    //System.out.println("relocate [3]  angle:"+(angle/Math.PI*180)+"  dx = "+Math.cos(angle)*(a)+"   dy ="+Math.sin(angle)*(a));
                    C.coordinates.setLocation( Bx+Math.cos(angle)*(a) , By+Math.sin(angle)*(a) );
                }          
            }
            //System.out.println("new  point "+C.toString(true));
            Cx = C.coordinates.getX();
            Cy = C.coordinates.getY();   
            
            double sx = ( Ax + Cx )/2 ; //coordinates of centre of polygon
            double sy = ( Ay + Cy )/2 ;
            double d_sx = Bx - sx;  //difference between centre of polygon and point 2
            double d_sy = By - sy;  
            D.coordinates.setLocation(sx-d_sx , sy-d_sy );     
        }
        //System.out.println("figures.Quadrilateral.validateEquilateral() is equilateral?"+isEquilateral());
    };
    /**
     * Validates this figure so its opposite sides will be in parallel positions.
     */
    private void validateParallel(){
        if(!isParallelogram()){
            generateLines(false);
            FigurePoint A = (FigurePoint)points.get(0);
            FigurePoint B = (FigurePoint)points.get(1);
            FigurePoint C = (FigurePoint)points.get(2);
            FigurePoint D = (FigurePoint)points.get(3);
            
            double a = A.coordinates.distance(B.coordinates);
            double b = B.coordinates.distance(C.coordinates);
            
            double Ax = A.coordinates.getX();
            double Ay = A.coordinates.getY();
            double Bx = B.coordinates.getX();
            double By = B.coordinates.getY();
            double Cx = C.coordinates.getX();
            double Cy = C.coordinates.getY();       
                    
            //System.out.println(" A("+Ax+","+Ay+") B("+B.coordinates.getX()+","+B.coordinates.getY()+")   C("+Cx+","+Cy+")  a="+a+"  b="+b); 
            // finding points that are on intersection of 2 circles
            // 1. circle: A as middle and b as radius
            // 2. circle: C as middle and a as radius
            // (x-Ax)^2 + (y-Ay)^2 - b^2 = (x-Cx)^2 + (y-Cy)^2 - a^2  =>
            // => 2x(Cx-Ax) + 2y(Cy-Ay) = b^2 - a^2 - Ax^2 -Ay^2 + Cx^2 + Cy^2 =>
            // => y = ( 2x(Ax-Cx)+ b^2 - a^2 - Ax^2 -Ay^2 + Cx^2 + Cy^2 ) / 2(Cy-Ay)   this is or line that points lie on.
            // lets use new variables y = hx + i
            double h = (Ax-Cx)/(Cy-Ay);
            double i = (b*b - a*a - Ax*Ax -Ay*Ay + Cx*Cx + Cy*Cy)/(2*(Cy-Ay));
            //System.out.println("hx+i = "+h+"x+"+i); 
            
            //now placing it into (x-Ax)^2 + (y-Ay)^2 = b^2
            //we get (x-Ax)^2 + (hx+i-Ay)^2 = b^2
            //let use new variable  j = i-Ay;
            double j = i - Ay;
            //System.out.println("j = "+j); 
            //then we have (x-Ax)^2 + (hx+j)^2 = b^2  = > (h^2+1)x^2 + 2(hj-Ax)x + Ax^2 + j^2 - b^2 = 0;
            //we get a0*x^2 + a1*x + a2 = 0;
            double a0 = h*h +1;
            double a1 = 2*(h*j-Ax);
            double a2 = Ax*Ax + j*j - b*b;
            //System.out.println("a0*x^2 + a1*x + a2 = "+a0+"x^2 + "+a1+"x"+a2 ); 
            //let's get our possible Dx's
            
            if(a0==0){
                if(a1==0){ //no possible outcome
                    lock_Parallel = false;              
                    //System.out.println("Nope");
                }else{ // we have only a1x + a2 = 0  
                    double Dx = -a2/a1;
                    //using y = hx+i;
                    double Dy = h*Dx+i;  
                    //System.out.println("D = ("+Dx+","+Dy+")");
                    D.coordinates.setLocation(Dx,Dy);
                }
            }else{ //solving quadratic formula
                double delta = a1*a1 - 4*a0*a2;
                    //System.out.println("delta = "+delta);
                if(delta < 0){
                    lock_Parallel = false;      
                    //System.out.println("Nope");
                }else{
                    if(delta == 0){
                        double Dx = -a1/(2*a0);
                        double Dy = h*Dx+i;
                    }else{
                        double deltaSQRT = Math.sqrt(delta);
                        //System.out.println("sqrt delta = "+deltaSQRT);
                        
                        double Dx1 = (-a1 - deltaSQRT)/(2*a0);                   
                        double Dy1 = h*Dx1+i;
                        
                        double Dx2 = (-a1 + deltaSQRT)/(2*a0);
                        double Dy2 = h*Dx2+i;
                        //System.out.println("D1 = ("+Dx1+","+Dy1+")");
                        //System.out.println("D2 = ("+Dx2+","+Dy2+")");
                        
                        // checking if lines from Point B to C intersects with lane from point A to new D
                        
                        
                       /* if(D.coordinates.distance(Dx1, Dy1)<D.coordinates.distance(Dx2, Dy2)){
                            D.coordinates.setLocation(Dx1, Dy1);
                        }else{
                            D.coordinates.setLocation(Dy2, Dy2);
                        }*/
                        
                        //System.out.println("figures.Quadrilateral.validateParallel()  BC AD1  "+(Line2D.linesIntersect(Bx, By, Cx , Cy , Ax, Ay, Dx1, Dy1))); 
                        //System.out.println("figures.Quadrilateral.validateParallel()  BC AD2  "+(Line2D.linesIntersect(Bx, By, Cx , Cy , Ax, Ay, Dx1, Dy1)));
                        //System.out.println("figures.Quadrilateral.validateParallel()  AC BD1  "+(Line2D.linesIntersect(Ax, Ay, Cx , Cy , Bx, By, Dx1, Dy1))); 
                        //System.out.println("figures.Quadrilateral.validateParallel()  AC BD2  "+(Line2D.linesIntersect(Ax, Ay, Cx , Cy , Bx, By, Dx1, Dy1)));
                        //System.out.println("figures.Quadrilateral.validateParallel()  AB CD1  "+(Line2D.linesIntersect(Bx, By, Ax , Ay , Cx, Cy, Dx1, Dy1))); 
                        //System.out.println("figures.Quadrilateral.validateParallel()  AB CD2  "+(Line2D.linesIntersect(Bx, By, Ax , Ay , Cx, Cy, Dx1, Dy1)));
                        
                        
                        if ((Line2D.linesIntersect(
                                  Bx, By, Cx , Cy //B to C
                                , Ax, Ay, Dx1, Dy1)  //A to D
                        )||(Line2D.linesIntersect(
                                  Ax, Ay, Bx , By //A to B
                                , Cx, Cy, Dx1, Dy1) //C to D
                        ))
                        {
                            D.coordinates.setLocation(Dx2,Dy2);    
                        }else{
                            //System.out.println("D2 is in intersection");
                            D.coordinates.setLocation(Dx1,Dy1);
                            //D.coordinates.setLocation(Dx2,Dy2);  //what the hell
                        }
                        
                        
                    }
                }
            }
        }
    };
    
    /**
     * Validates this figure so its adjacent sides will be in perpendicular positions.
     */
    private void validatePerpendicular(){
        if(!isRight()){
            generateLines(false);
            FigurePoint A = (FigurePoint)points.get(0);
            FigurePoint B = (FigurePoint)points.get(1);
            FigurePoint C = (FigurePoint)points.get(2);
            FigurePoint D = (FigurePoint)points.get(3);
            
            
            double a = A.coordinates.distance(B.coordinates);
            double b = B.coordinates.distance(C.coordinates);
            
            double Ax = A.coordinates.getX();
            double Ay = A.coordinates.getY();
            double Bx = B.coordinates.getX();
            double By = B.coordinates.getY();
            double Cx = C.coordinates.getX();
            double Cy = C.coordinates.getY();   
            //System.out.println(" A("+Ax+","+Ay+") B("+B.coordinates.getX()+","+B.coordinates.getY()+")   C("+Cx+","+Cy+")  a="+b+"  b="+b); 
            
            double Dx=0;
            double Dy=0;
            //lets get line
            //y=q*x+c1  which comes though A and B          
            boolean got_q = false;
            double q=0;  
            if((Bx-Ax)!=0){   
                got_q=true;    
                q = (By-Ay)/(Bx-Ax); 
                if((By-Ay)==0){
                    Dy = Cy;
                }
            }else{
                Dx = Cx;
            }
            double p = 0,w=0;
            
            if(got_q){
                if(q==0){
                    Cx = Bx;
                    Cy = By + b;
                    //System.out.println("[1]dist 1 "+C.coordinates.distance(Cx, Cy));
                    //System.out.println("[1]dist 2 "+C.coordinates.distance(Cx, By-b));
                    if(C.coordinates.distance(Cx, Cy)<b){
                        C.coordinates.setLocation(Cx, Cy);
                    }else{
                        C.coordinates.setLocation(Cx, By-b);
                    }
                }else{  
                    //let's make parallel y = p*x+w   which comes though B 
                    // By = p*Bx + w  -> w = 
                    p = -1/q;
                    w = By - p*Bx;
                    
                    double angle = Math.atan(p);
                    double dx = Math.cos(angle) * b;
                    double dy = Math.sin(angle) * b;
                    //System.out.println("[2]dist 1 "+C.coordinates.distance(Bx - dx, By - dy));
                    //System.out.println("[2]dist 2 "+C.coordinates.distance(Bx + dx, By + dy));
                    if(C.coordinates.distance(Bx - dx, By - dy)<b){
                        C.coordinates.setLocation(Bx - dx, By - dy);
                    }else{
                        C.coordinates.setLocation(Bx + dx, By + dy);
                    }
                }
            }else{
                p=0;
                Cy = By;
                Cx = Bx + b;
                //System.out.println("[3]dist 1 "+C.coordinates.distance(Cx, Cy));
                //System.out.println("[3]dist 2 "+C.coordinates.distance(Bx -b, Cy));
                
                if(C.coordinates.distance(Cx, Cy)<b){
                    C.coordinates.setLocation(Cx, Cy);
                }else{
                    C.coordinates.setLocation(Bx-b, Cy);
                }
            }
            Cx = C.coordinates.getX();
            Cy = C.coordinates.getY(); 
            
            //System.out.println("new C:"+C.toString(true));
            
            double dx = Cx - Bx;
            double dy = Cy - By;
             
            D.coordinates.setPoint(Ax + dx , Ay + dy);
        }
    };
    
    
    
    
    
    
                
   //#########################constructors######################################
    /**
     * Default constructor.
     * @param x X coordinates.
     * @param y Y coordinates.
     * @param border_c Border colour.
     * @param border_t Border thickness.
     * @param fill_c Fill colour.
     * @param config Configuration for Polygons.
     * @param t Text object.
     * @param container Container of this polygon.
     * @throws FigureException When: 
     * <br> - there is other amount of XY coordinates than 4.
     * <br> - one of pointers is null
     */
    public Quadrilateral(double[] x, double[] y, Color border_c, int border_t, Color fill_c, Configuration config, Text t,FigurePanel container) throws FigureException { 
        super(checkTab(x),checkTab(y),border_c,border_t,fill_c,config,t,container);
        menu_Point.setVisible(false);
        menu.remove(menu_Point);
        lock_Equilateral = false;
        lock_Parallel = false;
        lock_Right = false;
        menu_Lock_Equilateral = new JCheckBoxMenuItem(new AbstractAction(t.getText("QUADRILATERAL_LOCK_EQUILATERAL")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                lock_Equilateral = menu_Lock_Equilateral.getState();
                lock_Point_Drag=lock_Right||lock_Parallel||lock_Equilateral;
                if(lock_Equilateral){
                    validateEquilateral();
                }
            }
        });
        menu_Lock_Parallel = new JCheckBoxMenuItem(new AbstractAction(t.getText("QUADRILATERAL_LOCK_PARALLEL")) {
            @Override
            public void actionPerformed(ActionEvent e) { 
                lock_Parallel = menu_Lock_Parallel.getState();
                if(lock_Parallel){
                    validateParallel();
                }
            }
        });
        menu_Lock_Perpendicular = new JCheckBoxMenuItem(new AbstractAction(t.getText("QUADRILATERAL_LOCK_RIGHT")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                lock_Right = menu_Lock_Perpendicular.getState();
                lock_Point_Drag=lock_Right||lock_Parallel||lock_Equilateral;
                if(lock_Right){
                    validatePerpendicular();
                }    
                
            }
        });
        menu.add(menu_Lock_Equilateral);
        menu.add(menu_Lock_Parallel);
        menu.add(menu_Lock_Perpendicular);
               
        
    }
    
    /**
     * Constructor that uses default settings.
     * @param x X coordinates within rectangle
     * @param y Y coordinates within rectangle
     * @param config Configuration for rectangle
     * @param t Text object.
     * @param container Container of this rectangle
     * @throws FigureException When: 
     * <br> - there is other amount of XY coordinates than 4.
     * <br> - one of pointers is null
     */
    public Quadrilateral(double[] x, double[] y,Configuration config, Text t,FigurePanel container) throws FigureException {    
        this(x, y, config.getDefaultFigureBorderColour(), config.getDefaultFigureBorderThickness(), config.getDefaultFigureFillColour(), config,t,container);
    }     
    /**
     * Constructor. Needs only 3 coordinates [can be 4]. If only 3 points are given and no restrictions then  4th point will be made as (0,0).
     * @param x X coordinates within rectangle
     * @param y Y coordinates within rectangle
     * @param parallel makes opposite sides parallel.
     * @param equilateral makes sides same size.
     * @param right makes all angles right.
     * @param border_c Border colour.
     * @param border_t Border thickness.
     * @param fill_c Fill colour.
     * @param config Configuration for Polygons.
     * @param t Text object.
     * @param container Container of this polygon.
     * @throws FigureException When: 
     * <br> - there is other amount of XY coordinates than 3 or 4.
     * <br> - one of pointers is null
     */
    public Quadrilateral(double[] x, double[] y,boolean parallel,boolean equilateral,boolean right, Color border_c, int border_t, Color fill_c, Configuration config, Text t,FigurePanel container) throws FigureException { 
        this(make4Tab(x), make4Tab(y), border_c, border_t, fill_c, config, t, container);
        this.lock_Equilateral = equilateral;
        this.lock_Parallel = parallel;
        this.lock_Right = right;
        resize();
    }
    /**
     * Constructor.Used default style. Needs only 3 coordinates [can be 4]. If only 3 points are given and no restrictions then  4th point will be made as (0,0).
     * @param x X coordinates within rectangle
     * @param y Y coordinates within rectangle
     * @param parallel makes opposite sides parallel.
     * @param equilateral makes sides same size.
     * @param right makes all angles right.
     * @param config Configuration for Polygons.
     * @param t Text object.
     * @param container Container of this polygon.
     * @throws FigureException When: 
     * <br> - there is other amount of XY coordinates than 3 or 4.
     * <br> - one of pointers is null
     */
    public Quadrilateral(double[] x, double[] y,boolean parallel,boolean equilateral,boolean right, Configuration config, Text t,FigurePanel container) throws FigureException { 
        this(make4Tab(x), make4Tab(y), config, t, container);
        this.lock_Equilateral = equilateral;
        this.lock_Parallel = parallel;
        this.lock_Right = right;
        resize();
    }
    
    
    
    
    //#########################overriden methods#################################
    
    @Override
    protected void doThisWhenPopupMenuIsToBeShown(MouseEvent e) {
        menu_Lock_Equilateral.setState(lock_Equilateral);
        menu_Lock_Parallel.setState(lock_Parallel);
        menu_Lock_Perpendicular.setState(lock_Right);       
        super.doThisWhenPopupMenuIsToBeShown(e);
    }

    @Override
    public boolean addPoint(FigurePoint p, int index) {
        if(points.size()<4){
            return super.addPoint(p, index);
        }else{ 
            return false;
        }
    }

    @Override
    public boolean addPoint(FigurePoint p) {
        if(points.size()<4){
            return super.addPoint(p);
        }else{ 
            return false;
        }
    }

    @Override
    protected String getSaveableFormatSubclass() {
        String rtrn = 
                "["+this.lock_Right+"]"+
                "["+this.lock_Parallel+"]"+
                "["+this.lock_Equilateral+"]"+
                "%"
                +super.getSaveableFormatSubclass();
        return rtrn;
    }

    @Override
    protected void recreateFromSaveSubclass(String s) throws FigureException {  
        //System.out.println("figures.Quadrilateral.recreateFromSaveSubclass() getting :"+s);    
        try{
            boolean right = Boolean.parseBoolean( s.substring(s.indexOf("[")+1,s.indexOf("]"))   );
            s = s.substring(s.indexOf("]")+1);
            boolean parallel = Boolean.parseBoolean( s.substring(s.indexOf("[")+1,s.indexOf("]"))   );
            s = s.substring(s.indexOf("]")+1);
            boolean equilateral = Boolean.parseBoolean( s.substring(s.indexOf("[")+1,s.indexOf("]"))   );
            lock_Right = right;
            lock_Parallel = parallel;
            lock_Equilateral = equilateral;
        }catch(IllegalArgumentException | IndexOutOfBoundsException ex){
            throw new FigureException(FigureException.FIGURE_EXCEPTION_BAD_SAVE_STRING_FORMAT);
        }   
        //System.out.println("figures.Quadrilateral.recreateFromSaveSubclass() rest :"+s);    
        s = s.substring(s.indexOf("%")+1);
       // System.out.println("figures.Quadrilateral.recreateFromSaveSubclass() giving :"+s);
        super.recreateFromSaveSubclass(s);
        if(points.size()!=4){
            throw new FigureException(FigureException.FIGURE_EXCEPTION_BAD_SAVE_STRING_FORMAT);
        }
    }

    
    
    @Override
    public String getFigureName(int Language) {
        //System.out.println("figures.Quadrilateral.getFigureName()");
        String rtrn="";
        switch(whatQuadrilateralThisIs()){
                case QUADRILATERAL_PARALLELOGRAM: 
                    rtrn = t.getText("PARALLELOGRAM");
                    break;
                case QUADRILATERAL_RHOMB: 
                    rtrn = t.getText("RHOMB");
                    break;
                case QUADRILATERAL_RECTANGLE: 
                    rtrn = t.getText("RECTANGLE");
                    break;
                case QUADRILATERAL_SQUARE: 
                    rtrn = t.getText("SQUARE");
                    break;
                case QUADRILATERAL_OTHER: default:
                    rtrn = t.getText("QUADRILATERAL");
                break;
        }
        return rtrn;
    }

    @Override
    public void doThisWhenPointIsDragged(MouseEvent e, FigurePoint point, int dx, int dy) {
        //if(!lock_Point_Drag)
        super.doThisWhenPointIsDragged(e, point, dx, dy); //To change body of generated methods, choose Tools | Templates.
        resize();
    }

    @Override
    public void scalePoints(double v_X, double v_Y, int x, int y, boolean absolute) {
        super.scalePoints(v_X, v_Y, x, y, absolute); //To change body of generated methods, choose Tools | Templates.
        resize();
        
    }

    @Override
    public void resize() {
        validateFigure();
        super.resize(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    @Override
    public void setPointVisibility(boolean visible){
        if(lock_Equilateral||lock_Parallel||lock_Right){
            for (Object p : points) {
                if(p instanceof FigurePoint){
                    //System.out.println("figures.Quadrilateral.setPointVisibility() is not last:"+(points.indexOf(p) != points.size()-1) +" visible policy:"+visible);
                    ((FigurePoint) p).setVisible(  (points.indexOf(p) != points.size()-1) && visible ); 
                }
            }
        }else{
            super.setPointVisibility(visible);
        }
    }
    
    
}
