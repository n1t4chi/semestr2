with Model; use Model;
with Ada.Text_IO;
with log; use log;
with Ada.Strings;
with Ada.Strings.Fixed;
with Ada.Calendar.Formatting;
with train; use train;
with Ada.Real_Time ; use Ada.Real_Time;
--@Author: Piotr Olejarz 220398
--Package log is used for output and logging purposes. All output should be redirected here so it will be processed correspondingly with simulation options.
package body Log is


   --prints new line
   procedure putLine(line : String) is
   begin
      if Ada.Strings.Fixed.Head(line,3) = "#3#" then
         --Ada.Text_IO.Put_Line(line);
         Ada.Text_IO.Put_Line(Ada.Calendar.Formatting.Image(Ada.Calendar.Clock)&":"&line);
      else
         null;
      end if;
   end putLine;


   --prints new line depending on mode
   procedure putLine(line : String ; model_ptr : access Simulation_Model) is
   begin
      --Ada.Text_IO.Put_Line("test");
      if model_ptr /= null and then model_ptr.mode = Talking_Mode then
         putLine(line);
 --     elsif model_ptr = null then
 --        Ada.Text_IO.Put_Line("null ptr");
 --     elsif model_ptr.mode /= Talking_Mode then
  --       if model_ptr.mode = Silent_Mode then
   --         Ada.Text_IO.Put_Line("silent mode");
    --     else
     --       Ada.Text_IO.Put_Line("it's bucked");
      --   end if;

      end if;

   end putLine;



   --prints model based on mode
   procedure printModel(model_ptr : access Simulation_Model; talking_mode : Model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printModel(model_ptr);
      end if;

   end printModel;

   --prints model
   procedure printModel(model_ptr : access Simulation_Model) is
   begin
      if model_ptr /= null then
         Ada.Text_IO.Put_Line("Simulation speed: " & Positive'Image(model_ptr.speed) & "s -> 1h");
         printSteerings(model_ptr);
         printStations(model_ptr);
         printTracks(model_ptr);
         printTrains(model_ptr);
         printWorkers(model_ptr);
      end if;
   end printModel;


   --prints steerings based on mode
   procedure printSteerings(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printSteerings(model_ptr);
      end if;

   end printSteerings;

   --prints steerings
   procedure printSteerings(model_ptr : access Simulation_Model) is
   begin
      if model_ptr /= null then
         for it in model_ptr.steer'Range loop
            Ada.Text_IO.Put_Line(Model.toString(model_ptr.steer(it)));
         end loop;
      end if;
   end printSteerings;

   --prints tracks based on mode
   procedure printTracks(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printTracks(model_ptr);
      end if;

   end printTracks;
   --prints tracks
   procedure printTracks(model_ptr : access Simulation_Model) is
   begin
      if model_ptr /= null then
         for it in model_ptr.track'Range loop
            Ada.Text_IO.Put_Line(Model.toString(model_ptr.track(it)));
         end loop;
      end if;
   end printTracks;
   --prints trains based on mode
   procedure printTrains(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printTrains(model_ptr);
      end if;

   end printTrains;

   --prints trains
   procedure printTrains(model_ptr : access Simulation_Model) is
   begin
      if model_ptr /= null then
         for it in model_ptr.train'Range loop
            Ada.Text_IO.Put_Line(Model.toString(model_ptr.train(it)));
         end loop;
      end if;
   end printTrains;



   --prints train locations based on mode
   procedure printTrainLocations(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printTrainLocations(model_ptr);
      end if;

   end printTrainLocations;

   --prints train locations
   procedure printTrainLocations(model_ptr : access Simulation_Model) is
      c_t : access train.TRAIN;
   begin
      if model_ptr /= null then
         for it in model_ptr.train'Range loop
            c_t := model_ptr.train(it);
            if c_t.on_track /= 0 then
               Ada.Text_IO.Put_Line( Positive'Image(c_t.id) & " is at track: " & Positive'Image(c_t.on_track) & " and moves at " & Positive'Image(c_t.current_speed) & "kmph" );
            else
               Ada.Text_IO.Put_Line( Positive'Image(c_t.id) & " is at steering: " & Positive'Image(c_t.on_steer) &  "and moves at " & Positive'Image(c_t.current_speed) & "kmph" );
            end if;

         end loop;
      end if;
   end printTrainLocations;

 --prints stations based on mode
   procedure printWorkers(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printWorkers(model_ptr);
      end if;

   end printWorkers;

   --
   procedure printWorkers(model_ptr : access Simulation_Model) is
   begin
      if model_ptr /= null then
         for it in model_ptr.worker'Range loop
            Ada.Text_IO.Put_Line(Model.toString(model_ptr.worker(it)));
         end loop;
      end if;
   end printWorkers;


   --prints stations based on mode
   procedure printStations(model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printStations(model_ptr);
      end if;

   end printStations;

   --
   procedure printStations(model_ptr : access Simulation_Model) is
   begin
      if model_ptr /= null then
         for it in model_ptr.station'Range loop
            Ada.Text_IO.Put_Line(Model.toString(model_ptr.station(it)));
         end loop;
      end if;
   end printStations;


   --prints given train status status based on mode
   procedure printTrainStatus(train_id : Positive;model_ptr : access Simulation_Model) is
   begin
      printTrainStatus(model.getTrain(train_id ,model_ptr));
   end printTrainStatus;

   --prints given train status status
   procedure printTrainStatus(train_id : Positive;model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printTrainStatus(train_id,model_ptr);
      end if;
   end printTrainStatus;


   --prints given train status status based on mode
   procedure printTrainStatus(train_ptr : access train.TRAIN; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printTrainStatus(train_ptr);
      end if;

   end printTrainStatus;

   --prints given train status status
   procedure printTrainStatus(train_ptr : access train.TRAIN) is
   begin
      Ada.Text_IO.Put_Line(Model.toString(train_ptr));
   end printTrainStatus;



   --prints given steering status based on mode
   procedure printSteeringStatus(steer_id : Positive;model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printSteeringStatus(steer_id,model_ptr);
      end if;

   end printSteeringStatus;

   --prints given steering status
   procedure printSteeringStatus(steer_id : Positive;model_ptr : access Simulation_Model) is
   begin
      printSteeringStatus(model.getSteering(steer_id ,model_ptr));
   end printSteeringStatus;


   --prints given steering status based on mode
   procedure printSteeringStatus(steer_ptr : access steering.STEERING; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printSteeringStatus(steer_ptr);
      end if;

   end printSteeringStatus;

   --prints given steering status
   procedure printSteeringStatus(steer_ptr : access steering.STEERING) is
   begin
      Ada.Text_IO.Put_Line(Model.toString(steer_ptr));
   end printSteeringStatus;



   --prints given track status based on mode
   procedure printTrackStatus(track_id : Positive;model_ptr : access Simulation_Model; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printTrackStatus(track_id,model_ptr);
      end if;

   end printTrackStatus;

   --prints given track status
   procedure printTrackStatus(track_id : Positive;model_ptr : access Simulation_Model) is
   begin
      printTrackStatus(model.getTrack(track_id,model_ptr));
   end printTrackStatus;


   --prints given track status based on mode
   procedure printTrackStatus(track_ptr : access track.TRACK; talking_mode : model.Simulation_Mode) is
   begin
      if talking_mode = model.Talking_Mode then
         printTrackStatus(track_ptr);
      end if;

   end printTrackStatus;

   --prints given track status
   procedure printTrackStatus(track_ptr : access track.TRACK) is
   begin
      Ada.Text_IO.Put_Line(Model.toString(track_ptr));
   end printTrackStatus;


   function getRelativeTime(current_time : Ada.Real_Time.Time ; model_ptr : access Simulation_Model) return Time is
      t : Time;
    --  sc : Ada.Real_Time.Seconds_Count;
     -- dur : Standard.Duration;
     -- ts : Ada.Real_Time.Time_Span;
      sim_time : float;
      -- time : Ada.Real_Time.Time_Span;
      min : integer;
   begin
      --time := current_time-model_ptr.start_time;

      --Ada.Real_Time.Split(T => time,SC => sc,TS => ts);
     -- dur := Ada.Real_Time.To_Duration(current_time-model_ptr.start_time);
      --Float(Natural'Value(Seconds_Count'Image(sc))) +
      sim_time :=  Float'Value(Duration'Image(Ada.Real_Time.To_Duration(current_time-model_ptr.start_time))) / Float(model_ptr.speed);

      t.day := Integer(sim_time)/24;
      t.hour := Integer(sim_time) mod 24;
      --ada.Text_IO.Put_Line("&&&:"&Float'Image(Float'Fraction(sim_time)));
      min := Integer((Float'Fraction(sim_time))*60.0);
      if min < 0 then
         t.minute := 60+min;
         t.hour := t.hour -1;
      else
         t.minute := min;
      end if;

      return t;
   end;
   function toString(t : Time) return String is
   begin
     -- if t.day /= 0 then
       --  return Positive'Image(t.day)&"d "&Natural'Image(t.hour)&":"&Natural'Image(t.minute);
      --else
         return "+"&Positive'Image(t.day)&"d "&Natural'Image(t.hour)&"h "&Natural'Image(t.minute)&"m";
      --end if;

   end;


   --prints timetable for given train
   procedure printTrainTimetable(id : Positive;model_ptr : access Simulation_Model) is
      train_ptr : access train.TRAIN;
      c : train.vec.Cursor;
      th : train.Train_History;
      t_arr : Time;
      t_dep : Time;
   begin
      if model_ptr /= null then
         train_ptr := model.getTrain(id,model_ptr);
         if train_ptr /= null then
            Ada.Text_IO.Put_Line("Timetable for train:"&Positive'Image(train_ptr.id));
            Ada.Text_IO.Put_Line("platform"&ASCII.HT&"arrival"&ASCII.HT&"departure");
            c := train.vec.First(Container => train_ptr.history);
            while train.vec.Has_Element(c) loop
               th := train.vec.Element(c);
                if th.object_type = train.type_Platform then

                  t_arr := getRelativeTime(th.arrival,model_ptr);
                  t_dep := getRelativeTime(th.departure,model_ptr);
                  Ada.Text_IO.Put_Line(Positive'Image(th.object_id)&ASCII.HT&toString(t_arr)&ASCII.HT&toString(t_dep));

               end if;
               train.vec.Next(Position => c);
            end loop;
         else
            Ada.Text_IO.Put_Line("Train not found!");
         end if;
      else
         Ada.Text_IO.Put_Line("Null model!");
      end if;
   end;
   --prints timetable for given track
   procedure printTrackTimetable(id : Positive;model_ptr : access Simulation_Model) is
      track_ptr : access track.TRACK;
      c : track.vec.Cursor;
      th : track.Track_History;
      t_arr : Time;
      t_dep : Time;
   begin
      if model_ptr /= null then
         track_ptr := model.getTrack(id,model_ptr);
         if track_ptr /= null then
            Ada.Text_IO.Put_Line("Timetable for track:"&Positive'Image(track_ptr.id));
            Ada.Text_IO.Put_Line("train"&ASCII.HT&"arrival"&ASCII.HT&"departure");
            c := track.vec.First(Container => track_ptr.history);
            while track.vec.Has_Element(c) loop
               th := track.vec.Element(c);
               t_arr := getRelativeTime(th.arrival,model_ptr);
               t_dep := getRelativeTime(th.departure,model_ptr);
               Ada.Text_IO.Put_Line(Positive'Image(th.train_id)&ASCII.HT&toString(t_arr)&ASCII.HT&toString(t_dep));
               track.vec.Next(Position => c);
            end loop;
         else
            Ada.Text_IO.Put_Line("Track not found!");
         end if;
      else
         Ada.Text_IO.Put_Line("Null model!");
      end if;

   end;
end Log;
