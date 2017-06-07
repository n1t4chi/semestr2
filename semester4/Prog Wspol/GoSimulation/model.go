package main

import (
	"fmt"
	"strconv"
	"time"
)

//@Author: Piotr Olejarz 220398
//File model has Simulation_Model record declaration which contains all necessery data for simulation to work
//Also has methods used to get objects like tracks from it's ID and string representations of those objects

//Enum for simulation modes
type Simulation_Mode int64

type Log_Modes int64

//Enum for time intervals
type Time_Interval int64

func when(b bool, c chan int64) chan int64 {
	if !b {
		return nil
	} else {
		return c
	}
}

func whenSTS(b bool, c chan StartTaskStruct) chan StartTaskStruct {
	if !b {
		return nil
	} else {
		return c
	}
}
func whenTSP(b bool, c chan TrainStatPair) chan TrainStatPair {
	if !b {
		return nil
	} else {
		return c
	}
}

const (
	Time_Interval_Minute Time_Interval = iota
	Time_Interval_Hour   Time_Interval = iota
)

const (
	Mixed_Mode   Simulation_Mode = iota
	Silent_Mode  Simulation_Mode = iota
	Talking_Mode Simulation_Mode = iota
)
const (
	all_output  Log_Modes = iota
	second_task Log_Modes = iota
	third_task  Log_Modes = iota
)

/*
 * Simulation record used to store all necessary data
 */
type Simulation_Model struct {
	speed      int64
	start_time time.Time
	mode       Simulation_Mode

	log_mode Log_Modes
	debug    bool

	steer   []*Steering
	track   []*Track
	train   []*Train
	platf   []*Track
	station []*Station
	worker  []*Worker

	work bool
}

func EndSimulation(model_ptr *Simulation_Model) {
	if model_ptr != nil {
		model_ptr.work = false
	}
}

//returns train with given ID
func GetServiceTrain(model_ptr *Simulation_Model) *Train {
	var train_ptr *Train
	var service_ptr *Train = nil
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.train); it++ {
			train_ptr = model_ptr.train[it]
			if train_ptr != nil && train_ptr.t_type == Train_Type_Service {
				service_ptr = train_ptr
				if service_ptr.data[T_going_back] != 0 {
					return service_ptr
				}
			}
		}
	}
	return service_ptr
}

//returns worker with given ID
func GetWorker(work_id int64, model_ptr *Simulation_Model) *Worker {
	var work_ptr *Worker
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.worker); it++ {
			work_ptr = model_ptr.worker[it]
			if work_ptr != nil && work_ptr.id == work_id {
				return work_ptr
			}
		}
	}
	return nil
}

//returns station with given ID
func GetStation(stat_id int64, model_ptr *Simulation_Model) *Station {
	var stat_ptr *Station
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.station); it++ {
			stat_ptr = model_ptr.station[it]
			if stat_ptr != nil && stat_ptr.id == stat_id {
				return stat_ptr
			}
		}
	}
	return nil
}

//returns train with given ID
func GetTrain(train_id int64, model_ptr *Simulation_Model) *Train {

	var train_ptr *Train
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.train); it++ {
			train_ptr = model_ptr.train[it]
			if train_ptr != nil && train_ptr.id == train_id {
				return train_ptr
			}
		}
	}
	return nil
}

//returns track with given ID
func GetTrack(track_id int64, model_ptr *Simulation_Model) *Track {
	var track_ptr *Track

	if model_ptr != nil {
		for it := 0; it < len(model_ptr.track); it++ {
			track_ptr = model_ptr.track[it]
			if track_ptr != nil && track_ptr.id == track_id {
				return track_ptr
			}
		}
	}
	return nil
}

//returns next track for given train
func GetNextTrack(train_ptr *Train, model_ptr *Simulation_Model) *Track {

	if train_ptr != nil {
		return GetTrack((*train_ptr.tracklist)[int(train_ptr.track_it)%int(len(*train_ptr.tracklist))], model_ptr)
	}
	return nil
}

