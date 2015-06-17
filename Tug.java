// Class name: Tubeg  
//             
// Description: hydraulic tube L-R-C, dead right end 
//		  four-pole model form G
//	   
// Created:       22.09.2008
// Last modified: 14.02.2012; 02.05.2012; 18.12.2012
//
// Input variable (pole):  
//     p1 - pressure at the left port 
//
// Output variable (pole): 
//     Q1 - volumetric flow at the left port         
//
// Internal iteration variables:    
//     p2e0 - steady state output pressure at the right port
//     p30  - steady state medial pressure
//
// External iteration variables:
//     p2c - output value of the pressure p2 for this element
//     p2e - output value of the pressure p2 to another element
//
// Tube parameters:   
//     A - inner cross section   
//     d - inner diameter    
//     E - module of elasticity of  the tube material
//     l - length            
//     s - thickness of the tube walls 
//  
// Flow parameters: 
//     AL     - coefficient of hydraulical friction at laminar flow
//     lambda - coefficient of hydraulical friction at 
//	         turbulent flow
//     zeta   - coefficient of local hydraulic resistance
//
// Model parameters:
//     C   - volume elasticity 
//     RL  - resistance at laminar flow   
//     RT  - resistance at turbulent flow 
//     L   - inertia of the flow       
//     kC  - coefficient of correcting the value C    
//     kL  - coefficient of correcting the value L
//     kr  - coefficient of correcting the stationary resistances
//          in dynamic 
//          
// Calculation parameters:  
//     tau   - inverse value of yhe timestep, (1/delta)
//     delta - timestep  
//  
// State components:
// 	1:  Q1
// 	2:  p2
//	   
// Created:       22.09.2008
// Last modified: 14.02.2012; 02.05.2012
//-------------------------------------------------------------------

