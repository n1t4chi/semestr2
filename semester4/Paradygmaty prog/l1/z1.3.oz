declare Reverse in
fun {Reverse Ls}
   {List.foldL Ls fun {$ X Y} Y|X end nil}
  /* case Ls of L|Lr then
      {List.foldL Lr fun {$ X Y} Y|X end L}
   else
      Ls
      nil
   end*/
end

declare Append in
fun {Append Ls Xs}
      {List.foldR Ls fun {$ X Y} X|Y end Xs}
 /*  case Ls of L|Lr then
      L|{List.foldR Lr fun {$ X Y} X|Y end Xs}
   else
      Xs
   end*/
end

local Rev App in
   App={Append [ala ma] [dwa koty]}
   {Browse App}
   Rev={Reverse App}
   {Browse Rev}
end