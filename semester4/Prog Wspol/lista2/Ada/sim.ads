with model;
--@Author: Piotr Olejarz 220398
--Main procedure starts whole simulation. And required arguments below to work properly:
--<input file path> <'talking'/'responding'>");
--<input file path> - path where simulation can find file with model configuration
--<'talking'/'waiting'> - mode in which the simulation fill run:
--+ talking - information will be printed all the time
--+ waiting - information will be printed at user request
package sim is

   task type Silent_Task(model_ptr : access Model.Simulation_Model);
   procedure simulation_start;

end sim;
