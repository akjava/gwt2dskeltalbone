package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnimationFrame {
/*
 * not sure need this
*/
	/*
private int index;
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
*/

private Map<String,BoneFrame> boneFrames=new LinkedHashMap<String, BoneFrame>();
public Map<String, BoneFrame> getBoneFrames() {
	return boneFrames;
}
public void setBoneFrames(Map<String, BoneFrame> boneFrames) {
	this.boneFrames = boneFrames;
}
public void add(String boneName,int x,int y,double angle){
	BoneFrame boneFrame=new BoneFrame(boneName, x, y, angle);
	boneFrames.put(boneName, boneFrame);
}
public void add(BoneFrame boneFrame){
	boneFrames.put(boneFrame.getBoneName(), boneFrame);
}
public BoneFrame getBoneFrame(String name){
	return boneFrames.get(name);
}
}
