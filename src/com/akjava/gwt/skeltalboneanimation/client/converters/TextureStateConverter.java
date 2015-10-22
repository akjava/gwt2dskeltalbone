package com.akjava.gwt.skeltalboneanimation.client.converters;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame.TextureState;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;

public class TextureStateConverter extends Converter<TextureState,String>{

	@Override
	protected String doForward(TextureState textureState) {
		return textureState.toCsv(";");
	}

	@Override
	protected TextureState doBackward(String line) {
		String[] csv=line.split(";");
		TextureState state=new TextureState(csv[0]);
		
		
		/*
		 * map.put("id", id);
			map.put("x", x);
			map.put("y", y);
			map.put("angle", angle);
			map.put("scaleX", scaleX);
			map.put("scaleY", scaleY);
			map.put("alpha", alpha);
			map.put("flipHorizontal", flipHorizontal);
			map.put("flipVertical", flipVertical);
			map.put("visible", visible);
		 */
		if(csv.length>1){
			state.setX(ValuesUtils.toInt(csv[1], 0));
		}
		if(csv.length>2){
			state.setY(ValuesUtils.toInt(csv[2], 0));
		}
		if(csv.length>3){
			state.setAngle(ValuesUtils.toDouble(csv[3], 0));
		}
		if(csv.length>4){
			state.setScaleX(ValuesUtils.toDouble(csv[4], 0));
		}
		if(csv.length>5){
			state.setScaleY(ValuesUtils.toDouble(csv[5], 0));
		}
		if(csv.length>6){
			state.setAlpha(ValuesUtils.toDouble(csv[6], 0));
		}
		
		if(csv.length>7){
			state.setFlipHorizontal(ValuesUtils.toBoolean(csv[7], false));
		}
		
		if(csv.length>8){
			state.setFlipVertical(ValuesUtils.toBoolean(csv[8], false));
		}
		if(csv.length>9){
			
			state.setVisible(ValuesUtils.toBoolean(csv[9], false));
		}
		
		return state;
	}

}
