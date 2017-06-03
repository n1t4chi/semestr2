with Ada.Text_IO;
with track; use track;
with System.Address_Image;

--@Author: Piotr Olejarz 220398
--Dijkstra alghoritm
package body dijkstra is


   function findTracklistTo(train_id : Positive ; block : Boolean ; start_track_id : Positive ; target_id : Positive ; target_type : Train_History_Object_Type ; model_ptr : access model.Simulation_Model) return access TRACK_ARRAY is
      tl : access TRACK_ARRAY := null;


      steer : access steering.STEERING;

      steers : access  ArrS;
      dist : access  ArrF;
      prev : access  ArrN;
      d_length : Natural := 0;
      d_it : Natural :=0;

      train_ptr : access train.TRAIN;

      start_track : access track.TRACK;

      target_steer_id : Natural := 0;
      target_steer_id_2 : Natural := 0;

      target_track_ptr : access track.TRACK;
      target_train_ptr : access train.TRAIN;


   begin
      train_ptr := model.getTrain(train_id => train_id , model_ptr => model_ptr);

      start_track := model.getTrack(track_id => start_track_id , model_ptr => model_ptr);


      if target_type = type_Steering then
         target_steer_id := target_id;
      elsif target_type = type_Track then
         target_track_ptr := model.getTrack(target_id , model_ptr);
         target_steer_id := target_track_ptr.st_end;
         target_steer_id_2 := target_track_ptr.st_start;
      elsif target_type = type_Train then
         target_train_ptr := model.getTrain(target_id , model_ptr);
         if target_train_ptr.on_steer /= 0 then
            target_steer_id := target_train_ptr.on_steer;
         else
            target_track_ptr := model.getTrack(target_train_ptr.on_track , model_ptr);
            target_steer_id := target_track_ptr.st_end;
            target_steer_id_2 := target_track_ptr.st_start;
         end if;
      end if;



      --Ada.Text_IO.Put_Line("Looking for path from "& Positive'Image(start_track.st_start)&" or " & Positive'Image(start_track.st_end)
     --                      & " to "& Positive'Image(target_steer_id)&" or " & Positive'Image(target_steer_id_2)
      --);
      if (start_track.st_start = target_steer_id or start_track.st_end = target_steer_id) or else
        (target_steer_id_2 /= 0 and (start_track.st_start = target_steer_id_2 or start_track.st_end = target_steer_id_2))
      then
         tl := new TRACK_ARRAY(1 .. 1);
         tl(1) := start_track_id;
         return tl;
      end if;



      if block = true then
         for it in model_ptr.steer'Range loop
           -- Ada.Text_IO.Put_Line("%%%%teering "& Positive'Image(model_ptr.steer(it).id ) & " used by" & Positive'Image(model_ptr.steer(it).used_by) );
            if model_ptr.steer(it).used_by = 0 then
               select
                  model_ptr.steer(it).s_task.allowServiceTrain(train_id);
                --  Ada.Text_IO.Put_Line("Steering "& Positive'Image(model_ptr.steer(it).id )&" accepted train " & Positive'Image(d_length));
                  d_length := d_length+1;
               or
                  delay 0.05;
               --   Ada.Text_IO.Put_Line("Steering "& Positive'Image(model_ptr.steer(it).id )&" did not respond " & Positive'Image(d_length));
               end select;
            elsif model_ptr.steer(it).used_by = train_id or else model_ptr.steer(it).id = target_steer_id or else ( target_steer_id_2 /= 0 and model_ptr.steer(it).id = target_steer_id_2 )  then
            --   Ada.Text_IO.Put_Line("Steering "& Positive'Image(model_ptr.steer(it).id )&" is already used by this train " & Positive'Image(d_length));
               d_length := d_length+1;
           -- else
             --  Ada.Text_IO.Put_Line("Steering "& Positive'Image(model_ptr.steer(it).id )&" is used by other train " & Positive'Image(d_length));
            end if;

         end loop;

         for it in model_ptr.track'Range loop
            select
               model_ptr.track(it).t_task.allowServiceTrain(train_id);
            or
               delay 0.05;
               --Ada.Text_IO.Put_Line("Track "& Positive'Image(model_ptr.track(it).id )&" failed to respond");
            end select;
         end loop;
      else
         d_length := model_ptr.steer'Length;
      end if;
      --Ada.Text_IO.Put_Line("Finished length: " & Positive'Image(d_length));


      dist := new ArrF(1..d_length);
      prev := new ArrN(1..d_length);
      steers := new ArrS(1..d_length);

      -- arrays initialisation
      declare
         min_d : Float;
         min_i : Natural;
         copy_prev : Natural;
         v : Natural;
         is_v : Boolean;
         it_v : Natural;
         del : Float;
         alt : Float;
      begin
         if block = true then

            for it in model_ptr.steer'Range loop
               --Ada.Text_IO.Put_Line("$$$$$Steering "& Positive'Image(model_ptr.steer(it).id ) & " used by" & Positive'Image(model_ptr.steer(it).used_by) );
               if model_ptr.steer(it).used_by = train_id or else model_ptr.steer(it).id = target_steer_id or else ( target_steer_id_2 /= 0 and model_ptr.steer(it).id = target_steer_id_2 )  then
                  d_it := d_it + 1;
                  if d_it <= steers'Length then
                     --Ada.Text_IO.Put_Line("Adding "& Positive'Image(model_ptr.steer(it).id )&" to list " & Positive'Image(d_it));
                     --Ada.Text_IO.Put_Line("#$%#%#@%#@%#%#$% d_it" & Positive'Image(d_it) & " steers length:" & (Positive'Image(steers'Length)) );
                     steers(d_it) := model_ptr.steer(it);
                     if model_ptr.steer(it).id = start_track.st_end or model_ptr.steer(it).id = start_track.st_start then
                        dist(d_it) := 0.0;
                     else
                        dist(d_it) := Float'Last;
                     end if;

                     prev(d_it) := 0;
                  end if;

              -- else
                  --Ada.Text_IO.Put_Line("Adding "& Positive'Image(model_ptr.steer(it).id )&" not added " & Positive'Image(d_it));
               end if;
            end loop;
         else
            for it in model_ptr.steer'Range loop
               if model_ptr.steer(it).used_by = train_id then
                  d_it := d_it + 1;
                  steers(it) := model_ptr.steer(it);
                  if model_ptr.steer(it).id = start_track.st_end or model_ptr.steer(it).id = start_track.st_start then
                     dist(it) := 0.0;
                  else
                     dist(it) := Float'Last;
                  end if;

                  prev(it) := 0;
               end if;
            end loop;
         end if;

         --d_it last non null pointer
         if d_it > steers'Length then
            d_it := steers'Length;
           -- Ada.Text_IO.Put_Line("#$%#%#@%#@%#%#$%#$%#$%#@%#%#$%#$%#$%#$Null pointers!");
         end if;


         for it in Positive range d_it+1 .. d_length loop
            prev(it) := 0;
            dist(it) := Float'Last;
            steers(it) := null;
         end loop;

         while d_it >= 1 loop
          --  Ada.Text_IO.Put_Line("#$%#%  d_it:"& Natural'Image(d_it));
            --for it in steers'Range loop
            --   if steers(it) /= null then
            --      Ada.Text_IO.Put_Line("steer id:"& Natural'Image(steers(it).id) &" dist:" & Float'Image(dist(it)) & " prev " & Natural'Image(prev(it)) );
            --   else
            --      Ada.Text_IO.Put_Line("null" );
            --   end if;
            --
            --end loop;


            --min vertex u from q
            min_d:= Float'Last;
            min_i:= 0;
            for it in Positive range 1 .. d_it loop
              -- Ada.Text_IO.Put_Line("it:"& Natural'Image(it) &" d_it:" & Natural'Image(d_it) & " dist: " & Natural'Image(dist'Length) );
               if dist(it) <= min_d then
                  min_d := dist(it);
                  min_i := it;
               end if;
            end loop;

            --min
            steer := steers(min_i);

            if steer = null then
               exit;
            end if;

           -- Ada.Text_IO.Put_Line("min_i:" & Natural'Image(min_i) & " steer: " & Natural'Image(steer.id));

            --remove u from q
            steers(min_i) := steers(d_it);
            steers(d_it) := steer;

            dist(min_i) := dist(d_it);
            dist(d_it) := min_d;

            copy_prev := prev(min_i);
            prev(min_i) := prev(d_it);
            prev(d_it) := copy_prev;

            d_it := d_it -1;

            for it in model_ptr.track'Range loop
             --  Ada.Text_IO.Put_Line("track " & Natural'Image(model_ptr.track(it).id )
             --                       & " used by: " & Natural'Image(model_ptr.track(it).used_by )
              --                        & " start " & Natural'Image(model_ptr.track(it).st_start )
              --                        & " end " & Natural'Image(model_ptr.track(it).st_end )
              --                        & " steer: " & Natural'Image(steer.id)  );

               if (block = false or else model_ptr.track(it).used_by = train_id) and ( model_ptr.track(it).st_start = steer.id or model_ptr.track(it).st_end = steer.id ) then
              --    Ada.Text_IO.Put_Line("neighbouring track: " &  Natural'Image(model_ptr.track(it).id) );
                  if model_ptr.track(it).st_start = steer.id then
                     v := model_ptr.track(it).st_end;
                  else
                     v := model_ptr.track(it).st_start;
                  end if;
                  is_v := False;
                  it_v := 0;
                  for it2 in Positive range 1 .. d_it loop
                     if steers(it2).id = v then
                        is_v := True;
                        it_v := it2;
                        exit;
                     end if;
                  end loop;
                --  Ada.Text_IO.Put_Line("v: " &  Natural'Image(v) & " is ok? " & Boolean'Image(is_v));

                  if is_v = true then

                     if model_ptr.track(it).t_type = track.Track_Type_Track then
                        if model_ptr.track(it).max_speed < train_ptr.max_speed then
                           del := Float(model_ptr.track(it).distance)/Float(model_ptr.track(it).max_speed)*60.0;
                        else
                           del := Float(model_ptr.track(it).distance)/Float(train_ptr.max_speed)*60.0;
                        end if;
                     else
                        del := 1.0; --determining that platforms and service tracks will use only 1 minute for service track to get through
                     end if;

                     alt := Float(steer.min_delay) +  min_d + del;

                  --   Ada.Text_IO.Put_Line("alt: " &  Float'Image(alt) & " dist" &  Float'Image(dist(it_v)));


                     if alt <= dist(it_v) then
                        dist(it_v) := alt;
                        prev(it_v) := steer.id;
                     end if;

                  end if;
               end if;
            end loop;
          --  Ada.Text_IO.Put_Line("");

         end loop;
      end;




      declare
         min_1 : Float ;
         min_i_1 : Natural :=0;
         min_i_2 : Natural :=0;
         min_2 : Float := Float'Last;

         len : Positive := 1;
      begin
         for it in Positive range 1 .. d_length loop
            if steers(it) = null then
               null;
             --  Ada.Text_IO.Put_Line("$#%#@$%#@%@#%#@% null pointer" );
            else
               --Ada.Text_IO.Put_Line( Natural'Image(steers(it).id) &" =? "& Natural'Image(target_steer_id) &" or "&Natural'Image(target_steer_id_2) );
               if steers(it).id = target_steer_id then
                  min_i_1 := it;
                  min_1 := dist(it);
               end if;
               if target_steer_id_2 /= 0 and steers(it).id = target_steer_id_2 then
                  min_i_2 := it;
                  min_2 := dist(it);
               end if;
               if min_i_1 /= 0 and ( target_steer_id_2 = 0 or min_i_2 /= 0 ) then
                  exit;
               end if;
            end if;

         end loop;
         --if min_i_1 = 0 then


         --Ada.Text_IO.Put_Line("#$%#%#@%#@% min_1:"& Float'Image(min_1) &" min_i_1:"& Natural'Image(min_i_1) &" min_2:"& Float'Image(min_2) &" min_i_2:"& Natural'Image(min_i_2) &"   target1"& Natural'Image(target_steer_id) &" target2:" & Natural'Image(target_steer_id_2) );
         --end if;


         if min_i_2 /= 0 and min_1 > min_2 then
            min_1 := min_2;
            min_i_1 := min_i_2;
         end if;
         min_i_2 := min_i_1;
         if min_i_1 = 0 then

            if block = true then
               for it in model_ptr.track'Range loop
                  if model_ptr.track(it).used_by = train_id and model_ptr.track(it).id /= train_ptr.on_track then
                     model_ptr.track(it).t_task.freeFromServiceTrain(train_id);
                  end if;
               end loop;
               for it in model_ptr.steer'Range loop
                  if model_ptr.steer(it).used_by = train_id and model_ptr.steer(it).id /= train_ptr.on_steer then
                     model_ptr.steer(it).s_task.freeFromServiceTrain(train_id);
                  end if;
               end loop;
            end if;

            return null;
         end if;

         while prev(min_i_1) /= 0 loop
            len := len + 1;
            for it in Positive range 1 .. d_length loop
               if steers(it).id = prev(min_i_1) then
                  min_i_1 := it;
                  exit;
               end if;
            end loop;
         end loop;

         if block = false and then target_type = type_Track then
            tl := new TRACK_ARRAY(1 .. len+1);
            tl(len+1) := target_id;
         else
            tl := new TRACK_ARRAY(1 .. len);
         end if;

         min_i_1 := min_i_2;

         tl(1) := start_track_id;

       --  Ada.Text_IO.Put_Line("#$%#%#@%#@%#%#$%#$%#$%#@%#%#$%#$%#$%#$ tracklist length:"& Natural'Image(tl'Length) );
         for itt in reverse 2 .. len loop
        --    Ada.Text_IO.Put_Line("#$%#%#@%#@%#%#$%#$%#$%#@%#%#$%#$%#$%#$ itt"& Natural'Image(itt) & " iterator:" &  Natural'Image(min_i_1) & " " );
            tl(itt) := 0;

            for it in model_ptr.track'Range loop
          --     Ada.Text_IO.Put_Line("#$%#%#@%#@%#%#$"
          --                             &" start: " &   Natural'Image(model_ptr.track(it).st_start)
          --                             &" end: " & Natural'Image(model_ptr.track(it).st_end)
          --                             &" curr: " & Natural'Image(steers(min_i_1).id)
          --                             &" prev: " & Natural'Image(prev(min_i_1))
          --     );
               if ( model_ptr.track(it).st_start = steers(min_i_1).id and model_ptr.track(it).st_end = prev(min_i_1))
                 or (model_ptr.track(it).st_end = steers(min_i_1).id and model_ptr.track(it).st_start = prev(min_i_1))
               then
                  tl(itt) := model_ptr.track(it).id;
                  exit;
               end if;

            end loop;

            for it in Positive range 1 .. d_length loop
               if steers(it).id = prev(min_i_1) then
                  min_i_1 := it;
                  exit;
               end if;
            end loop;

         end loop;
        --for it in tl'Range loop
        --    Ada.Text_IO.Put_Line("it"& Natural'Image(it) & " tl:" &  Natural'Image(tl(it)));
         --end loop;
         if block = true then
            declare
               found : boolean;
               track_ptr : access track.TRACK;
            begin

               for it in model_ptr.track'Range loop
                  if model_ptr.track(it).used_by = train_id and model_ptr.track(it).id /= train_ptr.on_track then
                     found := false;
                     for itt in tl'Range loop
                        if tl(itt) = model_ptr.track(it).id then
                           found := true;
                           exit;
                        end if;
                     end loop;
                     if found = false then
                        model_ptr.track(it).t_task.freeFromServiceTrain(train_id);
                     end if;
                  end if;
               end loop;
               for it in model_ptr.steer'Range loop
                  if model_ptr.steer(it).used_by = train_id and model_ptr.steer(it).id /= train_ptr.on_steer then
                     found := false;
                     for itt in tl'Range loop
                        track_ptr := model.getTrack(tl(itt),model_ptr);
                        if track_ptr.st_end = model_ptr.steer(it).id or track_ptr.st_start = model_ptr.steer(it).id then
                           found := true;
                           exit;
                        end if;
                     end loop;
                     if found = false then
                        model_ptr.steer(it).s_task.freeFromServiceTrain(train_id);
                     end if;
                  end if;
               end loop;
            end;
         end if;
      end;

      return tl;
   end;





end dijkstra;
