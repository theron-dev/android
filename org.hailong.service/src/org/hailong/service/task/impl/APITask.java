package org.hailong.service.task.impl;

import org.hailong.service.ITask;
import org.hailong.service.tasks.IAPICancelTask;
import org.hailong.service.tasks.IAPITask;

public class APITask extends Task implements IAPITask, IAPICancelTask {

	private ITask _task;
	private Class<?> _taskType;
	private Object _object;
	
	@Override
	public ITask getTask() {
		return _task;
	}

	@Override
	public void setTask(ITask task) {
		_task = task;
	}

	@Override
	public Class<?> getTaskType() {
		return _taskType;
	}

	@Override
	public void setTaskType(Class<?> taskType) {
		_taskType = taskType;
	}

	@Override
	public Object getObject() {
		return _object;
	}

	@Override
	public void setObject(Object object) {
		_object = object;
	}

	
}
