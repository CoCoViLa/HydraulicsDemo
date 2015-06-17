import ee.ioc.cs.vsle.api.Subtask;

// Class name: TubeH  
//             
// Description: hydraulic tube C-R-L, 
//		  four-pole model form H
//
// Input variables (poles): 
//     Q1 - volumetric flow at the left port 
//     p2 - pressure at the right port 
//
// Output variables (poles): 
//     Q2 - volumetric flow at the right port     
//     p1 - pressure at the left port    
//
// Internal iteration variables:    
//     p10 - steady-state output pressure at the right port    
//
// External iteration variables:
//     p1 - output value of the pressure p1 for this element	   
//     p1e - output value of the pressure p1 to another element  
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
//           in dynamic 
//     nc  - number of similar connected tubes  
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
// 	1:  Q2e
//  	2:  p1
//
// Created:       01.12.2007
// Last modified: 14.02.2012
//-------------------------------------------------------------------

//import java.text.*;
class TubeH {
    /*@ specification TubeH {
      double  p2, Q1;
      double  Q2, p1e;
      double  p1, p10;
      double  tau;
      double  l, d, K, s;
      double  nc, kr;
      double  kC, kL;
      double  epsap, epsr;
      double  flp1, flp2, flp3, flp4, flp5, flp6, flp7;
      double  Pi;
        double  initQ2;  
        double  initp1e; 

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
// Iterable variables
 	double  initp_c, argp_c, resp_c, respres_c;
	  alias (double) initp   = (initp_c);
	  alias (double) argp    = (argp_c);	    
	  alias (double) resp    = (resp_c);
	  alias (double) respres = (respres_c);

//  Collecting outputs
	alias (double)   out = (p1, Q2);
  	alias (double[]) result = (state, nextstate, out);

//  Fluid parameters
   	alias (double) fluid_par = (flp1,flp2,flp3,flp4,flp5,flp6,flp7);
//  Tube parameters
   	alias (double) tube_par = (l, d, K, s);

// Organizing iterations
	argp_c    = p1e;
	respres_c = p1e;
	resp_c    = p1;

// Evaluating initial state 
	initval1 = initQ2;
	initval2 = initp1e;
  	  initp_c = initp1e;

// Equations
	kC = Pi/2*(2/Pi)^(1/nc);
	kL = Pi/2*(2/Pi)^(1/nc);

//  Method specification
 	[ Flow |- fluid_par, tube_par, pfl-> RL, RT, L, C ], 
     	    tau, Q1, p2, nc, kC, kL, kr, 
	    epsap, epsr, oldstate, state, fluid_par, tube_par 	
			      ->  result { tubeH_next };
//  Default conditions:
	nc  = 1;
	kr  = 1.5;
	Pi  = 3.1415926;
	l   = 14;
	d   = 0.019;
	K   = 2.1E11;
	s   = 0.003;
	epsap  = 0.1;
    	epsr   = 1.0E-8;
   	  initQ2  = 1E-4;	        
    	  initp1e = 5E6;
   }@*/

//===================================================================
// 	[ Fluid_ind |- len, diam, pfl-> RL, RT, L, C ], 
//     	    tau, Q1, p2, nc, kC, kL, kr,  
//	    epsap, epsr, oldstate, state, l, d 	
//			      	-> result { tubeH_next };
//			out = (p1, Q2);
//  			result = (state, nextstate, out);
//===================================================================
    public double[][] tubeH_next ( Subtask st, double tau, double Q1,
		 	    double p2, double nc, double kC, 
			    double kL, double kr,
			    double epsap, double epsr,
			    double[] oldstate, double[] state, 
			    double[] fluid_par, 
			    double[] tube_par) {
      double[][] result= { new double[state.length], 
		             new double[state.length], 
		             new double[2]};
      double  errstap, errstr;
      double  RL, RT, L, C;
      double  Q2;
      double  p10, p1;
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
	      Q2 = Q1;      
	      p1 = state[1];
	      pfl = p2;
//  System.out.println("tubeH_next:  p2=" + p2);
	      //System.out.println("tubeH_next: initial pfl= "+ pfl);
// Subtask: Preparing parameters, executing and 
//	     getting output parameters	
	        Object[] in = new Object[3];
	        in[0] = fluid_par;
	        in[1] = tube_par;
	        in[2] = pfl;
	        Object[] out = st.run(in);
		   RL = (Double)out[0];
		   RT = (Double)out[1];
//		   L  = (Double)out[2];
//		   C  = (Double)out[3];
		   //System.out.println("resH_next:  RL=" +
		   //RL+" RT="+RT+" L="+L+" C="+C);

	      p10 = p2 + ((RL + RT * Math.abs(Q1)) * Q1); 
  	      //System.out.println("tubeH_next:  p2c0="
	      //+ p2c0+"  p30=" + p30);
//-------------------------------------------------------------------
// Calculations of steady-state conditions
	      for ( int i=1;  i <= 10;  i++) {
	         pfl = (p10 + p2)/2;
	         //System.out.println("tubeH_next:  pfl=" + pfl);
// Subtask: Preparing parameters, executing and 
//	     getting output parameters	

		   in[2] = pfl;
		   out = st.run(in);
		     RL = (Double)out[0];
		     RT = (Double)out[1];
//		     L  = (Double)out[2];
//		     C  = (Double)out[3];
//  		 if ( Q1 <= 0 )  { p1 = p2; } tulemus jama
	         p1 = p2 + ((RL + RT * Math.abs(Q1)) * Q1);  
//  System.out.println("tubeH_next:  p1=" + p1);	
  	      if ( p1 < -1e5)    { p1 = -1e5; }  
//	      if ( p1 > (2.1e7)) { p1 = 2.1e7; }
//	      if ( p1 > (13.285e6)) { p1 = 13.285e6; }
	         errstap = Math.abs(p1 - p10);   
	         errstr  = Math.abs(p1 - p10)/p1;      
       	   if ((errstap < epsap) & (errstr < epsr)) break;
	         p10 = p1;	    
	      }  
	    
// Preparing output 
// state
	      result[0][0] = Q2; 
	      result[0][1] = p1;
// nextstate
	      result[1][0] = Q2; 
	      result[1][1] = p1;
// out
	      result[2][0] = p1;
	      result[2][1] = Q2;
	   }
//-------------------------------------------------------------------
// Dynamics calculations
	   if ( tau > 0 ) {
	      delta = 1 / tau;
	      Q2    = state[0];
	      p1   = state[1];
	      	pfl = (p1 + p2) / 2;
	      //System.out.println("tubeH_next:  pfl=  " + pfl);
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
            	dQ2 = deltaQ2 ( delta,kL,L,p1,p2,kr,RL,Q2,RT);  
	      	dp1  = deltap1 ( delta,kC,C,Q1,Q2 );	         
	              kq11 = dQ2; 
	    	    kp11 = dp1;     
	    		dq11 = Q2  + kq11/2;
	    		dp11 = p1 + kp11/2;    	    	 
		dQ2 = deltaQ2 ( delta,kL,L,dp11,p2,kr,RL,dq11,RT);
		dp1  = deltap1 ( delta,kC,C,Q1,dq11 );        
	    	    kq12 = dQ2  + kq11/2;
	    	    kp12 = dp1 + kp11/2;       
	      		dq12 = Q2  + kq12/2;  
	      		dp12 = p1 + kp12/2;         
		dQ2 = deltaQ2 ( delta,kL,L,dp12,p2,kr,RL,dq12,RT);
		dp1  = deltap1 ( delta,kC,C,Q1,dq12 );         
	    	    kq13 = dQ2  + kq12/2;
	    	    kp13 = dp1 + kp12/2;       
	    		dq13 = Q2  + kq13/2;  
	    		dp13 = p1 + kp13/2;	     		 
		dQ2 = deltaQ2 ( delta,kL,L,dp13,p2,kr,RL,dq13,RT);
		dp1  = deltap1 ( delta,kC,C,Q1,dq13 );         
 	    	    kq14 = dQ2  + kq13; 
	    	    kp14 = dp1 + kp13;      	

// Computing nextstate values: 
	Q2 = state[0] + ( kq11 + 2 * kq12 + 2 * kq13 + kq14) / 6;
	p1 = state[1] + ( kp11 + 2 * kp12 + 2 * kp13 + kp14) / 6;
   	      if ( p1 < -1e5)    { p1 = -1e5; }  
//	      if ( p1 > (2.5e7)) { p1 = 2.5e7; }

// Preparing output 
// state
	      result[0][0] = state[0];
	      result[0][1] = state[1];
// nextstate
	      result[1][0] = Q2; 
	      result[1][1] = p1;
// out
	      result[2][0] = p1;
	      result[2][1] = Q2;
	   }	
	 }
       	  catch (Exception e) {
    	      e.printStackTrace();
       	  }
        return result;
    }

//===================================================================
// 	deltaQ2 ( delta, kL, L, p1, p2, kr, RL, Rt, Q2)	
//===================================================================
   public double deltaQ2 ( double delta, double kL, double L,
		       double p1, double p2, double kr,
		        double RL, double Q2, double RT ) {                               
     double dQ2;
	dQ2 = delta /(kL * L ) * 
	       (p1 - p2 - kr * (RL + RT * Math.abs (Q2)) * Q2);         
    	return dQ2;		
   }

//===================================================================
// 	deltap1 ( delta, kC, C, Q1, Q2 )	
//===================================================================

 public double deltap1 ( double delta, double kC, double C,
		        double Q1, double Q2) {  
      double dp1;
	dp1 = delta / (kC * C) * (Q1 - Q2);
      	return dp1; 
   }
}



