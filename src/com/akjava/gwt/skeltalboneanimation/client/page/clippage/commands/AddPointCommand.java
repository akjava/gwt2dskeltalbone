package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;
import com.akjava.lib.common.graphics.Point;

public class AddPointCommand implements Command{
	private int pointIndex;
	public AddPointCommand(int pointIndex, Point point, ClipImageDataControler controler) {
		super();
		this.pointIndex = pointIndex;
		this.point = point;
		this.controler = controler;
	}
	private Point point;
	private ClipImageDataControler controler;
	
	@Override
	public void execute() {
		redo();
	}
	@Override
	public void undo() {
		controler.removePoint(pointIndex);
	}
	@Override
	public void redo() {
		controler.insertPoint(pointIndex, point.copy());
	}
}
