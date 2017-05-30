limited with model;
with Ada.Containers.Vectors;
with Ada.Strings.Unbounded;
with Ada.Real_Time;
--@Author: Piotr Olejarz 220398
--Track package declares record and task for tracks
package track is

   -- type of a track
   type Track_Type is (Track_Type_Track,Track_Type_Platform,Track_Type_Service);
   type TRACK (t_type : Track_Type);


   type Track_History is -- _Record
      record
         train_id : Natural :=0;
         arrival : Ada.Real_Time.Time;
         departure : Ada.Real_Time.Time;
      end record;
   --type Track_History is access Track_History_Record;

   package vec is new Ada.Containers.Vectors(Index_Type => Positive , Element_Type => Track_History);


   -- Track task. Allows accepted trains to switch onto their next track.
   task type TrackTask  ( track_ptr : access TRACK ; model_ptr : access model.Simulation_Model) is
      --accepts given train thus blocking track for others
      entry acceptTrain( train_id : in Positive);
      --clears out block for other trains after currently blocking train left the track.
      entry clearAfterTrain( train_id : in Positive) ;

      --service entries
      entry allowServiceTrain( train_id : in Positive) ;
      entry acceptServiceTrain( train_id : in Positive);
      entry repair( train_id : in Positive);
      entry freeFromServiceTrain ( train_id : in Positive ) ;

      entry breakSelect;
   end TrackTask;


   --track record.
   type TRACK (t_type : Track_Type) is
      record
         id : Positive;
         --id of steeering which is start of this track
         st_start : Positive;
         --id of steeering which is end of this track
         st_end : Positive;


         used_by : Natural :=0;

         out_of_order : Boolean := False;
         reliability : Float := 0.995 ;



         history : vec.Vector;
         t_task : access TrackTask :=null;
         case t_type is
            when Track_Type_Track =>
               distance : Positive :=1;
               max_speed: Positive :=1;
            when Track_Type_Platform =>
               min_delay : Positive :=1;
            when Track_Type_Service =>
               null;
         end case;
      end record;

end track;
