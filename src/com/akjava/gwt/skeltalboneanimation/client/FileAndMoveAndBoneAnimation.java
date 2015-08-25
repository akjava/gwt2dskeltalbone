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
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange.BoneControlListener;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeltalAnimations;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.lib.common.graphics.Rect;
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
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;


/*
 * select image files and move,turn,scale it.
 */
public class FileAndMoveAndBoneAnimation extends VerticalPanel{

private Canvas canvas;
private CanvasDragMoveControler canvasControler;

private List<ImageDrawingData> datas=new ArrayList<ImageDrawingData>();
private ImageDrawingData selection;
	
	private CheckBox showBoundsCheck;
	
	private boolean modeAnimation;
	public FileAndMoveAndBoneAnimation(){
		HorizontalPanel upButtons=new HorizontalPanel();
		add(upButtons);
		
		final HorizontalPanel buttons1=new HorizontalPanel();
		final HorizontalPanel buttons2=new HorizontalPanel();
		buttons2.setVisible(false);
		
		ToggleButton animationBt=new ToggleButton("animation-mode");
		animationBt.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				modeAnimation=event.getValue();
				buttons1.setVisible(!event.getValue());	
				buttons2.setVisible(event.getValue());	
				updateCanvas();
			}
			
		});
		upButtons.add(animationBt);
		
		upButtons.add(buttons1);
		upButtons.add(buttons2);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				uploadImage(ImageElementUtils.create(text));
			}

			
		});
		buttons1.add(upload);
		
		showBoundsCheck = new CheckBox("show bounds");
		showBoundsCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateCanvas();
			}
		});
		buttons1.add(showBoundsCheck);
		
		
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
		buttons1.add(up);
		
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
		buttons1.add(down);
		
Button unselect=new Button("unselect",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				selection=null;
				
				updateCanvas();
			}
			
		});
		buttons1.add(unselect);
		
		rootBone = new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=rootBone.addBone(new TwoDimensionBone("back",0, -100));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",0, -200));
		
		//create buttons2
		BoneControlRange boneControler=new BoneControlRange(rootBone);
		buttons2.add(boneControler);
		boneControler.setListener(new BoneControlListener() {
			@Override
			public void changed(TwoDimensionBone bone, int angle, int moveX, int moveY) {
				if(bone==null){
					return;
				}
				singleFrame.getBoneFrame(bone.getName()).setAngle(angle);
				updateCanvas();
			}
		});
		
		canvas = CanvasUtils.createCanvas(800, 800);
		
		CanvasUtils.disableSelection(canvas);//can avoid double click
		GWTHTMLUtils.disableContextMenu(canvas.getElement());
		GWTHTMLUtils.disableSelectionEnd(canvas.getElement());//not work
		add(canvas);
		
		canvasControler = new CanvasDragMoveControler(canvas,new CanvasMoveListener() {
			
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
					
					
					if(canvasControler.isRightMouse()){
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
		
		
		
		final SkeltalAnimations animations=new SkeltalAnimations("test", 33.3);
		
		singleFrame = BoneUtils.createEmptyAnimationFrame(rootBone);
		animations.add(singleFrame);
		
		painter = new CanvasBonePainter(canvas) {
			
			@Override
			public void paintBone(String name, String parent,int startX, int startY, int endX, int endY, double angle) {
				Rect rect=Rect.fromCenterPoint(endX,endY,10,10);
				
				String color;
				if(parent!=null){
					color="#f00";
				}else{
					color="#00f";
				}
				
				canvas.getContext2d().setFillStyle(color);//TODO method
				RectCanvasUtils.fillCircle(rect, canvas, true);
				//RectCanvasUtils.fill(rect,canvas,color);
				
				canvas.getContext2d().setStrokeStyle("#000");
				if(parent!=null){
					CanvasUtils.drawLine(canvas, startX, startY,endX,endY);
				}
				
				double[] turned=BoneUtils.turnedAngle(-10,0, angle);
				CanvasUtils.drawLine(canvas, endX, endY,endX+turned[0],endY+turned[1]);
			}
		};
		
		updateCanvas();
	}
	
	
	private AnimationFrame singleFrame;
	private CanvasBonePainter painter;
	private TwoDimensionBone rootBone;



	
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
		if(modeAnimation){
			updateCanvasOnAnimation();
		}else{
			updateCanvasOnEdit();
		}
	}

	private void updateCanvasOnEdit() {

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
		painter.paintBone(rootBone);//bone-last
	}

	private void updateCanvasOnAnimation() {
		//TODO add show bone check
		painter.paintBone(rootBone,singleFrame);
	}
}
