package main

import (
	"fmt"
	"math/rand"
	"reflect"
	"strconv"
	"sync"
	"time"
)

type Station struct {
	id int64

	mutex *sync.RWMutex

	trains         map[int64]bool
	passengers     map[*Worker]bool
	ready_workers  map[*Worker]bool
	chosen_workers map[*Worker]bool

	notifyAboutWorkerArrival   chan int64
	notifyAboutWorkerDeparture chan int64

	notifyAboutTrainArrival   chan int64
	notifyAboutTrainDeparture chan int64

	notifyAboutReadinessToWork  chan int64
	notifyAboutFinishingTheWork chan int64
}

func newStation(stat_id int64) *Station {
	t := new(Station)
	t.id = stat_id
	t.trains = make(map[int64]bool)
	t.passengers = make(map[*Worker]bool)
	t.ready_workers = make(map[*Worker]bool)
	t.chosen_workers = make(map[*Worker]bool)

	t.mutex = new(sync.RWMutex)

	t.notifyAboutWorkerArrival = make(chan int64)
	t.notifyAboutWorkerDeparture = make(chan int64)

	t.notifyAboutTrainArrival = make(chan int64)
	t.notifyAboutTrainDeparture = make(chan int64)

	t.notifyAboutReadinessToWork = make(chan int64)
	t.notifyAboutFinishingTheWork = make(chan int64)

	return t
}

