package com.akjava.gwt.skeltalboneanimation.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;

public class BoneUtils {

	public static List<TwoDimensionBone> getParents(TwoDimensionBone bone){
		List<TwoDimensionBone> parents=new ArrayList<TwoDimensionBone>();
		while(bone.getParent()!=null){
			TwoDimensionBone parent=bone.getParent();
			parents.add(0,parent);//root first
			bone=parent;
		}
		
		return parents;
	}
	
	public static List<BoneCalculator> getParents(BoneCalculator bone){
		List<BoneCalculator> parents=new ArrayList<BoneCalculator>();
		while(bone.getParent()!=null){
			BoneCalculator parent=bone.getParent();
			parents.add(0,parent);//root first
			bone=parent;
		}
		
		return parents;
	}
	//TODO make interface
	public static double[] getFinalPositionAndAngle(BoneCalculator bone){
		List<BoneCalculator> parents=getParents(bone);
		if(parents.size()==0){
			return new double[]{bone.getX(),bone.getY(),bone.getAngle()};
		}
		parents.add(bone);
		
		double x=parents.get(0).getX();
		double y=parents.get(0).getY();
		double angle=parents.get(0).getAngle();
		for(int i=1;i<parents.size();i++){
			BoneCalculator parent=parents.get(i);
			LogUtils.log(parent.getName()+",angle="+angle);
			double[] turnd=turnedAngle(parent.getX(), parent.getY(), angle);
			x+=turnd[0];
			y+=turnd[1];
			angle+=parent.getAngle();
		}
		return new double[]{x,y,angle};
	}
	
	public static double[] turnedAngle(double x,double y,double angle){
		double radian=Math.toRadians(angle);
		double turnedX=Math.cos(radian)*x-Math.sin(radian)*y;
		double turnedY=Math.cos(radian)*y+Math.sin(radian)*x;
		return new double[]{turnedX, turnedY};
	}
}
