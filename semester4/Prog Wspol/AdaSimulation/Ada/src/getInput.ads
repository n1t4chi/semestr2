with Ada.Text_IO;
with Ada.Strings.Unbounded;
with Model;
--@Author: Piotr Olejarz 220398
--Package GetInput is used to recover configuration of simulation from given file and creates Simulation_Model object from that.
package GetInput is


   --gets input from file and creates Simulation_Model object.
   function getModelFromFile(Filename: String) return access Model.Simulation_Model;
end GetInput;
