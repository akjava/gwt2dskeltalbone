package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.ui.LabeledInputRangeWidget;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BoneControlRange extends VerticalPanel{

	private ValueListBox<TwoDimensionBone> boneListBox;

	private Map<String,TwoDimensionBone> boneMap=new HashMap<String, TwoDimensionBone>();
	private Map<String,BoneFrame> rangeMap=new HashMap<String, BoneFrame>();

	private LabeledInputRangeWidget inputRange;

	private IntegerBox yBox;
	public LabeledInputRangeWidget getInputRange() {
		return inputRange;
	}
	
	
	
	
	private int getBoneFrameIntAngle(String boneName){
		if(rangeMap.get(boneName)==null){
			return 0;
		}
		return (int)rangeMap.get(boneName).getAngle();
	}
	private int getBoneFrameX(String boneName){
		if(rangeMap.get(boneName)==null){
			return 0;
		}
		return (int)rangeMap.get(boneName).getX();
	}
	private int getBoneFrameY(String boneName){
		if(rangeMap.get(boneName)==null){
			return 0;
		}
		return (int)rangeMap.get(boneName).getY();
	}
	private void setBoneFrameValue(String boneName,int x,int y,int angle){
		if(rangeMap.get(boneName)==null){
			rangeMap.put(boneName, new BoneFrame(boneName, x, y, angle));
			return;
		}
		BoneFrame boneFrame=rangeMap.get(boneName);
		boneFrame.setAngle(angle);
		boneFrame.setX(x);
		boneFrame.setY(y);
	}
	
	private void updateInputValues(String boneName){
		inputRange.setValue(getBoneFrameIntAngle(boneName),false);
		
		xBox.setValue(getBoneFrameX(boneName));
		yBox.setValue(getBoneFrameY(boneName));
		
		
		//xBox.setEnabled(isRootBone(boneName));
		//yBox.setEnabled(isRootBone(boneName));
	}
	
	private boolean isRootBone(String boneName){
		if(rootBone==null){
			return false;
		}
		return rootBone.getName().equals(boneName);
	}
	
	public void setFrame(AnimationFrame frame){
		rangeMap.clear();
		for(BoneFrame boneFrame:frame.getBoneFrames().values()){
			rangeMap.put(boneFrame.getBoneName(),boneFrame.copy());
		}
		
		String selection=boneListBox.getValue().getName();
		updateInputValues(selection);
	}

	public BoneControlRange(@Nullable TwoDimensionBone rootBone){
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
				
				
				updateInputValues(bone.getName());
				if(listener!=null){
					listener.changed(getSelection(),(int)inputRange.getValue(), xBox.getValue(), yBox.getValue());//TODO support move
				}
			}
		});
		
		inputRange = new LabeledInputRangeWidget("Angle:", -180, 180, 1);
		inputRange.addtRangeListener(new ValueChangeHandler<Number>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				//LogUtils.log("on-range-changed");
				
				/*
				TwoDimensionBone selection=getSelection();
				if(selection==null){
					return;
				}
				int x=xBox.getValue();
				int y=0;
				rangeMap.put(selection.getName(),new BoneFrame(selection.getName(),x,y ,event.getValue().intValue()));
				
				if(listener!=null){
					listener.changed(getSelection(), event.getValue().intValue(), x, y);//TODO support move
				}
				*/
				onValueChanged();
			}
		});
		
		panel.add(inputRange);
		
		panel.add(new Label("X:"));
		xBox = new IntegerBox();
		xBox.setWidth("60px");
		panel.add(xBox);
		xBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				//called when enter-key
				LogUtils.log("x-box:value changed:"+event.getValue());
				onValueChanged();
			}
		});
		
		panel.add(new Label("Y:"));
		yBox = new IntegerBox();
		yBox.setWidth("60px");
		panel.add(yBox);
		yBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				//called when enter-key
				LogUtils.log("y-box:value changed:"+event.getValue());
				onValueChanged();
			}
		});
		
		
		Button reset=new Button("Reset",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				inputRange.setValue(0,false);
				xBox.setValue(0);
				yBox.setValue(0);
				onValueChanged();
			}
		});
		panel.add(reset);
		
		//only initial null
		if(rootBone!=null){
			setRootBone(rootBone);
		}	
		
		}
	
	private void onValueChanged(){
		
		TwoDimensionBone selection=getSelection();
		if(selection==null){
			return;
		}
		int x=xBox.getValue();
		int y=yBox.getValue();
		int angle=(int)inputRange.getValue();
		
		setBoneFrameValue(selection.getName(),x,y,angle);
		
		if(listener!=null){
			listener.changed(getSelection(), angle, x, y);//TODO support move
		}
	}
	private BoneControlListener listener;

	private IntegerBox xBox;
		
		public BoneControlListener getListener() {
		return listener;
	}

	public void setListener(BoneControlListener listener) {
		this.listener = listener;
	}

	//label-range
		//set-change listener
		//select(if not found ,select root)
	private TwoDimensionBone rootBone;
	public void setRootBone(TwoDimensionBone rootBone){
		Preconditions.checkNotNull(rootBone,"LabeledInputRangeWidget need rootBone");
		this.rootBone=rootBone;
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

	public Optional<String> getSelectedBoneName(){
		if(getSelection()!=null){
			String name=getSelection().getName();
			return Optional.fromNullable(name);
		}
		
		return Optional.absent();
	}



	public int getX() {
		return xBox.getValue();
	}

	public void setPosition(int x,int y) {
		xBox.setValue(x);
		yBox.setValue(y);
		onValueChanged();
	}




	public int getY() {
		// TODO Auto-generated method stub
		return yBox.getValue();
	}
	
}
