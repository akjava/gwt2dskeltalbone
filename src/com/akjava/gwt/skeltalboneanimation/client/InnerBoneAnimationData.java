package com.akjava.gwt.skeltalboneanimation.client;

//almost same but has angle,usually create from framedata
public class InnerBoneAnimationData {
private String name;
public InnerBoneAnimationData(String name, double x, double y) {
	super();
	this.name = name;
	this.x = x;
	this.y = y;
}
private InnerBoneAnimationData parent;
public InnerBoneAnimationData getParent() {
	return parent;
}
public void setParent(InnerBoneAnimationData parent) {
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
