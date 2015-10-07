package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.List;

import com.akjava.lib.common.graphics.Point;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class BoneFrame {
private String boneName;
public BoneFrame(String boneName, double x, double y, double angle) {
	super();
	this.boneName = boneName;
	this.x = x;
	this.y = y;
	this.angle = angle;
}
public String toString(){
	List<String> arrays=Lists.newArrayList();
	arrays.add(boneName);
	arrays.add(String.valueOf(x));
	arrays.add(String.valueOf(y));
	arrays.add(String.valueOf(angle));
	return Joiner.on(",").join(arrays);
}
public BoneFrame(String name) {
	this(name,0,0,0);
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
public BoneFrame copy(){
	return new BoneFrame(boneName,x,y,angle);
}

public boolean isAllZeroFrame(){
	return x==0 && y==0 && angle==0;
}

//TODO convert to Double
public void setPosition(Point position) {
	this.x=(int)position.x;
	this.y=(int)position.y;
}
}
