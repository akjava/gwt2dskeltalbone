package com.akjava.gwt.skeltalboneanimation.client;

import java.util.List;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.MultiImageElementLoader;
import com.akjava.gwt.lib.client.MultiImageElementLoader.MultiImageElementListener;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.lib.client.game.PointXY;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageMoveAnimation extends VerticalPanel{
ImageDrawingData data;
private Canvas canvas;
private CanvasDragMoveControler controler;

	int zoom=100;
	public ImageMoveAnimation(){
		HorizontalPanel buttons=new HorizontalPanel();
		add(buttons);
		
		canvas = CanvasUtils.createCanvas(800, 800);
		
		CanvasUtils.disableSelection(canvas);//can avoid double click
		GWTHTMLUtils.disableContextMenu(canvas.getElement());
		GWTHTMLUtils.disableSelectionEnd(canvas.getElement());//not work
		add(canvas);
		
		controler = new CanvasDragMoveControler(canvas,new CanvasMoveListener() {
			
			@Override
			public void start(int sx, int sy) {
				//LogUtils.log("start:"+sx+","+sy);
			}
			
			@Override
			public void end(int sx, int sy) {//called on mouse out
				//LogUtils.log("end:"+sx+","+sy);
			}
			
			@Override
			public void dragged(int startX, int startY, int endX, int endY, int vectorX, int vectorY) {
				
				if(data!=null){
					
					
					if(controler.isRightMouse()){
						data.incrementAngle(vectorX);
					}else{
						data.incrementX(vectorX);
						data.incrementY(vectorY);
					}
					
					
					updateCanvas();
				}
				
			}
		});
		canvas.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				int v=event.getDeltaY();
				LogUtils.log(v);
				zoom+=v/3*5;
				if(zoom<10){
					zoom=10;
				}
				data.setScaleX((double)zoom/100);
				data.setScaleY((double)zoom/100);
				updateCanvas();
			}
		});
		
		
		MultiImageElementLoader loader=new MultiImageElementLoader();
		loader.loadImages(new MultiImageElementListener() {
			
			@Override
			public void onLoad(List<String> paths,List<ImageElement> elements) {
				data=new ImageDrawingData(paths.get(0),elements.get(0));
				data.setX(data.getImageElement().getWidth());
				data.setY(data.getImageElement().getHeight());
				updateCanvas();
			}
			
			@Override
			public void onError(List<String> paths) {
				// TODO Auto-generated method stub
				
			}
		}, "upper.png");
	}
	private void updateCanvas(){
		CanvasUtils.clear(canvas);
		data.draw(canvas);
		
		canvas.getContext2d().setFillStyle("#f00");
		for(PointXY pt:data.getCornerPoint()){
			LogUtils.log(pt);
			if(pt!=null){
				CanvasUtils.fillPoint(canvas, pt.getX(), pt.getY());
			}
		}
		//canvas.getContext2d().setStrokeStyle("#0f0");
		RectCanvasUtils.stroke(data.calculateBounds(), canvas, "#0f0");
	}
}
