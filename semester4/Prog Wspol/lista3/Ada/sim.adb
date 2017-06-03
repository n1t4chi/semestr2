with Ada.Text_IO;
with Ada.Strings.Unbounded;
with GetInput;
with Model; use Model;
with log;
with Ada.Command_Line;
with Ada.Strings.Bounded;
with train;
with steering;
with track;
with Ada.Real_Time;
with Ada.Strings.Equal_Case_Insensitive;
with station;
with worker;
with Ada.Exceptions;  use Ada.Exceptions;
--@Author: Piotr Olejarz 220398
--Sim package starts whole simulation. And requires arguments below to work properly:
--<input file path> <'talking'/'responding'>");
--<input file path> - path where simulation can find file with model configuration
--<'talking'/'waiting'> - mode in which the simulation fill run:
--+ talking - information will be printed all the time
--+ waiting - information will be printed at user request
package body sim is
   --clears screen
   procedure clear_scr is
   begin
      Ada.Text_IO.Put(ASCII.ESC & "[2J");
   end;

   --available options
   OPTION_CLEAR  : constant String:= "clear";
   OPTION_EXIT  : constant String:= "exit";
   OPTION_HELP  : constant String:= "help";
   OPTION_TRAINS  : constant String:= "trains";
   OPTION_TRACKS : constant String:= "tracks";
   OPTION_STEERINGS  : constant String:= "steerings";
   OPTION_WORKERS  : constant String:= "workers";
   OPTION_STATIONS  : constant String:= "stations";
   OPTION_MODEL  : constant String:= "model";
   OPTION_TIMETABLE_TRAINS  : constant String:= "timetable train";
   OPTION_TIMETABLE_PLATFORMS  : constant String:= "timetable platform";


   --Task used for silent mode where simulation runs in background and additional task waits for user input
   task body Silent_Task is
      input : String(1..80);
      last : Natural;
      id : Positive;
   begin
      if model_ptr /= null then
         Ada.Text_IO.Put_Line("Type '"&OPTION_HELP&"' to receive command list");
         loop
            if model_ptr.mode = model.Silent_Mode then
               Ada.Text_IO.Put("Type here: ");
            end if;

            Ada.Text_IO.Get_Line(Item => input , Last => last);


            if Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_CLEAR) then
               clear_scr;
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_TRACKS) then
               log.printTracks(model_ptr);
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_TRAINS) then
               log.printTrains(model_ptr);
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_STEERINGS) then
               log.printSteerings(model_ptr);
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_WORKERS) then
               log.printWorkers(model_ptr );
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_STATIONS) then
               log.printStations(model_ptr );
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_MODEL) then
               log.printModel(model_ptr );
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_TIMETABLE_TRAINS) then
               Ada.Text_IO.Put("type train ID for which to calculate timetable: ");
               Ada.Text_IO.Get_Line(Item => input , Last => last);
               id := Positive'Value(input(1..last));
               log.printTrainTimetable(id => id,model_ptr => model_ptr);
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_TIMETABLE_PLATFORMS) then
               Ada.Text_IO.Put("type platform ID for which to calculate timetable: ");
               Ada.Text_IO.Get_Line(Item => input , Last => last);
               id := Positive'Value(input(1..last));
               log.printTrackTimetable(id => id,model_ptr => model_ptr);
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_EXIT) then
               model.endSimulation(model_ptr);
               exit;
            elsif Ada.Strings.Equal_Case_Insensitive(input(1..last),OPTION_HELP) then
               Ada.Text_IO.Put_Line("Available commands:");
               Ada.Text_IO.Put_Line(OPTION_TRAINS & ASCII.HT & OPTION_TRACKS & ASCII.HT & OPTION_STEERINGS);
               Ada.Text_IO.Put_Line(OPTION_WORKERS & ASCII.HT & OPTION_STATIONS & ASCII.HT & OPTION_MODEL);
               Ada.Text_IO.Put_Line(OPTION_TIMETABLE_TRAINS & ASCII.HT & OPTION_TIMETABLE_PLATFORMS);
               Ada.Text_IO.Put_Line(OPTION_CLEAR & ASCII.HT & OPTION_HELP & ASCII.HT & OPTION_EXIT);
              -- Ada.Text_IO.Put_Line(OPTION_ & ASCII.HT & OPTION_ & ASCII.HT & OPTION_);
            else
               Ada.Text_IO.Put_Line("Illegal Command");
            end if;
         end loop;
      else
         Ada.Text_IO.Put_Line("Silent Mode Task received null pointer");
      end if;


   exception
      when Error : others =>
         Ada.Text_IO.Put_Line("Silent Mode Task encountered an error:");
         Ada.Text_IO.Put_Line(Exception_Information(Error));
         Ada.Text_IO.Put_Line(Exception_Message(Error));
   end Silent_Task;


   --starts simulation
   procedure simulation_start is
      package Bstr is new Ada.Strings.Bounded.Generic_Bounded_Length (Max => 100);
      input_path : Bstr.Bounded_String;
      proceed : Boolean := false;
      model_ptr : access Model.Simulation_Model := null;
      task_ptr : access Silent_Task := null;
      arg_it : Positive := 2;
     -- it : Positive;
   begin
      --checks validity of arguments
      if Ada.Command_Line.Argument_Count >=1 then
         input_path := Bstr.To_Bounded_String(Ada.Command_Line.Argument(1));
         model_ptr := GetInput.getModelFromFile(Bstr.To_String(input_path));

         if Ada.Command_Line.Argument_Count >= 2 then
            if Ada.Command_Line.Argument(2) = "talking"  then
               log.putLine("Selected talking mode for this simulation.");
               model_ptr.mode := model.Talking_Mode;
               proceed := true;
               arg_it := 3;
            elsif Ada.Command_Line.Argument(2) = "waiting" then
               log.putLine("Selected waiting mode for this simulation.");
               model_ptr.mode := model.Silent_Mode;
               proceed := true;
               arg_it := 3;
            end if;
         end if;
         if not proceed then
            log.putLine("Selected mixed mode for this simulation.");
            if Ada.Command_Line.Argument_Count >= 2 and Ada.Command_Line.Argument(2) = "mixed"  then
               arg_it := 3;
            end if;
            proceed := true;
         end if;
      end if;

      if proceed then --if arguments are valid then corresponding threads to each object are made
         if model_ptr.mode /= model.Silent_Mode then
            if Ada.Command_Line.Argument_Count >= arg_it then
               if Ada.Command_Line.Argument(arg_it) = "all" then
                  log.putLine("All logs will be displayed.");
                  arg_it := 4;
               elsif Ada.Command_Line.Argument(arg_it) = "2" then
                  log.putLine("Only logs for second task will be displayed.");
                  arg_it := 4;
                  model_ptr.log_mode := model.second_task;
               elsif Ada.Command_Line.Argument(arg_it) = "3" then
                  log.putLine("Only logs for third task will be displayed.");
                  arg_it := 4;
                  model_ptr.log_mode := model.third_task;
               else
                  log.putLine("All logs will be displayed.");
               end if;

               if Ada.Command_Line.Argument_Count >= arg_it and then Ada.Command_Line.Argument(arg_it) = "debug" then
                  log.putLine("Debug logs will be displayed.");
                  model_ptr.debug := true;
               end if;

            end if;
         end if;

         log.printModel(model_ptr,model_ptr.mode);
         model_ptr.start_time := Ada.Real_Time.Clock;

         --creating threads for steerings
         for it in model_ptr.steer'Range loop
            log.putLine("creating SteeringTask for steering: " & Positive'Image(model_ptr.steer(it).id),model_ptr);
            model_ptr.steer(it).s_task := new steering.SteeringTask(model_ptr.steer(it),model_ptr);
         end loop;
         --creating threads for tracks
         for it in model_ptr.track'Range loop
            log.putLine("creating TrackTask for track: " & Positive'Image(model_ptr.track(it).id),model_ptr);
            model_ptr.track(it).t_task := new track.TrackTask(model_ptr.track(it),model_ptr);
         end loop;


         --creating threads for trains
         for it in model_ptr.worker'Range loop
            --it := 1;
            log.putLine("Creating WorkerTask for worker: " & Positive'Image(model_ptr.worker(it).id),model_ptr);
            model_ptr.worker(it).w_task := new worker.WorkerTask(model_ptr.worker(it),model_ptr);
            --Ada.Text_IO.Put_Line("TrainTask created: " & Positive'Image(it));
         end loop;

         --creating threads for trains
         for it in model_ptr.station'Range loop
            --it := 1;
            log.putLine("Creating StationTask for station: " & Positive'Image(model_ptr.station(it).id),model_ptr);
            model_ptr.station(it).s_task := new station.StationTask(model_ptr.station(it),model_ptr);
            --Ada.Text_IO.Put_Line("TrainTask created: " & Positive'Image(it));
         end loop;

         --creating threads for trains
         for it in model_ptr.train'Range loop
            --it := 1;
            log.putLine("Creating TrainTask for train: " & Positive'Image(model_ptr.train(it).id),model_ptr);
            model_ptr.train(it).t_task := new train.TrainTask(model_ptr.train(it),model_ptr);
            --Ada.Text_IO.Put_Line("TrainTask created: " & Positive'Image(it));
         end loop;



         if model_ptr.mode /= model.Talking_Mode then
            log.putLine("Creating Console input Task.");
            task_ptr := new Silent_Task(model_ptr);
         end if;
      else --prints information otherwise
         Ada.Text_IO.Put_Line("usage: ");
         Ada.Text_IO.Put_Line("<input file path> ('mixed'/'talking'/'waiting') (!waiting -> ('all'/'2'/'3')) ('debug')");
         Ada.Text_IO.Put_Line("required arguments: ");
         Ada.Text_IO.Put_Line("<input file path>");
         Ada.Text_IO.Put_Line("<input file path> - path where simulation can find file with model configuration");
         Ada.Text_IO.Put_Line("optional arguments after required:");
         Ada.Text_IO.Put_Line("('mixed'/'talking'/'waiting') ('all'/'2'/'3') ('debug')");
         Ada.Text_IO.Put_Line("('mixed'/'talking'/'waiting') - mode in which the simulation fill run:");
         Ada.Text_IO.Put_Line("+ mixed - will both print logs and allow user to request information. It is default parameter and can be ommited.");
         Ada.Text_IO.Put_Line("+ talking - logs will be printed all the time.");
         Ada.Text_IO.Put_Line("+ waiting - information will be printed only at user request.");
         Ada.Text_IO.Put_Line("(!waiting -> ('all'/'2'/'3')) - only for mixed/talking mode! Selects which normal logs will be displayed. Does not affect debug logs");
         Ada.Text_IO.Put_Line("+ all - all normal logs will be displayed. It is default parameter and can be ommited.");
         Ada.Text_IO.Put_Line("+ 2 - only normal logs directly related to second task will be displayed.");
         Ada.Text_IO.Put_Line("+ 3 - only normal logs directly related to third task will be displayed.");
         Ada.Text_IO.Put_Line("('debug') - allows debug logs to be displayed. Does not affect normal logs. ");


      end if;
   end simulation_start;


end sim;
