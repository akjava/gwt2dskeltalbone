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
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.MultiImageElementLoader;
import com.akjava.gwt.lib.client.MultiImageElementLoader.MultiImageElementListener;
import com.akjava.gwt.lib.client.StorageDataList;
import com.akjava.gwt.lib.client.datalist.SimpleTextData;
import com.akjava.gwt.lib.client.datalist.TextAreaBasedDataList;
import com.akjava.gwt.lib.client.datalist.TextAreaBasedDataList.TextAreaBasedDataListListener;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler.KeyDownState;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.lib.client.experimental.ReplaceEachOther;
import com.akjava.gwt.lib.client.experimental.undo.Command;
import com.akjava.gwt.lib.client.experimental.undo.SimpleUndoControler;
import com.akjava.gwt.lib.client.experimental.undo.UndoButtons;
import com.akjava.gwt.lib.client.experimental.undo.UndoListener;
import com.akjava.gwt.lib.client.storage.MemoryStorageControler;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.CanvasDrawingDataControler;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.SkeltalFileFormat;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationControlRange.AnimationListener;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrameCopyFunction;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange.BoneControlListener;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneWithXYAngle;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.AnimationConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneAndAnimationConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.ClipImageDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.SimpleTextDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.TextureDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.CircleLineBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.page.HasSelectionName;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.BoneControler;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.CanvasDrawingDataControlCanvas;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.CanvasDrawingDataControlCanvas.ZoomListener;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.CanvasUpdater;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageData;
import com.akjava.gwt.skeltalboneanimation.client.ui.LabeledInputRangeWidget;
import com.akjava.lib.common.graphics.IntRect;
import com.akjava.lib.common.graphics.Point;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public  class AnimationPage extends AbstractPage implements HasSelectionName,BoneFrameRangeControler,CanvasUpdater,BoneVisibleControler{
	public AnimationPage(MainManager manager) {
		super(manager);
	}
	private AnimationPageUndoControler undoControler;
	
	//private CanvasDragMoveControler canvasControler;
	//private BonePositionControler bonePositionControler;
	private BoneControlRange boneControlerRange;

	private CheckBox moveRootOnlyCheck;
	

	private void onAnimationRangeChanged(int index){
		
		for(AnimationFrame frame:animationControler.getFrame(index).asSet()){
			//currentSelectionFrame=frame;
			
			
			/*for(SkeletalAnimation animations:getSkeletalAnimation().asSet()){
			int cindex=animations.getFrames().indexOf(currentSelectionFrame);
			LogUtils.log("index:"+index+"c-index:"+cindex+"animations:"+animations.getFrames().size());
			}*/
			
			boneControlerRange.setFrame(frame);
			
			boneControler.getBonePositionControler().updateBoth(frame);
			updateCanvas();
			return;
		}
		
		LogUtils.log("onAnimationRangeChanged:out of index"+index);
		
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
		
		animationControler.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStopped() {
				setBoneVisible(true);
			}
			
			@Override
			public void onAnimationStarted() {
				setBoneVisible(false);
			}
		});
		
		animationControler.getScaleRange().addtRangeListener(new ValueChangeHandler<Number>() {

			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				if(animationControler.getSelection()==null){
					return;
				}
				double oldScale=animationControler.getSelection().getScaleX();
				double newScale=event.getValue().doubleValue();
				
				boolean isReset=animationControler.isScaleRangeEventCalledFromResetButton();//TODO really not smart,find a way
				animationControler.setScaleRangeEventCalledFromResetButton(false);
				if(oldScale==newScale){
					LogUtils.log("no need scale");
					return;
				}
				
				//LogUtils.log("scaleRange-changed");
				animationControler.getSelection().setScaleX(newScale);
				animationControler.getSelection().setScaleY(newScale);
				
				//LogUtils.log("scale-changed");
				boneControler.getBonePositionControler().updateAnimationData(animationControler.getSelection());
				updateCanvas();
				
				boolean collapse=isReset?false:true;
				
				int index=animationControler.getSelectedIndex();
				for(String boneName:boneControlerRange.getSelectedBoneName().asSet()){
					undoControler.executeBoneScaleChanged(index, boneName, oldScale, newScale,collapse);
					return;
				}
				
				LogUtils.log("no selected bone name,generate undo faild");
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
		
		moveRootOnlyCheck = new CheckBox("move only  root-bone");
		moveRootOnlyCheck.setValue(true);
		panel.add(moveRootOnlyCheck);
		
		//opacity range
		LabeledInputRangeWidget opacityRange=new LabeledInputRangeWidget("Bone Opacity", 1, 100, 1);
		opacityRange.setValue(100);
		opacityRange.getRange().setWidth("120px");
		
		panel.add(opacityRange);
		opacityRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				opacity=event.getValue().doubleValue();
				updateCanvas();
			}
		});
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
	
	private Widget createBonesColumnButtons2() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		showPrevBone = new CheckBox("show prev bone");
		showPrevBone.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateCanvas();
			}
			
		});
		panel.add(showPrevBone);
		
		showNextBone = new CheckBox("show next bone");
		showNextBone.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateCanvas();
			}
			
		});
		panel.add(showNextBone);
		
		
		Button swapHorizontalChildrens=new Button("swap with children",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSwapHorizontalBone(true);
			}
		});
		panel.add(swapHorizontalChildrens);
		
		return panel;
	}
	
	protected void doSwapHorizontalBone(boolean withChildren) {
		AnimationFrame selectionFrame=animationControler.getSelection();
		if(selectionFrame==null){
			LogUtils.log("doSwapBone:no frame selection,quit.");
			return;
		}
		
		TwoDimensionBone selectedBone=boneControlerRange.getSelection();
		if(selectedBone==null){
			LogUtils.log("doSwapBone:no bone selection,quit.");
			return;
		}
		List<TwoDimensionBone> bones=withChildren?BoneUtils.getAllBone(selectedBone):Lists.newArrayList(selectedBone);
		
		List<String> allboneName=BoneUtils.getAllBoneName(getRootBone());
		
		SkeletalAnimation animation=animationControler.getAnimation();
		int oldIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> oldFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		
		boolean needUndo=false;
		for(TwoDimensionBone bone:bones){
			String boneName=bone.getName();
			String newBoneName=new ReplaceEachOther("left", "right").replaceEachCommonCase(boneName);
			if(!boneName.equals(newBoneName) && allboneName.contains(newBoneName)){
				needUndo=true;
				LogUtils.log("swapped:"+boneName);
				BoneFrame boneFrameA=selectionFrame.getBoneFrame(boneName)!=null?selectionFrame.getBoneFrame(boneName):new BoneFrame(boneName);
				BoneFrame boneFrameB=selectionFrame.getBoneFrame(newBoneName)!=null?selectionFrame.getBoneFrame(newBoneName):new BoneFrame(newBoneName);
				
				//swapping
				boneFrameA.swapValues(boneFrameB);
				
				//inverse
				boneFrameA.setX(boneFrameA.getX()*-1);
				boneFrameA.setAngle(boneFrameA.getAngle()*-1);
				
				boneFrameB.setX(boneFrameB.getX()*-1);
				boneFrameB.setAngle(boneFrameB.getAngle()*-1);
				
				//no need
				allboneName.remove(boneName);
				allboneName.remove(newBoneName);
			}else{
				//LogUtils.log("not exist swapp name:"+newBoneName);
			}
		}
		//animationControler.setSelection(frame, true);//don't care
		
		boneControlerRange.setFrame(selectionFrame); //animationControler store each boneFrame
		
		//TODO make undo
		if(needUndo){
		animation=animationControler.getAnimation();
		int newIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> newFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		undoControler.executeBoneAnimationChanged(oldFrames, newFrames, oldIndex, newIndex);
		
		
		boneControler.getBonePositionControler().updateAnimationData(selectionFrame);//position changed;
		updateCanvas();
		}
		
		//updateCanvas();
		
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
	private BoneControler boneControler;
	protected void doLoadTexture(String name,JSZip zip) {
		
		TextureDataConverter converter=new TextureDataConverter();
		
		textureData = converter.convert(zip);
		convertedDatas=null;;
		
		updateCanvas();
	}
	
	private void setNewBoneAndAnimation(TwoDimensionBone newRoot,SkeletalAnimation animations){
		undoControler.clear();
		setRootBone(newRoot);
		
		boneSelectionOnCanvas=null;
		
		if(animations==null || animations.getFrames().size()==0){
			LogUtils.log("animation null or empty");
		}
		
		animationControler.setAnimation(animations);
		
		AnimationFrame currentSelectionFrame = animations.getFrames().get(0);
		
		animationControler.setSelection(currentSelectionFrame, true);
		
		boneControlerRange.setRootBone(newRoot);//reset
		boneControlerRange.setFrame(currentSelectionFrame);
		
		//int index=animationControler.getSelectedIndex();
		
		animationControler.syncRangeMaxAndInvalidIndex();
		
		//no need always first frame would be selected.
		//animationControler.setSelection(animations.getFrames().get(0), false);
		
		boneControler.getBonePositionControler().updateBoth(currentSelectionFrame);
		updateCanvas();
	}
	private void setNewRootBone(TwoDimensionBone newRoot) {
		SkeletalAnimation animations=new SkeletalAnimation();
		animations.add(BoneUtils.createEmptyAnimationFrame(newRoot));
		
		setNewBoneAndAnimation(newRoot,animations);
	}
	
	private void onBoneAngleRangeChanged(TwoDimensionBone bone,int angle,double x,double y){
		//LogUtils.log("onBoneAngleRangeChanged");
		if(bone==null){
			return;
		}
		boneSelectionOnCanvas=bone;
		AnimationFrame currentSelectionFrame=animationControler.getSelection();
		//LogUtils.log("update:"+bone.getName());
		BoneFrame boneFrame=currentSelectionFrame.getBoneFrame(bone.getName());
		if(boneFrame==null){
			boneFrame=new BoneFrame(bone.getName());
			currentSelectionFrame.add(boneFrame);
			LogUtils.log("new-bone-frame-created:"+bone.getName());
		}
		
		if(boneFrame.getAngle()!=angle){
			int index=animationControler.getSelectedIndex();
			String boneName=bone.getName();
			double oldAngle=boneFrame.getAngle();
			
			boneFrame.setAngle(angle);
			
			undoControler.executeBoneAngleRangeChanged(index,boneName,oldAngle,angle);
		
			
			//updated
			//LogUtils.log("modified-range:"+bone.getName()+angle);
		}
		
		if(boneFrame.getX()!=x || boneFrame.getY()!=y){
			//LogUtils.log(boneFrame+","+x+","+y);
			int index=animationControler.getSelectedIndex();
			String boneName=bone.getName();
			Point oldPoint=new Point(boneFrame.getX(),boneFrame.getY());
			
			
			
			Point newPoint=new Point(x,y);
			
			boneFrame.setX(x);
			boneFrame.setY(y);
			undoControler.executeBonePositionChanged(index, boneName, oldPoint, newPoint);
			
			
		}
		
		
		
		
		//LogUtils.log("onBoneAngleRangeChanged");
		
		boneControler.getBonePositionControler().updateAnimationData(currentSelectionFrame);
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
			public void changed(TwoDimensionBone bone, int angle, double x, double y) {
				//this called every moveing
				onBoneAngleRangeChanged(bone,angle,x,y);
				
			}
		});
		
		//when bone selection changed by manual
		boneControlerRange.getBoneListBox().addValueChangeHandler(new ValueChangeHandler<TwoDimensionBone>() {
			@Override
			public void onValueChange(ValueChangeEvent<TwoDimensionBone> event) {
				if(canvasDrawingDataControlCanvas.getCanvasControler().isTouchDowning()){
					return;
				}
				
				//only call from list-box
				doZoom();//move selection bone center
			}
			
		});
		
		Button resetAll=new Button("Reset All",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SkeletalAnimation animation=animationControler.getAnimation();
				int oldIndex=animationControler.getSelectedIndex();
				List<AnimationFrame> oldFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
				
				AnimationFrame currentSelectionFrame=animationControler.getSelection();
				currentSelectionFrame.setScaleX(1);
				currentSelectionFrame.setScaleY(1);
				for(BoneFrame frame:currentSelectionFrame.getBoneFrames().values()){
					frame.setAngle(0);
					frame.setX(0);
					frame.setY(0);
					
				}
				
				//this clear range-map
				boneControlerRange.setFrame(currentSelectionFrame);
				
				boneControler.getBonePositionControler().updateAnimationData(currentSelectionFrame);
				updateCanvas();
				
				animation=animationControler.getAnimation();
				int newIndex=animationControler.getSelectedIndex();
				List<AnimationFrame> newFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
				undoControler.executeBoneAnimationChanged(oldFrames, newFrames, oldIndex, newIndex);
				
			}
		});
		panel.add(resetAll);
		return panel;
	}
	//clear all
	protected void doCreateNewData() {
		boolean confirm=Window.confirm("clear all frame?if not saved data would all gone");
		if(!confirm){
			return;
		}
		
		SkeletalAnimation animation=animationControler.getAnimation();
		int oldIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> oldFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		
		
		//this replace animation
		setNewRootBone(getRootBone());//this clear all undo.bug
		
		animation=animationControler.getAnimation();
		int newIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> newFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		undoControler.executeBoneAnimationChanged(oldFrames, newFrames, oldIndex, newIndex);
		
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
	/**
	 * @deprecated
	 * for old file.right now no plan to support
	 * @return
	 */
	private String createAnimationSaveData(){
		BoneAndAnimationData data=new BoneAndAnimationData();
		data.setBone(getRootBone());
		data.setAnimation(animationControler.getAnimation());
		
		
		List<String> lines=new BoneAndAnimationConverter().convert(data);
		return Joiner.on("\r\n").join(lines);
	}
	private String createAnimationsSaveData(){
		String csv=new SimpleTextDataConverter().convert(dataList.getDataList().getDataList());
		return csv;
	}
	
	private String createBoneSaveData(){
		return Joiner.on("\r\n").join(new BoneConverter().convert(getRootBone()));
	}
	
	protected void doAddAfterData() {
		SkeletalAnimation animation=animationControler.getAnimation();
		int oldIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> oldFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		
		AnimationFrame frame=animationControler.getSelection();
		AnimationFrame copy=frame.copy();
		animationControler.insertAfter(copy);
		
		animationControler.syncRangeMaxAndInvalidIndex();
		animationControler.setSelection(copy,false);//update later
		animationControler.updateNameLabel();
		
		onAnimationRangeChanged(animationControler.getSelectedIndex());
		updateCanvas();
		
		int newIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> newFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		undoControler.executeBoneAnimationChanged(oldFrames, newFrames, oldIndex, newIndex);
	}	
	protected void doAddBeforeData() {
		SkeletalAnimation animation=animationControler.getAnimation();
		int oldIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> oldFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		
		AnimationFrame frame=animationControler.getSelection();
		AnimationFrame copy=frame.copy();
		animationControler.insertBefore(copy);
		animationControler.syncRangeMaxAndInvalidIndex();
		animationControler.setSelection(copy,false);//update later
		animationControler.updateNameLabel();
		
		onAnimationRangeChanged(animationControler.getSelectedIndex());
		updateCanvas();
		
		int newIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> newFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		undoControler.executeBoneAnimationChanged(oldFrames, newFrames, oldIndex, newIndex);
		
	}
	protected void doRemoveData() {
		if(animationControler.getAnimationSize()<=1){
			//at leaset one frame need do this.
			return;
		}
		
		SkeletalAnimation animation=animationControler.getAnimation();
		int oldIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> oldFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		
		
		AnimationFrame frame=animationControler.getSelection();
		animationControler.removeFrame(frame);
		
		
		onAnimationRangeChanged(animationControler.getSelectedIndex());
		
		updateCanvas();
		
		int newIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> newFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		undoControler.executeBoneAnimationChanged(oldFrames, newFrames, oldIndex, newIndex);
		
	}
	
	protected void doAddBetweenAfterData() {
		
		SkeletalAnimation animation=animationControler.getAnimation();
		
		if(animation.getFrames().size()<=1){
			doAddAfterData();//no need between
			return;
		}
		
		int oldIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> oldFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		
		int nextIndex=oldIndex+1;
		if(nextIndex>=animation.getFrames().size()){
			nextIndex=0;
		}
		
		AnimationFrame firstFrame=animationControler.getSelection();
		AnimationFrame secondFrame=animation.getFrames().get(nextIndex);
		
		AnimationFrame copy=firstFrame.createBetween(secondFrame);
		
		animationControler.insertAfter(copy);
		animationControler.syncRangeMaxAndInvalidIndex();
		animationControler.setSelection(copy,false);//update later
		animationControler.updateNameLabel();
		
		onAnimationRangeChanged(animationControler.getSelectedIndex());
		updateCanvas();
		
		int newIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> newFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		undoControler.executeBoneAnimationChanged(oldFrames, newFrames, oldIndex, newIndex);
	}	
	
	protected void doAddBetweenBeforeData() {
		SkeletalAnimation animation=animationControler.getAnimation();
		int oldIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> oldFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		
		
		int prevIndex=oldIndex-1;
		if(prevIndex<0){
			prevIndex=animation.getFrames().size()-1;
		}
		
		AnimationFrame firstFrame=animationControler.getSelection();
		AnimationFrame secondFrame=animation.getFrames().get(prevIndex);
		
		AnimationFrame copy=firstFrame.createBetween(secondFrame);
		
		
		animationControler.insertBefore(copy);
		animationControler.syncRangeMaxAndInvalidIndex();
		animationControler.setSelection(copy,false);//update later
		animationControler.updateNameLabel();
		
		onAnimationRangeChanged(animationControler.getSelectedIndex());
		updateCanvas();
		
		int newIndex=animationControler.getSelectedIndex();
		List<AnimationFrame> newFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
		undoControler.executeBoneAnimationChanged(oldFrames, newFrames, oldIndex, newIndex);
		
	}

	
	//private CircleLineBonePainter painter;

	public String getSelectionName(){
		//range always select
		return boneControlerRange.getSelection().getName();
	}
	private void createBoneControls(TwoDimensionBone rootBone,final Canvas canvas){
		
		
		boneControler =new BoneControler(canvas){
			@Override
			public String getSelectionName() {
				return AnimationPage.this.getSelectionName();
			}
		};
		
		if(rootBone!=null){
			boneControler.setBone(rootBone);
		}
	}
	

	
	
	private class AnimationPageDrawingControler implements CanvasDrawingDataControler{

		@Override
		public void onWhelled(int delta, KeyDownState keydownState) {
			if(!isEnableEdit()){
				return;
			}
			//bone angle change by wheel
			TwoDimensionBone bone=boneControlerRange.getSelection();
			if(bone==null){
				return;
			}
			
			if(bone==getRootBone()){
				//root-bone special scalling
				double value=animationControler.getScaleRange().getValue();
				double scaleValue=0.05;
				if(keydownState.isShiftKeyDown()){
					scaleValue=0.01;
				}
				if(delta>0){
					value+=scaleValue;
					if(value>animationControler.getScaleRange().getRange().getMax()){
						value=animationControler.getScaleRange().getRange().getMax();
					}
				}else{
					value-=scaleValue;
					if(value<0.01){//min is zero(because some range problem)
						value=0.01;
					}
				}
				animationControler.getScaleRange().setValue(value, true);
			}else{
				//normal angle-range adding.
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
			
			
		}

		@Override
		public void onTouchDragged(int vectorX, int vectorY, boolean rightButton, KeyDownState keydownState) {
			
			if(!isEnableEdit()){
				return;
			}
			if(boneSelectionOnCanvas!=null && !boneSelectionOnCanvas.isLocked()){
				
				
					//root bone moving
					if(boneSelectionOnCanvas==getRootBone()){//handling root-bone
						
					if (rightButton) {
						//angle
						
						// TODO create fake circle
						int angle = (int) boneControlerRange.getInputRange().getValue();

						// LogUtils.log("angle changed:"+angle);

						angle += vectorX;
						if (angle < -180) {
							angle = 360 + angle;
						} else if (angle > 180) {
							angle = angle - 360;
						}
						boneControlerRange.getInputRange().setValue(angle);
					} else {
						double scaledVectorX=vectorX/animationControler.getScaleRange().getValue();
						double scaledVectorY=vectorY/animationControler.getScaleRange().getValue();
						//move
						double x = boneControlerRange.getX();
						double y = boneControlerRange.getY();

						boneControlerRange.setPosition(x + scaledVectorX, y + scaledVectorY);
					}
					
					
					}else{
						
						//change angle
						if(rightButton || moveRootOnlyCheck.getValue()){
						//draw angles
						int mouseX=canvasDrawingDataControlCanvas.getCanvasControler().getMovedX();
						int mouseY=canvasDrawingDataControlCanvas.getCanvasControler().getMovedY();
						
						
						
						BoneWithXYAngle boneData=boneControler.getBonePositionControler().getAnimationedDataByName(boneSelectionOnCanvas.getName());
						if(boneData.getBone().getParent()==null){//on root;
							return;
						}
						
						BoneWithXYAngle parentboneData=boneControler.getBonePositionControler().getAnimationedDataByName(boneData.getBone().getParent().getName());
						
						int boneX=boneData.getX()+boneControler.getBonePositionControler().getSettings().getOffsetX();
						int boneY=boneData.getY()+boneControler.getBonePositionControler().getSettings().getOffsetY();
						
						int parentX=parentboneData.getX()+boneControler.getBonePositionControler().getSettings().getOffsetX();
						int parentY=parentboneData.getY()+boneControler.getBonePositionControler().getSettings().getOffsetY();
						
						
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
						}else{
							double scaledVectorX=vectorX/animationControler.getScaleRange().getValue();
							double scaledVectorY=vectorY/animationControler.getScaleRange().getValue();
							//change position
							String name=boneControlerRange.getSelection().getName();
							BoneWithXYAngle boneData=boneControler.getBonePositionControler().getAnimationedDataByName(name);
							
							//LogUtils.log(name+","+boneData.getAngle());
							
							double x = boneControlerRange.getX();
							double y = boneControlerRange.getY();
							
							double[] pts=BoneUtils.turnedAngle(scaledVectorX, scaledVectorY, -boneData.getAngle());
							
							double newX=(x+pts[0]);
							double newY=(y+pts[1]);
							
							boneControlerRange.setPosition(newX, newY);
						}
					}
					
				
				
				AnimationFrame currentSelectionFrame=animationControler.getSelection();
				
				boneControler.getBonePositionControler().updateAnimationData(currentSelectionFrame);//position changed;
				
				
				
				
				
				updateCanvas();
			}
		}

		@Override
		public boolean onTouchStart(int mx, int my,boolean rightButton, KeyDownState keydownState) {
			
			if(!isEnableEdit()){
				return false;
			}
			//for drag move selection
			boneSelectionOnCanvas=boneControler.getBonePositionControler().collisionAnimationedData(mx, my);
			
			//LogUtils.log(boneSelectionOnCanvas);
			if(boneSelectionOnCanvas!=null){
				boneControlerRange.setSelection(boneSelectionOnCanvas);
				return true;
				//when range value is same,not change listener called.TODO improve smart way
			}
			return false;
		}

		@Override
		public void onTouchEnd(int mx, int my, boolean rightButton,KeyDownState keydownState) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

	private boolean isEnableEdit(){
		return showBoneCheck.getValue();//TODO use boolean for spped-up
	}
	
	protected void onCanvasDragged(int vectorX, int vectorY) {
	}
	
	protected double calculateAngle(double x, double y, double x2, double y2) {
	    double radian = Math.atan2(x-x2,y-y2)*-1;
	    return radian;
	}


	
	protected void onCanvasTouchStart(int sx, int sy) {
		
	}





	protected void executeUpdateCanvas() {
		
		
		//LogUtils.log("update-canvas");
		CanvasUtils.clear(canvas);
		
		if(drawBG2 && bg2!=null){
			CanvasUtils.drawCenter(canvas, bg2);
		}
		
		//painter.paintBone(currentSelectionFrame);
		//TODO paint textures
		
		drawTextureData(canvas);
		
		drawBones();
		
		
		
		if(!showBoneCheck.getValue()){
			return;
		}
		//TODO merge into above
		if(!boneControler.getBonePositionControler().isAvaiable()){
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
	//	int mouseX=canvasDrawingDataControlCanvas.getCanvasControler().getMovedX();
		//int mouseY=canvasDrawingDataControlCanvas.getCanvasControler().getMovedY();
		
		BoneWithXYAngle boneInitialData=boneControler.getBonePositionControler().getInitialDataByName(boneSelectionOnCanvas.getName());
		BoneWithXYAngle parentInitialData=boneControler.getBonePositionControler().getInitialDataByName(boneInitialData.getBone().getParent().getName());
		
		
		BoneWithXYAngle boneData=boneControler.getBonePositionControler().getAnimationedDataByName(boneSelectionOnCanvas.getName());
		if(boneData.getBone().getParent()==null){
			//on root;
			return;
		}
		
		BoneWithXYAngle parentboneData=boneControler.getBonePositionControler().getAnimationedDataByName(boneData.getBone().getParent().getName());
		
		
		int boneInitX=boneInitialData.getX();
		int boneInitY=boneInitialData.getY();
		int parenInittX=parentInitialData.getX();
		int parentInitY=parentInitialData.getY();
		int distance=getDistance(parenInittX, parentInitY, boneInitX, boneInitY);
		
		//int boneX=boneData.getX()+bonePositionControler.getSettings().getOffsetX();
		//int boneY=boneData.getY()+bonePositionControler.getSettings().getOffsetY();
		
		int parentX=parentboneData.getX()+boneControler.getBonePositionControler().getSettings().getOffsetX();
		int parentY=parentboneData.getY()+boneControler.getBonePositionControler().getSettings().getOffsetY();
		
		//LogUtils.log(parentX+","+parentY+","+mouseX+","+mouseY);
		
		canvas.getContext2d().setStrokeStyle("#00f");
		
		//int distance=getDistance(parentX, parentY, boneX, boneY);
		IntRect rect=IntRect.fromCenterPoint(parentX, parentY, distance, distance);
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
	
	
private List<Canvas> convertedDatas;//initialized when new texture loaded.
private void initializeConvetedCanvas(){
	if(textureData==null){
		return;
	}
	if(convertedDatas==null){
	//	LogUtils.log("convertedDatas");
		convertedDatas=new ArrayList<Canvas>();
		for(ImageDrawingData data:textureData.getImageDrawingDatas()){
			convertedDatas.add(data.convertToCanvas());
		}
	
		//LogUtils.log("debug");
		for(ImageDrawingData data:textureData.getDatas()){
			//test validate image?
			//LogUtils.log(data.getConvertedCanvas().get().toDataUrl());
			
			//LogUtils.log(data.convertToCanvas().toDataUrl());
		}
	}
	
	
	
}

public class TexturePainter{
	private TextureData texture;
	private AnimationFrame frame;
	private BoneControler boneControler;
	
	public void draw(Canvas canvas){
		if(texture==null){
			LogUtils.log("TexturePainter:no texture");
			return;
		}
		if(frame==null){
			LogUtils.log("TexturePainter:no frame");
			return;
		}
	}
}

private void drawTextureData(Canvas canvas){
	if(textureData==null){
		return;
	}
	AnimationFrame currentSelectionFrame=animationControler.getSelection();
	
	double scaleX=currentSelectionFrame.getScaleX();
	double scaleY=currentSelectionFrame.getScaleY();
	//LogUtils.log("drawTextureData:"+scaleX+","+scaleY);
/*	double scaleX=1;
	double scaleY=1;*/
	
	boneControler.getBonePositionControler().updateBoth(currentSelectionFrame);//TODO update on value changed only
	//TODO add show bone check
	//TODO make class,it's hard to understand
	 List<BoneWithXYAngle> initialBonePosition=boneControler.getBonePositionControler().getRawInitialData();
	 List<BoneWithXYAngle> movedBonePosition=boneControler.getBonePositionControler().getRawAnimationedData();
	 
	
	
	//int offsetX=painter.getOffsetX();
	//int offsetY=painter.getOffsetY();
	
	int offsetX=boneControler.getBone().getBoneOffSetX();
	int offsetY=boneControler.getBone().getBoneOffSetY();
	
	int canvasCenterX=boneControler.getBonePositionControler().getSettings().getOffsetX();
	int canvasCenterY=boneControler.getBonePositionControler().getSettings().getOffsetY();
	
	initializeConvetedCanvas();
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
		
		int boneX=(int)initialBonePosition.get(boneIndex).getX();//scale effected
		int boneY=(int)initialBonePosition.get(boneIndex).getY();
		
		/**
		 * bonePosition start(0,0) however on canvas cordinate center is 0,0 
		 * so need add offset*(this is always half canvas so far)
		 */
		
		
		
		
		
		if(!data.isVisible()){
			continue;
		}
		
		
		//texture
		Canvas converted=convertedDatas.get(i);
		
		double halfConvertedImageWidth=converted.getCoordinateSpaceWidth()/2*scaleX;
		double halfConvertedImageHeighth=converted.getCoordinateSpaceHeight()/2*scaleY;
		
		//this image drawing data cordinate absolute.so like bone cordinate need sub offset*
		
		/**
		 * 
		 * ((data.getIntX()-offsetX)*scaleX = texture(data) x position is left-top corner is 0:0 ,but boneX 's 0:0 center of canvas(800x800)
		 * and this position not scale effect yet.
		 * 
		 * data.getIntX() is center of texture,to get left-top coner position need sub half-converted-image-width.
		 * 
		 * diffX is relative from bone
		 */
		
		
		double imagePointX=((boneX)-((data.getX()-offsetX)*scaleX-halfConvertedImageWidth));
		double imagePointY=((boneY)-((data.getY()-offsetY)*scaleY-halfConvertedImageHeighth));
		
		
		
		/*int imageX=(int)(data.getIntX()-converted.getCoordinateSpaceWidth()/2)-(boneX+offsetX); //
		int imageY=(int)(data.getIntY()-converted.getCoordinateSpaceHeight()/2)-(boneY+offsetY);//
*/		//LogUtils.log(imageX+","+imageY);
		
		double movedX=movedBonePosition.get(boneIndex).getX();
		double movedY=movedBonePosition.get(boneIndex).getY();
		
		
		
		//LogUtils.log(boneX+","+boneY+","+movedX+","+movedY);
		double angle=movedBonePosition.get(boneIndex).getAngle();
		
		//CanvasUtils.drawCenter(canvas, converted.getCanvasElement(), offsetX, offsetY, scaleX, scaleY, angle, 1.0)
		drawImageAt(canvas,converted.getCanvasElement(),movedX+canvasCenterX,movedY+canvasCenterY,imagePointX,imagePointY,angle,scaleX,scaleY);
		//canvas.getContext2d().drawImage(converted.getCanvasElement(), (int)(data.getX()-converted.getCoordinateSpaceWidth()/2), (int)(data.getY()-converted.getCoordinateSpaceHeight()/2));
		//
	}
	
}

double opacity=100;
private void drawBones() {
	if(!showBoneCheck.getValue()){
		return;
	}
		//switch mode
		
		
		
		canvas.getContext2d().setGlobalAlpha(opacity/100);
		AnimationFrame currentSelectionFrame=animationControler.getSelection();
		
		AnimationFrame prevFrame=null;
		for(AnimationFrame frame:animationControler.getPrev().asSet()){
			prevFrame=frame;
		}
		if(prevFrame==null){
			for(AnimationFrame frame:animationControler.getLast().asSet()){
				prevFrame=frame;
			}
		}
		
		AnimationFrame nextFrame=null;
		for(AnimationFrame frame:animationControler.getNext().asSet()){
			nextFrame=frame;
		}
		if(nextFrame==null){
			for(AnimationFrame frame:animationControler.getFirst().asSet()){
				nextFrame=frame;
			}
		}
		if(prevFrame==nextFrame && showPrevBone.getValue()){
			nextFrame=null;
		}
		
		if(currentSelectionFrame!=prevFrame && nextFrame!=null && showNextBone.getValue()){
			boneControler.getPainter().setDrawSetting(CircleLineBonePainter.nextDrawSetting);
			boneControler.paintBone(nextFrame);
		}
		
		
		if(currentSelectionFrame!=prevFrame && prevFrame!=null && showPrevBone.getValue()){
			boneControler.getPainter().setDrawSetting(CircleLineBonePainter.prevDrawSetting);
			boneControler.paintBone(prevFrame);
		}
		
		
			
		//canvas.getContext2d().setGlobalAlpha(0.5);
		
		
		
		
		
		
		boneControler.getPainter().setDrawSetting(CircleLineBonePainter.defaultDrawSetting);
		boneControler.paintBone(currentSelectionFrame);
		
		
		
		
		
		
		canvas.getContext2d().setGlobalAlpha(1);
		//canvas.getContext2d().setGlobalAlpha(1.0);
		
	}
public void drawImageAt(Canvas canvas,CanvasElement image,double canvasX,double canvasY,double imagePointX,double imagePointY,double angle,double scaleX,double scaleY){
	canvas.getContext2d().save();
	double radiant=Math.toRadians(angle);
	canvas.getContext2d().translate(canvasX,canvasY);//rotate center
	
	canvas.getContext2d().rotate(radiant);
	canvas.getContext2d().translate(-(canvasX),-(canvasY));//and back
	
	canvas.getContext2d().translate(canvasX-imagePointX,canvasY-imagePointY);	
	
	
	canvas.getContext2d().scale(scaleX,scaleY);
	canvas.getContext2d().drawImage(image, 0,0);
	canvas.getContext2d().restore();
}

	/*
	 * for drag move selection,possible null
	 */
	TwoDimensionBone boneSelectionOnCanvas;
	
	
	
	//private AnimationFrame currentSelectionFrame;//should contain all bone
	//private CanvasBoneSettings settings;
	
	public TwoDimensionBone getRootBone() {
		return boneControler.getBone();
	}
	public void setRootBone(TwoDimensionBone rootBone) {
		boneControler.setBone(rootBone);
	}


	private HorizontalPanel downloadLinks;
	
	private AnimationControlRange animationControler;
	//private List<ImageDrawingData> imageDrawingDatas;
	private CheckBox showBoneCheck;
	//private SkeletalAnimation animations;
	private CheckBox autoReplaceBoneCheck;
	private CanvasDrawingDataControlCanvas canvasDrawingDataControlCanvas;

	private TextAreaBasedDataList dataList;

	


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
		
		//for initial data
		
		 
		initializeCanvas();
		createBoneControls(null,canvas);
		
		undoControler=new AnimationPageUndoControler(this);
		
		/*
		currentSelectionFrame = BoneUtils.createEmptyAnimationFrame(getRootBone());
		animations.add(currentSelectionFrame);
		animations.add(BoneUtils.createEmptyAnimationFrame(getRootBone()));
		bonePositionControler.updateBoth(currentSelectionFrame);
		*/
		
		canvasDrawingDataControlCanvas=new CanvasDrawingDataControlCanvas(canvas,800,800,this);
		canvasDrawingDataControlCanvas.setZoomListener(new ZoomListener() {
			
			@Override
			public void onZoom(double scale) {
				doZoom();
			}
		});
		AnimationPageDrawingControler controler=new AnimationPageDrawingControler();
		//undo controled by range & buttons
		
		//controler.setUndoControler(undoControler,this);
		
		canvasDrawingDataControlCanvas.add(controler);
		
		undoControler.setUndoListener(new UndoListener() {
			
			@Override
			public void onUndo(SimpleUndoControler controler, Command command) {
				writeToDataList();
			}
			
			@Override
			public void onRedo(SimpleUndoControler controler, Command command) {
				writeToDataList();
			}
			
			@Override
			public void onExecute(SimpleUndoControler controler, Command command) {
				writeToDataList();
			}

			@Override
			public void onUpdate(SimpleUndoControler controler, Command command) {
				writeToDataList();
			}
		});
	
	}
	protected void doZoom() {
		if(canvasDrawingDataControlCanvas.getScale()==1){
			return;
		}
		String name=boneControler.getSelectionName();
		BoneWithXYAngle data=boneControler.getBonePositionControler().getAnimationedDataByName(name);
		int offsetX=boneControler.getBonePositionControler().getSettings().getOffsetX();
		int offsetY=boneControler.getBonePositionControler().getSettings().getOffsetY();
		
		//LogUtils.log("data:"+data.getX()+","+data.getY());
		canvasDrawingDataControlCanvas.scrollToPoint(data.getX()+offsetX, data.getY()+offsetY);
		
	}
	protected void writeToDataList() {
		LogUtils.log("writeToDataList");
		String text=Joiner.on("\r\n").join(new AnimationConverter().convert(animationControler.getAnimation()));
		dataList.getTextArea().setText(text);
		dataList.save();
		dataList.getSimpleDataListWidget().getSelection();
	}
	@Override
	protected Widget createCenterPanel() {
		HorizontalPanel panel0=new HorizontalPanel();
		
		Panel panel=new VerticalPanel();
		panel0.add(panel);
		
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
	    
	    upper.add(new Button("Copy",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doCopy();
			}
		}));
	    upper.add(new Button("Cut",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doCut();
			}
		}));
	    upper.add(new Button("Paste",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doPaste();
			}
		}));
	    upper.add(new Button("Between Before",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doAddBetweenBeforeData();
			}
		}));
	    
	    upper.add(new Button("Between After",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doAddBetweenAfterData();
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
	    
upper.add(new UndoButtons(undoControler)); 
	    
	   
	//animation-range need animation set dummy	
	SkeletalAnimation animations = new SkeletalAnimation("test", 33.3);
		
		panel.add(createZeroColumnButtons(animations));    
		
		panel.add(createFirstColumnButtons());
		
		panel.add(createBonesColumnButtons());
		
		panel.add(createBonesColumnButtons2());
		panel.add(createBackground2ColumnButtons());
		
	    
	panel.add(canvasDrawingDataControlCanvas);
	
	
	updateCanvas();
	
	panel0.add(createRightPanel());
	
	return panel0;
	}
	
	private Widget createRightPanel() {
		VerticalPanel panel=new VerticalPanel();
		

		MemoryStorageControler memoryStorageControler=new MemoryStorageControler();
		
		dataList = new TextAreaBasedDataList(new StorageDataList(memoryStorageControler, "gwtdatalist"),new SimpleTextData( "default", ""));
		HorizontalPanel h=new HorizontalPanel();
		panel.add(h);
		
		dataList.setTextAreaBasedDataListListener(new TextAreaBasedDataListListener() {
			
			@Override
			public void onLoad(Optional<SimpleTextData> hv) {
				loadFromDataList(hv);
			}
		});
		
		
		
		
		panel.add(dataList.getTextArea());
		
		
		h.add(dataList.getSimpleDataListWidget());
		
		
		//modify datalist
		dataList.getSimpleDataListWidget().getSaveBt().setVisible(false);
		dataList.getSimpleDataListWidget().getSaveAsBt().setVisible(false);
		dataList.getSimpleDataListWidget().getCopyBt().setVisible(false);
		dataList.getSimpleDataListWidget().getPasteBt().setVisible(false);
		dataList.getSimpleDataListWidget().getCloneBt().setVisible(true);
		dataList.getSimpleDataListWidget().getExpandButton().setVisible(false);
		dataList.getSimpleDataListWidget().getUnselectBt().setVisible(false);
		dataList.getSimpleDataListWidget().getReloadBt().setVisible(false);
		
		dataList.getSimpleDataListWidget().getBackBt().setVisible(false);
		
		dataList.getSimpleDataListWidget().mergeButton1AndButton2();
		dataList.getSimpleDataListWidget().moveClearAllToButton1();
		
		SimpleUndoControler undoControler=new SimpleUndoControler();
		dataList.setUndoControler(undoControler);
		
		return panel;
	}
	
	private void loadFromDataList(Optional<SimpleTextData> hv){
		if(hv.isPresent()){
			String dataText=hv.get().getData();
			//LogUtils.log("loadFromDataList:"+hv.get().getName());
			//LogUtils.log(dataText);
			
			
			SkeletalAnimation animation=null;
			if(dataText.isEmpty()){
				animation=new SkeletalAnimation();
				animation.add(new AnimationFrame());//test empty
			}else{
				animation=new AnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(dataText));
			}
			
			if(manager.getBoneSupplier().get()==null){
				LogUtils.log("initial bone is null skipped");
				return;
			}
			
			
			
			BoneAndAnimationData data=new BoneAndAnimationData();
			data.setBone(manager.getBoneSupplier().get());
			data.setAnimation(animation);
			//LogUtils.log("setBoneAndAnimation from datalist");
			manager.getFileManagerBar().setBoneAndAnimation(hv.get().getName(),data);
			//get annimation
		}else{
			//when delete it's happen.
			
			if(dataList.isEmpty()){
				//need at least one
				dataList.execAdd(new SimpleTextData("default", ""), false);
			}
			dataList.next();
		}
	}
	
	AnimationFrame copiedData;
	
	public Optional<AnimationFrame> getCopiedData(){
		return Optional.fromNullable(copiedData);
	}
	
	protected void doCopy() {
		if(animationControler.getSelection()==null){
			return;
		}
		copiedData=animationControler.getSelection().copy();
	}
	
	/*
	 * insert after
	 */
	protected void doPaste() {
		for(AnimationFrame clipboard:getCopiedData().asSet()){
			AnimationFrame copy=clipboard.copy();
			SkeletalAnimation animation=animationControler.getAnimation();
			int oldIndex=animationControler.getSelectedIndex();
			List<AnimationFrame> oldFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
			
			
			animationControler.insertAfter(copy);
			animationControler.syncRangeMaxAndInvalidIndex();
			animationControler.setSelection(copy,false);//update later
			animationControler.updateNameLabel();
			
			onAnimationRangeChanged(animationControler.getSelectedIndex());
			updateCanvas();
			
			int newIndex=animationControler.getSelectedIndex();
			List<AnimationFrame> newFrames=FluentIterable.from(animation.getFrames()).transform(new AnimationFrameCopyFunction()).toList();
			undoControler.executeBoneAnimationChanged(oldFrames, newFrames, oldIndex, newIndex);
		}
		
		
	}
	
	protected void doCut() {
		if(animationControler.getSelection()==null){
			return;
		}
		doCopy();
		doRemoveData();
	}
	
	
	
	protected void doLoadFile(final String name, Uint8Array array) {
		String extension=FileNames.getExtension(name).toLowerCase();
		if(extension.equals("zip")){
			//must clear data-list
			
			
			
			//load animation
			JSZip zip=JSZip.loadFromArray(array);
			
			//load old single animation & import & select.any way at least one animation must be exist
			List<SimpleTextData> animationDatas=null;
			JSFile jsAnimationsFile=zip.getFile(SkeltalFileFormat.ANIMATIONS_FILE);
			if(jsAnimationsFile!=null){
				String text=jsAnimationsFile.asText();
				animationDatas=new SimpleTextDataConverter().reverse().convert(text);
			}else{
				LogUtils.log("not contain "+SkeltalFileFormat.ANIMATIONS_FILE);
				animationDatas=Lists.newArrayList();
			}
			
			
			JSFile jsFile=zip.getFile(SkeltalFileFormat.ANIMATION_FILE);
			if(jsFile!=null && animationDatas.isEmpty()){
				LogUtils.log("exist old animation text.this would replace 0 index data");
				String text=jsFile.asText();
				BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
				SimpleTextData animationData=new SimpleTextData("default", 
						Joiner.on("\r\n").join(new AnimationConverter().convert(data.getAnimation()))
						);
				animationData.setId(0);
				animationDatas.add(animationData);
				
			}else{
				LogUtils.log("not contain "+SkeltalFileFormat.ANIMATION_FILE);
			}
			
			//parse datas.
			TextureData textureData=new TextureDataConverter().convert(zip);
			
			
			//this call background & bone
			ClipImageData clipImageData=new ClipImageDataConverter().convert(zip);
			
			
			TwoDimensionBone bone=clipImageData.getBone();
			if(bone==null){
				LogUtils.log("bone not exist in clipImageData.means not exist in zip.quit");
				return;
			}
			
			//at least one data need
			if(animationDatas.isEmpty()){
				SkeletalAnimation animation=new SkeletalAnimation();
				AnimationFrame frame=BoneUtils.createEmptyAnimationFrame(bone);
				animation.add(frame);
				SimpleTextData animationData=new SimpleTextData("default", 
						Joiner.on("\r\n").join(new AnimationConverter().convert(animation))
						);
				animationDatas.add(animationData);
			}
			
			dataList.execRestore(animationDatas);
			dataList.next();//select first
			//why???
			
			
			if(clipImageData.getBone()!=null){
				setRootBone(clipImageData.getBone());
			}
			
			//these set single bone
			manager.getFileManagerBar().setTexture(name, textureData);
			manager.getFileManagerBar().setClipImageData(name, clipImageData);
			
			
			
			//BoneAndAnimationData boneAndAnimation=new BoneAndAnimationData(bone, animation)
			//manager.getFileManagerBar().setBoneAndAnimation(name, boneAndAnimation);
			
			
			Optional<ImageElement> bg2Image=JSZipUtils.getImagheFile(zip, SkeltalFileFormat.BACKGROUND_IMAGE2);
			for(ImageElement image:bg2Image.asSet()){
				bg2=image;
				updateCanvas();
			}
			
			String removeExtension=FileNames.getRemovedExtensionName(name);
			defaultAllDataSaveName=FileNames.getRemovedLastDownloadNumbers(removeExtension);
			
			
		}else{
			LogUtils.log("load single animation.almost deprecatd so far");
			//TODO import 
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
	/**
	 * @deprecated
	 * @param name
	 * @param text
	 */
	private void setBoneAndAnimationText(String name,String text){
		BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
		manager.getFileManagerBar().setBoneAndAnimation(name, data);
	}
	
	private String defaultAllDataSaveName="2dbone-all-data";

	private CheckBox showPrevBone;
	private CheckBox showNextBone;
	
	
	private void syncClipDataAndTextureDataFileName(ClipImageData clipImageData,TextureData textureData){
		if(clipImageData==null || textureData==null){
			return;
		}
		for(ClipData data:clipImageData.getClips()){
			for(ImageDrawingData texture:data.getLinkedImageDrawingData().asSet()){
				for(ImageDrawingData linkedTexture:textureData.findDataById(texture.getId()).asSet()){
					String expected=data.getId();
					String textureId=linkedTexture.getId();
					if(!textureId.equals(expected)){
						//LogUtils.log("texture  id renamed from "+textureId+" to "+expected);
						
						linkedTexture.setId(expected);
						linkedTexture.setImageName(expected+".png");
					}
				}
			}
		}
	}
	protected void doSaveAll() {
		LogUtils.log(defaultAllDataSaveName);
		//set new-bone
		TextureData textureData=manager.getTextureDataWithNewestBone();
		ClipImageData clipData=manager.getClipImageDataWithNewestBone();
		
		syncClipDataAndTextureDataFileName(clipData,textureData);
		
		if(textureData==null ){
			Window.alert("texture data is not exist.cant save all\njust do  save animation!");
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
		
		//String animationText=createAnimationSaveData();
	//	jszip.file(SkeltalFileFormat.ANIMATION_FILE, animationText);
		
		//save data list
		String animationsText=createAnimationsSaveData();
		jszip.file(SkeltalFileFormat.ANIMATIONS_FILE, animationsText);
		
		
		//bg2
		if(bg2!=null){
			//TODO support differenct format
			JSZipUtils.createImageFile(jszip, SkeltalFileFormat.BACKGROUND_IMAGE2, bg2);
		}
		
		
		downloadLinks.add(JSZipUtils.createDownloadAnchor(jszip, defaultAllDataSaveName+".zip", "download", true));
	
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
	protected void onTextureDataChanged(final TextureData textureData) {
		//TODO create TextureControler
		this.textureData = textureData;
		convertedDatas=null;
		
		List<String> urls=Lists.newArrayList();
		if(textureData!=null){
			for(ImageDrawingData data:textureData.getDatas()){
				if(data.getImageElement()!=null){
					urls.add(data.getImageElement().getSrc());
				}
			}
		}
		
		new MultiImageElementLoader().loadImages(urls, new MultiImageElementListener() {
			
			@Override
			public void onLoad(List<String> successPaths, List<ImageElement> imageElements) {
				//LogUtils.log("image loaded");
				convertedDatas=null;//super class call updateCanvas first,need remake after loading. //TODO class
				
				
				updateCanvas();
				
			}
			
			@Override
			public void onError(List<String> errorPaths) {
				LogUtils.log("load faild:"+Joiner.on(",").join(errorPaths));
			}
		});
		
		
		
		
	}
	@Override
	public String getOwnerName() {
		// TODO Auto-generated method stub
		return "Animation-Page";
	}
	
	private Optional<SkeletalAnimation> getSkeletalAnimation(){
		return Optional.fromNullable(animationControler.getAnimation());
	}
	
	//for undo
	@Override
	public void setRangeAt(int frameIndex, String boneName, double angle) {
		//set-angle first
		for(SkeletalAnimation animations:getSkeletalAnimation().asSet()){
			animations.getFrames().get(frameIndex).getBoneFrame(boneName).setAngle(angle);
			onAnimationRangeChanged(frameIndex);
			
			for(TwoDimensionBone bone:boneControler.findBoneByBoneName(boneName).asSet()){
				boneControlerRange.setSelection(bone);
				AnimationFrame currentSelectionFrame=animationControler.getSelection();
				boneControler.getBonePositionControler().updateBoth(currentSelectionFrame);
				updateCanvas();
				return;
			}
			
			LogUtils.log("setRangeAt:can't find bone:"+boneName);
			return;
		}
		
		LogUtils.log("setRangeAt:invalid index:"+frameIndex);
	}
	@Override
	public void replaceAnimations(List<AnimationFrame> frames, int selectedIndex) {
		for(SkeletalAnimation animations:getSkeletalAnimation().asSet()){
			animations.getFrames().clear();
			
			//LogUtils.log("replaceAnimations:"+frames.size());
			//copied frame
			for(AnimationFrame frame:frames){
				animations.getFrames().add(frame);
			}
			
			//AnimationFrame frame=frames.get(selectedIndex);
			animationControler.setAnimation(animations);
			
			onAnimationRangeChanged(selectedIndex);
			//animationControler.setSelection(frame, false);
			animationControler.syncRangeMaxAndInvalidIndex();
			
			updateCanvas();
			return;
		}
		
		LogUtils.log("replaceAnimations:no animation");
	}
	@Override
	public void setPositionAt(int frameIndex, String boneName, Point position) {
		for(SkeletalAnimation animations:getSkeletalAnimation().asSet()){
			animations.getFrames().get(frameIndex).getBoneFrame(boneName).setPosition(position);
			onAnimationRangeChanged(frameIndex);
			
			for(TwoDimensionBone bone:boneControler.findBoneByBoneName(boneName).asSet()){
				boneControlerRange.setSelection(bone);
				AnimationFrame currentSelectionFrame=animationControler.getSelection();
				boneControler.getBonePositionControler().updateBoth(currentSelectionFrame);
				updateCanvas();
				return;
			}
			
			LogUtils.log("setRangeAt:can't find bone:"+boneName);
			return;
		}
	}
	@Override
	public void setScaleAt(int frameIndex, String boneName, double scale) {
		for(SkeletalAnimation animations:getSkeletalAnimation().asSet()){
			//update widget & value
			animationControler.getScaleRange().setValue(scale,false);
			
			animationControler.getSelection().setScaleX(scale);
			animationControler.getSelection().setScaleY(scale);
			
			//LogUtils.log("scale-changed");
			boneControler.getBonePositionControler().updateAnimationData(animationControler.getSelection());
			updateCanvas();
			
			
			
			return;
		}
	}
	@Override
	public void setBoneVisible(boolean visible) {
		showBoneCheck.setValue(visible,true);
	}


}
