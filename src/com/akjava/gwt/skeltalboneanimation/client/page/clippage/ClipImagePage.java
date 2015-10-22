package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.jszip.client.JSZipUtils;
import com.akjava.gwt.jszip.client.JSZipUtils.ZipListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler.KeyDownState;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.lib.client.experimental.ExecuteWaitAsyncButton;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.lib.client.experimental.undo.UndoButtons;
import com.akjava.gwt.lib.client.game.PointD;
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
import com.akjava.gwt.skeltalboneanimation.client.functions.ClipDataToRelatedTextureIdFunction;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataChangeListener;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataOwner;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.BoneControler;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.CanvasDrawingDataControlCanvas;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.CanvasUpdater;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.ImageDrawingDatasUpdater;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.commands.DataUrlLoadExecuteButton;
import com.akjava.gwt.skeltalboneanimation.client.page.html5app.TransparentItPage;
import com.akjava.gwt.skeltalboneanimation.client.predicates.BonePredicates.NotExistInNames;
import com.akjava.gwt.skeltalboneanimation.client.predicates.BonePredicates.NotLocked;
import com.akjava.gwt.skeltalboneanimation.client.predicates.ImageDrawingDataPredicates.NotExistInIds;
import com.akjava.lib.common.graphics.Point;
import com.akjava.lib.common.graphics.Rect;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
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
import com.google.gwt.event.dom.client.ErrorEvent;
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

public class ClipImagePage extends AbstractPage implements DataOwner,ClipImageDataControler,CanvasUpdater,ImageDrawingDatasUpdater{
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
		
		//old one
		List<ClipData> oldDatas=ImmutableList.copyOf(cellObjects.getDatas());
		
		cellObjects.clearAllItems();
		
		
		updateCanvas();
		
		List<ClipData> newDatas=ImmutableList.of();
		
