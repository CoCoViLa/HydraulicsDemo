// Class name:  IEH6_22 
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
class IEH6_22 {
    /*@ specification IEH6_22  {

      double  p1, p2, p3, p4;
      double  Q1, Q2, Q3, Q4;

// Equations
	p2 = p1;
	p3 = p2;
	p4 = p3;
	Q1 = Q3 + Q4 - Q2;

    }@*/
}



