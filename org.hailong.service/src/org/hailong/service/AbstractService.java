package org.hailong.service;

public abstract class AbstractService<ST extends IServiceContext> implements IService<ST> {

	private ST _context;
	private Object _config;
	
	public AbstractService(){

	}
	
	public void didReceiveMemoryWarning() {
	}

	public ServiceState getServiceState() {
		return ServiceState.Running;
	}

	
	public ST getContext(){
		return _context;
	}
	
	public void setContext(ST context){
		_context = context;
	}
	
	public Object getConfig(){
		return _config;
	}
	
	public void setConfig(Object config){
		_config = config;
	}
	
	public void destroy(){
		_context = null;
		_config = null;
	}

}
