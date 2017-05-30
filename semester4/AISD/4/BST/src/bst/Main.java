/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bst;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author n1t4chi
 */
public class Main {
    private static final Pattern VALUE = Pattern.compile("(\\d+)");
    
    
    
    public static void main(String[] args) {
        if(args.length>0){
            try {
                TreeAdapter ta = new TreeAdapter();
                BufferedReader br = new BufferedReader(new FileReader(args[0]));
                String l;
                int i=1;
                while((l=br.readLine())!=null && i>0){
                    l = l.toLowerCase();
                    Matcher m = VALUE.matcher(l);
                    if(m.matches()){
                        i += Integer.parseInt(l);
                    }else{
                        if(m.find()){
                            String f = m.group();
                            int v = Integer.parseInt(f); 
                            if(l.startsWith("insert")){
                                ta.insert(v);
                            }else if(l.startsWith("delete")){
                                ta.delete(v);
                            }else if(l.startsWith("find")){
                                ta.find(v);
                            }else{
                                throw new IOException("Invalid command!");
                            }
                        }else{
                            if(l.startsWith("min")){
                                ta.min();
                            }else if(l.startsWith("max")){
                                ta.max();
                            }else if(l.startsWith("inorder")){
                                ta.inorder();
                            }else{
                                throw new IOException("Invalid command!");
                            }
                        }
                    }
                    i--;
                }
                
            } catch (FileNotFoundException ex) {
                System.out.println("File not found!:"+ex);
            } catch (IOException ex) {
                System.out.println("Invalid input file!:"+ex);
            } catch (Exception ex) {
                System.err.println("Error:"+ex);
                ex.printStackTrace();
            }
            
        }else{
            ArrayList<Future<long[]>> al = new ArrayList();
            ExecutorService es = Executors.newFixedThreadPool(10);
         /*   for(int i=1 ; i<= 300 ; i+=1){
                al.add(es.submit(new TreeWorker(i)));
            }
            for(int i=400 ; i<= 100000 ; i+=200){
                al.add(es.submit(new TreeWorker(i)));
            }*/
            for(int i=942000 ; i<= 1000000 ; i+=4000){
                al.add(es.submit(new TreeWorker(i)));
            }
            System.out.println("Size\tAvg\tMin\tMax");
            while(!al.isEmpty()){
                ArrayList<Future<long[]>> ac = (ArrayList<Future<long[]>>)al.clone();
                for(Future<long[]> f : ac){
                    if(f.isDone()){
                        try {
                            long[] r = f.get();
                            System.out.println(r[0]+"\t"+r[1]+"\t"+r[2]+"\t"+r[3]);
                            al.remove(f);
                        } catch (InterruptedException | ExecutionException ex) {
                            System.err.println("Something bucked:"+ex);
                            al.clear();
                        }
                    }else{
                        try{
                            Thread.sleep(1000);
                        }catch(InterruptedException ex){
                        }
                        break;
                    }
                }
            }
            es.shutdown();
        }
    }
    
    /**
     * returns long[] r;
     * r[0] - average find comparison
     * r[1] - minimum find comparison
     * r[2] - maximum find comparison
     */
    private static class TreeWorker implements Callable<long[]>{
        final int size;

        public TreeWorker(int size) {
            this.size = size;
        }
        
        @Override
        public long[] call() throws Exception {
            long[] rtrn = new long[4];
            int max_tests = 100;
            int finds = (int)Math.sqrt(size);
            rtrn[0] = size;
            rtrn[1] = 0;
            rtrn[2] = Long.MAX_VALUE;
            rtrn[3] = Long.MIN_VALUE;
            for(int test = 0; test<max_tests ; test++){
                BinaryTree<BinaryTreeNode<Integer>,Integer> t = new BinaryTree(BinaryTreeNode.class,Integer.class);
                ArrayList<Integer> al = new ArrayList<>();
                for(int i=0;i<size;i++){
                    al.add(i);
                }
                Collections.shuffle(al);
                al.forEach((i) -> {
                    t.insert(al.get(i));
                });

                List<BinaryTreeNode<Integer>> l = t.inorder();
                Collections.shuffle(l);
                if(l.size() ==size){
                    
                    for(int i=0 ; i< finds ; i++ ){
                    //for(BinaryTreeNode<Integer> n : l){
                        t.find(l.get(i).getValue());
                        int c = t.getCounter();
                        rtrn[1] += c;
                        if(c<rtrn[2])
                            rtrn[2]=c;
                        if(c>rtrn[3])
                            rtrn[3]=c;
                   // }
                    }
                }else{
                    throw new Exception("Illegal tree size!");
                }
            }
            //System.err.println(""+rtrn[1]+"/"+(finds*max_tests) );
            rtrn[1] /= finds*max_tests;
            return rtrn;
        }

        
    }
    
}
