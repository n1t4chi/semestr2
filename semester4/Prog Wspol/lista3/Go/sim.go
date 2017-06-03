package main

import (
	"fmt"
	"os"
	"strconv"
	"time"
)

//@Author: Piotr Olejarz 220398
//Sim file starts whole simulation. And requires arguments below to work properly:
//<input file path> <'talking'/'responding'>");
//<input file path> - path where simulation can find file with model configuration
//<'talking'/'waiting'> - mode in which the simulation fill run:
//+ talking - information will be printed all the time
//+ waiting - information will be printed at user request

//does not work
func clear_scr() {
	fmt.Print("\033[2J")
}

//Available options
const (
	OPTION_CLEAR               string = "clear"
	OPTION_EXIT                string = "exit"
	OPTION_HELP                string = "help"
	OPTION_TRAINS              string = "trains"
	OPTION_TRACKS              string = "tracks"
	OPTION_STEERINGS           string = "steerings"
	OPTION_WORKERS             string = "workers"
	OPTION_STATIONS            string = "stations"
	OPTION_MODEL               string = "model"
	OPTION_TIMETABLE_TRAINS    string = "timetable-train"
	OPTION_TIMETABLE_PLATFORMS string = "timetable-platform"
)

/*
 *Task used for silent mode where simulation runs in background and additional task waits for user input
 */
func Silent_Task(model_ptr *Simulation_Model, silentDone chan bool) {
	// task body Silent_Task {
	var input string = ""
	var id int64
	if model_ptr != nil {
		fmt.Println("Type '" + OPTION_HELP + "' to receive command list")
		for input != OPTION_EXIT {

			if model_ptr.mode == Silent_Mode {
				fmt.Print("Type here: ")
			}
			_, err := fmt.Scanln(&input)
			//fmt.Println("input:" + input + "(" + strconv.Itoa(last) + ")")
			if err == nil {
				switch input {
				case OPTION_CLEAR:
					clear_scr()
				case OPTION_TRACKS:
					PrintTracksAlways(model_ptr)
				case OPTION_TRAINS:
					PrintTrainsAlways(model_ptr)
				case OPTION_STEERINGS:
					PrintSteeringsAlways(model_ptr)
				case OPTION_WORKERS:
					PrintWorkersAlways(model_ptr)
				case OPTION_STATIONS:
					PrintSteeringsAlways(model_ptr)
				case OPTION_MODEL:
					PrintModelAlways(model_ptr)
				case OPTION_TIMETABLE_TRAINS:
					fmt.Print("type train ID for which to calculate timetable: ")
					_, err1 := fmt.Scanln(&id)
					if err1 == nil {

						//fmt.Println("train:" + strconv.FormatInt(id, 10) + "(" + strconv.Itoa(last) + ")")

						PrintTrainTimetable(id, model_ptr)
					} else {
						fmt.Scanln()
						fmt.Println("Not a number")
					}
				case OPTION_TIMETABLE_PLATFORMS:
					fmt.Print("type platform ID for which to calculate timetable: ")
					_, err1 := fmt.Scanln(&id)
					if err1 == nil {
						//fmt.Println("track:" + strconv.FormatInt(id, 10) + "(" + strconv.Itoa(last) + ")")

						PrintTrackTimetable(id, model_ptr)
					} else {
						fmt.Scanln()
						fmt.Println("Not a number")
					}
				case OPTION_EXIT:
					EndSimulation(model_ptr)
					//os.Exit(0)
					silentDone <- true
				case OPTION_HELP:
					fmt.Println("Available commands:")
					fmt.Println(OPTION_TRAINS + "\t" + OPTION_TRACKS + "\t" + OPTION_STEERINGS)
					fmt.Println(OPTION_STATIONS + "\t" + OPTION_WORKERS + "\t" + OPTION_MODEL)
					fmt.Println(OPTION_TIMETABLE_TRAINS + "\t" + OPTION_TIMETABLE_PLATFORMS)
					fmt.Println( /*OPTION_CLEAR + "\t" +*/ OPTION_HELP + "\t" + OPTION_EXIT)
					// fmt.Println(OPTION_ + "\t" + OPTION_ + "\t" + OPTION_);
				default:
					fmt.Println("Illegal Command")
				}
			} else {
				fmt.Println("Invalid input")
			}
		}
	} else {
		fmt.Println("Silent Mode Task received null pointer")
	}
}

