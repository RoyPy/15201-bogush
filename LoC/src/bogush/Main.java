package bogush;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("at least two arguments required: config file path and dir path");
            return;
        }
        String configFileName = args[0];
        Path configFilePath = Paths.get(configFileName);
        String dir = args[1];
        Path dirToWalk = Paths.get(dir);
        Statistics stats = new Statistics();
        Filter[] filters = new Filter[0];
        try {
            filters = FilterParser.parse(configFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StatisticsCollector collector = null;
        try {
            collector = new StatisticsCollector(stats, filters);
        } catch (NoFilterException e) {
            e.printStackTrace();
        }
//        collector.printVisited = true;
        try {
            Files.walkFileTree(dirToWalk, collector);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(stats.toString());
    }
}
