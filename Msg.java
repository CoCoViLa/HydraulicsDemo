// MultiSeriesGraph

// created:       
// last modified: 25.03.2011

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.lang.Number;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;

//#############################################################################
class Msg {
    /*@ specification Msg {
	double level;
        double x;
        alias (double) ys;
        void init_ready, drawing_ready, paintAll, done, updated;
        boolean repaintImmediately, axisAlwaysIncludeZero, showSeparateAxis;
        boolean autoSort, allowDuplicates;
        float lineThickness;
        boolean xValuesAscend;
        //NB! seriesNames if defined must match ys
        String[] seriesNames;
        String domainName;
        //----default settings
        repaintImmediately = false;
        axisAlwaysIncludeZero = false;
        showSeparateAxis = true;
        lineThickness = 2;   
        autoSort = false; 
        allowDuplicates = true;
	 xValuesAscend = true;
        domainName = "";

     	double  step_nr;
      	double  gr_red_rate;
      	double xPrev;
	double x1;

boolean delAllGraphs;
//delAllGraphs = true;
//delAllGraphs = false;

        //change settings
        ys.length, lineThickness, axisAlwaysIncludeZero, 
            showSeparateAxis, autoSort, 
                allowDuplicates, repaintImmediately
, delAllGraphs, cocovilaSpecObjectName
				-> init_ready {init};
        //setup axis labels
        init_ready, domainName, seriesNames, showSeparateAxis 
				-> updated {setSeriesName};
        //add coordinates
        init_ready, x, ys, level, step_nr, repaintImmediately 
				-> drawing_ready, (Exception){draw};
        //paint all at once
        paintAll, repaintImmediately -> done {drawAll};
    }@*/

//#############################################################################
//  MH090212
    private static Set<ChartFrame> instances = new HashSet<ChartFrame>();
//
    private JFreeChart chart;
    private ChartFrame frame;
    private XYPlot plot;
    private List<XYSeries> series = new ArrayList<XYSeries>();
    Paint color;
	Paint[] colors = new Paint[] {Color.red, Color.blue, Color.black,
			new Color (0, 140, 0), new Color(200, 0, 200),
			new Color (0,140,230), Color.cyan, Color.magenta,
			Color.green, Color.orange, Color.yellow};

//#############################################################################
    private void initChart(int length, boolean showSeparateAxis
, boolean delAllGraphs, String objectName
		) {
    	int width = 600;
        chart = ChartFactory.createXYLineChart( null, null, null,
                null, PlotOrientation.VERTICAL, 
                true, //legend
                true, //tooltip
                false );//url

// MH270911   very light Gray
//	chart.setBackgroundPaint(new Color (230, 230, 230));
	chart.setBackgroundPaint(Color.white);  
//
        plot = chart.getXYPlot();

// MH270911
	plot.setBackgroundPaint(Color.white); 
	plot.setDomainGridlinePaint(Color.lightGray);
	plot.setRangeGridlinePaint(Color.lightGray);
//

//        frame = new ChartFrame( "Graph", chart ); 
        frame = new ChartFrame( objectName, chart ); 

        if (showSeparateAxis) { width += 75*length; }
        frame.setPreferredSize(new java.awt.Dimension(width, 480));

//  MH090212
if (delAllGraphs) 
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                System.out.println("Chart frame closed - terminating program");
		     for(Iterator<ChartFrame> it = instances.iterator(); it.hasNext();) {
			it.next().dispose();
			it.remove();
		     }
//                ee.ioc.cs.vsle.api.ProgramContext.terminate();
            }
        } );
else 
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                System.out.println("Chart frame closed - terminating program");
                frame.dispose();
//                ee.ioc.cs.vsle.api.ProgramContext.terminate();
            }
        } );
if (delAllGraphs) 		instances.add(frame);
//  MH090212
    }

