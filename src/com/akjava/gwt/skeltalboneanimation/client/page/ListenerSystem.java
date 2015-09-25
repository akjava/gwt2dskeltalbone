package com.akjava.gwt.skeltalboneanimation.client.page;

import java.util.List;

import com.google.common.collect.Lists;

public class ListenerSystem<T> {
	private T data;
	private DataOwner owner;

	public DataOwner getOwner() {
		return owner;
	}

	public void setOwner(DataOwner owner) {
		this.owner = owner;
	}

	public T getData() {
		return data;
	}

	public void setData(T data,DataOwner owner) {
		this.data = data;
		modifyData();
	}

	public void modifyData(){
		for(DataChangeListener<T> listener:listeners){
			listener.dataChanged(data,owner);
		}
	}

	private List<DataChangeListener<T>> listeners=Lists.newArrayList();
	public void addListener(DataChangeListener<T> listener){
		listeners.add(listener);
	}

	public void removeClipImageDataChangeListener(DataChangeListener<T> listener){
		listeners.remove(listener);
	}

		public static interface DataChangeListener<T>{
			public void dataChanged(T data,DataOwner owner);
		}
		
		public static interface DataOwner{
			public String getName();
		}
}
