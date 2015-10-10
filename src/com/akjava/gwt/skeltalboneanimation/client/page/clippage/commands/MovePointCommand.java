package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;

import com.akjava.gwt.lib.client.experimental.undo.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;
import com.akjava.lib.common.graphics.Point;

//from mouse move
public class MovePointCommand implements Command {
	private int dataIndex;
	private int pointIndex;
	public MovePointCommand(int dataIndex,int pointIndex, Point oldPoint,Point newPoint, ClipImageDataControler controler) {
		super();
		this.pointIndex = pointIndex;
		this.oldPoint = oldPoint;
		this.newPoint = newPoint;
		
		this.dataIndex=dataIndex;
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
		controler.getPointAt(dataIndex,pointIndex).set(oldPoint);
		controler.updatePoints();
	}

	@Override
	public void redo() {
		controler.getPointAt(dataIndex,pointIndex).set(newPoint);
		controler.updatePoints();
	}

}
