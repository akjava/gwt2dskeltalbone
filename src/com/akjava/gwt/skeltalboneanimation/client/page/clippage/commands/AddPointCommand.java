package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;

import com.akjava.gwt.lib.client.experimental.undo.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;
import com.akjava.lib.common.graphics.Point;

public class AddPointCommand implements Command{
	private int pointIndex;
	private int dataIndex;
	public AddPointCommand(int dataIndex,int pointIndex, Point point, ClipImageDataControler controler) {
		super();
		this.pointIndex = pointIndex;
		this.point = point;
		this.controler = controler;
		this.dataIndex=dataIndex;
	}
	private Point point;
	private ClipImageDataControler controler;
	
	@Override
	public void execute() {
		redo();
	}
	@Override
	public void undo() {
		controler.removePoint(dataIndex,pointIndex);
		controler.updatePoints();
	}
	@Override
	public void redo() {
		controler.insertPoint(dataIndex,pointIndex, point.copy());
		controler.updatePoints();
	}
}
