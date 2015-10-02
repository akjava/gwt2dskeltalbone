package com.akjava.gwt.skeltalboneanimation.client.page.colorpick;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.comvnetjs.client.ConvnetJs;
import com.akjava.gwt.comvnetjs.client.Net;
import com.akjava.gwt.comvnetjs.client.Trainer;
import com.akjava.gwt.comvnetjs.client.Vol;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.CanvasDragMoveControler.KeyDownState;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.lib.client.graphics.Graphics;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.Shape;
import com.akjava.lib.common.graphics.IntRect;
import com.akjava.lib.common.graphics.Point;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * technically this is page but basically not receive any modify.
 * @author aki
 *
 */
public class ColorPickPage extends AbstractPage{
	private Canvas inputCanvas;
	private Canvas resultCanvas;


	private List<VolRGB> positives;
	private List<VolRGB> negatives;
	private Net net;
	private Trainer trainer;
	
	
	
	public ColorPickPage(MainManager manager) {
		super(manager);
		
	}

	@Override
	protected void onCanvasTouchEnd(int sx, int sy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onCanvasTouchStart(int sx, int sy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onCanvasDragged(int vectorX, int vectorY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onCanvasWheeled(int delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onBoneAndAnimationChanged(BoneAndAnimationData data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onBackgroundChanged(ImageDrawingData background) {
		LogUtils.log(getOwnerName()+" onBackgroundChanged call");
	}

	@Override
	protected void onTextureDataChanged(TextureData textureData) {
		// TODO Auto-generated method stub
		
	}
	
	private void doClearLeaning(){
		net=ConvnetJs.createTwoLayerNetworkDemo(1,1,3,2);
		trainer = ConvnetJs.createTrainer2(net);
		
	}
	
	private void initDatas(){
		doClearLeaning();
		
		positives=Lists.newArrayList();
		negatives=Lists.newArrayList();
		
		if(positivePanel!=null){
			positivePanel.clear();
			negativePanel.clear();
		}
		
		if(clipCheck!=null){
			clipCheck.setValue(true);
		}
		
		history.clear();
	}

	@Override
	protected void initialize() {
		scale=1;
		isPositive=true;
		history=Lists.newArrayList();
		initDatas();
		
	}

	Supplier<LuvData> luvDataSupplier;
	
	private ImageSender imageSender;
	private String senderDataId;
	protected void onLoadImage(String fileName, String text) {
		initDatas();
		//not resize yet
		CanvasUtils.copyTo(ImageElementUtils.create(text), inputCanvas);
		imageData = inputCanvas.getContext2d().getImageData(0, 0, inputCanvas.getCoordinateSpaceWidth(), inputCanvas.getCoordinateSpaceHeight());
	
		CanvasUtils.copyToSizeOnly(inputCanvas, resultCanvas);
		
		//every load data initialize it
		luvDataSupplier=Suppliers.memoize(new Supplier<LuvData>(){
			@Override
			public LuvData get() {
				return new LuvData(imageData);
			}
			
		});
		
		updateScale(scale);
		updateCanvas();	
	}
	
	public void sendImage(ImageSender sender,String dataId,String dataUrl){
		this.senderDataId=dataId;
		this.imageSender=sender;
		onLoadImage(dataId, dataUrl);
	}
	private void doSendBack(){
		if(imageSender!=null){
			imageSender.sendBack(senderDataId, resultCanvas.toDataUrl());
			manager.selectTab(3);
		}else{
			LogUtils.log("no imageSender");
		}
	}
	
	
	public static interface ImageSender{
		public void sendBack(String dataId,String dataUrl);
	}

	private boolean isPositive;
	
	private int strategy;
	protected boolean testPoint;
	public static final int DEEP_LEARNING_RGB=0;
	public static final int LENGTH_RGB=1;
	public static final int LENGTH_LUV=2;
	public static final int DEEP_LEARNING_LUV=3;
	
	@Override
	protected Widget createCenterPanel() {
		VerticalPanel root=new VerticalPanel();
		
		VerticalPanel topPanel=new VerticalPanel();
		root.add(topPanel);
		
		HorizontalPanel strategyPanel=new HorizontalPanel();
		strategyPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topPanel.add(strategyPanel);
		final ListBox strategyBox=new ListBox();
		strategyBox.addItem("deep learning(RGB)");
		strategyBox.addItem("length(RGB)");
		strategyBox.addItem("length(Luv)");
		strategyBox.addItem("deep learning(Luv)");
		strategyBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				strategy=strategyBox.getSelectedIndex();
				
				if(strategy==DEEP_LEARNING_RGB || strategy==DEEP_LEARNING_LUV){
					//reset
					doClearLeaning();
				}
				
				paintResult();
			}
		});
		strategyPanel.add(strategyBox);
		
		RadioButton testCheck = new RadioButton("type","test point");
		strategyPanel.add(testCheck);
		testCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					testPoint=true;
				}
				
			}
			
		});
		debugPanel = new HorizontalPanel();
		strategyPanel.add(debugPanel);
		
		clipCheck = new CheckBox();
		debugPanel.add(clipCheck);
		
		HorizontalPanel panel=new HorizontalPanel();
		root.add(panel);
		
		VerticalPanel leftPanel=new VerticalPanel();
		
		ScrollPanel scroll=new ScrollPanel();
		scroll.setHeight("800px");
		panel.add(scroll);
		
		int canvasW=400;
		int canvasH=400;
		
		inputCanvas = CanvasUtils.createCanvas(canvasW, canvasH);
		inputCanvas.setStylePrimaryName("transparent_bg");
		scroll.add(inputCanvas);
		
		positivePanel = new HorizontalPanel();
		HorizontalPanel pPanel=new HorizontalPanel();
		topPanel.add(pPanel);
		
		HorizontalPanel nPanel=new HorizontalPanel();
		topPanel.add(nPanel);
		
		HorizontalPanel h0=new HorizontalPanel();
		topPanel.add(h0);
		RadioButton positiveCheck = new RadioButton("type","add Positive point");
		positiveCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					testPoint=false;
					isPositive=true;
				}
			}
		});
		positiveCheck.setValue(true);
		
		
		pPanel.add(positiveCheck);
		pPanel.add(positivePanel);
		
		RadioButton negativeCheck = new RadioButton("type","add Negative Point");
		negativeCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					testPoint=false;
					isPositive=false;
				}
			}
		});
		
		
		negativePanel = new HorizontalPanel();
		
		nPanel.add(negativeCheck);
		nPanel.add(negativePanel);
		
		HorizontalPanel mainColumn=new HorizontalPanel();
		topPanel.add(mainColumn);
		
		Button doReset=new Button("do reset",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doReset();
			}
		});
		h0.add(doReset);
		
		Button doRemove=new Button("remove last added",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doRemove();
			}
		});
		h0.add(doRemove);
		
		trainingButton = new ExecuteButton("do repeat train"){

			@Override
			public void executeOnClick() {
				doRepeatTrain();
			}
			
		};
		mainColumn.add(trainingButton);
		
		Button cleartrainingButton = new Button("clear train",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doClearLeaning();
				paintResult();
			}
		});
		mainColumn.add(cleartrainingButton);
		
		enableClip = new CheckBox("enable-clip");
		enableClip.setValue(true);
		enableClip.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				paintResult();
			}
		});
		mainColumn.add(enableClip);
		
		Button sendBack=new Button("sendback result",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSendBack();
			}
		});
		h0.add(sendBack);
		
		//
		
		Button test=new Button("Test luv-values",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				double minL=Double.MAX_VALUE;
				double maxL=Double.MIN_VALUE;
				
				double minU=Double.MAX_VALUE;
				double maxU=Double.MIN_VALUE;
				
				double minV=Double.MAX_VALUE;
				double maxV=Double.MIN_VALUE;
				
				for(int r=0;r<256;r++){
					for(int g=0;g<256;g++){
						for(int b=0;b<256;b++){
							double[] luv=LuvUtils.toLuv(r,g,b);
							double l=luv[0];
							double u=luv[1];
							double v=luv[2];
							
							if(minL>l){
								minL=l;
							}
							if(maxL<l){
								maxL=l;
							}
							if(minU>u){
								minU=u;
							}
							if(maxU<u){
								maxU=u;
							}
							if(minV>v){
								minV=v;
							}
							if(maxV<v){
								maxV=v;
							}
						}
					}
				}
				LogUtils.log("L:"+minL+","+maxL);
				LogUtils.log("U:"+minU+","+maxU);
				LogUtils.log("V:"+minV+","+maxV);
			}
		});
		h0.add(test);
		
		/*
		Button repeat2=new Button("do repeat train",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doRepeatTrain2();
			}
		});
		*/
		//leftPanel.add(repeat2);
		
		inputCanvas.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
					pickColor(imageData,event.getX(),event.getY());
			}
			
		});
		
		FileUploadForm imageUpload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				onLoadImage(file.getFileName(),text);
			}
		});
		h0.add(imageUpload);
		
		//loadImage();
		
		//result
		VerticalPanel rightPanel=new VerticalPanel();
		panel.add(rightPanel);
		
		ScrollPanel scroll2=new ScrollPanel();
		scroll2.setHeight("800px");
		resultCanvas = CanvasUtils.createCanvas(canvasW, canvasH);
		resultCanvas.setStylePrimaryName("transparent_bg");
		scroll2.add(resultCanvas);
		
		resultCanvas.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
					pickColor(imageData,event.getX(),event.getY());
			}
			
		});
		
		rightPanel.add(scroll2);
		
		
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
		h0.add(new Label("scale:"));
		h0.add(scaleBox);
		scaleBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				updateScale(event.getValue());
			}
		});
		
		final HorizontalPanel downloadlink=new HorizontalPanel();
		
		CheckBox showLeft=new CheckBox("show-left image");
		showLeft.setValue(true);
		showLeft.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				inputCanvas.setVisible(event.getValue());
			}
			
		});
				h0.add(showLeft);
				
		Button dump=new Button("dump image",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				downloadlink.clear();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(inputCanvas.toDataUrl(), "image/png", "color-pick-dump.png", "download image", true);
				downloadlink.add(a);
			}
		});
		h0.add(dump);
		h0.add(downloadlink);
		

		h0.add(downloadlink);
		
		
		updateCanvas();
		return root;
	}
	protected void pickColor(ImageData imageData,int mx,int my) {
		if(imageData==null){
			return ;
		}
		int x=mx/scale;
		int y=my/scale;
		
			if(imageData.getAlphaAt(x, y)==255){
				
				if(testPoint){
					if(strategy==LENGTH_LUV){
						int rgbr=imageData.getRedAt(x, y);
						int rgbg=imageData.getGreenAt(x, y);
						int rgbb=imageData.getBlueAt(x, y);
						
						double l=luvDataSupplier.get().getRedAt(x, y);
						double u=luvDataSupplier.get().getGreenAt(x, y);
						double v=luvDataSupplier.get().getBlueAt(x, y);
						LogUtils.log("luv:"+l+","+u+","+v);
						
						debugPanel.clear();
						debugPanel.add(new Label("pick:"));
						debugPanel.add(createColorHtml(rgbr,rgbg,rgbb));
						
						boolean result=isShowVolLengthLuv(l,u,v,true);
						if(result){
							debugPanel.add(new Label("result-positive"));
						}else{
							debugPanel.add(new Label("result-negative"));
						}
						debugPanel.add(new Label("[closed positive]"));
						debugPanel.add(createColorHtml(debugPositive.r,debugPositive.g,debugPositive.b));
						
						debugPanel.add(new Label("[closed negative]"));
						debugPanel.add(createColorHtml(debugNegative.r,debugNegative.g,debugNegative.b));
						
						
					}else{
						LogUtils.log("not support other test");
					}
					return;
				}
				
				
			VolRGB rgb=new VolRGB(
					imageData.getRedAt(x, y),
					imageData.getGreenAt(x, y),
					imageData.getBlueAt(x, y)
					);
			
			if(alreadyExist(rgb)){
				LogUtils.log("already exist");
				return;
			}
			
			
			
			rgb.setX(x);
			rgb.setY(y);
			
			int index=0;
			if(!isPositive){
				index=1;
				rgb.setIndex(index);
				
				negatives.add(rgb);
				addLabel(rgb);
				}else{
					positives.add(rgb);	
					rgb.setClipLine(clipCheck.getValue());
					addLabel(rgb);
				}
			history.add(rgb);
			
			if(strategy==LENGTH_LUV || strategy==LENGTH_RGB){
				paintResult();
			}
			
			//Stats stat=trainer.train(rgb.getVol(), index);
			//LogUtils.log(stat);
			
			updateCanvas();
			}
	}

	private HTML createColorHtml(int r,int g,int b){
		HTML html=new HTML("&nbsp;");
		html.setWidth("20px");
		String value=ColorUtils.toCssColor(r,g,b);
		html.getElement().getStyle().setBackgroundColor(value);
		return html;
	}
	
	protected void addLabel(VolRGB rgb) {
		 boolean positive=rgb.isPositive();
		HTML html=new HTML("&nbsp;");
		html.setWidth("20px");
		String value=ColorUtils.toCssColor(rgb.r, rgb.g, rgb.b);
		html.getElement().getStyle().setBackgroundColor(value);
		if(positive){
			positivePanel.add(html);
		}else{
			negativePanel.add(html);
		}
	}

	private int scale;
	protected void updateScale(Integer value) {
		CanvasUtils.scaleViewerSize(inputCanvas,value);
		CanvasUtils.scaleViewerSize(resultCanvas,value);
		scale=value;
	}

	private List<VolRGB> history;
	protected void doRemove() {
		if(history!=null && !history.isEmpty()){
			
			VolRGB lastOne=history.remove(history.size()-1);
			
		
			if(lastOne.isPositive()){
				positives.remove(lastOne);
				int index=positivePanel.getWidgetCount();
				positivePanel.remove(index-1);
			}else {
				negatives.remove(lastOne);
				int index=negativePanel.getWidgetCount();
				negativePanel.remove(index-1);
			}
		}
		
		if(strategy==LENGTH_LUV || strategy==LENGTH_RGB){
			paintResult();
		}
		
		updateCanvas();
	}

	protected void doReset() {
		initDatas();
		
		CanvasUtils.clear(resultCanvas);
		updateCanvas();
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

	private Shape getLineShape(){
		List<Point> points=FluentIterable.from(positives).filter(new ClipLinePredicate()).transform(new RGBToPointFunction()).toList();
		Shape shape=Shape.createFromPoint(points);
	
		return shape;
	}
	
	@Override
	protected void updateCanvas() {
		if(imageData!=null){
			CanvasUtils.copyTo(imageData, inputCanvas);
		}else{
			return;
		}
		
		getLineShape().stroke("#888", inputCanvas);
		
		for(VolRGB pt:positives){
			IntRect rect=IntRect.fromCenterPoint(pt.x, pt.y, 1, 1);
			RectCanvasUtils.fill(rect, inputCanvas, "#0f0");
		}
		
		for(VolRGB pt:negatives){
			IntRect rect=IntRect.fromCenterPoint(pt.x, pt.y, 1, 1);
			RectCanvasUtils.fill(rect, inputCanvas, "#f00");
		}
	}
	
	public class ClipLinePredicate implements Predicate<VolRGB>{

		@Override
		public boolean apply(VolRGB input) {
			return input.isClipLine();
		}

		
		
	}
	
	public class RGBToPointFunction implements Function<VolRGB,Point>{

		@Override
		public Point apply(VolRGB input) {
			return new Point(input.getX(),input.getY());
		}
		
	}


	private boolean alreadyExist(VolRGB rgb){
		for(VolRGB pt:positives){
			if(pt.isSameColor(rgb)){
				return true;
			}
		}
		
		for(VolRGB pt:negatives){
			if(pt.isSameColor(rgb)){
				return true;
			}
		}
		return false;
	}
	
	int index=0;
	/*
	protected void doRepeatTrain2() {
		Timer timer=new Timer(){
			public void run(){
				double avloss=doRepeatTrain();
				index++;
				LogUtils.log("result:"+index+" avloss="+avloss);
			}
		};
		timer.scheduleRepeating(1000);
	}
	*/

	protected double doRepeatTrain() {
		if(strategy==LENGTH_LUV || strategy==LENGTH_RGB){
			paintResult();
			return 0;
		}
		
		long t=System.currentTimeMillis();
		double loss=0;
		for(int i=0;i<100;i++){
			for(VolRGB pt:positives){
				if(strategy==DEEP_LEARNING_RGB){
					loss+=trainer.train(pt.getVol(), 0).getLoss();
				}else{
					loss+=trainer.train(pt.getLuvVol(), 0).getLoss();
				}
			}
			
			
			for(VolRGB pt:negatives){
				if(strategy==DEEP_LEARNING_RGB){
					loss+=trainer.train(pt.getVol(), 1).getLoss();
				}else{
					loss+=trainer.train(pt.getLuvVol(), 1).getLoss();
				}
			}
		}
		paintResult();
		double avloss=loss/(100*(positives.size()+negatives.size()));
		LogUtils.log("loss="+avloss+",time="+(System.currentTimeMillis()-t));
		return avloss;
	}


	public boolean isShowVolDeepLearningRGB(VolRGB rgb){
		Vol result=net.forward(rgb.getVol(),false);
		return result.getW(0)>result.getW(1);
	}
	
	
	public class LuvData{
		ImageData imageData;
		List<double[]> luvValues;
		public LuvData(ImageData imageData) {
			super();
			this.imageData = imageData;
			
			luvValues=new ArrayList<double[]>(imageData.getWidth()*imageData.getHeight());
			convertDatas();
			
		}
		private void convertDatas() {
			for(int y=0;y<imageData.getHeight();y++){
			for(int x=0;x<imageData.getWidth();x++){
				
					if(imageData.getAlphaAt(x, y)==255){
						luvValues.add(
								LuvUtils.toLuv(imageData.getRedAt(x, y), imageData.getGreenAt(x, y), imageData.getBlueAt(x, y))
								);
					}else{
						luvValues.add(new double[3]);
					}
				}
			}
		}
		public int getAlphaAt(int x, int y){
			return imageData.getAlphaAt(x, y);
		}
		
		public double getRedAt(int x, int y){
			int index=y*imageData.getWidth()+x;
			return luvValues.get(index)[0];
		}
		
		public double getGreenAt(int x, int y){
			int index=y*imageData.getWidth()+x;
			return luvValues.get(index)[1];
		}
		
		public double getBlueAt(int x, int y){
			int index=y*imageData.getWidth()+x;
			return luvValues.get(index)[2];
		}
	}
	
	protected void paintResult() {
		resultData = ImageDataUtils.copySizeOnly(inputCanvas);
		Map<String,Boolean> colorResult=new HashMap<String, Boolean>();
		for(int x=0;x<imageData.getWidth();x++){
			for(int y=0;y<imageData.getHeight();y++){
				if(imageData.getAlphaAt(x, y)==255){
					VolRGB rgb=new VolRGB(
							imageData.getRedAt(x, y),
							imageData.getGreenAt(x, y),
							imageData.getBlueAt(x, y)
							);
					boolean isShow=false;
					if(colorResult.get(rgb.toRGBString())!=null){
						isShow=colorResult.get(rgb.toRGBString());
					}else{
					
					if(strategy==DEEP_LEARNING_RGB){
						isShow=isShowVolDeepLearningRGB(rgb);
					}else if(strategy==LENGTH_RGB){
						isShow=isShowVolLengthRGB(rgb);
					}else if(strategy==LENGTH_LUV){
						LuvData data=luvDataSupplier.get();
						isShow=isShowVolLengthLuv(
								data.getRedAt(x, y),
								data.getGreenAt(x, y),
								data.getBlueAt(x, y),
								false
								);
					}else if(strategy==DEEP_LEARNING_LUV){
						isShow=isShowVolDeepLearningLuv(rgb);
					}
					
					colorResult.put(rgb.toRGBString(), isShow);
					}
					if(isShow){
						resultData.setAlphaAt(255, x, y);
						resultData.setRedAt(rgb.r, x, y);
						resultData.setGreenAt(rgb.g, x, y);
						resultData.setBlueAt(rgb.b, x, y);
					}
				}
			}
		}
		
		//LogUtils.log("done");
		CanvasUtils.clear(resultCanvas);
		
		CanvasUtils.copyTo(resultData, resultCanvas);
		
		if(enableClip.getValue()){
			resultCanvas.getContext2d().save();
			getLineShape().clip(resultCanvas);
			
			Canvas imageDataCanvas=CanvasUtils.copyTo(imageData, null);
			Graphics.from(imageDataCanvas).drawTo(resultCanvas);
			//resultCanvas.c
			resultCanvas.getContext2d().restore();
		}
		
		/*
		//LogUtils.log("positives");
		for(PointXY pt:positives){
		//	debug(pt.x,pt.y);
		}
		
		//LogUtils.log("negatives");
		for(PointXY pt:negatives){
		//	debug(pt.x,pt.y);
		}
		*/
	}
	private boolean isShowVolDeepLearningLuv(VolRGB rgb) {
		Vol result=net.forward(rgb.getLuvVol(),false);
		return result.getW(0)>result.getW(1);
	}

	private boolean isShowVolLengthRGB(VolRGB rgb) {
		
		double positiveClosest=Double.MAX_VALUE;
		for(VolRGB value:positives){
			double length=ColorUtils.getColorLength(value.r,value.g,value.b,rgb.r, rgb.g, rgb.b);
			double diff=Math.abs(length);
			//double diff=Math.abs(rgbValue-length);
			if(diff<positiveClosest){
				positiveClosest=diff;
			}
		}
		double negativeClosest=Double.MAX_VALUE;
		for(VolRGB value:negatives){
			double length=ColorUtils.getColorLength(value.r,value.g,value.b,rgb.r, rgb.g, rgb.b);
			double diff=Math.abs(length);
			if(diff<negativeClosest){
				negativeClosest=diff;
			}
		}
		
		return positiveClosest<negativeClosest;
	}
	
	private VolRGB debugNegative;
	private VolRGB debugPositive;
	private boolean isShowVolLengthLuv(double r,double g,double b,boolean debug) {
		double[] luv=new double[]{r,g,b};
		
		double positiveClosest=Double.MAX_VALUE;
		
		for(VolRGB value:positives){
			double[] luvV2=value.toLuv();
			double length=ColorUtils.getColorLength(luv[0], luv[1], luv[2],luvV2[0], luvV2[1], luvV2[2]);
			double diff=Math.abs(length);
			
			//double diff=Math.abs(rgbValue-length);
			if(diff<positiveClosest){
				positiveClosest=diff;
				debugPositive=value;
			}
		}
		double negativeClosest=Double.MAX_VALUE;
		
		for(VolRGB value:negatives){
			double[] luvV2=value.toLuv();
			double length=ColorUtils.getColorLength(luv[0], luv[1], luv[2],luvV2[0], luvV2[1], luvV2[2]);
			double diff=Math.abs(length);
			if(diff<negativeClosest){
				negativeClosest=diff;
				debugNegative=value;
			}
		}
		
		boolean result= positiveClosest<negativeClosest;
		
		
		if(debug){
			LogUtils.log("closest-positive-luv:"+debugPositive.toLuv()[0]+","+debugPositive.toLuv()[1]+","+debugPositive.toLuv()[2]);
			LogUtils.log("closest-negative-luv:"+debugNegative.toLuv()[0]+","+debugNegative.toLuv()[1]+","+debugNegative.toLuv()[2]);
		}
		
		return result;
	}

	public Vol getVol(int x,int y){
		
			VolRGB rgb=new VolRGB(
					imageData.getRedAt(x, y),
					imageData.getGreenAt(x, y),
					imageData.getBlueAt(x, y)
					);
			
			return rgb.getVol();
	}
	
	public void debug(int x,int y){
		if(imageData.getAlphaAt(x, y)==255){
			VolRGB rgb=new VolRGB(
					imageData.getRedAt(x, y),
					imageData.getGreenAt(x, y),
					imageData.getBlueAt(x, y)
					);
			Vol vol=net.forward(rgb.getVol());
			
		//	LogUtils.log(rgb.r+","+rgb.g+","+rgb.b+" = "+vol.getW(0)+","+vol.getW(1));
			
		}
	}
	
	
	private ImageData imageData;
	private HorizontalPanel positivePanel;
	private HorizontalPanel negativePanel;
	private Button trainingButton;
	private ImageData resultData;
	private HorizontalPanel debugPanel;
	private CheckBox clipCheck;
	private CheckBox enableClip;


	/*
	public Vol createVol(int r,int g,int b){
		Vol vol= ConvnetJs.createVol(1, 1, 3, 0);
		vol.set(1, 1, new double[]{r,g,b});
		return vol;
	}
	*/
	
	public static class VolRGB{
		int r;
		int g;
		int b;
		int x;
		int y;
		int index;
		
		boolean clipLine;
		public boolean isClipLine() {
			return clipLine;
		}
		public void setClipLine(boolean clipLine) {
			this.clipLine = clipLine;
		}
		public int getX() {
			return x;
		}
		public boolean isPositive() {
			return index==0;
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
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public VolRGB(int r, int g, int b) {
			super();
			this.r = r;
			this.g = g;
			this.b = b;
		}
		Vol vol;
		public Vol getVol(){
			if(vol==null){
				vol=createVol(r, g, b);
			}
			return vol;
		}
		Vol luvVol;
		public Vol getLuvVol(){
			if(luvVol==null){
				luvVol=createLuvVol();
			}
			return luvVol;
		}
		public Vol createLuvVol(){
			Vol vol= ConvnetJs.createVol(1, 1, 3, 0);
			//LogUtils.log(r+","+g+","+b);
			
			double[] luv= toLuv();
			vol.set(0, 0, luv);
			
			/*
			double[] convert=new double[3];
			convert[0]=LuvUtils.normalizeL(luv[0]);
			convert[1]=LuvUtils.normalizeU(luv[1]);
			convert[2]=LuvUtils.normalizeV(luv[2]);
			vol.set(0, 0, convert);
			*/
			
			
			//LogUtils.log(vol);
			return vol;
		}
		public Vol createVol(int r,int g,int b){
			Vol vol= ConvnetJs.createVol(1, 1, 3, 0);
			//LogUtils.log(r+","+g+","+b);
			vol.set(0, 0, new double[]{toDouble(r),toDouble(g),toDouble(b)});
			//LogUtils.log(vol);
			return vol;
		}
		private double toDouble(int value){
			return ((double)value-128)/255;
		}
		public boolean isSameColor(VolRGB rgb){
			return isSameColor(rgb.r, rgb.g, rgb.b);
		}
		public boolean isSameColor(int r,int g,int b){
			return this.r==r && this.g==g&&this.b==b;
		}
		public String toRGBString(){
			return r+","+g+","+b;
		}
		private double[] luv;
		public double[] toLuv(){
			if(luv==null){
				luv=LuvUtils.toLuv(r, g, b);
			}
			return luv;
		}
	}

	@Override
	public String getOwnerName() {
		return "ColorPicker";
	}
}
