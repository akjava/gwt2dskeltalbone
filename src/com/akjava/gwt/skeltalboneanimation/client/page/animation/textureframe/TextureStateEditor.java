package com.akjava.gwt.skeltalboneanimation.client.page.animation.textureframe;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame.TextureState;
import com.akjava.gwt.skeltalboneanimation.client.page.SimpleBoneEditorPage2.FlushTextBox;
import com.akjava.gwt.skeltalboneanimation.client.ui.LabeledInputRangeWidget;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextureStateEditor extends VerticalPanel implements Editor<TextureState>,ValueAwareEditor<TextureState>{
private TextureState value;

public TextureState getValue() {
	return value;
}

private CheckBox flipHorizontalCheck;
private CheckBox flipVerticalCheck;
private CheckBox visibleCheck;
private IntegerBox xLabel,yLabel;
private DoubleBox scaleXLabel,scaleYLabel,angleLabel;

private LabeledInputRangeWidget alphaRange;

private Updater updater;
public void setUpdater(Updater updater) {
	this.updater = updater;
}

public TextureStateEditor(){
	
	
	HorizontalPanel visiblePanel=new HorizontalPanel();
	visiblePanel.setVerticalAlignment(ALIGN_MIDDLE);
	add(visiblePanel);
	visibleCheck=new CheckBox("switch visible");
	visiblePanel.add(visibleCheck);
	visibleCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			flush();
		}
	});
	

	HorizontalPanel checks=new HorizontalPanel();
	add(checks);
	checks.setVerticalAlignment(ALIGN_MIDDLE);
	flipHorizontalCheck=new CheckBox("switch flip horizontal");
	checks.add(flipHorizontalCheck);
	flipHorizontalCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			flush();
		}
	});
	flipVerticalCheck=new CheckBox("switch flip vertical");
	checks.add(flipVerticalCheck);
	flipVerticalCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			flush();
		}
	});
	HorizontalPanel status=new HorizontalPanel();
	status.setSpacing(2);
	add(status);
	status.setVerticalAlignment(ALIGN_MIDDLE);
	
	int labelWidth=40;
	status.add(new Label("X:"));
	xLabel=new IntegerBox();
	xLabel.setEnabled(false);
	xLabel.setWidth(labelWidth+"px");
	status.add(xLabel);
	
	status.add(new Label("Y:"));
	
	yLabel=new IntegerBox();
	yLabel.setEnabled(false);
	yLabel.setWidth(labelWidth+"px");
	status.add(yLabel);
	
	status.add(new Label("Angle:"));
	angleLabel=new DoubleBox();
	angleLabel.setEnabled(false);
	angleLabel.setWidth(labelWidth+"px");
	status.add(angleLabel);
	
	status.add(new Label("Scale:"));
	
	scaleXLabel=new DoubleBox();
	scaleXLabel.setEnabled(false);
	scaleXLabel.setWidth(labelWidth+"px");
	status.add(scaleXLabel);
	
	scaleYLabel=new DoubleBox();
	scaleYLabel.setEnabled(false);
	scaleYLabel.setWidth(labelWidth+"px");
	status.add(scaleXLabel);

	
	alphaRange = new LabeledInputRangeWidget("transparent:", -1, 1.0, 0.01);
	alphaRange.getRange().addValueChangeHandler(new ValueChangeHandler<Number>() {
		@Override
		public void onValueChange(ValueChangeEvent<Number> event) {
			flush();
		}
	});
	alphaRange.getRange().setWidth("100px");
	HorizontalPanel rangePanel=new HorizontalPanel();
	rangePanel.setVerticalAlignment(ALIGN_MIDDLE);
	add(rangePanel);
	//rangePanel.add(new Label("alpha:"));
	rangePanel.add(alphaRange);
	
}

/*public void updateValues(){
	if(value==null){
		return;
	}
	xLabel.setValue(value.getIntX());
	yLabel.setValue(value.getIntY());
	angleLabel.setValue(value.getAngle());
	scaleLabel.setValue(value.getScaleX());

	alphaRange.setValue(value.getAlpha(),false);
	
	oldValue=value.copy();
}*/


@Override
	public void setDelegate(EditorDelegate<TextureState> delegate) {
		// TODO Auto-generated method stub
	}

	@Override
	 public void flush() {
		
		
		if(value==null){
			return;
		}
		
		
		
		value.setFlipHorizontal(flipHorizontalCheck.getValue());
		value.setFlipVertical(flipVerticalCheck.getValue());
		
		value.setVisible(visibleCheck.getValue());
		
		
		value.setAlpha(alphaRange.getValue());
		
		
		//TODO x,y,angle,scalex,scaley
		
		
		//oldValue.setX(value.getX());
		/*
		oldValue.setX(xLabel.getValue());
		oldValue.setY(yLabel.getValue());
		oldValue.setAngle(angleLabel.getValue());
		oldValue.setScaleX(scaleLabel.getValue());
		oldValue.setScaleY(scaleLabel.getValue());
		*/
		
		if(updater!=null){
			updater.update();
		}
		
	}

	@Override
	public void onPropertyChange(String... paths) {
		// TODO Auto-generated method stub
	}

	//private TextureState oldValue;
	@Override
	 public void setValue(TextureState value) {
		this.value=value;
		if(value==null){
			LogUtils.log("setValue:null");
			
			//setVisible(false);
			return;
		}
		//oldValue=value.copy();
		//LogUtils.log("update old-value");
		
		
		
		flipHorizontalCheck.setValue(value.isFlipHorizontal());
		flipVerticalCheck.setValue(value.isFlipVertical());
		visibleCheck.setValue(value.isVisible());
		
		xLabel.setText(String.valueOf(value.getX()));
		yLabel.setText(String.valueOf(value.getY()));
		angleLabel.setText(String.valueOf(value.getAngle()));
		scaleXLabel.setText(String.valueOf(value.getScaleX()));
		scaleYLabel.setText(String.valueOf(value.getScaleY()));
		
		
		alphaRange.setValue(value.getAlpha(),false);
		
	}


}