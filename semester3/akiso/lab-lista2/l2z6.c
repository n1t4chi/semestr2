#include <stdio.h>
int main (int argc,char *argv[]){
    for(int i=0; i<=255;i++)
        printf("\x1B[38;5;%im [%i]Hello, World!%s",i,i,(i%5==0)?"\n":"");
        //           48 dla zmiany t³a

    printf("\x1B[0m"); //reset

    return 0;
}