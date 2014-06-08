package org.hailong.db;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.hailong.core.JSON;
import org.hailong.db.annotation.DBEntity;
import org.hailong.db.annotation.DBField;
import org.hailong.db.annotation.DBFieldType;
import org.json.JSONException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBContext {

	private Map<DBEntity,Constructor<DBObject>> _entityConstructors;
	private Map<DBEntity,Map<String,DBObjectValues>> _dataObjects;
	private final SQLiteDatabase _database;
	private Stack<Set<DBObject>> _updateObjects;
	private List<OnObjectSetChangedListener> _objectSetChangedListeners;
	
	public DBContext(SQLiteDatabase database){
		_database = database;
	}
	
	public SQLiteDatabase getDatabase(){
		return _database;
	}
	
	public String[] dataKeys(DBEntity dbEntity, String selection, String[] selectionArgs,String groupBy,String having,String orderBy,String limit){
		
		Cursor cursor = _database.query(dbEntity.value(), new String[]{dbEntity.dataKey()}, selection, selectionArgs, groupBy, having, orderBy,limit);
		
		List<String> dataKeys = new ArrayList<String>(4);
		
		if(cursor.moveToFirst()){
			do {
				
				String dataKey= cursor.getString(0);
				
				if(dataKey != null){
					dataKeys.add(dataKey);
				}
				
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		
		return dataKeys.toArray(new String[dataKeys.size()]);
	}
	
	public DBObject dataObject(DBEntity dbEntity, String dataKey){
		
		DBObjectValues objectValues = null;
		Map<String,DBObjectValues> objects = null;
		
		if(_dataObjects != null){
			
			objects = _dataObjects.get(dbEntity);
			
			if(objects != null){
				
				objectValues = objects.get(dataKey);
				
			}
			
		}
		
		if(objectValues == null){
			
			DBObject object = null;
			
			List<String> columns = new ArrayList<String>(4);
			
			columns.add("rowid");
			
			Map<String,DBField> fields = new HashMap<String,DBField>(4);
			
			for(DBField field : dbEntity.fields()){
				columns.add(field.value());
				fields.put(field.value(), field);
			}
			
			Cursor cursor = _database.query(dbEntity.value(), columns.toArray(new String[columns.size()])
					, dbEntity.dataKey().concat("=?"), new String[]{dataKey}, null, null, null, null);
	
			if(cursor.moveToFirst()){
			
				Map<String,Object> values = new HashMap<String,Object>(4);
			
				int count = cursor.getColumnCount();
				
				long rowid = cursor.getLong(0);
				
				for(int i=1;i<count;i++){
					
					String name = cursor.getColumnName(i);
					
					Object v = null;
					
					DBField field = fields.get(name);
					
					if(field != null){
						DBFieldType type = field.type();
						if(type == DBFieldType.INT){
							v = cursor.getInt(i);
						}
						else if(type == DBFieldType.BIGINT){
							v = cursor.getLong(i);
						}
						else if(type == DBFieldType.VARCHAR){
							v = cursor.getString(i);
						}
						else if(type == DBFieldType.DOUBLE){
							v = cursor.getDouble(i);
						}
						else if(type == DBFieldType.TEXT){
							v = cursor.getString(i);
						}
						else if(type == DBFieldType.BYTES){
							v = cursor.getBlob(i);
						}
						else if(type == DBFieldType.OBJECT){
							try {
								v = JSON.decodeString(cursor.getString(i));
							} catch (JSONException e) {
								Log.e(DB.TAG, Log.getStackTraceString(e));
							}
						}
						else {
							v = cursor.getString(i);
						}
						
					}
					else if("rowid".equals(name)){
						v = cursor.getLong(i);
					}
					else{
						v = cursor.getString(i);
					}
					
					if(v != null){
						values.put(name, v);
					}
				}
				
				objectValues = new DBObjectValues(dbEntity,dataKey,rowid,values);
				
				if(objects == null){
					objects = new HashMap<String,DBObjectValues>(4);
					if(_dataObjects == null){
						_dataObjects = new HashMap<DBEntity,Map<String,DBObjectValues>>(4);
					}
					_dataObjects.put(dbEntity, objects);
				}
				
				objects.put(dataKey, objectValues);
				
				object = newObject(dbEntity,objectValues);
				
				objectValues.release();
			}
			
			cursor.close();
			
			return object;
			
		}
		else {
			return newObject(dbEntity,objectValues);
		}
		
	}

	private DBObject newObject(DBEntity dbEntity,IDBObjectValues objectValues){
	
		if(_entityConstructors != null){
			Constructor<DBObject> constructor = _entityConstructors.get(dbEntity);
			if(constructor != null){
				try {
					DBObject object = constructor.newInstance();
					object.setObjectValues(objectValues);
					return object;
				} catch (Exception e) {
					Log.e(DB.TAG, Log.getStackTraceString(e));
				} 
			}
		}
		
		return null;
	}
	
	public DBObject dataObjectForCache(DBEntity dbEntity, String dataKey){
		
		if(_dataObjects != null){
			
			Map<String,DBObjectValues> objects = _dataObjects.get(dbEntity);
			
			if(objects != null){
				
				DBObjectValues objectValues = objects.get(dataKey);
				
				if(objectValues != null){
					return newObject(dbEntity,objectValues);
				}
	
			}
			
		}
		
		return null;
	}
	
	public void insertObject(DBObject object){
		
		Class<?> clazz = object.getClass();
		DBEntity dbEntity = clazz.getAnnotation(DBEntity.class);
		
		StringBuilder sb = new StringBuilder();
		StringBuilder values = new StringBuilder();
		
		sb.append("INSERT INTO [").append(dbEntity.value()).append("] (");
		
		boolean isFirst = true;
		for(DBField field : dbEntity.fields()){
			if(isFirst){
				isFirst = false;
			}
			else {
				sb.append(",");
				values.append(",");
			}
			sb.append("[").append(field.value()).append("]");
			values.append("?");
		}
		
		sb.append(") VALUES(").append(values.toString()).append(")");
		
		SQLiteStatement st = _database.compileStatement(sb.toString());
		
		int index = 1;
		
		for(DBField field : dbEntity.fields()){
			
			DBFieldType type = field.type();
			
			if(type == DBFieldType.INT){
				st.bindLong(index, object.longValue(field, 0));
			}
			else if(type == DBFieldType.BIGINT){
				st.bindLong(index, object.longValue(field, 0));
			}
			else if(type == DBFieldType.VARCHAR){
				st.bindString(index, object.stringValue(field, null));
			}
			else if(type == DBFieldType.TEXT){
				st.bindString(index, object.stringValue(field, null));
			}
			else if(type == DBFieldType.DOUBLE){
				st.bindDouble(index, object.doubleValue(field, 0.0));
			}
			else if(type == DBFieldType.BYTES){
				st.bindBlob(index, object.bytesValue(field, null));
			}
			else if(type == DBFieldType.OBJECT){
				try {
					st.bindString(index, JSON.encodeObject( object.getValue(field) ));
				} catch (JSONException e) {
					Log.e(DB.TAG, Log.getStackTraceString(e));
				}
			}
			else {
				st.bindNull(index);
			}
			
			index ++;
		}
		
		long rowid = st.executeInsert();
		
		String key = dbEntity.dataKey();
		String dataKey ;
		
		if("rowid".equals(key)){
			dataKey = String.valueOf(rowid);
		}
		else{
			dataKey = object.stringValue(key, null);
		}
		
		if(dataKey != null && rowid != 0){
			
			DBObjectValues objectValues = new DBObjectValues(dbEntity,dataKey,rowid,object.values());
			object.setObjectValues(objectValues);
			objectValues.release();
		
		}
	
	}
	
	public void delete(DBEntity dbEntity,String dataKey){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("DELETE FROM [").append(dbEntity.value()).append("] WHERE [").append(dbEntity.dataKey()).append("]=?");
		
		SQLiteStatement st = _database.compileStatement(sb.toString());
		
		st.bindString(0, dataKey);
		
		st.execute();
		
		if(_dataObjects != null){
			
			Map<String,DBObjectValues> objects = _dataObjects.get(dbEntity);
			
			if(objects != null){
				
				DBObjectValues objectValues = objects.get(dataKey);
				
				if(objectValues != null){
					objectValues.setDeleted(true);
					objects.remove(dataKey);
				}
	
			}
			
		}
	}
	
	public void deleteObject(DBObject object) {
		delete(object.getClass().getAnnotation(DBEntity.class),object.dataKey());
	}
	
	public void updateObject(DBObject object) {
		
		Class<?> clazz = object.getClass();
		DBEntity dbEntity = clazz.getAnnotation(DBEntity.class);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("UPDATE [").append(dbEntity.value()).append("] SET ");
		
		boolean isFirst = true;
		for(DBField field : dbEntity.fields()){
			if(isFirst){
				isFirst = false;
			}
			else {
				sb.append(",");
			}
			sb.append("[").append(field.value()).append("]=?");
		}
		
		sb.append(" WHERE [rowid]=?");
		
		SQLiteStatement st = _database.compileStatement(sb.toString());
		
		int index = 1;
		
		for(DBField field : dbEntity.fields()){
			
			DBFieldType type = field.type();
			
			if(type == DBFieldType.INT){
				st.bindLong(index, object.longValue(field, 0));
			}
			else if(type == DBFieldType.BIGINT){
				st.bindLong(index, object.longValue(field, 0));
			}
			else if(type == DBFieldType.VARCHAR){
				st.bindString(index, object.stringValue(field, null));
			}
			else if(type == DBFieldType.TEXT){
				st.bindString(index, object.stringValue(field, null));
			}
			else if(type == DBFieldType.DOUBLE){
				st.bindDouble(index, object.doubleValue(field, 0.0));
			}
			else if(type == DBFieldType.BYTES){
				st.bindBlob(index, object.bytesValue(field, null));
			}
			else if(type == DBFieldType.OBJECT){
				try {
					st.bindString(index, JSON.encodeObject( object.getValue(field) ));
				} catch (JSONException e) {
					Log.e(DB.TAG, Log.getStackTraceString(e));
				}
			}
			else {
				st.bindNull(index);
			}
			
			index ++;
		}
		
		st.bindLong(index, object.rowid());
		
		st.execute();
	
	}

	public void beginUpdate(){
		if(_updateObjects == null){
			_updateObjects = new Stack<Set<DBObject>>();
		}
		_updateObjects.add(new HashSet<DBObject>(4));
	}
	
	public void endUpdate(){
		if(_updateObjects != null){
			Set<DBObject> objects = _updateObjects.pop();
			if(objects != null && objects.size() >0 && _objectSetChangedListeners != null && _objectSetChangedListeners.size() >0){
				for(OnObjectSetChangedListener listener : _objectSetChangedListeners.toArray(new OnObjectSetChangedListener[_objectSetChangedListeners.size()])){
					listener.onObjectSetChanged(this, objects);
				}
			}
		}
	}
	
	public void addObjectSetChangedListener(OnObjectSetChangedListener objectSetChangedListener){
		if(_objectSetChangedListeners == null){
			_objectSetChangedListeners = new ArrayList<OnObjectSetChangedListener>(4);
		}
		_objectSetChangedListeners.add(objectSetChangedListener);
	}
	
	public void removeObjectSetChangedListener(OnObjectSetChangedListener objectSetChangedListener){
		if(_objectSetChangedListeners != null){
			_objectSetChangedListeners.remove(objectSetChangedListener);
		}
	}
	
	public boolean registerObjectClass(Class<?> objectClass){
		DBEntity dbEntity;
		
		if(DBObject.class.isAssignableFrom(objectClass) && (dbEntity = objectClass.getAnnotation(DBEntity.class)) != null){
			
			boolean isExists = false;
			
			Cursor cursor = _database.query("sqlite_master", new String[]{"[sql]"}, "[type]='table' and [name]='"+ dbEntity.value()+"'", null, null, null, null);
			
			if(cursor!= null){
				if(cursor.moveToNext()){
					isExists = true;
					String sql = cursor.getString(0);
					
					if(sql != null){
						DBField[] fields = dbEntity.fields();
						StringBuffer sb = new StringBuffer();
						
						if(fields !=null){
							for(DBField dataField : fields){
								if(!sql.contains("["+dataField.value()+"]")){
									sb.delete(0, sb.length());
									sb.append("ALTER TABLE [").append(dbEntity.value())
										.append("] ADD COLUMN [").append(dataField.value()).append("] ");
									DBFieldType type = dataField.type();
									int length = dataField.length();
									if(type == DBFieldType.VARCHAR){
										if(length == 0){
											length = 45;
										}
										sb.append(" VARCHAR(").append(length).append(")");
									}
									else if(type == DBFieldType.TEXT || type == DBFieldType.OBJECT ){
										sb.append(" TEXT");
									}
									else if(type == DBFieldType.INT){
										if(length == 0){
											sb.append(" INT");
										}
										else{
											sb.append(" INT(").append(length).append(")");
										}
									}
									else if(type == DBFieldType.BIGINT){
										if(length == 0){
											sb.append(" BIGINT");
										}
										else{
											sb.append(" BIGINT(").append(length).append(")");
										}
									}
									else if(type == DBFieldType.DOUBLE){
										if(length == 0){
											sb.append(" DOUBLE");
										}
										else{
											sb.append(" DOUBLE(").append(length).append(")");
										}
									}
									else if(type == DBFieldType.BYTES){
										if(length == 0){
											sb.append(" BLOB");
										}
										else{
											sb.append(" BLOB(").append(length).append(")");
										}
									}
									_database.execSQL(sb.toString());
								}
							}
						}
					}
				}
				cursor.close();
			}
			
			if(!isExists){
			
				StringBuffer sb=  new StringBuffer();
				
				sb.append("CREATE TABLE IF NOT EXISTS [").append(dbEntity.value()).append("] (");
				
			
				sb.append("[rawid] integer NOT NULL PRIMARY KEY AUTOINCREMENT");
				
				DBField[] fields = dbEntity.fields();
				
				if(fields !=null){
					for(DBField dataField : fields){
						sb.append(",[").append(dataField.value()).append("]");
						DBFieldType type = dataField.type();
						int length = dataField.length();
						if(type == DBFieldType.VARCHAR){
							if(length == 0){
								length = 45;
							}
							sb.append(" VARCHAR(").append(length).append(")");
						}
						else if(type == DBFieldType.TEXT || type == DBFieldType.OBJECT) {
							sb.append(" TEXT");
						}
						else if(type == DBFieldType.INT){
							if(length == 0){
								sb.append(" INT");
							}
							else{
								sb.append(" INT(").append(length).append(")");
							}
						}
						else if(type == DBFieldType.BIGINT){
							if(length == 0){
								sb.append(" BIGINT");
							}
							else{
								sb.append(" BIGINT(").append(length).append(")");
							}
						}
						else if(type == DBFieldType.DOUBLE){
							if(length == 0){
								sb.append(" DOUBLE");
							}
							else{
								sb.append(" DOUBLE(").append(length).append(")");
							}
						}
						else if(type == DBFieldType.BYTES){
							if(length == 0){
								sb.append(" BLOB");
							}
							else{
								sb.append(" BLOB(").append(length).append(")");
							}
						}
					}
				}
				
				sb.append(") ;");
				
				_database.execSQL(sb.toString());
			}
			
			if(_entityConstructors == null){
				_entityConstructors = new HashMap<DBEntity,Constructor<DBObject>>(4);
			}
			
			try {
				@SuppressWarnings("unchecked")
				Constructor<DBObject> constructor = (Constructor<DBObject>) objectClass.getConstructor();
				_entityConstructors.put(dbEntity, constructor);
			} catch (NoSuchMethodException e) {
				Log.e(DB.TAG, Log.getStackTraceString(e));
			}
			
		}
		
		return false;
	}
	
	public static interface OnObjectSetChangedListener {
		
		public void onObjectSetChanged(DBContext dbContext,Set<DBObject> objects);
		
	}
	
	private class DBObjectValues implements  IDBObjectValues{

		private long _rowid;
		private DBEntity _dbEntity;
		private String _dataKey;
		private Map<String,Object> _values;
		private int _retainCount;
		private boolean _deleted;
		
		public DBObjectValues(DBEntity dbEntity,String dataKey,long rowid,Map<String,Object> values){
			_dbEntity = dbEntity;
			_dataKey = dataKey;
			_values = values;
			_retainCount = 1;
			_rowid = rowid;
		}
		
		@Override
		public long rowid() {
			return _rowid;
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
			if(_values == null){
				_values = new HashMap<String,Object>(4);
			}
			_values.put(field.value(), value);
		}

		@Override
		public int retainCount() {
			return _retainCount;
		}

		@Override
		public IDBObjectValues retain() {
			_retainCount ++;
			return this;
		}

		@Override
		public void release() {
			if( -- _retainCount == 0){
				if(rowid() != 0 && ! isDeleted() && _dataObjects != null &&  _dataKey != null){
					Map<String,DBObjectValues> objects = _dataObjects.get(_dbEntity);
					if(objects != null){
						objects.remove(_dataKey);
					}
				}
			}
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
		
		public double doubleValue(String field, double defaultValue){
			
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

		@Override
		public byte[] bytesValue(String field,byte[] defaultValue) {
			
			Object v = getValue(field);
			
			if(v != null){
				
				if(v instanceof byte[]){
					return (byte[]) v;
				}
				
			}
			
			return defaultValue;
		}
		
		
	}
}
