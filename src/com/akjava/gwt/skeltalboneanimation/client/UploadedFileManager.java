package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.google.common.collect.Lists;

public class UploadedFileManager {

private TwoDimensionBone bone;


public TwoDimensionBone getBone() {
	return bone;
}

public void setBone(TwoDimensionBone bone) {
	this.bone = bone;
	for(BoneChangeListener listener:boneChangeListeners){
		listener.boneChanged(bone);
	}
}

public void modifyBone(){
	for(BoneChangeListener listener:boneChangeListeners){
		listener.boneChanged(bone);
	}
}

private List<BoneChangeListener> boneChangeListeners=Lists.newArrayList();
public void addBoneChangeListener(BoneChangeListener listener){
	boneChangeListeners.add(listener);
}

public void removeBoneChangeListener(BoneChangeListener listener){
	boneChangeListeners.remove(listener);
}

	public static interface BoneChangeListener{
		public void boneChanged(TwoDimensionBone bone);
	}
	
	
	private ImageDrawingData backgroundData;


	public ImageDrawingData getBackgroundData() {
		return backgroundData;
	}

	public void setBackgroundData(ImageDrawingData backgroundData) {
		this.backgroundData = backgroundData;
		modifyBackground();
	}
	private List<BackgroundChangeListener> backgroundChangeListeners=Lists.newArrayList();
	public void addBackgroundChangeListener(BackgroundChangeListener listener){
		backgroundChangeListeners.add(listener);
	}

	public void removeBackgroundChangeListener(BackgroundChangeListener listener){
		backgroundChangeListeners.remove(listener);
	}

	public static interface BackgroundChangeListener{
		public void backgroundChanged(ImageDrawingData backgroundDat);
	}
	public void modifyBackground(){
		for(BackgroundChangeListener listener:backgroundChangeListeners){
			listener.backgroundChanged(backgroundData);
		}
	}
}
