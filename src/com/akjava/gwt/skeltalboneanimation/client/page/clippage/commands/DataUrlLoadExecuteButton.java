package com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands;

import static com.google.common.base.Preconditions.checkNotNull;

import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ExecuteWaitAsyncButton;
import com.google.common.base.Supplier;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ErrorEvent;

public abstract class DataUrlLoadExecuteButton extends ExecuteWaitAsyncButton{

	private Supplier<String> dataUrlSupplier;
	
	public DataUrlLoadExecuteButton(String label,Supplier<String> dataUrlSupplier) {
		super(label);
		this.dataUrlSupplier=checkNotNull(dataUrlSupplier,"DataUrlLoadExecuteButton supplier must be not null");
	}
	
	 @Override
		public void beforeExecute() {
		
			ImageElementLoader loader=new ImageElementLoader();
			loader.load(dataUrlSupplier.get(), new ImageElementListener() {
				
				@Override
				public void onLoad(ImageElement element) {
					setReadyExecute(true);
				}
				
				@Override
				public void onError(String url, ErrorEvent event) {
					LogUtils.log("load-faild:"+url);
				}
			});
	 }
	

}
