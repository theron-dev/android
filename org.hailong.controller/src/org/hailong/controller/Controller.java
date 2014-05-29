package org.hailong.controller;

import org.hailong.core.URL;
import org.hailong.service.IServiceContext;
import org.hailong.service.tasks.IImageTask;
import org.hailong.service.tasks.ILocalResourceTask;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
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
	private IControllerContext<T> _controllerContext;
	private Handler _handler;

	public Controller(){
		_handler = new Handler();
	}
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return _viewLayout != null ? _viewLayout.getView(inflater, container) : null;  
    }  
	
	@SuppressWarnings("unchecked")
	public IControllerContext<T> getControllerContext(){
		
		if(_controllerContext != null){
			return  _controllerContext;
		}

		Activity activity = getActivity();
		
		if(activity != null && activity instanceof IControllerContext ){
			return ((IControllerContext<T>) activity);
		}
		
		return null;
	}
	
	public void setControllerContext(IControllerContext<T> controllerContext){
		_controllerContext = controllerContext;
	}

	@Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  

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
	
	public boolean onPressBack(){
		
		Controller<T> controller = getParentController();
		
		if(controller != null){
			
			return controller.onPressBack();
		}
		
		return false;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
	    if (keyCode == KeyEvent.KEYCODE_BACK 
	    		&& event.getRepeatCount() == 0 ) {
	    	
	    	if(onPressBack()){
	    		_handler.post(new Runnable(){

					public void run() {
						openURL(new URL(".", getURL()),true);
					}});
	    		
	    		return true;
	    	}
	    	return false;
	    }

	    return false;
	}
	
	public Handler getHandler(){
		return _handler;
	}
	
	public void downloadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isNeedDownload() && !imageTask.isLoading()){
				
				try {
					getServiceContext().handle(IImageTask.class, imageTask, 0);
				} catch (Exception e) {
					Log.d(C.TAG, Log.getStackTraceString(e));
				}
				
			}
		}
		
		if(view instanceof ViewGroup){
			
			ViewGroup viewGroup = (ViewGroup) view;
			
			int c = viewGroup.getChildCount();
			
			for(int i=0;i<c;i++){
				
				downloadImagesForView(viewGroup.getChildAt(i));
				
			}
			
		}
		
	}

	public void loadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isNeedDownload() && !imageTask.isLoading()){
				
				try {
					
					getServiceContext().handle(ILocalResourceTask.class, imageTask, 0);
					
				} catch (Exception e) {
					Log.d(C.TAG, Log.getStackTraceString(e));
				}
				
			}
		}

		if(view instanceof ViewGroup){
			
			ViewGroup viewGroup = (ViewGroup) view;
			
			int c = viewGroup.getChildCount();
			
			for(int i=0;i<c;i++){
				
				loadImagesForView(viewGroup.getChildAt(i));
				
			}
			
		}
	}

	public void cancelDownloadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isLoading()){
				
				try {
					getServiceContext().cancelHandle(IImageTask.class, imageTask);
				} catch (Exception e) {
					Log.d(C.TAG, Log.getStackTraceString(e));
				}
				
			}
		}

		if(view instanceof ViewGroup){
			
			ViewGroup viewGroup = (ViewGroup) view;
			
			int c = viewGroup.getChildCount();
			
			for(int i=0;i<c;i++){
				
				cancelDownloadImagesForView(viewGroup.getChildAt(i));
				
			}
			
		}
	}
	
}
