package com.akjava.gwt.skeltalboneanimation.client.predicates;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipImageData;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

public class ImageDrawingDataPredicates {

	public static class NotExistInIds implements Predicate<ImageDrawingData>{
		private List<String> ids;
		public NotExistInIds(List<String> ids) {
			super();
			this.ids = ids;
		}
		@Override
		public boolean apply(ImageDrawingData input) {
			return !ids.contains(input.getId());
		}
		
	}
	
	//texture data not linked in clipimage data ,checking by id;
	public static Predicate<ImageDrawingData> notExistInClipImageDatas(ClipImageData clipImageData){
		
		List<String> textureIds=FluentIterable.from(clipImageData.getClips()).transform(new Function<ClipData,String>(){

			@Override
			public String apply(ClipData input) {
				if(input.getLinkedImageDrawingData().isPresent()){
					return input.getLinkedImageDrawingData().get().getId();
				}else{
					return null;
				}
			}}).filter(Predicates.notNull()).toList();
		return new NotExistInIds(textureIds);
		
	}
}