//import java.text.*;
import ee.ioc.cs.vsle.api.Subtask;
class Tug  {
    /*@  specification Tug {

      double  p1;
      double  Q1;
      double  p2;
      double  tau;
      double  l, d, K, s;
      double  kr, kC, kL;
      double  flp1, flp2, flp3, flp4, flp5, flp6, flp7;
       double  initQ1;  
       double  initp2;   

//  State variables
	double  initval1, initval2; 
	double  oldval1, oldval2; 
	double  val1, val2; 
	double  nextval1, nextval2; 
	double  finalval1, finalval2;
	  alias (double) initstate = (initval1, initval2);
	  alias (double) oldstate  = (oldval1, oldval2);
	  alias (double) state     = (val1, val2);
	  alias (double) nextstate = (nextval1, nextval2);
	  alias (double) finalstate= (finalval1,finalval2);
// Collecting outputs
	  alias (double) out = ( Q1 );
  	  alias (double[]) result = (state, nextstate, out);

//  Fluid parameters
   	alias (double) fluid_par = (flp1,flp2,flp3,flp4,flp5,flp6,flp7);
//  Tube parameters
   	alias (double) tube_par = (l, d, K, s);

// Evaluating initial state
	initval1 = initQ1;
	initval2 = initp2;
// Equations
	kC = 1;
	kL = 1;

// Method specification
 	[ Flow |- fluid_par, tube_par, pfl-> RL, RT, L, C ], 
     	    tau, p1, kC, kL, kr, oldstate, state, 
	    fluid_par, tube_par	-> result { tubeg_next };
// Default conditions:
	kr  = 2;
	l   = 0.25;
	d   = 0.04;
	K   = 2.1e11;
	s   = 0.003;
   	  initQ1  = 0;
     	  initp2 = 21.15e6;     
   }@*/

//===================================================================
// 	[ Flow |- fluid_par, tube_par, pfl-> RL, RT, L, C ], 
//     	    tau, p1, kC, kL, kr, oldstate, state, 
//	    fluid_par, tube_par	-> result { tubeg_next };
//	  	out    = (Q1);
//  	  	result = (state, nextstate, out);
//===================================================================
    public double[][] tubeg_next ( Subtask st, double tau,
			    double p1,  double kC, 
			    double kL, double kr,
			    double[] oldstate, double[] state,
			    double[] fluid_par,
			    double[] tube_par) {
      double[][] result= { new double[state.length], 
		             new double[state.length], 
		             new double[1]};
      double  Q1;
      double  p2;
      double  delta, pfl;
      double  RL, RT, L, C;
      double  dQ1, dp2;
      double  kq11, kq12, kq13, kq14;
      double  dq11, dq12, dq13; 
      double  kp21, kp22, kp23, kp24;
      double  dp21, dp22, dp23;

	 //LSS.print_ar (" ******tubeG_next:  state ",state);
	 try {

//-------------------------------------------------------------------
// Initial approximate calculations for steady-state conditions
	   if ( tau == 0 ) {
	      Q1  = 0;
	      p2 = p1;

// Preparing output 
//  state
	       result[0][0] = Q1; 
	       result[0][1] = p2;
//  nextstate
	       result[1][0] = Q1; 
	       result[1][1] = p2;
//  out
	       result[2][0] = Q1;
	   }

//-------------------------------------------------------------------
// Dynamics calculations
	   if ( tau > 0 ) {
	      delta = 1 / tau;
	      Q1  = state[0];
	      p2  = state[1];
	      pfl = (p2 + p1) / 2;
	      //System.out.println("tubeG_next:  pfl=  " + pfl);

// Subtask: Preparing parameters, executing and 
//	     getting output parameters
	        Object[] in = new Object[3];
	        in[0] = fluid_par;
	        in[1] = tube_par;
	        in[2] = pfl;
	        Object[] out = st.run(in);
		   RL = (Double)out[0];
		   RT = (Double)out[1];
		   L  = (Double)out[2];
		   C  = (Double)out[3];

// Calculating Runge-Kutta coefficients
	      dQ1 = deltaQ1 (delta, kL, L, p1, p2, kr, RL, RT, Q1);
	      dp2 = deltap2 ( delta, kC, C, Q1);
	      	kq11 = dQ1;
	      	kp21 = dp2;
			dq11 = Q1  + kq11 / 2;
			dp21 = p2 + kp21 / 2;
	      dQ1 = deltaQ1 (delta, kL, L, p1, dp21, kr, RL, RT, dq11);
	      dp2 = deltap2 ( delta, kC, C, dq11);
	          kq12 = dQ1 + kq11 / 2;
	          kp22 = dp2 + kp21 / 2;
			dq12 = Q1  + kq12 / 2;
			dp22 = p2 + kp22 / 2;
	      dQ1 = deltaQ1 (delta, kL, L, p1, dp22, kr, RL, RT, dq12);
	      dp2 = deltap2 ( delta, kC, C, dq12);
	          kq13 = dQ1 + kq12 / 2;
	          kp23 = dp2 + kp22 / 2;
			dq13 = Q1  + kq13 / 2;
			dp23 = p2 + kp23 / 2;
	      dQ1 = deltaQ1 (delta, kL, L, p1, dp23, kr, RL, RT, dq13);
	      dp2 = deltap2 ( delta, kC, C, dq13);
	          kq14 = dQ1 + kq13;
	          kp24 = dp2 + kp23;
	//System.out.println("tubeG_next: kq11="+kq11+" kq12="+kq12+
	//		" kq13="+kq13+" kq14="+kq14);	
	//System.out.println("tubeG_next: kp31="+kp31+" kp32="+kp32+
	//		" kp33="+kp33+" kp34="+kp34);	

// Computing of the nextstate values: 
	Q1 = state[0] + ( kq11 + 2 * kq12 + 2 * kq13 + kq14) / 6;
	p2 = state[1] + ( kp21 + 2 * kp22 + 2 * kp23 + kp24) / 6;    
	    if ( p2 < (-1.0E+5)) { p2 = -1.0E+5; }
// 	    if ( p2 > (2.2E+7))  { p2 = 2.2E+7; }

// Preparing output 
//  state
	      result[0][0] = state[0]; 
	      result[0][1] = state[1];
//  nextstate
	      result[1][0] = Q1; 
	      result[1][1] = p2;
//  out
	      result[2][0] = Q1;
	   }	
	}
       	 catch (Exception e) {
    	    e.printStackTrace();
       	 }
          return result;
    }

//===================================================================
// 	deltaQ1e ( delta, kL, L, p1, p2, kr, RL, RT, Q1 ) 	
//===================================================================
   public double deltaQ1 ( double delta, double kL, double L, 
		        double p1, double p2, double kr,
		        double RL, double RT, double Q1) {
      double dQ1;          
	dQ1 = delta/kL/ L *
	      (p1 - p2 - kr*(RL + RT*Math.abs(Q1)) * Q1); 
      	return dQ1;		
   }

//===================================================================
// 	deltap3 ( delta, kC, C, Q1 )	
//===================================================================
    public double deltap2 ( double delta, double kC, double C,
		        double Q1 ) { 
       double dp2; 
	    dp2 = delta/kC/ C * Q1;
       return dp2; 
   }
}

