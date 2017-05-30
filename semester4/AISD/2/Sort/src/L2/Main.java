/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L2;

import Log.SortLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import static L2.InsertionSort.insertionSortWithLog;
import static Log.SortLog.historyToImage;
import static L2.MergeSort.mergeSortWithLog;
import static L2.MergeSort.mergeWithInsertionSortWithLog;
import static L2.QuickSort.dualPivotQuickSortWithLog;
import static L2.QuickSort.quickSortWithLog;
import static L2.QuickSort.quickWithInsertionSortWithLog;

/**
 *
 * @author n1t4chi
 */
public class Main {
    
    
    
    
    
    /**
     * Tests sorting alghoritms with random arrays.<br>
     * usage: (-s) {-[TYPE]} (-r) {-size N} {-min K -max L -step M} <br>
     * arguments within {} are required in specified order, () are optional<br>
     * -s - single test for given N size. Otherwise test alghoritms within [K,L] size range every M steps.<br>
     * -[TYPE] - determines type of a test:<br>
     * *a - tests all alghoritms. Cannot be used with -s. Can be skipped as it's default one.<br>
     * *i - tests only insertion sort<br>
     * *m - tests only merge sort<br>
     * *q - tests only quick sort<br>
     * *qi - tests only quick with insertion sort<br>
     * *mi - tests only merge with insertion sort<br>
     * *dpq - tests only dual-pivot quick sort<br>
     * -r - uses reverse sorted array instead of random.<br>
     * -size - required for -s - Size of tested array.<br>
     * -min - required for default (without -s) use - Minimal size boundary<br>
     * -max - required for default (without -s) use - Maximal size boundary<br>
     * -step - required for default (without -s) use - Step size for size iteration.<br>
     * @param args the command line arguments
     */
    public static void main(String[] args) {
     /*   for (String arg : args) {
            System.out.println(arg);
        }*/
        if(args.length>3){
            boolean single_test = args[0].equalsIgnoreCase("-s");
            boolean reverse = args[(single_test)?2:1].equalsIgnoreCase("-r");
            if(single_test){
                int size = getParameter("-size",args);
                Integer[] t = new Integer[size];
                Random r = new Random();
                SortLog log=null;
                for (int i = 0; i < t.length; i++) {
                    t[i] = r.nextInt(500);
                }
                if(reverse)
                    Arrays.sort(t, (Comparable o1, Comparable o2) -> -o1.compareTo(o2));
                String algo = "";
                Integer[] check = t.clone();
                Arrays.sort(check);
                switch(args[1]){
                    case "-i":
                            algo = "Insertion Sort";
                            log = insertionSortWithLog(t,System.out,true);
                        break;
                    case "-m":
                            algo = "Merge Sort";
                            log = mergeSortWithLog(t,System.out,true);
                        break;
                    case "-q":
                            algo = "Quick Sort";
                            log = quickSortWithLog(t,System.out,true);
                        break;
                    case "-mi":
                            algo = "Merge with Insertion Sort";
                            log = mergeWithInsertionSortWithLog(t,4,System.out,true);
                        break;
                    case "-qi":
                            algo = "Quick with Insertion Sort";
                            log = quickWithInsertionSortWithLog(t,2,System.out,true);
                        break;
                    case "-dpq":
                            algo = "Yaroslavskiy's Dual-Pivot Quick Sort";
                            log = dualPivotQuickSortWithLog(t,System.out,true);
                        break;
                    default:
                            System.err.println("Invalid test type argument!");
                        break;

                }
               /* if(!Arrays.equals(check, t)){
                    System.err.println("Found difference with java sorted array!");
                    for(int i=0;i<t.length;i++){
                        if(t[i].compareTo(check[i])!=0  )
                            System.err.println(i+" > "+t[i]+" !="+check[i]);
                    }
                }*/
                
                
                if(log!=null){
                    System.out.println(algo+" statistics for "+size+" array: "+log.getSwapCount()+" comp:"+log.getCompareCount());
                    JOptionPane.showMessageDialog(null,new JScrollPane(new JLabel(new ImageIcon(historyToImage(log)))),"Sorting",JOptionPane.PLAIN_MESSAGE);
                }
            }else{
                Alghoritm alg;
                switch(args[0]){
                    case "-i":
                            alg = Alghoritm.INSERT;
                        break;
                    case "-m":
                            alg = Alghoritm.MERGE;
                        break;
                    case "-q":
                            alg = Alghoritm.QUICK;
                        break;
                    case "-mi":
                            alg = Alghoritm.MERGE_W_INSERT;
                        break;
                    case "-qi":
                            alg = Alghoritm.QUICK_W_INSERT;
                        break;
                    case "-dpq":
                            alg = Alghoritm.DUAL_PIVOT_QUICK;
                        break;
                    case "-a": default:
                            alg = Alghoritm.ALL;
                        break;
                }
                int min  = getParameter("-min",args);
                int max  = getParameter("-max",args);
                int step  = getParameter("-step",args);
                if(min>0&&min<=max&&step>0){
                    SortLog log;
                    Integer[] t;
                    Random r = new Random();
                    ArrayList<long[][]> results = new ArrayList<>();
                                                                                                                                                                                                                                              
                    ExecutorService es = Executors.newFixedThreadPool(20);
                    ArrayList<Future<long[][]>> al = new ArrayList<>();
                    for(int size=min ; size<=max ; size+=step){
                        al.add( es.submit(new SortWorker(alg,size, reverse, 200 )));
                    }
                    
                    String ins = (alg == Alghoritm.ALL || alg == Alghoritm.INSERT)?"\tInsertion Sort compare count\tInsertion Sort swap count":"";
                    String mer = (alg == Alghoritm.ALL || alg == Alghoritm.MERGE)?"\tMerge Sort compare count\tMerge Sort swap count":"";
                    String qui = (alg == Alghoritm.ALL || alg == Alghoritm.QUICK)?"\tQuick Sort compare count\tQuick Sort swap count":"";
                    String mwi = (alg == Alghoritm.ALL || alg == Alghoritm.MERGE_W_INSERT)?"\tMerge with Insertion Sort compare count\tMerge with Insertion Sort swap count":"";
                    String qwi = (alg == Alghoritm.ALL || alg == Alghoritm.QUICK_W_INSERT)?"\tQuick with Insertion Sort compare count\tQuick with Insertion Sort swap count":"";
                    String dpq = (alg == Alghoritm.ALL || alg == Alghoritm.DUAL_PIVOT_QUICK)?"\tDual-Pivot Quick Sort compare count\tDual-Pivot Quick Sort swap count":"";
                            
                    System.out.println("Array Size"+ins+mer+qui+mwi+qwi+dpq);
                    while(!al.isEmpty()){
                        ArrayList<Future<long[][]>> cal = (ArrayList<Future<long[][]>>) al.clone();
                        for(Future<long[][]> fut : cal){
                            if(fut.isDone()){
                                long[][] tab;
                                try {
                                    tab = fut.get();   
                                    String ins1 = ( alg == Alghoritm.ALL || alg == Alghoritm.INSERT)?"\t"+tab[1][0]+"\t"+tab[1][1]:"";
                                    String mer1 = (alg == Alghoritm.ALL || alg == Alghoritm.MERGE)?"\t"+tab[2][0]+"\t"+tab[2][1]:"";
                                    String qui1 = (alg == Alghoritm.ALL || alg == Alghoritm.QUICK)?"\t"+tab[3][0]+"\t"+tab[3][1]:"";
                                    String mwi1 = (alg == Alghoritm.ALL || alg == Alghoritm.MERGE_W_INSERT)?"\t"+tab[4][0]+"\t"+tab[4][1]:"";
                                    String qwi1 = (alg == Alghoritm.ALL || alg == Alghoritm.QUICK_W_INSERT)?"\t"+tab[5][0]+"\t"+tab[5][1]:"";
                                    String dpq1 = (alg == Alghoritm.ALL || alg == Alghoritm.DUAL_PIVOT_QUICK)?"\t"+tab[6][0]+"\t"+tab[6][1]:"";
                                    //System.out.println(tab[0][0]+ins1+mer1+mwi1+qui1+qwi1);
                                    System.out.println(tab[0][0]+ins1+mer1+qui1+mwi1+qwi1+dpq1);
                                    results.add(tab);
                                    al.remove(fut);
                                } catch (InterruptedException | ExecutionException ex) {
                                    System.err.println("Could not retrieve result of a thread:\n"+ex.getMessage());
                                }
                            }else
                                break;
                        }
                        try{
                            Thread.sleep(50);
                        }catch(InterruptedException ex){
                            
                        }
                    }
                    es.shutdown();
                    
                }
            }
        }
        //
    }
    
