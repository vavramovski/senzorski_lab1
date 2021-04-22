import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Algorithms {


    // Try to locate sensors via anchor sensors
    public static List<Sensor> nonIterativeTrilateration(List<Sensor> startAnchorSensors, List<Sensor> sensors) {
        List<Sensor> newSensorList = new LinkedList<>();
        for (Sensor sensorValue : sensors) {
            Sensor sensor = sensorValue.clone();
            try {
                List<Sensor> closestAnchors = findClosestThreeAnchors(startAnchorSensors, sensor);
                double distance1 = euclideanDistance(closestAnchors.get(0), sensor);
                double distance2 = euclideanDistance(closestAnchors.get(1), sensor);
                double distance3 = euclideanDistance(closestAnchors.get(2), sensor);
                sensor.setPredictedPosition(trilateration3D(
                        closestAnchors.get(0), closestAnchors.get(1), closestAnchors.get(2), distance1, distance2, distance3
                ));
                if (sensor.predictedPosition.x == 0 && sensor.predictedPosition.y == 0)
                    continue;

                sensor.setAnchor(true);
                newSensorList.add(sensor);
            } catch (Exception ignored) {
            }
        }
        return newSensorList;
    }

    // Discovery of a sensor makes it an Anchor, so it helps with discovering new sensors using Euclid distance heuristic
    public static List<Sensor> iterativeTrilaterationDistanceHeuristic(List<Sensor> startAnchorSensors, List<Sensor> sensors) {
        List<Sensor> newSensorList = new LinkedList<>(startAnchorSensors);
        for (Sensor sensorValue : sensors) {
            Sensor sensor = sensorValue.clone();
            try {
                List<Sensor> closestAnchors = findClosestThreeAnchors(newSensorList, sensor);
                double distance1 = euclideanDistance(closestAnchors.get(0), sensor);
                double distance2 = euclideanDistance(closestAnchors.get(1), sensor);
                double distance3 = euclideanDistance(closestAnchors.get(2), sensor);
                sensor.setPredictedPosition(trilateration3D(
                        closestAnchors.get(0), closestAnchors.get(1), closestAnchors.get(2), distance1, distance2, distance3
                ));
                sensor.setAnchor(true);

                if (sensor.predictedPosition.x == 0 && sensor.predictedPosition.y == 0)
                    continue;
                newSensorList.add(sensor);
            } catch (Exception ignored) {
            }
        }
        return newSensorList;
    }

    // Discovery of a sensor makes it an Anchor, so it helps with discovering new sensors using Hops count heuristic
    public static List<Sensor> iterativeTrilaterationHopsHeuristic(List<Sensor> startAnchorSensors, List<Sensor> sensors) {
        List<Sensor> newSensorList = new LinkedList<>(startAnchorSensors);
        for (Sensor sensorValue : sensors) {
            Sensor sensor = sensorValue.clone();
            try {
                List<Sensor> closestAnchors = findLeastHopsThreeAnchors(newSensorList, sensor);
                double distance1 = euclideanDistance(closestAnchors.get(0), sensor);
                double distance2 = euclideanDistance(closestAnchors.get(1), sensor);
                double distance3 = euclideanDistance(closestAnchors.get(2), sensor);
                sensor.setPredictedPosition(trilateration3D(
                        closestAnchors.get(0), closestAnchors.get(1), closestAnchors.get(2), distance1, distance2, distance3
                ));

                int totalHops = closestAnchors.get(0).hops + closestAnchors.get(1).hops + closestAnchors.get(2).hops;
                sensor.setHops(totalHops + 1);
                sensor.setAnchor(true);
                if (sensor.predictedPosition.x == 0 && sensor.predictedPosition.y == 0)
                    continue;
                newSensorList.add(sensor);
            } catch (Exception ignored) {
            }
        }
        return newSensorList;
    }

    private static List<Sensor> findClosestThreeAnchors(List<Sensor> anchorSensors, Sensor sensor) throws Exception {
        List<Sensor> closestAnchors = anchorSensors.stream()
                .filter(sensor1 -> sensor1.isAnchor)
                .filter(anchor -> euclideanDistance(anchor, sensor) <= anchor.radiusRange)
                .sorted(Comparator.comparingDouble(anchor -> euclideanDistance(anchor, sensor)))
                .limit(3)
                .collect(Collectors.toList());

        for (Sensor anchor : closestAnchors) {
            if (anchor.radiusRange < euclideanDistance(anchor, sensor)) {
                throw new Exception("Out of range");
            }
        }
        return closestAnchors;
    }

    // filter sensors in range
    // Sort by least number of hops
    private static List<Sensor> findLeastHopsThreeAnchors(List<Sensor> anchorSensors, Sensor sensor) throws Exception {
        List<Sensor> closestAnchors = anchorSensors.stream()
                .filter(sensor1 -> sensor1.isAnchor)
                .filter(anchor -> euclideanDistance(anchor, sensor) <= anchor.radiusRange)
//                .sorted(Comparator.comparing((Sensor anchor) -> euclideanDistance(anchor, sensor)).thenComparing(Sensor::getHops))
                .sorted(Comparator.comparing(Sensor::getHops).thenComparing(anchor -> euclideanDistance(anchor,sensor)))
                .limit(3)
                .collect(Collectors.toList());

        for (Sensor anchor : closestAnchors) {
            if (anchor.radiusRange < euclideanDistance(anchor, sensor)) {
                throw new Exception("Out of range");
            }
        }
        return closestAnchors;
    }

    private static Position trilateration3D(Sensor anchorSensor1, Sensor anchorSensor2, Sensor anchorSensor3, double r1, double r2, double r3) {
        double x1 = anchorSensor1.position.x;
        double x2 = anchorSensor2.position.x;
        double x3 = anchorSensor3.position.x;
        double y1 = anchorSensor1.position.y;
        double y2 = anchorSensor2.position.y;
        double y3 = anchorSensor3.position.y;
        double C = r1 * r1 - r2 * r2 - x1 * x1 + x2 * x2 - y1 * y1 + y2 * y2;
        double F = r2 * r2 - r3 * r3 - x2 * x2 + x3 * x3 - y2 * y2 + y3 * y3;

        double A = -2 * x1 + 2 * x2;
        double B = -2 * y1 + 2 * y2;

        double D = -2 * x2 + 2 * x3;
        double E = -2 * y2 + 2 * y3;

        double x = 0;
        double y = 0;

        if ((E * A - B * D) != 0 && (B * D - A * E) != 0) {
            x = (C * E - F * B) / (E * A - B * D);
            y = (C * D - A * F) / (B * D - A * E);
        }

        return new Position(x, y);
    }

    public static double euclideanDistance(Sensor anchorSensor, Sensor sensor) {
        double x1 = anchorSensor.position.x;
        double x2 = sensor.position.x;
        double y1 = anchorSensor.position.y;
        double y2 = sensor.position.y;
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)) * (1 - (sensor.errorPercent / 100));
    }
}

