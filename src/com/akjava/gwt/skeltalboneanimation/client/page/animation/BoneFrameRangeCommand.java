package com.akjava.gwt.skeltalboneanimation.client.page.animation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.akjava.gwt.lib.client.experimental.undo.Command;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;

public class BoneFrameRangeCommand implements Command{
private int frameIndex;
public int getFrameIndex() {
	return frameIndex;
}

public String getBoneName() {
	return boneName;
}

private String boneName;
private double newAngle;
private double oldAngle;
public BoneFrameRangeCommand(int frameIndex, String boneName,  double oldAngle,double newAngle, BoneFrameRangeControler controler) {
	super();
	this.frameIndex = frameIndex;
	this.boneName = boneName;
	this.newAngle = newAngle;
	this.oldAngle = oldAngle;
	this.controler = controler;
}

public void setNewAngle(double newAngle) {
	this.newAngle = newAngle;
}

private BoneFrameRangeControler controler;
	@Override
	public void execute() {
		//
	}

	@Override
	public void undo() {
		checkNotNull(controler,"BoneFrameRangeCommand:need controler");
		controler.setRangeAt(frameIndex, boneName, oldAngle);
	}

	@Override
	public void redo() {
		checkNotNull(controler,"BoneFrameRangeCommand:need controler");
		controler.setRangeAt(frameIndex, boneName, newAngle);
	}
	public static class BoneFrameRangeCommandIndexNamePredicate implements Predicate<BoneFrameRangeCommand>{
		private int frameIndex;
		public BoneFrameRangeCommandIndexNamePredicate(int frameIndex, String boneName) {
			super();
			this.frameIndex = frameIndex;
			this.boneName = boneName;
		}

		private String boneName;
		
		@Override
		public boolean apply(BoneFrameRangeCommand input) {
			// TODO Auto-generated method stub
			return Objects.equal(input.getBoneName(),boneName) && frameIndex == input.getFrameIndex();
		}
		
	}
	

	public static class BoneFrameRangeCommandIndexNameEqualize extends Equivalence<BoneFrameRangeCommand>{

		@Override
		protected boolean doEquivalent(BoneFrameRangeCommand a, BoneFrameRangeCommand b) {
			// TODO Auto-generated method stub
			return Objects.equal(a.getBoneName(), b.getBoneName()) && a.frameIndex==b.frameIndex;
		}

		@Override
		protected int doHash(BoneFrameRangeCommand t) {
			// TODO Auto-generated method stub
			return t.hashCode();
		}
		
	}
	
}
