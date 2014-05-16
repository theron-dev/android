package org.hailong.service.tasks;

import java.io.File;

import org.hailong.service.ITask;

public interface IResourceTask extends ITask {

	public String getResourceUri();

	public boolean isNeedDownload();

	public boolean isForceDownload();
	
	public boolean isLoading();
	
	public void setLoading(boolean loading);

	public Object setResourceLocalFile(File localUri);
	
	public void setResourceObject(Object obj);

	public void onException(Exception ex);
	
}