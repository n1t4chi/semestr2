limited with model;
with Ada.Containers.Vectors;
with Ada.Strings.Unbounded;
with Ada.Real_Time;

--@Author: Piotr Olejarz 220398
--Steering package declares record and task for steerings
package steering is

   type STEERING;


   type Steering_History is --_Record
      record
         train_id : Natural :=0;
         arrival : Ada.Real_Time.Time;
         departure : Ada.Real_Time.Time;
      end record;
  -- type Steering_History is access Steering_History_Record;
   package vec is new Ada.Containers.Vectors(Index_Type => Positive , Element_Type => Steering_History);

   -- Steering task. Allows accepted trains to switch onto their next track.
   task type SteeringTask ( steering_ptr : access STEERING ; model_ptr : access model.Simulation_Model) is
      --accepts given train thus blocking steering for others
      entry acceptTrain( train_id : in Positive);
      --clears out block for other trains after currently blocking train left the steering.
      entry clearAfterTrain( train_id : in Positive) ;

      --service entries
      entry allowServiceTrain( train_id : in Positive) ;
      entry acceptServiceTrain( train_id : in Positive);
      entry repair( train_id : in Positive);
      entry freeFromServiceTrain ( train_id : in Positive ) ;


      entry breakSelect;
   end SteeringTask;

   --Steering record.
   type STEERING is
      record
         id : Positive;
         min_delay : Positive :=1;
         used_by : Natural  :=0;


         out_of_order : Boolean := False;
         reliability : Float := 0.995 ;

         s_task : access SteeringTask :=null;
         --curr_entry : Natural;
         --curr_exit : Natural;
         history : vec.Vector;
      end record;


end steering;
