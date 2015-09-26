package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageData;
import com.google.common.collect.Lists;

public class UploadedFileManager {

private BoneAndAnimationData boneAndAnimation;


public BoneAndAnimationData getBoneAndAnimation() {
	return boneAndAnimation;
}

public void setBoneAndAnimation(BoneAndAnimationData boneAndAnimation) {
	this.boneAndAnimation = boneAndAnimation;
	modifyBoneAndAnimation();
}

public void modifyBoneAndAnimation(){
	for(BoneAndAnimationChangeListener listener:boneChangeListeners){
		//use copy 
		listener.boneAndAnimationChanged(boneAndAnimation.copy());
	}
}

private List<BoneAndAnimationChangeListener> boneChangeListeners=Lists.newArrayList();
public void addBoneAndAnimationChangeListener(BoneAndAnimationChangeListener listener){
	boneChangeListeners.add(listener);
}

public void removeBoneAndAnimationChangeListener(BoneAndAnimationChangeListener listener){
	boneChangeListeners.remove(listener);
}

	public static interface BoneAndAnimationChangeListener{
		public void boneAndAnimationChanged(BoneAndAnimationData bone);
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
			listener.textureDataChanged(textureData.copy());
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

		public void setClipImageData(ClipImageData clipImageData) {
			this.clipImageData = clipImageData;
			modifyClipImageData();
		}

		public void modifyClipImageData(){
			for(ClipImageDataChangeListener listener:clipImageDataChangeListeners){
				listener.ClipImageDataChanged(clipImageData);
			}
		}

		private List<ClipImageDataChangeListener> clipImageDataChangeListeners=Lists.newArrayList();
		public void addClipImageDataChangeListener(ClipImageDataChangeListener listener){
			clipImageDataChangeListeners.add(listener);
		}

		public void removeClipImageDataChangeListener(ClipImageDataChangeListener listener){
			clipImageDataChangeListeners.remove(listener);
		}

			public static interface ClipImageDataChangeListener{
				public void ClipImageDataChanged(ClipImageData clipImageData);
			}
			
			
			/*
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
				*/
}
