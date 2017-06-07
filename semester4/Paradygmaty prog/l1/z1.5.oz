declare Perm in
fun {Perm L Curr First N} % cur - czê¶æ pojedyñczej permutacji, first - pierwszy pominiêty element w li¶cie dla danego wywo³ania
   case L of X|Xs then %rekurencyjne wywo³anie dla nie pustych list
      local T NL in
	 T =  {Append Curr [X]} % dodanie X do aktualnej czê¶ci permutacji
	 NL = {Flatten [Xs X]} % przerzucenie X na koniec listy dla wywo³ania o nie zwiêkszonym aktualnym stanie permutacji
	 if First == nil then % ¶cie¿ka w której nie wykonano przeczucenia elementu na koniec listy
	   {Append {Perm Xs T nil N-1}  {Perm NL Curr X N} } %³±czenie wyników
	 elseif First == X then % doj¶cie do zapêtlenia z wywo³aniem z przerzuceniem elementu na koniec.
	    nil %tutaj nie jest zwracane nic
	 else % po¶rednie kroki dla przezuconych elementów na koniec. Dla pierwszego First siê usuwa gdy¿ odebrano jeden z elementów
	    %ale dla drugiego nale¿y przes³aæ stary aby nie dosz³o do nieskoñczonych pêtli.
	   {Append {Perm Xs T nil N-1}  {Perm NL Curr First N} } %³±czenie wyników
	 end
	 
      end
   else % zwrócenie permutacji w postaci listy je¿eli ma ona odpowiedni± liczbê 
      if N==0 then
	 [Curr]
      else 
	 nil
      end
      %{Append Curr Ls}
   end
   
end


declare Permutacje in
fun {Permutacje L}
   {Perm L nil nil {Length L}}
end

local L in
   L = {Permutacje [1 2 3 4] }
   {Browse L}
end
