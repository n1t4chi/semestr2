with Ada.Containers;
with Model;
with Ada.Text_IO;
with Ada.Float_Text_IO;
with log;
with Ada.Exceptions;  use Ada.Exceptions;
with Ada.Numerics.Float_Random; use Ada.Numerics.Float_Random;
with Ada.Real_Time; use Ada.Real_Time;
with train;
with station;

with Ada.Containers.Hashed_Sets;
--@Author: Piotr Olejarz 220398
--Worker package declares record and task for workers
package body worker is

   task body WorkerTask is
      work_duration : Float;
      work : Boolean:=False;
      train_ptr : access train.TRAIN;
      stat_ptr : access station.STATION;

      check_train : Natural := 0;
      check_station : Natural := 0;
   begin
      if model_ptr /= null and work_ptr /= null then
         while model_ptr.work loop
            select
               when work_ptr.state = AtHome =>
                  accept acceptTask( stat_id : Positive ) do
                     work_ptr.state := TravellingToWork;
                     work_ptr.dest_Station := stat_id;
                  end acceptTask;
            or
               when work_ptr.state = WaitingForWork =>
                  accept startTask( stat_id : Positive ; work_time_hours : Float) do
                     if stat_id = work_ptr.dest_Station then
                        work_duration := work_time_hours;
                        work := True;
                     else
                        Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received illegal start task notification from station["&Positive'Image(stat_id)&"]");
                     end if;
                  end startTask;
            or
               when work_ptr.on_train /= 0 =>
                  accept trainStop ( train_id : Positive ; stat_id : Positive ) do
                     if train_id = work_ptr.on_train then
                        check_station := stat_id;
                     else
                        Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received illegal train stop notification from train["&Positive'Image(train_id)&"]");
                     end if;
                  end trainStop;
            or
               when work_ptr.on_Station /= 0 and ( work_ptr.state = TravellingToWork or work_ptr.state = TravellingToHome) =>
               accept notifyAboutTrainArrival(stat_id : Positive ; train_id : Positive) do
                  if stat_id = work_ptr.on_Station then
                     check_train := train_id;
                  else
                     Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received illegal train arrival notification from station["&Positive'Image(stat_id)&"]");
                  end if;
               end notifyAboutTrainArrival;
            or
               delay Standard.Duration(model.getTimeSimToReal(time_in_interval => 100.0,interval => model.Time_Interval_Minute , model_ptr => model_ptr));
            end select;
            --Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] ############ after select");



            if check_station /= 0 and then work_ptr.connectionlist(work_ptr.connectionlist_iterator).arrive_station_id = check_station then
               train_ptr := Model.getTrain(train_id => work_ptr.on_train , model_ptr => model_ptr);
               stat_ptr := Model.getStation(stat_id => check_station , model_ptr => model_ptr);
               if train_ptr /= null and stat_ptr /= null then
                  if work_ptr.connectionlist(work_ptr.connectionlist_iterator).arrive_station_id = check_station then


                     log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] tries to enter station["&Positive'Image(check_station)&"]",model_ptr);
                     select
                        stat_ptr.s_task.notifyAboutWorkerArrival(work_ptr.id);
                        train_ptr.t_task.leaveTrain(work_ptr.id);
                        work_ptr.on_train := 0;
                        work_ptr.on_Station := stat_ptr.id;
                        work_ptr.connectionlist_iterator := work_ptr.connectionlist_iterator +1;
                        log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] left the train["&Positive'Image(train_ptr.id) & "] and entered station["&Positive'Image(stat_ptr.id)&"]",model_ptr);
                     or
                        delay 10.0;
                        log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] failed to leave train["&Positive'Image(train_ptr.id)&"]",model_ptr);
                     end select;

                  end if;
               else
                  if train_ptr = null then
                     Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received null pointer for train["&Positive'Image(work_ptr.on_train)&"]");
                  else
                     Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received null pointer for station["&Positive'Image(check_station)&"]");
                  end if;
               end if;
               check_station := 0;
            end if;

            if check_train /= 0 and then work_ptr.connectionlist /= null and then (work_ptr.connectionlist(work_ptr.connectionlist_iterator)) /= null then
               stat_ptr := Model.getStation(stat_id => work_ptr.on_Station , model_ptr => model_ptr);
               if stat_ptr /= null then
                  if work_ptr.connectionlist(work_ptr.connectionlist_iterator).train_id = check_train then
                     train_ptr := Model.getTrain(train_id => check_train , model_ptr => model_ptr);
                     if train_ptr /= null then
                        if work_ptr.connectionlist(work_ptr.connectionlist_iterator).train_id = check_train then
                           log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] tries to enter train["&Positive'Image(train_ptr.id)&"]",model_ptr);
                           select
                              train_ptr.t_task.enterTrain(work_ptr.id);
                              stat_ptr.s_task.notifyAboutWorkerDeparture(work_ptr.id);
                              log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] aboards train["&Positive'Image(train_ptr.id)&"] and leaves station["&Positive'Image(stat_ptr.id)&"]",model_ptr);
                              work_ptr.on_train := train_ptr.id ;
                              work_ptr.on_Station := 0;
                           or
                              delay 10.0;
                              Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] failed to hop on train["&Positive'Image(train_ptr.id)&"]");
                           end select;
                        else
                           Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received null pointer for train["&Positive'Image(check_train)&"]");
                        end if;
                     else
                        Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received null pointer for station["&Positive'Image(work_ptr.on_Station)&"]");
                     end if;
                  end if;
               end if;
               check_train := 0;
            end if;


            if work_ptr.state = TravellingToWork and work_ptr.connectionlist = null then
               work_ptr.connectionlist := model.getConnection(source_station => work_ptr.home_stat_id , destination_station => work_ptr.dest_Station , model_ptr => model_ptr );
               work_ptr.connectionlist_iterator := 1;
               if work_ptr.connectionlist = null then
                  Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] did not found connection from "& Positive'Image(work_ptr.home_stat_id) & " to " & Positive'Image(work_ptr.dest_Station) & " stations.");
                  work_ptr.state := AtHome;
                  work_ptr.dest_Station := 0;
               elsif work_ptr.connectionlist'Length = 0 then
                  work_ptr.connectionlist := null;
                  work_ptr.connectionlist_iterator := 0;
                  work_ptr.state := WaitingForWork;
                  stat_ptr := Model.getStation(stat_id => work_ptr.dest_Station , model_ptr => model_ptr);
                  if stat_ptr /= null then
                     log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] is already at target station and is ready to start working.",model_ptr);
                     stat_ptr.s_task.notifyAboutReadinessToWork(work_ptr.id);
                  else
                     Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received null pointer for station["&Positive'Image(work_ptr.dest_Station)&"]");
                  end if;
               else
                  log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] has accepted request for work. Moving to station["&Positive'Image(work_ptr.dest_Station)&"]",model_ptr);
               end if;
            end if;


            if work then
               stat_ptr := Model.getStation(stat_id => work_ptr.dest_Station , model_ptr => model_ptr);
               if stat_ptr /= null then
                  work := false;
                  declare
                     dur: String(1..6);
                  begin
                     Ada.Float_Text_IO.Put(To => dur , Item => work_duration ,Aft => 3,Exp => 0);
                     log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] starts working for next " & dur & " hours.",model_ptr);
                  end;
                  delay Standard.Duration(Model.getTimeSimToReal(time_in_interval => work_duration,interval => model.Time_Interval_Hour,model_ptr => model_ptr));
                  log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] finished working on task.",model_ptr);
                  stat_ptr.s_task.notifyAboutFinishingTheWork(work_ptr.id);

               else
                  Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received null pointer for station["&Positive'Image(work_ptr.dest_Station)&"]");
               end if;

               work_ptr.connectionlist := model.getConnection(source_station => work_ptr.dest_Station , destination_station => work_ptr.home_stat_id , model_ptr => model_ptr );
               work_ptr.connectionlist_iterator := 1;
               work_ptr.dest_Station := work_ptr.home_stat_id;
               work_ptr.state := TravellingToHome;
               if work_ptr.connectionlist = null then
                  Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] does not found connection from "& Positive'Image(work_ptr.dest_Station) & " to " & Positive'Image(work_ptr.home_stat_id) & " stations.");
               elsif work_ptr.connectionlist'Length = 0 then
                  work_ptr.connectionlist := null;
                  work_ptr.connectionlist_iterator := 0;
                  work_ptr.state := AtHome;
                  log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] left work and went directly back home.",model_ptr);
               else
                  --  log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] &^&^&^^&."&Natural'Image(work_ptr.connectionlist'Length),model_ptr);
                  log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] left work and is going back to home station["&Positive'Image(work_ptr.home_stat_id)&"]",model_ptr);
               end if;
            elsif work_ptr.state = TravellingToWork and work_ptr.on_Station = work_ptr.dest_Station then
               work_ptr.connectionlist := null;
               work_ptr.connectionlist_iterator := 0;
               work_ptr.state := WaitingForWork;
               stat_ptr := Model.getStation(stat_id => work_ptr.dest_Station , model_ptr => model_ptr);
               if stat_ptr /= null then
                  log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] arrived to target station and is ready to start working.",model_ptr);
                  stat_ptr.s_task.notifyAboutReadinessToWork(work_ptr.id);
                  -- log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] notified that it's ready to work.",model_ptr);
               else
                  Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received null pointer for station["&Positive'Image(work_ptr.dest_Station)&"]");
               end if;
            elsif work_ptr.state = TravellingToHome and work_ptr.on_Station = work_ptr.home_stat_id then
               work_ptr.connectionlist := null;
               work_ptr.connectionlist_iterator := 0;
               work_ptr.state := AtHome;
               log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] arrived back at home.",model_ptr);
            elsif work_ptr.connectionlist /= null and work_ptr.on_Station /= 0  then
               stat_ptr := Model.getStation(stat_id => work_ptr.on_Station , model_ptr => model_ptr);
               if stat_ptr /= null then
                  for cur in IntSet.Iterate(stat_ptr.trains) loop
                     if IntSet.Element(Position => cur) = work_ptr.connectionlist(work_ptr.connectionlist_iterator).train_id then

                        train_ptr := model.getTrain(train_id => work_ptr.connectionlist(work_ptr.connectionlist_iterator).train_id , model_ptr => model_ptr);
                        if train_ptr /= null then


                           log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] tries to enter train["&Positive'Image(train_ptr.id)&"]",model_ptr);
                           select
                              train_ptr.t_task.enterTrain(work_ptr.id);
                              stat_ptr.s_task.notifyAboutWorkerDeparture(work_ptr.id);
                              log.putLine("#3# worker["& Positive'Image(work_ptr.id) &"] aboards train["&Positive'Image(train_ptr.id)&"] and leaves station["&Positive'Image(stat_ptr.id)&"]",model_ptr);
                              work_ptr.on_train := train_ptr.id ;
                              work_ptr.on_Station := 0;
                           or
                              delay 10.0;
                              Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] failed to hop on train["&Positive'Image(train_ptr.id)&"]");
                           end select;
                        else
                           Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received null pointer for train["&Positive'Image(work_ptr.connectionlist(work_ptr.connectionlist_iterator).train_id)&"]");
                        end if;

                     end if;
                  end loop;
               else
                  Ada.Text_IO.Put_Line("#3# worker["& Positive'Image(work_ptr.id) &"] received null pointer for station["&Positive'Image(work_ptr.dest_Station)&"]");
               end if;
            end if;
         end loop;
      else
         Ada.Text_IO.Put_Line("#3# WorkTask received null pointer.");
      end if;
   exception
      when Error : others =>
         Ada.Text_IO.Put_Line("WorkerTask encountered an error:");
         Ada.Text_IO.Put_Line(Exception_Information(Error));
         Ada.Text_IO.Put_Line(Exception_Message(Error));
   end WorkerTask;


   function Hash(Key : Positive) return Hash_Type is
   begin
      return Hash_Type(key);
   end;
   function Hash (Key : AWORKER) return Ada.Containers.Hash_Type is
   begin
      return Ada.Containers.Hash_Type(Key.id);
   end;

  -- function "="(Left : AWORKER ; Right : AWORKER) return Boolean is
  -- begin
  --    if Left = null or Right = null then
   --      return Left = null and Right = null;
    --  else
     --    return Left.id = Right.id;
     -- end if;
  -- end "=";

end worker;
