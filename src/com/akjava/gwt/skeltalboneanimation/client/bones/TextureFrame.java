package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.List;

import com.google.common.base.Optional;

public class TextureFrame {
	
	
	
	public TextureFrame(boolean needResetState, boolean needResetOrder) {
		super();
		this.needResetState = needResetState;
		this.needResetOrder = needResetOrder;
	}

	public TextureFrame(List<String> textureOrder, List<TextureState> textureUpdates) {
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

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		//TODO copy
	}

	public TextureFrame copy() {
		TextureFrame frame=new TextureFrame(textureOrder,textureUpdates);
		frame.setNeedResetOrder(isNeedResetOrder());
		frame.setNeedResetState(isNeedResetState());
		return frame;
	}
}
