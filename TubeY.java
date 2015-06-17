// Class name: TubeY 
//
// Description: hydraulic tube L/2-R/2-C-R/2-L/2,
//		four-pole model form Y  	
// Input variables (poles) 
//     p1 - pressure at the left port 
//     p2 - pressure at the right port
//
// Output variables (poles) 
//     Q1  - volumetric flow at the left port     
//     Q2  - volumetric flow at the right port  
//
// Inner variables 
//     p3	- middle pressure 
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
//	      turbulent flow
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
// Calculation parameters  
//     tau   - inverse value of yhe timestep, (1/delta)
//     delta - timestep  
//  
// State components
//     	1: Q1
//     	2: p3
//	3: Q2
//	   
// Created:       15.04.2009
// Last modified: 14.02.2012
//-------------------------------------------------------------------

//import java.text.*;
import ee.ioc.cs.vsle.api.Subtask;
class TubeY  {
    /*@  specification TubeY {

      double  p1, p2;
      double  Q1, Q2; 
      double  tau;   
      double  l, d, K, s;
      double  nc, kr;
      double  kC, kL;
      double  RL, RT;
      double  flp1, flp2, flp3, flp4, flp5, flp6, flp7;
      double  Pi;
        double  initQ1;  
        double  initp3;   
        double  initQ2; 

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

// Collecting outputs
	  alias (double) out = (Q1, Q2);
  	  alias (double[]) result = (state, nextstate, out);

//  Fluid parameters
   	alias (double) fluid_par = (flp1,flp2,flp3,flp4,flp5,flp6,flp7);

//  Tube parameters
   	alias (double) tube_par = (l, d, K, s);

// Evaluating initial state
	initval1 = initQ1;
	initval2 = initp3;
  	initval3 = initQ2; 

// Equations
	kC = Pi/2*(2/Pi)^(1/nc);
	kL = Pi*(2/Pi)^(2/nc);

// Method specification
 	[ Flow |- fluid_par, tube_par, pfl-> RL, RT, L, C ],
     	    tau, p1, p2, nc, kC, kL, kr, oldstate, state, 
	    fluid_par, tube_par	   -> result { tubeY_next };

// Default conditions:
	nc  = 1;
	kr  = 1.5;
	Pi  = 3.1415926;
	l   = 14;
	d   = 0.019;
	K   = 2.1e+11;
	s   = 0.003;
// Initial values
   	  initQ1 = 1E-4;
	  initp3 = 5E6;
     	  initQ2 = 1E-4;    
   }@*/

//===================================================================
// 	[ Flow |- fluid_par, tube_par, pfl-> RL, RT, L, C ],
//     	tau, p1, p2, nc, kC, kL, kr, oldstate, state, 
//	fluid_par, tube_par	   -> result { tubeY_next };
//			out = (Q1, Q2);
//  			result = (state, nextstate, out);
//===================================================================
    public double[][] tubeY_next ( Subtask st, double tau, double p1,
			    double p2, double nc, double kC, 
			    double kL, double kr,
 			    double[] oldstate, double[] state,
			    double[] fluid_par,
			    double[] tube_par) {
      double[][] result= { new double[state.length], 
		      new double[state.length], 
		      new double[2]};
      double  Q1, Q2;
      double  p3, pfl;
      double  delta;
      double  RL, RT, L, C;
      double  dQ1, dp3, dQ2;
      double  kq11, kq12, kq13, kq14;
      double  kp31, kp32, kp33, kp34;
      double  kq21, kq22, kq23, kq24;
      double  dq11, dq12, dq13; 
      double  dp31, dp32, dp33;
      double  dq21, dq22, dq23; 

	 try {
//-------------------------------------------------------------------
//  Calculations for steady-state conditions
	   if ( tau == 0 ) {
		pfl = (p1 + p2)/2.;   

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
		  //System.out.println("resG_next:  RL="+
		  //RL+" RT="+RT+" L="+L+" C="+C);

// Solving: p1 - p2 - kr * (RL + RT * Math.abs(Q1)) * Q1 = 0;
	Q1 = (- RL + Math.sqrt(RL * RL + 4 * RT * Math.abs(p1 - p2)))/(2 * RT);
	Q2 = Q1;
 	    if (Q1 <= 0) {Q1 = 0;}
  	    if (Q2 <= 0) {Q2 = 0;}
	p3 = pfl;    

// Preparing output 
// state
	       result[0][0] = Q1; 
	       result[0][1] = p3;
	       result[0][2] = Q2; 
// nextstate
	       result[1][0] = Q1; 
	       result[1][1] = p3;
	       result[1][2] = Q2; 
// out
	       result[2][0] = Q1;
	       result[2][1] = Q2;	   	    
	}

//-------------------------------------------------------------------
// Dynamics calculations
	   if ( tau > 0 ) {
	      delta = 1 / tau;
	      Q1    = state[0];
	      p3    = state[1];
	      Q2    = state[2];
	        pfl   = (p1 + p2) / 2;
	        //System.out.println("tubeG_next:  pfl=  " + pfl);

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
		   // System.out.println("tubeY_next:  RL="
		   // +RL+" RT="+RT+" L="+L+" C="+C);

// Calculating Runge-Kutta coefficients
	      dQ1 = deltaQ1 (delta, kL, L, p1, p3, kr, RL, RT, Q1);
	      dp3 = deltap3 ( delta, kC, C, Q1, Q2);
	      dQ2 = deltaQ2 (delta, kL, L, p3, p2, kr, RL, RT, Q2);
	      	kq11 = dQ1;
	      	kp31 = dp3;
		kq21 = dQ2;
			dq11 = Q1 + kq11 / 2;
			dp31 = p3 + kp31 / 2;
			dq21 = Q2 + kq21 / 2;
	      dQ1 = deltaQ1 (delta, kL, L, p1, dp31, kr, RL, RT, dq11);
	      dp3 = deltap3 (delta, kC, C, dq11, dq21);
	      dQ2 = deltaQ2 (delta, kL, L, dp31, p2, kr, RL, RT, dq21);
	          kq12 = dQ1 + kq11 / 2;
	          kp32 = dp3 + kp31 / 2;
		 kq22 = dQ2 + kq11 / 2;
			dq12 = Q1 + kq12 / 2;
			dp32 = p3 + kp32 / 2;
			dq22 = Q2 + kq22 / 2;
	      dQ1 = deltaQ1 (delta, kL, L, p1, dp32, kr, RL, RT, dq12);
	      dp3 = deltap3 (delta, kC, C, dq12, dq22);
	      dQ2 = deltaQ2 (delta, kL, L, dp32, p2, kr, RL, RT, dq22);
	          kq13 = dQ1 + kq12 / 2;
	          kp33 = dp3 + kp32 / 2;
	          kq23 = dQ2 + kq22 / 2;
			dq13 = Q1 + kq13 / 2;
			dp33 = p3 + kp33 / 2;
			dq23 = Q2 + kq23 / 2;
	      dQ1 = deltaQ1 (delta, kL, L, p1, dp33, kr, RL, RT, dq13);
	      dp3 = deltap3 (delta, kC, C, dq13, dq23);
	      dQ2 = deltaQ2 (delta, kL, L, dp33, p2, kr, RL, RT, dq23);
	          kq14 = dQ1 + kq13;
	          kp34 = dp3 + kp33;
	          kq24 = dQ2 + kq23;
	// System.out.println("tubeY_next: kq11="+kq11+" kq12="+kq12+
	//		" kq13="+kq13+" kq14="+kq14);	
	// System.out.println("tubeY_next: kp31="+kp31+" kp32="+kp32+
	//		" kp33="+kp33+" kp34="+kp34);	
	// System.out.println("tubeY_next: kq21="+kq21+" kq22="+kq22+
	//		" kq23="+kq23+" kq24="+kq24);	

// Computing of the nextstate values 
	Q1 = state[0] + (kq11 + 2 * kq12 + 2 * kq13 + kq14) / 6;
	p3 = state[1] + (kp31 + 2 * kp32 + 2 * kp33 + kp34) / 6;
	Q2 = state[2] + (kq21 + 2 * kq22 + 2 * kq23 + kq24) / 6;

	    if (p3 <= (-1E5)) {p3 = -1E5;}
  	    if (p3 >= ( 2.5E7)) {p3 =  2.5E7;}

// Preparing output 
// state
	      result[0][0] = state[0]; 
	      result[0][1] = state[1];
 	      result[0][2] = state[2];
// nextstate
	      result[1][0] = Q1; 
	      result[1][1] = p3;
  	      result[1][2] = Q2; 
// out
	      result[2][0] = Q1;
	      result[2][1] = Q2;
	   }	
	}
       	 catch (Exception e) {
    	    e.printStackTrace();
       	 }
          return result;
    }

//===================================================================
// 	deltaQ1 ( delta, kL, L, p1, p3, kr, RL, RT, Q1 ) 	
//===================================================================
   public double deltaQ1 ( double delta, double kL, double L, 
		       double p1, double p3, double kr,
		       double RL, double RT,
                            double Q1) {
      double dQ1;         
	dQ1 = delta/(kL * L/2) * (p1 - p3 - kr * (RL + 
		RT * Math.abs(Q1)) * Q1 / 2); 
      return dQ1;		
   }

//===================================================================
// 	deltap3 ( delta, kC, C, Q1, Q2 )	
//===================================================================
    public double deltap3 ( double delta, double kC, double C,
			 	double Q1, double Q2) { 
       double dp3;
	    dp3 = delta/(kC * C) *( Q1 - Q2);
       return dp3; 
   }

//===================================================================
// 	deltaQ2 ( delta, kL, L, p1, p3, kr, RL, RT, Q2 ) 	
//===================================================================
   public double deltaQ2 ( double delta, double kL, double L, 
		       double p3, double p2, double kr,
		       double RL, double RT, 
                            double Q2) {
      double dQ2;         
	dQ2 = delta/(kL * L/2) * (p3 - p2 - kr * (RL + 
		RT * Math.abs(Q2)) * Q2 / 2); 
      return dQ2;		
   }
//===================================================================
//	double sign ( double x );
//===================================================================
    public double sign ( double x ) {
      double res;
	 if ( x < 0 ) res = -1;
	   else res = 1;
        return res;	
    } 
}


