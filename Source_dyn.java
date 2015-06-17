// Class name:  Source_dyn   
//             
// Description: input disturbance signal used for process
//		different options can be chosen by setting
//		 the variable type_ ... "true"
// Input variables (poles):
//     in - in (normally time signal) 
// Output variables (poles):
//     out - output 
// Variables:
//     	in   - time
//
//  Constant:   
//     mean - the constant value at time = 0 	   
//  Step:
//     mean - the constant value at time = 0  
//     step - the height of the step   
//     tmin - for t < tmin, ramp function applies  
//     ste  - output value 	
//  Oscillation:  
//     omega   -  frequency   
//     fi      - phase lag (in degree)  
//     ampl    - amplitude
//     maxampl - limiting amplitude (to cut off)
//     osc     - output value 
//  Jum:  
//     height - the height of the step 
// 		  for t < tmin and 
//		  (tmin+dtjum)<t<(2*tmin+dtjum)
// 		  ramp function applies   
//     dtjum  - duration of jump    
//     jum    - output value 	   		   
//  Ramp: 
//     slope   - slope of the ramp function  
//     ram     - output value 	
//     
//     out     - disturbation as a function of in 
//     type    - user chosen type
//	   
// Created:       01.12.2007
// Last modified: 31.03.2011
//-------------------------------------------------------

import java.text.*;
class Source_dyn {
    /*@ specification Source_dyn {
      double  in, out;

      boolean type_con, type_ste, type_jum;
      boolean type_osc, type_ram;
      double 	mean;
      double	step;
      double	tmin;
      double  dtjum, height; 
      double  omega,fi,ampl,maxampl;
      double  slope;
//      double  Pi;

// State variables
      double 	initstate_c, oldstate_c, state_c; 
      double	nextstate_c, finalstate_c; 
	alias (double) initstate  = (initstate_c);
	alias (double) oldstate   = (oldstate_c);
	alias (double) state      = (state_c);
	alias (double) nextstate  = (nextstate_c);
	alias (double) finalstate = (finalstate_c);
// Collecting outputs
	alias (double) result = (out, nextstate_c);
// Evaluating initstate components
   	initstate_c = 0;

//**************************************************
// Method specifications
//Constant:
  	mean,type_con,state_c -> result {con_calc};
//Step:
//     mean - the constant value at time = 0
//     step - the height of the step   
//     tmin - for t < tmin, ramp function applies   
	in,mean,step,tmin,type_ste,state_c  
				-> result {ste_calc};
//  Oscillation:  
//     omega   -  frequency   
//     fi      - phase lag (in degree)  
//     ampl    - amplitude
//     maxampl - limiting amplitude (to cut off)
       in,mean,omega,fi,ampl,maxampl,type_osc,state_c 
			      -> result {osc_calc};
//  Jump:
//     height - the height of the step 
// 		for t < tmin and 
//		(tmin+dtjum)<t<(2*tmin+dtjum)
// 		ramp function applies   
//     dtjum  - duration of jump    
	in,mean,height,tmin,dtjum,type_jum,state_c 
			-> result {jum_calc};
//  Ramp: 
//     slope   - slope of the ramp function  
//     ram     - output value 	   
//     out     - disturbation as a function of in 
       in, mean, slope, type_ram, state_c  
			-> result {ram_calc};
    }@*/

//=======================================================
//	mean,type -> con {con_calc}
//=======================================================
    public double[] con_calc ( double mean, boolean type, 
				  double state_c) {
      double[] result = new double[2];
  	result[0] = mean;
       result[1] = state_c + 1;
//print_ar("ste_calc: result ",result);
       return result;
    }	

//=======================================================
// in,mean,step,tmin,type,state_c  -> result {ste_calc};	
//=======================================================
    public double[] ste_calc ( double in, double mean, 
				  double step, double tmin,
				  boolean type, double state_c){
      double[] result = new double[2];
      double ste;

	if ( in < tmin) ste = mean + in * step / tmin;
	  else ste = mean + step;
  	result[0] = ste;
      	result[1] = state_c + 1;
//print_ar("ste_calc: result ",result);
     return result;
   }	

//=======================================================
//	in,mean,height,tmin,dtjum, type,state_c 
//			-> result {jum_calc};
//=======================================================
    public double[] jum_calc ( double in, double mean,
			         double height, double tmin,
				  double dtjum,
 				  boolean type, double state_c){
      double[] result = new double[2];
      double jum = 0;

//	if ( in <= tmin) {
	if ( in < tmin) {
	   jum = mean + in * height / tmin;
	}
	 else if (in < (tmin + dtjum)) {
		  jum = mean + height;
	      }
		else if  (in < (2*tmin + dtjum)) {
			 jum = mean +(2*tmin + dtjum -in)*
				height/tmin;
		     }
		      else if (in >= (2*tmin + dtjum)) {
			       jum = mean;
			    }
  	result[0] = jum;
      	result[1] = state_c + 1;
//print_ar("jum_calc: result ",result);
      return result;
   }	

//=======================================================
//  in,mean,omega,fi,Pi,ampl,maxampl, type,state_c 
//			      		-> result {osc_calc};
//=======================================================
    public double[] osc_calc ( double in, double mean,
				  double omega, double fi,
				  double ampl, double maxampl,
 				  boolean type, double state_c){
      double[] result = new double[2];
      double osc;

	osc = ampl*Math.sin(2*Math.PI*omega*in + fi*Math.PI/180.)+mean;
  	result[0] = osc;
      	result[1] = state_c + 1;
//print_ar("osc_calc: result ",result);
     return result;
   }	

//=======================================================
//  in,mean,slope, type,state_c -> result {ram_calc};
//=======================================================
    public double[] ram_calc ( double in, double mean, 
				  double slope,
 				  boolean type, 
				  double state_c){
	double[] result = new double[2];
	double ram;

	ram = mean + slope*in;
  	result[0] = ram;
      	result[1] = state_c + 1;
//print_ar("ram_calc: result ",result);
     return result;
   }	

//=======================================================
//	void print_ar (String ar_name, double[] ar)
//=======================================================
    public void print_ar (String ar_name, double[] ar) {
      DecimalFormat dF = new DecimalFormat("0.###E0");
	System.out.print(ar_name + ":");
	for (int i = 0; i < ar.length;  i++ ) {
	   System.out.print("  " + dF.format(ar[i]));
	}
	System.out.println();
    } 
}
