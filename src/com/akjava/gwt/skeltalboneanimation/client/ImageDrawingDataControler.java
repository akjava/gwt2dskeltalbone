package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler.KeyDownState;
import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.ImageDrawingDataCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.ImageDrawingDatasUpdater;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Optional;


public class ImageDrawingDataControler implements CanvasDrawingDataControler{

	private ImageDrawingDataOwner owner;
	
	public ImageDrawingDataControler(ImageDrawingDataOwner owner) {
		super();
		this.owner = owner;
	}
	
	private SimpleUndoControler undoControler;
    private ImageDrawingDatasUpdater updater;
    
	public Optional<SimpleUndoControler> getUndoControler() {
		return Optional.fromNullable(undoControler);
	}

	public void setUndoControler(SimpleUndoControler undoControler,ImageDrawingDatasUpdater updater) {
		this.undoControler = undoControler;
		this.updater=updater;
	}

	private boolean isSupportUndo(){
		return undoControler!=null;
	}
	
	private ImageDrawingData collisioned;
	
	private int baseRatio=100;
	public void onWhelled(int delta,KeyDownState keydownState){
		if(!owner.isEditable()){
			return;
		}
		
		
		
		ImageDrawingData selection=owner.getSelection();
		if(selection!=null && selection.isVisible()){
			onWheelStart();
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
			onWheelEnd();
		}
	}
	
	private Optional<ImageDrawingDataCommand> getLastImageDrawingDataCommandIfExist(){
		
		for(Command command:undoControler.getLastUndoCommand().asSet()){
			if(command instanceof ImageDrawingDataCommand){
				ImageDrawingDataCommand dCommand=(ImageDrawingDataCommand)command;
				if(dCommand.isCollapseCommand()){
					return Optional.of(dCommand);
				}
				
			}
		}
		return Optional.absent();
	}
	
	private void onWheelEnd() {
		Optional<ImageDrawingDataCommand> optional=getLastImageDrawingDataCommandIfExist();
		if(optional.isPresent()){
			ImageDrawingData after=collisioned.copy();
			optional.get().setAfterData(after);
			before=null;
		}else{
			ImageDrawingData after=collisioned.copy();
			undoControler.execute(new ImageDrawingDataCommand(updater,collisioned,before,after,true));
			before=null;
		}
	}

	private void onWheelStart() {
		if(isSupportUndo() & collisioned!=null){
			before=collisioned.copy();
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
	
	ImageDrawingData before;
	public boolean onTouchStart(int mx,int my,KeyDownState keydownState){
		collisioned = owner.collision(mx, my);
		
		if(isSupportUndo() && collisioned!=null){
			before=collisioned.copy();
		}
		
		return collisioned!=null;
	}
	
	public void onTouchEnd(int mx,int my,KeyDownState keydownState){
		if(isSupportUndo()){
			if(before==null){
				return;
			}
			
			ImageDrawingData after=collisioned.copy();
			undoControler.execute(new ImageDrawingDataCommand(updater,collisioned,before,after,false));
			before=null;
				
		}
		
	}
	//modified or not
	public static class ImageDrawingDataEquivalenceWithoutIdAndImageElement extends Equivalence<ImageDrawingData>{

		@Override
		protected boolean doEquivalent(ImageDrawingData a, ImageDrawingData b) {
			
			if(!Objects.equal(a.getImageName(),(b.getImageName()))){
				return false;
			}
			if(!Objects.equal(a.getBoneName(),(b.getBoneName()))){
				return false;
			}
			if(a.getX()!=b.getX()){
				return false;
			}
			if(a.getAngle()!=b.getAngle()){
				return false;
				}
				if(a.getScaleX()!=b.getScaleX()){
				return false;
				}
				if(a.getScaleY()!=b.getScaleY()){
				return false;
				}
				if(a.getAlpha()!=b.getAlpha()){
				return false;
				}
				if(a.isFlipHorizontal()!=b.isFlipHorizontal()){
				return false;
				}
				if(a.isFlipVertical()!=b.isFlipVertical()){
				return false;
				}
				if(a.isVisible()!=b.isVisible()){
				return false;
				}

			
			return true;
		}

		@Override
		protected int doHash(ImageDrawingData t) {
			// not modify
			return t.hashCode();
		}
		
	}
	
	//TODO double-click

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "image-drawing-data-controler";
	}

}
