package com.akjava.gwt.skeltalboneanimation.client.bones;

import static com.google.common.base.Preconditions.checkNotNull;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.ui.LabeledInputRangeWidget;
import com.google.common.base.Optional;
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
	
	/**
	 * be careful this animation replace when new data loaded.
	 * @return
	 */
	public SkeletalAnimation getAnimation() {
		return animation;
	}
	public void setAnimation(SkeletalAnimation animation) {
		this.animation = animation;
	}
	
	public void insertBefore(AnimationFrame frame){
		AnimationFrame selection=getSelection();
		if(selection==null){
			LogUtils.log("some how selection is null.quit insert");
			return;
		}
		int index=animation.getFrames().indexOf(selection);
		if(index==-1){
			LogUtils.log("some how no index.quit insert");
			return;
		}
		animation.getFrames().add(index, frame);
	}
	public void insertAfter(AnimationFrame frame){
		AnimationFrame selection=getSelection();
		if(selection==null){
			LogUtils.log("some how selection is null.quit insert");
			return;
		}
		int index=animation.getFrames().indexOf(selection);
		if(index==-1){
			LogUtils.log("some how no index.quit insert");
			return;
		}
		animation.getFrames().add(index+1, frame);
	}
	public void removeFrame(AnimationFrame frame){
		
		animation.getFrames().remove(frame);
		
	}
	
	public AnimationFrame getSelection(){
		int index=(int)(inputRange.getValue()-1);
		if(index<0 || index>=animation.getFrames().size()){
			return null;
		}
		return animation.getFrames().get(index);
	}
	public AnimationControlRange(final SkeletalAnimation animation){
		checkNotNull("AnimationControlRange:need animation");
		this.animation=animation;
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		add(panel);
		
		int animationFrameSize=animation.getFrames().size();
		int min=animationFrameSize>0?1:1;//must be 1 here
		int max=animationFrameSize>0?animationFrameSize:1;
		inputRange = new LabeledInputRangeWidget("1 of "+max , min, max, 1);
		inputRange.addtRangeListener(new ValueChangeHandler<Number>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				
				//inputRange.getNameLabel().setText(event.getValue().intValue()+" of "+animation.getFrames().size());
				
				
				AnimationFrame frame=getSelection();
				if(listener!=null){
					listener.changed(frame);//TODO support move
					
				}
				updateNameLabel();
			}
		});
		inputRange.setValue(1);
		inputRange.getTextBox().setVisible(false);
		
		if(animationFrameSize>0){
			inputRange.setValue(1);
		}
		updateNameLabel();
		
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
	 * same as last time & usually not call fire-changed
	 */
	public void syncDatas(){//for modified animation data
		inputRange.getRange().setMax(animation.getFrames().size());
		//AnimationFrame frame=getSelection();
		
		int selectionIndex=getSelectionIndex();
		
		if(selectionIndex>animation.getFrames().size()){
			inputRange.setValue(animation.getFrames().size());
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

	public Optional<AnimationFrame> getFrame(int index){
		if(index>=0 && index<getAnimationSize()){
			return Optional.fromNullable(animation.getFrames().get(index));
			
		}
		return Optional.absent();
	}
	
	public int getAnimationSize(){
		return animation.getFrames().size();
	}

	public int getSelectionIndex() {
		return (int)inputRange.getValue()-1;
	}
	public void setSelection(AnimationFrame frame,boolean fireEvent) {
		checkNotNull(frame,"setSelection:need frame");
		int index=animation.getFrames().indexOf(frame);
		
		//LogUtils.log("setSelection:"+index+","+inputRange.get);
		if(index!=-1){
			inputRange.setValue(index+1,fireEvent);
		}
		//updateNameLabel();
	}
	
	
}
