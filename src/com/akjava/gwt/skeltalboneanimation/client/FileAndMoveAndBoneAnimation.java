package com.akjava.gwt.skeltalboneanimation.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DataUrlDropVerticalRootPanel;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.AbstractBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange.BoneControlListener;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneWithXYAngle;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.lib.common.graphics.Rect;
import com.akjava.lib.common.utils.ListUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ValueListBox;


/*
 * select image files and move,turn,scale it.
 */
public class FileAndMoveAndBoneAnimation extends DataUrlDropVerticalRootPanel{

private Canvas canvas;
private CanvasDragMoveControler canvasControler;

private List<ImageDrawingData> imageDrawingDatas=new ArrayList<ImageDrawingData>();
private Map<ImageDrawingData,Integer> dataBelongintMap=new HashMap<ImageDrawingData, Integer>();

private ImageDrawingData imageDataSelection;
	
	private CheckBox showBoundsCheck;
	
	private boolean modeAnimation;
	private List<Canvas> convertedDatas=new ArrayList<Canvas>();
	private ValueListBox<TwoDimensionBone> boneListBox;
	private boolean showBone=true;
	
	
	private BonePositionControler bonePositionControler;
	private CanvasBoneSettings settings;
	public FileAndMoveAndBoneAnimation(){
		super(false);
		HorizontalPanel upButtons=new HorizontalPanel();
		add(upButtons);
		
		final HorizontalPanel buttons1=new HorizontalPanel();
		buttons1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		final HorizontalPanel buttons2=new HorizontalPanel();
		buttons2.setVisible(false);
		buttons2.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		ToggleButton animationBt=new ToggleButton("animation-mode");
		animationBt.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				modeAnimation=event.getValue();
				buttons1.setVisible(!event.getValue());	
				buttons2.setVisible(event.getValue());	
				
				if(modeAnimation){
					
					convertedDatas.clear();
					for(ImageDrawingData data:imageDrawingDatas){
						convertedDatas.add(data.convertToCanvas());
					}
				}
				
				updateCanvas();
			}
			
		});
		upButtons.add(animationBt);
		
		upButtons.add(buttons1);
		upButtons.add(buttons2);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				uploadImage(file.getFileName(),ImageElementUtils.create(text),0);
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
				if(imageDataSelection!=null){
				if(ListUtils.isTop(imageDrawingDatas, imageDataSelection)){
					ListUtils.bottom(imageDrawingDatas, imageDataSelection);
					}else{
						ListUtils.up(imageDrawingDatas, imageDataSelection);
					}
				updateCanvas();
				}
			}
		});
		buttons1.add(up);
		
		Button down=new Button("down",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(imageDataSelection!=null){
					if(ListUtils.isBottom(imageDrawingDatas, imageDataSelection)){
						ListUtils.top(imageDrawingDatas, imageDataSelection);
					}else{
						ListUtils.down(imageDrawingDatas, imageDataSelection);
					}
				
				
				updateCanvas();
				}
			}
		});
		buttons1.add(down);
		
Button unselect=new Button("unselect",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				imageDataSelection=null;
				
				updateCanvas();
			}
			
		});
		buttons1.add(unselect);
		
