package Windows;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 * Created by arthu on 19/06/2017.
 */
public class Plot extends ApplicationFrame implements Runnable{

    private static long totalTime = 0;
    private static int numTraces = 0;

    final static XYSeries averageSeries = new XYSeries("Average Tracing Time (ns)");
    final static XYSeries realSeries = new XYSeries("Current Tracing Time (ns)");



    public Plot(String title) {
        super(title);

        averageSeries.setMaximumItemCount(100);
        realSeries.setMaximumItemCount(100);

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(averageSeries);
        dataset.addSeries(realSeries);

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Tracing Time",
                "Number of Traces",
                "Time (ns)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
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
    }

    @Override
    public void run() {

    }
}
