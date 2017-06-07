#lang racket
(define (flatten x) ;flatten list
  (cond
    [(null? x) null]
    [(pair? x) (append (car x) (flatten (cdr x))) ]
    [else (list x)]
   )
)


(define (splt start . args)
  (if (empty? args)
      (list start)
      (flatten
       (map (lambda (x) (apply splt (append start (list x)) (cdr args))) (car args) )
       )
  )
)

(define (splot . args)
  (apply splt null args)
)

(splot)
(splot '(1 2 3))
(splot '(1 2) '(3 4))
(splot '(1 2) '(3 4) '(5 6))
(splot '(1 2) '() '(5 6))