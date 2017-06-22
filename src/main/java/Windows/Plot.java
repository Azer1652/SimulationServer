package Windows;

import SimServer.SimServer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by arthu on 19/06/2017.
 */
public class Plot extends ApplicationFrame implements Runnable{

    private static long totalTime = 0;
    private static int numTraces = 0;

    final static XYSeries averageSeries = new XYSeries("Average Tracing Time (ns)");
    final static XYSeries realSeries = new XYSeries("Current Tracing Time (ns)");

    final static XYSeries numRobotsSeries = new XYSeries("Number of Robots)");

    final static XYSeriesCollection numRobotsDataset = new XYSeriesCollection();

    public Plot(String title) {
        super(title);

        averageSeries.setMaximumItemCount(100);
        realSeries.setMaximumItemCount(100);

        numRobotsSeries.setMaximumItemCount(100);

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(averageSeries);
        dataset.addSeries(realSeries);

        JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart, true, true, true, false, true);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private JFreeChart createChart(XYSeriesCollection dataset){
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Tracing Time",
                "Number of Traces",
                "Time (ns)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        rangeAxis1.setLowerMargin(0.40);

        numRobotsDataset.addSeries(numRobotsSeries);

        NumberAxis rangeAxis2 = new NumberAxis("Number of robots");
        rangeAxis2.setUpperMargin(1.00);  // to leave room for price line
        plot.setRangeAxis(1, rangeAxis2);
        plot.setDataset(1, numRobotsDataset);
        plot.setRangeAxis(1, rangeAxis2);
        plot.mapDatasetToRangeAxis(1, 1);
        XYBarRenderer renderer2 = new XYBarRenderer(0.20);
        plot.setRenderer(1, renderer2);
        ChartUtilities.applyCurrentTheme(chart);
        renderer2.setBarPainter(new StandardXYBarPainter());
        renderer2.setShadowVisible(false);
        return chart;
    }

    public static void update(long time){
        totalTime += time;
        numTraces++;
        synchronized (averageSeries){
            averageSeries.add(numTraces, totalTime/numTraces);
        }
        synchronized (realSeries){
            realSeries.add(numTraces, time);
        }

        numRobotsSeries.add(numTraces, SimServer.numRobots);
    }

    @Override
    public void run() {

    }
}
