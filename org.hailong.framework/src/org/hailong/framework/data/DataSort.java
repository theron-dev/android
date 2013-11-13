package org.hailong.framework.data;

import org.hailong.framework.data.annotation.DataField;

public final class DataSort {

	private DataSortType sortType;
	private DataField field;
	
	public DataSort(DataField field,DataSortType sortType){
		this.field = field;
		this.sortType = sortType;
	}
	
	public DataSortType getSortType(){
		return sortType;
	}
	
	public DataField getField(){
		return field;
	}
}
