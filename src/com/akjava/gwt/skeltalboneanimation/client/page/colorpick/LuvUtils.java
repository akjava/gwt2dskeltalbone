package com.akjava.gwt.skeltalboneanimation.client.page.colorpick;

import com.google.gwt.core.client.JsArrayNumber;

public class LuvUtils {

	public static final native void toLuv(JsArrayNumber rgb,JsArrayNumber luv)/*-{
		var xyy=[];
		$wnd.toxyY(rgb,xyy);
		$wnd.toLuv(xyy,luv);
		
		//$wnd.XYZUtils.toxyY(rgb,xyy);
		//$wnd.XYZUtils.toLuv(xyy,luv);
		
	}-*/;
	public static final native void toXyy(JsArrayNumber rgb,JsArrayNumber luv)/*-{
	var xyy=[];
	$wnd.toxyY(rgb,luv);
	//$wnd.toLuv(xyy,luv);
	
	//$wnd.XYZUtils.toxyY(rgb,xyy);
	//$wnd.XYZUtils.toLuv(xyy,luv);
	
}-*/;
	
	
	public static final double MIN_L=0;
	public static final double MIN_U=-23.125250072240018;
	public static final double MIN_V=-53.394529257344075;
	
	public static final double MAX_L=8.991442404369852;
	public static final double MAX_U=6.428740983009679;
	public static final double MAX_V=3.701130010404481;
	
	public static double normalizeL(double l){
		return l/((MAX_L-MIN_L)/2)-1;
	}
	public static double normalizeU(double u){
		return u/((MAX_U-MIN_U)/2)-1;
	}
	public static double normalizeV(double v){
		return v/((MAX_V-MIN_V)/2)-1;
	}
	/**
	 * 
	 * value range 
	 L:0,8.991442404369852
	 U:-23.125250072240018,6.428740983009679
	 V:-53.394529257344075,3.701130010404481
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	
	 public static final double[] toLuv(double r,double g,double b){
		JsArrayNumber rgb=JsArrayNumber.createArray().cast();
		rgb.push(r);
		rgb.push(g);
		rgb.push(b);
		JsArrayNumber luv=JsArrayNumber.createArray().cast();
		toLuv(rgb,luv);
		
		return toLab(r,g,b);//
		//return new double[]{luv.get(0),luv.get(1),luv.get(2)};
	}
	 
	
	/**
	 * luv is useless?
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static final double[] toLab(double r,double g,double b){
		JsArrayNumber rgb=JsArrayNumber.createArray().cast();
		rgb.push(r/255);
		rgb.push(g/255);
		rgb.push(b/255);
		JsArrayNumber luv=JsArrayNumber.createArray().cast();
		toXyy(rgb,luv);
		
		return xyzToLab(luv.get(0),luv.get(1),luv.get(2));
	}
	
	public static final double ref_X= 95.047;
	public static final double ref_Y = 100.000;
	public static final double ref_Z = 108.883;
	//from here
	//http://www.easyrgb.com/index.php?X=MATH&H=07#text7
	public static final double[] xyzToLab(double X,double Y,double Z){
				double var_X = X / ref_X  ;        //ref_X =  95.047   Observer= 2°, Illuminant= D65
				double var_Y = Y / ref_Y  ;        //ref_Y = 100.000
				double var_Z = Z / ref_Z  ;        //ref_Z = 108.883

				
				if ( var_X > 0.008856 ) var_X = Math.pow(var_X , ( 1.0/3 ));
				else                    var_X = ( 7.787 * var_X ) + ( 16 / 116 );
				if ( var_Y > 0.008856 ) var_Y = Math.pow(var_Y , ( 1.0/3 ));
				else                    var_Y = ( 7.787 * var_Y ) + ( 16 / 116 );
				if ( var_Z > 0.008856 ) var_Z = Math.pow(var_Z , ( 1.0/3 ));
				else                    var_Z = ( 7.787 * var_Z ) + ( 16 / 116 );

				double l = ( 116 * var_Y ) - 16;
				double a = 500 * ( var_X - var_Y );
				double b = 200 * ( var_Y - var_Z );
				
				return new double[]{l,a,b};
		
	}
	
	
}
