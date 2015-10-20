package com.akjava.gwt.skeltalboneanimation.client.page.html5app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.Blob;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.inpaint.client.InpaintEngine;
import com.akjava.gwt.inpaint.client.MaskData;
import com.akjava.gwt.inpaint.client.InpaintEngine.InpaintListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.GWTUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.functions.ImageDrawingDataIdFunction;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataChangeListener;
import com.akjava.gwt.skeltalboneanimation.client.page.ListenerSystem.DataOwner;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.PointShape;
import com.akjava.gwt.skeltalboneanimation.client.page.colorpick.ColorPickPage;
import com.akjava.gwt.skeltalboneanimation.client.page.colorpick.ColorPickPage.ImageSender;
import com.akjava.gwt.skeltalboneanimation.client.predicates.ImageDrawingDataPredicates.NotExistInIds;
import com.akjava.gwt.skeltalboneanimation.client.ui.LabeledInputRangeWidget;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/*
 * 
 * explain of links
 * top link to anchor of app list to show other apps
 * 
 * app link is directly apps,no annoying description page
 */
public class TransparentItPage extends Html5DemoEntryPoint {



	

	public TransparentItPage(MainManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	private Canvas overlayCanvas;


	private XYPoint lastPoint;
	private DataUriCommand currentCommand;
	private long lastAvatorUpdate;



	private int penSize=16;
	public static final int MODE_ERASE=0;
	public static final int MODE_BLACK=1;
	public static final int MODE_WHITE=2;
	public static final int MODE_COLOR=3;
	public static final int MODE_UNERASE=4;
	public static final int MODE_PICK=5;
	private int penMode=MODE_ERASE;
	private boolean mouseMoved;
	
	private Canvas pixelCanvas;
	
	
	

	private DockLayoutPanel dock;
	//private HorizontalPanel topPanel;
	private EasyCellTableObjects<ImageElementData2> easyCellTableObjects;



	private Button saveBt;



	private Button saveWithBgBt;



	private int canvasWidth;



	private ColorBox bgColorPicker;


	protected Boolean drawShape;


	protected int resetMode;
	private static final int RESET_MODE_TEXTURE=0;
	private static final int RESET_MODE_BACKGROUND_CLIP=1;
	private static final int RESET_MODE_BACKGROUND_ALL=2;
	
	public void removeItemById(String id){
		for(ImageElementData2 data:findDataByName(id).asSet()){
			easyCellTableObjects.removeItem(data);
		}
	}
	
	public void clearAll(){
		easyCellTableObjects.clearAllItems();
	}
	public void addItem(Supplier<String> idSupplier,ImageDrawingData drawingData,String dataUrl,PointShape pointShape){
		
		final ImageElementData2 data=new ImageElementData2(idSupplier,drawingData,dataUrl,pointShape);
		
		easyCellTableObjects.addItem(data);
		easyCellTableObjects.setSelected(data, true);
		
		
		/*
		//stack on mobile,maybe because of called async method
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				easyCellTableObjects.setSelected(data, true);
			}
		});
		*/
	}
	
	@Override
	public Panel initializeWidget() {
		DropDockDataUrlRootPanel root=new DropDockDataUrlRootPanel(Unit.PX,false){
			
			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
					TransparentItPage.this.loadFile(file, dataUrl);
				}
			}
			
			
		};
		root.setFilePredicate(FilePredicates.getImageExtensionOnly());
		
		
		
		
		
		dock = new DockLayoutPanel(Unit.PX);
		//dock.setSize("100%", "100%");
		root.add(dock);
		
