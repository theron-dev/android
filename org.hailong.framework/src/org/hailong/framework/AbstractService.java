package org.hailong.framework;

public abstract class AbstractService implements IService {

	protected TaskQueue taskQueue;
	private IServiceContext context;
	private Object config;
	
	public AbstractService(){
		taskQueue = new TaskQueue();
	}
	

	public void didReceiveMemoryWarning() {
	}

	public ServiceState getServiceState() {
		return ServiceState.Running;
	}

	public int getRunningTaskCount() {
		return taskQueue.size();
	}
	
	public IServiceContext getContext(){
		return context;
	}
	
	public void setContext(IServiceContext context){
		this.context = context;
	}
	
	public Object getConfig(){
		return config;
	}
	
	public void setConfig(Object config){
		this.config = config;
	}
	
	public void destroy(){
		taskQueue = null;
		context = null;
		config = null;
	}

}
