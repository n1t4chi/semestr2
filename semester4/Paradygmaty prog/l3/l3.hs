
import Data.List

--zad 1
scalarproduct :: Num a => [a] -> [a] -> a
scalarproduct xs ys = sum (map (prod) (zip xs ys))
      where prod z = fst z * snd z

--zad2
--most likely 0(n) and more closely to 3n where n is length of given list
split :: [a] -> ([a],[a])
split as = ([a | (a,m) <- pas , m `mod` 2 == 1 ],[a | (a,m) <- pas , m `mod` 2 == 0 ])
      where pas = zip as [1..] 

--zad3
permutacje :: Eq a => [a] -> [[a]]
permutacje xs  | xs == [] = [[]]
      | otherwise  = [ (y : ys) | y <- xs , ys <- (permutacje (delete y xs)) ]

     -- |  = [xs]
--(tail xs) /= []
