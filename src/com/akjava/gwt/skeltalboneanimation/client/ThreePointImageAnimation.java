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
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeltalAnimations;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ThreePointImageAnimation extends VerticalPanel{

	private int index;
	public ThreePointImageAnimation(){
		HorizontalPanel buttons=new HorizontalPanel();
		add(buttons);
		
		canvas = CanvasUtils.createCanvas(400, 400);
		
		add(canvas);
		
		//bones
		final TwoDimensionBone root=new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=root.addBone(new TwoDimensionBone("back",0, -50));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",0, -100));
		
		//animations
		final SkeltalAnimations animations=new SkeltalAnimations("test", 33.3);
		
		//first frame - do nothing
		for(int i=0;i<6;i++){
		AnimationFrame frame=new AnimationFrame();
		animations.add(frame);
		BoneFrame boneFrame1=new BoneFrame("root", 0, 0, i*60);
		frame.add(boneFrame1);
		}
		
		
		for(int i=0;i<6;i++){
			AnimationFrame frame=new AnimationFrame();
			animations.add(frame);
			
			BoneFrame boneFrame1=new BoneFrame("root", 0, 0, 60);
			frame.add(boneFrame1);
			
			BoneFrame boneFrame2=new BoneFrame("back", 0, 0, i*60);
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
		
		imageCanvas = CanvasUtils.createCanvas(20, 20);
		CanvasUtils.fillRect(imageCanvas, "#f00");
		
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
	
	public void drawImageAt(Canvas canvas,CanvasElement image,int canvasX,int canvasY,int imageX,int imageY,double angle){
		canvas.getContext2d().save();
		double radiant=Math.toRadians(angle);
		canvas.getContext2d().translate(canvasX+imageX,canvasY+imageY);//rotate center
		
		canvas.getContext2d().rotate(radiant);
		canvas.getContext2d().translate(-(canvasX+imageX),-(canvasY+imageY));//and back
		
		canvas.getContext2d().translate(canvasX,canvasY);	
		
		canvas.getContext2d().drawImage(image, 0,0);
		canvas.getContext2d().restore();
	}
	
	/*
	public void drawImageAt(Canvas canvas,ImageElement image,int canvasX,int canvasY,int imageX,int imageY,double angle){
		canvas.getContext2d().save();
		double radiant=Math.toRadians(angle);
		canvas.getContext2d().translate(imageX,imageY);//rotate center
		
		canvas.getContext2d().rotate(radiant);
		canvas.getContext2d().translate(-imageX,-imageY);//and back
		
		canvas.getContext2d().translate(canvasX,canvasY);	
		
		canvas.getContext2d().drawImage(image, 0,0);
		canvas.getContext2d().restore();
	}*/
	
	public void makeBoneCalculator(List<BoneCalculator> list,TwoDimensionBone bone,AnimationFrame frame,BoneCalculator parent){
		BoneCalculator calculator=new BoneCalculator(bone.getName(),bone.getX(),bone.getY());
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
	private Canvas imageCanvas;
	public void drawFrame(BoneCalculator bone,AnimationFrame frame){
		int offsetX=200;
		int offsetY=200;
		double[] pt=BoneUtils.getFinalPositionAndAngle(bone);
		
		//LogUtils.log(bone.getName());
		//native implement
		//Rect rect=Rect.fromCenterPoint((int)pt[0]+offsetX,(int)pt[1]+offsetY,10,10);
		
		
		
		int imageX=imageCanvas.getCoordinateSpaceWidth()/2;
		int imageY=0;//imageCanvas.getCoordinateSpaceHeight()/2;
		
		drawImageAt(canvas, imageCanvas.getCanvasElement(),(int)( -imageX+offsetX+pt[0]),(int)( -imageY+offsetY+pt[1]), imageX, imageY, pt[2]);
		
		/*
		canvas.getContext2d().setLineWidth(4);
		for(int i=0;i<list.size();i++){
			if(i!=0){
				LogUtils.log(list.get(i).getX()+","+list.get(i).getY()+","+list.get(i-1).getX()+","+list.get(i-1).getY());
				CanvasUtils.drawLine(canvas,list.get(i).getX(),list.get(i).getY(),list.get(i-1).getX(),list.get(i-1).getY());
			}
		}
		
		
		*/
		
		
		
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
	

	
	public void drawFrame(TwoDimensionBone root,AnimationFrame frame){
		LogUtils.log("frame:"+index);
		CanvasUtils.clear(canvas);
		cache.clear();
		List<BoneCalculator> list=new ArrayList<BoneCalculator>();
		makeBoneCalculator(list,root,frame,null);

		for(BoneCalculator bone:list){
			drawFrame(bone,frame);
		}
	}
}
