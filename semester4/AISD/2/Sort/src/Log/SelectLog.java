/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Log;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author n1t4chi
 */
public class SelectLog{
    
    public final PrintStream out;

    private final AtomicLong c_compare;
  //  private final AtomicLong c_swap;
    public long getCompareCount() {
        return c_compare.get();
    }
  //  public long getSwapCount() {
 //       return c_swap.get();
 //   }
    /**
     * Increments compare counter in given log
     * @param log log
     */
    public static void incrementCompare(SelectLog log){
        if(log!=null)
            log.c_compare.incrementAndGet();
    }
    /* *
     * Increments swap counter in given log
     * @param log log
     */
 /*   public static void incrementSwap(SelectLog log){
        if(log!=null)
            log.c_swap.incrementAndGet();
    }*/
    
    
    public SelectLog(PrintStream out) {
        this.c_compare = new AtomicLong();
      //  this.c_swap = new AtomicLong();
        this.out = out;
    }
    
    /**
     * Prints array if given log is not null.
     * @param tab array to print
     * @param log logging check
     * @param always if logging is activated then whether the string will always be printed or only if sorted array size is no more than 10
     */
    public static void printlnTab(Comparable[] tab,int mark,SelectLog log/*,boolean always*/){
        if(log!=null && log.out!=null/* && always*/){
            boolean first = true;
            for(int i=0;i<tab.length;i++){
               log.out.print(((first)?"":",")+ ((i==mark)?"{":"")+ tab[i] +((i==mark)?"}":"") );  
               first = false;
            }
            log.out.println();
        }
    }
    /**
     * Prints counter summary for given log.
     * @param tab tab to print
     * @param log logging check
     * @param always if logging is activated then whether the string will always be printed or only if sorted array size is no more than 10
     */
    public static void printlnSummary(Comparable[] tab,SelectLog log/*,boolean always*/){
        if(log!=null && log.out!=null/* && always*/){
          //  if(always || log.N<=10){
          //      
           //     String hist = (log.history!=null)?(" Log history size:"+log.history.size())+".":"";
            log.out.println("Compare count:"+log.getCompareCount()+".  Array state:");
            printlnTab(tab,-1,log);
        }
    }
    /**
     * Prints texts if given log is not null.
     * @param text text to print
     * @param log logging check
     * @param always if logging is activated then whether the string will always be printed or only if sorted array size is no more than 10
     */
    public static void println(String text,SelectLog log/*,boolean always*/){
        if(log!=null && log.out!=null/* && always*/){
                log.out.println(text);
        }
        
        
        
    }
}
