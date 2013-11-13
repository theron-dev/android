package org.hailong.framework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public abstract class AbstractActivity<T extends IServiceContext> extends android.app.Activity implements IActivity<T>{
	
	private T _serviceContext;
	private boolean _isBindServiceContext;
	private IActivity<T> _bindActivity;
	private boolean _isStarted = false;
	private boolean _isServiceContextStarted = false;
	
	private ServiceContextHandler<T> _serviceContextHandler = null;
	
	private ServiceContextConnection<T> _serviceContextConnection = null;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		 _serviceContextHandler = new ServiceContextHandler<T>(){

				@Override
				public void onConnected(T serviceContext) {
					_serviceContext = serviceContext;
					onServiceContextConnected();
				}

				@Override
				public void onDisconnected(T serviceContext) {
					onServiceContextDisconnected();
					_serviceContext = null;
				}
				
			};
			
		_serviceContextConnection = new ServiceContextConnection<T>(){

				@Override
				public void onConnected(){
					_serviceContext = getServiceContext();
					onServiceContextConnected();
				}
				
				@Override
				public void onDisconnected(){
					onServiceContextDisconnected();
					_serviceContext = null;
				}
				
			};
			
		if(!bindService(getServiceContextIntent(),_serviceContextConnection,Context.BIND_AUTO_CREATE)){
			_isBindServiceContext = false;
			Activity activity = getParent();
			while(activity !=null){
				if(activity instanceof IActivity){
					if(((IActivity<T>)activity).isBindServiceContext()){
						_bindActivity = (IActivity<T>)activity;
						_bindActivity.addServiceContextListener(_serviceContextHandler);
						break;
					}
				}
				activity = activity.getParent();
			}
		}
		else{
			_isBindServiceContext = true;
		}
	}

	@Override
	public void onDestroy(){
		if(_isBindServiceContext){
			unbindService(_serviceContextConnection);
		}
		if(_bindActivity != null){
			_bindActivity.removeServiceContextListener(_serviceContextHandler);
		}
		super.onDestroy();
	}

	
	@Override
	protected void onStart(){
		super.onStart();
		
		_isStarted = true;
		if(!_isServiceContextStarted && getServiceContext() !=null){
			_isServiceContextStarted = true;
			onServiceContextStart();
		}
	}
	
	@Override
	protected void onStop(){
		_isStarted =false;
		
		if(_isServiceContextStarted){
			_isServiceContextStarted = false;
			onServiceContextStop();
		}
		super.onStop();
	}
	

	public T getServiceContext(){
		return _serviceContext;
	}
	

	
	protected void onServiceContextStart(){
		
	}
	
	protected void onServiceContextStop(){
		
	}
	
	protected void onServiceContextConnected(){
		if(_isStarted){
			_isServiceContextStarted = true;
			onServiceContextStart();
		}
	}
	
	protected void onServiceContextDisconnected(){
		if(_isServiceContextStarted){
			_isServiceContextStarted = false;
			onServiceContextStop();
		}
	}
	
	public boolean isBindServiceContext(){
		return _isBindServiceContext;
	}
	
	public void addServiceContextListener(ServiceContextHandler<T> listener){
		_serviceContextConnection.addServiceContextListener(listener);
	}
	

	public void removeServiceContextListener(ServiceContextHandler<T> listener){
		_serviceContextConnection.removeServiceContextListener(listener);
	}
	
	abstract protected Intent getServiceContextIntent();
}
