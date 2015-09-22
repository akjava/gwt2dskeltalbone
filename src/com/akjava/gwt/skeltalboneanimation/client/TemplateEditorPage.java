package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.AbstractBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.lib.common.graphics.Rect;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


/*
 * select image files and move,turn,scale it.
 */
public class TemplateEditorPage extends VerticalPanel{
	
	 
	
private Canvas canvas;
private CanvasDragMoveControler canvasControler;
private BonePositionControler bonePositionControler;


	public TemplateEditorPage(DockLayoutPanel root){
		
		//default
		TwoDimensionBone rootBone = new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=rootBone.addBone(new TwoDimensionBone("back",0, -100));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",0, -100));
		
		
		//allbones = BoneUtils.getAllBone(rootBone);
		
		createCanvas();
		createBoneControls(rootBone,canvas);
		
		
	
		    HorizontalPanel upper=new HorizontalPanel();
		    upper.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		    upper.add(new Button("Clear All",new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					doCreateNewData();
				}
			}));

		    upper.add(new Button("Add",new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					doAddData();
				}
			}));
		    upper.add(new Button("Remove",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doRemoveData();
				}
			}));
		    upper.add(new Label("Load:"));
		    FileUploadForm load=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
				
				@Override
				public void uploaded(File file, String text) {
					doLoadData(text);
				}
			}, true);
		    load.setAccept(FileUploadForm.ACCEPT_TXT);
		    
		    upper.add(load);
		    upper.add(new Button("Save",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doSaveData();
				}
			}));
		    root.addNorth(upper, 32);
		    downloadLinks = new HorizontalPanel();
		    downloadLinks.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		    upper.add(downloadLinks);
		    /*
		//create widget
		add(createFirstColumnButtons());
		*/
		
		add(canvas);
		
		
		updateCanvas();
	}
	protected void doCreateNewData() {
		boolean confirm=Window.confirm("clear all frame?if not saved data all gone");
		if(!confirm){
			return;
		}
		//TODO
	}

	protected void doLoadData(String lines) {
		//TODO
		
	}
	protected void doSaveData() {
		//TODO
	}
	
	protected void doAddData() {
		//TODO
		
	}
	protected void doRemoveData() {
		//TODO
	}
	
	private void createCanvas(){
		//canvas
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
					}
					
					@Override
					public void dragged(int startX, int startY, int endX, int endY, int vectorX, int vectorY) {
						onCanvasDragged(vectorX,vectorY);
						
					}
				});
				canvas.addMouseWheelHandler(new MouseWheelHandler() {
					@Override
					public void onMouseWheel(MouseWheelEvent event) {
						onCanvasWheeled(event.getDeltaY());
					}
				});
	}
	
	private AbstractBonePainter painter;
	private String selectionName;
	private String getSelectionName(){
		return selectionName;
	}
	private void createBoneControls(TwoDimensionBone rootBone,final Canvas canvas){
		settings=new CanvasBoneSettings(canvas, rootBone);
		
		final SkeletalAnimation animations=new SkeletalAnimation("test", 33.3);
		
		singleFrame = BoneUtils.createEmptyAnimationFrame(getRootBone());
		animations.add(singleFrame);
		
		
		
		bonePositionControler=new BonePositionControler(settings);
		bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
		
		painter = new AbstractBonePainter(bonePositionControler) {
			
			@Override
			public void paintBone(String name, String parent,int startX, int startY, int endX, int endY, double angle,boolean locked) {
				Rect rect=Rect.fromCenterPoint(endX,endY,10,10);
				
				String color;
				if(parent!=null){
					color="#f00";
				}else{
					color="#00f";//root bone;
				}
				
				canvas.getContext2d().setFillStyle(color);//TODO method
				RectCanvasUtils.fillCircle(rect, canvas, true);
				
				//draw selection
				String selectionColor="#040";
				
				
				
				if(getSelectionName()!=null && name.equals(getSelectionName())){
					rect.expand(2, 2);
					//canvas.getContext2d().setStrokeStyle("#0f0");
					
					RectCanvasUtils.stroke(rect,canvas,selectionColor);
				}
				//
				
				canvas.getContext2d().setStrokeStyle("#000");
				if(parent!=null){
					CanvasUtils.drawLine(canvas, startX, startY,endX,endY);
				}
				
				double[] turned=BoneUtils.turnedAngle(-10,0, angle);
				CanvasUtils.drawLine(canvas, endX, endY,endX+turned[0],endY+turned[1]);
			}

			@Override
			public void startPaint() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void endPaint() {
				// TODO Auto-generated method stub
				
			}
		};
		
		

		
	}
	
	
	
	protected void onCanvasWheeled(int deltaY) {
		// TODO Auto-generated method stub
		
	}


	protected void onCanvasDragged(int vectorX, int vectorY) {
		//TODO
	}


	
	protected void onCanvasTouchStart(int sx, int sy) {
		//TODO
	}





	private void updateCanvas() {
		CanvasUtils.clear(canvas);
		painter.paintBone();
		//TODO paint textures
	}

	TwoDimensionBone boneSelection;
	
	
	
	private AnimationFrame singleFrame;
	private CanvasBoneSettings settings;
	
	public TwoDimensionBone getRootBone() {
		return settings.getBone();
	}
	public void setRootBone(TwoDimensionBone rootBone) {
		settings.setBone(rootBone);
	}


	private HorizontalPanel downloadLinks;




}
