package com.akjava.gwt.skeltalboneanimation.client.bones;

import com.akjava.gwt.skeltalboneanimation.client.ui.LabeledInputRangeWidget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AnimationControlRange extends VerticalPanel{

	private LabeledInputRangeWidget inputRange;
	public LabeledInputRangeWidget getInputRange() {
		return inputRange;
	}

	private SkeletalAnimation animation;
	
	public SkeletalAnimation getAnimation() {
		return animation;
	}
	public void setAnimation(SkeletalAnimation animation) {
		this.animation = animation;
	}
	public AnimationFrame getSelection(){
		int index=(int)(inputRange.getValue()-1);
		if(index<0 || index>=animation.getFrames().size()){
			return null;
		}
		return animation.getFrames().get(index);
	}
	public AnimationControlRange(final SkeletalAnimation animation){
		this.animation=animation;
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		add(panel);
		
		inputRange = new LabeledInputRangeWidget("1 of "+ animation.getFrames().size(), 1, animation.getFrames().size(), 1);
		inputRange.addtRangeListener(new ValueChangeHandler<Number>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				
				inputRange.getNameLabel().setText(event.getValue().intValue()+" of "+animation.getFrames().size());
				
				AnimationFrame frame=getSelection();
				if(listener!=null){
					listener.changed(frame);//TODO support move
				}
			}
		});
		inputRange.setValue(1);
		
		panel.add(inputRange);
		
		Button reset=new Button("First",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				inputRange.setValue(1);
			}
		});
		panel.add(reset);
		
		Button prev=new Button("Prev",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int v=(int) inputRange.getValue();
				v-=1;
				if(v<=0){
					v=(int) inputRange.getRange().getMax();
				}
				inputRange.setValue(v);
			}
		});
		panel.add(prev);
		
		Button next=new Button("Next",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int v=(int) inputRange.getValue();
				v+=1;
				if(v> inputRange.getRange().getMax()){
					v=1;
				}
				inputRange.setValue(v);
			}
		});
		panel.add(next);
		
		}
	
	public void updateNameLabel(){
		int value=(int)inputRange.getValue();
		inputRange.getNameLabel().setText(value+" of "+((int)inputRange.getRange().getMax()));
	}
	/*
	 * possible,not fire event
	 */
	public void syncDatas(){//for modified animation data
		inputRange.getRange().setMax(animation.getFrames().size());
		AnimationFrame frame=getSelection();
		if(frame==null){
			inputRange.setValue(1);//first;
		}else{
			int index=animation.getFrames().indexOf(frame);
			if(index!=-1){
				inputRange.setValue(index+1);
			}else{
				int selectionIndex=(int)inputRange.getValue()-1;
				if(selectionIndex<animation.getFrames().size()){
					inputRange.setValue(selectionIndex);//this is same value & possible not fire update
				}else{
					inputRange.setValue(animation.getFrames().size());
				}
				//inputRange.setValue(1);
			}
		}
		updateNameLabel();
	}
	
	private AnimationControlListener listener;
		
		public AnimationControlListener getListener() {
		return listener;
	}

	public void setListener(AnimationControlListener listener) {
		this.listener = listener;
	}

	
	public static interface AnimationControlListener{
		public void changed(AnimationFrame frame);
	}
	
	
}
