package com.akjava.gwt.skeltalboneanimation.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.MultiImageElementLoader;
import com.akjava.gwt.lib.client.MultiImageElementLoader.MultiImageElementListener;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.lib.common.utils.ListUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/*
 * select image files and move,turn,scale it.
 */
public class FileAndMoveAnimation extends VerticalPanel{

private Canvas canvas;
private CanvasDragMoveControler controler;

private List<ImageDrawingData> datas=new ArrayList<ImageDrawingData>();
private ImageDrawingData selection;
	
	private CheckBox showBoundsCheck;
	public FileAndMoveAnimation(){
		HorizontalPanel buttons=new HorizontalPanel();
		add(buttons);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				uploadImage(ImageElementUtils.create(text));
			}

			
		});
		buttons.add(upload);
		
		showBoundsCheck = new CheckBox("show bounds");
		showBoundsCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateCanvas();
			}
		});
		buttons.add(showBoundsCheck);
		
		
		Button up=new Button("up",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(selection!=null){
				if(ListUtils.isTop(datas, selection)){
					ListUtils.bottom(datas, selection);
					}else{
						ListUtils.up(datas, selection);
					}
				updateCanvas();
				}
			}
		});
		buttons.add(up);
		
		Button down=new Button("down",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(selection!=null){
					if(ListUtils.isBottom(datas, selection)){
						ListUtils.top(datas, selection);
					}else{
						ListUtils.down(datas, selection);
					}
				
				
				updateCanvas();
				}
			}
		});
		buttons.add(down);
		
Button unselect=new Button("unselect",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				selection=null;
				
				updateCanvas();
			}
			
		});
		buttons.add(unselect);
		
		
		canvas = CanvasUtils.createCanvas(800, 800);
		
		CanvasUtils.disableSelection(canvas);//can avoid double click
		GWTHTMLUtils.disableContextMenu(canvas.getElement());
		GWTHTMLUtils.disableSelectionEnd(canvas.getElement());//not work
		add(canvas);
		
		controler = new CanvasDragMoveControler(canvas,new CanvasMoveListener() {
			
			@Override
			public void start(int sx, int sy) {
				selection=null;
				for(int i=datas.size()-1;i>=0;i--){
					ImageDrawingData data=datas.get(i);
				//for(ImageDrawingData data:datas){
					if(data.collision(sx, sy)){
						selection=data;
						break;
					}
				}
				
				/* stop select first
				if(selection!=null){
					datas.remove(selection);
					datas.add(0, selection);
				}
				*/
				
				updateCanvas();
			}
			
			@Override
			public void end(int sx, int sy) {//called on mouse out
				//selection=null; //need selection for zoom
			}
			
			@Override
			public void dragged(int startX, int startY, int endX, int endY, int vectorX, int vectorY) {
				
				if(selection!=null){
					
					
					if(controler.isRightMouse()){
						selection.incrementAngle(vectorX);
					}else{
						selection.incrementX(vectorX);
						selection.incrementY(vectorY);
					}
					selection.updateBounds();
					
					updateCanvas();
				}
				
			}
		});
		canvas.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				int v=event.getDeltaY();//-3 - 3
				
				if(selection!=null){
					int zoom=(int) (100*selection.getScaleX());
					zoom+=v/3*5;
					if(zoom<5){
						zoom=5;
					}
					
					selection.setScaleX((double)zoom/100);
					selection.setScaleY((double)zoom/100);
					selection.updateBounds();
				}
				updateCanvas();
			}
		});
		
		
		MultiImageElementLoader loader=new MultiImageElementLoader();
		loader.loadImages(new MultiImageElementListener() {
			
			@Override
			public void onLoad(List<ImageElement> elements) {
				
			}
			
			@Override
			public void onError(List<String> paths) {
				// TODO Auto-generated method stub
				
			}
		}, "upper.png");
	}
	
	private void uploadImage(ImageElement element) {
		ImageDrawingData data=new ImageDrawingData(element);
		int maxObjectSize=200;
		int imgw=data.getImageElement().getWidth();
		int imgh=data.getImageElement().getHeight();
		int max=imgw>imgh?imgw:imgh;
		
		if(max>maxObjectSize){
			double ratio=(double)maxObjectSize/max;
			data.setScaleX(ratio);
			data.setScaleY(ratio);
		}
		
		data.setX(maxObjectSize);
		data.setY(maxObjectSize);
		datas.add(data);
		updateCanvas();
	}
	
	private void updateCanvas(){
		CanvasUtils.clear(canvas);
		for(int i=0;i<datas.size();i++){
		//for(int i=datas.size()-1;i>=0;i--){//first item draw last,because on-mouse-select item moved to first
			ImageDrawingData data=datas.get(i);
		data.draw(canvas);
		
		if(showBoundsCheck.getValue() || data==selection){
		/*
		canvas.getContext2d().setFillStyle("#f00");
		
		for(PointXY pt:data.getCornerPoint()){
			
			if(pt!=null){
				//CanvasUtils.fillPoint(canvas, pt.getX(), pt.getY());
			}
		}
		*/
		
			String color="#0f0";
			if(data==selection){
				color="#f00";
			}
			CanvasUtils.draw(canvas,data.getCornerPoint(),true,color);
		
			//total bounds.
			//RectCanvasUtils.stroke(data.calculateBounds(), canvas, color);
			
			}
		}
	}
}
