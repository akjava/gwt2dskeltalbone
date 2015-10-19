package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneWithXYAngle;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.page.CircleLineBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.page.HasSelectionName;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.gwt.canvas.client.Canvas;

public abstract class BoneControler implements HasSelectionName{
private Canvas canvas;
public BoneControler(Canvas canvas) {
	checkNotNull(canvas,"BoneControler:canvas is null");
	this.canvas = canvas;
	settings=new CanvasBoneSettings(canvas, null);
	
	bonePositionControler=new BonePositionControler(settings);
	bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
	
	painter = new CircleLineBonePainter(canvas, this, bonePositionControler);
}
public BonePositionControler getBonePositionControler() {
	return bonePositionControler;
}
private CircleLineBonePainter painter;
public CircleLineBonePainter getPainter() {
	return painter;
}
private CanvasBoneSettings settings;


BonePositionControler bonePositionControler;
public Canvas getCanvas() {
	return canvas;
}

public void setCanvas(Canvas canvas) {
	this.canvas = canvas;
}

public TwoDimensionBone getBone(){
	return settings.getBone();
}

public void setBone(TwoDimensionBone bone){
	checkNotNull(bone,"BoneControler: bone is null");
	settings.setBone(bone);
	bonePositionControler.updateInitialData();
}

private boolean visible=true;

public boolean isVisible() {
	return visible;
}

public void setVisible(boolean visible) {
	this.visible = visible;
}

public void paintBone() {
	if(visible){
		painter.paintBone();
	}
}
public void paintBone(AnimationFrame frame) {
	if(visible){
		painter.paintBone(frame);
	}
}
public Optional<TwoDimensionBone> findBoneByBoneName(@Nullable String name){
	return FluentIterable.from(BoneUtils.getAllBone(getBone())).filter(new BoneNamePredicate(name)).first();
}

public static class BoneNamePredicate implements Predicate<TwoDimensionBone>{
	private String name;
	public BoneNamePredicate(@Nullable String name) {
		super();
		this.name = name;
	}
	@Override
	public boolean apply(@Nullable TwoDimensionBone input) {
		if(input==null){
			return false;
		}
		// TODO Auto-generated method stub
		return Objects.equal(name, input.getName());
	}
	
}

public Optional<PointXY> getBoneInitialPosition(TwoDimensionBone bone){
	checkNotNull("getBoneInitialPosition:bone is null");
	if(bone==null){
		return Optional.absent();
	}
	BoneWithXYAngle data=bonePositionControler.getInitialDataByName(bone.getName());
	if(data!=null){
		return Optional.of(new PointXY(data.getX(),data.getY()).incrementXY(settings.getOffsetX(), settings.getOffsetY()));
	}else{
		return Optional.absent();
	}
}

}
