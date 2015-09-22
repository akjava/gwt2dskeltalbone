package com.akjava.gwt.skeltalboneanimation.client.converters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.skeltalboneanimation.client.BoneUtils;
import com.akjava.gwt.skeltalboneanimation.client.IgnoreStartWithShape;
import com.akjava.gwt.skeltalboneanimation.client.bones.TwoDimensionBone;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Functions;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class BoneConverter extends Converter<TwoDimensionBone, List<String>>{

	@Override
	protected List<String> doForward(TwoDimensionBone rootBone) {
		List<TwoDimensionBone> bones=BoneUtils.getAllBone(rootBone);
		
		
		List<String> lines=FluentIterable.from(bones).transform(Functions.toStringFunction()).toList();
		
		return lines;
	}

	@Override
	protected TwoDimensionBone doBackward(List<String> lines) {
		Map<String,TwoDimensionBone> boneMap=new LinkedHashMap<String,TwoDimensionBone>();
		List<String> parentNames=new ArrayList<String>();
		TwoDimensionBone root=null;
		for(String line:Collections2.filter(lines, new IgnoreStartWithShape())){
			String[] values=line.trim().split(",");//must be 4 or 5
			if(values.length!=4 && values.length!=5){
				LogUtils.log("invalid line:"+line);
				continue;//just ignore
			}
			int x=Integer.parseInt(values[2]);
			int y=Integer.parseInt(values[3]);
			TwoDimensionBone bone=new TwoDimensionBone(values[1], x, y);
			parentNames.add(values[0]);
			boneMap.put(bone.getName(), bone);
			if(root==null){
				root=bone;
			}
			if(values.length>4){
				bone.setLocked(ValuesUtils.toBoolean(values[4], false));
			}
			
		}
		List<TwoDimensionBone> boneList=Lists.newArrayList(boneMap.values());
		for(int i=0;i<boneList.size();i++){
			String parentName=parentNames.get(i);
			if(parentName!=null && !parentName.isEmpty()){
				TwoDimensionBone parent=boneMap.get(parentName);
				//boneList.get(i).setParent(parent);
				
				if(parent==null){
					LogUtils.log("invalid parent-name in "+boneList.get(i).getName()+" parentName="+parentName);
				}else{
					parent.addBone(boneList.get(i));
				}
			}
		}
		
		return root;
		
	}
	
	
	


}
