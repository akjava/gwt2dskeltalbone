package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;

public class ImageDrawingData {
private ImageElement imageElement;
public ImageDrawingData(ImageElement imageElement) {
	super();
	this.imageElement = imageElement;
}

private double alpha=1;
private int x;
private int y;
private double angle;
public ImageElement getImageElement() {
	return imageElement;
}

public void setImageElement(ImageElement imageElement) {
	this.imageElement = imageElement;
}

public double getAlpha() {
	return alpha;
}

public void setAlpha(double alpha) {
	this.alpha = alpha;
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

public double getAngle() {
	return angle;
}

public void setAngle(double angle) {
	this.angle = angle;
}

public double getScaleX() {
	return scaleX;
}

public void setScaleX(double scaleX) {
	this.scaleX = scaleX;
}

public double getScaleY() {
	return scaleY;
}

public void setScaleY(double scaleY) {
	this.scaleY = scaleY;
}

private double scaleX=1;
private double scaleY=1;

/*
 * draw at center
 */
public void draw(Canvas canvas){
	CanvasUtils.drawCenter(canvas, imageElement,x,y,scaleX,scaleY,angle,alpha);
}
}
