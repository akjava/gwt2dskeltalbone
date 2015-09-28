package com.akjava.gwt.skeltalboneanimation.client;

public interface CanvasDrawingDataControler {
	public void onWhelled(int delta,boolean shiftDowned);
	public void onTouchDragged(int vectorX, int vectorY,boolean rightButton);
	
	/*
	 * when this controler active return true;
	 */
	public boolean onTouchStart(int mx,int my);
	public void onTouchEnd(int mx,int my);
	public String getName();
}
