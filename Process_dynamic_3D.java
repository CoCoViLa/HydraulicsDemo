// Class name:    Process_dynamic 
//      
// Description:   process engine for 3D dynamics computations
//	          organizing iteration of the pressures 
//
// Variables:
//  	argp      - vector of all output pressures (from iteration procedure)
//	resp      - vector of all disconnected internal calculation pressures
//	len       - the length of the vector argp (and resp,error)
//	initp     - initial pressures vector  
//	respres   - resulting (after iteration) vector   
//	epsri     - allowed relative error
//	epsapi    - allowed absolute error for pressure  
//	errorap   - vector with the actual absolute errors for pressure
//	errorr    - vector with the actual relative errors   
//     noconverg - no convergence (true/false)      
//     maxcount  - max allowed number of iterations for the whole vector 
//     count     - number of iterations   
//	   
// created:       01.12.2007
// last modified: 06.03.2011
//---------------------------------------------------------------------------
//  ProgressBar 1-4 *********************************************************
import javax.swing.*;
import java.awt.event.*;
//***************************************************************************

import ee.ioc.cs.vsle.api.Subtask;
import java.text.*;

public class Process_dynamic_3D extends Fluid {
    /*@ specification Process_dynamic_3D {

      double  X_steps;
      double  Y_steps; 
      double  dyn_stat_steps;
      double  tau;	
      double  maxiter;
      double  adjust;	
      double  numofcomps;
      double  epsapi;	
      double  epsri;	
      double  timesteps; 
      double  step_nr;
      double  total_time;	
      double  printing_ready;
      double  steps_3D;
      double  level;
      double  Y_step; 
      double  lev;
      double  gr_red_rate;
      boolean delAllGraphs;

      String  fluid_type;
      double  fluid_temp, fluid_vol, fluid_ka;
      double  fluid_nue0, fluid_rho15, fluid_Af, fluid_Bf;

//  Simulation parameters
	alias (double) sim_params   = 
		( X_steps, Y_steps, level, tau, dyn_stat_steps,
		  maxiter, adjust, epsapi, epsri,
		  fluid_temp, fluid_vol, fluid_ka );

// Fluid parameters
   	alias (double) fluid_par = 
		( fluid_temp, fluid_vol, fluid_ka,
		  fluid_nue0, fluid_rho15, 
		  fluid_Af, fluid_Bf );

// State variables
	alias (double[])  initstate  = (*.initstate);
	alias (double[])  oldstate   = (*.oldstate);
	alias (double[])  state      = (*.state);
	alias (double[])  nextstate  = (*.nextstate);
	alias (double[])  finalstate = (*.finalstate);

// Y_State variables
	alias (double[])  Y_initstate  = (*.Y_initstate);
	alias (double[])  Y_oldstate   = (*.Y_oldstate);
	alias (double[])  Y_state      = (*.Y_state);
	alias (double[])  Y_nextstate  = (*.Y_nextstate);
	alias (double[])  Y_finalstate = (*.Y_finalstate);

// Iterable variables
	alias (double[])  respres = (*.respres);
	alias (double[])  initp   = (*.initp);
	alias (double[])  argp    = (*.argp);
	alias (double[])  resp    = (*.resp);

// Parameters values sharing
	alias (double) tau_all  = (*.tau);
	alias (double) steps    = (*.steps);
	alias (double) step_nrs = (*.step_nr);
	alias (double) dyn_stat_steps_all = (*.dyn_stat_steps);
	alias (double) X_steps_all    = (*.X_steps);
	alias (double) Y_steps_all    = (*.Y_steps);
	alias (double) Y_step_nrs = (*.Y_step_nr);
	alias (double) gr_red_rate_all = (*.gr_red_rate);
	alias (boolean) delAllGraphs_all = (*.delAllGraphs);

// Varia
	alias drawings = (*.drawing_ready);
	alias processEnded = (*.paintAll);
	alias (double[]) fluid_par_all = (*.fluid_par);
	alias (double) levels = (*.level);

// Collecting outputs
	alias  run_res=( finalstate, Y_finalstate, processEnded);
	alias (double[][]) bal_res = ( resp, respres);

//***************************************************************************
// Methods specifications
//***************************************************************************
// Calculating fluid parameters 
	fluid_type, fluid_temp -> fluid_nue0 {calc_nue0};
	fluid_type -> fluid_rho15 {calc_rho15};
	fluid_type, fluid_temp -> fluid_Af {calc_Af};
	fluid_type, fluid_temp -> fluid_Bf {calc_Bf}; 

// Sharing parameter values
	step_nr, tau, tau_all.length  -> tau_all { share_tau };
	timesteps, steps.length  -> steps { shareToComps };
	step_nr, step_nrs.length  -> step_nrs { shareToComps };
	fluid_par, fluid_par_all.length  
			-> fluid_par_all { share_fluid_par };
  	lev,  levels.length  -> levels { shareToComps };

	X_steps, Y_steps, level -> steps_3D { setSteps};

	dyn_stat_steps, dyn_stat_steps_all.length 
			-> dyn_stat_steps_all { shareToComps };
	X_steps, X_steps_all.length  -> X_steps_all { shareToComps };
	Y_steps, Y_steps_all.length  -> Y_steps_all { shareToComps };
	Y_step, Y_step_nrs.length  -> Y_step_nrs { shareToComps };

	gr_red_rate, gr_red_rate_all.length 
				-> gr_red_rate_all { shareToComps };
	delAllGraphs, delAllGraphs_all.length 
				-> delAllGraphs_all { shareBoolean };

// Dynamic process
//***************************************************************************

	[oldstate, state, Y_oldstate, Y_state, Y_step, step_nr, tau, lev    
		->  state, Y_nextstate],
	[oldstate, state, Y_oldstate, Y_state, Y_step, step_nr, tau, lev  
	      -> nextstate, Y_nextstate, respres, drawings],

	initstate, Y_initstate, steps_3D, tau, dyn_stat_steps, sim_params
				-> run_res { runpro_3DD }; 
// Iteration
//***************************************************************************
	[argp -> resp],
	  initp, epsapi, epsri, maxiter, adjust, numofcomps 
				-> bal_res { equalize_dynamic };
// Goal
	-> drawings;

//  Default values of simulation parameters
	level = 2;
	dyn_stat_steps = 2;
	numofcomps = 2;
	gr_red_rate = 1;
	delAllGraphs = false;
    }@*/	

//==========================================================================
//	[ state, Y_state, Y_step, lev   ->  state, Y_nextstate],
//	[oldstate, state, Y_oldstate, Y_state, Y_step, step_nr, tau, lev  
//	    -> nextstate, Y_nextstate, respres, drawings],
//
//	initstate, Y_initstate, steps_3D, tau, dyn_stat_steps, sim_params
//				-> run_res { runpro_2DD }; 
//
//			run_res = (finalstate, errorap, errorr);
//==========================================================================
    public Object[] runpro_3DD ( Subtask st1, Subtask st2, 
				double[][] initst, double[][] Y_initst,
				double timesteps, double tau,
				double dyn_stat_steps,
				double[] sim_params
				) {

      Object[] run_res = new Object[3];

      double[][]   finalst, Y_finalst ;
      double  t = 0;
      double  tau_stat = 0;
      double  X_steps;
      double  Y_steps; 

System.out.println();
System.out.println("runpro_3D_dyn:  X_steps=" + sim_params[0]);
System.out.println("runpro_3D_dyn:  Y_steps=" + sim_params[1]);
System.out.println("runpro_3D_dyn:  level=" + sim_params[2]);
System.out.println("runpro_3D_dyn:  tau=" + sim_params[3]);
System.out.println("runpro_3D_dyn:  st_steps=" + sim_params[4]);
System.out.println("runpro_3D_dyn:  maxiter=" + sim_params[5]);
System.out.println("runpro_3D_dyn:  adjust=" + sim_params[6]);
System.out.println("runpro_3D_dyn:  epsapi=" + sim_params[7]);
System.out.println("runpro_3D_dyn:  epsri=" + sim_params[8]);
System.out.println("runpro_3D_dyn:  fluid_temp=" + sim_params[9]);
System.out.println("runpro_3D_dyn:  fluid_vol=" + sim_params[10]);
System.out.println("runpro_3D_dyn:  fluid_ka=" + sim_params[11]);
System.out.println();
      X_steps = sim_params[0];
      Y_steps = sim_params[1];

//	[oldstate, state, Y_oldstate, Y_state, Y_step, tau, lev   ->  state, Y_nextstate],
//st1:	[ state, Y_state, Y_step, lev   ->  state, Y_nextstate],
//st2:	[oldstate, state, Y_oldstate, Y_state, Y_step, step_nr, tau, lev 
//	     -> nextstate, Y_nextstate, respres, drawings],


       Object[] in1 = new Object[8];
       in1[0] = initst;
       in1[1] = initst;
	 in1[2] = Y_initst;
	 in1[3] = Y_initst;
	 in1[4] = t;
	 in1[5] = t;
   	 in1[6] = tau_stat;
       in1[7] = sim_params[2];

// Start time counter
       double t1 = System.currentTimeMillis ();

//--------------------------------------------------------------------------
System.out.println
		("**** runpro_3D_dyn: Starting dynamic calculations ****");
//System.out.println("runpro_2D_dyn: X_steps="+timesteps); 

// Initializing st2 parameters
       Object[] in2 = new Object[8];
	in2[0] = initst;
	in2[1] = initst;
	in2[2] = Y_initst;
	in2[3] = Y_initst;
	in2[4] = Y_initst;
	in2[5] = t;  
   	in2[6] = tau_stat;
	in2[7] = sim_params[2] - 1;

//System.out.println("runpro: tau_stat ="+tau_stat); 
//HydUtil.print_state_2 ("runpro:  initst", initst);

	if (X_steps == 0) X_steps++;
	if (Y_steps == 0) Y_steps++;

//  ProgressBar 2-41 (Outer loop) *******************************************
        final JProgressBar Y_progressBar = new JProgressBar (0, (int)Y_steps);
        Y_progressBar.setValue(0);
        Y_progressBar.setStringPainted(true);

        final JDialog Y_d = new JDialog();
        Y_d.setLocationByPlatform( true );
        Y_d.add( Y_progressBar );
     	 Y_d.setTitle("Hydro 3D Progress");
        Y_d.setSize(250,70);
//      Y_d.pack();
     	 Y_d.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
              System.out.println("Chart frame closed - terminating program");
              Y_d.dispose();
              ee.ioc.cs.vsle.api.ProgramContext.terminate();
            }
        } );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Y_d.setVisible( true );
            }
        } );
