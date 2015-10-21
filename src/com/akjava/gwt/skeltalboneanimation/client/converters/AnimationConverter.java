package com.akjava.gwt.skeltalboneanimation.client.converters;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame;
import com.akjava.gwt.skeltalboneanimation.client.predicates.IgnoreStartWithShape;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class AnimationConverter extends Converter<SkeletalAnimation,List<String>> {

	public static final String FORMAT_KEY="skeletalanimation";
	public static final String TEXTURE_FRAME_FORMAT_KEY="_textureframe_";//TODO disallow bone key
	@Override
	protected List<String> doForward(SkeletalAnimation animation) {
		List<String> lines=new ArrayList<String>();
		
		//animation
		lines.add(FORMAT_KEY+","+animation.getFps()+","+animation.getName());
		for(int i=0;i<animation.getFrames().size();i++){
			//index
			
			
			//bone-frame
			AnimationFrame frame=animation.getFrames().get(i);
			lines.add(String.valueOf(i)+","+String.valueOf(frame.getScaleX())+","+String.valueOf(frame.getScaleY()));
			
			
			
			for(BoneFrame boneFrame:frame.getBoneFrames().values()){
				String data=boneFrame.getBoneName()+","+boneFrame.getAngle()+","+boneFrame.getX()+","+boneFrame.getY();
				lines.add(data);
			}
		}
		
		List<String> textureLines=Lists.newArrayList();
		TextureFrameConverter textureFrameConverter=new TextureFrameConverter();
		Joiner joiner=Joiner.on(",");
		
		for(int i=0;i<animation.getFrames().size();i++){
			if(animation.getFrames().get(i).getTextureFrame()!=null){
				String[] csv=textureFrameConverter.convert(animation.getFrames().get(i).getTextureFrame());
				csv[0]=String.valueOf(i);
				textureLines.add(joiner.join(csv));
			}
		}
		
		if(!textureLines.isEmpty()){
			lines.add(TEXTURE_FRAME_FORMAT_KEY);
			
			lines=ImmutableList.copyOf(Iterables.concat(lines,textureLines));
		}
		
		
		return lines;
	}
	
	
	

	@Override
	protected SkeletalAnimation doBackward(List<String> lines) {
		
		List<String> filterd=FluentIterable.from(lines).filter(new IgnoreStartWithShape()).toList();
		
		SkeletalAnimation animation=new SkeletalAnimation();
		AnimationFrame frame=null;
		boolean firstLineParsed=false;
		boolean textureFrameLineStarted=false;
		List<String[]> textureFrameCsv=Lists.newArrayList();
		for(String line:filterd){
			try{
			String[] data=line.trim().split(",");
			if(data[0].isEmpty()){ //if bone name is empty skipped
				continue;
			}
			
			if(textureFrameLineStarted){
				textureFrameCsv.add(data);
				continue;
			}
			
			//first line control 
			/**
			 * 
			 * FORMAT_KEY,fps,name
			 * skeletalanimation,33.34,test
			 * 
			 */
			if(!firstLineParsed && data[0].equals(FORMAT_KEY)){
				if(data.length>1){
					animation.setFps(Double.parseDouble(data[1]));
				}
				if(data.length>2){
					animation.setName(data[2]);
				}
				
			}else if(ValuesUtils.isDigitString(data[0])){
				
				
				int index=0;
				try{
				index=Integer.parseInt(data[0]);//not support yet
				}catch (Exception e) {
					LogUtils.log("parse-frame index faild input=:"+data[0]);
				}
				frame=new AnimationFrame();
				
				if(data.length>1){
					frame.setScaleX(ValuesUtils.toDouble(data[1], 1));
				}
				
				if(data.length>2){
					frame.setScaleY(ValuesUtils.toDouble(data[2], 1));
				}
				
				//frame.setIndex(index);	//re-think index
				animation.add(frame);
			}else{
				
				
				String boneName=data[0];
				
				if(boneName.equals(TEXTURE_FRAME_FORMAT_KEY)){
					textureFrameCsv.add(data);
					textureFrameLineStarted=true;
					continue;
				}
				
				
				
				double x=0;
				double y=0;
				double angle=0;
				
				if(data.length>1){
					angle=Double.parseDouble(data[1]);
				}
				
				if(data.length>2){
					x=Double.parseDouble(data[2]);
				}
				if(data.length>3){
					y=Double.parseDouble(data[3]);
				}
				
				BoneFrame boneFrame=new BoneFrame(boneName, x, y, angle);
				if(frame==null){
					frame=new AnimationFrame();
					animation.add(frame);
				}
				frame.add(boneFrame);
			}
			firstLineParsed=true;
			}catch (Exception e) {
				LogUtils.log("parse-faild:"+line);
				throw new RuntimeException("AnimationConverter:parse faild:"+line);
			}
		}
		
		//parsing texture frame
		for(String[] data:textureFrameCsv){
			
			if(data[0].equals(TEXTURE_FRAME_FORMAT_KEY)){
				//first line ,future modify
				continue;
			}
			
			int index=-1;
			try{
			index=Integer.parseInt(data[0]);
			}catch (Exception e) {
				LogUtils.log("parse-texture-frame index faild input=:"+data[0]);
				continue;
			}
			
			//check out of index
			if(index<0 || index>=animation.getFrames().size()){
				LogUtils.log("invalid index:"+Joiner.on(",").join(data));
				continue;
			}
			
			TextureFrame textureFrame=new TextureFrameConverter().reverse().convert(data);
			
			//validation
			if(!textureFrame.isNeedResetOrder() && !textureFrame.isNeedResetState() &&
					!textureFrame.getTextureOrder().isPresent() && !textureFrame.getTextureUpdates().isPresent()){
				textureFrame=null;
				LogUtils.log("invalid data loaded:"+Joiner.on(",").join(data));
			}
			
			if(textureFrame!=null){
				
				//must not exist.
				if(animation.getFrames().get(index).getTextureFrame()!=null){
					LogUtils.log("already exist overwrite:"+Joiner.on(",").join(data));
				}
				
				animation.getFrames().get(index).setTextureFrame(textureFrame);
				
			}
			
			
		}
		
		
		return animation;
	}
	

}
