package org.hailong.db;

import java.util.HashMap;
import java.util.Map;

import org.hailong.core.JSON;
import org.hailong.db.annotation.DBEntity;
import org.hailong.db.annotation.DBField;
import org.hailong.db.annotation.DBFieldType;
import org.json.JSONException;

import android.util.Log;

public class DBObject {

	private IDBObjectValues _objectValues;
	private Map<String,Object> _values;
	
	public DBObject() {	
	}
	
	public boolean isFault(){
		return _objectValues == null || _objectValues.isDeleted();
	}
	
	public Object getValue(DBField field){
		Object value = null;
		if(_objectValues != null){
			value = _objectValues.getValue(field);
		}
		else if(_values != null){
			value = _values.get(field.value());
		}
		if(value != null){
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
			else if(type == DBFieldType.BYTES){
				if(value instanceof byte[]){
					return (byte[]) value;
				}
				return null;
			}
			else if(type == DBFieldType.VARCHAR || type == DBFieldType.TEXT){
				if(value instanceof String){
					return (String) value;
				}
				return value.toString();
			}
			else if(type == DBFieldType.OBJECT){
				try {
					return JSON.decodeString(value.toString());
				} catch (JSONException e) {
					Log.e(DB.TAG, Log.getStackTraceString(e));
				}
			}
		}
		return value;
	}
	
	public void setValue(DBField field,Object value){
		if(_objectValues !=null){
			_objectValues.setValue(field, value);
		}
		else{
			if(_values == null){
				_values = new HashMap<String,Object>(4);
			}
			_values.put(field.value(), value);
		}
	}
	
	public boolean isDeleted(){
		return _objectValues != null && _objectValues.isDeleted();
	}
	
	public Object getValue(String field){
		
		if(_objectValues != null){
			return _objectValues.getValue(field);
		}
		else if(_values != null){
			return _values.get(field);
		}
		
		return null;
	}
	
	public long longValue(DBField field, long defaultValue){
		return longValue(field.value(),defaultValue);
	}
	
	public long longValue(String field, long defaultValue){
		
		if(_objectValues != null){
			return _objectValues.longValue(field, defaultValue);
		}
		else if(_values != null){
			Object v = _values.get(field);
			if(v != null){
				if(v instanceof Number){
					return ((Number) v).longValue();
				}
				else{
					return Long.valueOf(v.toString());
				}
			}
		}
		
		return defaultValue;
	}
	
	public long intValue(DBField field, int defaultValue){
		return intValue(field.value(),defaultValue);
	}
	
	public int intValue(String field, int defaultValue){
		
		if(_objectValues != null){
			return _objectValues.intValue(field, defaultValue);
		}
		else if(_values != null){
			Object v = _values.get(field);
			if(v != null){
				if(v instanceof Number){
					return ((Number) v).intValue();
				}
				else{
					return Integer.valueOf(v.toString());
				}
			}
		}
		
		return defaultValue;
	}
	
	public double doubleValue(DBField field, double defaultValue){
		return doubleValue(field.value(),defaultValue);
	}
	
	public double doubleValue(String field, double defaultValue){
		
		if(_objectValues != null){
			return _objectValues.doubleValue(field, defaultValue);
		}
		else if(_values != null){
			Object v = _values.get(field);
			if(v != null){
				if(v instanceof Number){
					return ((Number) v).doubleValue();
				}
				else{
					return Double.valueOf(v.toString());
				}
			}
		}
		
		return defaultValue;
	}
	
	public String stringValue(DBField field, String defaultValue){
		return stringValue(field.value(),defaultValue);
	}
	
	public String stringValue(String field, String defaultValue){
		if(_objectValues != null){
			return _objectValues.stringValue(field, defaultValue);
		}
		else if(_values != null){
			Object v = _values.get(field);
			if(v != null){
				if(v instanceof String){
					return ((String) v);
				}
				else{
					return v.toString();
				}
			}
		}
		
		return defaultValue;
	}

	public byte[] bytesValue(DBField field, byte[] defaultValue){
		return bytesValue(field.value(),defaultValue);
	}
	
	public byte[] bytesValue(String field,byte[] defaultValue){
		
		if(_objectValues != null){
			return _objectValues.bytesValue(field, defaultValue);
		}
		else if(_values != null){
			Object v = _values.get(field);
			if(v != null){
				if(v instanceof byte[]){
					return ((byte[]) v);
				}
			}
		}
		
		return defaultValue;
	}
	
	public long rowid(){
		return _objectValues != null ? _objectValues.rowid() : 0;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if(_objectValues !=null){
			_objectValues.release();
		}
		_objectValues = null;
		_values = null;
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
			
			if(_objectValues == ((DBObject) object)._objectValues){
				
				if(_objectValues == null){
					return super.equals(object);
				}
				
				return true;
			}

		}
		
		return super.equals(object);
	}
	
	public String dataKey(){
		DBEntity dbEntity = getClass().getAnnotation(DBEntity.class);
		if(dbEntity != null){
			String key = dbEntity.dataKey();
			if("rowid".equals(key)){
				return String.valueOf(rowid());
			}
			return stringValue(key,null);
		}
		return null;
	}
	
	IDBObjectValues objectValues(){
		return _objectValues;
	}
	
	Map<String,Object> values(){
		return _values;
	}
	
	void setObjectValues(IDBObjectValues objectValues){
		if(objectValues != null){
			objectValues.retain();
		}
		if(_objectValues != null){
			_objectValues.release();
		}
		_objectValues = objectValues;
		_values = null;
	}
}
