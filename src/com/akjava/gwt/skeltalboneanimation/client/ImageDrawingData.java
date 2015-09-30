package com.akjava.gwt.skeltalboneanimation.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.lib.common.graphics.IntRect;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Joiner;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;

public class ImageDrawingData {
private ImageElement imageElement;
private boolean flipVertical;
private boolean flipHorizontal;
private boolean visible=true;
private String imageName;
public String getImageName() {
	return imageName;
}

public void setImageName(String imageName) {
	this.imageName = imageName;
}

public boolean isVisible() {
	return visible;
}

public void setVisible(boolean visible) {
	this.visible = visible;
}

public boolean isFlipVertical() {
	return flipVertical;
}

public void setFlipVertical(boolean flipVertical) {
	this.flipVertical = flipVertical;
}

public boolean isFlipHorizontal() {
	return flipHorizontal;
}

public void setFlipHorizontal(boolean flipHorizontal) {
	this.flipHorizontal = flipHorizontal;
}

/**
 * 
 * @param id must be uniq,create from time or control image-file-name
 * @param imageElement
 */
public ImageDrawingData(String id,ImageElement imageElement) {
	super();
	this.imageElement = imageElement;
	this.id=id;
}
private String id;
public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}
private double alpha=1;
private int x;
private int y;
private double angle;

private String boneName;
public String getBoneName() {
	return boneName;
}

public void setBoneName(String boneName) {
	this.boneName = boneName;
}

public String toString(){
	List<String> values=new ArrayList<String>();
	if(id==null){
	values.add("");	
	}else{
	values.add(id);
	}
	
	values.add(String.valueOf(x));
	values.add(String.valueOf(y));
	values.add(String.valueOf(angle));
	values.add(String.valueOf(scaleX));
	values.add(String.valueOf(scaleY));
	values.add(String.valueOf(alpha));
	
	return Joiner.on(",").join(values);
}

/*
 * name,x,y,angle,scaleX,scaleY,alpha
 * imageElement must set by manual
 */
public static ImageDrawingData createFromCsv(String csv){
	ImageDrawingData data=new ImageDrawingData(null,null);
	if(csv==null){
		return data;
	}
	String values[]=csv.split(",");
	data.setId(values[0]);
	if(values.length>1){
		data.setX(ValuesUtils.toInt(values[1], 0));
	}
	if(values.length>2){
		data.setY(ValuesUtils.toInt(values[2], 0));
	}
	if(values.length>3){
		data.setAngle(ValuesUtils.toDouble(values[3], 0));
	}
	if(values.length>4){
		data.setScaleX(ValuesUtils.toDouble(values[4], 1));
	}
	if(values.length>5){
		data.setScaleY(ValuesUtils.toDouble(values[5], 1));
	}
	if(values.length>6){
		data.setAlpha(ValuesUtils.toDouble(values[6], 0));
	}
	return data;
}

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
private  Canvas workingCanvas;
public   Canvas getWorkingCanvas(){
	if(workingCanvas==null){
		workingCanvas=Canvas.createIfSupported();
	}
	return workingCanvas;
}

public void drawBorder(Canvas canvas,String color){
	CanvasUtils.draw(canvas,this.getCornerPoint(),true,color);
}

public void draw(Canvas canvas){
	if(imageElement==null){
		LogUtils.log(id+" no image-element");
		return;
	}
//ThreePointImageCustomAnimation.drawImageAt(canvas, imageElement, x, y, imageElement.getWidth()/2, imageElement.getHeight()/2, angle,scaleX,scaleY);
if(flipHorizontal || flipVertical){
	Canvas flipped=ImageElementUtils.flip(imageElement, flipHorizontal, flipVertical, getWorkingCanvas());
	CanvasUtils.drawCenter(canvas, flipped.getCanvasElement(),x-canvas.getCoordinateSpaceWidth()/2,y-canvas.getCoordinateSpaceHeight()/2,scaleX,scaleY,angle,alpha);
}else{
	CanvasUtils.drawCenter(canvas, imageElement,x-canvas.getCoordinateSpaceWidth()/2,y-canvas.getCoordinateSpaceHeight()/2,scaleX,scaleY,angle,alpha);
	}
}

/*
 * why need canvas?
 * re-calcuate scale or turn-angle is hard to do
 */
private Canvas convertedCanvas;
public Canvas convertToCanvas(){
	
	if(convertedCanvas==null){
		convertedCanvas=Canvas.createIfSupported();
	}
	
	IntRect bounds=getBounds();
	
	CanvasUtils.setSize(convertedCanvas, bounds.getWidth(), bounds.getHeight());
	
	//draw(canvas);
	if(flipHorizontal || flipVertical){
		Canvas flipped=ImageElementUtils.flip(imageElement, flipHorizontal, flipVertical, getWorkingCanvas());
		CanvasUtils.drawCenter(convertedCanvas, flipped.getCanvasElement(),0,0,scaleX,scaleY,angle,alpha);
		}else{
		CanvasUtils.drawCenter(convertedCanvas, imageElement,0,0,scaleX,scaleY,angle,alpha);}
	

	
	return convertedCanvas;
}

public void incrementAngle(int vectorX) {
	angle+=vectorX;
	angle=angle%360;
	if(angle<0){
		angle=360+angle;
	}
}
PointXY[] result;
IntRect rect;
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
private IntRect bounds;
public IntRect getBounds() {
	if(bounds==null){
		updateBounds();
	}
	return bounds;
}

public void setBounds(IntRect bounds) {
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
		IntRect r=new IntRect(x-iws/2,y-ihs/2,iws,ihs);
		return r.contains((int)turnedCordinates[0]+x, (int)turnedCordinates[1]+y);
	}
}

/**
 * watch out id is same as original ,must change by self
 * @return
 */
public ImageDrawingData copy(){
	ImageDrawingData data=new ImageDrawingData(getId(),ImageElementUtils.copy(getImageElement()));
	 copyTo(data);
	 return data;
}
public void copyTo(ImageDrawingData container){
	if(container==this){
		//no need to copy
		return;
	}
	container.setImageName(imageName);
	container.setBoneName(getBoneName());
	container.setX(x);
	container.setY(y);
	container.setAngle(angle);
	container.setScaleX(scaleX);
	container.setScaleY(scaleY);
	container.setAlpha(alpha);
	
	container.setFlipHorizontal(flipHorizontal);
	container.setFlipVertical(flipVertical);
	container.setVisible(visible);
	
}

public IntRect calculateBounds(){
	if(rect==null){
		rect=new IntRect();
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
