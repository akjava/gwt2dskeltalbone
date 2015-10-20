package com.akjava.gwt.skeltalboneanimation.client.page.studio;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.CanvasUpdater;
import com.google.gwt.user.client.ui.Widget;

public class StudioPage extends AbstractPage implements CanvasUpdater{

	public StudioPage(MainManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getOwnerName() {
		return "Studio-Page";
	}



	@Override
	protected void onBoneAndAnimationChanged(BoneAndAnimationData data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onBackgroundChanged(ImageDrawingData background) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTextureDataChanged(TextureData textureData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Widget createCenterPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Widget createWestPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateDatas() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void executeUpdateCanvas() {
		// TODO Auto-generated method stub
		
	}

}
