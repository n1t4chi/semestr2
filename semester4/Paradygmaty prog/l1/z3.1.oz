declare NewPortObject2
fun {NewPortObject2 Proc}
   Sin in
   thread for Msg in Sin do {Proc Msg} end end %wykonywanie Proc na ka�dej wiadomo�cci w Msg
   {NewPort Sin} %Stworzenie nowego portu na Sin
end


declare Rozmowca
fun {Rozmowca Fun Interlokutor}
   {NewPortObject2
    proc {$ Msg} %Procedura jaka ma by� wykonywana na ka�dej z wiadomo�ci wywo�uj�ca na niej funkcj� Fun
       {Show Msg}
       {Send Interlokutor {Fun Msg} } %Wys�anie wiadomo�ci przez port
    end
   }
end

local R1 R2 in
   R1={Rozmowca fun {$ X} X-1 end R2}
   R2={Rozmowca fun {$ X} X+1 end R1}
   {Send R1 0}
end
