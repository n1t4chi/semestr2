package main

import (
	"fmt"
	"math"
	"strconv"
	"strings"
	"time"
)

//@Author: Piotr Olejarz 220398
//File log is used for output and logging purposes. All output should be redirected here so it will be processed correspondingly with simulation options.

//prints new line
func PutLineAlways(line string) {
	//fmt.Println(line);
	fmt.Println( /*Ada.Calendar.Formatting.Image(Ada.Calendar.Clock)+":"&*/ line)
}

//prints new line depending on mode
func PutLine(line string, model_ptr *Simulation_Model) {
	//fmt.Println("test");
	if model_ptr != nil && model_ptr.mode != Silent_Mode {

		if !model_ptr.debug && strings.HasPrefix(line, "#debug#") {
			return
		}
		if model_ptr.log_mode == second_task && !strings.HasPrefix(line, "#2#") {
			return
		}
		if model_ptr.log_mode == third_task && !strings.HasPrefix(line, "#3#") {
			return
		}
		PutLineAlways(line)
	}

}

//prints model based on mode
func PrintModel(model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintModelAlways(model_ptr)
	}

}

//prints model
func PrintModelAlways(model_ptr *Simulation_Model) {
	if model_ptr != nil {
		fmt.Println("Simulation speed: " + strconv.FormatInt(model_ptr.speed, 10) + "s -> 1h")
		PrintSteeringsAlways(model_ptr)
		PrintStationsAlways(model_ptr)
		PrintTracksAlways(model_ptr)
		PrintTrainsAlways(model_ptr)
		PrintWorkersAlways(model_ptr)
	}
}

//prints steerings based on mode
func PrintSteerings(model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintSteeringsAlways(model_ptr)
	}

}

//prints steerings
func PrintSteeringsAlways(model_ptr *Simulation_Model) {
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.steer); it++ {
			fmt.Println(SteeringToString(model_ptr.steer[it]))
		}
	}
}

//prints tracks based on mode
func PrintTracks(model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintTracksAlways(model_ptr)
	}

}

//prints tracks
func PrintTracksAlways(model_ptr *Simulation_Model) {
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.track); it++ {
			fmt.Println(TrackToString(model_ptr.track[it]))
		}
	}
}

//prints trains based on mode
func PrintTrains(model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintTrainsAlways(model_ptr)
	}

}

//prints trains
func PrintTrainsAlways(model_ptr *Simulation_Model) {
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.train); it++ {
			fmt.Println(TrainToString(model_ptr.train[it]))
		}
	}
}

//prints stations based on mode
func PrintStations(model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintStationsAlways(model_ptr)
	}

}

//prints stations
func PrintStationsAlways(model_ptr *Simulation_Model) {
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.station); it++ {
			fmt.Println(StationToString(model_ptr.station[it]))
		}
	}
}

//prints workers based on mode
func PrintWorkers(model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintWorkersAlways(model_ptr)
	}

}

//prints workers
func PrintWorkersAlways(model_ptr *Simulation_Model) {
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.worker); it++ {
			fmt.Println(WorkerToString(model_ptr.worker[it]))
		}
	}
}

//prints train locations based on mode
func PrintTrainLocations(model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintTrainLocationsAlways(model_ptr)
	}

}

//prints train locations
func PrintTrainLocationsAlways(model_ptr *Simulation_Model) {
	if model_ptr != nil {
		for it := 0; it < len(model_ptr.train); it++ {
			c_t := model_ptr.train[it]
			if c_t.on_track != 0 {
				fmt.Println(strconv.FormatInt(c_t.id, 10) +
					" { at track: " + strconv.FormatInt((*c_t.tracklist)[c_t.track_it], 10) +
					" and moves at " + strconv.FormatInt(c_t.current_speed, 10) + "kmph")
			} else {
				fmt.Println(strconv.FormatInt(c_t.id, 10) +
					" { at steering: " + strconv.FormatInt((*c_t.tracklist)[c_t.track_it], 10))
			}
		}
	}
}

//prints given train status status based on mode
func PrintTrainStatusFromIDAlways(train_id int64, model_ptr *Simulation_Model) {
	PrintTrainStatusAlways(GetTrain(train_id, model_ptr))
}

//prints given train status status
func PrintTrainStatusFromID(train_id int64, model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintTrainStatusFromIDAlways(train_id, model_ptr)
	}
}

//prints given train status status based on mode
func PrintTrainStatus(train_ptr *Train, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintTrainStatusAlways(train_ptr)
	}
}

//prints given train status status
func PrintTrainStatusAlways(train_ptr *Train) {
	fmt.Println(TrainToString(train_ptr))
}

//prints given steering status based on mode
func PrintSteeringFromIDStatus(steer_id int64, model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintSteeringStatusFromIDAlways(steer_id, model_ptr)
	}
}

