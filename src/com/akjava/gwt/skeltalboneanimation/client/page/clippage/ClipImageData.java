package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
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
	return FluentIterable.from(getClips()).filter(new IsMatchId(id)).first();
}


public static class IsMatchId implements Predicate<ClipData>{
	private String id;
	public IsMatchId(String id){
		this.id=id;
	}
	@Override
	public boolean apply(ClipData input) {
		return input.getId().equals(id);
	}	
}


}
