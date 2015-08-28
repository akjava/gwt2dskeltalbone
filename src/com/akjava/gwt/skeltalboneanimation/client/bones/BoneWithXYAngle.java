package com.akjava.gwt.skeltalboneanimation.client.bones;
public  class BoneWithXYAngle{
	private TwoDimensionBone bone;
	private int x;
	private int y;
	public BoneWithXYAngle(TwoDimensionBone bone, int x, int y, double angle) {
		super();
		this.bone = bone;
		this.x = x;
		this.y = y;
		this.angle = angle;
	}
	private double angle;
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public TwoDimensionBone getBone() {
		return bone;
	}
	public void setBone(TwoDimensionBone bone) {
		this.bone = bone;
	}
}