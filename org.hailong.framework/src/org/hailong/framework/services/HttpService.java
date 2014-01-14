package org.hailong.framework.services;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.DefaultHttpClient;
import org.hailong.framework.AbstractService;
import org.hailong.framework.ITask;
import org.hailong.framework.tasks.IHttpTask;
import org.hailong.framework.value.Value;

import android.util.Log;

/**
 * 
 * Tasks IHttpRequestTask
 * @author hailongzhang
 *
 */
public class HttpService extends AbstractService {

	private final static String TAG = "HttpService";

	private ThreadPoolExecutor _poolExecutor;

	public HttpService(){
		super();
	}
	
	protected ThreadPoolExecutor getPoolExecutor(){
		
		if(_poolExecutor == null){
			
			int maxThreadCount = Value.intValueForKey(getConfig(), "maxThreadCount");
			
			if(maxThreadCount < 1){
				maxThreadCount = 1;
			}
			
			long keepAlive = Value.longValueForKey(getConfig(), "keepAlive");
			
			_poolExecutor = new ThreadPoolExecutor(1, maxThreadCount, keepAlive , TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
			
		}
		
		return _poolExecutor;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ITask> boolean handle(
			Class<T> taskType, T task, int priority) throws Exception {
		
		if(IHttpTask.class.isAssignableFrom(taskType)){
			
			getPoolExecutor().execute(new ConnectionRunnable((IHttpTask<Object>)task,taskType));
			
			return false;
		}
		
		return true;
	}

	public <T extends ITask> boolean cancelHandle(
			Class<T> taskType, T task) throws Exception {
		
		if(IHttpTask.class.isAssignableFrom(taskType)){
			
			ThreadPoolExecutor pool = getPoolExecutor();
		
			Object[] runnables = pool.getQueue().toArray();
			
			for(Object runnable : runnables){
				if(runnable instanceof ConnectionRunnable){
					ConnectionRunnable run = (ConnectionRunnable) runnable;
					if(run.taskType == taskType && (task == null || task == run.httpTask)){
						run.httpTask.setCanceled(true);
						pool.remove(run);
					}
				}
			}
			
			return false;
		}
		return true;
	}

	@Override
	public void destroy(){
		super.destroy();
	}
	
	private class ConnectionRunnable implements Runnable{

		public Class<?> taskType;
		public IHttpTask<Object> httpTask;
		
		public ConnectionRunnable(IHttpTask<Object> httpTask,Class<?> taskType){
			this.httpTask = httpTask;
			this.taskType = taskType;
			httpTask.setCanceled(false);
		}
		
		public void run() {
			
			Object waiter = new Object();
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			try {
				
				Object result = httpClient.execute(httpTask.getHttpRequest(),httpTask.getResponseHandler());
				
				httpTask.sendFinishMessage(result,waiter);
	
			}
			catch (Exception e) {
				httpTask.sendErrorMessage(e,waiter);
				Log.d(TAG, Log.getStackTraceString(e));
			}
			
		}
		
	}
}
