package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import com.akjava.lib.common.graphics.Point;
import com.google.common.base.Optional;

public interface ClipImageDataControler {

	public Point getPointAt(int dataIndex,int index);
	
	public Point insertPoint(int dataIndex,int index,Point pt);
	public Point removePoint(int dataIndex,int index);
	
	public void updatePoints();
	
	
	public ClipData insertData(int dataIndex,ClipData data);
	public ClipData removeData(int dataIndex);
	public ClipData getDataAt(int dataIndex);
	public void updateDatas();
	public void updateData(int dataIndex);
	
}
