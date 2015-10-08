package com.akjava.gwt.skeltalboneanimation.client.page.animation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.lib.common.graphics.Point;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;

public class BoneFrameScaleCommand implements Command{
private int frameIndex;
public int getFrameIndex() {
	return frameIndex;
}

public String getBoneName() {
	return boneName;
}

private String boneName;//FUTURE support individual scale
private double newScale;
private double oldScale;
private boolean collapse;
public boolean isCollapse() {
	return collapse;
}

public BoneFrameScaleCommand(int frameIndex, String boneName,  double oldPosition,double newPosition, boolean collapse,BoneFrameRangeControler controler) {
	super();
	this.frameIndex = frameIndex;
	this.boneName = boneName;
	this.newScale = newPosition;
	this.oldScale = oldPosition;
	this.controler = controler;
	this.collapse=collapse;
}



private BoneFrameRangeControler controler;
	@Override
	public void execute() {
		//
	}

	@Override
	public void undo() {
		checkNotNull(controler,"BoneFrameRangeCommand:need controler");
		controler.setScaleAt(frameIndex, boneName, oldScale);
	}

	@Override
	public void redo() {
		checkNotNull(controler,"BoneFrameRangeCommand:need controler");
		controler.setScaleAt(frameIndex, boneName, newScale);
	}
	
	//check collapse
	public static class BoneFrameScaleCommandIndexNamePredicate implements Predicate<BoneFrameScaleCommand>{
		private int frameIndex;
		public BoneFrameScaleCommandIndexNamePredicate(int frameIndex, String boneName) {
			super();
			this.frameIndex = frameIndex;
			this.boneName = boneName;
		}

		private String boneName;
		
		@Override
		public boolean apply(BoneFrameScaleCommand input) {
			// TODO Auto-generated method stub
			return Objects.equal(input.getBoneName(),boneName) && frameIndex == input.getFrameIndex() ;
		}
		
	}
	

	public static class BoneFramePositionCommandIndexNameEqualize extends Equivalence<BoneFrameScaleCommand>{

		@Override
		protected boolean doEquivalent(BoneFrameScaleCommand a, BoneFrameScaleCommand b) {
			// TODO Auto-generated method stub
			return Objects.equal(a.getBoneName(), b.getBoneName()) && a.frameIndex==b.frameIndex;
		}

		@Override
		protected int doHash(BoneFrameScaleCommand t) {
			// TODO Auto-generated method stub
			return t.hashCode();
		}
		
	}


	public void setNewScale(double newScale) {
		this.newScale=newScale;
	}
	
}
