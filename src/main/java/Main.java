import org.jfree.data.category.CategoryDataset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main {

    public static Random random = new Random();

    public static Integer areaSize = 200;
    public static int[] radiuses = {50, 65, 80};
    public static int[] err = {25, 35, 45};
    public static int[] numberAnchorSensors = {25, 40};

    public static void main(String[] args) {



        // 3 iterations

        // fraction
        for (int m = 0; m < 2; m++) {
            List<Series> series = new LinkedList<>();
            List<Series> discoverySeries = new LinkedList<>();
            // radius
            for (int i = 0; i < 3; i++) {
                // error
                for (int l = 0; l < 3; l++) {
                    int radius = radiuses[i];
                    int error = err[l];
                    List<Sensor> sensors = new ArrayList<>();
                    List<Sensor> anchorSensors = new LinkedList<>();
                    for (int j = 0; j < numberAnchorSensors[m]; j++) {
                            anchorSensors.add(new Sensor(new Position(random.nextInt(areaSize), random.nextInt(areaSize)),
                                    radius, true, true, generateError(error)));

                    }

                    int numOfRandomSensors = 100 - numberAnchorSensors[m];

                    // Generating Sensors
                    for (int j = 0; j < numOfRandomSensors; j++) {
                        int x = random.nextInt(areaSize);
                        int y = random.nextInt(areaSize);
                        double generatedError = generateError(error);
                        sensors.add(new Sensor(new Position(x, y), 10, false, false, generatedError));
                    }

                    System.out.println("Series: " + i + 1);
                    System.out.println("Radius: " + radiuses[i]);
                    System.out.println("Error: " + err[i]);
                    List<Sensor> nonIterative = Algorithms.nonIterativeTrilateration(anchorSensors, sensors);
                    double nonIterativeError = nonIterative.stream().mapToDouble(Sensor::predictedError).sum();
                    int discoveredNA = nonIterative.size() - anchorSensors.size();
                    double aleNA = (nonIterativeError / discoveredNA) * (100.0f / radius);
                    System.out.println("Non Iterative Error: " + aleNA);
                    System.out.println("Discovery: " + discoveredNA + "/" + sensors.size());

                    List<Sensor> iterativeDistance = Algorithms.iterativeTrilaterationDistanceHeuristic(anchorSensors, sensors);
                    double distanceIterativeError = iterativeDistance.stream()
                            .filter(sensor -> sensor.predictedPosition != null)
                            .mapToDouble(Sensor::predictedError).sum();
                    int discoveredIA1 = nonIterative.size() - anchorSensors.size();
                    double aleIA1 = (distanceIterativeError / discoveredIA1) * (100.0f / radius);
                    System.out.println("Iterative Distance Error: " + aleIA1);
                    System.out.println("Discovery: " + discoveredIA1 + "/" + sensors.size());

                    List<Sensor> iterativeHops = Algorithms.iterativeTrilaterationHopsHeuristic(anchorSensors, sensors);
                    double hopsIterativeError = iterativeHops.stream()
                            .filter(sensor -> sensor.predictedPosition != null)
                            .mapToDouble(Sensor::predictedError).sum();
                    int discoveredIA2 = nonIterative.size() - anchorSensors.size();
                    double aleIA2 = (hopsIterativeError / discoveredIA2) * (100.0f / radius);
                    System.out.println("Iterative Hops Error: " + aleIA2);
                    System.out.println("Discovery: " + discoveredIA2 + "/" + sensors.size());

                    series.add(new Series(aleNA, aleIA1, aleIA2));
                    discoverySeries.add(new Series(discoveredNA, discoveredIA1, discoveredIA2));
                    System.out.println("\n\n\n");
                }
            }
            LineChart_AWT.createALEChart(series, numberAnchorSensors[m]+"% anchors", "Radius", "ALE");
        }


    }

    private static CategoryDataset createDataset() {

        return null;
    }

    public static Sensor[] toArray(List<Sensor> sensors) {
        Sensor[] sensorsArr = new Sensor[sensors.size()];
        for (int i = 0; i < sensors.size(); i++) {
            sensorsArr[i] = sensors.get(i);
        }
        return sensorsArr;
    }

    // N(10,1)
    public static Double generateError(int mean) {
        return random.nextGaussian() + mean;
    }


}

class Sensor implements Cloneable {
    public Position position;
    public Position predictedPosition;
    public int radiusRange;
    public boolean isAnchor;
    public boolean trueAnchor;
    public int hops;
    public double errorPercent;

    public Sensor(Position position, Integer radiusRange, boolean trueAnchor, boolean isAnchor, Double errorPercent) {
        this.position = position;
        this.isAnchor = isAnchor;
        this.trueAnchor = trueAnchor;
        this.radiusRange = radiusRange;
        this.errorPercent = errorPercent;
        this.hops = 0;
    }

    public Sensor(Position position, Position predictedPosition, Integer radiusRange, Boolean isAnchor, Boolean trueAnchor, Integer hops, Double errorPercent) {
        this.position = position;
        this.predictedPosition = predictedPosition;
        this.radiusRange = radiusRange;
        this.isAnchor = isAnchor;
        this.trueAnchor = trueAnchor;
        this.hops = hops;
        this.errorPercent = errorPercent;
    }

    public void setPredictedPosition(Position predictedPosition) {
        this.predictedPosition = predictedPosition;
    }

    public void setAnchor(boolean anchor) {
        isAnchor = anchor;
    }

    public void setHops(Integer hops) {
        this.hops = hops;
    }

    public double predictedError() {
        double x1 = position.x;
        double x2 = predictedPosition.x;
        double y1 = position.y;
        double y2 = predictedPosition.y;
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    public int getHops() {
        return hops;
    }

    @Override
    protected Sensor clone() {
        Position newPredictedPosition = null;
        if (predictedPosition != null) {
            newPredictedPosition = new Position(predictedPosition.x, predictedPosition.y);
        }
        return new Sensor(
                new Position(position.x, position.y),
                newPredictedPosition,
                radiusRange,
                isAnchor,
                trueAnchor,
                hops,
                errorPercent);
    }
}

class Position {
    double x;
    double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }
}
