		; ustawienie flag
                opt f-g-h+l+o+
		;ustawienie lokacji programu
                org $1000
start           equ *

		lda #$1a
		ldx #'0' ;ustawienie liczby setek
hundrets	cmp #100
		bcc skip100
		sbc #100
		inx
		jsr hundrets
		
skip100		stx word
		ldx #'0' ;ustawienie liczby dziesiatek

tens		cmp #10
		bcc skip10
		sbc #10
		inx
		jsr tens

skip10		stx word+1
		adc #'0'
		sta word+2

                lda <word
                ldx >word
                jsr $ff80
                brk


		;przygotowanie pamięci
                org $2000
word            dta b(0)
                dta b(0)
                dta b(0)
                dta b(10) ; '\n'
                dta b(0)

                org $2E0
                dta a(start)

                end of file
