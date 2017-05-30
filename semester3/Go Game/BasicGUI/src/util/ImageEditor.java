/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Image editor class. Allows for transforming images/image icons and loads them from resources.
 * Resource path example: For image White.png in folder images.board, path will be images/board/White.png
 * @author n1t4chi
 */
public class ImageEditor {
    /**
     * Scales ImageIcon to given width and height in pixels
     * @param icon ImageIcon
     * @param width New icon width
     * @param height New icon height
     * @return New scaled image icon. Null when image is null or width/height are negative
     */
    public static final ImageIcon RescaleImageIcon(ImageIcon icon,int width, int height){
        if((icon!=null)&&(width>0)&&(height>0)){     
            return new ImageIcon(RescaleImage(icon.getImage(), width , height));
        }else return null;    
    }
    /**
     * Scales Image to given width and height in pixels
     * @param img Image
     * @param width New image width
     * @param height New image height
     * @return New scaled image. Null when image is null or width/height are negative
     */
    public static final Image RescaleImage(Image img,int width, int height){
        if((img!=null)&&(width>0)&&(height>0)){     
            return img.getScaledInstance(width,height,ScalingQuality);
        }else return null;    
    }
    
    /**
     * Scales ImageIcon width and height by given number.
     * @param icon ImageIcon
     * @param width Width resize scale
     * @param height Height resize scale
     * @return New scaled image icon. Null when image is null or width/height are negative
     */
    public static final ImageIcon RescaleImageIcon(ImageIcon icon,double width, double height){
        if((icon!=null)&&(width>0)&&(height>0)){     
            return new ImageIcon(RescaleImage(icon.getImage(), width, height));
        }else return null;    
    }
    
    /**
     * Scales Image width and height by given number.
     * @param img Image
     * @param width Width resize scale
     * @param height Height resize scale
     * @return New scaled image. Null when image is null or width/height are negative
     */
    public static final Image RescaleImage(Image img,double width, double height){
        if((img!=null)&&(width>0)&&(height>0)){     
            return img.getScaledInstance((int)(img.getWidth(null)*width),(int)(img.getHeight(null)*height),ScalingQuality);
        }else return null;    
    }
    
    /**
     * Scales Image width and height by given number.
     * @param icon ImageIcon
     * @param type 0-none, 1-vertical, 2-horizontal 3-both
     * @return New scaled image. Null when image is null or width/height are negative
     */
    public static final ImageIcon FlipImageIcon(ImageIcon icon,FlipType type){
        if(icon!=null){     
            return new ImageIcon(FlipImage(icon, type));
        }else return null;    
    }
    
    /**
     * Enum for flipping images.
     */
    public static enum FlipType{NONE,VERTICAL,HORIZONTAL,BOTH};
    
    /**
     * Flips image.
     * @param img Image
     * @param type Type of flip.
     * @return New flipped image.
     */
    public static final Image FlipImage(ImageIcon img,FlipType type){
        if(img!=null){
            BufferedImage image = toBufferedImage(getEmptyImage(img.getIconWidth(), img.getIconHeight()));
            int x,y,w,h;
            switch(type){
                case VERTICAL:  x=-1; y=1;  w=1; h=0; break;
                case HORIZONTAL:  x=1;  y=-1; w=0; h=1; break;
                case BOTH:  x=-1; y=-1; w=1; h=1; break;
                default: x=1;  y=1;  w=0; h=0; break;
            }         
            Graphics2D g = image.createGraphics();
            g.drawImage(img.getImage(), image.getWidth()*w, image.getHeight()*h, image.getWidth()*x, image.getHeight()*y, null);
            g.dispose();
            return toImage(image);
        }else return null;
    }
    /**
     * Rotates an image.
     * @param img The image to be rotated
     * @param angle The angle in degrees
     * @return The rotated image
     */  
    public static final Image rotate(Image img, double angle){
        if(img!=null){
            double sin = Math.abs(Math.sin(Math.toRadians(angle)));
            double cos = Math.abs(Math.cos(Math.toRadians(angle)));
            int width = img.getWidth(null), height = img.getHeight(null);
            while((width==-1)||(height==-1)){
                width = img.getWidth(null);
                height= img.getHeight(null);
            }
            int new_width = (int) Math.floor(width*cos + height*sin),
                new_height = (int) Math.floor(height*cos + width*sin);

            BufferedImage bimg = toBufferedImage(getEmptyImage(new_width, new_height));
            Graphics2D g = bimg.createGraphics();
            g.translate((new_width-width)/2, (new_height-height)/2);
            g.rotate(Math.toRadians(angle), width/2, height/2);
            g.drawRenderedImage(toBufferedImage(img), null);
            g.dispose();
            return toImage(bimg);
        }else return null;
    }

    /**
     * Returns empty image with transparency
     * @param width width
     * @param height height
     * @return New transparent image
     */
    public static Image getEmptyImage(int width, int height){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return toImage(img);
    }
    
