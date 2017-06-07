package main

import (
	"fmt"
	"math/rand"
	"strconv"
	"time"
)

//@Author: Piotr Olejarz 220398
//Steering file declares record and task for steerings

//history record
type Steering_History struct {
	train_id  int64
	arrival   time.Time
	departure time.Time
}

//Steering record with all necessary data.
type Steering struct {
	id        int64
	min_delay int64
	used_by   int64

	out_of_order bool
	reliability  float64

	history []Steering_History

	//accepts given train thus blocking steering for others
	acceptTrain chan int64
	//clears out block for other trains after currently blocking train left the steering.
	clearAfterTrain chan int64

	allowServiceTrain    chan int64
	acceptServiceTrain   chan int64
	repair               chan int64
	freeFromServiceTrain chan int64
}

//creates new steering
func NewSteering(id int64, min_delay int64) *Steering {
	t := new(Steering)
	t.out_of_order = false
	t.reliability = 0.99995
	t.id = id
	t.min_delay = min_delay
	t.used_by = 0
	t.acceptTrain = make(chan int64)
	t.clearAfterTrain = make(chan int64)
	t.allowServiceTrain = make(chan int64)
	t.acceptServiceTrain = make(chan int64)
	t.repair = make(chan int64)
	t.freeFromServiceTrain = make(chan int64)
	return t
}

