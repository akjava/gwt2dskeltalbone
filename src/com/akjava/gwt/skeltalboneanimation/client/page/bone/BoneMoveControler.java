package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler.KeyDownState;
import com.akjava.gwt.skeltalboneanimation.client.CanvasDrawingDataControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;

public abstract class BoneMoveControler implements CanvasDrawingDataControler{

	private TwoDimensionBone selection;
	@Override
	public void onWhelled(int delta,KeyDownState keydownState) {
		if(selection!=null){
			double scale=0.01;
			if(delta>0){
				scale*=-1;
			}
			selection.changeChildrenScale(1.0+scale);
			onBoneMoveEnd();
		}
	}

	public abstract void onBoneMoveEnd();
	
	//public abstract void onSelectBone(TwoDimensionBone bone);

	@Override
	public void onTouchDragged(int vectorX, int vectorY, boolean rightButton,KeyDownState keydownState) {
		if(selection==null){
			return;
		}
			/*
			if(bone==getRootBone()){
				
				//rotate root
				if( canvasControler.isRightMouse()&&vectorX!=0){
					bone.rotateChildrens(vectorX);
					updateBoneDatas();
				}
				
				
				return;//rootBone is fixed,so far.
			}
			*/
			
			//other bone moving
			if(rightButton){
				if(vectorX!=0){
					selection.rotateChildrens(vectorX);
					onBoneMoveEnd();
				}
				return;
			}
			
			//normal xy-move
			
			/*
			List<BonePositionData> posData=Lists.newArrayList();
			
			posData.add(new BonePositionData(bone, bone.getX(), bone.getY(), bone.getX()+vectorX, bone.getY()+vectorY));
			
			*/
			
			
			selection.setX(selection.getX()+vectorX);
			selection.setY(selection.getY()+vectorY);
			
			
			//move only selection
			if(keydownState.isShiftKeyDown()){
				for(TwoDimensionBone child:selection.getChildren()){
					//posData.add(new BonePositionData(child, child.getX(), child.getY(), child.getX()-vectorX, child.getY()-vectorY));
					
					child.setX(child.getX()-vectorX);
					child.setY(child.getY()-vectorY);
				}
			}
			//TODO move to mosue up
			//undoControler.execute(new BonePositionChangeCommand(posData));
			
			onBoneMoveEnd();
		
	}

	@Override
	public boolean onTouchStart(int mx, int my,KeyDownState keydownState) {
		
		
		return false;
	}

	@Override
	public void onTouchEnd(int mx, int my,KeyDownState keydownState) {
		// TODO Auto-generated method stub
		
		onBoneMoveEnd();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
