#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>

#include <wait.h>
#define SIGNAL_RECEIVED 255
#define NOTHING_RECEIVED 254
#define SIG_MIN 35
#define SIG_MAX 60
#define SIG_LOOP 100
#define TAB_MAX 65
#define TAB_MIN 1
void signalHandler(int dummy){
	//printf("[%d]Received signal \n",dummy);
	exit(SIGNAL_RECEIVED);
}
int i=1;
void handleAllSignalsTest(){
	int a,status,childs;
	int pids[TAB_MAX]; 
	//printf("creating childs\n");
	for(a=TAB_MIN;a<TAB_MAX;a++){
		if((a==32)||(a==33)){
			continue;
		}
		i=fork();
		if(i == 0){
			signal(a,signalHandler);
			//printf("[%d]Sending signal\n",a);
			raise(a);
			break;
		}else{
			pids[a-1]=i;	
		}
	}
	if(i != 0){
		//printf("checking signals\n");
		char output[200]="";
		for(a=TAB_MIN;a<TAB_MAX;a++){
			if((a==32)||(a==33)){
				continue;
			}
			int wp=waitpid(pids[a-1],&status,WUNTRACED | WCONTINUED);
			if(wp!=-1){
				if(!WIFEXITED(status)) {
					kill(pids[a-1],SIGKILL);
				}
				int rtrn_val=(WEXITSTATUS(status));
				//printf("[%d] returned [%d][%d][%d]\n",pids[a-1],rtrn_val,status,wp);
				if(rtrn_val!=SIGNAL_RECEIVED){
					sprintf(output, "%s  %d",output,a);
				}
			}else{
				printf("there was problem with child receiving %d signal",a);
			}
		}
		if(strlen(output)>0){
			printf("False, those signals were not handled:%s\n",output);
		}else{
			printf("True, all signals were handled properly\n");
		}
	}else{
		//printf("[%d]returning: %d\n",a,rtrn);
		exit(0);
	}

}

//run commands below to make this test work:
//gcc zad1.c -o zad2.o
//sudo chown root:root zad2.o
//sudo chmod 4755 zad2.o

void killInit(int root){
	int t=kill(1,SIGKILL);
	printf("%s, it is %spossible with%s root privileges.%s\n",(t == 0)?"Yes":"No",(t == 0)?"":"not ",(root==0)?"out":"",(t == 0)?"[Most likely signal was ignored nonetheless]":"");
}

void killInitTest(){
	int std_usr = 0;
	int cnt=0;
	if(geteuid()==0){	
		killInit(1);
		if(seteuid(getuid())==-1)printf("Could not drop root privileges\n");
	}else{
		printf("Could not perform test with root priviliges. Set root as effective user for this executable.\n");
	}
	killInit(0);
}

int sig_count=0;
int sig_chain=0;
int sig_chain_true=1;
void signalKiller(int dummy){
	int max = (SIG_MAX-SIG_MIN)*SIG_LOOP;
	printf("Received %d signals out of %d.\n",sig_count, max);
	//printf(", max signal id difference:%d\n",sig_chain_true );
	exit( ((sig_count==max)?10:0) + ((sig_chain_true==1)?1:0) );
}
void signalCounter(int dummy){
	//printf("Received %d signal\n",dummy);
	sig_count++; 
	//printf("sig_chain_true=%d+(%d<%d):%d\n",dummy-sig_chain,dummy,sig_chain,((dummy<sig_chain)?(SIG_MAX-SIG_MIN):0));
	/*if(sig_chain==0){
		sig_chain==dummy;
	}else{
		if(sig_chain_true==1){
			sig_chain_true=dummy-sig_chain+((dummy<sig_chain)?(SIG_MAX-SIG_MIN):0);
		}
	}
	sig_chain=dummy;*/
}

void chainSignTest(){
	int a,x,i=fork();
	if(i==0){
		for(a=SIG_MIN;a<SIG_MAX;a++){
			signal(a,signalCounter);
		}
		signal(SIG_MAX,signalKiller);
		while(1);
	}else{
		sleep(3); //allow main child to initialize signal handlers
		for(x=0;x<SIG_LOOP;x++){//20x
			int z=fork();
			if(z==0){
				for(a=SIG_MIN;a<SIG_MAX;a++){//20 signals
					//printf("Sending %d. signal, id %d\n",x*(SIG_MAX-SIG_MIN)+a-SIG_MIN,a);
					kill(i,a);
					//sleep(1);
				}
				exit(0);
			}
		}
		sleep(10); //allow rest of children to 'kill' the main child.
		kill(i,SIG_MAX);
		int status;
		int wp=waitpid(i,&status,0);	
		int rtrn_val=(WEXITSTATUS(status));
		int all = (rtrn_val>=10)?1:0;
		//int order = (rtrn_val%2==1)?1:0;
		printf("%s all signals were delivered.\n",(all==1)?"Yes,":"No, not");
		//printf("%s arrived in same order.\n",(order==1)?"Yes, they have":"No they have not");
	}	
}


int main(int argc, char** argv){
	if(argc<4){
		if(argc==1){
			printf("Is it possible for process to handle all possible signals?\n");
			handleAllSignalsTest();
			printf("\n\n");
			printf("Is it possible to send SIGKILL or other signals to init process?\n");
			killInitTest();
			printf("\n\n");
			printf("Were all signals delivered if multiple signals are sent?\n");
			chainSignTest();
		}else{
			printf("usage: <program> [y/n] [y/n] [y/n]  y/n determines whether n-th test will be performed\n");
		}
	}else{
		if(strcmp(argv[1],"y")==0){
			printf("Is it possible for process to handle all possible signals?\n");
			handleAllSignalsTest();
			printf("\n\n");
		}
		if(strcmp(argv[2],"y")==0){
			printf("Is it possible to send SIGKILL or other signals to init process?\n");
			killInitTest();
			printf("\n\n");
		}
		if(strcmp(argv[3],"y")==0){
			printf("Were all signals delivered if multiple signals are sent?\n");
			//printf("Are they delivered in same order they have been send?\n");
			chainSignTest();
		}
	}
	return 0;
}
