/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L3;

import Log.SelectLog;
import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author n1t4chi
 */
public class Select {

    
    public static Map.Entry<Integer,Comparable> Select(Comparable[] A, int i,int n_div){
        return Select(A, 0, A.length-1, i-1,n_div, null,true);
    }
    
    public static Map.Entry<Map.Entry<Integer,Comparable>,SelectLog> SelectWithLog(Comparable[] A, int i,int n_div,PrintStream out){
        SelectLog log = new SelectLog(out);
        Map.Entry<Map.Entry<Integer,Comparable>,SelectLog> rtrn = new AbstractMap.SimpleEntry<>(Select(A, 0,A.length-1,i-1,n_div,log,true),log);
        return rtrn;
    }
    
    
    /**
     * Sorts A within [start,end)
     * @param A
     * @param log 
     */
    private static void Sort(Comparable[] A,int start, int end,SelectLog log){
        int step = 0;
        for(int i=start+1;i<=end;i++){
            SelectLog.println("Selecting  element at "+i+" ["+A[i]+"] for comparision.", log);
            Comparable x = A[i];
            int j;
            //while(j>=0 && A[j].compareTo(x)>0){
            for(j=i-1;j>=start;j--){
                SelectLog.incrementCompare(log);                
                if(A[j].compareTo(x)>0){
                   A[j+1]=A[j]; 
                   SelectLog.println("Moving element at "+j+" ["+A[j]+"] one position higher.", log);
                   SelectLog.printlnSummary(A, log);
                   SelectLog.println("", log);
                }else{
                    break;
                }  
            }
            A[j+1]=x;
            SelectLog.println("Inserting ["+x+"] at "+(j+1)+" position", log);
            SelectLog.println("\n", log);
        }
    }
    /**
     * Divides A so that each part has at most n_div elements and then sorts each part within itself.
     * sorts A in [start,end]
     * @param A
     * @param n_div
     * @param log 
     */
    public static void Sort(Comparable[] A,int start,int end,int n_div,SelectLog log){
        SelectLog.println("", log);
        SelectLog.println("Sort(s:"+start+" e:"+end+" ,ndiv:"+n_div+") received for sort this array:", log);
        SelectLog.printlnTab(A,-1,log);
        
        for(int i=start ; i<=end; i+=n_div){
            if(i+n_div>end){
                Sort(A, i, end,log);
            }else{
                Sort(A, i, i+n_div,log);
            }     
                
        }
        SelectLog.println("Finished sorting the array. Results:", log);
        SelectLog.printlnSummary(A, log);
        SelectLog.println("", log);
    }
    
    public static int swapMedians(Comparable[] A,int start,int end,int n_div,SelectLog log){
        SelectLog.println("",log);
        SelectLog.println("swapMedians(p:"+start+" q:"+end+" ndiv:"+n_div+") received below tab:", log);
        SelectLog.printlnTab(A,-1, log);
        int ceil = (int)Math.ceil(n_div/2.0);
        int it = start;
        for(int i=start+ceil-1 ; i<=end; i+=n_div){
            swap(A, i, it, log);
            it++;
        }
        SelectLog.println("swapMedians(p:"+start+" q:"+end+" ndiv:"+n_div+") returns end:"+(it-1)+" and changes array to:", log);
        SelectLog.printlnTab(A,-1, log);
        SelectLog.println("",log);
        return it-1;
    }
    
    
    public static Map.Entry<Integer,Comparable> Select(Comparable[] A,int p,int q,int i,int n_div,SelectLog log,boolean first){
        if(first){
        SelectLog.println("Select(p:"+p+" q:"+q+" i:"+i+" ndiv:"+n_div+") received below A", log);
        SelectLog.printlnTab(A,-1, log);
        }
        Sort(A,p,q,n_div,log);
        Map.Entry<Integer,Comparable> rtrn;
        if( (q-p+1)<= n_div ){
            rtrn = new AbstractMap.SimpleEntry<>(i,A[i]);
            //rtrn = new AbstractMap.SimpleEntry<>(i,A[i]);
        }else{
            int end_med = swapMedians(A,p,q,n_div,log);

            SelectLog.println("Select(p:"+p+" q:"+q+" i:"+i+" ndiv:"+n_div+") sorted A within ["+p+","+q+"] range and swapped medians to beggining:", log);
            SelectLog.printlnTab(A,-1, log);
            SelectLog.println("", log);

            Map.Entry<Integer,Comparable> ent = Select(A,p,end_med,(int)Math.ceil((p+end_med)/2.0),n_div,log,false);

            int r = Partition(A,p,q,ent.getKey(),log);

            if(r==i){
               // System.out.println("$$$$$$$$$$$$$$$$$$$$$$$ r:"+r+" i:"+i);
                rtrn = new AbstractMap.SimpleEntry<>(r,A[r]);
            }else if (r>i){
               // System.out.println("####################### r:"+r+" i:"+i);
                rtrn = Select(A, p, r-1, i, n_div, log,false);
            }else{ //r<i
               // System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ r:"+r+" i:"+i);
                rtrn = Select(A, r+1,q, i, n_div, log,false);
            }

        }
        
        SelectLog.println("", log);
        if(first){
            SelectLog.printlnSummary(A, log);
        }
        SelectLog.println("Select(p:"+p+" q:"+q+" i:"+i+" ndiv:"+n_div+") returns element:"+rtrn.getValue()+" at "+(rtrn.getKey()+1)+"th position", log);
        SelectLog.printlnTab(A, rtrn.getKey(), log);
        return rtrn;   
    }

    private static int Partition(Comparable[] A, int left, int right,int piv_i,SelectLog log){
        SelectLog.println("", log);
        SelectLog.println("Partition(p:"+left+" q:"+right+" piv_i:"+piv_i+") received below A", log);
        SelectLog.printlnTab(A,-1, log);
        
        Comparable piv = A[piv_i];
        swap(A,piv_i,right,log);
        int si = left;
        for(int i=left ; i<right;i++){
            SelectLog.incrementCompare(log);
            if(A[i].compareTo(piv)<0){
                swap(A,i,si,log);
                si++;
            }
        }

        swap(A,right,si,log);
        SelectLog.println("Partition(p:"+left+" q:"+right+") finished working and divided array as seen below by pivot "+piv+" and division is at "+si+" position", log);
        SelectLog.printlnTab(A,si, log);
        SelectLog.println("", log);
        return si;
    }
    
    private static void swap(Comparable[] A,int i,int j,SelectLog log){
        if(i!=j){
            //SelectLog.incrementCompare(log);
           // if(A[i]!=A[j]){
                Comparable c = A[i];
                A[i] = A[j];
                A[j] = c;
           //    SelectLog.incrementSwap(log);
            //    SelectLog.addToHistory(A, log,true);
                SelectLog.println("swap("+i+","+j+") swaps "+A[i]+" with "+A[j], log /*always*/);
           // }
        }
    }
    
}
