package com.akjava.gwt.skeltalboneanimation.client.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler;
import com.akjava.gwt.lib.client.experimental.CanvasMoveListener;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneTextCell;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.AbstractBonePainter;
import com.akjava.gwt.skeltalboneanimation.client.bones.AnimationFrame;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneControlRange;
import com.akjava.gwt.skeltalboneanimation.client.bones.BonePositionControler;
import com.akjava.gwt.skeltalboneanimation.client.bones.CanvasBoneSettings;
import com.akjava.gwt.skeltalboneanimation.client.bones.SkeletalAnimation;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneConverter;
import com.akjava.lib.common.graphics.Rect;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;


/*
 * select image files and move,turn,scale it.
 */
public class SimpleBoneEditorPage extends VerticalPanel{
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
	 

	public class TwoDimensionBoneEditor extends VerticalPanel implements Editor<TwoDimensionBone>,ValueAwareEditor<TwoDimensionBone>{

private FlushTextBox<TwoDimensionBone> nameBox;
private FlushIntegerBox<TwoDimensionBone> xBox,yBox;


private TwoDimensionBone value;

private void onEditorFlush(){
	//LogUtils.log("flush by self:"+value.getName());
	List<TwoDimensionBone> bones=BoneUtils.getAllBone(getRootBone());
	
	if(bones.contains(value)){//when load new data
	value.setX(xBox.getValue());
	value.setY(yBox.getValue());
	
	String newName=nameBox.getValue();
	
	if(BoneUtils.existBoneByName(getRootBone(), newName,value)){
		Window.alert(newName+" is already exist.\n" +
				"name could not be updated.");
		nameBox.setValue(value.getName());
	}else{
		value.setName(newName);
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
			
			panel.add(new Label("Y:"));
			yBox=new FlushIntegerBox<TwoDimensionBone>(this);
			panel.add(yBox);
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
			}
	}
	
private Canvas canvas;
private CanvasDragMoveControler canvasControler;
private BonePositionControler bonePositionControler;


private final SingleSelectionModel<TwoDimensionBone> selectionModel = new SingleSelectionModel<TwoDimensionBone>();
	public SimpleBoneEditorPage(DockLayoutPanel root){
		
		
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
					doRemoveBone();
				}
			}));
		    upper.add(new Label("Load:"));
		    FileUploadForm load=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
				
				@Override
				public void uploaded(File file, String text) {
					doLoadBone(text);
				}
			}, true);
		    load.setAccept(FileUploadForm.ACCEPT_TXT);
		    
		    upper.add(load);
		    upper.add(new Button("Save",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doSaveBone();
				}
			}));
		    root.addNorth(upper, 32);
		    downloadLinks = new HorizontalPanel();
		    upper.add(downloadLinks);
		    /*
		//create widget
		add(createFirstColumnButtons());
		*/
		
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
	protected void doRemoveBone() {
		//cellTree.
		final TwoDimensionBone selection=selectionModel.getSelectedObject();
		if(selection==null || selection==getRootBone()){
			return;
		}
		selection.getParent().getChildrens().remove(selection);
		
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
	
	private AbstractBonePainter painter;
	private void createBoneControls(TwoDimensionBone rootBone,final Canvas canvas){
		settings=new CanvasBoneSettings(canvas, rootBone);
		
		final SkeletalAnimation animations=new SkeletalAnimation("test", 33.3);
		
		singleFrame = BoneUtils.createEmptyAnimationFrame(getRootBone());
		animations.add(singleFrame);
		
		
		
		bonePositionControler=new BonePositionControler(settings);
		bonePositionControler.setCollisionOrderDesc(true);//root is last select-target
		
		painter = new AbstractBonePainter(bonePositionControler) {
			
			@Override
			public void paintBone(String name, String parent,int startX, int startY, int endX, int endY, double angle) {
				Rect rect=Rect.fromCenterPoint(endX,endY,10,10);
				
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
				
				if(canvasSelection!=null && name.equals(canvasSelection.getName())){
					selectionColor="#0a0";
				}
				
				if(selectionModel.getSelectedObject()!=null && name.equals(selectionModel.getSelectedObject().getName())){
					rect.expand(2, 2);
					//canvas.getContext2d().setStrokeStyle("#0f0");
					
					RectCanvasUtils.stroke(rect,canvas,selectionColor);
				}
				//
				
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
	    		dataProvider.setList(bone.getChildrens());
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
	      dataProvider.setList(bone.getChildrens());
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
	     return bone.getChildrens().size()==0;
	    }
	  }
	
	protected void onCanvasWheeled(int deltaY) {
		// TODO Auto-generated method stub
		
	}


	protected void onCanvasDragged(int vectorX, int vectorY) {
		if(canvasSelection!=null){
			TwoDimensionBone bone=canvasSelection;
			
			if(bone==getRootBone()){
				return;//rootBone is fixed,so far.
			}
			
			bone.setX(bone.getX()+vectorX);
			bone.setY(bone.getY()+vectorY);
			
			
			driver.edit(bone);//data is update on editor,but not flash when same
			updateBoneDatas();
		}
	}


	private TwoDimensionBone canvasSelection;
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
			canvasSelection=newSelection;
			selectionModel.setSelected(newSelection, true);//select by click
		}else{
			canvasSelection=null;
			updateCanvas();
		}
	}


	private HorizontalPanel createFirstColumnButtons(){
		HorizontalPanel buttons=new HorizontalPanel();
		
		return buttons;
	}


	private void updateCanvas() {
		CanvasUtils.clear(canvas);
		painter.paintBone();
	}

	TwoDimensionBone boneSelection;
	
	
	
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




}
