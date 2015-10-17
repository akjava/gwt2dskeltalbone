package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import java.io.IOException;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class DoubleValueListBox extends ValueListBox<Double>{

	public DoubleValueListBox(List<Double> values,double value,ValueChangeHandler<Double> handler) {
		super(new Renderer<Double>() {

			@Override
			public String render(Double object) {
				if(object==null){
					return "";
				}
				return String.valueOf(object);
			}

			@Override
			public void render(Double object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		this.setValue(value);
		this.setAcceptableValues(values);
		this.addValueChangeHandler(handler);
		
	}

}
