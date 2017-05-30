with model;
with train;
with track;
with Ada.Text_IO;
with Ada.Float_Text_IO;
with Ada.Strings;
with Ada.Strings.Unbounded;
with Ada.Real_Time;
with log;
with Ada.Exceptions;  use Ada.Exceptions;

--@Author: Piotr Olejarz 220398
--Steering package declares record and task for steerings
package body steering is

   -- steering_ptr : access model.STEERING ; model_ptr : access model.Simulation_Model)

   -- Steering task. Allows accepted trains to switch onto their next track.
   task body SteeringTask is
      train_ptr : access train.TRAIN;
      delay_dur : Float;
      real_delay_dur : Float;
      sim_delay_str : String(1..9);
      real_delay_str : String(1..9);
      hist : access Steering_History;
   begin
      hist := null;
      if steering_ptr /= null and model_ptr /= null then
         loop
            --select works on train_ptr state.
            --If train is accepted then if below select is performed and on next cycle thread waits for clearAfterTrain.
            select
               -- when steering_ptr.used_by = 0 =>
               -- if pointer is null then it waits until the train is accepted.

               accept breakSelect do
                  null;
               end breakSelect;
            or

               when train_ptr = null =>
                  --accepts given train thus blocking steering for others
                  accept acceptTrain (train_id : in Positive) do
                     train_ptr := Model.getTrain(train_id,model_ptr);
                     if train_ptr /= null then
                        hist := new Steering_History; --_Record
                        hist.arrival := Ada.Real_Time.Clock;
                        hist.train_id := train_id;
                        log.putLine("Steering["&Natural'Image(steering_ptr.id)&"] blocked by passing train: ["&Positive'Image(train_ptr.id)&"]",model_ptr);
                        steering_ptr.used_by := train_ptr.id;
                     else
                        Ada.Text_IO.Put_Line("Steering["&Natural'Image(steering_ptr.id)&"] received null pointer for train ID["&Positive'Image(train_id)&"]");
                     end if;
                  end acceptTrain;
            or
               --otherwise waits for train to clear out the steering.
               when train_ptr /= null =>
                  --clears out block for other trains after currently blocking train left the steering.
                  accept clearAfterTrain(train_id : in Positive) do
                     if train_ptr.id = train_id then
                        hist.departure := Ada.Real_Time.Clock;
                        vec.Append(Container => steering_ptr.history , New_Item => hist.all , Count => 1);
                        log.putLine("Steering["&Natural'Image(steering_ptr.id)&"] unblocked after train["&Positive'Image(train_ptr.id)&"] passed by",model_ptr);
                        train_ptr := null;
                        steering_ptr.used_by := 0;
                        hist := null;
                     else
                        Ada.Text_IO.Put_Line("Steering["&Natural'Image(steering_ptr.id)&"] received clear out signal from invalid train:["&Positive'Image(train_id)&"], currently used by:["&Positive'Image(steering_ptr.used_by)&"]");
                     end if;
                  end clearAfterTrain;
            --or
            --   delay(Standard.duration(1));
            end select;

            --for given train waits specified duration and then signals the train that it's ready to depart from this steering.
            if not model_ptr.work then
               Ada.Text_IO.Put_Line("Steering["&Natural'Image(steering_ptr.id)&"] terminates its execution"  );
               exit;
            elsif train_ptr /= null then
               delay_dur := Float(steering_ptr.min_delay);
               real_delay_dur := model.getTimeSimToReal(delay_dur,model.Time_Interval_Minute,model_ptr);


               Ada.Float_Text_IO.Put(To => sim_delay_str , Item => delay_dur ,Aft => 3,Exp => 0);
               Ada.Float_Text_IO.Put(To => real_delay_str , Item => real_delay_dur ,Aft => 3,Exp => 0);
               log.putLine("Steering["&Natural'Image(steering_ptr.id)&"] with train["&Positive'Image(train_ptr.id)&"] switches tracks for "&sim_delay_str&" minutes ("&real_delay_str&"s)",model_ptr);

               --Ada.Text_IO.Put("Steering["&Natural'Image(steering_ptr.id)&"] with train["&Positive'Image(train_ptr.id)&"] switches tracks for " );
               --Ada.Float_Text_IO.Put(Item => delay_dur,Aft => 3,Exp => 0);
               --Ada.Text_IO.Put(" minutes (" );
               --Ada.Float_Text_IO.Put(Item => real_delay_dur,Aft => 3,Exp => 0);
               --Ada.Text_IO.Put_Line("s)");

               --steering delay
               delay Standard.Duration(real_delay_dur);

               log.putLine("Steering["&Natural'Image(steering_ptr.id)&"] signals the train["&Positive'Image(train_ptr.id)&"] that it's ready to depart onto next track",model_ptr);
               --notify train
               train_ptr.t_task.trainReadyToDepartFromSteering(steering_ptr.id);



            --   if next_track_ptr.used_by /= 0 then
            --      delay_dur := 1.0;
            --      real_delay_dur := model.getTime(delay_dur,model.Time_Interval_Minute,model_ptr);
            --
            --      Ada.Text_IO.Put("Steering["&Natural'Image(steering_ptr.id)&"] with train["&Positive'Image(train_ptr.id)&"] waits for track["&Natural'Image(next_track_ptr.id)&"] to be cleared out with 1 minute intervals (" );
            --      Ada.Float_Text_IO.Put(Item => real_delay_dur,Aft => 3,Exp => 0);
            --      Ada.Text_IO.Put_Line("s)");
            --
            --      while next_track_ptr.used_by = 0 loop
            --         delay Standard.Duration(delay_dur);
            --      end loop;
            --   end if;
            --    next_track_ptr.used_by := train_ptr.id;

            end if;
         end loop;
      else
         Ada.Text_IO.Put_Line("SteeringTask received null pointer! Task will not work");
      end if;
   exception
      when Error : others =>
         Ada.Text_IO.Put_Line("SteeringTask encountered an error:");
         Ada.Text_IO.Put_Line(Exception_Information(Error));
         Ada.Text_IO.Put_Line(Exception_Message(Error));
   end SteeringTask;

end steering;
