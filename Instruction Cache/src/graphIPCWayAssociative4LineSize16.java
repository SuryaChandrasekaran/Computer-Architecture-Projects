import java.nio.file.Files;
import java.nio.file.Paths;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class graphIPCWayAssociative4LineSize16 extends ApplicationFrame {

   public graphIPCWayAssociative4LineSize16( String applicationTitle , String chartTitle ) {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
         chartTitle,
         //"Years","Number of Schools",
         "Block Size","IPC",
         createDataset(),
         PlotOrientation.VERTICAL,
         true,true,false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }

   private DefaultCategoryDataset createDataset( ) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      dataset.addValue( 0.631 , "graph" , "1024" );
      dataset.addValue( 0.631 , "graph" , "2048" );
      dataset.addValue( 0.631 , "graph" ,  "4096" );
      dataset.addValue( 0.631 , "graph" , "8192" );
      dataset.addValue( 0.631 , "graph" , "16384" );
      dataset.addValue( 0.631 , "graph" , "32768" );
      return dataset;
   }

   public static void main( String[ ] args ) {
	   graphIPCWayAssociative4LineSize16 chart = new graphIPCWayAssociative4LineSize16(
         "IPC vs Block Size" ,
         "IPC vs Block Size");

      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
   }
}


