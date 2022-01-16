package gtfs.models;

import gtfs.Helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class StopTimes {
    public Integer trip_id;
    public Integer arrival_time;
    public Integer departure_time;
    public Integer stop_id;
    public Integer stop_sequence;



    public static List<StopTimes> fromCSV(String path) {
        List<StopTimes> stopTimesList = new ArrayList<>();

        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            br.lines()
                    .skip(1)
                    .map(line -> line.split(splitBy))
                    .forEach((element) -> {
                        var stopTime = new StopTimes();
                        stopTime.trip_id = Integer.parseInt(element[0]);
                        stopTime.arrival_time = Helpers.timeToSeconds(element[1]);
                        stopTime.departure_time = Helpers.timeToSeconds(element[2]);
                        stopTime.stop_id = Integer.parseInt(element[3]);
                        stopTime.stop_sequence = Integer.parseInt(element[4]);
                        stopTimesList.add(stopTime);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stopTimesList;
    }

    @Override
    public String toString() {
        return "StopTimes{" +
                "trip_id=" + trip_id +
                ", arrival_time=" + arrival_time +
                ", departure_time=" + departure_time +
                ", stop_id=" + stop_id +
                ", stop_sequence=" + stop_sequence +
                '}';
    }
}
