/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import figures.FigureException;
import figures.Ellipse;
import figures.Polygon;
import figures.Quadrilateral;
import java.awt.HeadlessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import window.Configuration;
import window.Window;

/**
 * Class that runs this application.
 * @author n1t4chi
 */
public class run {
    
   //##########################static context###################################
    /**
     * initialises and shows window.
     */
    private static void runGUI(){
        w = new Window();
        w.setVisible(true);
    }  
    /**
     * Places test figures onto panel.
     */
    private static void testGUI(){
        if(w!=null){
            double x=20;
            double y=20;
            double[] xx = {0,     0 ,   (0+x) ,(0+x)   , (0+3*x) , (0+3*x) , (0+1*x) , (0+1*x) , (0+3*x) , (0+3*x) };
            double[] yy = {0 ,5*y ,  5*y  ,  0+3*y ,   0+3*y ,   0+2*y ,   0+2*y ,   0+1*y ,   0+1*y , 0 };    
            double[] xf = {0,   0 ,   1*x ,2*x   ,   3*x , 3*x  , 2*x ,   2*x , x*3/2 ,    x, x };
            double[] yf = {0 ,4*y ,  5*y  ,  5*y ,   4*y ,   0  ,   0 ,   3*y+y/2 ,     4*y , 3*y+y/2 , 0};       
            double[] rx = {0, 0 , 20, 30 };
            double[] ry = {0 ,30,20,0};                         
            try {
                //System.out.println(w.config.getDefaultFigureBorderThickness());
                Polygon p = new Polygon(xx,yy,w.config,w.t,w.container);
                //w.container.addFigure(p);
                p = new Polygon(xf,yf,w.config,w.t,w.container);
                //w.container.addFigure(p);
                Quadrilateral r = new Quadrilateral(rx,ry, w.config,w.t,w.container);
                w.container.addFigure(r);
                Ellipse e = new Ellipse(50, 50, 30, w.config,w.t,w.container);
                //w.container.addFigure(e);
            } catch (FigureException ex) {
                Logger.getLogger(run.class.getName()).log(Level.SEVERE, null, ex);
            }    
        }
    }
    
    /**
     * Window objeect.
     */
    static Window w;
    
    /**
     * Main method. Does not need arguments. Optional arguments below:<br>
     * 1. argument - If  TRUE [case insensitive] then borders of components will be drawn.<br>
     * 2. argument - If  TRUE [case insensitive] then test figures will be placed into window.<br>
     * @param args Application arguments.
     */
    public static void main(String[] args)  {
        
        if(args.length>0){
            Configuration.DEFAULT_VALUE_DRAW_BORDER = args[0].equalsIgnoreCase("TRUE");
        }    
        try{
            
            runGUI();  
            if((args.length>1)&&(args[1].equalsIgnoreCase("TRUE"))){
                testGUI();
            }          
        }catch(HeadlessException ex){
            System.err.println("GUI not supported");
        }
    }
}
