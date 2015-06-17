// Class name: ResH  
//             
// Description: local hydraulic resistance,
//		  four-pole model form H
//	   
// Created:       01.12.2007
// Last modified: 14.02.2012 18.12.2012
//
// Input variables (poles): 
//     Q1 - volumetric flow at the left port 
//     p2 - pressure at the right port 
//
// Output variables (poles): 
//     Q2  - volumetric flow at the right port     
//     p1e - pressure at the left port    
//
// Internal iteration variables:    
//     p10 - steady state output pressure 
//	       at the right port
//     p1  - "disconnected" internal calculated 
//            value of the pressure p2
//
// External iteration variables:	   
//     p1e - output value of the pressure p2 
//	      to another element
//
// Resistance parameters:   
//     d - diameter of the channel   
//     l - length of the channel 
//
// Flow parameters: 
//     AL     - coefficient of hydraulical friction 
//		  at laminar flow
//     lambda - coefficient of hydraulical friction 
//              at turbulent flow 
//     zeta   - coefficient of local hydraulic resistance  
//
// Model parameters:
//     RL - resistance at laminar flow   
//     RT - resistance at turbulent flow 
//     L  - inertia of the flow               
//
// Iteration parameters:            
//     epsap - allowed absolute iteration error for pressure
//     epsr  - allowed relative iteration error for pressure
//     	  
// Calculation parameters:  
//     tau   - inverse value of yhe timestep, (1/delta)
//     delta - timestep  
//  
// State components:
// 	1:  p1 
//----------------------------------------------------------
//import java.text.*;
import ee.ioc.cs.vsle.api.Subtask;
class ResH {

