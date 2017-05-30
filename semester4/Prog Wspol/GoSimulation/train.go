package main

import (
	"fmt"
	"math/rand"
	"strconv"
	"time"
)

//@Author: Piotr Olejarz 220398
//Track file declares record and task for tracks

/*func when(b bool, c chan int64) chan int64 {
	if !b {
		return nil
	} else {
		return c
	}
}*/

type Railway_Object_Type int64

//Object types for history
const (
	Type_Steering Railway_Object_Type = iota
	Type_Track    Railway_Object_Type = iota
	Type_Platform Railway_Object_Type = iota
	Type_Train    Railway_Object_Type = iota
	Type_Service  Railway_Object_Type = iota
	Type_Unknown  Railway_Object_Type = iota
)

//History record
type Train_History struct {
	object_id   int64
	object_type Railway_Object_Type
	arrival     time.Time
	departure   time.Time
}
type Train_Type int64

const (
	Train_Type_Normal  Train_Type = iota
	Train_Type_Service Train_Type = iota
)

//Keys for additional fields.
const (
	T_capacity      string = "cap"
	T_service_track string = "strack"
	T_going_back    string = "g back" //0 false 1 - true
)

//Train record with all necessary data
type Train struct {
	id        int64
	max_speed int64

	t_type Train_Type

	track_it int

	on_track      int64
	on_steer      int64
	current_speed int64

	out_of_order bool
	reliability  float64

	data map[string]int64

	//t_task
	//chan_ready chan Train_Message
	tracklist *[]int64
	history   []Train_History

	trainReadyToDepartFromSteering chan int64
	trainReadyToDepartFromPlatform chan int64
	trainArrivedToTheEndOfTrack    chan int64

	//notifications for service train
	trackOutOfOrder    chan int64
	trainOutOfOrder    chan int64
	steeringOutOfOrder chan int64

	repair chan int64
}

func tracklistToString(train_ptr *Train) string {
	var track_list string = ""
	if train_ptr.tracklist != nil {
		for it := 0; it < len(*train_ptr.tracklist); it++ {
			if track_list != "" {
				track_list += "," + strconv.FormatInt((*train_ptr.tracklist)[it], 10)
			} else {
				track_list = strconv.FormatInt((*train_ptr.tracklist)[it], 10)
			}
		}
	}
	return track_list
}

func initTrain(id int64, max_speed int64, tracklist *[]int64) *Train {
	t := new(Train)
	t.id = id
	t.max_speed = max_speed

	t.out_of_order = false
	t.reliability = 0.6995

	t.track_it = 1
	t.on_track = 0
	t.on_steer = 0
	t.current_speed = 0
	t.tracklist = tracklist
	t.history = make([]Train_History, 0)

	t.data = make(map[string]int64)

	t.trainReadyToDepartFromSteering = make(chan int64)
	t.trainReadyToDepartFromPlatform = make(chan int64)
	t.trainArrivedToTheEndOfTrack = make(chan int64)
	t.trackOutOfOrder = make(chan int64)
	t.trainOutOfOrder = make(chan int64)
	t.steeringOutOfOrder = make(chan int64)
	t.repair = make(chan int64)

	return t
}

//Creates new train
func newTrain(id int64, max_speed int64, capacity int64, tracklist []int64) *Train {
	t := initTrain(id, max_speed, &tracklist)
	t.t_type = Train_Type_Normal
	t.data[T_capacity] = capacity
	return t
}

//Creates new service train
func newServiceTrain(id int64, max_speed int64, service_track int64) *Train {
	t := initTrain(id, max_speed, nil)
	t.t_type = Train_Type_Service
	t.data[T_service_track] = service_track
	t.data[T_going_back] = 1
	return t
}

