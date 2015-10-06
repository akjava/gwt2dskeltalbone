package com.akjava.gwt.skeltalboneanimation.client.page.texture;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.commands.AddDataCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.commands.EditDataCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.commands.OrderChangeCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.commands.RemoveDataCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.commands.ReplaceDatasCommand;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public abstract class EasyCellTableObjectsUndoControler<T> extends SimpleUndoControler implements UndoDataControler<T>{

	private EasyCellTableObjects<T> easyCellTableObjects;
	public EasyCellTableObjects<T> getEasyCellTableObjects() {
		return easyCellTableObjects;
	}

	public EasyCellTableObjectsUndoControler(DataUpdater dataUpdater) {
		this(null,dataUpdater);
	}
	
	public EasyCellTableObjectsUndoControler(EasyCellTableObjects<T> easyCellTableObjects, DataUpdater dataUpdater) {
		super();
		this.easyCellTableObjects = easyCellTableObjects;
		this.dataUpdater = dataUpdater;
	}

	public void setEasyCellTableObjects(EasyCellTableObjects<T> easyCellTableObjects) {
		this.easyCellTableObjects = easyCellTableObjects;
	}
	private DataUpdater dataUpdater;
	
	
	@Override
	public T insertData(int dataIndex, T data) {
		easyCellTableObjects.insertItem(dataIndex,data);
		return data;
	}

	@Override
	public T removeData(int dataIndex) {
		return easyCellTableObjects.removeItem(dataIndex);
	}

	@Override
	public T getDataAt(int dataIndex) {
		Optional<T> data=easyCellTableObjects.getItemAt(dataIndex);
		if(data.isPresent()){
			return data.get();
		}
		return null;
	}

	@Override
	public void setDatas(List<T> datas) {
		List<T> newDatas=Lists.newArrayList();
		
		for(T data:datas){
			newDatas.add(data);
		}
		
		easyCellTableObjects.setDatas(newDatas);
	}

	public abstract void copyData(T fromData,T toData);
	public abstract void updatedData(T data);
	
	@Override
	public void copyToAt(T data, int dataIndex) {
		for(T targetData:easyCellTableObjects.getItemAt(dataIndex).asSet()){
			copyData(data,targetData);
			return;
		}
		LogUtils.log("copyToAt:invalidly can't find data:"+dataIndex);
	}
	

	@Override
	public void updateDatas() {
		dataUpdater.updateDatas();
	}

	//need driver
	@Override
	public void updateData(int dataIndex) {
		for(T targetData:easyCellTableObjects.getItemAt(dataIndex).asSet()){
			updatedData(targetData);
			return;
		}
		LogUtils.log("updateData:invalidly can't find data:"+dataIndex);
	}
	
	public List<T> copyOrder(){
		List<T> order=Lists.newArrayList();
		for(T data:easyCellTableObjects.getDatas()){
			order.add(data);
		}
		return order;
	}
	
	public T execAddData(int dataIndex,T data){
		LogUtils.log("execAddData");
		
		execute(new AddDataCommand<T>(dataIndex,data, this));
		
		return data;
	}

	public void execRemoveData(int dataIndex) {
		LogUtils.log("execRemoveData");
		execute(new RemoveDataCommand<T>(dataIndex, this));
	}

	public void execEditData(int dataIndex,T oldData,T newData,boolean collapse) {
		
		if(collapse){//usually mouse wheel event.
		for(EditDataCommand<T> lastCommand:getLastEditDataCommandIfExist().asSet()){
			lastCommand.setNewData(newData);
			return;
			}
		}
		
		LogUtils.log("execEditData");
		execute(new EditDataCommand<T>(dataIndex, oldData,newData,collapse,this));
	}
	
	private Optional<EditDataCommand<T>> getLastEditDataCommandIfExist(){
		
		for(Command command:this.getLastUndoCommand().asSet()){
			if(command instanceof EditDataCommand){
				@SuppressWarnings("unchecked")
				EditDataCommand<T> dCommand=(EditDataCommand<T>)command;
				if(dCommand.isCollapse()){
					return Optional.of(dCommand);
				}
				
			}
		}
		return Optional.absent();
	}

	public void executeOrder(List<T> oldOrder,List<T> newOrder){
		LogUtils.log("executeOrder");
		execute(new OrderChangeCommand<T>(oldOrder, newOrder, this));
	}
	public void executeDataUpdate(List<T> oldDatas,List<T> newDatas){
		LogUtils.log("executeDataUpdate");
		execute(new ReplaceDatasCommand<T>(oldDatas, newDatas, this));
	}

}
