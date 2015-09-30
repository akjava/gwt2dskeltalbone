package com.akjava.gwt.skeltalboneanimation.client.page.html5app;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.PointShape;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

/*
 * has original image & dataurl for modify
 */
public class ImageElementData2 implements HasImageUrl{

		private PointShape pointShape;
		public Optional<PointShape> getPointShape() {
			return Optional.fromNullable(pointShape);
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
		
		public Optional<ImageDrawingData> getImageDrawingData() {
			return Optional.fromNullable(imageDrawingData);
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
			this.idSupplier = Suppliers.ofInstance(fileName);
			this.imageElement=imageElement;
			this.dataUrl = dataUrl;
			this.initialDataUrl=dataUrl;
		}
		
		private Supplier<String> idSupplier;
		public Supplier<String> getIdSupplier() {
			return idSupplier;
		}
		public void setIdSupplier(Supplier<String> idSupplier) {
			this.idSupplier = idSupplier;
		}
		public ImageElementData2(Supplier<String> idSupplier,ImageDrawingData imageDrawingData ,String dataUrl,PointShape pointShape) {
			super();
			//this.fileName = fileName;
			this.idSupplier=idSupplier;
			this.imageDrawingData=imageDrawingData;
			this.imageElement=imageDrawingData.getImageElement();
			this.dataUrl = dataUrl;
			this.initialDataUrl=dataUrl;
			this.pointShape=pointShape;
		}
		
		//private String fileName;
		
		public String getDataUrl() {
			return dataUrl;
		}
		public void setDataUrl(String dataUrl) {
			this.dataUrl = dataUrl;
		}
		public String getImageUrl() {
			return getDataUrl();
		}
		public String getId(){
			return idSupplier.get();
		}
		
		public ImageElementData2 copyAsFileData(){
			ImageElement element=ImageElement.as(DOM.createImg());
			element.setSrc(imageElement.getSrc());
			ImageElementData2 newData=new ImageElementData2(idSupplier.get(),element,getDataUrl());
			return newData;
		}
		
	}