package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.skeltalboneanimation.client.page.animation.AnimationPage;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.BonePage;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImagePage;
import com.akjava.gwt.skeltalboneanimation.client.page.colorpick.ColorPickPage;
import com.akjava.gwt.skeltalboneanimation.client.page.html5app.TransparentItPage;
import com.akjava.gwt.skeltalboneanimation.client.page.texture.TexturePage;
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
		
		DockLayoutPanel root=new DockLayoutPanel(Unit.PX);
		
		RootLayoutPanel.get().add(root);
		TabLayoutPanel tab=new TabLayoutPanel(24, Unit.PX);
		
		
		//TurnAngleAnimation animation=new TurnAngleAnimation();
		//ThreePointShapeAnimation animation=new ThreePointShapeAnimation();
		//ThreePointImageAnimation animation=new ThreePointImageAnimation();
		//ThreePointImageCustomAnimation animation=new ThreePointImageCustomAnimation();
		
		//ImageMoveAnimation animation=new ImageMoveAnimation();
		//FileAndMoveAnimation animation=new FileAndMoveAnimation();
		//FileAndMoveAndBoneAnimation animation=new FileAndMoveAndBoneAnimation();
		
		/*
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
		*/
		
		
		
		final MainManager manager=new MainManager(tab);
		
		FileManagerBar bar=new FileManagerBar(manager);
		root.addNorth(bar, 32);
		
		ColorPickPage colorPickPage=new ColorPickPage(manager);
		
		tab.add(new BonePage(manager),"Bone");
		tab.add(new AnimationPage(manager),"Animation");
		
		//clip & transparent is not so popular action,so connect directly
		ClipImagePage clip=new ClipImagePage(manager);
		tab.add(clip,"Clip");
		
		TransparentItPage transparentPage=new TransparentItPage(manager);
		tab.add(transparentPage,"Transparent");
		clip.setTransparentItPage(transparentPage);
		transparentPage.setColorPickPage(colorPickPage);
		
		//add everything inside
		tab.add(new TexturePage(manager),"Texture");
		tab.add(colorPickPage,"ColorPick");
		
		root.add(tab);
		//advanced
		/*
		DockLayoutPanel boneDock2=new DockLayoutPanel(Unit.PX);
		tab.add(boneDock2,"Bone2");
		SimpleBoneEditorPage2 bonePage2=new SimpleBoneEditorPage2(boneDock2);
		boneDock2.add(bonePage2);
		
		DockLayoutPanel animationDock2=new DockLayoutPanel(Unit.PX);
		tab.add(animationDock2,"Animation2");
		SimpleAnimationEditorPage2 animationPage2=new SimpleAnimationEditorPage2(animationDock2);
		animationDock2.add(animationPage2);
		*/
		
		/*
		DockLayoutPanel textureDock2=new DockLayoutPanel(Unit.PX);
		tab.add(textureDock2,"Texture2");
		SimpleTextureEditorPage2 texturePage2=new SimpleTextureEditorPage2(textureDock2);
		textureDock2.add(texturePage2);
		*/
		
		
		tab.selectTab(5);
	}
}
