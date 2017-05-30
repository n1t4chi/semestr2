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
import static L3.Select.Select;
import static L3.Select.swapMedians;
import static Log.SortLog.addToHistory;
import static Log.SortLog.incrementCompare;
import static Log.SortLog.incrementSwap;
import static Log.SortLog.println;
import static Log.SortLog.printlnSummary;
import static Log.SortLog.printlnTab;
import java.util.Map;

/**
 *
 * @author n1t4chi
 */
public class QuickSort {
    
     /**
     * quick Sort implementation.
     * @param tab array to sort
     */
    public static void quickSort(Comparable[] tab){
        quickSort(tab,0,tab.length-1,null,true);
    }
    /**
     * quick Sort implementation with logging enabled.
     * @param tab array to sort
     * @param out
     * @return log with complete sorting history and counts of key comparisons and swaps.
     */
    public static SortLog quickSortWithLog(Comparable[] tab,PrintStream out,boolean history){
        SortLog log = new SortLog(tab.length,out,history);
        quickSort(tab,0,tab.length-1,log,true);
        return log;
    }
    /**
     * quickSort method.
     * @param tab array to sort
     * @param log for logging purposes
     */
    private static void quickSort(Comparable[] tab,int start, int end,SortLog log,boolean first){
        if(start<end){
            String quick = "QuickSort(s:"+start+",e:"+end+") ";
            if(first){
                println(quick+"received for sort this array:", log, true);
                printlnTab(tab,log, true);
                println("", log, true);
                addToHistory(tab,/*step,0,*/log,true);
            }
            println(quick+"calls partition(s:"+start+",e:"+end+") ", log, false);
            int separator = partition(tab,start,end,log/*,step,id*/);
            println(quick+"calls  QuickSort(s:"+start+", e:"+separator+") and QuickSort(s:"+(separator+1)+", e:"+end+") ", log, false);
            quickSort(tab, start, separator, log,false/*, step+1, id<<1+0*/);
            quickSort(tab, separator+1, end, log,false/*, step+1, id<<1+1*/);

            if(first){
                println("Finished sorting the array. Results:", log, true);
                addToHistory(tab,/*log.history.size()+1,0,*/log,true);
                printlnSummary(tab, log, true);
               // println("Log history size:"+log.history.size(), log, true);
            }
        }
    }
    
    private static void medianOfThreeSinglePivotSelect(Comparable[] tab,int start, int end,int middle,SortLog log){
        if(start > middle){
            int i=0;
            incrementCompare(log);
            if(tab[middle].compareTo(tab[start])<0){ //b<a
                incrementCompare(log);
                if(tab[end].compareTo(tab[middle])<0){ //c<b<a
                    swap(tab,start,end,log,false);
                }else{  //b<a?c
                    swap(tab,start,middle,log,false);
                    //b,a,c
                    incrementCompare(log);
                    if(tab[end].compareTo(tab[start])<0){ //b<c<a                        
                        swap(tab,middle,end,log,false);
                    }//else b<a<c
                }  
            }else{ //a<b
                incrementCompare(log);
                if(tab[end].compareTo(tab[middle])<0){ //a?c<b
                    swap(tab,middle,end,log,false);
                    //a,c>b
                    incrementCompare(log);
                    if(tab[end].compareTo(tab[start])<0){ //c<a<b
                        swap(tab,start,middle,log,false);
                    }//else a<c<b
                }//else a<b<c
                    
            }
        }else{
            incrementCompare(log);
            if(tab[end].compareTo(tab[start])<0)
                swap(tab,end,start,log,false);
        }
        println("Selected pivot :"+tab[middle]+". Array after selecting pivot:", log, false);
        printlnTab(tab, log, false);
    }
    
