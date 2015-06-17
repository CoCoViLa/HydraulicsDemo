// Class name:  Source_stat   
//             
// Description: input constant disturbance 
//	( input disturbance signal used for process
//	  different options can be chosen by setting
//	  the variable type, e.g 'osc' or 'imp' ) 
//
// Output variables (poles):
//     out - output 
//
// Variables:
//     	init_stat     - 
//     	final_stat    - 
//     	initstate_val - 
//     	steps         - 
//     	step_nr       - 
//	   
// Created:       23.02.2011
// Last modified: 04.03.2011
//----------------------------------------------------

import java.text.*;
class Source_dyn_stat {
    /*@ specification Source_dyn_stat {

//      double init_stat;
//      double final_stat;
      double init_val;
      double final_val;
      double initstate_val;
      double out;
//      double steps;
//      double step_nr;
      double Y_steps;
      double Y_step_nr;

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
      initstate_c = initstate_val;

//*************************************************
// Methods specifications
      init_val, Y_steps, final_val, 
      Y_step_nr, state_c
		 ->  result {Source_dyn_stat_calc};

// Default conditions:
//      init_stat  = 0;    
//      final_stat = 10; 
//        initstate_val = init_stat;  
        initstate_val = init_val;    
    }@*/

//====================================================
//  	init_stat, steps, final_stat, 
//  	step_nr, state_c  
//			-> result {Source_stat_calc} 
//		result = (out, nextstate_c);
//====================================================
    public double[] Source_dyn_stat_calc 
		 ( double init_stat, double steps,
	  	   double final_stat, double step_nr,
		   double state_c)  {

      double[] result = new double[2];
      double stp, out, nextstate_c;

	if (steps == 1) steps++;
      	stp = (final_stat - init_stat)/(steps - 1);
      	out = init_stat + stp * (step_nr - 1); 

      	nextstate_c = out;

//System.out.println("Source_dyn_stat:  ch = "+changeable+
//			"  init_val = "+init_stat+
//			"  state = "+state_c+
//			"  steps = "+steps+
//			"  step_nr = "+step_nr+
//			"  out = "+out);

// Preparing output
	result[0] = out;
 	result[1] = nextstate_c; 
//print_ar("Source_dyn_stat: result ",result);    
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

