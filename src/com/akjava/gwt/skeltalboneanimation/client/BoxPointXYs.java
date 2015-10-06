package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.game.PointXY;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;

public class BoxPointXYs {
List<PointXY> points=Lists.newArrayList();

public List<PointXY> getPoints() {
	return points;
}
public void setPoints(List<PointXY> points) {
	this.points = points;
}
public BoxPointXYs(PointXY p1,PointXY p2,int width){
	this(p1.x,p1.y,p2.x,p2.y,width);
}
public BoxPointXYs(int x1,int y1,int x2,int y2,int width){
	double a=BoneUtils.calculateAngle(x1, y1, x2, y2);
	int direct=(int) Math.toDegrees(a);
	int right=direct+90;
	double[] pt=BoneUtils.turnedAngle(0, -width, right);
	
	
	points.add(new PointXY(x2+(int)-pt[0],y2+(int)-pt[1]));
	points.add(new PointXY(x2+(int)pt[0],y2+(int)pt[1]));
	
	
	points.add(new PointXY(x1+(int)pt[0],y1+(int)pt[1]));
	points.add(new PointXY(x1+(int)-pt[0],y1+(int)-pt[1]));
	
	
	//TODO control by method,at lease insert side
	//top curve
	PointXY p1=points.get(0);
	PointXY p2=points.get(1);
	PointXY b1=p1.between(p2);
	double a1=BoneUtils.calculateAngle(p1.x, p1.y, p2.x, p2.y);
	int direct1=(int) Math.toDegrees(a1);
	int right1=direct1-90;
	double[] pt1=BoneUtils.turnedAngle(0, -width, right1);
	
	points.add(1, b1.incrementXY((int)pt1[0], (int)pt1[1]));
	
	//bottom curve
	p1=points.get(2+1);
	p2=points.get(3+1);
	b1=p1.between(p2);
	a1=BoneUtils.calculateAngle(p1.x, p1.y, p2.x, p2.y);
	direct1=(int) Math.toDegrees(a1);
	right1=direct1-90;
	pt1=BoneUtils.turnedAngle(0, -width, right1);
	
	points.add(3+1, b1.incrementXY((int)pt1[0], (int)pt1[1]));
}

public void stroke(Canvas canvas,String style){
	
	//CanvasUtils.drawLine(canvas, points.get(0).x, points.get(0).y,points.get(1).x,points.get(1).y);
	//CanvasUtils.drawLine(canvas, points.get(2).x, points.get(2).y,points.get(3).x,points.get(3).y);
	CanvasUtils.drawPoint(canvas, points, true, style);
}

}
