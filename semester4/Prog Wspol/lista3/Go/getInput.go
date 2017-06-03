package main

/* @Author: Piotr Olejarz 220398
 * File GetInput is used to recover configuration of simulation from given file and creates Simulation_Model object from that.
 * File requires below structure for valid data. All columns must be spaced out with tabulation.
 * Simulation speed requires single record, other allow for multiple records.
 * tracks require valid IDs of steerings and train's trackilist requires valid track IDs.
 * Also tracklist must be valid in the sense that each two tracks are connected by common steering at one track start and other end.
 * Also tracklist must be cyclic so that start of first is the end of the last track.
 * File Structure:
 * @simulation_speed:
 * <real-time seconds to simulation-hour ratio>
 * @steering:
 * <nr>	<delay in minutes>
 * @tracks:
 * <nr>	<v1>	<v2>	stop	<delay in minutes>
 * <nr>	<v1>	<v2>	pass	<dist in km>	<max vel in km/h>
 * @trains:
 * <nr>	<max speed in km/h>	<capacity of people>	<tracklist>
 */

import (
	"bufio"
	"fmt"
	"os"
	"regexp"
	"strconv"
)

/*
 * Recovers data from file, ignores empty lines and those with # at first character.
 */
func getInput(filename string) []string {
	file, err := os.Open(filename)
	var rtrn []string = make([]string, 0)
	if err == nil {
		scanner := bufio.NewScanner(file)
		for scanner.Scan() {
			line := scanner.Text()
			if len(line) > 0 {
				if line[0] != '#' {
					rtrn = append(rtrn, line)
				}
			}
		}
	} else {
		fmt.Println("Could not open file " + filename)
	}
	return rtrn
}

/*
 * gets input from file and creates Simulation_Model object.
 */
