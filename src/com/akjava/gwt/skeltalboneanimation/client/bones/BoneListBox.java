package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.io.IOException;

import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class BoneListBox extends ValueListBox<TwoDimensionBone>{

	public BoneListBox() {
		super(new Renderer<TwoDimensionBone>() {

			@Override
			public String render(TwoDimensionBone object) {
				if(object!=null){
					return object.getName();
				}
				return "[null or not exist in list]";
			}

			@Override
			public void render(TwoDimensionBone object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
