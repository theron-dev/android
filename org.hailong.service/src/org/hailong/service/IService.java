package org.hailong.service;


/**
 * 
 * @author hailongzhang
 *
 */
public interface IService {

	public IServiceContext getContext();
	
	public void setContext(IServiceContext context);
	
	public <T extends ITask> boolean handle(Class<T> taskType,T task,int priority) throws Exception;
	
	public <T extends ITask> boolean cancelHandle(Class<T> taskType,T task) throws Exception;
	
	public void didReceiveMemoryWarning();
	
	public ServiceState getServiceState();
	
	public Object getConfig();
	
	public void setConfig(Object config);
	
	public void destroy();
	
}
