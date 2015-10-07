package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnimationFrame {
	private double scaleX=1;
	private double scaleY=1;
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
public AnimationFrame(){
	super();
}
public AnimationFrame(double scaleX, double scaleY) {
		super();
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
public double getScaleX() {
		return scaleX;
	}
	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}
	public double getScaleY() {
		return scaleY;
	}
	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

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
	BoneFrame boneFrame=boneFrames.get(name);

	return boneFrame;
}
public void insertEmptyFrames(List<TwoDimensionBone> bones){
	for(TwoDimensionBone bone:bones){
		if(boneFrames.get(bone.getName())==null){
			add(new BoneFrame(bone.getName()));
		}
	}
	
}

public AnimationFrame copy(){
	AnimationFrame frame=new AnimationFrame();
	frame.setScaleX(scaleX);
	frame.setScaleY(scaleY);
	for(BoneFrame bones:boneFrames.values()){
		frame.add(bones.copy());
	}
	return frame;
}
}
