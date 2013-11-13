package org.hailong.framework.tasks.impl;

import org.apache.http.client.methods.HttpUriRequest;
import org.hailong.framework.tasks.IHttpRequestTask;

import android.os.Handler;
import android.os.Message;

public abstract class AbstractHttpRequestTask<T> extends Handler implements IHttpRequestTask<T>{
	
	private final static int WHAT_FINISH = 1;
	private final static int WHAT_ERROR = 2;
	
	
	protected HttpUriRequest httpRequest;
	private Object waiter;
	
	public AbstractHttpRequestTask(HttpUriRequest httpRequest){
		this.httpRequest = httpRequest;
	}
	
	public HttpUriRequest getHttpRequest() {
		return httpRequest;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message message){
		if(message.what == WHAT_FINISH){
			onFinish((T)message.obj);
		}
		else if(message.what == WHAT_ERROR){
			onError((Exception)message.obj);
		}
		synchronized (waiter) {
			waiter.notifyAll();
		}
		waiter = null;
	}
	
	public abstract void onFinish(T result);
	
	public abstract void onError(Exception ex);
	

	public void sendFinishMessage(T result,Object waiter){
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

}
