#lang racket

(define 1st (lambda (x) (car x)) )
(define 2nd (lambda (x) (car (cdr x))) )
(define 3rd (lambda (x) (car (cdr (cdr x)))) )

(define simplify
  (lambda (wyr)
    (cond
       [(pair? wyr)
          (cond
             [(and (number? (1st wyr)) (number? (3rd wyr)))
               (cond
                 [(equal? (2nd wyr) '+) (+ (1st wyr) (3rd wyr))]
                 [(equal? (2nd wyr) '-) (- (1st wyr) (3rd wyr)) ]
                 [(equal? (2nd wyr) '*) (* (1st wyr) (3rd wyr)) ]
                 [(equal? (2nd wyr) '/) (/ (1st wyr) (3rd wyr)) ]
                 [else wyr]
               )
             ]
             [(and (equal? (2nd wyr) '+) (equal? (1st wyr) '0)) (simplify (3rd wyr))]
             [(and (equal? (2nd wyr) '+) (equal? (3rd wyr) '0)) (simplify (1st wyr))]
             [(and (equal? (2nd wyr) '-) (equal? (1st wyr) (3rd wyr))) '0]
             [(and (equal? (2nd wyr) '-) (equal? (3rd wyr) '0)) (simplify (1st wyr))]
             [(and (equal? (2nd wyr) '*) (equal? (1st wyr) '1) ) (simplify (3rd wyr))]
             [(and (equal? (2nd wyr) '*) (equal? (3rd wyr) '1) ) (simplify (1st wyr))]
             [(and (equal? (2nd wyr) '*) (equal? (1st wyr) '0) ) '0]
             [(and (equal? (2nd wyr) '*) (equal? (3rd wyr) '0) ) '0]
             [(and (equal? (2nd wyr) '/) (equal? (3rd wyr) '1)) (simplify (1st wyr))]
             [(and (equal? (2nd wyr) '/) (equal? (1st wyr) '0)) '0]
             [(and (equal? (2nd wyr) '/) (equal? (1st wyr) (3rd wyr))) '1]
             [(or (pair? (1st wyr)) (pair? (3rd wyr)))
               (let ( (x1 (simplify (1st wyr)) ) (x2 (simplify (3rd wyr)) ))
                 (cond
                   [(or (pair? x1) (pair? x2))
                     (if (equal? (list x1 (2nd wyr) x2 ) wyr)
                       wyr
                       (simplify (list x1 (2nd wyr) x2))
                     )
                   ]
                   [(pair? x1) wyr
                    (if (equal? (list x1 (2nd wyr) 'qwe ) (simplify (list x1 (2nd wyr) 'qwe )) )
                        (list x1 (2nd wyr) x2 )
                        (simplify (list x1 (2nd wyr) x2) )
                     )
                    ]
                    [(pair? x2)
                     (if (equal? (list 'qwe (2nd wyr) x2 ) (simplify (list 'qwe (2nd wyr) x2 )) )
                        (list x1 (2nd wyr) x2 )
                        (simplify (list x1 (2nd wyr) x2) )
                    )
                   ]
                   [else (simplify (list x1 (2nd wyr) x2) ) ]
                   
                 )
               )
             ]
             [else wyr]
          )
       ]
       [else wyr]
    )
  )
)

(define pochodna
  (lambda (wyr zm)
    (cond
       [(null? wyr) '()]
       [(pair? wyr)
          (cond
             [(equal? (2nd wyr) '+)(list
                (pochodna (1st wyr) zm )
                  '+
                (pochodna (3rd wyr) zm )
             )]
             [(equal? (2nd wyr) '-)(list
                (pochodna (1st wyr) zm )
                  '-
                (pochodna (3rd wyr) zm )

             )]
             [(equal? (2nd wyr) '*) (list
                 (list (pochodna (1st wyr) zm ) '* (3rd wyr))
                   '+
                 (list (1st wyr) '* (pochodna (3rd wyr) zm ))
                                          
             )]
             [(equal? (2nd wyr) '/) (list
                (list
                 (list (pochodna (1st wyr) zm ) '* (3rd wyr))
                   '-
                 (list (1st wyr) '* (pochodna (3rd wyr) zm ))
                )
                  '/
                (list (3rd wyr) '* (3rd wyr))
             )]
             [else '() ]
          )
        ]
       [else (if (equal? wyr zm) '1 '0 )   ]
    )
  )
)

(define pochodnaSimple
  (lambda (wyr zm)
    (simplify (pochodna wyr zm))
   )
)
(define pochodnaPrint
  (lambda (wyr zm)
    (display "d/d")
    (display zm)
    (display "(")
    (display wyr)
    (display ") = ")
   ; (display (pochodna wyr zm))
   ; (display " = ")
   ; (newline)
    (display (pochodnaSimple wyr zm))
    (newline)
   )
)


(pochodnaPrint 'x 'x)
(pochodnaPrint 'y 'x)
(pochodnaPrint 'y 'y)
(pochodnaPrint '(x + y) 'x)
(pochodnaPrint '(x * y) 'x)
(pochodnaPrint '(x * y) 'y)
(pochodnaPrint '(1 / y) 'x)
(pochodnaPrint '(x / y) 'x)
(pochodnaPrint '(1 / y) 'y)
(pochodnaPrint '((x + a) * (x + b)) 'x)
(pochodnaPrint '((x * y) / z) 'x)