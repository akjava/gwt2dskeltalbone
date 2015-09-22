package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import static com.google.common.base.Preconditions.checkNotNull;

import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneWithXYAngle;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.page.CircleLineBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.page.HasSelectionName;
import com.google.common.base.Optional;
import com.google.gwt.canvas.client.Canvas;

public abstract class BoneControler implements HasSelectionName{
private Canvas canvas;
public BoneControler(Canvas canvas) {
	super();
	this.canvas = canvas;
	settings=new CanvasBoneSettings(canvas, null);
	
	bonePositionControler=new BonePositionControler(settings);
	bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
	
	painter = new CircleLineBonePainter(canvas, this, bonePositionControler);
}
private CircleLineBonePainter painter;
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
