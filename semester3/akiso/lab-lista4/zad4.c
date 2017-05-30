#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <wait.h>
#include <fcntl.h>

#define MAX_ARG 128
#define MAX_PIPE 20

char* clearString(char* str){
	if(str!=NULL){
		while((strlen(str)>0)&&(str[0]==' ')){
			strncpy(str,&str[1],strlen(str)-1);
			str[strlen(str)-1]='\0';
		}
		while((strlen(str)>0)&&(str[strlen(str)-1]==' ')){
			str[strlen(str)-1]='\0';
		}
	}
	return str;
}
/**
 *Cuts src string at pos, returns len first cut characters
 */

char* cutString(char* dst,char* src,int pos,int len){
	if((src!=NULL)&&(pos+len<=strlen(src))){
		//printf("cutString([%s],[%s],[%d],[%d])\n",dst,src,pos,len);
		strncpy(dst,&src[pos],len);
		dst[len]='\0';	
		int i;
		for(i=pos ; (i+len)<=strlen(src) ; i++){
			//printf("src[%d]=%c <- src[%d]=%c // strlen:%d\n",i,src[i],i+len,src[i+len],strlen(src));
			src[i]=src[i+len];
		}
		src[pos+len+1]='\0';
		//printf("cutString -> dst[%s]: src:[%s]\n",dst,src);
	}
	return dst;
}
char* cutStringEndPos(char* dst,char* src,int pos){
	if((src!=NULL)&&(pos<strlen(src))){
		return 	cutString(dst,src,pos,strlen(src)-pos);
	}else
		return dst;
}
char* cutStringEndLen(char* dst,char* src,int len){
	if((src!=NULL)&&(len<=strlen(src))){
		return 	cutString(dst,src,strlen(src)-len,len);
	}else
		return dst;
}
char* cutStringBegin(char* dst,char* src,int len){
	if((src!=NULL)&&(len<=strlen(src))){
		return 	cutString(dst,src,0,len);
	}else
		return dst;
}
char* cutSubstring(char* dst,char* src,int pos_s,int pos_e){
	if((src!=NULL)&&(pos_s<=strlen(src))&&(pos_e<=strlen(src))&&(pos_s<=pos_e)){
		return 	cutString(dst,src,pos_s,pos_e-pos_s);
	}else
		return dst;
}


char* getPath(char* fullpath, char* cmd,char* path_env,int i){
	if((cmd!=NULL)&&(i>=0)&&(path_env!=NULL)&&(fullpath!=NULL)){
		char path_cpy[strlen(path_env)];
		strcpy(path_cpy,path_env);
		char path[strlen(path_env)];
		char* prefix;
		int start=0,end=0,it;
		
		for(it=i;(it>=0)&&(start<strlen(path_cpy));it--){
			start=end+1;
			for(end=start;end<strlen(path_cpy);end++){
				//printf("i[%d] path_cpy[%d]=%c \n",i,end,path_cpy[end]);
				if(path_cpy[end]==':'){
					//printf("i[%d] path_cpy[%d]=%c \n",it,end,path_cpy[end]);
					break;
				}
			}
			//printf("[%d]s[%d] e[%d] out of [%d]\n",it,start,end,strlen(path_cpy));
		}
		//printf("[%d]pathc[%s], s[%d] e[%d]\n",i,path_cpy,start,end);
		if(start<end){
			cutSubstring(path,path_cpy,start,end);
			//printf("path[%s]  pathc[%s]\n",path,path_cpy);
			//printf("fullpath[%s]\n",fullpath);
			//printf("path[%s]\n",path);
			//printf("cmd[%s]\n",cmd);
			strcat(fullpath,path);
			//printf("fullpath1[%s]\n",fullpath);
			strcat(fullpath,"/");
			//printf("fullpath2[%s]\n",fullpath);
			strcat(fullpath,cmd);
			//printf("fullpath3[%s]\n",fullpath);
		}
	}
	return fullpath;
}

