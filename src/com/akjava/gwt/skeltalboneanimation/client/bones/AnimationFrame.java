package com.akjava.gwt.skeltalboneanimation.client.bones;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

public class AnimationFrame {
	private double scaleX=1;
	private double scaleY=1;
	
	private TextureFrame textureFrame;
	public void setTextureFrame(TextureFrame textureFrame) {
		this.textureFrame = textureFrame;
	}
	public TextureFrame getTextureFrame(){
		return textureFrame;
	}
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
public AnimationFrame createBetween(AnimationFrame second){
	checkNotNull(second,"createBetween:second is null");
	AnimationFrame frame=new AnimationFrame();
	double newX=scaleX+(second.scaleX-scaleX)/2;
	double newY=scaleY+(second.scaleY-scaleY)/2;
	frame.setScaleX(newX);
	frame.setScaleY(newY);
	
	Set<String> boneName=Sets.newHashSet();
	for(BoneFrame bone:boneFrames.values()){
		boneName.add(bone.getBoneName());
	}
	for(BoneFrame bone:second.boneFrames.values()){
		boneName.add(bone.getBoneName());
	}
	
	for(String name:boneName){
		BoneFrame boneFrame=new BoneFrame(name);
		frame.add(boneFrame);
		
		double angleA=boneFrames.get(name)!=null?boneFrames.get(name).getAngle():0;
		double angleB=second.boneFrames.get(name)!=null?second.boneFrames.get(name).getAngle():0;
		double angleBetween=BoneUtils.betweenAngle(angleA, angleB);
		
		double xa=boneFrames.get(name)!=null?boneFrames.get(name).getX():0;
		double xb=second.boneFrames.get(name)!=null?second.boneFrames.get(name).getX():0;
		double betweenX=xa+(xb-xa)/2;
		
		double ya=boneFrames.get(name)!=null?boneFrames.get(name).getY():0;
		double yb=second.boneFrames.get(name)!=null?second.boneFrames.get(name).getY():0;
		double betweenY=ya+(yb-ya)/2;
		
		boneFrame.setX(betweenX);
		boneFrame.setY(betweenY);
		boneFrame.setAngle(angleBetween);
	}	
	return frame;
}




public AnimationFrame copy(){
	AnimationFrame frame=new AnimationFrame();
	frame.setScaleX(scaleX);
	frame.setScaleY(scaleY);
	for(BoneFrame bones:boneFrames.values()){
		frame.add(bones.copy());
	}
	
	if(getTextureFrame()!=null){
		frame.setTextureFrame(textureFrame.copy());
	}
	
	return frame;
}
}
