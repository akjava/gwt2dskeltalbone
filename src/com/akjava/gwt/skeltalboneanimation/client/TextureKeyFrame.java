package com.akjava.gwt.skeltalboneanimation.client;

import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.base.Optional;

/*
 * FUTURE
 * control texture on animation
 */
public class TextureKeyFrame {
	public static class TextureStatus{
		private String id;
		private boolean flipHorizontal;
		private boolean flipVertical;
		private boolean visible;
	}
	/**
	 * order by key
	 */
private List<String> order;

public Optional<List<String>> getOrder() {
		return Optional.fromNullable(order);
	}
	public void setOrder(List<String> order) {
		this.order = order;
	}
	
public Optional<TextureStatus> getStatus(String id){
	if(statuses==null){
		return Optional.absent();
	}
	return Optional.fromNullable(statuses.get(id));
}
private LinkedHashMap<String,TextureStatus> statuses;

public void addTextureStatus(TextureStatus status){
	if(statuses==null){
		statuses=new LinkedHashMap<String, TextureKeyFrame.TextureStatus>();
	}
	statuses.put(status.id,status);
}

}