Button remove=new Button("remove",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(imageDataSelection!=null){
					imageDrawingDatas.remove(imageDataSelection);
				}
				imageDataSelection=null;
				
				updateCanvas();
			}
			
		});
		buttons1.add(remove);
		
		buttons1.add(new Label("belong-bone"));
		
		rootBone = new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=rootBone.addBone(new TwoDimensionBone("back",0, -100));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",0, -100));
		
		allbones = BoneUtils.getAllBone(rootBone);
		boneListBox = new ValueListBox<TwoDimensionBone>(new Renderer<TwoDimensionBone>(){

			@Override
			public String render(TwoDimensionBone object) {
				if(object==null){
					return null;
				}
				return object.getName();
			}

			@Override
			public void render(TwoDimensionBone object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		
		buttons1.add(boneListBox);
		boneListBox.addValueChangeHandler(new ValueChangeHandler<TwoDimensionBone>() {

			@Override
			public void onValueChange(ValueChangeEvent<TwoDimensionBone> event) {
				TwoDimensionBone bone=event.getValue();
				if(bone==null){
					
					return;
				}else{
					int index=allbones.indexOf(bone);
					dataBelongintMap.put(imageDataSelection, index);
				}
				
			}
		});
		
		boneListBox.setValue(rootBone);
		boneListBox.setAcceptableValues(allbones);
		
		
		boneControlerRange = new BoneControlRange(rootBone);
		buttons2.add(boneControlerRange);
		boneControlerRange.setListener(new BoneControlListener() {
			@Override
			public void changed(TwoDimensionBone bone, int angle, int moveX, int moveY) {
				if(bone==null){
					return;
				}
				LogUtils.log("update:"+bone.getName());
				singleFrame.getBoneFrame(bone.getName()).setAngle(angle);
				updateCanvas();
			}
		});
		CheckBox showBoneCheck=new CheckBox("showBone");
		buttons2.add(showBoneCheck);
		showBoneCheck.setValue(true);
		showBoneCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				showBone=event.getValue();
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
				if(modeAnimation){
					onModeAnimationDragStart(sx,sy);
				}else{
					onModeEditDragStart(sx,sy);
				}
				
			}
			
			@Override
			public void end(int sx, int sy) {//called on mouse out
				//selection=null; //need selection for zoom
			}
			
			@Override
			public void dragged(int startX, int startY, int endX, int endY, int vectorX, int vectorY) {
				if(modeAnimation){
					onModeAnimationDrag(vectorX,vectorY);
				}else{
					onModeEditDrag(vectorX,vectorY);
				}
				
				
			}
		});
		canvas.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				int v=event.getDeltaY();//-3 - 3
				if(modeAnimation){
					onModeAnimationWheel(v);
				}else{
					onModeEditWheel(v);
				}
				
			}
		});
		
		settings=new CanvasBoneSettings(canvas, rootBone);
		
		final SkeletalAnimation animations=new SkeletalAnimation("test", 33.3);
		
		singleFrame = BoneUtils.createEmptyAnimationFrame(rootBone);
		animations.add(singleFrame);
		
		bonePositionControler=new BonePositionControler(settings);
		bonePositionControler.setCollisionOrderDesc(true);
		
		painter = new AbstractBonePainter(bonePositionControler) {
			
			@Override
			public void paintBone(String name, String parent,int startX, int startY, int endX, int endY, double angle,boolean locked) {
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

			@Override
			public void startPaint() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void endPaint() {
				// TODO Auto-generated method stub
				
			}

			
		};
		
		updateCanvas();
		
		
		
	}
	
	
	protected void onModeEditWheel(int v) {
		if(imageDataSelection!=null){
			int zoom=(int) (100*imageDataSelection.getScaleX());
			zoom+=v/3*5;
			if(zoom<5){
				zoom=5;
			}
			
			imageDataSelection.setScaleX((double)zoom/100);
			imageDataSelection.setScaleY((double)zoom/100);
			imageDataSelection.updateBounds();
		}
		updateCanvas();
	}


	protected void onModeAnimationWheel(int v) {
		// TODO Auto-generated method stub
		
	}


	protected void onModeEditDrag(int vectorX, int vectorY) {
		if(imageDataSelection!=null){
			
			
			if(canvasControler.isRightMouse()){
				imageDataSelection.incrementAngle(vectorX);
			}else{
				imageDataSelection.incrementX(vectorX);
				imageDataSelection.incrementY(vectorY);
			}
			imageDataSelection.updateBounds();
			
			updateCanvas();
		}
	}


	protected void onModeAnimationDrag(int vectorX, int vectorY) {
		if(boneSelection!=null){
			
			if(canvasControler.isRightMouse() || !canvasControler.isRightMouse()){//temporaly every mouse move support
				
				int angle=(int) boneControlerRange.getInputRange().getValue();
				
				LogUtils.log("angle changed:"+angle);
				
				angle+=vectorX;
				if(angle<-180){
					angle=360+angle;
				}else if(angle>180){
					angle=angle-360;
				}
				
				boneControlerRange.getInputRange().setValue(angle);
			}else{
				
			}
			
			
			updateCanvas();
		}
		
	}

	
	TwoDimensionBone boneSelection;
	
	protected void onModeAnimationDragStart(int sx, int sy) {
		
		
		boneSelection=bonePositionControler.collisionAnimationedData(sx, sy);
		boneControlerRange.setSelection(boneSelection);
	
	}


	private AnimationFrame singleFrame;
	private AbstractBonePainter painter;
	private TwoDimensionBone rootBone;
	private List<TwoDimensionBone> allbones;
	private BoneControlRange boneControlerRange;


	private void onModeEditDragStart(int x,int y){
		imageDataSelection=null;
		if(modeAnimation){
			return;//only editmode
		}
		
		
		
		for(int i=imageDrawingDatas.size()-1;i>=0;i--){
			ImageDrawingData data=imageDrawingDatas.get(i);
		//for(ImageDrawingData data:datas){
			if(data.collision(x, y)){
				imageDataSelection=data;
				break;
			}
		}
		
		/* stop select first
		if(selection!=null){
			datas.remove(selection);
			datas.add(0, selection);
		}
		*/

		if(imageDataSelection!=null){
			int index=dataBelongintMap.get(imageDataSelection);
			boneListBox.setValue(allbones.get(index));
		}
		
		updateCanvas();
	}
	
	private void uploadImage(String name,ImageElement element,int index) {
		ImageDrawingData data=new ImageDrawingData(name,element);
		int maxObjectSize=200;
		int imgw=data.getImageElement().getWidth();
		int imgh=data.getImageElement().getHeight();
		int max=imgw>imgh?imgw:imgh;
		
		if(max>maxObjectSize){
			double ratio=(double)maxObjectSize/max;
			data.setScaleX(ratio);
			data.setScaleY(ratio);
		}
		
		//TODO limit,otherwise over canvas
		data.setX(maxObjectSize+index*10);
		data.setY(maxObjectSize+index*10);
		imageDrawingDatas.add(data);
		updateCanvas();
		
		dataBelongintMap.put(data, 0);
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

		for(int i=0;i<imageDrawingDatas.size();i++){
		//for(int i=datas.size()-1;i>=0;i--){//first item draw last,because on-mouse-select item moved to first
			ImageDrawingData data=imageDrawingDatas.get(i);
		data.draw(canvas);
		
		if(showBoundsCheck.getValue() || data==imageDataSelection){
		/*
		canvas.getContext2d().setFillStyle("#f00");
		
		for(PointXY pt:data.getCornerPoint()){
			
			if(pt!=null){
				//CanvasUtils.fillPoint(canvas, pt.getX(), pt.getY());
			}
		}
		*/
		
			String color="#0f0";
			if(data==imageDataSelection){
				color="#f00";
			}
			CanvasUtils.draw(canvas,data.getCornerPoint(),true,color);
		
			//total bounds.
			//RectCanvasUtils.stroke(data.calculateBounds(), canvas, color);
			
			}
		}
		painter.paintBone();//bone-last
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

	private void updateCanvasOnAnimation() {
		bonePositionControler.updateBoth(singleFrame);//TODO update on value changed only
		//TODO add show bone check
		//TODO make class,it's hard to understand
		 List<BoneWithXYAngle> emptyBonePosition=bonePositionControler.getRawInitialData();
		 List<BoneWithXYAngle> movedBonePosition=bonePositionControler.getRawAnimationedData();
		 
		
		
		//int offsetX=painter.getOffsetX();
		//int offsetY=painter.getOffsetY();
		
		int offsetX=bonePositionControler.getSettings().getOffsetX();
		int offsetY=bonePositionControler.getSettings().getOffsetY();
		
		for(int i=0;i<imageDrawingDatas.size();i++){
			int boneIndex=dataBelongintMap.get(imageDrawingDatas.get(i));
			
			int boneX=(int)emptyBonePosition.get(boneIndex).getX();
			int boneY=(int)emptyBonePosition.get(boneIndex).getY();
			
			int movedX=(int)movedBonePosition.get(boneIndex).getX();
			int movedY=(int)movedBonePosition.get(boneIndex).getY();
			
			
			
			//LogUtils.log(boneX+","+boneY+","+movedX+","+movedY);
			double angle=movedBonePosition.get(boneIndex).getAngle();
			
			ImageDrawingData data=imageDrawingDatas.get(i);
			Canvas converted=convertedDatas.get(i);
			
			int diffX=(boneX+offsetX)-(data.getX()-converted.getCoordinateSpaceWidth()/2);
			int diffY=(boneY+offsetY)-(data.getY()-converted.getCoordinateSpaceHeight()/2);
			
			
			int imageX=(int)(data.getX()-converted.getCoordinateSpaceWidth()/2)-(boneX+offsetX); //
			int imageY=(int)(data.getY()-converted.getCoordinateSpaceHeight()/2)-(boneY+offsetY);//
			LogUtils.log(imageX+","+imageY);
			
			drawImageAt(canvas,converted.getCanvasElement(),movedX+offsetX-diffX,movedY+offsetY-diffY,diffX,diffY,angle);
			//canvas.getContext2d().drawImage(converted.getCanvasElement(), (int)(data.getX()-converted.getCoordinateSpaceWidth()/2), (int)(data.getY()-converted.getCoordinateSpaceHeight()/2));
			//
		}
		
		if(showBone){
		canvas.getContext2d().setGlobalAlpha(0.5);
		painter.paintBone(singleFrame);
		canvas.getContext2d().setGlobalAlpha(1.0);
		}
	}



	@Override
	public void loadFile(File file, String dataUrl) {
		uploadImage(file.getFileName(),ImageElementUtils.create(dataUrl),loadFileIndex);	
	}
}
