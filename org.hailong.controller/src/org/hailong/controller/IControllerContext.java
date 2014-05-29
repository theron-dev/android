package org.hailong.controller;


import org.hailong.core.URL;
import org.hailong.service.IServiceContext;

public interface IControllerContext <T extends IServiceContext>{

	public T getServiceContext();
	
	public Controller<T> getController(URL url,String basePath);
	
	public Object getValue(String key);
	
	public void setValue(String key,Object value);
	
	public void setResult(Object result,Object sender);
	
	public void setResultCallback(IResultCallback callback);
	
	public boolean hasResultCallback();
	
	public Controller<T> getRootController();
	
	public Object getConfig();

	public boolean isIdleTimerDisabled();
	
	public void setIdleTimerDisabled(boolean idleTimerDisabled);
}
