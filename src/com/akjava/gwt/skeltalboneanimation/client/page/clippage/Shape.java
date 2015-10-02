package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;

import com.akjava.lib.common.graphics.Point;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

public class Shape {
List<Point> points=Lists.newArrayList();

public void addPoint(Point point){
	points.add(point);
}



public void addPoint(double x,double y){
	points.add(new Point(x,y));
}

public void stroke(String style,Canvas canvas){
	canvas.getContext2d().setStrokeStyle(style);
	makePath(canvas);
	canvas.getContext2d().stroke();
}

public void clip(Canvas canvas){
	makePath(canvas);
	canvas.getContext2d().clip();
}

public void makePath(Canvas canvas){
	if(points.isEmpty()){
		return;
	}
	Context2d cx=canvas.getContext2d();
	cx.beginPath();
	cx.moveTo(points.get(points.size()-1).x, points.get(points.size()-1).y);
	for(Point pt:points){
		cx.lineTo(pt.x, pt.y);
	}
	cx.closePath();
}

public void addVector(double x,double y){
	for(Point pt:points){
		pt.x+=x;
		pt.y+=y;
	}
}

public static Shape createFromPoint(List<Point> pts){
	Shape shape=new Shape();
	for(Point pt:pts){
		shape.addPoint(pt);
	}
	return shape;
}

}
