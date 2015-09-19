package com.akjava.gwt.skeltalboneanimation.client.page;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.jszip.client.JSZipUtils;
import com.akjava.gwt.jszip.client.JSZipUtils.ZipListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.bones.AbstractBonePainter;
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
import com.akjava.gwt.skeltalboneanimation.client.converters.TextureDataConverter;
import com.akjava.lib.common.graphics.Rect;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Joiner;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/*
 * select image files and move,turn,scale it.
 */
public class SimpleAnimationEditorPage2 extends VerticalPanel implements HasSelectionName{
	
	 
	
private Canvas canvas;
private CanvasDragMoveControler canvasControler;
private BonePositionControler bonePositionControler;
private BoneControlRange boneControlerRange;

	public SimpleAnimationEditorPage2(DockLayoutPanel root){
		
		//default
		TwoDimensionBone rootBone = new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=rootBone.addBone(new TwoDimensionBone("back",-100,0));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",-100,0));
		
		
		//allbones = BoneUtils.getAllBone(rootBone);
		 SkeletalAnimation animations = new SkeletalAnimation("test", 33.3);
		 
		createCanvas();
		createBoneControls(animations,rootBone,canvas);
		
		
	
		    HorizontalPanel upper=new HorizontalPanel();
		    
		    upper.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		    
		    //load & save
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
		    downloadLinks.setWidth("80px");
		    downloadLinks.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		    upper.add(downloadLinks);
		    
		    
		    
		    upper.add(new Button("Clear All",new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					doCreateNewData();
				}
			}));

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
		    
		    
		    
		   
			currentSelectionFrame = BoneUtils.createEmptyAnimationFrame(getRootBone());
			animations.add(currentSelectionFrame);
			animations.add(BoneUtils.createEmptyAnimationFrame(getRootBone()));
			bonePositionControler.updateBoth(currentSelectionFrame);
			
		add(createZeroColumnButtons(animations));    
		add(createFirstColumnButtons());
		add(createBonesColumnButtons());
		add(createTextureColumnButtons());
		    
		    
		    
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
	private Widget createBonesColumnButtons() {
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
	
	private Widget createTextureColumnButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(new Label("[Texture Data] "));
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
	protected void doLoadTexture(String name,JSZip zip) {
		
		TextureDataConverter converter=new TextureDataConverter();
		
		textureDatas = converter.convert(zip);
		convertedDatas=null;;
		
		updateCanvas();
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
		currentSelectionFrame.getBoneFrame(bone.getName()).setAngle(angle);
		bonePositionControler.updateAnimationData(currentSelectionFrame);
		updateCanvas();
	}
	
	private Widget createFirstColumnButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		panel.add(new Label("Bone:"));
		boneControlerRange = new BoneControlRange(getRootBone());
		panel.add(boneControlerRange);
		boneControlerRange.setListener(new BoneControlListener() {
			@Override
			public void changed(TwoDimensionBone bone, int angle, int moveX, int moveY) {
				
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

	public String getSelectionName(){
		//range always select
		return boneControlerRange.getSelection().getName();
	}
	private void createBoneControls(SkeletalAnimation animations,TwoDimensionBone rootBone,final Canvas canvas){
		settings=new CanvasBoneSettings(canvas, rootBone);
		
		
		
		
		
		
		
		bonePositionControler=new BonePositionControler(settings);
		bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
		
		
		
		painter = new AbstractBonePainter(bonePositionControler) {
			
			@Override
			public void paintBone(String name, String parent,int startX, int startY, int endX, int endY, double angle) {
				int boneSize=bonePositionControler.getBoneSize();
				Rect rect=Rect.fromCenterPoint(endX,endY,boneSize/2,boneSize/2);
				
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
					rect=rect.expand(8, 8);//need expandSelf
					canvas.getContext2d().setStrokeStyle(selectionColor);
					
					RectCanvasUtils.strokeCircle(rect,canvas,true);
				}
				//
				
				canvas.getContext2d().setStrokeStyle("#000");
				
				//for bold selection line
				if(name.equals(getSelectionName())){
					canvas.getContext2d().setStrokeStyle("#0f0");
				}
				
				if(parent!=null){
					CanvasUtils.drawLine(canvas, startX, startY,endX,endY);
				}
				
				/* indicate angle but not good
				double[] turned=BoneUtils.turnedAngle(-10,0, angle);
				CanvasUtils.drawLine(canvas, endX, endY,endX+turned[0],endY+turned[1]);
				*/
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
		if(!isEnableEdit()){
			return;
		}
		//bone angle change by wheel
		TwoDimensionBone bone=boneControlerRange.getSelection();
		if(bone==null){
			return;
		}
		int value=(int) boneControlerRange.getInputRange().getValue();
		if(deltaY>0){
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


	private boolean isEnableEdit(){
		return showBoneCheck.getValue();//TODO use boolean for spped-up
	}
	
	protected void onCanvasDragged(int vectorX, int vectorY) {
		if(!isEnableEdit()){
			return;
		}
		if(boneSelectionOnCanvas!=null){
			
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
					
					
					int originalAngle=(int) Math.toDegrees(getRadian(parentX, parentY, boneX, boneY));
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
					
					int angle=(int) Math.toDegrees(getRadian(parentX, parentY, mouseX, mouseY));
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
	
	protected double getRadian(double x, double y, double x2, double y2) {
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





	private void updateCanvas() {
		//LogUtils.log("update-canvas");
		CanvasUtils.clear(canvas);
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
	if(convertedDatas==null){
		convertedDatas=new ArrayList<Canvas>();
		for(ImageDrawingData data:textureDatas){
			convertedDatas.add(data.convertToCanvas());
		}
	}
	
}
private void updateCanvasOnAnimation() {
		
		
		//switch mode
		if(textureDatas!=null){
		bonePositionControler.updateBoth(currentSelectionFrame);//TODO update on value changed only
		//TODO add show bone check
		//TODO make class,it's hard to understand
		 List<BoneWithXYAngle> emptyBonePosition=bonePositionControler.getRawInitialData();
		 List<BoneWithXYAngle> movedBonePosition=bonePositionControler.getRawAnimationedData();
		 
		
		
		//int offsetX=painter.getOffsetX();
		//int offsetY=painter.getOffsetY();
		
		int offsetX=bonePositionControler.getSettings().getOffsetX();
		int offsetY=bonePositionControler.getSettings().getOffsetY();
		
		List<ImageDrawingData> imageDrawingDatas=textureDatas;
		for(int i=0;i<imageDrawingDatas.size();i++){
			ImageDrawingData data=imageDrawingDatas.get(i);
			if(!data.isVisible()){
				continue;
			}
			LogUtils.log(data.getId()+","+data.isVisible());
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
	private List<ImageDrawingData> textureDatas;
	private CheckBox showBoneCheck;




}
