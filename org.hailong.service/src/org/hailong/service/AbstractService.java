package org.hailong.service;

public abstract class AbstractService implements IService {

	private IServiceContext context;
	private Object config;
	
	public AbstractService(){

	}
	
	public void didReceiveMemoryWarning() {
	}

	public ServiceState getServiceState() {
		return ServiceState.Running;
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
		context = null;
		config = null;
	}

}
