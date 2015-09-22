package com.akjava.gwt.skeltalboneanimation.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.lib.common.utils.FileNames;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class BoneUtils {

	/*
	 * not contain bone itself
	 */
	public static List<TwoDimensionBone> getParents(TwoDimensionBone bone){
		List<TwoDimensionBone> parents=new ArrayList<TwoDimensionBone>();
		while(bone.getParent()!=null){
			TwoDimensionBone parent=bone.getParent();
			parents.add(0,parent);//root first
			bone=parent;
		}
		
		return parents;
	}
	public static TwoDimensionBone getRoot(TwoDimensionBone bone){
		List<TwoDimensionBone> parents=new ArrayList<TwoDimensionBone>();
		while(bone.getParent()!=null){
			TwoDimensionBone parent=bone.getParent();
			parents.add(0,parent);//root first
			bone=parent;
		}
		
		if(parents.isEmpty()){
			return bone;
		}
		
		return parents.get(0);
	}
	
	public static  AnimationFrame createEmptyAnimationFrame(TwoDimensionBone root){
		AnimationFrame frame=new AnimationFrame();
		for(TwoDimensionBone bone:getAllBone(root)){
			frame.add(new BoneFrame(bone.getName(), 0, 0, 0));
		}
		return frame;
	}
	private static void getAllBone(List<TwoDimensionBone> list,TwoDimensionBone bone){
		list.add(bone);
		for(TwoDimensionBone children:bone.getChildren()){
			getAllBone(list,children);
		}
		
	}
	
	public static List<TwoDimensionBone> getAllBone(TwoDimensionBone bone){
		List<TwoDimensionBone> list=new ArrayList<TwoDimensionBone>();
		if(bone!=null){
			getAllBone(list,bone);
		}
		return list;
	}
	public static List<String> getAllBoneName(TwoDimensionBone root){
		List<TwoDimensionBone> list=new ArrayList<TwoDimensionBone>();
		getAllBone(list,root);
		List<String> names=new ArrayList<String>();
		 for(TwoDimensionBone bone:list){
			 names.add(bone.getName());
		 }
		 return names;
	}
	
	
	public static List<BoneAnimationData> getParents(BoneAnimationData bone){
		List<BoneAnimationData> parents=new ArrayList<BoneAnimationData>();
		while(bone.getParent()!=null){
			BoneAnimationData parent=bone.getParent();
			parents.add(0,parent);//root first
			bone=parent;
		}
		
		return parents;
	}
	
	public static TwoDimensionBone findBoneByName(Iterable<TwoDimensionBone> bones,String name){
		for(TwoDimensionBone bone:bones){
			if(bone.getName().equals(name)){
				return bone;
			}
		}
		return null;
	}
	
	//TODO make interface
	public static double[] getFinalPositionAndAngle(BoneAnimationData bone){
		List<BoneAnimationData> parents=getParents(bone);
		if(parents.size()==0){
			return new double[]{bone.getX(),bone.getY(),bone.getAngle()};
		}
		parents.add(bone);
		
		double x=parents.get(0).getX();
		double y=parents.get(0).getY();
		double angle=parents.get(0).getAngle();
		for(int i=1;i<parents.size();i++){
			BoneAnimationData parent=parents.get(i);
			//LogUtils.log(parent.getName()+",angle="+angle);
			angle+=parent.getAngle();
			double[] turnd=turnedAngle(parent.getX(), parent.getY(), angle);
			x+=turnd[0];
			y+=turnd[1];
			
		}
		return new double[]{x,y,angle};
	}
	
	public static double[] turnedAngle(double x,double y,double angle){
		double radian=Math.toRadians(angle);
		double turnedX=Math.cos(radian)*x-Math.sin(radian)*y;
		double turnedY=Math.cos(radian)*y+Math.sin(radian)*x;
		return new double[]{turnedX, turnedY};
	}
	public static PointXY turnedAngle(PointXY point,double angle){
		int x=point.getX();
		int y=point.getY();
		double radian=Math.toRadians(angle);
		double turnedX=Math.cos(radian)*x-Math.sin(radian)*y;
		double turnedY=Math.cos(radian)*y+Math.sin(radian)*x;
		 point.set((int)turnedX, (int)turnedY);
		 return point;
	}
	public static String createBoneName(TwoDimensionBone parentBone){
		TwoDimensionBone root=getRoot(parentBone);
		List<TwoDimensionBone> bones=getAllBone(root);
		//need make method?
		List<String> names=FluentIterable.from(bones).transform(new Function<TwoDimensionBone, String>() {
			@Override
			public String apply(TwoDimensionBone input) {
				return input.getName();
			}
		}).toList();
		String newName=FileNames.createotExistNumberName(names, parentBone.getName()+"-bone", 1);
		return newName;
	}
	public static boolean existBoneByName(TwoDimensionBone root,String name,TwoDimensionBone target){
		List<TwoDimensionBone> bones=getAllBone(root);
		bones.remove(target);
		//need make method?
		List<String> names=FluentIterable.from(bones).transform(new BoneNameFunction()).toList();
		
		for(String boneName:names){
			if(name.equals(boneName)){
				return true;
			}
		}

		return false;
	}
	
	public static double calculateAngle(double x, double y, double x2, double y2) {
	    double radian = Math.atan2(x-x2,y-y2)*-1;
	    return radian;
	}
	
	public static class BoneNameFunction implements Function<TwoDimensionBone, String>{
		@Override
		public String apply(TwoDimensionBone input) {
			return input.getName();
		}
	}
}
