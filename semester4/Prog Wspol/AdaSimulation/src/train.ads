limited with  model;
with Ada.Containers.Vectors;
with Ada.Strings.Unbounded;
with Ada.Real_Time;
with worker; use worker;
--@Author: Piotr Olejarz 220398
--Train package declares record and task for trains
package train is

   type Train_Type is (Train_Type_Normal,Train_Type_Service);
   type TRAIN (t_type:Train_Type ) ; --  Operand : Positive


   -- Train task. Cycles thourgh its tracklist and moves from track to steering to track and so on.
   task type TrainTask ( train_ptr : access TRAIN ; model_ptr : access model.Simulation_Model) is
      --notification from steering that train can move further
      entry trainReadyToDepartFromSteering(steer_id : in Positive);
      --notification from platform that train can move further
      entry trainReadyToDepartFromPlatform(track_id : in Positive);
      --notification from track that train can move further
      entry trainArrivedToTheEndOfTrack(track_id : in Positive);



      --notification for service train from track for help
      entry trackOutOfOrder(track_id : in Positive);
      --notification for service train from platform for help
      entry trainOutOfOrder(train_id : in Positive);
      --notification for service train from train for help
      entry steeringOutOfOrder(steer_id : in Positive);


      entry leaveTrain(worker_id : in Positive);
      entry enterTrain(worker_id : in Positive);

      --service entries
      entry repair( train_id : in Positive);


      entry breakSelect;
   end TrainTask;
   -- Track list array
   type TRACK_ARRAY is array(Positive range <>) of Integer;
   --Train record.

   type Train_History_Object_Type is  (type_Track,type_Platform,type_Steering,type_Train,type_Service,type_unknown);

   type Train_History is --_Record
      record
         arrival : Ada.Real_Time.Time;
         departure : Ada.Real_Time.Time;
         object_id : Natural :=0;
         object_type : Train_History_Object_Type := type_unknown;
      end record;

  -- type Train_History is access Train_History_Record;
   package vec is new Ada.Containers.Vectors(Index_Type => Positive , Element_Type => Train_History);



   type TRAIN (t_type:Train_Type ) is -- ; Operand : Positive
      record
         id : Positive;
         max_speed : Positive :=1;

         on_track : Natural :=0;
         on_steer : Natural :=0;
         current_speed : Natural :=0;

         out_of_order : Boolean := False;
         reliability : Float := 0.99995 ;

         t_task : access TrainTask :=null;
         history : vec.Vector;
         tracklist : access TRACK_ARRAY := null;


         track_it : Positive :=1;
         case t_type is
            when Train_Type_Normal =>
               passengers : HashSet.Set;
               capacity : Positive :=1;
               stationlist : access TRACK_ARRAY := null;
               uniqueStations : Natural := 0;
               --track iterator
               --tracklist : TRACK_ARRAY(1..Operand);
            when Train_Type_Service =>
               --tracklist : access TRACK_ARRAY;
               service_track : Positive;
               going_back : Boolean := true;
         end case;
     end record;

   function createStationList(train_ptr : access Train ; model_ptr : access model.Simulation_Model) return access TRACK_ARRAY;
  -- procedure moveTrainToNextTrack ( train_ptr : access TRAIN ; model_ptr : access model.Simulation_Model);


end train;
