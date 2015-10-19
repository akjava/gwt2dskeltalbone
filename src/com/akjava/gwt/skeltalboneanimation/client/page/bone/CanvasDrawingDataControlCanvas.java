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
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CanvasDrawingDataControlCanvas extends VerticalPanel{
	private CanvasDrawingDataControler activeDataControler;
	public Optional<CanvasDrawingDataControler> getActiveDataControler() {
		return Optional.fromNullable(activeDataControler);
	}

	public static interface ZoomListener{
		public void onZoom(double scale);
	}

	public void setActiveDataControler(CanvasDrawingDataControler activeDataControler) {
		this.activeDataControler = activeDataControler;
	}
	
	private ZoomListener zoomListener;


	public ZoomListener getZoomListener() {
		return zoomListener;
	}

	public void setZoomListener(ZoomListener zoomListener) {
		this.zoomListener = zoomListener;
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
		
		scroll.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
			//	LogUtils.log("scroll-handler:"+scroll.getHorizontalScrollPosition()+","+scroll.getVerticalScrollPosition());
			}
		});
		
		int space=18;
		scroll.setSize((width+space)+"px", (height+space)+"px");
		this.add(scroll);
		
		HorizontalPanel bottomPanel=new HorizontalPanel(); //0.5 not so good
		bottomPanel.add(new Label("Scale:"));
		bottomPanel.setVerticalAlignment(ALIGN_MIDDLE);
		DoubleValueListBox scaleBox=new DoubleValueListBox(ImmutableList.of(1.0,2.0,4.0,8.0),1.0,new ValueChangeHandler<Double>() {
			
			

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				canvasControler.setScale(event.getValue());
				zoomListener.onZoom(event.getValue());
			}

			
		});
		bottomPanel.add(scaleBox);
		this.add(bottomPanel);
		
		//FUTURE
		
		/*IntegerBox xBox=new IntegerBox();
		bottomPanel.add(xBox);
		xBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				canvasControler.setScrollX(event.getValue());
			}
		});*/
		
		
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
		
		//TODO merge canvas controler
		canvas.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				event.preventDefault();
				canvasControler.getKeyDownState().setAltKeyDown(event.isAltKeyDown());
				canvasControler.getKeyDownState().setShiftKeyDown(event.isShiftKeyDown());
				canvasControler.getKeyDownState().setControlKeyDown(event.isControlKeyDown());
				
				onCanvasWheeled(event.getDeltaY());
			}
		});
	}
	
	
	public void scrollToPoint(double x,double y){
		if(canvasControler.getScaleX()==1){
			return;
		}
		double scaleX=canvasControler.getScaleX();
		double scaleY=canvasControler.getScaleX();
		//LogUtils.log("scrollToPoint:"+x+","+y);
		
		double targetX=x*scaleX;
		double targetY=y*scaleY;
		
		double canvasWidth=canvas.getCoordinateSpaceWidth();
		double canvasHeight=canvas.getCoordinateSpaceHeight();
		
		double totalWidth=canvasWidth*scaleX;
		double totalHeight=canvasHeight*scaleY;
		
		double scrollX=targetX-canvasWidth/2;
		double scrollY=targetY-canvasHeight/2;
		
		if(scrollX<0){
			scrollX=0;
		}else if(scrollX>totalWidth){
			scrollX=totalWidth;
		}
		
		if(scrollY<0){
			scrollY=0;
		}else if(scrollY>totalHeight){
			scrollY=totalHeight;
		}
		
		
		//LogUtils.log("final scrollToPoint:"+scrollX+","+scrollY);
		scroll.setHorizontalScrollPosition((int) scrollX);
		scroll.setVerticalScrollPosition((int) scrollY);
		
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
