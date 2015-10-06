package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.skeltalboneanimation.client.CanvasDrawingDataControler;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CanvasDrawingDataControlCanvas extends VerticalPanel{
	private CanvasDrawingDataControler activeDataControler;
	public Optional<CanvasDrawingDataControler> getActiveDataControler() {
		return Optional.fromNullable(activeDataControler);
	}


	public void setActiveDataControler(CanvasDrawingDataControler activeDataControler) {
		this.activeDataControler = activeDataControler;
	}


	protected CanvasDragMoveControler canvasControler;
	protected CanvasUpdater canvasUpdater;
	protected Canvas canvas;
	
	public double getScale(){
		return canvasControler.getScaleX();//TODO how to handle scaley
	}
	
	public Canvas getCanvas() {
		return canvas;
	}


	public void setCanvas(Canvas canvas) {
		scroll.clear();
		scroll.add(canvas);
		this.canvas = canvas;
	}


	List<CanvasDrawingDataControler> drawingDataControlers;
	private ScrollPanel scroll;
	
	public CanvasDrawingDataControlCanvas(Canvas canvas,int width,int height,CanvasUpdater canvasUpdater){
		this.canvas=checkNotNull(canvas,"CanvasDrawingDataControlCanvas:need canvas");
		this.canvasUpdater=canvasUpdater;
		drawingDataControlers=Lists.newArrayList();
		scroll = new ScrollPanel();
		scroll.add(canvas);
		int space=18;
		scroll.setSize((width+space)+"px", (height+space)+"px");
		this.add(scroll);
		
		HorizontalPanel scalePanel=new HorizontalPanel();
		IntegerValueListBox scaleBox=new IntegerValueListBox(ImmutableList.of(1,2,4),1,new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				canvasControler.setScale(event.getValue());
			}
		});
		scalePanel.add(scaleBox);
		this.add(scalePanel);
		
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
				event.preventDefault();
				onCanvasWheeled(event.getDeltaY());
			}
		});
	}
	
	
	public CanvasDragMoveControler getCanvasControler() {
		return canvasControler;
	}


	public boolean hasActiveDataControler(){
		return activeDataControler!=null;
	}
	public void add(CanvasDrawingDataControler controler){
		drawingDataControlers.add(controler);
	}
	
	protected Optional<CanvasDrawingDataControler> onCanvasTouchStart(int sx, int sy) {
		//LogUtils.log("touchStart");
		CanvasDrawingDataControler active=null;
		for(CanvasDrawingDataControler data:drawingDataControlers){
			if(data.onTouchStart(sx, sy,canvasControler.isRightMouse(),canvasControler.getKeyDownState())){
				active=data;
				break;
			}
		}
		this.setActiveDataControler(active);
		canvasUpdater.updateCanvas();
		return Optional.fromNullable(active);
	}
	
	protected void onCanvasTouchEnd(int sx, int sy) {
		
		for(CanvasDrawingDataControler activeDataControler:getActiveDataControler().asSet()){
			
			activeDataControler.onTouchEnd(sx, sy,canvasControler.isRightMouse(),canvasControler.getKeyDownState());
		}
	}
	protected void onCanvasDragged(int vectorX, int vectorY) {
		for(CanvasDrawingDataControler activeDataControler:getActiveDataControler().asSet()){
			
			activeDataControler.onTouchDragged(vectorX, vectorY, canvasControler.isRightMouse(),canvasControler.getKeyDownState());
			canvasUpdater.updateCanvas();
		}
	}
	protected void onCanvasWheeled(int delta) {
		for(CanvasDrawingDataControler activeControler:getActiveDataControler().asSet()){
			activeControler.onWhelled(delta, canvasControler.getKeyDownState());
			canvasUpdater.updateCanvas();
		}
	}
		
}
