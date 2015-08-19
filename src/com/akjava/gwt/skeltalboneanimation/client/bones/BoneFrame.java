package com.akjava.gwt.skeltalboneanimation.client.bones;

public class BoneFrame {
private String boneName;
public BoneFrame(String boneName, double x, double y, double angle) {
	super();
	this.boneName = boneName;
	this.x = x;
	this.y = y;
	this.angle = angle;
}
public String getBoneName() {
	return boneName;
}
public void setBoneName(String boneName) {
	this.boneName = boneName;
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
private double x;
private double y;
private double angle;
public double getAngle() {
	return angle;
}
public void setAngle(double angle) {
	this.angle = angle;
}
}
