package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;

import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.lib.common.graphics.Rect;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

//TODO need id otherwise can't multi clip on bone.
public class ClipData {
public static int DEFAULT_EXPAND=64;
public static  int dotSize=10;

private String bone;
private List<PointXY> points=Lists.newArrayList();
public String getBone() {
	return bone;
}
public void setBone(String bone) {
	this.bone = bone;
}
public List<PointXY> getPoints() {
	return points;
}
public void setPoints(List<PointXY> points) {
	this.points = points;
}
public void addPoint(PointXY pt) {
	points.add(pt);
}

public PointXY collision(int sx,int sy){
	for(PointXY pt:getPoints()){
		Rect rect=Rect.fromCenterPoint(pt.getX(), pt.getY(), dotSize/2, dotSize/2);
		if(rect.contains(sx, sy)){
			return pt;
		}
	}
	return null;
}

public Rect getBound(){
	return Rect.fromPoints(getPoints());
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
	for(PointXY pt:points){
		values.add(pt.toString());
	}
	return Joiner.on(",").join(values);
}
}
