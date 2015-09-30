package com.akjava.gwt.skeltalboneanimation.client.converters;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;

public class ImageDrawingDataConverter extends Converter<ImageDrawingData,String>{

	@Override
	protected String doForward(ImageDrawingData data) {
		List<String> values=new ArrayList<String>();
		
		//must not contain , & line-separator
		values.add(data.getId()==null?"":data.getId()); //[0]
		values.add(data.getImageName()==null?"":data.getImageName()); //[0]
		values.add(data.getBoneName()==null?"":data.getBoneName()); 
		
		values.add(String.valueOf(data.getIntX()));//[3]
		values.add(String.valueOf(data.getIntY()));
		values.add(String.valueOf(data.getScaleX()));
		values.add(String.valueOf(data.getScaleY()));
		values.add(String.valueOf(data.getAngle()));
		values.add(String.valueOf(data.getAlpha()));
		
		values.add(data.isFlipHorizontal()?"true":"false");//[9]
		values.add(data.isFlipVertical()?"true":"false");
		values.add(data.isVisible()?"true":"false");
		
		return Joiner.on(",").join(values);
	}

	@Override
	protected ImageDrawingData doBackward(String line) {
		ImageDrawingData data=new ImageDrawingData(null,null);
		String[] values=line.split(",");
		
		 data.setId(values[0]);
		 
		 if(values.length>1){
			 data.setImageName(values[1]);
		 }
		 if(values.length>2){
			 data.setBoneName(values[2]);
		 }
		 
		 if(values.length>3){
			 data.setX(ValuesUtils.toInt(values[3], 0));
		 }
		 if(values.length>4){
			 data.setY(ValuesUtils.toInt(values[4], 0));
		 }
		 if(values.length>5){
			 data.setScaleX(ValuesUtils.toDouble(values[5], 1));
		 }
		 if(values.length>6){
			 data.setScaleY(ValuesUtils.toDouble(values[6], 1));
		 }
		 if(values.length>7){
			 data.setAngle(ValuesUtils.toDouble(values[7], 0));
		 }
		 if(values.length>8){
			 data.setAlpha(ValuesUtils.toDouble(values[8], 1));
		 }
		 
		 if(values.length>9){
			 data.setFlipHorizontal(ValuesUtils.toBoolean(values[9], false));
		 }
		 if(values.length>10){
			 data.setFlipVertical(ValuesUtils.toBoolean(values[10], false));
		 }
		 if(values.length>11){
			 data.setVisible(ValuesUtils.toBoolean(values[11], true));
		 }
		
		return data;
	}

	
}
