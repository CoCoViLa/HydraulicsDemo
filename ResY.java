// Class name:  ResY  (Former REL )
//               
// Description: Hydraulic resistor (Entlastungswiderstand) 
//
// Input variables (poles): 
//     p1 - pressure at the left port 
//     p2 - pressure at the right port
//
// Output variables (poles):
//     Q1 - volumetric flow at the left port      
//     Q2 - volumetric flow at the right port  
//
// Parameters:          
//     DDR   - diameter of the orifice         
//     alpha - discharge coefficient  
// 
// Calculation parameters:  
//     tau   - inverse value of the timestep, (1/dt) 
// 
// State components:
//     1:  Q1 
//	   
// Created:       01.12.2007
// Last modified: 14.02.2012
//----------------------------------------------------------------

//import java.text.*;
import ee.ioc.cs.vsle.api.Subtask;
class ResY {
    /*@ specification ResY {

      double p1, p2, Q1, Q2;
      double tau;
      String res_type;
      double l, d, dlt, kE, mu, dltpN, QN, A;  
      double flp1, flp2, flp3, flp4, flp5, flp6, flp7;
      double initQ1;

//  State variables
	double  initval1, oldval1, val1; 
	double  nextval1, finalval1;
	  alias (double) initstate    = (initval1);
	  alias (double) oldstate    = (oldval1);
	  alias (double) state         = (val1);
	  alias (double) nextstate  = (nextval1);
	  alias (double) finalstate  = (finalval1);
//  Collecting outputs
	alias (double) out = (Q1, Q2);
  	alias (double[]) result = (nextstate, out);
//  Fluid parameters
      alias (double) fluid_par = (flp1,flp2,flp3,flp4,flp5,flp6,flp7);
//  Hydraulic resistor parameters
      alias (double) res_par=(l,d,dlt,kE,mu,dltpN,QN,A);
// Evaluating initstate components
       initval1 = initQ1;

// Method specification
 	[ Flow |- fluid_par, res_par, res_type, pfl-> rho ],
       	  tau, d, mu, p2, p1, state,
	  fluid_par, res_par, res_type   -> result { ResY_next };
// Default conditions:
	l       = 0.02;
	d       = 0.002;
	res_type = "RRa";
	dlt = 5e-06;
	kE = 2;
	mu = 0.7;	
	dltpN = 2e+05;
	QN = 1e-04;
	A =  1e-04;	
        initQ1 = 2.892e-05; 

    }@*/

//================================================================
// 	[ Flow |- fluid_par, res_par, res_type, pfl-> rho ],
//       	  tau, d, mu, p2, p1, state,
//	  fluid_par, res_par, res_type   -> result { ResY_next };
//	       		out = (Q1, Q2);
//  	       		result = (nextstate, out);
//================================================================
    public double[][] ResY_next (Subtask st, double tau,
			 	    double d, double mu, 
			 	    double p2, double p1, 
			 	    double[] state,
			    	    double[] fluid_par,
				    double[] res_par,
				    String res_type ) {

     double[][] result = {new double[state.length],new double[2]};
     double  pfl, rho, A, Q1, Q2;
	Q1 = 0;
	Q2 = 0;
	//LSS.print_ar (" ******REL_next:  state ",state);
	try {
//----------------------------------------------------------------
//   Calculation of both steady-state conditions and dynamics: 
	   pfl = p2;     
	   //System.out.println("RVP_next: pfl="+pfl );        
//----------------------------------------------------------
//  Subtask: Preparing parameters, executing and 
//	   getting output parameters back
	        Object[] in = new Object[4];
	        in[0] = fluid_par;
	        in[1] = res_par;
	        in[2] = res_type;
	        in[3] = pfl;
	     Object[] out = st.run(in);
	     rho = (Double)out[0]; 
//----------------------------------------------------------
	   A  = Math.PI * Math.pow(d,2) / 4;
  	   //System.out.println("REL_next: p1=" 
	   //+p1+ "  p2="+p2 ); 
	   Q1 = mu*A*Math.sqrt(2*Math.abs(p1-p2)/rho)*sign(p1-p2);
	   if (p1 == p2) { Q1 = 0; }
	   Q2 = Q1; 
	   //System.out.println("REL_next: Q1=Q2= "+Q1);
// Preparing output
// nextstate
 	   result[0][0] = Q1; 
// out 
 	   result[1][0] = Q1;
 	   result[1][1] = Q2;
      	}
       	 catch (Exception e) {
    	    e.printStackTrace();
        }
       return result;
    }
//===================================================================
//	 double sign ( double x);
//===================================================================
    public double sign ( double x ) {		
      double res;
	  if ( x < 0 ) res = -1;
	    else res = 1;
         return res;
    }  
}
