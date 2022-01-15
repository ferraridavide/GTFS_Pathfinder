package gtfs.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Trips {
    public String route_id;
    public Long service_id;
    public Integer trip_id;
    public String trip_headsign;
    public Integer trip_short_name;
    public Integer direction_id;



    public static List<Trips> fromCSV(String path) {
        List<Trips> tripsList = new ArrayList<>();

        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            br.lines()
                    .skip(1)
                    .map(line -> line.split(splitBy))
                    .forEach((element) -> {
                        var trip = new Trips();
                        trip.route_id = element[0];
                        trip.service_id = Long.parseLong(element[1]);
                        trip.trip_id = Integer.parseInt(element[2]);
                        trip.trip_headsign = element[3];
                        trip.trip_short_name = Integer.parseInt(element[4]);
                        trip.direction_id = Integer.parseInt(element[5]);
                        tripsList.add(trip);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tripsList;
    }

    @Override
    public String toString() {
        return "Trips{" +
                "route_id='" + route_id + '\'' +
                ", trip_id=" + trip_id +
                '}';
    }
}
