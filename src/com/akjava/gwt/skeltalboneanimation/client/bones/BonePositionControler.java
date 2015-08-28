package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.graphics.Rect;

public class BonePositionControler {
	private CanvasBonePainter painter;
	public BonePositionControler(CanvasBonePainter painter, TwoDimensionBone rootBone) {
		super();
		this.painter = painter;
		this.rootBone = rootBone;
	}

	private TwoDimensionBone rootBone;
	private List<BoneWithXYAngle> rawInitialData;
	private List<BoneWithXYAngle> rawAnimationedData;

	private int boneSize=10;

	public List<BoneWithXYAngle> getRawInitialData() {
		return rawInitialData;
	}


	public void setRawInitialData(List<BoneWithXYAngle> rawInitialData) {
		this.rawInitialData = rawInitialData;
	}


	public List<BoneWithXYAngle> getRawAnimationedData() {
		return rawAnimationedData;
	}


	public void setRawAnimationedData(List<BoneWithXYAngle> rawAnimationedData) {
		this.rawAnimationedData = rawAnimationedData;
	}


	public void update(AnimationFrame frame){
		 rawInitialData = painter.calculatorBonesFinalPositionAndAngle(rootBone,AbstractBonePainter.EMPTY_FRAME);
		 rawAnimationedData = painter.calculatorBonesFinalPositionAndAngle(rootBone,frame);
	}
	
	public TwoDimensionBone collisionAnimationedData(int screenX,int screenY){
		BoneModifier modifier=painter;
		if(rawAnimationedData==null){
			return null;
		}
		screenX-=modifier.getOffsetX();
		screenY-=modifier.getOffsetY();
		//make rect & collision it.
		for(BoneWithXYAngle data:rawAnimationedData){
			Rect rect=Rect.fromCenterPoint(data.getX(), data.getY(), boneSize/2, boneSize/2);
		//	LogUtils.log(rect+","+screenX+2+"x"+screenY);
			if(rect.contains(screenX, screenY)){
				return data.getBone();
			}
		}
		
		return null;
	}
	
	

}
