package com.akjava.gwt.skeltalboneanimation.client.bones;

import java.util.ArrayList;
import java.util.List;

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
}
