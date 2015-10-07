package com.akjava.gwt.skeltalboneanimation.client.functions;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.google.common.base.Function;

public class ClipDataToRelatedTextureIdFunction implements Function<ClipData,String>{

	@Override
	public String apply(ClipData input) {
		for(ImageDrawingData data:input.getLinkedImageDrawingData().asSet()){
			return data.getId();
		}
		return null;
	}
	
}