//***************************************************************************

// Outer loop (level 2) *****************************************************
	try {
	    in1[0] = initst;
	    in1[1] = initst;

    	    for ( int j = 1;  j <= Y_steps;   j++ )  {
     	        in1[4] = (double) j;
//     	        in1[5] = (double) j;
     	        in1[5] = (double) 0;

     	        Object[] out1 = st1.run ( in1 );
      	        in1[0] = in1[1];
      	        in1[1] = (double[][]) out1[0];
      	        in1[2] = in1[3];
               in1[3] = (double[][]) out1[1];

	        in2[0] = initst;
	        in2[1] = initst;
	        in2[2] = Y_initst;
	        in2[3] = Y_initst;
	        in2[4] = (double) j;
	        in2[5] = t;  
   	        in2[6] = tau_stat;
	        in2[7] = sim_params[2] - 1;
// HydUtil.print_state_2("runpro_3DD_new:  in[0]",(double[][])in1[0]);
// HydUtil.print_state_2("runpro_3DD_new:  in[1]",(double[][])in1[1]);
// HydUtil.print_state("runpro:  state1", (double[][])in2[1]);	


// Inner loops (level 1) ****************************************************
// Static calculations-------------------------------------------------------
// System.out.println
//		("**** runpro_2D_dyn: Starting static calculations ****");

	        try {
			for ( int i = 1;   i <= dyn_stat_steps;   i++ ) {
		//System.out.println("runpro:  static step=" + i);
		      	    in2[5] = (double) i;  
	            	      Object[] out2 = st2.run ( in2 );
	        	      in2[0] = in2[1];
	        	      in2[2] = in2[3];
	        	      in2[1] = out2[0];
	        	      in2[3] = out2[1];
		 //HydUtil.print_state_2("runpro:  state",(double[][])in[1]);
	     		}
  	       }
	        catch (Exception e) {
	     		e.printStackTrace();
	        }
// System.out.println 
//		("****** runpro_2D_dyn: End of static calculations ******");
// HydUtil.print_state_2("runpro:  state",(double[][])in[1]);
//---------------------------------------------------------------------------

//  ProgressBar 2-42 (Inner loop) *******************************************
        final JProgressBar X_progressBar = new JProgressBar(0, (int)X_steps);

        X_progressBar.setValue(0);
        X_progressBar.setStringPainted(true);
        final JDialog X_d = new JDialog();
        X_d.setLocationByPlatform( true );
        X_d.add( X_progressBar );
     	  X_d.setTitle("Hydro 2D Progress");
        X_d.setSize(250,70);
        //   X_d.pack();
     	 X_d.addWindowListener( new WindowAdapter() {

           @Override
           public void windowClosing( WindowEvent e ) {
              System.out.println("Chart frame closed - terminating program");
              X_d.dispose();
              ee.ioc.cs.vsle.api.ProgramContext.terminate();
           }
        } );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                X_d.setVisible( true );
            }
        } );
