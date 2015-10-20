package com.akjava.gwt.skeltalboneanimation.client.bones;

import com.akjava.gwt.lib.client.LogUtils;
import com.google.gwt.canvas.client.Canvas;

public class CanvasBoneSettings implements BoneSettings{
private Canvas canvas;
private TwoDimensionBone bone;
private int offsetX;
private int offsetY;
public int getOffsetX() {
	return offsetX;
}
public void setOffsetX(int offsetX) {
	LogUtils.log("CanvasBoneSettings:not support");
	//this.offsetX = offsetX;
}
public int getOffsetY() {
	return offsetY;
}
public void setOffsetY(int offsetY) {
	LogUtils.log("CanvasBoneSettings:not support");
	//this.offsetY = offsetY;
}
public CanvasBoneSettings(Canvas canvas, TwoDimensionBone bone) {
	super();
	this.canvas = canvas;
	this.bone = bone;
	updateOffset();
}
public void updateOffset(){
	offsetX=canvas.getCoordinateSpaceWidth()/2;
	offsetY=canvas.getCoordinateSpaceHeight()/2;
}
public Canvas getCanvas() {
	return canvas;
}
public void setCanvas(Canvas canvas) {
	this.canvas = canvas;
	updateOffset();
}
public TwoDimensionBone getBone() {
	return bone;
}
public void setBone(TwoDimensionBone bone) {
	this.bone = bone;
}
}
