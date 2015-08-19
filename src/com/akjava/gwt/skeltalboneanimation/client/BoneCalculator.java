package com.akjava.gwt.skeltalboneanimation.client;

//same as bone?
public class BoneCalculator {
private String name;
public BoneCalculator(String name, double x, double y) {
	super();
	this.name = name;
	this.x = x;
	this.y = y;
}
private BoneCalculator parent;
public BoneCalculator getParent() {
	return parent;
}
public void setParent(BoneCalculator parent) {
	this.parent = parent;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
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
public double getAngle() {
	return angle;
}
public void setAngle(double angle) {
	this.angle = angle;
}
private double x;
private double y;
private double angle;
}