    /*@ specification ResH  {
      double  p2, Q1;
      double  Q2, p1e;
      double  p1;
      double  tau;
      double  l, d;
      String  res_type;
      double  dlt, kE, mu, dltpN, QN, A;  
      double  pmax;
      double  epsap, epsr;
      double  flp1, flp2, flp3, flp4, flp5, flp6, flp7;
      	double  initp1e;

// State variables
	double  initval1; 
	double  oldval1; 
	double  val1; 
	double  nextval1; 
	double  finalval1;
	  alias (double) initstate  = (initval1);
	  alias (double) oldstate   = (oldval1);
	  alias (double) state      = (val1);
	  alias (double) nextstate  = (nextval1);
	  alias (double) finalstate = (finalval1);
// Iterable variables
	double  initp_c, argp_c, resp_c, respres_c;
	  alias (double) initp   = (initp_c);
	  alias (double) argp    = (argp_c);	    
	  alias (double) resp    = (resp_c);
	  alias (double) respres = (respres_c);

// Collecting outputs
	alias (double) out = (p1, Q2);
  	alias (double[]) result = (nextstate,out);

//  Fluid parameters
     alias (double) fluid_par = (flp1,flp2,flp3,flp4,flp5,flp6,flp7);
//  Hydraulic resistor parameters
     alias (double) res_par=(l,d,dlt,kE,mu,dltpN,QN,A);

// Organizing iterations
	argp_c    = p1e;
	respres_c = p1e;
	resp_c    = p1;
// Evaluating initial state
	initval1 = initp1e;
   	  initp_c  = initp1e;

// Method specification
     [ Flow |- fluid_par, res_par, res_type, pfl-> RL, RT, L ],
   	tau, Q1, p2, epsap, epsr, pmax, state, 
	fluid_par, res_par, res_type   -> result { resH_next };

// Default conditions:
	l       = 0.02;
	d       = 0.0015;
	pmax    = 2.2e7;
	res_type = "RRa";
	dlt = 5e-6;
	kE = 2;
	mu = 0.7;	
	dltpN = 2e5;
	QN = 6.2e-4;
	A =  1e-4;	
	epsap   = 1e-8;
    	epsr    = 1e-8;
	 initp1e = 2.115e7;
    }@*/

//==========================================================
//     [ Flow |- fluid_par, res_par, res_type, pfl-> RL, RT, L ],
//   	tau, Q1, p2, epsap, epsr, pmax, state, 
//	fluid_par, res_par, res_type   -> result { resH_next };
//
//	  		out    = (p1, Q2);
//  	 		result = (nextstate, out);
//==========================================================
    public double[][] resH_next ( Subtask st, double tau, 
			   double Q1, double p2,
			   double epsap, double epsr, 
			   double pmax,
			   double[] state,
			   double[] fluid_par,
			   double[] res_par,
			   String res_type ) {

      double[][]  result = { new double[state.length], 
		          	   new double[2] };
      double  pfl, RL, RT;
      double  Q2;
      double  errstap, errstr;
      double  p1 = 0, p10;

	//LSS.print_ar (" ******RRaH_next:  state ",state);
	try {

//----------------------------------------------------------
// Static calculations
	   if ( tau == 0 ) {
	      pfl = p2;
	      Q2 = Q1;   
	      //System.out.println("RRaH_next: pfl=  "+pfl);

// Subtask: Preparing parameters, executing and 
//	     getting output parameters	
	        Object[] in = new Object[4];
	        in[0] = fluid_par;
	        in[1] = res_par;
	        in[2] = res_type;
	        in[3] = pfl;
	        Object[] out = st.run(in);
		   RL = (Double)out[0];
		   RT = (Double)out[1];
	        p10 = p2 + (RL + RT * Math.abs(Q1)) * Q1;
	      //System.out.println("resG_next: p10="+ p10);
	      for ( int i=1;  i <= 10;  i++) {
	         pfl = (p10 + p2)/2;
	        //System.out.println("resG_next: pfl="+ pfl);
// Subtask: Preparing parameters, executing and 
//	     getting output parameters	
		 in[3] = pfl;
		 out   = st.run(in);
		   RL  = (Double)out[0];
		   RT  = (Double)out[1];
		   //System.out.println("resG_next:  RL="
		   //+RL+" RT="+RT+" L="+L);
	         p1 = p2 + (RL + RT * Math.abs(Q1)) * Q1;
	         //System.out.println("resG_next: p1="+p1);
	         errstap = Math.abs(p1 - p10);   
	         errstr  = Math.abs(p1 - p10)/p1;
       	   if ((errstap < epsap) & (errstr < epsr)) break;
	         p10 = p1;	    
	      } 
	      if ( p1 >=  pmax) { p1 = pmax; }
       	      if ( p2 < -1e5)   { p2 = -1e5; }
	      //System.out.println("RRaH_next:  p1=" + p1);
// For Q1e we need the value at the previous timestep   
// the relation "state" is a "fictive" relation because
// a way to calculate "nextstate" must be defined    

// Preparing output 
//  nextstate
	      result[0][0] = p1; 
//  out
	      result[1][0] = p1;
	      result[1][1] = Q2;	          
	   }

//----------------------------------------------------------
// Dynamics calculations
	   if ( tau > 0 ) {
 	      p1    = state[0];	
	      pfl   = (p1 + p2)/2;            
	      if (pfl < 1e3)   { pfl = 1e3; }
// Subtask: Preparing parameters, executing and 
//	     getting output parameters	
	        Object[] in = new Object[4];
	        in[0] = fluid_par;
	        in[1] = res_par;
	        in[2] = res_type;
	        in[3] = pfl;
	        Object[] out = st.run(in);
		   RL = (Double)out[0];
		   RT = (Double)out[1];
       	        p1 = p2 + (RL+RT*Math.abs(Q1)) * Q1;
	      //System.out.println("RRaH_next:  p1=" + p1);
 	      if ( p1 >= pmax) { p1 = pmax;  }	    
      	      if (p1 < -1e5)  { p1 = -1e5; }
	      Q2 = Q1; 
	      //System.out.println("RRaH_next: p1=" + p1);
// Preparing output
//  nextstate
 	      result[0][0] = p1; 
//  out 
 	      result[1][0] = p1;
	      result[1][1] = Q2;
	   }
	}
       	 catch (Exception e) {
    	      e.printStackTrace();
       	 }
       return result;
    }
}
