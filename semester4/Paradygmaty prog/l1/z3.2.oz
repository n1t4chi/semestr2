


declare NewPortObject
fun {NewPortObject Init Fun}
   proc  {MsgLoop S1 State}
      case S1 of Msg|S2 then
	 {MsgLoop S2 {Fun Msg State}}
      [] nil then skip
      end
   end
   Sin
in
   thread
      {MsgLoop Sin Init}
   end
   {NewPort Sin}
end
/*

fun {NewPortObject Init Fun}
   Sin Sout in
   thread {FoldL Sin Fun Init Sout} end %
   {NewPort Sin}
end*/

declare NewPortObject2
fun {NewPortObject2 Proc}
   Sin in
   thread for Msg in Sin do {Proc Msg} end end %wykonywanie Proc na ka¿dej wiadomo¶ci w Msg
   {NewPort Sin} %Stworzenie nowego portu na Sin
end

declare ScheduleLast
fun {ScheduleLast L N}
   if L\=nil andthen {List.last L}==N then
      L
   else
      {Append L [N]}
   end
end


declare Timer in
fun {Timer}
   {NewPortObject2
    proc {$ Msg}
       case Msg of starttimer(T Pid) then
	  thread {Delay T} {Send Pid stoptimer} end
      else
	  {Browse Msg}
       end
    end}
end


declare Floor in
fun {Floor Num Init Lifts}
   Tid = {Timer}
   Fid = {NewPortObject Init
	  fun {$ Msg state(Called) }
	     case Called
	     of notcalled then Lran in
		case Msg
		of arrive(Ack) then %Lift arrives
		   {Browse 'Lift at floor '#Num#': open doors'}
		   {Send Tid starttimer(1000 Fid)}
		   state(doorsopen(Ack))
		[] call then %Floor makes a call so the random lift is called to given floor
		   {Browse 'Floor'#Num#': calls a lift.'}
		   Lran=Lifts.(1+{OS.rand} mod {Width Lifts})
		   {Send Lran call(Num)}
		   state(called)
		else
		   {Browse floor#received#nil#4}
		   state(Called)
		end
	     []called then
		case Msg
		of arrive(Ack) then %lift was called to given floor and opens doors
		   {Browse 'Lift at floor '#Num#': open doors'}
		   {Send Tid starttimer(1000 Fid)}
		   state(doorsopen(Ack))
		[] call then
		   state(called)
		else
		   {Browse floor#received#nil#3}
		   state(Called)
		end
	     [] doorsopen(Ack) then
		case Msg
		of stoptimer then %doors are closing and changes state to called
		   {Browse 'Lift at floor '#Num#': close doors'}
		   Ack = unit
		   state(notcalled)
		[] arrive(A) then
		   A=Ack
		   state(doorsopen(Ack))
		[] call then
		   state(doorsopen(Ack))
		else
		   {Browse floor#received#nil#2}
		   state(Called)
		end
	     else
		{Browse floor#received#nil#1}
		state(Called)
	     end
	  end}
in Fid end

declare Lift in
fun {Lift Num Init Cid Floors}
   {NewPortObject Init
    fun {$ Msg state(Pos Sched Moving)}
       case Msg
       of call(N) then
	  {Browse 'Lift '#Num#' needed at floor '#N}
	  if N==Pos andthen {Not Moving} then
	     {Wait {Send Floors.Pos arrive($)}}
	     state(Pos Sched false)
	  else Sched2 in
	     Sched2 = {ScheduleLast Sched N}
	     if {Not Moving} then
		{Send Cid step(N)}
	     end
	     state(Pos Sched2 true)
	  end
       [] 'at'(NewPos) then
	  {Browse 'Lift '#Num#' at floor '#NewPos}
	  case Sched
	  of S|Sched2 then
	     if NewPos==S then
		{Wait {Send Floors.S arrive($)}}
		if Sched2==nil then
		   state(NewPos nil false)
		else
		   {Send Cid step(Sched2.1)}
		   state(NewPos Sched2 true)
		end
	     else
		{Send Cid step(S)}
		state(NewPos Sched Moving)
	     end
	  else
	     {Browse lift#received#nil#2}
	     state(Pos Sched Moving)
	  end
       else
	  {Browse lift#received#nil#1}
	  state(Pos Sched Moving)
       end
    end}
end


declare Controller in
fun {Controller Init}
   Tid = {Timer}
   Cid={NewPortObject Init
	fun {$ Msg state(Motor F Lid) }
	   case Motor
	   of running then
	      case Msg
	      of stoptimer then
		% {Browse Msg#Lid}
		 {Send Lid 'at'(F) } %Sending message at floor
		 state(stopped F Lid)
	      else
		 {Browse controller#received#nil#2}
		 state(running F Lid) 
	      end
	   [] stopped then
	      case Msg
	      of step(Dest) then
		 if F==Dest then %Arriving at the destination
		    state(stopped F Lid) 
		 elseif F<Dest then %Lift is below the destination so waiting 1s to change floor
		    {Send Tid starttimer(1000 Cid) }
		    state(running F+1 Lid)
		 else %Lift is above the destination so waiting 1s to change floor
		    {Send Tid starttimer(1000 Cid)}
		    state(running F-1 Lid)
		 end
	      end
	   else
	      {Browse controllert#received#nil#1}
	      state(stopped F Lid) 
	   end
	end}
in Cid end


declare LiftShaft
fun {LiftShaft I state(F S M) Floors}
   Cid = {Controller state(stopped F Lid)}
   Lid = {Lift I state(F S M) Cid Floors}
in Lid end

declare Building
proc {Building Fn Ln ?Floors ?Lifts}
   Lifts = {MakeTuple lifts Ln}
   for I in 1..Ln do Cid in
      Lifts.I={LiftShaft I state(1 nil false) Floors} %LiftShaft upgrade
      % Cid = {Controller state(stopped 1 Lifts.I)}
      % Lifts.I={Lift I state(1 nil false) Cid Floors}
   end
   Floors = {MakeTuple floors Fn}
   for I in 1..Fn do
      Floors.I={Floor I state(notcalled) Lifts}
   end
end


local F L in
   {Building 20 2 F L}
   {Send F.20 call}
   {Send F.4 call}
   {Send F.10 call}
   {Send L.1 call(4)}

end
