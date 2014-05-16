package org.hailong.service.tasks;

import java.io.File;

import org.hailong.service.ITask;


public interface ILocalResourceTask extends ITask{

	public String getResourceUri();

	public Object setResourceLocalFile(File localUri);
	
	public void setResourceObject(Object obj);
}
