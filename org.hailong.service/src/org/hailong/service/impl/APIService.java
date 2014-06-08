package org.hailong.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.hailong.core.URL;
import org.hailong.core.Value;
import org.hailong.service.AbstractService;
import org.hailong.service.IServiceContext;
import org.hailong.service.ITask;
import org.hailong.service.S;
import org.hailong.service.task.impl.APIResponseTask;
import org.hailong.service.task.impl.JSONHttpTask;
import org.hailong.service.tasks.IAPICancelTask;
import org.hailong.service.tasks.IAPIRequestTask;
import org.hailong.service.tasks.IAPIResponseTask;
import org.hailong.service.tasks.IAPIWillRequestTask;
import org.hailong.service.tasks.IHttpAPITask;
import android.util.Log;

public class APIService extends AbstractService<IServiceContext> {

	private List<HttpTask> _httpTasks;
	
	@Override
	public <T extends ITask> boolean handle(Class<T> taskType, T task,
			int priority) throws Exception {
		
		if(taskType == IAPIRequestTask.class){
			
			IAPIRequestTask reqTask = (IAPIRequestTask) task;
			
			HttpTask httpTask = new HttpTask(null);
			
			httpTask.reqTask = reqTask;
			
			if(_httpTasks == null){
				_httpTasks = new ArrayList<HttpTask>(4);
			}
			
			_httpTasks.add(httpTask);
			
			getContext().handle(IHttpAPITask.class, httpTask, 0);
			
			return true;
		}
		
		return false;
	}

	@Override
	public <T extends ITask> boolean cancelHandle(Class<T> taskType, T task)
			throws Exception {
		
		if(taskType == IAPICancelTask.class){
			
			IAPICancelTask apiTask = (IAPICancelTask) task;
			
			if(_httpTasks != null){
				
				IServiceContext ctx = getContext();
				
				List<HttpTask> httpTasks = new ArrayList<HttpTask>(4);
				
				for(HttpTask httpTask : _httpTasks){
					
					IAPIRequestTask reqTask = httpTask.reqTask;
					
					if(reqTask.getTaskType() == apiTask.getTaskType() 
							&& (apiTask.getTask() == null || apiTask.getTask() == reqTask.getTask())){
						ctx.cancelHandle(IHttpAPITask.class, httpTask);
						httpTasks.add(httpTask);
					}
					
				}
				
				_httpTasks.removeAll(httpTasks);
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean cancelHandleForSource(Object source) throws Exception {
		
		if(_httpTasks != null){
			
			IServiceContext ctx = getContext();
			
			List<HttpTask> httpTasks = new ArrayList<HttpTask>(4);
			
			for(HttpTask httpTask : _httpTasks){
				
				IAPIRequestTask reqTask = httpTask.reqTask;
				
				if(reqTask.getSource() == source ){
					ctx.cancelHandle(IHttpAPITask.class, httpTask);
					httpTasks.add(httpTask);
				}
				
			}
			
			_httpTasks.removeAll(httpTasks);
		}
		
		return false;
	}

	private class HttpTask extends JSONHttpTask {

		public HttpTask(HttpUriRequest httpRequest) {
			super(httpRequest);
			// TODO Auto-generated constructor stub
		}

		public IAPIRequestTask reqTask;
		
		private int _statusCode;
		private Map<String,String> _headers;

		@Override
		public void onWillRequest(){
			
			try {
				
				IServiceContext ctx = APIService.this.getContext();
				
				ctx.handle(IAPIWillRequestTask.class, reqTask, 0);
				
			}
			catch(Exception ex){
				Log.e(S.TAG, Log.getStackTraceString(ex));
			}
			
			String url = reqTask.getApiUrl();
			
			if(url == null){
				url = Value.stringValueForKey(getConfig(), reqTask.getApiKey());
			}
			
			URL u = new URL(url,null,reqTask.getQueryValues());
			
			Log.d(S.TAG, u.toString());
			
			HttpUriRequest httpRequest;
			
			HttpEntity entity = reqTask.getEntity();
			
			if(entity == null){
				httpRequest = new HttpGet(u.toString());
			}
			else {
				HttpPost httpPost = new HttpPost(u.toString());
				httpPost.setEntity(entity);
				httpRequest = httpPost;
			}
			
			Map<String,String> headers = reqTask.getHeaders();
			
			if(headers != null){
				for(String key : headers.keySet()){
					httpRequest.addHeader(key, headers.get(key));
				}
			}
			
			setHttpRequest(httpRequest);
		}
		
		@Override
		public void onLoadedObject(Object result) {
			
			APIResponseTask respTask = new APIResponseTask();
			
			respTask.setTaskType( reqTask.getTaskType());
			respTask.setTask(reqTask.getTask());
			respTask.setObject(reqTask.getObject());
			respTask.setSource(reqTask.getSource());
			respTask.setResultsData(result);
			respTask.setStatusCode(_statusCode);
			respTask.setHeaders(_headers);
			
			try {
				
				IServiceContext ctx = APIService.this.getContext();
				
				ctx.handle(IAPIResponseTask.class, respTask, 0);
				
			}
			catch(Exception ex){
				Log.e(S.TAG, Log.getStackTraceString(ex));
			}
			
			_httpTasks.remove(this);
		}

		@Override
		public void onException(Exception ex) {

			APIResponseTask respTask = new APIResponseTask();
			
			respTask.setTaskType( reqTask.getTaskType());
			respTask.setTask(reqTask.getTask());
			respTask.setObject(reqTask.getObject());
			respTask.setSource(reqTask.getSource());
			respTask.setException(ex);
			respTask.setStatusCode(_statusCode);
			respTask.setHeaders(_headers);
			
			try {
				
				IServiceContext ctx = APIService.this.getContext();
				
				ctx.handle(IAPIResponseTask.class, respTask, 0);
				
			}
			catch(Exception e){
				Log.e(S.TAG, Log.getStackTraceString(e));
			}
			
			_httpTasks.remove(this);
		}
		
		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			_statusCode = response.getStatusLine().getStatusCode();
			_headers = new HashMap<String,String>(4);
			for(Header header : response.getAllHeaders()){
				_headers.put(header.getName(), header.getValue());
			}
			return super.handleResponse(response);
		}
	}
}
