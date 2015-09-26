package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataOwner;
import com.akjava.gwt.skeltalboneanimation.client.page.SkeltalBonePage;
import com.google.common.collect.Lists;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class MainManager {
private TabLayoutPanel tab;

public MainManager(TabLayoutPanel tab) {
	super();
	this.tab = tab;
	tab.addSelectionHandler(new SelectionHandler<Integer>() {
		@Override
		public void onSelection(SelectionEvent<Integer> event) {
			MainManager.this.getPageAt(event.getSelectedItem()).onSelection();
		}
	});

}

private UploadedFileManager uploadedFileManager=new UploadedFileManager();
private FileManagerBar fileManagerBar;

public FileManagerBar getFileManagerBar() {
	return fileManagerBar;
}

public void setFileManagerBar(FileManagerBar fileManagerBar) {
	this.fileManagerBar = fileManagerBar;
}

public UploadedFileManager getUploadedFileManager() {
	return uploadedFileManager;
}

public void setUploadedFileManager(UploadedFileManager uploadedFileManager) {
	this.uploadedFileManager = uploadedFileManager;
}

private List<SkeltalBonePage> pages=Lists.newArrayList();
public  void addPage(SkeltalBonePage page){
	pages.add(page);
}
public SkeltalBonePage getPageAt(int index){
	return pages.get(index);
}

public boolean isSelected(SkeltalBonePage page){
	int index=pages.indexOf(page);
	return index==tab.getSelectedIndex();
}

public void setTextureData(String fileName,TextureData textureData){
	getFileManagerBar().setTexture(fileName, textureData);
}
public TextureData getTextureData(){
	TextureData data= getUploadedFileManager().getTextureData();
	if(data!=null){
		data.setBone(getBone());//replace latest bone
	}
	return data;
}

public BoneAndAnimationData getBoneAndAnimation() {
	return getUploadedFileManager().getBoneAndAnimation();
}

private TwoDimensionBone getBone() {
	return getUploadedFileManager().getBoneAndAnimation().getBone();
}
public void setTextureOrder(List<String> names,DataOwner owner){
	textureOrderSystem.setData(names, owner);	
}

public ListenerSystem<List<String>> textureOrderSystem=new ListenerSystem<List<String>>();

public ListenerSystem<List<String>> getTextureOrderSystem() {
	return textureOrderSystem;
}



}
