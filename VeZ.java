import ee.ioc.cs.vsle.api.Subtask;

// Class name: veZ
// Description: hydraulic volume elasticity form Z
//
// Input variables (poles) 
//     Q1  - volumetric flow at the left port 
//     Q2  - volumetric flow at the right port
//
// Output variables (poles) 
//     p1  - pressure at the left port     
//     p2  - pressure at the right port    
//
// External iteration variables:
//     p1 - output value of the pressure p1 for this element
//     p1e - output value of the pressure p1 to another element
//     p2 - output value of the pressure p2 for this element
//     p2e - output value of the pressure p2 to another element
//
// Volume elasticity parameters   
//     l - volume lenght
//     d - volume diameter
//
// Flow characteristics
//     C - volume elasticity 
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
//     	2: p2
//	   
// Created:       01.12.2007
// Last modified: 07.05.2009
//-------------------------------------------------------------------

class VeZ  {
    /*@
	 specification VeZ  {
    	double Q1, Q2;
    	double p1e, p2e;
   	double  p1, p2;
   	double  dp1;
	double  l, d, V;
      String res_type;
      double  flp1, flp2, flp3, flp4, flp5, flp6, flp7;
    	double  initp1e; 
   	double  tau;
	double  betam;
   	double  Pi;

//  State variables
	double  initval1, initval2;
	double  oldval1, oldval2; 
	double  val1, val2; 
	double  nextval1, nextval2; 
	double  finalval1, finalval2;
	    alias (double) initstate   = (initval1, initval2);
	    alias (double) oldstate    = (oldval1, oldval2);
	    alias (double) state       = (val1, val2);
	    alias (double) nextstate   = (nextval1, nextval2);
	    alias (double) finalstate  = (finalval1, finalval2);

// Iterable variables
	double  initp_c1, argp_c1, resp_c1, respres_c1;
	double  initp_c2, argp_c2, resp_c2, respres_c2;
        	    alias (double) initp    = (initp_c1, initp_c2);
	    alias (double) argp     = (argp_c1, argp_c2);
	    alias (double) resp     = (resp_c1, resp_c2);
	    alias (double) respres  = (respres_c1, respres_c2);

// Collecting outputs  
	    alias (double)   out = ( p1, p2, p1e, p2e );
  	    alias (double[]) result = ( state, nextstate, out );

//  Fluid parameters
    alias (double) fluid_par = (flp1,flp2,flp3,flp4,flp5,flp6,flp7);

// Organizing iterations
	    argp_c1 = p1e;
	    argp_c2 = p2e;
	    respres_c1 = p1e;
	    respres_c2 = p2e;
	    resp_c1 = p1;
	    resp_c2 = p2;

// Evaluating initial states
	    initval1 = initp1e;
	    initval2 = initp1e;
   	      initp_c1 = initp1e;
   	      initp_c2 = initp1e;

// Equations
	V = l * Pi * d * d / 4; 

// Method specification
       [ Flow |- fluid_par, pfl-> betam ],
       	tau, Q1, Q2, V, initp1e, 
 	      oldstate, state, fluid_par   -> result { veZ_next };

// Parameters values
	l  = 0.1;
	d  = 0.1;
	Pi = 3.1415926;	
// Initial values
    	initp1e = 5E6; 
    }@*/

//============================================================
//       [ Flow |- fluid_par, pfl-> betam ],
//       	tau, Q1, Q2, V, initp1e, 
// 	oldstate, state, fluid_par   -> result { veZ_next };
//	   	result    = ( state, nextstate, out );
// 	   	out = ( p1c, p2c, p1e, p2e );
//============================================================
    public double[][] veZ_next ( Subtask st, double tau,
		 	  double Q1, double Q2, double V, 
			  double initp1e, double[] oldstate, 
			  double[] state, double[] fluid_par ) {
 
       double[][] result = { new double[state.length], 
		        new double[state.length], 
		        new double[4]};
        double  pfl;
        double  delta, betam;
        double  dp1;
        double  C;
        double  k1, k2, k3, k4;
        double  p1, p2;
//System.out.println("veZ_next:  start: tau="+tau+" Q1a="+Q1a+" Q2a="+Q2a+
//		" V="+V+" initp1c="+initp1c+" initp2c="+initp2c);
//System.out.println("veZ_next: use_flag_c=  " + use_flag_c);	
//print_ar (" ******veZ_next input:  oldstate ", (double[]) oldstate);
//print_ar (" ******veZ_next input:  state ", (double[]) state);
	try {
//----------------------------------------------------------------------
// Initial approximation calculations for steady-state conditions
	      if ( tau == 0 ) {
	         p1 = initp1e;   
                  p2 = initp1e;

// Preparing output 
// state
	        result[0][0] = p1; 
	        result[0][1] = p2;
// nextstate
	        result[1][0] = p1; 
	        result[1][1] = p2;
// out
	        result[2][0] = p1;
	        result[2][1] = p2;
	        result[2][2] = p1;
	        result[2][3] = p2;			
//print_ar (" ******veZ_next static output:  state, nextstate ", result[1]);
	      }

//----------------------------------------------------------------------
// Dynamics calculations
	      if ( tau > 0 ) {
		delta =1 / tau;
		p1 = state[0];
		p2 = state[1];
		pfl = (p1 + p2) / 2;
//System.out.println("VeZ_next:  pfl=  " + pfl);

//Subtask:  Preparing parameters, executing and getting output parameters	
		 Object[] in = new Object[2];
		 in[0] = fluid_par;
		 in[1] = pfl;
		   Object[] out = st.run(in);
		     betam = (Double)out[0];
//System.out.println("VeZ_next:  betam=  " + betam);
	
// Computing the difference   	         
		C = V * betam;	   
		dp1 = delta * (Q1 - Q2) / C;   		
// System.out.println("VeZ_next: dp1c="+dp1c);	

// Computing of the Runge-Kutta coefficients 
		k1 = dp1;		        
		k2 = k1 + k1/2;		          
		k3 = k1 + k2/2;		      
		k4 = k1 + k3;   	          
// Computing nextstate values    
	  	p1 = state[0] + (k1 + 2*k2 + 2*k3 + k4)/6;       
	    	p2 = p1;       

   	      if ( p1 < (-1E5)) { p1 = -1E5; }
	      if ( p2 < (-1E5)) { p2 = -1E5; }
	
// Preparing output 
// state
	        result[0][0] = state[0]; 
	        result[0][1] = state[1];
// nextstate
	        result[1][0] = p1; 
	        result[1][1] = p2;
// out
	        result[2][0] = p1;
	        result[2][1] = p2;
	        result[2][2] = p1;
	        result[2][3] = p2;	

// System.out.println("VeZ_next: p1, p1="+p1+" p2, p2="+p2);	
// print_ar (" ******VeZ_next dynamic output: state ", result[0]);
// print_ar (" ******VeZ_next dynamic output: nextstate ", result[1]);
	    }	
	}
       	  catch (Exception e) {
    	      e.printStackTrace();
       	  }
      return result;
    }

//============================================================
//	void print_ar (String ar_name, double[] ar)
//============================================================
//    public void print_ar (String ar_name, double[] ar) {		
//	System.out.print(ar_name + ":");
//	for (int i = 0; i < ar.length;  i++ ) {
//	        System.out.print("  " + ar[i]);	
//	}
//	System.out.println();			
//    }
}

