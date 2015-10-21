package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame.TextureState;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SkeletalAnimation {
	private String name;
	private double fps;
	public SkeletalAnimation(){
		this("",33.34);
	}
	public SkeletalAnimation(String name, double fps) {
		super();
		this.name = name;
		this.fps = fps;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getFps() {
		return fps;
	}
	public void setFps(double fps) {
		this.fps = fps;
	}
	
	private List<AnimationFrame> frames=new ArrayList<AnimationFrame>();
	public List<AnimationFrame> getFrames() {
		return frames;
	}
	public void setFrames(List<AnimationFrame> frames) {
		this.frames = frames;
	}
	public void add(AnimationFrame frame){
		frames.add(frame);
	}
	public void clear() {
		frames.clear();
	}
	
	public SkeletalAnimation copy(){
		SkeletalAnimation skeletalAnimation=new SkeletalAnimation(name,fps);
		for(AnimationFrame frame:frames){
			skeletalAnimation.add(frame);
		}
		return skeletalAnimation;
	}
	
	public Optional<TextureFrame> getMergedTextureFrameAt(AnimationFrame frame){
		int index=frames.indexOf(frame);
		if(index==-1){
			return Optional.absent();
		}
		
		List<String> order=null;
		Map<String,TextureState> states=null;
		
		//contain self
		for(int i=0;i<=index;i++){
			TextureFrame textureFrame=frames.get(i).getTextureFrame();
			if(textureFrame!=null){
				if(textureFrame.isNeedResetOrder()){
					order=null;
				}
				
				if(textureFrame.isNeedResetState()){
					states=null;
				}
				
				for(List<String> order2:textureFrame.getTextureOrder().asSet()){
					order=order2;
				}
				
				if(states==null){
					states=Maps.newHashMap();
				}
				for(List<TextureState> states2:textureFrame.getTextureUpdates().asSet()){
					
					//replace old one
					for(TextureState state:states2){
						states.put(state.getId()	, state);
					}
					
				}
			}
		}
		
		if(order!=null || states!=null){
			return Optional.of(new TextureFrame(order, ImmutableList.copyOf(states.values())));
		}else{
			return Optional.absent();
		}
		
	}
}
