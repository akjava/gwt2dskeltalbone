package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TurnAngleAnimation extends VerticalPanel{

	public TurnAngleAnimation(){
		HorizontalPanel buttons=new HorizontalPanel();
		add(buttons);
		
		Canvas canvas = CanvasUtils.createCanvas(400, 400);
		
		add(canvas);
		
		canvas.getContext2d().setFillStyle("#f00");
		for(int i=0;i<360;i++){
			double[] v2=BoneUtils.turnedAngle(10, 10, i);
			CanvasUtils.fillPoint(canvas, v2[0]+200, v2[1]+200);
		}
		
	}
	
}
