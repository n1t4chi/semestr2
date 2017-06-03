limited with model;
with Ada.Containers; use Ada.Containers;
with Ada.Containers.Hashed_Sets;
--@Author: Piotr Olejarz 220398
--Worker package declares record and task for workers
package worker is


   type WorkerState is (AtHome,TravellingToWork,WaitingForWork,TravellingToHome,Working);
   type WORKER;
   task type WorkerTask  ( work_ptr : access WORKER ; model_ptr : access model.Simulation_Model) is
      entry acceptTask( stat_id : in Positive );
      entry startTask( stat_id : in Positive ; work_time_hours : in Float);
      entry trainStop ( train_id : in Positive ; stat_id : in Positive );
      entry notifyAboutTrainArrival(stat_id : in Positive ; train_id : in Positive);

   end WorkerTask;

   type WORKER is
      record
         id : Positive;
         home_stat_id : Positive;
         on_train : Natural := 0;
         on_Station : Natural;
         dest_Station : Natural := 0;
         connectionlist : access model.Connection_Arr := null;
         connectionlist_iterator : Natural := 0;
         w_task  : access WorkerTask;
         state : WorkerState := AtHome;
      end record;

   type AWORKER is access all WORKER;

   function Hash(Key : Positive) return Hash_Type;

   function Hash (Key : AWORKER) return Ada.Containers.Hash_Type;
   --function "="(Left : AWORKER ; Right : AWORKER) return Boolean;

   package IntSet is new Ada.Containers.Hashed_Sets(Element_Type => Positive ,Hash =>  hash ,Equivalent_Elements => "=" , "=" => "=" );
   package HashSet is new Ada.Containers.Hashed_Sets(Element_Type => AWORKER ,Hash =>  Hash ,Equivalent_Elements => "=" , "=" => "=" );

end worker;
