import java.awt.*;

public class Drawing extends Canvas {

    Sensor[] sensors;
    Sensor[] anchorSensors;

    public Drawing(Sensor[] sensors, Sensor[] anchorSensors) {
        this.sensors = sensors;
        this.anchorSensors = anchorSensors;
    }

    public void setSensors(Sensor[] sensors) {
        this.sensors = sensors;
        repaint();
    }


    @Override
    public void paint(Graphics g) {
        getGraphics().setColor(Color.BLACK);
        for (int i = 7; i < anchorSensors.length; i++) {
            getGraphics().fillOval((int) anchorSensors[i].position.x, (int) anchorSensors[i].position.y, 2, 2);
            getGraphics().drawOval((int) anchorSensors[i].position.x, (int) anchorSensors[i].position.y,
                    anchorSensors[i].radiusRange, anchorSensors[i].radiusRange);
        }
        for (Sensor sensor : sensors) {
            if (sensor.isAnchor) {
                getGraphics().setColor(Color.red);
            } else {
                getGraphics().setColor(Color.BLACK);
            }
            getGraphics().fillOval((int) sensor.position.x, (int) sensor.position.y, 1, 1);
            getGraphics().drawOval((int) sensor.position.x, (int) sensor.position.y, sensor.radiusRange, sensor.radiusRange);
        }
    }


    @Override
    public void repaint() {
        getGraphics().setColor(Color.BLACK);
        for (int i = 7; i < anchorSensors.length; i++) {

            getGraphics().fillOval((int) anchorSensors[i].position.x, (int) anchorSensors[i].position.y, 2, 2);
            getGraphics().drawOval((int) anchorSensors[i].position.x, (int) anchorSensors[i].position.y,
                    anchorSensors[i].radiusRange, anchorSensors[i].radiusRange);
        }
        for (Sensor sensor : sensors) {
            if (sensor.isAnchor) {
                getGraphics().setColor(Color.red);
            } else {
                getGraphics().setColor(Color.BLACK);
            }
            getGraphics().fillOval((int) sensor.position.x, (int) sensor.position.y, 2, 2);
            getGraphics().drawOval((int) sensor.position.x, (int) sensor.position.y, sensor.radiusRange, sensor.radiusRange);
        }
    }
}
