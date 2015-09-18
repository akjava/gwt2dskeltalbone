package com.akjava.gwt.skeltalboneanimation.client.bones;

import com.google.gwt.canvas.client.Canvas;

public abstract class CanvasBonePainter extends AbstractBonePainter {
public CanvasBonePainter(BonePositionControler positionControler) {
		super(positionControler);
	}

protected Canvas canvas;

public Canvas getCanvas() {
	return canvas;
}

public void setCanvas(Canvas canvas) {
	this.canvas = canvas;
}

}
