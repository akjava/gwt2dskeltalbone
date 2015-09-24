package com.akjava.gwt.skeltalboneanimation.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;

public class Background implements ImageDrawingDataOwner{
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
	public boolean isEditable() {
		return enableEdit;
	}
	public void setEditable(boolean enableEdit) {
		this.enableEdit = enableEdit;
	}
	private boolean visible=true;
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	private boolean selected;
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Override
	public ImageDrawingData getSelection() {
		return getBackgroundData();
	}
	@Override
	public ImageDrawingData collision(int mx, int my) {
		
		if(backgroundData==null || !isEditable() || !isVisible()){
			setSelected(false);
			return null;
		}
		
		if(backgroundData.collision(mx, my)){
			
			setSelected(true);
			return backgroundData;
		}else{
			setSelected(false);
			return null;
		}
		
	}
	
	//TODO support modify color or borders
	public void draw(Canvas canvas){
		if(this.hasBackgroundData() && this.isVisible()){
			
			this.getBackgroundData().draw(canvas);
			String border="#000";
			if(this.isSelected() && isEditable()){
			//	LogUtils.log("selected");
				border="#0f0";
				this.getBackgroundData().drawBorder(canvas,border);
			}
			
		}
	}
}
