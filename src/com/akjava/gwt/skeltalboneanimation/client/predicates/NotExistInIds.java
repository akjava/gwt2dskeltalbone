package com.akjava.gwt.skeltalboneanimation.client.predicates;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.ImageDrawingData;
import com.google.common.base.Predicate;

public class NotExistInIds implements Predicate<ImageDrawingData>{
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