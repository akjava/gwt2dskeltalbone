package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

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

}
