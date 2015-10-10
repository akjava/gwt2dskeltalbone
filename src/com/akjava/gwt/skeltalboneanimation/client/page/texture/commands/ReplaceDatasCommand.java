package com.akjava.gwt.skeltalboneanimation.client.page.texture.commands;
import java.util.List;

import com.akjava.gwt.lib.client.experimental.undo.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.UndoDataControler;
/*
 * Called when
 * create data from bone
 * remove all
 * 
 */
public class ReplaceDatasCommand<T> implements Command {

	private List<T> oldData;
	private List<T> newData;
	
	/*
	 * newData must be not ref as old.
	 */
	public ReplaceDatasCommand(List<T> oldOrder,List<T> newOrder, UndoDataControler<T> controler) {
		super();
		this.oldData = oldOrder;
		this.newData = newOrder;
	
		this.controler = controler;
	}


	private UndoDataControler<T> controler;
	@Override
	public void execute() {
		//already changed.
	}

	@Override
	public void undo() {
		controler.setDatas(oldData);
		controler.updateDatas();
	}
	
	
	

	@Override
	public void redo() {
		controler.setDatas(newData);
		controler.updateDatas();
	}

}
