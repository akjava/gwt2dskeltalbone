package com.akjava.gwt.skeltalboneanimation.client.page.texture.commands;
import com.akjava.gwt.lib.client.experimental.undo.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.UndoDataControler;

public class RemoveDataCommand<T> implements Command{
	private T clipData;
	private UndoDataControler<T> controler;
	private int dataIndex;
	public RemoveDataCommand(int dataIndex, UndoDataControler<T> controler) {
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
