with steering;
with train; use train;
with Model;
--@Author: Piotr Olejarz 220398
--Dijkstra alghoritm
package dijkstra is


   type ArrN is array(Positive range <>) of Natural;
   type ArrF is array(Positive range <>) of Float;
   type ArrS is array(Positive range <>) of access steering.STEERING;

   function findTracklistTo(train_id : Positive ; block : Boolean ; start_track_id : Positive ; target_id : Positive ; target_type : Train_History_Object_Type ; model_ptr : access model.Simulation_Model) return access TRACK_ARRAY;

end dijkstra;