//***************************************************************************

//  Dynamics calculations ---------------------------------------------------
// System.out.println 
//	      ("**** runpro_3D_dyn: Starting dynamics calculations ****");
	     for ( int i = 1;  i <= X_steps;   i++ ){

	        in2[5] = (double) i;
	        in2[6] = tau;
// System.out.println("runpro:  dynamic step=" + i);
// HydUtil.print_state("runpro:  oldstate2", (double[][])in2[0]);
// HydUtil.print_state("runpro:  state2", (double[][])in2[1]);	
	        Object[] out2 = st2.run ( in2 );
	          in2[0] = in2[1];
	          in2[2] = in2[3];
	          in2[1] = out2[0];
	          in2[3] = out2[1];

//  ProgressBar 3-42 (Inner loop) *******************************************
		 final int p2 = i;
                SwingUtilities.invokeLater( new Runnable() {
                 public void run() {
                    X_progressBar.setValue( p2 );
                 }
                });
//***************************************************************************
 	    }
//  ProgressBar 4-42 (Inner loop) *******************************************
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                X_d.dispose();
            }
        } );
//***************************************************************************
//  ProgressBar 3-41 (Outer loop) *******************************************
		 final int p1 = j;
                SwingUtilities.invokeLater( new Runnable() {
                 public void run() {
                    Y_progressBar.setValue( p1 );
                 }
                });
