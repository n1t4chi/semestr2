declare Merge2 in
fun {Merge2 L K}
   case L of X|Xr then
      case K of Y|Yr then
	 if X==Y then
	    X|Y|{Merge2 Xr Yr}
	 elseif X<Y then
	    X|{Merge2 Xr K}
	 else
	    Y|{Merge2 L Yr}
	 end
      else
	 X
      end
   else
      K
   end
end

declare Merge in
fun {Merge L1 L2 L3}
   {Merge2 L1 {Merge2 L2 L3}}
end

local L in
   L={Merge [20 30 40] [4 5 6] [1 10 100] }
   {Browse L}
end