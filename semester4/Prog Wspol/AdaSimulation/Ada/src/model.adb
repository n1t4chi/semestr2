with Ada.Strings.Unbounded;
with Ada.Text_IO;
with track;
use track;
with Ada.Real_Time;
use Ada.Real_Time;
--@Author: Piotr Olejarz 220398
--Package model has Simulation_Model record declaration which contains all necessery data for simulation to work
--Also has methods used to get objects like tracks from it's ID and string representations of those objects
package body Model is
   procedure endSimulation(model_ptr : access Simulation_Model) is
   begin
      model_ptr.work := false;
      if model_ptr /= null then
         for it in model_ptr.track'Range loop
            model_ptr.track(it).t_task.breakSelect;
         end loop;
         for it in model_ptr.steer'Range loop
            model_ptr.steer(it).s_task.breakSelect;
         end loop;
         for it in model_ptr.train'Range loop
            model_ptr.train(it).t_task.breakSelect;
         end loop;

      end if;
   end;




   --returns train with given ID
   function getTrain(train_id : Positive ; model_ptr : access Simulation_Model) return access train.TRAIN is
      train_ptr : access train.TRAIN;
   begin
      if model_ptr /= null then
         for it in model_ptr.train'Range loop
            train_ptr := model_ptr.train(it);
            if train_ptr /= null and then train_ptr.id = train_id then
               return train_ptr;
            end if;
         end loop;
      end if;
      return null;
   end getTrain;

   --returns track with given ID
   function getTrack(track_id : Positive ; model_ptr : access Simulation_Model) return access track.TRACK is
      track_ptr : access track.TRACK;
   begin
      if model_ptr /= null then
         for it in model_ptr.track'Range loop
            track_ptr := model_ptr.track(it);
            if track_ptr /= null and then track_ptr.id = track_id then
               return track_ptr;
            end if;
         end loop;
      end if;
      return null;
   end getTrack;

   --returns next track for given train
   function getNextTrack(train_ptr : access train.TRAIN ; model_ptr : access Simulation_Model) return access track.TRACK is
   begin
      if train_ptr /= null then
         return getTrack(track_id => train_ptr.tracklist(1+(train_ptr.track_it mod train_ptr.tracklist'Length)) , model_ptr => model_ptr);
      end if;
      return null;
   end getNextTrack;

   --returns steering with given ID
   function getSteering(steering_id : Positive ; model_ptr : access Simulation_Model) return access steering.STEERING is
      steering_ptr : access steering.STEERING;
   begin
      if model_ptr /= null then
         for it in model_ptr.steer'Range loop
            steering_ptr := model_ptr.steer(it);
            if steering_ptr /= null and then steering_ptr.id = steering_id then
               return steering_ptr;
            end if;
         end loop;
      end if;
      return null;
   end getSteering;

   --returns steering at either start of end of given track
   function getSteering(track_ptr : access track.TRACK;end_of_track : Boolean ; model_ptr : access Simulation_Model) return access steering.STEERING is
   begin
      if track_ptr /= null then
         if end_of_track then
            return getSteering(steering_id =>  track_ptr.st_end , model_ptr => model_ptr);
         else
            return getSteering(steering_id =>  track_ptr.st_start , model_ptr => model_ptr);
         end if;
      end if;
      return null;
   end getSteering;

   -- String representation of given track
   function toString(track_ptr : access track.TRACK) return String is
   begin
      if track_ptr /= null then
         if track_ptr.t_type = track.Track_Type_Track then
            if track_ptr.used_by = 0 then
               return "track id:" & Positive'Image(track_ptr.id) & " , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end) & "]) , not used , speed:" & Positive'Image(track_ptr.max_speed) & "kmph , dist:" & Positive'Image(track_ptr.distance) & "km" ;
           else
               return "track id:" & Positive'Image(track_ptr.id) & " , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end) & "]) , used by train[" & Natural'Image(track_ptr.used_by) & "] , speed:" & Positive'Image(track_ptr.max_speed) & "kmph , dist:" & Positive'Image(track_ptr.distance) & "km" ;
            end if;

         else
            if track_ptr.used_by = 0 then
               return "platform id:" & Positive'Image(track_ptr.id) & " , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end) & "]) , not used , delay:" & Positive'Image(track_ptr.min_delay) & "min" ;
            else
               return "platform id:" & Positive'Image(track_ptr.id) & " , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end) & "]) , used by train[" & Natural'Image(track_ptr.used_by) & "] , delay:" & Positive'Image(track_ptr.min_delay) & "min" ;
            end if;
         end if;
      else
         return "null";
      end if;
   end toString;

   -- String representation of given train
   function toString(train_ptr : access train.TRAIN) return String is
      track_list : Ada.Strings.Unbounded.Unbounded_String;
      pos : Ada.Strings.Unbounded.Unbounded_String;
   begin
      if train_ptr /= null then
         track_list := Ada.Strings.Unbounded.To_Unbounded_String("");
         for it in train_ptr.tracklist'Range loop
            if Ada.Strings.Unbounded.To_String(track_list) /= "" then
               track_list := Ada.Strings.Unbounded.To_Unbounded_String(Ada.Strings.Unbounded.To_String(track_list)  & "," &  Integer'Image(train_ptr.tracklist(it)));
            else
               track_list := Ada.Strings.Unbounded.To_Unbounded_String(Integer'Image(train_ptr.tracklist(it)));
            end if;
         end loop;

         if train_ptr.on_track /= 0 then
            pos := Ada.Strings.Unbounded.To_Unbounded_String("on track["&Positive'Image(train_ptr.on_track)&"]");
         elsif train_ptr.on_steer /= 0 then
            pos := Ada.Strings.Unbounded.To_Unbounded_String("on steering["&Positive'Image(train_ptr.on_steer)&"]");
         else
            pos := Ada.Strings.Unbounded.To_Unbounded_String("nowhere");
         end if;

         return "train id:" & Positive'Image(train_ptr.id)
           &" , location: " & Ada.Strings.Unbounded.To_String(pos)
           & " , max spd:" & Positive'Image(train_ptr.max_speed)
           & "kmph , curr spd:" & Positive'Image(train_ptr.current_speed)
           & "kmph , cap:" & Positive'Image(train_ptr.capacity)
           & " , tracklist(at "&Natural'Image(train_ptr.track_it)&"): {" & Ada.Strings.Unbounded.To_String(track_list)&"}"
         ;
      else
         return "null";
      end if;
   end toString;

   -- String representation of given steering
   function toString(steer_ptr : access steering.STEERING) return String is
   begin
      if steer_ptr /= null then
         if steer_ptr.used_by = 0 then
            return "Steering id:" & Positive'Image(steer_ptr.id) & " , delay:" & Positive'Image(steer_ptr.min_delay) & "min , not used" ;
         else
            return "Steering id:" & Positive'Image(steer_ptr.id) & " , delay:" & Positive'Image(steer_ptr.min_delay) & "min , used by train[" & Natural'Image(steer_ptr.used_by)&"]";
         end if;

      else
         return "null";
      end if;
   end toString;




   -- Translates given simulation time to real time seconds
   function getTimeSimToReal(time_in_interval : Float ; interval : Time_Interval ; model_ptr : access Simulation_Model) return Float is
      --speed : Positive; -- real-time seconds to simulation-hour ratio
   begin
      if model_ptr /= null then
         return getTimeSimToReal(time_in_interval,interval,model_ptr.speed);
      else
         Ada.Text_IO.Put_Line("getTrain received null pointer. Returning 1");
         return 1.0;
      end if;

   end;
   -- Translates given simulation time to real time seconds
   function getTimeSimToReal(time_in_interval : Float ; interval : Time_Interval ; speed : Positive) return Float is
      --speed : Positive; -- real-time seconds to simulation-hour ratio
   begin
      if interval = Time_Interval_Hour then
         return time_in_interval*Float(speed);
      elsif interval = Time_Interval_Minute then
         return time_in_interval*Float(speed)/60.0;
      else
         Ada.Text_IO.Put_Line("getTrain received illegal interval. Returning 1");
         return 1.0;
      end if;
   end;




end Model;
