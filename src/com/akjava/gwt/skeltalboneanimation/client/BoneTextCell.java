/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.akjava.gwt.skeltalboneanimation.client;

import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
/**
 * A {@link Cell} used to render text.
 */
public class BoneTextCell extends AbstractSafeHtmlCell<TwoDimensionBone> {

  /**
   * Constructs a TextCell that uses a {@link SimpleSafeHtmlRenderer} to render
   * its text.
   */
  public BoneTextCell() {
    this(new SafeHtmlRenderer<TwoDimensionBone>() {

		@Override
		public SafeHtml render(final TwoDimensionBone object) {
			// TODO Auto-generated method stub
			return new SafeHtml() {
				
				@Override
				public String asString() {
					// TODO Auto-generated method stub
					return object.getName();
				}
			};
		}

		@Override
		public void render(TwoDimensionBone object, SafeHtmlBuilder builder) {
			if(object!=null){
				builder.appendEscaped(object.getName());
			}
		}
	});
  }

  /**
   * Constructs a TextCell that uses the provided {@link SafeHtmlRenderer} to
   * render its text.
   * 
   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
   */
  public BoneTextCell(SafeHtmlRenderer<TwoDimensionBone> renderer) {
    super(renderer);
  }

  @Override
  public void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
    if (value != null) {
      sb.append(value);
    }
  }
}
