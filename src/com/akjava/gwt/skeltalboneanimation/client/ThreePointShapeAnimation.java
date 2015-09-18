package com.akjava.gwt.skeltalboneanimation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ThreePointShapeAnimation extends VerticalPanel{

	private int index;
	public ThreePointShapeAnimation(){
		HorizontalPanel buttons=new HorizontalPanel();
		add(buttons);
		
		canvas = CanvasUtils.createCanvas(400, 400);
		
		add(canvas);
		
		//bones
		final TwoDimensionBone root=new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=root.addBone(new TwoDimensionBone("back",0, -50));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",0, -100));
		
		//animations
		final SkeletalAnimation animations=new SkeletalAnimation("test", 33.3);
		
		//first frame - do nothing
		for(int i=0;i<12;i++){
		AnimationFrame frame=new AnimationFrame();
		animations.add(frame);
		BoneFrame boneFrame1=new BoneFrame("root", 0, 0, i*30);
		frame.add(boneFrame1);
		}
		
		for(int i=0;i<12;i++){
			AnimationFrame frame=new AnimationFrame();
			animations.add(frame);
			
			BoneFrame boneFrame1=new BoneFrame("root", 0, 0, 45);
			frame.add(boneFrame1);
			
			BoneFrame boneFrame2=new BoneFrame("back", 0, 0, i*30);
			frame.add(boneFrame2);
			}
		
		for(int i=0;i<6;i++){
			AnimationFrame frame=new AnimationFrame();
			animations.add(frame);
			
			BoneFrame boneFrame1=new BoneFrame("root", 0, 0, 60);
			frame.add(boneFrame1);
			
			BoneFrame boneFrame2=new BoneFrame("back", 0, 0, 60);
			frame.add(boneFrame2);
			
			BoneFrame boneFrame3=new BoneFrame("chest", 0, 0, i*60);
			frame.add(boneFrame3);
			}
		
		
		//third frame - turn
		
		
		//test
		drawFrame(root,animations.getFrames().get(0));
		Button next=new Button("next",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				index++;
				if(index>=animations.getFrames().size()){
					index=0;
				}
				drawFrame(root,animations.getFrames().get(index));
			}
		});
		buttons.add(next);
	}
	
	public void makeBoneCalculator(List<BoneAnimationData> list,TwoDimensionBone bone,AnimationFrame frame,BoneAnimationData parent){
		BoneAnimationData calculator=new BoneAnimationData(bone.getName(),bone.getX(),bone.getY());
		calculator.setParent(parent);
		list.add(calculator);
		
		if(frame.getBoneFrames().get(bone.getName())!=null){
			BoneFrame boneFrame=frame.getBoneFrames().get(bone.getName());
			calculator.setX(calculator.getX()+boneFrame.getX());
			calculator.setY(calculator.getY()+boneFrame.getY());
			calculator.setAngle(boneFrame.getAngle());
		}
		for(TwoDimensionBone child:bone.getChildren()){
			makeBoneCalculator(list, child, frame, calculator);
		}
		
	}
	private Map<String,double[]> cache=new HashMap<String, double[]>();
	private Canvas canvas;
	public void drawFrame(BoneAnimationData bone,AnimationFrame frame){
		int offsetX=200;
		int offsetY=200;
		double[] pt=BoneUtils.getFinalPositionAndAngle(bone);
		
		//LogUtils.log(bone.getName());
		//native implement
		//Rect rect=Rect.fromCenterPoint((int)pt[0]+offsetX,(int)pt[1]+offsetY,10,10);
		
		
		String color=bone.getParent()==null?"#00f":"#f00";
		canvas.getContext2d().setFillStyle(color);
		List<PointXY> list=convert(pt[0]+offsetX,pt[1]+offsetY,pt[2]);
		
		
		/*
		canvas.getContext2d().setLineWidth(4);
		for(int i=0;i<list.size();i++){
			if(i!=0){
				LogUtils.log(list.get(i).getX()+","+list.get(i).getY()+","+list.get(i-1).getX()+","+list.get(i-1).getY());
				CanvasUtils.drawLine(canvas,list.get(i).getX(),list.get(i).getY(),list.get(i-1).getX(),list.get(i-1).getY());
			}
		}
		
		for(PointXY point:list){
			CanvasUtils.fillPoint(canvas,point.getX(),point.getY());
			LogUtils.log(point.getX()+","+ point.getY());
			//canvas.getContext2d().lineTo(point.getX(), point.getY());
		}
		*/
		
		canvas.getContext2d().beginPath();
		
		
		
		//LogUtils.log(list.get(0).getX()+","+ list.get(0).getY());
		
		canvas.getContext2d().moveTo(list.get(0).getX(), list.get(0).getY());
		for(PointXY point:list){
			CanvasUtils.fillPoint(canvas,point.getX(),point.getY());
			//LogUtils.log(point.getX()+","+ point.getY());
			canvas.getContext2d().lineTo(point.getX(), point.getY());
		}
		canvas.getContext2d().lineTo(list.get(0).getX(), list.get(0).getY());
		
		canvas.getContext2d().closePath();
		canvas.getContext2d().stroke();
		
		
		//RectCanvasUtils.fillCircle(rect, canvas, true);
		//RectCanvasUtils.fill(rect,canvas,color);
		
		
		
		if(bone.getParent()!=null){
		canvas.getContext2d().setStrokeStyle("#000");
		double[] parentPt=cache.get(bone.getParent().getName());
		if(parentPt!=null){
			CanvasUtils.drawLine(canvas, pt[0]+offsetX, pt[1]+offsetY, parentPt[0]+offsetX, parentPt[1]+offsetY);
		}else{
			//LogUtils.log(bone.getParent().getName()+" has not cached");
		}
		}
		
		cache.put(bone.getName(),pt);
	}
	
	private List<PointXY> convert(double finalX,double finalY,double finalAngle){
		List<PointXY> list=new ArrayList<PointXY>();
		
		double[] v0=BoneUtils.turnedAngle(0, -15, finalAngle);
		list.add(new PointXY((int)(v0[0]+finalX),(int)(v0[1]+finalY)));
		
		//left
		double[] v1=BoneUtils.turnedAngle(-10, -10, finalAngle);
		list.add(new PointXY((int)(v1[0]+finalX),(int)(v1[1]+finalY)));
		
		double[] v2=BoneUtils.turnedAngle(-10, 10, finalAngle);
		list.add(new PointXY((int)(v2[0]+finalX),(int)(v2[1]+finalY)));
		
		double[] v3=BoneUtils.turnedAngle(10, 10, finalAngle);
		list.add(new PointXY((int)(v3[0]+finalX),(int)(v3[1]+finalY)));
		
		double[] v4=BoneUtils.turnedAngle(10, -10, finalAngle);
		list.add(new PointXY((int)(v4[0]+finalX),(int)(v4[1]+finalY)));
		return list;
	}
	
	public void drawFrame(TwoDimensionBone root,AnimationFrame frame){
		LogUtils.log("frame:"+index);
		CanvasUtils.clear(canvas);
		cache.clear();
		List<BoneAnimationData> list=new ArrayList<BoneAnimationData>();
		makeBoneCalculator(list,root,frame,null);

		for(BoneAnimationData bone:list){
			drawFrame(bone,frame);
		}
	}
}
