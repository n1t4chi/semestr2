		; set flags
                opt f-g-h+l+o+
		;set program location
                org $1000
start           equ *



		; Results are in Q, Q+1 and remainder in R
		; best run with: emu6502 -v -m -b 2005 -e 2007 z3.obx
		; first 2 memory values are result, 3rd is remainder

		lda N
		cmp #0	;check if first 8 bits are zero
		bne divsmall ;N cannot be smaller than D

		lda N+1 ;load last 8 bits
		sec 
		sub D
		bcc divbig ;jump if D is greater than N
		cmp #0	;check if D is same as N
		beq divsame ;they are
		cmp N+1	;check if D is 0
		beq divzero ;it is
	 	jmp divsmall ;it is not

divzero 	lda #$ff ; ff/ff code for 0 division
		sta Q
		sta R
		jmp exit

divsame 	lda #1  ;immediately return 1/0 when diving by same N
		sta Q+1
		jmp exit

divbig		lda N+1 ;immediately return 0/N; when dividing by higher N
		sta R
		lda #0
		sta Q+1
		jmp exit

divsmall	lda #0
		sta Q
		sta R
		sta mask+1
		lda #%10000000
		sta mask
		
loop		;loop
		asl R ; R << 1  przesuniecie reszty o 1 w lewo
		
		;ustawienie najmlodszego bitu reszty z x-tym bitem liczby
		lda mask
		cmp #0
		BEQ othermaskR

		lda N
		and mask 
		cmp #0
		BEQ skipRadd ;nie ma co dodawać
		inc R
		jmp skipRAdd

othermaskR	lda N+1
		and mask+1 
		cmp #0
		BEQ skipRadd ;nie ma co dodawać
		inc R	
skipRAdd		
		;porownanie R z D
		lda R
		cmp D
		bcc skipRlesD ; pomin jezeli R jest mniejsze niz D
		;sec  ;need??
		sbc D ; R-D
		sta R ; R = R-D
		
		lda mask
		cmp #0
		BEQ othermaskQ
		lda Q
		ora mask
		sta Q 
		jmp skipRlesD
othermaskQ
		lda Q+1
		ora mask+1
		sta Q+1
skipRlesD
		
		lsr mask ;przesuniecie maski	
		ror mask+1 ;przesuniecie maski	
		bcc loop ;skacz dopoki maska jest wieksza od zera

exit		jsr $ff80
        	brk
		
		





		;przygotowanie pamięci
                org $2000
mask		dta b(0) ;0
		dta b(0) ;1
D		dta b(10) ;2
N		dta b(0) ;3
                dta b(125) ;4
Q 	        dta b(0) ;5
 	        dta b(0) ;6
R		dta b(0) ;7
                dta b(10) ; '\n'

                org $2E0
                dta a(start)

                end of file
