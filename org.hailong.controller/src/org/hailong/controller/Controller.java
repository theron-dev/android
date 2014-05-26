package org.hailong.controller;

import org.hailong.core.URL;
import org.hailong.service.IServiceContext;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Controller<T extends IServiceContext> extends Fragment {
	
	private Controller<T> _parentController;
	private Object _config;
	private String _alias;
	private String _basePath;
	private URL _url;
	private String _scheme;
	private String _title;
	private ViewLayout _viewLayout;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        return _viewLayout != null ? _viewLayout.getView(inflater, container) : null;  
    }  
	
	@SuppressWarnings("unchecked")
	public IControllerContext<T> getControllerContext(){
		
		Activity activity = getActivity();
		
		if(activity != null && activity instanceof IControllerContext ){
			return ((IControllerContext<T>) activity);
		}
		
		return null;
	}
	
	public T getServiceContext(){
		IControllerContext<T> context = getControllerContext();
		if(context != null){
			return context.getServiceContext();
		}
		return null;
	}
	
	public Controller<T> getParentController(){
		return _parentController;
	}
	
	public void setParentController(Controller<T> parentController){
		_parentController = parentController;
	}
	
	public Controller<T> getTopController(){
		return this;
	}
	
	public Object getConfig(){
		return _config;
	}
	
	public void setConfig(Object config){
		_config = config;
	}
	
	public String getAlias(){
		return _alias;
	}
	
	public void setAlias(String alias){
		_alias = alias;
	}
	
	public String getBasePath(){
		return _basePath;
	}
	
	public void setBasePath(String basePath){
		_basePath = basePath;
	}
	
	public URL getURL(){
		return _url;
	}
	
	public void setURL(URL url){
		_url = url;
	}
	
	public String getScheme(){
		return _scheme;
	}
	
	public void setScheme(String scheme){
		_scheme = scheme;
	}
	
	public boolean openURL(URL url,boolean animated){
		
		return _parentController != null ? _parentController.openURL(url, animated) : false;
	}
	
	public String loadURL(URL url, String basePath, boolean animated) {
		if(basePath != null){
			if(basePath.endsWith("/")){
				return basePath + getAlias();
			}
			return basePath + "/" + getAlias();
		}
		return null;
	}
	
	public String getTitle(){
		return _title;
	}
	
	public void setTitle(String title){
		_title = title;
	}
	
	public ViewLayout getViewLayout(){
		return _viewLayout;
	}
	
	public void setViewLayout(ViewLayout viewLayout){
		_viewLayout = viewLayout;
	}
}
