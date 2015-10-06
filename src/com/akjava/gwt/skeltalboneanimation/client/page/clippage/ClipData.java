package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.lib.common.graphics.Rect;
import com.akjava.lib.common.graphics.Point;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

//TODO need id otherwise can't multi clip on bone.
public class ClipData {
public static int DEFAULT_EXPAND=64;
public static  int dotSize=10;

private String bone;
private List<Point> points=Lists.newArrayList();
public String getBone() {
	return bone;
}
public void setBone(String bone) {
	this.bone = bone;
}
public List<Point> getPoints() {
	return points;
}
public void setPoints(List<Point> points) {
	this.points = points;
}
public void addPoint(Point pt) {
	points.add(pt);
}

public Point collision(int sx,int sy){
	for(Point pt:getPoints()){
		Rect rect=Rect.fromCenterPoint(pt.getX(), pt.getY(), dotSize/2, dotSize/2);
		if(rect.contains(sx, sy)){
			return pt;
		}
	}
	return null;
}

//TODO find create uniq-id-way
public String getId(){
	
	String boneName= this.getBone()!=null?this.getBone():"";

	return (boneName+","+this.getBounds().toKanmaString()).replace(',', '_');
}

public Rect getPointBound(){
	return Rect.fromPoints(getPoints());
}
public Rect getBounds(){
	return Rect.fromPoints(getPoints()).expandSelf(expand, expand);
}
private int expand=DEFAULT_EXPAND;
public int getExpand() {
	return expand;
}
public void setExpand(int expand) {
	this.expand = expand;
}

public String toString(){
	List<String> values=Lists.newArrayList();
	values.add(bone!=null?bone:"");
	values.add(String.valueOf(expand));
	for(Point pt:points){
		values.add(pt.toString());
	}
	return Joiner.on(",").join(values);
}

private ImageDrawingData linkedImageDrawingData;//load from initial same id
public Optional<ImageDrawingData> getLinkedImageDrawingData() {
	return Optional.fromNullable(linkedImageDrawingData);
}
public void setLinkedImageDrawingData(ImageDrawingData linkedImageDrawingData) {
	this.linkedImageDrawingData = linkedImageDrawingData;
}
public ClipData copy(boolean deepCopy){
	ClipData data=new ClipData();
	copyTo(data,deepCopy);
	return data;
}
public void copyTo(ClipData data, boolean deepCopy) {
	if(deepCopy==true){
		throw new UnsupportedOperationException("clipdata:copyto(true) not support yet");
	}
	data.setBone(bone);
	data.setExpand(expand);
	
	
	data.setLinkedImageDrawingData(linkedImageDrawingData);
	data.getPoints().clear();
	for(Point pt:points){
		data.getPoints().add(pt);
	}
	
	
}

}
