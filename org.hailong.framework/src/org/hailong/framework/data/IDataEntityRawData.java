package org.hailong.framework.data;

import org.hailong.framework.data.annotation.DataField;

public interface IDataEntityRawData {
	
	public long getRawId();
	
	public boolean hasChange();
	
	public boolean isDeleted();
	
	public Object getValue(DataField field);
	
	public void setValue(DataField field,Object value);
	
	public int retainCount();
	
	public void retain();
	
	public void release();
	
}
