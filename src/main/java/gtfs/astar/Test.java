package gtfs.astar;

import gtfs.Helpers;
import gtfs.astar.models.Station;
import gtfs.astar.models.TimeScorer;
import gtfs.dijkstra.Dijkstra;
import gtfs.dijkstra.Node;
import gtfs.models.Routes;
import gtfs.models.StopTimes;
import gtfs.models.Stops;
import gtfs.models.Trips;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) throws IOException {


        Set<Station> stations = new HashSet<>();
        Map<String, Set<String>> connections = new HashMap<>();

        List<Stops> stopsList = Stops.fromCSV("./gtfs/stops.txt");
        List<StopTimes> stopTimesList = StopTimes.fromCSV("./gtfs/stop_times.txt");
        List<Trips> tripsList = Trips.fromCSV("./gtfs/trips.txt");
        List<Routes> routesList = Routes.fromCSV("./gtfs/routes.txt");

        List<Station> tripsNodeList = new ArrayList<>();
        // Per ogni tratta... (Routes)
        for (Routes route : routesList) {
            var thisTrip = tripsList.stream().filter(x -> x.route_id.equals(route.route_id)).toList();

            // Per ogni orario possibile di tale tratta...
            for (Trips trip : thisTrip) {  //.stream().filter(x -> x.trip_id == 14153763).toList()

                // Prendo tutte le fermate che fa...
                var thisStopTime = stopTimesList.stream().filter(x -> x.trip_id.equals(trip.trip_id)).sorted((Comparator.comparing(o -> o.arrival_time))).toList();

                // Collego i nodi in avanti
                List<Station> stopsNodeList = thisStopTime.stream().map(x -> new Station(x.trip_id,x.stop_id,x.arrival_time)).toList();
                stations.addAll(stopsNodeList);
                for (int i = 0; i < stopsNodeList.size() - 1; i++) {
                    var thisNode = stopsNodeList.get(i);
                    var nextNode = stopsNodeList.get(i + 1);
                    var values = Stream.of(nextNode.getId()).collect(Collectors.toSet());
                    connections.put(thisNode.getId(), values);
                }
                connections.put(stopsNodeList.get(stopsNodeList.size() - 1).getId(), new HashSet<>());
                tripsNodeList.addAll(stopsNodeList);
            }
        }
        System.out.println("PRE");
        connections.entrySet().stream().forEach(x-> System.out.println(x.getKey() + " ---> " + String.join(",", x.getValue())));


        // Per permettere i cambi prendo ogni stazione...
        for (Stops stop : stopsList) {
            // di ogni stazione trovo tutti i possibili treni che passano durante la giornata
            var trainStopTimes = tripsNodeList.stream().filter(x -> x.stop_id.equals(stop.stop_id)).toList();
            for (Station nodeA : trainStopTimes) {
                for (Station nodeB : trainStopTimes) {
                    // Collego tutte le fermate trovate con fermate successive
                    if (!nodeA.equals(nodeB) && nodeA.arrival_time < nodeB.arrival_time && (nodeB.arrival_time - nodeA.arrival_time) < 3600) {
                        var prevConnections = new ArrayList<String>();
                        if (connections.get(nodeA.getId()) != null) {
                            prevConnections = (ArrayList<String>) connections.get(nodeA.getId()).stream().collect(Collectors.toList());

                        }
                        prevConnections.add(nodeB.getId());
                        var values = prevConnections.stream().collect(Collectors.toSet());
                        connections.put(nodeA.getId(), values);
                    }
                }
            }
        }

        System.out.println("POST");
        connections.entrySet().stream().forEach(x-> System.out.println(x.getKey() + " ---> " + String.join(",", x.getValue())));



        Graph<Station> underground = new Graph<>(stations, connections);
        RouteFinder<Station> routeFinder = new RouteFinder<>(underground, new TimeScorer(), new TimeScorer());
        List<Station> route = routeFinder.findRoute(underground.getNode("14156413-2564"), underground.getNode("14156413-964"));
        System.out.println(route.stream().map(Station::getId).collect(Collectors.toList()));

//        var partenze = tripsNodeList.stream().filter(x -> x.stop_id == 2564).toList();
//        var arrivi = tripsNodeList.stream().filter(x -> x.stop_id == 964).toList();
//
//        var progress = 0;
//
////        for (Station partenza : partenze.stream().sorted(Comparator.comparingInt(o -> o.arrival_time)).toList()) {
////            progress++;
////            System.out.println(progress + "/" + partenze.size() + " -> " + Helpers.secondsToTime(partenza.arrival_time));
////            for (Station arrivo : arrivi.stream().sorted(Comparator.comparingInt(o -> o.arrival_time)).toList()) {
////                try {
////                    Graph<Station> underground = new Graph<>(stations, connections);
////                    RouteFinder<Station> routeFinder =  new RouteFinder<>(underground, new TimeScorer(), new TimeScorer());
////                    List<Station> route = routeFinder.findRoute(underground.getNode(partenza.getId()), underground.getNode(arrivo.getId()));
////                    System.out.println(route.stream().map(Station::getId).collect(Collectors.toList()));
////                } catch (Exception e) {
////                    System.out.print("-");
////                }
////
////            }
////            System.out.println("\n");
////        }






    }

}
