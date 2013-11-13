package org.hailong.framework;

import android.os.Handler;
import android.os.Message;

public abstract class ServiceContextHandler<T extends IServiceContext> extends Handler {
	
	public final static int WHAT_ON_CONNECTED = 1;
	public final static int WHAT_ON_DISCONNECTED = 2;
	
	abstract public void onConnected(T serviceContext);
	abstract public void onDisconnected(T serviceContext);
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message message){
		if(message.what == WHAT_ON_CONNECTED){
			onConnected((T)message.obj);
		}
		else if(message.what == WHAT_ON_DISCONNECTED){
			onDisconnected((T)message.obj);
		}
	}
}
