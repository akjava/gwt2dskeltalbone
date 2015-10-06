package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.AddPointCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.MovePointCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.RemovePointCommand;
import com.akjava.lib.common.graphics.Point;

public class ClipPageUndoControler extends SimpleUndoControler{
private ClipImageDataControler controler;

public ClipPageUndoControler(ClipImageDataControler controler) {
	super();
	this.controler = controler;
}



public void execMovePoint(int dataIndex,int index,Point oldPoint,Point newPoint){
	
	execute(new MovePointCommand(dataIndex,index, oldPoint,newPoint, controler));
	
	
}

public Point execAddPoint(int dataIndex,int index,double x,double y){
	Point pt=new Point(x,y);
	
	execute(new AddPointCommand(dataIndex,index, pt, controler));
	
	return pt;
}

public void execRemovePoint(int dataIndex,int index) {
	execute(new RemovePointCommand(dataIndex,index, controler));
}

}
