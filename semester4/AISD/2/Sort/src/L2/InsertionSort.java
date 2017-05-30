/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L2;

import Log.SortLog;
import java.io.PrintStream;
import static Log.SortLog.addToHistory;
import static Log.SortLog.incrementCompare;
import static Log.SortLog.incrementSwap;
import static Log.SortLog.println;
import static Log.SortLog.printlnSummary;
import static Log.SortLog.printlnTab;

/**
 *
 * @author n1t4chi
 */
public class InsertionSort {
    
    /**
     * Insertion Sort implementation.
     * @param tab array to sort
     */
    public static void insertionSort(Comparable[] tab){
        insertionSort(tab,null,0,tab.length);
    }
    /**
     * Insertion Sort implementation with logging enabled.
     * @param tab array to sort
     * @param out
     * @param history
     * @return log with complete sorting history and counts of key comparisons and swaps.
     */
    public static SortLog insertionSortWithLog(Comparable[] tab,PrintStream out,boolean history){
        SortLog log = new SortLog(tab.length,out,history);
        insertionSort(tab,log,0,tab.length);
        return log;
    }
    /**
     * insertionSort method.
     * @param tab array to sort
     * @param log for logging purposes
     * @param start sorts elements since (inclusive)
     * @param end  sorts element until (exclusive)
     */
    static void insertionSort(Comparable[] tab,SortLog log,int start,int end){
        println("insertionSort() received for sort this array:", log, true);
        printlnTab(tab,log, true);
        println("", log, true);
        int step = 0;
        addToHistory(tab,/*step++,0,*/log,true);
        for(int i=start+1;i<end;i++){
            println("Selecting  element at "+i+" ["+tab[i]+"] for comparision.", log, true);
            Comparable x = tab[i];
            int j = i-1;
            //while(j>=0 && tab[j].compareTo(x)>0){
            for(j=i-1;j>=start;j--){
                incrementCompare(log);                
                if(tab[j].compareTo(x)>0){
                    incrementSwap(log);
                    tab[j+1]=tab[j]; 
                    println("Moving element at "+j+" ["+tab[j]+"] one position higher.", log, false);
                    printlnSummary(tab, log, false);
                    println("", log, false);
                    
                    addToHistory(tab,/*step++,0,*/ log,false); //krok pośredni
                }else{
                    break;
                }  
            }
            incrementSwap(log);
            tab[j+1]=x;
            println("Inserting ["+x+"] at "+(j+1)+" position", log, true);
            printlnSummary(tab, log, true);
            addToHistory(tab,/*step++,0,*/log,true);
            println("\n", log, true);
        }
        println("Finished sorting the array. Results:", log, true);
        addToHistory(tab,/*log.history.size()+1,0,*/log,true);
        printlnSummary(tab, log, true);
    }
    
    
    
 
    
    
}
