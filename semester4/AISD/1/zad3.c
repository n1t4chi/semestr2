#include<stdio.h>
#include<stdlib.h>
#include <sys/time.h>


typedef struct node node;
struct node {
    int val;
    node* prev;
    node* next;
};

/**
 * Returns index of given node starting from head and going through next nodes. Returns -1 if given node is not part of the given list.
 */
int indexOf(node* head,node* nodeToCheck){
    node* ptr = head;
    int it = 0;
    int found = -1;
    if(ptr !=NULL){
        do{
            if(ptr == nodeToCheck){
                found=it;
                break;
            }
            ptr = (*ptr).next;
            it++;
        }while(ptr!=head);
    }
    return found;
}

int sizeOfList(node* head){
    int size = 0;
    node* ptr = head;
    if(ptr!=NULL){
        do{
            size++;
            ptr = (*ptr).next;
        }while(ptr !=head);
    }
    return size;
}

/**
 * Returns first index of node with given value starting from given head of the list. Searches through next nodes. Returns -1 if no node with given value was found.
 */
int indexOfValue(node* head,int valueToCheck){
    node* ptr = head;
    int it = 0;
    int found = -1;
    if(ptr!=NULL){
        do{
            if((*ptr).val == valueToCheck){
                found=it;
                break;
            }
            ptr = (*ptr).next;
            it++;
        }while(ptr!=head);
    }
    return found;
}
/**
 * Returns node at given index. Cycles through list until it switches given index times. For positive index it goes through next nodes and negative goes through previous nodes
 */
node* getNodeWithValue(node* head,int valueToCheck){
    if(head == NULL){
        return NULL;
    }else{
        node* ptr = head;
        int found = 0 ;
        do{
            if((*ptr).val == valueToCheck){
                found = 1;
                break;
            }
            ptr = (*ptr).next;
        }while(ptr!=head);

        if(found==1){
            return ptr;
        }else
            return NULL;
    }
}
/**
 * Returns node at given index. Cycles through list until it switches given index times. For positive index it goes through next nodes and negative goes through previous nodes
 */
node* getNodeWithValue2(node* head,int valueToCheck){
    if(head == NULL){
        return NULL;
    }else{
        node* ptr1 = head;
        node* ptr2 = (*head).prev;
        int found = 0 ;
        do{
            if((*ptr1).val == valueToCheck){
                found = 1;
                break;
            }else if ((*ptr2).val == valueToCheck){
                found = 2;
                break;
            }
            ptr1 = (*ptr1).next;
            ptr2 = (*ptr2).prev;
        }while(ptr1!=ptr2);

        if(found==1){
            return ptr1;
        }else if(found ==2){
            return ptr2;
        }else
            return NULL;
    }
}

/**
 * Returns node at given index. Cycles through list until it switches given index times. For positive index it goes through next nodes and negative goes through previous nodes
 */
node* getNodeAt(node* head,int index){
    if(head == NULL){
        return NULL;
    }else{
        int i=0;
        node* ptr = head;
        if(index>0){
            while(i<index){
                ptr = (*ptr).next;
                i++;
            }
        }else if(index < 0){
            while(i>index){
                ptr = (*ptr).prev;
                i--;
            }
        }
        return ptr;
    }
}


/**
 * Merges first list pointed with head1 with second list pointed with head2.
 * Does not make new list, and will modify last element from first list so it will point at second list!
 */
node* merge(node* head1,node* head2){
    if(head1==NULL){
        return head2;
    }else{
        if(head2!=NULL){
            node* l1 = (*head1).prev;
            node* l2 = (*head2).prev;
            (*head1).prev=l2;
            (*l2).next=head1;
            (*head2).prev=l1;
            (*l1).next=head2;
        }
        return head1;
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
        node* ptr = (*nodeToDelete).next;
        (*ptr).prev = (*nodeToDelete).prev;
        (*(*nodeToDelete).prev).next = ptr;
        free(nodeToDelete);
        if(nodeToDelete==head)
            if(ptr == head)
                return NULL;
            else
                return ptr;
        else
            return head;
    }
}

/**
 * Deletes node at given index. Cycles through list until it switches given index times. For positive index it goes through next nodes and negative goes through previous nodes
 * Returns head node. New head node is returned if old one was deleted.
 */
node* deleteNodeAt(node* head,int index){
    return deleteNode(head,getNodeAt(head,index));
}

/**
 * Deletes node at given index. If index is outside the boundaries <0,sizeOfList(head)>
 * Returns head node if current head was deleted.
 */
node* deleteValue(node* head,int value){
    return deleteNode(head,getNodeWithValue(head,value));
}
/**
 * Inserts given value at the end of the list. Returns head of the list. Returns new node if given head was null, otherwise returns head.
 */
node* insertNext(node* ptr,int value){
    node* newPtr = malloc(sizeof(node));
    (*newPtr).val=value;
    if(ptr!=NULL){
        (*(*ptr).prev).next = newPtr;
        (*newPtr).prev = (*ptr).prev;
        (*newPtr).next = ptr;
        (*ptr).prev = newPtr;
        return ptr;
    }else{
        (*newPtr).next = newPtr;
        (*newPtr).prev = newPtr;
        return newPtr;
    }
}

/**
 * Inserts given value at the end of the list. Returns head of the list. Returns new node if given head was null, otherwise returns head.
 */
node* insertNode(node* head,int value){
    return insertNext(head,value);
}

/**
 * Inserts given value at given index. If index is positive then the list will cycle. 0 will add after head, -1 will add before head. Will not swap head pointer unless head is null
 */
node* insertNodeAt(node* head,int value,int index){
    return insertNext(getNodeAt(head,index),value);
}



double averageTimeAtFixedIndex(node* head,int loop,int index){
    clock_t t1;
    long clocks=0;
    int i;
    for(i=0; i<loop ; i++){
        t1=clock();
        //node* p666 = getNodeAt(head,index);
        node* p666 = getNodeWithValue2(head,index);

        clocks+=clock()-t1;
    }
    //return ((double)clocks);
    return ((double)clocks)/(CLOCKS_PER_SEC*loop);
}


int main(){
    node* head = NULL;
    int size = 1000;
    int i;
    clock_t t1;
    for(i=0;i< size;i++){
        head = insertNode(head,i);
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
        int index = (rand()-size)%size;
        t1=clock();
        node* p666 = getNodeAt(head,index);
        clocks+=clock()-t1;
    }
    printf("Average time for random node access: %.10f\n",((double)clocks)/(CLOCKS_PER_SEC*loop));


   /* printf("size:%d\n",sizeOfList(head));
    printf("index of 666: %dth\n",indexOfValue(head,666));
    node* p666 = getNodeAt(head,666);
    if(p666!=NULL){
        printf("value at 666th index: %d\n",(*p666).val);
    }else{
        printf("no node found at 666th index\n");
    }

    node* head2 = NULL;
    for(i=1000;i<3000;i++){
        head2 = insertNode(head2,i);
    }
    head = merge(head,head2);
    printf("size:%d [%p]\n",sizeOfList(head),head);
    head = deleteNodeAt(head,0);
    printf("size:%d [%p]\n",sizeOfList(head),head);
    head = deleteNodeAt(head,4);
    printf("size:%d [%p]\n",sizeOfList(head),head);
    printf("index of 1999: %dth\n",indexOfValue(head,1999));*/

    return 0;
}

