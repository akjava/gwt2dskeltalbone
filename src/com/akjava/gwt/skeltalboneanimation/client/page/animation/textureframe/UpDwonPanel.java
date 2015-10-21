package com.akjava.gwt.skeltalboneanimation.client.page.animation.textureframe;

import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UpDwonPanel<T> extends HorizontalPanel implements HasEnabled{

	EasyCellTableObjects<T> easyCellTableObjects;
	public T getValue(){
		return easyCellTableObjects.getSelection();
	}
	
	public UpDwonPanel(final EasyCellTableObjects<T> easyCellTableObjects,final Updater updater){
		super();
		this.easyCellTableObjects=easyCellTableObjects;
		
		
		this.add(new Button("Top",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if(getValue()==null){
					return;
				}
				
				easyCellTableObjects.topItem(getValue());
				
				updater.update();
				
			}
		}));
		this.add(new Button("Up",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(getValue()==null){
					return;
				}
				easyCellTableObjects.upItem(getValue());
				updater.update();
			}
		}));
		this.add(new Button("Down",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(getValue()==null){
					return;
				}
				easyCellTableObjects.downItem(getValue());
				updater.update();
			}
		}));
		this.add(new Button("Bottom",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(getValue()==null){
					return;
				}
				easyCellTableObjects.bottomItem(getValue());
				updater.update();
			}
		}));
		
	}
	
	private boolean enabled=true;
	public void setEnabled(boolean enabled){
		this.enabled=enabled;
		for(Widget widget: getChildren()){
			if(widget instanceof HasEnabled){
				((HasEnabled)widget).setEnabled(enabled);
			}
		}
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}
}
