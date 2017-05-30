package main

import (
	"fmt"
	"strconv"
	"time"
)

//@Author: Piotr Olejarz 220398
//Track file declares record and task for tracks

type Railway_Object_Type int64

//Object types for history
const (
	Type_Steering Railway_Object_Type = iota
	Type_Track    Railway_Object_Type = iota
	Type_Platform Railway_Object_Type = iota
	Type_Unknown  Railway_Object_Type = iota
)

//History record
type Train_History struct {
	object_id   int64
	object_type Railway_Object_Type
	arrival     time.Time
	departure   time.Time
}

//Creates new train
func newTrain(id int64, max_speed int64, capacity int64, tracklist []int64) *Train {
	t := new(Train)
	t.id = id
	t.max_speed = max_speed
	t.capacity = capacity
	t.track_it = 1
	t.on_track = 0
	t.on_steer = 0
	t.current_speed = 0
	t.tracklist = tracklist
	t.history = make([]Train_History, 0)
	t.chan_ready = make(chan Train_Message)

	return t
}

// Train task. Cycles thourgh its tracklist and moves from track to steering to track and so on.
func TrainTask(train_ptr *Train, model_ptr *Simulation_Model) {
	var track_ptr *Track
	var steer_ptr *Steering
	var ready bool = false
	var hist *Train_History = new(Train_History) //_Record
	var hist_prev *Train_History = nil

	if train_ptr != nil && model_ptr != nil {
		PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+"] prepares to start its schedule", model_ptr)
		// initialisation
		train_ptr.track_it = 1
		train_ptr.on_track = 0
		train_ptr.on_steer = 0
		train_ptr.current_speed = 0

		//retrieving first track
		track_ptr = GetTrack(train_ptr.tracklist[train_ptr.track_it-1], model_ptr)

		if track_ptr != nil { //checking validity
			//fmt.Println("Train["+strconv.FormatInt(train_ptr.id,10)+"]  begins its ride on track["+strconv.FormatInt(track_ptr.id,10)+"]" );

			hist.arrival = time.Now()
			train_ptr.on_track = track_ptr.id
			hist.object_id = track_ptr.id

			//waiting for first track to accept
			//track_ptr.t_task.acceptTrain(train_ptr.id);
			track_ptr.chan_accept <- train_ptr.id
			if track_ptr.t_type == Track_Type_Track {
				hist.object_type = Type_Track
			} else if track_ptr.t_type == Track_Type_Platform {
				hist.object_type = Type_Platform
			} else {
				hist.object_type = Type_Unknown
			}
			for model_ptr.work {
				if ready { // train is ready to depart from either track or steering
					ready = false
					hist_prev = hist
					hist = new(Train_History)    //_Record
					if train_ptr.on_track != 0 { //train is currently on track
						//retrieve next steering
						steer_ptr = GetSteering(track_ptr.st_end, model_ptr)
						if steer_ptr != nil {
							//if steering is not null { waits for it to accept this train
							train_ptr.current_speed = 0

							PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
								"] waits for steering ["+strconv.FormatInt(track_ptr.st_end, 10)+"]", model_ptr)

							//waiting for steering to accept
							steer_ptr.chan_accept <- train_ptr.id
							//steer_ptr.s_task.acceptTrain(train_ptr.id);

							hist.arrival = time.Now()
							hist.object_type = Type_Steering

							train_ptr.on_steer = steer_ptr.id
							hist.object_id = steer_ptr.id

							//and then clears out currently blocked track

							PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
								"] leaves the track ["+strconv.FormatInt(track_ptr.id, 10)+"]", model_ptr)

							//clearing the track
							track_ptr.chan_clear <- train_ptr.id
							//track_ptr.t_task.clearAfterTrain(train_ptr.id);
							hist_prev.departure = time.Now()
							train_ptr.history = append(train_ptr.history, *hist_prev)

							train_ptr.on_track = 0
							track_ptr = nil
						} else {
							fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) +
								"] received null pointer for steering ID[" + strconv.FormatInt(track_ptr.st_end, 10) + "].")
						}
					} else if train_ptr.on_steer != 0 { //train is currently on steering
						//incrementing the current track iterator and retrieving next track
						train_ptr.track_it = 1 + (train_ptr.track_it % len(train_ptr.tracklist))
						track_ptr = GetTrack(train_ptr.tracklist[train_ptr.track_it-1], model_ptr)
						if track_ptr != nil {
							//if track is not null { waits for it to accept this train
							train_ptr.current_speed = 0

							PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
								"] waits for track["+strconv.FormatInt(track_ptr.id, 10)+"]", model_ptr)

							//waiting for track to accept
							track_ptr.chan_accept <- train_ptr.id
							PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
								"] no longer waits for track["+strconv.FormatInt(track_ptr.id, 10)+"]", model_ptr)

							hist.arrival = time.Now()
							if track_ptr.t_type == Track_Type_Track {
								hist.object_type = Type_Track
							} else if track_ptr.t_type == Track_Type_Platform {
								hist.object_type = Type_Platform
							} else {
								hist.object_type = Type_Unknown
							}

							train_ptr.on_track = track_ptr.id
							hist.object_id = track_ptr.id
							//and then clears out currently blocked steering

							PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
								"] leaves the steering ["+strconv.FormatInt(steer_ptr.id, 10)+"]", model_ptr)

							//clearing the steering
							steer_ptr.chan_clear <- train_ptr.id
							hist_prev.departure = time.Now()
							train_ptr.history = append(train_ptr.history, *hist_prev)

							train_ptr.on_steer = 0
							steer_ptr = nil
						} else {
							fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) +
								"] received null pointer for track ID[" +
								strconv.FormatInt(train_ptr.tracklist[train_ptr.track_it-1], 10) + "].")
						}
					} else {
						fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) + "] is not on neither track or steering at the moment.")
					}

				} else { // train is not ready to depart and waits for notification from currently blocked track or steering.
					msg := <-train_ptr.chan_ready

					switch msg.object_type {
					case Type_Steering: //notification from steering that train can move further
						//fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) +
						//	"] received info from steering " + strconv.FormatInt(msg.object_id, 10))
						if steer_ptr != nil && msg.object_id == steer_ptr.id {
							train_ptr.current_speed = 0

							PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
								"] finished waiting on steering ["+strconv.FormatInt(steer_ptr.id, 10)+
								"] and is ready to move onto next track", model_ptr)

							ready = true
						} else {
							var txt string

							if train_ptr.on_steer != 0 {
								txt = "steering[" + strconv.FormatInt(train_ptr.on_steer, 10) + "]"
							} else if train_ptr.on_track != 0 {
								txt = "track[" + strconv.FormatInt(train_ptr.on_track, 10) + "]"
							} else {
								txt = "nowhere"
							}

							fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) +
								"] received ready signal from invalid steering:[" + strconv.FormatInt(msg.object_id, 10) +
								"], currently on: " + txt)
						}
					case Type_Platform: //notification from platform that train can move further
						//fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) +
						//	"] received info from platfrom " + strconv.FormatInt(msg.object_id, 10))
						if track_ptr != nil && msg.object_id == track_ptr.id {
							train_ptr.current_speed = 0

							PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
								"] finished waiting on platform ["+strconv.FormatInt(track_ptr.id, 10)+
								"] and is ready to move onto steering", model_ptr)

							ready = true
						} else {
							var txt string
							if train_ptr.on_steer != 0 {
								txt = "steering[" + strconv.FormatInt(train_ptr.on_steer, 10) + "]"
							} else if train_ptr.on_track != 0 {
								txt = "track[" + strconv.FormatInt(train_ptr.on_track, 10) + "]"
							} else {
								txt = "nowhere"
							}

							fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) +
								"] received ready signal from invalid platform:[" + strconv.FormatInt(msg.object_id, 10) +
								"], currently on: " + txt)
						}
					case Type_Track: //notification from track that train can move further
						//fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) +
						//	"] received info from track" + strconv.FormatInt(msg.object_id, 10))
						if track_ptr != nil && msg.object_id == track_ptr.id {
							train_ptr.current_speed = 0

							PutLine("Train["+strconv.FormatInt(train_ptr.id, 10)+
								"] finished riding on track ["+strconv.FormatInt(track_ptr.id, 10)+
								"] and is ready to move onto steering", model_ptr)

							ready = true
						} else {
							var txt string
							if train_ptr.on_steer != 0 {
								txt = "steering[" + strconv.FormatInt(train_ptr.on_steer, 10) + "]"
							} else if train_ptr.on_track != 0 {
								txt = "track[" + strconv.FormatInt(train_ptr.on_track, 10) + "]"
							} else {
								txt = "nowhere"
							}
							fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) +
								"] received ready signal from invalid track:[" + strconv.FormatInt(msg.object_id, 10) +
								"], currently on: " + txt)
						}
					}
				}
			}
			fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) + "] terminates its execution")
		} else {
			fmt.Println("Train[" + strconv.FormatInt(train_ptr.id, 10) + "] received null pointer for track ID[" +
				strconv.FormatInt(train_ptr.tracklist[train_ptr.track_it], 10) + "]. Task will terminate")
		}

	} else {
		fmt.Println("TrainTask received null pointer! Task will terminate")
	}
}

//Struct for messages for trains
type Train_Message struct {
	object_id   int64
	object_type Railway_Object_Type
}

//Creates message for train from track
func TrainMessageFromTrack(id int64) Train_Message {
	msg := new(Train_Message)
	msg.object_id = id
	msg.object_type = Type_Track
	return *msg
}

//Creates message for train from platform
func TrainMessageFromPlatform(id int64) Train_Message {
	msg := new(Train_Message)
	msg.object_id = id
	msg.object_type = Type_Platform
	return *msg
}

//Creates message for train from steering
func TrainMessageFromSteering(id int64) Train_Message {
	msg := new(Train_Message)
	msg.object_id = id
	msg.object_type = Type_Steering
	return *msg
}

//Train record with all necessary data
type Train struct {
	id            int64
	max_speed     int64
	capacity      int64
	track_it      int
	on_track      int64
	on_steer      int64
	current_speed int64
	//t_task
	chan_ready chan Train_Message
	tracklist  []int64
	history    []Train_History
}
