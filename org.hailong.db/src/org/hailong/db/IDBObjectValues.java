package org.hailong.db;

import org.hailong.db.annotation.DBField;


public interface IDBObjectValues {
	
	public long getRowId();
	
	public boolean isDeleted();
	
	public Object getValue(DBField field);
	
	public Object getValue(String field);
	
	public long longValue(String field, long defaultValue);
	
	public int intValue(String field, int defaultValue);
	
	public double doubleValue(String field, long defaultValue);
	
	public String stringValue(String field, String defaultValue);
	
	public void setValue(DBField field,Object value);
	
	public int retainCount();
	
	public void retain();
	
	public void release();
	
}
