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
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneAndAnimationConverter;
import com.akjava.gwt.skeltalboneanimation.client.converters.TextureDataConverter;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileManagerBar extends VerticalPanel{

	private Label boneNameLabel;
    private UploadedFileManager uploadedFileManager;
	private Label backgroundNameLabel;
	private Label textureNameLabel;
	public FileManagerBar(MainManager manager){
		manager.setFileManagerBar(this);
		
		this.uploadedFileManager=manager.getUploadedFileManager();
		
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		this.add(panel);
		
		panel.add(createLabel("Bone"));
		boneNameLabel = createLabel("");
		panel.add(boneNameLabel);
		
		//TODO maybe remove
		FileUploadForm boneUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String text) {
				setBoneAndAnimationText(file.getFileName(),text);
				
			}
		}, true);
		boneUpload.setAccept(FileUploadForm.ACCEPT_TXT);
		panel.add(boneUpload);
		
		
		panel.add(createLabel("Background:"));
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
		
		panel.add(createLabel("Texture:"));
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
		
		
	}
	
	public void setTexture(String fileName,TextureData textureData){
		textureNameLabel.setText(fileName);
		uploadedFileManager.setTextureData(textureData);
	}
	
	public void setBoneAndAnimationText(String fileName,String text){
	
		BoneAndAnimationData data=new BoneAndAnimationConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
		setBone(fileName,data.getBone());
	}
	
	public void setBone(String fileName,TwoDimensionBone bone){
		boneNameLabel.setText(fileName);
		uploadedFileManager.setBone(bone);
	}
	
	
	
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
