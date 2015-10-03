package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import javax.annotation.Nullable;

import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;

public interface BoneUpdater {
	public void setSelected(@Nullable TwoDimensionBone bone);
	public void refreshTree(TwoDimensionBone bone);
	public void updateBoneDatas();
}
