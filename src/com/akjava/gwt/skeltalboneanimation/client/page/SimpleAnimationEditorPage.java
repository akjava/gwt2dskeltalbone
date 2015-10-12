package com.akjava.gwt.skeltalboneanimation.client.page;

import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.AbstractBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange.BoneControlListener;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneAndAnimationConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneConverter;
import com.akjava.lib.common.graphics.IntRect;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Joiner;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/*
 * select image files and move,turn,scale it.
 */
/**
 * @deprecated
 * @author aki
 *
 */
public class SimpleAnimationEditorPage extends VerticalPanel{
	
	 
	
private Canvas canvas;
private CanvasDragMoveControler canvasControler;
private BonePositionControler bonePositionControler;
private BoneControlRange boneControlerRange;

	public SimpleAnimationEditorPage(DockLayoutPanel root){
		
		//default
		TwoDimensionBone rootBone = new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=rootBone.addBone(new TwoDimensionBone("back",0, -100));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",0, -100));
		
		
		//allbones = BoneUtils.getAllBone(rootBone);
		 SkeletalAnimation animations = new SkeletalAnimation("test", 33.3);
		 
		createCanvas();
		createBoneControls(animations,rootBone,canvas);
		
		
	
		    HorizontalPanel upper=new HorizontalPanel();
		    upper.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		    upper.add(new Button("Clear All",new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					doCreateNewData();
				}
			}));

		    upper.add(new Button("Add Before",new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					doAddBeforeData();
				}
			}));
		    upper.add(new Button("Add After",new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					doAddAfterData();
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
		    
		    
		   
			currentSelectionFrame = BoneUtils.createEmptyAnimationFrame(getRootBone());
			animations.add(currentSelectionFrame);
			animations.add(BoneUtils.createEmptyAnimationFrame(getRootBone()));
			bonePositionControler.updateBoth(currentSelectionFrame);
			
		add(createZeroColumnButtons(animations));    
		add(createBoneEditColumnButtons());
		add(createBoneLoadColumnButtons());
		
		    
		    
		    
		add(canvas);
		
		
		updateCanvas();
	}
	private void onAnimationRangeChanged(int index){
		currentSelectionFrame=animationControler.getSelection();
		
		boneControlerRange.setFrame(currentSelectionFrame);
		
		bonePositionControler.updateBoth(currentSelectionFrame);
		updateCanvas();
	}
	private Widget createZeroColumnButtons(SkeletalAnimation animations) {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		animationControler = new AnimationControlRange(animations);
		panel.add(animationControler);
		animationControler.getInputRange().addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				onAnimationRangeChanged(event.getValue().intValue()-1);
			}
		});
		
		return panel;
	}
	private Widget createBoneLoadColumnButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(new Label("[Bones Data] "));
		panel.add(new Label("Load:"));
		    FileUploadForm load=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
				
				@Override
				public void uploaded(File file, String text) {
					TwoDimensionBone newRoot=new BoneConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
					boolean confirm=Window.confirm("load new bone.clear all exist animation.\nare you sure?");
					if(!confirm){
						return;
					}
					setNewRootBone(newRoot);
				}
			}, true);
		    load.setAccept(FileUploadForm.ACCEPT_TXT);
		panel.add(load);
		
		
		return panel;
	}
	
	private void setNewBoneAndAnimation(TwoDimensionBone newRoot,SkeletalAnimation animations){
		setRootBone(newRoot);
		
		if(animations==null || animations.getFrames().size()==0){
			LogUtils.log("animation null or empty");
		}
		
		animationControler.setAnimation(animations);
		
		currentSelectionFrame = animations.getFrames().get(0);
		
		boneControlerRange.setRootBone(newRoot);//reset
		boneControlerRange.setFrame(currentSelectionFrame);
		
		
		animationControler.syncRangeMaxAndInvalidIndex();
		
		//no need always first frame would be selected.
		//animationControler.setSelection(animations.getFrames().get(0), false);
		
		bonePositionControler.updateBoth(currentSelectionFrame);
		updateCanvas();
	}
	private void setNewRootBone(TwoDimensionBone newRoot) {
		SkeletalAnimation animations=new SkeletalAnimation();
		animations.add(BoneUtils.createEmptyAnimationFrame(newRoot));
		
		setNewBoneAndAnimation(newRoot,animations);
	}
	
	private void onBoneAngleRangeChanged(TwoDimensionBone bone,int angle){
		if(bone==null){
			return;
		}
		//LogUtils.log("update:"+bone.getName());
		currentSelectionFrame.getBoneFrame(bone.getName()).setAngle(angle);
		bonePositionControler.updateAnimationData(currentSelectionFrame);
		updateCanvas();
	}
	
	private Widget createBoneEditColumnButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		panel.add(new Label("Bone:"));
		boneControlerRange = new BoneControlRange(getRootBone());
		panel.add(boneControlerRange);
		boneControlerRange.setListener(new BoneControlListener() {
			@Override
			public void changed(TwoDimensionBone bone, int angle, double moveX, double moveY) {
				onBoneAngleRangeChanged(bone,angle);
			}
		});
		
		Button resetAll=new Button("Reset All",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(BoneFrame frame:currentSelectionFrame.getBoneFrames().values()){
					frame.setAngle(0);
				}
				boneControlerRange.getInputRange().setValue(0);
				
				bonePositionControler.updateAnimationData(currentSelectionFrame);
				updateCanvas();
			}
		});
		panel.add(resetAll);
		return panel;
	}
	protected void doCreateNewData() {
		boolean confirm=Window.confirm("clear all frame?if not saved data would all gone");
		if(!confirm){
			return;
		}
		setNewRootBone(getRootBone());
	}

	protected void doLoadData(String lines) {
		BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(lines));
		
		List<TwoDimensionBone> bones=BoneUtils.getAllBone(data.getBone());
		
		for(AnimationFrame frame:data.getAnimation().getFrames()){
			frame.insertEmptyFrames(bones);
		}
		
		setNewBoneAndAnimation(data.getBone(), data.getAnimation());
	}
	protected void doSaveData() {
		
		BoneAndAnimationData data=new BoneAndAnimationData();
		data.setBone(getRootBone());
		data.setAnimation(animationControler.getAnimation());
		
		
		List<String> lines=new BoneAndAnimationConverter().convert(data);
		downloadLinks.add(HTML5Download.get().generateTextDownloadLink(Joiner.on("\r\n").join(lines), "2dboneanimation.txt", "download",true));
		
		
	
	}
	protected void doAddAfterData() {
		AnimationFrame frame=animationControler.getSelection();
		AnimationFrame copy=frame.copy();
		animationControler.insertAfter(copy);
		animationControler.syncRangeMaxAndInvalidIndex();
		animationControler.setSelection(copy,false);//update later
		onAnimationRangeChanged(animationControler.getSelectedIndex());
		updateCanvas();
	}	
	protected void doAddBeforeData() {
		AnimationFrame frame=animationControler.getSelection();
		AnimationFrame copy=frame.copy();
		animationControler.insertBefore(copy);
		animationControler.syncRangeMaxAndInvalidIndex();
		animationControler.setSelection(copy,false);//update later
		onAnimationRangeChanged(animationControler.getSelectedIndex());
		updateCanvas();
	}
	protected void doRemoveData() {
		AnimationFrame frame=animationControler.getSelection();
		animationControler.removeFrame(frame);
		animationControler.syncRangeMaxAndInvalidIndex();
		onAnimationRangeChanged(animationControler.getSelectedIndex());
		updateCanvas();
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

	private String getSelectionName(){
		//range always select
		return boneControlerRange.getSelection().getName();
	}
	private void createBoneControls(SkeletalAnimation animations,TwoDimensionBone rootBone,final Canvas canvas){
		settings=new CanvasBoneSettings(canvas, rootBone);
		
		
		
		
		
		
		
		bonePositionControler=new BonePositionControler(settings);
		bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
		
		
		
		painter = new AbstractBonePainter(bonePositionControler) {
			
			@Override
			public void paintBone(String name, String parent,int startX, int startY, int endX, int endY, double angle,boolean locked) {
				IntRect rect=IntRect.fromCenterPoint(endX,endY,10,10);
				
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
		if(boneSelectionOnCanvas!=null){
			
			if(canvasControler.isRightMouse() || !canvasControler.isRightMouse()){//temporaly every mouse move support
				
				int angle=(int) boneControlerRange.getInputRange().getValue();
				
				//LogUtils.log("angle changed:"+angle);
				
				angle+=vectorX;
				if(angle<-180){
					angle=360+angle;
				}else if(angle>180){
					angle=angle-360;
				}
				
				boneControlerRange.getInputRange().setValue(angle);
			}else{
				
			}
			
			bonePositionControler.updateAnimationData(currentSelectionFrame);//position changed;
			updateCanvas();
		}
	}


	
	protected void onCanvasTouchStart(int sx, int sy) {
		//for drag move selection
		boneSelectionOnCanvas=bonePositionControler.collisionAnimationedData(sx, sy);
		
		//LogUtils.log(boneSelectionOnCanvas);
		if(boneSelectionOnCanvas!=null){
			boneControlerRange.setSelection(boneSelectionOnCanvas);
			updateCanvas();//if range value same never called
		}
	}





	private void updateCanvas() {
		CanvasUtils.clear(canvas);
		painter.paintBone(currentSelectionFrame);
		//TODO paint textures
		
		
	}
	
	

	/*
	 * for drag move selection,possible null
	 */
	TwoDimensionBone boneSelectionOnCanvas;
	
	
	
	private AnimationFrame currentSelectionFrame;//should contain all bone
	private CanvasBoneSettings settings;
	
	public TwoDimensionBone getRootBone() {
		return settings.getBone();
	}
	public void setRootBone(TwoDimensionBone rootBone) {
		settings.setBone(rootBone);
	}


	private HorizontalPanel downloadLinks;
	
	private AnimationControlRange animationControler;




}
