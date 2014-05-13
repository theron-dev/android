package org.hailong.db;

import org.hailong.db.annotation.DBField;
import org.hailong.db.annotation.DBFieldType;

public class DBObject {

	private IDBObjectValues _objectValues;
	
	public boolean isFault(){
		return _objectValues == null || _objectValues.isDeleted();
	}
	
	public Object getObjectValue(DBField field){
		Object value = _objectValues != null? _objectValues.getValue(field):null;
		if(value == null){
			DBFieldType type = field.type();
			if(type == DBFieldType.BIGINT){
				return Long.valueOf(0);
			}
			else if(type ==DBFieldType.INT){
				return Integer.valueOf(0);
			}
			else if(type ==DBFieldType.DOUBLE){
				return Double.valueOf(0.0);
			}
		}
		return value;
	}
	
	public void setObjectValue(DBField field,Object value){
		if(_objectValues !=null){
			_objectValues.setValue(field, value);
		}
	}
	
	public long getRowId(){
		return _objectValues != null ? _objectValues.getRowId() : 0;
	}
	
	IDBObjectValues getObjectValues(){
		return _objectValues;
	}
	
	void setObjectValues(IDBObjectValues objectValues){
		if(objectValues !=null){
			objectValues.retain();
		}
		if(_objectValues !=null){
			_objectValues.release();
		}
		_objectValues = objectValues;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if(_objectValues !=null){
			_objectValues.release();
		}
		_objectValues = null;
		super.finalize();
	}
	
	@Override
	public int hashCode(){
		if(_objectValues != null){
			return _objectValues.hashCode();
		}
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object object){
		
		if(object instanceof DBObject){
			
			if(_objectValues == ((DBObject) object).getObjectValues()){
				
				if(_objectValues == null){
					return super.equals(object);
				}
				
				return true;
			}

		}
		
		return super.equals(object);
	}
}
