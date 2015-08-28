package com.akjava.gwt.skeltalboneanimation.client.bones;

public class BoneFrame {
private String boneName;
public BoneFrame(String boneName, int x, int y, double angle) {
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
public int getX() {
	return x;
}
public void setX(int x) {
	this.x = x;
}
public int getY() {
	return y;
}
public void setY(int y) {
	this.y = y;
}
private int x;
private int y;
private double angle;
public double getAngle() {
	return angle;
}
public void setAngle(double angle) {
	this.angle = angle;
}
}
