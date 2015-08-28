package com.akjava.gwt.skeltalboneanimation.client.converters;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.google.common.base.Converter;

public class BoneAndAnimationConverter extends Converter<BoneAndAnimationData,List<String>> {
	private AnimationConverter animationConverter=new AnimationConverter();
	private BoneConverter boneConverter=new BoneConverter();
	@Override
	protected List<String> doForward(BoneAndAnimationData data) {
		
		List<String> bones=boneConverter.convert(data.getBone());
		
		List<String> animations=animationConverter.convert(data.getAnimation());
		
		bones.add("");//for separator
		for(String line:animations){
			bones.add(line);
		}
		return bones;
	}


	@Override
	protected BoneAndAnimationData doBackward(List<String> lines) {
		List<String> boneLines=new ArrayList<String>();
		List<String> animationLines=new ArrayList<String>();
		boolean animationLine=false;
		
		for(String line:lines){
			if(line.startsWith(AnimationConverter.FORMAT_KEY)){
				String check=line.trim();
				if(check.equals(AnimationConverter.FORMAT_KEY) || check.startsWith(AnimationConverter.FORMAT_KEY+",")){
					animationLine=true;
				}
			}
			
			if(animationLine){
				animationLines.add(line);
			}else{
				boneLines.add(line);
			}
		}
		BoneAndAnimationData data=new BoneAndAnimationData();
		
		data.setAnimation(animationConverter.reverse().convert(animationLines));
		data.setBone(boneConverter.reverse().convert(boneLines));
		
		return data;
	}

}
