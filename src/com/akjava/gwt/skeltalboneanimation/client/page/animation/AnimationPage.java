package com.akjava.gwt.skeltalboneanimation.client.page.animation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.Blob;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataArrayListener;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.jszip.client.JSFile;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.jszip.client.JSZipUtils;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.SkeltalFileFormat;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange.BoneControlListener;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneWithXYAngle;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneAndAnimationConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.ClipImageDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.TextureDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.CircleLineBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.page.HasSelectionName;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageData;
import com.akjava.lib.common.graphics.Rect;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public  class AnimationPage extends AbstractPage implements HasSelectionName{
	public AnimationPage(MainManager manager) {
		super(manager);
	}
	private Canvas canvas;
	private CanvasDragMoveControler canvasControler;
	private BonePositionControler bonePositionControler;
	private BoneControlRange boneControlerRange;
	

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
	private Widget createBonesColumnButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(new Label("[Bones Data] "));
		/*
		panel.add(new Label("Load:"));
		    FileUploadForm load=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
				
				@Override
				public void uploaded(File file, String text) {
					
					TwoDimensionBone newRoot=new BoneConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
					
					selectOnLoadBone(newRoot);
					
				}
			}, true);
		    load.setAccept(FileUploadForm.ACCEPT_TXT);
		panel.add(load);
		*/
		Button fromTexture=new Button("from texture-zip-bone",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(textureData==null){
					Window.alert("texture-zip not loaded yet");
					return;
				}
				
				TwoDimensionBone bone=textureData.getBone();
				if(bone==null){
					Window.alert("this zip not contain bone.txt data");
				}else{
					manager.getFileManagerBar().setBoneAndAnimation("texture-data", new BoneAndAnimationData(bone));
					//selectOnLoadBone(bone);
					//setNewRootBone(bone);
				}
			}
		});
		panel.add(fromTexture);
		
		autoReplaceBoneCheck = new CheckBox("auto replace-bone on-load");
		panel.add(autoReplaceBoneCheck);
		
		
		showBoneCheck = new CheckBox("show/edit bone");
		showBoneCheck.setValue(true);
		showBoneCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateCanvas();
			}
			
		});
		panel.add(showBoneCheck);
		
		return panel;
	}
	
	private void selectOnLoadBone(final TwoDimensionBone bone){
		checkNotNull(bone,"selectOnLoadBone:need bone");
		
		if(bone.isSameStructure(getRootBone(), true)){
			LogUtils.log("same bone.no need care");
			//usually happen when clipimage-data loaded
			return;
		}
		
		if(getRootBone()==null){//no ask
			setNewRootBone(bone);
			return;
		}
		
		if(autoReplaceBoneCheck.getValue()){//no ask
			setNewBoneAndAnimation(bone,animationControler.getAnimation());
			return;
		}
		
		//cancel
		//clear animation
		//remain animation
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("on Load Bone choice");
		HorizontalPanel p=new HorizontalPanel();
		dialogBox.add(p);
		
		
		
		Button replaceBone=new Button("replace bone",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				dialogBox.hide();
				setNewBoneAndAnimation(bone,animationControler.getAnimation());
			}
		});
		p.add(replaceBone);
		
		Button cancelBone=new Button("cancel",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				dialogBox.hide();
				
			}
		});
		p.add(cancelBone);
		
		Button clearAnimation=new Button("nwe bone & clear animation",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				dialogBox.hide();
				setNewRootBone(bone);
			}
		});
		p.add(clearAnimation);
		
		dialogBox.show();
		dialogBox.center();
		
	}
	
	private Widget createTextureColumnButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(new Label("[Texture Data] "));
		/*
		panel.add(new Label("Load:"));
		 FileUploadForm load=JSZipUtils.createZipFileUploadForm(new ZipListener() {
				
				@Override
				public void onLoad(String name, JSZip zip) {
					doLoadTexture(name,zip);
				}
				
				@Override
				public void onFaild(int states, String statesText) {
					LogUtils.log("faild:"+states+","+statesText);
				}
			});
		panel.add(load);
		*/
		
		
		
		
		return panel;
	}
	
	private String bg2Name;
	private ImageElement bg2;
	private boolean drawBG2=true;
	private Widget createBackground2ColumnButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(new Label("[Background2] "));
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				bg2=ImageElementUtils.create(text);
				bg2Name=file.getFileName();
				updateCanvas();
			}
		});
		panel.add(upload);
		final CheckBox check=new CheckBox("draw bg2");
		check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				drawBG2=event.getValue();
				updateCanvas();
			}
			
		});
		check.setValue(true);
		panel.add(check);
		return panel;
	}
	private TextureData textureData;
	protected void doLoadTexture(String name,JSZip zip) {
		
		TextureDataConverter converter=new TextureDataConverter();
		
		textureData = converter.convert(zip);
		convertedDatas=null;;
		
		updateCanvas();
	}
	
	private void setNewBoneAndAnimation(TwoDimensionBone newRoot,SkeletalAnimation animations){
		setRootBone(newRoot);
		
		boneSelectionOnCanvas=null;
		
		if(animations==null || animations.getFrames().size()==0){
			LogUtils.log("animation null or empty");
		}
		
		animationControler.setAnimation(animations);
		
		currentSelectionFrame = animations.getFrames().get(0);
		
		boneControlerRange.setRootBone(newRoot);//reset
		boneControlerRange.setFrame(currentSelectionFrame);
		
		
		animationControler.syncDatas();
		
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
		boneSelectionOnCanvas=bone;
		//LogUtils.log("update:"+bone.getName());
		BoneFrame boneFrame=currentSelectionFrame.getBoneFrame(bone.getName());
		if(boneFrame==null){
			boneFrame=new BoneFrame(bone.getName());
			currentSelectionFrame.add(boneFrame);
			LogUtils.log("new-bone-frame-created:"+bone.getName());
		}
		boneFrame.setAngle(angle);
		
		bonePositionControler.updateAnimationData(currentSelectionFrame);
		updateCanvas();
	}
	
	private Widget createFirstColumnButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(new Label("Bone:"));
		boneControlerRange = new BoneControlRange(getRootBone());
		panel.add(boneControlerRange);
		boneControlerRange.setListener(new BoneControlListener() {
			@Override
			public void changed(TwoDimensionBone bone, int angle, int moveX, int moveY) {
				
				onBoneAngleRangeChanged(bone,angle);//TODO move x,y
			}
		});
		
		Button resetAll=new Button("Reset All",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(BoneFrame frame:currentSelectionFrame.getBoneFrames().values()){
					frame.setAngle(0);
				}
				
				//this clear range-map
				boneControlerRange.setFrame(currentSelectionFrame);
				
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

	/**
	 * @deprecated
	 * @param lines
	 */
	protected void doLoadAnimationAndBoneData(String lines) {
		BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(lines));
		
		List<TwoDimensionBone> bones=BoneUtils.getAllBone(data.getBone());
		
		for(AnimationFrame frame:data.getAnimation().getFrames()){
			frame.insertEmptyFrames(bones);
		}
		
		setNewBoneAndAnimation(data.getBone(), data.getAnimation());
	}
	protected void doSaveData() {
		
		
		downloadLinks.add(HTML5Download.get().generateTextDownloadLink(createAnimationSaveData(), "2dboneanimation.txt", "download",true));
		
		
	
	}
	private String createAnimationSaveData(){
		BoneAndAnimationData data=new BoneAndAnimationData();
		data.setBone(getRootBone());
		data.setAnimation(animationControler.getAnimation());
		
		
		List<String> lines=new BoneAndAnimationConverter().convert(data);
		return Joiner.on("\r\n").join(lines);
	}
	
	private String createBoneSaveData(){
		return Joiner.on("\r\n").join(new BoneConverter().convert(getRootBone()));
	}
	
	protected void doAddAfterData() {
		AnimationFrame frame=animationControler.getSelection();
		AnimationFrame copy=frame.copy();
		animationControler.insertAfter(copy);
		animationControler.syncDatas();
		animationControler.setSelection(copy,false);//update later
		onAnimationRangeChanged(animationControler.getSelectionIndex());
		updateCanvas();
	}	
	protected void doAddBeforeData() {
		AnimationFrame frame=animationControler.getSelection();
		AnimationFrame copy=frame.copy();
		animationControler.insertBefore(copy);
		animationControler.syncDatas();
		animationControler.setSelection(copy,false);//update later
		onAnimationRangeChanged(animationControler.getSelectionIndex());
		updateCanvas();
	}
	protected void doRemoveData() {
		AnimationFrame frame=animationControler.getSelection();
		animationControler.removeFrame(frame);
		animationControler.syncDatas();
		onAnimationRangeChanged(animationControler.getSelectionIndex());
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
						onCanvasWheeled(event.getDeltaY(),event.isShiftKeyDown());;
					}
				});
	}
	
	private CircleLineBonePainter painter;

	public String getSelectionName(){
		//range always select
		return boneControlerRange.getSelection().getName();
	}
	private void createBoneControls(SkeletalAnimation animations,TwoDimensionBone rootBone,final Canvas canvas){
		settings=new CanvasBoneSettings(canvas, rootBone);
		
		
		
		
		
		
		
		bonePositionControler=new BonePositionControler(settings);
		bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
		
		
		
		painter = new CircleLineBonePainter(canvas, this, bonePositionControler);
		
		

		
	}
	
	
	

	private boolean isEnableEdit(){
		return showBoneCheck.getValue();//TODO use boolean for spped-up
	}
	
	protected void onCanvasDragged(int vectorX, int vectorY) {
		if(!isEnableEdit()){
			return;
		}
		if(boneSelectionOnCanvas!=null && !boneSelectionOnCanvas.isLocked()){
			
			if(canvasControler.isRightMouse() || !canvasControler.isRightMouse()){//temporaly every mouse move support
				
				if(boneSelectionOnCanvas==getRootBone()){//handling root-bone
				//TODO create fake circle
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
					//draw angles
					int mouseX=canvasControler.getMovedX();
					int mouseY=canvasControler.getMovedY();
					
					
					
					BoneWithXYAngle boneData=bonePositionControler.getAnimationedDataByName(boneSelectionOnCanvas.getName());
					if(boneData.getBone().getParent()==null){//on root;
						return;
					}
					
					BoneWithXYAngle parentboneData=bonePositionControler.getAnimationedDataByName(boneData.getBone().getParent().getName());
					
					int boneX=boneData.getX()+bonePositionControler.getSettings().getOffsetX();
					int boneY=boneData.getY()+bonePositionControler.getSettings().getOffsetY();
					
					int parentX=parentboneData.getX()+bonePositionControler.getSettings().getOffsetX();
					int parentY=parentboneData.getY()+bonePositionControler.getSettings().getOffsetY();
					
					
					int originalAngle=(int) Math.toDegrees(calculateAngle(parentX, parentY, boneX, boneY));
					int rangeAngle=(int) boneControlerRange.getInputRange().getValue();
					
					
					int parentAngle=originalAngle-rangeAngle;
					//int boneangle=(int) Math.toDegrees(getRadian(parentX, parentY, boneX, boneY));
					/*
					List<TwoDimensionBone> parents=BoneUtils.getParents(boneSelectionOnCanvas);
					double parentAngle=0;
					BoneWithXYAngle boneInitialData=bonePositionControler.getInitialDataByName(boneSelectionOnCanvas.getName());
					BoneWithXYAngle parentInitialData=bonePositionControler.getInitialDataByName(boneInitialData.getBone().getParent().getName());
					
					int firstAngle=(int) Math.toDegrees(getRadian(parentInitialData.getX(), parentInitialData.getY(), boneInitialData.getX(), boneInitialData.getY()));
					parentAngle=firstAngle;
					//LogUtils.log(parentboneData.getBone().getName()+" - "+boneData.getBone().getName()+" angle="+firstAngle);
					//firstAngle=(int) bonePositionControler.getAnimationedDataByName(getRootBone().getName()).getAngle();//root-angle
					
					for(int i=0;i<parents.size();i++){
						parentAngle+=(int) bonePositionControler.getAnimationedDataByName(parents.get(i).getName()).getAngle();
					}
					*/
					
					/*
					for(int i=1;i<parents.size();i++){//ignore first root
						BoneWithXYAngle data=bonePositionControler.getInitialDataByName(parents.get(i).getName());
						BoneWithXYAngle dataParent=bonePositionControler.getInitialDataByName(parents.get(i).getParent().getName());
						int angle=(int) Math.toDegrees(getRadian(dataParent.getX(), dataParent.getY(), data.getX(), data.getY()));
						parentAngle+=angle;
						LogUtils.log(data.getBone().getName()+" - "+dataParent.getBone().getName()+" angle="+angle+",parent="+parentAngle);
					}
					*/
					
					int angle=(int) Math.toDegrees(calculateAngle(parentX, parentY, mouseX, mouseY));
					/*
					canvas.getContext2d().setStrokeStyle("#0f0");
					CanvasUtils.drawLine(canvas, parentX, parentY, mouseX, mouseY);
					LogUtils.log("parent-mouse:"+angle+",bone-data"+boneData.getAngle()+",parent-bone"+boneangle);
					*/
					
					
					
					int newAngle=(int) (angle-parentAngle)%360;
					
					//LogUtils.log(newAngle);
					
					if(newAngle<-180){
						newAngle+=360;
					}else if(newAngle>180){
						newAngle-=360;
					}
					
					boneControlerRange.getInputRange().setValue(newAngle);
				}
				
			}else{
				
			}
			
			bonePositionControler.updateAnimationData(currentSelectionFrame);//position changed;
			
			
			
			
			
			updateCanvas();
		}
	}
	
	protected double calculateAngle(double x, double y, double x2, double y2) {
	    double radian = Math.atan2(x-x2,y-y2)*-1;
	    return radian;
	}


	
	protected void onCanvasTouchStart(int sx, int sy) {
		if(!isEnableEdit()){
			return;
		}
		//for drag move selection
		boneSelectionOnCanvas=bonePositionControler.collisionAnimationedData(sx, sy);
		
		//LogUtils.log(boneSelectionOnCanvas);
		if(boneSelectionOnCanvas!=null){
			boneControlerRange.setSelection(boneSelectionOnCanvas);
			//when range value is same,not change listener called.TODO improve smart way
		}
	}





	protected void updateCanvas() {
		
		
		//LogUtils.log("update-canvas");
		CanvasUtils.clear(canvas);
		
		if(drawBG2 && bg2!=null){
			CanvasUtils.drawCenter(canvas, bg2);
		}
		
		//painter.paintBone(currentSelectionFrame);
		//TODO paint textures
		
		updateCanvasOnAnimation();
		if(!showBoneCheck.getValue()){
			return;
		}
		//TODO merge into above
		if(!bonePositionControler.isAvaiable()){
			return;
		}
		
		if(boneSelectionOnCanvas==null || boneSelectionOnCanvas==getRootBone()){
			return;
		}
		
		//no neeed to turn;
		if(boneSelectionOnCanvas.isLocked()){
			return;
		}
		
		//draw angles
		int mouseX=canvasControler.getMovedX();
		int mouseY=canvasControler.getMovedY();
		
		BoneWithXYAngle boneInitialData=bonePositionControler.getInitialDataByName(boneSelectionOnCanvas.getName());
		BoneWithXYAngle parentInitialData=bonePositionControler.getInitialDataByName(boneInitialData.getBone().getParent().getName());
		
		
		BoneWithXYAngle boneData=bonePositionControler.getAnimationedDataByName(boneSelectionOnCanvas.getName());
		if(boneData.getBone().getParent()==null){
			//on root;
			return;
		}
		
		BoneWithXYAngle parentboneData=bonePositionControler.getAnimationedDataByName(boneData.getBone().getParent().getName());
		
		
		int boneInitX=boneInitialData.getX();
		int boneInitY=boneInitialData.getY();
		int parenInittX=parentInitialData.getX();
		int parentInitY=parentInitialData.getY();
		int distance=getDistance(parenInittX, parentInitY, boneInitX, boneInitY);
		
		//int boneX=boneData.getX()+bonePositionControler.getSettings().getOffsetX();
		//int boneY=boneData.getY()+bonePositionControler.getSettings().getOffsetY();
		
		int parentX=parentboneData.getX()+bonePositionControler.getSettings().getOffsetX();
		int parentY=parentboneData.getY()+bonePositionControler.getSettings().getOffsetY();
		
		//LogUtils.log(parentX+","+parentY+","+mouseX+","+mouseY);
		
		canvas.getContext2d().setStrokeStyle("#00f");
		
		//int distance=getDistance(parentX, parentY, boneX, boneY);
		Rect rect=Rect.fromCenterPoint(parentX, parentY, distance, distance);
		RectCanvasUtils.strokeCircle(rect, canvas, true);
		
		/*
		int boneangle=(int) Math.toDegrees(getRadian(parentX, parentY, boneX, boneY));
		int angle=(int) Math.toDegrees(getRadian(parentX, parentY, mouseX, mouseY));
		canvas.getContext2d().setStrokeStyle("#0f0");
		CanvasUtils.drawLine(canvas, parentX, parentY, mouseX, mouseY);
		LogUtils.log("parent-mouse:"+angle+",bone-data"+boneData.getAngle()+",parent-bone"+boneangle);
		*/
	}
	
	protected int getDistance(double x, double y, double x2, double y2) {
	    double distance = Math.sqrt((x2 - x) * (x2 - x) + (y2 - y) * (y2 - y));

	    return (int) distance;
	}
	
	
	private int findIndex(List<BoneWithXYAngle> list,String name){
		for(int i=0;i<list.size();i++){
			BoneWithXYAngle data=list.get(i);
			if(data.getBone().getName().equals(name)){
				return i;
			}
		}
		return -1;
	}
	
	
