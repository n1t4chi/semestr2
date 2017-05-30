/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L3;

import Log.SortLog;
import Log.SortLog;
import Log.SortLog;
import static Log.SortLog.incrementCompare;
import static Log.SortLog.println;
import static Log.SortLog.printlnTab;
import java.util.AbstractMap;
import java.util.Map;

/**
 *
 * @author n1t4chi
 */
public class QuickSortSelect {
    /**
     * quickSort method.
     * @param tab array to sort
     * @param when at which size of array to switch to insertion to sort
     * @param log for logging purposes
     */
    private static void dualPivotQuickSort(Comparable[] tab,int left, int right,int div,SortLog log){
        if(left<right){
            int length = right-left;
            if(length>27){
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
                    swap(tab, m1, left, log);
                    swap(tab, m2, right, log);
                }else{
                    swap(tab, m1, right, log);
                    swap(tab, m2, left, log);
                }
                pivot1 = tab[left];
                pivot2 = tab[right];
                int less=left+1;
                int great=right-1;
                for(int k = less; k<=great ; k++){
                    incrementCompare(log);
                    if(tab[k].compareTo(pivot1) < 0){
                        swap(tab,k,less++,log);
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
                        swap(tab,k,great--,log);
                        incrementCompare(log);
                        if(tab[k].compareTo(pivot1)<0){
                            swap(tab,k,less++,log);
                        }
                    }else{
                        incrementCompare(log);
                    }
                }
                int dist = great - less;
                if(dist < 13){
                    div++;
                }
                swap(tab,less-1,left,log);
                swap(tab,great+1,right,log);
                dualPivotQuickSort(tab,left,less-2,div,log);
                dualPivotQuickSort(tab,great+2,right,div,log);
                if(dist > length -13){
                    incrementCompare(log);
                    if(pivot1.compareTo(pivot2)!=0){
                        for(int k=less;k<=great;k++){
                            incrementCompare(log);
                            if(tab[k].compareTo(pivot1)==0){
                                swap(tab, k, less++, log);
                            }else if(tab[k].compareTo(pivot2)==0){
                                incrementCompare(log);
                                swap(tab, k, great--, log);
                                incrementCompare(log);
                                if(tab[k].compareTo(pivot1)==0){
                                    swap(tab, k, less++, log);
                                }
                            }else
                                incrementCompare(log);
                        }
                    }
                }
                incrementCompare(log);
                if(pivot1.compareTo(pivot2)<0){
                    dualPivotQuickSort(tab, less, great, div, log);
                }
            }else{
                Sort(tab,left,right,log);
            }
        }
    }
    
    
        /**
     * quickSort method.
     * @param tab array to sort
     * @param when at which size of array to switch to insertion to sort
     * @param log for logging purposes
     */
    private static void quickWithInsertionSort(Comparable[] tab,int start, int end,SortLog log){
        if(start<end){
            if(end-start>27){
                int separator = Partition(tab,start,end,medianOfThree(tab,start,end,log),log);
                quickWithInsertionSort(tab, start, separator, log);
                quickWithInsertionSort(tab, separator+1, end, log);
            }else{
                Sort(tab,start,end,log);
            }
        }
    }
    
    
    private static int medianOfThree(Comparable[] tab,int start, int end,SortLog log){
        int middle = (start+end)/2;
        if(start > middle){
            incrementCompare(log);
            if(tab[middle].compareTo(tab[start])<0){ //b<a
                incrementCompare(log);
                if(tab[end].compareTo(tab[middle])<0){ //c<b<a
                    swap(tab,start,end,log);
                }else{  //b<a?c
                    swap(tab,start,middle,log);
                    //b,a,c
                    incrementCompare(log);
                    if(tab[end].compareTo(tab[start])<0){ //b<c<a                        
                        swap(tab,middle,end,log);
                    }//else b<a<c
                }  
            }else{ //a<b
                incrementCompare(log);
                if(tab[end].compareTo(tab[middle])<0){ //a?c<b
                    swap(tab,middle,end,log);
                    incrementCompare(log);
                    if(tab[end].compareTo(tab[start])<0){ //c<a<b
                        swap(tab,start,middle,log);
                    }
                }  
            }
        }else{
            incrementCompare(log);
            if(tab[end].compareTo(tab[start])<0)
                swap(tab,end,start,log);
        }
        return middle;
    }
    
    private static void quickSort(Comparable[] tab,int start, int end,SortLog log){
        if(start<end){
            int separator = Partition(tab,start,end,medianOfThree(tab,start,end,log),log);
            quickSort(tab, start, separator, log);
            quickSort(tab, separator+1, end, log);
        }
    }
    
    
    public static SortLog quickSortDual(Comparable[] tab){
        SortLog log = new SortLog(tab.length,null,false);
        dualPivotQuickSort(tab, 0, tab.length-1,3,log);
        return log;
    }
    public static SortLog quickSortInsert(Comparable[] tab){
        SortLog log = new SortLog(tab.length,null,false);
        quickWithInsertionSort(tab, 0, tab.length-1, log);
        return log;
    }
    public static SortLog quickSort(Comparable[] tab){
        SortLog log = new SortLog(tab.length,null,false);
        quickSort(tab, 0, tab.length-1, log);
        return log;
    }
    public static SortLog quickSortSelect(Comparable[] tab){
        SortLog log = new SortLog(tab.length,null,false);
        quickSortWithSelectAll(tab, 0, tab.length-1, log);
        return log;
    }
    public static SortLog quickSortMedian(Comparable[] tab){
        SortLog log = new SortLog(tab.length,null,false);
        quickSortWithSelectMedianOfMedians(tab, 0, tab.length-1, log);
        return log;
    }
    
    private static void quickSortWithSelectAll(Comparable[] tab,int start, int end,SortLog log){
        if(end-start>=20){
            int separator = Select(tab,start,end,(start+end)/2,5,log);
            quickSortWithSelectAll(tab, start, separator, log);
            quickSortWithSelectAll(tab, separator+1, end, log);
        }else{
            Sort(tab, start, end, log);
        }
    }
    private static void quickSortWithSelectMedianOfMedians(Comparable[] tab,int start, int end,SortLog log){
        if(end-start>=20){
            SortParts(tab,start,end,5,log);
            //int med = swapMedians(tab,start,end,5,log);
            int separator = Partition(tab,start,end,swapMedians(tab,start,end,5,log),log);
            //System.out.println("quickSortWithSelectMedianOfMedians("+start+","+end+") separator:"+separator+" med:"+med);
            quickSortWithSelectMedianOfMedians(tab, start, separator-1, log);
            quickSortWithSelectMedianOfMedians(tab, separator+1, end, log);
        }else{
            Sort(tab, start, end, log);
        }
    }
    
    
    private static int Partition(Comparable[] A, int left, int right,int piv_i,SortLog log){
        Comparable piv = A[piv_i];
        swap(A,piv_i,right,log);
        int si = left;
        for(int i=left ; i<right;i++){
            SortLog.incrementCompare(log);
            if(A[i].compareTo(piv)<0){
                swap(A,i,si,log);
                si++;
            }
        }
        swap(A,right,si,log);
        return si;
    }
    private static int Select(Comparable[] A,int p,int q,int i,int n_div,SortLog log){
        SortParts(A,p,q,n_div,log);
        if( (q-p+1)<= n_div ){
            return i;
        }else{
            int end_med = swapMedians(A,p,q,n_div,log);
            int r = Partition(A,p,q,Select(A,p,end_med,(int)Math.ceil((p+end_med)/2.0),n_div,log),log);
            if(r==i){
                return r;
            }else if (r>i){
                return Select(A, p, r-1, i, n_div, log);
            }else{
                return Select(A, r+1,q, i, n_div, log);
            }

        }
    }
    
    private static void SortParts(Comparable[] A,int start,int end,int n_div,SortLog log){
        for(int i=start ; i<=end; i+=n_div){
            if(i+n_div>end){
                Sort(A, i, end,log);
            }else{
                Sort(A, i, i+n_div,log);
            }     
                
        }
    }
    private static void Sort(Comparable[] A,int start, int end,SortLog log){
        for(int i=start+1;i<=end;i++){
            Comparable x = A[i];
            int j;
            for(j=i-1;j>=start;j--){
                SortLog.incrementCompare(log);                
                if(A[j].compareTo(x)>0){
                    SortLog.incrementSwap(log);   
                   A[j+1]=A[j]; 
                }else{
                    break;
                }  
            }
            SortLog.incrementSwap(log);   
            A[j+1]=x;
        }
    }
    private static int swapMedians(Comparable[] A,int start,int end,int n_div,SortLog log){
        int ceil = (int)Math.ceil(n_div/2.0);
        int it = start;
        for(int i=start+ceil-1 ; i<=end; i+=n_div){
            swap(A, i, it, log);
            it++;
        }
        return it-1;
    }
    private static void swap(Comparable[] A,int i,int j,SortLog log){
        if(i!=j){
            SortLog.incrementSwap(log);
            Comparable c = A[i];
            A[i] = A[j];
            A[j] = c;
        }
    }
}