		/*
		topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		topPanel.setStylePrimaryName("bg1");
		topPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topPanel.setSpacing(1);
		dock.addNorth(topPanel,30);
		
		
		topPanel.add(createTitleWidget());
		
		topPanel.add(new Anchor(textConstants.Help(), "transparent_help.html"));
		*/
		
		
		VerticalPanel controler=new VerticalPanel();
		controler.setSpacing(4);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				loadFile(file, value);
			}
		}, true);
		
		
		HorizontalPanel fileUps=new HorizontalPanel();
		controler.add(fileUps);
		fileUps.add(upload);
		
		
		
		backgroundList=new ListBox();
		backgroundList.addItem(textConstants.transparent());
		backgroundList.addItem(textConstants.black());
		backgroundList.addItem(textConstants.white());
		backgroundList.addItem(textConstants.selectcolor());
		
	
		HorizontalPanel bgPanel=new HorizontalPanel();
		bgPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(bgPanel);
		
		bgPanel.add(new Label(textConstants.Background()));
		
		bgPanel.add(backgroundList);
		backgroundList.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateBgStyle();;
			}
		});
		
		bgColorPicker = new ColorBox();
		bgColorPicker.setValue("#00ff00");
		bgPanel.add(bgColorPicker);
		Button updateBg=new Button(textConstants.update(),new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateBgStyle();
			}
		});
		bgPanel.add(updateBg);
		
		bgPanel.add(new Label(textConstants.Scale()));
		//
		ValueListBox<Integer> scaleBox=new ValueListBox<Integer>(new Renderer<Integer>() {

			@Override
			public String render(Integer object) {
	
				return ""+object;
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				
			}
		});
		
		
		scaleBox.setValue(1);
		scaleBox.setAcceptableValues(Lists.newArrayList(1,2,4,8));
		bgPanel.add(scaleBox);
		scaleBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				updateScale(event.getValue());
			}
		});
		
		bgPanel.add(new Label(textConstants.x_zoom()));
		
		int cbase=18;
		canvasWidth = cbase*16;
		//int ch=cbase*9;//what?
		zoomSize = 1;
	
		//size choose
		HorizontalPanel sizes=new HorizontalPanel();
		sizes.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(sizes);
		
		Label penSizeLabel=new Label(textConstants.Pen_Size());
		sizes.add(penSizeLabel);
		final ValueListBox<Integer> sizeListBox=new ValueListBox<Integer>(new Renderer<Integer>() {

			@Override
			public String render(Integer object) {
				
				return ""+object;
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				
			}
		});
		
		List<Integer> sizeList=Lists.newArrayList(1,2,3,4,5,6,8,12,16,24,32,48,64);
		sizeListBox.setValue(penSize);
		sizeListBox.setAcceptableValues(sizeList);
		sizeListBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				penSize=event.getValue();
			}
			
		});
		sizes.add(sizeListBox);
		
		sizes.add(new Label(textConstants.Similar_Color()+":"));
		
		final Label rangeLabel=new Label();
		rangeLabel.setText("10");
		rangeLabel.setWidth("20px");
		final InputRangeWidget colorRange=HTML5InputRange.createInputRange(1, 80, 10);
		colorRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				rangeLabel.setText(""+newValue);
			}
		});
		sizes.add(rangeLabel);
		colorRange.setWidth("80px");
		sizes.add(colorRange);
		 execTransparentBt = new Button(textConstants.ExecTransparent(),new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Timer timer=new Timer(){
					@Override
					public void run() {
						execTransparentBt.setEnabled(false);
						startCreateCommand();
						
						//convert to imagedata
						ImageData imageData=CanvasUtils.getImageData(pixelCanvas, true);
						//do transparnet
						int[] rgb=ColorUtils.toRGB(colorPicker.getValue());
						
						Uint8Array maskArray=createMaskBySimilarColor(imageData, rgb[0],rgb[1],rgb[2], colorRange.getValue());
						for(int x=0;x<imageData.getWidth();x++){
							for(int y=0;y<imageData.getHeight();y++){
								int mask=maskArray.get(y*imageData.getWidth()+x);
								if(mask==1){
									imageData.setAlphaAt(0, x, y);
								}
							}
						}
						//set imagedata
						CanvasUtils.copyTo(imageData, pixelCanvas);
						
						//store
						String dataUrl=pixelCanvas.toDataUrl();
						endCreateCommand(dataUrl);
						updateCurrentSelectionDataUrl(dataUrl);
						execTransparentBt.setEnabled(true);
					}
				};
				timer.schedule(50);
			}
		});
		sizes.add(execTransparentBt);
		
		//pen choose
		HorizontalPanel pens=new HorizontalPanel();
		pens.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		controler.add(pens);
		final RadioButton eraseR=new RadioButton("pens");
		pens.add(eraseR);
		eraseR.setValue(true);
		eraseR.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_ERASE;
			}
		});
		pens.add(new Label(textConstants.Erase()));
		final RadioButton uneraseR=new RadioButton("pens");
		uneraseR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_UNERASE;
			}
		});

		pens.add(uneraseR);
		pens.add(new Label(textConstants.UnErase()));
		RadioButton blackR=new RadioButton("pens");
		pens.add(blackR);
		pens.add(new Label(textConstants.black()));
		blackR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_BLACK;
			}
		});
		
		
		RadioButton whiteR=new RadioButton("pens");
		pens.add(whiteR);
		pens.add(new Label(textConstants.white()));
		whiteR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_WHITE;
			}
		});
		
		RadioButton customR=new RadioButton("pens");
		pens.add(customR);
		pens.add(new Label(textConstants.selectcolor()));
		customR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_COLOR;
			}
		});
		RadioButton pickR=new RadioButton("pens");
		pens.add(pickR);
		pens.add(new Label(textConstants.PickColor()));
		pickR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_PICK;
			}
		});
		
		
		colorPicker = new ColorBox();
		colorPicker.setValue("#ff0000");
		pens.add(colorPicker);
		
		
		
		
		pixelCanvas = Canvas.createIfSupported();//pixel handling
		
		canvas=Canvas.createIfSupported();//final mixed result
		
		canvas.setStylePrimaryName("transparent_bg");
		
		GWTHTMLUtils.disableSelectionStart(canvas.getElement());
		
		overlayCanvas=Canvas.createIfSupported();
		
		/*
		canvas.addTouchCancelHandler(new TouchCancelHandler() {
			
			@Override
			public void onTouchCancel(TouchCancelEvent event) {
				if(lockCheck.getValue()){
					//event.preventDefault();
					//event.stopPropagation();
				}
				LogUtils.log("touch-cancel");
			}
		});
		*/
		canvas.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				int code=event.getNativeKeyCode();
				LogUtils.log("transparent input key code for shortcut:"+code);
				if(code=='1'){
					sizeListBox.setValue(1);
				}
				if(code=='2'){
					sizeListBox.setValue(4);
				}
				if(code=='3'){
					sizeListBox.setValue(8);
				}
				if(code=='4'){
					sizeListBox.setValue(16);
				}
				if(code=='5'){
					sizeListBox.setValue(32);
				}
				if(code=='6'){
					sizeListBox.setValue(64);
				}
				if(code=='7'){
					sizeListBox.setValue(128);
				}
				penSize=sizeListBox.getValue();
				
				if(code==KeyCodes.KEY_TAB){
					if(penMode==MODE_ERASE){
						uneraseR.setValue(true);
						penMode=MODE_UNERASE;
					}else{
						eraseR.setValue(true);
						penMode=MODE_ERASE;
					}
				}
			}
		});
		
		
		canvas.addTouchMoveHandler(new TouchMoveHandler() {
			
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				
				if(!scrollLockCheck.getValue()){//for mobile
					return;
				}
				
				int[] pt=touchToPoint(event.getTouches());
				event.preventDefault();
				//event.stopPropagation();
			
				perfomeMoveEvent(pt[0], pt[1]);
				
				/*
				if(lockCheck.getValue()){
					
					int[] pt=touchToPoint(event.getTouches());
					event.preventDefault();
					//event.stopPropagation();
				
					perfomeMoveEvent(pt[0], pt[1]);
					LogUtils.log("move:"+pt[0]+","+pt[1]);
					
					event.preventDefault();
					//event.stopPropagation();
					
				}*/
				
				//LogUtils.log("touch-move:");
			}
		});
		
		
		
		canvas.addTouchStartHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				
				if(scrollLockCheck.getValue()){//on lock mode stop it
					event.preventDefault();
				}else{
					return;
				}
				
				int[] pt=touchToPoint(event.getTouches());
				//LogUtils.log("start:"+pt[0]+","+pt[1]);
				perfomeDownEvent(pt[0], pt[1]);
				//LogUtils.log("touch-start");
				
			}
		});
		/*
		*/
		
		
		canvas.addTouchEndHandler(new TouchEndHandler() {
			
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				if(!scrollLockCheck.getValue()){//for mobile
					return;
				}
				
				event.preventDefault();
				perfomeUpEvent();
				
			}
		});
		
		
		canvas.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				
				//LogUtils.log("mouse-move");
				perfomeMoveEvent(event.getX(),event.getY());
			}
		});
		
		canvas.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
			
				if(penMode==MODE_PICK){
					doPick(event.getX(), event.getY());
					startCreateCommand();
					return ;
				}
				
				//LogUtils.log("mouse-down");
				mouseRight=event.getNativeButton()==NativeEvent.BUTTON_RIGHT;
				perfomeDownEvent(event.getX(),event.getY());
				
			}
		});
		canvas.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				
				//LogUtils.log("mouse-out");
				perfomeUpEvent();
			}
		});
		
		canvas.addMouseUpHandler(new MouseUpHandler() {
			
			@Override
			public void onMouseUp(MouseUpEvent event) {
				//LogUtils.log("mouse-up");
				//LogUtils.log(event.getNativeEvent());
				perfomeUpEvent();
			}
		});
		
		//stop context menu;
		canvas.addDomHandler(new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.stopPropagation();
				event.preventDefault();
			}
		}, ContextMenuEvent.getType());
		

		
	
	
		
		
		
		
		HorizontalPanel buttons=new HorizontalPanel();
		buttons.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(buttons);
		
	
		

		
		
		
		
	
		
		scrollLockCheck = new CheckBox(textConstants.ScrollLock());
		scrollLockCheck.setTitle(textConstants.stop_scroll_on_mobile());
		/*//some how not work
		lockCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				boolean enabled=canvasScroll.setTouchScrollingDisabled(lockCheck.getValue());
				
				//LogUtils.log("result:"+enabled);
			}
		});
		*/
		scrollLockCheck.setValue(true);
		buttons.add(scrollLockCheck);
		
		
		
		
		
		undoBt = new Button(textConstants.Undo());
		undoBt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				currentCommand.undo();
				undoBt.setEnabled(false);
				redoBt.setEnabled(true);
				
			}
		});
		buttons.add(undoBt);
		undoBt.setEnabled(false);
		
		redoBt = new Button(textConstants.Redo());
		redoBt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				currentCommand.redo();
				undoBt.setEnabled(true);
				redoBt.setEnabled(false);
				
			}
		});
		redoBt.setEnabled(false);
		buttons.add(redoBt);
		
		
		
		
		
		
		
		reset = new Button(textConstants.reset());
		reset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doReset();
			}
		});
		buttons.add(reset);
		buttons.add(new Label("Mode:"));
		final ListBox resetModeList=new ListBox();
		resetModeList.addItem("Texture");
		resetModeList.addItem("BG(Clip)");
		resetModeList.addItem("BG(All)");
		buttons.add(resetModeList);
		resetModeList.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				resetMode=resetModeList.getSelectedIndex();
			}
		});
		
		HorizontalPanel buttons2=new HorizontalPanel();
		controler.add(buttons2);
		
		Button syncBt=new ExecuteButton("Transfer as Texture") {
			
			@Override
			public void executeOnClick() {
				doTransferSyncAsTexture();
			}
		};
		buttons2.add(syncBt);
		
		Button sendBt=new ExecuteButton("Send") {
			
			@Override
			public void executeOnClick() {
				doSendImage();
			}
		};
		buttons2.add(sendBt);
		
		buttons2.add(new DownloadButtonAndLink(pixelCanvas,"download","image.png"));
		
		//TODO selection?
		
		saveBt = new Button(textConstants.Save());
		saveBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createDownloadImage(false);
			}
		});
		//buttons.add(saveBt);
		
		//TODO remove
		saveWithBgBt = new Button(textConstants.SaveWithBg());
		saveWithBgBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createDownloadImage(true);
			}
		});
		
		//buttons.add(saveWithBgBt);
		
		
		HorizontalPanel exbuttons=new HorizontalPanel();
		exbuttons.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(exbuttons);
		HorizontalPanel exbuttons1=new HorizontalPanel();
		exbuttons1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(exbuttons1);
		
		//TODO below
		CheckBox drawShapeCheck=new CheckBox("show shape-line");
		drawShapeCheck.setValue(true);
		drawShapeCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				drawShape=event.getValue();
				updateCanvas();
			}
			
		});
		exbuttons.add(drawShapeCheck);
		//TODO as canvas-layer,this is just draw line can't hide
		
		//this is temporaly and not wrap command
		Button drawShapeBt=new Button("draw-shape",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(selection!=null){
					startCreateCommand();
					Optional<PointShape> optional=selection.getPointShape();
					if(optional.isPresent()){
						optional.get().stroke(colorPicker.getValue(), pixelCanvas);
					updateCurrentSelectionDataUrl(pixelCanvas.toDataUrl());
					endCreateCommand(pixelCanvas.toDataUrl());
					
					updateCanvas();
					}else{
						LogUtils.log("this selection has no pointShape");
					}
				}
			}
		});
		exbuttons.add(drawShapeBt);
		
		Button inpaintBt=new Button("Inpaint-Clip",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doInpaint(true);
			}
		});
		exbuttons1.add(inpaintBt);
		
		Button inpaintAllBt=new Button("Inpaint-All",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doInpaint(false);
			}
		});
		exbuttons1.add(inpaintAllBt);
		
		LabeledInputRangeWidget range=new LabeledInputRangeWidget("radius",1,20,1);
		exbuttons1.add(range);
		range.setValue(3);
		range.getRange().addValueChangeHandler(new ValueChangeHandler<Number>() {

			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				inpaintRadius=event.getValue().intValue();
			}
		});
		range.setWidgetWidthPx(40,80,20);
		
		HorizontalPanel exbuttons2=new HorizontalPanel();
		exbuttons2.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(exbuttons2);
		
		LabeledInputRangeWidget expandRange=new LabeledInputRangeWidget("expand",0,40,1);
		exbuttons2.add(expandRange);
		expandRange.setValue(0);
		expandRange.getRange().addValueChangeHandler(new ValueChangeHandler<Number>() {

			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				expand=event.getValue().intValue();
			}
		});
		expandRange.setWidgetWidthPx(40,100,20);
		expandRange.setTitle("just expand edge");
		
		LabeledInputRangeWidget fadeRange=new LabeledInputRangeWidget("fade",0,40,1);
		exbuttons2.add(fadeRange);
		fadeRange.setValue(0);
		fadeRange.getRange().addValueChangeHandler(new ValueChangeHandler<Number>() {

			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				fade=event.getValue().intValue();
			}
		});
		fadeRange.setWidgetWidthPx(40,100,20);
		fadeRange.setTitle("almost same expand.but mix original later");
		
		//controler,fist,pre,next,auto-play + time,clear
		
		/*
		FileUploadForm uploadFiles=FileUtils.createMultiFileUploadForm(new DataURLsListener() {
			@Override
			public void uploaded(final List<File> files, final List<String> values) {
				log("uploaded:"+values.size());
				String url=values.remove(0);
				final ImageElementLoader loader=new ImageElementLoader();
				
				final ImageElementListener listener= new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						overlayCanvas.getContext2d().save();
						clearCanvas(overlayCanvas);
						overlayCanvas.getContext2d().restore();
						overlayCanvas.getContext2d().save();
						drawFitCenter(overlayCanvas, element);
						overlayCanvas.getContext2d().restore();
						String resizedImage=overlayCanvas.toDataUrl("image/png");//origin
						
						
						ImageItem item=new ImageItem(resizedImage);
						container.add(item);
						if(values.size()>0){
							String url=values.remove(0);
							loader.load(url, this);
						}
					}
				};
				
				loader.load(url,listener);
			}
		}, true);
		listPanel.add(uploadFiles);
		*/
		
	
		
		
		
		
		
		
		
		
		
		downloadArea = new VerticalPanel();
		downloadArea.setSpacing(2);

		fileUps.add(downloadArea);
		
		SimpleCellTable<ImageElementData2> cellTable = new SimpleCellTable<ImageElementData2>(999) {
			@Override
			public void addColumns(CellTable<ImageElementData2> table) {
				 ButtonColumn<ImageElementData2> removeBtColumn=new ButtonColumn<ImageElementData2>() {
						@Override
						public void update(int index, ImageElementData2 object,
								String value) {
								easyCellTableObjects.removeItem(object);
								//when closed need undo
								undoBt.setEnabled(false);
								redoBt.setEnabled(false);
						}
						@Override
						public String getValue(ImageElementData2 object) {
							 return "X";
						}
					};
					table.addColumn(removeBtColumn);
					
					/*
					 ButtonColumn<GridImageData> saveBtColumn=new ButtonColumn<GridImageData>() {
							@Override
							public void update(int index, GridImageData object,
									String value) {
									createDownloadImage();
							}
							
							@Override
							public String getValue(GridImageData object) {
								 return "Save";
							}
						};
					table.addColumn(saveBtColumn);
					table.setColumnWidth(saveBtColumn, "50px");
					*/
					
					/*
					 ButtonColumn<GridImageData> regridBtColumn=new ButtonColumn<GridImageData>() {
							@Override
							public void update(int index, GridImageData object,
									String value) {
									regrid();
							}
							
							@Override
							public String getValue(GridImageData object) {
								 return "RegGrid";
							}
						};
					table.addColumn(regridBtColumn);
					table.setColumnWidth(regridBtColumn, "50px");
					*/
				
					
					
				    TextColumn<ImageElementData2> fileInfoColumn = new TextColumn<ImageElementData2>() {
					      public String getValue(ImageElementData2 value) {
					    	  
					    	  return value.getId();
					      }
					    };
					    table.addColumn(fileInfoColumn,textConstants.Name());
					    
					    
					    ButtonColumn<ImageElementData2> copyBtColumn=new ButtonColumn<ImageElementData2>() {
							@Override
							public void update(int index, ImageElementData2 object,
									String value) {
									easyCellTableObjects.addItem(object.copyAsFileData());
							}
							@Override
							public String getValue(ImageElementData2 object) {
								 return textConstants.Copy();
							}
						};
						table.addColumn(copyBtColumn);
					    
					    
			}
		};
		
		
		DockLayoutPanel eastPanel=new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 280);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		easyCellTableObjects=new EasyCellTableObjects<ImageElementData2>(cellTable,false) {
			@Override
			public void onSelect(ImageElementData2 selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		
		
		
		
		
		canvasScroll = new ScrollPanel();
		
		
		canvasScroll.setWidth("100%");
		canvasScroll.setHeight("100%");
		dock.add(canvasScroll);
		
		
		
		
		canvasScroll.add(canvas);
		canvas.setVisible(false);
		/*
		
		*/
		
		return root;
	}
	private Canvas canvas;
	
	private ColorPickPage colorPickPage;
	public ColorPickPage getColorPickPage() {
		return colorPickPage;
	}

	public void setColorPickPage(ColorPickPage colorPickPage) {
		this.colorPickPage = colorPickPage;
	}

	protected void doSendImage() {
		if(colorPickPage==null){
			LogUtils.log("doSendImage: colorPickPage not setted");
		return;
		}
		
		if(getSelection()==null){
			LogUtils.log("doSendImage: no selection");
				return;
			
		}
		
		String dataUrl=getSelection().getDataUrl();
		
			colorPickPage.sendImage(new ImageSender() {
				
				@Override
				public void sendBack(String fileName, final ImageData imageData) {
					//final Stopwatch watch=Stopwatch.createStarted();
					//LogUtils.log("send backed");
					Optional<ImageElementData2> optional=findDataByName(fileName);
					
					if(!optional.isPresent()){
						LogUtils.log("can't find data:"+fileName);
						
					}
					
					for(final ImageElementData2 data2:optional.asSet()){
						//LogUtils.log("set-selected:");
						easyCellTableObjects.setSelected(data2, true);
						Scheduler.get().scheduleFinally(new ScheduledCommand() {
							@Override
							public void execute() {
								if(getSelection()==data2){
							 
								startCreateCommand();
								
								// i wonder send back is slow?but it's ok
								//LogUtils.log("paint:"+"start-paint:"+watch.elapsed(TimeUnit.MILLISECONDS));
								//CanvasUtils.clear(pixelCanvas);
								ImageDataUtils.putImageData(imageData, pixelCanvas);
								//CanvasUtils.drawDataUrl(pixelCanvas, dataUrl);
								String dataUrl=pixelCanvas.toDataUrl();
								updateCurrentSelectionDataUrl(dataUrl);
								endCreateCommand(dataUrl);
								updateCanvas();
								}else{
									LogUtils.log("invalidly selection not changed");
								}
								
							}
						});
					}
				}
			},getSelection().getId(),  dataUrl);
			//LogUtils.log("sended:"+getSelection().getId());
			manager.selectTab(5);
		
	}

	int inpaintRadius;
	
	protected void doInpaint(final boolean clip) {
		if(selection!=null){
			//currentCommand=null;//store current data-url
			startCreateCommand();
			
			ImageElementUtils.createWithLoader(selection.getDataUrl(),new ImageElementListener() {
				
				@Override
				public void onLoad(ImageElement image) {
					InpaintEngine engine=new InpaintEngine();
					
					
					List<MaskData> masks=Lists.newArrayList(new MaskData().fade(fade).expand(expand));
					engine.doInpaint(image, inpaintRadius, masks, new InpaintListener() {
						
						@Override
						public void createMixedImage(ImageData dataUrl) {
							pixelCanvas.getContext2d().save();
							if(clip){
								for(PointShape pointShape:selection.getPointShape().asSet()){
									pointShape.clip(pixelCanvas);
								}
								
							}
							CanvasUtils.drawCenter(pixelCanvas, CanvasUtils.createCanvas(null, dataUrl).getCanvasElement());
							
							updateCurrentSelectionDataUrl(pixelCanvas.toDataUrl());
							
							pixelCanvas.getContext2d().restore();
						}
						
						@Override
						public void createInpainteMaks(ImageData dataUrl) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void createInpaintImage(ImageData dataUrl) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void createGreyScaleMaks(ImageData dataUrl) {
							// TODO Auto-generated method stub
							
						}
					});
					endCreateCommand(pixelCanvas.toDataUrl());
					
					updateCanvas();
				}
				
				@Override
				public void onError(String url, ErrorEvent event) {
					LogUtils.log("some how load faild:"+url);
				}
			});
			
		}
	}
	protected void doTransferSyncAsTexture() {
		final TextureData textureData=toTextureData();
		manager.getFileManagerBar().setTexture("transparentIt", textureData);
	}
	protected TextureData toTextureData() {
		
			List<ImageDrawingData> datas=Lists.newArrayList();
			for(ImageElementData2 element:easyCellTableObjects.getDatas()){
				Optional<ImageDrawingData> optional=element.getImageDrawingData();
				if(optional.isPresent()){
					ImageDrawingData data=optional.get();
					data.setImageElement(ImageElementUtils.create(element.getDataUrl()));
					datas.add(data);
					
					//before to texture data.clip data saved and texture data renamed.
					data.setId(element.getId());//newest id
					data.setImageName(data.getId()+".png");
					
				}else{
					LogUtils.log(element.getId()+" has no ImageDrawingData.skipped");
				}
			}
			
			
			/*LogUtils.log("ImageElementData2:");
			for(ImageElementData2 data:easyCellTableObjects.getDatas()){
				LogUtils.log(data.getId());
			}
			
			LogUtils.log("ImageDrawingData:");
			for(ImageDrawingData data:datas){
				LogUtils.log(data.getId());
			}*/
			
			
			TextureData oldTextureData=manager.getTextureDataWithNewestBone();
			
			
			
			if(oldTextureData!=null){
				
				/*LogUtils.log("TextureData:");
				for(ImageDrawingData data:oldTextureData.getDatas()){
					LogUtils.log(data.getId());
				}*/
				
				
				List<String> relatedTextureIds=FluentIterable.from(datas).transform(new ImageDrawingDataIdFunction()).filter(Predicates.notNull()).toList();
				
				List<ImageDrawingData> remains=FluentIterable.from(oldTextureData.getDatas()).filter(new NotExistInIds(relatedTextureIds)).toList();
				Iterables.addAll(datas, remains);
				if(remains.size()>0){
					LogUtils.log(remains.size()+" texture data merged");
				}
			}else{
				LogUtils.log("texture-data is null.skip merge");
			}
			
			
			
			//TODO merge already exist textures.
			
			TextureData textureData=new TextureData();
			//textureData.setBone(boneControler.getBone());
			textureData.setImageDrawingDatas(datas);
			return textureData;
		}
	
	private void updateBgImage(ImageElement bgImage){
		pixelCanvas.removeStyleName("newbg");
		if(injectedBgCss!=null){
			injectedBgCss.removeFromParent();
		}
		
		if(bgImage!=null){
			int w=(int) (bgImage.getWidth()*currentScale);
			int h=(int) (bgImage.getHeight()*currentScale);
			String css=".newbg{"+"background-image: url(\""+bgImage.getSrc()+"\");background-size:"+w+"px "+h+"px;"+"}";
			injectedBgCss = StyleInjector.injectStylesheet(css);
			
			pixelCanvas.addStyleName("newbg");
			
			updateDrawingCanvas(false);
		}
	}
	
	private boolean isTransparent(){
		return backgroundList.getSelectedIndex()==0;
	}
	private String getBgColor(){
		if(backgroundList.getSelectedIndex()==2){
			return "#ffffff";
		}else if(backgroundList.getSelectedIndex()==3){
			return bgColorPicker.getValue();
		}else{
			return "#000000";
		}
	}
	
	StyleElement bgElement;
	private void updateBgStyle(){
		if(isTransparent()){
			canvas.setStylePrimaryName("transparent_bg");
		}else{
			if(bgElement!=null){
				bgElement.removeFromParent();
			}
			String css=".colorbg{margin-left: 8px;padding: 0px;background:"+getBgColor()+";}";
			//LogUtils.log(css);
			bgElement = StyleInjector.injectStylesheet(css);
			
			canvas.setStylePrimaryName("colorbg");
			
		}
	}
	
	private double currentScale=1;
	protected void updateScale(Integer value) {
		currentScale=value;
		//zoomSize=value;
		if(selection!=null){
			updateBgImage(bgImage);
			updateDrawingCanvas(false);
		}
	}
	private StyleElement injectedBgCss;

	
	private int[] touchToPoint(JsArray<Touch> touchs){
		
		//JsArray<Touch> touchs=event.getTouches();
		if(touchs.length()>0){
			Touch touch=touchs.get(0);
			int x=touch.getRelativeX(pixelCanvas.getElement());
			int y=touch.getRelativeY(pixelCanvas.getElement());
			return new int[]{x,y};
		}
		
		return null;
	}
	
	private void perfomeMoveEvent(int mx,int my){

		mx/=currentScale;
		my/=currentScale;
	
		if(mouseDown){
		mouseMoved=true;
		int x=mx*zoomSize;
		int y=my*zoomSize;
		XYPoint newPoint=new XYPoint(x,y);
		
		
		switch(penMode){
		case MODE_ERASE:
			erase(lastPoint,newPoint);
			break;
		case MODE_UNERASE:
			unerase(lastPoint,newPoint);
			break;
		case MODE_BLACK:
			drawLine(lastPoint,newPoint,"#000");
			break;
		case MODE_WHITE:
			drawLine(lastPoint,newPoint,"#fff");
			break;
		case MODE_COLOR:
			drawLine(lastPoint,newPoint,colorPicker.getValue());
			break;
		}
		
		
		
		lastPoint=newPoint;
		
		long c=System.currentTimeMillis();
		
		if(lastAvatorUpdate+200<c){
			lastAvatorUpdate=c;
			
			}
		
		updateCanvas();
		}
		
		
	}
	
	private void perfomeDownEvent(int x,int y){

		
		x/=currentScale;
		y/=currentScale;
		
		mouseDown=true;
		lastPoint=mouseToXYPoint(x,y);
		
		
		
		
		startCreateCommand();//stop using timer,some time end command called before start command;
		
		/*
		//don't do heavy things in onStart
		Timer timer=new Timer(){//TODO fix it
			@Override
			public void run() {
				startCreateCommand();//for undo
			}
		};
		timer.schedule(50);
		*/
	}
	public ImageElementData2 getSelection(){
		return easyCellTableObjects.getSelection();
	}
	
	private void startCreateCommand(){
		//for cutdown todataUrl cost,use last command data-url
		
		DataUriCommand newCommand=new DataUriCommand();
		newCommand.setBeforeSelection(getSelection());
		if(currentCommand!=null && currentCommand.getAfterUri()!=null && !currentCommand.isUndoed()){
			newCommand.setBeforeUri(currentCommand.getAfterUri());
		}else{
			newCommand.setBeforeUri(selection.getDataUrl());
		}
		currentCommand=newCommand;
	}

	private void endCreateCommand(String dataUrl){
		if(currentCommand!=null){
			currentCommand.setAfterSelection(getSelection());
			currentCommand.setAfterUri(dataUrl);
			}
		undoBt.setEnabled(true);
	}

	private void updateCurrentSelectionDataUrl(String dataUrl){
		if(selection!=null){
			selection.setDataUrl(dataUrl);
		}
	}
	
	/**
	 * can't get up position
	 */
	private void perfomeUpEvent(){

		
		if(!mouseMoved){
			if(lastPoint!=null){
				
			
			XYPoint dummyPt=new XYPoint(lastPoint.getX()+1, lastPoint.getY()+1);
			
			switch(penMode){
			case MODE_ERASE:
				erase(lastPoint,dummyPt);
				break;
			case MODE_UNERASE:
				unerase(lastPoint,dummyPt);
				break;
			case MODE_BLACK:
				drawLine(lastPoint,dummyPt,"#000");
				break;
			case MODE_WHITE:
				drawLine(lastPoint,dummyPt,"#fff");
				break;
			case MODE_COLOR:
				drawLine(lastPoint,dummyPt,colorPicker.getValue());
				break;
			}
			}
		}
		
		mouseMoved=false;
		mouseDown=false;
		lastPoint=null;
	
		String dataUrl=pixelCanvas.toDataUrl("image/png");
		endCreateCommand(dataUrl);
		updateCurrentSelectionDataUrl(dataUrl);
		
		
		updateCanvas();
	}
	
	private void doPick(int cx, int cy) {
		cx/=currentScale;
		cy/=currentScale;
		
		//LogUtils.log(cx+","+cy);
		ImageData data=pixelCanvas.getContext2d().getImageData(cx, cy, 1, 1);
		int r=data.getRedAt(0, 0);
		int g=data.getGreenAt(0, 0);
		int b=data.getBlueAt(0, 0);
		//LogUtils.log(r+","+g+","+b);
		String hex=ColorUtils.toCssColor(r,g,b);
		
		//LogUtils.log(hex);
		colorPicker.setValue(hex);
		
		if(backgroundList.getSelectedIndex()==3){//
			updateBgStyle();
		}
	}
	
	
	protected void doReset() {
		if(selection==null){
			pixelCanvas.setVisible(false);
			return;
		}
		currentCommand=new DataUriCommand();
		currentCommand.setBeforeUri(pixelCanvas.toDataUrl("image/png"));
		
		
		
		//canvas.getContext2d().save();
		//canvas.getContext2d().setGlobalCompositeOperation(Composite.COPY);//seems broken in Chrome32
		//canvas.getContext2d().translate(originImage.getCoordinateSpaceWidth(), 0); //flip horizontal
		//canvas.getContext2d().scale(-1, 1);
		pixelCanvas.getContext2d().clearRect(0, 0, pixelCanvas.getCoordinateSpaceWidth(), pixelCanvas.getCoordinateSpaceHeight());
		//no need to flip
		//canvas.getContext2d().translate(originImage.getCoordinateSpaceWidth(), 0); //flip horizontal
		//canvas.getContext2d().scale(-1, 1);
		//canvas.getContext2d().transform(-1, 0, 0, 1, 0, 0);
		
		//need size,for some buggy case
		if(resetMode==RESET_MODE_TEXTURE){
		ImageElementUtils.copytoCanvas(ImageElementUtils.create(selection.getInitialDataUrl()), pixelCanvas);
		}else if(resetMode==RESET_MODE_BACKGROUND_CLIP){
			CanvasUtils.resizeCanvasFrom(selection.getImageElement(), pixelCanvas);
			pixelCanvas.getContext2d().save();
			
			for(PointShape shape:selection.getPointShape().asSet()){
				shape.clip(pixelCanvas);
				CanvasUtils.drawImage(pixelCanvas, selection.getImageElement());
			}
			
			pixelCanvas.getContext2d().restore();
			
			
		}else if(resetMode==RESET_MODE_BACKGROUND_ALL){
			ImageElementUtils.copytoCanvas(selection.getImageElement(), pixelCanvas);
		}
		//canvas.getContext2d().drawImage(imageElement), 0, 0);
		//canvas.getContext2d().restore();
		
		String dataUrl=pixelCanvas.toDataUrl("image/png");
		currentCommand.setAfterUri(dataUrl);
		
		
		undoBt.setEnabled(true);
		redoBt.setEnabled(false);
		updateCurrentSelectionDataUrl(dataUrl);
		
		updateScale((int)currentScale);
		
		//LogUtils.log("after-reset:"+drawingCanvas.getContext2d().getGlobalCompositeOperation());
	}







	


	



	
	
	
	
	

	

	private XYPoint mouseToXYPoint(int mx,int my){
		int x=mx*zoomSize;
		int y=my*zoomSize;
		XYPoint newPoint=new XYPoint(x,y);
		return newPoint;
	}
	
	private void erase(XYPoint p1,XYPoint p2){
		pixelCanvas.getContext2d().save();
		pixelCanvas.getContext2d().setLineWidth(penSize);
		pixelCanvas.getContext2d().setLineJoin(LineJoin.ROUND);
		pixelCanvas.getContext2d().setStrokeStyle("#000");
		pixelCanvas.getContext2d().setGlobalCompositeOperation("destination-out");
		
		pixelCanvas.getContext2d().beginPath();
		
		pixelCanvas.getContext2d().moveTo(p1.getX(),p1.getY());
		pixelCanvas.getContext2d().lineTo(p2.getX(),p2.getY());
		
		pixelCanvas.getContext2d().closePath();
		pixelCanvas.getContext2d().stroke();
		pixelCanvas.getContext2d().restore();
	}
	
	private void unerase(XYPoint p1,XYPoint p2){
		if(selection==null){
			return;
		}
		overlayCanvas.getContext2d().clearRect(0, 0, overlayCanvas.getCoordinateSpaceWidth(), overlayCanvas.getCoordinateSpaceHeight());
		
		overlayCanvas.getContext2d().save();
		overlayCanvas.getContext2d().setLineWidth(penSize+2);
		overlayCanvas.getContext2d().setLineJoin(LineJoin.ROUND);
		overlayCanvas.getContext2d().setStrokeStyle("#000");
		overlayCanvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_OVER);
		overlayCanvas.getContext2d().beginPath();
		overlayCanvas.getContext2d().moveTo(p1.getX(),p1.getY());
		overlayCanvas.getContext2d().lineTo(p2.getX(),p2.getY());
		overlayCanvas.getContext2d().closePath();
		overlayCanvas.getContext2d().stroke();
		
		//TODO clip
		overlayCanvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_IN);
		//overlayCanvas.getContext2d().translate(originImage.getCoordinateSpaceWidth(), 0); //flip horizontal
		//overlayCanvas.getContext2d().scale(-1, 1);
		overlayCanvas.getContext2d().drawImage(selection.getImageElement(), 0, 0);
		
		overlayCanvas.getContext2d().restore();
		
		pixelCanvas.getContext2d().save();
		pixelCanvas.getContext2d().drawImage(overlayCanvas.getCanvasElement(), 0, 0);
		pixelCanvas.getContext2d().restore();
	}
	
	private void drawLine(XYPoint p1,XYPoint p2,String color){
		//LogUtils.log("drawLine-before:"+canvas.getContext2d().getGlobalCompositeOperation());
		pixelCanvas.getContext2d().save();
		pixelCanvas.getContext2d().setLineWidth(penSize);
		pixelCanvas.getContext2d().setLineJoin(LineJoin.ROUND);
		pixelCanvas.getContext2d().setStrokeStyle(color);
		pixelCanvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_OVER);
		
		pixelCanvas.getContext2d().beginPath();
		
		pixelCanvas.getContext2d().moveTo(p1.getX(),p1.getY());
		pixelCanvas.getContext2d().lineTo(p2.getX(),p2.getY());
		
		pixelCanvas.getContext2d().closePath();
		pixelCanvas.getContext2d().stroke();
		pixelCanvas.getContext2d().restore();
		//LogUtils.log("drawLine-after:"+canvas.getContext2d().getGlobalCompositeOperation());
	}

	
	public static class XYPoint{
		public XYPoint(int x,int y){
			this.x=x;
			this.y=y;
		}
		private int x;
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		private int y;
		
		public String toString(){
			return x+":"+y;
		}
	}
	
	boolean mouseDown;
	boolean mouseRight;
	private int zoomSize;

	private Button undoBt;
	private Button redoBt;

	private Button reset;
	private ColorBox colorPicker;
	protected void drawImage(ImageElement img) {
		try{
		
		//need for some transparent image
		pixelCanvas.getContext2d().clearRect(0, 0, pixelCanvas.getCoordinateSpaceWidth(), pixelCanvas.getCoordinateSpaceHeight());
		
		//canvas.getContext2d().save();
		
		//no need to flip
		//canvas.getContext2d().translate(originImage.getCoordinateSpaceWidth(), 0); //flip horizontal
		//canvas.getContext2d().scale(-1, 1);
		//canvas.getContext2d().transform(-1, 0, 0, 1, 0, 0);
		pixelCanvas.getContext2d().drawImage(selection.getImageElement(), 0, 0);
		//canvas.getContext2d().restore();
		
		}catch(Exception e){
			LogUtils.log("error:"+e.getMessage());
		}
		
		//canvas.getContext2d().setFillStyle("rgba(0,0,0,0)");
		//canvas.getContext2d().setGlobalCompositeOperation("destination-out");
		//canvas.getContext2d().fillRect(100, 100, 100, 100);
	}
	
	



	
	public class DataUriCommand implements Command{
		private String beforeUri;
		private String afterUri;
		private ImageElementData2 beforeSelection;
		private ImageElementData2 afterSelection;
		public ImageElementData2 getBeforeSelection() {
			return beforeSelection;
		}

		public void setBeforeSelection(ImageElementData2 beforeSelection) {
			this.beforeSelection = beforeSelection;
		}

		public ImageElementData2 getAfterSelection() {
			return afterSelection;
		}

		public void setAfterSelection(ImageElementData2 afterSelection) {
			this.afterSelection = afterSelection;
		}

		public String getBeforeUri() {
			return beforeUri;
		}

		public void setBeforeUri(String beforeUri) {
			this.beforeUri = beforeUri;
		}

		public String getAfterUri() {
			return afterUri;
		}

		public void setAfterUri(String afterUri) {
			this.afterUri = afterUri;
		}

		public boolean undoed;
		
		public boolean isUndoed() {
			return undoed;
		}

		public void setUndoed(boolean undoed) {
			this.undoed = undoed;
		}

		@Override
		public void undo() {
			
			if(beforeSelection!=easyCellTableObjects.getSelection()){
				LogUtils.log("differenct selection.ignore undo");
				return;
			}
			
			ImageElementLoader loader=new ImageElementLoader();
			loader.load(beforeUri, new ImageElementListener() {
				@Override
				public void onLoad(ImageElement element) {
					pixelCanvas.getContext2d().save();
					pixelCanvas.getContext2d().setGlobalCompositeOperation(Composite.COPY);
					pixelCanvas.getContext2d().drawImage(element,0,0);
					pixelCanvas.getContext2d().restore();
				
					updateCurrentSelectionDataUrl(beforeUri);
					updateCanvas();
				}
				
				@Override
				public void onError(String url, ErrorEvent event) {
					Window.alert(event.toDebugString());
				}
			});
			
			currentCommand.setUndoed(true);
		}

		@Override
		public void redo() {
			if(afterSelection!=easyCellTableObjects.getSelection()){
				LogUtils.log("differenct selection.ignore redo");
				return;
			}
			
			ImageElementLoader loader=new ImageElementLoader();
			loader.load(afterUri, new ImageElementListener() {
				@Override
				public void onLoad(ImageElement element) {
					pixelCanvas.getContext2d().save();
					pixelCanvas.getContext2d().setGlobalCompositeOperation(Composite.COPY);
					pixelCanvas.getContext2d().drawImage(element,0,0);
					pixelCanvas.getContext2d().restore();
					
					updateCurrentSelectionDataUrl(afterUri);
					updateCanvas();
				}
				
				@Override
				public void onError(String url, ErrorEvent event) {
					Window.alert(event.toDebugString());
				}
			});
			
			currentCommand.setUndoed(false);
		}
		
	}
	
	/**
	 * this faild some edge showd,i need stop antialiase.
	 * @author aki
	 *
	 */
	public class EraseCommand implements Command{
		List<XYPoint> positions=new ArrayList<XYPoint>();
		public void add(XYPoint point){
			positions.add(point);
		}
		public int size(){
			return positions.size();
		}
		@Override
		public void undo() {
			if(size()==1){//click only
				unerase(positions.get(0), positions.get(0));
			}
			for(int i=0;i<positions.size()-1;i++){
				unerase(positions.get(i), positions.get(i+1));
			}
			
			updateCanvas();
		}

		@Override
		public void redo() {
			if(size()==1){//click only
				erase(positions.get(0), positions.get(0));
			}
			for(int i=0;i<positions.size()-1;i++){
				erase(positions.get(i), positions.get(i+1));
			}
			
			updateCanvas();
		}
	}
	

	


	Blob blob;
	private void createDownloadImage(boolean withBg) {
		if(selection==null){
			return;
		}
		downloadArea.clear();
		
		
		updateDrawingCanvas(withBg);//anway bg
		
		blob=Blob.createBase64Blob(pixelCanvas.toDataUrl(),"image/png");//for IE keep blob
		
		Anchor a=null;
		if(GWTUtils.isIE()){
			a=HTML5Download.get().generateDownloadLink(blob, "image/png","gridPaint.png", textConstants.RightClickAndSaveAs(),false);
			//a.setTitle("to download right mouse button to show contextmenu and select save as by yourself");
		}else{
			//TODO support ios
			a=HTML5Download.get().generateDownloadLink(blob, "image/png","transparent.png", textConstants.Download_Image(),true);
		}
				
		a.setStylePrimaryName("bt");
		downloadArea.add(a);
		
		updateDrawingCanvas(false);//clear invalid
	}
	
	
	

	protected void loadFile(final File file,final String asStringText) {
		try{
			//TODO create method
		
		new ImageElementLoader().load(asStringText, new ImageElementListener() {
			@Override
			public void onLoad(ImageElement element) {
				LogUtils.log(file.getFileName()+","+element.getWidth()+"x"+element.getHeight());
				
				
				final ImageElementData2 data=new ImageElementData2(file.getFileName(),element,asStringText);
				
				easyCellTableObjects.addItem(data);
				
				//stack on mobile,maybe because of called async method
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						easyCellTableObjects.setSelected(data, true);
					}
				});
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				Window.alert(event.toDebugString());
			}
			
			
		});
		
		
		
		//doSelecct(data);//only way to update on Android Chrome
		}catch (Exception e) {
			e.printStackTrace();
			LogUtils.log(e.getMessage());
		}
		
		
	}

	public void clearImage(){
		//GWT.getModuleName()+"/"
	}
	
	ImageElementData2 selection;




	private VerticalPanel downloadArea;



	private ScrollPanel canvasScroll;



	private CheckBox scrollLockCheck;




	




	
	public void doSelect(ImageElementData2 selection) {
		//Stopwatch watch=Stopwatch.createStarted();
		this.selection=selection;
		if(selection==null){
			canvas.setVisible(false);
		}else{
			canvas.setVisible(true);
			//ImageElement element=ImageElementUtils.create(selection.getDataUrl());
			//ImageElementUtils.copytoCanvas(element, canvas);
			undoBt.setEnabled(false);
			redoBt.setEnabled(false);
			new ImageElementLoader().load(selection.getDataUrl(), new ImageElementListener() {
				
				@Override
				public void onLoad(ImageElement element) {
					updateDrawingCanvas(false);
					
					startCreateCommand();
				}
				
				@Override
				public void onError(String url, ErrorEvent event) {
					LogUtils.log("invalid url"+url);
				}
			});
			
		}
		//LogUtils.log("selection-change-time-ms:"+watch.elapsed(TimeUnit.MILLISECONDS));
	}

	private ImageElement bgImage;



	//private CheckBox trasparentCheck;


	private ListBox backgroundList;
	//private CheckBox blackCheck;



	private Button execTransparentBt;


	private int expand;


	private int fade;

	/**
	 * only need when export,usually css draw backgorund
	 * @param withBg
	 */
	public void updateDrawingCanvas(boolean withBg){
		if(selection==null){
			return;
		}
		ImageElement selectionImage=ImageElementUtils.create(selection.getDataUrl());
		ImageElementUtils.copytoCanvas(selectionImage, pixelCanvas,false);//just same size
		
		CanvasUtils.copyToSizeOnly(pixelCanvas, canvas);
		
		//change scale
		if(currentScale!=1){
			canvas.setWidth((pixelCanvas.getCoordinateSpaceWidth()*currentScale)+"px");
			canvas.setHeight((pixelCanvas.getCoordinateSpaceHeight()*currentScale)+"px");
		}
		
		CanvasUtils.clear(pixelCanvas);
		CanvasUtils.clear(overlayCanvas);//should clear for old bg
		
		if(withBg){
		if(bgImage!=null){
			//bugs,not effect on bgcolor with bgimage,bgimage not repeated like css
			CanvasUtils.drawImage(pixelCanvas, bgImage);
		}else{
			if(!isTransparent()){
				pixelCanvas.getContext2d().setFillStyle(getBgColor());
				
				pixelCanvas.getContext2d().fillRect(0, 0, pixelCanvas.getCoordinateSpaceWidth(), pixelCanvas.getCoordinateSpaceHeight());
			}
		}
		}
		
		
		
		CanvasUtils.drawImage(pixelCanvas, selectionImage);
	
		
		ImageElementUtils.copytoCanvas(selectionImage, overlayCanvas,false);
		
		updateCanvas();
	}

	public abstract class HtmlColumn<T> extends Column<T,SafeHtml>{

		public HtmlColumn(Cell<SafeHtml> cell) {
			super(cell);
		}

		public abstract String toHtml(T object);
		
		@Override
		public SafeHtml getValue(T object) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
	    	 sb.appendHtmlConstant(toHtml(object));
	    	 return sb.toSafeHtml();
		}
		
	}
	
	//TODO move to ColorUtils
	public static Uint8Array createMaskByColor(ImageData data,int r,int g,int b){
		return createMaskByColor(data, r, g, b,true);
	}
	//TODO move to ColorUtils
	/**
	 * 
	 * @param data
	 * @param r
	 * @param g
	 * @param b
	 * @param maxLength
	 * @return imageData-width x imageData-height length (if similar value is 1,or 0)
	 */
	public static Uint8Array createMaskBySimilarColor(ImageData data,int r,int g,int b,int maxLength){
		Uint8Array bytes=Uint8Array.createUint8(data.getWidth()*data.getHeight());
		for(int y=0;y<data.getHeight();y++){
			for(int x=0;x<data.getWidth();x++){
				int imgR=data.getRedAt(x, y);
				int imgG=data.getGreenAt(x, y);
				int imgB=data.getBlueAt(x, y);
				
				double length=ColorUtils.getColorLength(r, g, b, imgR, imgG, imgB);
				if(length<maxLength){
					bytes.set(y*data.getWidth()+x, 1);
				}
			}
		}
		return bytes;
	}
	//TODO move to ColorUtils
	public static Uint8Array createMaskByColor(ImageData data,int r,int g,int b,boolean setSameColorCase){
		Uint8Array bytes=Uint8Array.createUint8(data.getWidth()*data.getHeight());
		for(int y=0;y<data.getHeight();y++){
			for(int x=0;x<data.getWidth();x++){
				int imgR=data.getRedAt(x, y);
				int imgG=data.getGreenAt(x, y);
				int imgB=data.getBlueAt(x, y);
				if(r==imgR && g==imgG&&b==imgB){
					if(setSameColorCase){
					bytes.set(y*data.getWidth()+x, 1);
					}
				}else{
					if(!setSameColorCase){
					bytes.set(y*data.getWidth()+x, 1);
					}
				}
			}
		}
		return bytes;
	}
	

	@Override
	public String getAppName() {
		return textConstants.TransparentIt();
	}

	/**
	 * 1.1.1 fix scaled pick not working.
	 */
	@Override
	public String getAppVersion() {
		return "1.1.1";
	}
	
	/*
	@Override
	public Panel getLinkContainer() {
		return topPanel;
	}
	*/

	@Override
	public String getAppUrl() {
		return "http://android.akjava.com/html5apps/index.html#transparentit";
	}



	@Override
	protected void onBoneAndAnimationChanged(BoneAndAnimationData data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onBackgroundChanged(ImageDrawingData background) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTextureDataChanged(TextureData textureData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() {
		drawShape=true;
		penSize=16;
		inpaintRadius=5;
		expand=0;
		fade=0;
		
		manager.getTextureOrderSystem().addListener(new DataChangeListener<List<String>>() {
			@Override
			public void dataChanged(List<String> data, DataOwner owner) {
				onTextureOrderChanged(data,owner);
			}
		});
	}
	
	public void onTextureOrderChanged(List<String> data, DataOwner owner){
		List<ImageElementData2> newDatas=Lists.newArrayList();
		
		for(String id:data){
			for(ImageElementData2 finded:findDataByName(id).asSet()){
				easyCellTableObjects.getDatas().remove(finded);
				newDatas.add(finded);
			}
		}
		
		for(ImageElementData2 remain:easyCellTableObjects.getDatas()){
			newDatas.add(remain);
		}
		
		easyCellTableObjects.setDatas(newDatas);
		easyCellTableObjects.update();
		
	}

	public Optional<ImageElementData2> findDataByName(String id){
		for(ImageElementData2 data:easyCellTableObjects.getDatas()){
			if(data.getId().equals(id)){
				return Optional.of(data);
			}
		}
		return Optional.absent();
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
	protected void executeUpdateCanvas() {
		//LogUtils.log("canvas-updated");
		CanvasUtils.clear(canvas);
		
		//this style extreamlly slow ,todo fix later
		canvas.getContext2d().drawImage(pixelCanvas.getCanvasElement(), 0, 0);
		
		
		if(selection==null){
			return;
		}
		if(drawShape ){
			//TODO draw dot
			//
			for(PointShape pointShape:selection.getPointShape().asSet()){
				pointShape.stroke("#888", canvas);
			}
		}
	}
	
	

	@Override
	public String getOwnerName() {
		return "TransparentIt";
	}
	
}
