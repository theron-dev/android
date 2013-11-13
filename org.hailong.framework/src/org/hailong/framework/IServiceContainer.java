package org.hailong.framework;



public interface IServiceContainer {

	public <T extends ITask> void addTaskType(Class<T> taskType);
	
	public <T extends ITask> void removeTaskType(Class<T> taskType);
	
	public boolean isInstance();
	
	public boolean isAllowDeallocInstance();
	
	public void setAllowDeallocInstance(boolean allow);
	
	public void createInstance() throws Exception;
	
	public void setConfig(Object config);
	
	public void destroy();
}
