// Class name: Flow
//              
// Description: oils properties in dependence of the arithmetical
//	      medial pressure pfl at the ends of element;
//	      flow characteristics RL, RT, C and L for tubes
//	      and RL, RT and L for local hydraulic resistances
//
// Input variables: 
//     temp    - temperature C  
//     pfl	   - pressure for computing the fluid properties 
//     vol     - volume of air in the fluid  
//     ka	   - polytrope exponent   
//     rho15   - fluid density at temperature 15 C
//     nue0    - fluid viscosity at temperature 40 C   
//     fl_type - type of the fluid   
//
// Output variables: 
//     betam   - compressibility factor of fluid with air  
//     rho	   - fluid density at given temperature and pressure 
//     nue	   - fluid viscosity at given temperature and pressure
//
// Created:       16.04.2009
// Last modified: 06.05.2009
//-----------------------------------------------------------------

class Flow {
   /*@ specification Flow {

     double  temp;
     double  pfl;
     double  y;
     double  y0, dr, d1;
     double  vol, ka;
     double  rho15, nue0;
     String  fl_type;
     String  res_type;

     double  RL, RT, L, C;
     double  dlt, kE, mu, dltpN, QN, A;  
     double  Af, Bf, K, s;
     double  betaf, betaa, betam, betat, alfa, rho, nue;
     double  AL, len, diam, zeta, zetaa, B, lambda;		

// Fluid parameters
   	alias (double) fluid_par = (temp, vol, ka, nue0, rho15, Af, Bf);
//  Tube parameters
   	alias (double) tube_par = (len, diam, K, s);
//  Hydraulic esistor parameters
   	alias (double) res_par=(len,diam,dlt,kE,mu,dltpN,QN,A);
//  Results
	alias (double) tube_result = (RL, RT, L, C);
	alias (double) res_result  = (RL, RT, L);

// Relations
	alfa = (4 + 0.1 * temp) * 0.0001; 		   
 	pfl, Af, Bf   -> betaf  {comp_betaf};
	pfl, vol, ka -> betaa {comp_betaa};
	betam = betaf + betaa;  
	pfl, rho15, alfa, temp, betam -> rho {comp_rho};
	pfl, nue0, vol, temp, betam -> nue  {comp_nue};
	zeta -> zetaa { comp_zetaa };

// For tubes
	AL, lambda, zetaa, betam,
	nue, rho, len, diam, K, s  -> tube_result {comp_RLRTLC};

// For resistors
	AL, lambda, zeta, 
	zetaa, nue, rho,
	res_type, len, diam,
	dlt, kE, mu, dltpN,
	QN, A           -> res_result {comp_RLRTL};

// For resistor (circular radial slot)
	AL, lambda, zeta, 
	zetaa, nue, rho,
	dr, d1, y0,
       len, diam,
	dlt, kE, mu, dltpN,
	QN, A, y        -> res_result {comp_RLRTL_rad};

// Default parameter values	
	AL     = 75;
	lambda = 0.04;
	zeta   = 2;
	y0     = 1e-3;
	dr     = 0.015;
	d1     = 0.005;
    }@*/

//=================================================================
// For hydraulic resistors
//	AL, lambda, zeta, 
//	zetaa, nue, rho,
//	res_type, len, diam,
//	dlt, kE, mu, dltpN,
//	QN, nuN, rhoN, A     -> res_result {comp_RLRTL_rad};
//
//=================================================================
    public double[] comp_RLRTL_rad (double AL, double lambda, 
			 double zeta, double zetaa, 
			 double nue, double rho, 
			 double dr, double d1,
			 double y0, double kE, double mu,
			 double dltpN, double QN,
			 double A, double y) {
      double[]  result = new double[3];
      double RL, RT, B, L, AA;
      double ll, deltat, dd;
      RL = 0;
      RT = 0;
      L  = 0;

// Circular radial slot  (res_type == "RRf")
	   ll = (dr-d1)/2;
	   deltat = y0+y;
	   dd = (dr+d1)/2;
	   RL = 12 * nue * rho * ll/(Math.PI * Math.pow(deltat,3)* dd * kE);  
	   RT = rho/2/(Math.pow((mu*Math.PI*dd*deltat*kE),2));  
	   L  = rho*ll/(Math.PI*dd*deltat*kE);

//System.out.println ("Flow:  RL="+RL+" RT="+RT+" L="+L);
//System.out.println ("Flow:  res_type"+res_type);
// Preparing output
	result[0] = RL;
	result[1] = RT;
	result[2] = L;
     return result;
    }	

//=================================================================
// For hydraulic tubes
//  AL,lambda,zetaa,betam,nue,rho,len,diam,K,s 
//    			-> tube_result {comp_RLRTLC};
//
//=================================================================
    public double[] comp_RLRTLC (double AL, double lambda, 
			  double zetaa, double betam, 
			  double nue, double rho, double l, 
			  double d, double E, double s) {
      double[]  result = new double[4];
      double RL, RT, B, L, C, betat;

	 RL = 2 * AL * l * nue * rho / Math.PI / Math.pow(d,4); 

	   B = zetaa + (lambda * l )/ d; 

       RT = 8 * rho / (Math.pow(Math.PI,2) * Math.pow(d,4)) * B; 

	   L  = 4 * rho * l / (Math.PI * Math.pow(d,2)); 

  	   betat = 2/E*(( Math.pow((d/2+s),2) + Math.pow((d/2),2)) /
		   (Math.pow((d/2+s),2) - Math.pow((d/2),2)) + 0.3);

	 C = l * Math.PI * Math.pow(d,2) / 4 * (betam + betat); 

// Preparing output
	result[0] = RL;
	result[1] = RT;
	result[2] = L;
	result[3] = C;
     return result;
    }	

//=================================================================
// For hydraulic resistors
//	AL, lambda, zeta, 
//	zetaa, nue, rho,
//	res_type, len, diam,
//	dlt, kE, mu, dltpN,
//	QN, nuN, rhoN, A     -> res_result {comp_RLRTL};
//
//=================================================================
    public double[] comp_RLRTL (double AL, double lambda, 
			 double zeta, double zetaa, 
			 double nue, double rho, 
			 String res_type,
			 double l, double d,
			 double dlt, double kE, double mu,
			 double dltpN, double QN,
			 double A) {
      double[]  result = new double[3];
      double RL, RT, B, L, AA;
      RL = 0;
      RT = 0;
      L  = 0;

// Circular axial slot
      if (res_type == "RRb") {
	   RL = 12*nue*rho*l/(Math.PI* Math.pow(dlt,3) *d*kE);  
	   RT = rho/2/  Math.pow((mu*Math.PI*d*dlt*kE),2);  
	   L  = rho*l/(Math.PI*d*dlt*kE);
      } else

// Round orifice 
        if (res_type == "RRc") {
	     RL = 0; 
	     RT = 8*rho/(mu*mu*Math.PI*Math.PI* Math.pow(d,4)); 
	     L  = 4*rho*l/(Math.PI * Math.pow(d,2)); 
        } else

// Not round orifice  
          if (res_type == "RRd") {
	       RL = 0; 
	       RT = rho/( 2* Math.pow(mu,2) * Math.pow(A,2) );
	       L  = rho*l/A; 
          } else

// Local hydraulic resistance
            if (res_type == "RRe") {
	         RL = 0; 
	         RT = 8 * zeta * rho /(Math.PI * Math.PI * Math.pow(d,4));
	         L  = 4 * rho * l /(Math.PI * Math.pow(d,2)); 
            } else

// Hydraulic device with linear resistance
              if (res_type == "RRg") {
	   	 RL = dltpN/QN;  
	           RT = 0; 
		    AA = QN/dltpN;		  
	           L  = rho*l/AA; 
              }  else

// Hydraulic device with square resistance 
      		   if (res_type == "RRh") {
	   		RL = 0;  
	   		RT = dltpN/QN/QN; 
		       AA = QN/mu/Math.sqrt(2/rho*dltpN);
	   		L  = rho*l/AA; 
      		   } else
// Round channel 
      		     if (res_type == "RRa") {	   
	   	      RL = 2 * AL * l * nue * rho / Math.PI / d*d*d*d; 
	   	          B  = zetaa+lambda*l/d;          
          		      RT = 8*rho/(Math.PI*Math.PI*d*d*d*d)*B;     
	   	      L  = 4*rho*l/(Math.PI*d*d);  
      		     } 
//System.out.println ("Flow:  RL="+RL+" RT="+RT+" L="+L);
//System.out.println ("Flow:  res_type"+res_type);
// Preparing output
	result[0] = RL;
	result[1] = RT;
	result[2] = L;
     return result;
    }	

//=================================================================
//
//=================================================================
    public double comp_betaa ( double pfl, double vol, double ka){
      double betaa;
	if (pfl < -9e4) {
  	   betaa = vol/ka/(-9e4 + 1e5) * (1e5/(-9e4 + 1e5)); 
	}   
	 else  { 
	    betaa = vol/ka/(pfl + 1e5) * (1e5 / (pfl + 1e5)); 
	 }    	 
	return betaa;
    }

//=================================================================
//
//=================================================================
    public double comp_rho ( double pfl, double rho15, double alfa,
		         double temp, double betam) {
      double rho;
	if (pfl <= 0)  { 
	   rho = rho15/(1. + alfa*(temp - 15)); 
	}   
	 else  {  
	   rho = rho15/(1 + alfa*(temp-15))*(1+betam * pfl);
	 } 
	return rho;
    }

//=================================================================
//
//=================================================================
    public double comp_nue ( double pfl, double nue0, double vol,
		         double temp, double betam) {
      double nue;
	if (pfl <= 0)  { 
	    nue = nue0 * (1 + 1.5 * vol); 
	}   
	 else  { 
	    nue = nue0 * (1 + 1.5 * vol) * (1 + 3e-8 * pfl); 
	 }         
		
 	if (((nue - nue0)/nue) > 0.4) { 
	    nue = 2.5 * nue0; 
	} 
	 else  { 
	    nue = nue0 * (1 + 1.5 * vol) * (1 + 3e-8 * pfl); 
	 }
	return nue;
    }

//=================================================================
//
//=================================================================
    public double comp_zetaa ( double zeta ) {
      double zetaa;
	if (zeta == 0) { zetaa = 0; }    
	  else {zetaa = 1+zeta; } 
       return zetaa;
    }

//=================================================================
//
//=================================================================
    public double comp_betaf  (double pfl, double Af, double Bf) {
        double betaf;
	if (pfl <= 0) { betaf = 1/Bf; }
	  else { betaf = 1/(Af*pfl + Bf); }
	return betaf;
    }	
}


