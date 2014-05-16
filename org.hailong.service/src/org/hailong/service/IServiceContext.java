package org.hailong.service;

import java.io.File;


public interface IServiceContext {

	public <T extends ITask> boolean handle(Class<T> taskType,T task,int priority) throws Exception;
	
	public <T extends ITask> boolean cancelHandle(Class<T> taskType,T task) throws Exception;
	
	public File getDir(String name,int mode);
	
	public File getDatabasePath(String name);
	
	public Object getSystemService(String name);

	public Object getConfig();

}
