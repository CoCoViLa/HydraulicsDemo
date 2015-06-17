// Class name:  Clock   
//             
// Description: timer
//
// Output variables (poles):
//     out - output 
//
// State components:
//     1:  out
//	   
// Created:       01.12.2007
// Last modified: 25.01.2008
//------------------------------------------------

class Clock {
    /*@	 specification Clock {

      double	tau;
      double 	out;
// State variables
      double 	initstate_c, oldstate_c, state_c; 
      double	nextstate_c, finalstate_c; 
	 alias (double) initstate  = (initstate_c);
	 alias (double) oldstate   = (oldstate_c);
	 alias (double) state      = (state_c);
	 alias (double) nextstate  = (nextstate_c);
	 alias (double) finalstate = (finalstate_c);
// Evaluating initstate components
      initstate_c = 0;
// Equations
      out = state_c;
// Method specification
      tau, state_c -> nextstate_c {clock_next};

    }@*/

//================================================
//   tau, state_c -> nextstate_c { clock_next };
//================================================
    public double clock_next ( double tau, 
				  double st) {
      double  nst; 
	if ( tau == 0 ) { nst = st; }
	   else  { nst = st + 1/tau; }	 
       return nst;
    }
}


