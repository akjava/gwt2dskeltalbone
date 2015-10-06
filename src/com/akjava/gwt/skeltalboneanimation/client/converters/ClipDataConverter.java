package com.akjava.gwt.skeltalboneanimation.client.converters;

import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.lib.common.graphics.Point;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;

public class ClipDataConverter extends Converter<ClipData, String>{

	@Override
	protected String doForward(ClipData data) {
		//bone,expand,pt1,pt2,..
		return data.toString();
	}

	@Override
	protected ClipData doBackward(String line) {
		String[] csv=line.split(",");
		
		ClipData data=new ClipData();
		
		data.setBone(csv[0]);
		
		if(csv.length>1){
			data.setExpand(ValuesUtils.toInt(csv[1], ClipData.DEFAULT_EXPAND));
		}
		for(int i=2;i<csv.length;i++){
			data.addPoint(Point.fromString(csv[i]));
		}
		//data.set
		return data;
	}

}
