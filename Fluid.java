// Class name: Fluid
//              
// Description: oils 
//
//     temp    - temperature C  
//     vol     - volume of air in the fluid  
//     ka	 - polytrope exponent   
//     rho15   - fluid density at temperature 15 C
//     nue0    - fluid viscosity at temperature 40 C   
//     fl_type - type of the fluid   
//
//
// Created:       23.04.2009
// Last modified: 23.04.2009
//-----------------------------------------------------------------

class Fluid {

//=========================================================================
// Calculating fluid parameter fluid_nue0 mm2/s
//	fluid_type, fluid_temp -> fluid_nue0 {calc_nue0};
//---------------------------------------------------------------
//       | HLP10  HLP15  HLP22 HLP32	HLP46   HLP68  HLP100  |
//--------------------------------------------------------------|
//  -10  | 180    360     600	  1000      2000    4000    8000   |
//    0  |  60     95     190	   320	 570     990    1850   |
//   10  |  38     50     100	   180	 270     500     900   |
//   20  |  20     32      52	    90	 150     250     400   |
//   30  |  15     20      34	    51	  80     115     180   |
//   40  |  10     12      22	    32	  46      68     100   |
//   50  |  7.5    10.2    15	    22	  30      42      63   |
//   60  |  5.4     8      11	    15	  20      28      38   |
//---------------------------------------------------------------
//=========================================================================
//    public double calc_nue0 (String fluid_type, double fluid_temp) {
    static double calc_nue0 (String fluid_type, double fluid_temp) {
	double nue0;
	int k = 0;
       String[] fl_types = { "HLP10", "HLP15", "HLP22", "HLP32", 
			                           "HLP46", "HLP68", "HLP100"};
	double[] fl_temps  = { -10, 0, 10, 20, 30, 40, 50, 60 };
       double nue0_s[][] = { {180,  360,  600, 1000,  2000, 4000, 8000},
		                 { 60,  95,   190,  320,   570,  990, 1850	},
			           { 38,  50,   100,  180,   270,  500,  900	},
			           { 20,  32,   52,    90,   150,  250,  400	},
			           { 15,  20,   34,    51,    80,  115,  180	},
			           { 10,  12,   22,    32,    46,   68,  100	},
			           { 7.5, 10.2, 15,    22,    30,   42,   63	},
			           { 5.4, 8,    11,    15,    20,   28,   38	} };
	  nue0    = 46;
	  for (int i = 0;   i < fl_temps.length;   i++ ){
 	       if (fluid_temp == fl_temps[i]) {
		    k++;
		    for (int j = 0;   j < fl_types.length;   j++ ){ 
		          if (fluid_type == fl_types[j]) {
				k++;
				nue0 = nue0_s[i][j];
			   }
		    }
		}
	    }
	  if ( k < 2) {
           System.out.println ("");
	    System.out.println ( "******* Unknown fluid parameters  ******");
	 System.out.println ( "Fluid HLP46 at temperature 40 C is taken" );
           System.out.println ("");
}
 	  nue0 = nue0 / 1E6;
System.out.println ( "=== Fluid.calc_nue0:  fluid_type="+fluid_type+
		       "  fluid_temp="+ fluid_temp + "  nue0=" + nue0 );
	return nue0;
    }

//=========================================================================
// Calculating fluid parameter fluid_rho15
//		fluid_type -> fluid_rho15 {calc_rho15}; 
//---------------------------------------------------------------
//  HLP10	  HLP15   HLP22    HLP32   HLP46   HLP68	  HLP100
//		
//     863	   865     864      870     875     877	   881
//---------------------------------------------------------------
//=========================================================================
//    public double calc_rho15 (String fluid_type) {
    static double calc_rho15 (String fluid_type) {
	double rho15;
       String[] fl_types = { "HLP10", "HLP15", "HLP22", "HLP32", 
			        "HLP46", "HLP68", "HLP100"};
       double[] rho15_s  = { 863, 865, 864, 870, 875, 877, 881 };

   	    rho15 = 875;
 	    for (int i = 0;   i < fl_types.length;   i++ ){
 	        if (fluid_type == fl_types[i]) rho15 = rho15_s[i];
	    }
System.out.println ( "=== Fluid.calc_rho15: fluid_type="+fluid_type+
		       "  rho15=" + rho15 );
	return rho15;
    }

//=========================================================================
// Calculating fluid parameter fluid_Af
//		fluid_type -> fluid_Af {calc_Af}; 
//=========================================================================
//    public double calc_Af (String fluid_type, double fluid_temp) {
    static double calc_Af (String fluid_type, double fluid_temp) {
        double Af;
    	 if ((fluid_type == "HLP10")|(fluid_type == "HLP15")) {
	     Af   = 12.5 - 0.05 * (fluid_temp - 20); 
	 } else {
	     Af   = 14 - 0.1 * (fluid_temp - 20);  
	   }
        return Af;
    }
//=========================================================================
// Calculating fluid parameter fluid_Bf
//		fluid_type -> fluid_Bf {calc_Bf}; 
//=========================================================================
//    public double calc_Bf (String fluid_type, double fluid_temp) {
    static double calc_Bf (String fluid_type, double fluid_temp) {
	double Bf;
    	 if ((fluid_type == "HLP10")|(fluid_type == "HLP15")) {
	      Bf   = (16.5 - 0.08 * (fluid_temp - 20)) * 1E8;
	 } else {
	      Bf   = (18.4 - 0.10 * (fluid_temp - 20)) * 1E8;
	   }
	return Bf;
    }
}




