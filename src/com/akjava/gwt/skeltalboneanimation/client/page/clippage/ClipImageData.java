package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.google.common.collect.Lists;

public class ClipImageData {
private ImageDrawingData imageDrawingData;
private List<ClipData> clips=Lists.newArrayList();

//background
public ImageDrawingData getImageDrawingData() {
	return imageDrawingData;
}
public void setImageDrawingData(ImageDrawingData imageDrawingData) {
	this.imageDrawingData = imageDrawingData;
}
public List<ClipData> getClips() {
	return clips;
}
public void setClips(List<ClipData> clips) {
	this.clips = clips;
}
}
