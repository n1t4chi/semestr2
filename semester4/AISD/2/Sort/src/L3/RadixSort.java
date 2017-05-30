/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L3;

import static L2.InsertionSort.insertionSortWithLog;
import static L2.MergeSort.mergeSortWithLog;
import static L2.MergeSort.mergeWithInsertionSortWithLog;
import static L2.QuickSort.dualPivotQuickSortWithLog;
import static L2.QuickSort.quickSortWithLog;
import static L2.QuickSort.quickWithInsertionSortWithLog;
import static L3.Main.compare;
import Log.SortLog;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
        
/**
 *
 * @author n1t4chi
 */
public class RadixSort {
    public static SortLogRadix RadixSortWLog(Comparable[][] tab,PrintStream out,boolean history){
        SortLogRadix log = new SortLogRadix(tab.length, out, history);
        RadixSort(tab, log);
        return log;
    }
    public static void RadixSort(Comparable[][] tab){
        RadixSort(tab, null);
    }
    /**
     * Uses radix sort algorithm to sort first dimension of an array. All second dimension sizes must be equal to work!
     * @param tab
     * @param log 
     */
    private static void RadixSort(Comparable[][] tab,SortLogRadix log){
        SortLogRadix.println("RadixSort received below tab to sort:", log, true);
        SortLogRadix.printlnTab(tab, log, true);
        SortLogRadix.addToHistory(tab, log, true);
        
        for(int i=tab[0].length-1 ; i>=0 ; i--){
            SortLogRadix.println("RadixSort invokes CountingSort on "+i+"th column", log, true);
            CountingSort(tab, i, log);
            SortLogRadix.addToHistory(tab, log, true);
            SortLogRadix.printlnSummary(tab, log, true);
            SortLogRadix.println("", log, true);
        }
    }
    /**
     * Sorts given 2d array at i-th column(Sorts for all x based on specified i -> Comparable[x][i]).
     * Because it works on Comparable objects the worst case performance is at least O(n^2) since HashMap is used to store different types of objects.
     * But on the other hand does not allow for radix range bigger than array first dimension size.
     * Also it is required to sort all possible values within I-th column in O(n*logn)
     * @param tab
     * @param i
     * @param log 
     */
    private static void CountingSort(Comparable[][] tab,int i,SortLogRadix log){
        SortLogRadix.println("CountingSort received below tab to sort based on "+i+"th column", log, true);
        SortLogRadix.printlnTab(tab, log, true);
        
        HashMap<Comparable,Integer> c = new HashMap<>(); //Cheating: Let's assume it's O(1) for general compatibility and not requiring to give a 
        for (Comparable[] tab1 : tab) {
            Integer c_x = c.get(tab1[i]);
            c.put(tab1[i], (c_x!=null)?c_x+1:1);
        }
        //Cheating for sake of compatibility, Of course the alghoritm could take sorted list of objects first but let's assume it's O(1)
        Comparable[] obj_tab = (c.keySet().toArray(new Comparable[c.size()])); 
        Arrays.sort(obj_tab);
        //
        
       

      //  System.out.println(obj_tab[0]+"\t"+c.get(obj_tab[0])+"\t"+c.get(obj_tab[0]));
        for(int x=1 ; x< obj_tab.length ; x++){
          //  System.out.print(obj_tab[x]+"\t"+c.get(obj_tab[x]));
            c.put(obj_tab[x],c.get(obj_tab[x])+c.get(obj_tab[x-1]) );
          //  System.out.println("\t"+c.get(obj_tab[x]));
        }
        
        Comparable[][] copy = Arrays.copyOf(tab, tab.length);
        
        for(int x=tab.length-1 ; x>=0 ; x--){
            SortLogRadix.println("", log, false);
            SortLogRadix.incrementSwap(log);
            int cx = c.get(copy[x][i]);
            tab[cx-1] = copy[x];
            c.put(copy[x][i], cx-1);
            SortLogRadix.println("CountingSort moves "+x+"th werse into "+cx+"th werse", log, false);
            SortLogRadix.addToHistory(tab, log, false);
            SortLogRadix.printlnSummary(tab, log, false);
        }
        SortLogRadix.println("", log, false);
      //  System.out.println("\n");
        
    }
    
    /**
     * Swaps i1-th row with i2-th 
     * @param tab
     * @param i1
     * @param i2
     * @param log 
     */
    private static void swap(Comparable[][] tab, int i1, int i2,SortLogRadix log){
        SortLogRadix.incrementSwap(log);
        Comparable[] copy = tab[i1];
        tab[i1] = tab[i2];
        tab[i2] = copy;
    }
    
    
    
    
    
    public static class RadixWorker implements Callable<long[]> {
        final int size;
        final boolean reverse;
        final int tests;
        final int word_l;
        final int radix_l;
        public RadixWorker(int size, boolean reverse, int tests,int word_l,int radix_l) {
            this.size = size;
            this.reverse = reverse;
            this.tests = tests;
            this.word_l = word_l;
            this.radix_l=radix_l;
        }

        @Override
        public long[] call() throws Exception {
            //System.out.println("Strating thread");
            try{
                long[] stats = new long[3];
                stats[0]=size;
                stats[1]=tests;
                SortLogRadix log;
                Integer[][] t = new Integer[size][word_l];
                for(int i=0;i<tests;i++){
                    for (int a = 0; a < t.length; a++) {
                        for (int b = 0; b < t[a].length; b++) {
                            t[a][b] = ThreadLocalRandom.current().nextInt(radix_l);
                        }
                    } 
                    if(reverse)
                        Arrays.sort(t, (Comparable[] o1, Comparable[] o2) -> -compare(o1,o2));
                    
                    log = RadixSortWLog(t.clone(),null,false);
                    stats[2] += log.getSwapCount();
                }
                stats[2] /=tests;
                return stats;
            }catch(Exception ex){
                System.err.println("Shit's pucked:"+ex.getMessage());
                ex.printStackTrace();
            }
            return null;
        }
    }
}
