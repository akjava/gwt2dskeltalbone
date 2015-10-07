package com.akjava.gwt.skeltalboneanimation.client.page.animation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.lib.common.graphics.Point;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;

public class BoneFramePositionCommand implements Command{
private int frameIndex;
public int getFrameIndex() {
	return frameIndex;
}

public String getBoneName() {
	return boneName;
}

private String boneName;
private Point newPosition;
private Point oldPosition;
public BoneFramePositionCommand(int frameIndex, String boneName,  Point oldPosition,Point newPosition, BoneFrameRangeControler controler) {
	super();
	this.frameIndex = frameIndex;
	this.boneName = boneName;
	this.newPosition = newPosition;
	this.oldPosition = oldPosition;
	this.controler = controler;
}



private BoneFrameRangeControler controler;
	@Override
	public void execute() {
		//
	}

	@Override
	public void undo() {
		checkNotNull(controler,"BoneFrameRangeCommand:need controler");
		controler.setPositionAt(frameIndex, boneName, oldPosition);
	}

	@Override
	public void redo() {
		checkNotNull(controler,"BoneFrameRangeCommand:need controler");
		controler.setPositionAt(frameIndex, boneName, newPosition);
	}
	
	public static class BoneFramePositionCommandIndexNamePredicate implements Predicate<BoneFramePositionCommand>{
		private int frameIndex;
		public BoneFramePositionCommandIndexNamePredicate(int frameIndex, String boneName) {
			super();
			this.frameIndex = frameIndex;
			this.boneName = boneName;
		}

		private String boneName;
		
		@Override
		public boolean apply(BoneFramePositionCommand input) {
			// TODO Auto-generated method stub
			return Objects.equal(input.getBoneName(),boneName) && frameIndex == input.getFrameIndex();
		}
		
	}
	

	public static class BoneFramePositionCommandIndexNameEqualize extends Equivalence<BoneFramePositionCommand>{

		@Override
		protected boolean doEquivalent(BoneFramePositionCommand a, BoneFramePositionCommand b) {
			// TODO Auto-generated method stub
			return Objects.equal(a.getBoneName(), b.getBoneName()) && a.frameIndex==b.frameIndex;
		}

		@Override
		protected int doHash(BoneFramePositionCommand t) {
			// TODO Auto-generated method stub
			return t.hashCode();
		}
		
	}


	public void setNewPosition(Point newPoint) {
		this.newPosition=newPoint;
	}
	
}
