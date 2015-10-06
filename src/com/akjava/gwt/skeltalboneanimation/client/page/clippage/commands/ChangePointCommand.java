package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;
import com.akjava.lib.common.graphics.Point;

//from mouse move
public class ChangePointCommand implements Command {

	private int pointIndex;
	public ChangePointCommand(int pointIndex, Point oldPoint,Point newPoint, ClipImageDataControler controler) {
		super();
		this.pointIndex = pointIndex;
		this.oldPoint = oldPoint;
		this.newPoint = newPoint;
		
		this.controler = controler;
	}

	private Point newPoint;
	private Point oldPoint;
	private ClipImageDataControler controler;
	@Override
	public void execute() {
		//already moved.
	}

	@Override
	public void undo() {
		controler.getPoint(pointIndex).set(oldPoint);
		controler.updatePoints();
	}

	@Override
	public void redo() {
		controler.getPoint(pointIndex).set(newPoint);
		controler.updatePoints();
	}

}
