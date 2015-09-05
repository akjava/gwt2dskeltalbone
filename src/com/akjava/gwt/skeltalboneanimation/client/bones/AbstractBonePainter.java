package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;

public abstract class AbstractBonePainter implements BonePainter{
	public static final AnimationFrame EMPTY_FRAME=new AnimationFrame();
	private Map<String,double[]> cache=new HashMap<String, double[]>();
	private BonePositionControler positionControler;
	
	public AbstractBonePainter(BonePositionControler positionControler){
		this.positionControler=positionControler;
	}

	private boolean useOffset=true;
	
public boolean isUseOffset() {
		return useOffset;
	}



	public void setUseOffset(boolean useOffset) {
		this.useOffset = useOffset;
	}



public void paintBone(){
		
		paintBone(EMPTY_FRAME);
	}



	public void paintBone(AnimationFrame frame){
		int offsetX=0;
		int offsetY=0;
		TwoDimensionBone rootBone=positionControler.getSettings().getBone();
		if(useOffset){
			offsetX=positionControler.getSettings().getOffsetX();
			offsetY=positionControler.getSettings().getOffsetY();
		}
		startPaint();
		cache.clear();
		//List<BoneAnimationData> list=new ArrayList<BoneAnimationData>();
		//positionControler.makeBoneAnimationData(list,rootBone,frame);
		
		List<BoneWithXYAngle> result = positionControler.calculatorBonesFinalPositionAndAngle(rootBone,frame);

		for(BoneWithXYAngle data:result){
			TwoDimensionBone bone=data.getBone();
			String parentName;
			int startX,startY,endX,endY;
			endX=data.getX();
			endY=data.getY();
			if(data.getBone().isRoot()){
				startX=endX;
				startY=endY;
				parentName=null;
			}else{
				if(data.getBone().getParent()==null){
					LogUtils.log("has no parent:"+data.getBone().getName());
				}
				parentName=data.getBone().getParent().getName();
				double[] point=cache.get(parentName);
				if(point==null){
					LogUtils.log("not found point:"+parentName);
				}
				startX=(int)point[0];
				startY=(int)point[1];
			}
			cache.put(bone.getName(), new double[]{endX,endY});
			paintBone(bone.getName(),parentName,startX+offsetX,startY+offsetY,endX+offsetX,endY+offsetY,data.getAngle());
			
		}
		endPaint();
	}
}
