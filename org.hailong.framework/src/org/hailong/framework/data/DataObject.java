package org.hailong.framework.data;

import org.hailong.framework.data.annotation.DataField;
import org.hailong.framework.data.annotation.DataFieldType;

public class DataObject {

	private IDataEntityRawData _rawData;
	
	public boolean isFault(){
		return _rawData == null || _rawData.isDeleted();
	}
	
	public Object getValue(DataField field){
		Object value = _rawData != null? _rawData.getValue(field):null;
		if(value == null){
			DataFieldType type = field.type();
			if(type == DataFieldType.BIGINT){
				return Long.valueOf(0);
			}
			else if(type ==DataFieldType.INT){
				return Integer.valueOf(0);
			}
			else if(type ==DataFieldType.DOUBLE){
				return Double.valueOf(0.0);
			}
		}
		return value;
	}
	
	public void setValue(DataField field,Object value){
		if(_rawData !=null){
			_rawData.setValue(field, value);
		}
	}
	
	IDataEntityRawData getRawData(){
		return _rawData;
	}
	
	void setRawData(IDataEntityRawData rawData){
		if(rawData !=null){
			rawData.retain();
		}
		if(_rawData !=null){
			_rawData.release();
		}
		_rawData = rawData;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if(_rawData !=null){
			_rawData.release();
		}
		_rawData = null;
		super.finalize();
	}
	
}
