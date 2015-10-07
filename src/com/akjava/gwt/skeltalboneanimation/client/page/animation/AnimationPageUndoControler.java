package com.akjava.gwt.skeltalboneanimation.client.page.animation;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.page.animation.BoneFramePositionCommand.BoneFramePositionCommandIndexNamePredicate;
import com.akjava.gwt.skeltalboneanimation.client.page.animation.BoneFrameRangeCommand.BoneFrameRangeCommandIndexNamePredicate;
import com.akjava.lib.common.graphics.Point;
import com.google.common.base.Optional;

public class AnimationPageUndoControler  extends SimpleUndoControler{
private BoneFrameRangeControler controler;
	public AnimationPageUndoControler(BoneFrameRangeControler controler){
		this.controler=controler;
	}
	
	/**
	 * find last undo-command for collapse
	 * @param frameIndex
	 * @param boneName
	 * @return
	 */
	protected Optional<BoneFrameRangeCommand> getLastCommandIfBoneFrameRangeCommand(int frameIndex,String boneName){
		for(Command command:getLastUndoCommand().asSet()){
			if(command instanceof BoneFrameRangeCommand){
				BoneFrameRangeCommand boneCommand= (BoneFrameRangeCommand)command;
				if(new BoneFrameRangeCommandIndexNamePredicate(frameIndex,boneName).apply(boneCommand)){
					return Optional.fromNullable(boneCommand);
				}
			}
		}
		return Optional.absent();
	}
	protected Optional<BoneFramePositionCommand> getLastCommandIfBoneFramePositionCommand(int frameIndex,String boneName){
		for(Command command:getLastUndoCommand().asSet()){
			if(command instanceof BoneFramePositionCommand){
				BoneFramePositionCommand boneCommand= (BoneFramePositionCommand)command;
				if(new BoneFramePositionCommandIndexNamePredicate(frameIndex,boneName).apply(boneCommand)){
					return Optional.fromNullable(boneCommand);
				}
			}
		}
		return Optional.absent();
	}

	//collapse command inside
	public void executeBoneAngleRangeChanged(int index, String boneName, double oldAngle, int newAngle) {
		//LogUtils.log("executeBoneAngleRangeChanged");
		Optional<BoneFrameRangeCommand> command=this.getLastCommandIfBoneFrameRangeCommand(index,boneName);
		if(command.isPresent()){
			command.get().setNewAngle(newAngle);
		}else{
			BoneFrameRangeCommand newCommand=new BoneFrameRangeCommand(index,boneName,oldAngle,newAngle,controler);
			this.execute(newCommand);
		}
	}
	
	public void executeBonePositionChanged(int index, String boneName, Point oldPoint,Point newPoint) {
		Optional<BoneFramePositionCommand> command=this.getLastCommandIfBoneFramePositionCommand(index,boneName);
		if(command.isPresent()){
			command.get().setNewPosition(newPoint);
		}else{
			BoneFramePositionCommand newCommand=new BoneFramePositionCommand(index,boneName,oldPoint,newPoint,controler);
			this.execute(newCommand);
		}
		
		
		//LogUtils.log("executeBoneAngleRangeChanged");
		
	}
	public void executeBoneAnimationChanged(List<AnimationFrame> oldFrames, List<AnimationFrame> newFrames, int oldSelection,int newSelection){
		//LogUtils.log("executeBoneAnimationChanged");
		execute(new BoneAnimationCommand(oldFrames, newFrames, oldSelection, newSelection, controler));
	}
}