// Train task. Cycles thourgh its tracklist and moves from track to steering to track and so on.
func TrainTask(train_ptr *Train, model_ptr *Simulation_Model) {
	var track_ptr *Track
	var steer_ptr *Steering
	var ready bool = true
	var hist *Train_History = new(Train_History) //_Record
	var hist_prev *Train_History = nil
	var type_str string

	var help_train_ptr *Train = nil
	var help_track_ptr *Track = nil
	var help_steer_ptr *Steering = nil
	var first bool = true

	var r *rand.Rand = rand.New(rand.NewSource(int64(time.Now().Nanosecond() + int(4*train_ptr.id) + 65536)))

	if train_ptr != nil && model_ptr != nil {

		if train_ptr.t_type == Train_Type_Normal {
			type_str = "Train"
			PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] prepares to start its schedule", model_ptr)
			// initialisation

			train_ptr.track_it = 1
			train_ptr.on_track = 0
			train_ptr.on_steer = 0
			train_ptr.current_speed = 0
		} else if train_ptr.t_type == Train_Type_Service {
			type_str = "Service Train"
			PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] begins waiting for service call", model_ptr)
		} else {
			type_str = "Unknown"
		}
		//retrieving first track
		//	track_ptr = GetTrack((*train_ptr.tracklist)[train_ptr.track_it-1], model_ptr)

		//	if track_ptr != nil { //checking validity
		//fmt.Println(type_str+"["+strconv.FormatInt(train_ptr.id,10)+"]  begins its ride on track["+strconv.FormatInt(track_ptr.id,10)+"]" );

		/*
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
			}*/

		var help_service_train_ptr *Train = nil
		var help bool = false
		for model_ptr.work {

			if train_ptr.t_type == Train_Type_Normal && train_ptr.out_of_order == true && help == false {
				help_service_train_ptr = GetServiceTrain(model_ptr)
				if help_service_train_ptr != nil {

					select {
					case help_service_train_ptr.trainOutOfOrder <- train_ptr.id:
						help = true
					default:
						//  delay Standard.Duration(1);
					}

				} else {
					//Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["&Positive'Image(steering_ptr.id)&"] received null pointer for service train" );
					fmt.Println(type_str + "[" + strconv.FormatInt(train_ptr.id, 10) + "] received null pointer for service train")
				}
			}

			if train_ptr.out_of_order == false && ready == true { // train is ready to depart from either track or steering
				ready = false
				hist_prev = hist
				hist = new(Train_History)                                  //_Record
				if train_ptr.tracklist != nil && train_ptr.on_track != 0 { //train is currently on track
					//retrieve next steering
					steer_ptr = GetSteering(track_ptr.st_end, model_ptr)
					if (train_ptr.t_type == Train_Type_Service && train_ptr.data[T_going_back] == 0) && train_ptr.track_it >= len(*train_ptr.tracklist) {
						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] arrived near its target. Proceeding to repair routine.", model_ptr)

						real_delay_dur := GetTimeSimToRealFromModel(1.0, Time_Interval_Hour, model_ptr)

						if help_track_ptr != nil {
							PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] will be repairing track ["+strconv.FormatInt(help_track_ptr.id, 10)+"] for next 1 hour ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)
						} else if help_train_ptr != nil {
							PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] will be repairing train ["+strconv.FormatInt(help_train_ptr.id, 10)+"] for next 1 hour ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)
						} else {
							PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] will be repairing steering ["+strconv.FormatInt(help_steer_ptr.id, 10)+"] for next 1 hour ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)
						}

						time.Sleep(time.Second * time.Duration(real_delay_dur))

						if help_track_ptr != nil {
							select {
							case help_track_ptr.repair <- train_ptr.id:
							case <-time.After(time.Second * time.Duration(2.0*real_delay_dur)):
								PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] could not reach out to train ["+strconv.FormatInt(help_train_ptr.id, 10)+"] with repair offer. Will manualy force repair.", model_ptr)
								help_track_ptr.out_of_order = false
							}

						} else if help_train_ptr != nil {
							select {
							case help_train_ptr.repair <- train_ptr.id:

							case <-time.After(time.Second * time.Duration(2.0*real_delay_dur)):
								//delay Standard.Duration(2.0*real_delay_dur);
								PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] could not reach out to track ["+strconv.FormatInt(help_track_ptr.id, 10)+"] with repair offer. Will manualy force repair.", model_ptr)
								help_train_ptr.out_of_order = false
							}
						} else {
							select {
							case help_steer_ptr.repair <- train_ptr.id:
							case <-time.After(time.Second * time.Duration(2.0*real_delay_dur)):
								PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] could not reach out to steering ["+strconv.FormatInt(help_steer_ptr.id, 10)+"] with repair offer. Will manualy force repair.", model_ptr)
								help_steer_ptr.out_of_order = false
							}
						}

						help_track_ptr = nil
						help_train_ptr = nil
						help_steer_ptr = nil
						train_ptr.tracklist = findTracklistTo(train_ptr.id, false, train_ptr.on_track, train_ptr.data[T_service_track], Type_Track, model_ptr)
						train_ptr.track_it = 1
						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] finished repairing target. Going back to service track using tracklist: "+tracklistToString(train_ptr), model_ptr)

						train_ptr.data[T_going_back] = 1
					} else if (train_ptr.t_type == Train_Type_Service && train_ptr.data[T_going_back] != 0) && train_ptr.track_it == len(*train_ptr.tracklist) {
						train_ptr.tracklist = nil
						train_ptr.track_it = 1
					} else {
						steer_ptr = GetSteering(track_ptr.st_end, model_ptr)
						if steer_ptr != nil {
							//if steering is not null then waits for it to accept this train
							train_ptr.current_speed = 0

							PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+
								"] waits for steering ["+strconv.FormatInt(track_ptr.st_end, 10)+"]", model_ptr)

							if train_ptr.t_type == Train_Type_Service && train_ptr.data[T_going_back] == 0 {
								if steer_ptr.out_of_order == true {
									real_delay_dur := GetTimeSimToRealFromModel(1.0, Time_Interval_Hour, model_ptr)

									PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] will be repairing encountered out of order steering ["+strconv.FormatInt(help_steer_ptr.id, 10)+"] for next 1 hour ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)

									time.Sleep(time.Second * time.Duration(real_delay_dur))

									select {
									case steer_ptr.repair <- train_ptr.id:
									case <-time.After(time.Second * time.Duration(2.0*real_delay_dur)):
										PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] could not reach out to steering ["+strconv.FormatInt(steer_ptr.id, 10)+"] with repair offer. Will manualy force repair.", model_ptr)
										steer_ptr.out_of_order = false
									}

								}

								//waiting for steering to accept
								steer_ptr.acceptServiceTrain <- train_ptr.id
							} else {
								//waiting for steering to accept
								//steer_ptr.chan_accept <- train_ptr.id
								steer_ptr.acceptTrain <- train_ptr.id
							}

							hist.arrival = time.Now()
							hist.object_type = Type_Steering

							train_ptr.on_steer = steer_ptr.id
							hist.object_id = steer_ptr.id

							//&& clears out currently blocked track

							PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+
								"] leaves the track ["+strconv.FormatInt(track_ptr.id, 10)+"]", model_ptr)

							//clearing the track
							if train_ptr.t_type == Train_Type_Service && (train_ptr.data[T_going_back] == 0 && train_ptr.track_it > 1) {
								track_ptr.freeFromServiceTrain <- train_ptr.id
							} else {
								track_ptr.clearAfterTrain <- train_ptr.id
							}

							//track_ptr.chan_clear <- train_ptr.id

							//track_ptr.t_task.clearAfterTrain(train_ptr.id);
							hist_prev.departure = time.Now()
							train_ptr.history = append(train_ptr.history, *hist_prev)

							train_ptr.on_track = 0
							track_ptr = nil
						} else {
							fmt.Println(type_str + "[" + strconv.FormatInt(train_ptr.id, 10) +
								"] received null pointer for steering ID[" + strconv.FormatInt(track_ptr.st_end, 10) + "].")
						}
					}
				} else if train_ptr.tracklist != nil && train_ptr.on_steer != 0 { //train is currently on steering
					//incrementing the current track iterator and retrieving next track
					train_ptr.track_it = 1 + (train_ptr.track_it % len(*train_ptr.tracklist))
					track_ptr = GetTrack((*train_ptr.tracklist)[train_ptr.track_it-1], model_ptr)

					if track_ptr != nil {
						//if track is not null { waits for it to accept this train
						train_ptr.current_speed = 0

						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+
							"] waits for track["+strconv.FormatInt(track_ptr.id, 10)+"]", model_ptr)

						if train_ptr.t_type == Train_Type_Service && train_ptr.data[T_going_back] == 0 {
							if track_ptr.out_of_order == true {
								real_delay_dur := GetTimeSimToRealFromModel(1.0, Time_Interval_Hour, model_ptr)

								PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] will be repairing encountered out of order steering ["+strconv.FormatInt(help_steer_ptr.id, 10)+"] for next 1 hour ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)

								time.Sleep(time.Second * time.Duration(real_delay_dur))

								select {
								case track_ptr.repair <- train_ptr.id:
								case <-time.After(time.Second * time.Duration(2.0*real_delay_dur)):
									PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] could not reach out to track ["+strconv.FormatInt(track_ptr.id, 10)+"] with repair offer. Will manualy force repair.", model_ptr)
									track_ptr.out_of_order = false
								}

							}

							//waiting for steering to accept
							track_ptr.acceptServiceTrain <- train_ptr.id
						} else {
							//waiting for steering to accept
							//steer_ptr.chan_accept <- train_ptr.id
							track_ptr.acceptTrain <- train_ptr.id
						}

						//waiting for track to accept
						//track_ptr.chan_accept <- train_ptr.id
						//PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+
						//	"] no longer waits for track["+strconv.FormatInt(track_ptr.id, 10)+"]", model_ptr)

						hist.arrival = time.Now()
						if track_ptr.t_type == Track_Type_Track {
							hist.object_type = Type_Track
						} else if track_ptr.t_type == Track_Type_Platform {
							hist.object_type = Type_Platform
						} else if track_ptr.t_type == Track_Type_Service {
							hist.object_type = Type_Service
						} else {
							hist.object_type = Type_Unknown
						}

						train_ptr.on_track = track_ptr.id
						hist.object_id = track_ptr.id
						//&& clears out currently blocked steering

						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+
							"] leaves the steering ["+strconv.FormatInt(steer_ptr.id, 10)+"]", model_ptr)

						//clearing the steering
						if train_ptr.t_type == Train_Type_Service && train_ptr.data[T_going_back] == 0 {
							steer_ptr.freeFromServiceTrain <- train_ptr.id
						} else {
							steer_ptr.clearAfterTrain <- train_ptr.id
						}

						//	steer_ptr.chan_clear <- train_ptr.id

						hist_prev.departure = time.Now()
						train_ptr.history = append(train_ptr.history, *hist_prev)

						train_ptr.on_steer = 0
						steer_ptr = nil
					} else {
						fmt.Println(type_str + "[" + strconv.FormatInt(train_ptr.id, 10) +
							"] received null pointer for track ID[" +
							strconv.FormatInt((*train_ptr.tracklist)[train_ptr.track_it-1], 10) + "].")
					}
				} else {

					if first == true {
						first = false
						if train_ptr.t_type == Train_Type_Normal {
							track_ptr = GetTrack((*train_ptr.tracklist)[train_ptr.track_it], model_ptr)
						} else {
							track_ptr = GetTrack(train_ptr.data[T_service_track], model_ptr)
						}

						if track_ptr != nil {
							hist.arrival = time.Now()
							train_ptr.on_track = track_ptr.id
							hist.object_id = track_ptr.id
							track_ptr.acceptTrain <- train_ptr.id

							if track_ptr.t_type == Track_Type_Track {
								hist.object_type = Type_Track
							} else if track_ptr.t_type == Track_Type_Platform {
								hist.object_type = Type_Platform
							} else {
								hist.object_type = Type_Unknown
							}
						} else {
							if train_ptr.t_type == Train_Type_Service {
								fmt.Println(type_str + "[" + strconv.FormatInt(train_ptr.id, 10) +
									"] received null pointer for track ID[" + strconv.FormatInt(train_ptr.data[T_service_track], 10) + "].")
							} else {
								fmt.Println(type_str + "[" + strconv.FormatInt(train_ptr.id, 10) +
									"] received null pointer for track ID[" + strconv.FormatInt((*train_ptr.tracklist)[train_ptr.track_it], 10) + "].")
							}
						}
					} else {
						fmt.Println(type_str + "[" + strconv.FormatInt(train_ptr.id, 10) + "] is not on neither track or steering at the moment.")
					}
				}
			} else {
				select {
				// when train_ptr.out_of_order = true =>
				case train_id := <-train_ptr.repair:
					if help_service_train_ptr != nil && help_service_train_ptr.id == train_id {
						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] was just repaired. Returning to schedule.", model_ptr)
						train_ptr.out_of_order = false
					} else {
						if help_service_train_ptr != nil {
							PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] has no information about service train but received repair signal from service train["+strconv.FormatInt(train_id, 10)+"]. Accepting the repair and moving along with schedule.", model_ptr)
							train_ptr.out_of_order = false
						} else {
							PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] received repair signal from illegal service train ["+strconv.FormatInt(train_id, 10)+"]. Accepting the repair and moving along with schedule.", model_ptr)
							train_ptr.out_of_order = false
						}
					}

					//notification from steering that train can move further
				case steer_id := <-when(train_ptr.tracklist != nil && train_ptr.out_of_order == false,
					train_ptr.trainReadyToDepartFromSteering):
					train_ptr.current_speed = 0
					PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] finished waiting on steering ["+strconv.FormatInt(steer_id, 10)+"] and is ready to move onto next track", model_ptr)
					ready = true
					//notification from platform that train can move further
				case track_id := <-when(train_ptr.tracklist != nil && train_ptr.out_of_order == false,
					train_ptr.trainReadyToDepartFromPlatform):
					train_ptr.current_speed = 0
					PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] finished waiting on platform ["+strconv.FormatInt(track_id, 10)+"] and is ready to move onto steering", model_ptr)
					ready = true
					//notification from track that train can move further
				case track_id := <-when(train_ptr.tracklist != nil && train_ptr.out_of_order == false,
					train_ptr.trainArrivedToTheEndOfTrack):
					train_ptr.current_speed = 0
					PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] finished riding on track ["+strconv.FormatInt(track_id, 10)+"] and is ready to move onto steering", model_ptr)
					ready = true

					//notification for service train from track for help
				case track_id := <-when(train_ptr.t_type == Train_Type_Service && (train_ptr.data[T_going_back] != 0 && train_ptr.on_track != 0),
					train_ptr.trackOutOfOrder):
					train_ptr.data[T_going_back] = 0
					//PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] printing model:", model_ptr)
					//PrintModel(model_ptr, model_ptr.mode)
					train_ptr.tracklist = findTracklistTo(train_ptr.id, true, train_ptr.on_track, track_id, Type_Track, model_ptr)
					if train_ptr.tracklist == nil {
						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] could not find path to the train", model_ptr)
						train_ptr.data[T_going_back] = 1
					} else {
						train_ptr.track_it = 1

						help_track_ptr = GetTrack(track_id, model_ptr)

						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] received help request from track ["+strconv.FormatInt(track_id, 10)+"]. Using tracklist: "+tracklistToString(train_ptr), model_ptr)
					}

					//notification for service train from platform for help
				case train_id := <-when(train_ptr.t_type == Train_Type_Service && (train_ptr.data[T_going_back] != 0 && train_ptr.on_track != 0),
					train_ptr.trainOutOfOrder):
					train_ptr.data[T_going_back] = 0
					//PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] printing model:", model_ptr)
					//PrintModel(model_ptr, model_ptr.mode)
					train_ptr.tracklist = findTracklistTo(train_ptr.id, true, train_ptr.on_track, train_id, Type_Train, model_ptr)
					if train_ptr.tracklist == nil {
						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] could not find path to the track", model_ptr)
						train_ptr.data[T_going_back] = 1
					} else {
						train_ptr.track_it = 1

						help_train_ptr = GetTrain(train_id, model_ptr)

						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] received help request from train ["+strconv.FormatInt(train_id, 10)+"]. Using tracklist: "+tracklistToString(train_ptr), model_ptr)
					}

					//notification for service train from train for help
				case steer_id := <-when(train_ptr.t_type == Train_Type_Service && (train_ptr.data[T_going_back] != 0 && train_ptr.on_track != 0),
					train_ptr.steeringOutOfOrder):
					train_ptr.data[T_going_back] = 0
					//PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] printing model:", model_ptr)
					//PrintModel(model_ptr, model_ptr.mode)
					train_ptr.tracklist = findTracklistTo(train_ptr.id, true, train_ptr.on_track, steer_id, Type_Steering, model_ptr)
					if train_ptr.tracklist == nil {
						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] could not find path to the steering", model_ptr)
						train_ptr.data[T_going_back] = 1
					} else {
						train_ptr.track_it = 1

						help_steer_ptr = GetSteering(steer_id, model_ptr)

						PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] received help request from steering ["+strconv.FormatInt(steer_id, 10)+"]. Using tracklist: "+tracklistToString(train_ptr), model_ptr)
					}

				case <-time.After(time.Second * time.Duration(GetTimeSimToRealFromModel(1.0, Time_Interval_Hour, model_ptr))):

				}

			}
			if train_ptr.t_type != Train_Type_Service && train_ptr.out_of_order == false {
				ran := r.Float64()
				//fmt.Println(type_str + "[" + strconv.FormatInt(train_ptr.id, 10) + "] rolled " + strconv.FormatFloat(ran, 'f', 3, 64) + " at time " + TimeToString(GetRelativeTime(time.Now(), model_ptr)))

				if train_ptr.reliability < ran {
					PutLine(type_str+"["+strconv.FormatInt(train_ptr.id, 10)+"] broke at time "+TimeToString(GetRelativeTime(time.Now(), model_ptr)), model_ptr)
					train_ptr.out_of_order = true
					help = false
				}
			}
		}
		fmt.Println(type_str + "[" + strconv.FormatInt(train_ptr.id, 10) + "] terminates its execution")
		//} else {
		//	fmt.Println(type_str + "[" + strconv.FormatInt(train_ptr.id, 10) + "] received null pointer for track ID[" +
		//		strconv.FormatInt((*train_ptr.tracklist)[train_ptr.track_it], 10) + "]. Task will terminate")
		//}

	} else {
		fmt.Println("TrainTask received null pointer! Task will terminate")
	}
}

/*
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
}*/
