package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.AddPointCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.RemovePointCommand;
import com.akjava.lib.common.graphics.Point;

public class ClipPageUndoControler extends SimpleUndoControler{
private ClipImageDataControler controler;

public ClipPageUndoControler(ClipImageDataControler controler) {
	super();
	this.controler = controler;
}

public Point execAddPoint(int index,double x,double y){
	Point pt=new Point(x,y);
	
	execute(new AddPointCommand(index, pt, controler));
	
	return pt;
}

public void execRemovePoint(int index) {
	execute(new RemovePointCommand(index, controler));
}

}
