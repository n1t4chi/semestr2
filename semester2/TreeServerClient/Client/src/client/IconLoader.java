/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

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
     * Icon ID for insert.
     */
    public static final int ICON_INSERT=0;
    /**
     * Icon ID for delete.
     */
    public static final int ICON_DELETE=1;
    /**
     * Icon ID for select.
     */
    public static final int ICON_SELECT=2;
    /**
     * Icon ID for new.
     */
    public static final int ICON_NEW=3;

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
        URL path = getClass().getResource( "/images/"+name+".png"  );
        if(path!=null){
            i = new ImageIcon(path);
        }
        return i;
    }
    
    



   //#########################constructors######################################
    /**
     * Loads all icons.
     */
    public IconLoader() {
        ic = new ImageIcon[ICON_NEW+1];
        for(int i=0;i<=ICON_NEW;i++){
            String name="";
            switch(i){
                case ICON_INSERT: name = "insert"; break;
                case ICON_DELETE: name = "delete"; break;
                case ICON_SELECT: name = "select"; break;
                case ICON_NEW: name = "new"; break;
            }
            ic[i] = new ImageIcon(loadIcon(name).getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH)   );
        }
    }
    
    
    
    
}
