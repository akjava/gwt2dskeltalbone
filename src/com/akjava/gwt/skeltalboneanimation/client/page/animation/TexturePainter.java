package com.akjava.gwt.skeltalboneanimation.client.page.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneWithXYAngle;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.BoneControler;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;

public class TexturePainter{

	
	private Supplier<TextureData> currentTextureData;
	private Supplier<AnimationFrame> currentFrame;
	private Supplier<SkeletalAnimation> currentAnimation;
	


	private BoneControler boneControler;
	
	public TexturePainter(BoneControler boneControler,Supplier<TextureData> currentTextureData, Supplier<AnimationFrame> currnetFrame, Supplier<SkeletalAnimation> currentAnimation) {
		super();
		this.currentTextureData = currentTextureData;
		this.currentFrame = currnetFrame;
		this.currentAnimation = currentAnimation;
		
		this.boneControler = boneControler;
	}
	public void draw(Canvas canvas){
		
		TextureData textureData=currentTextureData.get();
		SkeletalAnimation animation=currentAnimation.get();
		AnimationFrame animationFrame=currentFrame.get();
		
		if(animation==null){
			LogUtils.log("TexturePainter:no animation");
			return;
		}
		
		if(textureData==null){
			LogUtils.log("TexturePainter:no texture");
			return;
		}
		if(animationFrame==null){
			LogUtils.log("TexturePainter:no frame");
			return;
		}
		
		if(canvas.getCoordinateSpaceWidth()!=boneControler.getCanvas().getCoordinateSpaceWidth() || canvas.getCoordinateSpaceHeight()!=boneControler.getCanvas().getCoordinateSpaceHeight()){
			LogUtils.log("TexturePainter:invalid canvas size");
			return;
		}
		

		double scaleX=animationFrame.getScaleX();
		double scaleY=animationFrame.getScaleY();
		//LogUtils.log("drawTextureData:"+scaleX+","+scaleY);
	/*	double scaleX=1;
		double scaleY=1;*/
		
		
		
		boneControler.getBonePositionControler().updateBoth(animationFrame);//TODO update on value changed only
		//TODO add show bone check
		//TODO make class,it's hard to understand
		 List<BoneWithXYAngle> initialBonePosition=boneControler.getBonePositionControler().getRawInitialData();
		 List<BoneWithXYAngle> movedBonePosition=boneControler.getBonePositionControler().getRawAnimationedData();
		 
		
		
		//int offsetX=painter.getOffsetX();
		//int offsetY=painter.getOffsetY();
		
		int offsetX=textureData.getOffsetX();
		int offsetY=textureData.getOffsetY();
		
		int canvasCenterX=boneControler.getBonePositionControler().getSettings().getOffsetX();
		int canvasCenterY=boneControler.getBonePositionControler().getSettings().getOffsetY();
		
		initializeConvetedCanvas();
		List<ImageDrawingData> imageDrawingDatas=textureData.getImageDrawingDatas();
		
		
		List<ImageDrawingData> sorted=imageDrawingDatas;
		
		//sort order
		for(TextureFrame textureFrame:animation.getMergedTextureFrameAt(animationFrame).asSet()){
			for(List<String> order:textureFrame.getTextureOrder().asSet()){
				sorted=sort(imageDrawingDatas, order);
			}
		}
		
		
		
		List<ImageDrawingData> finalDatas=Lists.newArrayList();
		
		for(ImageDrawingData data:sorted){
		//	LogUtils.log("texture-painter:"+data.getId());
			//update & use copy
			
			finalDatas.add(data);
		}
		
		
		
		
		for(int i=0;i<finalDatas.size();i++){
			ImageDrawingData data=finalDatas.get(i);
			if(!data.isVisible()){
				continue;
			}
			
			String boneName=data.getBoneName();
			
		
			
			int boneIndex=findIndex(movedBonePosition,boneName);
			
			if(boneIndex==-1){
				//noindex
				continue;
			}
			
			int boneX=(int)initialBonePosition.get(boneIndex).getX();//scale effected
			int boneY=(int)initialBonePosition.get(boneIndex).getY();
			
			/**
			 * bonePosition start(0,0) however on canvas cordinate center is 0,0 
			 * so need add offset*(this is always half canvas so far)
			 */
			
			
			
			int textureIndex=textureData.indexOf(data.getId());
			
			if(textureIndex==-1){
				LogUtils.log("invalid texture index:"+data.getId());
			}
			
			
			if(!data.isVisible()){
				continue;
			}
			
			
			//texture
			Canvas converted=convertedDatas.get(textureIndex);
			
			double halfConvertedImageWidth=converted.getCoordinateSpaceWidth()/2*scaleX;
			double halfConvertedImageHeighth=converted.getCoordinateSpaceHeight()/2*scaleY;
			
			//this image drawing data cordinate absolute.so like bone cordinate need sub offset*
			
			/**
			 * 
			 * ((data.getIntX()-offsetX)*scaleX = texture(data) x position is left-top corner is 0:0 ,but boneX 's 0:0 center of canvas(800x800)
			 * and this position not scale effect yet.
			 * 
			 * data.getIntX() is center of texture,to get left-top coner position need sub half-converted-image-width.
			 * 
			 * diffX is relative from bone
			 */
			
			
			double imagePointX=((boneX)-((data.getX()-offsetX)*scaleX-halfConvertedImageWidth));
			double imagePointY=((boneY)-((data.getY()-offsetY)*scaleY-halfConvertedImageHeighth));
			
			
			
			/*int imageX=(int)(data.getIntX()-converted.getCoordinateSpaceWidth()/2)-(boneX+offsetX); //
			int imageY=(int)(data.getIntY()-converted.getCoordinateSpaceHeight()/2)-(boneY+offsetY);//
	*/		//LogUtils.log(imageX+","+imageY);
			
			double movedX=movedBonePosition.get(boneIndex).getX();
			double movedY=movedBonePosition.get(boneIndex).getY();
			
			
			
			//LogUtils.log(boneX+","+boneY+","+movedX+","+movedY);
			double angle=movedBonePosition.get(boneIndex).getAngle();
			
			//CanvasUtils.drawCenter(canvas, converted.getCanvasElement(), offsetX, offsetY, scaleX, scaleY, angle, 1.0)
			drawImageAt(canvas,converted.getCanvasElement(),movedX+canvasCenterX,movedY+canvasCenterY,imagePointX,imagePointY,angle,scaleX,scaleY);
			//canvas.getContext2d().drawImage(converted.getCanvasElement(), (int)(data.getX()-converted.getCoordinateSpaceWidth()/2), (int)(data.getY()-converted.getCoordinateSpaceHeight()/2));
			//
		}
		
	}
	
