package com.akjava.gwt.skeltalboneanimation.client.page.clippage;

import com.google.common.base.Equivalence;
import com.google.common.base.Objects;

public class ClipDataWithtouChildrenEquivalence extends Equivalence<ClipData>{

	/*
	 * right now just compare bone & expand
	 * (non-Javadoc)
	 * @see com.google.common.base.Equivalence#doEquivalent(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected boolean doEquivalent(ClipData a, ClipData b) {
		if(!Objects.equal(a, b)){
			return false;
		}
		
		if(a.getExpand()!=b.getExpand()){
			return false;
		}
		
		return true;
	}

	@Override
	protected int doHash(ClipData t) {
		return t.hashCode();
	}

}
