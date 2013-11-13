package org.hailong.framework.services;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.hailong.framework.AbstractService;
import org.hailong.framework.Framework;
import org.hailong.framework.ITask;
import org.hailong.framework.tasks.IHttpRequestTask;
import org.hailong.framework.tasks.ILocalResourceTask;
import org.hailong.framework.tasks.IResourceTask;
import org.hailong.framework.tasks.impl.FileHttpRequestTask;
import android.util.Log;

public class ResourceService extends AbstractService{

	private final static String TAG = "ResourceService";
	
	private IHttpRequestTask<File> _requestTask;
	private MessageDigest _md5;
	private Map<String,Object> _resourceCache;

	public ResourceService(){
		try {
			_md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.d(TAG, Log.getStackTraceString(e));
		}
		_resourceCache = null;
	}
	
	private String urlToMd5(String url){
		_md5.reset();
		try {
			_md5.update(url.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Log.d(TAG, Log.getStackTraceString(e));
		}
		
		StringBuilder hexString = new StringBuilder();
        for (byte b : _md5.digest()) {
                hexString.append(Integer.toHexString(0xFF & b));
        }
		return hexString.toString();
	}
	
	private String uriToUrl(String uri,double size){
		if(uri!= null && uri.length() >0){
			if(!uri.startsWith("http://")){
				@SuppressWarnings("unchecked")
				Map<String,Object> config = (Map<String,Object>)getConfig();
				if(config != null){
					String baseUri = (String)config.get("url");
					if(baseUri != null){
						baseUri = baseUri.replace("{width}", String.valueOf((int)size));
						return baseUri.replace("{url}", URLEncoder.encode(uri));
					}
				}
			}
		}
		return uri;
	}

	private void setNeedRequest() throws Exception{
		if(_requestTask == null){
			IResourceTask task = taskQueue.beginTaskType(IResourceTask.class);
			if(task != null){
				String uri = task.getResourceUri();
				if(uri!= null && uri.length() >0 && task.isNeedDownload()){
					
					uri = uriToUrl(uri,task.getResourceSize());
					
					String md5 = urlToMd5(uri);
					
					if(!task.isForceDownload() && _resourceCache != null && _resourceCache.containsKey(md5)){
						task.setResourceObject(_resourceCache.get(md5));
						taskQueue.removeTask(IResourceTask.class, task);
						setNeedRequest();
						return ;
					}
					
					File dir = getContext().getDir("resources", 0x0777);
					if(!dir.exists()){
						dir.mkdirs();
					}
					File file = new File(dir,md5);
					if(file.exists() && !task.isForceDownload()){
						Object obj = task.setResourceLocalFile(file);
						if(obj != null){
							if(_resourceCache == null){
								_resourceCache = new HashMap<String,Object>(4);
							}
							_resourceCache.put(md5, obj);
						}
						taskQueue.removeTask(IResourceTask.class, task);
						setNeedRequest();
					}
					else{
						
						Log.d(Framework.TAG, uri);
						
						_requestTask = new FileHttpRequestTask(new HttpGet(uri), file){
							
							@Override
							public void onFinish(File result) {
								IResourceTask task = taskQueue.beginTaskType(IResourceTask.class);
								if(task != null){
	
									Object obj = task.setResourceLocalFile(result);
									
									if(obj != null){
										
										String uri = uriToUrl(httpRequest.getURI().toString(),task.getResourceSize());
										
										String md5 = urlToMd5(uri);
										
										if(_resourceCache == null){
											_resourceCache = new HashMap<String,Object>(4);
										}
										_resourceCache.put(md5, obj);
									}
									
									taskQueue.removeTask(IResourceTask.class, task);
								}
								_requestTask = null;
								try {
									setNeedRequest();
								} catch (Exception e) {
									Log.d(TAG, Log.getStackTraceString(e));
								}
								
							}
		
							@Override
							public void onError(Exception ex) {
								IResourceTask task = taskQueue.beginTaskType(IResourceTask.class);
								if(task != null){
									taskQueue.removeTask(IResourceTask.class, task);
								}
								_requestTask = null;
								try {
									setNeedRequest();
								} catch (Exception e) {
									Log.d(TAG, Log.getStackTraceString(e));
								}
							}
						};
						
						getContext().handle(IHttpRequestTask.class, _requestTask, 0);
					}
				}
				else{
					taskQueue.removeTask(IResourceTask.class, task);
					setNeedRequest();
				}
			}
		}
	}
	
	public <T extends ITask> boolean handle(Class<T> taskType, T task,
			int priority) throws Exception {

		if(taskType == ILocalResourceTask.class){
			
			ILocalResourceTask resTask = (ILocalResourceTask) task;
			
			String uri = resTask.getResourceUri();
			
			if(uri!= null && uri.length() >0 ){

				uri = uriToUrl(uri,resTask.getResourceSize());
				
				String md5 = urlToMd5(uri);
				
				if(_resourceCache != null && _resourceCache.containsKey(md5)){
					resTask.setResourceObject(_resourceCache.get(md5));
				}
				else{
					File dir = getContext().getDir("resources", 0x0777);
					if(!dir.exists()){
						dir.mkdirs();
					}
					File file = new File(dir,md5);
					if(file.exists()){
						Object obj = resTask.setResourceLocalFile(file);
						if(obj != null){
							if(_resourceCache == null){
								_resourceCache = new HashMap<String,Object>(4);
							}
							_resourceCache.put(md5, obj);
						}
					}
				}
			}
			
			return false;
		}
		else if(taskType == IResourceTask.class){
			
			taskQueue.addTask(taskType, task, priority);
			
			setNeedRequest();
			
			return false;
		}
		
		return true;
	}

	public <T extends ITask> boolean cancelHandle(Class<T> taskType, T task)
			throws Exception {

		if(taskType == IResourceTask.class){
			
			if(task == null || task == taskQueue.beginTaskType(taskType)){
				if(_requestTask !=null){
					getContext().cancelHandle(IHttpRequestTask.class, _requestTask);
					_requestTask = null;
				}
			}
			
			taskQueue.removeTask(taskType, task);
			
			setNeedRequest();
			
			return false;
		}
		
		return true;
	}

	@Override
	public void didReceiveMemoryWarning(){
		_resourceCache = null;
	}
}
