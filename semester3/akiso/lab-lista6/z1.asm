		; ustawienie flag
                opt f-g-h+l+o+
		;ustawienie lokacji programu
                org $1000
start           equ *

		;załadowanie do akumulatora text
                lda <text
		;przeniesienie akumulatora do pamięci
                sta $80
		;załadowanie do akumulatora text
                lda >text
		;przeniesienie akumulatora do pamięci
                sta $81
		;przeniesienie pamięci do rejestru y
                ldy #0
		;załadowanie do akumulatora a5
                lda #$1a
		;wywołanie phex
                jsr PHEX

		;załadowanie do akumulatora text
                lda <text
		;załadowanie do rejestru X text
                ldx >text
                jsr $ff80
                brk


		;wepchanie akumulatora na stos
PHEX		PHA
		;wywołanie pxdig
		JSR PXDIG
		;wziecie ze wartosci ze stosu do akumulatora
		PLA 
		;wepchniecie ostatnich 4 bitów na poczatek
		asl @
		asl @
		asl @
		asl @
		;zostawienie tylko 4 poczatkowych znaków
PXDIG		AND #%11110000
		;przesuniecie do końca 4 poczatkowe bity
		LSR @
		LSR @
		LSR @
		LSR @
		;dodanie kodu 0 do akumulatora
		ORA #'0'
		;sprawdza czy akumulator ma kod większy od 9
		CMP #'9'+1
		;skok do pr jeżeli nie
		BCC PR
		;dodanie roznicy miedzy A i 9
		ADC #'A'-'9'-2
		;zapisanie akumulatora do pamieci
PR		STA ($80),Y
		;zwiekszenie Y      
		iny
		;powrót
		RTS


		;przygotowanie pamięci
                org $2000
text            equ *
                dta b(0)
                dta b(0)
                dta b(10) ; '\n'
                dta b(0)

                org $2E0
                dta a(start)

                end of file
