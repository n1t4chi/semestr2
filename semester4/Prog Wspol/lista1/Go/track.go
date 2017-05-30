package main

import (
	"fmt"
	"strconv"
	"time"
)

//@Author: Piotr Olejarz 220398
//Track package declares record and task for tracks

//History record
type Track_History struct {
	train_id  int64
	arrival   time.Time
	departure time.Time
}

//Types of tracks
type Track_Type int64

const (
	Track_Type_Unknown  Track_Type = iota
	Track_Type_Track    Track_Type = iota
	Track_Type_Platform Track_Type = iota
)

//Initialises common fields for all tracks
func initTrack(id int64, st_start int64, st_end int64) *Track {
	t := new(Track)
	t.id = id
	t.st_start = st_start
	t.st_end = st_end
	t.used_by = 0
	t.history = make([]Track_History, 0)
	t.chan_accept = make(chan int64)
	t.chan_clear = make(chan int64)
	return t
}

//Creates new Track of Track_Type_Track type.
func NewTrack(id int64, st_start int64, st_end int64, distance int64, speed int64) *Track {
	t := initTrack(id, st_start, st_end)
	t.t_type = Track_Type_Track
	t.data = make(map[string]int64)
	t.data[T_distance] = distance
	t.data[T_max_speed] = speed
	return t
}

//Creates new Track of Track_Type_Platform.
func NewPlatform(id int64, st_start int64, st_end int64, min_delay int64) *Track {
	t := initTrack(id, st_start, st_end)
	t.used_by = 0
	t.t_type = Track_Type_Platform
	t.data = make(map[string]int64)
	t.data[T_min_delay] = min_delay
	return t
}

