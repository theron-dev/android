package org.hailong.service.tasks;

public interface IUplinkTaskListener {

	public void onDidUplinkTaskLoaded(Class<?> taskType,IUplinkTask uplinkTask,Object resultsData);

	public void onDidUnlinkTaskException(Class<?> taskType,IUplinkTask uplinkTask,Exception exception);
	
}
