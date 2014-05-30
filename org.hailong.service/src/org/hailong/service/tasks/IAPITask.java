package org.hailong.service.tasks;

import org.hailong.service.ITask;

public interface IAPITask extends ITask {

	public ITask getTask();
	
	public void setTask(ITask task);
	
	public Class<?> getTaskType();
	
	public void setTaskType(Class<?> taskType);
	
	public Object getObject();
	
	public void setObject(Object object);
	
}
