package com.akjava.gwt.skeltalboneanimation.client;

import com.google.gwt.dom.client.ImageElement;

public class Background {
	private ImageDrawingData backgroundData;
	public ImageDrawingData getBackgroundData() {
		return backgroundData;
	}
	public void setBackgroundData(ImageDrawingData backgroundData) {
		this.backgroundData = backgroundData;
	}
	public void setBackground(String name,ImageElement imageElement){
		backgroundData=new ImageDrawingData(name, imageElement);
	}
	public void clear(){
		backgroundData=null;
	}
	public boolean hasBackgroundData(){
		return backgroundData!=null;
	}
	private boolean enableEdit;
	public boolean isEnableEdit() {
		return enableEdit;
	}
	public void setEnableEdit(boolean enableEdit) {
		this.enableEdit = enableEdit;
	}
	private boolean visible=true;
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
