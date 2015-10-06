package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;

import com.akjava.lib.common.graphics.Point;

public interface ClipImageDataControler {

	public Point getPointAt(int dataIndex,int index);
	
	public Point insertPoint(int dataIndex,int index,Point pt);
	public Point removePoint(int dataIndex,int index);
	
	public void updatePoints();
	
	
	public ClipData insertData(int dataIndex,ClipData data);
	public ClipData removeData(int dataIndex);
	public ClipData getDataAt(int dataIndex);
	public void setDatas(List<ClipData> dats);
	
	public void updateDatas();
	public void updateData(int dataIndex);
	
}
