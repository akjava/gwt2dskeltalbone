package com.akjava.gwt.skeltalboneanimation.client.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class TextureDataFunctions {

	public static TextureIdFunction getTextureIdFunction(){
		return TextureIdFunction.INSTANCE;
	}
	
	public static enum TextureIdFunction implements Function<ImageDrawingData,String>{
		INSTANCE;
		@Override
		public String apply(ImageDrawingData input) {
			return input.getId();
		}
	}
	
	
	public static List<String> convertIdString(TextureData data){
		checkNotNull(data,"convertTextureId:data is null");
		return FluentIterable.from(data.getDatas()).transform(getTextureIdFunction()).toList();
	}
}
