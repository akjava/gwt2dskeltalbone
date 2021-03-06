package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.lib.common.graphics.Point;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;

public class BoxPoints {
List<Point> points=Lists.newArrayList();

public List<Point> getPoints() {
	return points;
}
public void setPoints(List<Point> points) {
	this.points = points;
}
public BoxPoints(Point p1,Point p2,int width){
	this(p1.x,p1.y,p2.x,p2.y,width);
}
public BoxPoints(double x1,double y1,double x2,double y2,double width){
	double a=BoneUtils.calculateAngle(x1, y1, x2, y2);
	int direct=(int) Math.toDegrees(a);
	int right=direct+90;
	double[] pt=BoneUtils.turnedAngle(0, -width, right);
	
	
	points.add(new Point(x2+(int)-pt[0],y2+(int)-pt[1]));
	points.add(new Point(x2+(int)pt[0],y2+(int)pt[1]));
	
	
	points.add(new Point(x1+(int)pt[0],y1+(int)pt[1]));
	points.add(new Point(x1+(int)-pt[0],y1+(int)-pt[1]));
	
	
	//TODO control by method,at lease insert side
	//top curve
	Point p1=points.get(0);
	Point p2=points.get(1);
	Point b1=p1.between(p2);
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
	CanvasUtils.draw(canvas, points, true, style);
}

}
