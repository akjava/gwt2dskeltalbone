package com.akjava.gwt.skeltalboneanimation.client.page;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.UploadedFileManager.BackgroundChangeListener;
import com.akjava.gwt.skeltalboneanimation.client.UploadedFileManager.BoneChangeListener;
import com.akjava.gwt.skeltalboneanimation.client.UploadedFileManager.TextureDataChangeListener;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractPage extends DockLayoutPanel implements SkeltalBonePage{
protected MainManager manager;
	public AbstractPage(final MainManager manager){
		super(Unit.PX);
		this.manager=manager;
		manager.addPage(this);
		
		initialize();
		
		Widget westWidget=createWestPanel();
		if(westWidget!=null){
			this.addWest(westWidget,300);
		}else{
			LogUtils.log("AbstractPage:west-widget is null");
		}
		Widget centerWidget=createCenterPanel();
		if(centerWidget!=null){
		this.add(centerWidget);
		}else{
			LogUtils.log("AbstractPage:center-widget is null");
		}
		
		manager.getUploadedFileManager().addBackgroundChangeListener(new BackgroundChangeListener() {
			
			@Override
			public void backgroundChanged(ImageDrawingData backgroundDat) {
				
				if(manager.isSelected(AbstractPage.this)){
					onBackgroundChanged(backgroundDat);
				}else{
					needBackgroundUpdate=true;
				}
				
				
			}
		});
		
		manager.getUploadedFileManager().addBoneChangeListener(new BoneChangeListener() {
			
			@Override
			public void boneChanged(TwoDimensionBone bone) {
				if(manager.isSelected(AbstractPage.this)){
					onBoneChanged(bone);
				}else{
					needBoneUpdate=true;
				}
			}
		});
		
		manager.getUploadedFileManager().addTextureDataChangeListener(new TextureDataChangeListener() {
			
			

			@Override
			public void textureDataChanged(TextureData textureData) {
				if(manager.isSelected(AbstractPage.this)){
					onTextureDataChanged(textureData);
				}else{
					needTextureUpdate=true;
				}
			}
		});
	}
	
	

	protected CanvasDragMoveControler canvasControler;
	public void initializeCanvas(){
		canvas = CanvasUtils.createCanvas(800, 800);
				CanvasUtils.disableSelection(canvas);//can avoid double click
				GWTHTMLUtils.disableContextMenu(canvas.getElement());
				GWTHTMLUtils.disableSelectionEnd(canvas.getElement());//not work
				add(canvas);
				
				canvasControler = new CanvasDragMoveControler(canvas,new CanvasMoveListener() {
					
					@Override
					public void start(int sx, int sy) {
						onCanvasTouchStart(sx,sy);
						
					}
					
					@Override
					public void end(int sx, int sy) {//called on mouse out
						//selection=null; //need selection for zoom
						onCanvasTouchEnd(sx,sy);
					}
					
					@Override
					public void dragged(int startX, int startY, int endX, int endY, int vectorX, int vectorY) {
						onCanvasDragged(vectorX,vectorY);
						
					}
				});
				canvas.addMouseWheelHandler(new MouseWheelHandler() {
					@Override
					public void onMouseWheel(MouseWheelEvent event) {
						
						onCanvasWheeled(event.getDeltaY(),event.isShiftKeyDown());
					}
				});
		
	}
	
	protected abstract void  onCanvasTouchEnd(int sx,int sy);
	protected abstract void  onCanvasTouchStart(int sx,int sy);
	protected abstract void onCanvasDragged(int vectorX, int vectorY);
	protected abstract void onCanvasWheeled(int delta,boolean shiftDown);

	protected abstract void onBoneChanged(TwoDimensionBone bone);
	
	protected abstract void onBackgroundChanged(ImageDrawingData background);
	
	protected abstract void onTextureDataChanged(TextureData textureData) ;
	
	protected abstract void initialize();
	protected abstract Widget createCenterPanel() ;

	protected abstract Widget createWestPanel();


	protected boolean needBoneUpdate;
	protected boolean needBackgroundUpdate;
	protected boolean needTextureUpdate;
	protected Canvas canvas;
	@Override
	public void onSelection() {
		if(needBoneUpdate){
			onBoneChanged(manager.getUploadedFileManager().getBone());
			needBoneUpdate=false;
		}
		if(needBackgroundUpdate){
			onBackgroundChanged(manager.getUploadedFileManager().getBackgroundData());
		}
		
		if(needTextureUpdate){
			onTextureDataChanged(manager.getUploadedFileManager().getTextureData());
		}
		
		updateDatas();
		updateCanvas();
	}
	
	protected abstract void updateDatas();



	protected abstract void updateCanvas();
}
