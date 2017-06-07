declare Rev in
fun {Rev Xs List}
   case Xs of X|Xr then
      if List == nil then
	 {Rev Xr X}
      else
	 {Rev Xr X|List}
      end
   else
      if List == nil then
	 Xs
      elseif Xs == nil then
	 List
      else
	 {Append [Xs] List}
      end
   end
end

declare Reverse in
fun {Reverse Ls}
   {Rev Ls nil}
end

local L K in
   L={Reverse [1 2 3 4 5 6 7 8 9 10]}
   {Browse L}
   K={Reverse }
   {Browse K}
end