package com.akjava.gwt.skeltalboneanimation.client.page.texture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.jszip.client.JSZipUtils;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.experimental.ReplaceEachOther;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.skeltalboneanimation.client.Background;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneWithXYAngle;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneAndAnimationConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.TextureDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.HasSelectionName;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataChangeListener;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataOwner;
import com.akjava.gwt.skeltalboneanimation.client.page.SimpleBoneEditorPage.FlushTextBox;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.BoneControler;
import com.akjava.gwt.skeltalboneanimation.client.ui.LabeledInputRangeWidget;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TexturePage extends AbstractPage implements HasSelectionName{
	
private Canvas canvas;
private CanvasDragMoveControler canvasControler;
private BonePositionControler bonePositionControler;

interface Driver extends SimpleBeanEditorDriver< ImageDrawingData,  ImageDrawingDataEditor> {}
Driver driver;
private BoneControler boneControler;




public class ImageDrawingDataEditor extends VerticalPanel implements Editor<ImageDrawingData>,ValueAwareEditor<ImageDrawingData>{
private ImageDrawingData value;
private ValueListBox<String> boneNameList;
private CheckBox flipHorizontalCheck;
private CheckBox flipVerticalCheck;
private CheckBox visibleCheck;
private Label xLabel,yLabel,scaleLabel,angleLabel;
private FlushTextBox<ImageDrawingData> idEditor;
private LabeledInputRangeWidget alphaRange;

public ImageDrawingDataEditor(){
	//move controler
	HorizontalPanel movePanel=new HorizontalPanel();
	movePanel.add(new Button("Top",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(value==null){
				return;
			}
			drawingDataObjects.topItem(value);
			updateCanvas();
		}
	}));
	movePanel.add(new Button("Up",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(value==null){
				return;
			}
			drawingDataObjects.upItem(value);
			updateCanvas();
		}
	}));
	movePanel.add(new Button("Down",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(value==null){
				return;
			}
			drawingDataObjects.downItem(value);
			updateCanvas();
		}
	}));
	movePanel.add(new Button("Bottom",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(value==null){
				return;
			}
			drawingDataObjects.bottomItem(value);
			updateCanvas();
		}
	}));
	add(movePanel);
	
	movePanel.add(new Button("Sync others",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			doSyncTextureOrder();
		}
	}));
	add(movePanel);
	
	HorizontalPanel visiblePanel=new HorizontalPanel();
	visiblePanel.setVerticalAlignment(ALIGN_MIDDLE);
	add(visiblePanel);
	visibleCheck=new CheckBox("show");
	visiblePanel.add(visibleCheck);
	visibleCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			flush();
		}
	});
	
	visiblePanel.add(new Label("Bone:"));
	
	boneNameList = new ValueListBox<String>(new Renderer<String>() {

		@Override
		public String render(String object) {
			if(object!=null){
				return object;
			}
			return "";
		}

		@Override
		public void render(String object, Appendable appendable) throws IOException {
			
		}
	});
	visiblePanel.add(boneNameList);
	boneNameList.addValueChangeHandler(new ValueChangeHandler<String>() {
		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			if(value!=null){
				flush();
			}
		}
		
	});
	HorizontalPanel checks=new HorizontalPanel();
	add(checks);
	checks.setVerticalAlignment(ALIGN_MIDDLE);
	flipHorizontalCheck=new CheckBox("flip horizontal");
	checks.add(flipHorizontalCheck);
	flipHorizontalCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			flush();
		}
	});
	flipVerticalCheck=new CheckBox("flip vertical");
	checks.add(flipVerticalCheck);
	flipVerticalCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			flush();
		}
	});
	HorizontalPanel status=new HorizontalPanel();
	status.setSpacing(2);
	add(status);
	status.setVerticalAlignment(ALIGN_MIDDLE);
	
	status.add(new Label("X:"));
	xLabel=new Label();
	status.add(xLabel);
	
	status.add(new Label("Y:"));
	yLabel=new Label();
	status.add(yLabel);
	
	status.add(new Label("Angle:"));
	angleLabel=new Label();
	status.add(angleLabel);
	
	status.add(new Label("Scale:"));
	scaleLabel=new Label();
	status.add(scaleLabel);
	
	HorizontalPanel namePanel=new HorizontalPanel();
	add(namePanel);
	namePanel.add(new Label("Id:"));
	idEditor = new FlushTextBox<ImageDrawingData>(this);
	idEditor.setWidth("160px");
	namePanel.add(idEditor);
	
	alphaRange = new LabeledInputRangeWidget("transparent:", 0.01, 1.0, 0.01);
	alphaRange.getRange().addValueChangeHandler(new ValueChangeHandler<Number>() {
		@Override
		public void onValueChange(ValueChangeEvent<Number> event) {
			flush();
		}
	});
	alphaRange.getRange().setWidth("100px");
	HorizontalPanel rangePanel=new HorizontalPanel();
	rangePanel.setVerticalAlignment(ALIGN_MIDDLE);
	add(rangePanel);
	//rangePanel.add(new Label("alpha:"));
	rangePanel.add(alphaRange);
	
	HorizontalPanel imagePanel=new HorizontalPanel();
	imagePanel.setVerticalAlignment(ALIGN_MIDDLE);
	add(imagePanel);
	imagePanel.add(new Label("Image:"));
	final CheckBox fitScale=new CheckBox("fit scale");
	imagePanel.add(fitScale);
	FileUploadForm imageUpload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
		@Override
		public void uploaded(File file, String text) {
			ImageElement imageElement=ImageElementUtils.create(text);
			double ratio=(double)value.getImageElement().getWidth()/imageElement.getWidth();
			double newScale=value.getScaleX()*ratio;
			value.setImageElement(imageElement);
			
			
			//change-image-name 
			
			value.setImageName(fixFileName(file.getFileName(),imageElement));
			
			//TODO support scale range
			if(fitScale.getValue()){
			value.setScaleX(newScale);
			value.setScaleY(newScale);
			}
			flush();
		}
	});
	add(imageUpload);
}

