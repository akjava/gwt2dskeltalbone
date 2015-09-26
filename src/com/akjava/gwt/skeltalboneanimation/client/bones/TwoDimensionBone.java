package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.game.PointD;
import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.google.common.collect.Lists;

public class TwoDimensionBone {
private double x;
private double y;
public TwoDimensionBone(String name,double x, double y,TwoDimensionBone parent) {
	super();
	this.x = x;
	this.y = y;
	this.name = name;
	this.parent=parent;
}
public TwoDimensionBone(String name,double x, double y){
	this(name,x,y,null);
}
private String name;
private TwoDimensionBone parent;
private List<TwoDimensionBone> children=new ArrayList<TwoDimensionBone>();
public TwoDimensionBone getParent() {
	return parent;
}
public void setParent(TwoDimensionBone parent) {
	this.parent = parent;
}
public List<TwoDimensionBone> getChildren() {
	return children;
}
public List<TwoDimensionBone> getChildrenAll() {
	List<TwoDimensionBone> allbone= BoneUtils.getAllBone(this);
	allbone.remove(this);
	return allbone;
}

public void setChildrens(List<TwoDimensionBone> childrens) {
	this.children = childrens;
}
public void set(double x,double y){
	this.x=x;
	this.y=y;
}
public double getX() {
	return x;
}
public void setX(double x) {
	this.x = x;
}
public double getY() {
	return y;
}
public void setY(double y) {
	this.y = y;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public boolean isRoot(){
	return parent==null;
}

public void changeChildrenScale(double scale){
	List<TwoDimensionBone> children=getChildrenAll();
	for(TwoDimensionBone bone:children){
		bone.x*=scale;
		bone.y*=scale;
	}
}

//not test yet
public void rotateChildrens(double angle){
	List<TwoDimensionBone> children=BoneUtils.getAllBone(this);
	children.remove(this);
	
	List<PointD> positions=Lists.newArrayList();
	for(TwoDimensionBone child:children){
		PointD pos=child.getAbsolutePosition();
		//LogUtils.log(child.getName()+","+pos.toString());
		positions.add(pos);
	}
	
	List<PointD> newPositions=Lists.newArrayList();
	PointD center=getAbsolutePosition();
	for(PointD position:positions){
		PointD pos=PointD.turnedAngle(center, position, angle);
		//LogUtils.log(angle+","+pos);
		
		/*
		PointD parentPosition=this.getAbsolutePosition();
		parentPosition.x-=this.x;
		parentPosition.y-=this.y;
		
		pos.x+=parentPosition.x;
		pos.y+=parentPosition.y;
		*/
		
		newPositions.add(pos);
		//add other parent
		
	}
	
	//reset
	for(int i=0;i<children.size();i++){
		TwoDimensionBone bone=children.get(i);
		PointD parentPosition=bone.getAbsolutePosition();
		//LogUtils.log(bone.getName()+"-ab,"+parentPosition.toString());
		//LogUtils.log(bone.getName()+","+bone.x+"x"+bone.y);
		
		parentPosition.x-=bone.x;
		parentPosition.y-=bone.y;
		//LogUtils.log(bone.getName()+"-parent,"+parentPosition.toString());
		
		double newX=newPositions.get(i).x-parentPosition.x;
		double newY=newPositions.get(i).y-parentPosition.y;
		bone.set(newX,newY);
	}
}



/*
 * becareful maybe parent's ref is other
 */
public TwoDimensionBone copy(boolean copyChildren){
	TwoDimensionBone copyBone=new TwoDimensionBone(name, x, y,parent);
	copyBone.setLocked(locked);
	
	if(copyChildren){
		for(TwoDimensionBone child:children){
			TwoDimensionBone copyChild=child.copy(true);
			copyBone.addBone(copyChild);
			
		}
	}
	return copyBone;
}

public PointD getAbsolutePosition(){
	double x=this.x;
	double y=this.y;
	for(TwoDimensionBone parent:BoneUtils.getParents(this)){
		x+=parent.getX();
		y+=parent.getY();
	}
	return new PointD(x,y);
}

public TwoDimensionBone addBone(TwoDimensionBone twoDimensionBone) {

	children.add(twoDimensionBone);
	twoDimensionBone.setParent(this);
	return twoDimensionBone;
}

private boolean locked;

public boolean isLocked() {
	return locked;
}
public void setLocked(boolean locked) {
	this.locked = locked;
}
public String toString(){
	String parent=getParent()==null?"":getParent().getName();
	return parent+","+getName()+","+getX()+","+getY()+","+locked;
}


public boolean isSameStructure(TwoDimensionBone bone,boolean recursive) {
	if(!isSameStructure(this,bone,recursive)){
		return false;
	}
	//not 
	return true;
}


public static boolean isSameStructure(TwoDimensionBone boneA,TwoDimensionBone boneB,boolean recursive) {
	if(boneA.getX()!=boneB.getX()){
		return false;
	}
	if(boneA.getY()!=boneB.getY()){
		return false;
	}
	if(!boneA.getName().equals(boneB.getName())){
		return false;
	}
	if(boneA.isLocked()!=boneB.isLocked()){
		return false;
	}
	
	String boneParentName=boneA.getParent()!=null?boneA.getParent().getName():"";
	String parentName=boneB.getParent()!=null?boneB.getParent().getName():"";
	if(!boneParentName.equals(parentName)){
		return false;
	}
	if(boneA.getChildren().size()!=boneB.getChildren().size()){
		return false;
	}
	if(recursive){
		for(int i=0;i<boneA.getChildren().size();i++){
			TwoDimensionBone childA=boneA.getChildren().get(i);
			TwoDimensionBone childB=boneB.getChildren().get(i);
			boolean same=isSameStructure(childA,childB,true);
			if(!same){
				return false;
			}
		}
	}
	
	//not 
	return true;
}

}
