package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.ArrayList;
import java.util.List;

public class TwoDimensionBone {
private double x;
private double y;
public TwoDimensionBone(String name,double x, double y,TwoDimensionBone parent) {
	super();
	this.x = x;
	this.y = y;
	this.name = name;
	this.parent=parent;
}
public TwoDimensionBone(String name,double x, double y){
	this(name,x,y,null);
}
private String name;
private TwoDimensionBone parent;
private List<TwoDimensionBone> childrens=new ArrayList<TwoDimensionBone>();
public TwoDimensionBone getParent() {
	return parent;
}
public void setParent(TwoDimensionBone parent) {
	this.parent = parent;
}
public List<TwoDimensionBone> getChildrens() {
	return childrens;
}
public void setChildrens(List<TwoDimensionBone> childrens) {
	this.childrens = childrens;
}
public double getX() {
	return x;
}
public void setX(double x) {
	this.x = x;
}
public double getY() {
	return y;
}
public void setY(double y) {
	this.y = y;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public boolean isRoot(){
	return parent==null;
}
public TwoDimensionBone addBone(TwoDimensionBone twoDimensionBone) {

	childrens.add(twoDimensionBone);
	twoDimensionBone.setParent(this);
	return twoDimensionBone;
}

}
