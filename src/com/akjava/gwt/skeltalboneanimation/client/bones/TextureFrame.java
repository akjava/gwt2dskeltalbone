package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.List;

import com.google.common.base.Optional;

public class TextureFrame {
	
	
	
	public TextureFrame(boolean needResetState, boolean needResetOrder) {
		super();
		this.needResetState = needResetState;
		this.needResetOrder = needResetOrder;
	}

	public TextureFrame(List<TextureState> textureUpdates, List<String> textureOrder) {
		super();
		this.textureUpdates = textureUpdates;
		this.textureOrder = textureOrder;
	}

	public boolean needResetState;
	public boolean isNeedResetState() {
		return needResetState;
	}
	public void setNeedResetState(boolean needResetState) {
		this.needResetState = needResetState;
	}
	public boolean isNeedResetOrder() {
		return needResetOrder;
	}
	public void setNeedResetOrder(boolean needResetOrder) {
		this.needResetOrder = needResetOrder;
	}
	public void setTextureUpdates(List<TextureState> textureUpdates) {
		this.textureUpdates = textureUpdates;
	}
	public void setTextureOrder(List<String> textureOrder) {
		this.textureOrder = textureOrder;
	}

	public boolean needResetOrder;
	
	
	
	
	private List<TextureState> textureUpdates;
	private List<String> textureOrder;
	
	public Optional<List<String>> getTextureOrder(){
		return Optional.fromNullable(textureOrder);
	}
	public Optional<List<TextureState>> getTextureUpdates(){
		return Optional.fromNullable(textureUpdates);
	}
	
	//reset order
	//reset texturestate
	//new order
	//state changes
	
	//state control,textureData	
	
	public static class TextureState{
		private String id;
	}
}
