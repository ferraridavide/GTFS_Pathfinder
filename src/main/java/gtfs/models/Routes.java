package gtfs.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Routes {
    public String route_id;
    public String agency_id;
    public String route_short_name;
    public String route_long_name;
    public String route_desc;
    public int route_type;


    public static List<Routes> fromCSV(String path) {
        List<Routes> routesList = new ArrayList<>();

        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            br.lines()
                    .skip(1)
                    .map(line -> line.split(splitBy))
                    .forEach((element) -> {
                        var route = new Routes();
                        route.route_id = element[0];
                        route.agency_id = element[1];
                        route.route_short_name = element[2];
                        route.route_long_name = element[3];
                        route.route_desc = element[4];
                        route.route_type = Integer.parseInt(element[5]);
                        routesList.add(route);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return routesList;
    }

    @Override
    public String toString() {
        return "Routes{" +
                "route_id='" + route_id + '\'' +
                ", agency_id='" + agency_id + '\'' +
                ", route_short_name='" + route_short_name + '\'' +
                ", route_long_name='" + route_long_name + '\'' +
                ", route_desc='" + route_desc + '\'' +
                ", route_type=" + route_type +
                '}';
    }
}
