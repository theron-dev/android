package org.hailong.db;

import java.util.Map;

import org.hailong.db.annotation.DBEntity;
import org.hailong.db.annotation.DBField;

public class DBContext {

	private Map<DBEntity,Map<String,IDBObjectValues>> _dataObjects;
	private IDBStorage _storage;
	
	public DBContext(IDBStorage storage){
		_storage = storage;
	}
	
	public IDBStorage getStorage(){
		return _storage;
	}
	
	public void destroy(){
		if(_storage != null){
			_storage.destroy();
			_storage = null;
		}
		if(_dataObjects != null){
			_dataObjects = null;
		}
	}
	
	public String[] dataKeys(DBEntity dbEntity, String sql, Object[] args) throws Throwable{
		
		return null;
	}
	
	public DBObject dataObject(DBEntity dbEntity, String dataKey) throws Throwable{
		
		IDBObjectValues objectValues = null;
		
		if(_dataObjects != null){
			
			Map<String,IDBObjectValues> objects = _dataObjects.get(dbEntity);
			
			if(objects != null){
				
				objectValues = objects.get(dataKey);
				
			}
			
		}
		
		if(objectValues == null){
			
			Map<String,Object> values = _storage.get(dbEntity, dataKey);
			
			if(values != null){
				
			}
			
		}
		
		return null;
	}

	public DBObject dataObjectForCache(DBEntity dbEntity, String dataKey) throws Throwable{
		
		if(_dataObjects != null){
			
			Map<String,IDBObjectValues> objects = _dataObjects.get(dbEntity);
			
			if(objects != null){
				
				IDBObjectValues objectValues = objects.get(dataKey);
				
				if(objectValues != null){
					
					DBObject dataObject = new DBObject();
					
					dataObject.setObjectValues(objectValues);
					
					return dataObject;
				}
	
			}
			
		}
		
		return null;
	}

	private class DBObjectValues implements  IDBObjectValues{

		private DBEntity _dbEntity;
		private String _dataKey;
		private Map<String,Object> _values;
		private int _retainCount;
		private boolean _deleted;
		
		public DBObjectValues(DBEntity dbEntity,String dataKey,Map<String,Object> values){
			_dbEntity = dbEntity;
			_dataKey = dataKey;
			_values = values;
			_retainCount = 1;
		}
		
		@Override
		public long getRowId() {
			return longValue("rowid",0);
		}

		@Override
		public boolean isDeleted() {
			return _deleted;
		}
		
		public void setDeleted(boolean deleted){
			_deleted = deleted;
		}

		@Override
		public Object getValue(DBField field) {
			return getValue(field.value());
		}

		@Override
		public Object getValue(String field) {
			return _values != null ? _values.get(field) : null;
		}

		@Override
		public void setValue(DBField field, Object value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int retainCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void retain() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void release() {
			// TODO Auto-generated method stub
			
		}
		
		public long longValue(String field, long defaultValue){
			
			Object v = getValue(field);
			
			if(v != null){
				
				if(v instanceof Number){
					return ((Number) v).longValue();
				}
				
				if(v instanceof String){
					return Long.valueOf((String) v);
				}
				
				return defaultValue;
			}
			
			return defaultValue;
		}
		
		public int intValue(String field, int defaultValue){
			
			Object v = getValue(field);
			
			if(v != null){
				
				if(v instanceof Number){
					return ((Number) v).intValue();
				}
				
				if(v instanceof String){
					return Integer.valueOf((String) v);
				}
				
				return defaultValue;
			}
			
			return defaultValue;
		}
		
		public double doubleValue(String field, long defaultValue){
			
			Object v = getValue(field);
			
			if(v != null){
				
				if(v instanceof Number){
					return ((Number) v).doubleValue();
				}
				
				if(v instanceof String){
					return Double.valueOf((String) v);
				}
				
				return defaultValue;
			}
			
			return defaultValue;
			
		}
		
		public String stringValue(String field, String defaultValue){
			
			Object v = getValue(field);
			
			if(v != null){
				
				if(v instanceof String){
					return (String) v;
				}
				
				return v.toString();
			}
			
			return defaultValue;
			
		}
		
		
	}
}