//starts simulation
func Simulation_start() {
	var proceed bool = false
	var model_ptr *Simulation_Model
	arg_it := 2
	if len(os.Args) > 1 {
		model_ptr = getModel(os.Args[1])
		if model_ptr != nil {
			if len(os.Args) > 2 {
				if os.Args[2] == "talking" {
					model_ptr.mode = Talking_Mode
					PutLineAlways("Selected talking mode for this simulation.")
					proceed = true
					arg_it = 3
				} else if os.Args[2] == "waiting" {
					model_ptr.mode = Silent_Mode
					PutLineAlways("Selected waiting mode for this simulation.")
					proceed = true
					arg_it = 3
				}
			}
			if !proceed {
				PutLineAlways("Selected mixed mode for this simulation.")
				if len(os.Args) > 2 && os.Args[2] == "mixed" {
					arg_it = 3
				}
				proceed = true
			}
		}
	}

	if proceed {

		if model_ptr.mode != Silent_Mode {
			if len(os.Args) > arg_it {
				if os.Args[arg_it] == "all" {
					PutLineAlways("All logs will be displayed.")
					arg_it = 4
				} else if os.Args[arg_it] == "2" {
					PutLineAlways("Only logs for second task will be displayed.")
					arg_it = 4
					model_ptr.log_mode = second_task
				} else if os.Args[arg_it] == "3" {
					PutLineAlways("Only logs for third task will be displayed.")
					arg_it = 4
					model_ptr.log_mode = third_task
				} else {
					PutLineAlways("All logs will be displayed.")
				}

				if len(os.Args) > arg_it && os.Args[arg_it] == "debug" {
					PutLineAlways("Debug logs will be displayed.")
					model_ptr.debug = true
				}

			}
		}

		model_ptr.work = true
		PrintModel(model_ptr, model_ptr.mode)
		model_ptr.start_time = time.Now()

		var silentDone = make(chan bool)

		for it := 0; it < len(model_ptr.steer); it++ {
			PutLine("creating SteeringTask for steering: "+strconv.FormatInt(model_ptr.steer[it].id, 10), model_ptr)
			go SteeringTask(model_ptr.steer[it], model_ptr)
		}
		for it := 0; it < len(model_ptr.track); it++ {
			PutLine("creating TrackTask for track: "+strconv.FormatInt(model_ptr.track[it].id, 10), model_ptr)
			go TrackTask(model_ptr.track[it], model_ptr)
		}
		for it := 0; it < len(model_ptr.worker); it++ {
			PutLine("creating WorkerTask for worker: "+strconv.FormatInt(model_ptr.worker[it].id, 10), model_ptr)
			go WorkerTask(model_ptr.worker[it], model_ptr)
		}
		for it := 0; it < len(model_ptr.station); it++ {
			PutLine("creating StationTask for station: "+strconv.FormatInt(model_ptr.station[it].id, 10), model_ptr)
			go StationTask(model_ptr.station[it], model_ptr)
		}
		for it := 0; it < len(model_ptr.train); it++ {
			PutLine("creating TrainTask for train: "+strconv.FormatInt(model_ptr.train[it].id, 10), model_ptr)
			go TrainTask(model_ptr.train[it], model_ptr)
		}

		if model_ptr.mode != Talking_Mode {
			fmt.Println("creating go routine for Silent_Task")
			go Silent_Task(model_ptr, silentDone)

			<-silentDone
			//fmt.Println("go routine of Silent_Task has finished working")
		} else {
			for {
				time.Sleep(24 * time.Hour)
				fmt.Println("The ride never ends")
			}
		}
	} else {
		fmt.Println("usage: ")
		fmt.Println("<input file path> ('mixed'/'talking'/'waiting') (!waiting -> ('all'/'2'/'3')) ('debug')")
		fmt.Println("required arguments: ")
		fmt.Println("<input file path>")
		fmt.Println("<input file path> - path where simulation can find file with model configuration")
		fmt.Println("optional arguments after required:")
		fmt.Println("('mixed'/'talking'/'waiting') ('all'/'2'/'3') ('debug')")
		fmt.Println("('mixed'/'talking'/'waiting') - mode in which the simulation fill run:")
		fmt.Println("+ mixed - will both print logs and allow user to request information. It is default parameter and can be ommited.")
		fmt.Println("+ talking - logs will be printed all the time.")
		fmt.Println("+ waiting - information will be printed only at user request.")
		fmt.Println("(!waiting -> ('all'/'2'/'3')) - only for mixed/talking mode! Selects which normal logs will be displayed. Does not affect debug logs")
		fmt.Println("+ all - all normal logs will be displayed. It is default parameter and can be ommited.")
		fmt.Println("+ 2 - only normal logs directly related to second task will be displayed.")
		fmt.Println("+ 3 - only normal logs directly related to third task will be displayed.")
		fmt.Println("('debug') - allows debug logs to be displayed. Does not affect normal logs. ")

	}
}
