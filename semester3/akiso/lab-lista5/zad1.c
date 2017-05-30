
//#include<string.h>


void myprintf(const char *text,...){

	int it=0,len=0,clen=0;
	char* c;
	int val,a,s,base;
	for(a=0;text[a]!=0;a++){
		len++;
	}

   	char *p = (char *) &text + sizeof text;
	while(it<len){
		if((text[it]=='%')&&(it<len-1)){
			it++;
			base=16;
			switch(text[it]){
				case 's':
				   	c = (char *)*((int *)p);
					p+= sizeof (int *);
					for(a=0;c[a]!=0;a++){
						clen++;
					}
					write(0,c,clen-1);
					it++;
				break;
				case 'b':
					base-=8;
				case 'd':
					base-=6;
				case 'x':
				   	val = *((int *)p);
					p+= sizeof (int *);
					if(val<0){
						val*=-1;
						write(0,"-",1);
					}else if(val == 0){
						write(0,"0",1);
					}
					a=val;
					s=1;
					for(a=val;a>0;a/=base){
						s*=base;
						//printf("a:[%d] s[%d]\n",a,s);

					}
					//printf("s:[%d]\n",s);
					for(;s>1;s/=base){
						int a = (val%s)/(s/base);
						//printf("\n(%d%%%d)/(%d/%d)=%d\n",val,s,s,base,((val%s)/(s/base)));

						char c = (a<10)?'0'+a:'A'+(a%10);
						char str[1] = {c};
						//printf("div:[%d] [%c] [%s]\n",(l%s)/(s/10),('0'+(l%s)/(s/10)),str);
						write(0,str,1);
					}
					it++;

				case '%':
					write(0,&text[it],1);
					it++;
				break;
			}
		}else{
			write(0,&text[it],1);
			it++;
		}
	}


}

int isBase(char c,int base){
	//char s[]={c,0};
	//myprintf("\n%s in %d",s,base);
	if(base == 2)
		return ((c==48)||(c==49))?1:0;
	else if(base == 10)
		return ((c>=48)&&(c<=57))?1:0;
	else if(base == 16)
		return ( ((c>=48)&&(c<=57)) || ((c>=65)&&(c<=70)) || ((c>=97)&&(c<=102)) ) ?1:0;
}
int valueBase(char c,int base){
	if((base == 16)&&(c>=65)){
		if(c>=97)
			return 10+c-97;
		else
			return 10+c-65;

	}else
		return c-48;
}


void myscanf(const char *text,...){
	int it=0,len=0,clen=0;
	char *c;
	int *val,a,base;
	for(a=0;text[a]!=0;a++){
		len++;
	}
   	char *p = (char *) &text + sizeof text;
	while(it<len){
		if((text[it]=='%')&&(it<len-1)){
			it++;
			base=16;
			switch(text[it]){
				case 's':
				   	c = (char *)*((int *)p);
					p+= sizeof (int *);
					read(1,c,255);
					it++;
				break;
				case 'b':
					base-=8;
				case 'd':
					base-=6;
				case 'x':
				   	val = (int *)*((int *)p);
					p+= sizeof (int *);
					char d='0';
					(*val) = 0;
					while(d!=10){
						read(1,&d,1);
						if(isBase(d,base)==1){
							(*val) = (*val)*base + valueBase(d,base);
						}

					}
					//myprintf("dec:%d\n",(*val));
					//myprintf("hex:%x\n",(*val));
					//myprintf("bin:%b\n",(*val));
					it++;
				break;
			}
		}else{
			it++;
		}
	}


}



int main(int argc, char *argv[]){
	int a=-1337,b=-255,c=-15;
	char d[255]="test";
	//myscanf("%s",d);
	myprintf("Write some decimal, hexadecimal and binary value and then text.\n");
	myscanf("%d %x %b %s",&a,&b,&c,d);
	myprintf("Written numbers:\ndec\thex\tbin\n%d\t%x\t%b\n%d\t%x\t%b\n%d\t%x\t%b\nWritten text: %s \n",a,a,a,b,b,b,c,c,c,d);


}


