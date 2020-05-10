package com.triage.logic;

import com.triage.rest.models.users.Exam;
import com.triage.utils.NLPUtils;
import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.joda.time.LocalDate;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class OCRChart {
    static {
        System.setProperty("java.awt.headless", "true");
        System.out.println(java.awt.GraphicsEnvironment.isHeadless());
        Toolkit tk = Toolkit.getDefaultToolkit();
        tk.beep();
    }
    private ArrayList<Exam> exams=null;
    private static final long serialVersionUID = 1L;
    public OCRChart( int chatid,ArrayList<Exam> exams) {
       this.exams=exams;
       //System.out.println("Versioneeee" + System.getProperty("os.name"));
       Path path = Paths.get("C:/Users/frank/TriageBotRestServer-data/OCRCharts");
       if(!System.getProperty("os.name").equals("Windows 8.1"))
        path = Paths.get("/home/baccaro/TriageBotRestServer-data/OCRCharts");
       // Create dataset
       XYDataset dataset = createDataset(exams);
       // Create chart
       JFreeChart chart = ChartFactory.createTimeSeriesChart(
                exams.get(0).getName(), // Chart
                "", // X-Axis Label
                exams.get(0).getUnit() , // Y-Axis Label
                dataset);

       XYPlot plot = (XYPlot)chart.getPlot();
       customizeplot(plot);
       saveChartAsPNG(path,chatid,chart);
      }
      private void saveChartAsPNG(Path path, int chatid, JFreeChart chart){
          try {
              ChartUtilities.saveChartAsPNG(new File(path+"/chart_"+chatid+".png"), chart, 450, 400);
          } catch (IOException e) {
              e.printStackTrace();
          }
      }

      private void customizeplot(XYPlot plot){
          //imposto il colore della curva
          XYLineAndShapeRenderer renderer= new XYLineAndShapeRenderer();
          renderer.setSeriesStroke(0, new BasicStroke( 2.5f ));
          plot.setRenderer(0, renderer);
          plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.blue);

          plot.setBackgroundPaint(Color.white);
          plot.setDomainGridlinePaint(Color.black);
          //  plot.setRangeGridlinePaint(Color.black);
          //imposto il range sull'asse Y usando il valore minimo e massimo dei risultati
          NumberAxis range = (NumberAxis) plot.getRangeAxis();
          //prendo il range del primo elemento nella lista perchè il range è uguale per tutti.
          // E' il risultato associato ad ogni esame che varia.
          double max= exams.get(0).getMax();
          double min= exams.get(0).getMin();
          for(Exam x: exams){
              if (x.getResult()>max){
                  max= x.getResult();
              }
              if (x.getResult()<min){
                  min= x.getResult();
              }
          }
          //diminuisco del 45% il valore del minimo cosi che la linea verde possa essere visualizzabile
          if( min==exams.get(0).getMin())
              min = min - (min*10)/100;
          else if(min<exams.get(0).getMin())
              min = min - (min*5)/100;
          else if(min==0)
              min = min - 5;
          //incremento del 45% il valore del massimo cosi che la linea rossa possa essere visualizzabile
          if( max==exams.get(0).getMax())
              max = max + (max*10)/100;
          else
              max = max + (max*5)/100;
          //imposto il nuovo range sull'asse Y
          range.setRange(min, max);

          //imposto le rette che evidenziano il minimo e il massimo
          ValueMarker marker_min = new ValueMarker(exams.get(0).getMin(),Color.green,new BasicStroke( 2.0f ));
          ValueMarker marker_max= new ValueMarker(exams.get(0).getMax(),Color.red,new BasicStroke( 2.0f ));
          plot.addRangeMarker(marker_min);
          plot.addRangeMarker(marker_max);

          //creo la legenda personalizzata
          LegendItemCollection chartLegend = new LegendItemCollection();
          Shape shape = new Rectangle(10, 10);

          chartLegend.add(new LegendItem("Andamento", null, null, null, shape, Color.blue));
          chartLegend.add(new LegendItem("Soglia minima", null, null, null, shape, Color.green));
          chartLegend.add(new LegendItem("Soglia massima", null, null, null, shape, Color.red));
          plot.setFixedLegendItems(chartLegend);

          //imposto il formato della data sull'asse X, in italiano
          DateAxis axis = (DateAxis) plot.getDomainAxis();
          axis.setDateFormatOverride(new SimpleDateFormat("MMM-yy", new Locale("it", "IT")));
      }

    private XYDataset createDataset(ArrayList<Exam> exams) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries series = new TimeSeries("Andamento");

        for (Exam exam: exams){
            LocalDate dt = new LocalDate(exam.getDate());
           series.addOrUpdate(new Day(dt.getDayOfMonth(),dt.getMonthOfYear(),dt.getYear()), exam.getResult());
        }
        dataset.addSeries(series);
        return dataset;
    }
}
