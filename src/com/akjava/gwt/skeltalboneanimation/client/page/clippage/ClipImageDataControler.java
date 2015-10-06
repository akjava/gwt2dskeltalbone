package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import com.akjava.lib.common.graphics.Point;
import com.google.common.base.Optional;

public interface ClipImageDataControler {

	public Point getPoint(int dataIndex,int index);
	
	public Point insertPoint(int dataIndex,int index,Point pt);
	public Point removePoint(int dataIndex,int index);
	
	public void updatePoints();
	public void updateDatas();
	
}
