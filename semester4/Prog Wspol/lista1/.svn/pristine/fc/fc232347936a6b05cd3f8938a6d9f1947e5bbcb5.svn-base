with steering;
with train;
with track;
with Ada.Real_Time;
--@Author: Piotr Olejarz 220398
--Package model has Simulation_Model record declaration which contains all necessery data for simulation to work
--Also has methods used to get objects like tracks from it's ID and string representations of those objects
package Model is


   --Array of tracks
   type TRACK_ARR is array(Positive range <>) of access track.TRACK;
   --Array of steerings
   type STEERING_ARR is array(Positive range <>) of access steering.STEERING;
   --Array of trains
   type TRAIN_ARR is array(Positive range <>) of access Train.TRAIN;

   type Simulation_Mode is (Silent_Mode,Talking_Mode);
   --Simulation record
   type Simulation_Model (Steer_Length : Positive;Track_Length : Positive;Train_Length : Positive) is
      record
         -- real-time seconds to simulation-hour ratio
         speed : Positive :=1;
         -- starting time of simulation
         start_time : Ada.Real_Time.Time;
         mode : Simulation_Mode :=Talking_Mode;
         steer : STEERING_ARR(1..Steer_Length);
         track : TRACK_ARR(1..Track_Length);
         train : TRAIN_ARR(1..Train_Length);
         --indicates whether the simulation should work or not.
         work : Boolean := True;
      end record;

   --procedure moveTrainToNextTrack (train_ptr : access TRAIN;speed : Natural);

   --returns train with given ID
   function getTrain(train_id : Positive ; model_ptr : access Simulation_Model) return access train.TRAIN;

   --returns track with given ID
   function getTrack(track_id : Positive ; model_ptr : access Simulation_Model) return access track.TRACK;
   --returns next track for given train
   function getNextTrack(train_ptr : access train.TRAIN ; model_ptr : access Simulation_Model) return access track.TRACK;

   --returns steering with given ID
   function getSteering(steering_id : Positive ; model_ptr : access Simulation_Model) return access steering.STEERING;
   --returns steering at either start of end of given track
   function getSteering(track_ptr : access track.TRACK;end_of_track : Boolean ; model_ptr : access Simulation_Model) return access steering.STEERING;

   -- String representation of given track
   function toString(track_ptr : access track.TRACK) return String;
   -- String representation of given train
   function toString(train_ptr : access train.TRAIN) return String;
   -- String representation of given steering
   function toString(steer_ptr : access steering.STEERING) return String;


   -- Simulation time interval (Minute or Hour)
   type Time_Interval is (Time_Interval_Minute , Time_Interval_Hour);
   -- Translates given simulation time to real time seconds
   function getTimeSimToReal(time_in_interval : Float ; interval : Time_Interval ; speed : Positive) return Float;
   -- Translates given simulation time to real time seconds
   function getTimeSimToReal(time_in_interval : Float ; interval : Time_Interval ; model_ptr : access Simulation_Model) return Float;

   procedure endSimulation(model_ptr : access Simulation_Model);
end Model;