//#############################################################################
    public void init( int length, float lineThickness,
            	boolean axisAlwaysIncludeZero, boolean showSeparateAxis, 
            	boolean autoSort, boolean allowDuplicates,
		boolean repaintImmediately
, boolean del_flag, String objectName
		) {
      
        initChart(length,showSeparateAxis
, del_flag, objectName
			);
        
        if (showSeparateAxis) {
            for (int i = 0; i < length; i++) {
                XYSeriesCollection dataset = new XYSeriesCollection();
                XYSeries ser = new XYSeries("" + 1 + (i+1), autoSort, allowDuplicates);
                dataset.addSeries(ser);
                series.add( ser );
                plot.setDataset(i, dataset);
            }
        } else {
            XYSeriesCollection dataset = new XYSeriesCollection();
            for (int i = 0; i < length; i++) {
                XYSeries ser = new XYSeries("" + 1+ (i+1), autoSort, allowDuplicates);
                dataset.addSeries(ser);
                series.add( ser );
            }
            plot.setDataset(0, dataset);
        }

        Stroke stroke = new BasicStroke( lineThickness );
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRangeIncludesZero( axisAlwaysIncludeZero );
          DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance();
          nf.applyPattern("#0.####E0");
        if ( showSeparateAxis && length > 1 ) {
            chart.removeLegend();
            for ( int i = 0, len = series.size(); i < len; i++ ) {
                XYSeries ser  = series.get( i );
                if (i >= colors.length) {
                	color = plot.getDrawingSupplier().getNextPaint();
                }
                 else color = colors[i];
                NumberAxis axis = new NumberAxis( ser.getKey().toString() );
                axis.setAutoRangeIncludesZero( axisAlwaysIncludeZero );
                if (length > 2)  {axis.setNumberFormatOverride(nf);}

                axis.setLabelPaint( color );
                axis.setTickLabelPaint( color );
                if (i < length / 2) {
                    plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
    			}                               
                plot.setRangeAxis( i, axis );
                plot.mapDatasetToRangeAxis( i, i);
                XYLineAndShapeRenderer rend = 
					new XYLineAndShapeRenderer(true, false);
                rend.setSeriesStroke( 0, stroke );
                rend.setSeriesPaint( 0, color );
                plot.setRenderer( i, rend );
            }
        } else {
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setAutoRangeIncludesZero( axisAlwaysIncludeZero );
//              rangeAxis.setNumberFormatOverride(nf);
            plot.getRenderer().setStroke( stroke );
            for ( int i = 0, len = series.size(); i < len; i++ ) {
                if (i >= colors.length) {
                	color = plot.getDrawingSupplier().getNextPaint();
                }
                 else color = colors[i];
            	plot.getRenderer().setSeriesPaint( i, color );
            }
        }        
        frame.pack();
	if (repaintImmediately) frame.setVisible( true );
    }

//#############################################################################
    private int mitmes = 0;
    private int currentReducedStep = 1;
