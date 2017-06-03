package main

import (
	"fmt"
	"strconv"
	"time"
)

type WorkerState int64

const (
	AtHome           WorkerState = iota
	TravellingToWork WorkerState = iota
	WaitingForWork   WorkerState = iota
	TravellingToHome WorkerState = iota
	Working          WorkerState = iota
)

type Worker struct {
	id                      int64
	home_stat_id            int64
	on_train                int64
	on_Station              int64
	dest_Station            int64
	connectionlist          *[]*Connection
	connectionlist_iterator int64
	state                   WorkerState

	acceptTask              chan int64
	startTask               chan StartTaskStruct
	trainStop               chan TrainStatPair
	notifyAboutTrainArrival chan TrainStatPair
}

func TSPMessage(train_id int64, stat_id int64) TrainStatPair {
	t := new(TrainStatPair)
	t.stat_id = stat_id
	t.train_id = train_id
	return *t
}
func STSMessage(stat_id int64, work_time float64) StartTaskStruct {
	t := new(StartTaskStruct)
	t.stat_id = stat_id
	t.work_time_hours = work_time
	return *t
}

type TrainStatPair struct {
	train_id int64
	stat_id  int64
}

type StartTaskStruct struct {
	stat_id         int64
	work_time_hours float64
}

func newWorker(id int64, home_stat int64) *Worker {
	t := new(Worker)
	t.id = id
	t.home_stat_id = home_stat
	t.on_train = 0
	t.on_Station = home_stat
	t.dest_Station = 0
	t.connectionlist = nil
	t.connectionlist_iterator = -1
	t.state = AtHome

	t.acceptTask = make(chan int64)
	t.startTask = make(chan StartTaskStruct)
	t.trainStop = make(chan TrainStatPair)
	t.notifyAboutTrainArrival = make(chan TrainStatPair)

	return t
}

