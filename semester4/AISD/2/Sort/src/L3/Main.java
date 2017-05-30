/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L3;

import Log.SelectLog;
import Log.SortLog;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author n1t4chi
 */
public class Main {
    /**
     * Checks if there is given parameter.
     * @param param
     * @param args
     * @return 
     */
    private static boolean isThereParameter(String param,String[] args){
        int rtrn = -1;
        boolean found = false;
        for(int i=0;i<args.length;i++){
            //System.out.println("["+args[i]+"] ? "+param);
            if(args[i].equalsIgnoreCase(param)){
                return true;
            }
        }
        return false;
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
            if(args[i].equalsIgnoreCase(param)){
                try{ 
                    rtrn = Integer.parseInt(args[i+1].trim());
                    found = true;
                }catch(NumberFormatException ex){
                    System.err.println(param+" has no valid number!");
                }
            }
        }
       //if(!found){
        //    System.err.println("No "+param+" parameter!");
        //}
        return rtrn;
    }
    /**
     * 0 > o1 == o2
     * -1 > o1 < o2
     * -2 > o2 < o1
     * @param o1
     * @param o2
     * @return 
     */
    public static int compare(Comparable[] o1, Comparable[] o2) {
        if(o1 == o2 || Arrays.equals(o1, o2))
            return 0;
        
        if(o1 == null)
            return -1;
        else if(o2 == null)
            return 1;
            
        int min = Math.min(o1.length,o2.length);
        int rtrn = 0;
        for(int i=0 ;i<min;i++){
            if( (rtrn = o1[i].compareTo(o2[i]))!=0  )
                return rtrn;
        }
        if(o1.length<o2.length)
            return -1;
        else if(o1.length>o2.length)
            return 1;
        return 0;
    }
    
    public static enum Alghoritm {
        radix,rand_select,select,select_w_quicksort
    }
    /**
     * Tests specified algorithms in third list.
     * usage: -[TYPE] {{-s -size ###} / {-min ### -max ### -step ###}} (-r) #algorithm specific params#
     * arguments within {} are required, () are optional, -a / -b means either -a or -b parameter,  ### means required value right after the parameter <br>
     * -[TYPE] - determines type of a test:<br>
     * *all - tests all implemented quick sort algorithms. Checks min/max/avg compare,swap and time Cannot be used with -s.<br>
     * *radix - tests radix sort. specific params: { -d / {-rsize ### -wsize ###}}<br>
     * **-d distinct values<br>
     * **-rsize radix size <br>
     * **-wsize word size <br>
     * *rselect - tests random select<br>
     * *select - tests select<br>
     * *wqs - tests quick with select<br>
     * -s - single test for given N size. Otherwise test alghoritms within [K,L] size range every M steps.<br>
     * -size - required for -s - Size of tested array.<br>
     * -min - required for default (without -s) use - Minimal size boundary<br>
     * -max - required for default (without -s) use - Maximal size boundary<br>
     * -step - required for default (without -s) use - Step size for size iteration.<br>
     * -r - uses reverse sorted array.<br>
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length>1){   
            boolean single_test = isThereParameter("-s",args);
            boolean reverse = isThereParameter("-r",args);
            boolean distinct = isThereParameter("-d",args);
            int stat_pos = getParameter("-pos", args);
            int tab_size = getParameter("-size", args);
            int step_min_size = getParameter("-min", args);
            int step_max_size = getParameter("-max", args);
            int step_size = getParameter("-step", args);
            
            String algo = "";
            SortLogRadix log1 = null;
            SelectLog log2 = null;
            switch(args[0]){
                case "-radix":
                        algo = "Radix Sort";
                        int word_size = getParameter("-wsize", args);
                        int radix_size = getParameter("-rsize", args);
                        if(distinct){
                            radix_size = 10;
                            word_size = (int)Math.ceil(Math.log10(tab_size));
                        }else if ((word_size < 1 || radix_size < 1 ) && !distinct){
                            if(radix_size < 1 )
                                radix_size = 10;
                            if(word_size < 1 )
                                word_size = 10;
                        }
                        if(single_test){
                            if(tab_size <= 0 ){
                                System.err.println("No specified tab size!");
                                return;
                            }    
                            Integer[][] t = new Integer[tab_size][word_size];
                            for(int i=0 ; i< t.length ; i++){
                                int n;
                                if(distinct){
                                    n = i;
                                    for (int j=t[i].length-1 ; j>=0 && n>=0 ; j--){
                                        t[i][j]  = n%radix_size;
                                        n/=radix_size;
                                    }
                                }else{
                                    for (int j=0 ; j< t[i].length ; j++){
                                        t[i][j]  = ThreadLocalRandom.current().nextInt(radix_size);
                                    }
                                }
                            }
                            if(reverse)
                                Arrays.sort(t, (Comparable[] o1, Comparable[] o2) -> -compare(o1,o2) );
                            else if (distinct){
                                List l = Arrays.asList(t);
                                Collections.shuffle(l);
                                t = (Integer[][])l.toArray();
                            }
                            
                          /*  for (Integer[] t1 : t) {
                                for (int j = 0; j < t1.length; j++) {
                                    System.out.print(((j>0)?",":"") + t1[j]);
                                }
                                System.out.println("");
                            }*/
                            //Integer[][] copy = t.clone();
                            
                            log1 = RadixSort.RadixSortWLog(t,System.out,true);
                            
                            //Arrays.sort(copy, (Comparable[] o1, Comparable[] o2) -> compare(o1,o2) );
                            //System.out.println("works??"+(Arrays.equals(t, copy)));
                            
                            if(log1!=null){
                                System.out.println(algo+" statistics for "+tab_size+" element array. Swap count:"+log1.getSwapCount()+" Compare count:"+log1.getCompareCount());
                                JOptionPane.showMessageDialog(null,new JScrollPane(new JLabel(new ImageIcon(SortLogRadix.historyToImage(log1)))),"Sorting",JOptionPane.PLAIN_MESSAGE);
                            }
                        }else{                 
                            Random r = new Random();

                            ExecutorService es = Executors.newFixedThreadPool(1);
                            ArrayList<Future<long[]>> al = new ArrayList<>();
                            
                            if(step_min_size<1 || step_max_size < step_min_size || step_size<0 ){
                                System.err.println("Illegal min,max or step size!");
                                return;
                            }
                            
                            for(int size=step_min_size ; size<=step_max_size ; size+=step_size){
                                al.add( es.submit(new RadixSort.RadixWorker(size, reverse, 1,word_size,radix_size)));
                            }
                            
                            System.out.println("Array Size\t Radix Sort swap count");
                            while(!al.isEmpty()){
                                ArrayList<Future<long[]>> cal = (ArrayList<Future<long[]>>) al.clone();
                                for(Future<long[]> fut : cal){
                                    if(fut.isDone()){
                                        long[] tab;
                                        try {
                                            tab = fut.get();   
                                            System.out.println(tab[0]+"\t"+tab[2]);
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
                       // log1 = insertionSortWithLog(t,System.out,true);
                    break;
                case "-rselect":
                        algo = "Randomized Select";
                        if(single_test){
                            if(tab_size>0 && stat_pos>0 && stat_pos<tab_size){
                                Comparable[] t = new Comparable[tab_size];
                                for(int i=0 ; i< t.length ; i++){
                                    int n;
                                    if(distinct){
                                        t[i]  = i;
                                    }else{
                                        t[i]  = ThreadLocalRandom.current().nextInt(250);
                                    }
                                }
                                if(reverse)
                                    Arrays.sort(t, (Comparable o1, Comparable o2) -> -o1.compareTo(o2) );
                                else if (distinct){
                                    List l = Arrays.asList(t);
                                    Collections.shuffle(l);
                                    t = (Integer[])l.toArray();
                                }

                                Map.Entry<Map.Entry<Integer,Comparable>,SelectLog> ent = RandSelect.randSelectWithLog(t,stat_pos,System.out);
                                log2 = ent.getValue();

                                System.out.println("\nSorted array with marked "+stat_pos+"th element:");
                                Arrays.sort(t);
                                SelectLog.printlnTab(t, stat_pos-1, log2);

                            }
                        }else{
                            Random r = new Random();

                            ExecutorService es = Executors.newFixedThreadPool(4);
                            ArrayList<Future<long[]>> al = new ArrayList<>();
                            
                            if(step_min_size<1 || step_max_size < step_min_size || step_size<0 ){
                                System.err.println("Illegal min,max or step size!");
                                return;
                            }
                            for(int size=step_min_size ; size<=step_max_size ; size+=step_size){
                                al.add( es.submit(new SelectWorker(size, reverse, 1000 ,true)));
                            }
                            
                            System.out.println("Array Size\tRandomized Select average compare count\tRandomized Select minimum compare count\tRandomized Select maximum compare count");
                            while(!al.isEmpty()){
                                ArrayList<Future<long[]>> cal = (ArrayList<Future<long[]>>) al.clone();
                                for(Future<long[]> fut : cal){
                                    if(fut.isDone()){
                                        long[] tab;
                                        try {
                                            tab = fut.get();   
                                            System.out.println(tab[0]+"\t"+tab[2]+"\t"+tab[3]+"\t"+tab[4]);
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
                    break;
                case "-select":
                        algo = "Select";
                        if(single_test){
                            if(tab_size>0 && stat_pos>0 && stat_pos<=tab_size){
                                Comparable[] t = new Comparable[tab_size];
                                for(int i=0 ; i< t.length ; i++){
                                    int n;
                                    if(distinct){
                                        t[i]  = i;
                                    }else{
                                        t[i]  = ThreadLocalRandom.current().nextInt(250);
                                    }
                                }
                                if(reverse)
                                    Arrays.sort(t, (Comparable o1, Comparable o2) -> -o1.compareTo(o2) );
                                else if (distinct){
                                    List l = Arrays.asList(t);
                                    Collections.shuffle(l);
                                    t = (Integer[])l.toArray();
                                }

                                Map.Entry<Map.Entry<Integer,Comparable>,SelectLog> ent = Select.SelectWithLog(t,stat_pos,5,System.out);
                                log2 = ent.getValue();

                                System.out.println("\nSorted array with marked "+stat_pos+"th element:");
                                Arrays.sort(t);
                                SelectLog.printlnTab(t, stat_pos-1, log2);

                            }
                        }else{
                            Random r = new Random();

                            ExecutorService es = Executors.newFixedThreadPool(10);
                            ArrayList<Future<long[]>> al = new ArrayList<>();
                            
                            if(step_min_size<1 || step_max_size < step_min_size || step_size<0 ){
                                System.err.println("Illegal min,max or step size!");
                                return;
                            }
                            for(int size=step_min_size ; size<=step_max_size ; size+=step_size){
                                al.add( es.submit(new SelectWorker(size, reverse, 300 ,false)));
                            }
                            
                            System.out.println("Array Size\tRandomized Select average compare count\tRandomized Select minimum compare count\tRandomized Select maximum compare count");
                            while(!al.isEmpty()){
                                ArrayList<Future<long[]>> cal = (ArrayList<Future<long[]>>) al.clone();
                                for(Future<long[]> fut : cal){
                                    if(fut.isDone()){
                                        long[] tab;
                                        try {
                                            tab = fut.get();   
                                            System.out.println(tab[0]+"\t"+tab[2]+"\t"+tab[3]+"\t"+tab[4]);
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
                    break;
                case "-qws":
                        algo = "Quick Sort with Select/Median of Medians test";
                        
                        if(tab_size<0){
                            System.err.println("Invalid size");
                            return;
                        }
                            
                        Comparable[] t = new Comparable[tab_size];
                        
                        for(int i=0 ; i< t.length ; i++){
                            t[i]  = ThreadLocalRandom.current().nextInt(250);
                        }
                        System.out.println("\nTest Array:");
                        SortLog l = new SortLog(tab_size, System.out, false);
                        SortLog.printlnTab(t,l, true);
                        
                        Comparable[] tc = (Comparable[])t.clone();
                        Arrays.sort(tc, (Comparable o1, Comparable o2) -> o1.compareTo(o2) );
                        
                        System.out.println("\nMedian of Medians:");
                        Comparable[] t1 = (Comparable[])t.clone();
                        SortLog log = QuickSortSelect.quickSortMedian(t1);
                        SortLog.printlnTab(t1,l, true);
                        if(!Arrays.equals(tc, t1)){
                            System.out.println("Array is wrongly sorted!");
                        }
                        
                        System.out.println("\nSelect:");
                        Comparable[] t2 = (Comparable[])t.clone();
                        log =QuickSortSelect.quickSortSelect(t2);
                        SortLog.printlnTab(t2,l, true);
                        if(!Arrays.equals(tc, t2)){
                            System.out.println("Array is wrongly sorted!");
                        }
                    break;
                case "-all":
                        ExecutorService es = Executors.newFixedThreadPool(8);
                        ArrayList<Future<Entry<Integer,long[][]>>> al = new ArrayList<>();

                        if(step_min_size<1 || step_max_size < step_min_size || step_size<0 ){
                            System.err.println("Illegal min,max or step size!");
                            return;
                        }
                        for(int size=step_min_size ; size<=step_max_size ; size+=step_size){
                            al.add( es.submit(new QuickSortWorker(size,1000)));
                        }

                        System.out.println(
                                  "Array Size\t"
                                + "QS avg comps\t"
                                + "QS min comps\t"
                                + "QS max comps\t"
                                + "QS avg swaps\t"
                                + "QS min swaps\t"
                                + "QS max swaps\t"
                                + "QS avg Time\t"
                                + "QS min Time\t"
                                + "QS max Time\t"
                                          
                                + "QS w Ins avg comps\t"
                                + "QS w Ins min comps\t"
                                + "QS w Ins max comps\t"
                                + "QS w Ins avg swaps\t"
                                + "QS w Ins min swaps\t"
                                + "QS w Ins max swaps\t"
                                + "QS w Ins avg Time\t"
                                + "QS w Ins min Time\t"
                                + "QS w Ins max Time\t"
                                          
                                + "QS dual avg comps\t"
                                + "QS dual min comps\t"
                                + "QS dual max comps\t"
                                + "QS dual avg swaps\t"
                                + "QS dual min swaps\t"
                                + "QS dual max swaps\t"
                                + "QS dual avg Time\t"
                                + "QS dual min Time\t"
                                + "QS dual max Time\t"
                                          
                                + "QS med avg comps\t"
                                + "QS med min comps\t"
                                + "QS med max comps\t"
                                + "QS med avg swaps\t"
                                + "QS med min swaps\t"
                                + "QS med max swaps\t"
                                + "QS med avg Time\t"
                                + "QS med min Time\t"
                                + "QS med max Time\t"
                                          
                                + "QS sel avg comps\t"
                                + "QS sel min comps\t"
                                + "QS sel max comps\t"
                                + "QS sel avg swaps\t"
                                + "QS sel min swaps\t"
                                + "QS sel max swaps\t"
                                + "QS sel avg Time\t"
                                + "QS sel min Time\t"
                                + "QS sel max Time\t"
                        );
                        while(!al.isEmpty()){
                            ArrayList<Future<Entry<Integer,long[][]>>> cal = (ArrayList<Future<Entry<Integer,long[][]>>>) al.clone();
                            for(Future<Entry<Integer,long[][]>> fut : cal){
                                if(fut.isDone()){
                                    try {
                                        Entry<Integer,long[][]> ent = fut.get();  
                                        long[][] tab = ent.getValue();
                                        
                                        System.out.print(ent.getKey());
                                        for(int i=0 ; i<tab.length;i++){   
                                            for(int j=0 ; j<tab[i].length;j++){
                                                System.out.print("\t"+tab[i][j]);
                                            } 
                                        }
                                        System.out.println();
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
                    break;
                default:
                        System.err.println("Invalid test type argument!");
                    break;

            }
        }
    }
     /**
     * Returns entry where key value is test array size and array long[5][9] are results:<br>
     * t[0]- quickSort ith median of three pivot selection.<br>
     * t[1]- quickSort with median of three pivot selection and insertion sort at arrays below the size of 27.<br>
     * t[2]- dual pivot quick sort and insertion sort at arrays below the size of 27.<br>
     * t[3]- quickSort with median of medians pivot selection and insertion sort at arrays below the size of 27.<br>
     * t[4]- quickSort with Select pivot sleection and insertion sort at arrays below the size of 27.<br>
     * 
     * t[x][0] - average compare count
     * t[x][1] - min compare count
     * t[x][2] - max compare count
     * t[x][3] - average swap count
     * t[x][4] - min swap count
     * t[x][5] - max swap count
     * t[x][6] - average time in ns
     * t[x][7] - min time in ns
     * t[x][8] - max time in ns
     */
    public static class QuickSortWorker implements Callable<Entry<Integer,long[][]>>{ 
        final int size;
        final int tests;
        public QuickSortWorker(int size,int tests) {
            this.size = size;
            this.tests = tests;
        }
        private void sort(int type,long[] res,Integer[] t){
            SortLog log;
            long t_start = System.nanoTime();
            switch(type){
                case 0:
                        log = QuickSortSelect.quickSort(t);
                    break;
                case 1:
                        log = QuickSortSelect.quickSortInsert(t);
                    break;
                case 2:
                        log = QuickSortSelect.quickSortDual(t);
                    break;
                case 3:
                        t_start = System.nanoTime();
                        log = QuickSortSelect.quickSortMedian(t);
                    break;
                default:
                        log = QuickSortSelect.quickSortSelect(t);
                    break;
            }
            long time = System.nanoTime() - t_start;
            
            long comp = log.getCompareCount();
            long swap = log.getSwapCount();
            res[0]+=comp;
            res[3]+=swap;
            res[6]+=time;
            if(comp<res[1])
                res[1]=comp;
            if(comp>res[2])
                res[2]=comp;
            if(swap<res[4])
                res[4]=swap;
            if(swap>res[5])
                res[5]=swap;
            if(time<res[7])
                res[7]=time;
            if(time>res[8])
                res[8]=time;
            
        }
        @Override
        public Entry<Integer,long[][]> call() throws Exception {
            //System.out.println("Strating thread");
            try{
                long[][] res = new long[5][9];
                for(int i=0;i<res.length;i++){
                    for(int a=0;a<3;a++){
                        res[i][a*3] = 0;
                        res[i][a*3+1] = Long.MAX_VALUE;
                        res[i][a*3+2] = Long.MIN_VALUE;
                    }
                }
                
                Integer[] t = new Integer[size];
                for(int i=0;i<tests;i++){
                    for(int x=0;x<t.length;x++){
                        t[x] = ThreadLocalRandom.current().nextInt(size);
                    }
                    Integer[] copy = (Integer[])t.clone();
                    Arrays.sort(copy);   
                    Integer[] t_work = (Integer[])t.clone();
                    for(int r=0;r<res.length;r++){
                        sort(r, res[r],t_work);
                        if(!Arrays.equals(t_work, copy)){
                            System.err.println("Wrongly sorted array\noriginal array:\n"+Arrays.toString(t));
                            System.err.println("sorted array:\n"+Arrays.toString(copy));
                            System.err.println("sorted by "+r+"th algo array:\n"+Arrays.toString(t_work));
                            throw new Exception("Wrongly sorted array");
                        }
                    }
                    
                }
                for(int i=0;i<res.length;i++){
                    for(int a=0;a<3;a++){
                        res[i][a*3] /= tests;
                    }
                }
                
                return new AbstractMap.SimpleEntry<>(size,res);
            }catch(Exception ex){
                System.err.println("Shit's pucked:"+ex.getMessage());
                ex.printStackTrace();
                System.exit(-1);
            }
            return null;
        }
    }    
    /**
     * Returns long[4][2] where long[0..3] returns count of compare and swap respectively. long[4] returns size and number of tests
     */
    public static class SelectWorker implements Callable<long[]>{ 
        final int size;
        final boolean reverse;
        final int tests;
        final boolean random;
        public SelectWorker(int size, boolean reverse, int tests,boolean random) {
            this.size = size;
            this.reverse = reverse;
            this.tests = tests;
            this.random = random;
        }

        @Override
        public long[] call() throws Exception {
            //System.out.println("Strating thread");
            try{
                long[] stats = new long[5];
                stats[0]=size;
                stats[1]=tests;
                stats[3]=Long.MAX_VALUE;
                stats[4]=Long.MIN_VALUE;
                SelectLog log;
                Integer[] t = new Integer[1000/*size*/];
                for (int a = 0; a < t.length; a++) {
                    t[a] = ThreadLocalRandom.current().nextInt(size);
                } 
                
                for(int i=0;i<tests;i++){
                 /*   for (int a = 0; a < t.length; a++) {
                        t[a] = ThreadLocalRandom.current().nextInt(size);
                    } */
                    int pos = 1+(int)Math.log(size);
                    //if(pos>size){
                        pos = size;
                    //}
                    if(reverse)
                        Arrays.sort(t, (Comparable o1, Comparable o2) -> o1.compareTo(o2));      
                    Map.Entry<Map.Entry<Integer,Comparable>,SelectLog> ent ;
                    if(random)
                        ent = RandSelect.randSelectWithLog(t.clone(),pos,null);
                    else{
                        ent = Select.SelectWithLog(t.clone(),pos,5,null);
                    }
                        
                    Arrays.sort(t);
                    if(t[pos-1].compareTo((Integer)ent.getKey().getValue()) != 0 ){
                        throw new Exception("They are not the same elements!");
                    }
                    
                    log = ent.getValue();
                    long cc = log.getCompareCount();
                    
                    if(cc>stats[4])
                        stats[4] = cc;
                    if(cc<stats[3])
                        stats[3] = cc;
                }
                stats[2] /=tests;
                return stats;
            }catch(Exception ex){
                System.err.println("Shit's pucked:"+ex.getMessage());
                ex.printStackTrace();
                System.exit(-1);
            }
            return null;
        }
    }    
    
    
}