		undoControler.executeDataUpdate(oldDatas, newDatas);
	}
	
	
	/*
	 * create each bone area without locked,root is specially creating circle area
	 */
	protected void doCreateClipDataFromBone() {
		for(TwoDimensionBone rootBone:getRootBone().asSet()){
			
			List<ClipData> oldDatas=ImmutableList.copyOf(cellObjects.getDatas());
			List<String> existNames=FluentIterable.from(cellObjects.getDatas()).transform(new BoneNameTransfer()).toList();
			
			
			List<TwoDimensionBone> bones=BoneUtils.getAllBone(rootBone);
			
			//not exist and not locked
			List<TwoDimensionBone> filtered=FluentIterable.from(bones).filter(new NotLocked()).filter(new NotExistInNames(existNames)).toList();
			
			for(TwoDimensionBone bone:filtered){
				if(bone.getParent()!=null && !bone.isLocked()){
					Point selfPoint=boneControler.getBoneInitialPosition(bone).get().toPoint();
					Point parentPoint=boneControler.getBoneInitialPosition(bone.getParent()).get().toPoint();
					if(selfPoint!=null && parentPoint!=null){
						BoxPoints box=new BoxPoints(selfPoint,parentPoint,20);
						ClipData clip=new ClipData();
						clip.setPoints(box.getPoints());
						clip.setBone(bone.getName());
						cellObjects.addItem(clip);
					}
				}else if(bone.getParent()==null && !bone.isLocked()){
					//Bone is circle
					Point selfPoint=boneControler.getBoneInitialPosition(bone).get().toPoint();
					int width=40;
					int max=6;
					Point base=new Point(0,-width);
					ClipData clip=new ClipData();
					
					for(int i=0;i<max;i++){
						int angle=360/max;
						Point newPoint=BoneUtils.turnedAngle(base.copy(), angle*i);
						clip.addPoint(newPoint.incrementXY(selfPoint.x, selfPoint.y));
					}
					clip.setBone(bone.getName());
					cellObjects.addItem(clip);
					
				}else{
					//ignore locked;
				}
			}
			//these add data is new-created.and not exist old data.
			//if old data modified.when undo come here,every data is same as old data.
			List<ClipData> newDatas=ImmutableList.copyOf(cellObjects.getDatas());
			undoControler.executeDataUpdate(oldDatas, newDatas);
			//BoneUtils.
		}
		updateCanvas();
	}


	protected void doRemove() {
		//if(isClipDataSelected()){
			
			for(Integer index:cellObjects.getSelectedIndex(getSelection()).asSet()){
				undoControler.execRemoveData(index);
				//cellObjects.removeItem(getSelection());
				updateCanvas();
			}
			
		//}
	}

	private int pointSelectionIndex=-1;
	
	public Point getSelectedPoint(){
		if(pointSelectionIndex>=0 && pointSelectionIndex<getSelection().getPoints().size()){
			return getSelection().getPoints().get(pointSelectionIndex);
		}
		
		return null;
	}
	
	//remoing selectionPt field.

	private int getDataIndex(){
		checkNotNull(getSelection(),"getDataIndex:selection is null");
		return cellObjects.getDatas().indexOf(getSelection());
	}
	
	protected void doRemovePoint() {
		if(isClipDataSelected() && getSelectedPoint()!=null){
			
			int index=pointSelectionIndex;
			if(index!=-1){
				undoControler.execRemovePoint(getDataIndex(),index);
			}else{
				LogUtils.log("some how selection point exist");
			}
			updateCanvas();
		}
	}
	

	
	public static class BoneNameTransfer implements Function<ClipData,String>{

		@Override
		public String apply(ClipData input) {
			return input.getBone();
		}
		
	}
	
	public static class BoneTransfer implements Function<TwoDimensionBone,String>{

		@Override
		public String apply(TwoDimensionBone input) {
			return input.getName();
		}
		
	}
	
	public List<String> filterNotUsedBoneName(){
		for(TwoDimensionBone bone:getRootBone().asSet()){
			
			List<String> existNames=FluentIterable.from(cellObjects.getDatas()).transform(new BoneNameTransfer()).toList();
			
			List<TwoDimensionBone> bones=BoneUtils.getAllBone(bone);	
			return FluentIterable.from(bones).filter(new NotExistInNames(existNames)).transform(new BoneTransfer()).toList();
		}
		
		return Lists.newArrayList();
	}
	
	public List<String> getNotLockedAndNotUsedBoneName(){
		for(TwoDimensionBone bone:getRootBone().asSet()){
			
			List<String> existNames=FluentIterable.from(cellObjects.getDatas()).transform(new BoneNameTransfer()).toList();
			
			List<TwoDimensionBone> bones=BoneUtils.getAllBone(bone);	
			return FluentIterable.from(bones).filter(new NotLocked()).filter(new NotExistInNames(existNames)).transform(new BoneTransfer()).toList();
		}
		
		return Lists.newArrayList();
	}
	
	
	
	protected void doAdd() {
		
		ClipData data=new ClipData();
		
		
		List<String> names=getNotLockedAndNotUsedBoneName();
		
		if(!names.isEmpty()){
			data.setBone(names.get(0));
		}else{
			for(TwoDimensionBone bone:getRootBone().asSet()){
				data.setBone(bone.getName());
			}
		}
		
		
		
		
		
		undoControler.execAddData(cellObjects.getDatas().size(), data);
		cellObjects.setSelected(data, true);
		
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
	
	//protected CanvasDragMoveControler canvasControler;
	private BoneControler boneControler;
	private CanvasDrawingDataControlCanvas canvasDrawingDataControlCanvas;
	protected Widget createCenterPanel() {
		
		initializeCanvas();
		
		canvasDrawingDataControlCanvas=new CanvasDrawingDataControlCanvas(canvas,800,800,this);
		
		

		
		
		
		/**
		 * don't insert above,should canvas initialize first.
		 */
		VerticalPanel panel=new VerticalPanel();
		
		panel.add(createClipButtons());
		panel.add(createBoneButtons());
		panel.add(createBackgroundButtons());
		
		panel.add(canvasDrawingDataControlCanvas);
		
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
		
		//drawingDataControlers=Lists.newArrayList();
		
		background = new Background();
		background.setEditable(false);
		
		
		clipDrawingDataControler = new ClipDrawingDataControler();
		canvasDrawingDataControlCanvas.add(clipDrawingDataControler);
		
		//modify background
		ImageDrawingDataControler controler=new ImageDrawingDataControler(background);
		canvasDrawingDataControlCanvas.add(controler);
		
		controler.setUndoControler(undoControler,this);
		
		return panel;
	}

	//TODO merge in CanvasDrawingDataControlCanvas
	private void doDoubleClick(int x,int y) {
		if(!isClipDataSelected()){
			return;
		}
		x/=canvasDrawingDataControlCanvas.getScale();
		y/=canvasDrawingDataControlCanvas.getScale();
		int index=getSelection().getPoints().size();
		Point p=undoControler.execAddPoint(getDataIndex(),index, x, y);
		
		//Point pt=new Point(p);
		
		
		
		
		
		pointSelectionIndex=index;
		//selectionPt=pt;
		
		canvasDrawingDataControlCanvas.setActiveDataControler(clipDrawingDataControler);
		
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
			setPointUnselected();
			
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
	 
	 Button exportSelection=new DataUrlLoadExecuteButton("selection only",backgroundDataUrlSupplier){

		@Override
		public void executeOnClick() {
			if(!isClipDataSelected()){
				return;
			}
			initializeBackgroundDataUrl();
			
			
			String dataUrl=generateClippedImage(getSelection());
			Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-clip.png", "clip", true);
			downloadLinks.add(a);
			
			//test position
			ImageDrawingData data=convertToImageDrawingDataNoId(getSelection());
			data.draw(canvas);
		}
		 
	 };
	 

	 h3.add(exportSelection);
	 
	 
	 panel.add(new Label("Export as texture-data(contain clip-data)"));
	 
	 HorizontalPanel h4=new HorizontalPanel();
	 panel.add(h4);
	 clipToTextureButton = new DataUrlLoadExecuteButton("clip to texture & save",backgroundDataUrlSupplier){
			
		 @Override
			public void beforeExecute() {
			 super.beforeExecute();
			 downloadLinks.clear();
		 }
		 
		@Override
		public void executeOnClick() {
			if(manager.getTextureDataWithNewestBone()!=null){
				boolean confirm=Window.confirm("this crop images replace existing texture.are you sure?");
				if(!confirm){
					return;
				}
			}
			
			final TextureData textureData=toTextureData();
			manager.getFileManagerBar().setTexture("clip-editor", textureData);
			
			ClipImageData clipData=generateSaveData();
			manager.getFileManagerBar().setClipImageData("clip-editor", clipData);
			
		
			
			
			
		}
		 
	 };
	 h4.add(clipToTextureButton);
	 
	 /*
	 Button exportAsTexture=new ExecuteButton("clip to texture & save"){
		
		 @Override
			public void beforeExecute() {
			
			 downloadLinks.clear();
		 }
		 
		@Override
		public void executeOnClick() {
			if(manager.getTextureDataWithNewestBone()!=null){
				boolean confirm=Window.confirm("this crop images replace existing texture.are you sure?");
				if(!confirm){
					return;
				}
			}
			
			Stopwatch watch=Stopwatch.createStarted();
			final TextureData textureData=toTextureData();
			TextureDataConverter converter=new TextureDataConverter();
			JSZip jszip=converter.reverse().convert(textureData);
			
			ClipImageData clipData=generateSaveData();
			//no need sync texture data,because alerady generate new texture and would replace it later
			
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
	 */
	 
	 HorizontalPanel h5=new HorizontalPanel();
	 panel.add(h5);
	 Button transparent=new DataUrlLoadExecuteButton("transparent",backgroundDataUrlSupplier){
			
		
		@Override
		public void executeOnClick() {
			doTransparent();
			doSaveData();
		}
		 
	 };
	 h5.add(transparent);
	 
	 Button transparentSelection=new DataUrlLoadExecuteButton("transparent selection",backgroundDataUrlSupplier) {
		 
		@Override
		public void executeOnClick() {
			if(isClipDataSelected()){
				
				if(!background.hasBackgroundData()){
					Window.alert("no background image.quit");
					return;
				}
				
				//LogUtils.log(getSelection().getBounds());
				TextureData textureData=manager.getTextureDataWithNewestBone();
				boolean success=doTransparent(textureData,getSelection());
				
				if(!success){
					Window.alert("some clip is empty and ignored");
					return;
				}
				
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
	
	/**
	 * initialize on initialize() because of super class,this call initializeBackgroundDataUrl();
	 */
	private Supplier<String> backgroundDataUrlSupplier;
	
	/**
	 * for speed up & some case generateBackgroundImage broken
	 */
	private String initializeBackgroundDataUrl(){
		LogUtils.log("initializeBackgroundDataUrl");
		backgroundDataUrl=generateBackgroundImage();
		return backgroundDataUrl;
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
			ImageDrawingData imageDrawingData=convertToImageDrawingDataNoId(clip);
			
			//use id as image,that why id must be uniq
			String clippedImageSrc=imageDrawingData.getImageElement().getSrc();//clipped image
			
			String notClippedSrc=generateClippedImage(clip,false);
			imageDrawingData.getImageElement().setSrc(notClippedSrc);//in here imagedrawing using data need clipping but transparent need not clippling data.
			
			//linked data is set by loading compare both id.FUTURE change by editor
			for(ImageDrawingData linked:clip.getLinkedImageDrawingData().asSet()){
				Rect clipBound=clip.getBounds();
				if(clip.getBounds().equals(linked.getBounds())){
					//LogUtils.log("same no need convert");
					//same size,no need care
					clippedImageSrc=linked.getImageElement().getSrc();
				}else{
					
					Canvas canvas=Graphics.createCanvas().copyToSizeOnly(clip.getBounds()).getCanvas();
					//offset
					ImageDrawingData textureImage=linked.copy();
					textureImage.incrementXY(clipBound.getXY().inverse());
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
	

	
	protected boolean doTransparent(TextureData textureData,final ClipData original) {
		
		
		if(original.isPointsEmpty()){
			LogUtils.log("doTransparent:empty clip");
			return false;
		}
		
		ClipData clipData=original.copy(true);
		
		
	
		
		TransparentData data=new ClipDataToTransparentDataFunction(textureData).apply(clipData);
		
		
		transparentItPage.removeItemById(clipData.getId());

		transparentItPage.addItem(new Supplier<String>() {
			@Override
			public String get() {
				return original.getId();
			}
		},data.imageDrawingData, data.imageSrc,data.pointShape);
		
	
		//debugImage(data.imageSrc);
		
		
		return true;
	}
	
	
	private void debugImage(final String dataUrl){
	new ImageElementLoader().load(dataUrl, new ImageElementListener() {
			
			@Override
			public void onLoad(ImageElement element) {
				LogUtils.log(element.getWidth()+"x"+element.getHeight());
				LogUtils.log(dataUrl);
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				LogUtils.log("invalid:"+url);
			}
		});
	}
	
	
	
	protected void doTransparent() {

		if(!background.hasBackgroundData()){
			Window.alert("no background image.quit");
			return;
		}
		
		TextureData textureData=manager.getUploadedFileManager().getTextureData();
		List<ImageDrawingData> datas=Lists.newArrayList();
		
		//transparentItPage.clearAll();
		
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
		
		boolean warn=false;
		for(final ClipData original:cellObjects.getDatas()){
			//ClipData clip=original.copy(true);//use copy not plan to integrate or modify
			//now only one image per bone support.
			
		/*	ImageDrawingData data=convertToImageDrawingData(clip);
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
					return  original.getId();//id must be share with original
				}
			},data, src,pointShape);*/
			boolean success=doTransparent(textureData, original);
			if(!success){
				warn=true;
			}
		}
		
		if(warn){
			Window.alert("some clip is empty and ignored");
		}
		
		manager.selectTab(MainManager.TransparentPageIndex);
	}
	


	private TextureData toTextureData(){
		List<ImageDrawingData> datas=Lists.newArrayList();
		for(ClipData clip:cellObjects.getDatas()){
			ImageDrawingData data=convertToImageDrawingDataNoId(clip);
			datas.add(data);
			
			//set fresh id here
			data.setId(clip.getId());
			data.setImageName(data.getId()+".png");
		}
		
		
		TextureData oldTextureData=manager.getTextureDataWithNewestBone();
		if(oldTextureData!=null){
			List<String> relatedTextureIds=FluentIterable.from(cellObjects.getDatas()).transform(new ClipDataToRelatedTextureIdFunction()).filter(Predicates.notNull()).toList();
			
			List<ImageDrawingData> remains=FluentIterable.from(oldTextureData.getDatas()).filter(new NotExistInIds(relatedTextureIds)).toList();
			Iterables.addAll(datas, remains);
			if(remains.size()>0){
				LogUtils.log(remains.size()+" texture data merged");
			}
		}else{
			LogUtils.log("texture-data is null.skip merge");
		}
		
		TextureData textureData=new TextureData();
		textureData.setBone(boneControler.getBone());
		textureData.setImageDrawingDatas(datas);
		return textureData;
	}
	

	

	


	/*
	 * this convert has no id.because usually id is old and no plan to use id-supply
	 */
	public ImageDrawingData convertToImageDrawingDataNoId(ClipData clip){
		Rect rect=clip.getPointBound();
		rect.expandSelf(clip.getExpand(), clip.getExpand());
		PointD pt=rect.getCenterPoint();
		ImageElement element=ImageElementUtils.create(generateClippedImage(clip));
		
		
		
		
		String id=null;
		
		for(ImageDrawingData linked:clip.getLinkedImageDrawingData().asSet()){
		//	id=linked.getId();//this is used for removing linked texture.clip-id is possible change
		}
		
		ImageDrawingData data=new ImageDrawingData(id, element);
		data.setBoneName(clip.getBone());
		data.setX((int)pt.getX());
		data.setY((int)pt.getY());
		
		//data.setImageName(data.getId()+".png");
		
	//	data.incrementX(canvas.getCoordinateSpaceWidth()/2);
	//	data.incrementY(canvas.getCoordinateSpaceHeight()/2);
		
		return data;
	}
	


	protected String generateClippedImage(ClipData selection){
		return generateClippedImage(selection,true);
	}
	
	protected String generateClippedImage(ClipData selection,boolean clip) {
		//int expand=64;
		
		
		Rect rect=selection.getPointBound();
		rect.expandSelf(selection.getExpand(), selection.getExpand());
		Canvas clipCanvas=CanvasUtils.createCanvas(rect.getWidth(), rect.getHeight());
		Context2d context=clipCanvas.getContext2d();
		
		if(clip){
		List<Point> newPoints=Lists.newArrayList();
		for(Point pt:selection.getPoints()){
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
		
		
		
		ImageElement bg=ImageElementUtils.create(backgroundDataUrl);
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
	public void updateDatas() {
		cellObjects.update();
		updateCanvas();
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
				List<Point> pts=data.getPoints();
				if(data.getPoints().size()>2){
					canvas.getContext2d().beginPath();
					canvas.getContext2d().moveTo(pts.get(pts.size()-1).getX(), pts.get(pts.size()-1).getY());
					for(Point pt:data.getPoints()){
						canvas.getContext2d().lineTo(pt.getX(),pt.getY());
					}
					canvas.getContext2d().closePath();
					canvas.getContext2d().fill();
				}
				
			}
			
		}else if(drawMode==INSIDE){
			for(ClipData data:cellObjects.getDatas()){
				List<Point> pts=data.getPoints();
				if(data.getPoints().size()>2){
					canvas.getContext2d().beginPath();
					canvas.getContext2d().moveTo(pts.get(pts.size()-1).getX(), pts.get(pts.size()-1).getY());
					for(Point pt:data.getPoints()){
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
	protected void executeUpdateCanvas() {
		CanvasUtils.clear(canvas);
		
		ClipData selection=getSelection();
		
		
		drawBackground(canvas);
		
		boneControler.paintBone();
		
		for(ClipData data:cellObjects.getDatas()){
			
			//draw selection first
			if(data==selection){
				int dotSize=10;
				
				for(Point pt:data.getPoints()){
					String rectColor="#0a0";
					if(pt==data.getPoints().get(data.getPoints().size()-1)){
						rectColor="#0f0";
					}
					Rect rect=Rect.fromCenterPoint(pt.getX(), pt.getY(), dotSize/2, dotSize/2);
					RectCanvasUtils.fill(rect, canvas, rectColor);
					
					if(pt==getSelectedPoint()){
						rect.expandSelf(8,8);
						RectCanvasUtils.stroke(rect, canvas, "#000");
						
						
						//draw insert
						int index=pointSelectionIndex;
						if(index!=-1 && index<data.getPoints().size()){
							int at=index+1;
							
							if(index == data.getPoints().size()-1){
								at=0;
							}
							
							double x=getSelectedPoint().x + data.getPoints().get(at).x;
							double y=getSelectedPoint().y + data.getPoints().get(at).y;
							Point insertPt=new Point(x/2, y/2);
							
							Rect insertRect=Rect.fromCenterPoint(insertPt.getX(), insertPt.getY(), dotSize/2, dotSize/2);
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
				double x1=data.getPoints().get(i).getX();
				double y1=data.getPoints().get(i).getY();
				double x2=data.getPoints().get(i+1).getX();
				double y2=data.getPoints().get(i+1).getY();
				CanvasUtils.drawLine(canvas, x1, y1, x2, y2);
			}
			
			//close
			if(data.getPoints().size()>2){
				double x1=data.getPoints().get(0).getX();
				double y1=data.getPoints().get(0).getY();
				double x2=data.getPoints().get(data.getPoints().size()-1).getX();
				double y2=data.getPoints().get(data.getPoints().size()-1).getY();
				CanvasUtils.drawLine(canvas, x1, y1, x2, y2);
			}
			
			if(data.getPoints().size()==1){
				double x1=data.getPoints().get(0).getX();
				double y1=data.getPoints().get(0).getY();
			
				CanvasUtils.fillPoint(canvas, x1, y1);
			}
			}
			
			//stroke bounds
			if(data==selection || !drawSelectionOnly){
			if(data.getPoints().size()>2 && drawBounds){
			Rect boundRect=Rect.fromPoints(data.getPoints());
			int expand=data.getExpand();
			boundRect.expandSelf(expand, expand);
			RectCanvasUtils.stroke(boundRect, canvas, "#444");
			}
			}
			
		
			
		}
		
		//bone
		
			
		
	}





	




	//Point selectionPt; //need insertPoint;
	private ClipDataEditor editor;

	//List<CanvasDrawingDataControler> drawingDataControlers;
	//private CanvasDrawingDataControler activeDataControler;
	private Background background;
	





	


	public class ClipDrawingDataControler implements CanvasDrawingDataControler{

		@Override
		public void onWhelled(int delta, KeyDownState keydownState) {
			
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
			Point pt=null;
			if(getSelectedPoint()==null){//sadly never happen,so far .CanvasDrawingDataControler selected when clicked.
				//LogUtils.log("select first");
				pt=getSelection().getPoints().get(0);//select first
			}else{
				int index=pointSelectionIndex;
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
			if(pt==null){
				setPointUnselected();
			}else{
			pointSelectionIndex=getSelection().getPoints().indexOf(pt);
			}
		}

		@Override
		public void onTouchDragged(int vectorX, int vectorY, boolean rightButton, KeyDownState keydownState) {
			if(!isClipDataSelected()){
				return;
			}
			
			if(getSelectedPoint()!=null){
				getSelectedPoint().incrementX(vectorX);
				getSelectedPoint().incrementY(vectorY);
			}
		}

		@Override
		public boolean onTouchStart(int mx, int my, boolean rightButton,KeyDownState keydownState) {
			if(!isClipDataSelected()){
				setPointUnselected();
				return false;
			}
			
			
			//right button insert
			if(rightButton){
				if(getSelectedPoint()==null){
					return false;
				}
				
				for(Integer index:execInsertPoint(getSelection(),getSelectedPoint(),new Point(mx,my)).asSet()){
					pointSelectionIndex=index;
					startPosition=getSelectedPoint().copy();
					return true;
				}
				
				LogUtils.log("somehow can't insert point");
				return false;//
				
			}else{
			
			setSelectedPoint(getSelection().collision(mx, my));
			
			if(getSelectedPoint()!=null){
				startPosition=getSelectedPoint().copy();
				return true;
			}else{
				
				return false;
			}
			
			
			}
		}
		private Point startPosition;

		@Override
		public void onTouchEnd(int mx, int my, boolean rightButton,KeyDownState keydownState) {
			if(startPosition!=null){
				Point endPosition=getSelectedPoint().copy();
				if(!startPosition.equals(endPosition)){
					undoControler.execMovePoint(getDataIndex(),pointSelectionIndex, startPosition, endPosition);
				}
				startPosition=null;
				return;
			}
			
			startPosition=null;
		}

		@Override
		public String getName() {
			return "ClipImagePage";
		}
		
	}

	private void setSelectedPoint(Point pt){
		pointSelectionIndex=getSelection().getPoints().indexOf(pt);
	}

	//this make getSelectionPoint return null
	private void setPointUnselected(){
		pointSelectionIndex=-1;
	}



	
	private void execOrderChange(List<ClipData> oldOrder){
		List<ClipData> newOrder=copyOrder();
		undoControler.executeOrder(oldOrder, newOrder);
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
					
					List<ClipData> oldOrder=copyOrder();
					
					cellObjects.topItem(value);
					execOrderChange(oldOrder);
					updateCanvas();
				}
			}));
			movePanel.add(new Button("Up",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(value==null){
						return;
					}
					List<ClipData> oldOrder=copyOrder();
					cellObjects.upItem(value);
					execOrderChange(oldOrder);
					updateCanvas();
				}
			}));
			movePanel.add(new Button("Down",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(value==null){
						return;
					}
					List<ClipData> oldOrder=copyOrder();
					cellObjects.downItem(value);
					execOrderChange(oldOrder);
					updateCanvas();
				}
			}));
			movePanel.add(new Button("Bottom",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(value==null){
						return;
					}
					List<ClipData> oldOrder=copyOrder();
					cellObjects.bottomItem(value);
					execOrderChange(oldOrder);
					updateCanvas();
				}
			}));
			add(movePanel);
			
			movePanel.add(new Button("Sync Order",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doSyncTextureOrder();
				}
			}));
			add(movePanel);
			
			add(new Label("high-Green dot is last point.insert at white box"));
			
			HorizontalPanel movePointPanel=new HorizontalPanel();
			add(movePointPanel);
			movePointPanel.add(new Label("Move corner:"));
			Button topPoint=new Button("top",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doMovePointTop();
					
				}
			});
			movePointPanel.add(topPoint);
			
			
			Button bottomPoint=new Button("bottom",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doMovePointBottom();
					
				}
			});
			movePointPanel.add(bottomPoint);
			
			
			Button leftPoint=new Button("left",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doMovePointLeft();
					
				}
			});
			movePointPanel.add(leftPoint);
			
			
			Button rightPoint=new Button("right",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doMovePointRight();
					
				}
			});
			movePointPanel.add(rightPoint);
			
			
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
		
		

		protected void doMovePointTop() {
			Point beforePosition=getSelectedPoint().copy();
			
			if(beforePosition.getY()!=0){
				Point afterPoisition=new Point(beforePosition.getX(),0);
				
				getSelectedPoint().set(afterPoisition);
				undoControler.execMovePoint(getDataIndex(),pointSelectionIndex, beforePosition, afterPoisition);
				updateCanvas();
			}
			
				
		}
		protected void doMovePointBottom() {
			Point beforePosition=getSelectedPoint().copy();
			int max=boneControler.getCanvas().getCoordinateSpaceHeight();
			if(beforePosition.getY()!=max){
				Point afterPoisition=new Point(beforePosition.getX(),max);
				
				getSelectedPoint().set(afterPoisition);
				undoControler.execMovePoint(getDataIndex(),pointSelectionIndex, beforePosition, afterPoisition);
				updateCanvas();
			}
			
				
		}
		
		protected void doMovePointLeft() {
			Point beforePosition=getSelectedPoint().copy();
			
			if(beforePosition.getX()!=0){
				Point afterPoisition=new Point(0,beforePosition.getY());
				
				getSelectedPoint().set(afterPoisition);
				undoControler.execMovePoint(getDataIndex(),pointSelectionIndex, beforePosition, afterPoisition);
				updateCanvas();
			}
			
				
		}
		protected void doMovePointRight() {
			Point beforePosition=getSelectedPoint().copy();
			int max=boneControler.getCanvas().getCoordinateSpaceWidth();
			if(beforePosition.getX()!=max){
				Point afterPoisition=new Point(max,beforePosition.getY());
				
				getSelectedPoint().set(afterPoisition);
				undoControler.execMovePoint(getDataIndex(),pointSelectionIndex, beforePosition, afterPoisition);
				updateCanvas();
			}
			
				
		}



		protected void doInsertPoint() {
			if(!isClipDataSelected() || getSelectedPoint()==null){
				return;
			}
			
			for(Integer index:execInsertPoint(getSelection(),getSelectedPoint(),null).asSet()){
				
				pointSelectionIndex=index;
				updateCanvas();
				return;
			}
			/*
			int index=pointSelectionIndex;
			if(index!=-1 && index<value.getPoints().size()){
				int at=index+1;
				
				if(index == value.getPoints().size()-1){
					at=0;
				}
				
				double x=getSelectedPoint().x + value.getPoints().get(at).x;
				double y=getSelectedPoint().y + value.getPoints().get(at).y;
				Point pt=new Point(x/2, y/2);
				
				int newIndex=index+1;
				Point p=undoControler.execAddPoint(getDataIndex(),index+1, pt.x, pt.y);
				
				
				
				//value.getPoints().add(index+1, pt);
				
				pointSelectionIndex=newIndex;
				updateCanvas();
			}
			*/
		}

		@Override
			public void setDelegate(EditorDelegate<ClipData> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				//LogUtils.log("flush");
				if(value==null){
					return;
				}
				value.setBone(boneList.getValue().getName());
				
				//TODO support expand
				if(!eqvialence.doEquivalent(oldValue, value)){
					onDataModified(oldValue,value);
				}
				
				updateDatas();
				updateCanvas();
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			private ClipDataWithtouChildrenEquivalence eqvialence=new ClipDataWithtouChildrenEquivalence();
			private ClipData oldValue;
			@Override
			public void setValue(ClipData value) {
				this.value=value;
				if(value==null){
					boneList.setEnabled(false);
					return;
				}else{
					boneList.setEnabled(true);
				}
				oldValue=value.copy(false);
				
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
	
	private Optional<Integer> execInsertPoint(ClipData value,Point target,@Nullable Point newPoint){
		int index=value.getPoints().indexOf(target);
		if(index!=-1 && index<value.getPoints().size()){
			int at=index+1;
			
			//selected index is last,make between last & first.
			if(index == value.getPoints().size()-1){
				at=0;
			}
			
			if(newPoint==null){//center point
			double x=getSelectedPoint().x + value.getPoints().get(at).x;
			double y=getSelectedPoint().y + value.getPoints().get(at).y;
			newPoint=new Point(x/2, y/2);
			}
			
			int insertedIndex=index+1;
			undoControler.execAddPoint(getDataIndex(),index+1, newPoint.x, newPoint.y);
			//value.getPoints().add(insertedIndex, newPoint);
			
			return Optional.of(insertedIndex);
		}
		return Optional.absent();
	}
	
	public void onDataModified(ClipData oldValue, ClipData value) {
		for(Integer index:cellObjects.getSelectedIndex(value).asSet()){
			undoControler.execEditData(index, oldValue, value.copy(false));
			return;
		}
		LogUtils.log("onDataModified:not exist in datas");
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
	    
	    
	    //TODO set better position
	    UndoButtons undoButtons=new UndoButtons(undoControler);
	    panel.add(undoButtons);
	    
	    
	    downloadLinks = new HorizontalPanel();
	    panel.add(downloadLinks);
		
	    
	    return panel;
	}
	protected void doSaveData() {
		ClipImageData data=generateSaveData();
		
		for(TextureData textureData:getRenamedTextureDataIfNeed().asSet()){
			manager.setTextureData("clip-image", textureData);
		}
		
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
	
	private Optional<TextureData> getRenamedTextureDataIfNeed(){
		boolean needSave=false;
		TextureData textureData=manager.getTextureDataWithNewestBone();
		if(textureData==null){
			return Optional.absent();
		}
		for(ClipData clip:cellObjects.getDatas()){
			String clipId=clip.getId();
			for(ImageDrawingData linked:clip.getLinkedImageDrawingData().asSet()){
				String linkedId=linked.getId();
				if(!linkedId.equals(clipId)){
					needSave=true;
					
					linked.setId(clipId);//call before saved
					//need modify id
					Optional<ImageDrawingData> optional=textureData.findDataById(linkedId);
					if(optional.isPresent()){
						ImageDrawingData renameData=optional.get();
						renameData.setId(clipId);
						renameData.setImageName(clipId+".png");
					}
					
					
				}
			}
			
		}
		
		if(needSave){
			return Optional.of(textureData);
		}else{
			return Optional.absent();
		}
	}


	protected void onClipImageDataChanged(ClipImageData data){
		LogUtils.log("onClipImageDataChanged:");
		//background and bone called if exist.
		
		
		cellObjects.clearAllItems();
		for(ClipData clip:data.getClips()){
			cellObjects.addItem(clip);
		}
		
		
		for(ClipData clip:cellObjects.getFirst().asSet()){
			cellObjects.setSelected(clip, true);
		}
		
		cellObjects.update();
		
		updateCanvas();
		
		undoControler.clear();//for sync other datas.
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
					List<Point> pts=data.getPoints();
					if(data.getPoints().size()>2){
						extractCanvas.getContext2d().moveTo(pts.get(pts.size()-1).getX(), pts.get(pts.size()-1).getY());
						for(Point pt:data.getPoints()){
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
	
	private String generateBackgroundImage(){
		Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
		background.getBackgroundData().draw(extractCanvas);
		String dataUrl=extractCanvas.toDataUrl();
		return dataUrl;
	}
	
	private String backgroundDataUrl;
	
	
	

	ClipPageUndoControler undoControler;
	private ExecuteWaitAsyncButton clipToTextureButton;

	@Override
	protected void initialize() {
		manager.getTextureOrderSystem().addListener(new DataChangeListener<List<String>>() {
			@Override
			public void dataChanged(List<String> data, DataOwner owner) {
				onTextureOrderChanged(data,owner);
			}
		});
		
		undoControler=new ClipPageUndoControler(this);
		
		backgroundDataUrlSupplier=Suppliers.memoize(new Supplier<String>() {
			@Override
			public String get() {
				// TODO Auto-generated method stub
				return initializeBackgroundDataUrl();
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
		List<ClipData> oldOrder=copyOrder();
		
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
		
		execOrderChange(oldOrder);
		
	}

	@Override
	protected void onTextureDataChanged(TextureData textureData) {
		LogUtils.log("onTextureDataChanged");
		
		Optional<TextureData> newData=getRenamedTextureDataIfNeed();
		if(newData.isPresent()){
			manager.setTextureData("clip-image", newData.get());
			//recall onTextureDataChanged
			
		}else{
			//this link texture & clip data
			ClipImageData data=generateSaveData();
			manager.getFileManagerBar().setClipImageData(getOwnerName(), data);
		}
		
		
	}


	@Override
	public String getOwnerName() {
		// TODO Auto-generated method stub
		return "Clip-Editor";
	}


	@Override
	public Point getPointAt(int dataIndex,int index) {
		return cellObjects.getDatas().get(dataIndex).getPoints().get(index);
	}


	@Override
	public Point insertPoint(int dataIndex,int index, Point pt) {
	
		cellObjects.getDatas().get(dataIndex).getPoints().add(index, pt);
		return pt;
	}


	@Override
	public Point removePoint(int dataIndex,int index) {
		Point point=cellObjects.getDatas().get(dataIndex).getPoints().remove(index);
		if(point==null){
			LogUtils.log("removePoint:basically never happen null="+index);
		}
		return new Point(point.x,point.y);
	}


	@Override
	public void updatePoints() {
		updateCanvas();
	}


	@Override
	public ClipData insertData(int dataIndex, ClipData data) {
		cellObjects.insertItem(dataIndex,data);
		return data;
	}


	@Override
	public ClipData removeData(int dataIndex) {
		return cellObjects.removeItem(dataIndex);
	}


	@Override
	public ClipData getDataAt(int dataIndex) {
		Optional<ClipData> data=cellObjects.getItemAt(dataIndex);
		if(data.isPresent()){
			return data.get();
		}
		return null;
	}


	@Override
	public void updateData(int dataIndex) {
		for(ClipData data:cellObjects.getItemAt(dataIndex).asSet()){
		driver.edit(data);//update values
		return;
		}
		 LogUtils.log("updateData not exist:"+dataIndex);
	}


	@Override
	public void setDatas(List<ClipData> datas) {
		List<ClipData> newDatas=Lists.newArrayList();
		
		for(ClipData data:datas){
			newDatas.add(data);
		}
		
		cellObjects.setDatas(newDatas);
	}
	
	public List<ClipData> copyOrder(){
		List<ClipData> order=Lists.newArrayList();
		for(ClipData data:cellObjects.getDatas()){
			order.add(data);
		}
		return order;
	}


	//public boolean isSame



	@Override
	public void updateImageDrawingDatas() {
		updateCanvas();
	}
	
	
}
