package org.hailong.service.task.impl;

import org.hailong.service.ITask;

public class Task implements ITask {

	private Object _source;
	
	@Override
	public Object getSource() {
		return _source;
	}

	@Override
	public void setSource(Object source) {
		_source = source;
	}

}
