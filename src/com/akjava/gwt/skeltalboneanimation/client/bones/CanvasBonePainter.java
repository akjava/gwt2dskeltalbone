package com.akjava.gwt.skeltalboneanimation.client.bones;

import com.google.gwt.canvas.client.Canvas;

public abstract class CanvasBonePainter extends AbstractBonePainter{

	protected Canvas canvas;
	public CanvasBonePainter(Canvas canvas){
		this.canvas=canvas;
		offsetX=canvas.getCoordinateSpaceWidth()/2;
		offsetY=canvas.getCoordinateSpaceHeight()/2;
	}
	@Override
	public void startPaint() {
		//do nothing
	}

	@Override
	public void endPaint() {
		//do nothing
	}

	

}
