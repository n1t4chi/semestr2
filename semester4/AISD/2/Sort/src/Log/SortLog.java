/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Log;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author n1t4chi
 */
public class SortLog {
     public final PrintStream out;
    //private final HashMap<Integer,HashMap<Integer,Comparable[]>> history;
    private final ArrayList<Comparable[]> history;
    private final AtomicLong c_compare;
    private final AtomicLong c_swap;
    private final int N;
    public long getCompareCount() {
        return c_compare.get();
    }
    public long getSwapCount() {
        return c_swap.get();
    }
    public ArrayList<Comparable[]> getSortHistory() {
        return history;
    }
  /*  public HashMap<Integer,HashMap<Integer,Comparable[]>> getSortHistory() {
        return history;
    }*/
    
    /**
     * Default constructor
     * @param N Size of sorted array.
     * @param out output stream to print log to
     */
    public SortLog(int N,PrintStream out,boolean history) {
        this.out = out;
       // this.history = new HashMap<>();
        if(history)
            this.history = new ArrayList<>();
        else
            this.history = null;
        this.c_compare = new AtomicLong();
        this.c_swap = new AtomicLong();
        this.N=N;
    }
    
    /**
     * Increments swap counter in given log
     * @param log log
     */
    public static void addToHistory(Comparable[] tab/*,int step,int branch*/, SortLog log,boolean always){
        if(log!=null){
          /*  HashMap<Integer,Comparable[]> hm = log.history.get(step);
            if(hm==null){
                hm = new HashMap<>();
                log.history.put(step, hm);
            }
            hm.put(branch, tab.clone());*/
            if(log.history!=null)
            if(always || log.N<=20)
                log.history.add(tab.clone());
        }
    }
    /**
     * Increments swap counter in given log
     * @param log log
     */
    public static void incrementSwap(SortLog log){
        if(log!=null)
            log.c_swap.incrementAndGet();
    }
    /**
     * Increments compare counter in given log
     * @param log log
     */
    public static void incrementCompare(SortLog log){
        if(log!=null)
            log.c_compare.incrementAndGet();
    }
    /**
     * Prints array if given log is not null.
     * @param tab array to print
     * @param log logging check
     * @param always if logging is activated then whether the string will always be printed or only if sorted array size is no more than 10
     */
    public static void printlnTab(Comparable[] tab,SortLog log,boolean always){
        if(log!=null){
            if(log.out!=null)
            if(always || log.N<=20){
                boolean first = true;
                for(Comparable c : tab){
                    log.out.print(((first)?"":",")+c);  
                    first = false;
                }
                log.out.println();
            }
        }
    }
    /**
     * Prints counter summary for given log.
     * @param tab tab to print
     * @param log logging check
     * @param always if logging is activated then whether the string will always be printed or only if sorted array size is no more than 10
     */
    public static void printlnSummary(Comparable[] tab,SortLog log,boolean always){
        if(log!=null){
            if(log.out!=null)
            if(always || log.N<=20){
                
                String hist = (log.history!=null)?(" Log history size:"+log.history.size())+".":"";
                log.out.println("Compare count:"+log.getCompareCount()+". Swap count:"+log.getSwapCount()+"."+hist+" Array state:");
                printlnTab(tab,log,always);
            }
        }
    }
    /**
     * Prints texts if given log is not null.
     * @param text text to print
     * @param log logging check
     * @param always if logging is activated then whether the string will always be printed or only if sorted array size is no more than 10
     */
    public static void println(String text,SortLog log,boolean always){
        if(log!=null){
            if(log.out!=null)
            if(always || log.N<=20)
                log.out.println(text);
        }
    }
    
