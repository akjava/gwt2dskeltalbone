package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;

public abstract class AbstractBonePainter implements BonePainter, BoneModifier{
	public static final AnimationFrame EMPTY_FRAME=new AnimationFrame();
	private Map<String,double[]> cache=new HashMap<String, double[]>();
	
	protected void makeBoneAnimationData(List<BoneAnimationData> list,TwoDimensionBone bone,AnimationFrame frame,BoneAnimationData parent){
		BoneAnimationData calculator=new BoneAnimationData(bone.getName(),bone.getX(),bone.getY());
		calculator.setParent(parent);
		list.add(calculator);
		
		if(frame.getBoneFrames().get(bone.getName())!=null){
			BoneFrame boneFrame=frame.getBoneFrames().get(bone.getName());
			calculator.setX(calculator.getX()+boneFrame.getX());
			calculator.setY(calculator.getY()+boneFrame.getY());
			calculator.setAngle(boneFrame.getAngle());
		}
		for(TwoDimensionBone child:bone.getChildrens()){
			makeBoneAnimationData(list, child, frame, calculator);
		}
	}
	protected int offsetX;
	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}
	protected int offsetY;
	
	public void paintBone(BoneAnimationData bone,AnimationFrame frame){
		
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
		List<BoneAnimationData> list=new ArrayList<BoneAnimationData>();
		makeBoneAnimationData(list,root,EMPTY_FRAME,null);

		for(BoneAnimationData bone:list){
			paintBone(bone,EMPTY_FRAME);
		}
		endPaint();
	}

	public List<BoneWithXYAngle> calculatorBonesFinalPositionAndAngle(TwoDimensionBone root,AnimationFrame frame){
		List<BoneWithXYAngle> boneWiths=new ArrayList<BoneWithXYAngle>();
		List<TwoDimensionBone> bones=BoneUtils.getAllBone(root);
		
		List<BoneAnimationData> list=new ArrayList<BoneAnimationData>();
		makeBoneAnimationData(list,root,frame,null);
		//List<double[]> pts=new ArrayList<double[]>();
		
		for(BoneAnimationData bc:list){
			double[] pts=BoneUtils.getFinalPositionAndAngle(bc);
			TwoDimensionBone tbone=BoneUtils.findBoneByName(bones, bc.getName());
			boneWiths.add(new BoneWithXYAngle(tbone, (int)pts[0], (int)pts[1], pts[2]));
		}
		return boneWiths;
	}

	public void paintBone(TwoDimensionBone root,AnimationFrame frame){
		
		startPaint();
		cache.clear();
		List<BoneAnimationData> list=new ArrayList<BoneAnimationData>();
		makeBoneAnimationData(list,root,frame,null);

		for(BoneAnimationData bone:list){
			paintBone(bone,frame);
		}
		endPaint();
	}
}
