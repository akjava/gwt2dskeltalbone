package com.akjava.gwt.skeltalboneanimation.client.page.html5app;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.PointShape;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

/*
 * has original image & dataurl for modify
 */
public class ImageElementData2 implements HasImageUrl{

		private PointShape pointShape;
		public PointShape getPointShape() {
			return pointShape;
		}
		public void setPointShape(PointShape pointShape) {
			this.pointShape = pointShape;
		}

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
		private String initialDataUrl;//for reset;
		public String getInitialDataUrl() {
			return initialDataUrl;
		}
		public void setInitialDataUrl(String initialDataUrl) {
			this.initialDataUrl = initialDataUrl;
		}
		public ImageElementData2(String fileName,ImageElement imageElement ,String dataUrl) {
			super();
			this.fileName = fileName;
			this.imageElement=imageElement;
			this.dataUrl = dataUrl;
		}
		
		public ImageElementData2(String fileName,ImageDrawingData imageDrawingData ,String dataUrl,PointShape pointShape) {
			super();
			this.fileName = fileName;
			this.imageDrawingData=imageDrawingData;
			this.imageElement=imageDrawingData.getImageElement();
			this.dataUrl = dataUrl;
			this.initialDataUrl=dataUrl;
			this.pointShape=pointShape;
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