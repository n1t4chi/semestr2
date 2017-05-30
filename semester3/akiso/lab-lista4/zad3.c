#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <wait.h>

int main(int argc, char** argv){

	char* path_env = getenv("PATH");
	char input[500] = "";
	char pwd[200] = "";

	while((strcmp(input,"exit")!=0)&&!((strlen(input)>0)&&(input[0]==EOF))){
		int status=0;	
		input[0]=EOF;
		printf("lsh:%s:%s~",getenv("USER"),getcwd(pwd,200));
		fgets(input,sizeof(input),stdin);

		if(input[0]!=EOF){
			input[(strlen(input)-1)]='\0';
		}

		if( (input!=NULL) && (strlen(input)>1) && (strcmp(input," ")!=0) && (input[0]!=EOF) && (strcmp(input,"exit")!=0) ){
			int waitCMD = 0;
			int exe=255;		
			if((strlen(input)>1)&&(input[strlen(input)-1]=='&')){
				input[(strlen(input)-1)]='\0';
				waitCMD=1;
			}
			
			char path[strlen(path_env)];
			strcpy(path,path_env);
			char *s;
			char* CMD=NULL;
			char* ARG[128]={NULL};
			int arg=0;
			s=strtok(input," ");
			while((s!=NULL)&&(arg<127)){
				ARG[arg]="";
				if(arg==0){
					CMD = s;
				}else{
					ARG[arg]=s;
				}
				arg++;
				s=strtok(NULL," ");
			}

			ARG[arg]=NULL;			
			char* prefix = strtok(path,":");
			s="";

			while((exe==255)&&(prefix!=NULL)){
				char fullpath[300]="";	
				strcat(fullpath,s);
				strcat(fullpath,prefix);
				strcat(fullpath,"/");
				strcat(fullpath,CMD);
				int cont=0;

				if ( access( fullpath,F_OK) != -1 ){
					if ( access( fullpath,X_OK) != -1 ){
						int pid=fork();
						if(pid==0){	
							exe=execvp(fullpath,ARG);
							exit(exe);
						}else{
							int opt = 0;
							if(waitCMD==1){
								opt = WNOWAIT;
								printf("PID [%d] will run in background\n",pid);
							}
							int x=waitpid(pid,&status,opt);
							exe=(WEXITSTATUS(status));	
						}
					}else{
						printf("Unable to execute %s\n",fullpath);
					}
				}	
				prefix=strtok(NULL,":");
			}
			if(exe==255){
				printf("No such command found.\n");
			}
		}
		int rtrn_pid = waitpid(-1,&status,WNOHANG);
		if(rtrn_pid>0){
			printf("PID [%d] have finished working\n",rtrn_pid);
		}	
	}
	return 0;
}
