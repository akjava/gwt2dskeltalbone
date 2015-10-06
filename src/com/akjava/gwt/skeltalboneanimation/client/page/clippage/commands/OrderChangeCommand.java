package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;
import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;
/*
 * Called when
 * top,up,down,bottom pushed.
 * sync order called from other page
 */
public class OrderChangeCommand implements Command {

	private List<ClipData> oldOrder;
	private List<ClipData> newOrder;
	
	public OrderChangeCommand(List<ClipData> oldOrder,List<ClipData> newOrder, ClipImageDataControler controler) {
		super();
		this.oldOrder = oldOrder;
		this.newOrder = newOrder;
	
		this.controler = controler;
	}


	private ClipImageDataControler controler;
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
