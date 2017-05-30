#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
//Matrices A B and W where W=A*B
int **A;
int **B;
int **W;
//matrices dimensions
int W_w;
int W_h;
int AB_hw;
//Next cell to calculate
int W_x;
int W_y;

//getNextCell lock
pthread_mutex_t lock;

int* getNextCell(){
	pthread_mutex_lock(&lock);
	int* rtrn = NULL;
	if(W_y<W_h){
		rtrn = (int *)calloc(2,sizeof(int));
		rtrn[0]=W_x;
		rtrn[1]=W_y;
		W_x++;
		if(W_x>=W_w){
			W_x=0;
			W_y++;	
		}
	}
	pthread_mutex_unlock(&lock);
	return rtrn;
}

void* calculateCell(){
	int* cell;
	while( (cell= getNextCell())!=NULL){
		int x=cell[0];
		int y=cell[1];
		//printf("calculating cell [%d][%d]\n",x,y);
		free(cell);
		int a,b;
		//printf("W[%d][%d]=",x,y);
		for(a=0;a<AB_hw;a++){
			//printf("A[][]*B[][]",x,a,b,y);
			W[x][y]+=A[x][a]*B[a][y];
		}

		//printf("W[%d][%d]=%d\n",x,y,W[x][y]);
	}
	return NULL;
}

void printMatrixSimple(int **M,int w,int h){
	int a,b;
	for(a=0;a< w ; a++ ){
		for(b=0;b< h; b++ ){
			printf("%d\t",M[a][b]);
		}
		printf("\n");

	}
}
void printMatrixWolfram(int **M,int w,int h){
	int a,b;
	printf("{");
	for(a=0;a< w ; a++ ){
		printf("{");
		for(b=0;b< h; b++ ){
			printf("%d",M[a][b]);
			if(b<h-1)
				printf(",");
		}
		printf("}");
		if(a<w-1)
			printf(",");

	}
	printf("}");
}



int multiply(int max_thrd_cnt){
	A=(int **)calloc(W_w,sizeof(int *));
	B=(int **)calloc(AB_hw,sizeof(int *));	
	W=(int **)calloc(W_w,sizeof(int *));	
	int a,b,
		max_w=((AB_hw>W_w)?AB_hw:W_w),
		max_h=((AB_hw>W_h)?AB_hw:W_h)
	;
	for(a=0;a<max_w  ; a++ ){
		if(a<W_w){
			A[a]=(int *)calloc(AB_hw,sizeof(int));
			W[a]=(int *)calloc(W_h,sizeof(int));
		}
		if(a<AB_hw){	
			B[a]=(int *)calloc(W_h,sizeof(int));
					
		}
		for(b=0;b< max_h ; b++ ){
			if((a<W_w)&&(b<AB_hw)){
				A[a][b]=random()%41-20;
			}
			if((a<AB_hw)&&(b<W_h)){
				B[a][b]=random()%41-20;
			}
		}			
	}
	
	printMatrixWolfram(A,W_w,AB_hw);
	printf("*");
	printMatrixWolfram(B,AB_hw,W_h);
	printf("\n");

	if(pthread_mutex_init(&lock, NULL)!=0){
		fprintf(stderr,"Lock initialisation failed\n");
	}else{
		printf("\nW\n");
		pthread_t tid[max_thrd_cnt];

		int c_t;
		//printf("starting threads\n");

		for(c_t=0;c_t<max_thrd_cnt;c_t++){
			
			int err = pthread_create(&tid[c_t],NULL,calculateCell,NULL);
			if(err){
				fprintf(stderr,"Couldn't create new thread\n");
				c_t--;
			}
		}
		
		//printf("waiting for threads\n");
		for(c_t=0;c_t<max_thrd_cnt;c_t++){
			//printf("waiting for thread [%d]\n",c_t);
	    		pthread_join(tid[c_t], NULL);
		}

		//printf("all threads finished working\n");
		printMatrixSimple(W,W_w,W_h);
	}
	free(A);
	free(B);
	free(W);
}




int main(int argc, char *argv[]){
	if(argc<5){
		printf("Multiplication of 2 random matrices A*B with given sizes \n Usage: [width A] [height A/width B] [height B] [threads]\n All values must be positive!");
	}else{
		int w_a=atoi(argv[1]),h_a_w_b=atoi(argv[2]),h_b=atoi(argv[3]),thrd_cnt=atoi(argv[4]);
		if((w_a<1)||(h_a_w_b<1)||(h_b<1)||(thrd_cnt<1)){
			printf("Atleast one of the values was not positive");
		}else{
			W_x=0;
			W_y=0;
			W_w=w_a;
			W_h=h_b;
			AB_hw=h_a_w_b;
			multiply(thrd_cnt);
			pthread_mutex_destroy(&lock);
		}
	}
}