	private List<ImageDrawingData> sort(List<ImageDrawingData> datas,List<String> order){
		if(order==null){
			return datas;
		}
		
		Map<String,ImageDrawingData> map=new HashMap<String,ImageDrawingData>();
		
		for(ImageDrawingData data:datas){
			map.put(data.getId(), data);
		}
		
		
		List<ImageDrawingData> sorted=Lists.newArrayList();
		
		for(String id:order){
			ImageDrawingData data=map.get(id);
			if(data==null){
				LogUtils.log("invalid order:"+id);
				continue;
			}
			sorted.add(data);
		}
		
		
		return sorted;
	}
	
	private List<Canvas> convertedDatas;//initialized when new texture loaded.
	public void clearConvertedDatas(){
		convertedDatas=null;
	}
	public void initializeConvetedCanvas(){
		TextureData textureData=currentTextureData.get();
		
		if(textureData==null){
			return;
		}
		if(convertedDatas==null){
		//	LogUtils.log("convertedDatas");
			convertedDatas=new ArrayList<Canvas>();
			for(ImageDrawingData data:textureData.getImageDrawingDatas()){
				convertedDatas.add(data.convertToCanvas());
			}
		
			//LogUtils.log("debug");
			for(ImageDrawingData data:textureData.getDatas()){
				//test validate image?
				//LogUtils.log(data.getConvertedCanvas().get().toDataUrl());
				
				//LogUtils.log(data.convertToCanvas().toDataUrl());
			}
		}
		
		
		
	}
	
	public void drawImageAt(Canvas canvas,CanvasElement image,double canvasX,double canvasY,double imagePointX,double imagePointY,double angle,double scaleX,double scaleY){
		canvas.getContext2d().save();
		double radiant=Math.toRadians(angle);
		canvas.getContext2d().translate(canvasX,canvasY);//rotate center
		
		canvas.getContext2d().rotate(radiant);
		canvas.getContext2d().translate(-(canvasX),-(canvasY));//and back
		
		canvas.getContext2d().translate(canvasX-imagePointX,canvasY-imagePointY);	
		
		
		canvas.getContext2d().scale(scaleX,scaleY);
		canvas.getContext2d().drawImage(image, 0,0);
		canvas.getContext2d().restore();
	}
	
	private int findIndex(List<BoneWithXYAngle> list,String name){
		for(int i=0;i<list.size();i++){
			BoneWithXYAngle data=list.get(i);
			if(data.getBone().getName().equals(name)){
				return i;
			}
		}
		return -1;
	}
}