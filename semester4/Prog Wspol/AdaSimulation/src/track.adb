with Model; use model;
with train; use train;
with steering;
with Ada.Text_IO;
with Ada.Float_Text_IO;
with Ada.Strings.Unbounded;
with log;
with Ada.Containers.Vectors;
with Ada.Exceptions;  use Ada.Exceptions;
with Ada.Real_Time; use Ada.Real_Time;
with Ada.Numerics.Float_Random; use Ada.Numerics.Float_Random;
--@Author: Piotr Olejarz 220398
--Track package declares record and task for tracks
package body track is
   package ustr renames Ada.Strings.Unbounded;
   --Track task. Allows accepted trains to move/wait on them
   task body TrackTask is


    --  package Rand is new Ada.Numerics.Discrete_Random(Random.Random_Float);
   --  use Rand;

      type_str : Ada.Strings.Unbounded.Unbounded_String;
      train_ptr : access train.TRAIN;
      delay_dur : Float;
      real_delay_dur : Float;
      sim_delay_str : String(1..8);
      real_delay_str : String(1..8);
      G : Generator;
      ran : Float;
      hist : access Track_History;

      help_service_train_ptr : access train.TRAIN := null;
      pass_service_train_ptr : access train.TRAIN := null;
      help : boolean := false;

      work : Boolean := false;

   begin
      hist := null;
      if track_ptr /= null and model_ptr /= null then
         Reset(G,2*track_ptr.id+16384 + Integer(Float'Value(Duration'Image(Ada.Real_Time.To_Duration(Ada.Real_Time.Clock-model_ptr.start_time)))));



         --track naming
         if track_ptr.t_type = Track_Type_Platform then
           type_str := ustr.To_Unbounded_String("Platform");
         elsif track_ptr.t_type = Track_Type_Track then
            type_str := ustr.To_Unbounded_String("Track");
         elsif track_ptr.t_type = Track_Type_Service then
            type_str := ustr.To_Unbounded_String("Service Track");
         else
            type_str := ustr.To_Unbounded_String("Unknown");
         end if;

         loop
            if track_ptr.out_of_order = true and help = false then
               help_service_train_ptr := model.getServiceTrain(model_ptr);
               if help_service_train_ptr /= null then

                  while help_service_train_ptr.t_task = null loop
                     delay Standard.Duration(1);
                  end loop;


                  select
                     help_service_train_ptr.t_task.trackOutOfOrder(track_ptr.id);
                     help := true;
                  or
                     delay Standard.Duration(1);
                  end select;

               else
                  Ada.Text_IO.Put_Line("#2# " & ustr.To_String(type_str)&"["&Positive'Image(track_ptr.id)&"] received null pointer for service train" );
               end if;
            end if;

            --select works on train_ptr state.
            --If train is accepted then if below select is performed and on next cycle thread waits for clearAfterTrain.
            select
               -- when s_ptr.used_by = 0 =>
               -- if pointer is null then it waits until the train is accepted.
            --   when not model_ptr.work =>
            --      terminate;
            --or
               accept breakSelect do
                  null;
               end breakSelect;
            or
               when track_ptr.used_by = 0 =>
                  accept allowServiceTrain( train_id : in Positive) do
                     pass_service_train_ptr := model.getTrain(train_id,model_ptr);
                     if pass_service_train_ptr /= null then
                        track_ptr.used_by := train_id;
                        log.putLine("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] received accept request from service train["&Positive'Image(pass_service_train_ptr.id)&"]. Blocking track for other trains.",model_ptr);
                     else
                        Ada.Text_IO.Put_Line("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] received null pointer for service train["&Positive'Image(train_id)&"]");
                     end if;
                  end allowServiceTrain;
            or
               accept acceptServiceTrain( train_id : in Positive) do
                  if pass_service_train_ptr /= null and then pass_service_train_ptr.id = train_id then
                     train_ptr := pass_service_train_ptr;
                     work:= true;

                     hist := new Track_History; --_Record
                     hist.arrival := Ada.Real_Time.Clock;
                     hist.train_id := train_id;

                     log.putLine("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] blocked by passing service train["&Positive'Image(pass_service_train_ptr.id)&"]",model_ptr);
                  else
                     if track_ptr.used_by = 0 or track_ptr.used_by = train_id then
                        log.putLine("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] received accept signal from invalid serivce train["&Positive'Image(train_id)&"] no service train expected. Blocking the track anyway.",model_ptr);
                        pass_service_train_ptr := model.getTrain(train_id,model_ptr);
                        track_ptr.used_by := train_id;
                        train_ptr := pass_service_train_ptr;
                        work:= true;


                        hist := new Track_History; --_Record
                        hist.arrival := Ada.Real_Time.Clock;
                        hist.train_id := train_id;

                     else
                        log.putLine("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] received accept signal from invalid serivce train["&Positive'Image(train_id)&"] no service train expected. Currently used by other train.",model_ptr);
                     end if;
                  end if;
               end acceptServiceTrain;
            or
               accept freeFromServiceTrain ( train_id : in Positive ) do
                  if pass_service_train_ptr /= null and then pass_service_train_ptr.id = train_id then
                     log.putLine("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] unblocked from service train["&Positive'Image(pass_service_train_ptr.id)&"].",model_ptr);
                     track_ptr.used_by := 0;
                     train_ptr := null;
                     pass_service_train_ptr := null;
                  else
                     if pass_service_train_ptr = null then
                        Ada.Text_IO.Put_Line("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] receive free signal from invalid serivce train["&Positive'Image(train_id)&"] no service train accepted.");
                     else
                        Ada.Text_IO.Put_Line("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] receive free signal from invalid serivce train["&Positive'Image(train_id)&"] accepted: ["&Positive'Image(pass_service_train_ptr.id)&"]");
                     end if;
                  end if;
               end freeFromServiceTrain;
            or
                 --when track_ptr.out_of_order = true =>
               accept repair ( train_id : in Positive)  do
                  if help_service_train_ptr /= null and then help_service_train_ptr.id = train_id then
                     log.putLine("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] was just repaired. Ready to accept incoming trains anew.",model_ptr);
                     track_ptr.out_of_order := false;
                  else
                     if help_service_train_ptr /= null then
                        log.putLine("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] has no information about service train but received repair signal from service train["&Positive'Image(train_id)&"]. Accepting the repair and moving along with schedule.",model_ptr);
                        track_ptr.out_of_order := false;
                     else
                        log.putLine("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] received repair signal from illegal service train["&Positive'Image(train_id)&"]. Accepting the repair and moving along with schedule.",model_ptr);
                        track_ptr.out_of_order := false;
                     end if;
                  end if;
               end repair;
            or
               when track_ptr.out_of_order = false and then train_ptr = null =>
                  --accepts given train thus blocking track for others
                  accept acceptTrain (train_id : in Positive) do
                     train_ptr := Model.getTrain(train_id,model_ptr);
                     if train_ptr /= null then
                        work:= true;
                        hist := new Track_History; --_Record
                        hist.arrival := Ada.Real_Time.Clock;
                        hist.train_id := train_id;
                        log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] is now blocked by train["&Positive'Image(train_ptr.id)&"]",model_ptr);
                        track_ptr.used_by := train_ptr.id;
                     else
                        Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] received null pointer for train["&Positive'Image(train_id)&"]" );
                     end if;
                  end acceptTrain;
            or
               --otherwise waits for train to clear out the steering.
               when track_ptr.out_of_order = false and then train_ptr /= null =>
                  --clears out block for other trains after currently blocking train left the track.
                  accept clearAfterTrain(train_id : in Positive) do
                     if train_ptr.id = train_id then
                        hist.departure := Ada.Real_Time.Clock;
                        vec.Append(Container => track_ptr.history , New_Item => hist.all , Count => 1);
                        log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] is now unblocked from train["&Positive'Image(train_ptr.id)&"]",model_ptr);
                        train_ptr := null;
                        track_ptr.used_by := 0;
                     else
                        Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] received clear out signal from invalid train["&Positive'Image(train_id)&"], currently used by:["&Positive'Image(track_ptr.used_by)&"]"  );
                     end if;
                  end clearAfterTrain;
             -- when track_ptr.out_of_order = true =>
              --    accept
            or
               delay(Standard.Duration(model.getTimeSimToReal(1.0 , Model.Time_Interval_Hour ,model_ptr )));
            end select;

            --for given train waits specified duration and then signals the train that it's ready to depart from this track.
            if not model_ptr.work then
               Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] terminates its execution"  );
               exit;
            elsif track_ptr.out_of_order = false and then (train_ptr /= null and work) then
               work := false;
               --track delay based on track type
               if track_ptr.t_type = Track_Type_Track then
                  --for normal tracks checks speed the train can move on this track.
                  if track_ptr.max_speed < train_ptr.max_speed then
                     train_ptr.current_speed := track_ptr.max_speed;
                     delay_dur := Float(track_ptr.distance)/ Float(track_ptr.max_speed );
                     real_delay_dur := model.getTimeSimToReal(delay_dur,model.Time_Interval_Hour,model_ptr);


                     Ada.Float_Text_IO.Put(To => sim_delay_str , Item => delay_dur ,Aft => 3,Exp => 0);
                     Ada.Float_Text_IO.Put(To => real_delay_str , Item => real_delay_dur ,Aft => 3,Exp => 0);
                     --Ada.Text_IO.Put_Line("["&sim_delay_str&"]");
                     log.putLine("Train["&Positive'Image(train_ptr.id)&"] moves on track["&Positive'Image(track_ptr.id)&"] with track max speed  for next"&sim_delay_str&" hours ("&real_delay_str&"s)",model_ptr);


                     --Ada.Text_IO.Put("Train["&Positive'Image(train_ptr.id)&"] moves on track["&Positive'Image(track_ptr.id)&"] with track max speed  for next " );
                     --Ada.Float_Text_IO.Put(Item => delay_dur  ,Aft => 3,Exp => 0);
                     --Ada.Text_IO.Put( " hours (" );
                     --Ada.Float_Text_IO.Put(Item => real_delay_dur ,Aft => 3,Exp => 0);
                     --Ada.Text_IO.Put_Line("s)");
                  else
                     train_ptr.current_speed := train_ptr.max_speed;
                     delay_dur := Float(track_ptr.distance)/ Float(train_ptr.max_speed);
                     real_delay_dur := model.getTimeSimToReal(delay_dur,model.Time_Interval_Hour,model_ptr);


                     Ada.Float_Text_IO.Put(To => sim_delay_str , Item => delay_dur ,Aft => 3,Exp => 0);
                     Ada.Float_Text_IO.Put(To => real_delay_str , Item => real_delay_dur ,Aft => 3,Exp => 0);
                     --Ada.Text_IO.Put_Line("["&sim_delay_str&"]");

                     log.putLine("Train["&Positive'Image(train_ptr.id)&"] moves with its top speed on track["&Positive'Image(track_ptr.id)&"] for next "&sim_delay_str&" hours ("&real_delay_str&"s)",model_ptr);

                     --Ada.Text_IO.Put("Train["&Positive'Image(train_ptr.id)&"] moves with its top speed on track["&Positive'Image(track_ptr.id)&"] for next ");
                     --Ada.Float_Text_IO.Put(To => test , Item => delay_dur ,Aft => 3,Exp => 0);
                     --Ada.Text_IO.Put( " hours (" );
                     --Ada.Float_Text_IO.Put(Item => real_delay_dur ,Aft => 3,Exp => 0);
                     --Ada.Text_IO.Put_Line("s)");

                  end if;
                  --delay for tracks
                  delay Standard.Duration(real_delay_dur);
                  log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] signals the train["&Positive'Image(train_ptr.id)&"] that it's ready to depart onto next steering",model_ptr);
                  --notify train
                  train_ptr.t_task.trainArrivedToTheEndOfTrack(track_ptr.id);
                  log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] signaled the train["&Positive'Image(train_ptr.id)&"] that it's ready to depart onto next steering",model_ptr);
               elsif track_ptr.t_type = Track_Type_Platform and train_ptr.t_type = Train_Type_Normal then -- and pass_service_train_ptr = null
                  train_ptr.current_speed := 0;
                  delay_dur := Float(track_ptr.min_delay);
                  real_delay_dur := model.getTimeSimToReal(delay_dur,model.Time_Interval_Minute,model_ptr);


                  Ada.Float_Text_IO.Put(To => sim_delay_str , Item => delay_dur ,Aft => 3,Exp => 0);
                  Ada.Float_Text_IO.Put(To => real_delay_str , Item => real_delay_dur ,Aft => 3,Exp => 0);
                  --Ada.Text_IO.Put_Line("["&sim_delay_str&"]");

                  log.putLine("Train["&Positive'Image(train_ptr.id)&"] waits on platform["&Positive'Image(track_ptr.id)&"] for next "&sim_delay_str&" minutes ("&real_delay_str&"s)",model_ptr);


                  --Ada.Text_IO.Put("Train["&Positive'Image(train_ptr.id)&"] waits on platform["&Positive'Image(track_ptr.id)&"] for next " );
                  --Ada.Float_Text_IO.Put(Item => delay_dur ,Aft => 3,Exp => 0);
                  --Ada.Text_IO.Put(" minutes (" );
                  --Ada.Float_Text_IO.Put(Item => real_delay_dur,Aft => 3,Exp => 0);
                  --Ada.Text_IO.Put_Line("s)");


                  --delay for platforms
                  delay Standard.Duration(real_delay_dur);
                  log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] signals the train train["&Positive'Image(train_ptr.id)&"] that it's ready to depart onto next steering",model_ptr);
                  --notify train
                  train_ptr.t_task.trainReadyToDepartFromPlatform(track_ptr.id);
                  log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] signaled the train train["&Positive'Image(train_ptr.id)&"] that it's ready to depart onto next steering",model_ptr);
               --elsif track_ptr.t_type = Track_Type_Service then
               else --for service tracks and platforms on which service trains moves on
                  train_ptr.current_speed := 0;
                  delay_dur :=1.0;
                  real_delay_dur := model.getTimeSimToReal(delay_dur,model.Time_Interval_Minute,model_ptr);


                  Ada.Float_Text_IO.Put(To => real_delay_str , Item => real_delay_dur ,Aft => 3,Exp => 0);

                  log.putLine("Train["&Positive'Image(train_ptr.id)&"] moves on "&ustr.To_String(type_str)&"["&Positive'Image(track_ptr.id)&"] for 1 minute ("&real_delay_str&"s)",model_ptr);

                  --Ada.Text_IO.Put("Train["&Positive'Image(train_ptr.id)&"] waits on unindetified track ["&Positive'Image(track_ptr.id)&"] for 1 minute (" );
                  --Ada.Float_Text_IO.Put(Item => real_delay_dur,Aft => 3,Exp => 0);
                  --Ada.Text_IO.Put_Line("s)");

                  --delay for unknown tracks
                  delay Standard.Duration(real_delay_dur);
                  log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] signals the train["&Positive'Image(train_ptr.id)&"] that it's ready to depart onto next steering",model_ptr);
                  --notify train
                  train_ptr.t_task.trainArrivedToTheEndOfTrack(track_ptr.id);
                  log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] signaled the train["&Positive'Image(train_ptr.id)&"] that it's ready to depart onto next steering",model_ptr);
               end if;
            end if;




            if track_ptr.t_type /= Track_Type_Service and then track_ptr.used_by = 0 and then track_ptr.out_of_order = false then
               ran := Random(G);
               --Ada.Float_Text_IO.Put(To => sim_delay_str , Item => ran ,Aft => 3,Exp => 0);
               --Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] rolled " & sim_delay_str & " at time " & log.toString(log.getRelativeTime(Ada.Real_Time.Clock,model_ptr))  );

               if track_ptr.reliability < ran then
                  log.putLine("#2# " & ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] broke at time " & log.toString(log.getRelativeTime(Ada.Real_Time.Clock,model_ptr)),model_ptr);
                  track_ptr.out_of_order := true;
                  help := false;
               end if;

            end if;

         end loop;
      else
         Ada.Text_IO.Put_Line("TrackTask received null pointer! Task will not work");
      end if;
   exception
      when Error : others =>
         Ada.Text_IO.Put_Line("TrackTask encountered an error:");
         Ada.Text_IO.Put_Line(Exception_Information(Error));
         Ada.Text_IO.Put_Line(Exception_Message(Error));
   end TrackTask;



end track;
