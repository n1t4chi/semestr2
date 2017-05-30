with Model; use model;
with train;
with steering;
with Ada.Text_IO;
with Ada.Float_Text_IO;
with Ada.Strings.Unbounded;
with log;
with Ada.Containers.Vectors;
with Ada.Exceptions;  use Ada.Exceptions;
with Ada.Real_Time;

--@Author: Piotr Olejarz 220398
--Track package declares record and task for tracks
package body track is
   package ustr renames Ada.Strings.Unbounded;
   --Track task. Allows accepted trains to move/wait on them
   task body TrackTask is
      type_str : Ada.Strings.Unbounded.Unbounded_String;
      train_ptr : access train.TRAIN;
      delay_dur : Float;
      real_delay_dur : Float;
      sim_delay_str : String(1..8);
      real_delay_str : String(1..8);
      hist : access Track_History;
   begin
      hist := null;
      if track_ptr /= null and model_ptr /= null then
         --track naming
         if track_ptr.t_type = Track_Type_Platform then
           type_str := ustr.To_Unbounded_String("Platform");
         elsif track_ptr.t_type = Track_Type_Track then
            type_str := ustr.To_Unbounded_String("Track");
         else
            type_str := ustr.To_Unbounded_String("Unknown");
         end if;

         loop
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
               when train_ptr = null =>
                  --accepts given train thus blocking track for others
                  accept acceptTrain (train_id : in Positive) do
                     train_ptr := Model.getTrain(train_id,model_ptr);
                     if train_ptr /= null then
                        hist := new Track_History; --_Record
                        hist.arrival := Ada.Real_Time.Clock;
                        hist.train_id := train_id;
                        log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] is now blocked by train["&Positive'Image(train_ptr.id)&"]",model_ptr);
                        track_ptr.used_by := train_ptr.id;
                     else
                        Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] received null pointer for train ID["&Positive'Image(train_id)&"]" );
                     end if;
                  end acceptTrain;
            or
               --otherwise waits for train to clear out the steering.
               when train_ptr /= null =>
                  --clears out block for other trains after currently blocking train left the track.
                  accept clearAfterTrain(train_id : in Positive) do
                     if train_ptr.id = train_id then
                        hist.departure := Ada.Real_Time.Clock;
                        vec.Append(Container => track_ptr.history , New_Item => hist.all , Count => 1);
                        log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] is now unblocked from train["&Positive'Image(train_ptr.id)&"]",model_ptr);
                        train_ptr := null;
                        track_ptr.used_by := 0;
                     else
                        Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] received clear out signal from invalid train:["&Positive'Image(train_id)&"], currently used by:["&Positive'Image(track_ptr.used_by)&"]"  );
                     end if;
                  end clearAfterTrain;
            --or
           --    delay(Standard.duration(1));
            end select;

            --for given train waits specified duration and then signals the train that it's ready to depart from this track.
            if not model_ptr.work then
               Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] terminates its execution"  );
            elsif train_ptr /= null then
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
                  log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] signals the train:["&Positive'Image(train_ptr.id)&"] that it's ready to depart onto next steering",model_ptr);
                  --notify train
                  train_ptr.t_task.trainArrivedToTheEndOfTrack(track_ptr.id);
               elsif track_ptr.t_type = Track_Type_Platform then
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
               else --safe clause
                  train_ptr.current_speed := 0;
                  delay_dur :=1.0;
                  real_delay_dur := model.getTimeSimToReal(delay_dur,model.Time_Interval_Minute,model_ptr);


                  Ada.Float_Text_IO.Put(To => real_delay_str , Item => real_delay_dur ,Aft => 3,Exp => 0);

                  log.putLine("Train["&Positive'Image(train_ptr.id)&"] waits on unindetified track ["&Positive'Image(track_ptr.id)&"] for 1 minute ("&real_delay_str&"s)",model_ptr);

                  --Ada.Text_IO.Put("Train["&Positive'Image(train_ptr.id)&"] waits on unindetified track ["&Positive'Image(track_ptr.id)&"] for 1 minute (" );
                  --Ada.Float_Text_IO.Put(Item => real_delay_dur,Aft => 3,Exp => 0);
                  --Ada.Text_IO.Put_Line("s)");

                  --delay for unknown tracks
                  delay Standard.Duration(real_delay_dur);
                  log.putLine(ustr.To_String(type_str)&"["&Natural'Image(track_ptr.id)&"] signals the train["&Positive'Image(train_ptr.id)&"] that it's ready to depart onto next steering",model_ptr);
                  --notify train
                  train_ptr.t_task.trainArrivedToTheEndOfTrack(track_ptr.id);
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
