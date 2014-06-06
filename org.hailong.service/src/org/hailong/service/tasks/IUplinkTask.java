package org.hailong.service.tasks;

import org.hailong.service.ITask;

public interface IUplinkTask extends ITask {
	
	public IUplinkTaskListener getListener();
	
}
