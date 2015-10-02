package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler.KeyDownState;

public interface CanvasDrawingDataControler {
	public void onWhelled(int delta,KeyDownState keydownState);
	public void onTouchDragged(int vectorX, int vectorY,boolean rightButton,KeyDownState keydownState);
	
	/*
	 * when this controler active return true;
	 */
	public boolean onTouchStart(int mx,int my,KeyDownState keydownState);
	public void onTouchEnd(int mx,int my,KeyDownState keydownState);
	public String getName();
}