func StationTask(stat_ptr *Station, model_ptr *Simulation_Model) {

	var ran float64
	var work_ptr *Worker
	var created_task bool = false
	var active_task bool = false

	var notify_train int64 = 0

	if model_ptr != nil && stat_ptr != nil {
		var r *rand.Rand = rand.New(rand.NewSource(int64(time.Now().Nanosecond() + int(10*stat_ptr.id+65536))))
		for model_ptr.work {
			// fmt.Println("#3#%%%%%%%%%%%%%%%%%%%%%%%%% { ### before select" );
			select {
			case train_id := <-stat_ptr.notifyAboutTrainArrival:
				stat_ptr.mutex.Lock()
				if _, ok := stat_ptr.trains[train_id]; !ok {
					PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] welcomes train["+strconv.FormatInt(train_id, 10)+"]", model_ptr)
					stat_ptr.trains[train_id] = true
					notify_train = train_id
				} else {
					fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received illegal arrival notification from train[" + strconv.FormatInt(train_id, 10) + "]")
				}
				stat_ptr.mutex.Unlock()

			case train_id := <-stat_ptr.notifyAboutTrainDeparture:
				stat_ptr.mutex.Lock()
				if _, ok := stat_ptr.trains[train_id]; ok {
					PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] bids farewell to train["+strconv.FormatInt(train_id, 10)+"]", model_ptr)
					delete(stat_ptr.trains, train_id)
				} else {
					fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received illegal departure notification from train[" + strconv.FormatInt(train_id, 10) + "]")
				}
				stat_ptr.mutex.Unlock()

			case work_id := <-stat_ptr.notifyAboutWorkerArrival:
				work_ptr = GetWorker(work_id, model_ptr)
				if work_ptr != nil {
					if _, ok := stat_ptr.passengers[work_ptr]; !ok {
						stat_ptr.passengers[work_ptr] = true
						PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] welcomes passenger["+strconv.FormatInt(work_id, 10)+"]", model_ptr)
					} else {
						fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received illegal arrival notification from worker[" + strconv.FormatInt(work_id, 10) + "]")
					}
				} else {
					fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received nil pointer for worker[" + strconv.FormatInt(work_id, 10) + "]")
				}

			case work_id := <-stat_ptr.notifyAboutWorkerDeparture:
				work_ptr = GetWorker(work_id, model_ptr)
				if work_ptr != nil {
					if _, ok := stat_ptr.passengers[work_ptr]; ok {
						PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] bids farewell to passenger["+strconv.FormatInt(work_id, 10)+"]", model_ptr)
						delete(stat_ptr.passengers, work_ptr)

						if stat_ptr.ready_workers[work_ptr] {
							delete(stat_ptr.ready_workers, work_ptr)
							fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received illegal departure notification from worker[" + strconv.FormatInt(work_id, 10) + "] before he finished task.")
						}
						if stat_ptr.chosen_workers[work_ptr] {
							delete(stat_ptr.chosen_workers, work_ptr)
							fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received illegal departure notification from worker[" + strconv.FormatInt(work_id, 10) + "] before he started task.")
						}

					} else {
						// log.printStations(model_ptr);
						// log.printWorkers(model_ptr);
						fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received illegal departure notification from worker[" + strconv.FormatInt(work_id, 10) + "]")

					}
				} else {
					fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received nil pointer for worker[" + strconv.FormatInt(work_id, 10) + "]")
				}

			case work_id := <-when(created_task,
				stat_ptr.notifyAboutReadinessToWork):
				work_ptr = GetWorker(work_id, model_ptr)
				if work_ptr != nil {

					_, ok1 := stat_ptr.passengers[work_ptr]
					_, ok2 := stat_ptr.chosen_workers[work_ptr]
					_, ok3 := stat_ptr.ready_workers[work_ptr]

					if ok1 && ok2 && !ok3 {
						PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] received notification that worker["+strconv.FormatInt(work_id, 10)+"] is ready to work", model_ptr)
						stat_ptr.ready_workers[work_ptr] = true
					} else {
						fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received illegal ready notification from worker[" + strconv.FormatInt(work_id, 10) + "]")
					}
				} else {
					fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received nil pointer for worker[" + strconv.FormatInt(work_id, 10) + "]")
				}

			case work_id := <-when(active_task,
				stat_ptr.notifyAboutFinishingTheWork):
				work_ptr = GetWorker(work_id, model_ptr)
				if work_ptr != nil {
					if stat_ptr.ready_workers[work_ptr] && stat_ptr.chosen_workers[work_ptr] {
						PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] received notification that worker["+strconv.FormatInt(work_id, 10)+"] finished his task", model_ptr)
						delete(stat_ptr.ready_workers, work_ptr)
						delete(stat_ptr.chosen_workers, work_ptr)
					} else {
						if stat_ptr.ready_workers[work_ptr] {
							delete(stat_ptr.ready_workers, work_ptr)
							fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] had worker:" + strconv.FormatInt(work_id, 10) + " only within ready worker pool.")
						} else if stat_ptr.chosen_workers[work_ptr] {
							fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received illegal finish notification from worker[" + strconv.FormatInt(work_id, 10) + "] before he started task.")
						} else {
							fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received illegal finish notification from worker[" + strconv.FormatInt(work_id, 10) + "] .")
						}

					}
				} else {
					fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] received nil pointer for worker[" + strconv.FormatInt(work_id, 10) + "]")
				}

			case <-time.After(time.Second * time.Duration(GetTimeSimToRealFromModel(1, Time_Interval_Hour, model_ptr))):
			}

			if notify_train != 0 {
				for cur := range stat_ptr.passengers {
					work_ptr = cur
					select {
					case work_ptr.notifyAboutTrainArrival <- TSPMessage(notify_train, stat_ptr.id):
					default:
					}
				}
				notify_train = 0
			}

			if created_task {
				if reflect.DeepEqual(stat_ptr.ready_workers, stat_ptr.chosen_workers) {

					ran = 5.0 + 5.0*r.Float64()

					PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] got notifications from all chosen workers. Notifying workers thath they can start working for next "+strconv.FormatFloat(ran, 'f', 3, 64)+" hours.", model_ptr)
					created_task = false
					active_task = true
					for cur := range stat_ptr.ready_workers {
						work_ptr = cur
						work_ptr.startTask <- STSMessage(stat_ptr.id, ran)
					}
				} else {
					if len(stat_ptr.ready_workers) == len(stat_ptr.chosen_workers) {
						fmt.Println("#3# station[" + strconv.FormatInt(stat_ptr.id, 10) + "] has same amount of ready and chosen workers but sets are different")
					}
				}
			} else if (!active_task) && (!created_task) {
				ran = r.Float64()
				if ran < 0.05 {
					PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] needs to have new task performed. Notifying workers", model_ptr)
					var worker_count int64 = 0
					for it := 0; it < len(model_ptr.worker); it++ {
						work_ptr = model_ptr.worker[it]
						if work_ptr.state == AtHome && r.Float64() < 0.25 {
							select {
							case work_ptr.acceptTask <- stat_ptr.id:
								//PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] choose worker["+strconv.FormatInt(work_ptr.id, 10)+"] for task.", model_ptr)
								stat_ptr.chosen_workers[work_ptr] = true
								worker_count = worker_count + 1
							case <-time.After(time.Second * time.Duration(1)):
								//PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] couldnt choose worker["+strconv.FormatInt(work_ptr.id, 10)+"] for task.", model_ptr)
							}

						}
					}
					if worker_count > 0 {
						PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] choose "+strconv.FormatInt(worker_count, 10)+" workers for task.", model_ptr)
						created_task = true
					} else {
						PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] could not choose any workers for this task. Abandoning task.", model_ptr)

					}
				}
			} else if active_task {
				if len(stat_ptr.chosen_workers) < 0 {
					PutLine("#3# station["+strconv.FormatInt(stat_ptr.id, 10)+"] and all workers finished performing a task.", model_ptr)
					active_task = false
				}
			}

		}
	} else {
		fmt.Println("#3# StationTask received nil pointer.")
	}

}
