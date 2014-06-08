package org.hailong.service;


/**
 * 
 * @author hailongzhang
 *
 */
public interface IService<ST extends IServiceContext> {

	public ST getContext();
	
	public void setContext(ST context);
	
	public <T extends ITask> boolean handle(Class<T> taskType,T task,int priority) throws Exception;
	
	public <T extends ITask> boolean cancelHandle(Class<T> taskType,T task) throws Exception;
	
	public boolean cancelHandleForSource(Object source) throws Exception;
	
	public void didReceiveMemoryWarning();
	
	public ServiceState getServiceState();
	
	public Object getConfig();
	
	public void setConfig(Object config);
	
	public void destroy();
	
}
