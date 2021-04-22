import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.util.List;

public class LineChart_AWT extends ApplicationFrame {

    public LineChart_AWT(String applicationTitle, String chartTitle, DefaultCategoryDataset dataset,
                         String categoryAxis, String valueAxis) {
        super(applicationTitle);
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Radius", "ALE",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private static DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(15, "schools", "1970");
        dataset.addValue(30, "schools", "1980");
        dataset.addValue(60, "schools", "1990");
        dataset.addValue(120, "schools", "2000");
        dataset.addValue(240, "schools", "2010");
        dataset.addValue(300, "schools", "2014");
        return dataset;
    }

    public static void createALEChart(List<Series> ALESeries, String appTitle, String categoryAxis,
                                      String valueAxis) {
        String[] titles = {"Non-Iterative", "Iterative-Distance", "Iterative-Hops"};
        String[] algorithms = {"NA", "IA1", "IA2"};

        for (int i = 0; i < titles.length; i++) {
            LineChart_AWT chart = new LineChart_AWT(
                    appTitle,
                    titles[i],
                    createALEData(ALESeries, algorithms[i]),
                    categoryAxis, valueAxis
            );
            chart.pack();
            RefineryUtilities.centerFrameOnScreen(chart);
            chart.setVisible(true);
        }
    }

    public static void createFractionChart(List<Series> ALESeries, String appTitle, String categoryAxis,
                                           String valueAxis) {
        String[] titles = {"Non-Iterative", "Iterative-Distance", "Iterative-Hops"};
        String[] algorithms = {"NA", "IA1", "IA2"};

        for (int i = 0; i < titles.length; i++) {
            LineChart_AWT chart = new LineChart_AWT(
                    appTitle,
                    titles[i],
                    createALEData(ALESeries, algorithms[i]),
                    categoryAxis, valueAxis
            );
            chart.pack();
            RefineryUtilities.centerFrameOnScreen(chart);
            chart.setVisible(true);
        }
    }

    private static DefaultCategoryDataset createALEData(List<Series> series, String algorithm) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue((int) series.get(0).getByString(algorithm), "25% err","50");
        dataset.addValue((int) series.get(1).getByString(algorithm), "35% err","50");
        dataset.addValue((int) series.get(2).getByString(algorithm), "45% err","50");

        dataset.addValue((int) series.get(3).getByString(algorithm), "25% err","65");
        dataset.addValue((int) series.get(4).getByString(algorithm), "35% err","65");
        dataset.addValue((int) series.get(5).getByString(algorithm), "45% err","65");

        dataset.addValue((int) series.get(6).getByString(algorithm), "25% err","80");
        dataset.addValue((int) series.get(7).getByString(algorithm), "35% err","80");
        dataset.addValue((int) series.get(8).getByString(algorithm), "45% err","80");

        return dataset;
    }

}

