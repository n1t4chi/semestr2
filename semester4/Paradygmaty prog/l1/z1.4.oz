declare FindAllNotFlatten in
fun {FindAllNotFlatten V T }
   if T \= leaf then 
      local L1 L2 L3 in
	 L1 = {FindAllNotFlatten V T.3}
	 if T.2 == V then
	    L2 = T.1
	 else
	    L2 = nil
	 end
	 L3 = {FindAllNotFlatten V T.4}
	 if L1 == nil then
	    if L2 == nil then
	       L3
	    else
	       if L3 == nil then
		  L2
	       else
		  %L2|L3
		  [L2 L3]
	       end 
	    end 
	 else
	    if L2 == nil then
	       if L3 == nil then
		  L1
	       else
		  %L1|L3
		  [L1 L3]
	       end
	    else
	       if L3 == nil then
		  %L1|L2
		  [L1 L2]
	       else
		  %L1|L2|L3
		  [L1 L2 L3]
	       end
	    end 
	 end
      end
   else
      nil
   end
end
declare FindAll in
fun {FindAll V T }
  % {FindAllNotFlatten V T} % (10|20)|50|60 O(n)
   {Flatten {FindAllNotFlatten V T}} %[10 20 50 60] O(n) + flatten(n) most likely its O(n)
end

local L Root V1 V2 V3 V4 V5 V6 V7 in
   Root = tree(45 nietest V2 V7)
   V1 = tree(10 test leaf leaf)
   V2 = tree(20 test V1 V3)
   V3 = tree(30 nietest leaf V4)
   V4 = tree(40 nietest leaf leaf)
   V5 = tree(50 test leaf leaf)
   V6 = tree(60 test V5 leaf)
   V7 = tree(70 nietest V6 leaf)
   L={FindAll test Root}
   {Browse L}
end
