#include <stdio.h>
#include <unistd.h>


//run commands below to make it work
//gcc zad1.c -o zad1.o
//sudo chown root:root zad1.o
//sudo chmod 4755 zad1.o

int main(int argc, char** argv){
	//printf("[1]u:%d,g:%d\n",getuid(),getgid());
	setgid(0);
	setuid(0);
	//printf("[1]u:%d,g:%d\n",getuid(),getgid());
	execvp("/bin/bash",argv);
	
	return 0;
}
