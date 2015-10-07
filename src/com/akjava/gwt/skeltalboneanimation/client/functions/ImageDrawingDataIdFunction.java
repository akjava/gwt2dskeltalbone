package com.akjava.gwt.skeltalboneanimation.client.functions;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.google.common.base.Function;

public class ImageDrawingDataIdFunction implements Function<ImageDrawingData,String>{

	@Override
	public String apply(ImageDrawingData input) {
		return input.getId();
	}
	
}