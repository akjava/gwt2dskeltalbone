package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import java.io.IOException;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class IntegerValueListBox extends ValueListBox<Integer>{

	public IntegerValueListBox(List<Integer> values,int value,ValueChangeHandler<Integer> handler) {
		super(new Renderer<Integer>() {

			@Override
			public String render(Integer object) {
				if(object==null){
					return "";
				}
				return String.valueOf(object);
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		this.setValue(value);
		this.setAcceptableValues(values);
		this.addValueChangeHandler(handler);
		
	}

}
