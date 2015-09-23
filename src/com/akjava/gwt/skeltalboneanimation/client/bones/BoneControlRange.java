package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.ui.LabeledInputRangeWidget;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BoneControlRange extends VerticalPanel{

	private ValueListBox<TwoDimensionBone> boneListBox;

	private Map<String,TwoDimensionBone> boneMap=new HashMap<String, TwoDimensionBone>();
	private Map<String,Integer> rangeMap=new HashMap<String, Integer>();

	private LabeledInputRangeWidget inputRange;
	public LabeledInputRangeWidget getInputRange() {
		return inputRange;
	}
	
	public void setFrame(AnimationFrame frame){
		rangeMap.clear();
		for(BoneFrame boneFrame:frame.getBoneFrames().values()){
			rangeMap.put(boneFrame.getBoneName(),(int) boneFrame.getAngle());
		}
		
		String selection=boneListBox.getValue().getName();
		inputRange.setValue(MoreObjects.firstNonNull(rangeMap.get(selection), 0));
	}

	public BoneControlRange(TwoDimensionBone rootBone){
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		add(panel);
		
		
		//bone list
		
		boneListBox = new ValueListBox<TwoDimensionBone>(new Renderer<TwoDimensionBone>(){

			@Override
			public String render(TwoDimensionBone object) {
				if(object==null){
					return null;
				}
				return object.getName();
			}

			@Override
			public void render(TwoDimensionBone object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		
		panel.add(boneListBox);
		boneListBox.addValueChangeHandler(new ValueChangeHandler<TwoDimensionBone>() {

			@Override
			public void onValueChange(ValueChangeEvent<TwoDimensionBone> event) {
				TwoDimensionBone bone=event.getValue();
				if(bone==null){
					inputRange. getRange().setVisible(false);//TODO enabled
					return;
				}else{
					inputRange. getRange().setVisible(true);
				}
				inputRange.setValue(MoreObjects.firstNonNull(rangeMap.get(bone.getName()), 0),false);
				if(listener!=null){
					listener.changed(getSelection(),(int)inputRange.getValue(), 0, 0);//TODO support move
				}
			}
		});
		
		inputRange = new LabeledInputRangeWidget("Angle:", -180, 180, 1);
		inputRange.addtRangeListener(new ValueChangeHandler<Number>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				//LogUtils.log("on-range-changed");
				TwoDimensionBone selection=getSelection();
				if(selection==null){
					return;
				}
				rangeMap.put(selection.getName(), event.getValue().intValue());
				
				if(listener!=null){
					listener.changed(getSelection(), event.getValue().intValue(), 0, 0);//TODO support move
				}
			}
		});
		
		panel.add(inputRange);
		
		Button reset=new Button("Reset",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				inputRange.setValue(0);
			}
		});
		panel.add(reset);
		
		setRootBone(rootBone);
			
		}
	private BoneControlListener listener;
		
		public BoneControlListener getListener() {
		return listener;
	}

	public void setListener(BoneControlListener listener) {
		this.listener = listener;
	}

	//label-range
		//set-change listener
		//select(if not found ,select root)
	public void setRootBone(TwoDimensionBone rootBone){
		Preconditions.checkNotNull(rootBone);
		rangeMap.clear();
		
		List<TwoDimensionBone> bones=BoneUtils.getAllBone(rootBone);
		boneListBox.setValue(rootBone);
		boneListBox.setAcceptableValues(bones);
		
		boneMap.clear();
		for(TwoDimensionBone bone:bones){
			boneMap.put(bone.getName(), bone);
		}
	}
	public static interface BoneControlListener{
		public void changed(TwoDimensionBone bone,int angle,int moveX,int moveY);
	}
	public TwoDimensionBone getBoneByName(String name){
		return boneMap.get(name);
	}
	
	public void setSelection(TwoDimensionBone value){
		if(value!=null)
		 boneListBox.setValue(value,true);
	}
	
	public TwoDimensionBone getSelection(){
		return boneListBox.getValue();
	}
	
}
