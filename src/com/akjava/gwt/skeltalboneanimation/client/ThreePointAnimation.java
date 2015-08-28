package com.akjava.gwt.skeltalboneanimation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.lib.common.graphics.Rect;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ThreePointAnimation extends VerticalPanel{

	private int index;
	public ThreePointAnimation(){
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
		AnimationFrame frame=new AnimationFrame();
		animations.add(frame);
		
		//second frame - move
		AnimationFrame frame2=new AnimationFrame();
		animations.add(frame2);
		
		BoneFrame boneFrame2=new BoneFrame("root", 100, 0, 0);
		frame2.add(boneFrame2);
		
		//third frame - turn
		AnimationFrame frame3=new AnimationFrame();
		animations.add(frame3);
		
		BoneFrame boneFrame3=new BoneFrame("root", 100, 0, 45);
		frame3.add(boneFrame3);
		
		//fourth frame - turn both
		AnimationFrame frame4=new AnimationFrame();
		animations.add(frame4);
				
		BoneFrame boneFrame4=new BoneFrame("root",100, 0, 0);
		frame4.add(boneFrame4);
		
		BoneFrame boneFrame4b=new BoneFrame("back", 0, 0, 45);
		frame4.add(boneFrame4b);
		
		
		AnimationFrame frame5=new AnimationFrame();
		animations.add(frame5);
		
		BoneFrame boneFrame5a=new BoneFrame("root",100, 0, 45);
		frame5.add(boneFrame5a);
				
		BoneFrame boneFrame5=new BoneFrame("back", 0, 0, 45);
		frame5.add(boneFrame5);
		
		AnimationFrame frame6=new AnimationFrame();
		animations.add(frame6);
				
		BoneFrame boneFrame6=new BoneFrame("back", 0, 25, 0);
		frame6.add(boneFrame6);
		
		//test
		drawFrame(root,frame);
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
		for(TwoDimensionBone child:bone.getChildrens()){
			makeBoneCalculator(list, child, frame, calculator);
		}
		
	}
	private Map<String,double[]> cache=new HashMap<String, double[]>();
	private Canvas canvas;
	public void drawFrame(BoneAnimationData bone,AnimationFrame frame){
		int offsetX=200;
		int offsetY=200;
		double[] pt=BoneUtils.getFinalPositionAndAngle(bone);
		
		LogUtils.log(bone.getName()+","+pt[0]+" x "+pt[1]);
		//native implement
		Rect rect=Rect.fromCenterPoint((int)pt[0]+offsetX,(int)pt[1]+offsetY,10,10);
		String color=bone.getParent()==null?"#00f":"#f00";
		canvas.getContext2d().setFillStyle(color);
		RectCanvasUtils.fillCircle(rect, canvas, true);
		//RectCanvasUtils.fill(rect,canvas,color);
		
		if(bone.getParent()!=null){
		canvas.getContext2d().setStrokeStyle("#000");
		double[] parentPt=cache.get(bone.getParent().getName());
		if(parentPt!=null){
			CanvasUtils.drawLine(canvas, pt[0]+offsetX, pt[1]+offsetY, parentPt[0]+offsetX, parentPt[1]+offsetY);
		}else{
			LogUtils.log(bone.getParent().getName()+" has not cached");
		}
		}
		cache.put(bone.getName(),pt);
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
