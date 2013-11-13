package org.hailong.framework.controllers;

import org.hailong.framework.IActivity;
import org.hailong.framework.IServiceContext;

public interface IViewControllerContext<T extends IServiceContext> extends IActivity<T> {

	public ViewController<T> getInstance(String alias,IViewControllerContext<T> controllerContext);
	
	public ViewController<T> getInstance(String alias);
	
	public IViewControllerContext<T> getRootContext();
	
	public IViewControllerContext<T> getParentContext();
	
	public boolean openUrl(String uri,boolean animated);
	
	public Object getValue(String key);
	
	public void setValue(String key,Object value);
	
	public String setValue(Object value);
	
	public void setResult(Object result);
	
	public Object getResult();
	
	public ViewController<T> getFocusViewController();
	
	public void onFocusViewControllerChanged();
	
}
