package org.hailong.service.impl;

import org.hailong.service.AbstractService;
import org.hailong.service.IServiceContext;
import org.hailong.service.tasks.IUplinkTask;
import org.hailong.service.tasks.IUplinkTaskListener;

public abstract class UplinkService<ST extends IServiceContext> extends AbstractService<ST> {

	public void doDidUplinkTaskLoaded(Class<?> taskType,IUplinkTask uplinkTask,Object resultsData){
		IUplinkTaskListener listener = uplinkTask.getListener();
		if(listener != null){
			listener.onDidUplinkTaskLoaded(taskType, uplinkTask, resultsData);
		}
	}

	public void doDidUnlinkTaskException(Class<?> taskType,IUplinkTask uplinkTask,Exception exception){
		IUplinkTaskListener listener = uplinkTask.getListener();
		if(listener != null){
			listener.onDidUnlinkTaskException(taskType, uplinkTask, exception);
		}
	}
	

}
