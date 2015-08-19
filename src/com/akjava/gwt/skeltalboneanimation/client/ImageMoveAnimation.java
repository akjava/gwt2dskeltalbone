package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.MultiImageElementLoader;
import com.akjava.gwt.lib.client.MultiImageElementLoader.MultiImageElementListener;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageMoveAnimation extends VerticalPanel{

	public ImageMoveAnimation(){
		HorizontalPanel buttons=new HorizontalPanel();
		add(buttons);
		
		Canvas canvas = CanvasUtils.createCanvas(800, 800);
		
		add(canvas);
		
		MultiImageElementLoader loader=new MultiImageElementLoader();
		loader.loadImages(new MultiImageElementListener() {
			
			@Override
			public void onLoad(List<ImageElement> elements) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(List<String> paths) {
				// TODO Auto-generated method stub
				
			}
		}, "upper.png");
	}
}
