#include<stdio.h>
#include<stdlib.h>
#include <sys/time.h>



typedef struct node node;
struct node {
    int val;
    node* next;
};

/**
 * Merges first list pointed with head1 with second list pointed with head2.
 * Does not make new list, and will modify last element from first list so it will point at second list!
 */
node* merge(node* head1,node* head2){
    if(head1==NULL){
        return head2;
    }else{
        if(head2!=NULL){
            node* ptr = head1;
            while((*ptr).next!=NULL){
                ptr = (*ptr).next;
            }
            (*ptr).next =  head2;
        }
        return head1;
    }
}





/**
 * Deletes node at given index. If index is outside the boundaries <0,sizeOfList(head)>
 * Returns head node. New head node is returned if old one was deleted.
 */
node* deleteNodeAt(node* head,int index){
    if((index<0) || (head == NULL)){
            return head;
    }else{
        if(index == 0){ //remove first element
            node* ptr = (*head).next;
            free(head);
            return ptr;
        }else{ //remove any other element
            node* ptr = head;
            int i=1;
            while(((*ptr).next!=NULL)&&(i<index)){
                i++;
                ptr = (*ptr).next;
            }
            if(i==index){
                node* del = (*ptr).next;
                if( del!=NULL ){
                    (*ptr).next = (*del).next;
                    free(del);
                }
            }
            return head;
        }
    }
}
/**
 * Deletes node at given index. If index is outside the boundaries <0,sizeOfList(head)>
 * Returns head node if current head was deleted.
 */
node* deleteNode(node* head,node* nodeToDelete){
    if((nodeToDelete==NULL) || (head == NULL)){
        return head;
    }else{
        if(nodeToDelete==head){
            node* ptr = (*head).next;
            free(head);
            return ptr;
        }else{
            node* ptr = head;
            while((*ptr).next!=NULL && (*ptr).next!=nodeToDelete){
                ptr = (*ptr).next;
            }
            node* del = (*ptr).next;
            if(del!=NULL){
                (*ptr).next = (*del).next;
                free(del);
            }
            return head;
        }
    }
}
/**
 * Deletes node at given index. If index is outside the boundaries <0,sizeOfList(head)>
 * Returns head node if current head was deleted.
 */
node* deleteValue(node* head,int value){
    if(head==NULL){
        return head;
    }else{
        if((*head).val == value){
           // printf("Deleting head\n");
            node* ptr = (*head).next;
            free(head);
            return ptr;
        }else{
            node* ptr = head;
            while((*ptr).next!=NULL && (*(*ptr).next).val!=value){
                ptr = (*ptr).next;
            }
            node* del = (*ptr).next;
            if(del!=NULL){
                (*ptr).next = (*del).next;
                free(del);
            }
            return head;
        }
    }
}

/**
 * Inserts given value at the end of the list. Returns head of the list. Returns new node if given head was null, otherwise returns head.
 */
node* insertNode(node* head,int value){
    node* ptr = head;
    node* newPtr = malloc(sizeof(node));
    (*newPtr).next = NULL;
    (*newPtr).val=value;
    if(ptr!=NULL){
        while((*ptr).next!=NULL){
            ptr=(*ptr).next;
        }
        (*ptr).next = newPtr;
        return head;
    }else{
        return newPtr;
    }
}

/**
 * Inserts given value at given index so that indexOfValue will return that index with given head. 0 for index changes head of the list, -1 and greater values than the size of the list append at the end.
 */
node* insertNodeAt(node* head,int value,int index){
    if(index == -1)//automatically insert at the end.
        return insertNode(head,value);
    else{
        node* newPtr = malloc(sizeof(node));
        (*newPtr).next = NULL;
        (*newPtr).val=value;

        int it = 1;
        node* ptr = head;
        if(ptr!=NULL){
            if(index == 0 ){ //insert at the startnode
                (*newPtr).next = ptr;
                return newPtr;
            }else{//insert in the middle/at the end
                while((*ptr).next!=NULL&&it<index ){
                    ptr=(*ptr).next;
                    it++;
                }
                (*newPtr).next = (*ptr).next;
                (*ptr).next = newPtr;
                return head;
            }
        }else{
            return newPtr;
        }
    }
}

/**
 * Returns index of given node starting from given head of the list. Returns -1 if given node is not part of the given list was found.
 */
int indexOf(node* head,node* nodeToCheck){
    node* ptr = head;
    int it = 0;
    int found = -1;
    while(ptr !=NULL){
        if(ptr == nodeToCheck){
            found=it;
            break;
        }
        ptr = (*ptr).next;
        it++;
    }
    return found;
}

int sizeOfList(node* head){
    int size = 0;
    node* ptr = head;
    while(ptr !=NULL){
        size++;
        ptr = (*ptr).next;
    }
    return size;
}

/**
 * Returns first index of node with given value starting from given head of the list. Returns -1 if no node with given value was found.
 */
int indexOfValue(node* head,int valueToCheck){
    node* ptr = head;
    int it = 0;
    int found = -1;
    while(ptr !=NULL){
        if((*ptr).val == valueToCheck){
            found=it;
            break;
        }
        ptr = (*ptr).next;
        it++;
    }
    return found;
}

/**
 * Returns node at given index. Null if index is outside <0,sizeOfList(head)> boundaries or head is null.
 */
node* getNodeAt(node* head,int index){
    if((index<0) || (head == NULL)){
        return NULL;
    }else{
        int i=0;
        node* ptr = head;
        while((i<index)&& (ptr!=NULL)){
            ptr = (*ptr).next;
            i++;
        }
        return ptr;
    }
}

double averageTimeAtFixedIndex(node* head,int loop,int index){
    clock_t t1;
    long clocks=0;
    int i;
    for(i=0; i<loop ; i++){
        t1=clock();
        node* p666 = getNodeAt(head,index);
        clocks+=clock()-t1;
    }
    return ((double)clocks)/(CLOCKS_PER_SEC*loop);
}



int main(){
    node* head = NULL;
    int size = 1000;
    int i;
    clock_t t1;
    for(i=0;i< size;i++){
        head = insertNodeAt(head,i,0);
        //printf("%p,%d,%d\n",head,(*head).val,i);
    }
    int loop = 1000000;
    printf("List size:%d  number of loops: %d\n",size,loop);
    printf("Average time for 100th node access: %.10f\n",averageTimeAtFixedIndex(head,loop,100));
    printf("Average time for %dth node access: %.10f\n",size/4,averageTimeAtFixedIndex(head,loop,size/4));
    printf("Average time for middle node access: %.10f\n",averageTimeAtFixedIndex(head,loop,size/2));
    printf("Average time for %dth node access: %.10f\n",size*3/4,averageTimeAtFixedIndex(head,loop,size*3/4));
    printf("Average time for last node access: %.10f\n",averageTimeAtFixedIndex(head,loop,size-1));

    srand(time(NULL));
    long clocks=0;
    for(i=0; i<loop ; i++){
        int index = rand()%size;
        t1=clock();
        node* p666 = getNodeAt(head,index);
        clocks+=clock()-t1;
    }
    printf("Average time for random node access: %.10f\n",((double)clocks)/(CLOCKS_PER_SEC*loop));

    return 0;
}
