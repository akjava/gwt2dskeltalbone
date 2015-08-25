package com.akjava.gwt.skeltalboneanimation.client.bones;

public interface BonePainter {
	public void startPaint();
	public void endPaint();
	/*
	 * if bone is root start & end cordinates are same.
	 */
	public  void paintBone(String name,String parentName,int startX,int startY,int endX,int endY,double angle);
}
