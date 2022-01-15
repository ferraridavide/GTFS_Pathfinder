package gtfs.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Stops {
    public Integer stop_id;
    public String stop_code;
    public String stop_name;
    public String stop_desc;
    public double stop_lat;
    public double stop_lon;
    public String stop_url;
    public String location_type;
    public String parent_station;


    public static List<Stops> fromCSV(String path)  {
        List<Stops> stopsList = new ArrayList<>();

        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            br.lines()
                    .skip(1)
                    .map(line -> line.split(splitBy))
                    .forEach((element) -> {
                        var stop = new Stops();
                        stop.stop_id = Integer.parseInt(element[0]);
                        stop.stop_code = element[1];
                        stop.stop_name = element[2];
                        stop.stop_desc = element[3];
                        stop.stop_lat = Double.parseDouble(element[4]);
                        stop.stop_lon = Double.parseDouble(element[5]);
                        stop.stop_url = element[6];
                        stop.location_type = element[7];
                        stop.parent_station = element[8];
                        stopsList.add(stop);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stopsList;
    }

    @Override
    public String toString() {
        return "Stops{" +
                "stop_id=" + stop_id +
                ", stop_code='" + stop_code + '\'' +
                ", stop_name='" + stop_name + '\'' +
                ", stop_desc='" + stop_desc + '\'' +
                ", stop_lat=" + stop_lat +
                ", stop_lon=" + stop_lon +
                ", stop_url='" + stop_url + '\'' +
                ", location_type='" + location_type + '\'' +
                ", parent_station='" + parent_station + '\'' +
                '}';
    }
}
