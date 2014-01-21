package org.hailong.framework.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hailong.framework.data.annotation.DataEntity;

public class DataModel {
	
	private List<DataEntity> _dataEntitys;
	private Map<DataEntity,Class<?>> _dataObjectClasss;
	
	public DataModel(){
		_dataEntitys = new ArrayList<DataEntity>();
		_dataObjectClasss =new HashMap<DataEntity,Class<?>>();
	}
	
	public DataEntity[] getDataEntitys(){
		return _dataEntitys.toArray(new DataEntity[_dataEntitys.size()]);
	}

	
	public <T extends DataObject> void addDataObjectClass(Class<T> dataObjectClass) {
		DataEntity dataEntity = dataObjectClass.getAnnotation(DataEntity.class);
		if(dataEntity != null){
			if(_dataEntitys.indexOf(dataEntity) <0){
				_dataEntitys.add(dataEntity);
				_dataObjectClasss.put(dataEntity, dataObjectClass);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends DataObject> Class<T> getDataObjectClass(DataEntity dataEntity){
		return (Class<T>)_dataObjectClasss.get(dataEntity);
	}
	
	
}
