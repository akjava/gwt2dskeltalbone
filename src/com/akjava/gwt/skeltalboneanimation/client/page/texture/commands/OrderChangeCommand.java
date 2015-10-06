package com.akjava.gwt.skeltalboneanimation.client.page.texture.commands;
import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.UndoDataControler;
/*
 * Called when
 * top,up,down,bottom pushed.
 * sync order called from other page
 */
public class OrderChangeCommand<T> implements Command {

	private List<T> oldOrder;
	private List<T> newOrder;
	
	public OrderChangeCommand(List<T> oldOrder,List<T> newOrder, UndoDataControler<T> controler) {
		super();
		this.oldOrder = oldOrder;
		this.newOrder = newOrder;
	
		this.controler = controler;
	}


	private UndoDataControler<T> controler;
	@Override
	public void execute() {
		//already changed.
	}

	@Override
	public void undo() {
		controler.setDatas(oldOrder);
		controler.updateDatas();
	}
	
	
	

	@Override
	public void redo() {
		controler.setDatas(newOrder);
		controler.updateDatas();
	}
}
