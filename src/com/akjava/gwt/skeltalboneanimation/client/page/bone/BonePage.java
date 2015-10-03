package com.akjava.gwt.skeltalboneanimation.client.page.bone;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.game.PointD;
import com.akjava.gwt.skeltalboneanimation.client.Background;
import com.akjava.gwt.skeltalboneanimation.client.BoneTextCell;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.CanvasDrawingDataControler;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingDataControler;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.UndoButtons;
import com.akjava.gwt.skeltalboneanimation.client.SimpleUndoControler.Command;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneConverter;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.CircleLineBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.page.HasSelectionName;
import com.akjava.gwt.skeltalboneanimation.client.page.SimpleBoneEditorPage2.FlushIntegerBox;
import com.akjava.gwt.skeltalboneanimation.client.page.SimpleBoneEditorPage2.FlushTextBox;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.BonePageUndoControler.BonePositionChangeCommand;
import com.akjava.gwt.skeltalboneanimation.client.page.bone.BonePageUndoControler.BonePositionChangeCommandByWheel;
import com.akjava.lib.common.graphics.Point;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class BonePage extends AbstractPage implements HasSelectionName,BoneUpdater,ImageDrawingDatasUpdater{
	 interface Driver extends SimpleBeanEditorDriver< TwoDimensionBone,  TwoDimensionBoneEditor> {}
	 Driver driver;
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
			public void update(){
				setValue(value);
			}

			@Override
			public void setValue(TwoDimensionBone value) {
				
				this.value=value;
				if(value==null){
					setVisible(false);
					return;
				}else{
					setVisible(true);
				}
				
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

			public boolean hasValue() {
				return value!=null;
			}
	}
	
	
	//private CanvasDragMoveControler canvasControler;
	private BonePositionControler bonePositionControler;

	private  SingleSelectionModel<TwoDimensionBone> selectionModel ;
	

	
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
		TwoDimensionBone bone=getRootBone();
		List<String> lines=new BoneConverter().convert(getRootBone());
		downloadLinks.clear();
		downloadLinks.add(HTML5Download.get().generateTextDownloadLink(Joiner.on("\r\n").join(lines), "2dbones.txt", "download",true));
	
		 
		manager.getFileManagerBar().setBoneAndAnimation("bone-editor", new BoneAndAnimationData(bone));
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
	
	/**
	 * update bone position for mouse-event
	 * redraw canvas
	 * update tree(seem not stable)
	 */
	public void updateBoneDatas(){
		bonePositionControler.updateInitialData();
		updateCanvas();
		
		treeNodeExpand(cellTree.getRootTreeNode());
		twoDimensionBoneEditor.update();// undo/redo change value
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
		undoControler.execute(undoControler.createAddBoneCommand(selection));
		/*
		TwoDimensionBone newBone=new TwoDimensionBone(BoneUtils.createBoneName(selection), 0, 0);
		selection.addBone(newBone);
		
		refreshTree(selection);
		updateBoneDatas();
		
		selectionModel.setSelected(newBone, true);
		*/	
	}
	
	protected void doRemoveBone(boolean removeChildren) {
		//cellTree.
		final TwoDimensionBone selection=selectionModel.getSelectedObject();
		if(selection==null || selection==getRootBone()){
			return;
		}
		
		undoControler.execute(undoControler.createRemoveBoneCommand(removeChildren,selection));
		
		/*
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
		*/
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
		
		

		boneMoveControler = new BoneMoveControler(){

			private void updateSelection(){
				Optional<TwoDimensionBone> bone=boneMoveControler.getSelection();
				if(bone.isPresent()){
					if(bone.get().equals(selectionModel.getSelectedObject())){
						driver.edit(bone.get());//for update state
						updateBoneDatas();
					}else{
						selectionModel.setSelected(bone.get(), true);
					}
					
				}else{
					//basicall never call
					if(selectionModel.getSelectedObject()!=null){
						selectionModel.setSelected(selectionModel.getSelectedObject(), false);
					}
				}
			}
			
			@Override
			public void onBoneMoveEnd() {
				//LogUtils.log("moveEnd");
				//LogUtils.log("move-end");
				if(getSelection().isPresent()){
					
					//make undo
					if(posData!=null){
						//LogUtils.log("selected & started");
						
						List<BonePositionData> add=FluentIterable.from(BoneUtils.getAllBone(getRootBone())).transform(new BonePositionDataTransform(posData)).toList();
						//LogUtils.log(add.size());
						//LogUtils.log(BoneUtils.getAllBone(getRootBone()).size());
						for(BonePositionData data:add){
							//LogUtils.log(data);
						}
						
						
						
						List<BonePositionData> changed=FluentIterable.from(BoneUtils.getAllBone(getRootBone())).transform(new BonePositionDataTransform(posData)).filter(new MovedBone()).toList();
						
						
						
						//LogUtils.log("changed:"+changed.size());
						if(!changed.isEmpty()){
							BonePositionChangeCommand command=undoControler.createBonePositionChangeCommand(changed);
							undoControler.execute(command);
						}
						posData=null;
					}
				}
				updateSelection();
				//TODO if has selection 
			}

			List<BonePositionData> posData;
			private ImmutableList<BonePositionData> wheelPosData;
			@Override
			public void onBoneMoveStart() {
				//LogUtils.log("moveStart");
				updateSelection();
				//save current pos
				posData=FluentIterable.from(BoneUtils.getAllBone(getRootBone())).transform(new BonePositionDataTransform()).toList();
				for(BonePositionData data:posData){
				//	LogUtils.log(data);
				}
				}

			//can replace supplier?
			@Override
			public BonePositionControler getBonePositionControler() {
				return bonePositionControler;
			}

			@Override
			public void onBoneMoveUpdate() {
				updateSelection();
			}

			@Override
			public void onBoneWheelEnd() {
				
				Optional<TwoDimensionBone> selection=getSelection();
				if(!selection.isPresent()){
					return;
				}
				TwoDimensionBone selectedBone=selection.get();
				
				Optional<BonePositionChangeCommandByWheel> command=getLastSameWheelUndoCommandIfExist(selection.get());
				if(command.isPresent()){
					//re-use
					//LogUtils.log("re-use-pos");
					//need dummy output,otherwise optimized & not call transform
					List<BonePositionData> dummy=FluentIterable.from(BoneUtils.getAllBone(getRootBone())).transform(new BonePositionDataTransform(command.get().getDatas())).toList();
				}else{
					//no filter for future modify?
					List<BonePositionData> datas=FluentIterable.from(BoneUtils.getAllBone(getRootBone())).transform(new BonePositionDataTransform(wheelPosData)).toList();
					
					BonePositionChangeCommandByWheel newCommand=undoControler.createBonePositionChangeCommandByWheel(datas,selectedBone);
					undoControler.execute(newCommand);
				}
				
			}
			//nice code
			private Optional<BonePositionChangeCommandByWheel> getLastSameWheelUndoCommandIfExist(TwoDimensionBone selection){
				Optional<Command> optional=undoControler.getLastUndoCommand();
				if(optional.isPresent()){
					Command command=optional.get();
					if(command instanceof BonePositionChangeCommandByWheel){
						BonePositionChangeCommandByWheel bonePosCommand=(BonePositionChangeCommandByWheel)command;
						if(bonePosCommand.getSelected()==selection){
							return Optional.of(bonePosCommand);
						}
					}
				}
				return Optional.absent();
			}

			@Override
			public void onBoneWheelStart() {
				Optional<TwoDimensionBone> selection=getSelection();
				if(!selection.isPresent()){
					LogUtils.log("never happen");
					return;
				}
				
				if(!getLastSameWheelUndoCommandIfExist(selection.get()).isPresent()){
					wheelPosData=FluentIterable.from(BoneUtils.getAllBone(getRootBone())).transform(new BonePositionDataTransform()).toList();
				}
				
			}
			
		};
		
		drawingDataControlers.add(boneMoveControler);
		
	}
	

	
	
	public BonePage(MainManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCanvasTouchEnd(int sx, int sy) {
		
		if(activeDataControler!=null){
			activeDataControler.onTouchEnd(sx, sy,canvasControler.getKeyDownState());
		}
	}

	@Override
	protected void onCanvasTouchStart(int sx, int sy) {
		//LogUtils.log("touchStart");
		CanvasDrawingDataControler active=null;
		for(CanvasDrawingDataControler data:drawingDataControlers){
			if(data.onTouchStart(sx, sy,canvasControler.getKeyDownState())){
				active=data;
				break;
			}
		}
		activeDataControler=active;
		updateCanvas();
		
		
		
		
	/*	TwoDimensionBone newSelection=null;
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
		}*/
	}
	
	//private boolean backgroundSelected;
	//private TwoDimensionBone boneSelectedOnCanvas;
	
	@Override
	protected void onCanvasDragged(int vectorX, int vectorY) {
		if(activeDataControler!=null){
			activeDataControler.onTouchDragged(vectorX, vectorY, canvasControler.isRightMouse(),canvasControler.getKeyDownState());
			updateCanvas();
		}
		
		
/*		//TODO switch to activeDataControler
		if(boneSelectedOnCanvas!=null){
			TwoDimensionBone bone=boneSelectedOnCanvas;
			
		
			
			//other bone moving
			if(canvasControler.isRightMouse()){
				if(vectorX!=0){
					bone.rotateChildrens(vectorX);
					updateBoneDatas();
				}
				return;
			}
			
			//normal xy-move
			
			List<BonePositionData> posData=Lists.newArrayList();
			
			posData.add(new BonePositionData(bone, bone.getX(), bone.getY(), bone.getX()+vectorX, bone.getY()+vectorY));
			
			
			//move only selection
			if(canvasControler.isShiftKeyDown()){
				for(TwoDimensionBone child:bone.getChildren()){
					posData.add(new BonePositionData(child, child.getX(), child.getY(), child.getX()-vectorX, child.getY()-vectorY));
				
				}
			}
			//TODO move to mosue up
			//undoControler.execute(new BonePositionChangeCommand(posData));
			
			driver.edit(bone);//data is update on editor,but not flash when same
			updateBoneDatas();
		}*/
	}

	@Override
	protected void onCanvasWheeled(int delta) {
		if(activeDataControler!=null){
			activeDataControler.onWhelled(delta, canvasControler.getKeyDownState());
			updateCanvas();
		}
		/*
		if(boneSelectedOnCanvas!=null){
			double scale=0.01;
			if(delta>0){
				scale*=-1;
			}
			boneSelectedOnCanvas.changeChildrenScale(1.0+scale);
			updateBoneDatas();
		}
		*/
	}

	@Override
	protected void onBoneAndAnimationChanged(BoneAndAnimationData data) {
		setNewRoot(data.getBone());
		//TODO support animation?
	}

	@Override
	protected void onBackgroundChanged(ImageDrawingData bg) {
		
		background.setBackgroundData(bg);
		//not select
		updateCanvas();
	}
	
	private Widget createBoneSystemButtons(){
		HorizontalPanel panel=new HorizontalPanel();
	    panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	
	    panel.add(new Label("Bones:"));
	    
	    //northPanel.add(new Label("Load:"));
	    
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
	    northPanel.add(load2);
	    load2.setAccept(FileUploadForm.ACCEPT_TXT,FileUploadForm.ACCEPT_ZIP);
	    */
	   
	    
	    panel.add(new Button("Reload",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onBoneAndAnimationChanged(manager.getBoneAndAnimation().copy());
			}
		}));
 
	    panel.add(new Button("Save",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSaveBone();
			}
		}));
	    
	    downloadLinks = new HorizontalPanel();
	    panel.add(downloadLinks);
	    
	   
	    UndoButtons undoButtons=new UndoButtons(undoControler);
	    panel.add(undoButtons);
	    
		
	    return panel;
	}

	@Override
	protected Widget createCenterPanel() {
		VerticalPanel panel=new VerticalPanel();
		panel.add(createBoneSystemButtons());
		
		
		selectionModel.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				//LogUtils.log("selection-changed");
				//LogUtils.log(selectionModel.getSelectedObject());
				if(twoDimensionBoneEditor.hasValue()){
					driver.flush();
				}
				boneMoveControler.setSelection(selectionModel.getSelectedObject());
				driver.edit(selectionModel.getSelectedObject());
			}
		});
	    
	    
		
	    
	    
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
	    
	    panel.add(upper);
	   
	    
	    panel.add(createBackgroundButtons());
	    panel.add(createBackgroundButtons1());
	    panel.add(createCopyColumnButtons());
	
	
	twoDimensionBoneEditor = new TwoDimensionBoneEditor();    
	    
	updateBoneDatas();//editor call
	
	
	driver.initialize(twoDimensionBoneEditor);
	
	
	driver.edit(null);//dummy?
	
	selectionModel.setSelected(getRootBone(), true);
	
	VerticalPanel editorSpace=new VerticalPanel();
	editorSpace.setHeight("40px");
	editorSpace.add(twoDimensionBoneEditor);
	panel.add(editorSpace);
	panel.add(canvas);
		
		return panel;
	}

	@Override
	protected Widget createWestPanel() {
		VerticalPanel panel=new VerticalPanel();
		panel.setWidth("100%");
		treeModel = new CustomTreeModel();

	    cellTree = new CellTree(treeModel, null);
	    
	    //cellTree.getRootTreeNode()
	    
	    // Add the tree to the root layout panel.
	    panel.add(cellTree);
		return panel;
	}

	@Override
	protected void updateDatas() {
		updateBoneDatas();
		
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
		buttons.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
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

	protected void updateCanvas() {
		checkNotNull(canvas,"somehow canvas is't initialized");
		
		CanvasUtils.clear(canvas);
		
		background.draw(canvas);
		
		
		if(transparentEditCheck.getValue()){
			canvas.getContext2d().setGlobalAlpha(0.3);
		}else{
			canvas.getContext2d().setGlobalAlpha(1);
		}
		
		painter.paintBone();
		canvas.getContext2d().setGlobalAlpha(1);
	}

	//TwoDimensionBone boneSelection;
	
	
	private CheckBox transparentEditCheck;
	private Widget createBackgroundButtons1() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	
		
		
		transparentEditCheck = new CheckBox("Transparent-Bone");
		panel.add(transparentEditCheck);
		transparentEditCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateCanvas();
			}
		});
		
		final HorizontalPanel downloadLinks=new HorizontalPanel();
		
	
		
		Button extractBoneBt=new Button("Paint bone on Image",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				Canvas extractCanvas=CanvasUtils.copyToSizeOnly(canvas,null);
				
				painter.setCanvas(extractCanvas);
				painter.paintBone();
				painter.setCanvas(canvas);
				
				String dataUrl=extractCanvas.toDataUrl();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "bone-bone.png", "bone", true);
				downloadLinks.add(a);
				
				ImageDrawingData data=background.getBackgroundData();
				manager.getFileManagerBar().setBackground("bone-editor", data);
			}
		});
		panel.add(extractBoneBt);
		panel.add(downloadLinks);
		
		return panel;
	}
	
	
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
	

	@Override
	public String getSelectionName() {
		return selectionModel.getSelectedObject()!=null?selectionModel.getSelectedObject().getName():null;
	}

	List<CanvasDrawingDataControler> drawingDataControlers;
	private CanvasDrawingDataControler activeDataControler;
	private Background background;
	private BonePageUndoControler undoControler;
	private BoneMoveControler boneMoveControler;
	private TwoDimensionBoneEditor twoDimensionBoneEditor;
	@Override
	protected void initialize() {
		driver = GWT.create(Driver.class);
		selectionModel = new SingleSelectionModel<TwoDimensionBone>();
		
		TwoDimensionBone rootBone = new TwoDimensionBone("root",0, 0,null);
		
		TwoDimensionBone back=rootBone.addBone(new TwoDimensionBone("back",0, -100));
		TwoDimensionBone chest=back.addBone(new TwoDimensionBone("chest",0, -100));
		
		//allbones = BoneUtils.getAllBone(rootBone);
		
		
		undoControler = new BonePageUndoControler(this);
		
		drawingDataControlers=Lists.newArrayList();
		
		background = new Background();
		background.setEditable(false);
		
		ImageDrawingDataControler controler=new ImageDrawingDataControler(background);
		controler.setUndoControler(undoControler,this);
		
		drawingDataControlers.add(controler);
		
		
		initializeCanvas();
		createBoneControls(rootBone,canvas);
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


	@Override
	protected void onTextureDataChanged(TextureData textureData) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getOwnerName() {
		return "Bone-Editor";
	}
	
	
	
	
	public static class BonePositionData{
		TwoDimensionBone bone;
		public BonePositionData(TwoDimensionBone bone,double beforeX,double beforeY,double afterX,double afterY) {
			this(bone,new Point(beforeX,beforeY),new Point(afterX,afterY));
		}
		public BonePositionData(TwoDimensionBone bone, Point beforePoint, Point afterPoint) {
			super();
			this.bone = bone;
			this.beforePoint = beforePoint;
			this.afterPoint = afterPoint;
		}
		 Point beforePoint;
		 Point afterPoint;
		
		public String toString(){
			return bone.getName()+","+beforePoint.toString()+","+afterPoint.toString();
		}
	}
	
	public static class BonePositionDataTransform implements Function<TwoDimensionBone,BonePositionData>{
		private Map<TwoDimensionBone,BonePositionData> dataMap;
		public BonePositionDataTransform(){
			//LogUtils.log("BonePositionDataTransform");
		}
		/**
		 * somehow iterable working horrible
		 * @param datas
		 */
		public BonePositionDataTransform(List<BonePositionData> datas){
			//LogUtils.log("BonePositionDataTransform:datas");
			dataMap=new HashMap<TwoDimensionBone, BonePage.BonePositionData>();
			for(BonePositionData data:datas){
				dataMap.put(data.bone, data);
			}
		}
		@Override
		public BonePositionData apply(TwoDimensionBone input) {
			if(dataMap!=null){
			
				BonePositionData data=dataMap.get(input);
				
				if(input.getName().equals("head")){
				//	LogUtils.log(data);
				}
				
				data.afterPoint.x=input.getX();
				data.afterPoint.y=input.getY();
				
				return data;
			}else{
				if(input.getName().equals("head")){
				//	LogUtils.log("init-head");
				}
			double x=input.getX();
			double y=input.getY();
			
			
			BonePositionData data=new BonePositionData(input,x,y,0,0);
			
			return data;
			}
		}
	}
	
	
	public static class MovedBone implements Predicate<BonePositionData> {
		@Override
		public boolean apply(BonePositionData input) {
			return !input.afterPoint.equals(input.beforePoint);
		}
	}
	
	
	



	
	public void setSelected(TwoDimensionBone bone) {
		if(bone==null){
			TwoDimensionBone selection=selectionModel.getSelectedObject();
			selectionModel.setSelected(selection, false);
		}else{
			selectionModel.setSelected(bone, true);
		}
	}
	@Override
	public void updateImageDrawingDatas() {
		updateCanvas();
	}





}
