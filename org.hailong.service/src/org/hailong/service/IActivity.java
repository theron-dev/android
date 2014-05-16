package org.hailong.service;

public interface IActivity<T extends IServiceContext> {
	
	public T getServiceContext();
	
	public boolean isBindServiceContext();
	
	public void addServiceContextListener(ServiceContextHandler<T> listener);
	
	public void removeServiceContextListener(ServiceContextHandler<T> listener);
	
}
