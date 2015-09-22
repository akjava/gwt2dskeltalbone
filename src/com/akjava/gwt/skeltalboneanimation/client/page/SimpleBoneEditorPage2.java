package com.akjava.gwt.skeltalboneanimation.client.page;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.game.PointD;
import com.akjava.gwt.skeltalboneanimation.client.BoneTextCell;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneConverter;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;


/*
 * select image files and move,turn,scale it.
 */
public class SimpleBoneEditorPage2 extends VerticalPanel implements HasSelectionName{
	 interface Driver extends SimpleBeanEditorDriver< TwoDimensionBone,  TwoDimensionBoneEditor> {}
	 Driver driver = GWT.create(Driver.class);



/*
 * try to code compact 	
 */
	 public static class FlushTextBox<T> extends TextBox{
		 public FlushTextBox(final ValueAwareEditor<T> editor){
			 this.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						editor.flush();
					}
				});
		 }
	 }
	 public static class FlushIntegerBox<T> extends IntegerBox{
		 public FlushIntegerBox(final ValueAwareEditor<T> editor){
			 this.addValueChangeHandler(new ValueChangeHandler<Integer>() {
					@Override
					public void onValueChange(ValueChangeEvent<Integer> event) {
						editor.flush();
					}
				});
		 }
	 }
	 
		private Widget createClipButtons(){
			HorizontalPanel northPanel=new HorizontalPanel();
		    northPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		    northPanel.add(new Label("Bones:"));
		    
		    northPanel.add(new Label("Load:"));
		    /*
		    FileUploadForm load=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
				
				@Override
				public void uploaded(File file, String text) {
					doLoadBone(text);
				}
			}, true);
		    load.setAccept(FileUploadForm.ACCEPT_TXT);
		    northPanel.add(load);
		    */
		    
		    FileUploadForm load2=FileUtils.createSingleFileUploadForm(new DataArrayListener() {
				@Override
				public void uploaded(File file, Uint8Array array) {
					String extension=FileNames.getExtension(file.getFileName()).toLowerCase();
					if(extension.equals("zip")){
						JSZip zip=JSZip.loadFromArray(array);
						JSFile jsFile=zip.getFile("bone.txt");
						if(jsFile!=null){
							String text=jsFile.asText();
							doLoadBone(text);
						}else{
							Window.alert("not contain bone.txt");
						}
					}else{
						Blob blob=Blob.createBlob(array, "plain/text");
						final FileReader reader=FileReader.createFileReader();
						reader.setOnLoad(new FileHandler() {
							
							@Override
							public void onLoad() {
								String text=reader.getResultAsString();
								doLoadBone(text);
							}
						});
						reader.readAsText((File)blob.cast(), "uff8");
					}
				}
			});
		    northPanel.add(load2);
		    load2.setAccept(FileUploadForm.ACCEPT_TXT,FileUploadForm.ACCEPT_ZIP);
		   
		    
		    northPanel.add(new Button("Save",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doSaveBone();
				}
			}));
		    
		    downloadLinks = new HorizontalPanel();
		    northPanel.add(downloadLinks);
			
		    return northPanel;
		}
		

	public class TwoDimensionBoneEditor extends VerticalPanel implements Editor<TwoDimensionBone>,ValueAwareEditor<TwoDimensionBone>{

private FlushTextBox<TwoDimensionBone> nameBox;
private FlushIntegerBox<TwoDimensionBone> xBox,yBox;

private CheckBox lockedEditor;
private TwoDimensionBone value;
private ValueListBox<TwoDimensionBone> parentEditor;
private void onEditorFlush(){
	//LogUtils.log("flush by self:"+value.getName());
	List<TwoDimensionBone> bones=BoneUtils.getAllBone(getRootBone());
	
	if(bones.contains(value)){//when load new data
	value.setX(xBox.getValue());
	value.setY(yBox.getValue());
	value.setLocked(lockedEditor.getValue());
	
	String newName=nameBox.getValue();
	
	if(BoneUtils.existBoneByName(getRootBone(), newName,value)){
		Window.alert(newName+" is already exist.\n" +
				"name could not be updated.");
		nameBox.setValue(value.getName());
	}else{
		value.setName(newName);
	}
	//update parent
	TwoDimensionBone newParent=parentEditor.getValue();
	if(newParent!=null){
	//need move
	if(!newParent.getChildren().contains(value)){
		
		PointD absoluteValuePosition=value.getAbsolutePosition();
		TwoDimensionBone oldParent=value.getParent();
		value.getParent().getChildren().remove(value);
		
		PointD absoluteParentPosition=newParent.getAbsolutePosition();
		
		double newX=absoluteValuePosition.getX()-absoluteParentPosition.getX();
		double newY=absoluteValuePosition.getY()-absoluteParentPosition.getY();
		value.setX(newX);
		value.setY(newY);
		
		
		xBox.setValue((int)newX);
		yBox.setValue((int)newY);
		
		newParent.addBone(value);
		refreshTree(oldParent);
	}
	}
	
	refreshTree(value);
	
	updateBoneDatas();//redraw
	}
}

		public TwoDimensionBoneEditor(){
			
			HorizontalPanel panel=new HorizontalPanel();
			add(panel);
			
			panel.add(new Label("Name:"));
			nameBox = new FlushTextBox<TwoDimensionBone>(this);
			panel.add(nameBox);
		
			panel.add(new Label("X:"));
			xBox=new FlushIntegerBox<TwoDimensionBone>(this);
			panel.add(xBox);
			xBox.setWidth("80px");
			
			panel.add(new Label("Y:"));
			yBox=new FlushIntegerBox<TwoDimensionBone>(this);
			panel.add(yBox);
			yBox.setWidth("80px");
			
			panel.add(new Label("Parent:"));
			//TODO class
			parentEditor=new ValueListBox<TwoDimensionBone>(new Renderer<TwoDimensionBone>() {

				@Override
				public String render(TwoDimensionBone object) {
					if(object!=null){
						return object.getName();
					}
					return null;
				}

				@Override
				public void render(TwoDimensionBone object, Appendable appendable) throws IOException {
					// TODO Auto-generated method stub
					
				}
			});
			parentEditor.addValueChangeHandler(new ValueChangeHandler<TwoDimensionBone>() {

				@Override
				public void onValueChange(ValueChangeEvent<TwoDimensionBone> event) {
					onEditorFlush();
				}
				
			});
			panel.add(parentEditor);
			
			lockedEditor=new CheckBox("locked");
			panel.add(lockedEditor);
			lockedEditor.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					onEditorFlush();
				}
				
			});
		}
		
		
		public void updateParentEditor(){
			if(value==null){
				return;
			}
			List<TwoDimensionBone> bones=BoneUtils.getAllBone(getRootBone());
			List<TwoDimensionBone> filters=Lists.newArrayList(bones);
			filters.removeAll(BoneUtils.getAllBone(value));
			parentEditor.setAcceptableValues(filters);
			
			//root can't modify
			parentEditor.setVisible(value!=getRootBone());
		}
		
		
		
		