    /**
     * Converts a given Image into a BufferedImage
     * @param img The Image to be converted
     * @return converted image to BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img){
        if(img!=null){
            if (img instanceof BufferedImage) {
                return (BufferedImage) img;
            }
            // Create a buffered image with transparency
            BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            // Draw the image on to the buffered image
            Graphics2D bGr = bimage.createGraphics();
            bGr.drawImage(img, 0, 0, null);
            bGr.dispose();
            // Return the buffered image
            return bimage;
        }else return null;
        
    }
    /**
     * Converts a given BufferedImage into an Image
     * @param bimage The BufferedImage to be converted
     * @return The converted Image
     */
    public static Image toImage(BufferedImage bimage){
        if(bimage!=null){
            Image img = (Image) bimage;
            return img;
        }else return null;
    }
    public static int ScalingQuality = Image.SCALE_AREA_AVERAGING;
    
    /**
     * Returns scaled down Image to given width/height
     * @param path Resource path to given image
     * @param width width in pixels
     * @param height height in pixels
     * @return Image
     */
    public static final Image getImage(String path,int width, int height){
        Image image = getImage(path);
        if(image!=null){     
            return image.getScaledInstance(width,height,ScalingQuality);
        }    
        else 
            return null;
    }    
    /**
     * Returns scaled down Image Icon to given width/height
     * @param path Resource path to given image
     * @param width width in pixels
     * @param height height in pixels
     * @return Image Icon
     */
    public static final ImageIcon getImageIcon(String path,int width, int height){
        return new ImageIcon(getImage(path, width, height));
    }   
    /**
     * Returns scaled down Image Icon by percent of original size.
     * @param path Resource path to given image
     * @param width width scale
     * @param height height scale
     * @return Image 
     */
    public static final Image getImage(String path,double width, double height){
        Image image = getImage(path);
        if(image!=null){
            return image.getScaledInstance(Math.round((float)(image.getWidth(null)*width)), Math.round((float)(image.getHeight(null)*height)),ScalingQuality);
        }
        else 
            return null;
    }
    /**
     * Returns scaled down Image from given path by given scale.
     * @param path Resource path to given image
     * @param width width scale
     * @param height height scale
     * @return Image Icon
     */
    public static final ImageIcon getImageIcon(String path,double width, double height){
        return new ImageIcon(getImage(path, width, height));
    }    
    /**
     * Returns Image from given path.
     * @param path Resource path to given image
     * @return Image
     */
    public static final Image getImage(String path){
       //Start.Prnt(path);      
        ImageIcon image = getImageIcon(path);
        if(image!=null)
            return getImageIcon(path).getImage();
        else
            return null;
    }
    
     /**
     * Returns Image Icon from given path.
     * @param path Resource path to given image
     * @return New Image Icon
     */
    public final static ImageIcon getImageIcon(String path){
        return IE.getImageIconPrivate(path);
    }
    
    /**
     * Returns partially transparent copy of image.
     * @param img Image
     * @param alpha transparency setting between 0-255. For lower/higher it will return empty/non edited image
     * @return transparent image
     */
    public static Image TransparentImage(Image img,int alpha){
        if(img!=null){
            float rtrn=0;
            if(alpha>255){
                rtrn=1;
            }
            else
                if(alpha>=0){
                    rtrn=((float)alpha)/255f;
                }
            //Start.Prntl("Transparent Image alpha="+alpha+"  return="+rtrn);
            return TransparentImage(img,rtrn);
        }else return null;
    }
    /**
     * Returns partially transparent copy of image.
     * @param img Image
     * @param alpha transparency setting between 0-1. For lower/higher it will return empty/non edited image.
     * @return transparent image
     */
    public static Image TransparentImage(Image img,float alpha){
        if(img!=null){
            int type = AlphaComposite.SRC_OVER;
            //Start.Prnt("Transparent Image alpha="+alpha);
            if(alpha<0)
                alpha=0;
            if(alpha>1)
                alpha=1;
            //Start.Prntl("  changed to "+alpha);
            AlphaComposite composite =  AlphaComposite.getInstance(type, alpha);        
            BufferedImage bimg = toBufferedImage(getEmptyImage(img.getWidth(null), img.getHeight(null)));
            Graphics2D g = bimg.createGraphics();
            g.setComposite(composite);
            g.drawRenderedImage(toBufferedImage(img), null);
            g.dispose();
            return toImage(bimg);
        }else return null;
    }
    
    private static final ImageEditor IE = new ImageEditor();
     /**
     * Returns Image Icon from given path.
     * @param path Resource path to given image
     * @return new image
     */
    private ImageIcon getImageIconPrivate(String path){
       //Start.Prnt(path);        
        if((path!=null)&&(!path.equals(""))&&(!path.equals(" "))){
            ImageIcon image = null;  
            java.net.URL imgURL = getClass().getResource("/images/"+path);
            if(imgURL!=null){               
                image = new ImageIcon(imgURL);
                
            }else{
            }
            return image;           
        }else{ 
            return null;
        }    
    }
    
    
    
}
