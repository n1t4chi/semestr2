/*arm-none-eabi-as z8.asm -o z8.obx*/
/*arm-none-eabi-gcc -specs=rdimon.specs z8.obx -o z8*/
/*qemu-arm ./z8*/
.text
.global main
main:

	ldr	r0, =in
	sub	sp, sp, #4
	mov	r1,sp
	bl	scanf
	ldr	r4, [sp, #0]
	add	sp, sp, #4

	ldr	r0, =in
	sub	sp, sp, #4
	mov	r1,sp
	bl	scanf
	ldr	r3, [sp, #0]
	add	sp, sp, #4

loop:
	cmp	r4,r3
	sublt	r3,r3,r4
	subgt	r4,r4,r3
	bne	loop 		/*simple gcd using subtraction*/

	
	ldr	r0, =out
        mov	r1, r3
	bl	printf

	mov	%r0, $0		/*ret 0*/
	mov	%r7, $1		/*set syscall to exit*/
	swi	$0		/*exit*/
.data
in:	.asciz "%d"
out:	.asciz "%d\n\0"
	
