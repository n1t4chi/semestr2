package main

import (
	"fmt"
	"math/rand"
	"strconv"
	"time"
)

//@Author: Piotr Olejarz 220398
//Track package declares record and task for tracks

/*func when(b bool, c chan int64) chan int64 {
	if !b {
		return nil
	} else {
		return c
	}
}*/

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
	Track_Type_Service  Track_Type = iota
)

//Keys for additional fields.
const (
	T_distance   string = "dist"
	T_max_speed  string = "speed"
	T_min_delay  string = "delay"
	T_station_id string = "stat"
)

//Track record with all necessary data. Additional fields based on type are stored in data field
type Track struct {
	id       int64
	t_type   Track_Type
	st_start int64
	st_end   int64
	used_by  int64
	data     map[string]int64

	out_of_order bool
	reliability  float64

	//accepts given train thus blocking track for others
	acceptTrain chan int64
	//clears out block for other trains after currently blocking train left the track.
	clearAfterTrain chan int64

	//service entries
	allowServiceTrain    chan int64
	acceptServiceTrain   chan int64
	repair               chan int64
	freeFromServiceTrain chan int64

	history []Track_History
}

//Initialises common fields for all tracks
func initTrack(id int64, st_start int64, st_end int64) *Track {
	t := new(Track)
	t.id = id
	t.out_of_order = false
	t.reliability = 0.99995
	t.st_start = st_start
	t.st_end = st_end
	t.used_by = 0
	t.data = make(map[string]int64)
	t.history = make([]Track_History, 0)
	t.acceptTrain = make(chan int64)
	t.clearAfterTrain = make(chan int64)
	t.allowServiceTrain = make(chan int64)
	t.acceptServiceTrain = make(chan int64)
	t.repair = make(chan int64)
	t.freeFromServiceTrain = make(chan int64)
	return t
}

//Creates new Track of Track_Type_Track type.
func NewTrack(id int64, st_start int64, st_end int64, distance int64, speed int64) *Track {
	t := initTrack(id, st_start, st_end)
	t.t_type = Track_Type_Track
	t.data[T_distance] = distance
	t.data[T_max_speed] = speed
	return t
}

//Creates new Track of Track_Type_Platform.
func NewPlatform(id int64, st_start int64, st_end int64, min_delay int64, station_id int64) *Track {
	t := initTrack(id, st_start, st_end)
	t.t_type = Track_Type_Platform
	t.data[T_min_delay] = min_delay
	t.data[T_station_id] = station_id
	return t
}

//Creates new Track of Track_Type_Service.
func NewServiceTrack(id int64, st_start int64, st_end int64) *Track {
	t := initTrack(id, st_start, st_end)
	t.t_type = Track_Type_Service
	return t
}

