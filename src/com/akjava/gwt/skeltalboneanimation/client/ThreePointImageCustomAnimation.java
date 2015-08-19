package com.akjava.gwt.skeltalboneanimation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeltalAnimations;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.cell.client.ImageLoadingCell;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ThreePointImageCustomAnimation extends VerticalPanel{

	private int index;
	int chestHeight=50;
	int chestSide=25;
	public ThreePointImageCustomAnimation(){
		HorizontalPanel buttons=new HorizontalPanel();
		add(buttons);
		
		canvas = CanvasUtils.createCanvas(400, 400);
		
		add(canvas);
		
		//bones
		final TwoDimensionBone root=new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=root.addBone(new TwoDimensionBone("back",0, -50));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",chestSide, -chestHeight));
		
		//animations
		final SkeltalAnimations animations=new SkeltalAnimations("test", 33.3);
		
		//first frame - do nothing
		for(int i=0;i<6;i++){
		AnimationFrame frame=new AnimationFrame();
		animations.add(frame);
		BoneFrame boneFrame1=new BoneFrame("root", 0, 0, -i*10);
		frame.add(boneFrame1);
		}
		
		
		for(int i=0;i<6;i++){
			AnimationFrame frame=new AnimationFrame();
			animations.add(frame);
			
			BoneFrame boneFrame1=new BoneFrame("root", 0, 0, 0);
			frame.add(boneFrame1);
			
			BoneFrame boneFrame2=new BoneFrame("back", 0, 0, -i*10);
			frame.add(boneFrame2);
			}
		
		for(int i=0;i<3;i++){
			AnimationFrame frame=new AnimationFrame();
			animations.add(frame);
			
			BoneFrame boneFrame1=new BoneFrame("root", 0, 0, 0);
			frame.add(boneFrame1);
			
			BoneFrame boneFrame2=new BoneFrame("back", 0, 0, 0);
			frame.add(boneFrame2);
			
			BoneFrame boneFrame3=new BoneFrame("chest", 0, 0, -i*10);
			frame.add(boneFrame3);
			}
		
		for(int i=0;i<3;i++){
			AnimationFrame frame=new AnimationFrame();
			animations.add(frame);
			
			BoneFrame boneFrame1=new BoneFrame("root", 0, 0, 0);
			frame.add(boneFrame1);
			
			BoneFrame boneFrame2=new BoneFrame("back", 0, 0, -60);
			frame.add(boneFrame2);
			
			BoneFrame boneFrame3=new BoneFrame("chest", 0, 0, -i*10);
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
		
		ImageElementLoader loader=new ImageElementLoader();
		loader.load("root.png", new ImageElementListener() {
			
			@Override
			public void onLoad(ImageElement element) {
				rootImage=element;
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		loader.load("back.png", new ImageElementListener() {
			
			@Override
			public void onLoad(ImageElement element) {
				backImage=element;
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
loader.load("upper.png", new ImageElementListener() {
			
			@Override
			public void onLoad(ImageElement element) {
				chestImage=element;
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	ImageElement rootImage,backImage,chestImage;
	
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
	
	
	public void drawImageAt(Canvas canvas,ImageElement image,int canvasX,int canvasY,int imageX,int imageY,double angle){
		canvas.getContext2d().save();
		double radiant=Math.toRadians(angle);
		canvas.getContext2d().translate(canvasX+imageX,canvasY+imageY);//rotate center
		
		canvas.getContext2d().rotate(radiant);
		canvas.getContext2d().translate(-(canvasX+imageX),-(canvasY+imageY));//and back
		
		canvas.getContext2d().translate(canvasX,canvasY);	
		
		canvas.getContext2d().drawImage(image, 0,0);
		canvas.getContext2d().restore();
	}
	
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
		
		
		
		int imageX=0;
		int imageY=0;//imageCanvas.getCoordinateSpaceHeight()/2;
		
		ImageElement element=null;
		if(bone.getName().equals("root") && rootImage!=null){
			element=rootImage;
			imageX=element.getWidth()/2;
			//imageY=element.getHeight();
			imageY=50;
		}
		else if(bone.getName().equals("back") && backImage!=null){
			element=backImage;
			imageX=element.getWidth()/2;
			imageY=element.getHeight();
		}
		else if(bone.getName().equals("chest") && chestImage!=null){
			element=chestImage;
			imageX=element.getWidth()/2+chestSide;;
			imageY=element.getHeight();;
		}
		
		if(element!=null){
		
		drawImageAt(canvas, element,(int)( -imageX+offsetX+pt[0]),(int)( -imageY+offsetY+pt[1]), imageX, imageY, pt[2]);
		}
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