private List<Canvas> convertedDatas;
private void initializeConvetedCanvas(){
	if(textureData==null){
		return;
	}
	if(convertedDatas==null){
		convertedDatas=new ArrayList<Canvas>();
		for(ImageDrawingData data:textureData.getImageDrawingDatas()){
			convertedDatas.add(data.convertToCanvas());
		}
	}
	
}

private void drawTextureData(Canvas canvas){

	bonePositionControler.updateBoth(currentSelectionFrame);//TODO update on value changed only
	//TODO add show bone check
	//TODO make class,it's hard to understand
	 List<BoneWithXYAngle> emptyBonePosition=bonePositionControler.getRawInitialData();
	 List<BoneWithXYAngle> movedBonePosition=bonePositionControler.getRawAnimationedData();
	 
	
	
	//int offsetX=painter.getOffsetX();
	//int offsetY=painter.getOffsetY();
	
	int offsetX=bonePositionControler.getSettings().getOffsetX();
	int offsetY=bonePositionControler.getSettings().getOffsetY();
	
	List<ImageDrawingData> imageDrawingDatas=textureData.getImageDrawingDatas();
	for(int i=0;i<imageDrawingDatas.size();i++){
		ImageDrawingData data=imageDrawingDatas.get(i);
		if(!data.isVisible()){
			continue;
		}
		
		String boneName=data.getBoneName();
		
	
		
		int boneIndex=findIndex(movedBonePosition,boneName);
		
		if(boneIndex==-1){
			//noindex
			continue;
		}
		
		int boneX=(int)emptyBonePosition.get(boneIndex).getX();
		int boneY=(int)emptyBonePosition.get(boneIndex).getY();
		
		int movedX=(int)movedBonePosition.get(boneIndex).getX();
		int movedY=(int)movedBonePosition.get(boneIndex).getY();
		
		
		
		//LogUtils.log(boneX+","+boneY+","+movedX+","+movedY);
		double angle=movedBonePosition.get(boneIndex).getAngle();
		
		
		if(!data.isVisible()){
			continue;
		}
		initializeConvetedCanvas();
		
		Canvas converted=convertedDatas.get(i);
		
		int diffX=(boneX+offsetX)-(data.getX()-converted.getCoordinateSpaceWidth()/2);
		int diffY=(boneY+offsetY)-(data.getY()-converted.getCoordinateSpaceHeight()/2);
		
		
		int imageX=(int)(data.getX()-converted.getCoordinateSpaceWidth()/2)-(boneX+offsetX); //
		int imageY=(int)(data.getY()-converted.getCoordinateSpaceHeight()/2)-(boneY+offsetY);//
		//LogUtils.log(imageX+","+imageY);
		
		drawImageAt(canvas,converted.getCanvasElement(),movedX+offsetX-diffX,movedY+offsetY-diffY,diffX,diffY,angle);
		//canvas.getContext2d().drawImage(converted.getCanvasElement(), (int)(data.getX()-converted.getCoordinateSpaceWidth()/2), (int)(data.getY()-converted.getCoordinateSpaceHeight()/2));
		//
	}
	
}

