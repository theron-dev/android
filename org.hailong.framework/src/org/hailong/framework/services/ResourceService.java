package org.hailong.framework.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.hailong.framework.AbstractService;
import org.hailong.framework.ITask;
import org.hailong.framework.tasks.IHttpResourceTask;
import org.hailong.framework.tasks.ILocalResourceTask;
import org.hailong.framework.tasks.IResourceTask;
import org.hailong.framework.tasks.impl.FileHttpRequestTask;
import org.hailong.framework.value.Value;

import android.util.Log;

public class ResourceService extends AbstractService{

	private final static String TAG = "ResourceService";
	
	private MessageDigest _md5;
	private Map<String,Object> _resourceCache;
	private Map<String,HttpTask> _httpTasks;
	
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
	
	private String uriToUrl(String uri){
		if(uri!= null && uri.length() >0){
			if(!uri.startsWith("http://")){
				
				int index = uri.indexOf("://");
				
				if(index >=0){
					
					String schema = uri.substring(0,index);
					
					String baseUri = Value.stringValueForKey(getConfig(), schema);
					
					if(baseUri != null){
						return baseUri + uri.substring(index + 3);
					}
					
					
				}
				
			}
		}
		return uri;
	}

	public <T extends ITask> boolean handle(Class<T> taskType, T task,
			int priority) throws Exception {

		if(ILocalResourceTask.class.isAssignableFrom(taskType)){
			
			ILocalResourceTask resTask = (ILocalResourceTask) task;
			
			String uri = resTask.getResourceUri();
			
			if(uri!= null && uri.length() >0 ){

				uri = uriToUrl(uri);
				
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
			
		}
		
		if(taskType == IResourceTask.class){
			
			IResourceTask resTask = (IResourceTask) task;
			
			if(resTask.isNeedDownload() || resTask.isForceDownload()){
				
				String uri = resTask.getResourceUri();
				
				if(uri!= null && uri.length() >0 ){
	
					uri = uriToUrl(uri);
					
					String key = urlToMd5(uri);
					
					HttpTask httpTask = null;
		
					if(_httpTasks != null){
						httpTask = _httpTasks.get(key);
					}
					
					if(httpTask == null){
					
						File dir = getContext().getDir("resources", 0x0777);
						
						if(!dir.exists()){
							dir.mkdirs();
						}
						
						File file = new File(dir, key + ".tmp");
						
						httpTask = new HttpTask(new HttpGet(uri),file);
						
						httpTask.tasks = new ArrayList<IResourceTask>();
						httpTask.key = key;
						
						httpTask.tasks.add(resTask);
						
						if(_httpTasks == null){
							_httpTasks = new HashMap<String,HttpTask>();
						}
						
						_httpTasks.put(key, httpTask);
						
						getContext().handle(IHttpResourceTask.class, httpTask, 0);
					}
					else{
						httpTask.tasks.add(resTask);
					}
				}
				else{
					resTask.onException(new URISyntaxException(uri, ""));
				}
			}
			
			return false;
		}
		
		return true;
	}

	public <T extends ITask> boolean cancelHandle(Class<T> taskType, T task)
			throws Exception {

		if(taskType == IResourceTask.class){
			
			if(_httpTasks != null){
				if(task == null){
					
				}
				else {
					
					IResourceTask resTask = (IResourceTask) task;
						
					String uri = resTask.getResourceUri();
						
					if(uri!= null && uri.length() >0 ){
			
						uri = uriToUrl(uri);
						
						String key = urlToMd5(uri);
						
						HttpTask httpTask = _httpTasks.get(key);
						
						if(httpTask != null){
							
							httpTask.tasks.remove(task);
							
							if(httpTask.tasks.size() == 0){
								getContext().cancelHandle(IHttpResourceTask.class, httpTask);
							}
							
						}
						
					}
				}
			}
			return false;
		}
		
		return true;
	}

	@Override
	public void didReceiveMemoryWarning(){
		_resourceCache = null;
	}
	
	class HttpTask extends FileHttpRequestTask{

		public HttpTask(HttpUriRequest httpRequest, File file) {
			super(httpRequest, file);
		}

		public String key;
		public ArrayList<IResourceTask> tasks;
		
		
		@Override
		public void onFinish(File result){
			
			File dir = getContext().getDir("resources", 0x0777);
			
			if(!dir.exists()){
				dir.mkdirs();
			}
			
			File toFile = new File(dir, key );
	

			try {
				
				FileInputStream in = new FileInputStream(result);
				FileOutputStream out = new FileOutputStream(toFile);
				
				byte[] buffer = new byte[102400];
				int len;
				
				while((len = in.read(buffer) ) >0){
					out.write(buffer, 0, len);
				}
				
				in.close();
				out.close();
				
			} catch (Exception e) {
				onException(e);
				return;
			}
			
			Object v = null;
			
			if(_resourceCache != null){
				v = _resourceCache.get(key);
			}
			
			for(IResourceTask task : tasks){
				
				if(task.isForceDownload() || v == null){
					
					v = task.setResourceLocalFile(toFile);
					
					if(v != null){
						
						if(_resourceCache == null){
							_resourceCache = new HashMap<String,Object>();
						}
						
						_resourceCache.put(key, v);
					}
				}
				else {
					task.setResourceObject(v);
				}
			}
			
			if(_httpTasks != null){
				_httpTasks.remove(key);
			}
			
			super.onFinish(result);
		}
		
		@Override
		public void onException(Exception ex){
			
			this.getFile().delete();
			
			for(IResourceTask task : tasks){
				task.onException(ex);
			}
			
			if(_httpTasks != null){
				_httpTasks.remove(key);
			}
			
			super.onException(ex);
		}
	}
}
