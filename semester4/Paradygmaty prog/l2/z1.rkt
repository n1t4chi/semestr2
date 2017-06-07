#lang racket
(define mymap
  (lambda (fun lista)
    (cond
      [(null? lista) lista ]
      [else ;(not(null? (cdr lista)))
         (cons
           (fun (car lista))
           (mymap fun (cdr lista))
         )  
      ]
     ; [ else (null? (cdr lista)) (list (fun (car lista)))   ]
    )
  )
)

(mymap (lambda (x) (- 9 x)) '(0 1 2 3 4 5 6 7 8 9)  )
(mymap (lambda (x) (+ 1 x)) '(0 2 4 6 8))
(mymap car '((1 2 3) (4 5 6) (7 8 9)))