//returns steering with given ID
func GetSteering(steering_id int64, model_ptr *Simulation_Model) *Steering {
	var steering_ptr *Steering

	if model_ptr != nil {
		for it := 0; it < len(model_ptr.steer); it++ {
			steering_ptr = model_ptr.steer[it]
			if steering_ptr != nil && steering_ptr.id == steering_id {
				return steering_ptr
			}
		}
	}
	return nil
}

//returns steering at either start of
func GetSteeringFromTrack(track_ptr *Track, end_of_track bool, model_ptr *Simulation_Model) *Steering {
	if track_ptr != nil {
		if end_of_track {
			return GetSteering(track_ptr.st_end, model_ptr)
		} else {
			return GetSteering(track_ptr.st_start, model_ptr)
		}
	}
	return nil
}

// string representation of given track
func TrackToString(track_ptr *Track) string {

	if track_ptr != nil {
		txt := "id:" + strconv.FormatInt(track_ptr.id, 10) +
			" , steerings:([" + strconv.FormatInt(track_ptr.st_start, 10) +
			"],[" + strconv.FormatInt(track_ptr.st_end, 10) + "])"
		if track_ptr.t_type == Track_Type_Track {

			if track_ptr.used_by == 0 {
				return ("track " + txt +
					" , out of order: " + strconv.FormatBool(track_ptr.out_of_order) +
					" , not used , speed:" + strconv.FormatInt(track_ptr.data[T_max_speed], 10) + "kmph , " +
					"dist:" + strconv.FormatInt(track_ptr.data[T_distance], 10) + "km")
			} else {
				return ("track " + txt +
					" , out of order: " + strconv.FormatBool(track_ptr.out_of_order) +
					" , used by train[" + strconv.FormatInt(track_ptr.used_by, 10) + "] , " +
					"speed:" + strconv.FormatInt(track_ptr.data[T_max_speed], 10) + "kmph , " +
					"dist:" + strconv.FormatInt(track_ptr.data[T_distance], 10) + "km")
			}

		} else if track_ptr.t_type == Track_Type_Platform {
			if track_ptr.used_by == 0 {
				return ("platform " + txt +
					" , station: " + strconv.FormatInt(track_ptr.data[T_station_id], 10) +
					" , out of order: " + strconv.FormatBool(track_ptr.out_of_order) +
					" , not used , delay:" + strconv.FormatInt(track_ptr.data[T_min_delay], 10) + "min")
			} else {
				return "platform " + txt +
					" , station: " + strconv.FormatInt(track_ptr.data[T_station_id], 10) +
					" , out of order: " + strconv.FormatBool(track_ptr.out_of_order) +
					" , used by train[" + strconv.FormatInt(track_ptr.used_by, 10) +
					"] , delay:" + strconv.FormatInt(track_ptr.data[T_min_delay], 10) + "min"
			}
		} else {
			if track_ptr.used_by == 0 {
				return "Service track " + txt +
					" , out of order: " + strconv.FormatBool(track_ptr.out_of_order) +
					" , not used"
			} else {
				return "Service track " + txt +
					" , out of order: " + strconv.FormatBool(track_ptr.out_of_order) +
					" , used by train[" + strconv.FormatInt(track_ptr.used_by, 10) + "]"
			}
		}
	} else {
		return "null"
	}
}

