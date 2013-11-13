package org.hailong.framework.controllers;

import org.hailong.framework.IServiceContext;

public interface IViewControllerContainer<T extends IServiceContext> {
	public void setLayout(String viewLayout);
	public void setClass(String className);
	public void setToken(String token);
	public void setConfig(Object config);
	public void setTitle(String title);
	public ViewController<T> getInstance(IViewControllerContext<T> context);
	public void onLowMemory();
	public int getInstaceCount();
	public void destroy();
	public void onServiceContextStart() ;
	public void onServiceContextStop() ;
}
