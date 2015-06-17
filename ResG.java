// Class name: ResG  
//             	   
// Description: local hydraulic resistor, 
//		  four-pole model form G
//             	   
// Created:       01.12.2007
// Last modified: 14.02.2012 18.12.2012
//
// Input variables (poles): 
//     Q2  - volumetric flow at the right port 
//     p1  - pressure at the left port 
//
// Output variables (poles): 
//     Q1  - volumefric flow at the left port     
//     p2e - pressure at the right port    
//
// Internal iteration variables:    
//     p20 - steady state output pressure at the right port
//     p2  - "disconnected" internal calculated value 
//            of the pressure p2
//
// External iteration variables:
//     p2e - output value of the pressure p2 to another element
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
// Calculation parameters:  
//     tau   - inverse value of yhe timestep, (1/delta)
//     delta - timestep  
//  
// State components:
// 	1:  p2
//-------------------------------------------------------------

import ee.ioc.cs.vsle.api.Subtask;
class ResG  {
    /*@ specification ResG  {
      double  p1, Q2;
      double  Q1, p2e;
      double  p2;
      double  tau;
      double  l, d;
      String res_type;
      double dlt, kE, mu, dltpN, QN, A;  
      double  pmax;
      double  epsap, epsr;
      double  flp1, flp2, flp3, flp4, flp5, flp6, flp7;
      double  initp2e;

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
	  alias (double) out = (p2, Q1);
  	  alias (double[]) result = (nextstate, out);

//  Fluid parameters
       alias (double) fluid_par=(flp1,flp2,flp3,flp4,flp5,flp6,flp7);
//  Hydraulic resistor parameters
       alias (double) res_par=(l,d,dlt,kE,mu,dltpN,QN,A);

// Organizing iterations
	argp_c    = p2e;
	respres_c = p2e;
	resp_c	= p2;
// Evaluating initial state
	initval1 = initp2e;
   	    initp_c  = initp2e;

// Method specification
      [ Flow |- fluid_par, res_par, res_type, pfl-> RL, RT, L ],
	tau, Q2, p1, epsap, epsr, pmax, state,
 	fluid_par, res_par, res_type    ->  result {resG_next};

// Default conditions:
	l       = 0.02;
	d       = 0.003;
	pmax    = 1.5e6;
	res_type = "RRa";
	dlt = 5e-6;
	kE = 2;
	mu = 0.7;	
	dltpN = 2e5;
	QN = 6.2e-4;
	A =  1e-4;		
	epsap   = 1e-8;
    	epsr    = 1e-8;
	  initp2e = 1.5e6; 
    }@*/

//=============================================================
// 	[ Flow |- fluid_par, res_par, res_type, pfl-> RL, RT, L ],
//	   tau, Q2, p1, epsap, epsr, pmax, state,
// 	   fluid_par, res_par, res_type    ->  result {resG_next};
//
//	  		out = (p2, Q1);
//  	  		result = (state, nextstate, out);
//=============================================================
    public double[][] resG_next ( Subtask st, double tau,
			   double Q2,  double p1,
			   double epsap, double epsr,
			   double pmax,
			   double[] state, 
			   double[] fluid_par,
			   double[] res_par,
			   String res_type ) {

      double[][]  result = { new double[state.length], 
				   new double[2] };
      double  pfl, RL, RT;
      double  Q1;
      double  errstap, errstr;
      double  p2, p20;

 	 p2 = 0;
	 try {
//-------------------------------------------------------------
// Static calculations
	    if ( tau == 0 ) {
	       pfl = p1;
  	       Q1  = Q2; 
	       //System.out.println("resG_next: pfl=  " + pfl);
// Subtask: Preparing parameters, executing and 
//	  getting output parameters	
	        Object[] in = new Object[4];
	        in[0] = fluid_par;
	        in[1] = res_par;
	        in[2] = res_type;
	        in[3] = pfl;
	         Object[] out = st.run(in);
		RL = (Double)out[0];
		RT = (Double)out[1];
	       //System.out.println
	       //("resG_next:  RL="+RL+" RT="+RT+" L="+L);
    	       p20 = p1 - (RL + RT * Math.abs(Q2)) * Q2;
	      //System.out.println("resG_next: p20=" + p20);
	       for ( int i=1;  i <= 10;  i++) {
	          pfl = (p20 + p1)/2;
		//System.out.println(
		//"resG_next:  changed pfl=" + pfl);
// Subtask: Preparing parameters, executing and 
//	     getting output parameters	
		  in[3] = pfl;
		  out = st.run(in);
		  RL  = (Double)out[0];
		  RT  = (Double)out[1];
	 	p2 = p1 - (RL + RT * Math.abs(Q2)) * Q2;
		//System.out.println("resG_next:  p2= " + p2);
		errstap = Math.abs(p2 - p20);   
		errstr  = Math.abs(p2 - p20)/p2;
           	   if ((errstap < epsap) & (errstr < epsr)) break;
		p20 = p2;
	         if ( p2 >=  pmax) { p2 = pmax; }
		if ( p2 <= (-1e-05) ) { p2 = -1e-05; }
	       }  
	       Q1 = Q2;
	       //System.out.println("resG_next:  p2=" + p2);
// Preparing output 
// nextstate
	       result[0][0] = p2; 
// out
	       result[1][0] = p2;
	       result[1][1] = Q1;
	    }

//-------------------------------------------------------------
// Dynamics calculations
	    if ( tau > 0 ) {
 	       p2   = state[0];	
	       pfl  = (p2 + p1)/2;            
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
       	       p2 = p1 - (RL + RT*Math.abs(Q2)) * Q2;
	       //System.out.println("resG_next:  p2=" + p2);
	       if (p2 >= pmax) { p2 = pmax; }	    
      	       if (p2 < -1e5)  { p2 = -1e5; }
	       Q1 = Q2;
	       //System.out.println("resG_next:  p2=" + p2);
// Preparing output
//  nextstate
 	       result[0][0] = p2; 
//  out 
 	       result[1][0] = p2;
	       result[1][1] = Q1;
	    }
	}
       	 catch (Exception e) {
    	     e.printStackTrace();
       	 }
       return result;
    }
}
