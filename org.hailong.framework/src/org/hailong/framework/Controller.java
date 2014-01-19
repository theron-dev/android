package org.hailong.framework;

import android.app.Activity;
import android.content.Context;

public abstract class Controller<T extends IServiceContext> {

	protected IActivity<T> _activity;

	public Controller(IActivity<T> activity){
		_activity = activity;
	}
	
	public T getServiceContext() {
		return _activity.getServiceContext();
	}

	public Context getContext() {
		if(_activity instanceof Context){
			return (Context)_activity;
		}
		return null;
	}
	
	public Activity getActivity() {
		if(_activity instanceof Activity){
			return (Activity)_activity;
		}
		return null;
	}
	
	public boolean isBindServiceContext(){
		return _activity.isBindServiceContext();
	}

	public void onLowMemory(){
		
	}
	
	abstract public void onServiceContextStart() ;

	abstract public void onServiceContextStop() ;

}