public void updateValues(){
	if(value==null){
		return;
	}
	xLabel.setText(String.valueOf(value.getIntX()));
	yLabel.setText(String.valueOf(value.getIntY()));
	angleLabel.setText(String.valueOf(value.getAngle()));
	scaleLabel.setText(String.valueOf(value.getScaleX()));

	alphaRange.setValue(value.getAlpha(),false);
}

public void setBoneNames(List<String> names){
	if(names.size()>0){
	boneNameList.setValue(names.get(0));
	}
	boneNameList.setAcceptableValues(names);
}
@Override
	public void setDelegate(EditorDelegate<ImageDrawingData> delegate) {
		// TODO Auto-generated method stub
	}

	@Override
	 public void flush() {
		
		// TODO Auto-generated method stub
		if(value==null){
			return;
		}
		
		if(isExistDrawingDataName(value.getId(),idEditor.getValue())){
			Window.alert(idEditor.getValue()+" is already exist.can't update.value id is "+value.getId());
			idEditor.setValue(value.getId());//reback for notify
			return;
		}
		value.setId(idEditor.getValue());
		
		value.setBoneName(boneNameList.getValue());
		
		value.setFlipHorizontal(flipHorizontalCheck.getValue());
		value.setFlipVertical(flipVerticalCheck.getValue());
		
		value.setVisible(visibleCheck.getValue());
		
		
		value.setAlpha(alphaRange.getValue());
		
		onImageDrawingDataFlush();
	}

	@Override
	public void onPropertyChange(String... paths) {
		// TODO Auto-generated method stub
	}

	@Override
	 public void setValue(ImageDrawingData value) {
		this.value=value;
		if(value==null){
			//LogUtils.log("setValue:null");
			this.setVisible(false);
			return;
		}
		this.setVisible(true);
		boneNameList.setValue(value.getBoneName());
		
		flipHorizontalCheck.setValue(value.isFlipHorizontal());
		flipVerticalCheck.setValue(value.isFlipVertical());
		visibleCheck.setValue(value.isVisible());
		
		xLabel.setText(String.valueOf(value.getIntX()));
		yLabel.setText(String.valueOf(value.getIntY()));
		angleLabel.setText(String.valueOf(value.getAngle()));
		scaleLabel.setText(String.valueOf(value.getScaleX()));
	
		alphaRange.setValue(value.getAlpha(),false);
		
		idEditor.setValue(value.getId());
	}


}
	public TexturePage(MainManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCanvasTouchEnd(int sx, int sy) {
		// TODO Auto-generated method stub
		
	}



	@Override
	protected void onCanvasWheeled(int delta, boolean shiftDown) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onBoneAndAnimationChanged(BoneAndAnimationData data) {
		if(data.getBone()==null){
			LogUtils.log("TexturePage:invalidly bone is null");
		}
		boneControler.setBone(data.getBone());
	}

	@Override
	protected void onBackgroundChanged(ImageDrawingData background) {
		// TODO support later
		
	}

	@Override
	protected void onTextureDataChanged(TextureData textureData) {
		
		List<ImageDrawingData> datas=textureData.getImageDrawingDatas();
		
		drawingDataObjects.setDatas(datas);
		drawingDataObjects.update();
		driver.edit(null);
		
		notDrawingItem.clear();//handle visible
		
		updateCanvas();
	}

	@Override
	protected void initialize() {
		 driver = GWT.create(Driver.class);
		 convertedDatas=new ArrayList<Canvas>();
		 background=new Background();
		 
			manager.getTextureOrderSystem().addListener(new DataChangeListener<List<String>>() {
				@Override
				public void dataChanged(List<String> data, DataOwner owner) {
					onTextureOrderChanged(data,owner);
				}
			});
			notDrawingItem=Lists.newArrayList();
	}

	protected void doSyncTextureOrder() {
		List<String> names=FluentIterable.from(drawingDataObjects.getDatas()).transform(new Function<ImageDrawingData,String>(){
			@Override
			public String apply(ImageDrawingData input) {
				return input.getId();
			}}).toList();
		manager.setTextureOrder(names, this);
	}

	public Optional<ImageDrawingData> findDataById(String id){
		for(ImageDrawingData clip:drawingDataObjects.getDatas()){
			if(clip.getId().equals(id)){
				return Optional.of(clip);
			}
		}
		return Optional.absent();
	}
	private void onTextureOrderChanged(List<String> data, DataOwner owner){
		if(owner==this){ //called by myself no need to change
			return;
		}
		List<ImageDrawingData> newDatas=Lists.newArrayList();
		
		for(String id:data){
			for(ImageDrawingData finded:findDataById(id).asSet()){
				drawingDataObjects.getDatas().remove(finded);
				newDatas.add(finded);
			}
		}
		
		for(ImageDrawingData remain:drawingDataObjects.getDatas()){
			newDatas.add(remain);
		}
		
		drawingDataObjects.setDatas(newDatas);
		drawingDataObjects.update();
		
	}
	
	@Override
	protected Widget createCenterPanel() {
		VerticalPanel panel=new VerticalPanel();

		
		
		
		
		//cerate default bone
		TwoDimensionBone rootBone = new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=rootBone.addBone(new TwoDimensionBone("back",0, -100));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",0, -100));
		
		
		//allbones = BoneUtils.getAllBone(rootBone);
		SkeletalAnimation animations = new SkeletalAnimation("test", 33.3);
		 
		createCanvas();
		createBoneControls(animations,rootBone,canvas);
		
		boneControler =new BoneControler(canvas){
			@Override
			public String getSelectionName() {
				return drawingDataObjects.getSelection()!=null?drawingDataObjects.getSelection().getBoneName():null;
			}
		};
		
		//createWestPanel(root);
	
		    HorizontalPanel northPanel=new HorizontalPanel();
		    northPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		    
		 
		    
		
		    /*
		    northPanel.add(new Label("Load:"));
		    FileUploadForm load=JSZipUtils.createZipFileUploadForm(new ZipListener() {
				
				@Override
				public void onLoad(String name, JSZip zip) {
					doLoadData(name,zip);
				}
				
				@Override
				public void onFaild(int states, String statesText) {
					LogUtils.log("faild:"+states+","+statesText);
				}
			});
			northPanel.add(load);
			*/
		    
		   
		    
		    northPanel.add(new Button("Reload",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doReloadData();
				}
			}));
		    
		    
		    northPanel.add(new Button("Save",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doSaveData();
				}
			}));
		    
		    showBoneCheck = new CheckBox("show Bone on animation");
		    showBoneCheck.setValue(true);
		    showBoneCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					updateCanvas();
				}
		    	
			});
		    northPanel.add(showBoneCheck);
		    
		    panel.add(northPanel);
		    downloadLinks = new HorizontalPanel();
		    downloadLinks.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		    northPanel.add(downloadLinks);
		    
		   
		    
		    
		   //create default animation
			currentSelectionFrame = BoneUtils.createEmptyAnimationFrame(getRootBone());
			animations.add(currentSelectionFrame);
			
			for(BoneFrame frame:currentSelectionFrame.getBoneFrames().values()){
				frame.setAngle(45);
			}
			
			
			
			animations.add(BoneUtils.createEmptyAnimationFrame(getRootBone()));
			bonePositionControler.updateBoth(currentSelectionFrame);
		
		//panel.add(createBackgroundButtons());
		panel.add(createAnimationLoadButtons());
		panel.add(createAnimationFrameControlButtons(animations));    
		
		
		
		    
		panel.add(canvas);
		
		setNewBoneAndAnimation(rootBone, animations);
		return panel;
	}
	

	protected void doReloadData() {
		onTextureDataChanged(manager.getTextureData().copy());
	}

	public void onImageDrawingDataFlush() {
		drawingDataObjects.update();
		updateCanvas();//anyway update
	}
	

	protected void doCopyHorizontal() {
		ImageDrawingData selection=drawingDataObjects.getSelection();
		if(selection==null){
			return;
		}
		//TODO support chose base bone
		int index=0;
		int centerX=bonePositionControler.getRawInitialData().get(index).getX()+bonePositionControler.getSettings().getOffsetX();
		
		ImageDrawingData newData=selection.copy();
		newData.setId(getUniqDrawingName(newData.getId()));
		
		
		newData.setX(calcurateFlip(centerX,selection.getIntX()));
		
		newData.setFlipHorizontal(!selection.isFlipHorizontal());
		newData.setAngle(360-selection.getAngle());
		
		
		//auto match belonging
		String newBoneName=new ReplaceEachOther("left", "right").replaceEachCommonCase(newData.getBoneName());
		
		String matchName=findMatchBoneName(newBoneName);
		if(matchName.equals(getRootBone().getName())){//possible not correct match
			matchName=newData.getBoneName();
		}
		newData.setBoneName(newBoneName);
		
		addDrawingData(newData);
	}
	protected void doCopyVertical() {
		ImageDrawingData selection=drawingDataObjects.getSelection();
		if(selection==null){
			return;
		}
		//TODO support chose base bone
		int index=0;
		int centerY=bonePositionControler.getRawInitialData().get(index).getY()+bonePositionControler.getSettings().getOffsetY();
		
		ImageDrawingData newData=selection.copy();
		newData.setId(getUniqDrawingName(newData.getId()));
		
		
		newData.setY(calcurateFlip(centerY,selection.getIntY()));
		
		newData.setFlipVertical(!selection.isFlipVertical());
		newData.setAngle(360-selection.getAngle());
		
		//auto match belonging
				String newBoneName=new ReplaceEachOther("up", "down").replaceEachCommonCase(newData.getBoneName());
				
				String matchName=findMatchBoneName(newBoneName);
				if(matchName.equals(getRootBone().getName())){//possible not correct match
					matchName=newData.getBoneName();
				}
				newData.setBoneName(newBoneName);
				
		addDrawingData(newData);
	}
	
	public int calcurateFlip(int center,int position){
		int diff=position-center;
		return center-diff;
	}
	
	
	protected void doDupricate() {
		ImageDrawingData selection=drawingDataObjects.getSelection();
		if(selection==null){
			return;
		}
		ImageDrawingData newData=selection.copy();
		newData.setId(getUniqDrawingName(newData.getId()));
			
		addDrawingData(newData);
	}
	private String getUniqDrawingName(String name){
		List<String> exist=Lists.newArrayList();
		for(ImageDrawingData data:drawingDataObjects.getDatas()){
			exist.add(data.getId());
		}
		int index=1;
		if(!exist.contains(name)){
			return name;
		}
		do{
			
			name=FileNames.getRemovedLastNumbers(name);
			if(!name.endsWith("-")){
				name+="-";
			}
			name+=index;
			
		index++;
		}while(exist.contains(name));
		
		return name;
	}
	
	private boolean isExistImageName(String fileName){
		List<String> exist=Lists.newArrayList();
		for(ImageDrawingData data:drawingDataObjects.getDatas()){
			exist.add(data.getImageName());
		}
		
		return exist.contains(fileName);
	}
	private String getUniqFileName(String fileName){
		List<String> exist=Lists.newArrayList();
		for(ImageDrawingData data:drawingDataObjects.getDatas()){
			exist.add(data.getImageName());
		}
		String name=fileName;
		int index=1;
		if(!exist.contains(name)){
			return name;
		}
		do{
			String extension=FileNames.getExtension(fileName);
			String removedExtension=FileNames.getRemovedExtensionName(name);
		name=removedExtension+"-"+index+"."+extension;
		index++;
		}while(exist.contains(name));
		
		return name;
	}
	
	private boolean isExistDrawingDataName(String oldName,String name){
		if(oldName.equals(name)){
			return false;
		}
		List<String> exist=Lists.newArrayList();
		for(ImageDrawingData data:drawingDataObjects.getDatas()){
			exist.add(data.getId());
		}
		return exist.contains(name);
	}
	

	
	private String findMatchBoneName(String fileName){
		String boneName=getRootBone().getName();
		String name=FileNames.getRemovedExtensionName(fileName);
		List<TwoDimensionBone> allbone=BoneUtils.getAllBone(getRootBone());
		List<String> names=Lists.newArrayList();
		
		
		names.add(name);
		names.add(name.toLowerCase());
		names.add(ValuesUtils.toUpperCamel(name));
		names.add(name.toUpperCase());
		
		for(String checkName:names){
			for(TwoDimensionBone bone:allbone){
				if(bone.getName().equals(checkName)){
					return bone.getName();
				}
			}
		}
		
		
		return boneName;
	}
	
	private String fixFileName(String name,ImageElement base64Element){
		if(!isExistImageName(name)){
			return name;
		}
		ImageDrawingData data=findImageDrawingDataByImageName(name);
		if(data.getImageElement().getSrc().equals(base64Element.getSrc())){
			return name;
		}
		return getUniqFileName(name);
	}
	private ImageDrawingData findImageDrawingDataByImageName(String name){
		for(ImageDrawingData data:drawingDataObjects.getDatas()){
			if(data.getImageName().equals(name)){
				return data;
			}
		}
		return null;
	}
	
