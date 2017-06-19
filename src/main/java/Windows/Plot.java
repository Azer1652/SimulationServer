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

    final static XYSeries averageSeries = new XYSeries("Average Tracing Time (ms)");
    final static XYSeries realSeries = new XYSeries("Current Tracing Time (ms)");



    public Plot(String title) {
        super(title);

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(averageSeries);
        dataset.addSeries(realSeries);

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Tracing Time",
                "Number of Traces",
                "Time (ms)",
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
        averageSeries.add(numTraces, totalTime/numTraces);
        realSeries.add(numTraces, time);
        if(averageSeries.getItemCount() > 100){
            averageSeries.remove(0);
            realSeries.remove(0);
        }
    }

    @Override
    public void run() {

    }
}
