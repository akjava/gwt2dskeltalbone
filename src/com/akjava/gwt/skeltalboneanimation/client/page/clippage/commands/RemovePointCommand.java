package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;
import com.akjava.lib.common.graphics.Point;

public class RemovePointCommand implements Command{
	private int pointIndex;
	private int dataIndex;
	public RemovePointCommand(int dataIndex,int pointIndex, ClipImageDataControler controler) {
		super();
		this.pointIndex = pointIndex;
		this.controler = controler;
		this.dataIndex=dataIndex;
	}
	private Point point;
	private ClipImageDataControler controler;
	
	@Override
	public void execute() {
		point=controler.removePoint(dataIndex,pointIndex).copy();
	}
	@Override
	public void undo() {
		controler.insertPoint(dataIndex,pointIndex, point.copy());
		controler.updatePoints();
	}
	@Override
	public void redo() {
		controler.removePoint(dataIndex,pointIndex).copy();
		controler.updatePoints();
	}
}
