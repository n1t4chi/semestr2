#include<stdio.h>
#include<stdlib.h>

typedef struct data data;

struct data {
    void* val;
    data* next;
};

data* first=NULL;
data* last=NULL;

/**
 * Puts value to FIFO queue. Does not accept NULL values. Returns -1 on error.
 */
int put(int* val){
    if(val!=NULL){
        data* pt = malloc(sizeof(data));
        if(pt != NULL){
            (*pt).val = val;
            (*pt).next = NULL;
            if(first==NULL){
                first = pt;
                last = pt;
            }else{
                (*last).next = pt;
                last = pt;
            }
           // printf("put:%p[%d] first:%p[%d]  last%p[%d]\n",pt,(*(*pt).val),first,(*(*first).val),last,(*(*last).val));
        }else{
            return -1;
        }
    }else{
        return -1;
    }
}

/**
 * Removes first element in FIFO queue. Returns NULL when queue is empty.
 */
void* take(){
    if(first!=NULL){
        int* ret =(*first).val;
        if(first == last){
            free(first);
            first= NULL;
            last = NULL;
           // printf("took: [%d] no more elements in queue\n",(*ret));
        }else{
            data* copy=first;
            first = (*copy).next;
            free(copy);
            //printf("took: [%d] first:%p[%d]  last%p[%d]\n",(*ret),first,(*(*first).val),last,(*(*last).val));
        }
        return ret;
    }else{
        //printf("empty queue\n");
        return NULL;
    }
}


int main(){
    int* x = malloc(sizeof(int));
    (*x)=10;
    if(put(x)==-1)
        printf("ERROR while adding element\n");
    else
        printf("Put: [%d]\n",(*x));

    //####################
    x = malloc(sizeof(int));
    (*x)=20;
    if(put(x)==-1)
        printf("ERROR while adding element\n");
    else
        printf("Put: [%d]\n",(*x));

    //####################
    x = take();
    if(x==NULL)
        printf("ERROR while removing element\n");
    else
        printf("Received: [%d]\n",(*x));

    //####################
    x = malloc(sizeof(int));
    (*x)=30;
    if(put(x)==-1)
        printf("ERROR while adding element\n");
    else
        printf("Put: [%d]\n",(*x));

    //####################
    x = take();
    if(x==NULL)
        printf("ERROR while removing element\n");
    else
        printf("Received: [%d]\n",(*x));

    //####################
    x = take();
    if(x==NULL)
        printf("ERROR while removing element\n");
    else
        printf("Received: [%d]\n",(*x));

    //####################
    x = take();
    if(x==NULL)
        printf("ERROR while removing element\n");
    else
        printf("Received: [%d]\n",(*x));

    //####################
    x = malloc(sizeof(int));
    (*x)=30;
    if(put(x)==-1)
        printf("ERROR while adding element\n");
    else
        printf("Put: [%d]\n",(*x));

    //####################
    x = take();
    if(x==NULL)
        printf("ERROR while removing element\n");
    else
        printf("Received: [%d]\n",(*x));


}



