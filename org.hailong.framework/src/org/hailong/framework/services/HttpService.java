package org.hailong.framework.services;

import org.apache.http.impl.client.DefaultHttpClient;
import org.hailong.framework.AbstractService;
import org.hailong.framework.ITask;
import org.hailong.framework.tasks.IHttpRequestTask;
import android.util.Log;

/**
 * 
 * Tasks IHttpRequestTask
 * @author hailongzhang
 *
 */
public class HttpService extends AbstractService {

	private final static String TAG = "HttpService";
	
	private Object threadLocker;
	private Thread thread;
	
	public HttpService(){
		super();
		threadLocker = new Object();
	}
	
	private void setNeedRequest(){
		if(thread == null){
			IHttpRequestTask<?> task = null;
			synchronized (taskQueue) {
				task = taskQueue.beginTaskType(IHttpRequestTask.class);
			}
			if(task != null){
				synchronized (threadLocker) {
					if(thread == null){
						thread = new Thread(new ConnectionRunnable());
						thread.start();
					}
				}
			}
		}
	}
	
	public <T extends ITask> boolean handle(
			Class<T> taskType, T task, int priority) throws Exception {
		
		if(taskType == IHttpRequestTask.class){
			
			synchronized (taskQueue) {
				taskQueue.addTask( IHttpRequestTask.class, (IHttpRequestTask<?>)task, priority);
			}
			
			this.setNeedRequest();
			
			return false;
		}
		
		return true;
	}

	public <T extends ITask> boolean cancelHandle(
			Class<T> taskType, T task) throws Exception {
		
		if(taskType == IHttpRequestTask.class){
			
			synchronized (taskQueue) {
				if(task == taskQueue.beginTaskType(IHttpRequestTask.class)){
					if(thread !=null){
						synchronized (threadLocker) {
							if(thread !=null){
								thread.interrupt();
								thread = null;
							}
						}
					}
				}
				taskQueue.removeTask( IHttpRequestTask.class, (IHttpRequestTask<?>)task);
			}
			
			this.setNeedRequest();
			
			return false;
		}
		return true;
	}

	@Override
	public void destroy(){
		if(thread !=null){
			synchronized (threadLocker) {
				if(thread !=null){
					thread.interrupt();
					thread = null;
				}
			}
		}
		super.destroy();
	}
	
	private class ConnectionRunnable implements Runnable{

		@SuppressWarnings("unchecked")
		public void run() {
			Object waiter = new Object();
			IHttpRequestTask<Object> task = null;
			DefaultHttpClient httpClient = new DefaultHttpClient();
			while(true){
				
				synchronized (taskQueue) {
					task = taskQueue.beginTaskType(IHttpRequestTask.class);
				}
				
				if(task == null){
					break;
				}
				
				try {
					
					Object result = httpClient.execute(task.getHttpRequest(),task.getResponseHandler());
					
					task.sendFinishMessage(result,waiter);
		
				}
				catch (Exception e) {
					task.sendErrorMessage(e,waiter);
					Log.d(TAG, Log.getStackTraceString(e));
				}
				finally{
					
					synchronized (taskQueue) {
						taskQueue.removeTask(IHttpRequestTask.class, task);
					}
				}
			}
			
			synchronized (threadLocker) {
				thread = null;
			}
			
			setNeedRequest();
		}
		
	}
}