    private static int partition(Comparable[] tab,int start, int end,SortLog log){
        String quick = "Partition(s:"+start+",e:"+end+")";
        println(quick+" partitions given array:", log, false);
        printlnTab(tab, log, false);
        int middle = (start+end)/2;
        medianOfThreeSinglePivotSelect(tab, start, end, middle,log);
        Comparable pivot = tab[middle];
        int i=start-1;
        int j=end+1;
        while(true){
            do{
                i++;
                incrementCompare(log);
            }while(tab[i].compareTo(pivot)<0);
            do{
                j--;
                incrementCompare(log);
            }while(tab[j].compareTo(pivot)>0);
            
            
            if(i>=j){
                println(quick+" finished working.", log, true);
                //addToHistory(tab, log,true);
                printlnSummary(tab, log, true);
                println("\n", log, true);
                return j;
            }
            
          //  incrementCompare(log);
           // if(tab[i].compareTo(tab[j])!=0)
            swap(tab,i,j,log,false);
            //println("SAME SWAP!!!!!!!#################################33", log, false);
         //   println("Selected pivot :"+tab[middle]+". Array after selecting pivot:", log, false);
          //  printlnTab(tab, log, true);
        }
    }
    
    
    
    
     /**
     * quick Sort implementation.
     * @param tab array to sort
     * @param when at which size of array to switch to insertion to sort
     */
    public static void quickWithInsertionSort(Comparable[] tab,int when){
        quickWithInsertionSort(tab,0,tab.length-1,when,null,true);
    }
    /**
     * quick Sort implementation with logging enabled.
     * @param tab array to sort
     * @param when at which size of array to switch to insertion to sort
     * @param out
     * @param history
     * @return log with complete sorting history and counts of key comparisons and swaps.
     */
    public static SortLog quickWithInsertionSortWithLog(Comparable[] tab,int when,PrintStream out,boolean history){
        SortLog log = new SortLog(tab.length,out,history);
        quickWithInsertionSort(tab,0,tab.length-1,when,log,true);
        return log;
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * quickSort method.
     * @param tab array to sort
     * @param when at which size of array to switch to insertion to sort
     * @param log for logging purposes
     */
    private static void quickWithInsertionSort(Comparable[] tab,int start, int end,int when,SortLog log,boolean first){
        if(start<end){
            String quick = "QuickSort(s:"+start+",e:"+end+") ";
            if(first){
                println(quick+"received for sort this array:", log, true);
                printlnTab(tab,log, true);
                println("", log, true);
                addToHistory(tab,/*step,0,*/log,true);
            }
            if(end-start>when){
                println(quick+"calls partition(s:"+start+",e:"+end+") ", log, false);
                int separator = partition(tab,start,end,log/*,step,id*/);
                println(quick+"calls  quickWithInsertionSort(s:"+start+", e:"+separator+") and quickWithInsertionSort(s:"+(separator+1)+", e:"+end+") ", log, false);
                quickWithInsertionSort(tab, start, separator,when, log,false/*, step+1, id<<1+0*/);
                quickWithInsertionSort(tab, separator+1, end,when, log,false/*, step+1, id<<1+1*/);
            }else{
                println(quick+" switches to insertionSort", log, false);
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
    
    private static void swap(Comparable[] tab,int i,int j,SortLog log,boolean always){
        incrementCompare(log);
        if(tab[i]!=tab[j]){
            Comparable c = tab[i];
            tab[i] = tab[j];
            tab[j] = c;
            incrementSwap(log);
            addToHistory(tab, log,true);
            println("swap("+i+","+j+") swaps "+tab[i]+" with "+tab[j], log, true /*always*/);
        }
    }
    
    
    
    

    
    
    
    
    
    
    
    
     /**
     * Yaroslavskiy dual-pivot quick Sort implementation.
     * @param tab array to sort
     */
    public static void dualPivotQuickSort(Comparable[] tab){
        QuickSort.dualPivotQuickSort(tab,0,tab.length-1,3,null,true);
    }
    /**
     * Yaroslavskiy dual-pivot quick Sort implementation with logging enabled
     * @param tab array to sort
     * @param out
     * @param history
     * @return log with complete sorting history and counts of key comparisons and swaps.
     */
    public static SortLog dualPivotQuickSortWithLog(Comparable[] tab,PrintStream out,boolean history){
        SortLog log = new SortLog(tab.length,out,history);
        QuickSort.dualPivotQuickSort(tab,0,tab.length-1,3,log,true);
        return log;
    }
    
    
    /**
     * quickSort method.
     * @param tab array to sort
     * @param when at which size of array to switch to insertion to sort
     * @param log for logging purposes
     */
    private static void dualPivotQuickSort(Comparable[] tab,int left, int right,int div,SortLog log,boolean first){
        if(left<right){
            int length = right-left;
            String quick = "Dual Pivot QuickSort(s:"+left+",e:"+right+") ";
            if(first){
                println(quick+"received for sort this array:", log, true);
                printlnTab(tab,log, true);
                println("", log, true);
                addToHistory(tab,/*step,0,*/log,true);
            }
            if(length>=13){
                Comparable pivot1,pivot2;
                int third = length/div;
                int m1 = left + third;
                int m2 = right - third;
                if(m1<=left)
                    m1 = left + 1;
                if(m2>=right)
                    m2 = right - 1;
                
                incrementCompare(log);
                if(tab[m1].compareTo(tab[m2])<0 ){
                    swap(tab, m1, left, log, first);
                    swap(tab, m2, right, log, first);
                }else{
                    swap(tab, m1, right, log, first);
                    swap(tab, m2, left, log, first);
                }
                pivot1 = tab[left];
                pivot2 = tab[right];

                int less=left+1;
                int great=right-1;

                for(int k = less; k<=great ; k++){
                    incrementCompare(log);
                    if(tab[k].compareTo(pivot1) < 0){
                        swap(tab,k,less++,log,false);
                    }else if (tab[k].compareTo(pivot2)>0){
                        incrementCompare(log);
                        while((k<great)){
                            incrementCompare(log);
                            if(tab[great].compareTo(pivot2)>0){
                                incrementCompare(log);
                                great--;
                            }else
                                break;
                        }
                        swap(tab,k,great--,log,false);
                        incrementCompare(log);
                        if(tab[k].compareTo(pivot1)<0){
                            swap(tab,k,less++,log,false);
                        }
                    }else{
                        incrementCompare(log);
                    }
                }
                int dist = great - less;
                if(dist < 13){
                    div++;
                }
                
                swap(tab,less-1,left,log,false);
                swap(tab,great+1,right,log,false);
                
                println(quick+"calls dualPivotQuicksort(s:"+left+", e:"+(less-2)+")", log, false);
                QuickSort.dualPivotQuickSort(tab,left,less-2,div,log,false);
                
                println(quick+"calls dualPivotQuicksort(s:"+(great+2)+", e:"+right+")", log, false);
                QuickSort.dualPivotQuickSort(tab,great+2,right,div,log,false);
                
                if(dist > length -13){
                    incrementCompare(log);
                    if(pivot1.compareTo(pivot2)!=0){
                        for(int k=less;k<=great;k++){
                            incrementCompare(log);
                            if(tab[k].compareTo(pivot1)==0){
                                swap(tab, k, less++, log, false);
                            }else if(tab[k].compareTo(pivot2)==0){
                                incrementCompare(log);
                                swap(tab, k, great--, log, false);
                                incrementCompare(log);
                                if(tab[k].compareTo(pivot1)==0){
                                    swap(tab, k, less++, log, false);
                                }
                            }else
                                incrementCompare(log);
                        }
                    }
                }
                incrementCompare(log);
                if(pivot1.compareTo(pivot2)<0){
                    println(quick+"calls dualPivotQuicksort(s:"+less+", e:"+great+")", log, false);
                    QuickSort.dualPivotQuickSort(tab, less, great, div, log, false);
                }
                
            }else{
                println(quick+" switches to insertionSort", log, false);
                insertionSort(tab,log,left,right+1);
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
