package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;

import com.akjava.gwt.lib.client.experimental.undo.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;

public class AddDataCommand implements Command{
	private ClipData clipData;
	private ClipImageDataControler controler;
	private int dataIndex;
	public AddDataCommand(int dataIndex, ClipData clipData, ClipImageDataControler controler) {
		super();
		this.dataIndex = dataIndex;
		this.clipData = clipData;
		this.controler = controler;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		redo();
	}

	@Override
	public void undo() {
		controler.removeData(dataIndex);
		controler.updateDatas();
	}

	@Override
	public void redo() {
		controler.insertData(dataIndex, clipData);
		controler.updateDatas();
	}

}
