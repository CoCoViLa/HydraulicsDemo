// Class name:  IEH4_21_2 
//               
// Description: hydraulic interface element (branching off) 
//		  1 connection at left, 2 connections at right
//		  universal form 
//
// Input and output variables (poles):
//	p1,p2,p3 - pressures
//	Q1,Q2,Q3 - volume flows
//	   
// Created:       22.09.2008
// Last modified: 23.09.2008
//------------------------------------------------------------------

class IEH4_21_2 {
    /*@ specification IEH4_21_2 {

      double  p1, p2, p3;
      double  Q1, Q2, Q3;
	
// Equations
	p2 = p1;
	p3 = p2;
	Q2 = Q3 - Q1; 

    }@*/
}
