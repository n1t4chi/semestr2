/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zad1;

import L3.RandSelect;
import L3.Select;
import bst.avl.OSAVL;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author n1t4chi
 */
public class Z1 {
    
    
    /**
     * returns 
     * size,tab
     * tab:
     * tab[0]- select
     * tab[1]- rand select
     * tab[2]- tree select
     * tab[x][0]- min 1 tab - 1 select
     * tab[x][1]- max 1 tab - 1 select
     * tab[x][2]- avg 1 tab - 1 select
     * tab[x][3]- min 1 tab - multiple selects
     * tab[x][4]- max 1 tab - multiple selects
     * tab[x][5]- avg 1 tab - multiple selects
     */
    private static class TestWorker implements Callable<Entry<Integer,long[][]>>{
        final int size;
        private static final int TESTS = 1000;
        public TestWorker(int size) {
            this.size = size;
        }
            
        
        @Override
        public Entry<Integer,long[][]> call() throws Exception {
            try{
                ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
                long[][] rtrn = new long[3][6];

                for (long[] rtrn1 : rtrn) {
                    rtrn1[3] = rtrn1[0] = Long.MAX_VALUE;
                    rtrn1[4] = rtrn1[1] = Long.MIN_VALUE; 
                }


                Integer[] tab = new Integer[size];
                for (int i = 0; i < tab.length; i++) {
                    tab[i] = i;
                }
                for(int test = 0; test<3 /*TESTS*/ ;test++){
                    /*test select*/{
                        int r = ThreadLocalRandom.current().nextInt(size)+1;
                        long start = bean.getCurrentThreadCpuTime();
                        Select.Select(tab, r, 7);
                        long single = bean.getCurrentThreadCpuTime();
                        for(int i= 0 ; i< TESTS ; i++){
                            Select.Select(tab, ThreadLocalRandom.current().nextInt(size)+1, 7);
                        }
                        long multiple = bean.getCurrentThreadCpuTime();
                        single -=start;
                        multiple -=start;

                        rtrn[0][2] += single;
                        rtrn[0][5] += multiple;

                        if(rtrn[0][0] > single)
                            rtrn[0][0] = single;

                        if(rtrn[0][3] > multiple)
                            rtrn[0][3] = multiple;

                        if(rtrn[0][1] < single)
                            rtrn[0][1] = single;

                        if(rtrn[0][4] < multiple)
                            rtrn[0][4] = multiple;
                    }
                    /*test rand select*/{
                        int r = ThreadLocalRandom.current().nextInt(size)+1;
                        long start = bean.getCurrentThreadCpuTime();
                        RandSelect.randSelect(tab, r);
                        long single = bean.getCurrentThreadCpuTime();
                        for(int i= 0 ; i< TESTS ; i++){
                            RandSelect.randSelect(tab, ThreadLocalRandom.current().nextInt(size)+1);
                        }
                        long multiple = bean.getCurrentThreadCpuTime();
                        single -=start;
                        multiple -=start;

                        rtrn[1][2] += single;
                        rtrn[1][5] += multiple;

                        if(rtrn[1][0] > single)
                            rtrn[1][0] = single;

                        if(rtrn[1][3] > multiple)
                            rtrn[1][3] = multiple;

                        if(rtrn[1][1] < single)
                            rtrn[1][1] = single;

                        if(rtrn[1][4] < multiple)
                            rtrn[1][4] = multiple;
                    }
                    /*test OSAVL select*/{
                        int r = ThreadLocalRandom.current().nextInt(size)+1;
                        long start = bean.getCurrentThreadCpuTime();
                        OSAVL<Integer> tree = new OSAVL(Integer.class);
                        for (Integer integer : tab) {
                            tree.insert(integer);
                        }
                        tree.Select(r);
                        long single = bean.getCurrentThreadCpuTime();
                        for(int i= 0 ; i< TESTS ; i++){
                            tree.Select(ThreadLocalRandom.current().nextInt(size)+1);
                        }
                        long multiple = bean.getCurrentThreadCpuTime();
                        single -=start;
                        multiple -=start;

                        rtrn[2][2] += single;
                        rtrn[2][5] += multiple;

                        if(rtrn[2][0] > single)
                            rtrn[2][0] = single;

                        if(rtrn[2][3] > multiple)
                            rtrn[2][3] = multiple;

                        if(rtrn[2][1] < single)
                            rtrn[2][1] = single;

                        if(rtrn[2][4] < multiple)
                            rtrn[2][4] = multiple;
                    }



                }

                for (long[] rtrn1 : rtrn) {
                    rtrn1[2] /= TESTS;
                    rtrn1[5] /= TESTS; 
                }
                return new AbstractMap.SimpleEntry<>(size,rtrn);
            }catch(Exception ex){
                System.err.println("Exception: "+ex);
                return null;
            }
        }
        
    }
    
    
             
    private static void test() {
        ExecutorService se = Executors.newFixedThreadPool(16);
        ArrayList<Future<Entry<Integer,long[][]>>> al = new ArrayList(1_000_000/1_000);
        for(int i=10_000/*1_000*/;i<=10_000 ;i+=1_000){
            al.add(se.submit(new TestWorker(i)));
        }
        for(int i=100_000/*20_000*/;i<=100_000 ;i+=10_000){
            al.add(se.submit(new TestWorker(i)));
        }
        for(int i=1_000_000/*200_000*/;i<=1_000_000 ;i+=100_000){
            al.add(se.submit(new TestWorker(i)));
        }
        System.out.println("size"
                + "\tsel 1 min\tsel 1 max\tsel 1 avg\tsel mult min\tsel mult max\tsel mult avg"
                + "\trsel 1 min\trsel 1 max\trsel 1 avg\trsel mult min\trsel mult max\trsel mult avg"
                + "\tOSt 1 min\tOSt 1 max\tOSt 1 avg\tOSt mult min\tOSt mult max\tOSt mult avg"
        );
        while(!al.isEmpty()){
            ArrayList<Future<Entry<Integer,long[][]>>> cal = (ArrayList<Future<Entry<Integer,long[][]>>>) al.clone();
            for (Future<Entry<Integer, long[][]>> future : cal) {
                try {
                    Entry<Integer, long[][]> ent = future.get();
                    System.out.print(ent.getKey());
                    for (long[] ls : ent.getValue()) {
                        for (long l : ls) {
                            System.out.print("\t"+l);
                        }
                    }
                    System.out.println();
                } catch (InterruptedException | ExecutionException ex) {
                    System.err.println("Cannot retrieve the result "+ex);
                }
                al.remove(future);
            }
            
        }
        se.shutdown();
        
    }
    
    public static class dummy{
        public void doIt(int t,int i){
            t = i;
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        test();
        
    }
}
