package com.akjava.gwt.skeltalboneanimation.client.converters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.jszip.client.JSFile;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.lib.client.Base64Utils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ImageBuilder;
import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.TextureData;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.lib.common.io.FileType;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class TextureDataConverter extends Converter<JSZip,TextureData>{

	@Override
	protected TextureData doForward(JSZip zip) {
		checkNotNull(zip);
		JSFile indexFile=zip.getFile("index.txt");
		if(indexFile==null){
			LogUtils.log("not contain index.txt");
			return null;
		}
		String text=indexFile.asText();
		List<String> lines=CSVUtils.splitLinesWithGuava(text);
		
		List<ImageDrawingData> datas=Lists.newArrayList(new ImageDrawingDataConverter().reverse().convertAll(lines));
		
		for(ImageDrawingData data:datas){
			
			if(data.getImageName()==null || data.getImageName().isEmpty()){
				LogUtils.log(data.getId()+" has null or empty image-name");
				continue;
			}
			
			JSFile jsFile=zip.getFile(data.getImageName());
			if(jsFile==null){
				LogUtils.log(data.getId()+" not exist in zip");
				continue;
			}
		
			
			FileType type=null;
			Optional<FileType> typeOptional=FileType.getFileTypeFromFileName(data.getImageName());
			if(typeOptional.isPresent()){
				type=typeOptional.get();
			}else{
				type=FileType.PNG;
			}
			String dataUrl=Base64Utils.toDataUrl(type.getMimeType(),jsFile.asUint8Array());
			data.setImageElement(ImageElementUtils.create(dataUrl));
		}
		
		TextureData data=new TextureData();
		data.setImageDrawingDatas(datas);
		
		data.setBone(getBone(zip));
		
		return data;
	}
	
	public static TwoDimensionBone getBone(JSZip zip){
		JSFile jsFile=zip.getFile("bone.txt");
		if(jsFile!=null){
			String text=jsFile.asText();
			return new BoneConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
		}
		return null;
	}
	

	@Override
	protected JSZip doBackward(TextureData textureData) {
		final JSZip zip=JSZip.newJSZip();
		
		Iterable<String> lines=new ImageDrawingDataConverter().convertAll(textureData.getImageDrawingDatas());
		
		String indexText=Joiner.on("\r\n").join(lines);
		zip.file("index.txt", indexText);
		
		//LogUtils.log("index-created");
		
		List<String> zipped=new ArrayList<String>();
		
		for(ImageDrawingData data:textureData.getImageDrawingDatas()){
			if(data.getImageElement()==null){
				LogUtils.log(data.getId()+" has no image element");
				continue;
			}
			String fileName=data.getImageName();
			if(zipped.contains(fileName)){
				LogUtils.log("file-name already exist skipped");
				continue;
			}
			String dataUrl=ImageBuilder.from(data.getImageElement()).onFileName(fileName).toDataUrl();
			zip.base64UrlFile(fileName, dataUrl);
			
		}
		
		//bone
		if(textureData.getBone()!=null){
		zip.file("bone.txt", Joiner.on("\r\n").join(new BoneConverter().convert(textureData.getBone())));
		}
		
		return zip;
	}

}
