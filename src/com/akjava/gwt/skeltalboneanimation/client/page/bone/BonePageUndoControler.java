package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.UndoControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.BonePage.BonePositionData;
import com.google.common.collect.Lists;

public class BonePageUndoControler extends UndoControler{
private BoneUpdater boneUpdater;

public BonePageUndoControler(BoneUpdater boneUpdater){
	this.boneUpdater=boneUpdater;
}


private void execAddBone(TwoDimensionBone parent,TwoDimensionBone bone){
	parent.addBone(bone);
	
	boneUpdater.refreshTree(parent);
	boneUpdater.updateBoneDatas();
	
	boneUpdater.setSelected(bone);	
}
private void execRemoveBone(TwoDimensionBone bone){
	LogUtils.log("execRemove:"+bone);
	bone.getParent().getChildren().remove(bone);
	
	boneUpdater.refreshTree(bone);
	boneUpdater.updateBoneDatas();
	boneUpdater.setSelected(bone.getParent());	
	
}

private void execRemoveBoneWithoutChildren(TwoDimensionBone target){
	
	
	target.getParent().getChildren().remove(target);
	
	
	TwoDimensionBone newParent=target.getParent();
	double addOldX=target.getX();
	double addOldY=target.getY();
	for(TwoDimensionBone bone:target.getChildren()){
			bone.setX(bone.getX()+addOldX);
			bone.setY(bone.getY()+addOldY);
			newParent.addBone(bone);
		}
	
	boneUpdater.refreshTree(target);
	boneUpdater.updateBoneDatas();
	boneUpdater.setSelected(target.getParent());	
}

public class BonePositionChangeCommandByWheel extends BonePositionChangeCommand{
	private TwoDimensionBone selected;
	public TwoDimensionBone getSelected() {
		return selected;
	}
	public BonePositionChangeCommandByWheel(List<BonePositionData> datas,TwoDimensionBone selected) {
		super(datas);
		this.selected=selected;
	}
	
}
public class BonePositionChangeCommand implements Command{

	List<BonePositionData> datas;
	public List<BonePositionData> getDatas() {
		return datas;
	}

	public BonePositionChangeCommand(List<BonePositionData> datas) {
		super();
		this.datas = datas;
	}

	@Override
	public void execute() {
		//usually set by self.
		
		/*
		for(BonePositionData data:datas){
			data.bone.set(data.afterPoint);
		}
		updateCanvas();
		*/
		
	}

	@Override
	public void undo() {
		for(BonePositionData data:datas){
			data.bone.set(data.beforePoint);
		}
		boneUpdater.updateBoneDatas();
	}

	@Override
	public void redo() {
		for(BonePositionData data:datas){
			data.bone.set(data.afterPoint);
		}
		boneUpdater.updateBoneDatas();
	}
	
}

public AddBoneCommand createAddBoneCommand(TwoDimensionBone bone){
	return new AddBoneCommand(bone);
}
public RemoveBoneCommand createRemoveBoneCommand(boolean child,TwoDimensionBone bone){
	return new RemoveBoneCommand(child,bone);
}

public BonePositionChangeCommand createBonePositionChangeCommand(List<BonePositionData> datas){
	return new BonePositionChangeCommand(datas);
}
public BonePositionChangeCommandByWheel createBonePositionChangeCommandByWheel(List<BonePositionData> datas,TwoDimensionBone selectedBone){
	return new BonePositionChangeCommandByWheel(datas,selectedBone);
}

public class AddBoneCommand implements Command{

	TwoDimensionBone parent;
	TwoDimensionBone newBone;
	public AddBoneCommand(TwoDimensionBone parent) {
		super();
		this.parent = parent;
	}

	@Override
	public void execute() {
		newBone=new TwoDimensionBone(BoneUtils.createBoneName(parent), 0, 0);
		execAddBone(parent,newBone);
	}

	@Override
	public void undo() {
		//remove
		execRemoveBone(newBone);
	}

	@Override
	public void redo() {
		execAddBone(parent,newBone);
	}
	
}



public class RemoveBoneCommand implements Command{

	boolean withChildren;
	TwoDimensionBone bone;
	public RemoveBoneCommand(boolean withChildren,TwoDimensionBone bone) {
		super();
		this.withChildren = withChildren;
		this.bone=bone;
	}

	@Override
	public void execute() {
		
		if(withChildren){
			execRemoveBone(bone);
		}else{
			execRemoveBoneWithoutChildren(bone);
		}
	}

	@Override
	public void undo() {
		if(withChildren){
			execAddBone(bone.getParent(),bone);
		}else{
			List<TwoDimensionBone> children=Lists.newArrayList();
			
			for(TwoDimensionBone child:bone.getChildren()){
				child.getParent().getChildren().remove(child);
				
				
				double insertBoneX=bone.getX();
				double insertBoneY=bone.getY();
			
				child.setX(child.getX()-insertBoneX);
				child.setY(child.getY()-insertBoneY);
				
				children.add(child);
			}
			bone.getChildren().clear();
			
			for(TwoDimensionBone child:children){
				bone.addBone(child);
			}
			
			execAddBone(bone.getParent(),bone);
		}
	}

	@Override
	public void redo() {
		if(withChildren){
			execRemoveBone(bone);
		}else{
			execRemoveBoneWithoutChildren(bone);
		}
	}
	
}
}
