package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler.KeyDownState;


public class ImageDrawingDataControler implements CanvasDrawingDataControler{

	private ImageDrawingDataOwner owner;
	
	public ImageDrawingDataControler(ImageDrawingDataOwner owner) {
		super();
		this.owner = owner;
	}

	private ImageDrawingData collisioned;
	
	private int baseRatio=100;
	public void onWhelled(int delta,KeyDownState keydownState){
		if(!owner.isEditable()){
			return;
		}
		
		ImageDrawingData selection=owner.getSelection();
		if(selection!=null && selection.isVisible()){
			int zoom=(int) (baseRatio*selection.getScaleX());
			
			int vector=1;
			if(delta<0){
				vector=-1;
			}
			
			int add=keydownState.isShiftKeyDown()?1:5;
			zoom+=vector*add;
			if(zoom<5){
				zoom=5;
			}
			
			selection.setScaleX((double)zoom/baseRatio);
			selection.setScaleY((double)zoom/baseRatio);
			selection.updateBounds();
		}
	}
	
	public void onTouchDragged(int vectorX, int vectorY,boolean rightButton,KeyDownState keydownState){
		//LogUtils.log("dragged");
		
		if(collisioned==null){
			return;
		}
		
		if(rightButton){
			collisioned.incrementAngle(vectorX);
		}else{
			collisioned.incrementX(vectorX);
			collisioned.incrementY(vectorY);
		}
		
		collisioned.updateBounds();
	}
	
	public boolean onTouchStart(int mx,int my,KeyDownState keydownState){
		collisioned = owner.collision(mx, my);
		
		
		return collisioned!=null;
	}
	
	public void onTouchEnd(int mx,int my,KeyDownState keydownState){
		
	}
	//TODO double-click

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "image-drawing-data-controler";
	}

}
