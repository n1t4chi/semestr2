global main
extern printf 

section .text
main:
		push	info
		call	printf
    		mov	eax,3
   		mov	ebx,0
		mov	ecx,str
		mov	edx,3
		int	0x80


		mov	ah,0
		mov	al,byte [str]
		cmp	al,'1'
		jl	skip1byte
		sub	al,'0'
		cmp	al,9
		jg	skip1byte
		mov	byte [N],al 	;moving to N

skip1byte:	mov	al,byte [str+1] 
		cmp	al,'0'
		jl	skip2byte
		sub	al,'0'
		cmp	al,9
		jg	skip2byte
		mov	dl,al	;dh < singles

		mov	al,10	
		mul	byte [N]	;multiply tens * 10
		add	al,dl		;add tens and singles
		mov	byte[N],al
skip2byte:		
		mov	ecx,0
		mov	cl,byte [N]
		
		mov	dword[R4],1

zoop:		

		mov	eax,dword[R4]
		mul	ecx
		mov	dword[R4],eax
		mov	ebx,edx
		

		mov	eax,dword[R3]
		mul	ecx
		add	eax,ebx
		jno	no_of1
		inc	edx
no_of1:		mov	dword[R3],eax
		mov	ebx,edx

		mov	eax,dword[R2]
		mul	ecx
		add	eax,ebx
		jno	no_of2
		inc	edx
no_of2:		mov	dword[R2],eax
		mov	ebx,edx

		mov	eax,dword[R1]
		mul	ecx
		add	eax,ebx
		jno	no_of3
		mov	byte[O],1	
no_of3:		mov	dword[R1],eax


		loop	zoop ;loop until ecx is zero



		cmp	byte[O],0
		je	sk
		push	ofl
		call	printf
sk:


		cmp	dword[R1],0
		je	sk1
		push	dword[R1]
		push	int
		call	printf
sk1:				
		cmp	dword[R2],0
		je	sk2
		push	dword[R2]
		push	int
		call	printf

sk2:

		cmp	dword[R3],0
		je	sk3
		push	dword[R3]
		push	int
		call	printf


sk3:
		cmp	dword[R4],0
		je	lastzero
		push	dword[R4]
		push	intnl
		call	printf
		
		jmp	skipzero
lastzero:	
		cmp	dword[R3],0
		je	skipzero
		
		push	zeros
		call	printf

skipzero:
		mov	eax,1	;set sys_exit
		int	0x80  



section .data
info:	DB 'type number within 0-34 range to calculate n!. Result is in base16',10,0
ofl:	DB 'there was overflow, result is cut to 128 bits',10,0
zeros:	DB '00000000',10,0
int:	DB '%X',0
intnl:	DB '%X',10,0
inf:	DB 'print',10,0
nl:	DB ' ',10,0
str:	times 3		DB	0 ;100! is way too much for 128 bits
	DB	0 	;number
N:	DB	0 	;number
R1:	DD	0 ;result
R2:	DD	0 ;result
R3:	DD	0 ;result
R4:	DD	0 ;result
O:	DD	0 ;overflow







