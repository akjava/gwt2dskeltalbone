package com.akjava.gwt.skeltalboneanimation.client.predicates;

import com.akjava.gwt.skeltalboneanimation.client.page.clippage.ClipData;
import com.google.common.base.Predicate;

public class ClipDataPredicates {

	public static class IsMatchId implements Predicate<ClipData>{
		private String id;
		public IsMatchId(String id){
			this.id=id;
		}
		@Override
		public boolean apply(ClipData input) {
			return input.getId().equals(id);
		}	
	}
	


}