//prints given steering status
func PrintSteeringStatusFromIDAlways(steer_id int64, model_ptr *Simulation_Model) {
	PrintSteeringStatusAlways(GetSteering(steer_id, model_ptr))
}

//prints given steering status based on mode
func PrintSteeringStatus(steer_ptr *Steering, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintSteeringStatusAlways(steer_ptr)
	}
}

//prints given steering status
func PrintSteeringStatusAlways(steer_ptr *Steering) {
	fmt.Println(SteeringToString(steer_ptr))
}

//prints given track status based on mode
func PrintTrackStatusFromID(track_id int64, model_ptr *Simulation_Model, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintTrackStatusFromIDAlways(track_id, model_ptr)
	}
}

//prints given track status
func PrintTrackStatusFromIDAlways(track_id int64, model_ptr *Simulation_Model) {
	PrintTrackStatusAlways(GetTrack(track_id, model_ptr))
}

//prints given track status based on mode
func PrintTrackStatus(track_ptr *Track, talking_mode Simulation_Mode) {
	if talking_mode != Silent_Mode {
		PrintTrackStatusAlways(track_ptr)
	}
}

//prints given track status
func PrintTrackStatusAlways(track_ptr *Track) {
	fmt.Println(TrackToString(track_ptr))
}

type Time struct {
	day    int64
	hour   int64
	minute int64
}

func GetRelativeTime(current_time time.Time, model_ptr *Simulation_Model) Time {
	var t Time

	dur := current_time.Sub(model_ptr.start_time)
	sim_time := dur.Seconds() / float64(model_ptr.speed)

	//Float'Value(Duration'Image(Ada.Real_Time.To_Duration(current_time-model_ptr.start_time))) / Float(model_ptr.speed);

	//fmt.Println("dur sec:" + strconv.FormatFloat(dur.Seconds(), 'f', 3, 64))
	//fmt.Println("Sim+time:" + strconv.FormatFloat(sim_time, 'f', 3, 64))

	t.day = int64(sim_time) / 24
	t.hour = int64(sim_time) % 24
	//ada.Text_IO.Put_Line("&&&:"&Float'Image(Float'Fraction(sim_time)));
	_, frac := math.Modf(sim_time)
	min := int64(frac * 60.0)
	if min < 0 {
		t.minute = 60 + min
		t.hour = t.hour - 1
	} else {
		t.minute = min
	}

	return t
}

func TimeToString(t Time) string {
	// if t.day != 0 {
	//  return strconv.FormatInt(t.day)+"d "+strconv.FormatInt(t.hour)+":"+strconv.FormatInt(t.minute);
	//}else{
	return "+" + strconv.FormatInt(t.day, 10) + "d " + strconv.FormatInt(t.hour, 10) + "h " + strconv.FormatInt(t.minute, 10) + "m"
	//}

}

//prints timetable for given train
func PrintTrainTimetable(id int64, model_ptr *Simulation_Model) {
	if model_ptr != nil {
		train_ptr := GetTrain(id, model_ptr)
		if train_ptr != nil {
			fmt.Println("Timetable for train:" + strconv.FormatInt(train_ptr.id, 10))
			fmt.Println("platform\tarrival\tdeparture")
			for it := 0; it < len(train_ptr.history); it++ {
				th := train_ptr.history[it]
				if th.object_type == Type_Platform {

					t_arr := GetRelativeTime(th.arrival, model_ptr)
					t_dep := GetRelativeTime(th.departure, model_ptr)
					fmt.Println(strconv.FormatInt(th.object_id, 10) + "\t" + TimeToString(t_arr) + "\t" + TimeToString(t_dep))

				}

			}
		} else {
			fmt.Println("Train not found!")
		}
	} else {
		fmt.Println("Null model!")
	}
}

//prints timetable for given track
func PrintTrackTimetable(id int64, model_ptr *Simulation_Model) {
	if model_ptr != nil {
		track_ptr := GetTrack(id, model_ptr)
		if track_ptr != nil {
			fmt.Println("Timetable for track:" + strconv.FormatInt(track_ptr.id, 10))
			fmt.Println("train\tarrival\tdeparture")
			for it := 0; it < len(track_ptr.history); it++ {
				th := track_ptr.history[it]
				t_arr := GetRelativeTime(th.arrival, model_ptr)
				t_dep := GetRelativeTime(th.departure, model_ptr)
				fmt.Println(strconv.FormatInt(th.train_id, 10) + "\t" + TimeToString(t_arr) + "\t" + TimeToString(t_dep))
			}
		} else {
			fmt.Println("Track not found!")
		}
	} else {
		fmt.Println("Null model!")
	}

}