private void updateCanvasOnAnimation() {
		
		
		//switch mode
		if(textureData!=null){
			drawTextureData(canvas);
		}
		
		if(showBoneCheck.getValue()){
		//canvas.getContext2d().setGlobalAlpha(0.5);
		painter.paintBone(currentSelectionFrame);
		//canvas.getContext2d().setGlobalAlpha(1.0);
		}
	}
public void drawImageAt(Canvas canvas,CanvasElement image,int canvasX,int canvasY,int imageX,int imageY,double angle){
	canvas.getContext2d().save();
	double radiant=Math.toRadians(angle);
	canvas.getContext2d().translate(canvasX+imageX,canvasY+imageY);//rotate center
	
	canvas.getContext2d().rotate(radiant);
	canvas.getContext2d().translate(-(canvasX+imageX),-(canvasY+imageY));//and back
	
	canvas.getContext2d().translate(canvasX,canvasY);	
	
	canvas.getContext2d().drawImage(image, 0,0);
	canvas.getContext2d().restore();
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
	//private List<ImageDrawingData> imageDrawingDatas;
	private CheckBox showBoneCheck;
	private SkeletalAnimation animations;
	private CheckBox autoReplaceBoneCheck;


	@Override
	protected void onCanvasTouchEnd(int sx, int sy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onCanvasWheeled(int delta, boolean shiftDown) {
		if(!isEnableEdit()){
			return;
		}
		//bone angle change by wheel
		TwoDimensionBone bone=boneControlerRange.getSelection();
		if(bone==null){
			return;
		}
		int value=(int) boneControlerRange.getInputRange().getValue();
		if(delta>0){
			value++;
		}else{
			value--;
		}
		if(value>180){
			value=value-360;
		}
		if(value<-180){
			value=360+value;
		}
		boneControlerRange.getInputRange().setValue(value, true);
	}
	@Override
	protected void onBoneAndAnimationChanged(BoneAndAnimationData data) {
		
		if(data.isAnimationNullOrEmpty()){
			//bone only data usually modify bone by other page
			selectOnLoadBone(data.getBone());
		}else{
			//LogUtils.log(data.getAnimation().getFrames().size());//some case return 0?
			//usually by self(load or save)
			setNewBoneAndAnimation(data.getBone(), data.getAnimation());
		}
		
	}
	@Override
	protected void onBackgroundChanged(ImageDrawingData background) {
		// TODO support later
		
	}
	@Override
	protected void initialize() {

		
		//default
		/*
		TwoDimensionBone rootBone = new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=rootBone.addBone(new TwoDimensionBone("back",-100,0));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",-100,0));
		*/
		
		animations = new SkeletalAnimation("test", 33.3);
		 
		createCanvas();
		createBoneControls(animations,null,canvas);
		
		
		
		/*
		currentSelectionFrame = BoneUtils.createEmptyAnimationFrame(getRootBone());
		animations.add(currentSelectionFrame);
		animations.add(BoneUtils.createEmptyAnimationFrame(getRootBone()));
		bonePositionControler.updateBoth(currentSelectionFrame);
		*/
	
	}
	@Override
	protected Widget createCenterPanel() {
		Panel panel=new VerticalPanel();
	    HorizontalPanel upper=new HorizontalPanel();
	    
	    upper.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    
	    //load & save
	    upper.add(new Label("Animation-Load:"));
	    /*
	    FileUploadForm load=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				doLoadAnimationAndBoneData(text);
			}
		}, true);
	    load.setAccept(FileUploadForm.ACCEPT_TXT);
	    */
	    
	    
	    
	    FileUploadForm load2=FileUtils.createSingleFileUploadForm(new DataArrayListener() {
			@Override
			public void uploaded(final File file, Uint8Array array) {
				doLoadFile(file.getFileName(),array);
			}
		});
	    upper.add(load2);
	    load2.setAccept(FileUploadForm.ACCEPT_TXT,FileUploadForm.ACCEPT_ZIP);
	    
	    
	   
	    upper.add(new Button("Save animation",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSaveData();
			}
		}));
	    /*
	    upper.add(new Button("Save All",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSaveAll();
			}
		}));
		*/
	    
	    Button doSaveallButton=new ExecuteButton("Save All"){
			
			 @Override
				public void beforeExecute() {
				 downloadLinks.clear();
			 }
			 
			@Override
			public void executeOnClick() {
				doSaveAll();
			}
			 
		 };
	    upper.add(doSaveallButton);
	    
	    
	    
	    panel.add(upper);
	    downloadLinks = new HorizontalPanel();
	    downloadLinks.setWidth("80px");
	    downloadLinks.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    upper.add(downloadLinks);
	    
	    
	    
	   

	    upper.add(new Button("Insert Before",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doAddBeforeData();
			}
		}));
	    upper.add(new Button("Insert After",new ClickHandler() {
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
	    
	    upper.add(new Button("Clear All",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doCreateNewData();
			}
		}));
	    
Button extractImageBt=new Button("Draw Frame",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
				
				if(drawBG2 && bg2!=null){
					CanvasUtils.drawCenter(extractCanvas, bg2);
				}
				
				drawTextureData(extractCanvas);
				//background.getBackgroundData().draw(extractCanvas);
				String dataUrl=extractCanvas.toDataUrl();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-frame.png", "background", true);
				downloadLinks.add(a);
				
			}
		});