// string representation of given train
func TrainToString(train_ptr *Train) string {
	var track_list string = ""
	var station_list string = ""
	var passengers string = ""
	var pos string

	if train_ptr != nil {
		if train_ptr.t_type == Train_Type_Normal {
			for it := 0; it < len(*train_ptr.tracklist); it++ {
				if track_list != "" {
					track_list += "," + strconv.FormatInt((*train_ptr.tracklist)[it], 10)
				} else {
					track_list = strconv.FormatInt((*train_ptr.tracklist)[it], 10)
				}
			}
			for it := 0; it < len(*train_ptr.stationlist); it++ {
				if station_list != "" {
					station_list += "," + strconv.FormatInt((*train_ptr.stationlist)[it], 10)
				} else {
					station_list = strconv.FormatInt((*train_ptr.stationlist)[it], 10)
				}
			}
			for work_ptr := range *train_ptr.passengers {
				if passengers != "" {
					passengers += "," + strconv.FormatInt(work_ptr.id, 10)
				} else {
					passengers = strconv.FormatInt(work_ptr.id, 10)
				}
			}

			if train_ptr.on_track != 0 {
				pos = "on track[" + strconv.FormatInt(train_ptr.on_track, 10) + "]"
			} else if train_ptr.on_steer != 0 {
				pos = "on steering[" + strconv.FormatInt(train_ptr.on_steer, 10) + "]"
			} else {
				pos = "nowhere"
			}

			return "train id:" + strconv.FormatInt(train_ptr.id, 10) +
				" , out of order: " + strconv.FormatBool(train_ptr.out_of_order) +
				" , location: " + pos +
				" , max spd:" + strconv.FormatInt(train_ptr.max_speed, 10) +
				"kmph , curr spd:" + strconv.FormatInt(train_ptr.current_speed, 10) +
				"kmph , cap:" + strconv.FormatInt(train_ptr.data[T_capacity], 10) +
				" , tracklist(at " + strconv.FormatInt(int64(train_ptr.track_it), 10) +
				"){" + track_list + "}" +
				" , stations{" + station_list + "} uniq: " + strconv.FormatInt(int64(train_ptr.data[T_uniqueStations]), 10) +
				" , passengers{" + passengers + "}"

		} else {
			if train_ptr.tracklist != nil {
				track_list = ""
				for it := 0; it < len(*train_ptr.tracklist); it++ {
					if track_list != "" {
						track_list += "," + strconv.FormatInt((*train_ptr.tracklist)[it], 10)
					} else {
						track_list = strconv.FormatInt((*train_ptr.tracklist)[it], 10)
					}
				}
				track_list = " , tracklist(at " + strconv.FormatInt(int64(train_ptr.track_it), 10) + "): {" + track_list + "}"
			}
			if train_ptr.on_track != 0 {
				pos = "on track[" + strconv.FormatInt(train_ptr.on_track, 10) + "]"
			} else if train_ptr.on_steer != 0 {
				pos = "on steering[" + strconv.FormatInt(train_ptr.on_steer, 10) + "]"
			} else {
				pos = "nowhere"
			}

			return "service train id:" + strconv.FormatInt(train_ptr.id, 10) +
				" , out of order: " + strconv.FormatBool(train_ptr.out_of_order) +
				" , location: " + pos +
				" , max spd:" + strconv.FormatInt(train_ptr.max_speed, 10) +
				"kmph , curr spd:" + strconv.FormatInt(train_ptr.current_speed, 10) +
				" , tracklist(at " + strconv.FormatInt(int64(train_ptr.track_it), 10) + ")" +
				track_list

		}
	} else {
		return "null"
	}
}

// string representation of given steering
func SteeringToString(steer_ptr *Steering) string {
	if steer_ptr != nil {

		txt := "Steering id:" + strconv.FormatInt(steer_ptr.id, 10) +
			" , delay:" + strconv.FormatInt(steer_ptr.min_delay, 10) + "min"

		if steer_ptr.used_by == 0 {
			return txt + " , out of order: " + strconv.FormatBool(steer_ptr.out_of_order) + " , not used"
		} else {
			return txt + " , out of order: " + strconv.FormatBool(steer_ptr.out_of_order) + " , used by train[" + strconv.FormatInt(steer_ptr.used_by, 10) + "]"
		}

	} else {
		return "null"
	}
}

