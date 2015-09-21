package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.skeltalboneanimation.client.bones.BoneAndAnimationData;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.gwt.skeltalboneanimation.client.converters.BoneAndAnimationConverter;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileManagerBar extends VerticalPanel{

	private Label boneNameLabel;
    private UploadedFileManager uploadedFileManager;
	private Label backgroundNameLabel;
	public FileManagerBar(MainManager manager){
		manager.setFileManagerBar(this);
		
		this.uploadedFileManager=manager.getUploadedFileManager();
		
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		this.add(panel);
		
		panel.add(createLabel("Bone"));
		boneNameLabel = createLabel("");
		panel.add(boneNameLabel);
		
		/*
		FileUploadForm boneUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String text) {
				setBoneAndAnimationText(file.getFileName(),text);
				
			}
		}, true);
		boneUpload.setAccept(FileUploadForm.ACCEPT_TXT);
		panel.add(boneUpload);
		*/
		
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
