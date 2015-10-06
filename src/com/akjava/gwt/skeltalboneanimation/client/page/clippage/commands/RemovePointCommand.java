package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;
import com.akjava.lib.common.graphics.Point;

public class RemovePointCommand implements Command{
	private int pointIndex;
	public RemovePointCommand(int pointIndex, ClipImageDataControler controler) {
		super();
		this.pointIndex = pointIndex;
		this.controler = controler;
	}
	private Point point;
	private ClipImageDataControler controler;
	
	@Override
	public void execute() {
		point=controler.removePoint(pointIndex).copy();
	}
	@Override
	public void undo() {
		controler.insertPoint(pointIndex, point.copy());
	}
	@Override
	public void redo() {
		controler.removePoint(pointIndex).copy();
	}
}
