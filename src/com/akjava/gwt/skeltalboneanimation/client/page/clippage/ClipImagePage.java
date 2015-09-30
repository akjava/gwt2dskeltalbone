package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.jszip.client.JSZipUtils;
import com.akjava.gwt.jszip.client.JSZipUtils.ZipListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.lib.client.experimental.ImageBuilder;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.lib.client.game.PointD;
import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.lib.client.graphics.Graphics;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.skeltalboneanimation.client.Background;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoxPoints;
import com.akjava.gwt.skeltalboneanimation.client.CanvasDrawingDataControler;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingDataControler;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.UploadedFileManager.ClipImageDataChangeListener;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneListBox;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.ClipImageDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.TextureDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataChangeListener;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataOwner;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.BoneControler;
import com.akjava.gwt.skeltalboneanimation.client.page.html5app.TransparentItPage;
import com.akjava.lib.common.graphics.IntRect;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ClipImagePage extends AbstractPage implements DataOwner{
	 interface Driver extends SimpleBeanEditorDriver< ClipData,  ClipDataEditor> {}
	
	 //dont initialize here
	 
	Driver driver;
	private EasyCellTableObjects<ClipData> cellObjects;

	private TransparentItPage transparentItPage;
	 
	public TransparentItPage getTransparentItPage() {
		return transparentItPage;
	}


	public void setTransparentItPage(TransparentItPage transparentItPage) {
		this.transparentItPage = transparentItPage;
	}


	public ClipImagePage(final MainManager manager){
		super(manager);
		//clip is minor
		manager.getUploadedFileManager().addClipImageDataChangeListener(new ClipImageDataChangeListener() {
			
			@Override
			public void ClipImageDataChanged(ClipImageData data) {
				onClipImageDataChanged(data);
			}
		});
	}
	
	
	public Panel createClipControlPanel(){
		VerticalPanel root=new VerticalPanel();
		
		HorizontalPanel panel1=new HorizontalPanel();
		root.add(panel1);
		
		Button createFromBoneBt=new Button("Create data from bone",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doCreateClipDataFromBone();
			}
		});
		panel1.add(createFromBoneBt);
		
		
		HorizontalPanel panel=new HorizontalPanel();
		root.add(panel);
		
		Button addBt=new Button("Add",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doAdd();
			}
		});
		panel.add(addBt);
		
		Button removeBt=new Button("Remove",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doRemove();
			}
		});
		panel.add(removeBt);
		
