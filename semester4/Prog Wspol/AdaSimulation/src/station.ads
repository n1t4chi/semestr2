limited with model;
with worker; use worker;
--@Author: Piotr Olejarz 220398
--Station package declares record and task for stations
package station is
   type STATION;
   task type StationTask  ( stat_ptr : access STATION ; model_ptr : access model.Simulation_Model) is

      entry notifyAboutWorkerArrival (work_id : in Positive);
      entry notifyAboutWorkerDeparture (work_id : in Positive);

      entry notifyAboutTrainArrival (train_id : in Positive);
      entry notifyAboutTrainDeparture (train_id :in Positive);

      entry notifyAboutReadinessToWork (work_id : in Positive);
      entry notifyAboutFinishingTheWork (work_id : in Positive);

   end StationTask;


   type STATION is
      record
         id : Positive;
         --  platf : access model.TRACK_ARR;
         --  train : access model.TRAIN_ARR;
         s_task : access StationTask;

         trains : IntSet.Set;
         passengers : HashSet.Set;
         ready_workers : HashSet.Set;
         chosen_workers : HashSet.Set;
      end record;





end station;
