package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.predicates.ClipDataPredicates;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class ClipImageData {
private ImageDrawingData imageDrawingData;
private TwoDimensionBone bone;
public TwoDimensionBone getBone() {
	return bone;
}
public void setBone(TwoDimensionBone bone) {
	this.bone = bone;
}
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

public Optional<ClipData> findDataById(String id){
	return FluentIterable.from(getClips()).filter(new ClipDataPredicates.IsMatchId(id)).first();
}


}
