package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import static com.google.common.base.Preconditions.checkNotNull;

import com.akjava.gwt.lib.client.experimental.undo.Command;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;

public class ImageDrawingDataCommand implements Command{
	private ImageDrawingData beforeData;
	private ImageDrawingData afterData;
	private ImageDrawingData targetData;
	private ImageDrawingDatasUpdater updater;
	public ImageDrawingDataCommand(ImageDrawingDatasUpdater updater,ImageDrawingData targetData,ImageDrawingData beforeData, ImageDrawingData afterData, boolean collapseCommand) {
		super();
		this.beforeData = checkNotNull(beforeData);
		this.afterData = checkNotNull(afterData);
		this.targetData = checkNotNull(targetData);
		this.collapseCommand = collapseCommand;//for wheel-action
		this.updater=checkNotNull(updater);
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
		
		beforeData.copyToWithoutImageElementAndId(targetData);
		updater.updateImageDrawingDatas();
	}

	@Override
	public void redo() {
		afterData.copyToWithoutImageElementAndId(targetData);
		updater.updateImageDrawingDatas();
	}

}
