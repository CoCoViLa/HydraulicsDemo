import ee.ioc.cs.vsle.api.Subtask;

// Class name: Tubeh  
//             
// Description: hydraulic tube C-R-L, 
//		  two-pole model form H
//
// Input variables (poles): 
//     p2 - input pressure at the right port 
//
// Output variables (poles): 
//     Q2 - output volume flow at the right port       
//
// Tube parameters:   
//     d - inner diameter    
//     K - module of elasticity of  the tube material
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
//           in dynamic 
//
// Calculation parameters:  
//     tau   - inverse value of yhe timestep, (1/delta)
//     delta - timestep  
//
// State components:
// 	1:  Q2
// 	2:  p1 
//
// Created:       01.12.2007
// Last modified: 14.02.2012
//-------------------------------------------------------------------

//import java.text.*;
class Tuh {
    /*@ specification Tuh {
      double  p2;
      double  Q2;
      double  p1c;
      double  tau;
      double  l, d, K, s;
      double  kr, kC, kL;
      double  flp1, flp2, flp3, flp4, flp5, flp6, flp7;
       double  initQ2;  
       double  initp1; 

//  State variables
	double  initval1, initval2; 
	double  oldval1, oldval2; 
	double  val1, val2; 
	double  nextval1, nextval2; 
	double  finalval1, finalval2;
	  alias (double) initstate  = (initval1, initval2);
	  alias (double) oldstate   = (oldval1, oldval2);
	  alias (double) state      = (val1, val2);
	  alias (double) nextstate  = (nextval1, nextval2);
	  alias (double) finalstate = (finalval1, finalval2);
//  Collecting outputs
	alias (double)   out = (Q2);
  	alias (double[]) result = (state, nextstate, out);
//  Fluid parameters
   	alias (double) fluid_par = (flp1,flp2,flp3,flp4,flp5,flp6,flp7);
//  Tube parameters
   	alias (double) tube_par = (l, d, K, s);

// Evaluating initial state 
	initval1 = initQ2;
	initval2 = initp1;

// Equations
	kC = 1;
	kL = 1;

//  Method specification
 	[ Flow |- fluid_par, tube_par, pfl-> RL, RT, L, C ], 
     	    tau, p2, kC, kL, kr, oldstate, state, 
	    fluid_par, tube_par	->  result { tuh_next };

//  Default conditions:
	kr  = 3;
	l   = 2;
	d   = 0.02;
	K   = 2.1e+11;
	s   = 0.003;
// Initial values
   	  initQ2  = 7.93e-4;	        
    	  initp1 = 1.37E+7;
   }@*/

//===================================================================
// 	[ Flow |- fluid_par, tube_par, pfl-> RL, RT, L, C ], 
//     	    tau, p2, kC, kL, kr, oldstate, state, 
//	    fluid_par, tube_par	->  result { tuh_next };
//			out = (Q2);
//  			result = (state, nextstate, out);
//===================================================================
    public double[][] tuh_next ( Subtask st, double tau,
		 	   double p2, double kC, 
			   double kL, double kr,
			   double[] oldstate, double[] state, 
			   double[] fluid_par,
			   double[] tube_par) {
      double[][] result= { new double[state.length], 
		             new double[state.length], 
		             new double[1]};

      double  RL, RT, L, C;
      double  Q2;
      double  p1,p1c = 0;
      double  delta;
      double  pfl;

      double  dQ2, dp1;
      double  kq11, kq12, kq13, kq14;
      double  dq11, dq12, dq13; 
      double  kp11, kp12, kp13, kp14;
      double  dp11, dp12, dp13;

	 //LSS.print_ar (" ******tubeH_next:  state ", state);
	 try {
//-------------------------------------------------------------------
// Initial approximation calculations for steady-state conditions
	   if ( tau == 0 ) {
	      Q2 = 0;
	      p1 = p2;      

// Preparing output 
// state
	      result[0][0] = Q2; 
	      result[0][1] = p1;
// nextstate
	      result[1][0] = Q2; 
	      result[1][1] = p1;
// out
	      result[2][0] = Q2;
	   }
//-------------------------------------------------------------------
// Dynamics calculations
	   if ( tau > 0 ) {
	      delta = 1 / tau;
	      Q2 = state[0];
	      p1 = state[1];
	       pfl = (p1c + p2) / 2;
	      //System.out.println("tubeH_next:  pfl=  " + pfl);
// Subtask: Preparing parameters, executing and 
//	  getting output parameters	
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
            	dQ2 = deltaQ2 ( delta,kL,L,p1,p2,kr,RL,Q2,RT);  
	      	dp1  = deltap1 ( delta,kC,C,Q2 );	         
	              kq11 = dQ2; 
	    	    kp11 = dp1;     
	    		dq11 = Q2  + kq11/2;
	    		dp11 = p1 + kp11/2;    	    	 
		dQ2 = deltaQ2 ( delta,kL,L,dp11,p2,kr,RL,dq11,RT);
		dp1  = deltap1  ( delta,kC,C,dq11 );        
	    	    kq12 = dQ2  + kq11/2;
	    	    kp12 = dp1 + kp11/2;       
	      		dq12 = Q2  + kq12/2;  
	      		dp12 = p1 + kp12/2;         
		dQ2 = deltaQ2 ( delta,kL,L,dp12,p2,kr,RL,dq12,RT);
		dp1  = deltap1  ( delta,kC,C,dq12 );         
	    	    kq13 = dQ2  + kq12/2;
	    	    kp13 = dp1 + kp12/2;       
	    		dq13 = Q2  + kq13/2;  
	    		dp13 = p1 + kp13/2;	     		 
		dQ2 = deltaQ2 ( delta,kL,L,dp13,p2,kr,RL,dq13,RT);
		dp1  = deltap1  ( delta,kC,C,dq13 );         
 	    	    kq14 = dQ2  + kq13; 
	    	    kp14 = dp1 + kp13;      
	//System.out.println("tubeH_next: kq11="+kq11+" kq12="+kq12+
	//" kq13="+kq13+" kq14="+kq14);	
	//System.out.println("tubeH_next: kp31="+kp31+" kp32="+kp32+
	//" kp33="+kp33+" kp34="+kp34);	

// Computing of the nextstate values: 
	Q2  = state[0] + ( kq11 + 2 * kq12 + 2 * kq13 + kq14) / 6;
	p1  = state[1] + ( kp11 + 2 * kp12 + 2 * kp13 + kp14) / 6;
   	  if ( p1 < -1e+05)    { p1 = -1e+05; }  
	  if ( p1 > (2.5e+07)) { p1 = 2.5e+07; }
// Preparing output 
// state
	      result[0][0] = state[0];
	      result[0][1] = state[1];
// nextstate
	      result[1][0] = Q2; 
	      result[1][1] = p1;
// out
	      result[2][0] = Q2;
	   }	
	 }
       	  catch (Exception e) {
    	      e.printStackTrace();
       	  }
        return result;
    }

//===================================================================
// 	deltaQ2 ( delta, kL, L, p1, p2, kr, RL, Q2, RT)	
//===================================================================
   public double deltaQ2 ( double delta, double kL, double L,
		       double p1, double p2, double kr,
		        double RL, double Q2, double RT ) {	                    
    double dQ2;
	dQ2 = delta /(kL * L )*
	      (p1 - p2 - kr * (RL +RT * Math.abs (Q2)) * Q2);     
     	return dQ2;		
   }

//===================================================================
// 	deltap3 ( delta, kC, C, Q2 )	
//===================================================================
   public double deltap1 ( double delta, double kC,
		       double C, double Q2) {  
      double dp1;
	dp1 = delta / (kC * C) * ( -Q2 );
      	return dp1; 
   }
}
