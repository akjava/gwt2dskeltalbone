package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.skeltalboneanimation.client.page.SimpleAnimationEditorPage;
import com.akjava.gwt.skeltalboneanimation.client.page.SimpleAnimationEditorPage2;
import com.akjava.gwt.skeltalboneanimation.client.page.SimpleBoneEditorPage;
import com.akjava.gwt.skeltalboneanimation.client.page.SimpleBoneEditorPage2;
import com.akjava.gwt.skeltalboneanimation.client.page.SimpleTextureEditorPage;
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
		
		//advanced
		DockLayoutPanel boneDock2=new DockLayoutPanel(Unit.PX);
		tab.add(boneDock2,"Bone2");
		SimpleBoneEditorPage2 bonePage2=new SimpleBoneEditorPage2(boneDock2);
		boneDock2.add(bonePage2);
		
		DockLayoutPanel animationDock2=new DockLayoutPanel(Unit.PX);
		tab.add(animationDock2,"Animation2");
		SimpleAnimationEditorPage2 animationPage2=new SimpleAnimationEditorPage2(animationDock2);
		animationDock2.add(animationPage2);
		
		tab.selectTab(3);
	}
}
