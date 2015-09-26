package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.HasUniqData;
import com.google.common.collect.Lists;

public class TextureData extends HasUniqData<ImageDrawingData>{
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


@Override
public String getId(ImageDrawingData data) {
	return data.getId();
}
@Override
public List<ImageDrawingData> getDatas() {
	return imageDrawingDatas;
}

public TextureData copy(){
	TextureData data=new TextureData();
	if(data.getBone()!=null){
		data.setBone(bone.copy(true));
	}
	
	
	for(ImageDrawingData idata:imageDrawingDatas){
		data.getDatas().add(idata.copy());
	}
	
	return data;
}


}
