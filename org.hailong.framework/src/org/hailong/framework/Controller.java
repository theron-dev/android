package org.hailong.framework;

import android.app.Activity;
import android.content.Context;

public abstract class Controller<T extends IServiceContext> {

	protected IActivity<T> activity;

	public Controller(IActivity<T> activity){
		this.activity = activity;
	}
	
	public T getServiceContext() {
		return activity.getServiceContext();
	}

	public Context getContext() {
		if(activity instanceof Context){
			return (Context)activity;
		}
		return null;
	}
	
	public Activity getActivity() {
		if(activity instanceof Activity){
			return (Activity)activity;
		}
		return null;
	}

	public void onLowMemory(){
		
	}
	
	abstract public void onServiceContextStart() ;

	abstract public void onServiceContextStop() ;
	
	public void destroy(){
		activity = null;
	}


}