// Steering task. Allows accepted trains to switch onto their next track.
func SteeringTask(steering_ptr *Steering, model_ptr *Simulation_Model) {
	var train_ptr *Train
	var hist *Steering_History = nil

	var r *rand.Rand = rand.New(rand.NewSource(int64(time.Now().Nanosecond() + int(3*steering_ptr.id) + 256)))

	if steering_ptr != nil && model_ptr != nil {
		var help_service_train_ptr *Train = nil
		var pass_service_train_ptr *Train = nil
		var help bool = false
		var work bool = false

		for model_ptr.work {
			PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
				"] starts new loop", model_ptr)

			if steering_ptr.out_of_order == true && help == false {
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] calls for help", model_ptr)
				help_service_train_ptr = GetServiceTrain(model_ptr)
				if help_service_train_ptr != nil {

					select {
					case help_service_train_ptr.steeringOutOfOrder <- steering_ptr.id:
						help = true
					default:
						//  delay Standard.Duration(1);
					}

				} else {
					//Ada.Text_IO.Put_Line(ustr.To_String(type_str)&"["+strconv.FormatInt(steering_ptr.id, 10)+"] received null pointer for service train" );
					fmt.Println("#2# Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
						"] received null pointer for service train")
				}
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] called for help", model_ptr)
			}
			PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
				"] enters select", model_ptr)

			select {

			case train_id := <-when(steering_ptr.used_by == 0, steering_ptr.allowServiceTrain):
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] enters allowServiceTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)
				pass_service_train_ptr = GetTrain(train_id, model_ptr)
				if pass_service_train_ptr != nil {
					steering_ptr.used_by = train_id
					PutLine("#2# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
						"] received accept request from service train ["+strconv.FormatInt(pass_service_train_ptr.id, 10)+
						"]. Blocking track for other trains.", model_ptr)
				} else {
					fmt.Println("#2# Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
						"] received null pointer for service train ID[" + strconv.FormatInt(train_id, 10) + "]")
				}
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] leaves allowServiceTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)

			case train_id := <-steering_ptr.acceptServiceTrain:
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] enters acceptServiceTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)
				if pass_service_train_ptr != nil && pass_service_train_ptr.id == train_id {
					PutLine("#2# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
						"] blocked by passing service train: ["+strconv.FormatInt(pass_service_train_ptr.id, 10)+
						"]", model_ptr)
					train_ptr = pass_service_train_ptr

					hist = new(Steering_History) //_Record
					hist.arrival = time.Now()
					hist.train_id = train_id
					work = true
				} else {
					if pass_service_train_ptr != nil {
						if steering_ptr.used_by == 0 || steering_ptr.used_by == train_id {
							PutLine("#2# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
								"] received accept signal from invalid serivce train ID["+strconv.FormatInt(train_id, 10)+
								"] no service train expected. Blocking the track anyway.", model_ptr)
							pass_service_train_ptr = GetTrain(train_id, model_ptr)
							steering_ptr.used_by = train_id
							train_ptr = pass_service_train_ptr

							hist = new(Steering_History) //_Record
							hist.arrival = time.Now()
							hist.train_id = train_id
							work = true

						} else {
							PutLine("#2# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
								"] received accept signal from invalid serivce train ID["+strconv.FormatInt(train_id, 10)+
								"] no service train expected. Currently used by other train.", model_ptr)
						}
					} else {
						PutLine("#2# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
							"] received accept signal from invalid serivce train["+strconv.FormatInt(train_id, 10)+
							"] expected: ["+strconv.FormatInt(pass_service_train_ptr.id, 10)+"]", model_ptr)
					}
				}
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] leaves acceptServiceTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)

			case train_id := <-steering_ptr.freeFromServiceTrain:
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] enters freeFromServiceTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)
				if pass_service_train_ptr != nil && pass_service_train_ptr.id == train_id {
					PutLine("#2# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
						"] unblocked from service train["+strconv.FormatInt(pass_service_train_ptr.id, 10)+"].",
						model_ptr)
					steering_ptr.used_by = 0
					train_ptr = nil
					pass_service_train_ptr = nil
				} else {
					if pass_service_train_ptr == nil {
						fmt.Println("#2# Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
							"] receive free signal from invalid serivce train ID[" + strconv.FormatInt(train_id, 10) +
							"] no service train accepted.")
					} else {
						fmt.Println("#2# Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
							"] receive free signal from invalid serivce train ID[" + strconv.FormatInt(train_id, 10) +
							"] accepted: [" + strconv.FormatInt(pass_service_train_ptr.id, 10) + "]")
					}
				}
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] leaves freeFromServiceTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)
			case train_id := <-steering_ptr.repair:
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] enters repair("+strconv.FormatInt(train_id, 10)+")", model_ptr)
				if help_service_train_ptr != nil && help_service_train_ptr.id == train_id {
					PutLine("#2# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
						"] was just repaired. Ready to accept incoming trains anew.",
						model_ptr)
					steering_ptr.out_of_order = false
				} else {
					if help_service_train_ptr != nil {
						PutLine("#2# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
							"] has no information about service train but received repair signal from service train["+strconv.FormatInt(train_id, 10)+
							"]. Accepting the repair and moving along with schedule.",
							model_ptr)
						steering_ptr.out_of_order = false
					} else {
						PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
							"] received repair signal from illegal service train ["+strconv.FormatInt(train_id, 10)+
							"]. Accepting the repair and moving along with schedule.",
							model_ptr)
						steering_ptr.out_of_order = false
					}
				}

				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] leaves repair("+strconv.FormatInt(train_id, 10)+")", model_ptr)
			//accepts given train thus blocking track for others
			case train_id := <-when(steering_ptr.out_of_order == false && train_ptr == nil, steering_ptr.acceptTrain):
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] enters acceptTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)
				train_ptr = GetTrain(train_id, model_ptr)
				if train_ptr != nil {
					hist = new(Steering_History) //_Record
					hist.arrival = time.Now()
					hist.train_id = train_id
					work = true
					PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
						"] is now blocked by train["+strconv.FormatInt(train_ptr.id, 10)+"]", model_ptr)

					steering_ptr.used_by = train_ptr.id
				} else {
					fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
						"] received null pointer for train ID[" + strconv.FormatInt(train_id, 10) + "]")
				}
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] leaves acceptTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)

			//otherwise waits for train to clear out the steering.
			//clears out block for other trains after currently blocking train left the track.
			case train_id := <-when(steering_ptr.out_of_order == false && train_ptr != nil, steering_ptr.clearAfterTrain):
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] enters clearAfterTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)
				if train_ptr.id == train_id {
					hist.departure = time.Now()
					steering_ptr.history = append(steering_ptr.history, *hist)

					PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
						"] is now unblocked from train["+strconv.FormatInt(train_ptr.id, 10)+"]", model_ptr)

					train_ptr = nil
					steering_ptr.used_by = 0
				} else {
					fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
						"] received clear out signal from invalid train:[" + strconv.FormatInt(train_id, 10) +
						"], currently used by:[" + strconv.FormatInt(steering_ptr.used_by, 10) + "]")
				}
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] leaves clearAfterTrain("+strconv.FormatInt(train_id, 10)+")", model_ptr)
			case <-time.After(time.Second * time.Duration(GetTimeSimToRealFromModel(1.0, Time_Interval_Hour, model_ptr))):

				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] had timeout in select", model_ptr)
			}
			PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
				"] leaves select", model_ptr)

			//fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
			//	"] is after first if and has train " + strconv.FormatInt(train_ptr.id, 10))

			//for given train waits specified duration and { signals the train that it's ready to depart from this steering.
			if steering_ptr.out_of_order == false && train_ptr != nil && work {
				work = false
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] starts processing train routine ", model_ptr)
				delay_dur := float64(steering_ptr.min_delay)
				real_delay_dur := GetTimeSimToRealFromModel(delay_dur, Time_Interval_Minute, model_ptr)

				PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] with train["+strconv.FormatInt(train_ptr.id, 10)+
					"] switches tracks for "+strconv.FormatFloat(delay_dur, 'f', 3, 64)+
					" minutes ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)

				//steering delay

				time.Sleep(time.Duration(real_delay_dur) * time.Second)

				PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] signals the train["+strconv.FormatInt(train_ptr.id, 10)+
					"] that it's ready to depart onto next track", model_ptr)

				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] starts train["+strconv.FormatInt(train_ptr.id, 10)+
					"].trainReadyToDepartFromSteering("+strconv.FormatInt(steering_ptr.id, 10)+")", model_ptr)
				train_ptr.trainReadyToDepartFromSteering <- steering_ptr.id
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] finished train["+strconv.FormatInt(train_ptr.id, 10)+
					"].trainReadyToDepartFromSteering("+strconv.FormatInt(steering_ptr.id, 10)+")", model_ptr)
				PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] signaled the train["+strconv.FormatInt(train_ptr.id, 10)+
					"] that it's ready to depart onto next track", model_ptr)
				PutLine("#debug# Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] finishes processing train routine ", model_ptr)

			}
			//fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
			//	"] is after second if and has train " + strconv.FormatInt(train_ptr.id, 10))

			if steering_ptr.used_by == 0 && steering_ptr.out_of_order == false {
				ran := r.Float64()
				//fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) + "] rolled " + strconv.FormatFloat(ran, 'f', 3, 64) + " at time " + TimeToString(GetRelativeTime(time.Now(), model_ptr)))

				if steering_ptr.reliability < ran {
					PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
						"] broke at time "+TimeToString(GetRelativeTime(time.Now(), model_ptr)), model_ptr)
					steering_ptr.out_of_order = true
					help = false
				}
			}
		}
		fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) + "] terminates its execution")
	} else {
		fmt.Println("SteeringTask received null pointer! Task will not work")
	}

}
