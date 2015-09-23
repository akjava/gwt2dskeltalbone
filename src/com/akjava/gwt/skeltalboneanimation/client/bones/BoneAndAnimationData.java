package com.akjava.gwt.skeltalboneanimation.client.bones;

//TODO optional
public class BoneAndAnimationData {
private TwoDimensionBone bone;
public BoneAndAnimationData(TwoDimensionBone bone, SkeletalAnimation animation) {
	super();
	this.bone = bone;
	this.animation = animation;
}
public BoneAndAnimationData(TwoDimensionBone bone) {
	this(bone,null);
}
public BoneAndAnimationData() {
	this(null,null);
}
private SkeletalAnimation animation;
public TwoDimensionBone getBone() {
	return bone;
}
public void setBone(TwoDimensionBone bone) {
	this.bone = bone;
}
public SkeletalAnimation getAnimation() {
	return animation;
}
public void setAnimation(SkeletalAnimation animation) {
	this.animation = animation;
}
}
