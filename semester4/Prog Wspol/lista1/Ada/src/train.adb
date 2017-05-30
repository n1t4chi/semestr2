with Model; use model;

with steering;
with Ada.Text_IO;
with Ada.Float_Text_IO;
with track; use track;
with log;
with Ada.Exceptions;  use Ada.Exceptions;
--@Author: Piotr Olejarz 220398
--Train package declares record and task for trains
package body train is


   --procedure moveTrainToNextTrack ( train_ptr : access TRAIN ; model_ptr : access model.Simulation_Model) is
   --   curr_track_ptr : access track.TRACK;
  --    steering_ptr : access steering.STEERING;
 --     delay_dur : Float;
--      real_delay_dur : Float;
--   begin
      --Ada.Text_IO.Put_Line("am I here?");
     -- if train_ptr /= null and model_ptr /= null then
     --    --Ada.Text_IO.Put_Line("am I here?? track it:"&Positive'Image(train_ptr.track_it));
      --   --Ada.Text_IO.Put_Line("am I here?? tracklist: "&Positive'Image(train_ptr.tracklist(train_ptr.track_it)));
    --     curr_track_ptr := model.getTrack(train_ptr.tracklist(train_ptr.track_it),model_ptr);
  --       if curr_track_ptr /= null then
--            --Ada.Text_IO.Put_Line("am I here???");
--            null;
--         else
 --           Ada.Text_IO.Put_Line("moveTrainToNextTrack received invalid steering ID:."&Natural'Image(train_ptr.tracklist(train_ptr.track_it)));
 --        end if;
 --        --Ada.Text_IO.Put_Line("why am I here??");
 --     else
 --        Ada.Text_IO.Put_Line("moveTrainToNextTrack received null pointers.");
 --     end if;
 --  end moveTrainToNextTrack;

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

      ready: Boolean;

      hist : access Train_History;
      hist_prev : access Train_History;
   begin
      hist := new Train_History; --_Record
      --vec.Append(Container => train_ptr.history , New_Item => hist , Count => 1);
      hist_prev := null;
      if train_ptr /= null and model_ptr /= null then
         log.putLine("Train["&Positive'Image(train_ptr.id)&"] prepares to start its schedule",model_ptr);
         --moveTrainToNextTrack(train_ptr,model_ptr);

         -- initialisation
         train_ptr.track_it := 1;
         train_ptr.on_track := 0;
         train_ptr.on_steer := 0;
         train_ptr.current_speed := 0;
         --retrieving first track
         track_ptr := model.getTrack(train_ptr.tracklist(train_ptr.track_it),model_ptr);
         steer_ptr := null;

         if track_ptr /= null then --checking validity
            --Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"]  begins its ride on track["&Positive'Image(track_ptr.id)&"]" );

            hist.arrival := Ada.Real_Time.Clock;
            train_ptr.on_track := track_ptr.id;
            hist.object_id := track_ptr.id;
            track_ptr.t_task.acceptTrain(train_ptr.id);

            if track_ptr.t_type = track.Track_Type_Track then
              hist.object_type := type_Track;
            elsif track_ptr.t_type = track.Track_Type_Platform then
               hist.object_type := type_Platform;
            else
               hist.object_type:= type_unknown;
            end if;

            ready := false;
            loop
               --Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] starts new loop" );
               if not model_ptr.work then
                  Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] terminates its execution"  );
               exit;
               elsif ready then -- train is ready to depart from either track or steering
                  --Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] enters ready branch" );
                  ready := false;
                  hist_prev := hist;
                  hist := new Train_History; --_Record
                  --vec.Append(Container => train_ptr.history , New_Item => hist , Count => 1);

                  if train_ptr.on_track /=0 then --train is currently on track
                     --Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] on track branch" );
                     --retrieve next steering
                     steer_ptr := model.getSteering(track_ptr.st_end,model_ptr);
                     if steer_ptr /= null then
                        --if steering is not null then waits for it to accept this train
                        train_ptr.current_speed := 0;
                        log.putLine("Train["&Positive'Image(train_ptr.id)&"] waits for steering"&Positive'Image(track_ptr.st_end),model_ptr);
                        steer_ptr.s_task.acceptTrain(train_ptr.id);

                        hist.arrival := Ada.Real_Time.Clock;
                        hist.object_type := type_Steering;

                        train_ptr.on_steer := steer_ptr.id;
                        hist.object_id := steer_ptr.id;
                        --and then clears out currently blocked track
                        log.putLine("Train["&Positive'Image(train_ptr.id)&"] leaves the track ["&Positive'Image(track_ptr.id)&"]",model_ptr);
                        track_ptr.t_task.clearAfterTrain(train_ptr.id);
                        hist_prev.departure := Ada.Real_Time.Clock;
                        vec.Append(Container => train_ptr.history , New_Item => hist_prev.all , Count => 1);
                        --vec.Append(Container => train_ptr.history , New_Item => hist_prev , Count => 1);
                        train_ptr.on_track :=0;
                        track_ptr := null;
                     else
                        Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] received null pointer for steering ID["&Positive'Image(track_ptr.st_end)&"]." );
                     end if;
                  elsif train_ptr.on_steer /=0 then --train is currently on steering
                    -- Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] on steer branch" );
                     --incrementing the current track iterator and retrieving next track
                     train_ptr.track_it := 1+ (train_ptr.track_it mod train_ptr.tracklist'length);
                     track_ptr := model.getTrack(train_ptr.tracklist(train_ptr.track_it),model_ptr);
                     if track_ptr /= null then
                        --if track is not null then waits for it to accept this train
                        train_ptr.current_speed := 0;

                        log.putLine("Train["&Positive'Image(train_ptr.id)&"] waits for track["&Positive'Image(track_ptr.id) &"]",model_ptr);
                        track_ptr.t_task.acceptTrain(train_ptr.id);

                        hist.arrival := Ada.Real_Time.Clock;
                        if track_ptr.t_type = track.Track_Type_Track then
                           hist.object_type := type_Track;
                        elsif track_ptr.t_type = track.Track_Type_Platform then
                           hist.object_type := type_Platform;
                        else
                           hist.object_type:= type_unknown;
                        end if;

                        train_ptr.on_track:= track_ptr.id;
                        hist.object_id := track_ptr.id;
                        --and then clears out currently blocked steering
                        log.putLine("Train["&Positive'Image(train_ptr.id)&"] leaves the steering ["&Positive'Image(steer_ptr.id)&"]",model_ptr);
                        steer_ptr.s_task.clearAfterTrain(train_ptr.id);
                        hist_prev.departure := Ada.Real_Time.Clock;
                        vec.Append(Container => train_ptr.history , New_Item => hist_prev.all , Count => 1);
                        --vec.Append(Container => train_ptr.history , New_Item => hist_prev , Count => 1);

                        train_ptr.on_steer :=0;
                        steer_ptr := null;
                     else
                        Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] received null pointer for track ID["&Positive'Image(train_ptr.tracklist(train_ptr.track_it))&"]." );
                     end if;
                  else
                     Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] is not on neither track or steering at the moment.");
                  end if;
                --  Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] end ready branch" );
               else -- train is not ready to depart and waits for notification from currently blocked track or steering.
                --  Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] enters select branch" );
                  select


                     accept breakSelect do
                        null;
                     end breakSelect;
                  or
                     --notification from steering that train can move further
                     accept trainReadyToDepartFromSteering(steer_id : Positive)  do
                        train_ptr.current_speed := 0;
                        log.putLine("Train["&Positive'Image(train_ptr.id)&"] finished waiting on steering ["&Positive'Image(steer_id)&"] and is ready to move onto next track",model_ptr);
                        ready := true;
                     end trainReadyToDepartFromSteering;
                  or
                       --notification from platform that train can move further
                     accept trainReadyToDepartFromPlatform(track_id : Positive) do
                        train_ptr.current_speed := 0;
                        log.putLine("Train["&Positive'Image(train_ptr.id)&"] finished waiting on platform ["&Positive'Image(track_id)&"] and is ready to move onto steering",model_ptr);
                        ready := true;
                     end trainReadyToDepartFromPlatform;
                  or
                       --notification from track that train can move further
                     accept trainArrivedToTheEndOfTrack(track_id : Positive)  do
                        train_ptr.current_speed := 0;
                        log.putLine("Train["&Positive'Image(train_ptr.id)&"] finished riding on track ["&Positive'Image(track_id)&"] and is ready to move onto steering",model_ptr);
                        ready := true;
                     end trainArrivedToTheEndOfTrack;
                  end select;
                 -- Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] leaves select if branch" );
               end if;
               --vec.Iterate(Container => train_ptr.history , Process => hist_it'Access);
            end loop;







         else
            Ada.Text_IO.Put_Line("Train["&Positive'Image(train_ptr.id)&"] received null pointer for track ID["&Natural'Image(train_ptr.tracklist(train_ptr.track_it))&"]. Task will terminate" );
         end if;
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
