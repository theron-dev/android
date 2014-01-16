package org.hailong.framework.controllers;


import org.hailong.framework.IServiceContext;
import org.hailong.framework.URL;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

public interface IViewController<T extends IServiceContext> {

	public IViewControllerContext<T> getViewControllerContext();
	
	public IViewController<T> getParentController();
	
	public void setParentController(IViewController<T> parentController);
	
	public IViewController<T> getTopController();
	
	public boolean isDisplaced();
	
	public Object getConfig();
	
	public void setConfig(Object config);
	
	public String getAlias();
	
	public void setAlias(String alias);
	
	public String getBasePath();
	
	public void setBasePath(String basePath);
	
	public URL getURL();
	
	public void setURL(URL url);
	
	public String getScheme();
	
	public void setScheme(String scheme);
	
	public boolean openURL(URL url,boolean animated);
	
	public String loadURL(URL url,String basePath,boolean animated);
	
	public void onLowMemory();
	
	public String getTitle();
	
	public void setTitle(String title);
	
	public boolean onKeyDown(int keyCode, KeyEvent event);
	
	public void onOrientationChanged(int orientation);

	public View getView();
	
	public void setView(View view);
	
	public boolean isViewLoaded();
	
	public boolean isViewAppeared();
	
	public void viewWillAppear(boolean animated);
	
	public void viewDidAppear(boolean animated);
	
	public void viewWillDisappear(boolean animated);
	
	public void viewDidDisappear(boolean animated);
	
	public void viewAppearToSuperView(ViewGroup superView,boolean animated);

	public void viewRemoveForSuperView(boolean animated);
	
	public boolean isAnimation();
	
	public void setAnimation(boolean animation);
	
	public boolean onPressBack();

}
