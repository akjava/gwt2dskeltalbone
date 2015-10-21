package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.TextureFrame.TextureState;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
		List<TextureState> states=null;
		
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
				
				for(List<TextureState> states2:textureFrame.getTextureUpdates().asSet()){
					if(states==null){
						states=Lists.newArrayList();
					}
					Iterables.concat(states,states2);
				}
			}
		}
		
		if(order!=null || states!=null){
			return Optional.of(new TextureFrame(states, order));
		}else{
			return Optional.absent();
		}
		
	}
}
