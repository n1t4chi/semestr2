limited with  model;
with Ada.Containers.Vectors;
with Ada.Strings.Unbounded;
with Ada.Real_Time;
--@Author: Piotr Olejarz 220398
--Train package declares record and task for trains
package train is
   type TRAIN;


   -- Train task. Cycles thourgh its tracklist and moves from track to steering to track and so on.
   task type TrainTask ( train_ptr : access TRAIN ; model_ptr : access model.Simulation_Model) is
      --notification from steering that train can move further
      entry trainReadyToDepartFromSteering(steer_id : in Positive);
      --notification from platform that train can move further
      entry trainReadyToDepartFromPlatform(track_id : in Positive);
      --notification from track that train can move further
      entry trainArrivedToTheEndOfTrack(track_id : in Positive);
      entry breakSelect;
   end TrainTask;
   -- Track list array
   type TRACK_ARRAY is array(Positive range <>) of Integer;
   --Train record.

   type Train_History_Object_Type is  (type_Track,type_Platform,type_Steering,type_unknown);

   type Train_History is --_Record
      record
         arrival : Ada.Real_Time.Time;
         departure : Ada.Real_Time.Time;
         object_id : Natural :=0;
         object_type : Train_History_Object_Type := type_unknown;
      end record;

  -- type Train_History is access Train_History_Record;
   package vec is new Ada.Containers.Vectors(Index_Type => Positive , Element_Type => Train_History);



   type TRAIN (Track_Length : Positive) is
      record
         id : Positive;
         max_speed : Positive :=1;
         capacity : Positive :=1;
         --track iterator
         track_it : Positive :=1;

         on_track : Natural :=0;
         on_steer : Natural :=0;


         current_speed : Natural :=0;
         t_task : access TrainTask :=null;
         --current_distance : Float;
         tracklist : TRACK_ARRAY(1..Track_Length);
         history : vec.Vector;
     end record;


  -- procedure moveTrainToNextTrack ( train_ptr : access TRAIN ; model_ptr : access model.Simulation_Model);


end train;