func getModel(filename string) *Simulation_Model {
	//var inp String
	var str []string = getInput(filename)

	var line_state int64 = 0
	var model_ptr *Simulation_Model = new(Simulation_Model)
	model_ptr.log_mode = all_output
	model_ptr.mode = Mixed_Mode
	model_ptr.debug = false
	steer_regex, err1 := regexp.Compile("(\\d+)\\s(\\d+)")
	platf_regex, err3 := regexp.Compile("(\\d+)\\s(\\d+)\\s(\\d+)\\sstop\\s(\\d+)\\s(\\d+)")
	track_regex, err4 := regexp.Compile("(\\d+)\\s(\\d+)\\s(\\d+)\\spass\\s(\\d+)\\s(\\d+)")
	service_track_regex, err5 := regexp.Compile("(\\d+)\\s(\\d+)\\s(\\d+)\\sservice")
	station_regex, err11 := regexp.Compile("(\\d+)")
	worker_regex, err12 := regexp.Compile("(\\d+)\\s(\\d+)")

	train_regex, err2 := regexp.Compile("(\\d+)\\s(\\d+)\\snormal\\s(\\d+)\\s([\\d+,?]+)")
	service_train_regex, err6 := regexp.Compile("(\\d+)\\s(\\d+)\\sservice\\s(\\d+)")

	train_tracklist_regex, err7 := regexp.Compile("(\\d+)")
	if err1 != nil || err2 != nil || err3 != nil || err4 != nil || err5 != nil || err6 != nil || err7 != nil || err11 != nil || err12 != nil {
		fmt.Println("Could not compile one of the input regexes")
		return nil
	}
	for i := 0; i < len(str); i++ {
		//fmt.Println("[", i, "]"+str[i])
		var curr_line string = str[i]

		if curr_line == "@simulation_speed:" {
			line_state = 1
		} else if curr_line == "@steering:" {
			line_state = 2
		} else if curr_line == "@tracks:" {
			line_state = 3
		} else if curr_line == "@trains:" {
			line_state = 4
		} else if curr_line == "@stations:" {
			line_state = 5
		} else if curr_line == "@workers:" {
			line_state = 6
		} else {
			switch line_state {
			case 1:
				speed, err := strconv.ParseInt(curr_line, 10, 64)
				if err != nil {
					fmt.Println("Could not parse simulation speed value from this line: [" + curr_line + "]")
					return nil
				} else {
					model_ptr.speed = speed
				}
			case 2:
				if steer_regex.MatchString(curr_line) {
					res := steer_regex.FindAllStringSubmatch(curr_line, -1)
					if len(res) > 0 && len(res[0]) >= 3 {
						id, err1 := strconv.ParseInt(res[0][1], 10, 64)
						delay, err2 := strconv.ParseInt(res[0][2], 10, 64)
						if err1 == nil && err2 == nil {
							//inserting steering pointer into array
							model_ptr.steer = append(model_ptr.steer, NewSteering(id, delay))
							//fmt.Println(SteeringToString(model_ptr.steer[len(model_ptr.steer)-1]))
						} else {
							fmt.Println("Could not parse data for steering record from line[", i, "]\""+curr_line+"\"")
						}

					} else {
						fmt.Println("Problem with steer result for line [", i, "]\""+curr_line+"\"")
					}
				} else {
					fmt.Println("line[", i, "]\""+curr_line+"\" does not matches the steering record format.")
				}
			case 3: // tracks
				if track_regex.MatchString(curr_line) {
					res := track_regex.FindAllStringSubmatch(curr_line, -1)
					if len(res) > 0 && len(res[0]) >= 6 {
						id, err1 := strconv.ParseInt(res[0][1], 10, 64)
						v1, err2 := strconv.ParseInt(res[0][2], 10, 64)
						v2, err3 := strconv.ParseInt(res[0][3], 10, 64)
						dist, err4 := strconv.ParseInt(res[0][4], 10, 64)
						max_speed, err5 := strconv.ParseInt(res[0][5], 10, 64)
						if err1 == nil && err2 == nil && err3 == nil && err4 == nil && err5 == nil {
							//inserting track pointer into array
							model_ptr.track = append(model_ptr.track, NewTrack(id, v1, v2, dist, max_speed))
							//fmt.Println(TrackToString(model_ptr.track[len(model_ptr.track)-1]))
						} else {
							fmt.Println("Could not parse data for track record from line[", i, "]\""+curr_line+"\"")
						}
					} else {
						fmt.Println("Problem with track regex result for line [", i, "]\""+curr_line+"\"")
					}
				} else if platf_regex.MatchString(curr_line) {
					res := platf_regex.FindAllStringSubmatch(curr_line, -1)
					if len(res) > 0 && len(res[0]) >= 6 {
						id, err1 := strconv.ParseInt(res[0][1], 10, 64)
						v1, err2 := strconv.ParseInt(res[0][2], 10, 64)
						v2, err3 := strconv.ParseInt(res[0][3], 10, 64)
						delay, err4 := strconv.ParseInt(res[0][4], 10, 64)
						stat, err5 := strconv.ParseInt(res[0][5], 10, 64)
						if err1 == nil && err2 == nil && err3 == nil && err4 == nil && err5 == nil {
							//inserting track pointer into array
							model_ptr.track = append(model_ptr.track, NewPlatform(id, v1, v2, delay, stat))

							model_ptr.platf = append(model_ptr.platf, model_ptr.track[len(model_ptr.track)-1])

							//fmt.Println(TrackToString(model_ptr.track[len(model_ptr.track)-1]))
						} else {
							fmt.Println("Could not parse data for platform record from line[", i, "]\""+curr_line+"\"")
						}
					} else {
						fmt.Println("Problem with platf regex result for line [", i, "]\""+curr_line+"\"")
					}
				} else if service_track_regex.MatchString(curr_line) {
					res := service_track_regex.FindAllStringSubmatch(curr_line, -1)
					if len(res) > 0 && len(res[0]) >= 4 {
						id, err1 := strconv.ParseInt(res[0][1], 10, 64)
						v1, err2 := strconv.ParseInt(res[0][2], 10, 64)
						v2, err3 := strconv.ParseInt(res[0][3], 10, 64)
						if err1 == nil && err2 == nil && err3 == nil {
							//inserting track pointer into array
							model_ptr.track = append(model_ptr.track, NewServiceTrack(id, v1, v2))
							//fmt.Println("service track" + TrackToString(model_ptr.track[len(model_ptr.track)-1]))
						} else {
							fmt.Println("Could not parse data for service track record from line[", i, "]\""+curr_line+"\"")
						}
					} else {
						fmt.Println("Problem with service track result for line [", i, "]\""+curr_line+"\"")
					}
				} else {
					fmt.Println("line[", i, "]\""+curr_line+"\" does not matches the track or platform record format.")
				}
			case 4: //trains
				if train_regex.MatchString(curr_line) {
					res := train_regex.FindAllStringSubmatch(curr_line, -1)
					if len(res) > 0 && len(res[0]) >= 5 {
						id, err1 := strconv.ParseInt(res[0][1], 10, 64)
						speed, err2 := strconv.ParseInt(res[0][2], 10, 64)
						capacity, err3 := strconv.ParseInt(res[0][3], 10, 64)
						res_track := train_tracklist_regex.FindAllStringSubmatch(res[0][4], -1)
						var tracklist []int64 = make([]int64, 0)
						for i := 0; i < len(res_track); i++ {
							v, err_t := strconv.ParseInt(res_track[i][0], 10, 64)
							if err_t == nil {
								tracklist = append(tracklist, v)
							} else {
								fmt.Println("Could not parse one of the parameters \""+res[i][0]+"\" for tracklist from line[", i, "]\""+curr_line+"\"")
							}
						}
						if err1 == nil && err2 == nil && err3 == nil /*&& err4 == nil*/ {
							//inserting train pointer into array
							model_ptr.train = append(model_ptr.train, newTrain(id, speed, capacity, tracklist))
							//fmt.Println(TrainToString(model_ptr.train[len(model_ptr.train)-1]))
						} else {
							fmt.Println("Could not parse data for train record from line[", i, "]\""+curr_line+"\"")
						}
					} else {
						fmt.Println("Problem with train result for line [", i, "]\""+curr_line+"\"")
					}

				} else if service_train_regex.MatchString(curr_line) {
					res := service_train_regex.FindAllStringSubmatch(curr_line, -1)
					//fmt.Println("service train " + strconv.FormatInt(int64(len(res[0])), 10))
					if len(res) > 0 && len(res[0]) >= 4 {
						id, err1 := strconv.ParseInt(res[0][1], 10, 64)
						speed, err2 := strconv.ParseInt(res[0][2], 10, 64)
						track, err3 := strconv.ParseInt(res[0][3], 10, 64)
						if err1 == nil && err2 == nil && err3 == nil {
							//inserting train pointer into array
							model_ptr.train = append(model_ptr.train, newServiceTrain(id, speed, track))
							//fmt.Println("service train" + TrainToString(model_ptr.train[len(model_ptr.train)-1]))
						} else {
							fmt.Println("Could not parse data for service train record from line[", i, "]\""+curr_line+"\"")
						}
					} else {
						fmt.Println("Problem with service train result for line [", i, "]\""+curr_line+"\"")
					}

				} else {
					fmt.Println("line[", i, "]\""+curr_line+"\" does not matches the train record format.")
				}
			case 5: //stations
				if station_regex.MatchString(curr_line) {
					res := station_regex.FindAllStringSubmatch(curr_line, -1)
					if len(res) > 0 && len(res[0]) >= 2 {
						id, err1 := strconv.ParseInt(res[0][1], 10, 64)
						if err1 == nil {
							//inserting station pointer into array
							model_ptr.station = append(model_ptr.station, newStation(id))
						} else {
							fmt.Println("Could not parse data for station record from line[", i, "]\""+curr_line+"\"")
						}

					} else {
						fmt.Println("Problem with steer result for line [", i, "]\""+curr_line+"\"")
					}
				} else {
					fmt.Println("line[", i, "]\""+curr_line+"\" does not matches the station record format.")
				}

			case 6: //workers
				if worker_regex.MatchString(curr_line) {
					res := worker_regex.FindAllStringSubmatch(curr_line, -1)
					if len(res) > 0 && len(res[0]) >= 3 {
						id, err1 := strconv.ParseInt(res[0][1], 10, 64)
						stat_id, err2 := strconv.ParseInt(res[0][2], 10, 64)
						if err1 == nil && err2 == nil {
							//inserting worker pointer into array
							model_ptr.worker = append(model_ptr.worker, newWorker(id, stat_id))
						} else {
							fmt.Println("Could not parse data for worker record from line[", i, "]\""+curr_line+"\"")
						}

					}
				} else {
					fmt.Println("line[", i, "]\""+curr_line+"\" does not matches the worker record format.")
				}
			}
		}
	}

	var unique bool = false
	for it := 0; it < len(model_ptr.train); it++ {
		train_ptr := model_ptr.train[it]
		if train_ptr.t_type == Train_Type_Normal {
			var uniq int64 = 0
			for itt := 0; itt < len(*train_ptr.stationlist); itt++ {

				unique = true
				for ittt := itt + 1; ittt < len(*train_ptr.stationlist); ittt++ {
					if (*train_ptr.stationlist)[itt] == (*train_ptr.stationlist)[ittt] {
						unique = false
						break
					}
				}
				if unique {
					uniq++
				}
			}
			train_ptr.data[T_uniqueStations] = uniq
		}
	}
	for it := 0; it < len(model_ptr.train); it++ {
		train_ptr := model_ptr.train[it]
		if train_ptr.t_type == Train_Type_Normal {
			for it := 0; it < len(*train_ptr.tracklist); it++ {
				track_id := (*train_ptr.tracklist)[it]
				for itt := 0; itt < len(model_ptr.platf); itt++ {
					if track_id == model_ptr.platf[itt].id {
						ap := append(*train_ptr.stationlist, model_ptr.platf[itt].data[T_station_id])
						train_ptr.stationlist = &ap
						break
					}
				}
			}
		}
	}

	for it := 0; it < len(model_ptr.station); it++ {
		stat_ptr := model_ptr.station[it]
		if stat_ptr == nil {
			fmt.Println("null statioin")
		}
		for itt := 0; itt < len(model_ptr.worker); itt++ {
			if model_ptr.worker[itt] == nil {
				fmt.Println("null worker")
			}
			if model_ptr.worker[itt].home_stat_id == stat_ptr.id {
				stat_ptr.passengers[model_ptr.worker[itt]] = true
			}
		}
	}

	return model_ptr

}
