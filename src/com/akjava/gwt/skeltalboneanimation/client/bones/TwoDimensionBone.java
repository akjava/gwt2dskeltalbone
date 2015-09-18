package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.game.PointD;
import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;

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

public TwoDimensionBone copy(boolean copyChildren){
	TwoDimensionBone copyBone=new TwoDimensionBone(name, x, y,parent);
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
public String toString(){
	String parent=getParent()==null?"":getParent().getName();
	return parent+","+getName()+","+getX()+","+getY();
}
}
