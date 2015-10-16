package com.akjava.gwt.skeltalboneanimation.client.predicates;

import java.util.List;

import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.google.common.base.Predicate;

public class BonePredicates {

	public static class NotLocked implements Predicate<TwoDimensionBone>{

		@Override
		public boolean apply(TwoDimensionBone input) {
			return !input.isLocked();
		}
		
	}
	
	public static class NotExistInNames implements Predicate<TwoDimensionBone>{
		private List<String> names;
		
		public NotExistInNames(List<String> names) {
			super();
			this.names = names;
		}

		@Override
		public boolean apply(TwoDimensionBone input) {
			return !names.contains(input.getName());
		}
		
	}
	public static class HasName implements Predicate<TwoDimensionBone>{
		@Override
		public boolean apply(TwoDimensionBone input) {
			return input.getName()!=null;
		}
		
	}
}
