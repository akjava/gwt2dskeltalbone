package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataOwner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainManager {
private TabLayoutPanel tab;
public static final int TransparentPageIndex=3;
public static final int ColorPickPageIndex=5;
public MainManager(final TabLayoutPanel tab) {
	super();
	this.tab = tab;
	tab.addSelectionHandler(new SelectionHandler<Integer>() {
		@Override
		public void onSelection(SelectionEvent<Integer> event) {
			//casting widget is not smart.
			AbstractPage page=(AbstractPage)tab.getWidget(event.getSelectedItem());
			page.onSelectedFromTab();
		}
	});

}

public int getTabIndex(Widget child){
	return tab.getWidgetIndex(child);
}
public void selectTab(int index){
	tab.selectTab(index);
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

/*
private List<SkeltalBonePage> pages=Lists.newArrayList();
public  void addPage(SkeltalBonePage page){
	pages.add(page);
}
public SkeltalBonePage getPageAt(int index){
	return pages.get(index);
}
*/

public boolean isSelected(AbstractPage page){
	int index=getTabIndex(page);
	
	return index==tab.getSelectedIndex();
}

public void setTextureData(String fileName,TextureData textureData){
	getFileManagerBar().setTexture(fileName, textureData);
}
/*
 * this auto replace newer bone
 */
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

/*
 * return always newest bone
 */
public Supplier<TwoDimensionBone> getBoneSupplier(){
	return Suppliers.memoize(new Supplier<TwoDimensionBone>(){
		@Override
		public TwoDimensionBone get() {
			return getBone();
		}
		
	});
}



}
