
declare Play in
fun {Play Id In}
   case In of X|Xs then
      case X
      of ping then
	 {Show Id#pong}
	 pong |{Play Id Xs}
      else
	 {Show Id#ping}
	 ping |{Play Id Xs}
      end
   end
end

declare Game in
proc {Game Id1 Id2}
   local X Y Z in
      X=pong|Z
      Y=thread {Play Id1 X} end
      Z=thread {Play Id2 Y} end
   end
end

{Game p1 p2}
