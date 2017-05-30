/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L2;

import Log.SortLog;
import java.io.PrintStream;
import java.util.Arrays;
import static L2.InsertionSort.insertionSort;
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
public class MergeSort {
    
    
    /**
     * Merge Sort implementation.
     * @param tab array to sort
     */
    public static void mergeSort(Comparable[] tab){
        mergeSort(tab,0,tab.length-1,null,true);
    }
    /**
     * merge Sort implementation with logging enabled.
     * @param tab array to sort
     * @param out
     * @return log with complete sorting history and counts of key comparisons and swaps.
     */
    public static SortLog mergeSortWithLog(Comparable[] tab,PrintStream out,boolean history){
        SortLog log = new SortLog(tab.length,out,history);
        mergeSort(tab,0,tab.length-1,log,true);
        return log;
    }
    /**
     * merge Sort method.
     * @param tab array to sort
     * @param log for logging purposes
     */
    private static void mergeSort(Comparable[] tab,int start, int end,SortLog log,boolean first/*,int step,int id*/){
        if(start<end){
            String merge = "MergeSort(s:"+start+",e:"+end+") ";
            if(first){
                println(merge+"received for sort this array:", log, true);
                printlnTab(tab,log, true);
                println("", log, true);
                addToHistory(tab,/*step,0,*/log,true);
            }
            int separator = (start+end)/2;
            println(merge+"calls  MergeSort(s:"+start+", e:"+separator+") and MergeSort(s:"+(separator+1)+", e:"+end+") ", log, false);
            mergeSort(tab, start, separator, log,false/*, step+1, id<<1+0*/);
            mergeSort(tab, separator+1, end, log,false/*, step+1, id<<1+1*/);
            println(merge+"calls merge(s:"+start+",sep:"+separator+",e:"+end+") ", log, false);
            merge(tab,start,separator,end,log/*,step,id*/);

            if(first){
                println("Finished sorting the array. Results:", log, true);
                addToHistory(tab,/*log.history.size()+1,0,*/log,true);
                printlnSummary(tab, log, true);
               // println("Log history size:"+log.history.size(), log, true);
            }
        }
    }
    private static void merge(Comparable[] tab,int start, int separator,int end,SortLog log/*,int step,int id*/){
        Comparable[] A = Arrays.copyOfRange(tab, start, separator+1);
        Comparable[] B = Arrays.copyOfRange(tab, separator+1, end+1);
        String merge = "merge(s:"+start+",sep:"+separator+",e:"+end+") ";
        println(merge+"merges two arrays (A and B) below :",log,true);
        printlnTab(A, log, true);
        printlnTab(B, log, true);
        int a=0;
        int b=0;
        for(int x = start ; x<=end;x++){
            println(merge+" determines element at "+x+", current iterators for sub arrays: a:"+a+"/"+A.length+" b:"+b+"/"+B.length,log,false);
            if (b>=B.length){
                println(merge+" selects "+A[a]+" from A, since there are no more elements in B",log,false);
                incrementSwap(log);
                tab[x] = A[a];
                a++;
            }else if (a>=A.length){
                println(merge+" selects "+B[b]+" from B, since there are no more elements in A",log,false);
                incrementSwap(log);
                tab[x] = B[b];
                b++;
            }else{
                incrementCompare(log);
                if(A[a].compareTo(B[b])<=0 ){
                    println(merge+" selects "+A[a]+" from A, since it's lesser than:"+B[b]+" from B",log,false);
                    incrementSwap(log);
                    tab[x] = A[a];
                    a++;
                }else{
                    println(merge+" selects "+B[b]+" from B, since it's lesser than:"+A[b]+" from B",log,false);
                    incrementSwap(log);
                    tab[x] = B[b];
                    b++;
                }
            } 
            addToHistory(tab,/*step++,0,*/ log,false); //krok pośredni
        }
        
        println(merge+"finished working",log,true);
        printlnSummary(tab, log, true);
        addToHistory(tab, /*step, id, */log,true);
        println("\n", log, true);
    }
    
    
    
    
    
    /**
     * Merge Sort implementation.
     * @param tab array to sort
     * @param when at which size of array to switch to insertion to sort
     */
    public static void mergeWithInsertionSort(Comparable[] tab,int when){
        mergeWithInsertionSort(tab,0,tab.length-1,when,null,true);
    }
    /**
     * merge Sort implementation with logging enabled.
     * @param tab array to sort
     * @param when at which size of array to switch to insertion to sort
     * @param out
     * @return log with complete sorting history and counts of key comparisons and swaps.
     */
    public static SortLog mergeWithInsertionSortWithLog(Comparable[] tab,int when,PrintStream out,boolean history){
        SortLog log = new SortLog(tab.length,out,history);
        mergeWithInsertionSort(tab,0,tab.length-1,when,log,true);
        return log;
    }
    /**
     * merge Sort method.
     * @param tab array to sort
     * @param when at which size of array to switch to insertion to sort
     * @param log for logging purposes
     */
    private static void mergeWithInsertionSort(Comparable[] tab,int start, int end,int when,SortLog log,boolean first/*,int step,int id*/){
        if(start<end){
            String merge = "mergeWithInsertionSort(s:"+start+",e:"+end+") ";
            if(first){
                println(merge+"received for sort this array:", log, true);
                printlnTab(tab,log, true);
                println("", log, true);
                addToHistory(tab,/*step,0,*/log,true);
            }
            if(end-start>when){
                int separator = (start+end)/2;
                println(merge+"calls  mergeWithInsertionSort(s:"+start+", e:"+separator+") and mergeWithInsertionSort(s:"+(separator+1)+", e:"+end+") ", log, false);
                mergeWithInsertionSort(tab, start, separator,when,log,false/*, step+1, id<<1+0*/);
                mergeWithInsertionSort(tab, separator+1, end, when,log,false/*, step+1, id<<1+1*/);
                println(merge+"calls merge(s:"+start+",sep:"+separator+",e:"+end+") ", log, false);
                merge(tab,start,separator,end,log/*,step,id*/);
            }else{
                println(merge+" switches to insertionSort", log, false);
                insertionSort(tab,log,start,end+1);
            }
            if(first){
                println("Finished sorting the array. Results:", log, true);
                addToHistory(tab,/*log.history.size()+1,0,*/log,true);
                printlnSummary(tab, log, true);
               // println("Log history size:"+log.history.size(), log, true);
            }
        }
    }
    
    
}
