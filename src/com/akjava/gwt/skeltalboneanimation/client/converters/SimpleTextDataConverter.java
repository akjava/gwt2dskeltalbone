package com.akjava.gwt.skeltalboneanimation.client.converters;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.lib.client.datalist.SimpleTextData;
import com.akjava.gwt.lib.client.datalist.functions.CsvArrayToHeadAndValueFunction;
import com.akjava.gwt.lib.client.datalist.functions.HeadAndValueToCsvFunction;
import com.akjava.lib.common.csv.CSVReader;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.gwt.user.client.Window;

public class SimpleTextDataConverter extends Converter<List<SimpleTextData>,String>{

	@Override
	protected String doForward(List<SimpleTextData> datas) {
		List<String> lines=FluentIterable.from(datas).transform(new HeadAndValueToCsvFunction()).toList();
		String exportText=Joiner.on("\r\n").join(lines);
		return exportText;
	}

	@Override
	protected List<SimpleTextData> doBackward(String csv) {
		CSVReader reader=new CSVReader(csv,'\t','"',true);
		try {
			List<String[]> csvs=reader.readAll();
			return FluentIterable.from(csvs).transform(new CsvArrayToHeadAndValueFunction()).toList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Window.alert(e.getMessage());
		}
		return null;
	}

}
