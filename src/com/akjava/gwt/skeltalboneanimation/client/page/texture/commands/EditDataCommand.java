package com.akjava.gwt.skeltalboneanimation.client.page.texture.commands;
import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.UndoDataControler;

public class EditDataCommand<T> implements Command{
	private T oldData;
	private T newData;
	private UndoDataControler<T> controler;
	private int dataIndex;
	private boolean collapse;
	public boolean isCollapse() {
		return collapse;
	}

	public EditDataCommand(int dataIndex,T oldData,T newData,boolean collapse,UndoDataControler<T> controler) {
		super();
		this.dataIndex = dataIndex;
		this.controler = controler;
		this.oldData=oldData;
		this.newData=newData;
		this.collapse=collapse;
	}

	@Override
	public void execute() {
		
	}

	@Override
	public void undo() {
		controler.copyToAt(oldData,dataIndex);
		
		//controler.getDataAt(dataIndex);
		controler.updateData(dataIndex);
		controler.updateDatas();

	}

	@Override
	public void redo() {
		controler.copyToAt(newData,dataIndex);
		controler.updateData(dataIndex);
		controler.updateDatas();
	}
}
