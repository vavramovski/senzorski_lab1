import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.IOException;
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
                double distance1 = euclideanDistanceErrorless(closestAnchors.get(0), sensor);
                double distance2 = euclideanDistanceErrorless(closestAnchors.get(1), sensor);
                double distance3 = euclideanDistanceErrorless(closestAnchors.get(2), sensor);
                double distance4 = euclideanDistanceErrorless(closestAnchors.get(3), sensor);
                sensor.setPredictedPosition(trilateration3D(
                        closestAnchors.get(0), closestAnchors.get(1), closestAnchors.get(2), closestAnchors.get(3),
                        distance1, distance2, distance3, distance4
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
                double distance1 = euclideanDistanceErrorless(closestAnchors.get(0), sensor);
                double distance2 = euclideanDistanceErrorless(closestAnchors.get(1), sensor);
                double distance3 = euclideanDistanceErrorless(closestAnchors.get(2), sensor);
                double distance4 = euclideanDistanceErrorless(closestAnchors.get(3), sensor);

                sensor.setPredictedPosition(trilateration3D(
                        closestAnchors.get(0), closestAnchors.get(1), closestAnchors.get(2), closestAnchors.get(3),
                        distance1, distance2, distance3, distance4
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
                double distance1 = euclideanDistanceErrorless(closestAnchors.get(0), sensor);
                double distance2 = euclideanDistanceErrorless(closestAnchors.get(1), sensor);
                double distance3 = euclideanDistanceErrorless(closestAnchors.get(2), sensor);
                double distance4 = euclideanDistanceErrorless(closestAnchors.get(2), sensor);
                sensor.setPredictedPosition(trilateration3D(
                        closestAnchors.get(0), closestAnchors.get(1), closestAnchors.get(2), closestAnchors.get(3),
                        distance1, distance2, distance3, distance4
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
                .filter(anchor -> euclideanDistanceErrorless(anchor, sensor) <= anchor.radiusRange)
                .sorted(Comparator.comparingDouble(anchor -> euclideanDistanceErrorless(anchor, sensor)))
                .limit(4)
                .collect(Collectors.toList());

        for (Sensor anchor : closestAnchors) {
            if (anchor.radiusRange < euclideanDistanceErrorless(anchor, sensor)) {
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
                .filter(anchor -> euclideanDistanceErrorless(anchor, sensor) <= anchor.radiusRange)
//                .sorted(Comparator.comparing((Sensor anchor) -> euclideanDistance(anchor, sensor)).thenComparing(Sensor::getHops))
                .sorted(Comparator.comparing(Sensor::getHops).thenComparing(anchor -> euclideanDistanceErrorless(anchor, sensor)))
                .limit(4)
                .collect(Collectors.toList());

        for (Sensor anchor : closestAnchors) {
            if (anchor.radiusRange < euclideanDistanceErrorless(anchor, sensor)) {
                throw new Exception("Out of range");
            }
        }
        return closestAnchors;
    }

    public static Vector3D getVector(Sensor sensor) {
        return new Vector3D(sensor.position.x,
                sensor.position.y,
                sensor.position.z
        );
    }

    private static Position trilateration3D(Sensor anchorSensor1, Sensor anchorSensor2,
                                            Sensor anchorSensor3, Sensor anchorSensor4,
                                            double r1, double r2, double r3, double r4) {
        Vector3D vectorSensor1 = getVector(anchorSensor1);
        Vector3D vectorSensor2 = getVector(anchorSensor2);
        Vector3D vectorSensor3 = getVector(anchorSensor3);
        Vector3D vectorSensor4 = getVector(anchorSensor4);

        Vector3D vec2min1 = vectorSensor2.subtract(vectorSensor1);
        double norm2min1 = vec2min1.getNorm();

        Vector3D e_x = new Vector3D(vec2min1.getX() / norm2min1, vec2min1.getY() / norm2min1, vec2min1.getZ() / norm2min1);

        double i = e_x.dotProduct(vectorSensor3.subtract(vectorSensor2));
        Vector3D mide_y = vectorSensor3.subtract(vectorSensor1).subtract(e_x.scalarMultiply(i));
        Vector3D e_y = new Vector3D(mide_y.getX() / mide_y.getNorm(),
                mide_y.getY() / mide_y.getNorm(),
                mide_y.getZ() / mide_y.getNorm());

        Vector3D e_z = e_x.crossProduct(e_y);

        double d = vec2min1.getNorm();

        double j = e_y.dotProduct(vectorSensor3.subtract(vectorSensor1));
        double x = ((r1 * r1) - (r2 * r2) + (d * d)) / (2 * d);
        double y = (((r1 * r1) - (r3 * r3) + (i * i) + (j * j)) / (2 * j)) - ((i / j) * (x));

        double z1 = Math.pow(Math.abs(r1 * r1 - x * x - y * y),1/2);
        double z2 = z1 * (-1);


        Vector3D ans1 = vectorSensor1.add(e_x.scalarMultiply(x)).add(e_y.scalarMultiply(y)).add(e_z.scalarMultiply(z1));
        Vector3D ans2 = vectorSensor1.add(e_x.scalarMultiply(x)).add(e_y.scalarMultiply(y)).add(e_z.scalarMultiply(z2));

        double dist1 = vectorSensor4.subtract(ans1).getNorm();
        double dist2 = vectorSensor4.subtract(ans2).getNorm();

        if (Math.abs(r4 - dist1) < Math.abs(r4 - dist2))
            return new Position(ans1.getX(), ans1.getY(), ans1.getZ());
        else
            return new Position(ans2.getX(), ans2.getY(), ans2.getZ());
    }

    public static double euclideanDistanceErrorless(Sensor anchorSensor, Sensor sensor) {
        double x1 = anchorSensor.position.x;
        double x2 = sensor.position.x;
        double y1 = anchorSensor.position.y;
        double y2 = sensor.position.y;
        double z1 = anchorSensor.position.z;
        double z2 = sensor.position.z;

        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }

}