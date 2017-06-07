declare Perm in
fun {Perm L Curr First N} % cur - cz�� pojedy�czej permutacji, first - pierwszy pomini�ty element w li�cie dla danego wywo�ania
   case L of X|Xs then %rekurencyjne wywo�anie dla nie pustych list
      local T NL in
	 T =  {Append Curr [X]} % dodanie X do aktualnej cz�ci permutacji
	 NL = {Flatten [Xs X]} % przerzucenie X na koniec listy dla wywo�ania o nie zwi�kszonym aktualnym stanie permutacji
	 if First == nil then % �cie�ka w kt�rej nie wykonano przeczucenia elementu na koniec listy
	   {Append {Perm Xs T nil N-1}  {Perm NL Curr X N} } %��czenie wynik�w
	 elseif First == X then % doj�cie do zap�tlenia z wywo�aniem z przerzuceniem elementu na koniec.
	    nil %tutaj nie jest zwracane nic
	 else % po�rednie kroki dla przezuconych element�w na koniec. Dla pierwszego First si� usuwa gdy� odebrano jeden z element�w
	    %ale dla drugiego nale�y przes�a� stary aby nie dosz�o do niesko�czonych p�tli.
	   {Append {Perm Xs T nil N-1}  {Perm NL Curr First N} } %��czenie wynik�w
	 end
	 
      end
   else % zwr�cenie permutacji w postaci listy je�eli ma ona odpowiedni� liczb� 
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
