with Model; use Model;
with train;
with steering;
with track;
with Ada.Real_Time ;

--@Author: Piotr Olejarz 220398
--Package log is used for output and logging purposes. All output should be redirected here so it will be processed correspondingly with simulation options.
package Log is


   type Time is
      record
         day : Integer;
         hour : Natural;
         minute : Natural;
      end record;
   function toString(t : Time) return String;
   function getRelativeTime(current_time : Ada.Real_Time.Time ; model_ptr : access Simulation_Model) return Time;

   --puts new line
   procedure putLine(line : String);
   --prints new line depending on mode
   procedure putLine(line : String ; model_ptr : access Simulation_Model);

   --prints model based on mode
   procedure printModel(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);
   --prints model
   procedure printModel(model_ptr : access Simulation_Model);

   --prints track locations based on mode
   procedure printTrainLocations(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);
   --prints train locations
   procedure printTrainLocations(model_ptr : access Simulation_Model);

   --prints given train status status based on mode
   procedure printTrainStatus(train_id : Positive;model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);
   --prints given train status status
   procedure printTrainStatus(train_id : Positive;model_ptr : access Simulation_Model);
   --prints given train status based on mode
   procedure printTrainStatus(train_ptr : access train.TRAIN; talking_mode : model.Simulation_Mode);
   --prints given train status
   procedure printTrainStatus(train_ptr : access train.TRAIN);

   --prints given steering status based on mode
   procedure printSteeringStatus(steer_id : Positive;model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);
   --prints given steering status
   procedure printSteeringStatus(steer_id : Positive;model_ptr : access Simulation_Model);
   --prints given steering status based on mode
   procedure printSteeringStatus(steer_ptr : access steering.STEERING; talking_mode : model.Simulation_Mode);
   --prints given steering status
   procedure printSteeringStatus(steer_ptr : access steering.STEERING);

   --prints given track status based on mode
   procedure printTrackStatus(track_id : Positive;model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);
   --prints given track status
   procedure printTrackStatus(track_id : Positive;model_ptr : access Simulation_Model);
   --prints given track status based on mode
   procedure printTrackStatus(track_ptr : access track.TRACK; talking_mode : model.Simulation_Mode);
   --prints given track status
   procedure printTrackStatus(track_ptr : access track.TRACK);



   --prints stations
   procedure printStations(model_ptr : access Simulation_Model);
   --prints stations based on mode
   procedure printStations(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);

   --prints stations
   procedure printWorkers(model_ptr : access Simulation_Model);
   --prints stations based on mode
   procedure printWorkers(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);

   --prints trains
   procedure printTrains(model_ptr : access Simulation_Model);
   --prints tracks based on mode
   procedure printTrains(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);
   --prints tracks
   procedure printTracks(model_ptr : access Simulation_Model);
   --prints tracks based on mode
   procedure printTracks(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);
   --prints steerings
   procedure printSteerings(model_ptr : access Simulation_Model);
   --prints steerings based on mode
   procedure printSteerings(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode);
   --prints timetable for given train
   procedure printTrainTimetable(id : Positive;model_ptr : access Simulation_Model);
   --prints timetable for given track
   procedure printTrackTimetable(id : Positive;model_ptr : access Simulation_Model);



end Log;
