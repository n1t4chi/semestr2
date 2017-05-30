/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L3;

import Log.SelectLog;
import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author n1t4chi
 */
public class RandSelect {
    
    public static Entry<Integer,Comparable> randSelect(Comparable[] A, int i){
        return randSelect(A, 0, A.length-1, i, null,true);
    }
    
    public static Entry<Entry<Integer,Comparable>,SelectLog> randSelectWithLog(Comparable[] A, int i,PrintStream out){
        SelectLog log = new SelectLog(out);
        Entry<Entry<Integer,Comparable>,SelectLog> rtrn = new AbstractMap.SimpleEntry<>(randSelect(A, 0,A.length-1,i,log,true),log);
        return rtrn;
    }
    
    private static Entry<Integer,Comparable> randSelect(Comparable[] A,int p,int q,int i,SelectLog log,boolean first){
        if(first){
            SelectLog.println("randSelect(p:"+p+" q:"+q+" i:"+i+") received below tab", log);
            SelectLog.printlnTab(A,-1, log);
        }
        Entry<Integer,Comparable> rtrn;
        if(p==q){
            rtrn = new AbstractMap.SimpleEntry<>(p,A[p]); 
        }else{
            int r = randPartition(A,p,q,log);
            int k = r-p+1;
            if(i==k){
                rtrn = new AbstractMap.SimpleEntry<>(r,A[r]); 
            }else{
                if(i<k){
                    rtrn = randSelect(A, p, r-1, i, log, false);
                }else{ /*(i>k)*/
                    rtrn = randSelect(A, r+1, q, i-k, log, false);
                }     
            }
        }
        SelectLog.println("", log);
        if(first){
            SelectLog.printlnSummary(A, log);
        }
        SelectLog.println("randSelect(p:"+p+" q:"+q+" i:"+i+") returns element:"+rtrn.getValue()+" at "+(rtrn.getKey()+1)+"th position", log);
        SelectLog.printlnTab(A, rtrn.getKey(), log);
        return rtrn;   
    }

    private static int randPartition(Comparable[] A, int p, int q,SelectLog log){
        SelectLog.println("", log);
        SelectLog.println("randPartition(p:"+p+" q:"+q+") received below tab", log);
        SelectLog.printlnTab(A,-1, log);
        int piv_i;
        Comparable piv;
        if(p>=q){ 
            piv_i = q;
            piv = A[q];
        }else{
            piv_i = ThreadLocalRandom.current().nextInt(p,q+1);
            piv = A[piv_i];
            SelectLog.println("randPartition(p:"+p+" q:"+q+") selected pivot "+piv+" at "+piv_i, log);
            swap(A,piv_i,p,log);
            piv_i = p;
            int start = p+1;
            int end = q;
           /* piv = 4
            4,(1),2,3,7,6,5 s=2 e=7
            4,1,(2),3,7,6,5 s=3 e=7
            4,1,2,(3),7,6,5 s=4 e=7
            4,1,2,3,(7),6,5 s=5 e=7
            4,1,2,3,(5),6,7 s=5 e=6
            4,1,2,3,(6),5,7 s=5 e=5        
            */


            while(start<end){
                SelectLog.incrementCompare(log);
                if(piv.compareTo(A[start])<0){
                    swap(A,start,end,log);
                    end--;
                }else{
                    start++;
                }
            }
            SelectLog.incrementCompare(log);
            if(piv.compareTo(A[start])>0){
                swap(A,p,start,log);
                piv_i = start;
            }else{
                swap(A,p,start-1,log);
                piv_i = start-1;
            }
        }
        SelectLog.println("randPartition(p:"+p+" q:"+q+") finished working and divided array as seen below by pivot"+piv+" and division is at "+piv_i+" position", log);
        SelectLog.printlnTab(A,piv_i, log);
        return piv_i;
    }
    
    private static void swap(Comparable[] tab,int i,int j,SelectLog log){
        if(i!=j){
            SelectLog.incrementCompare(log);
            if(tab[i]!=tab[j]){
                Comparable c = tab[i];
                tab[i] = tab[j];
                tab[j] = c;
           //    SelectLog.incrementSwap(log);
            //    SelectLog.addToHistory(tab, log,true);
                SelectLog.println("swap("+i+","+j+") swaps "+tab[i]+" with "+tab[j], log /*always*/);
            }
        }
    }
}
