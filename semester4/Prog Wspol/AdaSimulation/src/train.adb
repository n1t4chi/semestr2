with Model; use model;

with steering; use steering;
with Ada.Text_IO;
with Ada.Float_Text_IO;
with track; use track;
with log;
with Ada.Strings;
with Ada.Strings.Unbounded;
with Ada.Exceptions;  use Ada.Exceptions;
with Ada.Numerics.Float_Random; use Ada.Numerics.Float_Random;
with Ada.Real_Time; use Ada.Real_Time;
with dijkstra ; use dijkstra;
with station;

--@Author: Piotr Olejarz 220398
--Train package declares record and task for trains
package body train is


   function createStationList(train_ptr : access Train ; model_ptr : access model.Simulation_Model) return access TRACK_ARRAY is
      rtrn : access TRACK_ARRAY;
      platf_it : Natural := 0;
      track_id : Positive;
   begin
      for it in train_ptr.tracklist'Range loop
         track_id := train_ptr.tracklist(it);
         for itt in model_ptr.platf'Range loop
            if track_id = model_ptr.platf(itt).id then
               platf_it := platf_it + 1;
            end if;
         end loop;
      end loop;

      rtrn := new TRACK_ARRAY(1..platf_it);
      platf_it := 1;
      for it in train_ptr.tracklist'Range loop
         track_id := train_ptr.tracklist(it);
         for itt in model_ptr.platf'Range loop
            if track_id = model_ptr.platf(itt).id then
               rtrn(platf_it) := model_ptr.platf(itt).station_id;
               --Ada.Text_IO.Put_Line(Positive'Image(train_ptr.id) & " > " & Positive'Image(itt) & " station" & Positive'Image(model_ptr.platf(itt).station_id) );
               platf_it := platf_it + 1;
               exit;
            end if;
         end loop;
      end loop;

      return rtrn;
   end;


   function tracklistToString(train_ptr : access TRAIN) return String is
      track_list : Ada.Strings.Unbounded.Unbounded_String;
   begin
      track_list := Ada.Strings.Unbounded.To_Unbounded_String("");
      if train_ptr.tracklist /= null then
         for it in train_ptr.tracklist'Range loop
            if Ada.Strings.Unbounded.To_String(track_list) /= "" then
               track_list := Ada.Strings.Unbounded.To_Unbounded_String(Ada.Strings.Unbounded.To_String(track_list)  & "," &  Integer'Image(train_ptr.tracklist(it)));
            else
               track_list := Ada.Strings.Unbounded.To_Unbounded_String(Integer'Image(train_ptr.tracklist(it)));
            end if;
         end loop;
      end if;
      return Ada.Strings.Unbounded.To_String(track_list);
   end tracklistToString;

   package ustr renames Ada.Strings.Unbounded;
   procedure hist_it(c: vec.Cursor) is
      hist_c : Train_History;
   begin
      hist_c := vec.Element(c);
      if hist_c.object_type = type_Steering then
         Ada.Text_IO.Put_Line("iteration history #"&vec.Extended_Index'Image(vec.To_Index(c))&": steer:"&Positive'Image(hist_c.object_id));
      elsif hist_c.object_type = type_Platform then
         Ada.Text_IO.Put_Line("iteration history #"&vec.Extended_Index'Image(vec.To_Index(c))&": platform:"&Positive'Image(hist_c.object_id));
      elsif hist_c.object_type = type_Track then
         Ada.Text_IO.Put_Line("iteration history #"&vec.Extended_Index'Image(vec.To_Index(c))&": track:"&Positive'Image(hist_c.object_id));
      end if;
   end;

   -- Train task. Cycles thourgh its tracklist and moves from track to steering to track and so on.
   task body TrainTask is
      track_ptr : access track.TRACK;
      steer_ptr : access steering.STEERING;
      first : Boolean := true;
      ready: Boolean;

      G : Generator;
      ran : Float;

      type_str : Ada.Strings.Unbounded.Unbounded_String;

      help_service_train_ptr : access TRAIN := null;


      help_train_ptr : access TRAIN := null;
      help_track_ptr : access track.TRACK := null;
      help_steer_ptr : access steering.STEERING := null;

      work_ptr : access worker.WORKER;
      stat_ptr : access station.STATION;

      hist : access Train_History;
      hist_prev : access Train_History;


      on_station : Boolean := false;

      help : boolean := false;

   begin
      hist := new Train_History; --_Record
      --vec.Append(Container => train_ptr.history , New_Item => hist , Count => 1);
      hist_prev := null;
      if train_ptr /= null and model_ptr /= null then
         Reset(G,4*train_ptr.id+65536 + Integer(Float'Value(Duration'Image(Ada.Real_Time.To_Duration(Ada.Real_Time.Clock-model_ptr.start_time)))));


         if train_ptr.t_type = Train_Type_Normal then
            type_str := ustr.To_Unbounded_String("Train");
            log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                          "] prepares to start its schedule",model_ptr);
            -- initialisation
            train_ptr.track_it := 1;
            train_ptr.on_track := 0;
            train_ptr.on_steer := 0;
            train_ptr.current_speed := 0;
         elsif train_ptr.t_type = Train_Type_Service then
            type_str := ustr.To_Unbounded_String("Service Train");
            log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                          "] begins waiting for service call",model_ptr);
         else
            type_str := ustr.To_Unbounded_String("Unknown");
         end if;
         ready := true;
         --retrieving first track
         track_ptr := null;
         steer_ptr := null;

         --Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"]  begins its ride on track["&Positive'Image(track_ptr.id)&"]" );
         loop
            log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                          "] starts new loop" ,model_ptr);

            if train_ptr.t_type = Train_Type_Normal and then (train_ptr.out_of_order = true and help = false) then
               log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                             "] calls for help" ,model_ptr);
               help_service_train_ptr := model.getServiceTrain(model_ptr);
               if help_service_train_ptr /= null then

                  while help_service_train_ptr.t_task = null loop
                     delay Standard.Duration(1);
                  end loop;

                  select
                     help_service_train_ptr.t_task.trainOutOfOrder(train_ptr.id);
                     help := true;
                  or
                     delay Standard.Duration(1);
                  end select;
               else
                  Ada.Text_IO.Put_Line("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] received null pointer for service train" );
               end if;
               log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                             "] called for help" ,model_ptr);
            end if;


            --Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] starts new loop" );
            if not model_ptr.work then
               Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] terminates its execution"  );
               exit;
            elsif train_ptr.out_of_order = false and then ready = true then -- train is ready to depart from either track or steering
               log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                             "] tries to depart from track or steering" ,model_ptr);
               --Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] enters ready branch" );
               ready := false;
               hist_prev := hist;
               hist := new Train_History; --_Record
               --vec.Append(Container => train_ptr.history , New_Item => hist , Count => 1);

               if train_ptr.tracklist /=null and then train_ptr.on_track /=0 then --train is currently on track
                  log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                "] tries to process on track routine" ,model_ptr);
                  --Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] on track branch" );

                  if (train_ptr.t_type = Train_Type_Service and then train_ptr.going_back = false) and then train_ptr.track_it >= train_ptr.tracklist'length  then
                     log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] arrived near its target. Proceeding to repair routine.",model_ptr);

                     declare
                        real_delay_dur : Float;
                        real_delay_str : String(1..8);
                     begin
                        real_delay_dur := model.getTimeSimToReal(1.0,model.Time_Interval_Hour,model_ptr);
                        Ada.Float_Text_IO.Put(To => real_delay_str , Item => real_delay_dur ,Aft => 3,Exp => 0);

                        if help_track_ptr /= null then
                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] will be repairing track["&Positive'Image(help_track_ptr.id)&
                                         "] for next 1 hour ("&real_delay_str&"s)",model_ptr);
                        elsif help_train_ptr /= null then
                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] will be repairing train["&Positive'Image(help_train_ptr.id)&
                                         "] for next 1 hour ("&real_delay_str&"s)",model_ptr);
                        else
                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] will be repairing steering["&Positive'Image(help_steer_ptr.id)&
                                         "] for next 1 hour ("&real_delay_str&"s)",model_ptr);
                        end if;

                        delay Standard.Duration(real_delay_dur);



                        if help_track_ptr /= null then
                           select
                              help_track_ptr.t_task.repair(train_ptr.id);
                           or
                              delay Standard.Duration(2.0*real_delay_dur);
                              log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] could not reach out to train["&Positive'Image(help_train_ptr.id)&
                                            "] with repair offer. Will manualy force repair.",model_ptr);
                              help_track_ptr.out_of_order := false;
                           end select;

                        elsif help_train_ptr /= null then
                           select
                              help_train_ptr.t_task.repair(train_ptr.id);
                           or
                              delay Standard.Duration(2.0*real_delay_dur);
                              log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] could not reach out to track["&Positive'Image(help_track_ptr.id)&
                                            "] with repair offer. Will manualy force repair.",model_ptr);
                              help_train_ptr.out_of_order := false;
                           end select;
                        else
                           select
                              help_steer_ptr.s_task.repair(train_ptr.id);
                           or
                              delay Standard.Duration(2.0*real_delay_dur);
                              log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] could not reach out to steering["&Positive'Image(help_steer_ptr.id)&
                                            "] with repair offer. Will manualy force repair.",model_ptr);
                              help_steer_ptr.out_of_order := false;
                           end select;
                        end if;


                     end ;
                     help_track_ptr:=null;
                     help_train_ptr:=null;
                     help_steer_ptr:=null;
                     train_ptr.tracklist := findTracklistTo(train_ptr.id,false,train_ptr.on_track,train_ptr.service_track,type_Track,model_ptr);
                     train_ptr.track_it := 1;
                     log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] finished repairing target. Going back to service track using tracklist: " & tracklistToString(train_ptr),model_ptr);

                     train_ptr.going_back := true;
                  elsif (train_ptr.t_type = Train_Type_Service and then train_ptr.going_back) and then train_ptr.track_it = train_ptr.tracklist'length  then
                     log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] arrived back to it's home service track ",model_ptr);
                     train_ptr.tracklist := null;
                     train_ptr.track_it := 1;
                  else
                     log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] starts to depart from track" ,model_ptr);


                     if train_ptr.t_type = Train_Type_Normal and then track_ptr.t_type = track.Track_Type_Platform then
                        on_station := false;
                        declare
                           stat_ptr : access station.STATION;
                        begin
                           stat_ptr := model.getStation(stat_id => track_ptr.station_id , model_ptr => model_ptr);
                           if stat_ptr /= null then
                              log.putLine("#3# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] notifies station["&Positive'Image(stat_ptr.id)&
                                            "] that its about to depart from platform and cannot accept new passengers." ,model_ptr);
                              log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] starts station["&Positive'Image(stat_ptr.id)&
                                            "].notifyAboutTrainDeparture("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                              stat_ptr.s_task.notifyAboutTrainDeparture(train_ptr.id);
                              log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] finished station["&Positive'Image(stat_ptr.id)&
                                            "].notifyAboutTrainDeparture("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                              log.putLine("#3# "&ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] departs from station["&Positive'Image(stat_ptr.id)&"]",model_ptr);
                           else
                              Ada.Text_IO.Put_Line("#3# "&ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                     "] received null pointer for station["&Positive'Image(track_ptr.station_id)&"]." );
                           end if;

                        end;
                     end if;

                     steer_ptr := model.getSteering(track_ptr.st_end,model_ptr);
                     if steer_ptr /= null then
                        --if steering is not null then waits for it to accept this train
                        train_ptr.current_speed := 0;
                        log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] waits for steering["&Positive'Image(track_ptr.st_end)&"]",model_ptr);

                        if train_ptr.t_type = Train_Type_Service and then train_ptr.going_back = false then
                           if steer_ptr.out_of_order = true then
                              declare
                                 real_delay_dur : Float;
                                 real_delay_str : String(1..8);
                              begin
                                 real_delay_dur := model.getTimeSimToReal(1.0,model.Time_Interval_Hour,model_ptr);
                                 Ada.Float_Text_IO.Put(To => real_delay_str , Item => real_delay_dur ,Aft => 3,Exp => 0);

                                 log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                               "]  will be repairing encountered out of order steering["&Positive'Image(steer_ptr.id)&
                                               "] for next 1 hour ("&real_delay_str&"s)",model_ptr);

                                 delay Standard.Duration(real_delay_dur);
                                 select
                                    steer_ptr.s_task.repair(train_ptr.id);
                                 or
                                    delay Standard.Duration(2.0*real_delay_dur);
                                    log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                  "] could not reach out to steering["&Positive'Image(steer_ptr.id)&
                                                  "] with repair offer. Will manualy force repair.",model_ptr);
                                    steer_ptr.out_of_order := false;
                                 end select;
                              end ;
                           end if;

                           log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] starts steering["&Positive'Image(steer_ptr.id)&
                                         "].acceptServiceTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                           steer_ptr.s_task.acceptServiceTrain(train_ptr.id);
                           log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] finished steering["&Positive'Image(steer_ptr.id)&
                                         "].acceptServiceTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                        else
                           log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] starts steering["&Positive'Image(steer_ptr.id)&
                                         "].acceptTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                           steer_ptr.s_task.acceptTrain(train_ptr.id);
                           log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] finished steering["&Positive'Image(steer_ptr.id)&
                                         "].acceptTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                        end if;


                        hist.arrival := Ada.Real_Time.Clock;
                        hist.object_type := type_Steering;

                        train_ptr.on_steer := steer_ptr.id;
                        hist.object_id := steer_ptr.id;
                        --and then clears out currently blocked track
                        log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] leaves the track["&Positive'Image(track_ptr.id)&"]",model_ptr);

                        if train_ptr.t_type = Train_Type_Service and then ( train_ptr.going_back = false and train_ptr.track_it > 1 ) then
                           log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] starts track["&Positive'Image(track_ptr.id)&
                                         "].freeFromServiceTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                           track_ptr.t_task.freeFromServiceTrain(train_ptr.id);
                           log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] finished track["&Positive'Image(track_ptr.id)&
                                         "].freeFromServiceTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                        else
                           log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] starts track["&Positive'Image(track_ptr.id)&
                                         "].clearAfterTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                           track_ptr.t_task.clearAfterTrain(train_ptr.id);
                           log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] finished track["&Positive'Image(track_ptr.id)&
                                         "]. clearAfterTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);


                        end if;

                        hist_prev.departure := Ada.Real_Time.Clock;
                        vec.Append(Container => train_ptr.history , New_Item => hist_prev.all , Count => 1);
                        --vec.Append(Container => train_ptr.history , New_Item => hist_prev , Count => 1);
                        train_ptr.on_track :=0;
                        track_ptr := null;
                     else
                        Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                               "] received null pointer for steering["&Positive'Image(track_ptr.st_end)&"]." );
                     end if;
                  end if;
                  log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                "] finished processing on track routine" ,model_ptr);

               elsif train_ptr.tracklist /=null and then train_ptr.on_steer /=0 then --train is currently on steering
                  log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                "] tries to process on steering routine" ,model_ptr);
                  -- Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] on steer branch" );
                  --incrementing the current track iterator and retrieving next track

                  train_ptr.track_it := 1+ (train_ptr.track_it mod train_ptr.tracklist'length);

                  track_ptr := model.getTrack(train_ptr.tracklist(train_ptr.track_it),model_ptr);
                  if track_ptr /= null then
                     --if track is not null then waits for it to accept this train
                     train_ptr.current_speed := 0;

                     log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] waits for track["&Positive'Image(track_ptr.id) &"]",model_ptr);

                     if train_ptr.t_type = Train_Type_Service then
                        if track_ptr.out_of_order = true then
                           declare
                              real_delay_dur : Float;
                              real_delay_str : String(1..8);
                           begin
                              real_delay_dur := model.getTimeSimToReal(1.0,model.Time_Interval_Hour,model_ptr);
                              Ada.Float_Text_IO.Put(To => real_delay_str , Item => real_delay_dur ,Aft => 3,Exp => 0);

                              log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "]  will be repairing encountered out of order track["&Positive'Image(track_ptr.id)&
                                            "] for next 1 hour ("&real_delay_str&"s)",model_ptr);

                              delay Standard.Duration(real_delay_dur);

                              select
                                 track_ptr.t_task.repair(train_ptr.id);
                              or
                                 delay Standard.Duration(2.0*real_delay_dur);
                                 log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                               "] could not reach out to track["&Positive'Image(track_ptr.id)&
                                               "] with repair offer. Will manualy force repair.",model_ptr);
                                 track_ptr.out_of_order := false;
                              end select;
                           end ;
                        end if;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] starts track["&Positive'Image(track_ptr.id)&
                                      "].acceptServiceTraine("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                        track_ptr.t_task.acceptServiceTrain(train_ptr.id);
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] finished track["&Positive'Image(track_ptr.id)&
                                      "].acceptServiceTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                     else
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] starts track["&Positive'Image(track_ptr.id)&
                                      "].acceptTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                        track_ptr.t_task.acceptTrain(train_ptr.id);
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] finished track["&Positive'Image(track_ptr.id)&
                                      "].acceptTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                     end if;

                     hist.arrival := Ada.Real_Time.Clock;
                     if track_ptr.t_type = track.Track_Type_Track then
                        hist.object_type := type_Track;
                     elsif track_ptr.t_type = track.Track_Type_Platform then
                        hist.object_type := type_Platform;
                        on_station := true;

                        if train_ptr.t_type = Train_Type_Normal then
                           stat_ptr := model.getStation(stat_id => track_ptr.station_id , model_ptr => model_ptr);
                           if stat_ptr /= null then
                              log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] starts station["&Positive'Image(stat_ptr.id)&
                                            "].notifyAboutTrainArrival("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                              stat_ptr.s_task.notifyAboutTrainArrival(train_ptr.id);
                              log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] finished station["&Positive'Image(stat_ptr.id)&
                                            "].notifyAboutTrainArrival("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                              log.putLine("#3# "&ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] arrived at station["&Positive'Image(stat_ptr.id)&"]",model_ptr);

                              --log.printTrains(model_ptr);
                              --log.putLine("#3# 1" & Integer'Image(Integer(HashSet.Length(Container => train_ptr.passengers))));
                              --log.putLine("#3# 2" & Boolean'Image(HashSet.Is_Empty(Container => train_ptr.passengers)));
                              if not HashSet.Is_Empty(Container => train_ptr.passengers) then
                                 for cur in HashSet.Iterate(Container => train_ptr.passengers) loop
                                    work_ptr := HashSet.Element(Position => cur);
                                    log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                  "] starts worker["&Positive'Image(work_ptr.id)&
                                                  "].trainStop("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                                    select
                                       work_ptr.w_task.trainStop(stat_id => track_ptr.station_id ,train_id => train_ptr.id);
                                    else
                                       null;
                                    end select;
                                    log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                  "] finished worker["&Positive'Image(work_ptr.id)&
                                                  "].trainStop("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                                 end loop;
                              end if;
                           else
                              Ada.Text_IO.Put_Line("#3# "&ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                     "] received null pointer for station["&Positive'Image(track_ptr.station_id)&"]." );
                           end if;
                        end if;

                     elsif track_ptr.t_type = track.Track_Type_Service then
                        hist.object_type := type_Service;
                     else
                        hist.object_type:= type_unknown;
                     end if;

                     train_ptr.on_track:= track_ptr.id;
                     hist.object_id := track_ptr.id;
                     --and then clears out currently blocked steering
                     log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] leaves the steering["&Positive'Image(steer_ptr.id)&"]",model_ptr);

                     if train_ptr.t_type = Train_Type_Service and then train_ptr.going_back = false then
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] starts steering["&Positive'Image(steer_ptr.id)&
                                      "].freeFromServiceTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                        steer_ptr.s_task.freeFromServiceTrain(train_ptr.id);
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] finished steering["&Positive'Image(steer_ptr.id)&
                                      "].freeFromServiceTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                     else
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] starts steering["&Positive'Image(steer_ptr.id)&
                                      "].clearAfterTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                        steer_ptr.s_task.clearAfterTrain(train_ptr.id);
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] finished steering["&Positive'Image(steer_ptr.id)&
                                      "].clearAfterTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                     end if;

                     hist_prev.departure := Ada.Real_Time.Clock;
                     vec.Append(Container => train_ptr.history , New_Item => hist_prev.all , Count => 1);
                     --vec.Append(Container => train_ptr.history , New_Item => hist_prev , Count => 1);

                     train_ptr.on_steer :=0;
                     steer_ptr := null;
                  else
                     Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] received null pointer for track["&Positive'Image(train_ptr.tracklist(train_ptr.track_it))&"]." );
                  end if;
                  log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                "] finished processing on steering routine" ,model_ptr);
               else
                  if first = True then
                     log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] first time entry" ,model_ptr);
                     first := false;
                     if train_ptr.t_type = Train_Type_Normal then
                        track_ptr := model.getTrack(train_ptr.tracklist(train_ptr.track_it),model_ptr);
                     else
                        track_ptr := model.getTrack(train_ptr.service_track,model_ptr);
                     end if;
                     if track_ptr /=null then
                        hist.arrival := Ada.Real_Time.Clock;
                        train_ptr.on_track := track_ptr.id;
                        hist.object_id := track_ptr.id;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] starts track["&Positive'Image(track_ptr.id)&
                                      "].acceptTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                        track_ptr.t_task.acceptTrain(train_ptr.id);
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] finished track["&Positive'Image(track_ptr.id)&
                                      "].acceptTrain("&Positive'Image(train_ptr.id)&")" ,model_ptr);

                        if track_ptr.t_type = track.Track_Type_Track then
                           hist.object_type := type_Track;
                        elsif track_ptr.t_type = track.Track_Type_Platform then
                           hist.object_type := type_Platform;
                           on_station := true;

                           if train_ptr.t_type = Train_Type_Normal then
                              stat_ptr := model.getStation(stat_id => track_ptr.station_id , model_ptr => model_ptr);
                              if stat_ptr /= null then
                                 log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                               "] starts station["&Positive'Image(stat_ptr.id)&
                                               "].notifyAboutTrainArrival("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                                 stat_ptr.s_task.notifyAboutTrainArrival(train_ptr.id);
                                 log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                               "] finished station["&Positive'Image(stat_ptr.id)&
                                               "].notifyAboutTrainArrival("&Positive'Image(train_ptr.id)&")" ,model_ptr);
                                   log.putLine("#3# "&ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                 "] arrived at station["&Positive'Image(stat_ptr.id)&"]",model_ptr);
                                 if not HashSet.Is_Empty(Container => train_ptr.passengers) then
                                    for cur in HashSet.Iterate(Container => train_ptr.passengers) loop
                                       work_ptr := HashSet.Element(Position => cur);
                                       log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                     "] starts worker["&Positive'Image(work_ptr.id)&
                                                     "].trainStop("&Positive'Image(train_ptr.id)&") on worker["&Positive'Image(work_ptr.id)&"]" ,model_ptr);
                                       select
                                          work_ptr.w_task.trainStop(stat_id => track_ptr.station_id ,train_id => train_ptr.id);
                                       else
                                          null;
                                       end select;
                                       log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                     "] finished worker["&Positive'Image(work_ptr.id)&
                                                     "].trainStop("&Positive'Image(train_ptr.id)&") on worker["&Positive'Image(work_ptr.id)&"]" ,model_ptr);
                                    end loop;
                                 end if;
                              else
                                 Ada.Text_IO.Put_Line("#3# "&ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                        "] received null pointer for stationD["&Positive'Image(track_ptr.station_id)&"]." );
                              end if;
                           end if;
                        else
                           hist.object_type:= type_unknown;
                        end if;
                     else
                        if train_ptr.t_type = Train_Type_Service then
                           Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                  "] received null pointer for track["&Positive'Image(train_ptr.service_track)&"]." );
                        else
                           Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                                  "] received null pointer for track["&Positive'Image(train_ptr.tracklist(train_ptr.track_it))&"]." );
                        end if;
                     end if;
                     log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] ends first time entry" ,model_ptr);
                  elsif train_ptr.tracklist /=null then
                     Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                            "] is on neither track nor steering at the moment.");
                  end if;
               end if;
               --  Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] end ready branch" );
            else -- train is not ready to depart and waits for notification from currently blocked track or steering.
               --  Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] enters select branch" );

               log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                             "] enters select" ,model_ptr);
               select
                  accept breakSelect do
                     null;
                  end breakSelect;
               or
                    -- when train_ptr.out_of_order = true =>
                  accept repair ( train_id : in Positive)  do
                     log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] enters repair("&Positive'Image(train_id)&")" ,model_ptr);
                     if help_service_train_ptr /= null and then help_service_train_ptr.id = train_id then
                        log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] was just repaired. Returning to schedule.",model_ptr);
                        train_ptr.out_of_order := false;
                     else
                        if help_service_train_ptr /= null then
                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] has no information about service train but received repair signal from service train["
                                       &Positive'Image(train_id)&"]. Accepting the repair and moving along with schedule.",model_ptr);
                           train_ptr.out_of_order := false;
                        else
                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] received repair signal from illegal service train["&Positive'Image(train_id)&
                                         "]. Accepting the repair and moving along with schedule.",model_ptr);
                           train_ptr.out_of_order := false;
                        end if;
                     end if;
                     log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                   "] leaves repair("&Positive'Image(train_id)&")" ,model_ptr);

                  end repair;
               or
                    --notification from steering that train can move further
                  when train_ptr.tracklist /=null and then train_ptr.out_of_order = false =>
                     accept trainReadyToDepartFromSteering(steer_id : Positive)  do
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] enters trainReadyToDepartFromSteering("&Positive'Image(steer_id)&")" ,model_ptr);
                        train_ptr.current_speed := 0;
                        log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] finished waiting on steering["&Positive'Image(steer_id)&"] and is ready to move onto next track",model_ptr);
                        ready := true;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] leaves trainReadyToDepartFromSteering("&Positive'Image(steer_id)&")" ,model_ptr);
                     end trainReadyToDepartFromSteering;
               or
                    --notification from platform that train can move further
                  when train_ptr.tracklist /= null and then train_ptr.out_of_order = false =>
                     accept trainReadyToDepartFromPlatform(track_id : Positive) do
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] enters trainReadyToDepartFromPlatform("&Positive'Image(track_id)&")" ,model_ptr);
                        train_ptr.current_speed := 0;
                        log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] finished waiting on platform["&Positive'Image(track_id)&
                                      "] and is ready to move onto steering",model_ptr);
                        ready := true;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] leaves trainReadyToDepartFromPlatform("&Positive'Image(track_id)&")" ,model_ptr);
                     end trainReadyToDepartFromPlatform;
               or
                    --notification from track that train can move further
                  when train_ptr.tracklist /= null and then train_ptr.out_of_order = false =>
                     accept trainArrivedToTheEndOfTrack(track_id : Positive)  do
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] enters trainArrivedToTheEndOfTrack("&Positive'Image(track_id)&")" ,model_ptr);
                        train_ptr.current_speed := 0;
                        log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] finished riding on track["&Positive'Image(track_id)&"] and is ready to move onto steering",model_ptr);
                        ready := true;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] leaves trainArrivedToTheEndOfTrack("&Positive'Image(track_id)&")" ,model_ptr);
                     end trainArrivedToTheEndOfTrack;
               or
                    --notification for service train from track for help
                  when train_ptr.t_type = Train_Type_Service and then ( train_ptr.going_back = true and train_ptr.on_track /= 0 )=>
                     accept trackOutOfOrder(track_id : in Positive) do
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] enters trackOutOfOrder("&Positive'Image(track_id)&")" ,model_ptr);
                        train_ptr.going_back := false;
                        --log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] printing model:" ,model_ptr);
                        --log.printModel(model_ptr, model_ptr.mode);
                        train_ptr.tracklist := findTracklistTo(train_ptr.id,True,train_ptr.on_track,track_id,type_Track,model_ptr);
                        if train_ptr.tracklist = null then
                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] could not find path to the train" ,model_ptr);
                           train_ptr.going_back := true;
                        else
                           train_ptr.track_it := 1;

                           help_track_ptr := model.getTrack(track_id , model_ptr);

                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] received help request from track["&Positive'Image(track_id)&
                                         "]. Using tracklist: " & tracklistToString(train_ptr),model_ptr);
                        end if;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] leaves trackOutOfOrder("&Positive'Image(track_id)&")" ,model_ptr);

                     end trackOutOfOrder;
               or
                    --notification for service train from platform for help
                  when train_ptr.t_type = Train_Type_Service and then ( train_ptr.going_back = true and train_ptr.on_track /= 0 )=>
                     accept trainOutOfOrder(train_id : in Positive) do
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] enters trainOutOfOrder("&Positive'Image(train_id)&")" ,model_ptr);
                        train_ptr.going_back := false;
                        --log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] printing model:" ,model_ptr);
                        --log.printModel(model_ptr, model_ptr.mode);
                        train_ptr.tracklist := findTracklistTo(train_ptr.id,True,train_ptr.on_track,train_id,type_Train,model_ptr);
                        if train_ptr.tracklist = null then
                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] could not find path to the track" ,model_ptr);
                           train_ptr.going_back := true;
                        else
                           train_ptr.track_it := 1;

                           help_train_ptr := model.getTrain(train_id , model_ptr);

                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] received help request from train["&Positive'Image(train_id)&
                                         "]. Using tracklist: " & tracklistToString(train_ptr),model_ptr);
                        end if;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] leaves trainOutOfOrder("&Positive'Image(train_id)&")" ,model_ptr);

                     end trainOutOfOrder;
               or
                    --notification for service train from train for help
                  when train_ptr.t_type = Train_Type_Service and then ( train_ptr.going_back = true and train_ptr.on_track /= 0 )=>
                     accept steeringOutOfOrder(steer_id : in Positive) do
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] enters steeringOutOfOrder("&Positive'Image(steer_id)&")" ,model_ptr);
                        train_ptr.going_back := false;
                        --log.putLine(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] printing model:" ,model_ptr);
                        --log.printModel(model_ptr, model_ptr.mode);
                        train_ptr.tracklist := findTracklistTo(train_ptr.id,True,train_ptr.on_track,steer_id,type_Steering,model_ptr);
                        if train_ptr.tracklist = null then
                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] could not find path to the steering" ,model_ptr);
                           train_ptr.going_back := true;
                        else
                           train_ptr.track_it := 1;


                           help_steer_ptr := model.getSteering(steer_id , model_ptr);

                           log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                         "] received help request from steering["&Positive'Image(steer_id)&
                                         "]. Using tracklist: " & tracklistToString(train_ptr) ,model_ptr);
                        end if;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] leaves steeringOutOfOrder("&Positive'Image(steer_id)&")" ,model_ptr);

                     end steeringOutOfOrder;
               or
                  when train_ptr.t_type = Train_Type_Normal and then ( train_ptr.on_track /= 0 and on_station)=>
                     accept leaveTrain(worker_id : in Positive)  do
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] enters leaveTrain("&Positive'Image(worker_id)&")" ,model_ptr);
                        declare
                           work_ptr : access worker.WORKER;
                        begin
                           work_ptr := model.getWorker(work_id => worker_id , model_ptr => model_ptr);
                           if work_ptr /= null then
                              if worker.HashSet.Contains(Container => train_ptr.passengers , Item => work_ptr) then
                                 worker.HashSet.Delete(Container => train_ptr.passengers , Item => work_ptr);
                                 log.putLine("#3# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                               "] bids farewell to passenger["  & Positive'Image(work_ptr.id)&"]" ,model_ptr);
                              else
                                 Ada.Text_IO.Put_Line("#3# " & ustr.To_String(type_str)&"]" & Positive'Image(train_ptr.id) &
                                                        "] received illegal leave notification from worker[" & Positive'Image(worker_id)&"]");
                              end if;

                           else
                              Ada.Text_IO.Put_Line("#3# " & ustr.To_String(type_str)&"[" & Positive'Image(train_ptr.id) &
                                                     "] received null pointer for worker[" & Positive'Image(worker_id)&"]");
                           end if;
                        end;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] leaves leaveTrain("&Positive'Image(worker_id)&")" ,model_ptr);

                     end leaveTrain;
               or
                  when train_ptr.t_type = Train_Type_Normal and then ( train_ptr.on_track /= 0 and on_station)=>
                     accept enterTrain(worker_id : in Positive)  do
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] enters enterTrain("&Positive'Image(worker_id)&")" ,model_ptr);
                        declare
                           work_ptr : access worker.WORKER;
                        begin
                           work_ptr := model.getWorker(work_id => worker_id , model_ptr => model_ptr);
                           if work_ptr /= null then
                              if not worker.HashSet.Contains(Container => train_ptr.passengers , Item => work_ptr) then
                                 worker.HashSet.Insert(Container => train_ptr.passengers , New_Item => work_ptr);
                                 log.putLine("#3# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                               "] welcomes passenger["  & Positive'Image(work_ptr.id)&"]" ,model_ptr);

                              else
                                 Ada.Text_IO.Put_Line("#3# " & ustr.To_String(type_str)&"[" & Positive'Image(train_ptr.id) &
                                                        "] received illegal leave notification from worker[" & Positive'Image(worker_id)&"]");
                              end if;

                           else
                              Ada.Text_IO.Put_Line("#3# " & ustr.To_String(type_str)&"[" & Positive'Image(train_ptr.id) &
                                                     "] received null pointer for worker[" & Positive'Image(worker_id)&"]");
                           end if;
                        end;
                        log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                      "] leaves enterTrain("&Positive'Image(worker_id)&")" ,model_ptr);

                     end enterTrain;

               or
                  delay(Standard.Duration(model.getTimeSimToReal(1.0 , Model.Time_Interval_Hour ,model_ptr )));
                  log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                "] had timeout in select" ,model_ptr);
               end select;
               log.putLine("#debug# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                             "] leaves select" ,model_ptr);
               -- Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] leaves select if branch" );
            end if;
            --vec.Iterate(Container => train_ptr.history , Process => hist_it'Access);

            if train_ptr.t_type = Train_Type_Normal and then train_ptr.out_of_order = false then
               ran := Random(G);
               --Ada.Float_Text_IO.Put(To => sim_delay_str , Item => ran ,Aft => 3,Exp => 0);
               --Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&"] rolled " & sim_delay_str & " at time " & log.toString(log.getRelativeTime(Ada.Real_Time.Clock,model_ptr))  );
               if train_ptr.reliability < ran then
                  log.putLine("#2# " & ustr.To_String(type_str)&"["&Positive'Image(train_ptr.id)&
                                "] broke at time " & log.toString(log.getRelativeTime(Ada.Real_Time.Clock,model_ptr))  ,model_ptr);
                  train_ptr.out_of_order := true;
                  help := false;

               end if;

            end if;
         end loop;

      else
         Ada.Text_IO.Put_Line("TrainTask received null pointer! Task will terminate");
      end if;
   exception
      when Error : others =>
         Ada.Text_IO.Put_Line("TrainTask encountered an error:");
         Ada.Text_IO.Put_Line(Exception_Information(Error));
         Ada.Text_IO.Put_Line(Exception_Message(Error));
   end TrainTask;



end train;
