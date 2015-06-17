// Class name:  IEH8_13_2 
//               
// Description: hydraulic interface element (branching off) 
//		  1 connection at left, 3 connections at right
//		  universal form 
//	   
// Created:       01.12.2007
// Last modified: 25.01.2008 18.12.2012
//
// Input and output variables (poles):
//	p1,p2,p3,p4 - pressures
//	Q1,Q2,Q3,Q4 - volumetric flows
// 
//------------------------------------------------------------------
class IEH8_13_2 {
    /*@ specification IEH8_13_2 {

      double  p1, p2, p3, p4;
      double  Q1, Q2, Q3, Q4;

// Equations
	p2 = p1;
	p3 = p2;
	p4 = p3;
 	Q2 = Q1 - Q4 - Q3;
 
    }@*/
}