Button removeAllBt=new Button("Remove All",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doRemoveAll();
			}

			
		});
		panel.add(removeAllBt);
		
		HorizontalPanel buttons2=new HorizontalPanel();
		root.add(buttons2);
		final CheckBox drawBoundsCheck=new CheckBox("draw bounds");
		drawBoundsCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				drawBounds=event.getValue();
				updateCanvas();
			}
			
		});
		buttons2.add(drawBoundsCheck);
		
		final CheckBox drawSelectionOnlyCheck=new CheckBox("draw selection-only");
		drawSelectionOnlyCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				drawSelectionOnly=event.getValue();
				updateCanvas();
			}
			
		});
		buttons2.add(drawSelectionOnlyCheck);
		
		//no need preview,because without other-posing,image quality is best.no difference order.
		
		
		return root;
	}
	private boolean drawBounds;
	private boolean drawSelectionOnly;
	private void doRemoveAll() {
		if(!Window.confirm("remove all clip-image-data.are you sure?")){
			return;
		}
		cellObjects.clearAllItems();
		updateCanvas();
	}
	
	
	/*
	 * create each bone area without locked,root is specially creating circle area
	 */
	protected void doCreateClipDataFromBone() {
		for(TwoDimensionBone rootBone:getRootBone().asSet()){
			List<TwoDimensionBone> bones=BoneUtils.getAllBone(rootBone);
			for(TwoDimensionBone bone:bones){
				if(bone.getParent()!=null && !bone.isLocked()){
					PointXY selfPoint=boneControler.getBoneInitialPosition(bone).get();
					PointXY parentPoint=boneControler.getBoneInitialPosition(bone.getParent()).get();
					if(selfPoint!=null && parentPoint!=null){
						BoxPoints box=new BoxPoints(selfPoint,parentPoint,20);
						ClipData clip=new ClipData();
						clip.setPoints(box.getPoints());
						clip.setBone(bone.getName());
						cellObjects.addItem(clip);
					}
				}else if(bone.getParent()==null && !bone.isLocked()){
					//Bone is circle
					PointXY selfPoint=boneControler.getBoneInitialPosition(bone).get();
					int width=40;
					int max=6;
					PointXY base=new PointXY(0,-width);
					ClipData clip=new ClipData();
					
					for(int i=0;i<max;i++){
						int angle=360/max;
						PointXY newPoint=BoneUtils.turnedAngle(base.copy(), angle*i);
						clip.addPoint(newPoint.incrementXY(selfPoint.x, selfPoint.y));
					}
					clip.setBone(bone.getName());
					cellObjects.addItem(clip);
					
				}
			}
			//BoneUtils.
		}
		updateCanvas();
	}


	protected void doRemove() {
		if(isClipDataSelected()){
			cellObjects.removeItem(getSelection());
			updateCanvas();
		}
	}


	protected void doRemovePoint() {
		if(isClipDataSelected() && selectionPt!=null){
			getSelection().getPoints().remove(selectionPt);
			updateCanvas();
		}
	}
	protected void doAdd() {
		
		ClipData data=new ClipData();
		
		for(TwoDimensionBone bone:getRootBone().asSet()){
			
			data.setBone(bone.getName());
		}
		
		cellObjects.addItem(data);
		
		
		cellObjects.setSelected(data, true);
		
		updateCanvas();
	}

	public ClipData getSelection(){
		return cellObjects.getSelection();
	}

	public boolean isClipDataSelected(){
		return getSelection()!=null;
	}
	
	//private CanvasBonePainter painter;
	//private CanvasBoneSettings settings;
	//private BonePositionControler bonePositionControler;
	
	private BoneControler boneControler;
	protected Widget createCenterPanel() {
		/**
		 * don't insert above,should canvas initialize first.
		 */
		VerticalPanel panel=new VerticalPanel();
		
		panel.add(createClipButtons());
		panel.add(createBoneButtons());
		panel.add(createBackgroundButtons());
		initializeCanvas();
		panel.add(canvas);
		
		boneControler =new BoneControler(canvas){
			@Override
			public String getSelectionName() {
				return getSelection()!=null?getSelection().getBone():null;
			}
		};
		
		
		
		
		
		
		canvas.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				doDoubleClick(event.getX(), event.getY());
			}

			
		});
		
		drawingDataControlers=Lists.newArrayList();
		
		background = new Background();
		background.setEditable(false);
		
		
		clipDrawingDataControler = new ClipDrawingDataControler();
		drawingDataControlers.add(clipDrawingDataControler);
		
		//modify background
		ImageDrawingDataControler controler=new ImageDrawingDataControler(background);
		drawingDataControlers.add(controler);
		
		
		
		return panel;
	}

	private void doDoubleClick(int x,int y) {
		if(!isClipDataSelected()){
			return;
		}
		
		PointXY pt=new PointXY(x,y);
		getSelection().addPoint(pt);
		
		selectionPt=pt;
		
		//TODO method setActive
		activeDataControler=clipDrawingDataControler;
		
		updateCanvas();			
		}
	
	protected Widget createWestPanel() {
		VerticalPanel panel=new VerticalPanel();
		panel.add(new Label("Clips"));
		
		
		panel.add(createClipControlPanel());
		
		VerticalPanel editorPanel=new VerticalPanel();
		
		
		editor = new ClipDataEditor();    
		
		driver = GWT.create(Driver.class);
		driver.initialize(editor);
		
		driver.edit(new ClipData());//dummy
		editorPanel.add(editor);
		
		/*
	    Button updateBt=new Button("Update",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ClipData data=driver.flush();
				//TODO
				System.out.println(data);
			}
		});
	    editorPanel.add(updateBt);
	    */
	   

	   
	
	//create easy cell tables
	SimpleCellTable<ClipData> table=new SimpleCellTable<ClipData>(999) {
		@Override
		public void addColumns(CellTable<ClipData> table) {
			TextColumn<ClipData> nameColumn=new TextColumn<ClipData>() {
				@Override
				public String getValue(ClipData object) {
					return Strings.isNullOrEmpty(object.getBone())?"NONE":object.getBone();
				}
			};
			table.addColumn(nameColumn);
			
			TextColumn<ClipData> hasLinkColumn=new TextColumn<ClipData>() {
				@Override
				public String getValue(ClipData object) {
					for(ImageDrawingData data:object.getLinkedImageDrawingData().asSet()){
						return "yes";
					}
					return "";
				}
			};
			table.addColumn(hasLinkColumn,"has texture");
		}
	};
	
	cellObjects = new EasyCellTableObjects<ClipData>(table,false){
		@Override
		public void onSelect(ClipData selection) {
			
			driver.edit(selection);
			
			updateCanvas();
		}};
		
		ScrollPanel scroll=new ScrollPanel();
		scroll.setHeight("400px");
		panel.add(scroll);
		
		scroll.add(table);
	
	
	 panel.add(editorPanel);
	 
	 panel.add(new Label("export selection image"));
	 
	 HorizontalPanel h3=new HorizontalPanel();
	 panel.add(h3);
	 final HorizontalPanel downloadLinks=new HorizontalPanel();
	 
	 Button exportSelection=new Button("selection only",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(!isClipDataSelected()){
				return;
			}
			String dataUrl=generateClippedImage(getSelection());
			Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-clip.png", "clip", true);
			downloadLinks.add(a);
			
			//test position
			ImageDrawingData data=convertToImageDrawingData(getSelection());
			data.draw(canvas);
			
			
			
		}
	});
	 h3.add(exportSelection);
	 
	 
	 panel.add(new Label("Export as texture-data(contain clip-data)"));
	 
	 HorizontalPanel h4=new HorizontalPanel();
	 panel.add(h4);
	 Button syncAsTexture=new ExecuteButton("sync datas"){
			
		 @Override
			public void beforeExecute() {
			 downloadLinks.clear();
		 }
		 
		@Override
		public void executeOnClick() {
			final TextureData textureData=toTextureData();
			manager.getFileManagerBar().setTexture("clip-editor", textureData);
			
			ClipImageData clipData=generateSaveData();
			manager.getFileManagerBar().setClipImageData("clip-editor", clipData);
		}
		 
	 };
	 h4.add(syncAsTexture);
	 
	 
	 Button exportAsTexture=new ExecuteButton("sync & save"){
		
		 @Override
			public void beforeExecute() {
			 downloadLinks.clear();
		 }
		 
		@Override
		public void executeOnClick() {
			Stopwatch watch=Stopwatch.createStarted();
			final TextureData textureData=toTextureData();
			TextureDataConverter converter=new TextureDataConverter();
			JSZip jszip=converter.reverse().convert(textureData);
			
			ClipImageData clipData=generateSaveData();
			//addition clip-data
			new ClipImageDataConverter().convertToJsZip(jszip, clipData);
			
			
			downloadLinks.add(JSZipUtils.createDownloadAnchor(jszip, "2dbone-clips-textures.zip", "download", true));
		
			manager.getFileManagerBar().setTexture("clip-editor", textureData);
			manager.getFileManagerBar().setClipImageData("clip-editor", clipData);
			double time=watch.elapsed(TimeUnit.MILLISECONDS);
			LogUtils.log("clip-zip generation-millisecond"+time);
		}
		 
	 };
	 h4.add(exportAsTexture);
	 
	 HorizontalPanel h5=new HorizontalPanel();
	 panel.add(h5);
	 Button transparent=new ExecuteButton("transparent"){
			
		 @Override
			public void beforeExecute() {
			 //downloadLinks.clear();
		 }
		 
		@Override
		public void executeOnClick() {
			
			doTransparent();
			doSaveData();
		}
		 
	 };
	 h5.add(transparent);
	 
	 Button transparentSelection=new ExecuteButton("transparent selection"){
			
		 @Override
			public void beforeExecute() {
			 //downloadLinks.clear();
		 }
		 
		@Override
		public void executeOnClick() {
			if(isClipDataSelected()){
				doTransparent(getSelection());
				//added data's order is invalid,fix it
				doSyncTextureOrder();
				
				doSaveData();
				
				manager.selectTab(MainManager.TransparentPageIndex);
			}else{
				LogUtils.log("clip-data not selected.skipped");
			}
		}
		 
	 };
	 
	 
	 h5.add(transparentSelection);
	 
	
	
	 panel.add(downloadLinks);
	 
		return panel;
	}
	

	
	public class TransparentData{
		ImageDrawingData imageDrawingData;
		String imageSrc;
		PointShape pointShape;
		//TODO add bone
	}
	
	public class ClipDataToTransparentDataFunction implements Function<ClipData, TransparentData>{
		private TextureData textureData;
		public ClipDataToTransparentDataFunction(@Nullable TextureData textureData) {
			super();
			this.textureData = textureData;
		}
		@Override
		public TransparentData apply(ClipData clip) {
			TransparentData transParentData=new TransparentData();
			ImageDrawingData imageDrawingData=convertToImageDrawingData(clip);
			
			//use id as image,that why id must be uniq
			String clippedImageSrc=imageDrawingData.getImageElement().getSrc();//clipped image
			
			String notClippedSrc=generateClippedImage(clip,false);
			imageDrawingData.getImageElement().setSrc(notClippedSrc);//in here imagedrawing using data need clipping but transparent need not clippling data.
			
			//linked data is set by loading compare both id.FUTURE change by editor
			for(ImageDrawingData linked:clip.getLinkedImageDrawingData().asSet()){
				IntRect clipBound=clip.getBounds();
				if(clip.getBounds().equals(linked.getBounds())){
					LogUtils.log("same no need convert");
					//same size,no need care
					clippedImageSrc=linked.getImageElement().getSrc();
				}else{
					
					Canvas canvas=Graphics.createCanvas().copyToSizeOnly(clip.getBounds().toRect()).getCanvas();
					//offset
					ImageDrawingData textureImage=linked.copy();
					textureImage.incrementXY(clipBound.toRect().getXY().inverse());
					textureImage.draw(canvas);
					//LogUtils.log("converted:"+linked.getBounds()+","+clip.getBounds()+",xy:"+textureImage.getX()+","+textureImage.getY());
					clippedImageSrc=canvas.toDataUrl();
				}
			}
			
			/*
			if(textureData!=null){
				for(ImageDrawingData textureImage:textureData.findDataById(imageDrawingData.getId()).asSet()){
					if(ImageElementUtils.isSameSize(imageDrawingData.getImageElement(),textureImage.getImageElement())){
						//possible texturedata created by clipped-data,use old texture data
						clippedImageSrc=textureImage.getImageElement().getSrc();
						}
				}
			}*/
			
			PointShape pointShape=new ClipDataToShapeFunction().apply(clip);
			
			transParentData.imageDrawingData=imageDrawingData;
			transParentData.pointShape=pointShape;
			transParentData.imageSrc=clippedImageSrc;
			
			//transparentItPage.addItem(data, clippedImageSrc,pointShape);
			return transParentData;
		}
	}
	

	
	protected void doTransparent(final ClipData clipData) {

		TextureData textureData=manager.getTextureData();
		transparentItPage.removeItemById(clipData.getId());
		TransparentData data=new ClipDataToTransparentDataFunction(textureData).apply(clipData);
		transparentItPage.addItem(new Supplier<String>() {
			
			@Override
			public String get() {
				// TODO Auto-generated method stub
				return clipData.getId();
			}
		},data.imageDrawingData, data.imageSrc,data.pointShape);
		
	}
	
	protected void doTransparent() {

		TextureData textureData=manager.getUploadedFileManager().getTextureData();
		List<ImageDrawingData> datas=Lists.newArrayList();
		
		transparentItPage.clearAll();
		
		//check duplicate bone-name
		boolean duplicateBoneName=false;
		List<String> names=Lists.newArrayList();
		for(ClipData clip:cellObjects.getDatas()){
			if(names.indexOf(clip.getId())!=-1){
				duplicateBoneName=true;
				break;
			}
			names.add(clip.getId());
		}
		if(duplicateBoneName){
			Window.alert("same id exist.maybe something wrong.");
		}
		
		//idea should do id with bone-name + bounds?
		
		for(final ClipData clip:cellObjects.getDatas()){
			//now only one image per bone support.
			ImageDrawingData data=convertToImageDrawingData(clip);
			datas.add(data);
			//TODO change uniq id & name.id not empty
			data.setImageName(data.getId()+".png");
			
			
			
			String src=data.getImageElement().getSrc();
			String notClipped=generateClippedImage(clip,false);
			data.getImageElement().setSrc(notClipped);//in here imagedrawing using data need clipping but transparent need not clippling data.
			if(textureData!=null){
				//ImageDrawingData pastData=null;
				for(ImageDrawingData textureImage:textureData.getImageDrawingDatas()){
					if(textureImage.getId().equals(data.getId())){
						//use texture image as start image
						
						//if same size,no problem.
						if(ImageElementUtils.isSameSize(data.getImageElement(),textureImage.getImageElement())){
						src=textureImage.getImageElement().getSrc();
						break;
						}
					}
				}
			}
			PointShape pointShape=new ClipDataToShapeFunction().apply(clip);
			transparentItPage.addItem(new Supplier<String>() {
				
				@Override
				public String get() {
					// TODO Auto-generated method stub
					return  clip.getId();
				}
			},data, src,pointShape);
			manager.selectTab(MainManager.TransparentPageIndex);
		}
		
	}
	


	private TextureData toTextureData(){
		List<ImageDrawingData> datas=Lists.newArrayList();
		for(ClipData clip:cellObjects.getDatas()){
			ImageDrawingData data=convertToImageDrawingData(clip);
			datas.add(data);
			
			//TODO change uniq id & name.id not empty
			data.setImageName(data.getId()+".png");
		}
		TextureData textureData=new TextureData();
		textureData.setBone(boneControler.getBone());
		textureData.setImageDrawingDatas(datas);
		return textureData;
	}
	


	public ImageDrawingData convertToImageDrawingData(ClipData clip){
		IntRect rect=clip.getPointBound();
		rect.expandSelf(clip.getExpand(), clip.getExpand());
		PointD pt=rect.getCenterPoint();
		ImageElement element=ImageElementUtils.create(generateClippedImage(clip));
		
		
		ImageDrawingData data=new ImageDrawingData(clip.getId(), element);
		data.setBoneName(clip.getBone());
		data.setX((int)pt.getX());
		data.setY((int)pt.getY());
		
		data.setImageName(data.getId()+".png");
		
	//	data.incrementX(canvas.getCoordinateSpaceWidth()/2);
	//	data.incrementY(canvas.getCoordinateSpaceHeight()/2);
		
		return data;
	}
	


	protected String generateClippedImage(ClipData selection){
		return generateClippedImage(selection,true);
	}
	
	protected String generateClippedImage(ClipData selection,boolean clip) {
		//int expand=64;
		
		
		IntRect rect=selection.getPointBound();
		rect.expandSelf(selection.getExpand(), selection.getExpand());
		Canvas clipCanvas=CanvasUtils.createCanvas(rect.getWidth(), rect.getHeight());
		Context2d context=clipCanvas.getContext2d();
		
		if(clip){
		List<PointXY> newPoints=Lists.newArrayList();
		for(PointXY pt:selection.getPoints()){
			newPoints.add(pt.copy().incrementXY(-rect.getX(), -rect.getY()));
		}
		
		
		context.beginPath();
		
		
		context.moveTo(newPoints.get(0).x, newPoints.get(0).y);
		for(int i=1;i<newPoints.size();i++){
			context.lineTo(newPoints.get(i).x, newPoints.get(i).y);
		}
		context.lineTo(newPoints.get(0).x, newPoints.get(0).y);
		context.closePath();
		
		context.clip();
		}
		
		ImageElement bg=ImageElementUtils.create(generateBackgroundImage());
		context.drawImage(bg, -rect.getX(), -rect.getY());
		return clipCanvas.toDataUrl();
	}

	

	@Override
	protected void onBoneAndAnimationChanged(BoneAndAnimationData boneAndAnimationData) {
		
		//not support animation-yet
		
		TwoDimensionBone bone=boneAndAnimationData.getBone();
		
		List<TwoDimensionBone> bones=BoneUtils.getAllBone(bone);
		
		for(ClipData data:cellObjects.getDatas()){
			if(data.getBone()==null || BoneUtils.findBoneByName(bones,data.getBone())==null){
				//invalid bone use root
				data.setBone(bone.getName());
			}
		}
		
		/*
		if(!isClipDataSelected()){
			driver.edit(new ClipData());//set dummy
		}
		*/
		
		editor.setBones(bones);
		boneControler.setBone(bone);
		updateCanvas();
	}





	@Override
	protected void onBackgroundChanged(ImageDrawingData bg) {
		background.setBackgroundData(bg);
		//not select
		updateCanvas();
	}





	@Override
	protected void updateDatas() {
		cellObjects.update();
	}


	public Optional<TwoDimensionBone> getRootBone(){
		
		return Optional.fromNullable(boneControler.getBone());
	}


	private void drawBackground(Canvas canvas){

		canvas.getContext2d().save();
		
		
		//there are 
		if(drawMode==BOTH){
			background.draw(canvas);
		}else if(drawMode==OUTSIDE){
			background.draw(canvas);
			canvas.getContext2d().setGlobalCompositeOperation("destination-out");
			canvas.getContext2d().setFillStyle("#000");
			
			
			for(ClipData data:cellObjects.getDatas()){
				List<PointXY> pts=data.getPoints();
				if(data.getPoints().size()>2){
					canvas.getContext2d().beginPath();
					canvas.getContext2d().moveTo(pts.get(pts.size()-1).getX(), pts.get(pts.size()-1).getY());
					for(PointXY pt:data.getPoints()){
						canvas.getContext2d().lineTo(pt.getX(),pt.getY());
					}
					canvas.getContext2d().closePath();
					canvas.getContext2d().fill();
				}
				
			}
			
		}else if(drawMode==INSIDE){
			for(ClipData data:cellObjects.getDatas()){
				List<PointXY> pts=data.getPoints();
				if(data.getPoints().size()>2){
					canvas.getContext2d().beginPath();
					canvas.getContext2d().moveTo(pts.get(pts.size()-1).getX(), pts.get(pts.size()-1).getY());
					for(PointXY pt:data.getPoints()){
						canvas.getContext2d().lineTo(pt.getX(),pt.getY());
					}
					canvas.getContext2d().closePath();
					canvas.getContext2d().save();
					canvas.getContext2d().clip();
					background.draw(canvas);
					canvas.getContext2d().restore();
				}
				
			}
		}
		
		canvas.getContext2d().restore();
		
	}
	
	@Override
	protected void updateCanvas() {
		CanvasUtils.clear(canvas);
		
		ClipData selection=getSelection();
		
		
		drawBackground(canvas);
		
		boneControler.paintBone();
		
		for(ClipData data:cellObjects.getDatas()){
			
			//draw selection first
			if(data==selection){
				int dotSize=10;
				
				for(PointXY pt:data.getPoints()){
					String rectColor="#0a0";
					if(pt==data.getPoints().get(data.getPoints().size()-1)){
						rectColor="#0f0";
					}
					IntRect rect=IntRect.fromCenterPoint(pt.getX(), pt.getY(), dotSize/2, dotSize/2);
					RectCanvasUtils.fill(rect, canvas, rectColor);
					
					if(pt==selectionPt){
						rect.expandSelf(8,8);
						RectCanvasUtils.stroke(rect, canvas, "#000");
						
						
						//draw insert
						int index=data.getPoints().indexOf(selectionPt);
						if(index!=-1 && index<data.getPoints().size()){
							int at=index+1;
							
							if(index == data.getPoints().size()-1){
								at=0;
							}
							
							int x=selectionPt.x + data.getPoints().get(at).x;
							int y=selectionPt.y + data.getPoints().get(at).y;
							PointXY insertPt=new PointXY(x/2, y/2);
							
							IntRect insertRect=IntRect.fromCenterPoint(insertPt.getX(), insertPt.getY(), dotSize/2, dotSize/2);
							RectCanvasUtils.stroke(insertRect, canvas, "#888");
						}
						
					}
					
				}
			}
			
			
			
			String color="#888";
			if(data==selection){
				color="#00f";
			}
			canvas.getContext2d().setStrokeStyle(color);
			if(data==selection || !drawSelectionOnly){
			for(int i=0;i<data.getPoints().size()-1;i++){
				int x1=data.getPoints().get(i).getX();
				int y1=data.getPoints().get(i).getY();
				int x2=data.getPoints().get(i+1).getX();
				int y2=data.getPoints().get(i+1).getY();
				CanvasUtils.drawLine(canvas, x1, y1, x2, y2);
			}
			
			//close
			if(data.getPoints().size()>2){
				int x1=data.getPoints().get(0).getX();
				int y1=data.getPoints().get(0).getY();
				int x2=data.getPoints().get(data.getPoints().size()-1).getX();
				int y2=data.getPoints().get(data.getPoints().size()-1).getY();
				CanvasUtils.drawLine(canvas, x1, y1, x2, y2);
			}
			
			if(data.getPoints().size()==1){
				int x1=data.getPoints().get(0).getX();
				int y1=data.getPoints().get(0).getY();
			
				CanvasUtils.fillPoint(canvas, x1, y1);
			}
			}
			
			//stroke bounds
			if(data==selection || !drawSelectionOnly){
			if(data.getPoints().size()>2 && drawBounds){
			IntRect boundRect=IntRect.fromPoints(data.getPoints());
			int expand=data.getExpand();
			boundRect.expandSelf(expand, expand);
			RectCanvasUtils.stroke(boundRect, canvas, "#444");
			}
			}
			
		
			
		}
		
		//bone
		
			
		
	}





	@Override
	protected void onCanvasTouchEnd(int sx, int sy) {
		//selectionPt=null;
		if(activeDataControler!=null){
			activeDataControler.onTouchEnd(sx, sy);
		}
	}




	PointXY selectionPt; //need insertPoint;
	private ClipDataEditor editor;

	List<CanvasDrawingDataControler> drawingDataControlers;
	private CanvasDrawingDataControler activeDataControler;
	private Background background;
	@Override
	protected void onCanvasTouchStart(int sx, int sy) {
		
		CanvasDrawingDataControler active=null;
		for(CanvasDrawingDataControler data:drawingDataControlers){
			if(data.onTouchStart(sx, sy)){
				active=data;
				break;
			}
		}
		activeDataControler=active;
		updateCanvas();
		
		
		//LogUtils.log(activeDataControler!=null?activeDataControler.getName():"null-active");
		//updateCanvas();
		
		
		
	}





	@Override
	protected void onCanvasDragged(int vectorX, int vectorY) {
		if(activeDataControler!=null){
			activeDataControler.onTouchDragged(vectorX, vectorY, canvasControler.isRightMouse());
			updateCanvas();
		}
		
		
		//TODO 
		/*
		if(!isClipDataSelected()){
			return;
		}
		
		if(selectionPt!=null){
			selectionPt.incrementX(vectorX);
			selectionPt.incrementY(vectorY);
			updateCanvas();
		}
		*/
	}


	public class ClipDrawingDataControler implements CanvasDrawingDataControler{

		@Override
		public void onWhelled(int delta, boolean shiftDowned) {
			
			if(!isClipDataSelected()){
				
				return;
			}
			
			if(getSelection().getPoints().isEmpty()){
				return;
			}
			
			int direction=1;
			if(delta<0){
				direction=-1;
			}
			PointXY pt=null;
			if(selectionPt==null){//sadly never happen,so far .CanvasDrawingDataControler selected when clicked.
				//LogUtils.log("select first");
				pt=getSelection().getPoints().get(0);//select first
			}else{
				int index=getSelection().getPoints().indexOf(selectionPt);
				if(index==-1){
					LogUtils.log("somehow invalid selection.");
					return;
				}
				index+=direction;
				if(index<0){
					index=getSelection().getPoints().size()-1;
				}else if(index>=getSelection().getPoints().size()){
					index=0;
				}
				pt=getSelection().getPoints().get(index);
			}
			selectionPt=pt;
		}

		@Override
		public void onTouchDragged(int vectorX, int vectorY, boolean rightButton) {
			if(!isClipDataSelected()){
				return;
			}
			
			if(selectionPt!=null){
				selectionPt.incrementX(vectorX);
				selectionPt.incrementY(vectorY);
			}
		}

		@Override
		public boolean onTouchStart(int mx, int my) {
			if(!isClipDataSelected()){
				selectionPt=null;
				return false;
			}
			
			boolean rightMouse=canvasControler.isRightMouse();
			if(rightMouse){
				if(selectionPt==null){
					return false;
				}
				
				for(PointXY pt:execInsertPoint(getSelection(),selectionPt,new PointXY(mx,my)).asSet()){
					selectionPt=pt;
					return true;
				}
				
				LogUtils.log("somehow can't insert point");
				return false;//
				
			}else{
			
			selectionPt=getSelection().collision(mx, my);
			
			return selectionPt!=null;
			}
		}

		@Override
		public void onTouchEnd(int mx, int my) {
			//do nothing
		}

		@Override
		public String getName() {
			return "ClipImagePage";
		}
		
	}



	@Override
	protected void onCanvasWheeled(int delta, boolean shiftDown) {
		if(activeDataControler!=null){
			activeDataControler.onWhelled(delta, shiftDown);
			updateCanvas();
		}
	}



	public class ClipDataEditor extends VerticalPanel implements Editor<ClipData>,ValueAwareEditor<ClipData>{
		ClipData value;
		private BoneListBox boneList;
		public ClipDataEditor(){
			
			HorizontalPanel movePanel=new HorizontalPanel();
			movePanel.add(new Button("Top",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(value==null){
						return;
					}
					cellObjects.topItem(value);
					updateCanvas();
				}
			}));
			movePanel.add(new Button("Up",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(value==null){
						return;
					}
					cellObjects.upItem(value);
					updateCanvas();
				}
			}));
			movePanel.add(new Button("Down",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(value==null){
						return;
					}
					cellObjects.downItem(value);
					updateCanvas();
				}
			}));
			movePanel.add(new Button("Bottom",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(value==null){
						return;
					}
					cellObjects.bottomItem(value);
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
			
			add(new Label("high-Green dot is last point.insert at white box"));
			
			HorizontalPanel panel=new HorizontalPanel();
			panel.setVerticalAlignment(ALIGN_MIDDLE);
			Button insertPointBt=new Button("Insert Point",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doInsertPoint();
				}
			});
			panel.add(insertPointBt);
			
			Button removePointBt=new Button("Remove Point",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doRemovePoint();
				}
			});
			panel.add(removePointBt);
			add(panel);
			
			
			HorizontalPanel h1=new HorizontalPanel();
			h1.setVerticalAlignment(ALIGN_MIDDLE);
			h1.add(new Label("bone:"));
			add(h1);
			
			boneList=new BoneListBox();
			boneList.addValueChangeHandler(new ValueChangeHandler<TwoDimensionBone>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<TwoDimensionBone> event) {
					flush();
				}
			});
			h1.add(boneList);
			boneList.setEnabled(false);
			
			
			
		}
		
		

		protected void doInsertPoint() {
			if(!isClipDataSelected() || selectionPt==null){
				return;
			}
			int index=value.getPoints().indexOf(selectionPt);
			if(index!=-1 && index<value.getPoints().size()){
				int at=index+1;
				
				if(index == value.getPoints().size()-1){
					at=0;
				}
				
				int x=selectionPt.x + value.getPoints().get(at).x;
				int y=selectionPt.y + value.getPoints().get(at).y;
				PointXY pt=new PointXY(x/2, y/2);
				value.getPoints().add(index+1, pt);
				selectionPt=pt;
				updateCanvas();
			}
		}

		@Override
			public void setDelegate(EditorDelegate<ClipData> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				LogUtils.log("flush");
				if(value==null){
					return;
				}
				value.setBone(boneList.getValue().getName());
				
				updateDatas();
				updateCanvas();
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValue(ClipData value) {
				this.value=value;
				if(value==null){
					boneList.setEnabled(false);
					return;
				}else{
					boneList.setEnabled(true);
				}
				
				//is this heavy?
				TwoDimensionBone bone=findBoneByName(value.getBone());
				
				boneList.setValue(bone);
			}
			
			
			public void setBones(List<TwoDimensionBone> bones){
			
				if(value!=null && value.getBone()==null){
					value.setBone(bones.get(0).getName());//for initial dummy-data	
				}
				
				if(value!=null){
					boneList.setValue(findBoneByName(value.getBone()));//must modified before change bone-list
				}else{
					boneList.setValue(bones.get(0));
				}
				
				boneList.setAcceptableValues(bones);//when value is null
				
			}
	}
	
	private Optional<PointXY> execInsertPoint(ClipData value,PointXY target,@Nullable PointXY newPoint){
		int index=value.getPoints().indexOf(target);
		if(index!=-1 && index<value.getPoints().size()){
			int at=index+1;
			
			if(index == value.getPoints().size()-1){
				at=0;
			}
			
			if(newPoint==null){//center point
			int x=selectionPt.x + value.getPoints().get(at).x;
			int y=selectionPt.y + value.getPoints().get(at).y;
			newPoint=new PointXY(x/2, y/2);
			}
			
			value.getPoints().add(index+1, newPoint);
			
			return Optional.of(newPoint);
		}
		return Optional.absent();
	}
	
	protected void doSyncTextureOrder() {
		List<String> names=FluentIterable.from(cellObjects.getDatas()).transform(new Function<ClipData,String>(){
			@Override
			public String apply(ClipData input) {
				return input.getId();
			}}).toList();
		manager.setTextureOrder(names, this);
	}
	
	private HorizontalPanel downloadLinks;
	
	private TwoDimensionBone findBoneByName(String name){
		if(name==null){
			return null;
		}
		
		for(TwoDimensionBone bone:getRootBone().asSet()){
			return BoneUtils.findBoneByName(BoneUtils.getAllBone(bone), name);
		}
		
		return null;
	}
	
	private Widget createClipButtons(){
		HorizontalPanel panel=new HorizontalPanel();
	    panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	
	    panel.add(new Label("Clips:"));
	    
	    Button test=new Button("test",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//test deadly code
				String dataUrl=ImageBuilder.from(ImageElementUtils.create(canvas.toDataUrl())).onFileName("root").toDataUrl();
				LogUtils.log("fine");
				
			}
		});
	    panel.add(test);
	    
	    panel.add(new Label("Load:(Clips,Bone,Background)"));
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
	    
	   
	    
	   
	    
	    panel.add(load);
	    panel.add(new Button("Save(not contain texture)",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSaveData();
			}
		}));
	    
	    downloadLinks = new HorizontalPanel();
	    panel.add(downloadLinks);
		
	    return panel;
	}
	protected void doSaveData() {
		ClipImageData data=generateSaveData();
		JSZip zip=new ClipImageDataConverter().reverse().convert(data);
		downloadLinks.clear();
		downloadLinks.add(JSZipUtils.createDownloadAnchor(zip, "2dbone-clips.zip", "download", true));
		
		manager.getFileManagerBar().setClipImageData(getOwnerName(), data);
	}
	private ClipImageData generateSaveData(){
		ClipImageData data=new ClipImageData();
		
		//background
		data.setImageDrawingData(background.getBackgroundData());
		
		//bone
		for(TwoDimensionBone bone:getRootBone().asSet()){
			data.setBone(bone);
		}
		
		//clips
		for(ClipData clip:cellObjects.getDatas()){
			data.getClips().add(clip);
		}
		return data;
	}


	protected void onClipImageDataChanged(ClipImageData data){
		//LogUtils.log("onClipImageDataChanged:");
		//background and bone called if exist.
		
		cellObjects.clearAllItems();
		for(ClipData clip:data.getClips()){
			cellObjects.addItem(clip);
		}
		
		
		for(ClipData clip:cellObjects.getFirst().asSet()){
			cellObjects.setSelected(clip, true);
		}
		
		updateCanvas();
	}
	
	protected void doLoadData(String name, JSZip zip) {
		ClipImageData data=new ClipImageDataConverter().convert(zip);
		
		
		manager.getFileManagerBar().setClipImageData(name, data);
		
		
	}


	private Widget createBoneButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(new Label("Bone:"));
		
		CheckBox VisibleCheck = new CheckBox("Visible");
		panel.add(VisibleCheck);
		VisibleCheck.setValue(true);
		VisibleCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boneControler.setVisible(event.getValue());
				updateCanvas();
			}
		});
		
		return panel;
	}
	private Widget createBackgroundButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(new Label("Background:"));
		
		CheckBox backgroundEditCheck = new CheckBox("Editable");
		panel.add(backgroundEditCheck);
		backgroundEditCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				background.setEditable(event.getValue());
				updateCanvas();
			}
		});
		
		CheckBox VisibleCheck = new CheckBox("Visible");
		panel.add(VisibleCheck);
		VisibleCheck.setValue(true);
		VisibleCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				background.setVisible(event.getValue());
				updateCanvas();
			}
		});
		
		final HorizontalPanel downloadLinks=new HorizontalPanel();
		
		Button extractImageBt=new Button("Extract BG",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
				drawBackground(extractCanvas);
				//background.getBackgroundData().draw(extractCanvas);
				String dataUrl=extractCanvas.toDataUrl();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-bg.png", "background", true);
				downloadLinks.add(a);
				
				ImageDrawingData data=background.getBackgroundData();
				manager.getFileManagerBar().setBackground("clip-editor", data);
			}
		});
		panel.add(extractImageBt);
		
		panel.add(new Label("Mode"));
		
		RadioButton both=new RadioButton("draw-mode", "both");
		both.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				
					drawMode=BOTH;
					updateCanvas();
				
			}
		});
		RadioButton inside=new RadioButton("draw-mode", "inside");
		inside.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				
				
					drawMode=INSIDE;
					updateCanvas();
				
			}
		});
		RadioButton outside=new RadioButton("draw-mode", "outside");
		outside.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				
					drawMode=OUTSIDE;
					updateCanvas();
				
			}
		});
		panel.add(both);
		both.setValue(true);
		panel.add(inside);
		panel.add(outside);
		/*
		CheckBox clipCheck=new CheckBox("showClipedAreaOnly");
		panel.add(clipCheck);
		clipCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				showClipedAreaOnly=event.getValue();
				updateCanvas();
			}
		});
		*/
		/*
		Button extractRemainImageBt=new Button("Extract RemainBG",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
				background.getBackgroundData().draw(extractCanvas);
				
				extractCanvas.getContext2d().setFillStyle("#000");
				extractCanvas.getContext2d().beginPath();
				extractCanvas.getContext2d().setGlobalCompositeOperation("destination-out");
				for(ClipData data:cellObjects.getDatas()){
					List<PointXY> pts=data.getPoints();
					if(data.getPoints().size()>2){
						extractCanvas.getContext2d().moveTo(pts.get(pts.size()-1).getX(), pts.get(pts.size()-1).getY());
						for(PointXY pt:data.getPoints()){
							extractCanvas.getContext2d().lineTo(pt.getX(),pt.getY());
						}
					}
					
				}
				extractCanvas.getContext2d().closePath();
				extractCanvas.getContext2d().fill();
				
				
				String dataUrl=extractCanvas.toDataUrl();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-remain-bg.png", "background", true);
				downloadLinks.add(a);
				
				ImageDrawingData data=background.getBackgroundData();
				manager.getFileManagerBar().setBackground("clip-editor", data);
			}
		});
		panel.add(extractRemainImageBt);
		*/
		
		panel.add(downloadLinks);
		
		return panel;
	}
	private static final int BOTH=0;
	private static final int INSIDE=1;
	private static final int OUTSIDE=2;
	private int drawMode=BOTH;
	private boolean showClipedAreaOnly;
	private ClipDrawingDataControler clipDrawingDataControler;
	
	public String generateBackgroundImage(){
		Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
		background.getBackgroundData().draw(extractCanvas);
		String dataUrl=extractCanvas.toDataUrl();
		return dataUrl;
	}


	@Override
	protected void initialize() {
		manager.getTextureOrderSystem().addListener(new DataChangeListener<List<String>>() {
			@Override
			public void dataChanged(List<String> data, DataOwner owner) {
				onTextureOrderChanged(data,owner);
			}
		});
	}
	public Optional<ClipData> findDataById(String id){
		for(ClipData clip:cellObjects.getDatas()){
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
		List<ClipData> newDatas=Lists.newArrayList();
		
		for(String id:data){
			for(ClipData finded:findDataById(id).asSet()){
				cellObjects.getDatas().remove(finded);
				newDatas.add(finded);
			}
		}
		
		for(ClipData remain:cellObjects.getDatas()){
			newDatas.add(remain);
		}
		
		cellObjects.setDatas(newDatas);
		cellObjects.update();
		
	}

	@Override
	protected void onTextureDataChanged(TextureData textureData) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getOwnerName() {
		// TODO Auto-generated method stub
		return "Clip-Editor";
	}


	




	
	
}