upper.add(extractImageBt);
	    
	    
	   
		
		
		panel.add(createZeroColumnButtons(animations));    
		
		panel.add(createFirstColumnButtons());
		
		panel.add(createBonesColumnButtons());
		
		panel.add(createTextureColumnButtons());
		panel.add(createBackground2ColumnButtons());
		
	    
	panel.add(canvas);
	
	
	updateCanvas();
	
	return panel;
	}
	
	protected void doLoadFile(final String name, Uint8Array array) {
		String extension=FileNames.getExtension(name).toLowerCase();
		if(extension.equals("zip")){
			
			//load animation
			JSZip zip=JSZip.loadFromArray(array);
			JSFile jsFile=zip.getFile(SkeltalFileFormat.ANIMATION_FILE);
			if(jsFile!=null){
				String text=jsFile.asText();
				setBoneAndAnimationText(name,text);
			}else{
				Window.alert("not contain "+SkeltalFileFormat.ANIMATION_FILE);
			}
			
			//parse datas.
			TextureData textureData=new TextureDataConverter().convert(zip);
			manager.getFileManagerBar().setTexture(name, textureData);
			
			//this call background & bone
			ClipImageData clipImageData=new ClipImageDataConverter().convert(zip);
			manager.getFileManagerBar().setClipImageData(name, clipImageData);
			
			Optional<ImageElement> bg2Image=JSZipUtils.getImagheFile(zip, SkeltalFileFormat.BACKGROUND_IMAGE2);
			for(ImageElement image:bg2Image.asSet()){
				bg2=image;
				updateCanvas();
			}
			
			
		}else{
			Blob blob=Blob.createBlob(array, "plain/text");
			final FileReader reader=FileReader.createFileReader();
			reader.setOnLoad(new FileHandler() {
				
				@Override
				public void onLoad() {
					String text=reader.getResultAsString();
					setBoneAndAnimationText(name,text);
				}
			});
			reader.readAsText((File)blob.cast(), "uff8");
		}
	}
	private void setBoneAndAnimationText(String name,String text){
		BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
		manager.getFileManagerBar().setBoneAndAnimation(name, data);
	}
	
	protected void doSaveAll() {
		
		//set new-bone
		TextureData textureData=manager.getTextureData();
		ClipImageData clipData=manager.getUploadedFileManager().getClipImageData();
		
		if(textureData==null ){
			Window.alert("texture data is not exist.cant save all\njust do try save animation!");
			return;
		}
		
		//save bone & animation & texture & clip
		Stopwatch watch=Stopwatch.createStarted();
		
		TextureDataConverter converter=new TextureDataConverter();
		JSZip jszip=converter.reverse().convert(textureData);
		
		if(clipData!=null){
		//addition clip-data
			new ClipImageDataConverter().convertToJsZip(jszip, clipData);
		}else{
			Window.alert("clipImageData not exist!this contain only texture and animation");
		}
		//add animation. bone is created by textureData
		
		String animationText=createAnimationSaveData();
		jszip.file(SkeltalFileFormat.ANIMATION_FILE, animationText);
		
		//bg2
		if(bg2!=null){
			//TODO support differenct format
			JSZipUtils.createImageFile(jszip, SkeltalFileFormat.BACKGROUND_IMAGE2, bg2);
		}
		
		
		downloadLinks.add(JSZipUtils.createDownloadAnchor(jszip, "2dbone-all-data.zip", "download", true));
	
		double time=watch.elapsed(TimeUnit.MILLISECONDS);
		LogUtils.log("clip-zip generation-millisecond "+time+" ms");
		
	}
	@Override
	protected Widget createWestPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void updateDatas() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onTextureDataChanged(TextureData textureData) {
		//TODO create TextureControler
		this.textureData = textureData;
		convertedDatas=null;
		
		updateCanvas();
	}
	@Override
	public String getOwnerName() {
		// TODO Auto-generated method stub
		return "Animation-Page";
	}


}
