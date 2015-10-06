package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;
import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;

public class EditDataCommand implements Command{
	private ClipData oldData;
	private ClipData newData;
	private ClipImageDataControler controler;
	private int dataIndex;
	public EditDataCommand(int dataIndex,ClipData oldData,ClipData newData, ClipImageDataControler controler) {
		super();
		this.dataIndex = dataIndex;
		this.controler = controler;
		this.oldData=oldData;
		this.newData=newData;
	}

	@Override
	public void execute() {
		
	}

	@Override
	public void undo() {
		oldData.copyTo(controler.getDataAt(dataIndex),false);
		//controler.getDataAt(dataIndex);
		controler.updateData(dataIndex);
		controler.updateDatas();

	}

	@Override
	public void redo() {
		newData.copyTo(controler.getDataAt(dataIndex),false);
		controler.updateData(dataIndex);
		controler.updateDatas();
	}
}