//private List<ImageDrawingData> imageDrawingDatas=new ArrayList<ImageDrawingData>();
	private void uploadImage(String name,ImageElement element,int index) {
		String idName=FileNames.getRemovedExtensionName(name);
		//name=FileNames.getRemovedLastNumbers(name);
		
		ImageDrawingData data=new ImageDrawingData(getUniqDrawingName(idName),element);
		data.setImageName(fixFileName(name,element));
		int maxObjectSize=200;
		int imgw=data.getImageElement().getWidth();
		int imgh=data.getImageElement().getHeight();
		int max=imgw>imgh?imgw:imgh;
		
		if(max>maxObjectSize && uploadImageAutoScaleCheck.getValue()){
			double ratio=(double)maxObjectSize/max;
			
			//TODO change better way
			int tmp=(int) (ratio*100);
			ratio=(double)tmp/100;
			
			data.setScaleX(ratio);
			data.setScaleY(ratio);
		}
		
		data.setBoneName(findMatchBoneName(name));//on initial set bone
		
		//TODO limit,otherwise over canvas
		data.setX(maxObjectSize+index*10);
		data.setY(maxObjectSize+index*10);
		
		
		
		
		addDrawingData(data);
	}
	
	private void addDrawingData(ImageDrawingData data){
		//set-editor & update list
		
		drawingDataObjects.addItem(data);
		animationModeToggle.setValue(false, true);//for convert image
		
		drawingDataObjects.setSelected(data, true);//select upload
		
		updateCanvas();
	}
	
	private void onAnimationRangeChanged(int index){
		currentSelectionFrame=animationControler.getSelection();
		
		
		
		bonePositionControler.updateBoth(currentSelectionFrame);
		updateCanvas();
	}
	
	
	private List<Canvas> convertedDatas;
	
	private Widget createAnimationFrameControlButtons(SkeletalAnimation animations) {
		
		HorizontalPanel panel=new HorizontalPanel();
		panel.setHeight("32px");
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		animationModeToggle = new ToggleButton("animation-mode");
		animationModeToggle.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				modeAnimation=event.getValue();
				
				animationControler.setVisible(event.getValue());
				
				if(modeAnimation){
					convertedDatas.clear();
					for(ImageDrawingData data:drawingDataObjects.getDatas()){
						convertedDatas.add(data.convertToCanvas());
					}
				}
				
				updateCanvas();
			}
			
		});
		//panel.add(animationModeToggle);
		
		
		
		animationControler = new AnimationControlRange(animations);
		panel.add(animationControler);
		animationControler.getInputRange().addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				onAnimationRangeChanged(event.getValue().intValue()-1);
			}
		});
		animationControler.setVisible(false);
		
		return panel;
	}
	private boolean modeAnimation;
	
	
	private void setNewBoneAndAnimation(TwoDimensionBone newRoot,SkeletalAnimation animations){
		setRootBone(newRoot);
		
		if(animations==null || animations.getFrames().size()==0){
			LogUtils.log("animation null or empty.create dummy");
			//usually select bone-only this happen,create dammy empty frame
			if(animations.getFrames().isEmpty()){
				animations.getFrames().add(BoneUtils.createEmptyAnimationFrame(newRoot));
			}
		}
		
		animationControler.setAnimation(animations);
		
		currentSelectionFrame = animations.getFrames().get(0);
		
		
		
		
		animationControler.syncDatas();
		
		//no need always first frame would be selected.
		//animationControler.setSelection(animations.getFrames().get(0), false);
		
		bonePositionControler.updateBoth(currentSelectionFrame);
		
		List<String> names=BoneUtils.getAllBoneName(newRoot);
		drawingDataEditor.setBoneNames(names);
		
		updateCanvas();
	}

	

	
	private Widget createAnimationLoadButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.add(new Label("deprecated-Animation-Data:"));
		 panel.add(new Label("Load:"));
		    FileUploadForm load=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
				
				@Override
				public void uploaded(File file, String text) {
					doLoadAnimation(text);
				}
			}, true);
		    load.setAccept(FileUploadForm.ACCEPT_TXT);
		
		//panel.add(load);
		
		   
