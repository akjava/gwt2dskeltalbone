package com.akjava.gwt.skeltalboneanimation.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.akjava.gwt.html5.client.input.Range;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * becareful  original is in threejstest.some modified here
 * @author aki
 *
 */
public class LabeledInputRangeWidget extends HorizontalPanel{
	
		private Range range;
		public Range getRange() {
			return range;
		}
		private TextBox textBox;
		public TextBox getTextBox() {
			return textBox;
		}
		private Map<ValueChangeHandler<Number>,HandlerRegistration> registMap=new HashMap<ValueChangeHandler<Number>, HandlerRegistration>();
		private Label nameLabel;
		public Label getNameLabel() {
			return nameLabel;
		}
		public LabeledInputRangeWidget(String name,Number min,Number max,Number step){
			this.setVerticalAlignment(ALIGN_MIDDLE);
			nameLabel = new Label(name);
			nameLabel.setAutoHorizontalAlignment(ALIGN_CENTER);
			nameLabel.setWidth("80px");
			add(nameLabel);
			range = new Range(name,min,max,step);
			range.setWidth("360px");
			add(range);
			textBox = new TextBox();
			textBox.setEnabled(true);
			textBox.setWidth("40px");
			add(textBox);
			textBox.setValue(String.valueOf(range.getValue()));
			range.addValueChangeHandler(new ValueChangeHandler<Number>() {

				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					
					setTextBoxText(String.valueOf(event.getValue()));
				}
				
			});
			
			textBox.setReadOnly(true);//not yet
		}
		
		public void setWidgetWidthPx(int nameWidth,int rangeWidth,int boxWidth){
			nameLabel.setWidth(nameWidth+"px");
			range.setWidth(rangeWidth+"px");
			textBox.setWidth(boxWidth+"px");
		}
		
		public void setTextBoxText(String value){
		value=value.substring(0, Math.min(5, value.length()));//limit text
		textBox.setValue(value);
		}
		public void setValue(double value,boolean fireevent){
			setTextBoxText(String.valueOf(value));
			range.setValue(value,fireevent);
		} 
		/**
		 * watch out this set possible call flush in your editor
		 * 
		 * watch out this Widget fire event 
		 * @param value
		 */
		public void setValue(double value){
			setValue(value,true);
		} 
		public double getValue(){
			return range.getValue().doubleValue();
		}
		
		public void addtRangeListener(ValueChangeHandler<Number> handler){
			HandlerRegistration regist=range.addValueChangeHandler(handler);
			registMap.put(handler,regist);
		}
		public void removeRangeListener(ValueChangeHandler<Number> handler){
			if(registMap.get(handler)!=null){
				registMap.get(handler).removeHandler();
			}
		}
		
		/*now range set .point value.
		public int parseDivided(Number value){
			if(value instanceof Integer){
				return 1;
			}else{
				String v=String.valueOf(value);
				int s=v.indexOf(".");
				if(s==-1){
					return 1;
				}
				
				return (int)Math.pow(10, v.length()-s);
			}
		}*/
	}