// 3D graph manager:
//	Data collecting
//	3D graph plotting

// created:       
// last modified: 24.03.2011

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.text.*;
import javax.media.j3d.*;
//#############################################################################
class Graph_3D {
    /*@ specification Graph_3D {
    double x;
    double y;
    double z;
    double level;
    double tau;

    double  X_steps, Y_steps;
    double dyn_stat_steps;

// MH310112
    double  step_nr;
    double  gr_red_rate;

    String X_axis_name;
    String Y_axis_name;
    String Z_axis_name;

    double data_ready;  
    void  paintAll;
    void done;

// State variables
    	double initstate_c, oldstate_c, state_c,
		nextstate_c, finalstate_c; 
	alias (double) initstate	= (initstate_c);
	alias (double) oldstate    	= (oldstate_c);
	alias (double) state 	= (state_c);
	alias (double) nextstate	= (nextstate_c);
	alias (double) finalstate	= (finalstate_c);
// Y_State variables
      	double 	Y_initstate_c, Y_oldstate_c, Y_state_c; 
      	double	Y_nextstate_c, Y_finalstate_c; 
	alias (double) Y_initstate  = (Y_initstate_c);
	alias (double) Y_oldstate   = (Y_oldstate_c);
	alias (double) Y_state      = (Y_state_c);
	alias (double) Y_nextstate  = (Y_nextstate_c);
	alias (double) Y_finalstate = (Y_finalstate_c);
// Collecting outputs
	alias (double) result = ( nextstate_c, Y_nextstate_c);

// Methods specifications
// MH310112
//	state_c, Y_state_c, level, x, y, z,  tau, dyn_stat_steps,
	state_c, Y_state_c, level, x, y, z,  tau, dyn_stat_steps, step_nr 
				-> result {addPoint};

	finalstate_c  -> data_ready {printing_data};

	paintAll, X_steps, Y_steps, 
	X_axis_name, Y_axis_name, Z_axis_name  
					-> done{draw};
//  Default initial values
	initstate_c = 0;
   	Y_initstate_c = 0;
//  Default axis names 
	X_axis_name = "X_axis";
	Y_axis_name = "Y_axis";
	Z_axis_name = "Z_axis";
    }@*/

//¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤
//    private static Map<Double, Set<Double>> data = 
//		new LinkedHashMap<Double, Set<Double>>();

//  MH 051010
//    private static Map<Double, LinkedList<Double>> data = 
    private Map<Double, LinkedList<Double>> data = 
		new LinkedHashMap<Double, LinkedList<Double>>();
//
//¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤
//  MH 051010
//    private static double prev_state_c, prev_lev, prev_x;
    private  double prev_state_c, prev_lev, prev_x;

// MH310112
    private int currentReducedStep = 1;
//
//¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤

//==================================================
//	state_c, Y_state_c, level, x, y, z,  tau, dyn_stat_steps 
//				-> result {addPoint};
//		alias (double) result = ( nextstate_c, Y_nextstate_c);
//==================================================
    public double[] addPoint ( double state_c, double Y_state_c, 
				  double lev,
				  double xx,  double yy, double zz,
				  double tau, double dyn_stat_steps
// MH310112
	, double  step_nr
							) {
       double[] result = new double[2];
       double x, y, z;
       double element;
       double YAxisFlag = 1234e27;
	DecimalFormat dF = new DecimalFormat("0.###E0");
	result[0] = state_c;	
	result[1] = Y_state_c;

	x = xx;
 	if ( lev == 1 ) { y = zz; }
	    else { y = yy; }

//System.out.println("addPoint:  state="+state_c+"  lev = "+lev+
//				"    x = "+x+"   y = "+y );
// Static calculations before dynamic calculations

//  MH280911 modified
//System.out.println("addPoint2: lev = "+lev+"  x = "+x+"  y = "+y+"  tau = "+tau );
//	if ( ( tau == 0 ) && ( dyn_stat_steps != 0 ) ) return result;
	if ( ( lev == 1 ) && ( tau == 0 ) && ( dyn_stat_steps != 0 ) ) return result;
//System.out.println("addPoint2");
//

//System.out.println("addPoint2:  lev = "+lev+"    x = "+x+"   y = "+y );

//****************************************lev=1********************************
	if ( lev == 1 ) {
// MH310112
	   if ( step_nr > 1 ) {
		if ( currentReducedStep++ < gr_red_rate ) return result;
			else currentReducedStep = 1;
	   }
// MH310112
	      result[0]++;
	      if ( data.containsKey( x ) ) {
//System.out.println ( "datacontainskey"+x);
	         if ( state_c == prev_state_c & lev == prev_lev & x==prev_x) {
		     element = data.get(x).removeLast();
//System.out.println ( "vaja kustutada"+x);
//System.out.println ( "st="+state_c+" lev="+ lev+" x="+x+
//					"  removed y="+element);
//System.out.println ( "st="+state_c+" lev="+ lev+" x="+x+"  removed last");
	         }
	         data.get( x ).add( y );
//System.out.println ( "addPoint:  state="+state_c+"   lev="+ lev+
//	"    x="+dF.format(x)+"   y="+dF.format(y)+ "  added");

	      } 
	      else {
	         LinkedList<Double> yAxis = new LinkedList<Double>();
	         data.put( x, yAxis);
	         data.get( x ).add( y );
//System.out.println ( "addPoint:  state="+state_c+"   lev="+ lev+
//"  x="+dF.format(x)+"  y="+dF.format(y)+ " added new x and y into x");
	      }
	      prev_state_c = state_c;	
	      prev_lev = lev; 
	      prev_x = x; 
	   }

//**********************************************lev=2**************************
	   if ( lev == 2 ) {
// MH310112
	      currentReducedStep = 1;
// MH310112
	      result[1]++;
	      if ( data.containsKey( YAxisFlag ) ) {
	         if ( state_c == prev_state_c & lev == prev_lev & 
			prev_x ==YAxisFlag) {
		     element = data.get(YAxisFlag).removeLast();
//System.out.println("st="+state_c+" lev="+ lev+" x="+x+
//				" removed y="+element);
	         }
	         data.get( YAxisFlag ).add( y );
//System.out.println ( "addPoint:  state="+state_c+"   lev="+ lev+
//"  x="+dF.format(x)+" y="+dF.format(y)+ " added y into YAxis");
//System.out.println();
	      } 
	      else {
	         LinkedList<Double> yAxis = new LinkedList<Double>();
	         data.put( YAxisFlag, yAxis);
	         data.get( YAxisFlag ).add( y );
//System.out.println ( "addPoint:  state="+state_c+"   lev="+ lev+
//			"  x="+dF.format(x)+" y="+dF.format(y)+ 
//			" added new YAxis and y into YAxis");
//System.out.println();
	      }
	      prev_state_c = state_c;	
	      prev_lev = lev; 
	      prev_x = YAxisFlag;
	   }
//System.out.println("addPoint2:  result[0] = "+result[0]+
//				"    result[1] = "+result[1] );
 	return result;
    }

//==================================================
//	    finalstate_c -> data_ready {closing};
//==================================================
    public double printing_data ( double finalstate_c ) {
	double result;
	    result = finalstate_c;
 	    return result;
    }

//==================================================
//	    finalstate_c -> data_ready {closing};
//==================================================
    public Object ret_data ( double finalstate_c ) {
//	if (mode == 0){
	  System.out.println("data.size = " + data.size());
//	  System.out.println(data.toString());
//	}
	return data;
    }

//==================================================
//	paintAll, X_axis_name, Y_axis_name, Z_axis_name  
//					-> done{draw};
//============3DPlot.draw: =============================
    public void draw ( double  X_steps, double Y_steps, 
			 String  X_axis_name , 
			 String  Y_axis_name, 
			 String  Z_axis_name
//   MH 100111 changing background brightness       
//		, double background
  				  			) {
	if ((X_steps <= 1) || (Y_steps <= 1)) {
	   System.out.println(
			"\n ******** Can not display 3D Plot ********\n"); 
	   return;
	}

       JFrame frame = new JFrame( "Hydro3Dplot" );
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
//   MH 100111 changing background brightness       
//    SurfacePlot surf = new SurfacePlot(background);
       SurfacePlot surf = new SurfacePlot();
        surf.setData( new Hydro3DBinned2DData( (Map<Double, LinkedList<Double>>)data ));

        frame.add(surf,BorderLayout.CENTER);
        frame.setSize( 1000, 1000 );
        frame.setVisible( true );
        surf.setXAxisLabel ( X_axis_name );
        surf.setYAxisLabel ( Y_axis_name );
        surf.setZAxisLabel ( Z_axis_name ); 
System.out.println("3DPlot.draw:  X_Axis:  " + surf.getXAxisLabel());
System.out.println("3DPlot.draw:  Y_Axis:  " + surf.getYAxisLabel());
System.out.println("3DPlot.draw:  Z_Axis:  " + surf.getZAxisLabel());
//	surf.setLogZscaling(true);
// System.out.println("LogZscaling = " + surf.getLogZscaling()); 
    }
}