//***************************************************************************
	}
     }
     catch (Exception e) {
	  e.printStackTrace();
     }

//  ProgressBar 4-41 (Outer loop) *******************************************
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Y_d.dispose();
            }
        } );
//***************************************************************************

 System.out.println 
		("**** runpro_3D_dyn: End of dynamic calculations *****");

      finalst = (double[][])in2[1];
      Y_finalst = (double[][])in2[3];

//HydUtil.print_state_2 ("runpro:  finalstate", finalst);
//HydUtil.print_state_2 ("runpro:  Y_finalstate", Y_finalst);

// Stop time counter 
   	double t2= System.currentTimeMillis();

// Counting time 
      System.out.println(); 
      System.out.println ( "runpro_3D_dyn:  process computation time " +
		       (t2-t1)/1000 + " sec" );
      System.out.println(); 

// Preparing output
      run_res[0] = finalst;
      run_res[1] = Y_finalst;
      return run_res;
    }

//==================================================
//	[argp -> resp],
//	    initp, epsapi, epsri, maxiter, adjust, numofcomps 
//				 -> bal_res { equalize_dynamic };
//			bal_res = ( initp, respres, errorap, errorr);
//  performs the iteration 
//==================================================
    public double[][][] equalize_dynamic ( Subtask st, double[][] initp, 
				  double epsapi, double epsri, 
				  double maxcount, double adjust,
				  double comp) { 
      int len=0;
      for (int i = 0;   i < initp.length;   i++ ){
 	  	if ((initp[i]).length > len) len = initp[i].length;
      }

      double[][][]  result  = new double[2][][];
      double[][] 	errorap = new double[initp.length] [len];
      double[][] 	errorr  = new double[initp.length] [len];
      double[][] 	argp 	 = new double[][] { new double[initp.length], 
			            			new double[len]};
      double[][] 	resp   = new double[][] { new double[initp.length], 
			            			new double[len]};
      double  count = 0;
      double  noconverg = 1;
      double  arg, res;

	argp   = initp;
   	resp   = initp;
	Object[] in = new Object[1];

// Iteration loop		
	count = 0;
	for (int k = 0;  k < maxcount;  k++ ){
	    noconverg = 0;
	    try {
// Subtask: Preparing parameters, executing, getting output parameters back	
	        in[0] = argp;
	        Object[] out = st.run(in);
	        resp = (double[][])out[0];
//For each component of the argument calculate the next iteration input value
	        for (int i = 0;  i < argp.length;  i++ ){
		    	for (int j = 0;  j < argp[i].length;  j++ ){
		      		arg = argp[i][j];
		      		res = resp[i][j];
// If  res = NaN
				if (Double.isNaN(res)) {
		    // Preparing output
					result[0] = argp; 
					result[1] = argp; 
		      		return result;
		    		}
		     	 	if (res == 0) {
		   	  		errorap[i][j] = Math.abs(res-arg);
	    	   	  		errorr [i][j] = 1000;
	         	      } 
		       	  else {
		           		errorap[i][j] = Math.abs(res - arg);
		   	    		errorr[i][j] = Math.abs((res - arg)/res);
		       	  }	
// If one hydraulic element equalizing loop is not required
		        	if (comp == 1) {
 		                arg = res;
		     	     		errorap[i][j] = 0.0;
		   	     		errorr[i][j]  = 0.0;
		        	}
// Calculatimg new arg if iteration not converged
		        	if ((errorap[i][j] > epsapi)||(errorr[i][j] > epsri)){
		   	    		noconverg = 1;
	   	 	    		arg = (res - arg )*adjust + arg; 
		        	}	
		        	argp[i][j] = arg;
		        	resp[i][j] = res;
  		   	}
	        }
	        count++; 
	    }
	     catch (Exception e) {
	         e.printStackTrace();
	     }
	     if ( noconverg == 0) break;
       }

//¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤
//	if (count >= maxcount)
//	    System.out.println  
//		("WARNING: allowed iteration error exceeded ("
//					    + count+ " steps)");
//¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤

// Preparing output
	result[0] = resp; 
	result[1] = resp; 
      return result;
    }

