with model;
with train;
with track;
with Ada.Text_IO;
with Ada.Float_Text_IO;
with Ada.Strings;
with Ada.Strings.Unbounded;
with Ada.Real_Time; use Ada.Real_Time;
with log;
with Ada.Exceptions;  use Ada.Exceptions;
with Ada.Numerics.Float_Random; use Ada.Numerics.Float_Random;

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
      G : Generator;
      ran : Float;
      hist : access Steering_History;

      help_service_train_ptr : access train.TRAIN := null;
      pass_service_train_ptr : access train.TRAIN := null;

      help : boolean := false;


   begin
      hist := null;
      if steering_ptr /= null and model_ptr /= null then
         Reset(G,3*steering_ptr.id+256 + Integer(Float'Value(Duration'Image(Ada.Real_Time.To_Duration(Ada.Real_Time.Clock-model_ptr.start_time)))));

         loop
            if steering_ptr.out_of_order = true and help = false then
               help_service_train_ptr := model.getServiceTrain(model_ptr);
               if help_service_train_ptr /= null then

                  while help_service_train_ptr.t_task = null loop
                     delay Standard.Duration(1);
                  end loop;

                  select
                     help_service_train_ptr.t_task.steeringOutOfOrder(steering_ptr.id);
                     help := true;
                  or
                     delay Standard.Duration(1);
                  end select;

               else
                  Ada.Text_IO.Put_Line("#2#" & "Steering["&Positive'Image(steering_ptr.id)&"] received null pointer for service train" );
               end if;
            end if;


            --select works on train_ptr state.
            --If train is accepted then if below select is performed and on next cycle thread waits for clearAfterTrain.
            select
               -- when steering_ptr.used_by = 0 =>
               -- if pointer is null then it waits until the train is accepted.

               accept breakSelect do
                  null;
               end breakSelect;
            or
               when steering_ptr.used_by = 0 =>
               accept allowServiceTrain( train_id : in Positive) do
                     pass_service_train_ptr := model.getTrain(train_id,model_ptr);
                     if pass_service_train_ptr /= null then
                        steering_ptr.used_by := train_id;
                      --  log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] received accept request from service train ["&Positive'Image(pass_service_train_ptr.id)&"]. Blocking steering for other trains.",model_ptr);
                     else
                        Ada.Text_IO.Put_Line("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] received null pointer for service train ID["&Positive'Image(train_id)&"]");
                     end if;
               end allowServiceTrain;
            or
               accept acceptServiceTrain( train_id : in Positive) do
                  if pass_service_train_ptr /= null and then pass_service_train_ptr.id = train_id then
                     log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] blocked by passing service train: ["&Positive'Image(pass_service_train_ptr.id)&"]",model_ptr);
                     train_ptr := pass_service_train_ptr;

                     hist := new Steering_History; --_Record
                     hist.arrival := Ada.Real_Time.Clock;
                     hist.train_id := train_id;

                  else
                     if pass_service_train_ptr = null  then
                        if steering_ptr.used_by = 0 or steering_ptr.used_by = train_id then
                           log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] received accept signal from invalid serivce train ID["&Positive'Image(train_id)&"] no service train expeted. Blocking the track anyway.",model_ptr);
                           pass_service_train_ptr := model.getTrain(train_id,model_ptr);
                           steering_ptr.used_by := train_id;
                           train_ptr := pass_service_train_ptr;


                           hist := new Steering_History; --_Record
                           hist.arrival := Ada.Real_Time.Clock;
                           hist.train_id := train_id;

                        else
                           log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] received accept signal from invalid serivce train ID["&Positive'Image(train_id)&"] no service train expeted at is currently used by other train.",model_ptr);

                        end if;
                     else
                        log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] received accept signal from invalid serivce train ID["&Positive'Image(train_id)&"] expected: ["&Positive'Image(pass_service_train_ptr.id)&"]",model_ptr);
                     end if;
                  end if;
               end acceptServiceTrain;
            or
               when steering_ptr.out_of_order = false and then steering_ptr.used_by /= 0 =>
               accept freeFromServiceTrain ( train_id : in Positive ) do
                  if pass_service_train_ptr /= null and then pass_service_train_ptr.id = train_id then
                     log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] unblocked from service train["&Positive'Image(pass_service_train_ptr.id)&"]",model_ptr);
                     steering_ptr.used_by := 0;
                     train_ptr := null;
                     pass_service_train_ptr := null;
                  else
                     if pass_service_train_ptr = null then
                        Ada.Text_IO.Put_Line("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] receive free signal from invalid serivce train ID["&Positive'Image(train_id)&"] no service train accepted.");
                     else
                        Ada.Text_IO.Put_Line("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] receive free signal from invalid serivce train ID["&Positive'Image(train_id)&"] accepted: ["&Positive'Image(pass_service_train_ptr.id)&"]");
                     end if;
                  end if;
               end freeFromServiceTrain;
            or
                 --when steering_ptr.out_of_order = true =>
               accept repair ( train_id : in Positive)  do
                  if help_service_train_ptr /= null and then help_service_train_ptr.id = train_id then
                     log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] was just repaired. Ready to accept incoming trains anew.",model_ptr);
                     steering_ptr.out_of_order := false;
                  else
                     if help_service_train_ptr /= null then
                        log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] has no information about service train but received repair signal from service train["&Positive'Image(train_id)&"]. Accepting the repair and moving along with schedule.",model_ptr);
                        steering_ptr.out_of_order := false;
                     else
                        log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] received repair signal from illegal service train ["&Positive'Image(train_id)&"]. Accepting the repair and moving along with schedule.",model_ptr);
                        steering_ptr.out_of_order := false;
                     end if;
                  end if;

                  end repair;
            or

               when steering_ptr.out_of_order = false and then steering_ptr.used_by = 0 =>
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
               when steering_ptr.out_of_order = false and then steering_ptr.used_by /= 0 =>
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
            or
               delay(Standard.Duration(model.getTimeSimToReal(1.0 , Model.Time_Interval_Hour ,model_ptr )));
            end select;

            --for given train waits specified duration and then signals the train that it's ready to depart from this steering.
            if not model_ptr.work then
               Ada.Text_IO.Put_Line("Steering["&Natural'Image(steering_ptr.id)&"] terminates its execution"  );
               exit;
            elsif steering_ptr.out_of_order = false and then train_ptr /= null then
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




            end if;

            if steering_ptr.used_by = 0 and steering_ptr.out_of_order = false then
               ran := Random(G);
               --Ada.Float_Text_IO.Put(To => sim_delay_str , Item => ran ,Aft => 3,Exp => 0);
               --Ada.Text_IO.Put_Line("Steering["&Natural'Image(steering_ptr.id)&"] rolled " & sim_delay_str & " at time " & log.toString(log.getRelativeTime(Ada.Real_Time.Clock,model_ptr))  );
               if steering_ptr.reliability < ran then
                  log.putLine("#2#" & "Steering["&Natural'Image(steering_ptr.id)&"] broke at time " & log.toString(log.getRelativeTime(Ada.Real_Time.Clock,model_ptr)) ,model_ptr );
                  steering_ptr.out_of_order := true;
                  help := false;
               end if;

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
