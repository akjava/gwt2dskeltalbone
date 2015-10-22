package com.akjava.gwt.skeltalboneanimation.client.converters;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame.TextureState;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TextureFrameConverter extends Converter<TextureFrame,String[]>{

	@Override
	protected String[] doForward(TextureFrame frame) {
		String[] csv=new String[4];
		csv[1]=String.valueOf(frame.isNeedResetOrder());
		csv[2]=String.valueOf(frame.isNeedResetState());
		csv[3]="";
		csv[4]="";
		
		for(List<String> order:frame.getTextureOrder().asSet()){
			csv[3]=Joiner.on(":").join(order);
		}
		
		//TODO add state
		for(List<TextureState> states:frame.getTextureUpdates().asSet()){
			
		csv[4]=Joiner.on(":").join(new TextureStateConverter().convertAll(states));
			
			
		}
		
		return csv;
	}

	/*
	 * ignore first index parse by yourself
	 * (non-Javadoc)
	 * @see com.google.common.base.Converter#doBackward(java.lang.Object)
	 */
	@Override
	protected TextureFrame doBackward(String[] data) {
		boolean resetOrder=false;
		boolean resetState=false;
		List<String> order=null;
		List<TextureState> states=null;
		if(data.length>1){
			resetOrder=ValuesUtils.toBoolean(data[1], false);
		}
		if(data.length>2){
			resetState=ValuesUtils.toBoolean(data[2], false);
		}
		if(data.length>3 && !data[3].isEmpty()){
		order=Splitter.on(":").splitToList(data[3]);
		}
		
		if(data.length>4 && !data[4].isEmpty()){
			List<String> stateString=Splitter.on(":").splitToList(data[4]);
			states=Lists.newArrayList(new TextureStateConverter().reverse().convertAll(stateString));
			//LogUtils.log("TextureFrameConverter:state loaded:"+states.get(0));
		}
		
		//TODO add state
		
		TextureFrame textureFrame=new TextureFrame(resetState,resetOrder);
		textureFrame.setTextureOrder(order);
		textureFrame.setTextureUpdates(states);
		
		return textureFrame;
	}

}