//==============================================================
//	timesteps,  steps.length  -> steps { shareToComps };
//	step_nr, step_nrs.length  -> step_nrs { shareToComps };
//==============================================================
    public double[] shareToComps (double value, int length ) {
	double[]  tree = new double[length]; 
	    for (int i = 0;   i < length;   i++ ){
  	        tree[i] = value;
	    }
	return tree;
    }

//==================================================
//	
//
//==================================================
    public boolean[] shareBoolean (boolean value, int length ) {
	boolean[]  tree = new boolean[length]; 
	    for (int i = 0;   i < length;   i++ ){
  	        tree[i] = value;
	    }
	return tree;
    }

//==================================================
//	step_nr, tau,  tau_all.length  -> tau_all { share_tau };
//==================================================
    public double[] share_tau (double t, double value, int length ) {
	double[]  tree = new double[length]; 
	    for (int i = 0;   i < length;   i++ ){
  	       if ( t == 0 )   tree[i] = 0;
	        else     tree[i] = value;
	    }
	return tree;
    }

//==================================================
// Sharing fluid parameters values
//	fluid_par, tau_all.length  -> tau_all { share_fluid_par };
//==================================================
    public double[][] share_fluid_par (double[] fluid_par, int length ) {
	double[][]  tree = new double[length][fluid_par.length]; 
	    for (int i = 0;   i < length;   i++ ){
		for (int j = 0;   j < fluid_par.length;   j++ ){ 
		   tree[i][j] = fluid_par[j];
		}
	    }
	    return tree;
    }

//==================================================
//          X_steps, Y_steps, level -> 3D_steps { setSteps};
//==================================================
    public double setSteps (double X_st,  double Y_st, double lev ) {
	if (lev == 1)  return X_st;
	if (lev == 2)  return Y_st;
	  else return 2;
    }
}
