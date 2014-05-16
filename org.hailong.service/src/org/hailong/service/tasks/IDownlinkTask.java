package org.hailong.service.tasks;

import org.hailong.service.ITask;


public interface IDownlinkTask extends ITask {

	public boolean isCached();
	
	public void onDidLoadedFromCached(Class<?> taskType,Object resultsData, long timestamp);
	
	public void onDidLoaded(Class<?> taskType,Object resultsData);

	public void onDidException(Class<?> taskType,Exception exception);
	
}