func WorkerTask(work_ptr *Worker, model_ptr *Simulation_Model) {
	var work_duration float64
	var work bool = false
	var train_ptr *Train
	var stat_ptr *Station

	var check_train int64 = 0
	var check_station int64 = 0

	if model_ptr != nil && work_ptr != nil {
		for model_ptr.work {
			//fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] enters select. is at home?" + strconv.FormatBool(work_ptr.state == AtHome))
			select {
			case stat_id := <-when(work_ptr.state == AtHome,
				work_ptr.acceptTask):
				work_ptr.state = TravellingToWork
				work_ptr.dest_Station = stat_id

			case ST := <-whenSTS(work_ptr.state == WaitingForWork,
				work_ptr.startTask):
				if ST.stat_id == work_ptr.dest_Station {
					work_duration = ST.work_time_hours
					work = true
				} else {
					fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received illegal start task notification from station[" + strconv.FormatInt(ST.stat_id, 10) + "]")
				}

			case ST := <-whenTSP(work_ptr.on_train != 0,
				work_ptr.trainStop):
				if ST.train_id == work_ptr.on_train {
					check_station = ST.stat_id
				} else {
					fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received illegal train stop notification from train[" + strconv.FormatInt(ST.train_id, 10) + "]")
				}

			case ST := <-whenTSP(work_ptr.on_Station != 0 && (work_ptr.state == TravellingToWork || work_ptr.state == TravellingToHome),
				work_ptr.notifyAboutTrainArrival):
				if ST.stat_id == work_ptr.on_Station {
					check_train = ST.train_id
				} else {
					fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received illegal train arrival notification from station[" + strconv.FormatInt(ST.stat_id, 10) + "]")
				}

			case <-time.After(time.Second * time.Duration(GetTimeSimToRealFromModel(100.0, Time_Interval_Minute, model_ptr))):
			}
			//fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] leaves select")

			if check_station != 0 && (*work_ptr.connectionlist)[work_ptr.connectionlist_iterator].arrive_station_id == check_station {
				train_ptr = GetTrain(work_ptr.on_train, model_ptr)
				stat_ptr = GetStation(check_station, model_ptr)
				if train_ptr != nil && stat_ptr != nil {
					if (*work_ptr.connectionlist)[work_ptr.connectionlist_iterator].arrive_station_id == check_station {

						PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] tries to enter station["+strconv.FormatInt(check_station, 10)+"]", model_ptr)
						select {
						case stat_ptr.notifyAboutWorkerArrival <- work_ptr.id:
							train_ptr.leaveTrain <- work_ptr.id
							work_ptr.on_train = 0
							work_ptr.on_Station = stat_ptr.id
							work_ptr.connectionlist_iterator = work_ptr.connectionlist_iterator + 1
							PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] left the train["+strconv.FormatInt(train_ptr.id, 10)+"] and entered station["+strconv.FormatInt(stat_ptr.id, 10)+"]", model_ptr)
						case <-time.After(time.Second * time.Duration(10.0)):
							PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] failed to leave train["+strconv.FormatInt(train_ptr.id, 10)+"]", model_ptr)
						}

					}
				} else {
					if train_ptr == nil {
						fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received nil pointer for train[" + strconv.FormatInt(work_ptr.on_train, 10) + "]")
					} else {
						fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received nil pointer for station[" + strconv.FormatInt(check_station, 10) + "]")
					}
				}
				check_station = 0
			}

			if check_train != 0 && work_ptr.connectionlist != nil && ((*work_ptr.connectionlist)[work_ptr.connectionlist_iterator]) != nil {
				stat_ptr = GetStation(work_ptr.on_Station, model_ptr)
				if stat_ptr != nil {
					if (*work_ptr.connectionlist)[work_ptr.connectionlist_iterator].train_id == check_train {
						train_ptr = GetTrain(check_train, model_ptr)
						if train_ptr != nil {
							if (*work_ptr.connectionlist)[work_ptr.connectionlist_iterator].train_id == check_train {
								PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] tries to enter train["+strconv.FormatInt(train_ptr.id, 10)+"]", model_ptr)
								select {
								case train_ptr.enterTrain <- work_ptr.id:
									stat_ptr.notifyAboutWorkerDeparture <- work_ptr.id
									PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] aboards train["+strconv.FormatInt(train_ptr.id, 10)+"] and leaves station["+strconv.FormatInt(stat_ptr.id, 10)+"]", model_ptr)
									work_ptr.on_train = train_ptr.id
									work_ptr.on_Station = 0
								case <-time.After(time.Second * time.Duration(10.0)):
									fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] failed to hop on train[" + strconv.FormatInt(train_ptr.id, 10) + "]")
								}
							} else {
								fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received nil pointer for train[" + strconv.FormatInt(check_train, 10) + "]")
							}
						} else {
							fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received nil pointer for station[" + strconv.FormatInt(work_ptr.on_Station, 10) + "]")
						}
					}
				}
				check_train = 0
			}

			if work_ptr.state == TravellingToWork && work_ptr.connectionlist == nil {
				work_ptr.connectionlist = GetConnection(work_ptr.home_stat_id, work_ptr.dest_Station, model_ptr)
				work_ptr.connectionlist_iterator = 0
				if work_ptr.connectionlist == nil {
					fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] did not found connection from " + strconv.FormatInt(work_ptr.home_stat_id, 10) + " to " + strconv.FormatInt(work_ptr.dest_Station, 10) + " stations.")
					work_ptr.state = AtHome
					work_ptr.dest_Station = 0
				} else if len(*work_ptr.connectionlist) == 0 {
					work_ptr.connectionlist = nil
					work_ptr.connectionlist_iterator = -1
					work_ptr.state = WaitingForWork
					stat_ptr = GetStation(work_ptr.dest_Station, model_ptr)
					if stat_ptr != nil {
						PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] is already at target station and is ready to start working.", model_ptr)
						stat_ptr.notifyAboutReadinessToWork <- work_ptr.id
					} else {
						fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received nil pointer for station[" + strconv.FormatInt(work_ptr.dest_Station, 10) + "]")
					}
				} else {
					PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] has accepted request for work. Moving to station["+strconv.FormatInt(work_ptr.dest_Station, 10)+"]", model_ptr)
				}
			}

			if work {
				stat_ptr = GetStation(work_ptr.dest_Station, model_ptr)
				if stat_ptr != nil {
					work = false
					PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] starts working for next "+strconv.FormatFloat(work_duration, 'f', 3, 64)+" hours.", model_ptr)

					time.Sleep(time.Duration(GetTimeSimToRealFromModel(work_duration, Time_Interval_Hour, model_ptr)) * time.Second)
					PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] finished working on task.", model_ptr)
					stat_ptr.notifyAboutFinishingTheWork <- work_ptr.id

				} else {
					fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received nil pointer for station[" + strconv.FormatInt(work_ptr.dest_Station, 10) + "]")
				}

				work_ptr.connectionlist = GetConnection(work_ptr.dest_Station, work_ptr.home_stat_id, model_ptr)
				work_ptr.connectionlist_iterator = 0
				work_ptr.dest_Station = work_ptr.home_stat_id
				work_ptr.state = TravellingToHome
				if work_ptr.connectionlist == nil {
					fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] does not found connection from " + strconv.FormatInt(work_ptr.dest_Station, 10) + " to " + strconv.FormatInt(work_ptr.home_stat_id, 10) + " stations.")
				} else if len(*work_ptr.connectionlist) == 0 {
					work_ptr.connectionlist = nil
					work_ptr.connectionlist_iterator = -1
					work_ptr.state = AtHome
					PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] left work and went directly back home.", model_ptr)
				} else {
					PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] left work and is going back to home station["+strconv.FormatInt(work_ptr.home_stat_id, 10)+"]", model_ptr)
				}
			} else if work_ptr.state == TravellingToWork && work_ptr.on_Station == work_ptr.dest_Station {
				work_ptr.connectionlist = nil
				work_ptr.connectionlist_iterator = -1
				work_ptr.state = WaitingForWork
				stat_ptr = GetStation(work_ptr.dest_Station, model_ptr)
				if stat_ptr != nil {
					PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] arrived to target station and is ready to start working.", model_ptr)
					stat_ptr.notifyAboutReadinessToWork <- work_ptr.id
					// PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] notified that it's ready to work.",model_ptr);
				} else {
					fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received nil pointer for station[" + strconv.FormatInt(work_ptr.dest_Station, 10) + "]")
				}
			} else if work_ptr.state == TravellingToHome && work_ptr.on_Station == work_ptr.home_stat_id {
				work_ptr.connectionlist = nil
				work_ptr.connectionlist_iterator = -1
				work_ptr.state = AtHome
				PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] arrived back at home.", model_ptr)
			} else if work_ptr.connectionlist != nil && work_ptr.on_Station != 0 {
				stat_ptr = GetStation(work_ptr.on_Station, model_ptr)
				if stat_ptr != nil {

					stat_ptr.mutex.RLock()
					for cur := range stat_ptr.trains {
						if cur == (*work_ptr.connectionlist)[work_ptr.connectionlist_iterator].train_id {

							train_ptr = GetTrain((*work_ptr.connectionlist)[work_ptr.connectionlist_iterator].train_id, model_ptr)
							if train_ptr != nil {

								PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] tries to enter train["+strconv.FormatInt(train_ptr.id, 10)+"]", model_ptr)
								select {
								case train_ptr.enterTrain <- work_ptr.id:
									stat_ptr.notifyAboutWorkerDeparture <- work_ptr.id
									PutLine("#3# worker["+strconv.FormatInt(work_ptr.id, 10)+"] aboards train["+strconv.FormatInt(train_ptr.id, 10)+"] and leaves station["+strconv.FormatInt(stat_ptr.id, 10)+"]", model_ptr)
									work_ptr.on_train = train_ptr.id
									work_ptr.on_Station = 0
								case <-time.After(time.Second * time.Duration(10.0)):
									fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] failed to hop on train[" + strconv.FormatInt(train_ptr.id, 10) + "]")
								}
							} else {
								fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received nil pointer for train[" + strconv.FormatInt((*work_ptr.connectionlist)[work_ptr.connectionlist_iterator].train_id, 10) + "]")
							}

						}
					}
					stat_ptr.mutex.RUnlock()
				} else {
					fmt.Println("#3# worker[" + strconv.FormatInt(work_ptr.id, 10) + "] received nil pointer for station[" + strconv.FormatInt(work_ptr.dest_Station, 10) + "]")
				}
			}
		}
	} else {
		fmt.Println("#3# WorkTask received nil pointer.")
	}
}
