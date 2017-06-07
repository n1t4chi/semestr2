declare Sieve in
fun {Sieve Xs} %Eratostenes sieve
   case Xs of nil then nil
   [] X|Xr then Ys in
      thread Ys = {Filter Xr fun {$ Y} Y mod X \= 0 end} end
      X|{Sieve Ys}
   end
end

declare Generate in
fun {Generate N K} %Generates list of numbers from K to N inclusive and end with nil
   if K =< N then
      K|{Generate N K+1}
   else
      nil
   end
end


declare NPrime in
fun {NPrime N K} %Calculates K first primes, N is iterator and should be initalised with 2 or N*2
   local X in
      X = {Sieve {Generate N 2}}
      if {Length X} == K then
	 X
      else
	 {NPrime N+2 K}
      end
   end
end

declare KPrime in
fun {KPrime K} %Calculates K first primes.
   {NPrime 2*K K}
end


declare Times in
fun lazy {Times N H} %Multiply the incoming stream of numbers
   case H of X|H2 then N*X|{Times N H2} end
end

declare Merge in 
fun lazy {Merge Xs Ys} %Merge two streams of numbers
   case Xs#Ys of (X|Xr)#(Y|Yr) then
      if X<Y then X|{Merge Xr Ys}
      elseif X>Y then Y|{Merge Xs Yr}
      else X|{Merge Xr Yr}
      end
   end
end

declare Touch in
proc {Touch N H} %Touch N elements from list of H
   if N>0 then {Touch N-1 H.2} else skip end
end

declare MultStreamWithPrime
fun  {MultStreamWithPrime Primes H}
   case Primes of P|Ps then
      if Ps \= nil then
	 local X in
	    thread X = {MultStreamWithPrime Ps H} end
	    {Merge {Times P H} X}
	 end
      else
	 {Times P H}
      end
   else
      nil
   end
end


declare Hamming in
fun {Hamming N K} %Shows N first numbers which only factors are first K primes
   local Primes H in %First get list of K first primes
      Primes = {KPrime K}
      %{Browse Primes}
      
      %Run MultStreamWithPrime which launches {Times P H} for each prime in Primes
      H = 1|{MultStreamWithPrime Primes H} 
      {Touch N H}
      H
   end
end


local H in
   H = {Hamming 50 10}
   {Browse H}
end
