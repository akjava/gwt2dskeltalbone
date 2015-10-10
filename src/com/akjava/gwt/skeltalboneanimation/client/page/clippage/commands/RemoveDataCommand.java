package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;
import com.akjava.gwt.lib.client.experimental.undo.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;

public class RemoveDataCommand implements Command{
	private ClipData clipData;
	private ClipImageDataControler controler;
	private int dataIndex;
	public RemoveDataCommand(int dataIndex, ClipImageDataControler controler) {
		super();
		this.dataIndex = dataIndex;
		this.controler = controler;
	}

	@Override
	public void execute() {
		clipData=controler.removeData(dataIndex);
		controler.updateDatas();
	}

	@Override
	public void undo() {
		controler.insertData(dataIndex, clipData);
		controler.updateDatas();

	}

	@Override
	public void redo() {
		controler.removeData(dataIndex);
		controler.updateDatas();
	}

}
