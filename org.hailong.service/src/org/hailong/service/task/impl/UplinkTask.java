package org.hailong.service.task.impl;

import org.hailong.service.tasks.IUplinkTask;
import org.hailong.service.tasks.IUplinkTaskListener;

public class UplinkTask extends Task implements IUplinkTask {

	private IUplinkTaskListener _listener;
	
	@Override
	public IUplinkTaskListener getListener() {
		return _listener;
	}
	
	public void setListener(IUplinkTaskListener listener){
		_listener = listener;
	}

}