// string representation of given worker
func WorkerToString(work_ptr *Worker) string {
	if work_ptr != nil {
		var connection_list string = ""

		if work_ptr.connectionlist != nil {
			for it := 0; it < len(*work_ptr.connectionlist); it++ {
				conn := (*work_ptr.connectionlist)[it]
				conn_ent := "(t:" + strconv.FormatInt(conn.train_id, 10) + " ,d:" + strconv.FormatInt(conn.depart_station_id, 10) + " ,a:" + strconv.FormatInt(conn.arrive_station_id, 10) + ")"
				if connection_list != "" {
					connection_list += "," + conn_ent
				} else {
					connection_list = conn_ent
				}
			}
		}

		txt := "Worker id:" + strconv.FormatInt(work_ptr.id, 10) +
			" , home station:" + strconv.FormatInt(work_ptr.home_stat_id, 10) +
			" , at stat:" + strconv.FormatInt(work_ptr.on_Station, 10) +
			" , on train:" + strconv.FormatInt(work_ptr.on_train, 10)
		switch work_ptr.state {
		case AtHome:
			return txt + " , at home: "
		case TravellingToWork:
			txt += " , going to work at station:" + strconv.FormatInt(work_ptr.dest_Station, 10)
			if work_ptr.on_train != 0 {
				return txt +
					" , on train:" + strconv.FormatInt(work_ptr.on_train, 10) +
					" , connection list: { " + connection_list + "}"
			} else {
				return txt +
					" , at station:" + strconv.FormatInt(work_ptr.on_Station, 10) +
					" , connection list: { " + connection_list + "}"
			}
		case WaitingForWork:
			return txt + " , waiting to start work at station: " + strconv.FormatInt(work_ptr.dest_Station, 10)
		case TravellingToHome:
			txt += " , going back to home"
			if work_ptr.on_train != 0 {
				return txt +
					" , on train:" + strconv.FormatInt(work_ptr.on_train, 10) +
					" , connection list: { " + connection_list + "}"
			} else {
				return txt +
					" , at station:" + strconv.FormatInt(work_ptr.on_Station, 10) +
					" , connection list: { " + connection_list + "}"
			}
		case Working:
			return txt + " , working at station: " + strconv.FormatInt(work_ptr.dest_Station, 10)
		default:
			return txt + " , undefined state"
		}

	} else {
		return "null"
	}
}

// string representation of given station
func StationToString(stat_ptr *Station) string {
	if stat_ptr != nil {
		var passengers string = ""
		var r_work string = ""
		var c_work string = ""
		var trains string = ""

		for work_ptr := range stat_ptr.passengers {
			if passengers != "" {
				passengers += "," + strconv.FormatInt(work_ptr.id, 10)
			} else {
				passengers = strconv.FormatInt(work_ptr.id, 10)
			}
		}
		for work_ptr := range stat_ptr.ready_workers {
			if r_work != "" {
				r_work += "," + strconv.FormatInt(work_ptr.id, 10)
			} else {
				r_work = strconv.FormatInt(work_ptr.id, 10)
			}
		}
		for work_ptr := range stat_ptr.chosen_workers {
			if c_work != "" {
				c_work += "," + strconv.FormatInt(work_ptr.id, 10)
			} else {
				c_work = strconv.FormatInt(work_ptr.id, 10)
			}
		}
		for train := range stat_ptr.trains {
			if trains != "" {
				trains += "," + strconv.FormatInt(train, 10)
			} else {
				trains = strconv.FormatInt(train, 10)
			}
		}

		return "Station id: " + strconv.FormatInt(stat_ptr.id, 10) +
			" ,trains on station: { " + trains + "}" +
			" ,passengers: { " + passengers + "}" +
			" ,chosen workers: { " + c_work + "}" +
			" ,ready workers: { " + r_work + "}"

	} else {
		return "null"
	}
}

// Translates given simulation time to real time seconds
func GetTimeSimToRealFromModel(time_in_interval float64, interval Time_Interval, model_ptr *Simulation_Model) float64 {
	//speed  int64; // real-time seconds to simulation-hour ratio

	if model_ptr != nil {
		return GetTimeSimToReal(time_in_interval, interval, model_ptr.speed)
	} else {
		fmt.Println("getTrain received nil pointer. Returning 1")
		return 1.0
	}

}

