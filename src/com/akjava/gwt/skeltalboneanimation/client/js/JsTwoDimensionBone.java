package com.akjava.gwt.skeltalboneanimation.client.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
/**
 * @deprecated rebuild later

 */
public class JsTwoDimensionBone extends JavaScriptObject{
	
protected JsTwoDimensionBone(){}


public static final  native JsTwoDimensionBone create(String name,double x,double y)/*-{
return {name:name,x:x,y:y};
}-*/;

public final  native double getX()/*-{
return this.x;
}-*/;
public final  native void setX(double  param)/*-{
this.x=param;
}-*/;

public final  native double getY()/*-{
return this.y;
}-*/;
public final  native void setPositionY(double  param)/*-{
this.y=param;
}-*/;





public final  native String getName()/*-{
return this.name;
}-*/;
public final  native void setName(String  param)/*-{
this.name=param;
}-*/;


public final  native JsTwoDimensionBone getParent()/*-{
return this.parent;
}-*/;
public final  native void setParent(JsTwoDimensionBone  param)/*-{
this.parent=param;
}-*/;

public final  native boolean isRoot()/*-{
return this.parent==undefined;
}-*/;

public final  native void addBone(JsTwoDimensionBone  param)/*-{
if(!this.childrens){
this.childrens=[];	
}
this.childrens.push(param);
}-*/;



public final  native JsArray<JsTwoDimensionBone> getChildrens()/*-{
if(!this.childrens){
this.childrens=[];	
}
return this.childrens;
}-*/;
public final  native void setChildrens(JsArray<JsTwoDimensionBone>  param)/*-{
this.childrens=param;
}-*/;

}
