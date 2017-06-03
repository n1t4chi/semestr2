package main

//Author: Piotr Olejarz 220398
//Dijkstra alghoritm

const MAX = 100000000

type vertice struct {
	steer *Steering
	dist  float64
	prev  int64
}

func newVert(steer *Steering) *vertice {
	v := new(vertice)
	v.steer = steer
	v.dist = MAX
	v.prev = 0
	return v
}

func findTracklistTo(train_id int64, block bool, start_track_id int64, target_id int64, target_type Railway_Object_Type, model_ptr *Simulation_Model) *[]int64 {
	var tl []int64 = nil

	var vert []*vertice = make([]*vertice, 0)

	var train_ptr *Train = GetTrain(train_id, model_ptr)

	var start_track *Track = GetTrack(start_track_id, model_ptr)

	var target_steer_id int64 = 0
	var target_steer_id_2 int64 = 0

	var target_track_ptr *Track
	var target_Train_ptr *Train

	if target_type == Type_Steering {
		target_steer_id = target_id
	} else if target_type == Type_Track {
		target_track_ptr = GetTrack(target_id, model_ptr)
		target_steer_id = target_track_ptr.st_end
		target_steer_id_2 = target_track_ptr.st_start
	} else if target_type == Type_Train {
		target_Train_ptr = GetTrain(target_id, model_ptr)
		if target_Train_ptr.on_steer != 0 {
			target_steer_id = target_Train_ptr.on_steer
		} else {
			target_track_ptr = GetTrack(target_Train_ptr.on_track, model_ptr)
			target_steer_id = target_track_ptr.st_end
			target_steer_id_2 = target_track_ptr.st_start
		}
	}

	//Ada.Text_IO.Put_Line("Looking for path from "+ int64'Image(start_track.st_start)+" or " + int64'Image(start_track.st_end)
	//                      + " to "+ int64'Image(target_steer_id)+" or " + int64'Image(target_steer_id_2)
	//)
	if (start_track.st_start == target_steer_id || start_track.st_end == target_steer_id) ||
		(target_steer_id_2 != 0 && (start_track.st_start == target_steer_id_2 || start_track.st_end == target_steer_id_2)) {
		tl = append(tl, start_track_id)
		return &tl
	}

	for it := 0; it < len(model_ptr.steer); it++ {
		// Ada.Text_IO.Put_Line("%%%%steering "+ int64'Image(model_ptr.steer[it].id ) + " used by" + int64'Image(model_ptr.steer[it].used_by) )
		if model_ptr.steer[it].used_by == 0 {
			if block == true {
				select {
				case model_ptr.steer[it].allowServiceTrain <- train_id:
					//  Ada.Text_IO.Put_Line("Steering "+ int64'Image(model_ptr.steer[it].id )+" accepted Train " + int64'Image(d_length))
					vert = append(vert, newVert(model_ptr.steer[it]))
				//case <- time.After(time.Microsecond * 5):
				default:
					//   Ada.Text_IO.Put_Line("Steering "+ int64'Image(model_ptr.steer[it].id )+" did not respond " + int64'Image(d_length))
				}
			} else {
				vert = append(vert, newVert(model_ptr.steer[it]))
			}
		} else if model_ptr.steer[it].used_by == train_id || (model_ptr.steer[it].id == target_steer_id || (target_steer_id_2 != 0 && model_ptr.steer[it].id == target_steer_id_2)) {
			//   Ada.Text_IO.Put_Line("Steering "+ int64'Image(model_ptr.steer[it].id )+" is already used by this Train " + int64'Image(d_length))
			vert = append(vert, newVert(model_ptr.steer[it]))

			if model_ptr.steer[it].id == start_track.st_end || model_ptr.steer[it].id == start_track.st_start {
				vert[len(vert)-1].dist = 0.0
			}

			// } else {
			//  Ada.Text_IO.Put_Line("Steering "+ int64'Image(model_ptr.steer[it].id )+" is used by other Train " + int64'Image(d_length))
		}
	}

	if block == true {
		for it := 0; it < len(model_ptr.track); it++ {
			select {
			case model_ptr.track[it].allowServiceTrain <- train_id:
				//nothing
				//case <- time.After(time.Microsecond * 5):
			default:
				//Ada.Text_IO.Put_Line("Track "+ int64'Image(model_ptr.track[it].id )+" failed to respond")
			}
		}
	}
	//Ada.Text_IO.Put_Line("Finished length: " + int64'Image(d_length))

	//dist = new ArrF(1..d_length)
	//prev = new ArrN(1..d_length)
	//steers = new ArrS(1..d_length)

	// arrays initialisation

	/* if block == true {

	            for it := 0 ; it<len(model_ptr.steer) it++ {
	               //Ada.Text_IO.Put_Line("$$$$$Steering "+ int64'Image(model_ptr.steer[it].id ) + " used by" + int64'Image(model_ptr.steer[it].used_by) )
	               if model_ptr.steer[it].used_by == train_id ||
				    model_ptr.steer[it].id == target_steer_id ||
				 ( target_steer_id_2 != 0 && model_ptr.steer[it].id == target_steer_id_2 )  {
	                  d_it = d_it + 1
	                  if d_it <= len(steers) {
	                     //Ada.Text_IO.Put_Line("Adding "+ int64'Image(model_ptr.steer[it].id )+" to list " + int64'Image(d_it))
	                     //Ada.Text_IO.Put_Line("#$%#%#@%#@%#%#$% d_it" + int64'Image(d_it) + " steers length:" + (int64'Image(steers'Length)) )
	                     steers(d_it) = model_ptr.steer[it]
	                     if model_ptr.steer[it].id == start_track.st_end || model_ptr.steer[it].id == start_track.st_start {
	                        dist(d_it) = 0.0
	                     } else {
	                        dist(d_it) = 100000000 //to lazy to makem max float
	                     }

	                     prev(d_it) = 0
	                  }

	              // } else {
	                  //Ada.Text_IO.Put_Line("Adding "+ int64'Image(model_ptr.steer[it].id )+" not added " + int64'Image(d_it))
	               }
	            }
	         } else {
	            for it := 0 ; it<len(model_ptr.steer) ; it++ {
	               if model_ptr.steer[it].used_by == train_id {
	                  d_it = d_it + 1
	                  steers[it] = model_ptr.steer[it]
	                  if model_ptr.steer[it].id == start_track.st_end || model_ptr.steer[it].id == start_track.st_start {
	                     dist[it] = 0.0
	                  } else {
	                     dist[it] = 100000000 //to lazy to makem max float
	                  }

	                  prev[it] = 0
	               }
	            }
	         }*/

	var d_it int = len(vert) - 1

	for d_it >= 1 {
		//  Ada.Text_IO.Put_Line("#$%#%  d_it:"+ int64'Image(d_it))
		//for it in steers) it++ {
		//   if steers[it] != nil {
		//      Ada.Text_IO.Put_Line("steer id:"+ int64'Image(steers[it].id) +" dist:" + Float'Image(dist[it]) + " prev " + int64'Image(prev[it]) )
		//   } else {
		//      Ada.Text_IO.Put_Line("nil" )
		//   }
		//
		//}

		//min vertex u from q
		var min_d = vert[0].dist
		var min_i = 0
		for it := 1; it <= d_it; it++ {
			// Ada.Text_IO.Put_Line("it:"+ int64'Image[it] +" d_it:" + int64'Image(d_it) + " dist: " + int64'Image(dist'Length) )
			if vert[it].dist <= min_d {
				min_d = vert[it].dist
				min_i = it
			}
		}

		//min u
		var u = vert[min_i]

		//remove u from q

		// Ada.Text_IO.Put_Line("min_i:" + int64'Image(min_i) + " steer: " + int64'Image(steer.id))
		vert[min_i] = vert[d_it]
		vert[d_it] = u
		d_it = d_it - 1

		for it := 0; it < len(model_ptr.track); it++ {

			//var copy_prev *vertice
			var v int64
			var is_v bool
			var it_v int
			var del float64
			var alt float64

			//  Ada.Text_IO.Put_Line("track " + int64'Image(model_ptr.track[it].id )
			//                       + " used by: " + int64'Image(model_ptr.track[it].used_by )
			//                        + " start " + int64'Image(model_ptr.track[it].st_start )
			//                        + " end " + int64'Image(model_ptr.track[it].st_end )
			//                        + " steer: " + int64'Image(steer.id)  )

			if (block == false || model_ptr.track[it].used_by == train_id) && (model_ptr.track[it].st_start == u.steer.id || model_ptr.track[it].st_end == u.steer.id) {
				//    Ada.Text_IO.Put_Line("neighbouring track: " +  int64'Image(model_ptr.track[it].id) )
				if model_ptr.track[it].st_start == u.steer.id {
					v = model_ptr.track[it].st_end
				} else {
					v = model_ptr.track[it].st_start
				}
				is_v = false
				it_v = 0
				for it2 := 0; it2 <= d_it; it2++ {
					if vert[it2].steer.id == v {
						is_v = true
						it_v = it2
						break
					}
				}
				//  Ada.Text_IO.Put_Line("v: " +  int64'Image(v) + " is ok? " +  bool'Image(is_v))

				if is_v == true {

					if model_ptr.track[it].t_type == Track_Type_Track {
						if model_ptr.track[it].data[T_max_speed] < train_ptr.max_speed {
							del = float64(model_ptr.track[it].data[T_distance]) / float64(model_ptr.track[it].data[T_max_speed]) * 60.0
						} else {
							del = float64(model_ptr.track[it].data[T_distance]) / float64(train_ptr.max_speed) * 60.0
						}
					} else {
						del = 1.0 //determining that platforms && service tracks will use only 1 minute for service track to Get through
					}

					alt = float64(u.steer.min_delay) + min_d + del

					//   Ada.Text_IO.Put_Line("alt: " +  Float'Image(alt) + " dist" +  Float'Image(dist(it_v)))

					if alt <= vert[it_v].dist {
						vert[it_v].dist = alt
						vert[it_v].prev = u.steer.id
					}

				}
			}
		}
		//  Ada.Text_IO.Put_Line("")

	}

	var min_1 float64
	var min_i_1 int = -1
	var min_i_2 int = -1
	var min_2 float64 = MAX //to lazy to makem max float

	//var leng int64 = 1

	for it := 0; it < len(vert); it++ {
		if vert[it].steer != nil { //==
			//   nil
			//  Ada.Text_IO.Put_Line("$#%#@$%#@%@#%#@% nil pointer" )
			//} else {
			//Ada.Text_IO.Put_Line( int64'Image(vert[it].steers.id) +" ==? "+ int64'Image(target_steer_id) +" || "+int64'Image(target_steer_id_2) )
			if vert[it].steer.id == target_steer_id {
				min_i_1 = it
				min_1 = vert[it].dist
			}
			if target_steer_id_2 != 0 && vert[it].steer.id == target_steer_id_2 {
				min_i_2 = it
				min_2 = vert[it].dist
			}
			if min_i_1 != -1 && (target_steer_id_2 == 0 || min_i_2 != -1) {
				break
			}
		}

	}
	//if min_i_1 == 0 {
	//Ada.Text_IO.Put_Line("#$%#%#@%#@% min_1:"+ Float'Image(min_1) +" min_i_1:"+ int64'Image(min_i_1) +" min_2:"+ Float'Image(min_2) +" min_i_2:"+ int64'Image(min_i_2) +"   target1"+ int64'Image(target_steer_id) +" target2:" + int64'Image(target_steer_id_2) )
	//}

	if min_i_2 != -1 && min_1 > min_2 {
		min_1 = min_2
		min_i_1 = min_i_2
	}
	min_i_2 = min_i_1
	if min_i_1 == -1 {

		if block == true {
			for it := 0; it < len(model_ptr.track); it++ {
				if model_ptr.track[it].used_by == train_id && model_ptr.track[it].id != train_ptr.on_track {
					model_ptr.track[it].freeFromServiceTrain <- train_id
				}
			}
			for it := 0; it < len(model_ptr.steer); it++ {
				if model_ptr.steer[it].used_by == train_id && model_ptr.steer[it].id != train_ptr.on_steer {
					model_ptr.steer[it].freeFromServiceTrain <- train_id
				}
			}
		}

		return nil
	}

	/*     for vert[min_i_1].prev != 0 {
	       for it:=0 ; it< len(vert) ; it++ {
	          if vert[it].steers.id == vert[min_i_1].prev {
	             min_i_1 = it
	             break
	          }
	       }

	    }*/

	/*    if block == false && target_type == Type_Track {
	         tl = new Track_ARRAY(1 .. len+1)
	         tl(len+1) = target_id
	      } else {
	         tl = new Track_ARRAY(1 .. len)
	      }*/

	//tl must be reversed after!
	if block == false && target_type == Type_Track {
		tl = append(tl, target_id)
	}

	//  Ada.Text_IO.Put_Line("#$%#%#@%#@%#%#$%#$%#$%#@%#%#$%#$%#$%#$ tracklist length:"+ int64'Image(tl'Length) )

	for vert[min_i_1].prev != 0 {
		//    Ada.Text_IO.Put_Line("#$%#%#@%#@%#%#$%#$%#$%#@%#%#$%#$%#$%#$ itt"+ int64'Image[itt] + " iterator:" +  int64'Image(min_i_1) + " " )

		for it := 0; it < len(model_ptr.track); it++ {
			//     Ada.Text_IO.Put_Line("#$%#%#@%#@%#%#$"
			//                             +" start: " +   int64'Image(model_ptr.track[it].st_start)
			//                             +" end: " + int64'Image(model_ptr.track[it].st_end)
			//                             +" curr: " + int64'Image(vert[min_i_1].steers.id)
			//                             +" prev: " + int64'Image(vert[min_i_1].prev)
			//     )
			if (model_ptr.track[it].st_start == vert[min_i_1].steer.id && model_ptr.track[it].st_end == vert[min_i_1].prev) ||
				(model_ptr.track[it].st_end == vert[min_i_1].steer.id && model_ptr.track[it].st_start == vert[min_i_1].prev) {

				tl = append(tl, model_ptr.track[it].id)
				break
			}
		}

		for it := 0; it < len(vert); it++ {
			if vert[it].steer.id == vert[min_i_1].prev {
				min_i_1 = it
				break
			}
		}

	}
	tl = append(tl, start_track_id)

	//reverse tl
	for it := 0; it < len(tl)/2; it++ {
		tl_cpy := tl[it]
		tl[it] = tl[len(tl)-it-1]
		tl[len(tl)-it-1] = tl_cpy

	}

	//for it in tl) it++ {
	//    Ada.Text_IO.Put_Line("it"+ int64'Image[it] + " tl:" +  int64'Image(tl[it]))
	//}
	if block == true {
		var found bool
		var track_ptr *Track

		for it := 0; it < len(model_ptr.track); it++ {
			if model_ptr.track[it].used_by == train_id && model_ptr.track[it].id != train_ptr.on_track {
				found = false
				for itt := 0; itt < len(tl); itt++ {
					if tl[itt] == model_ptr.track[it].id {
						found = true
						break
					}
				}
				if found == false {
					model_ptr.track[it].freeFromServiceTrain <- train_id
				}
			}
		}
		for it := 0; it < len(model_ptr.steer); it++ {
			if model_ptr.steer[it].used_by == train_id && model_ptr.steer[it].id != train_ptr.on_steer {
				found = false
				for itt := 0; itt < len(tl); itt++ {
					track_ptr = GetTrack(tl[itt], model_ptr)
					if track_ptr.st_end == model_ptr.steer[it].id || track_ptr.st_start == model_ptr.steer[it].id {
						found = true
						break
					}
				}
				if found == false {
					model_ptr.steer[it].freeFromServiceTrain <- train_id
				}
			}
		}
	}
	//    }
	//}

	return &tl
}
