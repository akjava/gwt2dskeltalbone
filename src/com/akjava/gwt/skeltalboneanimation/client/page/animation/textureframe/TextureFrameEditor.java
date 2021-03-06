package com.akjava.gwt.skeltalboneanimation.client.page.animation.textureframe;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame.TextureState;
import com.akjava.gwt.skeltalboneanimation.client.functions.TextureDataFunctions;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextureFrameEditor extends VerticalPanel implements Editor<TextureFrame>,ValueAwareEditor<TextureFrame>{
	private TextureFrame value;
	private Supplier<TextureData> currentTextureData;
		
	

	private Supplier<AnimationFrame> currentFrame;
	private Supplier<SkeletalAnimation> currentAnimation;



	private EasyCellTableObjects<String> orderEditor;
	private Updater updater;
	
	private TextureStateEditor textureStateEditor;
	
	private void onSelectId(String selection){
		if(value==null){
			LogUtils.log("onSelectId:never happen");
		}
		TextureState textureState=value.getTextureState(selection);
		
		if(textureState==null){
			//LogUtils.log("onSelect:null selection.create");
			textureState=new TextureState(selection);
		}
		
		if(!resetStateAllCheck.getValue()){
			textureStateEditor.setVisible(true);
		}
		
		textureStateEditor.setValue(textureState);
	}
	
	public TextureFrameEditor(final Updater updater,Supplier<TextureData> currentTextureData, Supplier<AnimationFrame> currnetFrame, Supplier<SkeletalAnimation> currentAnimation) {
		
		super();
		this.currentTextureData = currentTextureData;
		this.currentFrame = currnetFrame;
		this.currentAnimation = currentAnimation;
		this.updater=updater;
		add(new Label("Order"));
		SimpleCellTable<String> orderTable=new SimpleCellTable<String>(999) {

			@Override
			public void addColumns(CellTable<String> table) {
				TextColumn<String> nameColumn=new TextColumn<String>() {
					
					@Override
					public String getValue(String object) {
						//return object.getId();
						return object.toString();
					}
				};
				table.addColumn(nameColumn);
			}
		};
		
		
		
		
		orderEditor = new EasyCellTableObjects<String>(orderTable,false) {
			
			@Override
			public void onSelect(String selection) {
				
				onSelectId(selection);
				
				
			}
		};
		
		updownPanel = new UpDwonPanel<String>(orderEditor, new Updater() {
			@Override
			public void update() {
				flush();
			}
		});
		updownPanel.setEnabled(false);
		HorizontalPanel radios=new HorizontalPanel();
		this.add(radios);
		noneButton = new RadioButton("mode","none");
		noneButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					updownPanel.setEnabled(false);
					orderMode=ORDER_MODE_NONE;
					flush();
				}
			}
		});
		radios.add(noneButton);
		
		editButton = new RadioButton("mode","edit");
		editButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					updownPanel.setEnabled(true);
					orderMode=ORDER_MODE_EDIT;
					flush();
				}
			}
		});
		radios.add(editButton);
		
		resetButton = new RadioButton("mode","reset");
		resetButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					updownPanel.setEnabled(false);
					orderMode=ORDER_MODE_RESET;
					flush();
				}
			}
		});
		radios.add(resetButton);
		noneButton.setValue(true);
		
		this.add(updownPanel);
		
		
		this.add(orderTable);
		
		this.add(new Label("Texture relative changes"));
		
		resetStateAllCheck = new CheckBox("Reset all");
		this.add(resetStateAllCheck);
		resetStateAllCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				textureStateEditor.setVisible(!event.getValue());
				flush();
			}
			
		});
		
		
		textureStateEditor=new TextureStateEditor();
		textureStateEditor.setUpdater(new Updater() {
			@Override
			public void update() {
				
				
				
				
				TextureState state=textureStateEditor.getValue();
				if(!state.isModified()){
					value.removeTextureState(state.getId());
				}else{
					//LogUtils.log("textureStateEditor-update");
					value.updateTextureState(state);
				}
				flush();
				
				
			}
		});
		this.add(textureStateEditor);
		textureStateEditor.setVisible(false);//no need not selected
	}
	public static final int ORDER_MODE_NONE=0;
	public static final int ORDER_MODE_EDIT=1;
	public static final int ORDER_MODE_RESET=2;
	private int orderMode=ORDER_MODE_NONE;



	private RadioButton noneButton;



	private RadioButton editButton;



	private RadioButton resetButton;



	private UpDwonPanel<String> updownPanel;
	private CheckBox resetStateAllCheck;
	
	
	@Override
	public void setDelegate(EditorDelegate<TextureFrame> delegate) {
		// TODO Auto-generated method stub
		
	}

	public void setOrderMode(int mode){
		orderMode=mode;
		switch(mode){
		case ORDER_MODE_NONE:
			noneButton.setValue(true);
			updownPanel.setEnabled(false);
			break;
		case ORDER_MODE_EDIT:
			editButton.setValue(true);
			updownPanel.setEnabled(true);
			break;
		case ORDER_MODE_RESET:
			resetButton.setValue(true);
			updownPanel.setEnabled(false);
			break;
		}
	}
	
	@Override
	public void flush() {
		//LogUtils.log("flush");
		
		boolean orderReset=false;
		List<String> order=null;
		
		if(orderMode==ORDER_MODE_EDIT){
			order=ImmutableList.copyOf(orderEditor.getDatas());
		}else if(orderMode==ORDER_MODE_RESET){
			orderReset=true;
		}
		
		AnimationFrame frame=checkNotNull(currentFrame.get(),"TextureFrameEditor:need frame");
		
		TextureFrame textureFrame=new TextureFrame();
		
		if(order!=null){
			textureFrame.setTextureOrder(order);
		}else if(orderReset){
			textureFrame.setNeedResetOrder(true);
		}
		
		
		if(resetStateAllCheck.getValue()){
			textureFrame.setNeedResetState(true);
		}else{
			for(List<TextureState> states:value.getTextureUpdates().asSet()){
			textureFrame.setTextureUpdates(states);
			}
		}
		
		
		
		value=textureFrame;
		
		if(!textureFrame.isValid()){
			textureFrame=null;
		}
		
		//TODO compare & undo
		
		frame.setTextureFrame(textureFrame);
		
		updater.update();
	}

	
	public void unselect(){
		orderEditor.unselect();
	}
	
	@Override
	public void onPropertyChange(String... paths) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(TextureFrame value) {
		this.value=checkNotNull(value,"TextureFrameEditor:value not null,if real value is null make them before call");
		
		
		SkeletalAnimation animation=checkNotNull(currentAnimation.get(),"TextureFrameEditor:need animation");
		AnimationFrame frame=checkNotNull(currentFrame.get(),"TextureFrameEditor:need frame");
		
		
		
		
		List<String> order=null;
		int orderMode=ORDER_MODE_NONE;
		
		
		Optional<TextureFrame> prev=animation.getMergedTextureFrameAt(frame);
		
		if(prev.isPresent()){
			for(List<String> order2:prev.get().getTextureOrder().asSet()){
				order=order2;
				
				
			}
		}
		
		
		if(value.getTextureOrder().isPresent()){
			orderMode=ORDER_MODE_EDIT;
			
			
		}
		
		if(value.isNeedResetOrder()){
			orderMode=ORDER_MODE_RESET;
			
		}
		
		setOrderMode(orderMode);
		
		
		if(order==null){
			TextureData data=currentTextureData.get();
			if(data!=null){
				order=TextureDataFunctions.convertIdString(data);
			}else{
				LogUtils.log("TextureFrameEditor:not texture data");
			}
		}
		
		if(order==null){
			LogUtils.log("TextureFrameEditor:not texture order");
			order=Lists.newArrayList();//for dummy
		}
		
		resetStateAllCheck.setValue(value.isNeedResetState(),true);
		
		//possible immutable
		orderEditor.setDatas(Lists.newArrayList(order));
		orderEditor.update();
		
		String id=orderEditor.getSelection();
		if(id!=null){
			onSelectId(id);
		}
		
	}

}
