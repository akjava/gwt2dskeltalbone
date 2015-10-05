package com.akjava.gwt.skeltalboneanimation.client.bones;

import com.google.common.base.Function;

public class AnimationFrameCopyFunction implements Function<AnimationFrame,AnimationFrame>{

	@Override
	public AnimationFrame apply(AnimationFrame input) {
		return input.copy();
	}

}
