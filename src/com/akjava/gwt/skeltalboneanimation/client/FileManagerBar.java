package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.jszip.client.JSZipUtils;
import com.akjava.gwt.jszip.client.JSZipUtils.ZipListener;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneAndAnimationConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.TextureDataConverter;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageData;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileManagerBar extends VerticalPanel{

	private Label boneNameLabel;
    private UploadedFileManager uploadedFileManager;
	private Label backgroundNameLabel;
	private Label textureNameLabel;

	private Label clipNameLabel;
	public FileManagerBar(MainManager manager){
		manager.setFileManagerBar(this);
		
		this.uploadedFileManager=manager.getUploadedFileManager();
		
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		this.add(panel);
		
		panel.add(new Label("Bone/Animation:"));
		boneNameLabel = createLabel("");
		panel.add(boneNameLabel);
		
		//TODO maybe remove
		FileUploadForm boneUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String text) {
				BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
				
				setBoneAndAnimation(file.getFileName(),data);
				
			}
		}, true);
		boneUpload.setAccept(FileUploadForm.ACCEPT_TXT);
		panel.add(boneUpload);
		
		/*
		panel.add(new Label("Animation"));
		animationNameLabel = createLabel("");
		panel.add(animationNameLabel);
		
		//TODO maybe remove
		FileUploadForm animatiomUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String text) {
				setBoneText(file.getFileName(),text);
				
			}
		}, true);
		boneUpload.setAccept(FileUploadForm.ACCEPT_TXT);
		panel.add(animatiomUpload);
		*/
		
		
		panel.add(new Label("Background:"));
		backgroundNameLabel = createLabel("");
		panel.add(backgroundNameLabel);
		
		FileUploadForm backgroundUpload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String text) {
				setBackground(file.getFileName(),text);
				
			}
		}, true);
		backgroundUpload.setAccept(FileUploadForm.ACCEPT_IMAGE);
		panel.add(backgroundUpload);
		
		panel.add(new Label("Texture:"));
		textureNameLabel = createLabel("");
		panel.add(textureNameLabel);
		
		panel.add(new Label("Load:"));
		 FileUploadForm textureUpload=JSZipUtils.createZipFileUploadForm(new ZipListener() {
				
				@Override
				public void onLoad(String name, JSZip zip) {
					TextureDataConverter converter=new TextureDataConverter();
					
					setTexture(name,converter.convert(zip));
					
				}
				
				@Override
				public void onFaild(int states, String statesText) {
					LogUtils.log("faild:"+states+","+statesText);
				}
			});
		panel.add(textureUpload);
		
		panel.add(new Label("Clip:"));
		clipNameLabel = createLabel("");
		panel.add(clipNameLabel);
		
		/*
		panel.add(new Label("Load:"));
		 FileUploadForm textureUpload=JSZipUtils.createZipFileUploadForm(new ZipListener() {
				
				@Override
				public void onLoad(String name, JSZip zip) {
					TextureDataConverter converter=new TextureDataConverter();
					
					setTexture(name,converter.convert(zip));
					
				}
				
				@Override
				public void onFaild(int states, String statesText) {
					LogUtils.log("faild:"+states+","+statesText);
				}
			});
		panel.add(textureUpload);
		*/
		
	}
	
	public void setTexture(String fileName,TextureData textureData){
		textureNameLabel.setText(fileName);
		uploadedFileManager.setTextureData(textureData);
	}
	public void setBoneAndAnimation(String fileName,BoneAndAnimationData data){
		boneNameLabel.setText(fileName);
		
		uploadedFileManager.setBoneAndAnimation(data);
	}
	//TODO clip
	public void setClipImageData(String fileName,ClipImageData data){
		clipNameLabel.setText(fileName);
		
	
		
		//link texture and clip using texture-id & clip-id
		TextureData textures=uploadedFileManager.getTextureData();
		for(ImageDrawingData drawing:textures.getImageDrawingDatas()){
			for(ClipData clip:data.findDataById(drawing.getId()).asSet()){
				clip.setLinkedImageDrawingData(drawing);
			}
		}
		
		
		uploadedFileManager.setClipImageData(data);
		
		if(data.getImageDrawingData()!=null){
			//maybe this set background
			setBackground(fileName,data.getImageDrawingData());
		}else{
			LogUtils.log("clip-data has no background");
		}
		
		//link with textures
		
		
		
		if(data.getBone()!=null){
			BoneAndAnimationData baa=new BoneAndAnimationData();
			baa.setBone(data.getBone());
			//LogUtils.log("setBoneAndAnimation from clipImageData");
			setBoneAndAnimation(fileName,baa);
		}
	}
	/*
	public void setBoneText(String fileName,String text){
	
		BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
		setBone(fileName,data.getBone());
	}
	
	public void setAnimationText(String fileName,String text){
		
		BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
		//setAnimation(fileName,data.getAnimation());
	}
	*/
	
	/*
	public void setBone(String fileName,TwoDimensionBone bone){
		boneNameLabel.setText(fileName);
		uploadedFileManager.setBoneAndAnimation(bone);
	}
	*/
	
	
	
	public void setBackground(String fileName,String dataUrl){
		ImageDrawingData data=new ImageDrawingData(fileName, ImageElementUtils.create(dataUrl));
		data.setImageName(fileName);
		//temporay center on canvas
		data.setX(400);
		data.setY(400);
		
		setBackground(fileName,data);
		
		
	}
	
	public void setBackground(String fileName,ImageDrawingData data){
		backgroundNameLabel.setText(fileName);
		uploadedFileManager.setBackgroundData(data);
		
		
	}
	
	public Label createLabel(String title){
		Label label=new Label(title);
		label.setWidth("110px");
		return label;
	}

}