//Track task. Allows accepted trains to move/wait on them
func TrackTask(track_ptr *Track, model_ptr *Simulation_Model) {
	var type_str string
	var train_ptr *Train
	var hist *Track_History = nil

	if track_ptr != nil && model_ptr != nil {
		//track naming
		if track_ptr.t_type == Track_Type_Platform {
			type_str = "Platform"
		} else if track_ptr.t_type == Track_Type_Track {
			type_str = "Track"
		} else {
			type_str = "Unknown"
		}

		for model_ptr.work {
			//If train is accepted then below if statement is performed and on next cycle thread waits for clearAfterTrain.
			if train_ptr == nil { // if pointer is null then it waits until the train is accepted.
				var train_id int64
				//if track_ptr.id == 2 {
				//fmt.Println("$%$%$%$%" + type_str + "[" + strconv.FormatInt(track_ptr.id, 10) + "] waits on chan_accept")
				//}
				//go func() { track_ptr.chan_accept <- 1 }()
				train_id = <-track_ptr.chan_accept

				//if track_ptr.id == 2 {
				//fmt.Println("$%$%$%$%" + type_str + "[" + strconv.FormatInt(track_ptr.id, 10) + "] accepted on chan_accept: " + strconv.FormatInt(train_id, 10))
				//}

				train_ptr = GetTrain(train_id, model_ptr)
				if train_ptr != nil {
					hist = new(Track_History) //_Record
					hist.arrival = time.Now()
					hist.train_id = train_id

					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] is now blocked by train["+strconv.FormatInt(train_ptr.id, 10)+"]", model_ptr)

					track_ptr.used_by = train_ptr.id
				} else {
					fmt.Println(type_str + "[" + strconv.FormatInt(track_ptr.id, 10) +
						"] received null pointer for train ID[" + strconv.FormatInt(train_id, 10) + "]")
				}
			} else { //otherwise waits for train to clear out the steering.
				//if track_ptr.id == 2 {
				//	fmt.Println("$%$%$%$%" + type_str + "[" + strconv.FormatInt(track_ptr.id, 10) + "] waits on chan_clear")
				//}
				train_id := <-track_ptr.chan_clear
				//if track_ptr.id == 2 {
				//	fmt.Println("$%$%$%$%" + type_str + "[" + strconv.FormatInt(track_ptr.id, 10) + "] accepted on chan_clear: " + strconv.FormatInt(train_id, 10))
				//}

				if train_ptr.id == train_id {
					hist.departure = time.Now()
					track_ptr.history = append(track_ptr.history, *hist)

					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] is now unblocked from train["+strconv.FormatInt(train_ptr.id, 10)+"]", model_ptr)

					train_ptr = nil
					track_ptr.used_by = 0
				} else {
					fmt.Println(type_str + "[" + strconv.FormatInt(track_ptr.id, 10) +
						"] received clear out signal from invalid train:[" + strconv.FormatInt(train_id, 10) +
						"], currently used by:[" + strconv.FormatInt(track_ptr.used_by, 10) + "]")
				}
			}

			//for given train waits specified duration and then signals the train that it's ready to depart from this track.

			if train_ptr != nil {
				var delay_dur, real_delay_dur float64
				//track delay based on track type
				if track_ptr.t_type == Track_Type_Track {
					//for normal tracks checks speed the train can move on this track.
					if track_ptr.data[T_max_speed] < train_ptr.max_speed {
						train_ptr.current_speed = track_ptr.data[T_max_speed]
						delay_dur = float64(track_ptr.data[T_distance]) / float64(track_ptr.data[T_max_speed])
						real_delay_dur = GetTimeSimToRealFromModel(delay_dur, Time_Interval_Hour, model_ptr)

						PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
							"] moves on track["+strconv.FormatInt(track_ptr.id, 10)+
							"] with track max speed for next"+strconv.FormatFloat(delay_dur, 'f', 3, 64)+
							" hours ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)

					} else {
						train_ptr.current_speed = train_ptr.max_speed
						delay_dur = float64(track_ptr.data[T_distance]) / float64(train_ptr.max_speed)
						real_delay_dur = GetTimeSimToRealFromModel(delay_dur, Time_Interval_Hour, model_ptr)

						PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
							"] moves with its top speed on track["+strconv.FormatInt(track_ptr.id, 10)+
							"] for next "+strconv.FormatFloat(delay_dur, 'f', 3, 64)+" hours ("+
							strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)

					}
					//delay for tracks
					time.Sleep(time.Duration(real_delay_dur) * time.Second)

					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] signals the train:["+strconv.FormatInt(train_ptr.id, 10)+
						"] that it's ready to depart onto next steering", model_ptr)
					//notify train
					train_ptr.chan_ready <- TrainMessageFromTrack(track_ptr.id)
				} else if track_ptr.t_type == Track_Type_Platform {
					train_ptr.current_speed = 0
					delay_dur := float64(track_ptr.data[T_min_delay])
					real_delay_dur := GetTimeSimToRealFromModel(delay_dur, Time_Interval_Minute, model_ptr)

					//fmt.Println("["&sim_delay_str&"]");

					PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
						"] waits on platform["+strconv.FormatInt(track_ptr.id, 10)+
						"] for next "+strconv.FormatFloat(delay_dur, 'f', 3, 64)+
						" minutes ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)

					//delay for platforms
					time.Sleep(time.Duration(real_delay_dur) * time.Second)

					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] signals the train train["+strconv.FormatInt(train_ptr.id, 10)+
						"] that it's ready to depart onto next steering", model_ptr)
					//notify train
					train_ptr.chan_ready <- TrainMessageFromPlatform(track_ptr.id)
				} else { //safe clause
					train_ptr.current_speed = 0
					delay_dur := 1.0
					real_delay_dur := GetTimeSimToRealFromModel(delay_dur, Time_Interval_Minute, model_ptr)

					PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
						"] waits on unindetified track ["+strconv.FormatInt(track_ptr.id, 10)+
						"] for 1 minute ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)

					//delay for unknown tracks
					time.Sleep(time.Duration(real_delay_dur) * time.Second)

					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] signals the train["+strconv.FormatInt(train_ptr.id, 10)+
						"] that it's ready to depart onto next steering", model_ptr)
					//notify train
					//train_ptr.t_task.trainArrivedToTheEndOfTrack(track_ptr.id)
					train_ptr.chan_ready <- TrainMessageFromTrack(track_ptr.id)
				}
			}
		}
		fmt.Println(type_str + "[" + strconv.FormatInt(track_ptr.id, 10) + "] terminates its execution")
	} else {
		fmt.Println("TrackTask received null pointer! Task will not work")
	}
}

//Keys for additional fields.
const (
	T_distance  string = "dist"
	T_max_speed string = "speed"
	T_min_delay string = "delay"
)

//Track record with all necessary data. Additional fields based on type are stored in data field
type Track struct {
	id          int64
	t_type      Track_Type
	st_start    int64
	st_end      int64
	used_by     int64
	data        map[string]int64
	chan_accept chan int64
	chan_clear  chan int64
	history     []Track_History
}
