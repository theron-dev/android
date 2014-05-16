package org.hailong.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.hailong.core.MD5;
import org.hailong.core.URL;
import org.hailong.core.Value;
import org.hailong.service.ITask;
import org.hailong.service.S;
import org.hailong.service.task.impl.JSONHttpTask;
import org.hailong.service.tasks.IDownlinkTask;
import org.hailong.service.tasks.IHttpAPITask;
import org.hailong.service.tasks.IURLDownlinkTask;

import android.annotation.SuppressLint;
import android.util.Log;

public class URLDownlinkService extends DownlinkService {

	private List<HttpTask> _httpTasks ;
	
	public <T extends ITask> boolean handle(Class<T> taskType, T task,
			int priority) throws Exception {

		if(taskType == IURLDownlinkTask.class){
			
			IURLDownlinkTask downTask = (IURLDownlinkTask) task;
			
			String url = downTask.getUrl();
			
			if(url == null){
				url = Value.stringValueForKey(getConfig(), downTask.getUrlKey());
			}
			
			if(url != null){
				
				url = url.replace("{offset}", String.valueOf( downTask.getOffset()));
				url = url.replace("{pageIndex}", String.valueOf(downTask.getPageIndex()));
				url = url.replace("{pageSize}", String.valueOf(downTask.getPageSize()));
				
				url = new URL(url,null,downTask.getQueryValues()).toString();
				
				Log.d(S.TAG, url);
				
				if(downTask.isCached() && downTask.getPageIndex() == 1){
					
					didLoadedFormCached(downTask, taskType);
					
				}
				
				HttpGet http = new HttpGet(url);

				http.setHeader("Accept-Encoding", "gzip, deflate");

				HttpTask httpTask = new HttpTask(http);
				
				httpTask.task = downTask;
				
				if(_httpTasks== null){
					_httpTasks = new ArrayList<HttpTask>(4);
				}
				
				_httpTasks.add(httpTask);
				
				getContext().handle(IHttpAPITask.class, httpTask, 0);
				
			}
		}
		 
		
		return false;
	}

	public <T extends ITask> boolean cancelHandle(Class<T> taskType, T task)
			throws Exception {

		if(taskType == IURLDownlinkTask.class){
			
			if(_httpTasks != null){
			
				if(task == null){
					
					for(HttpTask httpTask :_httpTasks){
						
						getContext().cancelHandle(IHttpAPITask.class, httpTask);
						
					}
					
					_httpTasks.clear();
				}
				else{
					
					List<HttpTask> httpTasks = new ArrayList<HttpTask>(2);
					
					for(HttpTask httpTask :_httpTasks){
						
						if(httpTask.task == task){
							getContext().cancelHandle(IHttpAPITask.class, httpTask);
							httpTasks.add(httpTask);
						}
					}

					_httpTasks.removeAll(httpTasks);
				}
			
			}
		}
		
		return false;
	}
	
	public String dataKey(IDownlinkTask downlinkTask,Class<?> taskType){
		
		if(taskType == IURLDownlinkTask.class){
			
			IURLDownlinkTask downTask = (IURLDownlinkTask) downlinkTask;
			
			String url = downTask.getUrl();
			
			if(url == null){
				url = Value.stringValueForKey(getConfig(), downTask.getUrlKey());
			}
			
			if(url != null){
				
				url = url.replace("{offset}", String.valueOf( downTask.getOffset()));
				url = url.replace("{pageIndex}", String.valueOf(downTask.getPageIndex()));
				url = url.replace("{pageSize}", String.valueOf(downTask.getPageSize()));
				
				url = new URL(url,null,downTask.getQueryValues()).toString();
				
				try {
					return MD5.md5String(url);
				} catch (NoSuchAlgorithmException e) {
				} catch (UnsupportedEncodingException e) {
				}
			}
		}
		
		return super.dataKey(downlinkTask, taskType);
	}
	
	@SuppressLint("HandlerLeak")
	class HttpTask extends JSONHttpTask{

		public IURLDownlinkTask task;
		
		public HttpTask(HttpUriRequest httpRequest) {
			super(httpRequest);

		}


		@Override
		public void onException(Exception ex) {
			
			didException(task, IURLDownlinkTask.class, ex);
			
			_httpTasks.remove(this);
		}


		@Override
		public void onLoadedObject(Object result) {

			didLoaded(task, IURLDownlinkTask.class, result, task.isCached() && task.getPageIndex() == 1);
			
			_httpTasks.remove(this);
		}
		
	}
}
