package com.akjava.gwt.skeltalboneanimation.client.bones;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.InnerBoneAnimationData;
import com.akjava.lib.common.graphics.IntRect;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/*
 * too similar CanvasBonePainter.must be refactoring
 */
public class BonePositionControler {
	private BoneSettings settings;
	public BoneSettings getSettings() {
		return settings;
	}

	private boolean collisionOrderDesc;

	public boolean isCollisionOrderDesc() {
		return collisionOrderDesc;
	}


	public void setCollisionOrderDesc(boolean collisionOrderDesc) {
		this.collisionOrderDesc = collisionOrderDesc;
	}


	public void setSettings(BoneSettings settings) {
		this.settings = settings;
	}


	public BonePositionControler(BoneSettings painter) {
		super();
		this.settings = painter;

	}

	
	private List<BoneWithXYAngle> rawInitialData;
	private List<BoneWithXYAngle> rawAnimationedData;

	private int boneSize=20;

	
	


	public int getBoneSize() {
		return boneSize;
	}


	public void setBoneSize(int boneSize) {
		this.boneSize = boneSize;
	}


	private int findIndex(List<BoneWithXYAngle> list,String name){
		checkNotNull(list,"findIndex:list is null");
		checkNotNull(name,"findIndex:name is null");
		
		for(int i=0;i<list.size();i++){
			BoneWithXYAngle data=list.get(i);
			if(data.getBone().getName().equals(name)){
				return i;
			}
		}
		return -1;
	}
	
	public BoneWithXYAngle getAnimationedDataByName(String name){
		int index=findIndex(rawAnimationedData, name);
		if(index==-1){
			return null;
		}
		return rawAnimationedData.get(index);
	}
	
	public BoneWithXYAngle getInitialDataByName(String name){
		int index=findIndex(rawInitialData, name);
		if(index==-1){
			return null;
		}
		return rawInitialData.get(index);
	}
	
	
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
	protected void makeBoneAnimationData(List<InnerBoneAnimationData> list,TwoDimensionBone bone,AnimationFrame frame){
		makeBoneAnimationData(list, bone, frame,null);
	}

	protected void makeBoneAnimationData(List<InnerBoneAnimationData> list,TwoDimensionBone bone,AnimationFrame frame,InnerBoneAnimationData parent){
		InnerBoneAnimationData calculator=new InnerBoneAnimationData(bone.getName(),bone.getX(),bone.getY());
		calculator.setParent(parent);
		list.add(calculator);
		
		if(frame.getBoneFrames().get(bone.getName())!=null){
			BoneFrame boneFrame=frame.getBoneFrames().get(bone.getName());
			calculator.setX(calculator.getX()+boneFrame.getX());
			calculator.setY(calculator.getY()+boneFrame.getY());
			calculator.setAngle(boneFrame.getAngle());
		}
		for(TwoDimensionBone child:bone.getChildren()){
			makeBoneAnimationData(list, child, frame, calculator);
		}
	}
	
	public List<BoneWithXYAngle> calculatorBonesFinalPositionAndAngle(TwoDimensionBone root,AnimationFrame frame){
		
		List<BoneWithXYAngle> boneWiths=new ArrayList<BoneWithXYAngle>();
		List<TwoDimensionBone> bones=BoneUtils.getAllBone(root);
		
		List<InnerBoneAnimationData> list=new ArrayList<InnerBoneAnimationData>();
		makeBoneAnimationData(list,root,frame,null);
		//List<double[]> pts=new ArrayList<double[]>();
		
		for(InnerBoneAnimationData bc:list){
			double[] pts=BoneUtils.getFinalPositionAndAngle(bc);
			TwoDimensionBone tbone=BoneUtils.findBoneByName(bones, bc.getName());
			boneWiths.add(new BoneWithXYAngle(tbone, (int)pts[0], (int)pts[1], pts[2]));
		}
		return boneWiths;
	}
	
	public boolean isAvaiable(){
		return rawInitialData!=null && rawAnimationedData!=null;
	}
	
	public void updateInitialData(){
		 rawInitialData = calculatorBonesFinalPositionAndAngle(settings.getBone(),AbstractBonePainter.EMPTY_FRAME);
		 
	}
	public void updateAnimationData(AnimationFrame frame){
		Preconditions.checkNotNull(frame);
		rawAnimationedData = calculatorBonesFinalPositionAndAngle(settings.getBone(),frame);
	}
	public void updateBoth(AnimationFrame frame){
		updateInitialData();
		updateAnimationData(frame);
	}
	//TODO add collision initial
	public TwoDimensionBone collisionInitialData(int screenX,int screenY){
		BoneSettings modifier=settings;
		if(rawInitialData==null){
			return null;
		}
		screenX-=modifier.getOffsetX();
		screenY-=modifier.getOffsetY();
		//make rect & collision it.
		
		List<BoneWithXYAngle> datas=rawInitialData;
		if(collisionOrderDesc){
			datas=Lists.reverse(datas);
		}
		
		for(BoneWithXYAngle data:datas){
			IntRect rect=IntRect.fromCenterPoint(data.getX(), data.getY(), boneSize/2, boneSize/2);
		//	LogUtils.log(rect+","+screenX+2+"x"+screenY);
			if(rect.contains(screenX, screenY)){
				return data.getBone();
			}
		}
		
		return null;
	}
	
	public boolean isCollisionOnInitialData(TwoDimensionBone target,int screenX,int screenY){
		BoneSettings modifier=settings;
		if(rawInitialData==null){
			return false;
		}
		screenX-=modifier.getOffsetX();
		screenY-=modifier.getOffsetY();
		//make rect & collision it.
		
		List<BoneWithXYAngle> datas=rawInitialData;
		
		
		for(BoneWithXYAngle data:datas){
			if(data.getBone()==target){
			IntRect rect=IntRect.fromCenterPoint(data.getX(), data.getY(), boneSize/2, boneSize/2);
		//	LogUtils.log(rect+","+screenX+2+"x"+screenY);
			if(rect.contains(screenX, screenY)){
				return true;
			}
			}
		}
		
		return false;
	}
	
	public TwoDimensionBone collisionAnimationedData(int screenX,int screenY){
		Preconditions.checkNotNull(settings);
		
		if(rawAnimationedData==null){
			LogUtils.log("rawAnimationData is null.need update");
			return null;
		}
		screenX-=settings.getOffsetX();
		screenY-=settings.getOffsetY();
		
		List<BoneWithXYAngle> datas=rawAnimationedData;
		if(collisionOrderDesc){
			datas=Lists.reverse(datas);
		}
		
		for(BoneWithXYAngle data:datas){
			IntRect rect=IntRect.fromCenterPoint(data.getX(), data.getY(), boneSize/2, boneSize/2);
		//	LogUtils.log(rect+","+screenX+2+"x"+screenY);
			if(rect.contains(screenX, screenY)){
				return data.getBone();
			}
		}
		
		return null;
	}
	
	

}
