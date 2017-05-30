with Ada.Containers.Hashed_Sets;
with worker; use worker;
with Model;
with Ada.Text_IO;
with Ada.Float_Text_IO;
with log;
with Ada.Exceptions;  use Ada.Exceptions;
with Ada.Numerics.Float_Random; use Ada.Numerics.Float_Random;
with Ada.Real_Time; use Ada.Real_Time;
--@Author: Piotr Olejarz 220398
--Station package declares record and task for stations
package body station is



   task body StationTask is
      G : Generator;
      ran : Float;
      work_ptr : access worker.WORKER;
      created_task : Boolean := false;
      active_task : Boolean := false;

      notify_train : Natural:=0;
   begin
    --  Ada.Text_IO.Put_Line("#3#%%%%%%%%%%%%%%%%%%%%%%%%% initial" );
      if model_ptr /= null and stat_ptr /= null then
         Reset(G,10*stat_ptr.id+65536 + Integer(Float'Value(Duration'Image(Ada.Real_Time.To_Duration(Ada.Real_Time.Clock-model_ptr.start_time)))));
        -- Ada.Text_IO.Put_Line("#3#%%%%%%%%%%%%%%%%%%%%%%%%% before loop" );
         while model_ptr.work loop
           -- Ada.Text_IO.Put_Line("#3#%%%%%%%%%%%%%%%%%%%%%%%%% loop ### before select" );
            select
               accept notifyAboutTrainArrival (train_id : Positive) do
                  --Ada.Text_IO.Put_Line("%%%%%%%%%%%%%%%%%%%%%%%%% loop ### train arrival $$$ before" );
                  if not IntSet.Contains(Container => stat_ptr.trains,Item => train_id) then
                     log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " welcomes train:" & Positive'Image(train_id) , model_ptr);
                     IntSet.Insert(Container => stat_ptr.trains,New_Item => train_id);
                     notify_train:= train_id;
                     else
                        Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received illegal arrival notification from train:" & Positive'Image(train_id));
                  end if;
               end notifyAboutTrainArrival;
            or
               accept notifyAboutTrainDeparture (train_id : Positive) do
                  if IntSet.Contains(Container => stat_ptr.trains,Item => train_id) then
                     log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " bids farewell to train:" & Positive'Image(train_id) , model_ptr);
                     IntSet.Delete(Container => stat_ptr.trains,Item => train_id);
                  else
                     Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received illegal departure notification from train:" & Positive'Image(train_id));
                  end if;


               end notifyAboutTrainDeparture;
            or
               accept notifyAboutWorkerArrival (work_id : Positive) do
                  work_ptr := Model.getWorker(work_id,model_ptr);
                  if work_ptr /= null then
                     if not HashSet.Contains(Container => stat_ptr.passengers , Item => work_ptr) then
                        HashSet.Insert(Container => stat_ptr.passengers , New_Item => work_ptr);
                        log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " welcomes passenger:" & Positive'Image(work_id) , model_ptr);
                     else
                        Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received illegal arrival notification from worker:" & Positive'Image(work_id));
                     end if;
                  else
                     Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received null pointer for worker id: " & Positive'Image(work_id));
                  end if;
               end notifyAboutWorkerArrival;
            or
               accept notifyAboutWorkerDeparture (work_id : Positive) do
                  work_ptr := Model.getWorker(work_id,model_ptr);
                  if work_ptr /= null then
                     if HashSet.Contains(Container => stat_ptr.passengers , Item => work_ptr) then
                        log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " bids farewell to passenger:" & Positive'Image(work_id) , model_ptr);
                        HashSet.Delete(Container => stat_ptr.passengers ,Item => work_ptr);

                        if HashSet.Contains(Container => stat_ptr.ready_workers , Item => work_ptr) then
                           HashSet.Delete(Container => stat_ptr.ready_workers ,Item => work_ptr);
                           Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received illegal departure notification from worker:" & Positive'Image(work_id) & " before he finished task.");
                        end if;
                        if HashSet.Contains(Container => stat_ptr.chosen_workers , Item => work_ptr) then
                           HashSet.Delete(Container => stat_ptr.chosen_workers ,Item => work_ptr);
                           Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received illegal departure notification from worker:" & Positive'Image(work_id) & " before he started task.");
                        end if;

                     else
                       -- log.printStations(model_ptr);
                       -- log.printWorkers(model_ptr);
                        Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received illegal departure notification from worker:" & Positive'Image(work_id));

                     end if;
                  else
                     Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received null pointer for worker id: " & Positive'Image(work_id));
                  end if;
               end notifyAboutWorkerDeparture;
            or
               when created_task =>
               --when not HashSet.Is_Empty(Container => stat_ptr.chosen_workers) =>
                  accept notifyAboutReadinessToWork (work_id : Positive) do
                     work_ptr := Model.getWorker(work_id,model_ptr);
                     if work_ptr /= null then
                        if HashSet.Contains(Container => stat_ptr.passengers , Item => work_ptr) and
                          HashSet.Contains(Container => stat_ptr.chosen_workers , Item => work_ptr) and
                          not HashSet.Contains(Container => stat_ptr.ready_workers , Item => work_ptr)
                        then
                           log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " received notification that worker:" & Positive'Image(work_id) & " is ready to work" , model_ptr);
                           HashSet.Insert(Container => stat_ptr.ready_workers , New_Item => work_ptr);
                        else
                           Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received illegal ready notification from worker:" & Positive'Image(work_id));
                        end if;
                     else
                        Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received null pointer for worker id: " & Positive'Image(work_id));
                     end if;
                  end notifyAboutReadinessToWork;
            or
               when active_task =>
               --when not HashSet.Is_Empty(Container => stat_ptr.chosen_workers) =>
                  accept notifyAboutFinishingTheWork (work_id : Positive) do
                     work_ptr := Model.getWorker(work_id,model_ptr);
                     if work_ptr /= null then
                        if HashSet.Contains(Container => stat_ptr.ready_workers , Item => work_ptr) and HashSet.Contains(Container => stat_ptr.chosen_workers , Item => work_ptr)  then
                           log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " received notification that worker:" & Positive'Image(work_id) & " finished his task" , model_ptr);
                           HashSet.Delete(Container => stat_ptr.ready_workers ,Item => work_ptr);
                           HashSet.Delete(Container => stat_ptr.chosen_workers ,Item => work_ptr);
                        else
                           if HashSet.Contains(Container => stat_ptr.ready_workers , Item => work_ptr) then
                              HashSet.Delete(Container => stat_ptr.ready_workers ,Item => work_ptr);
                              Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " had worker:" & Positive'Image(work_id)& " only within ready worker pool.");
                           elsif HashSet.Contains(Container => stat_ptr.chosen_workers , Item => work_ptr)  then
                              Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received illegal finish notification from worker:" & Positive'Image(work_id) & " before he started task.");
                           else
                              Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received illegal finish notification from worker:" & Positive'Image(work_id) & " .");
                           end if;

                        end if;
                     else
                        Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received null pointer for worker id: " & Positive'Image(work_id));
                     end if;
                  end notifyAboutFinishingTheWork;
            or
               delay Standard.Duration(model.getTimeSimToReal(time_in_interval => 1.0,interval => model.Time_Interval_Hour , model_ptr => model_ptr));
            end select;


            if notify_train /= 0 then
               for cur in HashSet.Iterate(Container => stat_ptr.passengers) loop
                  work_ptr := HashSet.Element(Position => cur);
                  select
                     work_ptr.w_task.notifyAboutTrainArrival(stat_id => stat_ptr.id,train_id => notify_train);
                  else
                     null;
                  end select;
               end loop;
               notify_train := 0;
            end if;


         --   Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " active" & Boolean'Image(active_task) & "  created" & Boolean'Image(created_task));
            if created_task then
               if HashSet.Equivalent_Sets(Left => stat_ptr.ready_workers , Right => stat_ptr.chosen_workers) then

                  ran := 5.0 + 5.0*Random(G);
                  declare
                     dur: String(1..6);
                  begin
                     Ada.Float_Text_IO.Put(To => dur , Item => ran ,Aft => 3,Exp => 0);
                     log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " got notifications from all chosen workers. Notifying workers thath they can start working for next " & dur & " hours.");
                  end;
                --  log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & "###################1");
                  created_task := false;
                  active_task := true;
                 -- log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & "###################2");
                  for cur in HashSet.Iterate(Container => stat_ptr.ready_workers) loop
                     work_ptr := HashSet.Element(Position => cur);
                     --work_ptr := Model.getWorker(work_id => HashSet.Element(Position => cur) , model_ptr => model_ptr);
                     -- if work_ptr /= null then
                  --   log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & "###################3.1");
                    -- select
                        work_ptr.w_task.startTask(stat_ptr.id,ran);
                    --    log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & "###################3.22 ok");
                   --  or
                    --    delay 10.0;
                   --     log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & "###################3.2 nope");
                   --  end select;
                   --  log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & "###################3.2");
                     -- else
                     --    Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " received null pointer for worker id: " & Positive'Image(HashSet.Element(Position => cur).id));
                     -- end if;
                  end loop;
                 -- log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & "###################4");
               else
                  if Natural(HashSet.Length(Container => stat_ptr.ready_workers)) = Natural(HashSet.Length(Container => stat_ptr.chosen_workers)) then
                     Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " has same amount of ready and chosen workers but sets are different");
                  end if;
               end if;
            elsif (not active_task) and (not created_task) then
               ran := Random(G);
               if ran < 0.05 then
                  log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " needs to have new task performed. Notifying workers");
                  declare
                     worker_count : Natural:=0;
                  begin
                     for it in model_ptr.worker'Range loop
                        work_ptr := model_ptr.worker(it);
                        if work_ptr.state = AtHome and then Random(G) < 0.25 then
                           select
                              work_ptr.w_task.acceptTask(stat_ptr.id);
                              HashSet.Insert(Container => stat_ptr.chosen_workers , New_Item => work_ptr);
                              worker_count := worker_count+1;
                           or
                              delay 1.0;
                              --Ada.Text_IO.Put_Line("#3# station:" & Positive'Image(stat_ptr.id) & " failed to reach out to worker: " & Natural'Image(work_ptr.id) & "with task offer.");
                           end select;

                        end if;
                     end loop;
                     if worker_count > 0 then
                        log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " choose " & Natural'Image(worker_count) & " workers for task.");
                        created_task := true;
                     else
                        log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " could not choose any workers for this task. Abandoning task.");

                     end if;

                  end;
               end if;
            elsif active_task then
               if HashSet.Is_Empty(stat_ptr.chosen_workers) then
                  log.putLine("#3# station:" & Positive'Image(stat_ptr.id) & " and all workers finished performing a task." , model_ptr);
                  active_task := false;
               end if;
            end if;


         end loop;
      else
         Ada.Text_IO.Put_Line("#3# StationTask received null pointer.");
      end if;

   exception
      when Error : others =>
         Ada.Text_IO.Put_Line("StationTask encountered an error:");
         Ada.Text_IO.Put_Line(Exception_Information(Error));
         Ada.Text_IO.Put_Line(Exception_Message(Error));
   end StationTask;



end station;