void setInputPipe(int* in_pipe,int in){
	if(in_pipe!=NULL){
		if(dup2(in_pipe[0], in)<0){
			fprintf(stderr,"ERR:couldn't dup in pipe\n");
		}
		if((close(in_pipe[0])<0)||
		   (close(in_pipe[1])<0)){
			fprintf(stderr,"ERR:couldn't close in pipe\n");
		}
		
	}
}
void setOutputPipe(int* out_pipe,int out){
	if(out_pipe!=NULL){
		if(dup2(out_pipe[1], out)<0){
			fprintf(stderr,"ERR:couldn't dup out pipe\n");
		}
		if((close(out_pipe[0])<0)||
		   (close(out_pipe[1])<0)){
			fprintf(stderr,"ERR:couldn't close out pipe\n");
		}
		
	}
}

void execute(char* cmd, char** arg, int* pipe_in, int* pipe_out,char* in,char* out, char* err){
	int in_fd=-1;
	int out_fd=-1;
	int err_fd=-1;
	int status;
	
	int pid=fork();
	if(pid==0){			

		if(strcmp(in,"")!=0){
			//fprintf(stderr,"setting up input file [%s]\n",in);
			in_fd= open(in, O_RDONLY, 0400);
		}
		if(in_fd<0){
			if(pipe_in!=NULL){
				//fprintf(stderr,"[%s]setting up stdin from pipe\n",cmd);
				setInputPipe(pipe_in,0);
			}
		}else{	
			//fprintf(stderr,"[%s]setting up stdin from file\n",cmd);
			dup2(in_fd, 0);
		}


		if(strcmp(out,"")!=0){
			//fprintf(stderr,"setting up output file [%s]\n",out);
			out_fd= open(out, O_RDWR|O_CREAT|O_APPEND, 0600);
		}
		if(out_fd<0){
			if(pipe_out!=NULL){
				//fprintf(stderr,"[%s]setting up stdout to pipe\n",cmd);
				setOutputPipe(pipe_out,1);
			}
		}else{
			//fprintf(stderr,"[%s]setting up stdout to file\n",cmd);
			dup2(out_fd, 1);
		}


		if(strcmp(err,"")!=0){
			//fprintf(stderr,"setting up err file [%s]\n",out);
			err_fd= open(err, O_RDWR|O_CREAT|O_APPEND, 0600);	
			if(err_fd>=0){dup2(err_fd, 2);}
		}

		int exe=execvp(cmd,arg);
		if(in_fd>0)
			close(in_fd);
		if(out_fd>0)
			close(out_fd);
		if(err_fd>0)
			close(err_fd);

		exit(WEXITSTATUS(exe));
	}else{	
		//fprintf(stderr,"running [%s] with [%d]pid\n",cmd,pid);
		/*int x=waitpid(pid,&status,0);
		if(pipe_out!=NULL){
			if( (close(pipe_out[0]<0)) || (close(pipe_out[1]<0)) ){	
				fprintf(stderr,"ERR:coudln't close pipe out of fork after exe\n");
			}
		}
		fprintf(stderr,"[%s] exited\n",cmd);*/
	}

}

