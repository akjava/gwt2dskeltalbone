package com.akjava.gwt.skeltalboneanimation.client.page;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBonePainter;
import com.akjava.lib.common.graphics.Rect;
import com.google.gwt.canvas.client.Canvas;

public class CircleLineBonePainter extends CanvasBonePainter{

private HasSelectionName hasSelectionName;
	public CircleLineBonePainter(Canvas canvas,HasSelectionName hasSelectionName,BonePositionControler positionControler) {
		super(positionControler);
		this.canvas=canvas;
		this.hasSelectionName=hasSelectionName;
	}

	@Override
	public void startPaint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endPaint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintBone(String name, String parent, int startX, int startY, int endX, int endY, double angle) {
		
		int boneSize=positionControler.getBoneSize();
		Rect rect=Rect.fromCenterPoint(endX,endY,boneSize/2,boneSize/2);
		
		String color;
		if(parent!=null){
			color="#f00";
		}else{
			color="#00f";//root bone;
		}
		//LogUtils.log("paint:"+name+","+rect.toKanmaString());
		canvas.getContext2d().setFillStyle(color);//TODO method
		RectCanvasUtils.fillCircle(rect, canvas, true);
		
		//draw selection
		String selectionColor="#040";
		
		
		
		if(hasSelectionName.getSelectionName()!=null && name.equals(hasSelectionName.getSelectionName())){
			rect=rect.expand(8, 8);//need expandSelf
			canvas.getContext2d().setStrokeStyle(selectionColor);
			
			RectCanvasUtils.strokeCircle(rect,canvas,true);
		}
		//
		
		String lineColor="#fff";
		
		//for bold selection line
		if(name.equals(hasSelectionName.getSelectionName())){
			lineColor="#0f0";
		}
		
		if(parent!=null){
			canvas.getContext2d().setLineWidth(5);
			canvas.getContext2d().setStrokeStyle("#000");
			CanvasUtils.drawLine(canvas, startX, startY,endX,endY);
			
			canvas.getContext2d().setLineWidth(1);
			canvas.getContext2d().setStrokeStyle(lineColor);
			CanvasUtils.drawLine(canvas, startX, startY,endX,endY);
		}
	}

}