//==================================================
//	 class Hydro3DBinned2DData implements Binned2DData
//==================================================
    class Hydro3DBinned2DData implements Binned2DData {

       private int    	  xBins;
       private int    	  yBins = -1;
       private  Rainbow  rainbow = new Rainbow();
       private float[][]  data;
 
       private double	  xMin = 1e20;
       private double	  xMax = -1e20;
       private double	  yMin = 1e20;
       private double	  yMax = -1e20;
       private double	  zMin = 1e20;
       private double	  zMax = -1e20;

//============================================================
//  public Hydro3DBinned2DData( Map<Double, LinkedList<Double>> values )
//============================================================
    public Hydro3DBinned2DData( Map<Double, LinkedList<Double>> values ) {

       int i, istep;
       int j, jstep;
       int k;
       int MinInd = 0;
       int MaxInd = 0;
       double first, last;
       double YAxisFlag = 1234e27;

        DecimalFormat dF = new DecimalFormat("0.0000");

        xBins = values.size()-1;
        data = new float[ xBins ][];

        LinkedList<Double> yind = values.get( YAxisFlag );
          first = yind.getFirst();
          last  = yind.getLast();
 System.out.println( "First_Y: " + first + ", Last_Y: " + last );
          if ( first < last ) { i=0; istep=1; yMin=first; yMax=last;}
 	  else {i=xBins-1; istep=-1; yMin=last; yMax=first;}
        k = 0;
        for ( Double x : values.keySet() ) {
          if ( x != YAxisFlag) {
	  k++;
	  if ( x < xMin ) { xMin = x; MinInd = k; }
	  if ( x > xMax ) { xMax = x; MaxInd = k; }
	}
        }
 System.out.println("Min_X Ind: "+MinInd+", Max_X_Ind: "+MaxInd+", k: "+k);

        j = 0;
        for ( Double x : values.keySet() ) {
          if ( x != YAxisFlag) {
	  LinkedList<Double> ys = values.get( x );
            if( yBins == -1 ) {
                yBins = ys.size();
            }
//	  if ( MaxInd < MinInd ) Collections.reverse(ys);
	  if ( MaxInd > MinInd ) Collections.reverse(ys);

            data[i] = new float[yBins];
//System.err.println( "x: " + i + ", y len: " + ys.size() );
            for ( Double y : ys ) {
//System.out.println ( "i="+i+"  j="+j);
                data[ i ][ j ] = y.floatValue();
	      if ( data[i][j] > zMax ) zMax = data[i][j];
	      if ( data[i][j] < zMin ) zMin = data[i][j];
//System.err.println( "y: " + j );
                j++;
            }
	  i = i + istep;
	  j = 0;
	}
        }
        if (xMin == xMax ) xMax++;
        if (yMin == yMax ) yMax++;
        if (zMin == zMax ) zMax++;

System.out.println
  ("3DPlot.draw: xMin="+dF.format(xMin)+", xMax="+dF.format(xMax)+
   ", xMin_Ind="+MinInd+", xMax_Ind="+MaxInd);
System.out.println
  ("3DPlot.draw: yMin="+dF.format(yMin)+", yMax="+dF.format(yMax)+
   ", yFirst="+dF.format(first)+", yLast="+dF.format(last));
System.out.println
  ("3DPlot.draw: zMin="+dF.format(zMin)+
   ", zMax="+dF.format(zMax ));
    }
//============================================================
    public int xBins()  { return xBins; }
//============================================================
    public int yBins()  { return yBins; }
//============================================================
    public float xMin() { return (float)xMin; }
//============================================================
    public float xMax() { return (float)xMax; }
//============================================================
    public float yMin() { return (float)yMin; }
//============================================================
    public float yMax() { return (float)yMax; }
//============================================================
    public float zMin() { return (float)zMin; }
//============================================================
    public float zMax() { return (float)zMax; }
//============================================================
    public float zAt( int xIndex, int yIndex ) {
// MH 04012011: Normalizing z
	float z, zn;
	z = data[ xIndex ][ yIndex ];
	zn = (z-zMin())/(zMax()-zMin());
// System.err.println( "xIndex: "+xIndex+", yIndex: "+yIndex+", 
//			  z: "+data[xIndex][yIndex]);
//     return data[ xIndex ][ yIndex ];
       return zn;
    }
//============================================================
    public javax.vecmath.Color3b colorAt(int xIndex, int yIndex){
        return rainbow.colorFor( zAt( xIndex, yIndex ) );
    }
  }
