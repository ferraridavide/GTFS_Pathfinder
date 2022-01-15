package gtfs;

import gtfs.dijkstra.Dijkstra;
import gtfs.dijkstra.Graph;
import gtfs.dijkstra.Node;
import gtfs.models.Routes;
import gtfs.models.StopTimes;
import gtfs.models.Stops;
import gtfs.models.Trips;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Entrypoint {
    public static void main(String[] args) throws IOException {
        // Importo i dati dai file CSV
        List<Stops> stopsList = Stops.fromCSV("./gtfs/stops.txt");
        List<StopTimes> stopTimesList = StopTimes.fromCSV("./gtfs/stop_times.txt");
        List<Trips> tripsList = Trips.fromCSV("./gtfs/trips.txt");
        List<Routes> routesList = Routes.fromCSV("./gtfs/routes.txt");


        List<Node> tripsNodeList = new ArrayList<>();
        for (Routes route : routesList) {
            var thisTrip = tripsList.stream().filter(x -> x.route_id.equals(route.route_id)).toList();
            for (Trips trip : thisTrip) {
//                var trip = thisTrip.get(0);
                var thisStopTime = stopTimesList.stream().filter(x -> x.trip_id.equals(trip.trip_id)).sorted((Comparator.comparing(o -> o.arrival_time))).toList();
//                System.out.println(trip.toString());
//                for (StopTimes stopTime : thisStopTime) {
//                    var stop = stopsList.stream().filter(x -> x.stop_id == stopTime.stop_id).findFirst().get();
//                    System.out.println("\t" + stop.stop_name + "\t" + stopTime.arrival_time);
//                }

                List<Node> stopsNodeList = thisStopTime.stream().map(x -> new Node(x)).toList();
                for (int i = 0; i < stopsNodeList.size() - 1; i++) {
                    var thisNode = stopsNodeList.get(i);
                    var nextNode = stopsNodeList.get(i+1);
                    thisNode.addDestination(nextNode, nextNode.stopTime.arrival_time - thisNode.stopTime.arrival_time);
                    if (nextNode.stopTime.arrival_time - thisNode.stopTime.arrival_time < 0){
                        System.out.println("QUESTO NON DOVREBBE SUCCEDERE!");
                    }
                }
                tripsNodeList.addAll(stopsNodeList);


            }
        }

        System.out.println(tripsNodeList.size());

        for (Stops stop : stopsList) {
            var lol = tripsNodeList.stream().filter(x -> x.stopTime.stop_id.equals(stop.stop_id)).toList();
            for (Node node : lol) {
                for (Node node1 : lol) {
                    if (!node.equals(node1) && node.stopTime.arrival_time < node1.stopTime.arrival_time) {
                        node.addDestination(node1, node1.stopTime.arrival_time - node.stopTime.arrival_time); //forse mettere il tempo?
                    }
                }
            }
        }




        var partenze = tripsNodeList.stream().filter(x -> x.stopTime.stop_id == 737).toList();
        var arrivi = tripsNodeList.stream().filter(x -> x.stopTime.stop_id == 2994).toList();


        List<List<Node>> solutions = new ArrayList<>();
        var progress = 0;

        for (Node partenza : partenze.stream().sorted(Comparator.comparingInt(o -> o.stopTime.arrival_time)).toList()) {
            progress++;
            System.out.println(progress + "/" + partenze.size() + " -> " + Helpers.secondsToTime(partenza.stopTime.arrival_time));
            Graph graph = new Graph();
            List<Node> cloned_list = tripsNodeList.stream().collect(Collectors.toList());
            cloned_list.forEach(graph::addNode);
            graph = Dijkstra.calculateShortestPathFromSource(graph, partenza);

            for (Node arrivo : arrivi.stream().sorted(Comparator.comparingInt(o -> o.stopTime.arrival_time)).toList()) {


                if (arrivo.getShortestPath().size() != 0) {
                    System.out.print("+");
                    List<Node> solution = arrivo.getShortestPath();
                    solution.add(arrivo);
                    solutions.add(solution);
                    break;

                } else {
                    System.out.print("-");
                }
            }
            System.out.println("\n");

            if (progress == 2){
                break;
            }
        }


        for (List<Node> solution : solutions) {
            solution.forEach(x -> {
            var route = tripsList.stream().filter(y -> y.trip_id.equals(x.stopTime.trip_id)).findFirst().get().route_id;
            var stop = stopsList.stream().filter(y -> y.stop_id.equals(x.stopTime.stop_id)).findFirst().get().stop_name;
            System.out.println(route + " - " + stop + " - " + Helpers.secondsToTime(x.stopTime.arrival_time));

        });

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.readLine();
        }


//        var nodePartenza = tripsNodeList.stream().filter(x -> x.stopTime.stop_id == 737).findFirst().get();
//        graph = Dijkstra.calculateShortestPathFromSource(graph, nodePartenza);
//        var nodoArrivo = graph.getNodes().stream().filter(x -> x.stopTime.stop_id == 2994).findFirst().get();
//
//        var percorsoCitta = nodoArrivo.getShortestPath();
//
//
//        percorsoCitta.forEach(x -> {
//            var route = tripsList.stream().filter(y -> y.trip_id.equals(x.stopTime.trip_id)).findFirst().get().route_id;
//            var stop = stopsList.stream().filter(y -> y.stop_id.equals(x.stopTime.stop_id)).findFirst().get().stop_name;
//            System.out.println(route + " - " + stop + " - " + Helpers.secondsToTime(x.stopTime.arrival_time));
//        });
//
//
//
//        List<Stops> listOfStops = percorsoCitta.stream().map(x -> stopsList.stream().filter(y -> y.stop_id.equals(x.stopTime.stop_id)).findFirst().get()).toList();
//        Helpers.saveCSV("path.csv", listOfStops);


    }
}
