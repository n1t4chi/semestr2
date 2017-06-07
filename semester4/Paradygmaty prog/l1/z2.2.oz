declare SortTask in
fun {SortTask In1 Val Has_next Out2 In2 }
   case In1 of X|Xs then %check if value on first input is a list. if it is then
      if Val == nil then %first time receiving input so calling itself w
	 %ith value set for X
	 {SortTask Xs X Has_next Out2 nil}
      else %already have value accumulated
	 local Out_next in
	    if Val =< X then %
	       Out2 = X|Out_next
	       if Has_next == yes then
		  {SortTask Xs Val yes Out_next In2}
	       else
		  local In_next Out2_next in
		     thread In_next = {SortTask Out2 nil no Out2_next nil} end
		     {SortTask Xs Val yes Out_next In_next}
		  end
	       end
	    else
	       Out2 = Val|Out_next
	       if Has_next == yes then
		  {SortTask Xs X yes Out_next In2}
	       else
		  local In_next Out2_next in
		     thread In_next = {SortTask Out2 nil no Out2_next nil} end
		     {SortTask Xs X yes Out_next In_next}
		  end
	       end
	    end
	 end
      end
   else%first input has ended, ending output for next accumulators and returning values
      Out2 = nil
      if Val == nil then
	 nil
      else
	 Val | In2
	 %{Append [Val] In2}
      end
   end
end

declare NSort
proc {NSort IN OUT}
   local Out_next in
      OUT = {SortTask IN nil no Out_next nil}
   end
end
    
local X in
   {NSort [3 8 7 2 18 23 3 12 1 0 ] X }
   {Browse X}
   {Browse {NSort [3 8 7 2 18 23 3 12 1 0 ]}}
end
