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

//Enum for time intervals
type Time_Interval int64

const (
	Time_Interval_Minute Time_Interval = iota
	Time_Interval_Hour   Time_Interval = iota
)

const (
	Silent_Mode  Simulation_Mode = iota
	Talking_Mode Simulation_Mode = iota
)

/*
 * Simulation record used to store all necessary data
 */
type Simulation_Model struct {
	speed      int64
	start_time time.Time
	mode       Simulation_Mode
	steer      []*Steering
	track      []*Track
	train      []*Train

	work bool
}

func EndSimulation(model_ptr *Simulation_Model) {
	if model_ptr != nil {
		model_ptr.work = false
	}
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
		return GetTrack(train_ptr.tracklist[int(train_ptr.track_it)%int(len(train_ptr.tracklist))], model_ptr)
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
				return ("track " + txt + " , not used , speed:" + strconv.FormatInt(track_ptr.data[T_max_speed], 10) + "kmph , " +
					"dist:" + strconv.FormatInt(track_ptr.data[T_distance], 10) + "km")
			} else {
				return ("track " + txt + " , used by train[" + strconv.FormatInt(track_ptr.used_by, 10) + "] , " +
					"speed:" + strconv.FormatInt(track_ptr.data[T_max_speed], 10) + "kmph , " +
					"dist:" + strconv.FormatInt(track_ptr.data[T_distance], 10) + "km")
			}

		} else {
			if track_ptr.used_by == 0 {
				return ("platform " + txt + " , not used , delay:" + strconv.FormatInt(track_ptr.data[T_min_delay], 10) + "min")
			} else {
				return "platform " + txt + " , used by train[" + strconv.FormatInt(track_ptr.used_by, 10) + "] , delay:" + strconv.FormatInt(track_ptr.data[T_min_delay], 10) + "min"
			}
		}
	} else {
		return "null"
	}
}

// string representation of given train
func TrainToString(train_ptr *Train) string {
	var track_list string
	var pos string

	if train_ptr != nil {
		track_list = ""
		for it := 0; it < len(train_ptr.tracklist); it++ {
			if track_list != "" {
				track_list += "," + strconv.FormatInt(train_ptr.tracklist[it], 10)
			} else {
				track_list = strconv.FormatInt(train_ptr.tracklist[it], 10)
			}
		}

		if train_ptr.on_track != 0 {
			pos = "on track[" + strconv.FormatInt(train_ptr.on_track, 10) + "]"
		} else if train_ptr.on_steer != 0 {
			pos = "on steering[" + strconv.FormatInt(train_ptr.on_steer, 10) + "]"
		} else {
			pos = "nowhere"
		}

		return "train id:" +
			strconv.FormatInt(train_ptr.id, 10) +
			" , location: " + pos +
			" , max spd:" + strconv.FormatInt(train_ptr.max_speed, 10) +
			"kmph , curr spd:" + strconv.FormatInt(train_ptr.current_speed, 10) +
			"kmph , cap:" + strconv.FormatInt(train_ptr.capacity, 10) +
			" , tracklist(at " + strconv.FormatInt(int64(train_ptr.track_it), 10) +
			"){" + track_list + "}"

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
			return txt + " , not used"
		} else {
			return txt + " , used by train[" + strconv.FormatInt(steer_ptr.used_by, 10) + "]"
		}

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
