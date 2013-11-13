package org.hailong.framework.data;

import java.util.Map;

import org.hailong.framework.data.annotation.DataEntity;

import android.database.Cursor;

public interface IDataStorage {

	public void beginTransaction();
	
	public void commitTransaction();
	
	public void rerollTransaction();
	
	public void register(DataEntity dataEntity);
	
	public void delete(DataEntity dataEntity,long rawId);
	
	public long insert(DataEntity dataEntity,Map<String,Object> values);
	
	public void update(DataEntity dataEntity,long rawId,Map<String,Object> values);
	
	public Map<String,Object> get(DataEntity dataEntity,long rawId);
	
	public <T extends DataItem> Cursor query(DataFetchRequest<T> fetchRequest);
	
	public void destroy();
}
