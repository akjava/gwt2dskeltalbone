package com.akjava.gwt.skeltalboneanimation.client.page.html5app;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class DownloadButtonAndLink extends HorizontalPanel{
private Canvas source;
public Canvas getSource() {
	return source;
}

public void setSource(Canvas source) {
	this.source = source;
}

public String getDownloadLabel() {
	return downloadLabel;
}

public void setDownloadLabel(String downloadLabel) {
	this.downloadLabel = downloadLabel;
}

public String getDefaultFileName() {
	return defaultFileName;
}

public void setDefaultFileName(String defaultFileName) {
	this.defaultFileName = defaultFileName;
}

private String downloadLabel;
private String defaultFileName;

HorizontalPanel panel;
public DownloadButtonAndLink(final Canvas source,final String downloadLabel,final String defaultFileName) {
	super();
	this.source = source;
	this.downloadLabel = downloadLabel;
	this.defaultFileName = defaultFileName;
	
	ExecuteButton bt=new ExecuteButton("Save") {
		public void beforeExecute(){
			panel.clear();
		}
		@Override
		public void executeOnClick() {
			Anchor anchor=HTML5Download.get().generateBase64DownloadLink(source.toDataUrl(), "image/png", defaultFileName, downloadLabel, true);
			panel.add(anchor);
		}
	};
	this.add(bt);
	panel=new HorizontalPanel();
	panel.setWidth("40px");
	this.add(panel);
}



}
