with Ada.Text_IO;
with track; use track;
with train; use train;
with Ada.Real_Time;
use Ada.Real_Time;
with Ada.Strings;
with Ada.Strings.Unbounded;
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
   function getServiceTrain(model_ptr : access Simulation_Model) return access train.TRAIN is
      train_ptr : access train.TRAIN;
      service_ptr : access train.TRAIN := null;
   begin
      if model_ptr /= null then
         for it in model_ptr.train'Range loop
            train_ptr := model_ptr.train(it);
            if train_ptr /= null and then train_ptr.t_type = Train_Type_Service then
               service_ptr := train_ptr;
               if service_ptr.going_back = true then
                  return service_ptr;
               end if;
            end if;
         end loop;
      end if;
      return service_ptr;
   end getServiceTrain;


   function getWorker(work_id : Positive ; model_ptr : access Simulation_Model) return access worker.WORKER is
      work_ptr : access worker.WORKER;
   begin
      if model_ptr /= null then
         for it in model_ptr.worker'Range loop
            work_ptr := model_ptr.worker(it);
            if work_ptr /= null and then work_ptr.id = work_id then
               return work_ptr;
            end if;
         end loop;
      end if;
      return null;
   end getWorker;

   function getStation(stat_id : Positive ; model_ptr : access Simulation_Model) return access station.STATION is
      stat_ptr : access station.STATION;
   begin
      if model_ptr /= null then
         for it in model_ptr.station'Range loop
            stat_ptr := model_ptr.station(it);
            if stat_ptr /= null and then stat_ptr.id = stat_id then
               return stat_ptr;
            end if;
         end loop;
      end if;
      return null;
   end getStation;

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
               return "track id:" & Positive'Image(track_ptr.id)
                 & " , out of order: " & Boolean'Image(track_ptr.out_of_order)
                 & "  , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end)
                 & "]) , not used , speed:" & Positive'Image(track_ptr.max_speed)
                 & "kmph , dist:" & Positive'Image(track_ptr.distance) & "km" ;
           else
               return "track id:" & Positive'Image(track_ptr.id)
                 & " , out of order: " & Boolean'Image(track_ptr.out_of_order)
                 & "  , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end)
                 & "]) , used by train[" & Natural'Image(track_ptr.used_by) & "] , speed:" & Positive'Image(track_ptr.max_speed)
                 & "kmph , dist:" & Positive'Image(track_ptr.distance) & "km" ;
            end if;
         elsif track_ptr.t_type = track.Track_Type_Platform then
            if track_ptr.used_by = 0 then
               return "platform id:" & Positive'Image(track_ptr.id)
                 & " , station: " & Positive'Image(track_ptr.station_id)
                 & " , out of order: " & Boolean'Image(track_ptr.out_of_order)
                 & "  , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end)
                 & "]) , not used , delay:" & Positive'Image(track_ptr.min_delay) & "min" ;
            else
               return "platform id:" & Positive'Image(track_ptr.id)
                 & " , station: " & Positive'Image(track_ptr.station_id)
                 & " , out of order: " & Boolean'Image(track_ptr.out_of_order)
                 & "  , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end)
                 & "]) , used by train[" & Natural'Image(track_ptr.used_by)
                 & "] , delay:" & Positive'Image(track_ptr.min_delay) & "min" ;
            end if;
         else
            if track_ptr.used_by = 0 then
               return "platform id:" & Positive'Image(track_ptr.id)
                 & "  , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end)
                 & "]) , not used" ;
            else
               return "service track id:" & Positive'Image(track_ptr.id)
                 & "  , steerings:([" & Positive'Image(track_ptr.st_start) & "],[" & Positive'Image(track_ptr.st_end)
                 & "]) , used by train[" & Natural'Image(track_ptr.used_by) & "]";
            end if;
         end if;
      else
         return "null";
      end if;
   end toString;

   -- String representation of given train
   function toString(train_ptr : access train.TRAIN) return String is
      track_list : Ada.Strings.Unbounded.Unbounded_String;
      station_list : Ada.Strings.Unbounded.Unbounded_String;
      pos : Ada.Strings.Unbounded.Unbounded_String;
      passengers : Ada.Strings.Unbounded.Unbounded_String;

   begin
      if train_ptr /= null then
         if train_ptr.t_type = Train_Type_Normal then
            track_list := Ada.Strings.Unbounded.To_Unbounded_String("");
            for it in train_ptr.tracklist'Range loop
               if Ada.Strings.Unbounded.To_String(track_list) /= "" then
                  track_list := Ada.Strings.Unbounded.To_Unbounded_String(Ada.Strings.Unbounded.To_String(track_list)  & "," &  Integer'Image(train_ptr.tracklist(it)));
               else
                  track_list := Ada.Strings.Unbounded.To_Unbounded_String(Integer'Image(train_ptr.tracklist(it)));
               end if;
            end loop;


            station_list := Ada.Strings.Unbounded.To_Unbounded_String("");
            for it in train_ptr.stationlist'Range loop
               if Ada.Strings.Unbounded.To_String(station_list) /= "" then
                  station_list := Ada.Strings.Unbounded.To_Unbounded_String(Ada.Strings.Unbounded.To_String(station_list)  & "," &  Integer'Image(train_ptr.stationlist(it)));
               else
                  station_list := Ada.Strings.Unbounded.To_Unbounded_String(Integer'Image(train_ptr.stationlist(it)));
               end if;
            end loop;

            if train_ptr.on_track /= 0 then
               pos := Ada.Strings.Unbounded.To_Unbounded_String("on track["&Positive'Image(train_ptr.on_track)&"]");
            elsif train_ptr.on_steer /= 0 then
               pos := Ada.Strings.Unbounded.To_Unbounded_String("on steering["&Positive'Image(train_ptr.on_steer)&"]");
            else
               pos := Ada.Strings.Unbounded.To_Unbounded_String("nowhere");
            end if;

            passengers := Ada.Strings.Unbounded.To_Unbounded_String("");
            for itt in worker.HashSet.Iterate(Container => train_ptr.passengers) loop
               if Ada.Strings.Unbounded.To_String(passengers) /= "" then
                  passengers := Ada.Strings.Unbounded.To_Unbounded_String( Ada.Strings.Unbounded.To_String(passengers) & "," & Positive'Image(worker.HashSet.Element(itt).id)  );
               else
                  passengers := Ada.Strings.Unbounded.To_Unbounded_String(Positive'Image(worker.HashSet.Element(itt).id));
               end if;
            end loop;


            return "train id:" & Positive'Image(train_ptr.id)
              & " , out of order: " & Boolean'Image(train_ptr.out_of_order)
              &" , location: " & Ada.Strings.Unbounded.To_String(pos)
              & " , max spd:" & Positive'Image(train_ptr.max_speed)
              & "kmph , curr spd:" & Positive'Image(train_ptr.current_speed)
              & "kmph , cap:" & Positive'Image(train_ptr.capacity)
              & " , tracklist(at "&Natural'Image(train_ptr.track_it)&"): {" & Ada.Strings.Unbounded.To_String(track_list)&"}"
              & " , stations {" & Ada.Strings.Unbounded.To_String(station_list)&"} uniq:"&Natural'Image(train_ptr.uniqueStations)
              & " passengers: { " & Ada.Strings.Unbounded.To_String(passengers) &" }"
            ;
         else
            if train_ptr.on_track /= 0 then
               pos := Ada.Strings.Unbounded.To_Unbounded_String("on track["&Positive'Image(train_ptr.on_track)&"]");
            elsif train_ptr.on_steer /= 0 then
               pos := Ada.Strings.Unbounded.To_Unbounded_String("on steering["&Positive'Image(train_ptr.on_steer)&"]");
            else
               pos := Ada.Strings.Unbounded.To_Unbounded_String("nowhere");
            end if;


            track_list := Ada.Strings.Unbounded.To_Unbounded_String("");
            if train_ptr.tracklist /= null then
               for it in train_ptr.tracklist'Range loop
                  if Ada.Strings.Unbounded.To_String(track_list) /= "" then
                     track_list := Ada.Strings.Unbounded.To_Unbounded_String(Ada.Strings.Unbounded.To_String(track_list)  & "," &  Integer'Image(train_ptr.tracklist(it)));
                  else
                     track_list := Ada.Strings.Unbounded.To_Unbounded_String(Integer'Image(train_ptr.tracklist(it)));
                  end if;
               end loop;
               track_list := Ada.Strings.Unbounded.To_Unbounded_String(" , tracklist(at "&Natural'Image(train_ptr.track_it)&"): {" & Ada.Strings.Unbounded.To_String(track_list)&"}");
            end if;


            return "service train id:" & Positive'Image(train_ptr.id)
              &" , location: " & Ada.Strings.Unbounded.To_String(pos)
              & " , max spd:" & Positive'Image(train_ptr.max_speed)
              & "kmph , curr spd:" & Positive'Image(train_ptr.current_speed)
              & Ada.Strings.Unbounded.To_String(track_list)
            ;

         end if;

      else
         return "null";
      end if;
   end toString;

   -- String representation of given steering
   function toString(steer_ptr : access steering.STEERING) return String is
   begin
      if steer_ptr /= null then
         if steer_ptr.used_by = 0 then
            return "Steering id:" & Positive'Image(steer_ptr.id)
              & " , out of order: " & Boolean'Image(steer_ptr.out_of_order)
              & " , delay:" & Positive'Image(steer_ptr.min_delay)
              & "min , not used" ;
         else
            return "Steering id:" & Positive'Image(steer_ptr.id)
              & " , out of order: " & Boolean'Image(steer_ptr.out_of_order)
              & " , delay:" & Positive'Image(steer_ptr.min_delay)
              & "min , used by train[" & Natural'Image(steer_ptr.used_by)&"]";
         end if;

      else
         return "null";
      end if;
   end toString;


   function toString(work_ptr : access worker.WORKER) return String is
      connection_list : Ada.Strings.Unbounded.Unbounded_String;
      conn_ent : Ada.Strings.Unbounded.Unbounded_String;
      conn : access Connection;
   --   train_list : Ada.Strings.Unbounded.Unbounded_String;
        -- platf : access TRACK_ARR;
        -- train : access TRAIN_ARR;
   begin
      if work_ptr /= null then
         connection_list := Ada.Strings.Unbounded.To_Unbounded_String("");
         if work_ptr.connectionlist /= null then
            for it in work_ptr.connectionlist'Range loop
               conn := work_ptr.connectionlist(it);
               if conn /= null then
                  conn_ent := Ada.Strings.Unbounded.To_Unbounded_String("(t:"&Positive'Image(conn.train_id)&" ,d:"&Positive'Image(conn.depart_station_id)&" ,a:"&Positive'Image(conn.arrive_station_id) );
                  if Ada.Strings.Unbounded.To_String(connection_list) /= "" then
                     connection_list := Ada.Strings.Unbounded.To_Unbounded_String(Ada.Strings.Unbounded.To_String(connection_list)  & "," & Ada.Strings.Unbounded.To_String(conn_ent) );
                  else
                     connection_list := conn_ent;
                  end if;
               end if;

            end loop;
         end if;

         case work_ptr.state is
            when worker.AtHome =>
               return "Worker id:" & Positive'Image(work_ptr.id) & " , home station:" & Positive'Image(work_ptr.home_stat_id) & " , at home"
                       & " , at stat: " & Positive'Image(work_ptr.on_Station)& " , on train: " & Positive'Image(work_ptr.on_train);
            when worker.TravellingToWork =>
               if work_ptr.on_train /= 0 then
                  return "Worker id:" & Positive'Image(work_ptr.id) & " , home station:" & Positive'Image(work_ptr.home_stat_id)
                       & " , at stat: " & Positive'Image(work_ptr.on_Station)& " , on train: " & Positive'Image(work_ptr.on_train)
                       & " , going to work at station:" & Positive'Image(work_ptr.dest_Station) & " , on train: " & Positive'Image(work_ptr.on_train)
                       & " , connection list: { " & Ada.Strings.Unbounded.To_String(connection_list) & "}"
                     ;
               else
                  return "Worker id:" & Positive'Image(work_ptr.id) & " , home station:" & Positive'Image(work_ptr.home_stat_id)
                       & " , at stat: " & Positive'Image(work_ptr.on_Station)& " , on train: " & Positive'Image(work_ptr.on_train)
                       & " , going to work at station:" & Positive'Image(work_ptr.dest_Station) & " , on station: " & Positive'Image(work_ptr.on_Station)
                       & " , connection list: { " & Ada.Strings.Unbounded.To_String(connection_list) & "}"
                     ;

               end if;
            when worker.WaitingForWork =>
               return "Worker id:" & Positive'Image(work_ptr.id) & " , home station:" & Positive'Image(work_ptr.home_stat_id)
                       & " , at stat: " & Positive'Image(work_ptr.on_Station)& " , on train: " & Positive'Image(work_ptr.on_train)
                 & " , waiting to start work at station: " & Positive'Image(work_ptr.dest_Station);
            when worker.TravellingToHome =>
               if work_ptr.on_train /= 0 then
                  return "Worker id:" & Positive'Image(work_ptr.id) & " , home station:" & Positive'Image(work_ptr.home_stat_id)
                       & " , at stat: " & Positive'Image(work_ptr.on_Station)& " , on train: " & Positive'Image(work_ptr.on_train)
                       & " , going back to home , on train: " & Positive'Image(work_ptr.on_train)
                       & " , connection list: { " & Ada.Strings.Unbounded.To_String(connection_list) & "}"
                     ;
               else
                  return "Worker id:" & Positive'Image(work_ptr.id) & " , home station:" & Positive'Image(work_ptr.home_stat_id)
                       & " , at stat: " & Positive'Image(work_ptr.on_Station)& " , on train: " & Positive'Image(work_ptr.on_train)
                       & " , going back to home , on station: " & Positive'Image(work_ptr.on_Station)
                       & " , connection list: { " & Ada.Strings.Unbounded.To_String(connection_list) & "}"
                     ;
               end if;
            when worker.Working =>
               return "Worker id:" & Positive'Image(work_ptr.id) & " , home station:" & Positive'Image(work_ptr.home_stat_id)
                       & " , at stat: " & Positive'Image(work_ptr.on_Station)& " , on train: " & Positive'Image(work_ptr.on_train)
                 & " , working at station: " & Positive'Image(work_ptr.dest_Station);
         end case;
      else
         return "null";
      end if;
   end toString;

   function toString(stat_ptr : access station.STATION) return String is
      --   platform_list : Ada.Strings.Unbounded.Unbounded_String;
      --   train_list : Ada.Strings.Unbounded.Unbounded_String;
      -- platf : access TRACK_ARR;
      -- train : access TRAIN_ARR;
      passengers : Ada.Strings.Unbounded.Unbounded_String;
      r_work : Ada.Strings.Unbounded.Unbounded_String;
      c_work : Ada.Strings.Unbounded.Unbounded_String;
      trains : Ada.Strings.Unbounded.Unbounded_String;
   begin
      if stat_ptr /= null then

         passengers := Ada.Strings.Unbounded.To_Unbounded_String("");
         for itt in worker.HashSet.Iterate(Container => stat_ptr.passengers) loop
            if Ada.Strings.Unbounded.To_String(passengers) /= "" then
               passengers := Ada.Strings.Unbounded.To_Unbounded_String( Ada.Strings.Unbounded.To_String(passengers) & "," & Positive'Image(worker.HashSet.Element(itt).id)  );
            else
               passengers := Ada.Strings.Unbounded.To_Unbounded_String(Positive'Image(worker.HashSet.Element(itt).id));
            end if;
         end loop;

         r_work := Ada.Strings.Unbounded.To_Unbounded_String("");
         for itt in worker.HashSet.Iterate(Container => stat_ptr.ready_workers) loop
            if Ada.Strings.Unbounded.To_String(r_work) /= "" then
               r_work := Ada.Strings.Unbounded.To_Unbounded_String( Ada.Strings.Unbounded.To_String(r_work) & "," & Positive'Image(worker.HashSet.Element(itt).id)  );
            else
               r_work := Ada.Strings.Unbounded.To_Unbounded_String(Positive'Image(worker.HashSet.Element(itt).id));
            end if;
         end loop;

         c_work := Ada.Strings.Unbounded.To_Unbounded_String("");
         for itt in worker.HashSet.Iterate(Container => stat_ptr.chosen_workers) loop
            if Ada.Strings.Unbounded.To_String(c_work) /= "" then
               c_work := Ada.Strings.Unbounded.To_Unbounded_String( Ada.Strings.Unbounded.To_String(c_work) & "," & Positive'Image(worker.HashSet.Element(itt).id)  );
            else
               c_work := Ada.Strings.Unbounded.To_Unbounded_String(Positive'Image(worker.HashSet.Element(itt).id));
            end if;
         end loop;

         trains := Ada.Strings.Unbounded.To_Unbounded_String("");
         for itt in worker.IntSet.Iterate(Container => stat_ptr.trains) loop
            if Ada.Strings.Unbounded.To_String(trains) /= "" then
               trains := Ada.Strings.Unbounded.To_Unbounded_String( Ada.Strings.Unbounded.To_String(trains) & "," & Positive'Image(worker.IntSet.Element(itt))  );
            else
               trains := Ada.Strings.Unbounded.To_Unbounded_String(Positive'Image(worker.IntSet.Element(itt)));
            end if;
         end loop;

         return "Station id: " & Positive'Image(stat_ptr.id)
           & " trains on station: { " & Ada.Strings.Unbounded.To_String(trains) &" }"
           & " passengers: { " & Ada.Strings.Unbounded.To_String(passengers) &" }"
           & " chosen workers: { " & Ada.Strings.Unbounded.To_String(c_work) &" }"
           & " ready workers: { " & Ada.Strings.Unbounded.To_String(r_work) &" }"

         ;

     --    & "platforms: {" & Ada.Strings.Unbounded.To_String(platform_list) & "}"
     --    & "trains: {" & Ada.Strings.Unbounded.To_String(train_list) & "}" ;


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





   function getConnection(source_station : Positive ; destination_station : Positive ; model_ptr : access Simulation_Model) return access Connection_Arr is
      rtrn : access Connection_Arr;

      depar : access TRAIN_ARR;
      arriv : access TRAIN_ARR;


      src : Positive;
      des : Positive;

      arriv_it : Positive;
      depar_it : Positive;
      rtrn_st_it : Natural := 1;
      found_connection : Boolean := false;
   begin
      if source_station /= destination_station then
         depar := new TRAIN_ARR(1..model_ptr.train'Length);
         arriv := new TRAIN_ARR(1..model_ptr.train'Length);
         src := source_station;
         des := destination_station;

         --while not found_connection and then rtrn_st_it <= rtrn'Length  loop
         arriv_it := 1;
         depar_it := 1;
         for it in depar'Range loop
            depar(it) := null;
         end loop;

         declare
            found_arrv : Boolean;
            found_dep : Boolean;
            max_a_stat : Natural := 0;
            max_a_it : Natural := 0;
            max_d_stat : Natural := 0;
            max_d_it : Natural := 0;
            train_ptr : access train.TRAIN;
         begin
            --find all trains that start from source station
            for it in model_ptr.train'Range loop
               train_ptr:= model_ptr.train(it);
               if train_ptr.t_type = train.Train_Type_Normal then
                  found_arrv := false;
                  found_dep := false;
                  for itt in train_ptr.stationlist'Range loop

                     if train_ptr.stationlist(itt) = src then
                        if max_d_stat <= train_ptr.uniqueStations then
                           max_d_it := itt;
                           max_d_stat := train_ptr.uniqueStations;
                        end if;
                        found_dep := true;
                     end if;

                     if train_ptr.stationlist(itt) = des then
                        if max_a_stat <= train_ptr.uniqueStations then
                           max_a_it := itt;
                           max_a_stat := train_ptr.uniqueStations;
                        end if;
                        found_arrv := true;
                     end if;

                     exit when found_arrv and found_dep;
                  end loop;
                  if found_dep or found_arrv then --found direct connection between source and destination
                     if found_dep and found_arrv then
                        rtrn := new Connection_Arr(1..1);
                        rtrn(rtrn_st_it) := new Connection;
                        rtrn(rtrn_st_it).train_id := train_ptr.id;
                        rtrn(rtrn_st_it).depart_station_id := src;
                        rtrn(rtrn_st_it).arrive_station_id := des;
                        found_connection := true;
                        exit;
                     else
                        if found_dep then
                           depar(depar_it) := train_ptr;
                           depar_it := depar_it +1;
                        else
                           arriv(arriv_it) := train_ptr;
                           arriv_it := arriv_it +1;
                        end if;
                     end if;
                  end if;
               end if;
            end loop;
            if not found_connection then
               --trying to find connection with one train switch
               declare
                  arriv_ptr : access train.TRAIN;
                  depar_ptr : access train.TRAIN;
               begin
                  for it_a in arriv'Range loop
                     arriv_ptr := arriv(it_a);
                     if arriv_ptr /= null then
                        for it_d in depar'Range loop
                           depar_ptr := depar(it_d);
                           if depar_ptr /= null then
                              for s_a in arriv_ptr.stationlist'Range loop
                                 for s_d in depar_ptr.stationlist'Range loop
                                    if arriv_ptr.stationlist(s_a) = depar_ptr.stationlist(s_d) then
                                       found_connection := true;
                                       rtrn := new Connection_Arr(1..2);

                                       rtrn(rtrn_st_it) := new Connection;
                                       rtrn(rtrn_st_it).train_id := depar_ptr.id;
                                       rtrn(rtrn_st_it).depart_station_id := src;
                                       rtrn(rtrn_st_it+1) := new Connection;
                                       rtrn(rtrn_st_it+1).train_id := arriv_ptr.id;
                                       rtrn(rtrn_st_it+1).arrive_station_id := des;

                                       rtrn(rtrn_st_it).arrive_station_id := arriv_ptr.stationlist(s_a);
                                       rtrn(rtrn_st_it+1).depart_station_id := arriv_ptr.stationlist(s_a);

                                       exit;
                                    end if;
                                 end loop;
                              end loop;
                           end if;
                        end loop;
                     end if;
                  end loop;
               end;
               --choosing train to
               if not found_connection then
                  return null;
               end if;
            end if;
         end;
         --end loop;
      else
         rtrn := new Connection_Arr(1..0);
      end if;
      return rtrn;
   end;

end Model;
