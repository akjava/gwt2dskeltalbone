package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import com.akjava.lib.common.graphics.Rect;
import com.google.common.base.Function;

public class ClipDataToShapeFunction implements Function<ClipData,PointShape>{

	@Override
	public PointShape apply(ClipData input) {
		Rect rect=input.getBound();
		rect.expandSelf(input.getExpand(), input.getExpand());
		
		PointShape shape=PointShape.createFromPointXY(input.getPoints());
		shape.addVector(-rect.getX(), -rect.getY());
		
		return shape;
	}

}
