package com.akjava.gwt.skeltalboneanimation.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT2DSkeltalBoneAnimation implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		TabLayoutPanel tab=new TabLayoutPanel(24, Unit.PX);
		
		RootLayoutPanel.get().add(tab);
		
		
		//TurnAngleAnimation animation=new TurnAngleAnimation();
		//ThreePointShapeAnimation animation=new ThreePointShapeAnimation();
		//ThreePointImageAnimation animation=new ThreePointImageAnimation();
		//ThreePointImageCustomAnimation animation=new ThreePointImageCustomAnimation();
		
		//ImageMoveAnimation animation=new ImageMoveAnimation();
		//FileAndMoveAnimation animation=new FileAndMoveAnimation();
		//FileAndMoveAndBoneAnimation animation=new FileAndMoveAndBoneAnimation();
		
		DockLayoutPanel boneDock=new DockLayoutPanel(Unit.PX);
		tab.add(boneDock,"Bone");
		SimpleBoneEditorPage bonePage=new SimpleBoneEditorPage(boneDock);
		boneDock.add(bonePage);
		
		DockLayoutPanel animationDock=new DockLayoutPanel(Unit.PX);
		tab.add(animationDock,"Animation");
		SimpleAnimationEditorPage animationPage=new SimpleAnimationEditorPage(animationDock);
		animationDock.add(animationPage);
		
		DockLayoutPanel textureDock=new DockLayoutPanel(Unit.PX);
		tab.add(textureDock,"Texture");
		SimpleTextureEditorPage texturePage=new SimpleTextureEditorPage(textureDock);
		textureDock.add(texturePage);
		
		tab.selectTab(0);
	}
}
