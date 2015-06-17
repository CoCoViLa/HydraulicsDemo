// Class name:  Lss 
//               
// Description: Methods for load sensing system simulation system 
//
//	   
// Created:       02.03.2008
// Last modified: 25.01.2008
//----------------------------------------------------------------

import java.text.*;
public class HydUtil {

//===========================================================================
//	state_name, state -> done_print_state  { print_state };
//===========================================================================
    static void print_state (String state_name, double[][] st) {
        DecimalFormat dF = new DecimalFormat("0.###E0");
	System.out.print(state_name + ":");
	for (int i = 0; i < st.length;  i++ ){
  	    if (st[i].length > 1 ) System.out.print(" (");
	    for (int j = 0; j < st[i].length;  j++ ){
	        System.out.print ( " " + dF.format(st[i][j]) + " ");
	    }
	    if (st[i].length > 1 ) System.out.print(") ");
	}
	System.out.println();
    }

//===========================================================================
// 	state_name, state -> done_print_state  { print_state_2 };
//===========================================================================
    static void print_state_2 (String state_name, double[][] st) {
      DecimalFormat dF_num = new DecimalFormat("#.####");
      DecimalFormat dF_sci = new DecimalFormat("0.###E0");
	System.out.println("");
	System.out.println(state_name + ":");
	for (int i = 0; i < st.length;  i++ ){
	   if (i < 9) System.out.print(" ");
	   System.out.print((i+1)+":");
	   for (int j = 0; j < st[i].length;  j++ ){
	      if ((st[i].length > 1)&&(j==0)) System.out.print(" (");
	      if ((st[i].length > 1)&&(j>0)) System.out.print("     ");

	      if ( (Math.abs(st[i][j]) > 0.01) && (Math.abs(st[i][j]) < 10000))
    		  {System.out.print ( " " + dF_num.format(st[i][j]) + " "); }
		else 
    		  {System.out.print ( " " + dF_sci.format(st[i][j]) + " "); }  

	      if ((st[i].length > 1)&&(j==st[i].length-1)) 
				    System.out.print(" )");
	      System.out.println();
	   }
	}
    }

//===========================================================================
//  	state_name, state -> done_print_state  { print_state_3 };
//===========================================================================
    static void print_state_3 (String state_name, double[][][] st) {
        DecimalFormat dF = new DecimalFormat("0.###E0");
        System.out.println("");
        System.out.println(state_name + ":");
        for (int i = 0; i < st.length;  i++ ){
	if (i < 9) System.out.print(" ");
	System.out.print((i+1)+":");
	for (int j = 0; j < st[i].length;  j++ ){
	  if ((st[i].length > 1)&&(j==0)) System.out.print(" (");
	  if ((st[i].length > 1)&&(j>0)) System.out.print("     ");
	  if (j < 9) System.out.print(" ");
	  System.out.print((j+1)+":");
	  for (int k = 0; k < st[i][j].length;  k++ ){
	    if ((st[i][j].length > 1)&&(k==0)) System.out.print(" (");
	    if ((st[i][j].length > 1)&&(k>0)) System.out.print("          ");
	    System.out.print ( " " + dF.format(st[i][j][k]) + " ");
	    if ((st[i][j].length > 1)&&(k==st[i][j].length-1)) 
				    System.out.print(" )");
	    if ((st[i].length > 1)&&(j==st[i].length-1)) 
				    System.out.print(" )");
	    System.out.println();
	  }
	}
        }
    }

//===========================================================================
//         errorap, errorr, finalstate -> 
//			printing_ready { printErrorValues }; 
//===========================================================================
    static double printErrorValues ( double[][] errorap,
			      double[][] errorr, 
			      double[][] finalstate ) {
        print_state_2 ("====== Absolute error values", errorap);
        print_state_2 ("====== Relative error values", errorr);
        return (double) 1;
    }

//================================================================
//	void print_ar (String ar_name, double[] ar)
//================================================================
//    public void print_ar (String ar_name, double[] ar) {
    static void print_ar (String ar_name, double[] ar) {
      DecimalFormat dF = new DecimalFormat("0.###E0");
	System.out.print(ar_name + ":");
	for (int i = 0; i < ar.length;  i++ ) {
	        System.out.print("  " + dF.format(ar[i]));
	}
	System.out.println();
    }  
}


