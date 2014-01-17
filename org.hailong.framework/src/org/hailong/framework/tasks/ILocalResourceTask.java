package org.hailong.framework.tasks;

import java.io.File;

import org.hailong.framework.ITask;

public interface ILocalResourceTask extends ITask{

	public String getResourceUri();

	public Object setResourceLocalFile(File localUri);
	
	public void setResourceObject(Object obj);
}