// Translates given simulation time to real time seconds
func GetTimeSimToReal(time_in_interval float64, interval Time_Interval, speed int64) float64 {
	//speed  int64; // real-time seconds to simulation-hour ratio

	if interval == Time_Interval_Hour {
		return time_in_interval * float64(speed)
	} else if interval == Time_Interval_Minute {
		return time_in_interval * float64(speed) / 60.0
	} else {
		fmt.Println("getTrain received illegal interval. Returning 1")
		return 1.0
	}
}

type Connection struct {
	train_id          int64
	depart_station_id int64
	arrive_station_id int64
}

func GetConnection(source_station int64, destination_station int64, model_ptr *Simulation_Model) *[]*Connection {
	rtrn := make([]*Connection, 0)

	if source_station != destination_station {
		//var max_d_it int64 = 0
		//var rtrn_st_it int64 = 1
		var found_connection bool = false
		var depar = make([]*Train, 0)
		var arriv = make([]*Train, 0)
		//var arriv_it int64 = 0
		//var depar_it int64 = 0
		var src int64 = source_station
		var des int64 = destination_station

		var found_arrv bool
		var found_dep bool
		//var max_a_stat int64 = 0
		//var max_a_it int64 = 0
		//var max_d_stat int64 = 0
		var train_ptr *Train

		//find all trains that start from source station
		for it := 0; it < len(model_ptr.train); it++ {
			train_ptr = model_ptr.train[it]
			if train_ptr.t_type == Train_Type_Normal {
				found_arrv = false
				found_dep = false
				for itt := 0; itt < len(*train_ptr.stationlist); itt++ {

					if (*train_ptr.stationlist)[itt] == src {
						/*if max_d_stat <= train_ptr.data[T_uniqueStations] {
							max_d_it = int64(itt)
							max_d_stat = train_ptr.data[T_uniqueStations]
						}*/
						found_dep = true
					}

					if (*train_ptr.stationlist)[itt] == des {
						/*if max_a_stat <= train_ptr.data[T_uniqueStations] {
							max_a_it = int64(itt)
							max_a_stat = train_ptr.data[T_uniqueStations]
						}*/
						found_arrv = true
					}

					if found_dep && found_arrv {
						break
					}
				}
				if found_dep || found_arrv { //found direct connection between source and destination
					if found_dep && found_arrv {
						con := new(Connection)
						con.train_id = train_ptr.id
						con.depart_station_id = src
						con.arrive_station_id = des
						rtrn = append(rtrn, con)
						found_connection = true
						break
					} else {
						if found_dep {
							depar = append(depar, train_ptr)
						} else {
							arriv = append(arriv, train_ptr)
						}
					}
				}
			}
		}
		if !found_connection {
			//trying to find connection with one train switch

			for it_a := 0; it_a < len(arriv); it_a++ {
				arriv_ptr := arriv[it_a]
				if arriv_ptr != nil {
					for it_d := 0; it_d < len(depar); it_d++ {
						depar_ptr := depar[it_d]
						if depar_ptr != nil {
							for s_a := 0; s_a < len(*arriv_ptr.stationlist); s_a++ {
								for s_d := 0; s_d < len(*depar_ptr.stationlist); s_d++ {
									if (*arriv_ptr.stationlist)[s_a] == (*depar_ptr.stationlist)[s_d] {
										found_connection = true
										con1 := new(Connection)
										con2 := new(Connection)

										con1.train_id = depar_ptr.id
										con1.depart_station_id = src
										con2.train_id = arriv_ptr.id
										con2.arrive_station_id = des

										con1.arrive_station_id = (*arriv_ptr.stationlist)[s_a]
										con2.depart_station_id = (*arriv_ptr.stationlist)[s_a]
										rtrn = append(rtrn, con1)
										rtrn = append(rtrn, con2)

										break
									}
								}
								if found_connection == true {
									break
								}
							}
						}
						if found_connection == true {
							break
						}
					}
				}
				if found_connection == true {
					break
				}
			}
			//choosing train to
			if !found_connection {
				return nil
			}
		}
	}
	return &rtrn
}
