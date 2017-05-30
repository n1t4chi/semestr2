global main
global invsinh
global sinh
global exp
global ln
extern printf 

section .text
main:
	mov	dword[x],0x4048f5c3 ;3.14 -> ~1.14422
	;fild	dword[x]
	;fstp	dword[x]
	push	dword[x]
	call	ln
	mov	dword[r],eax
	call	printx

	mov	dword[x],0x3f8ccccd ;1.1 -> ~3.00417
	;fild	dword[x]
	;fstp	dword[x]
	push	dword[x]
	call	exp
	mov	dword[r],eax
	call	printx

	mov	dword[x],0x3f000000 ;0,5 -> ~0.521095
	;fild	dword[x]
	;fstp	dword[x]
	push	dword[x]
	call	sinh
	mov	dword[r],eax
	call	printx

	mov	dword[x],0x40490e56 ;3.1415 -> ~1.86227
	;fild	dword[x]
	;fstp	dword[x]
	push	dword[x]
	call	invsinh
	mov	dword[r],eax
	call	printx
	
	mov	eax,1	;set sys_exit
	int	0x80  

;end main


ln:
	push	ebp
	mov	ebp, esp
	mov	eax,[ebp +8]
	mov	dword[x],eax
	;func here

	;;ln x = log2 x / log2 e

	fld1
	fld	dword[x]
	fyl2x
	fldl2e
	fdivp
	fstp	dword[r]
	mov	eax,dword[r]

	;func end
	mov	esp, ebp
	pop	ebp
	retn	4	

;end ln

exp:
	push	ebp
	mov	ebp, esp
	mov	eax,[ebp +8]
	mov	dword[x],eax
	;func here
	;e^x = 2^(x*log2e)
	fldl2e
	fld	dword[x]
	fmul
	;w st0 mamy x*log2e

	fstcw	word[SaveCW]
	fstcw	word[MaskedCW]
	or	byte[MaskedCW+1],1100b
	fldcw	word[MaskedCW]

	fld	st0
	frndint
	fxch
	fsub	st0,st1
	f2xm1
	fld1
	faddp
	fxch
	fld1
	fscale
	fstp	st1
	fmulp
	fldcw	word[SaveCW]
	fstp	dword[r]
	mov	eax,dword[r]

	;func end
	mov	esp, ebp
	pop	ebp
	retn	4	

;end exp

sinh:
	push	ebp
	mov	ebp, esp
	mov	eax,[ebp +8]
	;func here

	;mov	dword[r],eax  	
	push	eax
	call	exp
	;eax = e^x
	mov	dword[r],eax  		;x=e^x


	fld1	;st1=e^x st0=1 
	fld	dword[r]
	fdivp		;st0=e^-x
	fld	dword[r];st0=e^x st1=e^-x
	fsubrp		;st0=e^x - e^-x
	fld1	
	fld1
	faddp		;st0=2	st1=e^x - e^-x
	fdivp		;st0=(e^x - e^-x)/2
	fstp	dword[r]
	mov	eax,dword[r]
	
	;func end
	mov	esp, ebp
	pop	ebp
	retn	4	

;end sinh

invsinh:
	push	ebp
	mov	ebp, esp
	mov	eax,[ebp +8]
	mov	dword[x],eax
	;func here

	;sing-1 = ln (x+sqrt(x^2+1))

	fld	dword[x]
	fld	st0
	fmul	;st0=x^2
	fld1
	fadd	;st0=x^2+1
	fsqrt	;st0=sqrt(x^2+1)
	fld	dword[x]
	fadd	;st0= x+sqrt(x^2+1)
	fstp	dword[x]
	push	dword[x]
	call	ln	

	;func end
	mov	esp, ebp
	pop	ebp
	retn	4	

;end invsinh


printx:
	push	ebp
	mov	ebp, esp

	fld	dword[r]
	fstp	qword[copy]
	push	dword[copy+4]
	push	dword[copy]
	push	doub

	call	printf

	mov	esp, ebp
	pop	ebp
	ret



section .data
x_copy:	dd	0


SaveCW:		dw	0
MaskedCW:	dw	0
x:	dd	0
r:	dd	0
copy:	dq	0
doub:	db '%f',10,0
