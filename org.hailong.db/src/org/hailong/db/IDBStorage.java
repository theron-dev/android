package org.hailong.db;

import java.util.Map;

import org.hailong.db.annotation.DBEntity;

import android.database.Cursor;

public interface IDBStorage {

	public void beginTransaction() throws Throwable;
	
	public void commitTransaction() throws Throwable;
	
	public void rerollTransaction() throws Throwable;
	
	public void register(DBEntity dbEntity) throws Throwable;
	
	public void delete(DBEntity dbEntity,String dataKey) throws Throwable;
	
	public long insert(DBEntity dbEntity,Map<String,Object> values) throws Throwable;
	
	public void update(DBEntity dbEntity,String dataKey,Map<String,Object> values) throws Throwable;
	
	public Map<String,Object> get(DBEntity dbEntity,String dataKey) throws Throwable;
	
	public <T extends DBObject> Cursor query(DBEntity dbEntity, String sql,Object[] args) throws Throwable;
	
	public void destroy();
	
}
