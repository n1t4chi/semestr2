with Ada.Text_IO;
with Ada.Strings.Unbounded;
with Model;
with train; use train;
with track; use track;
with steering;
with station;
with worker;
with Ada.Characters.Handling;

--@Author: Piotr Olejarz 220398
--Package GetInput is used to recover configuration of simulation from given file and creates Simulation_Model object from that.
--File requires below structure for valid data. All columns must be spaced out with tabulation.
--Simulation speed requires single record, other allow for multiple records.
--tracks require valid IDs of steerings and train's trackilist requires valid track IDs.
--Also tracklist must be valid in the sense that each two tracks are connected by common steering at one track start and other end.
--Also tracklist must be cyclic so that start of first is the end of the last track.
--File Structure:
--@simulation_speed:
--<real-time seconds to simulation-hour ratio>
--@steering:
--<nr>	<delay in minutes>
--@tracks:
--<nr>	<v1>	<v2>	stop	<delay in minutes>
--<nr>	<v1>	<v2>	pass	<dist in km>	<max vel in km/h>
--@trains:
--<nr>	<max speed in km/h>	<capacity of people>	<tracklist>
--
package body GetInput is
   package TextIO renames Ada.Text_IO;
   package Ustr renames  Ada.Strings.Unbounded;

   type Block_Collection is array (Positive range <>) of Ustr.Unbounded_String;

   --Recovers data from file, ignores empty lines and those with # at first character.
   function getInput (Filename: String) return access Block_Collection is
      Input_File : TextIO.File_Type;
      Arr : access Block_Collection;
      i : Integer;
      line : Ustr.Unbounded_String;
   begin
      Arr := new Block_Collection(1 .. 255);
      --opens file
      TextIO.Open(File => Input_File,Mode => TextIO.In_File, Name => Filename);
      i:=1;
      --reads each line from file.
      while not TextIO.End_Of_File (Input_File) loop
         line := Ustr.To_Unbounded_String(TextIO.Get_Line(File => Input_File));
         if Ustr.Length(Source => line)>0  and then Ustr.Element(line,1) /= '#' then
            Arr(i) := line;
           -- TextIO.Put_Line(Ustr.To_String(Arr(i)));
            i:=i+1;
         end if;
      end loop;
      --closes descriptor and returns content.
      TextIO.Close(Input_File);
      return Arr;
   exception
      when TextIO.Name_Error =>
         TextIO.Put_Line("File not found!");
         return null;
   end getInput;


   --gets input from file and creates Simulation_Model object.
   function getModelFromFile(Filename: String) return access Model.Simulation_Model is
      Arr : access Block_Collection;
      model_ptr : access Model.Simulation_Model;
      st_l,trk_l,trn_l,sta_l,wor_l : Positive;
      line_state  : Positive;
      curr_line : Ustr.Unbounded_String;
      line_s,data_state,item_it,line_it : Natural;
      street_ptr : access steering.STEERING;
      train_ptr : access train.TRAIN;
      track_ptr : access track.TRACK;
      stat_ptr : access station.STATION;
      work_ptr : access worker.WORKER;
      track_id,track_v1,track_v2 : Positive;
      train_id,train_vel,train_list_it,train_list_length : Positive;

      platf_it : Natural := 0;

   begin

      --get file input
      Arr := getInput(Filename => Filename);
      line_state := 1;
      --calculate ranges for arrays.
      for it in Arr'Range loop
         curr_line := Arr(it);
         --TextIO.Put_Line( Positive'Image(it) & " " & ustr.To_String(curr_line));
         if ustr.To_String(curr_line) = "@steering:" then
            line_state :=it;
         elsif ustr.To_String(curr_line) = "@tracks:" then
            st_l := it - line_state -1;
            line_state := it;
         elsif ustr.To_String(curr_line) = "@trains:" then
            trk_l := it - line_state -1;
            line_state := it;
         elsif ustr.To_String(curr_line) = "@stations:" then
            trn_l := it - line_state -1;
            line_state := it;
         elsif ustr.To_String(curr_line) = "@workers:" then
            sta_l := it - line_state -1;
            line_state := it;
         elsif ustr.Length(curr_line) = 0 then
            wor_l := it - line_state -1;
            exit;
         end if;
      end loop;

      --create new model
      --TextIO.Put_Line("steer:" & Positive'Image(st_l) & " tracks:" & Positive'Image(trk_l) & " trains:" & Positive'Image(trn_l) & " stations:" & Positive'Image(sta_l) );
      model_ptr := new Model.Simulation_Model(Steer_Length => st_l,Track_Length => trk_l,Train_Length => trn_l,Station_Length => sta_l , Worker_Length => wor_l);

      --iterate for all lines
      for it in Arr'Range loop
         curr_line := Arr(it);
         if ustr.Length(curr_line) /= 0 then
            --first 4 ifs are state switches, They search for a tag so the else clause can translate text into valid simulation data.
            if ustr.To_String(curr_line) = "@simulation_speed:" then
               line_state :=1;
               item_it := 1;
            elsif ustr.To_String(curr_line) = "@steering:" then
               line_state :=2;
               item_it := 1;
            elsif ustr.To_String(curr_line) = "@tracks:" then
               line_state := 3;
               item_it := 1;
            elsif ustr.To_String(curr_line) = "@trains:" then
               line_state := 4;
               item_it := 1;
            elsif ustr.To_String(curr_line) = "@stations:" then
               line_state := 5;
               item_it := 1;
            elsif ustr.To_String(curr_line) = "@workers:" then
               line_state := 6;
               item_it := 1;
            else
               line_s := 0;
               data_state := 0;
               --depending on a current state, the line will be treated like corresponding record representation.
               case line_state is
               when 1 => --simulation speed
                  model_ptr.speed := Positive'Value(ustr.To_String(curr_line));
                  --TextIO.Put_Line("Simulation speed: " & Positive'Image(model_ptr.speed) & "s -> 1h");
               when 2 => --steering
                  street_ptr := new steering.STEERING;
                  line_it := 1;
                  street_ptr.used_by := 0;
                  while line_it <= ustr.Length(Source => curr_line)+1 loop
                     --if line_it <= ustr.Length(Source => curr_line) then
                     --   TextIO.Put_Line(Positive'Image(line_it) & " > [" & ustr.Element(curr_line,line_it) & "]");
                     --else
                     --   TextIO.Put_Line(Positive'Image(line_it) & " last > " & ustr.Slice(curr_line,line_s+1,line_it-1));
                     -- end if;

                     --for it in Ustr.Lengthcurr_line loop
                     if line_it > ustr.Length(Source => curr_line) or else not Ada.Characters.Handling.Is_Alphanumeric(ustr.Element(curr_line,line_it)) then
                        case data_state is
                        when 0 => -- steering id;
                           street_ptr.id := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 1;
                           line_s := line_it;
                        when 1 =>--steering minimum delay
                           --TextIO.Put_Line("am i here?");
                           street_ptr.min_delay := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 2;
                           null;
                        when others =>
                           --exit;
                           null;
                        end case;

                     end if;
                     line_it := line_it + 1;
                  end loop;
                  --inserting steering pointer into array
                  model_ptr.steer(item_it) := street_ptr;
                  --TextIO.Put_Line(Model.toString(model_ptr.steer(item_it)));
                  item_it := item_it + 1;
               when 3 => -- tracks
                  --TextIO.Put_Line("am i here?");
                  track_ptr := null;
                  line_it := 1;
                  while line_it <= ustr.Length(Source => curr_line)+1 loop
                     --if line_it <= ustr.Length(Source => curr_line) then
                     --   TextIO.Put_Line(Positive'Image(line_it) & " > [" & ustr.Element(curr_line,line_it) & "]");
                     --else
                     --    TextIO.Put_Line(Positive'Image(line_it) & " last > " & ustr.Slice(curr_line,line_s+1,line_it-1));
                     --end if;

                     --for it in Ustr.Lengthcurr_line loop
                     if line_it > ustr.Length(Source => curr_line) or else not Ada.Characters.Handling.Is_Alphanumeric(ustr.Element(curr_line,line_it)) then
                        --if ustr.Element(curr_line,line_it) = Ada.Characters.Latin_1.HT or else ustr.Element(curr_line,line_it) = Ada.Characters.Latin_1.LF then
                        case data_state is
                        when 0 => --id
                           track_id := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 1;
                           line_s := line_it;
                        when 1 => --start
                           track_v1 := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 2;
                           line_s := line_it;
                        when 2 => --end
                           track_v2 := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 3;
                           line_s := line_it;
                        when 3 => --type
                           if ustr.Slice(curr_line,line_s+1,line_it-1) = "stop" then
                              track_ptr := new track.TRACK(track.Track_Type_Platform);
                              platf_it := platf_it + 1;
                              data_state := 10;
                              line_s := line_it;
                           elsif ustr.Slice(curr_line,line_s+1,line_it-1) = "pass" then
                              track_ptr := new track.TRACK(track.Track_Type_Track);
                              data_state := 100;
                              line_s := line_it;
                           elsif ustr.Slice(curr_line,line_s+1,line_it-1) = "service" then
                              track_ptr := new track.TRACK(track.Track_Type_Service);
                              data_state := 1337;
                              line_s := line_it;
                           else
                              TextIO.Put_Line("Illegal track type");
                              exit;
                           end if;
                           track_ptr.id := track_id;
                           track_ptr.st_start := track_v1;
                           track_ptr.st_end := track_v2;
                        when 10 => -- platform delay
                           track_ptr.min_delay := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 11;
                           line_s := line_it;
                        when 11 => -- platform station id
                           track_ptr.station_id := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 1337;
                           line_s := line_it;
                        when 100 => -- track dist
                           track_ptr.distance := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 101;
                           line_s := line_it;
                        when 101 => -- track vel
                           track_ptr.max_speed := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 1337;
                           line_s := line_it;
                        when others => -- 1337
                           exit;
                        end case;
                     end if;
                     line_it := line_it + 1;
                  end loop;
                  --inserting valid track pointer into array
                  if track_ptr /=null then
                     track_ptr.used_by := 0;
                     model_ptr.track(item_it) := track_ptr;
                     --TextIO.Put_Line(Model.toString(model_ptr.track(item_it)));
                     item_it := item_it + 1;
                  end if;

               when 4 =>--trains
                  --TextIO.Put_Line("am i here?");
                  train_ptr := null;
                  line_it := 1;
                  while line_it <= ustr.Length(Source => curr_line)+1 loop
                     -- if line_it <= ustr.Length(Source => curr_line) then
                     --    TextIO.Put_Line(Positive'Image(line_it) & " > [" & ustr.Element(curr_line,line_it) & "]");
                     -- else
                     --    TextIO.Put_Line(Positive'Image(line_it) & " last > " & ustr.Slice(curr_line,line_s+1,line_it-1));
                     --   end if;

                     --for it in Ustr.Lengthcurr_line loop
                     if line_it > ustr.Length(Source => curr_line) or else ( not Ada.Characters.Handling.Is_Alphanumeric(ustr.Element(curr_line,line_it)) and ustr.Element(curr_line,line_it) /= ',' ) then
                        --if ustr.Element(curr_line,line_it) = Ada.Characters.Latin_1.HT or else ustr.Element(curr_line,line_it) = Ada.Characters.Latin_1.LF then
                        case data_state is
                        when 0 => -- train id
                           train_id := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 1;
                           line_s := line_it;
                        when 1 => -- train speed
                           train_vel := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 2;
                           line_s := line_it;
                        when 2 => -- train type
                           if ustr.Slice(curr_line,line_s+1,line_it-1) = "normal" then
                              train_ptr := new train.TRAIN(train.Train_Type_Normal);
                              -- t_type := train.Train_Type_Normal;
                              data_state := 3;
                           elsif ustr.Slice(curr_line,line_s+1,line_it-1) = "service" then
                              train_ptr := new train.TRAIN(train.Train_Type_Service);
                              --t_type := train.Train_Type_Service;
                              data_state := 5;
                           else
                              TextIO.Put_Line("Illegal train type");
                              data_state := 6;
                              exit;
                           end if;
                           train_ptr.id := train_id;
                           train_ptr.max_speed := train_vel;
                           line_s := line_it;
                        when 3 => -- train capacity
                           --TextIO.Put_Line(ustr.Slice(curr_line,line_s+1,line_it-1));
                           train_ptr.capacity := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 4;
                           line_s := line_it;
                        when 4=> --tracklist
                           train_list_length:=1;
                           for it in Positive range line_s+1..line_it-1 loop
                              if ustr.Element(curr_line,it) = ',' then
                                 train_list_length := train_list_length + 1;
                              end if;
                           end loop;
                           train_ptr.tracklist := new train.TRACK_ARRAY(1..train_list_length);
                           --train_ptr := new train.TRAIN(t_type,train_list_length);
                           train_list_it:= 1;
                           --TextIO.Put_Line(Positive'Image(line_s+1) & " .. " & Positive'Image(line_it));
                           for it in Positive range line_s+1..line_it loop --slicing tracklist text into array
                              --TextIO.Put_Line("loop zoop");
                              --if it <= ustr.Length(Source => curr_line) then
                              --   TextIO.Put_Line(Positive'Image(it) & " > [" & ustr.Element(curr_line,it) & "]");
                              --else
                              --    TextIO.Put_Line(Positive'Image(line_it) & " last > " & ustr.Slice(curr_line,line_s+1,it-1));
                              -- end if;

                              --slices at the end and on each non digit character
                              if it > ustr.Length(Source => curr_line) or else not Ada.Characters.Handling.Is_Digit(ustr.Element(curr_line,it))  then
                                 --TextIO.Put_Line("am i here??");
                                 train_ptr.tracklist(train_list_it) := Positive'Value(ustr.Slice(curr_line,line_s+1,it-1));
                                 --TextIO.Put_Line("am i here???");
                                 line_s := it;
                                 train_list_it := train_list_it+1;
                              end if;
                           end loop;
                           --TextIO.Put_Line("am i here????");
                           data_state := 6;
                           line_s := line_it;
                        when 5=> --service track
                           train_ptr.service_track := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 6;
                           line_s := line_it;
                        when others =>
                           exit;
                        end case;
                     end if;
                     line_it := line_it + 1;
                  end loop;
                  --inserting valid train pointer into array
                  if train_ptr /=null then
                     model_ptr.train(item_it) := train_ptr;
                     --TextIO.Put_Line(Model.toString(model_ptr.train(item_it)));
                     item_it := item_it + 1;
                  end if;
               when 5 =>--stations
                  stat_ptr := new station.STATION;
                  stat_ptr.id := Positive'Value(ustr.To_String(curr_line));
                  --inserting steering pointer into array
                  --TextIO.Put_Line("adding station" & ustr.To_String(curr_line) &" at " & Positive'Image(item_it) &"/"&Positive'Image(model_ptr.station'Length) );
                  model_ptr.station(item_it) := stat_ptr;
                  item_it := item_it + 1;
               when 6 =>--workers
                  work_ptr := new worker.WORKER;
                  line_it := 1;
                  while line_it <= ustr.Length(Source => curr_line)+1 loop
                     if line_it > ustr.Length(Source => curr_line) or else not Ada.Characters.Handling.Is_Alphanumeric(ustr.Element(curr_line,line_it)) then
                        case data_state is
                        when 0 => -- worker id;
                           work_ptr.id := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           data_state := 1;
                           line_s := line_it;
                        when 1 =>-- home station
                           work_ptr.home_stat_id := Positive'Value(ustr.Slice(curr_line,line_s+1,line_it-1));
                           work_ptr.on_Station := work_ptr.home_stat_id;
                           data_state := 2;
                           null;
                        when others =>
                           --exit;
                           null;
                        end case;

                     end if;
                     line_it := line_it + 1;
                  end loop;
                  --inserting steering pointer into array
                  model_ptr.worker(item_it) := work_ptr;
                  --TextIO.Put_Line(Model.toString(model_ptr.steer(item_it)));
                  item_it := item_it + 1;
               when others =>
                  null;
               end case;
            end if;

         end if;
      end loop;

      model_ptr.platf := new model.TRACK_ARR(1..platf_it);
      platf_it := 1;
      for it in model_ptr.track'Range loop
         if model_ptr.track(it).t_type = track.Track_Type_Platform then
            model_ptr.platf(platf_it) := model_ptr.track(it);
            platf_it := platf_it + 1;
         end if;
      end loop;

      declare
         unique : Boolean;
      begin
         for it in model_ptr.train'Range loop
            train_ptr := model_ptr.train(it);
            if train_ptr.t_type = train.Train_Type_Normal then
               train_ptr.stationlist := createStationList(train_ptr,model_ptr);
               for itt in model_ptr.train(it).stationlist'Range loop
                  unique := true;
                  for ittt in Positive range itt+1..train_ptr.stationlist'Length  loop
                     if train_ptr.stationlist(itt) = train_ptr.stationlist(ittt) then
                        unique := false;
                        exit;
                     end if;
                  end loop;
                  if unique then
                     train_ptr.uniqueStations := train_ptr.uniqueStations +1;
                  end if;
               end loop;
            end if;
         end loop;
      end;

      for it in model_ptr.station'Range loop
         stat_ptr := model_ptr.station(it);
         for itt in model_ptr.worker'Range loop
            if model_ptr.worker(itt).home_stat_id = stat_ptr.id then
               worker.HashSet.Insert(Container => stat_ptr.passengers,New_Item => model_ptr.worker(itt)  );
            end if;
         end loop;
      end loop;


      if False then
         declare
            con : access model.Connection_Arr;
         begin
            for it in model_ptr.station'Range loop
               for it2 in model_ptr.station'Range loop
                  if it /= it2 then
                     con := Model.getConnection(source_station => model_ptr.station(it).id , destination_station => model_ptr.station(it2).id , model_ptr => model_ptr);
                     if con /= null then
                        TextIO.Put_Line("Connection between " & Positive'Image(model_ptr.station(it).id) & " and " & Positive'Image(model_ptr.station(it2).id) & ":");
                        for it3 in con'Range loop
                           if con(it3) /= null then
                              TextIO.Put_Line(Positive'Image(it3) & " train: " & Positive'Image(con(it3).train_id) & " depart: " & Positive'Image(con(it3).depart_station_id) & " arriv: " & Positive'Image(con(it3).arrive_station_id));
                           else
                              TextIO.Put_Line(Positive'Image(it3) & " null");
                              exit;
                           end if;

                        end loop;
                     else
                        TextIO.Put_Line("no connection between " & Positive'Image(model_ptr.station(it).id) & " and " & Positive'Image(model_ptr.station(it2).id) );
                     end if;
                  end if;
               end loop;
            end loop;
         end;
      end if;
      return model_ptr;
   end getModelFromFile;
end GetInput;
