package com.akjava.gwt.skeltalboneanimation.client.page.html5app;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

/*
 * has original image & dataurl for modify
 */
public class ImageElementData2 implements HasImageUrl{

		private ImageElement imageElement;
		public ImageElement getImageElement() {
			return imageElement;
		}
		public void setImageElement(ImageElement imageElement) {
			this.imageElement = imageElement;
		}

		private ImageDrawingData imageDrawingData;
		
		public ImageDrawingData getImageDrawingData() {
			return imageDrawingData;
		}
		public void setImageDrawingData(ImageDrawingData imageDrawingData) {
			this.imageDrawingData = imageDrawingData;
		}

		private String dataUrl;

		public ImageElementData2(String fileName,ImageElement imageElement ,String dataUrl) {
			super();
			this.fileName = fileName;
			this.imageElement=imageElement;
			this.dataUrl = dataUrl;
		}
		
		public ImageElementData2(String fileName,ImageDrawingData imageDrawingData ,String dataUrl) {
			super();
			this.fileName = fileName;
			this.imageDrawingData=imageDrawingData;
			this.imageElement=imageDrawingData.getImageElement();
			this.dataUrl = dataUrl;
		}
		
		private String fileName;
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getDataUrl() {
			return dataUrl;
		}
		public void setDataUrl(String dataUrl) {
			this.dataUrl = dataUrl;
		}
		public String getImageUrl() {
			return getDataUrl();
		}
		
		public ImageElementData2 copy(){
			ImageElement element=ImageElement.as(DOM.createImg());
			element.setSrc(imageElement.getSrc());
			ImageElementData2 newData=new ImageElementData2(getFileName(),element,getDataUrl());
			return newData;
		}
		
	}