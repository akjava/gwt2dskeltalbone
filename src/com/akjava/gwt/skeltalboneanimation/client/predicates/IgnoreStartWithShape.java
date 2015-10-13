package com.akjava.gwt.skeltalboneanimation.client.predicates;

import com.google.common.base.Predicate;

public  class IgnoreStartWithShape implements Predicate<String>{

	@Override
	public boolean apply(String input) {
		input=input.trim();
		if(input==null || input.isEmpty() || input.startsWith("#")){
			return false;
		}else{
			return true;
		}
	}
	
}