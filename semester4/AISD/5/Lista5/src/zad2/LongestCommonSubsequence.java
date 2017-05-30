/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zad2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import zad1.Z1;

/**
 *
 * @author n1t4chi
 */
public class LongestCommonSubsequence {
    
    
    private static byte LCS(String A, String B,byte[][] C,int x, int y,AtomicLong ctr){
        if(ctr!=null)
            ctr.incrementAndGet();
        if (x<0 || y< 0)
            return 0;
        if( A.charAt(x) == B.charAt(y)){
            C[x][y] = (byte) (((x>0&&y>0)?C[x-1][y-1]:0)+1);
        }else{
            C[x][y] = (byte) Math.max( (y>0)?C[x][y-1]:0,(x>0)?C[x-1][y]:0 );
        }
        return C[x][y];
    }
    private static void rLCS(String A, String B,byte[][] C,int x, int y, ArrayList<String> al,String s,AtomicLong ctr){
      //  if(ctr!=null)
      //      ctr.incrementAndGet();
        if( x<0 || y<0){
            if(s.length() == C[A.length()-1][B.length()-1]){
                if(!al.contains(s))
                    al.add(s);
            }
        }else if( A.charAt(x) == B.charAt(y)){
            rLCS(A,B,C,x-1,y-1,al,A.charAt(x)+s,ctr);
        }else{
            if(x>0 && y >0){
                if(C[x-1][y]>= C[x][y-1])
                    rLCS(A,B,C,x-1,y,al,s,ctr);
                if(C[x][y-1]>= C[x-1][y])
                    rLCS(A,B,C,x,y-1,al,s,ctr);
            }else if (x>0){
                rLCS(A,B,C,x-1,y,al,s,ctr);
            }else{
                rLCS(A,B,C,x,y-1,al,s,ctr);
            }
            
        }
    }
    
    
    public static List<String> LCS(String A, String B,AtomicLong lcs,AtomicLong rlcs){
        if(A == null || B == null)
            return null;
        if(A.length() > B.length()){
            return LCS(B,A,lcs,rlcs);
        }
        ArrayList<String> al = new ArrayList();
        byte[][] C = new byte[A.length()][B.length()];
        for(int x=0; x<C.length;x++)
            for(int y=0; y<C[x].length;y++)
                LCS(A,B,C,x,y,lcs);
        
        rLCS(A,B,C,A.length()-1,B.length()-1,al,"",rlcs);
        return al;
    }
    
    private static class LCSworker implements Callable<long[]>{
        final int sizeA;
        final int sizeB;

        public LCSworker(int sizeA, int sizeB) {
            this.sizeA = sizeA;
            this.sizeB = sizeB;
        }
        private static final int TESTS = 1;
        
        @Override
        public long[] call() throws Exception {
            long[] rtrn = new long[2+3+3];
            rtrn[0] = sizeA;
            rtrn[1] = sizeB;
            rtrn[3] = Long.MAX_VALUE;
            rtrn[4] = Long.MIN_VALUE;
            rtrn[6] = Long.MAX_VALUE;
            rtrn[7] = Long.MIN_VALUE;
            
            for(int test = 0; test<TESTS; test++){
                char[] tabA = new char[sizeA]; 
                char[] tabB = new char[sizeB];
                for(int i=0 ; i< tabA.length ; i++){
                    tabA[i] = (char)(ThreadLocalRandom.current().nextInt('A', 'Z'+1));
                }
                for(int i=0 ; i< tabB.length ; i++){
                    tabB[i] = (char)(ThreadLocalRandom.current().nextInt('A', 'Z'+1));
                }
                AtomicLong lcs = new AtomicLong();
                AtomicLong rlcs = new AtomicLong();
                
                LCS(String.valueOf(tabA), String.valueOf(tabB), lcs, rlcs);
                long l_l = lcs.get();
                long l_r = rlcs.get();
                
                rtrn[2] += l_l;
                if(rtrn[3] > l_l)
                    rtrn[3] = l_l;
                if(rtrn[4] < l_l)
                    rtrn[4] = l_l;
                
                rtrn[5] += l_r;
                if(rtrn[6] > l_r)
                    rtrn[6] = l_r;
                if(rtrn[7] < l_r)
                    rtrn[7] = l_r;
                
            }
            rtrn[2] /= TESTS;
            rtrn[5] /= TESTS;
            return rtrn;
        }
        
    }
    
    
    public static void main(String[] args) {
        List<String> l = LCS("1234","3214",null,null);
        for (String string : l) {
            System.out.println(string);
        }
        l = LCS("ABCBDAB","BDCABA",null,null);
        for (String string : l) {
            System.out.println(string);
        }
     
  /*    ExecutorService se = Executors.newFixedThreadPool(8);
        ArrayList<Future<long[]>> al = new ArrayList(60*60);
        for(int i=1;i<=500 ;i++){
            for(int j=1;j<=500 ;j++){            
                if(i==1)
                    System.out.print("\t"+j);

                al.add(se.submit(new LCSworker(j, i)));
            }
        }
        
        
        
        long prev = -1;
        while(!al.isEmpty()){
            ArrayList<Future<long[]>> cal = (ArrayList<Future<long[]>>) al.clone();
            for (Future<long[]> future : cal) {
                try {
                    long[] tab = future.get();
                    if(tab[1] != prev){
                        prev = tab[1];
                        System.out.print("\n"+prev);
                    }
                    System.out.print("\t"+(tab[2]+tab[5]));
                    
                } catch (InterruptedException | ExecutionException ex) {
                    System.err.println("Cannot retrieve the result "+ex);
                }
                al.remove(future);
            }
            
        }
        System.out.println("");
        se.shutdown();
        */
    }
}
