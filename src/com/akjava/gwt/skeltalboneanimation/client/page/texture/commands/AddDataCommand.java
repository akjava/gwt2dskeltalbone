package com.akjava.gwt.skeltalboneanimation.client.page.texture.commands;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.UndoDataControler;

public class AddDataCommand<T> implements Command{
	private T data;
	private UndoDataControler<T> controler;
	private int dataIndex;
	public AddDataCommand(int dataIndex, T data, UndoDataControler<T> controler) {
		super();
		this.dataIndex = dataIndex;
		this.data = data;
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
		controler.insertData(dataIndex, data);
		controler.updateDatas();
	}

}
