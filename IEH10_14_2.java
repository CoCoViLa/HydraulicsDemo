// Class name:  IEH10_14_2
//               
// Description: hydraulic interface element (branching off) 
//		  4 connection at left, 8 connections at right
//		  universal form 
//	   
// Created:       01.12.2007
// Last modified: 25.01.2008 18.12.2012 24.02.2013
//
// Input and output variables (poles):
//	p1,p2,p3,p4,p5,p6 - pressures
//	Q1,Q2,Q3,Q4,Q5,Q6 - volumetric flows
// 
//------------------------------------------------------------------
class IEH10_14_2 {
    /*@ specification IEH10_14_2  {

      double  p1, p2, p3, p4, p5;
      double  Q1, Q2, Q3, Q4, Q5;

// Equations
	p1 = p5;
	p2 = p1;
	p3 = p2;
	p4 = p3;
	Q5 = Q1 - Q2 - Q3 - Q4 ;
  
    }@*/
}
