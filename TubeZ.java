import ee.ioc.cs.vsle.api.Subtask;

// Class name: TubeZ 
//
// Description: hydraulic tube C/2-L-R-C/2 
//		four-pole model form Z  	
//	   
// Created:       16.04.2009
// Last modified: 14.02.2012 18.12.2012
//
// Input variables (poles) 
//     Q1  - volumetric flow at the left port     
//     Q2  - volumetric flow at the right port 
//
// Output variables (poles) 
//     p1 - pressure at the left port 
//     p2 - pressure at the right port
//
// Inner variables 
//     Q3 - middle volumetric flow 
//
// External iteration variables:
//     p1  - output value of the pressure p1 for this element
//     p1e - output value of the pressure p1 to another element
//     p2  - output value of the pressure p2 for this element
//     p2e - output value of the pressure p2 to another element
//
// Tube parameters   
//     d - inner diameter    
//     K - module of elasticity of  the tube material
//     l - length            
//     s - thickness of the tube walls 
//
// Flow parameters 
//     AL     - coefficient of hydraulical friction at laminar flow
//     lambda - coefficient of hydraulical friction at 
//	       turbulent flow
//     zeta   - coefficient of local hydraulic resistance
//
// Flow characteristics
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
// Iteration parameters            
//     epsap - allowed absolute iteration error for pressure 
//     epsr  - allowed relative iteration error for pressure 
//     	  
// Calculation parameters  
//     tau   - inverse value of yhe timestep, (1/delta)
//     delta - timestep  
//  
// State components
//     	1: p1
//     	2: Q3
//	3: p2
//	   
// Created:       16.04.2009
// Last modified: 14.02.2012
//-------------------------------------------------------------------

