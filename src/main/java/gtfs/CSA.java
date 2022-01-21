package gtfs;

import com.sun.jdi.connect.Connector;
import gtfs.dijkstra.Node;
import gtfs.models.Routes;
import gtfs.models.StopTimes;
import gtfs.models.Stops;
import gtfs.models.Trips;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Connection {
    public int departure_station, arrival_station;
    public int departure_timestamp, arrival_timestamp;

    public int departure_station_trip, arrival_station_trip;

    public Connection(int departure_station, int arrival_station, int departure_timestamp, int arrival_timestamp, int departure_station_trip, int arrival_station_trip) {
        this.departure_station = departure_station;
        this.arrival_station = arrival_station;
        this.departure_timestamp = departure_timestamp;
        this.arrival_timestamp = arrival_timestamp;
        this.departure_station_trip = departure_station_trip;
        this.arrival_station_trip = arrival_station_trip;
    }
};

class Timetable {
    public List<Connection> connections;

    public Timetable(List<Connection> connections) {
        this.connections = connections;
    }
// Timetable constructor: reads all the connections from stdin

};

public class CSA {
    public static final int MAX_STATIONS = 100000;

    public Timetable timetable;
    Connection in_connection[];
    int earliest_arrival[];

    public CSA(Timetable timetable) {
        this.timetable = timetable;
    }

    void main_loop(int arrival_station) {
        int earliest = Integer.MAX_VALUE;
        timetable.connections = timetable.connections.stream().sorted(Comparator.comparingInt(x -> x.arrival_timestamp)).toList();

        for (Connection connection : timetable.connections) {
            if (connection.departure_timestamp >= earliest_arrival[connection.departure_station] &&
                    connection.arrival_timestamp < earliest_arrival[connection.arrival_station]) {
                earliest_arrival[connection.arrival_station] = connection.arrival_timestamp;
                in_connection[connection.arrival_station] = connection;

                if (connection.arrival_station == arrival_station) {
                    earliest = Math.min(earliest, connection.arrival_timestamp);
                }
            } else if (connection.arrival_timestamp > earliest) {
                return;
            }
        }
    }

    List<Connection> print_result(int arrival_station) {
        if (in_connection[arrival_station] == null) {
            System.out.println("NO_SOLUTION");
            return null;
        } else {
            List<Connection> route = new ArrayList<Connection>();
            // We have to rebuild the route from the arrival station 
            Connection last_connection = in_connection[arrival_station];
            while (last_connection != null) {
                route.add(last_connection);
                last_connection = in_connection[last_connection.departure_station];
            }
            // And now print it out in the right direction
            Collections.reverse(route);
            return route;
        }
    }

    List<Connection> compute(int departure_station, int arrival_station, int departure_time) {
        in_connection = new Connection[MAX_STATIONS];
        earliest_arrival = new int[MAX_STATIONS];
        for (int i = 0; i < MAX_STATIONS; ++i) {
            in_connection[i] = null;
            earliest_arrival[i] = Integer.MAX_VALUE;
        }
        earliest_arrival[departure_station] = departure_time;

        if (departure_station <= MAX_STATIONS && arrival_station <= MAX_STATIONS) {
            main_loop(arrival_station);
        }
        return print_result(arrival_station);
    }

    public static void main(String[] args) {
        List<Stops> stopsList = Stops.fromCSV("./gtfs/stops.txt");
        List<StopTimes> stopTimesList = StopTimes.fromCSV("./gtfs/stop_times.txt");
        List<Trips> tripsList = Trips.fromCSV("./gtfs/trips.txt");
        List<Routes> routesList = Routes.fromCSV("./gtfs/routes.txt");

        List<Connection> connectionList = new ArrayList<Connection>();


        List<Node> tripsNodeList = new ArrayList<>();

        // Per ogni tratta... (Routes)
        for (Routes route : routesList) {
            var thisTrip = tripsList.stream().filter(x -> x.route_id.equals(route.route_id)).toList();

            // Per ogni orario possibile di tale tratta...
            for (Trips trip : thisTrip) {

                // Prendo tutte le fermate che fa...
                var thisStopTime = stopTimesList.stream().filter(x -> x.trip_id.equals(trip.trip_id)).sorted((Comparator.comparing(o -> o.arrival_time))).toList();

                // Collego i nodi in avanti
                List<Node> stopsNodeList = thisStopTime.stream().map(x -> new Node(x)).toList();
                for (int i = 0; i < stopsNodeList.size() - 1; i++) {
                    var thisNode = stopsNodeList.get(i);
                    var nextNode = stopsNodeList.get(i + 1);
                    connectionList.add(new Connection(thisNode.stopTime.stop_id,nextNode.stopTime.stop_id,thisNode.stopTime.departure_time, nextNode.stopTime.arrival_time, thisNode.stopTime.trip_id, nextNode.stopTime.trip_id));
                }
                tripsNodeList.addAll(stopsNodeList);
            }
        }

        // Per permettere i cambi prendo ogni stazione...
        for (Stops stop : stopsList) {
            // di ogni stazione trovo tutti i possibili treni che passano durante la giornata
            var trainStopTimes = tripsNodeList.stream().filter(x -> x.stopTime.stop_id.equals(stop.stop_id)).toList();
            for (Node nodeA : trainStopTimes) {
                for (Node nodeB : trainStopTimes) {
                    // Collego tutte le fermate trovate con fermate successive
                    if (!nodeA.equals(nodeB) && nodeA.stopTime.arrival_time < nodeB.stopTime.arrival_time && (nodeB.stopTime.arrival_time - nodeA.stopTime.arrival_time) < 3600) {
                        connectionList.add(new Connection(nodeA.stopTime.stop_id,nodeB.stopTime.stop_id,nodeA.stopTime.departure_time, nodeB.stopTime.arrival_time, nodeA.stopTime.trip_id, nodeB.stopTime.trip_id));
                    }
                }
            }
        }


        var STOP_ID_PARTENZA = 332;
        var STOP_ID_ARRIVO = 2793;

        var timetable = new Timetable(connectionList);

        CSA csa = new CSA(timetable);

        var isDone = false;
        var lastTime = 0;
        do {
            var result = csa.compute(STOP_ID_PARTENZA,STOP_ID_ARRIVO,lastTime);
            if (result != null) {
                System.out.println("Partenza: " + Helpers.secondsToTime(result.get(0).departure_timestamp) + " - Durata: " + Helpers.secondsToTime(result.get(result.size()-1).arrival_timestamp - result.get(0).departure_timestamp));
                System.out.println("Cambi: " + (result.stream().map(x -> x.departure_station_trip).distinct().count() - 1));
                for (Connection x : result) {
                    var routeFrom = tripsList.stream().filter(y -> y.trip_id.equals(x.departure_station_trip)).findFirst().get().route_id;
                    var routeTo = tripsList.stream().filter(y -> y.trip_id.equals(x.arrival_station_trip)).findFirst().get().route_id;
                    System.out.println(
                            "[" + routeFrom + "] " + stopsList.stream().filter(y -> y.stop_id == x.departure_station).findAny().get().stop_name + " (" + Helpers.secondsToTime(x.departure_timestamp)
                                    + ") -> [" + routeTo + "] " +  stopsList.stream().filter(y -> y.stop_id == x.arrival_station).findAny().get().stop_name + " (" + Helpers.secondsToTime(x.arrival_timestamp) + ")");
                }
                System.out.println("\n\n");
                lastTime = result.get(0).departure_timestamp +1;
            } else {
                isDone = true;
            }


        } while (!isDone);
    }
}