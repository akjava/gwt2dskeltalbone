package com.akjava.gwt.skeltalboneanimation.client.page.texture;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public abstract class HasUniqData<T> {
public  abstract String getId(T data);

public abstract List<T> getDatas();

public Optional<T> findDataById(String id){
	for(T data:getDatas()){
		if(Objects.equal(id, getId(data))){
			return Optional.of(data);
		}
	}
	return Optional.absent();
}

}
