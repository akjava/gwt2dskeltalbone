package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageData;
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
	
	
	private TextureData textureData;


	public TextureData getTextureData() {
		return textureData;
	}

	public void setTextureData(TextureData textureData) {
		this.textureData = textureData;
		modifyTextureData();
	}

	public void modifyTextureData(){
		for(TextureDataChangeListener listener:textureDataChangeListeners){
			listener.textureDataChanged(textureData);
		}
	}

	private List<TextureDataChangeListener> textureDataChangeListeners=Lists.newArrayList();
	public void addTextureDataChangeListener(TextureDataChangeListener listener){
		textureDataChangeListeners.add(listener);
	}

	public void removeTextureDataChangeListener(TextureDataChangeListener listener){
		textureDataChangeListeners.remove(listener);
	}

		public static interface TextureDataChangeListener{
			public void textureDataChanged(TextureData textureData);
		}
		
		private ClipImageData clipImageData;


		public ClipImageData getClipImageData() {
			return clipImageData;
		}

		public void setClipImageData(ClipImageData ClipImageData) {
			this.clipImageData = ClipImageData;
			modifyClipImageData();
		}

		public void modifyClipImageData(){
			for(ClipImageDataChangeListener listener:ClipImageDataChangeListeners){
				listener.ClipImageDataChanged(clipImageData);
			}
		}

		private List<ClipImageDataChangeListener> ClipImageDataChangeListeners=Lists.newArrayList();
		public void addClipImageDataChangeListener(ClipImageDataChangeListener listener){
			ClipImageDataChangeListeners.add(listener);
		}

		public void removeClipImageDataChangeListener(ClipImageDataChangeListener listener){
			ClipImageDataChangeListeners.remove(listener);
		}

			public static interface ClipImageDataChangeListener{
				public void ClipImageDataChanged(ClipImageData ClipImageData);
			}
			
			
			
			private SkeletalAnimation skeletalAnimation;


			public SkeletalAnimation getSkeletalAnimation() {
				return skeletalAnimation;
			}

			public void setSkeletalAnimation(SkeletalAnimation SkeletalAnimation) {
				this.skeletalAnimation = SkeletalAnimation;
				modifySkeletalAnimation();
			}

			public void modifySkeletalAnimation(){
				for(SkeletalAnimationChangeListener listener:SkeletalAnimationChangeListeners){
					listener.SkeletalAnimationChanged(skeletalAnimation);
				}
			}

			private List<SkeletalAnimationChangeListener> SkeletalAnimationChangeListeners=Lists.newArrayList();
			public void addSkeletalAnimationChangeListener(SkeletalAnimationChangeListener listener){
				SkeletalAnimationChangeListeners.add(listener);
			}

			public void removeSkeletalAnimationChangeListener(SkeletalAnimationChangeListener listener){
				SkeletalAnimationChangeListeners.remove(listener);
			}

				public static interface SkeletalAnimationChangeListener{
					public void SkeletalAnimationChanged(SkeletalAnimation SkeletalAnimation);
				}
}
