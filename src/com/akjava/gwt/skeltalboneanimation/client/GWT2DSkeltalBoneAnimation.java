package com.akjava.gwt.skeltalboneanimation.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT2DSkeltalBoneAnimation implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		VerticalPanel root=new VerticalPanel();
		RootPanel.get().add(root);
		
		//TurnAngleAnimation animation=new TurnAngleAnimation();
		//ThreePointShapeAnimation animation=new ThreePointShapeAnimation();
		//ThreePointImageAnimation animation=new ThreePointImageAnimation();
		//ThreePointImageCustomAnimation animation=new ThreePointImageCustomAnimation();
		
		//ImageMoveAnimation animation=new ImageMoveAnimation();
		FileAndMoveAnimation animation=new FileAndMoveAnimation();
		
		root.add(animation);
	}
}
