package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;

public class ImageDrawingDataCommand implements Command{
	private ImageDrawingData beforeData;
	private ImageDrawingData afterData;
	private ImageDrawingData targetData;
	private ImageDrawingDatasUpdater updater;
	public ImageDrawingDataCommand(ImageDrawingDatasUpdater updater,ImageDrawingData targetData,ImageDrawingData beforeData, ImageDrawingData afterData, boolean collapseCommand) {
		super();
		this.beforeData = beforeData;
		this.afterData = afterData;
		this.targetData = targetData;
		this.collapseCommand = collapseCommand;
	}

	public ImageDrawingData getBeforeData() {
		return beforeData;
	}

	public void setBeforeData(ImageDrawingData beforeData) {
		this.beforeData = beforeData;
	}

	public ImageDrawingData getAfterData() {
		return afterData;
	}

	public void setAfterData(ImageDrawingData afterData) {
		this.afterData = afterData;
	}

	public ImageDrawingData getTargetData() {
		return targetData;
	}

	public void setTargetData(ImageDrawingData targetData) {
		this.targetData = targetData;
	}

	public void setCollapseCommand(boolean collapseCommand) {
		this.collapseCommand = collapseCommand;
	}

	private boolean collapseCommand;
	
	public boolean isCollapseCommand() {
		return collapseCommand;
	}

	@Override
	public void execute() {
		//afterData.copyTo(targetData);
	}

	@Override
	public void undo() {
		beforeData.copyTo(targetData);
		updater.updateImageDrawingDatas();
	}

	@Override
	public void redo() {
		afterData.copyTo(targetData);
		updater.updateImageDrawingDatas();
	}

}