/*		Button fromTexture=new Button("from texture-zip-bone",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TwoDimensionBone bone=textureData.getBone();
				if(bone==null){
					Window.alert("this zip not contain bone.txt data");
				}else{
					AnimationFrame frame=BoneUtils.createEmptyAnimationFrame(bone);
					BoneAndAnimationData data=new BoneAndAnimationData();
					data.setBone(bone);
					SkeletalAnimation animation=new SkeletalAnimation();
					animation.add(frame);
					data.setAnimation(animation);
					doLoadAnimation(data);
				}
			}
		});
		panel.add(fromTexture);*/
		
		return panel;
	}
	private void doLoadAnimation(String lines){
		BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(lines));
		
		doLoadAnimation(data);
	}
	private void doLoadAnimation(BoneAndAnimationData data){
		
		List<TwoDimensionBone> bones=BoneUtils.getAllBone(data.getBone());
		
		for(AnimationFrame frame:data.getAnimation().getFrames()){
			frame.insertEmptyFrames(bones);
		}
		
		
		
		
		setNewBoneAndAnimation(data.getBone(), data.getAnimation());
	}
	protected void doCreateNewData() {
		boolean confirm=Window.confirm("clear all frame?if not saved data would all gone");
		if(!confirm){
			return;
		}
		drawingDataObjects.clearAllItems();
		driver.edit(null);
		updateCanvas();
	}

	private TextureData textureData;
	protected void doLoadData(String name,JSZip zip) {
		
		TextureDataConverter converter=new TextureDataConverter();
		textureData=converter.convert(zip);
		
		List<ImageDrawingData> datas=textureData.getImageDrawingDatas();
		
		drawingDataObjects.setDatas(datas);
		drawingDataObjects.update();
		driver.edit(null);
		updateCanvas();
	}
	protected void doSaveData() {
		
		TextureDataConverter converter=new TextureDataConverter();
		TextureData data=new TextureData();
		data.setImageDrawingDatas(drawingDataObjects.getDatas());
		data.setBone(getRootBone());
		
		JSZip jszip=converter.reverse().convert(data);
		downloadLinks.clear();
		downloadLinks.add(JSZipUtils.createDownloadAnchor(jszip, "2dbone-textures.zip", "download", true));
		
		manager.setTextureData(getEditorName(), data);
	}

	public String getEditorName(){
		return "texture-page";
	}
	
	protected void doRemoveData() {
		ImageDrawingData selection=drawingDataObjects.getSelection();
		drawingDataObjects.removeItem(selection);
		for(ImageDrawingData data:drawingDataObjects.getFirst().asSet()){
			//remove and select first
			drawingDataObjects.setSelected(data, true);
		}
		
		driver.edit(null);//can do it?
		
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
						shiftDowned=event.isShiftKeyDown();
						onCanvasWheeled(event.getDeltaY());
					}
				});
	}
	private boolean shiftDowned;
	
	//private CircleLineBonePainter painter;


	private void createBoneControls(SkeletalAnimation animations,TwoDimensionBone rootBone,final Canvas canvas){
		settings=new CanvasBoneSettings(canvas, rootBone);
		
		
		
		
		
		
		
		bonePositionControler=new BonePositionControler(settings);
		bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
		
		
		
		//painter = new CircleLineBonePainter(canvas, this, bonePositionControler);
		
		

		
	}
	private Background background;
	private Widget createBackgroundButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(new Label("deprecated-Background:"));
		final CheckBox backgroundVisibleCheck = new CheckBox("Show");
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				ImageElement element=ImageElementUtils.create(text);
				
				background.setBackground(file.getFileName(), element);
				
				
				background.getBackgroundData().setX(bonePositionControler.getSettings().getOffsetX());
				background.getBackgroundData().setY(bonePositionControler.getSettings().getOffsetY());
				
				backgroundVisibleCheck.setValue(true);
				background.setVisible(true);
				
				//backgroundEditCheck.setValue(false);
				updateCanvas();
			}
		});
		//panel.add(upload);
		Button reset=new Button("Clear",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				background.clear();
				updateCanvas();
			}
		});
		//panel.add(reset);
		
		
		panel.add(backgroundVisibleCheck);
		backgroundVisibleCheck.setValue(true);
		backgroundVisibleCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				background.setVisible(event.getValue());
				updateCanvas();
			}
		});
		
		CheckBox backgroundEditCheck = new CheckBox("Edit");
		panel.add(backgroundEditCheck);
		backgroundEditCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				background.setEditable(event.getValue());
			}
		});
		
		
		
		final HorizontalPanel downloadLinks=new HorizontalPanel();
		
		
		
		Button extractBoneBt=new Button("Extract Textures",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
				
				for(ImageDrawingData data:drawingDataObjects.getDatas()){
					if(data.isVisible()){
						data.draw(extractCanvas);
					}
					String color="#0f0";
					
					CanvasUtils.draw(extractCanvas,data.getCornerPoint(),true,color);
				}
				
				String dataUrl=extractCanvas.toDataUrl();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-texture.png", "texture", true);
				downloadLinks.add(a);
			}
		});
		panel.add(extractBoneBt);
		panel.add(downloadLinks);
		
		return panel;
	}
	
	protected void onCanvasWheeled(int deltaY) {
if(modeAnimation){
			
		}else{
			onModeEditWheel(deltaY);
		}
	}


	protected void onCanvasDragged(int vectorX, int vectorY) {
if(modeAnimation){
			
		}else{
			onModeEditDrag(vectorX, vectorY);
		}
	}


	
	protected void onCanvasTouchStart(int sx, int sy) {
		if(modeAnimation){
			
		}else{
			onModeEditDragStart(sx, sy);
		}
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
	protected  void updateCanvas(){
		CanvasUtils.clear(canvas);
		if(background.hasBackgroundData() && background.isVisible()){
			
			
			background.getBackgroundData().draw(canvas);
			String border="#000";
			/*
			if(backgroundSelected){
				border="#0f0";
			}
			backgroundData.drawBorder(canvas,border);
			*/
			
		}
		
		
		if(modeAnimation){
			updateCanvasOnAnimation();
		}else{
			updateCanvasOnEdit();
		}
	}
	private void updateCanvasOnAnimation() {
		
		
		//switch mode
		
		bonePositionControler.updateBoth(currentSelectionFrame);//TODO update on value changed only
		//TODO add show bone check
		//TODO make class,it's hard to understand
		 List<BoneWithXYAngle> emptyBonePosition=bonePositionControler.getRawInitialData();
		 List<BoneWithXYAngle> movedBonePosition=bonePositionControler.getRawAnimationedData();
		 
		
		
		//int offsetX=painter.getOffsetX();
		//int offsetY=painter.getOffsetY();
		
		int offsetX=bonePositionControler.getSettings().getOffsetX();
		int offsetY=bonePositionControler.getSettings().getOffsetY();
		
		List<ImageDrawingData> imageDrawingDatas=drawingDataObjects.getDatas();
		for(int i=0;i<imageDrawingDatas.size();i++){
			ImageDrawingData data=imageDrawingDatas.get(i);
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
			Canvas converted=convertedDatas.get(i);
			
			int diffX=(boneX+offsetX)-(data.getIntX()-converted.getCoordinateSpaceWidth()/2);
			int diffY=(boneY+offsetY)-(data.getIntY()-converted.getCoordinateSpaceHeight()/2);
			
			
			int imageX=(int)(data.getIntX()-converted.getCoordinateSpaceWidth()/2)-(boneX+offsetX); //
			int imageY=(int)(data.getIntY()-converted.getCoordinateSpaceHeight()/2)-(boneY+offsetY);//
			//LogUtils.log(imageX+","+imageY);
			
			drawImageAt(canvas,converted.getCanvasElement(),movedX+offsetX-diffX,movedY+offsetY-diffY,diffX,diffY,angle);
			//canvas.getContext2d().drawImage(converted.getCanvasElement(), (int)(data.getX()-converted.getCoordinateSpaceWidth()/2), (int)(data.getY()-converted.getCoordinateSpaceHeight()/2));
			//
		}
		
		if(showBoneCheck.getValue()){
		canvas.getContext2d().setGlobalAlpha(0.5);
		//painter.paintBone(currentSelectionFrame);
		canvas.getContext2d().setGlobalAlpha(1.0);
		}
	}
	private void updateCanvasOnEdit() {

		for(ImageDrawingData data:drawingDataObjects.getDatas()){
			if(data.isVisible() && isDrawItem(data)){//drawitem is temporaly draw or not modify data itself
				data.draw(canvas);
			}
			String color="#0f0";
			if(data==drawingDataObjects.getSelection()){
				color="#f00";
			}
			CanvasUtils.draw(canvas,data.getCornerPoint(),true,color);
		}
		
		if(showBoneCheck.getValue()){
			boneControler.paintBone();
		}
		//painter.paintBone();//bone-last
		
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

	

	
	
	
	private AnimationFrame currentSelectionFrame;//should contain all bone
	private CanvasBoneSettings settings;
	
	public TwoDimensionBone getRootBone() {
		return settings.getBone();
	}
	public void setRootBone(TwoDimensionBone rootBone) {
		settings.setBone(rootBone);
		
		//update all bone-relative-name to root-bone
		
		/*
		for(ImageDrawingData data:drawingDataObjects.getDatas()){
			data.setBoneName(rootBone.getName());
		}
		*/
	}


	private HorizontalPanel downloadLinks;
	
	private AnimationControlRange animationControler;
	private EasyCellTableObjects<ImageDrawingData> drawingDataObjects;
	private CheckBox showBoneCheck;
	private ToggleButton animationModeToggle;


	private void onModeEditDragStart(int x,int y){
		
		imageDataSelectionOnCanvas=null;
		
		//current selection first;
		if(drawingDataObjects.getSelection()!=null && drawingDataObjects.getSelection().collision(x, y)){
			
			imageDataSelectionOnCanvas=drawingDataObjects.getSelection();
			
		}else{
		
		//detect last first,last image are draw at top
		for(int i=drawingDataObjects.getDatas().size()-1;i>=0;i--){
			ImageDrawingData data=drawingDataObjects.getDatas().get(i);
			if(data.collision(x, y)){
				imageDataSelectionOnCanvas=data;
				break;
			}
		}
		
		}
		
		if(imageDataSelectionOnCanvas!=null){
			drawingDataObjects.setSelected(imageDataSelectionOnCanvas, true);
		}else{
			//
		}

		//TODO create data-editor
		/*
		if(imageDataSelection!=null){
			int index=dataBelongintMap.get(imageDataSelection);
			boneListBox.setValue(allbones.get(index));
		}
		*/
		
		//updateCanvas();//if call update ,duplicate called from selection-changed-listener
	}
	
	protected void onModeEditDrag(int vectorX, int vectorY) {
		if(imageDataSelectionOnCanvas!=null && imageDataSelectionOnCanvas.isVisible()){
			
			
			if(canvasControler.isRightMouse()){
				imageDataSelectionOnCanvas.incrementAngle(vectorX);
			}else{
				imageDataSelectionOnCanvas.incrementX(vectorX);
				imageDataSelectionOnCanvas.incrementY(vectorY);
			}
			imageDataSelectionOnCanvas.updateBounds();
			
			updateCanvas();
			drawingDataEditor.updateValues();
		}
	}
	ImageDrawingData imageDataSelectionOnCanvas;
	private ImageDrawingDataEditor drawingDataEditor;
	private CheckBox uploadImageAutoScaleCheck;
	protected void onModeEditWheel(int v) {
		
		if(imageDataSelectionOnCanvas!=null && imageDataSelectionOnCanvas.isVisible()){
			int zoom=(int) (100*imageDataSelectionOnCanvas.getScaleX());
			
			int vector=1;
			if(v<0){
				vector=-1;
			}
			
			int add=shiftDowned?1:5;
			zoom+=vector*add;
			if(zoom<5){
				zoom=5;
			}
			
			imageDataSelectionOnCanvas.setScaleX((double)zoom/100);
			imageDataSelectionOnCanvas.setScaleY((double)zoom/100);
			imageDataSelectionOnCanvas.updateBounds();
		}
		updateCanvas();
		drawingDataEditor.updateValues();
	}

	@Override
	public String getSelectionName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Widget createWestPanel() {
		
		VerticalPanel panel=new VerticalPanel();

		panel.setSpacing(2);
		
		panel.add(new Label("drawing-mage-datas"));
		
		
		HorizontalPanel removePanel=new HorizontalPanel();
		
		removePanel.add(new Button("Clear All",new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						doCreateNewData();
					}
				}));

		removePanel.add(new Button("Remove",new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						doRemoveData();
					}
				}));
		panel.add(removePanel);
		
		
		HorizontalPanel option1=new HorizontalPanel();
		option1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		option1.add(new Label("Add:"));
		
		uploadImageAutoScaleCheck = new CheckBox("auto-scale");
		uploadImageAutoScaleCheck.setValue(true);
		option1.add(uploadImageAutoScaleCheck);
		panel.add(option1);
		
		HorizontalPanel buttons=new HorizontalPanel();
		panel.add(buttons);
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				uploadImage(file.getFileName(),ImageElementUtils.create(text),0);
			}

			
		});
		upload.setAccept(FileUploadForm.ACCEPT_IMAGE);//only png support transparent
		buttons.add(upload);
		
	
		
		
		ScrollPanel scroll=new ScrollPanel();
		scroll.setSize(westPanelWidth+"px","400px");
		panel.add(scroll);
		SimpleCellTable<ImageDrawingData> dataTable=new SimpleCellTable<ImageDrawingData>(999) {
			@Override
			public void addColumns(CellTable<ImageDrawingData> table) {
				TextColumn<ImageDrawingData> nameColumn=new TextColumn<ImageDrawingData>() {
					
					@Override
					public String getValue(ImageDrawingData object) {
						//return object.getId();
						return object.getBoneName();//it's short
					}
				};
				table.addColumn(nameColumn);
				
				  Column<ImageDrawingData,Boolean> activeColumn=new Column<ImageDrawingData,Boolean>(new CheckboxCell()){
					    @Override public Boolean getValue(    ImageDrawingData iteration){
					      return isDrawItem(iteration);
					    }
					  }
					;
					  activeColumn.setFieldUpdater(new FieldUpdater<ImageDrawingData,Boolean>(){
					    @Override public void update(    int index,    ImageDrawingData iteration,    Boolean value){
					      
					      setDrawItem(iteration,value);
					     updateCanvas();
					    }
					  }
					);
					  table.addColumn(activeColumn,"Show");
					  
					  
					  ButtonColumn<ImageDrawingData> removeBtColumn=new ButtonColumn<ImageDrawingData>() {
							@Override
							public void update(int index, ImageDrawingData object,
									String value) {
								
								for(ImageDrawingData original:manager.getTextureData().findDataById(object.getId()).asSet()){
									original.copyTo(object);
									updateCanvas();
								}
								//possible not found if modified after
									
							}
							@Override
							public String getValue(ImageDrawingData object) {
								 return "Reset";
							}
						};
						table.addColumn(removeBtColumn);
					
			}
		};
		scroll.add(dataTable);
		
		drawingDataObjects = new EasyCellTableObjects<ImageDrawingData>(dataTable,false) {
			
			@Override
			public void onSelect(ImageDrawingData selection) {
				//LogUtils.log(selection.getName());
				driver.edit(selection);
				updateCanvas();
				imageDataSelectionOnCanvas=selection;//for mouse-wheel-zoom
			}
		};
		
		drawingDataEditor = new ImageDrawingDataEditor();
		driver.initialize(drawingDataEditor);
		
		driver.edit(new ImageDrawingData("",null));//dummy for flush
	
		
		panel.add(drawingDataEditor);
		
		panel.add(new Label("Copy"));
		
		HorizontalPanel copyButtons=new HorizontalPanel();
		panel.add(copyButtons);
		
		Button copyBt=new Button("same",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doDupricate();
			}
		});
		copyButtons.add(copyBt);
		Button copyHBt=new Button("horizontal",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doCopyHorizontal();
			}
		});
		copyButtons.add(copyHBt);
		Button copyVBt=new Button("vertical",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doCopyVertical();
			}
		});
		copyButtons.add(copyVBt);
		
		
		return panel;
	}
	
	private boolean isDrawItem(ImageDrawingData data){
		return !notDrawingItem.contains(data);
	}
	private void setDrawItem(ImageDrawingData data,boolean value){
	
		if(value){
			notDrawingItem.remove(data);
		}else{
			notDrawingItem.add(data);
		}
	}
	
	List<ImageDrawingData> notDrawingItem;
	

	@Override
	protected void updateDatas() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getOwnerName() {
		return "Texture-Editor";
	}

	

}
