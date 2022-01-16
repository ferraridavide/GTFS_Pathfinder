package gtfs.astar.models;

import gtfs.Helpers;
import gtfs.astar.GraphNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Station implements GraphNode {
    public Integer trip_id;
    public Integer stop_id;
    public Integer arrival_time;

    public Station(Integer trip_id, Integer stop_id, Integer arrival_time) {
        this.trip_id = trip_id;
        this.stop_id = stop_id;
        this.arrival_time = arrival_time;
    }

    @Override
    public String getId() {
        return this.trip_id + "-"+ this.stop_id;
    }
}
