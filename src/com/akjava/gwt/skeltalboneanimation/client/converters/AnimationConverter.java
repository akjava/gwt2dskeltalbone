package com.akjava.gwt.skeltalboneanimation.client.converters;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.IgnoreStartWithShape;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.collect.FluentIterable;

public class AnimationConverter extends Converter<SkeletalAnimation,List<String>> {

	public static final String FORMAT_KEY="skeletalanimation";
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
		
		
		return lines;
	}

	@Override
	protected SkeletalAnimation doBackward(List<String> lines) {
		
		List<String> filterd=FluentIterable.from(lines).filter(new IgnoreStartWithShape()).toList();
		
		SkeletalAnimation animation=new SkeletalAnimation();
		AnimationFrame frame=null;
		boolean firstLineParsed=false;
		for(String line:filterd){
			String[] data=line.trim().split(",");
			if(data[0].isEmpty()){
				continue;
			}
			//first line control
			if(!firstLineParsed && data[0].equals(FORMAT_KEY)){
				if(data.length>1){
					animation.setFps(Double.parseDouble(data[1]));
				}
				if(data.length>2){
					animation.setName(data[2]);
				}
				
			}else if(ValuesUtils.isDigitString(data[0])){
				int index=Integer.parseInt(data[0]);//not support yet
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
				int x=0;
				int y=0;
				double angle=0;
				
				if(data.length>1){
					angle=Double.parseDouble(data[1]);
				}
				
				if(data.length>2){
					x=Integer.parseInt(data[2]);
				}
				if(data.length>3){
					y=Integer.parseInt(data[3]);
				}
				
				BoneFrame boneFrame=new BoneFrame(boneName, x, y, angle);
				if(frame==null){
					frame=new AnimationFrame();
					animation.add(frame);
				}
				frame.add(boneFrame);
			}
			firstLineParsed=true;
		}
		
		return animation;
	}
	

}
