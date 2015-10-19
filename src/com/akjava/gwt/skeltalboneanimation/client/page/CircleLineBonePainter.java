package com.akjava.gwt.skeltalboneanimation.client.page;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBonePainter;
import com.akjava.lib.common.graphics.IntRect;
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

	public static final DrawSetting defaultDrawSetting=new DrawSetting();
	public static final DrawSetting prevDrawSetting=new DrawSetting("#666","#333","#030","#000",1);
	public static final DrawSetting nextDrawSetting=new DrawSetting("#ccc","#aaa","#afa","#ccc",1);
	public static class DrawSetting{
		
		public DrawSetting(){};
		public DrawSetting(String boneColor, String boneRootColor, String selectionColor, String lineColor, int lineShadowWidth) {
			super();
			this.boneColor = boneColor;
			this.boneRootColor = boneRootColor;
			this.selectionColor = selectionColor;
			this.lineColor = lineColor;
			this.lineShadowWidth = lineShadowWidth;
		}
		String boneColor="#f00";
		String boneRootColor="#00f";
		String selectionColor="#0a0";
		String lineColor="#fff";
		int lineShadowWidth=5;
	}
	
	private DrawSetting drawSetting=defaultDrawSetting;
	public void setDrawSetting(DrawSetting drawSetting) {
		this.drawSetting = drawSetting;
	}

	@Override
	public void paintBone(String name, String parent, int startX, int startY, int endX, int endY, double angle,boolean locked) {
		
		int boneSize=positionControler.getBoneSize();
		IntRect rect=IntRect.fromCenterPoint(endX,endY,boneSize/2,boneSize/2);
		
		String color;
		if(parent!=null){
			color=drawSetting.boneColor;
		}else{
			color=drawSetting.boneRootColor;//root bone;
		}
		//LogUtils.log("paint:"+name+","+rect.toKanmaString());
		
		if(locked){
			RectCanvasUtils.fill(rect, canvas,color);
		}else{
			canvas.getContext2d().setFillStyle(color);//TODO method
			RectCanvasUtils.fillCircle(rect, canvas, true);
			//target().color().circle().fill()
		}
		
		//draw selection
		String selectionColor=drawSetting.selectionColor;
		
		
		
		if(hasSelectionName.getSelectionName()!=null && name.equals(hasSelectionName.getSelectionName())){
			rect=rect.expand(8, 8);//need expandSelf
			canvas.getContext2d().setStrokeStyle(selectionColor);
			
			RectCanvasUtils.strokeCircle(rect,canvas,true);
		}
		//
		
		String lineColor=drawSetting.lineColor;
		
		//for bold selection line
		if(name.equals(hasSelectionName.getSelectionName())){
			lineColor=selectionColor;
		}
		
		if(parent!=null){
			canvas.getContext2d().setLineWidth(drawSetting.lineShadowWidth);
			canvas.getContext2d().setStrokeStyle("#000");
			CanvasUtils.drawLine(canvas, startX, startY,endX,endY);
			
			canvas.getContext2d().setLineWidth(1);
			canvas.getContext2d().setStrokeStyle(lineColor);
			CanvasUtils.drawLine(canvas, startX, startY,endX,endY);
			
			
			//for test
			/*
			BoxPoints box=new BoxPoints(startX, startY, endX, endY, 20);
			box.stroke(canvas, "#f00");
			*/
			
			/*
			canvas.getContext2d().setStrokeStyle("#f00");
			double a=BoneUtils.calculateAngle(startX, startY, endX, endY);
			int direct=(int) Math.toDegrees(a);
			int right=direct+90;
			double[] pt=BoneUtils.turnedAngle(0, -100, right);
			double[] pt2=new double[2];
			pt2[0]=pt[0]+endX-startX;
			pt2[1]=pt[1]+endY-startY;
			
			double[] pt3=new double[2];
			pt3[0]=startX-pt[0];
			pt3[1]=startY-pt[1];
			
			CanvasUtils.drawLine(canvas, startX, startY,startX+pt[0],startY+pt[1]);
			CanvasUtils.drawLine(canvas, startX+pt2[0], startY+pt2[1],startX+pt[0],startY+pt[1]);
			CanvasUtils.drawLine(canvas, startX, startY,pt3[0],pt3[1]);
			*/
		}
	}

}