    public static enum Alghoritm {
        INSERT,MERGE,QUICK,ALL,MERGE_W_INSERT,QUICK_W_INSERT,DUAL_PIVOT_QUICK
    }
    
    /**
     * Returns long[4][2] where long[0..3] returns count of compare and swap respectively. long[4] returns size and number of tests
     */
    public static class SortWorker implements Callable<long[][]>{
        final int size;
        final boolean reverse;
        final int tests;
        final Alghoritm alg;
        
        public SortWorker(Alghoritm alg,int size, boolean reverse, int tests) {
            this.size = size;
            this.reverse = reverse;
            this.tests = tests;
            this.alg = alg;
        }
        
        @Override
        public long[][] call() throws Exception {
            //System.out.println("Strating thread");
            try{
                long[][] stats = new long[7][2];
                stats[0][0]=size;
                stats[0][1]=tests;
                SortLog log;
                Integer[] t = new Integer[size];
                for(int i=0;i<tests;i++){
                    for (int a = 0; a < t.length; a++) {
                        t[a] = ThreadLocalRandom.current().nextInt(size*2);
                    } 
                    if(reverse)
                        Arrays.sort(t, (Comparable o1, Comparable o2) -> -o1.compareTo(o2));
                    if(alg==Alghoritm.ALL||alg == Alghoritm.INSERT){
                        log = insertionSortWithLog(t.clone(),null,false);
                        //log = quickSortWithLog(t.clone(),null,false);
                        stats[1][0] += log.getCompareCount();
                        stats[1][1] += log.getSwapCount();
                    }
                    if(alg==Alghoritm.ALL||alg == Alghoritm.MERGE){
                        //log = quickWithInsertionSortWithLog(t.clone(),7,null,false);
                        log = mergeSortWithLog(t.clone(),null,false);
                        stats[2][0] += log.getCompareCount();
                        stats[2][1] += log.getSwapCount();
                    }
                    if(alg==Alghoritm.ALL||alg == Alghoritm.QUICK){
                        //log = quickWithInsertionSortWithLog(t.clone(),10,null,false);
                        log = quickSortWithLog(t.clone(),null,false);
                        stats[3][0] += log.getCompareCount();
                        stats[3][1] += log.getSwapCount();
                    }
                    if(alg==Alghoritm.ALL||alg == Alghoritm.MERGE_W_INSERT){
                        //switching at 4 at 8000 array changes ~4k swaps into comparisions. 
                        //At 2 it barely increases comparisions (~100) and decreases amount of swaps by over 2 thousands
                      //  log = mergeWithInsertionSortWithLog(t.clone(),4,null,false);  
                        log = mergeWithInsertionSortWithLog(t.clone(),2,null,false);
                       // log = quickWithInsertionSortWithLog(t.clone(),13,null,false);
                        stats[4][0] += log.getCompareCount();
                        stats[4][1] += log.getSwapCount();
                    }
                    if(alg==Alghoritm.ALL||alg == Alghoritm.QUICK_W_INSERT){
                        log = quickWithInsertionSortWithLog(t.clone(),13,null,false);
                        stats[5][0] += log.getCompareCount();
                        stats[5][1] += log.getSwapCount();
                    }
                    if(alg==Alghoritm.ALL||alg == Alghoritm.DUAL_PIVOT_QUICK){
                        log = dualPivotQuickSortWithLog(t.clone(),null,false);
                        stats[6][0] += log.getCompareCount();
                        stats[6][1] += log.getSwapCount();
                    }
                }
                for(int i=1;i<stats.length;i++)
                    for(int j=0;j<stats[i].length;j++)
                        stats[i][j]/=tests;
                return stats;
            }catch(Exception ex){
                System.err.println("Shit's pucked:"+ex.getMessage());
                ex.printStackTrace();
            }
            return null;
        }
    }    
    
    /**
     * Returns value associated with given parameter. Returns -1 if nothing was found
     * @param param
     * @param args
     * @return 
     */
    private static int getParameter(String param,String[] args){
        int rtrn = -1;
        boolean found = false;
        for(int i=0;i<args.length-1;i++){
            if(args[i].startsWith(param)){
                try{ 
                    rtrn = Integer.parseInt(args[i+1].trim());
                    found = true;
                }catch(NumberFormatException ex){
                    System.err.println(param+" has no valid number!");
                }
            }
        }
        if(!found){
            System.err.println("No "+param+" parameter!");
        }
        return rtrn;
    }
}
