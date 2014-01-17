package org.hailong.framework.controllers;

import org.hailong.framework.IActivity;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.URL;

public interface IViewControllerContext<T extends IServiceContext> extends IActivity<T> {

	public IViewController<T> getViewController(URL url,String basePath);
	
	public Object getValue(String key);
	
	public void setValue(String key,Object value);
	
	public void setResult(Object result);
	
	public void setResultCallback(IResultCallback callback);
	
	public boolean hasResultCallback();
	
	public IViewController<T> getRootViewController();
	
	public Object getConfig();

	public boolean isIdleTimerDisabled();
	
	public void setIdleTimerDisabled(boolean idleTimerDisabled);
	
}
