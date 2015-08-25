package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneCalculator;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;

public abstract class AbstractBonePainter implements BonePainter{
	private static final AnimationFrame emptyFrame=new AnimationFrame();
	private Map<String,double[]> cache=new HashMap<String, double[]>();
	
	protected void makeBoneCalculator(List<BoneCalculator> list,TwoDimensionBone bone,AnimationFrame frame,BoneCalculator parent){
		BoneCalculator calculator=new BoneCalculator(bone.getName(),bone.getX(),bone.getY());
		calculator.setParent(parent);
		list.add(calculator);
		
		if(frame.getBoneFrames().get(bone.getName())!=null){
			BoneFrame boneFrame=frame.getBoneFrames().get(bone.getName());
			calculator.setX(calculator.getX()+boneFrame.getX());
			calculator.setY(calculator.getY()+boneFrame.getY());
			calculator.setAngle(boneFrame.getAngle());
		}
		for(TwoDimensionBone child:bone.getChildrens()){
			makeBoneCalculator(list, child, frame, calculator);
		}
	}
	protected int offsetX;
	protected int offsetY;
	
	public void paintBone(BoneCalculator bone,AnimationFrame frame){
		
		double[] pt=BoneUtils.getFinalPositionAndAngle(bone);
		if(bone.getParent()!=null){
		double[] parentPt=cache.get(bone.getParent().getName());
		if(parentPt!=null){
			paintBone(bone.getName(),bone.getParent().getName(), (int)(parentPt[0]+offsetX), (int)(parentPt[1]+offsetY),(int)(pt[0]+offsetX), (int)(pt[1]+offsetY),pt[2]);
			}else{
			LogUtils.log(bone.getParent().getName()+" has not cached");
			}
		}else{
			paintBone(bone.getName(), null,(int)(pt[0]+offsetX), (int)(pt[1]+offsetY), (int)(pt[0]+offsetX), (int)(pt[1]+offsetY),pt[2]);
		}
		cache.put(bone.getName(),pt);
	}
	
public void paintBone(TwoDimensionBone root){
		
		startPaint();
		cache.clear();
		List<BoneCalculator> list=new ArrayList<BoneCalculator>();
		makeBoneCalculator(list,root,emptyFrame,null);

		for(BoneCalculator bone:list){
			paintBone(bone,emptyFrame);
		}
		endPaint();
	}

	public void paintBone(TwoDimensionBone root,AnimationFrame frame){
		
		startPaint();
		cache.clear();
		List<BoneCalculator> list=new ArrayList<BoneCalculator>();
		makeBoneCalculator(list,root,frame,null);

		for(BoneCalculator bone:list){
			paintBone(bone,frame);
		}
		endPaint();
	}
}
