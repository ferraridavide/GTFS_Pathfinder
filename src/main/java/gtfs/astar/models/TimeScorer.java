package gtfs.astar.models;

import gtfs.astar.Scorer;

public class TimeScorer implements Scorer<Station> {
    @Override
    public double computeCost(Station from, Station to) {
        if (from.stop_id == to.stop_id){
            return (to.arrival_time - from.arrival_time) + 1;
        } else {
            return (to.arrival_time - from.arrival_time);
        }
    }
}