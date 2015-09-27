package com.akjava.gwt.skeltalboneanimation.client.page.colorpick;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.comvnetjs.client.ConvnetJs;
import com.akjava.gwt.comvnetjs.client.Net;
import com.akjava.gwt.comvnetjs.client.Stats;
import com.akjava.gwt.comvnetjs.client.Trainer;
import com.akjava.gwt.comvnetjs.client.Vol;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.MainManager;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.page.AbstractPage;
import com.akjava.lib.common.graphics.Rect;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
	protected void onCanvasWheeled(int delta, boolean shiftDown) {
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
	
	private void initDatas(){
		net=ConvnetJs.createTwoLayerNetworkDemo(1,1,3,2);
		trainer = ConvnetJs.createTrainer2(net);
		positives=Lists.newArrayList();
		negatives=Lists.newArrayList();
	}

	@Override
	protected void initialize() {
		scale=1;
		isPositive=true;
		initDatas();
		
	}

	private ImageSender imageSender;
	private String senderDataId;
	protected void onLoadImage(String fileName, String text) {
		initDatas();
		//not resize yet
		CanvasUtils.copyTo(ImageElementUtils.create(text), inputCanvas);
		imageData = inputCanvas.getContext2d().getImageData(0, 0, inputCanvas.getCoordinateSpaceWidth(), inputCanvas.getCoordinateSpaceHeight());
	
		CanvasUtils.copyToSizeOnly(inputCanvas, resultCanvas);
		
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
	@Override
	protected Widget createCenterPanel() {
		VerticalPanel root=new VerticalPanel();
		
		VerticalPanel topPanel=new VerticalPanel();
		root.add(topPanel);
		
		HorizontalPanel panel=new HorizontalPanel();
		root.add(panel);
		
		VerticalPanel leftPanel=new VerticalPanel();
		
		ScrollPanel scroll=new ScrollPanel();
		scroll.setHeight("600px");
		panel.add(scroll);
		
		int canvasW=400;
		int canvasH=400;
		
		inputCanvas = CanvasUtils.createCanvas(canvasW, canvasH);
		inputCanvas.setStylePrimaryName("transparent_bg");
		scroll.add(inputCanvas);
		
		
		HorizontalPanel h0=new HorizontalPanel();
		topPanel.add(h0);
		RadioButton positiveCheck = new RadioButton("type","add positive point");
		positiveCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					isPositive=true;
				}
			}
		});
		positiveCheck.setValue(true);
		
		
		h0.add(positiveCheck);
		
		RadioButton negativeCheck = new RadioButton("type","add Negative Point");
		negativeCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					isPositive=false;
				}
			}
		});
		h0.add(negativeCheck);
		
		HorizontalPanel h1=new HorizontalPanel();
		topPanel.add(h1);
		
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
		h1.add(doRemove);
		
		Button repeat=new Button("do repeat train",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doRepeatTrain();
			}
		});
		h1.add(repeat);
		
		Button sendBack=new Button("sendback result",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSendBack();
			}
		});
		h0.add(sendBack);
		
		
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
				int x=event.getX()/scale;
				int y=event.getY()/scale;
				
					if(imageData.getAlphaAt(x, y)==255){
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
						negatives.add(rgb);
						}else{
							positives.add(rgb);	
						}
					lastOne=rgb;
					rgb.setIndex(index);
					Stats stat=trainer.train(rgb.getVol(), index);
					LogUtils.log(stat);
					
					updateCanvas();
					}
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
		
		resultCanvas = CanvasUtils.createCanvas(canvasW, canvasH);
		resultCanvas.setStylePrimaryName("transparent_bg");
		rightPanel.add(resultCanvas);
		
		
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
		
		
		updateCanvas();
		return root;
	}
	private int scale;
	protected void updateScale(Integer value) {
		CanvasUtils.scaleViewerSize(inputCanvas,value);
		scale=value;
	}

	private VolRGB lastOne;
	protected void doRemove() {
		if(lastOne!=null){
			positives.remove(lastOne);
			negatives.remove(lastOne);
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

	@Override
	protected void updateCanvas() {
		if(imageData!=null){
			CanvasUtils.copyTo(imageData, inputCanvas);
		}
		for(VolRGB pt:positives){
			Rect rect=Rect.fromCenterPoint(pt.x, pt.y, 1, 1);
			RectCanvasUtils.fill(rect, inputCanvas, "#0f0");
		}
		
		for(VolRGB pt:negatives){
			Rect rect=Rect.fromCenterPoint(pt.x, pt.y, 1, 1);
			RectCanvasUtils.fill(rect, inputCanvas, "#f00");
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

	protected double doRepeatTrain() {
		long t=System.currentTimeMillis();
		double loss=0;
		for(int i=0;i<100;i++){
			for(VolRGB pt:positives){
				loss+=trainer.train(pt.getVol(), 0).getLoss();
			}
			
			
			for(VolRGB pt:negatives){
				loss+=trainer.train(pt.getVol(), 1).getLoss();
			}
		}
		paintResult();
		double avloss=loss/(100*(positives.size()+negatives.size()));
		LogUtils.log(System.currentTimeMillis()-t);
		return avloss;
	}


	protected void paintResult() {
		ImageData newData=ImageDataUtils.copySizeOnly(inputCanvas);
		Map<String,Boolean> colorResult=new HashMap<String, Boolean>();
		for(int x=0;x<imageData.getWidth();x++){
			for(int y=0;y<imageData.getHeight();y++){
				if(imageData.getAlphaAt(x, y)==255){
					VolRGB rgb=new VolRGB(
							imageData.getRedAt(x, y),
							imageData.getGreenAt(x, y),
							imageData.getBlueAt(x, y)
							);
					boolean isShow;
					if(colorResult.get(rgb.toRGBString())!=null){
						isShow=colorResult.get(rgb.toRGBString());
					}else{
					
					Vol result=net.forward(rgb.getVol(),false);
					
					if(result.getW(0)>result.getW(1)){
						isShow=true;
					}else{
						isShow=false;
					}
					colorResult.put(rgb.toRGBString(), isShow);
					}
					if(isShow){
						newData.setAlphaAt(255, x, y);
						newData.setRedAt(rgb.r, x, y);
						newData.setGreenAt(rgb.g, x, y);
						newData.setBlueAt(rgb.b, x, y);
					}
				}
			}
		}
		
		//LogUtils.log("done");
		CanvasUtils.clear(resultCanvas);
		
		CanvasUtils.copyTo(newData, resultCanvas);
		
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
	


	public Vol createVol(int r,int g,int b){
		Vol vol= ConvnetJs.createVol(1, 1, 3, 0);
		vol.set(1, 1, new double[]{r,g,b});
		return vol;
	}
	
	public static class VolRGB{
		int r;
		int g;
		int b;
		int x;
		int y;
		int index;
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
	}

	@Override
	public String getOwnerName() {
		return "ColorPicker";
	}
}
