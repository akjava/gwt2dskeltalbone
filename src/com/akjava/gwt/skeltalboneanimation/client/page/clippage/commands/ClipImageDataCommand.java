package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;
import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageDataControler;
/*
 * Called when
 * create data from bone
 * remove all
 * 
 */
public class ClipImageDataCommand implements Command {

	private List<ClipData> oldData;
	private List<ClipData> newData;
	
	/*
	 * newData must be not ref as old.
	 */
	public ClipImageDataCommand(List<ClipData> oldOrder,List<ClipData> newOrder, ClipImageDataControler controler) {
		super();
		this.oldData = oldOrder;
		this.newData = newOrder;
	
		this.controler = controler;
	}


	private ClipImageDataControler controler;
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
