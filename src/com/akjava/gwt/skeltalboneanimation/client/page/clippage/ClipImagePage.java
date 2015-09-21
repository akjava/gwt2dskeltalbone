package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.jszip.client.JSZipUtils;
import com.akjava.gwt.jszip.client.JSZipUtils.ZipListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.lib.client.game.PointD;
import com.akjava.gwt.lib.client.game.PointXY;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.skeltalboneanimation.client.Background;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.DrawingDataControler;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingDataControler;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneListBox;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.ClipImageDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.TextureDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.CircleLineBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.page.HasSelectionName;
import com.akjava.lib.common.graphics.Rect;
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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ClipImagePage extends AbstractPage implements HasSelectionName {
	 interface Driver extends SimpleBeanEditorDriver< ClipData,  ClipDataEditor> {}
	
	 //dont initialize here
	 
	Driver driver;
	private EasyCellTableObjects<ClipData> cellObjects;

	 
	 
	public ClipImagePage(final MainManager manager){
		super(manager);
		
	}
	
	
	public Panel createClipImagePanel(){
		HorizontalPanel panel=new HorizontalPanel();
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
		
		downloadLinks = new HorizontalPanel();
		panel.add(downloadLinks);
		
		return panel;
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
		TwoDimensionBone bone=manager.getUploadedFileManager().getBone();
		
		ClipData data=new ClipData();
		data.setBone(bone.getName());
		
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
	
	private CanvasBonePainter painter;
	private CanvasBoneSettings settings;
	private BonePositionControler bonePositionControler;
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
		
		
		settings=new CanvasBoneSettings(canvas, null);
		
		bonePositionControler=new BonePositionControler(settings);
		bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
		
		painter = new CircleLineBonePainter(canvas, this, bonePositionControler);
		
		
		
		
		
		canvas.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				doDoubleClick(event.getX(), event.getY());
			}

			
		});
		
		drawingDataControlers=Lists.newArrayList();
		
		background = new Background();
		background.setEditable(false);
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
		
		updateCanvas();			
		}
	
	protected Widget createWestPanel() {
		VerticalPanel panel=new VerticalPanel();
		panel.add(new Label("Clips"));
		
		panel.add(createClipImagePanel());
		
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
					return object.getBone()==null?"NONE":object.getBone();
				}
			};
			table.addColumn(nameColumn);
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
	 
	 panel.add(new Label("export images"));
	 
	 HorizontalPanel h3=new HorizontalPanel();
	 panel.add(h3);
	 final HorizontalPanel downloadLinks=new HorizontalPanel();
	 
	 Button exportSelection=new Button("selection",new ClickHandler() {
		
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
	 Button exportAsTexture=new Button("as texture",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				List<ImageDrawingData> datas=Lists.newArrayList();
				for(ClipData clip:cellObjects.getDatas()){
					ImageDrawingData data=convertToImageDrawingData(clip);
					datas.add(data);
					//TODO change uniq id & name.id not empty
					data.setImageName(data.getId()+".png");
				}
				TextureData textureData=new TextureData();
				textureData.setBone(settings.getBone());
				textureData.setImageDrawingDatas(datas);
				
				TextureDataConverter converter=new TextureDataConverter();
				JSZip jszip=converter.reverse().convert(textureData);
				downloadLinks.clear();
				downloadLinks.add(JSZipUtils.createDownloadAnchor(jszip, "2dbone-textures.zip", "download", true));
				
			}
		});
	 h3.add(exportAsTexture);
	
	 panel.add(downloadLinks);
	 
		return panel;
	}


	public ImageDrawingData convertToImageDrawingData(ClipData clip){
		Rect rect=clip.getBound();
		rect.expandSelf(clip.getExpand(), clip.getExpand());
		PointD pt=rect.getCenterPoint();
		ImageElement element=ImageElementUtils.create(generateClippedImage(clip));
		
		
		ImageDrawingData data=new ImageDrawingData(clip.getBone()!=null?clip.getBone():"", element);
		data.setBoneName(clip.getBone());
		data.setX((int)pt.getX());
		data.setY((int)pt.getY());
		
		
	//	data.incrementX(canvas.getCoordinateSpaceWidth()/2);
	//	data.incrementY(canvas.getCoordinateSpaceHeight()/2);
		
		return data;
	}
	



	protected String generateClippedImage(ClipData selection) {
		//int expand=64;
		Rect rect=selection.getBound();
		rect.expandSelf(selection.getExpand(), selection.getExpand());
		Canvas clipCanvas=CanvasUtils.createCanvas(rect.getWidth(), rect.getHeight());
		List<PointXY> newPoints=Lists.newArrayList();
		for(PointXY pt:selection.getPoints()){
			newPoints.add(pt.copy().incrementXY(-rect.getX(), -rect.getY()));
		}
		Context2d context=clipCanvas.getContext2d();
		context.beginPath();
		
		
		context.moveTo(newPoints.get(0).x, newPoints.get(0).y);
		for(int i=1;i<newPoints.size();i++){
			context.lineTo(newPoints.get(i).x, newPoints.get(i).y);
		}
		context.lineTo(newPoints.get(0).x, newPoints.get(0).y);
		context.closePath();
		context.clip();
		
		ImageElement bg=ImageElementUtils.create(generateBackgroundImage());
		context.drawImage(bg, -rect.getX(), -rect.getY());
		return clipCanvas.toDataUrl();
	}

	

	@Override
	protected void onBoneChanged(TwoDimensionBone bone) {
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
		settings.setBone(bone);
		updateCanvas();
	}





	@Override
	protected void onBackgroundChange(ImageDrawingData bg) {
		background.setBackgroundData(bg);
		//not select
		updateCanvas();
	}





	@Override
	protected void updateDatas() {
		cellObjects.update();
	}


	public TwoDimensionBone getRootBone(){
		return manager.getUploadedFileManager().getBone();
	}


	@Override
	protected void updateCanvas() {
		CanvasUtils.clear(canvas);
		
		//draw background
		if(background.hasBackgroundData() && background.isVisible()){
		
			background.getBackgroundData().draw(canvas);
			String border="#000";
			if(background.isSelected()){
			//	LogUtils.log("selected");
				border="#0f0";
			}
			background.getBackgroundData().drawBorder(canvas,border);
		}
		
		
		ClipData selection=getSelection();
		for(ClipData data:cellObjects.getDatas()){
			String color="#044";
			if(data==selection){
				color="#00f";
			}
			canvas.getContext2d().setStrokeStyle(color);
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
			
				CanvasUtils.drawPoint(canvas, x1, y1);
			}
			
			if(data.getPoints().size()>2){
			Rect boundRect=Rect.fromPoints(data.getPoints());
			int expand=16;
			boundRect.expandSelf(expand, expand);
			RectCanvasUtils.stroke(boundRect, canvas, "#888");
			}
			
			
			if(data==selection){
				int dotSize=10;
				
				for(PointXY pt:data.getPoints()){
					String rectColor="#0c0";
					if(pt==data.getPoints().get(data.getPoints().size()-1)){
						rectColor="#00c";
					}
					Rect rect=Rect.fromCenterPoint(pt.getX(), pt.getY(), dotSize/2, dotSize/2);
					RectCanvasUtils.fill(rect, canvas, rectColor);
					
					if(pt==selectionPt){
						rect.expandSelf(8, 8);
						RectCanvasUtils.stroke(rect, canvas, "#000");
					}
					
				}
			}
			
		}
		
		//bone
		if(visibleBone){ //i believe separate bone-painter
			painter.paintBone();
		}
	}





	@Override
	protected void onCanvasTouchEnd(int sx, int sy) {
		//selectionPt=null;
		if(activeDataControler!=null){
			activeDataControler.onTouchEnd(sx, sy);
		}
	}




	PointXY selectionPt;
	private ClipDataEditor editor;

	List<DrawingDataControler> drawingDataControlers;
	private DrawingDataControler activeDataControler;
	private Background background;
	@Override
	protected void onCanvasTouchStart(int sx, int sy) {
		
		DrawingDataControler active=null;
		for(DrawingDataControler data:drawingDataControlers){
			if(data.onTouchStart(sx, sy)){
				active=data;
				break;
			}
		}
		activeDataControler=active;
		updateCanvas();
		//LogUtils.log(activeDataControler!=null?activeDataControler.getName():"null-active");
		//updateCanvas();
		
		
		if(!isClipDataSelected()){
			selectionPt=null;
			return;
		}
		
		selectionPt=getSelection().collision(sx, sy);
		updateCanvas();
	}





	@Override
	protected void onCanvasDragged(int vectorX, int vectorY) {
		if(activeDataControler!=null){
			activeDataControler.onTouchDragged(vectorX, vectorY, canvasControler.isRightMouse());
			updateCanvas();
		}
		
		
		//TODO 
		if(!isClipDataSelected()){
			return;
		}
		
		if(selectionPt!=null){
			selectionPt.incrementX(vectorX);
			selectionPt.incrementY(vectorY);
			updateCanvas();
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
			add(new Label("Blue dot is last point"));
			
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
			if(index!=-1 && index<value.getPoints().size()-1){
				int x=selectionPt.x + value.getPoints().get(index+1).x;
				int y=selectionPt.y + value.getPoints().get(index+1).y;
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
	
	private boolean visibleBone=true;
	private HorizontalPanel downloadLinks;
	
	private TwoDimensionBone findBoneByName(String name){
		if(name==null){
			return null;
		}
		return BoneUtils.findBoneByName(BoneUtils.getAllBone(getRootBone()), name);
	}
	
	private Widget createClipButtons(){
		HorizontalPanel northPanel=new HorizontalPanel();
	    northPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	
	    northPanel.add(new Label("Clips:"));
	    
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
	    northPanel.add(new Button("Save",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSaveData();
			}
		}));
	    return northPanel;
	}
	protected void doSaveData() {
		ClipImageData data=new ClipImageData();
		data.setImageDrawingData(background.getBackgroundData());
		for(ClipData clip:cellObjects.getDatas()){
			data.getClips().add(clip);
		}
		JSZip zip=new ClipImageDataConverter().reverse().convert(data);
		downloadLinks.clear();
		downloadLinks.add(JSZipUtils.createDownloadAnchor(zip, "2dbone-clips.zip", "download", true));
	}


	protected void doLoadData(String name, JSZip zip) {
		ClipImageData data=new ClipImageDataConverter().convert(zip);
		if(data.getImageDrawingData()!=null){
			LogUtils.log(data.getImageDrawingData()+","+data.getImageDrawingData().getImageElement());
			//maybe this set background
			manager.getUploadedFileManager().setBackgroundData(data.getImageDrawingData());
		}
		cellObjects.clearAllItems();
		for(ClipData clip:data.getClips()){
			cellObjects.addItem(clip);
		}
		
		
		for(ClipData clip:cellObjects.getFirst().asSet()){
			cellObjects.setSelected(clip, true);
		}
		
		updateCanvas();
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
				visibleBone=(event.getValue());
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
				background.getBackgroundData().draw(extractCanvas);
				String dataUrl=extractCanvas.toDataUrl();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-bg.png", "background", true);
				downloadLinks.add(a);
			}
		});
		panel.add(extractImageBt);
		
		
		panel.add(downloadLinks);
		
		return panel;
	}
	
	public String generateBackgroundImage(){
		Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
		background.getBackgroundData().draw(extractCanvas);
		String dataUrl=extractCanvas.toDataUrl();
		return dataUrl;
	}


	@Override
	public String getSelectionName() {
		return null;
	}
	
}
