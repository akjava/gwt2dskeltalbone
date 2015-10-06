package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.AddDataCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.AddPointCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.ClipImageDataCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.EditDataCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.MovePointCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.OrderChangeCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.RemoveDataCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.RemovePointCommand;
import com.akjava.lib.common.graphics.Point;

public class ClipPageUndoControler extends SimpleUndoControler{
private ClipImageDataControler controler;

public ClipPageUndoControler(ClipImageDataControler controler) {
	super();
	this.controler = controler;
}



public void execMovePoint(int dataIndex,int index,Point oldPoint,Point newPoint){
	LogUtils.log("execMovePoint");
	execute(new MovePointCommand(dataIndex,index, oldPoint,newPoint, controler));
	
	
}

public Point execAddPoint(int dataIndex,int index,double x,double y){
	LogUtils.log("execAddPoint");
	Point pt=new Point(x,y);
	
	execute(new AddPointCommand(dataIndex,index, pt, controler));
	
	return pt;
}

public void execRemovePoint(int dataIndex,int index) {
	LogUtils.log("execRemovePoint");
	execute(new RemovePointCommand(dataIndex,index, controler));
}


public ClipData execAddData(int dataIndex,ClipData data){
	LogUtils.log("execAddData");
	
	execute(new AddDataCommand(dataIndex,data, controler));
	
	return data;
}

public void execRemoveData(int dataIndex) {
	LogUtils.log("execRemoveData");
	execute(new RemoveDataCommand(dataIndex, controler));
}

public void execEditData(int dataIndex,ClipData oldData,ClipData newData) {
	LogUtils.log("execEditData");
	execute(new EditDataCommand(dataIndex, oldData,newData,controler));
}

public void executeOrder(List<ClipData> oldOrder,List<ClipData> newOrder){
	LogUtils.log("executeOrder");
	execute(new OrderChangeCommand(oldOrder, newOrder, controler));
}
public void executeDataUpdate(List<ClipData> oldDatas,List<ClipData> newDatas){
	LogUtils.log("executeDataUpdate");
	execute(new ClipImageDataCommand(oldDatas, newDatas, controler));
}

}
