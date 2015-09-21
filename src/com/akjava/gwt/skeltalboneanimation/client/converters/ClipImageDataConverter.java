package com.akjava.gwt.skeltalboneanimation.client.converters;

import java.util.List;

import com.akjava.gwt.jszip.client.JSFile;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.lib.client.Base64Utils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ImageBuilder;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageData;
import com.akjava.lib.common.io.FileType;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ClipImageDataConverter extends Converter<JSZip,ClipImageData>{

	@Override
	protected ClipImageData doForward(JSZip zip) {
		ClipImageData clipImageData=new ClipImageData();
		JSFile clipFile=zip.getFile("clips.txt");
		if(clipFile!=null){
			String text=clipFile.asText();
			if(text.isEmpty()){
			List<ClipData> datas=Lists.newArrayList(
					new ClipDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(text))
					);
			clipImageData.setClips(datas);
			}
			
		}else{
			LogUtils.log("clips.txt not contain");
		}
		
		JSFile bgFile=zip.getFile("background.txt");
		if(bgFile!=null){
			ImageDrawingData background=new ImageDrawingDataConverter().reverse().convert(bgFile.asText());
			if(background.getImageName()!=null){
			JSFile bgImage=zip.getFile(background.getImageName());
			if(bgImage!=null){
				String extension=FileNames.getExtension(background.getImageName());
				FileType type=FileType.getFileTypeByExtension(extension);
				String dataUrl=Base64Utils.toDataUrl(type.getMimeType(),bgImage.asUint8Array());
				background.setImageElement(ImageElementUtils.create(dataUrl));
			}else{
				LogUtils.log("background.txt has "+background.getImageName()+" but not exist in zip");
			}
			}else{
				LogUtils.log("background.txt has no image name");
			}
			
		}else{
			LogUtils.log("background.txt not contain");
		}
		
		return clipImageData;
	}

	@Override
	protected JSZip doBackward(ClipImageData data) {
		return convertToJsZip(JSZip.newJSZip(), data);
	}

	public JSZip convertToJsZip(JSZip baseZip,ClipImageData data){
		//clips
		Iterable<String> lines=new ClipDataConverter().convertAll(data.getClips());
		String indexText=Joiner.on("\r\n").join(lines);
		baseZip.file("clips.txt", indexText);
		
		ImageDrawingData imData=data.getImageDrawingData();
		
		String fileName=imData.getImageName();
		if(fileName!=null){
		String extension="png";
		for(FileType type:FileType.getFileTypeFromFileName(fileName).asSet()){
			extension=type.getExtension();
		}
		String newName="skeltal-background"+"."+extension;
		imData.setImageName(newName);
		}else{
			LogUtils.log("image name is null.force set");
			imData.setImageName("skeltal-background.png");
		}
		
		if(imData!=null){
			if(imData.getImageElement()==null){
				LogUtils.log(imData.getId()+" has no image element");
			}else{
		
			
			String dataUrl=ImageBuilder.from(imData.getImageElement()).onFileName(fileName).toDataUrl();
			baseZip.base64UrlFile(imData.getImageName(), dataUrl);
			
			}
			
			
		String bg=new ImageDrawingDataConverter().convert(data.getImageDrawingData());
		baseZip.file("background.txt", bg);
		
		
		
		
		
		}
		return baseZip;
	}
}