//Track task. Allows accepted trains to move/wait on them
func TrackTask(track_ptr *Track, model_ptr *Simulation_Model) {
	var type_str string
	var train_ptr *Train
	var hist *Track_History = nil

	var r *rand.Rand = rand.New(rand.NewSource(int64(time.Now().Nanosecond() + int(2*track_ptr.id) + 16384)))
	var work bool = false

	if track_ptr != nil && model_ptr != nil {
		//track naming
		if track_ptr.t_type == Track_Type_Platform {
			type_str = "Platform"
		} else if track_ptr.t_type == Track_Type_Track {
			type_str = "Track"
		} else if track_ptr.t_type == Track_Type_Service {
			type_str = "Service Track"
		} else {
			type_str = "Unknown"
		}

		var help_service_train_ptr *Train = nil
		var pass_service_train_ptr *Train = nil
		var help bool = false
		for model_ptr.work {

			if track_ptr.out_of_order == true && help == false {
				help_service_train_ptr = GetServiceTrain(model_ptr)
				if help_service_train_ptr != nil {

					select {
					case help_service_train_ptr.trackOutOfOrder <- track_ptr.id:
						help = true
					default:
						//  delay Standard.Duration(1);
					}

				} else {
					//Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(track_ptr.id)&"] received null pointer for service train" );
					fmt.Println("#2# " + type_str + "[" + strconv.FormatInt(track_ptr.id, 10) + "] received null pointer for service train")
				}
			}

			select {

			case train_id := <-when(track_ptr.used_by == 0, track_ptr.allowServiceTrain):
				pass_service_train_ptr = GetTrain(train_id, model_ptr)
				if pass_service_train_ptr != nil {
					track_ptr.used_by = train_id
					PutLine("#2# "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] received accept request from service train ["+strconv.FormatInt(pass_service_train_ptr.id, 10)+
						"]. Blocking track for other trains.", model_ptr)
				} else {
					fmt.Println("#2# " + type_str + "[" + strconv.FormatInt(track_ptr.id, 10) +
						"] received null pointer for service train ID[" + strconv.FormatInt(train_id, 10) + "]")
				}

			case train_id := <-track_ptr.acceptServiceTrain:
				if pass_service_train_ptr != nil && pass_service_train_ptr.id == train_id {
					train_ptr = pass_service_train_ptr
					work = true

					hist = new(Track_History) //_Record
					hist.arrival = time.Now()
					hist.train_id = train_id

					PutLine("#2# "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] blocked by passing service train: ["+strconv.FormatInt(pass_service_train_ptr.id, 10)+
						"]", model_ptr)
				} else {
					if track_ptr.used_by == 0 || track_ptr.used_by == train_id {
						PutLine("#2# "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
							"] received accept signal from invalid serivce train ID["+strconv.FormatInt(train_id, 10)+
							"] no service train expected. Blocking the track anyway.", model_ptr)
						pass_service_train_ptr = GetTrain(train_id, model_ptr)
						track_ptr.used_by = train_id
						train_ptr = pass_service_train_ptr
						work = true

						hist = new(Track_History) //_Record
						hist.arrival = time.Now()
						hist.train_id = train_id

					} else {
						PutLine("#2# "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
							"] received accept signal from invalid serivce train ID["+strconv.FormatInt(train_id, 10)+
							"] no service train expected. Currently used by other train.", model_ptr)
					}
				}

			case train_id := <-track_ptr.freeFromServiceTrain:
				if pass_service_train_ptr != nil && pass_service_train_ptr.id == train_id {
					PutLine("#2# "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] unblocked from service train["+strconv.FormatInt(pass_service_train_ptr.id, 10)+"].",
						model_ptr)
					track_ptr.used_by = 0
					train_ptr = nil
					pass_service_train_ptr = nil
				} else {
					if pass_service_train_ptr == nil {
						fmt.Println("#2# " + type_str + "[" + strconv.FormatInt(track_ptr.id, 10) +
							"] receive free signal from invalid serivce train ID[" + strconv.FormatInt(train_id, 10) +
							"] no service train accepted.")
					} else {
						fmt.Println("#2# " + type_str + "[" + strconv.FormatInt(track_ptr.id, 10) +
							"] receive free signal from invalid serivce train ID[" + strconv.FormatInt(train_id, 10) +
							"] accepted: [" + strconv.FormatInt(pass_service_train_ptr.id, 10) + "]")
					}
				}
			case train_id := <-track_ptr.repair:
				if help_service_train_ptr != nil && help_service_train_ptr.id == train_id {
					PutLine("#2# "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] was just repaired. Ready to accept incoming trains anew.",
						model_ptr)
					track_ptr.out_of_order = false
				} else {
					if help_service_train_ptr != nil {
						PutLine("#2# "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
							"] has no information about service train but received repair signal from service train["+strconv.FormatInt(train_id, 10)+
							"]. Accepting the repair and moving along with schedule.",
							model_ptr)
						track_ptr.out_of_order = false
					} else {
						PutLine("#2# "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
							"] received repair signal from illegal service train ["+strconv.FormatInt(train_id, 10)+
							"]. Accepting the repair and moving along with schedule.",
							model_ptr)
						track_ptr.out_of_order = false
					}
				}

			//accepts given train thus blocking track for others
			case train_id := <-when(track_ptr.out_of_order == false && train_ptr == nil, track_ptr.acceptTrain):
				train_ptr = GetTrain(train_id, model_ptr)
				if train_ptr != nil {
					hist = new(Track_History) //_Record
					hist.arrival = time.Now()
					hist.train_id = train_id
					work = true
					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] is now blocked by train["+strconv.FormatInt(train_ptr.id, 10)+"]", model_ptr)

					track_ptr.used_by = train_ptr.id
				} else {
					fmt.Println(type_str + "[" + strconv.FormatInt(track_ptr.id, 10) +
						"] received null pointer for train ID[" + strconv.FormatInt(train_id, 10) + "]")
				}

			//otherwise waits for train to clear out the steering.
			//clears out block for other trains after currently blocking train left the track.
			case train_id := <-when(track_ptr.out_of_order == false && train_ptr != nil, track_ptr.clearAfterTrain):
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
			case <-time.After(time.Second * time.Duration(GetTimeSimToRealFromModel(1.0, Time_Interval_Hour, model_ptr))):

			}

			//for given train waits specified duration and then signals the train that it's ready to depart from this track.

			if track_ptr.out_of_order == false && train_ptr != nil && work {
				work = false
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
					train_ptr.trainArrivedToTheEndOfTrack <- track_ptr.id /* chan_ready <- TrainMessageFromTrack(track_ptr.id)*/
					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] signaled the train:["+strconv.FormatInt(train_ptr.id, 10)+
						"] that it's ready to depart onto next steering", model_ptr)
					//notify train
				} else if track_ptr.t_type == Track_Type_Platform && pass_service_train_ptr == nil {
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
					//train_ptr.chan_ready <- TrainMessageFromPlatform(track_ptr.id)
					train_ptr.trainReadyToDepartFromPlatform <- track_ptr.id /* chan_ready <- TrainMessageFromTrack(track_ptr.id)*/
					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] signaled the train train["+strconv.FormatInt(train_ptr.id, 10)+
						"] that it's ready to depart onto next steering", model_ptr)
				} else { //for service tracks and platforms on which service trains moves on
					train_ptr.current_speed = 0
					delay_dur := 1.0
					real_delay_dur := GetTimeSimToRealFromModel(delay_dur, Time_Interval_Minute, model_ptr)

					PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
						"] waits on "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] for 1 minute ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)

					//delay for unknown tracks
					time.Sleep(time.Duration(real_delay_dur) * time.Second)

					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] signals the train["+strconv.FormatInt(train_ptr.id, 10)+
						"] that it's ready to depart onto next steering", model_ptr)
					//notify train
					//train_ptr.t_task.trainArrivedToTheEndOfTrack(track_ptr.id)
					train_ptr.trainArrivedToTheEndOfTrack <- track_ptr.id /* chan_ready <- TrainMessageFromTrack(track_ptr.id)*/
					PutLine(type_str+"["+strconv.FormatInt(track_ptr.id, 10)+
						"] signaled the train["+strconv.FormatInt(train_ptr.id, 10)+
						"] that it's ready to depart onto next steering", model_ptr)
				}
			}

			if track_ptr.t_type != Track_Type_Service && track_ptr.used_by == 0 && track_ptr.out_of_order == false {
				ran := r.Float64()
				//fmt.Println(type_str + "[" + strconv.FormatInt(track_ptr.id, 10) + "] rolled " + strconv.FormatFloat(ran, 'f', 3, 64) + " at time " + TimeToString(GetRelativeTime(time.Now(), model_ptr)))

				if track_ptr.reliability < ran {
					PutLine("#2# "+type_str+"["+strconv.FormatInt(track_ptr.id, 10)+"] broke at time "+TimeToString(GetRelativeTime(time.Now(), model_ptr)), model_ptr)
					track_ptr.out_of_order = true
					help = false
				}
			}

		}
		fmt.Println(type_str + "[" + strconv.FormatInt(track_ptr.id, 10) + "] terminates its execution")
	} else {
		fmt.Println("TrackTask received null pointer! Task will not work")
	}
}
