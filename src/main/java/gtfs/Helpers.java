package gtfs;

import gtfs.models.Stops;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Helpers {
    public static void saveCSV(String path, List<Stops> nodi) throws IOException {
        FileWriter csvWriter = new FileWriter(path);
        csvWriter.append("lat, long, name\n");

        for (Stops n : nodi) {
            csvWriter.append(n.stop_lat + ", " + n.stop_lon + ", " + n.stop_name + "\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    public static String secondsToTime(Integer seconds){
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    public static Integer timeToSeconds(String time) {
        var hh = time.substring(0,2);
        var mm = time.substring(3,5);
        var ss = time.substring(6,8);
        return Integer.parseInt(hh) * 3600 + Integer.parseInt(mm) * 60 + Integer.parseInt(ss);
    }
}
