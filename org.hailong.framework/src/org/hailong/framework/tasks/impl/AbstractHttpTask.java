package org.hailong.framework.tasks.impl;

import org.apache.http.client.methods.HttpUriRequest;
import org.hailong.framework.tasks.IHttpAPITask;
import org.hailong.framework.tasks.IHttpResourceTask;
import org.hailong.framework.tasks.IHttpTask;

import android.os.Handler;
import android.os.Message;

public abstract class AbstractHttpTask<T> extends Handler implements IHttpTask<T>,IHttpResourceTask<T>,IHttpAPITask<T>{
	
	private final static int WHAT_FINISH = 1;
	private final static int WHAT_ERROR = 2;
	
	
	protected HttpUriRequest httpRequest;
	private Object waiter;
	private boolean canceled;
	
	public AbstractHttpTask(HttpUriRequest httpRequest){
		this.httpRequest = httpRequest;
	}
	
	public HttpUriRequest getHttpRequest() {
		return httpRequest;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message message){
		
		if(!canceled){
			if(message.what == WHAT_FINISH){
				onLoaded((T)message.obj);
			}
			else if(message.what == WHAT_ERROR){
				onException((Exception)message.obj);
			}
		}
		
		synchronized (waiter) {
			waiter.notifyAll();
		}
		
		waiter = null;
	}
	
	public void onBackgroundLoaded(T result){
		
	}
	
	public abstract void onLoaded(T result);
	
	public abstract void onException(Exception ex);
	

	public void sendFinishMessage(T result,Object waiter){
		
		onBackgroundLoaded(result);
		
		this.waiter = waiter;
		Message msg = new Message();
		msg.what = WHAT_FINISH;
		msg.obj = result;
		synchronized (waiter) {
			sendMessage(msg);
			try {
				waiter.wait(1000);
			} catch (InterruptedException e) {
			}
		}
	}
	

	public void sendErrorMessage(Exception ex,Object waiter){
		this.waiter = waiter;
		Message msg = new Message();
		msg.what = WHAT_ERROR;
		msg.obj = ex;
		synchronized (waiter) {
			sendMessage(msg);
			try {
				waiter.wait(1000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public boolean isCanceled(){
		return canceled;
	}
	
	public void setCanceled(boolean canceled){
		this.canceled = canceled;
	}

}
