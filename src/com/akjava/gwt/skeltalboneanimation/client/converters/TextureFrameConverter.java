package com.akjava.gwt.skeltalboneanimation.client.converters;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class TextureFrameConverter extends Converter<TextureFrame,String[]>{

	@Override
	protected String[] doForward(TextureFrame frame) {
		String[] csv=new String[4];
		csv[1]=String.valueOf(frame.isNeedResetOrder());
		csv[2]=String.valueOf(frame.isNeedResetState());
		csv[3]="";
		for(List<String> order:frame.getTextureOrder().asSet()){
			csv[3]=Joiner.on(":").join(order);
		}
		
		//TODO add state
		
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
		if(data.length>1){
			resetOrder=ValuesUtils.toBoolean(data[1], false);
		}
		if(data.length>2){
			resetState=ValuesUtils.toBoolean(data[2], false);
		}
		if(data.length>3 && !data[3].isEmpty()){
		order=Splitter.on(":").splitToList(data[3]);
		}
		
		//TODO add state
		
		TextureFrame textureFrame=new TextureFrame(resetState,resetOrder);
		textureFrame.setTextureOrder(order);
		return textureFrame;
	}

}
