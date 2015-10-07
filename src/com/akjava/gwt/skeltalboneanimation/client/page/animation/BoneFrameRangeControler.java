package com.akjava.gwt.skeltalboneanimation.client.page.animation;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.lib.common.graphics.Point;

public interface BoneFrameRangeControler {
	public void setRangeAt(int frameIndex,String boneName,double angle);
	public void setPositionAt(int frameIndex,String boneName,Point position);
	public void replaceAnimations(List<AnimationFrame> frames,int selectedIndex);
}
