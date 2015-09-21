package com.akjava.gwt.skeltalboneanimation.client;

public interface DrawingDataControler {
	public void onWhelled(int delta,boolean shiftDowned);
	public void onTouchDragged(int vectorX, int vectorY,boolean rightButton);
	public boolean onTouchStart(int mx,int my);
	public void onTouchEnd(int mx,int my);
	public String getName();
}
