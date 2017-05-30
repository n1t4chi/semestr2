global main
extern printf 

section .text
main:
		mov	eax,3
   		mov	ebx,0
		mov	ecx,str
		mov	edx,40
		int	0x80

		mov	ebx,str
		mov	ecx,0	;0 - l1 dec, 1 - l2 dec, 2 l1 frac, 3- l2 frac  , 4 - operand
zoop:		;loop

		cmp	byte[ebx],10
		je	next
		
		cmp	ecx,4
		je	op	
		
		cmp	byte[ebx],' '
		jne	notspace
		cmp	ecx,1
		je	breakloop
		cmp	ecx,3
		je	breakloop

		mov	ecx,4
		jmp	next

notspace:	cmp	byte[ebx],'.'
		jne	notdot
		add	ecx,2
		jmp	next	

notdot:		cmp	byte[ebx],','
		jne	notcomma
		add	ecx,2	
		jmp	next		
				
		
notcomma:
		cmp	ecx,1
		jg	frac		
dec:		;decimal
		mov	eax,0
		mov	al,byte[ebx]

		cmp	al,'0'
		jl	invalid
		sub	al,'0'
		cmp	al,9
		jg	invalid
		


                mov	word[mult_add],ax
		cmp	ecx,1
		je	dec_num2	
		mov	dword[mult_ptr],l1d
		jmp	dec_cont
dec_num2:	mov	dword[mult_ptr],l2d
		mov	byte[valid_input],1
dec_cont:	
		call	multiplyTen
		jmp	next
		

frac:		;fraction
		
		mov	eax,0
		mov	al,byte[ebx]
		cmp	al,'0'
		jl	invalid
		sub	al,'0'
		cmp	al,9
		jg	invalid

                mov	word[mult_add],ax
		cmp	ecx,3
		je	frac_num2

	
		mov	dword[mult_ptr],l1l
		call	multiplyTen
		mov	word[mult_add],0
		mov	dword[mult_ptr],l1m
		call	multiplyTen
		jmp	next

frac_num2:	mov	dword[mult_ptr],l2l
		call	multiplyTen
		mov	word[mult_add],0
		mov	dword[mult_ptr],l2m
		call	multiplyTen

		jmp	next


op:		;operand

		
				
		cmp	byte[ebx],'/'
		je	valid_op
		cmp	byte[ebx],'+'
		je	valid_op
		cmp	byte[ebx],'*'
		je	valid_op
		cmp	byte[ebx],'-'
		je	valid_op
		cmp	byte[ebx],' ' 
		jne	invalid		
		mov	ecx,1	;on space switch to 2nd number
		jmp	next

valid_op:	mov	al,byte[ebx]
		mov	byte[dzi],al
		
next:		
		inc	ebx
		cmp	byte[ebx],0
		jne	zoop	;loop until char is 0


breakloop:
 
		cmp	byte[valid_input],0
		je	invalid



		fild	word[l1l]
		fild	word[l1m]
		fdivp	
		fild	word[l1d]
		faddp	;st0 <- first number
		fst	dword[N1]
		fild	word[l2l]
		fild	word[l2m]
		fdivp		
		fild	word[l2d]
		faddp     ;st0<- second number, st1<- first number
		fst	dword[N2]
	
				
		cmp	byte[dzi],"+"	
		jne	sub
add:		
		faddp
		jmp	print

sub:			
		cmp	byte[dzi],"-"	
		jne	mult
		fsubp
		jmp	print

mult:		
		cmp	byte[dzi],"*"	
		jne	div
		fmulp
		jmp	print
div:		
		cmp	byte[dzi],"*"
		cmp	word[l2l],0
		jne	gooddiv	
		cmp	word[l2d],0
		jne	gooddiv	
zerodiv:
		push	divzeroinfo
		call	printf
		jmp	exit
		
gooddiv:	
		fdivp

print:	             
                fst	qword[R]                 
		push	dword[R+4]                  
		push	dword[R]     
		push	doub
		call	printf            
		push	dword[R+4]



exit:
		mov	eax,1	;set sys_exit
		int	0x80  

invalid:	push	info
		call	printf
		mov	eax,1	;set sys_exit
		int	0x80  	


;multiplyTen use mult_ptr i mult_add
multiplyTen:		

                mov     esi,dword[mult_ptr]
                mov     ax,word[esi]
                mov     dx,10
                mul     dx
                add     ax,word[mult_add]
                mov     word [esi],ax
            
		ret


section .data
valid_input:    db  0
mult_ptr:    dd  0
mult_add:    dw  0
N1:		dd	0
N2:		dd	0
R:		dq	0

info:	db 'Invalid data, accepted format: "<num1>_<op>_<num2>" where <op> is +, -, * or / operand and _ is space',10,0
divzeroinfo:	db 'cannot divide by zero!',10,0
doub:	db '%f',10,0
str:	times	41	DB	0
l1d:	dw	0	;liczba 1, część całkowita
l1l:	dw	0	;liczba 1, część ułamkowa, licznik
l1m:	dw	1	;liczba 1, część ułamkowa, mianownik
l2d:	dw	0	;liczba 2, część całkowita
l2l:	dw	0	;liczba 2, część ułamkowa, licznik
l2m:	dw	1	;liczba 2, część ułamkowa, mianownik
dzi:	db	0	;działanie
