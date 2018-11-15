import java.nio.file.Files;
import java.nio.file.Paths;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class graphHitWayAssociative4LineSize2 extends ApplicationFrame {

   public graphHitWayAssociative4LineSize2( String applicationTitle , String chartTitle ) {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
         chartTitle,
         //"Years","Number of Schools",
         "Cache Size","Hit Ratio",
         createDataset(),
         PlotOrientation.VERTICAL,
         true,true,false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }

   private DefaultCategoryDataset createDataset( ) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      dataset.addValue( 95.83 , "graph" , "1024" );
      dataset.addValue( 95.83 , "graph" , "2048" );
      dataset.addValue( 95.83 , "graph" ,  "4096" );
      dataset.addValue( 95.83 , "graph" , "8192" );
      dataset.addValue( 95.83 , "graph" , "16384" );
      dataset.addValue( 95.83 , "graph" , "32768" );
      return dataset;
   }

   public static void main( String[ ] args ) {
	   graphHitWayAssociative4LineSize2 chart = new graphHitWayAssociative4LineSize2(
         "Hit Ratio vs Cache Size in Bytes" ,
         "Hit Ratio vs Cache Size in Bytes");

      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
   }
}




