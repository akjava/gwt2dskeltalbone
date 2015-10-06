package com.akjava.gwt.skeltalboneanimation.client.page.texture;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;

public interface UndoDataControler<T> {	
	public T insertData(int dataIndex,T data);
	public T removeData(int dataIndex);
	public T getDataAt(int dataIndex);
	public void setDatas(List<T> datas);
	public void copyToAt(T data,int dataIndex);
	
	public void updateDatas();
	public void updateData(int dataIndex);	
}
