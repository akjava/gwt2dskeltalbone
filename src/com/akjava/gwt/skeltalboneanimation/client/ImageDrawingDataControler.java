package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.lib.client.LogUtils;

public class ImageDrawingDataControler implements DrawingDataControler{

	private ImageDrawingDataOwner owner;
	
	public ImageDrawingDataControler(ImageDrawingDataOwner owner) {
		super();
		this.owner = owner;
	}

	private ImageDrawingData collisioned;
	
	public void onWhelled(int delta,boolean shiftDowned){
		if(!owner.isEditable()){
			return;
		}
		
		ImageDrawingData selection=owner.getSelection();
		if(selection!=null && selection.isVisible()){
			int zoom=(int) (100*selection.getScaleX());
			
			int vector=1;
			if(delta<0){
				vector=-1;
			}
			
			int add=shiftDowned?1:5;
			zoom+=vector*add;
			if(zoom<5){
				zoom=5;
			}
			
			selection.setScaleX((double)zoom/100);
			selection.setScaleY((double)zoom/100);
			selection.updateBounds();
		}
	}
	
	public void onTouchDragged(int vectorX, int vectorY,boolean rightButton){
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
	
	public boolean onTouchStart(int mx,int my){
		collisioned = owner.collision(mx, my);
		
		
		return collisioned!=null;
	}
	
	public void onTouchEnd(int mx,int my){
		
	}
	//TODO double-click

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "image-drawing-data-controler";
	}

}