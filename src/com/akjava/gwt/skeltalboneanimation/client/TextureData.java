package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.google.common.collect.Lists;

public class TextureData {
private List<ImageDrawingData> imageDrawingDatas=Lists.newArrayList();
private TwoDimensionBone bone;
public List<ImageDrawingData> getImageDrawingDatas() {
	return imageDrawingDatas;
}
public void setImageDrawingDatas(List<ImageDrawingData> imageDrawingDatas) {
	this.imageDrawingDatas = imageDrawingDatas;
}
public TwoDimensionBone getBone() {
	return bone;
}
public void setBone(TwoDimensionBone bone) {
	this.bone = bone;
} 
}
