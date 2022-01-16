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

public class Entrypoint {
    public static void main(String[] args) throws IOException {
        // Importo i dati dai file CSV
        List<Stops> stopsList = Stops.fromCSV("./gtfs/stops.txt");
        List<StopTimes> stopTimesList = StopTimes.fromCSV("./gtfs/stop_times.txt");
        List<Trips> tripsList = Trips.fromCSV("./gtfs/trips.txt");
        List<Routes> routesList = Routes.fromCSV("./gtfs/routes.txt");


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
                    thisNode.addDestination(nextNode, nextNode.stopTime.arrival_time - thisNode.stopTime.arrival_time);
                }
                tripsNodeList.addAll(stopsNodeList);
            }
        }

        System.out.println(tripsNodeList.size());

        // Per permettere i cambi prendo ogni stazione...
        for (Stops stop : stopsList) {
            // di ogni stazione trovo tutti i possibili treni che passano durante la giornata
            var trainStopTimes = tripsNodeList.stream().filter(x -> x.stopTime.stop_id.equals(stop.stop_id)).toList();
            for (Node nodeA : trainStopTimes) {
                for (Node nodeB : trainStopTimes) {
                    // Collego tutte le fermate trovate con fermate successive
                    if (!nodeA.equals(nodeB) && nodeA.stopTime.arrival_time < nodeB.stopTime.arrival_time && (nodeB.stopTime.arrival_time - nodeA.stopTime.arrival_time) < 3600) {
                        nodeA.addDestination(nodeB, (nodeB.stopTime.arrival_time - nodeA.stopTime.arrival_time) + 1); // + 1 è il prezzo simbolico del cambio, per incentivare soluzioni con meno cambi possibili
                    }
                }
            }
        }





        var partenze = tripsNodeList.stream().filter(x -> x.stopTime.stop_id == 2033).toList();
        var arrivi = tripsNodeList.stream().filter(x -> x.stopTime.stop_id == 964).toList();


        var progress = 0;

        for (Node partenza : partenze.stream().sorted(Comparator.comparingInt(o -> o.stopTime.arrival_time)).toList()) {
            progress++;
            System.out.println(progress + "/" + partenze.size() + " -> " + Helpers.secondsToTime(partenza.stopTime.arrival_time));
            Graph graph = new Graph();

            tripsNodeList.forEach(graph::addNode);
            graph = Dijkstra.calculateShortestPathFromSource(graph, partenza);


            for (Node arrivo : arrivi.stream().sorted(Comparator.comparingInt(o -> o.stopTime.arrival_time)).toList()) {
                if (arrivo.getShortestPath().size() != 0) {
                    List<Node> solution = arrivo.getShortestPath();
                    solution.add(arrivo);


                    System.out.println(
                            "Partenza: " + Helpers.secondsToTime(partenza.stopTime.arrival_time) +
                                    "\tArrivo: " + Helpers.secondsToTime(arrivo.stopTime.arrival_time) +
                                    "\tDurata: " + Helpers.secondsToTime(arrivo.stopTime.arrival_time - partenza.stopTime.arrival_time) +
                                    "\tFermate: " + solution.size() +
                                    "\tCambi: " + (solution.stream().map(x -> tripsList.stream().filter(y -> y.trip_id.equals(x.stopTime.trip_id)).findFirst().get().route_id).distinct().toList().size() - 1));

                    solution.forEach(x -> {
                        var route = tripsList.stream().filter(y -> y.trip_id.equals(x.stopTime.trip_id)).findFirst().get().route_id;
                        var stop = stopsList.stream().filter(y -> y.stop_id.equals(x.stopTime.stop_id)).findFirst().get().stop_name;
                        System.out.println("\t" + route + " - " + stop + " - " + Helpers.secondsToTime(x.stopTime.arrival_time) + " - " + x.stopTime.trip_id + " - " + x.stopTime.stop_id);
                    });

//                    readLine();

                    break;

                }
            }
            System.out.println("\n");
        }


    }

    private static void readLine(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.readLine();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
