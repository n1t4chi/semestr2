pragma Ada_95;
pragma Warnings (Off);
pragma Source_File_Name (ada_main, Spec_File_Name => "b__main.ads");
pragma Source_File_Name (ada_main, Body_File_Name => "b__main.adb");
pragma Suppress (Overflow_Check);

with System.Restrictions;
with Ada.Exceptions;

package body ada_main is

   E135 : Short_Integer; pragma Import (Ada, E135, "system__os_lib_E");
   E013 : Short_Integer; pragma Import (Ada, E013, "system__soft_links_E");
   E023 : Short_Integer; pragma Import (Ada, E023, "system__exception_table_E");
   E222 : Short_Integer; pragma Import (Ada, E222, "ada__containers_E");
   E120 : Short_Integer; pragma Import (Ada, E120, "ada__io_exceptions_E");
   E240 : Short_Integer; pragma Import (Ada, E240, "ada__numerics_E");
   E046 : Short_Integer; pragma Import (Ada, E046, "ada__strings_E");
   E048 : Short_Integer; pragma Import (Ada, E048, "ada__strings__maps_E");
   E052 : Short_Integer; pragma Import (Ada, E052, "ada__strings__maps__constants_E");
   E122 : Short_Integer; pragma Import (Ada, E122, "ada__tags_E");
   E119 : Short_Integer; pragma Import (Ada, E119, "ada__streams_E");
   E063 : Short_Integer; pragma Import (Ada, E063, "interfaces__c_E");
   E090 : Short_Integer; pragma Import (Ada, E090, "interfaces__c__strings_E");
   E025 : Short_Integer; pragma Import (Ada, E025, "system__exceptions_E");
   E138 : Short_Integer; pragma Import (Ada, E138, "system__file_control_block_E");
   E130 : Short_Integer; pragma Import (Ada, E130, "system__file_io_E");
   E133 : Short_Integer; pragma Import (Ada, E133, "system__finalization_root_E");
   E131 : Short_Integer; pragma Import (Ada, E131, "ada__finalization_E");
   E164 : Short_Integer; pragma Import (Ada, E164, "system__storage_pools_E");
   E160 : Short_Integer; pragma Import (Ada, E160, "system__finalization_masters_E");
   E158 : Short_Integer; pragma Import (Ada, E158, "system__storage_pools__subpools_E");
   E105 : Short_Integer; pragma Import (Ada, E105, "system__task_info_E");
   E221 : Short_Integer; pragma Import (Ada, E221, "ada__calendar_E");
   E219 : Short_Integer; pragma Import (Ada, E219, "ada__calendar__delays_E");
   E252 : Short_Integer; pragma Import (Ada, E252, "ada__calendar__time_zones_E");
   E061 : Short_Integer; pragma Import (Ada, E061, "system__object_reader_E");
   E041 : Short_Integer; pragma Import (Ada, E041, "system__dwarf_lines_E");
   E274 : Short_Integer; pragma Import (Ada, E274, "system__pool_global_E");
   E246 : Short_Integer; pragma Import (Ada, E246, "system__random_seed_E");
   E017 : Short_Integer; pragma Import (Ada, E017, "system__secondary_stack_E");
   E156 : Short_Integer; pragma Import (Ada, E156, "ada__strings__unbounded_E");
   E195 : Short_Integer; pragma Import (Ada, E195, "system__tasking__initialization_E");
   E036 : Short_Integer; pragma Import (Ada, E036, "system__traceback__symbolic_E");
   E006 : Short_Integer; pragma Import (Ada, E006, "ada__real_time_E");
   E117 : Short_Integer; pragma Import (Ada, E117, "ada__text_io_E");
   E203 : Short_Integer; pragma Import (Ada, E203, "system__tasking__protected_objects_E");
   E207 : Short_Integer; pragma Import (Ada, E207, "system__tasking__protected_objects__entries_E");
   E211 : Short_Integer; pragma Import (Ada, E211, "system__tasking__queuing_E");
   E270 : Short_Integer; pragma Import (Ada, E270, "system__tasking__stages_E");
   E268 : Short_Integer; pragma Import (Ada, E268, "steering_E");
   E217 : Short_Integer; pragma Import (Ada, E217, "track_E");
   E280 : Short_Integer; pragma Import (Ada, E280, "worker_E");
   E278 : Short_Integer; pragma Import (Ada, E278, "station_E");
   E264 : Short_Integer; pragma Import (Ada, E264, "train_E");
   E175 : Short_Integer; pragma Import (Ada, E175, "model_E");
   E266 : Short_Integer; pragma Import (Ada, E266, "dijkstra_E");
   E173 : Short_Integer; pragma Import (Ada, E173, "getinput_E");
   E248 : Short_Integer; pragma Import (Ada, E248, "log_E");
   E140 : Short_Integer; pragma Import (Ada, E140, "sim_E");

   Local_Priority_Specific_Dispatching : constant String := "";
   Local_Interrupt_States : constant String := "";

   Is_Elaborated : Boolean := False;

   procedure finalize_library is
   begin
      E268 := E268 - 1;
      E217 := E217 - 1;
      E280 := E280 - 1;
      E264 := E264 - 1;
      declare
         procedure F1;
         pragma Import (Ada, F1, "getinput__finalize_body");
      begin
         E173 := E173 - 1;
         F1;
      end;
      declare
         procedure F2;
         pragma Import (Ada, F2, "train__finalize_spec");
      begin
         F2;
      end;
      declare
         procedure F3;
         pragma Import (Ada, F3, "worker__finalize_spec");
      begin
         F3;
      end;
      declare
         procedure F4;
         pragma Import (Ada, F4, "track__finalize_spec");
      begin
         F4;
      end;
      declare
         procedure F5;
         pragma Import (Ada, F5, "steering__finalize_spec");
      begin
         F5;
      end;
      E207 := E207 - 1;
      declare
         procedure F6;
         pragma Import (Ada, F6, "system__tasking__protected_objects__entries__finalize_spec");
      begin
         F6;
      end;
      E117 := E117 - 1;
      declare
         procedure F7;
         pragma Import (Ada, F7, "ada__text_io__finalize_spec");
      begin
         F7;
      end;
      E156 := E156 - 1;
      declare
         procedure F8;
         pragma Import (Ada, F8, "ada__strings__unbounded__finalize_spec");
      begin
         F8;
      end;
      declare
         procedure F9;
         pragma Import (Ada, F9, "system__file_io__finalize_body");
      begin
         E130 := E130 - 1;
         F9;
      end;
      E160 := E160 - 1;
      E158 := E158 - 1;
      E274 := E274 - 1;
      declare
         procedure F10;
         pragma Import (Ada, F10, "system__pool_global__finalize_spec");
      begin
         F10;
      end;
      declare
         procedure F11;
         pragma Import (Ada, F11, "system__storage_pools__subpools__finalize_spec");
      begin
         F11;
      end;
      declare
         procedure F12;
         pragma Import (Ada, F12, "system__finalization_masters__finalize_spec");
      begin
         F12;
      end;
      declare
         procedure Reraise_Library_Exception_If_Any;
            pragma Import (Ada, Reraise_Library_Exception_If_Any, "__gnat_reraise_library_exception_if_any");
      begin
         Reraise_Library_Exception_If_Any;
      end;
   end finalize_library;

   procedure adafinal is
      procedure s_stalib_adafinal;
      pragma Import (C, s_stalib_adafinal, "system__standard_library__adafinal");

      procedure Runtime_Finalize;
      pragma Import (C, Runtime_Finalize, "__gnat_runtime_finalize");

   begin
      if not Is_Elaborated then
         return;
      end if;
      Is_Elaborated := False;
      Runtime_Finalize;
      s_stalib_adafinal;
   end adafinal;

   type No_Param_Proc is access procedure;

   procedure adainit is
      Main_Priority : Integer;
      pragma Import (C, Main_Priority, "__gl_main_priority");
      Time_Slice_Value : Integer;
      pragma Import (C, Time_Slice_Value, "__gl_time_slice_val");
      WC_Encoding : Character;
      pragma Import (C, WC_Encoding, "__gl_wc_encoding");
      Locking_Policy : Character;
      pragma Import (C, Locking_Policy, "__gl_locking_policy");
      Queuing_Policy : Character;
      pragma Import (C, Queuing_Policy, "__gl_queuing_policy");
      Task_Dispatching_Policy : Character;
      pragma Import (C, Task_Dispatching_Policy, "__gl_task_dispatching_policy");
      Priority_Specific_Dispatching : System.Address;
      pragma Import (C, Priority_Specific_Dispatching, "__gl_priority_specific_dispatching");
      Num_Specific_Dispatching : Integer;
      pragma Import (C, Num_Specific_Dispatching, "__gl_num_specific_dispatching");
      Main_CPU : Integer;
      pragma Import (C, Main_CPU, "__gl_main_cpu");
      Interrupt_States : System.Address;
      pragma Import (C, Interrupt_States, "__gl_interrupt_states");
      Num_Interrupt_States : Integer;
      pragma Import (C, Num_Interrupt_States, "__gl_num_interrupt_states");
      Unreserve_All_Interrupts : Integer;
      pragma Import (C, Unreserve_All_Interrupts, "__gl_unreserve_all_interrupts");
      Detect_Blocking : Integer;
      pragma Import (C, Detect_Blocking, "__gl_detect_blocking");
      Default_Stack_Size : Integer;
      pragma Import (C, Default_Stack_Size, "__gl_default_stack_size");
      Leap_Seconds_Support : Integer;
      pragma Import (C, Leap_Seconds_Support, "__gl_leap_seconds_support");
      Bind_Env_Addr : System.Address;
      pragma Import (C, Bind_Env_Addr, "__gl_bind_env_addr");

      procedure Runtime_Initialize (Install_Handler : Integer);
      pragma Import (C, Runtime_Initialize, "__gnat_runtime_initialize");

      Finalize_Library_Objects : No_Param_Proc;
      pragma Import (C, Finalize_Library_Objects, "__gnat_finalize_library_objects");
   begin
      if Is_Elaborated then
         return;
      end if;
      Is_Elaborated := True;
      Main_Priority := -1;
      Time_Slice_Value := -1;
      WC_Encoding := 'b';
      Locking_Policy := ' ';
      Queuing_Policy := ' ';
      Task_Dispatching_Policy := ' ';
      System.Restrictions.Run_Time_Restrictions :=
        (Set =>
          (False, False, False, False, False, False, False, False, 
           False, False, False, False, False, False, False, False, 
           False, False, False, False, False, False, False, False, 
           False, False, False, False, False, False, False, False, 
           False, False, False, False, False, False, False, False, 
           False, False, False, False, False, False, False, False, 
           False, False, False, False, False, False, False, False, 
           False, False, False, False, False, False, False, False, 
           False, False, False, False, False, False, False, False, 
           False, False, False, False, True, False, False, False, 
           False, False, False, False, False, False, False, False, 
           False, False, False),
         Value => (0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
         Violated =>
          (False, False, False, True, True, True, False, True, 
           False, False, True, True, True, True, False, False, 
           True, False, False, True, True, False, True, True, 
           False, True, True, True, True, False, False, False, 
           False, False, True, False, False, True, False, True, 
           False, False, True, False, True, False, True, True, 
           False, True, True, False, False, True, False, False, 
           True, False, True, False, True, True, True, False, 
           False, True, False, True, True, True, False, True, 
           True, False, True, True, True, True, False, False, 
           True, False, False, False, False, True, True, True, 
           False, False, False),
         Count => (0, 0, 0, 0, 11, 10, 1, 0, 0, 0),
         Unknown => (False, False, False, False, False, False, True, False, False, False));
      Priority_Specific_Dispatching :=
        Local_Priority_Specific_Dispatching'Address;
      Num_Specific_Dispatching := 0;
      Main_CPU := -1;
      Interrupt_States := Local_Interrupt_States'Address;
      Num_Interrupt_States := 0;
      Unreserve_All_Interrupts := 0;
      Detect_Blocking := 0;
      Default_Stack_Size := -1;
      Leap_Seconds_Support := 0;

      Runtime_Initialize (1);

      Finalize_Library_Objects := finalize_library'access;

      System.Soft_Links'Elab_Spec;
      System.Exception_Table'Elab_Body;
      E023 := E023 + 1;
      Ada.Containers'Elab_Spec;
      E222 := E222 + 1;
      Ada.Io_Exceptions'Elab_Spec;
      E120 := E120 + 1;
      Ada.Numerics'Elab_Spec;
      E240 := E240 + 1;
      Ada.Strings'Elab_Spec;
      E046 := E046 + 1;
      Ada.Strings.Maps'Elab_Spec;
      Ada.Strings.Maps.Constants'Elab_Spec;
      E052 := E052 + 1;
      Ada.Tags'Elab_Spec;
      Ada.Streams'Elab_Spec;
      E119 := E119 + 1;
      Interfaces.C'Elab_Spec;
      Interfaces.C.Strings'Elab_Spec;
      System.Exceptions'Elab_Spec;
      E025 := E025 + 1;
      System.File_Control_Block'Elab_Spec;
      E138 := E138 + 1;
      System.Finalization_Root'Elab_Spec;
      E133 := E133 + 1;
      Ada.Finalization'Elab_Spec;
      E131 := E131 + 1;
      System.Storage_Pools'Elab_Spec;
      E164 := E164 + 1;
      System.Finalization_Masters'Elab_Spec;
      System.Storage_Pools.Subpools'Elab_Spec;
      System.Task_Info'Elab_Spec;
      E105 := E105 + 1;
      Ada.Calendar'Elab_Spec;
      Ada.Calendar'Elab_Body;
      E221 := E221 + 1;
      Ada.Calendar.Delays'Elab_Body;
      E219 := E219 + 1;
      Ada.Calendar.Time_Zones'Elab_Spec;
      E252 := E252 + 1;
      System.Object_Reader'Elab_Spec;
      System.Dwarf_Lines'Elab_Spec;
      System.Pool_Global'Elab_Spec;
      E274 := E274 + 1;
      System.Random_Seed'Elab_Body;
      E246 := E246 + 1;
      E158 := E158 + 1;
      System.Finalization_Masters'Elab_Body;
      E160 := E160 + 1;
      System.File_Io'Elab_Body;
      E130 := E130 + 1;
      E090 := E090 + 1;
      E063 := E063 + 1;
      Ada.Tags'Elab_Body;
      E122 := E122 + 1;
      E048 := E048 + 1;
      System.Soft_Links'Elab_Body;
      E013 := E013 + 1;
      System.Os_Lib'Elab_Body;
      E135 := E135 + 1;
      System.Secondary_Stack'Elab_Body;
      E017 := E017 + 1;
      E041 := E041 + 1;
      E061 := E061 + 1;
      Ada.Strings.Unbounded'Elab_Spec;
      E156 := E156 + 1;
      System.Traceback.Symbolic'Elab_Body;
      E036 := E036 + 1;
      System.Tasking.Initialization'Elab_Body;
      E195 := E195 + 1;
      Ada.Real_Time'Elab_Spec;
      Ada.Real_Time'Elab_Body;
      E006 := E006 + 1;
      Ada.Text_Io'Elab_Spec;
      Ada.Text_Io'Elab_Body;
      E117 := E117 + 1;
      System.Tasking.Protected_Objects'Elab_Body;
      E203 := E203 + 1;
      System.Tasking.Protected_Objects.Entries'Elab_Spec;
      E207 := E207 + 1;
      System.Tasking.Queuing'Elab_Body;
      E211 := E211 + 1;
      System.Tasking.Stages'Elab_Body;
      E270 := E270 + 1;
      steering'elab_spec;
      track'elab_spec;
      worker'elab_spec;
      station'elab_spec;
      train'elab_spec;
      E175 := E175 + 1;
      E266 := E266 + 1;
      Getinput'Elab_Body;
      E173 := E173 + 1;
      E248 := E248 + 1;
      train'elab_body;
      E264 := E264 + 1;
      station'elab_body;
      E278 := E278 + 1;
      worker'elab_body;
      E280 := E280 + 1;
      track'elab_body;
      E217 := E217 + 1;
      steering'elab_body;
      E268 := E268 + 1;
      sim'elab_body;
      E140 := E140 + 1;
   end adainit;

   procedure Ada_Main_Program;
   pragma Import (Ada, Ada_Main_Program, "_ada_main");

   function main
     (argc : Integer;
      argv : System.Address;
      envp : System.Address)
      return Integer
   is
      procedure Initialize (Addr : System.Address);
      pragma Import (C, Initialize, "__gnat_initialize");

      procedure Finalize;
      pragma Import (C, Finalize, "__gnat_finalize");
      SEH : aliased array (1 .. 2) of Integer;

      Ensure_Reference : aliased System.Address := Ada_Main_Program_Name'Address;
      pragma Volatile (Ensure_Reference);

   begin
      gnat_argc := argc;
      gnat_argv := argv;
      gnat_envp := envp;

      Initialize (SEH'Address);
      adainit;
      Ada_Main_Program;
      adafinal;
      Finalize;
      return (gnat_exit_status);
   end;

--  BEGIN Object file/option list
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\model.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\dijkstra.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\getInput.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\log.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\train.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\station.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\worker.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\track.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\steering.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\sim.o
   --   S:\OneDrive\studia\semestr4\wspol\Simulation\obj\main.o
   --   -LS:\OneDrive\studia\semestr4\wspol\Simulation\obj\
   --   -LS:\OneDrive\studia\semestr4\wspol\Simulation\obj\
   --   -LS:/ada/gnat/lib/gcc/i686-pc-mingw32/4.9.4/adalib/
   --   -static
   --   -lgnarl
   --   -lgnat
   --   -Xlinker
   --   --stack=0x200000,0x1000
   --   -mthreads
   --   -Wl,--stack=0x2000000
--  END Object file/option list   

end ada_main;