int main(int argc, char** argv){
	/*char str_a[11] = "0123456789";
	char str_b[5];
	for(int i=4;i>0;i--){
		cutStringBegin(str_b,str_a,i);
		printf("#%d[%s][%s]\n",i,str_b,str_a);
	}*/
	char input[500] = "";
	char pwd[200] = "";
	char* path_env = getenv("PATH");

	while((1==1)&&(strcmp(input,"exit")!=0)&&!((strlen(input)>0)&&(input[0]==EOF))){
		int status=0;	
		input[0]=EOF;
		printf("lsh:%s:%s~",getenv("USER"),getcwd(pwd,200));
		fgets(input,sizeof(input),stdin);
		if(input[0]!=EOF){
			input[(strlen(input)-1)]='\0';
		}
		clearString(input);

		if( (input!=NULL) && (strlen(input)>1) && (strcmp(input," ")!=0) && (input[0]!=EOF) && (strcmp(input,"exit")!=0) ){
			int waitCMD = 0;
			int exe=255;		
			if((strlen(input)>1)&&(input[strlen(input)-1]=='&')){
				input[(strlen(input)-1)]='\0';
				waitCMD=1;
			}
			
			char path[strlen(path_env)];
			strcpy(path,path_env);
			char *s_pipe;
			char *s;
			char CMD[MAX_PIPE][200]={""};
			char CMD_IN[MAX_PIPE][50]={""};
			char CMD_OUT[MAX_PIPE][50]={""};
			char CMD_ERROUT[MAX_PIPE][50]={""};
			char* CMD_ARG[MAX_PIPE][MAX_ARG]={NULL};
			int arg_it=0;
			int pipe_it=0;
			char *pipe_ptr=NULL;
			s_pipe=strtok_r(input,"|",&pipe_ptr);
			while((s_pipe!=NULL)&&(pipe_it<MAX_PIPE)){
				//printf("[s_pipe][%s] len[%d]\n",s_pipe,strlen(s_pipe));
				char *arg_ptr=NULL;
				int a;				
				for(a=strlen(s_pipe)-2;a>=1;a--){
					clearString(s_pipe);
					//printf("[%c]\n",s_pipe[a]);
					char *dst=NULL;
					if(s_pipe[a]=='<'){
						dst = CMD_IN[pipe_it];		
					}else{					
						if(s_pipe[a]=='>'){
							if((a>1)&&(s_pipe[a-1]=='2')){
								dst = CMD_ERROUT[pipe_it];
								s_pipe[a-1]=' ';
							}else{
								dst = CMD_OUT[pipe_it];			
							}	
						}					
					}
					if(dst!=NULL){
						cutStringEndPos(dst,s_pipe,a+1);
						s_pipe[a]=' ';
						//printf("\nclear\n");
						clearString(dst);
						//printf("[%d][%s]\n",pipe,dst);
						//printf("#2cut dst[%s] src[%s] a[%d]\n",dst,s_pipe,a);
					}
					
				}
				s=strtok_r(s_pipe," ",&arg_ptr);
				arg_it=0;
				while((s!=NULL)&&(arg_it<MAX_ARG-1)){
					CMD_ARG[pipe_it][arg_it]="";
					char fullpath[300]="";
					if(arg_it==0){
						int found=0;
						int a=0;
						do{
							memset(fullpath,0,300);
							getPath(fullpath,s,path_env,a);
							a++;	
							if(strcmp(fullpath,"")!=0){
								if ( access( fullpath,X_OK) != -1 ){
									strcpy(CMD[pipe_it],fullpath);
									found=1;
								}	
							}else{
								found=1;
							}
							//printf("path:[%s] found[%d] cmd:[%s]\n",fullpath,found,CMD[pipe_it]);
						}while((found==0));

					}else{
						CMD_ARG[pipe_it][arg_it]=s;
					}
					arg_it++;
					s=strtok_r(NULL," ",&arg_ptr);
				}
				CMD_ARG[pipe_it][arg_it]=NULL;	

				
				/*fprintf(stderr,"ERR:cmd:%s\n",CMD[pipe_it]);
				arg_it = 0;
				while(CMD_ARG[pipe_it][arg_it]!=NULL){
					fprintf(stderr,"ERR:arg[%d][%s]\n",arg_it,CMD_ARG[pipe_it][arg_it]);
					arg_it++;
				}
				fprintf(stderr,"ERR:in:[%s]\n",CMD_IN[pipe_it]);
				fprintf(stderr,"ERR:out:[%s]\n",CMD_OUT[pipe_it]);
				fprintf(stderr,"ERR:err:[%s]\n",CMD_ERROUT[pipe_it]);*/
				
				pipe_it++;
				s_pipe=strtok_r(NULL,"|",&pipe_ptr);
			}

			char* prefix = strtok(path,":");
			s="";
			int current_max_pipe=pipe_it;
			pipe_it=0;	
			int mpid = fork();
			if(mpid==0){
				pipe_it=0;


				if(current_max_pipe<2){
					execute(CMD[pipe_it],CMD_ARG[pipe_it],NULL,NULL,CMD_IN[pipe_it],CMD_OUT[pipe_it],CMD_ERROUT[pipe_it]);
					
				}else{
					int p_l=current_max_pipe;
					int p[p_l-1][2];

					if(pipe(p[0])<0){	
						fprintf(stderr,"ERR: problem with pipe 0.\n");
					}	
					execute(CMD[0],CMD_ARG[0],NULL,p[0],CMD_IN[0],CMD_OUT[0],CMD_ERROUT[0]);


					//############################################################
					//int i;
					int i=1;
					for(i=1; i< p_l-1;i++){
						if(pipe(p[i])<0){	
							fprintf(stderr,"ERR: problem with pipe %d.\n",i);
						}
						execute(CMD[i],CMD_ARG[i],p[i-1],p[i],CMD_IN[i],CMD_OUT[i],CMD_ERROUT[i]);
						if(	(close(p[i-1][0])) || 
							(close(p[i-1][1]))
				 		){	
							fprintf(stderr,"ERR:coudln't close pipe %d\n",i-1);
						}
					}

					//############################################################
					execute(CMD[p_l-1],CMD_ARG[p_l-1],p[p_l-2],NULL,CMD_IN[p_l-1],CMD_OUT[p_l-1],CMD_ERROUT[p_l-1]);
					if(	(close(p[p_l-2][0])) || 
						(close(p[p_l-2][1]))
					){	
						fprintf(stderr,"ERR:coudln't close pipe %d\n",p_l-2);
					}



				/*	//fprintf(stderr,"INFO:pipe(p_tab[0])\n");
					if(pipe(p_tab[0])<0){	
						fprintf(stderr,"ERR:couldn't create first pipe\n");
					}
					//fprintf(stderr,"INFO:exe(cmd[0]) with p_in[NULL] p_out[0]\n");
					execute(CMD[0],CMD_ARG[0],NULL,p_tab[0],CMD_IN[0],CMD_OUT[0],CMD_ERROUT[0]);
					
					for(pipe_it=1;pipe_it<current_max_pipe-1;pipe_it++){
						//fprintf(stderr,"INFO:pipe(p_tab[%d])\n",pipe_it);
						if(pipe(p_tab[pipe_it])<0){	
							fprintf(stderr,"ERR:couldn't create output pipe for [%s] exe\n",CMD[pipe_it]);
						}
						
						//fprintf(stderr,"INFO:exe(cmd[%d] with p_in[%d] p_out[%d])\n",pipe_it,pipe_it-1,pipe_it);
						execute(CMD[pipe_it],CMD_ARG[pipe_it],p_tab[pipe_it-1],p_tab[pipe_it],CMD_IN[pipe_it],CMD_OUT[pipe_it],CMD_ERROUT[pipe_it]);	
						
						//fprintf(stderr,"INFO:close p-tab[%d])\n",pipe_it-1);
						if( (close(p_tab[pipe_it-1][0]<0)) || (close(p_tab[pipe_it-1][1]<0)) ){	
							fprintf(stderr,"ERR:coudln't close pipe before [%s] exe\n",CMD[pipe_it]);
						}
					}
					//fprintf(stderr,"INFO:exe(cmd[%d]) with p_in[%d] p_out[NULL]\n",current_max_pipe-1,current_max_pipe-2);
					execute(CMD[current_max_pipe-1],CMD_ARG[current_max_pipe-1],p_tab[current_max_pipe-2],NULL,CMD_IN[current_max_pipe-1],CMD_OUT[current_max_pipe-1],CMD_ERROUT[current_max_pipe-1]);
					
					//fprintf(stderr,"INFO:close p-tab[%d])\n",current_max_pipe-2);
					if( (close(p_tab[current_max_pipe-2][0]<0)) || (close(p_tab[current_max_pipe-2][1]<0)) ){	
						fprintf(stderr,"ERR:coudln't close last pipe\n");
					}*/


				}


				int x=0;
				while(x>=0){
					x=waitpid(-1,&status,0);
					//if(x>0)
					//	fprintf(stderr,"[%d] ppid exited\n",x);
				}
				exit(0);		
			}else{
				int opt = 0;
				int status;
				if(waitCMD==1){
					opt = WNOWAIT;
					printf("PID [%d] will run in background\n",mpid);
				}
				int x=waitpid(mpid,&status,opt);
				exe=(WEXITSTATUS(status));
			}
		
		}
		int rtrn_pid = waitpid(-1,&status,WNOHANG);
		if(rtrn_pid>0){
			printf("PID [%d] have finished working\n",rtrn_pid);
		}	
	}
	return 0;
}
