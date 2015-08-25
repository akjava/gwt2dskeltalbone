package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.lib.common.graphics.Rect;
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

public int incrementX(int mx){
	this.x+=mx;
	return this.x;
}
public int incrementY(int my){
	this.y+=my;
	return this.y;
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
//ThreePointImageCustomAnimation.drawImageAt(canvas, imageElement, x, y, imageElement.getWidth()/2, imageElement.getHeight()/2, angle,scaleX,scaleY);
	CanvasUtils.drawCenter(canvas, imageElement,x-canvas.getCoordinateSpaceWidth()/2,y-canvas.getCoordinateSpaceHeight()/2,scaleX,scaleY,angle,alpha);
}

public void incrementAngle(int vectorX) {
	angle+=vectorX;
	angle=angle%360;
	if(angle<0){
		angle=360+angle;
	}
}
PointXY[] result;
Rect rect;
public PointXY[] getCornerPoint(){
	if(result==null){
		result=new PointXY[4];
		for(int i=0;i<4;i++){
			result[i]=new PointXY(0, 0);
		}
	}
	int iw=imageElement.getWidth();
	int ih=imageElement.getHeight();
	int iws=(int) (scaleX*iw);
	int ihs=(int) (scaleY*ih);
	result[0].set(-iws/2, -ihs/2);
	BoneUtils.turnedAngle(result[0], angle);
	result[0].incrementXY(x, y);
	
	result[1].set(iws/2, -ihs/2);
	BoneUtils.turnedAngle(result[1], angle);
	result[1].incrementXY(x, y);
	
	result[3].set(-iws/2, ihs/2);
	BoneUtils.turnedAngle(result[3], angle);
	result[3].incrementXY(x, y);
	
	result[2].set(iws/2, ihs/2);
	BoneUtils.turnedAngle(result[2], angle);
	result[2].incrementXY(x, y);
	
	
	
	return result;
}
private Rect bounds;
public Rect getBounds() {
	if(bounds==null){
		updateBounds();
	}
	return bounds;
}

public void setBounds(Rect bounds) {
	this.bounds = bounds;
}
public void updateBounds(){
	bounds=calculateBounds();
}

private boolean useBoundsForCollision=false;
public boolean collision(int screenX,int screenY){
	if(useBoundsForCollision){
	return getBounds().contains(screenX, screenY);
	}else{
		
		int offx=screenX-x;
		int offy=screenY-y;
		
		double[] turnedCordinates=BoneUtils.turnedAngle(offx,offy, -angle);
		
		
		int iw=imageElement.getWidth();
		int ih=imageElement.getHeight();
		int iws=(int) (scaleX*iw);
		int ihs=(int) (scaleY*ih);
		Rect r=new Rect(x-iws/2,y-ihs/2,iws,ihs);
		return r.contains((int)turnedCordinates[0]+x, (int)turnedCordinates[1]+y);
	}
}

public Rect calculateBounds(){
	if(rect==null){
		rect=new Rect();
	}
	int minX=Integer.MAX_VALUE;int minY=Integer.MAX_VALUE;int maxX=Integer.MIN_VALUE;int maxY=Integer.MIN_VALUE;
	
	PointXY[] corners=getCornerPoint();
	for(PointXY pt:corners){
		if(pt.getX()<minX){
			minX=pt.getX();
		}
		if(pt.getY()<minY){
			minY=pt.getY();
		}
		if(pt.getX()>maxX){
			maxX=pt.getX();
		}
		if(pt.getY()>maxY){
			maxY=pt.getY();
		}
	}
	int w=maxX-minX;
	int h= maxY-minY;
	
	rect.set(minX, minY, w,h);
	return rect;
}
}
