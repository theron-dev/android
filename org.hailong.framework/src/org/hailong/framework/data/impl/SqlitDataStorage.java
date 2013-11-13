package org.hailong.framework.data.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.hailong.framework.Framework;
import org.hailong.framework.data.DataFetchRequest;
import org.hailong.framework.data.DataItem;
import org.hailong.framework.data.DataSort;
import org.hailong.framework.data.DataSortType;
import org.hailong.framework.data.IDataStorage;
import org.hailong.framework.data.annotation.DataEntity;
import org.hailong.framework.data.annotation.DataField;
import org.hailong.framework.data.annotation.DataFieldType;
import org.hailong.framework.data.predicate.Exp;
import org.hailong.framework.data.predicate.Field;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqlitDataStorage implements IDataStorage {

	private SQLiteDatabase _database;
	
	public SqlitDataStorage(Context context,String name){
		_database = context.openOrCreateDatabase(name, Activity.MODE_PRIVATE, null);
	}
	
	public void beginTransaction() {
		_database.beginTransaction();
	}

	public void commitTransaction() {
		if(_database.inTransaction()){
			_database.setTransactionSuccessful();
			_database.endTransaction();
		}
	}

	public void rerollTransaction() {
		_database.endTransaction();
	}

	public void register(DataEntity dataEntity) {
		
		boolean isExists = false;
		Cursor cursor = _database.query("sqlite_master", new String[]{"[sql]"}, "[type]='table' and [name]='"+ dataEntity.value()+"'", null, null, null, null);
		
		if(cursor!= null){
			if(cursor.moveToNext()){
				isExists = true;
				String sql = cursor.getString(0);
				
				if(sql != null){
					DataField[] fields = dataEntity.fields();
					StringBuffer sb = new StringBuffer();
					
					if(fields !=null){
						for(DataField dataField : fields){
							if(!sql.contains("["+dataField.value()+"]")){
								sb.delete(0, sb.length());
								sb.append("ALTER TABLE [").append(dataEntity.value())
									.append("] ADD COLUMN [").append(dataField.value()).append("] ");
								DataFieldType type = dataField.type();
								int length = dataField.length();
								if(type == DataFieldType.VARCHAR){
									if(length == 0){
										length = 45;
									}
									sb.append(" VARCHAR(").append(length).append(")");
								}
								else if(type == DataFieldType.TEXT){
									sb.append(" TEXT");
								}
								else if(type == DataFieldType.INT){
									if(length == 0){
										sb.append(" INT");
									}
									else{
										sb.append(" INT(").append(length).append(")");
									}
								}
								else if(type == DataFieldType.BIGINT){
									if(length == 0){
										sb.append(" BIGINT");
									}
									else{
										sb.append(" BIGINT(").append(length).append(")");
									}
								}
								else if(type == DataFieldType.DOUBLE){
									if(length == 0){
										sb.append(" DOUBLE");
									}
									else{
										sb.append(" DOUBLE(").append(length).append(")");
									}
								}
								else if(type == DataFieldType.OBJECT || type == DataFieldType.BYTES){
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
			
			sb.append("CREATE TABLE IF NOT EXISTS [").append(dataEntity.value()).append("] (");
			
		
			sb.append("[rawid] integer NOT NULL PRIMARY KEY AUTOINCREMENT");
			
			DataField[] fields = dataEntity.fields();
			
			if(fields !=null){
				for(DataField dataField : fields){
					sb.append(",[").append(dataField.value()).append("]");
					DataFieldType type = dataField.type();
					int length = dataField.length();
					if(type == DataFieldType.VARCHAR){
						if(length == 0){
							length = 45;
						}
						sb.append(" VARCHAR(").append(length).append(")");
					}
					else if(type == DataFieldType.TEXT){
						sb.append(" TEXT");
					}
					else if(type == DataFieldType.INT){
						if(length == 0){
							sb.append(" INT");
						}
						else{
							sb.append(" INT(").append(length).append(")");
						}
					}
					else if(type == DataFieldType.BIGINT){
						if(length == 0){
							sb.append(" BIGINT");
						}
						else{
							sb.append(" BIGINT(").append(length).append(")");
						}
					}
					else if(type == DataFieldType.DOUBLE){
						if(length == 0){
							sb.append(" DOUBLE");
						}
						else{
							sb.append(" DOUBLE(").append(length).append(")");
						}
					}
					else if(type == DataFieldType.OBJECT || type == DataFieldType.BYTES){
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
		
	}

	public void delete(DataEntity dataEntity, long rawId) {
		_database.delete(dataEntity.value(), "[rawid]=" + rawId, null);
	}

	public long insert(DataEntity dataEntity, Map<String, Object> values) {
		DataField[] fields = dataEntity.fields();
		return _database.insert(dataEntity.value(), "rawid", parseContentValues(values,fields));
	}


	public void update(DataEntity dataEntity, long rawId,
			Map<String, Object> values) {
		_database.update(dataEntity.value(), parseContentValues(values,dataEntity.fields()), "[rawid]="+rawId, null);
	}


	public Map<String,Object> get(DataEntity dataEntity, long rawId) {
		
		DataField[] fields = dataEntity.fields();
		String[] fieldNames = new String[fields.length];
		Map<String,DataField> fieldMap = new HashMap<String,DataField>(fields.length);
		
		for(int i=0;i<fields.length;i++){
			DataField dataField =fields[i];
			fieldNames[i] = dataField.value();
			fieldMap.put(dataField.value(), dataField);
		}
		
		Map<String,Object> values = new HashMap<String,Object>(4);
		
		Cursor cursor = _database.query(dataEntity.value(), fieldNames, "[rawid]="+rawId, null, null, null, null);
		
		if(cursor !=null){
			
			if(cursor.moveToNext()){
				
				for(int i=0;i<cursor.getColumnCount();i++){
					String key = cursor.getColumnName(i);
					if(fieldMap.containsKey(key)){
						DataField dataField = fieldMap.get(key);
						DataFieldType fieldType = dataField.type();
						Object value = null;
						if(fieldType == DataFieldType.VARCHAR || fieldType == DataFieldType.TEXT){
							value = cursor.getString(i);
						}
						else if(fieldType == DataFieldType.INT){
							value = cursor.getInt(i);
						}
						else if(fieldType == DataFieldType.BIGINT){
							value = cursor.getLong(i);
						}
						else if(fieldType == DataFieldType.DOUBLE){
							value = cursor.getDouble(i);
						}
						else if(fieldType == DataFieldType.BYTES){
							value = cursor.getBlob(i);
						}
						else if(fieldType == DataFieldType.OBJECT){
							byte[] bytes = cursor.getBlob(i);
							if(bytes!=null){
								try{
									ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
									ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
									value = objectInputStream.readObject();
								}
								catch(Exception ex){
									Log.d("SqlitDataStorage", Log.getStackTraceString(ex));
								}
							}
						}
						if(value != null){
							values.put(key, value); 
						}
					}
				}
			}
			cursor.close();
		}
		return values;
	}

	public <T extends DataItem> Cursor query(DataFetchRequest<T> fetchRequest) {
		DataEntity dataEntity = fetchRequest.getDataEntity();
		DataField[] fields = dataEntity.fields();
		String[] fieldNames = new String[fields.length+1];
		fieldNames[0] = "[rawid]";
		for(int i=0;i<fields.length;i++){
			DataField dataField =fields[i];
			fieldNames[i+1] = "["+dataField.value()+"]";
		}
		
		String selection = null;
		Exp exp = fetchRequest.getDataPredicate();
		if(exp != null){
			selection = exp.sql("[", "]");
		}
		
		DataSort[] sorts = fetchRequest.getDataSorts();
		String orderBy = null;
		if(sorts!=null){
			StringBuffer sb = new StringBuffer();
			for(DataSort sort : sorts){
				if(sb.length() !=0){
					sb.append(",");
				}
				Field field = new Field(sort.getField());
				sb.append(field.sql("[", "]"));
				if(sort.getSortType() == DataSortType.DESC){
					sb.append(" DESC");
				}
				else{
					sb.append(" ASC");
				}
			}
			if(sb.length() >0){
				orderBy = sb.toString();
			}
		}
		
		int offset = fetchRequest.getFetchOffset();
		int limit = fetchRequest.getFetchLimit() + offset;
		
		String limitStr = null;
		
		if(limit >0){
			limitStr = String.valueOf(limit);
		}
		
		Cursor cursor = _database.query(dataEntity.value(), fieldNames, selection, null, null,null, orderBy, limitStr);
		
		Log.i(Framework.TAG, "SELECT "+fieldNames+" FROM "+dataEntity.value()+" WHERE "+selection+" ORDER BY "+orderBy+" LIMIT "+limitStr);
		
		if(cursor != null && offset >0 ){
			cursor.move(offset);
		}
		
		return cursor;
	}

	public void destroy() {
		_database.close();
	}

	private static ContentValues parseContentValues(Map<String,Object> values,DataField[] fields){
		ContentValues contentValues = new ContentValues();
		for(DataField dataField : fields){
			String key = dataField.value();
			if(!values.containsKey(key)){
				continue;
			}
			Object value = values.get(key);
			DataFieldType fieldType = dataField.type();
			if(value !=null ){
				if(fieldType == DataFieldType.BIGINT){
					contentValues.put(key, (Long)value);
				}
				else if(fieldType == DataFieldType.INT){
					contentValues.put(key, (Integer)value);
				}
				else if(fieldType == DataFieldType.TEXT || fieldType == DataFieldType.VARCHAR){
					contentValues.put(key, (String)value);
				}
				else if(fieldType == DataFieldType.DOUBLE){
					contentValues.put(key, (Double)value);
				}
				else if(fieldType == DataFieldType.BYTES){
					contentValues.put(key, (byte[])value);
				}
				else if(fieldType == DataFieldType.OBJECT){
					ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
					ObjectOutputStream objectOutputStream;
					try {
						objectOutputStream = new ObjectOutputStream(byteOutputStream);
						objectOutputStream.writeObject(value);
						contentValues.put(key, byteOutputStream.toByteArray());
					} catch (Exception e) {
						Log.d("SqlitDataStorage",Log.getStackTraceString(e));
					}
				}
				
			}
			else{
				contentValues.putNull(key);
			}
		}
		return contentValues;
	}

}
