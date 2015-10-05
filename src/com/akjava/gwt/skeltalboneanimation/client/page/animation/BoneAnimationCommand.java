package com.akjava.gwt.skeltalboneanimation.client.page.animation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;

public class BoneAnimationCommand implements Command{
private List<AnimationFrame> oldFrames;
private List<AnimationFrame> newFrames;
private BoneFrameRangeControler controler;
private int oldSelection;
private int newSelection;

	public BoneAnimationCommand(List<AnimationFrame> oldFrames, List<AnimationFrame> newFrames, int oldSelection,int newSelection,BoneFrameRangeControler controler) {
	super();
	this.oldFrames = oldFrames;
	this.newFrames = newFrames;
	this.controler = controler;
	this.oldSelection=oldSelection;
	this.newSelection=newSelection;
}

	@Override
	public void undo() {
		checkNotNull(controler,"BoneAnimationCommand:need controler");
		controler.replaceAnimations(oldFrames, oldSelection);
	}

	@Override
	public void redo() {
		checkNotNull(controler,"BoneFrameRangeCommand:need:need controler");
		
		controler.replaceAnimations(newFrames, newSelection);
		
	}

	@Override
	public void execute() {
		// these snap shot style undo,already executed.
		
	}

	
}
