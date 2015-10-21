package com.akjava.gwt.skeltalboneanimation.client.bones;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Equivalence;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TextureFrame {
	
	
	
	public TextureFrame(boolean needResetState, boolean needResetOrder) {
		super();
		this.needResetState = needResetState;
		this.needResetOrder = needResetOrder;
	}

	public TextureFrame(List<String> textureOrder, List<TextureState> textureUpdates) {
		super();
		this.textureUpdates = textureUpdates;
		this.textureOrder = textureOrder;
	}

	public boolean needResetState;
	public boolean isNeedResetState() {
		return needResetState;
	}
	public void setNeedResetState(boolean needResetState) {
		this.needResetState = needResetState;
	}
	public boolean isNeedResetOrder() {
		return needResetOrder;
	}
	public void setNeedResetOrder(boolean needResetOrder) {
		this.needResetOrder = needResetOrder;
	}
	public void setTextureUpdates(List<TextureState> textureUpdates) {
		this.textureUpdates = textureUpdates;
	}
	public void setTextureOrder(List<String> textureOrder) {
		this.textureOrder = textureOrder;
	}

	public boolean needResetOrder;
	
	
	
	
	private List<TextureState> textureUpdates;
	private List<String> textureOrder;
	
	public Optional<List<String>> getTextureOrder(){
		return Optional.fromNullable(textureOrder);
	}
	public Optional<List<TextureState>> getTextureUpdates(){
		return Optional.fromNullable(textureUpdates);
	}
	
	private Equivalence<TextureState> idEquivalence=new Equivalence<TextureFrame.TextureState>() {
		
		@Override
		protected int doHash(TextureState t) {
			return t.hashCode();
		}
		
		@Override
		protected boolean doEquivalent(TextureState a, TextureState b) {
			return a.getId().equals(b.getId());
		}
	};
	
	
	public void updateTextureState(TextureState state){
		if(textureUpdates==null){
			textureUpdates=Lists.newArrayList();
		}
		int index=-1;
		for(int i=0;i<textureUpdates.size();i++){
			if(idEquivalence.equivalent(state, textureUpdates.get(i))){
				index=i;
				break;
			}
		}
		
		//replace
		if(index!=-1){
			LogUtils.log("updateTextureState");
			textureUpdates.remove(index);
			textureUpdates.add(index, state);
		}else{
			//add
			textureUpdates.add(state);
			//LogUtils.log("updateTextureState:not found "+state.getId());
		}
	}
	
	public TextureState getTextureState(String id){
		if(textureUpdates==null){
			return null;
		}
		TextureState tmp=new TextureState(id);
		
		int index=-1;
		for(int i=0;i<textureUpdates.size();i++){
			if(idEquivalence.equivalent(tmp, textureUpdates.get(i))){
				index=i;
				break;
			}
		}
		
		if(index!=-1){
			return textureUpdates.get(index);
		}else{
			//LogUtils.log("getTestureState:not found:"+id);
			return null;
		}
	}
	
	public void removeTextureState(String id){
		if(textureUpdates==null){
			return;
		}
		TextureState tmp=new TextureState(id);
		
		int index=-1;
		for(int i=0;i<textureUpdates.size();i++){
			if(idEquivalence.equivalent(tmp, textureUpdates.get(i))){
				index=i;
				break;
			}
		}
		
		if(index!=-1){
		textureUpdates.remove(index);
		if(textureUpdates.isEmpty()){
			textureUpdates=null;
		}
		}else{
			LogUtils.log("removeTextureState:not found:"+id);
		}
	}
	
	//reset order
	//reset texturestate
	//new order
	//state changes
	
	//state control,textureData	
	
	public static class TextureState{
		private String id;

		public TextureState(String id) {
			super();
			this.id =checkNotNull(id,"TextureState:not null id permitted");
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		//additional,means 0 or false no effect
		private double x;
		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public double getAngle() {
			return angle;
		}

		public void setAngle(double angle) {
			this.angle = angle;
		}

		public double getScaleX() {
			return scaleX;
		}

		public void setScaleX(double scaleX) {
			this.scaleX = scaleX;
		}

		public double getScaleY() {
			return scaleY;
		}

		public void setScaleY(double scaleY) {
			this.scaleY = scaleY;
		}

		public double getAlpha() {
			return alpha;
		}

		public void setAlpha(double alpha) {
			this.alpha = alpha;
		}

		public boolean isFlipHorizontal() {
			return flipHorizontal;
		}

		public void setFlipHorizontal(boolean flipHorizontal) {
			this.flipHorizontal = flipHorizontal;
		}

		public boolean isFlipVertical() {
			return flipVertical;
		}

		public void setFlipVertical(boolean flipVertical) {
			this.flipVertical = flipVertical;
		}

		public boolean isVisible() {
			return visible;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
		}
		private double y;
		private double angle;
		private double scaleX;
		private double scaleY;
		private double alpha;
		private boolean flipHorizontal;
		private boolean flipVertical;
		private boolean visible;
		
		
		public boolean isModified(){
			if(x!=0){
				return true;
			}
			if(y!=0){
				return true;
			}
			if(angle!=0){
				return true;
			}
			if(scaleX!=0){
				return true;
			}
			if(scaleY!=0){
				return true;
			}
			
			if(alpha!=0){
				return true;
			}
			
			if(flipHorizontal){
				return true;
			}
			
			if(flipVertical){
				return true;
			}
			
			
			if(visible){
				return true;
			}
			
			return false;
		}
		
		public String toCsv(String separator){
			
			return Joiner.on(separator).join(toMap().values());
		}
		//TODO copy
		public String toString(){
			
			return Joiner.on(",").withKeyValueSeparator("=").join(toMap());
		}
		
		private Map<String,Object> toMap(){
			Map<String,Object> map=Maps.newLinkedHashMap();
			map.put("id", id);
			map.put("x", x);
			map.put("y", y);
			map.put("angle", angle);
			map.put("scaleX", scaleX);
			map.put("scaleY", scaleY);
			map.put("alpha", alpha);
			map.put("flipHorizontal", flipHorizontal);
			map.put("flipVertical", flipVertical);
			map.put("visible", visible);
			return  map;
		}
	}

	public TextureFrame copy() {
		TextureFrame frame=new TextureFrame(textureOrder,textureUpdates);
		frame.setNeedResetOrder(isNeedResetOrder());
		frame.setNeedResetState(isNeedResetState());
		return frame;
	}
}
