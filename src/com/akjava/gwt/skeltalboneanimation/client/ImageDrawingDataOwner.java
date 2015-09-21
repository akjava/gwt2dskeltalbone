package com.akjava.gwt.skeltalboneanimation.client;
public  interface ImageDrawingDataOwner{
	public ImageDrawingData getSelection();
	public boolean isEditable();
	public ImageDrawingData collision(int mx,int my);
}