    public static BufferedImage historyToImage(SortLog log){
        if(log!=null){
          /*  LinkedHashMap<Integer, HashMap<Integer, Comparable[]>> h = log.history.entrySet().stream().sorted((e1,e2) -> e1.getKey().compareTo(e2.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
            if(h!=null&&h.size()>0){*/
            if(!log.history.isEmpty()){
                int width = log.history.size()*2;
                int height = log.N*2;
                BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = bi.createGraphics();
           /*     HashMap<Integer, Comparable[]> first = h.get(h.size());
                 Comparable[] tab=  first.get(0); */
                 Comparable[] tab=  log.history.get(log.history.size()-1);
                
                HashMap<Comparable,Color> hm = new HashMap<>();
                double cr = 0;
                double cg = 0;
                double cb = 0;
                int stage = 0;
                int diff = 1;
                for(int i=1;i<tab.length;i++){
                    if(tab[i].compareTo(tab[i-1])!=0)
                        diff++;
                }
                double step = (256*7)/(double)diff;
                if(step>255)
                    step = 255;
                cb+=step;
                //System.out.println("diffs:"+diff);
                for(int i=0;i<tab.length;i++){
                    if(!hm.containsKey(tab[i])){
                        hm.put(tab[i],new Color((int)cr,(int)cg,(int)cb));
                      //  System.out.println(tab[i]+" color RGB("+new Color((int)cr,(int)cg,(int)cb)+")");
                        switch(stage){
                            case 0:
                                if(cb+step>=255){
                                    cg += (cb+step-255);
                                    cb = 255;
                                    stage = 1;
                                }else
                                    cb+=step;
                            break;
                            case 1:
                                if(cg+step>=255){
                                    cb -= (cg+step-255);
                                    cg = 255;
                                    stage = 2;
                                }else
                                    cg+=step;
                            break;
                            case 2:
                                if(cb-step<=0){
                                    cr -= (cb-step);
                                    cb = 0;
                                    stage = 3;
                                }else
                                    cb-=step;
                            break;
                            case 3:
                                if(cr+step>=255){
                                    cg -= (cr+step-255);
                                    cr = 255;
                                    stage = 4;
                                }else
                                    cr+=step;
                            break;
                            case 4:
                                if(cg-step<=0){
                                    cb -= (cg-step);
                                    cg = 0;
                                    stage = 5;
                                }else
                                    cg-=step;
                            break;
                            case 5:
                                if(cb+step>=255){
                                    cg += (cb+step-255);
                                    cb = 255;
                                    stage = 6;
                                }else
                                    cb+=step;
                            break;
                            case 6:
                                if(cg+step>=255){
                                    cg = 255;
                                    stage = 8;
                                }else
                                    cg+=step;
                            break;
                                        
                        }
                    }
                }
                            
                int i = 0;
                for(Comparable[] t : log.history){
            /*    for(Entry<Integer, HashMap<Integer, Comparable[]>> ent : h.entrySet()){
                    LinkedHashMap<Integer, Comparable[]> step_tab = ent.getValue().entrySet().stream().sorted((e1,e2) -> e1.getKey().compareTo(e2.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));*/
                    int j=0;
                   /* for(Entry<Integer, Comparable[]> entry : step_tab.entrySet()){
                        for(Comparable c : entry.getValue() ){*/
                    for(Comparable c : t ){
                       // System.out.println("("+i+","+j+")"+c);
                        g.setColor(hm.get(c));
                        g.fillRect((i)*2, (j)*2, 2, 2);
                        j++;
                     //   }
                    }
                    i++;
                }
                g.dispose();
                
                int end_width = 1600;
                int end_height = 800;
                double scale1 = (end_width/(double)width);
                double scale2 = (end_height/(double)height);
                boolean fix_ratio = scale1<3*scale2;
                boolean max_width = scale1<scale2;
                double scale = (max_width)?scale1:scale2;
                scale1 = scale;
              //  System.out.println("scale1 "+scale1);
             //   System.out.println("scale2 "+scale2);
              //  System.out.println("scale "+scale);
                if(width<100 && height<100){
                    end_width = 8*width;
                    end_height = 8*height;
                    scale = 8;
                }else if(width<200 && height<200){
                    end_width = 4*width;
                    end_height = 4*height;
                    scale = 4;
                }else{
                    if(fix_ratio){
                        scale = scale2/2;
                        end_height = (int)(scale*height);
                        end_width = (int)(end_height*4.25);
                        scale1 = (end_width/(double)width);
                        
                    }else{
                        end_height = (int)(scale*height);
                        end_width = (int)(scale*width);
                    }
                        
                }
                BufferedImage b = new BufferedImage(end_width,end_height, BufferedImage.TYPE_INT_RGB);
                g = b.createGraphics();
                g.scale(scale1, scale);
                if(g.drawImage(bi,0, 0, null)){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.err.println("Can't wait for image");
                    }
                }
                g.dispose();
                return b;
            }
        }
        return new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB);
    }
    
}
