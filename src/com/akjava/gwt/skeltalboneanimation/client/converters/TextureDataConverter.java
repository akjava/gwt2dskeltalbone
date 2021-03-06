package com.akjava.gwt.skeltalboneanimation.client.converters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class TextureDataConverter extends Converter<JSZip,TextureData>{

	public static String TEXTURE_HEADER="#texturedata";
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
		
		TextureData textureData=new TextureData();
		if(lines.get(0).startsWith(TEXTURE_HEADER)){
			String parameters=lines.get(0).substring(TEXTURE_HEADER.length());
			Map<String,String> textureDataParameter=Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator("=").split(parameters);
			int format=ValuesUtils.toInt(textureDataParameter.get("format"), 1);
			
			int offsetX=ValuesUtils.toInt(textureDataParameter.get("offsetX"), -1);
			int offsetY=ValuesUtils.toInt(textureDataParameter.get("offsetY"), -1);
			
			if(offsetX!=-1){
				textureData.setOffsetX(offsetX);
			}
			if(offsetY!=-1){
				textureData.setOffsetY(offsetY);
			}
			lines.remove(0);
			
			//LogUtils.log("texturedata format="+format+",offsetX="+offsetX+",offsetY="+offsetY);
		}
		
		
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
		
		
		textureData.setImageDrawingDatas(datas);
		
		textureData.setBone(getBone(zip));
		
		return textureData;
	}
	
	public static TwoDimensionBone getBone(JSZip zip){
		JSFile jsFile=zip.getFile("bone.txt");
		if(jsFile!=null){
			String text=jsFile.asText();
			return new BoneConverter().reverse().convert(CSVUtils.splitLinesWithGuava(text));
		}
		return null;
	}
	
	public String toTextureDataHeader(TextureData data){
		List<String> values=Lists.newArrayList();
		
		values.add(TEXTURE_HEADER);
		values.add("format=1");
		values.add("offsetX="+data.getOffsetX());
		values.add("offsetY="+data.getOffsetY());
		
		return Joiner.on(",").join(values)+"\r\n";
	}

	@Override
	protected JSZip doBackward(TextureData textureData) {
		final JSZip zip=JSZip.newJSZip();
		
		Iterable<String> lines=new ImageDrawingDataConverter().convertAll(textureData.getImageDrawingDatas());
		
		String indexText=Joiner.on("\r\n").join(lines);
		zip.file("index.txt", toTextureDataHeader(textureData)+indexText);
		
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
