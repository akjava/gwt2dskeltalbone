package com.akjava.gwt.skeltalboneanimation.client.page.animation;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler;
import com.akjava.gwt.skeltalboneanimation.client.page.animation.BoneFrameRangeCommand.BoneFrameRangeCommandIndexNamePredicate;
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

	//collapse command inside
	public void executeBoneAngleRangeChanged(int index, String boneName, double oldAngle, int newAngle) {
		Optional<BoneFrameRangeCommand> command=this.getLastCommandIfBoneFrameRangeCommand(index,boneName);
		if(command.isPresent()){
			command.get().setNewAngle(newAngle);
		}else{
			BoneFrameRangeCommand newCommand=new BoneFrameRangeCommand(index,boneName,oldAngle,newAngle,controler);
			this.execute(newCommand);
		}
	}
}
