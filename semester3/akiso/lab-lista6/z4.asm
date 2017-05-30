global main
extern printf ;for easy printing
;extern sleep

section .text
main:
		;its easier to just print 2 since it's obvious prime numer 
		;than check every time if there is no 2/2 division
		push	2
		push	msg
		call	printf

		mov	ebx,3	; using bx since 16 bits are enough for 10000

			;loop for bx=3 -> 10000
		mov	edi,1
zoop:		mov	ecx,2		
p_loop:			;loop for cx=2 -> bx
		mov	edx,0  	;ustawienie dx na 0
		mov	ax,bx
		div 	cx	;dzielenie bx/cx
		cmp	dx,0	;check if remainder is zero
		je	not_prime ;if yes then ax is not a prime number and we need to jump out
		inc	cx
		cmp	bx,cx
		jg	p_loop ;loop for bx=2 -> edx-1
		
		;if loop ends normaly then ebx has prime number
		movzx	eax, bx
                push	eax
		push	msg
		call	printf	;print prime number

		inc	edi
		cmp	edi,10
		jne	skipline
		mov	edi,0
		push	msgnl
		call	printf ;print new line after 10 primes
skipline:	
		
not_prime:	;if jumped here then ax was not a prime number
		inc	bx
		cmp 	bx,10000
		jle	zoop ;loop for ax=3 -> 10000

		push	msgnl
		call	printf

  		;push	1
		;call	sleep
		mov	eax,1	;set sys_exit
		int	0x80  

section .data

msg     db  '%hu',9,0
msgnl    db  10,0