//import java.text.*;
class TubeZ {
    /*@ specification TubeZ {
      double  Q1, Q2;  
      double  p1, p2;  
      double  p1e, p2e;
      double  tau;
      double  l, d, K, s;
      double  nc, kr;
      double  kC, kL;
      double  flp1, flp2, flp3, flp4, flp5, flp6, flp7;
      double  Pi;
        double  initp1e;  
        double  initQ3;   
     	    double  initp2e;  
//  State variables
	double  initval1, initval2, initval3; 
	double  oldval1, oldval2, oldval3; 
	double  val1, val2, val3; 
	double  nextval1, nextval2, nextval3; 
	double  finalval1, finalval2, finalval3;
	  alias (double) initstate  = (initval1, initval2, initval3);
	  alias (double) oldstate   = (oldval1, oldval2, oldval3);
	  alias (double) state      = (val1, val2, val3);
	  alias (double) nextstate  = (nextval1, nextval2, nextval3);
	  alias (double) finalstate = (finalval1, finalval2, finalval3);

// Iterable variables
	double  initp_c1, argp_c1, resp_c1, respres_c1;
	double  initp_c2, argp_c2, resp_c2, respres_c2;
	  alias (double) initp   =  (initp_c1, initp_c2);
	  alias (double) argp    = (argp_c1, argp_c2);	    
	  alias (double) resp    = (resp_c1, resp_c2);
	  alias (double) respres = (respres_c1, respres_c2);

// Collecting outputs
	  alias (double) out = ( p1, p2, p1e, p2e );
  	  alias (double[]) result = (state, nextstate, out);

//  Fluid parameters
   	alias (double) fluid_par = (flp1,flp2,flp3,flp4,flp5,flp6,flp7);

//  Tube parameters
   	alias (double) tube_par = (l, d, K, s);

// Organizing iterations
	    argp_c1 = p1e;
	    argp_c2 = p2e;
	    respres_c1 = p1e;
	    respres_c2 = p2e;
	    resp_c1 = p1;
	    resp_c2 = p2;

// Evaluating initial state
	        initval1 = initp1e;
	        initval2 = initQ3;
	        initval3 = initp2e;
   	          initp_c1 = initp1e;
   	          initp_c2 = initp2e;
// Equations
	kC = Pi*(2/Pi)^(2/nc);
	kL = Pi/2*(2/Pi)^(1/nc);

//  Method specification
 	[ Flow |- fluid_par, tube_par, pfl-> RL, RT, L, C ], 
     	    tau, Q1, Q2, nc, kC, kL, kr, oldstate, state, 
	    fluid_par, tube_par 	->  result { tubeZ_next };

// Geometrical, physical data and constants:      
	nc  = 1;
	kr  = 5;
	Pi  = 3.1415926;
	l   = 0.25;
	d   = 0.10;
	K   = 2.1e11;
	s   = 0.003;
// Initial values:
   	  initp1e = 2.115e7;
	  initQ3  = 0;
	  initp2e = 2.115e7;  	
   }@*/

//===================================================================
// 	[ Flow |- fluid_par, tube_par, pfl-> RL, RT, L, C ], 
//     	    tau, Q1, Q2, nc, kC, kL, kr,
//	    oldstate, state, fluid_par, tube_par 	
//			      ->  result { tubeZ_next };
//			out = ( p1, p2, p1e, p2e );
//  			result = (state, nextstate, out);
//===================================================================
    public double[][] tubeZ_next ( Subtask st, double tau, double Q1,
		 	     double Q2, double nc, double kC, 
			     double kL, double kr,
			     double[] oldstate, double[] state, 
			     double[] fluid_par, 
			     double[] tube_par) {
      double[][] result = { new double[state.length], 
		       new double[state.length], 
		       new double[4]};
      double  p1, p2;
      double  Q3, pfl;
      double  delta;
      double  RL, RT, L, C;
      double  dp1, dQ3, dp2;
      double  kp11, kp12, kp13, kp14;
      double  kq31, kq32, kq33, kq34;
      double  kp21, kp22, kp23, kp24;
      double  dp11, dp12, dp13; 
      double  dq31, dq32, dq33;
      double  dp21, dp22, dp23; 

//LSS.print_ar (" ******tubeZ_next:  state ", state);
	 try {
//-------------------------------------------------------------------
// calculation of the steady state conditions:            
// Form tubeZ does not have steady state conditions.
// Initstates must be computed using another forms of tube model,
// which is used in the system model for steady state conditons.
	   if ( tau == 0 ) {
		p1 = state[0];
		Q3 = state[1];
		p2 = state[2];	    
// Preparing output 
// state
		result[0][0] = p1; 
		result[0][1] = Q3;
		result[0][2] = p2;
// nextstate
		result[1][0] = p1; 
		result[1][1] = Q3;
		result[1][2] = p2;
// out
	    	result[2][0] = p1;
	    	result[2][1] = p2;
	     	result[2][2] = p1;
	     	result[2][3] = p2;
	   }
//-------------------------------------------------------------------
// Dynamics calculations
	   if ( tau > 0 ) {
	      delta = 1 / tau;
	      p1    = state[0];
	      Q3    = state[1];
	      p2    = state[2];
	        pfl   = (p1 + p2) / 2;
	        // System.out.println("tubeZ_next:  pfl=  " + pfl);

// Subtask: Preparing parameters, executing and getting output parameters
	        Object[] in = new Object[3];
	        in[0] = fluid_par;
	        in[1] = tube_par;
	        in[2] = pfl;
	        Object[] out = st.run(in);
		   RL = (Double)out[0];
		   RT = (Double)out[1];
		   L  = (Double)out[2];
		   C  = (Double)out[3];
		   // System.out.println("tubeZ_next:  RL="
		   //+RL+" RT="+RT+" L="+L+" C="+C);

// Calculating Runge-Kutta coefficients
	      dp1 = deltap1 (delta, kC, C, Q1, Q3);
	      dQ3 = deltaQ3 (delta, kL, L, p1, p2, kr, RL, RT, Q3);
	      dp2 = deltap2 (delta, kC, C, Q3, Q2);
	      	kp11 = dp1;
	      	kq31 = dQ3;
		kp21 = dp2;
			dp11 = p1 + kp11 / 2;
			dq31 = Q3 + kq31 / 2;
			dp21 = p2 + kp21 / 2;

	      dp1 = deltap1 (delta, kC, C, Q1, dq31);
	      dQ3 = deltaQ3 (delta, kL, L, dp11, dp21, kr, RL, RT, dq31);
	      dp2 = deltap2 (delta, kC, C, dq31, Q2);
	      	kp12 = dp1 + kp11 / 2;
	      	kq32 = dQ3 + kq31 / 2;
		kp22 = dp2 + kp21 / 2;
			dp12 = p1 + kp12 / 2;
			dq32 = Q3 + kq32 / 2;
			dp22 = p2 + kp22 / 2;

	      dp1 = deltap1 (delta, kC, C, Q1, dq32);
	      dQ3 = deltaQ3 (delta, kL, L, dp12, dp22, kr, RL, RT, dq32);
	      dp2 = deltap2 (delta, kC, C, dq32, Q2);
	      	kp13 = dp1 + kp12 / 2;
	      	kq33 = dQ3 + kq32 / 2;
		kp23 = dp2 + kp22 / 2;
			dp13 = p1 + kp13 / 2;
			dq33 = Q3 + kq33 / 2;
			dp23 = p2 + kp23 / 2;

	      dp1 = deltap1 (delta, kC, C, Q1, dq33);
	      dQ3 = deltaQ3 (delta, kL, L, dp13, dp23, kr, RL, RT, dq33);
	      dp2 = deltap2 (delta, kC, C, dq33, Q2);
	      	kp14 = dp1 + kp13;
	      	kq34 = dQ3 + kq33;
		kp24 = dp2 + kp23;

	// System.out.println("tubeZ_next: kp11="+kp11+" kp12="+kp12+
	//		" kp13="+kp13+" kp14="+kp14);	
	// System.out.println("tubeZ_next: kq31="+kq31+" kq32="+kq32+
	//		" kq33="+kq33+" kq34="+kq34);	
	// System.out.println("tubeZ_next: kp21="+kp21+" kp22="+kp22+
	//		" kp23="+kp23+" kp24="+kp24);	

// Computing of the nextstate values 
	      p1 = state[0] + (kp11 + 2 * kp12 + 2 * kp13 + kp14) / 6;
	      Q3 = state[1] + (kq31 + 2 * kq32 + 2 * kq33 + kq34) / 6;
	      p2 = state[2] + (kp21 + 2 * kp22 + 2 * kp23 + kp24) / 6;

  	      if (p1 < (-1e5))  {p1 = -1e5;}
//	      if (p1 > (2.2e7)) {p1 = 2.2e7;} 
   	      if (p2 < (-1e5))  {p2 = -1e5;} 
//	      if (p2 > (2.2e7)) {p2 = 2.2e7;} 

// Preparing output 
// state
	      result[0][0] = state[0]; 
	      result[0][1] = state[1];
 	      result[0][2] = state[2];
// nextstate
	      result[1][0] = p1; 
	      result[1][1] = Q3;
  	      result[1][2] = p2; 
// out
	      result[2][0] = p1;
	      result[2][1] = p2;
	      result[2][2] = p1;
	      result[2][3] = p2;
	   }	
	}
       	 catch (Exception e) {
    	    e.printStackTrace();
       	 }
          return result;
    }

//===================================================================
// 	deltap1 ( delta, kC, C, Q1, Q3 )	
//===================================================================
    public double deltap1 ( double delta, double kC, double C,
			 	double Q1, double Q3) { 
       double dp1;
	    dp1 = delta/(kC * C/2) *( Q1 - Q3);
       return dp1; 
   }
//===================================================================
// 	deltaQ3 ( delta, kL, L, p1, p3, kr, RL, RT, Q3 ) 	
//===================================================================
   public double deltaQ3 ( double delta, double kL, double L, 
		       double p1, double p2, double kr,
		       double RL, double RT,
                            double Q3) {
      double dQ3;         
	dQ3 = delta/(kL * L) * (p1 - p2 - kr * (RL + 
	      RT * Math.abs(Q3)) * Q3); 
      return dQ3;		
   }

//===================================================================
// 	deltap2 ( delta, kC, C, Q3, Q2 )	
//===================================================================
    public double deltap2 ( double delta, double kC, double C,
			 	double Q3, double Q2) { 
       double dp2c;
	    dp2c = delta/(kC * C/2) *( Q3 - Q2);
       return dp2c; 
   }
}


