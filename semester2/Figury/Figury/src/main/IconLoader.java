/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Class that loads icons.
 * @author n1t4chi
 */
public class IconLoader {

    
   //##########################static context###################################
    /**
     * Icon width/height.
     */
    public static final int ICON_SIZE = 25;
    /**
     * Icon ID for logo.
     */
    public static final int ICON_ID_LOGO=0;
    /**
     * Icon ID for circle.
     */
    public static final int ICON_ID_CIRCLE=1;
    /**
     * Icon ID for ellipse.
     */
    public static final int ICON_ID_ELLIPSE=2;
    /**
     * Icon ID for parallelogram.
     */
    public static final int ICON_ID_PARALLELOGRAM=3;
    /**
     * Icon ID for polygon.
     */
    public static final int ICON_ID_POLYGON=4;
    /**
     * Icon ID for quadrilateral.
     */
    public static final int ICON_ID_QUADRILATERAL=5;
    /**
     * Icon ID for rectangle.
     */
    public static final int ICON_ID_RECTANGLE=6;
    /**
     * Icon ID for rhomb.
     */
    public static final int ICON_ID_RHOMB=7;
    /**
     * Icon ID for square.
     */
    public static final int ICON_ID_SQUARE=8;

   //##########################fields###########################################
    /**
     * Array of icons.
     */
    final ImageIcon[] ic;

   //#########################methods###########################################
    /**
     * Returns specified icon.
     * @param Which ID of icon.
     * @return Icon,
     */
    public ImageIcon getIcon(int Which){
        try{
            return ic[Which];
        }catch(ArrayIndexOutOfBoundsException ex){
            return null;
        }
    }
    
    /**
     * Loads specified icon from resources/icons.
     * @param name icon name [without extension].
     * @return icon.
     */ 
    public ImageIcon loadIcon(String name){
        ImageIcon i = null;
        URL path = getClass().getResource( "/icons/"+name+".png"  );
        if(path!=null){
            i = new ImageIcon(path);
            //System.out.println("main.IconLoader.loadIcon() its working");
        }
        return i;
    }
    
    



   //#########################constructors######################################
    /**
     * Loads all icons.
     */
    public IconLoader() {
        ic = new ImageIcon[ICON_ID_SQUARE+1];
        for(int i=0;i<=ICON_ID_SQUARE;i++){
            String name="";
            switch(i){
                case ICON_ID_LOGO: name = "logo"; break;
                case ICON_ID_CIRCLE: name = "circle"; break;
                case ICON_ID_ELLIPSE: name = "ellipse"; break;
                case ICON_ID_PARALLELOGRAM: name = "parallelogram"; break;
                case ICON_ID_POLYGON: name = "polygon"; break;
                case ICON_ID_QUADRILATERAL: name = "quadrilateral"; break;
                case ICON_ID_RECTANGLE: name = "rectangle"; break;
                case ICON_ID_RHOMB: name = "rhomb"; break;
                case ICON_ID_SQUARE: name = "square"; break;
            }
            if(i>0)
                ic[i] = new ImageIcon(loadIcon(name).getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH)   );
            else
                ic[i] = loadIcon(name);
        }
    }
    
    
    
    
}
