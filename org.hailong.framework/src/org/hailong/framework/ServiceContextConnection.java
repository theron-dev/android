package org.hailong.framework;

import java.util.ArrayList;
import java.util.List;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;

public class ServiceContextConnection<T extends IServiceContext> implements ServiceConnection{
	
	private T _serviceContext;
	private List<ServiceContextHandler<T>> _serviceContextListeners;
	
	public ServiceContextConnection(){
		_serviceContext = null;
		_serviceContextListeners = new ArrayList<ServiceContextHandler<T>>();
	}
	
	@SuppressWarnings("unchecked")
	public void onServiceConnected(ComponentName name, IBinder service) {
		_serviceContext = (T)((ServiceContext.LocalBinder)service).getService();

		Message message = new Message();
		message.what = ServiceContextHandler.WHAT_ON_CONNECTED;
		message.obj = _serviceContext;
		
		onConnected();
		
		for(ServiceContextHandler<T> listener : _serviceContextListeners){
			listener.sendMessage(message);
		}
	}

	public void onServiceDisconnected(ComponentName name) {
		
		Message message = new Message();
		message.what = ServiceContextHandler.WHAT_ON_DISCONNECTED;
		message.obj = _serviceContext;
		
		onDisconnected();
		
		for(ServiceContextHandler<T> listener : _serviceContextListeners){
			listener.sendMessage(message);
		}

		_serviceContext = null;
	}
	
	public void onConnected(){
		
	}
	
	public void onDisconnected(){
		
	}
	
	public T getServiceContext(){
		return _serviceContext;
	}
	
	public void addServiceContextListener(ServiceContextHandler<T> listener){
		_serviceContextListeners.add(listener);
		if(_serviceContext != null){
			Message message = new Message();
			message.what = ServiceContextHandler.WHAT_ON_CONNECTED;
			message.obj = _serviceContext;
			
			listener.sendMessage(message);
		}
	}
	
	public void removeServiceContextListener(ServiceContextHandler<T> listener){
		_serviceContextListeners.remove(listener);
	}

}