//#############################################################################
    public void draw(  final double x, final double[] ys, double level, double step_nr,
            final boolean repaintImmediately ) throws Exception {

      int cnt;
// 	if ( step_nr <10 ) System.out.println("step_nr = "+step_nr+"  x = "+x);
	if ( step_nr == 0 ) return ;
// MH 010212
	if ( step_nr == 1 ) x1 = x; ;
	if ( step_nr == 2 & x < x1 ) xValuesAscend = false;
	if ( step_nr > 1 ) {
	    if ( currentReducedStep++ < gr_red_rate ) return;
	     else currentReducedStep = 1;
	}
// MH 010212

//System.out.println("MultiSeriesGraph_draw: x = "+x+
//" level = "+level+"  ys.length = "+ys.length);
//HydUtil.print_ar ("MultiSeriesGraph_draw:  ys", ys);
      if ( level !=1 ) return;
      for ( int i = 0; i < ys.length; i++ ) {
  //System.out.println("tsykli alguses "); 
  //System.out.println("1 i = "+i);
	 cnt = series.get( mitmes*ys.length+i ).getItemCount();
//System.out.println("2 i = "+i+ "  cnt = "+cnt);
	   if (cnt > 0)  {
//System.out.println("draw:  cnt = "+cnt+"    ys.length = "+ys.length );
//  System.out.println( "draw:  x_prev = "+series.get( i ).getX(cnt-1) + 
//  "  x = "+x);
//	if ((Double)x <  (Double)series.get( i ).getX(cnt-1)) {
//System.out.println("i = "+i+ "  mitmes = "+mitmes);
//System.out.println("draw:  x_prev = "+series.get(mitmes*ys.length+i).getX(cnt-1)+
//"  x = "+x+ "    mitmes = "+mitmes);

// MH 310112
	     xPrev = (Double)series.get(mitmes*ys.length+i).getX(cnt-1);
	     if ((xValuesAscend & (Double)x < xPrev)|| 
		  (!xValuesAscend & (Double)x > xPrev)) {
//Langev if ((Double)x > (Double)series.get(mitmes*ys.length+i).getX(cnt-1)) {
//Tõusev if ((Double)x < (Double)series.get(mitmes*ys.length+i).getX(cnt-1)) {
// MH 310112

//System.out.println("alusta algusest "); 
    	       if ( i == 0 ) {
                mitmes = mitmes+1;
//System.out.println("Uued serid i = "+i+" mitmes = "+mitmes+
//" mitmes*ys.length+i = "+(mitmes*ys.length+i));
                  for ( int k = 0; k < ys.length; k++ ) {
                    XYSeries ser=new XYSeries(""+(mitmes+1)+(k+1),autoSort,allowDuplicates);
        		if (showSeparateAxis) {
		  	  ((XYSeriesCollection) plot.getDataset(k)).addSeries(ser);
                        series.add( ser );
           		   plot.getRenderer(k).setStroke( new BasicStroke( lineThickness ) );
   			   plot.getRenderer(k).setSeriesPaint((mitmes),
						 (Color)plot.getRenderer(k).getSeriesPaint(0));
		   	} 
		    	else {
	    	       	   ((XYSeriesCollection) plot.getDataset(0)).addSeries(ser);
                	   series.add( ser );
   		       	   plot.getRenderer(0).setSeriesPaint((mitmes*ys.length+k),
						 (Color)plot.getRenderer(0).getSeriesPaint(k));
	  	    	}
    	            } 
	         }
	      }
      	   }
//  Miskiparast oli jargmine rida 2011 suvel aktiivseks muutunud
//(see rida põhjustab tagasijooksujoone valjajoonistamist):
//  	series.get( i ).add( x, ys[i], repaintImmediately );

//System.out.println("2 i = "+i+"  mitmes = "+mitmes+" mitmes*ys.length+i = "
//+((mitmes*ys.length)+i)+" x = "+x+" ys[i] = "+ys[i]);
          series.get((mitmes*ys.length)+i).add( x, ys[i], repaintImmediately );
//System.out.println("2 i tehtud ");
       }     
    }

//#############################################################################
    public void setSeriesName( String domain, String[] names, 
				   boolean showSeparateAxis ) {

        if(domain != null && domain.length() > 0) {
            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            domainAxis.setLabel( domain );
        }
        for (int i = 0; i < names.length; i++) {
            if(showSeparateAxis) {
                plot.getRangeAxis(i).setLabel(names[i]); 
            }
            series.get( i ).setKey(names[i]);   
        }
    }

//#############################################################################
    public void drawAll( boolean repaintImmediately ) {
        if ( !repaintImmediately ) {
	     if(!frame.isVisible()) frame.setVisible(true);
            for( XYSeries ser : series ){
//System.out.println("drawAll " + ser.getItems());
                ser.fireSeriesChanged();
            }
        }
    }

//#############################################################################
    /**
     * @param args
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception {
        /*MultiSeriesGraph g = new MultiSeriesGraph();
        String[] range = new String[] { "one", "two", "333" };
        boolean showSeparateAxis = false;
        g.init( range.length, 1, 4f, false, showSeparateAxis,false,true);
        g.setSeriesName( "domain", range, showSeparateAxis );
        g.draw( 1, new double[] { 0.2, 6, 60 }, true );
        g.draw( 2, new double[] { 0.3, 8, 40 }, true );
        g.draw( 3, new double[] { 0.6, 7, 12 }, true );*/
    }
}
