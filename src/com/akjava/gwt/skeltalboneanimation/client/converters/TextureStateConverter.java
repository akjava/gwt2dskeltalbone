package com.akjava.gwt.skeltalboneanimation.client.converters;

import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame.TextureState;
import com.google.common.base.Converter;

public class TextureStateConverter extends Converter<TextureState,String>{

	@Override
	protected String doForward(TextureState textureState) {
		return textureState.toCsv(":");
	}

	@Override
	protected TextureState doBackward(String line) {
		String[] csv=line.split(":");
		TextureState state=new TextureState(csv[0]);
		
		if(csv.length>1){
			//TODO implements
		}
		
		return state;
	}

}
