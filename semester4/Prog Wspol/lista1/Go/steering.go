package main

import (
	"fmt"
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

//creates new steering
func NewSteering(id int64, min_delay int64) *Steering {
	t := new(Steering)
	t.id = id
	t.min_delay = min_delay
	t.used_by = 0
	t.chan_accept = make(chan int64)
	t.chan_clear = make(chan int64)
	return t
}

// Steering task. Allows accepted trains to switch onto their next track.
func SteeringTask(steering_ptr *Steering, model_ptr *Simulation_Model) {
	var train_ptr *Train
	var hist *Steering_History = nil

	if steering_ptr != nil && model_ptr != nil {
		for model_ptr.work {

			//If train is accepted { if below select is performed and on next cycle thread waits for clearAfterTrain.
			if train_ptr == nil { // if pointer is null { it waits until the train is accepted.
				//accepts given train thus blocking steering for others
				train_id := <-steering_ptr.chan_accept

				train_ptr = GetTrain(train_id, model_ptr)
				if train_ptr != nil {
					hist = new(Steering_History) //_Record
					hist.arrival = time.Now()
					hist.train_id = train_id

					PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
						"] blocked by passing train: ["+strconv.FormatInt(train_ptr.id, 10)+"]", model_ptr)

					steering_ptr.used_by = train_ptr.id
				} else {
					fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) + "] received null pointer for train ID[" + strconv.FormatInt(train_id, 10) + "]")
				}
			} else { //otherwise waits for train to clear out the steering.
				//clears out block for other trains after currently blocking train left the steering.
				train_id := <-steering_ptr.chan_clear
				if train_ptr.id == train_id {
					hist.departure = time.Now()
					steering_ptr.history = append(steering_ptr.history, *hist)

					PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
						"] unblocked after train["+strconv.FormatInt(train_ptr.id, 10)+"] passed by", model_ptr)

					train_ptr = nil
					steering_ptr.used_by = 0
					hist = nil
				} else {
					fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
						"] received clear out signal from invalid train:[" + strconv.FormatInt(train_id, 10) +
						"], currently used by:[" + strconv.FormatInt(steering_ptr.used_by, 10) + "]")
				}
			}

			//fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
			//	"] is after first if and has train " + strconv.FormatInt(train_ptr.id, 10))

			//for given train waits specified duration and { signals the train that it's ready to depart from this steering.
			if train_ptr != nil {
				delay_dur := float64(steering_ptr.min_delay)
				real_delay_dur := GetTimeSimToRealFromModel(delay_dur, Time_Interval_Minute, model_ptr)

				//fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
				//	"] is in second if")

				PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] with train["+strconv.FormatInt(train_ptr.id, 10)+
					"] switches tracks for "+strconv.FormatFloat(delay_dur, 'f', 3, 64)+
					" minutes ("+strconv.FormatFloat(real_delay_dur, 'f', 3, 64)+"s)", model_ptr)

				//steering delay

				//fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
				//	"] goes to sleep for:")
				//fmt.Println(time.Duration(real_delay_dur) * time.Second)

				time.Sleep(time.Duration(real_delay_dur) * time.Second)

				PutLine("Steering["+strconv.FormatInt(steering_ptr.id, 10)+
					"] signals the train["+strconv.FormatInt(train_ptr.id, 10)+
					"] that it's ready to depart onto next track", model_ptr)

				//notify train
				//train_ptr.t_task.trainReadyToDepartFromSteering(steering_ptr.id);

				//fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
				//	"] is ready to notify train")

				train_ptr.chan_ready <- TrainMessageFromSteering(steering_ptr.id)

			}
			//fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) +
			//	"] is after second if and has train " + strconv.FormatInt(train_ptr.id, 10))

		}
		fmt.Println("Steering[" + strconv.FormatInt(steering_ptr.id, 10) + "] terminates its execution")
	} else {
		fmt.Println("SteeringTask received null pointer! Task will not work")
	}

}

//Steering record with all necessary data.
type Steering struct {
	id          int64
	min_delay   int64
	used_by     int64
	chan_accept chan int64
	chan_clear  chan int64
	history     []Steering_History
}