@Override
			public void setDelegate(EditorDelegate<TwoDimensionBone> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				
				onEditorFlush();
				//TODO update datas
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValue(TwoDimensionBone value) {
				
				this.value=value;
				nameBox.setText(value.getName());
				xBox.setValue((int)value.getX());
				yBox.setValue((int)value.getY());
				
				if(value==getRootBone()){
					xBox.setEnabled(false);
					yBox.setEnabled(false);
				}else{
					xBox.setEnabled(true);
					yBox.setEnabled(true);
				}
				
				parentEditor.setValue(value.getParent());//not fire
				updateParentEditor();
				
				lockedEditor.setValue(value.isLocked());
				
			}
	}
	
private Canvas canvas;
private CanvasDragMoveControler canvasControler;
private BonePositionControler bonePositionControler;


private final SingleSelectionModel<TwoDimensionBone> selectionModel = new SingleSelectionModel<TwoDimensionBone>();
	public SimpleBoneEditorPage2(DockLayoutPanel root){
		
		
		TwoDimensionBone rootBone = new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=rootBone.addBone(new TwoDimensionBone("back",0, -100));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",0, -100));
		
		//allbones = BoneUtils.getAllBone(rootBone);
		
		createCanvas();
		createBoneControls(rootBone,canvas);
		
		
		treeModel = new CustomTreeModel();

		    cellTree = new CellTree(treeModel, null);
		    
		    //cellTree.getRootTreeNode()
		    
		    // Add the tree to the root layout panel.
		    root.addWest(cellTree,300);
		   
		    
		    selectionModel.addSelectionChangeHandler(new Handler() {
				
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					//LogUtils.log("selection-changed");
					//LogUtils.log(selectionModel.getSelectedObject());
					driver.flush();
					driver.edit(selectionModel.getSelectedObject());
				}
			});
		    
		    
		    add(createClipButtons());
		    
		    
		    HorizontalPanel upper=new HorizontalPanel();
		    upper.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		    upper.add(new Button("Clear All",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doCreateNewRoot();
				}
			}));

		    upper.add(new Button("Add",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doCreateNewBone();
				}
			}));
		    upper.add(new Button("Remove",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doRemoveBone(false);
				}
			}));
		    upper.add(new Button("Remove with children",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doRemoveBone(true);
				}
			}));
		    upper.add(new Label("Load:"));
		    /*
		    FileUploadForm load=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
				
				@Override
				public void uploaded(File file, String text) {
					doLoadBone(text);
				}
			}, true);
		    load.setAccept(FileUploadForm.ACCEPT_TXT);
		    upper.add(load);
		    */
		    
		    /*
		    FileUploadForm load2=FileUtils.createSingleFileUploadForm(new DataArrayListener() {
				@Override
				public void uploaded(File file, Uint8Array array) {
					String extension=FileNames.getExtension(file.getFileName()).toLowerCase();
					if(extension.equals("zip")){
						JSZip zip=JSZip.loadFromArray(array);
						JSFile jsFile=zip.getFile("bone.txt");
						if(jsFile!=null){
							String text=jsFile.asText();
							doLoadBone(text);
						}else{
							Window.alert("not contain bone.txt");
						}
					}else{
						Blob blob=Blob.createBlob(array, "plain/text");
						final FileReader reader=FileReader.createFileReader();
						reader.setOnLoad(new FileHandler() {
							
							@Override
							public void onLoad() {
								String text=reader.getResultAsString();
								doLoadBone(text);
							}
						});
						reader.readAsText((File)blob.cast(), "uff8");
					}
				}
			});
		    upper.add(load2);
		    load2.setAccept(FileUploadForm.ACCEPT_TXT,FileUploadForm.ACCEPT_ZIP);
		    
		    //
		    upper.add(new Button("Save",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doSaveBone();
				}
			}));
		    */
		    
		    root.addNorth(upper, 32);
		    downloadLinks = new HorizontalPanel();
		    upper.add(downloadLinks);
		    
		add(createBackgroundButtons());	
		add(createCopyColumnButtons());
		
		
		    
		updateBoneDatas();//editor call
		
		TwoDimensionBoneEditor editor=new TwoDimensionBoneEditor();    
		driver.initialize(editor);
		
		
		driver.edit(new TwoDimensionBone("",0,0));//dummy?
		
		selectionModel.setSelected(getRootBone(), true);
		
		
		add(editor);
		add(canvas);
		
		
		
	}
	protected void doCreateNewRoot() {
		boolean confirm=Window.confirm("clear all bone?if not saved data all gone");
		if(!confirm){
			return;
		}
		setNewRoot(new TwoDimensionBone("root",0,0));
	}
	protected void setNewRoot(TwoDimensionBone newRoot){
settings.setBone(newRoot);
		
		for(TwoDimensionBone b:BoneUtils.getAllBone(newRoot)){
			//LogUtils.log(b);
		}
		
		treeModel.refresh();
		
		selectionModel.setSelected(newRoot, true);//select root;
		updateBoneDatas();
	}
	protected void doLoadBone(String lines) {
		
		TwoDimensionBone newRoot=new BoneConverter().reverse().convert(CSVUtils.splitLinesWithGuava(lines));
		setNewRoot(newRoot);
		
	}
	protected void doSaveBone() {
		List<String> lines=new BoneConverter().convert(getRootBone());
		downloadLinks.add(HTML5Download.get().generateTextDownloadLink(Joiner.on("\r\n").join(lines), "2dbones.txt", "download",true));
	}
	//for redraw-tree when add/remove children or renamed
	public void refreshTree(TwoDimensionBone value) {
		if(value.getParent()==null){
			treeModel.refresh(null);
		}else{
			treeModel.refresh(value.getParent());
		}
		treeModel.refresh(value);
	}
	
	public void updateBoneDatas(){
		bonePositionControler.updateInitialData();
		updateCanvas();
		
		treeNodeExpand(cellTree.getRootTreeNode());
	}
	
	public void treeNodeExpand(TreeNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
		if (!node.isChildLeaf(i)) {
			treeNodeExpand(node.setChildOpen(i,

		true));
		}

		}

		}
	
	
	protected void doCreateNewBone() {
		//cellTree.
		final TwoDimensionBone selection=selectionModel.getSelectedObject();
		if(selection==null){
			return;
		}
		TwoDimensionBone newBone=new TwoDimensionBone(BoneUtils.createBoneName(selection), 0, 0);
		selection.addBone(newBone);
		
		refreshTree(selection);
		updateBoneDatas();
		
		selectionModel.setSelected(newBone, true);	
	}
	
	protected void doRemoveBone(boolean removeChildren) {
		//cellTree.
		final TwoDimensionBone selection=selectionModel.getSelectedObject();
		if(selection==null || selection==getRootBone()){
			return;
		}
		
		selection.getParent().getChildren().remove(selection);
		
		if(!removeChildren){
			TwoDimensionBone newParent=selection.getParent();
			double offX=selection.getX();
			double offY=selection.getY();
			for(TwoDimensionBone bone:selection.getChildren()){
				bone.setX(bone.getX()+offX);
				bone.setY(bone.getY()+offY);
				newParent.addBone(bone);
			}
		}
		
		refreshTree(selection);
		updateBoneDatas();
		
		selectionModel.setSelected(selection.getParent(), true);//re-select
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
	
	private CircleLineBonePainter painter;
	private void createBoneControls(TwoDimensionBone rootBone,final Canvas canvas){
		settings=new CanvasBoneSettings(canvas, rootBone);
		
		final SkeletalAnimation animations=new SkeletalAnimation("test", 33.3);
		
		singleFrame = BoneUtils.createEmptyAnimationFrame(getRootBone());
		animations.add(singleFrame);
		
		
		
		bonePositionControler=new BonePositionControler(settings);
		bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
		
		painter = new CircleLineBonePainter(canvas, this, bonePositionControler);
		
		

		
	}
	
	/*
	 * this model custom for show & select root objects.
	 */
	private  class CustomTreeModel implements TreeViewModel {
		private Map<TwoDimensionBone,ListDataProvider<TwoDimensionBone>> providerMap=new HashMap<TwoDimensionBone, ListDataProvider<TwoDimensionBone>>();
	    public void refresh(TwoDimensionBone bone){
	    	
	    	ListDataProvider<TwoDimensionBone> dataProvider=providerMap.get(bone);
	    	if(dataProvider==null){//possible if not expand yet.this means no need resync yet.
	    		
	    		//LogUtils.log("null-provider:"+bone);
	    		
	    		return;
	    	}
	    	//resync trees,
	    	
	    	/*
	    	dataProvider.getList().clear();
	    	for(TwoDimensionBone child:bone.getChildrens()){
	    		dataProvider.getList().add(child);
	    	}
	    	dataProvider.setList(listToWrap)
	    	*/
	    	if(bone==null){//must be root
	    		dataProvider.setList(Lists.newArrayList(getRootBone()));
	    	}else{
	    		dataProvider.setList(bone.getChildren());
	    	}
	    	//dataProvider.refresh();
	    	//cellTree.
	    }
		// Get the NodeInfo that provides the children of the specified value.
	    
	    public void refresh(){
	    	//providerMap.clear();
	    	refresh(null);
	    }
	    
	    public <T> NodeInfo<?> getNodeInfo(T value) {

	    	if(value==null){
	    	ListDataProvider<TwoDimensionBone> dataProvider = new ListDataProvider<TwoDimensionBone>();
	    	dataProvider.getList().add(getRootBone());
	    	
	    	providerMap.put(null, dataProvider);
	    	
	   	   	return new DefaultNodeInfo<TwoDimensionBone>(dataProvider, new BoneTextCell(),selectionModel,null);
	    	}
	    	
	     //very important only create node-info,when node expanded.before that only check isleaf();so refresh parent-node	
	      // Create some data in a data provider. Use the parent value as a prefix for the next level.
	      ListDataProvider<TwoDimensionBone> dataProvider = new ListDataProvider<TwoDimensionBone>();
	      TwoDimensionBone bone=(TwoDimensionBone)value;
	      dataProvider.setList(bone.getChildren());
	      /*
	      for(TwoDimensionBone child:bone.getChildrens()){
	    	  dataProvider.getList().add(child);
	      }
	      */

	      providerMap.put(bone, dataProvider);
	      // Return a node info that pairs the data with a cell.
	      return new DefaultNodeInfo<TwoDimensionBone>(dataProvider, new BoneTextCell(),selectionModel,null);
	    }

	    // Check if the specified value represents a leaf node. Leaf nodes cannot be opened.
	    public boolean isLeaf(Object value) {
	     if(value==null){
	    	 return false;
	     }
	     TwoDimensionBone bone=(TwoDimensionBone)value;
	     return bone.getChildren().size()==0;
	    }
	  }
	
	protected void onCanvasWheeled(int v) {
		if(backgroundEditCheck.getValue() && backgroundSelected && backgroundData!=null){
			int zoom=(int) (100*backgroundData.getScaleX());
			zoom+=v/3*5;
			if(zoom<5){
				zoom=5;
			}
			
			backgroundData.setScaleX((double)zoom/100);
			backgroundData.setScaleY((double)zoom/100);
			backgroundData.updateBounds();
		}
		updateCanvas();
	}


	protected void onCanvasDragged(int vectorX, int vectorY) {
		if(boneSelectedOnCanvas!=null){
			TwoDimensionBone bone=boneSelectedOnCanvas;
			
			if(bone==getRootBone()){
				return;//rootBone is fixed,so far.
			}
			
			bone.setX(bone.getX()+vectorX);
			bone.setY(bone.getY()+vectorY);
			
			if(canvasControler.isShiftKeyDown()){
				for(TwoDimensionBone child:bone.getChildren()){
					child.setX(child.getX()-vectorX);
					child.setY(child.getY()-vectorY);
				}
			}
			
			
			driver.edit(bone);//data is update on editor,but not flash when same
			updateBoneDatas();
		}else{//background
			if(backgroundEditCheck.getValue() &&  backgroundSelected && backgroundData!=null){
				
				
				if(canvasControler.isRightMouse()){
					backgroundData.incrementAngle(vectorX);
				}else{
					backgroundData.incrementX(vectorX);
					backgroundData.incrementY(vectorY);
					
					
					
				}
				backgroundData.updateBounds();
				
				updateCanvas();
			}
		}
	}

	private boolean backgroundSelected;
	private TwoDimensionBone boneSelectedOnCanvas;
	protected void onCanvasTouchStart(int sx, int sy) {
		TwoDimensionBone newSelection=null;
		if(selectionModel.getSelectedObject()!=null){
			if(bonePositionControler.isCollisionOnInitialData(selectionModel.getSelectedObject(),sx,sy)){
				
				newSelection=selectionModel.getSelectedObject();
			}
		}
		
		//not current selection
		if(newSelection==null){
			newSelection=bonePositionControler.collisionInitialData(sx,sy);
		}
		
		
		
		if(newSelection!=null){
			backgroundSelected=false;
			boneSelectedOnCanvas=newSelection;
			selectionModel.setSelected(newSelection, true);//select by click
		}else{
			
			
			if(backgroundEditCheck.getValue() && backgroundData!=null && backgroundData.collision(sx, sy)){
				backgroundSelected=true;
				
			}else{
				backgroundSelected=false;
			}
			
			
			boneSelectedOnCanvas=null;
			updateCanvas();
		}
	}

	private String getUniqBoneName(List<String> exist,String name){
		
		int index=1;
		if(!exist.contains(name)){
			return name;
		}
		do{
		name=name+"-"+index;
		index++;
		}while(exist.contains(name));
		
		return name;
	}

	private HorizontalPanel createCopyColumnButtons(){
		HorizontalPanel buttons=new HorizontalPanel();
		buttons.setVerticalAlignment(ALIGN_MIDDLE);
		buttons.add(new Label("Copy"));
		
		Button horizontal=new Button("horizontal",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TwoDimensionBone selection=selectionModel.getSelectedObject();
				if(selection==null || selection==getRootBone()){
					return;
				}
				
				
				List<String> exist=BoneUtils.getAllBoneName(getRootBone());
				TwoDimensionBone newBone=selection.copy(true);
				
				for(TwoDimensionBone bone:BoneUtils.getAllBone(newBone)){
					bone.setX(bone.getX()*-1);
					
					//name
					String name=bone.getName();
					if(name.indexOf("right")!=-1 && name.indexOf("left")==-1){
						name=name.replace("right", "left");
					}else if(name.indexOf("left")!=-1 && name.indexOf("right")==-1){
						name=name.replace("left", "right");
					}
					//TODO Right & RIGHT
					name=getUniqBoneName(exist,name);
					bone.setName(name);
					exist.add(name);
				}
				
				selection.getParent().addBone(newBone);
				updateBoneDatas();
				
			}
		});
		buttons.add(horizontal);
		
		//TODO copy & vertical
		
		return buttons;
	}

	
	private ImageDrawingData backgroundData;
	private CheckBox transparentEditCheck;
	private Widget createBackgroundButtons() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		panel.add(new Label("Background:"));
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				ImageElement element=ImageElementUtils.create(text);
				backgroundData=new ImageDrawingData("",element);
				int canvasW=canvas.getCoordinateSpaceWidth();
				int canvasH=canvas.getCoordinateSpaceHeight();
				int imageW=element.getWidth();
				int imageH=element.getHeight();
				
				backgroundData.setX(bonePositionControler.getSettings().getOffsetX());
				backgroundData.setY(bonePositionControler.getSettings().getOffsetY());
				
				//backgroundEditCheck.setValue(false);
				updateCanvas();
			}
		});
		panel.add(upload);
		Button reset=new Button("Clear",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				backgroundData=null;
				updateCanvas();
			}
		});
		panel.add(reset);
		backgroundEditCheck = new CheckBox("Edit");
		panel.add(backgroundEditCheck);
		
		transparentEditCheck = new CheckBox("Transparent-Bone");
		panel.add(transparentEditCheck);
		transparentEditCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateCanvas();
			}
		});
		
		final HorizontalPanel downloadLinks=new HorizontalPanel();
		
		Button extractImageBt=new Button("Extract BG",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
				backgroundData.draw(extractCanvas);
				String dataUrl=extractCanvas.toDataUrl();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-bg.png", "background", true);
				downloadLinks.add(a);
			}
		});
		panel.add(extractImageBt);
		
		Button extractBoneBt=new Button("Extract Bone",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
				
				painter.setCanvas(extractCanvas);
				painter.paintBone();
				painter.setCanvas(canvas);
				
				String dataUrl=extractCanvas.toDataUrl();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-bone.png", "bone", true);
				downloadLinks.add(a);
			}
		});
		panel.add(extractBoneBt);
		panel.add(downloadLinks);
		
		return panel;
	}
	

	private void updateCanvas() {
		CanvasUtils.clear(canvas);
		if(backgroundData!=null){
			
			
			backgroundData.draw(canvas);
			String border="#000";
			if(backgroundSelected){
				border="#0f0";
			}
			backgroundData.drawBorder(canvas,border);
			
		}
		if(transparentEditCheck.getValue()){
			canvas.getContext2d().setGlobalAlpha(0.3);
		}else{
			canvas.getContext2d().setGlobalAlpha(1);
		}
		
		painter.paintBone();
		canvas.getContext2d().setGlobalAlpha(1);
	}

	//TwoDimensionBone boneSelection;
	
	
	
	private AnimationFrame singleFrame;
	private CanvasBoneSettings settings;
	
	public TwoDimensionBone getRootBone() {
		return settings.getBone();
	}
	public void setRootBone(TwoDimensionBone rootBone) {
		settings.setBone(rootBone);
	}

	//private List<TwoDimensionBone> allbones;
	private BoneControlRange boneControlerRange;
	private CellTree cellTree;
	private CustomTreeModel treeModel;
	private HorizontalPanel downloadLinks;
	private CheckBox backgroundEditCheck;
	@Override
	public String getSelectionName() {
		return selectionModel.getSelectedObject()!=null?selectionModel.getSelectedObject().getName():null;
	}




}
