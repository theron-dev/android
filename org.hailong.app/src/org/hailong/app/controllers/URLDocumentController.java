package org.hailong.app.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.hailong.app.App;
import org.hailong.app.AppContext;
import org.hailong.core.MD5;
import org.hailong.core.URL;
import org.hailong.core.Value;
import org.hailong.dom.DOM;
import org.hailong.dom.DOMDocumentView;
import org.hailong.service.task.impl.BaseHttpTask;
import org.hailong.service.tasks.IHttpResourceTask;
import android.os.Bundle;
import android.util.Log;

public class URLDocumentController<T extends AppContext> extends DocumentController<T>
	implements DOMDocumentView.OnElementVisableListener,DOMDocumentView.OnElementActionListener{

	private OnURLDocumentControllerListener<T> _OnURLDocumentControllerListener;
	private boolean _allowLoadFromCached;
	private BaseHttpTask _httpTask;
	
	@Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  

		reloadData();
    } 

	public void cancel(){
		if(_httpTask != null){
			try {
				getServiceContext().cancelHandle(IHttpResourceTask.class, _httpTask);
			} catch (Exception e) {
				Log.e(App.TAG, Log.getStackTraceString(e));
			}
			_httpTask = null;
		}
	}
	
	@Override
	public void onStop(){
		cancel();
		super.onStop();
	}
	
	public URL getDocumentURL(){
		String url = Value.stringValueForKey(getConfig(), "url");
		if(url != null){
			return new URL(url);
		}
		return null;
	}
	
	
	public File getDocumentCacheFile(URL url){
		
		File dir = getActivity().getDir("Documents", 0777);
		
		StringBuilder path = new StringBuilder();
		
		if(url.getPort() ==0){
			path.append(url.getHost()).append(File.pathSeparator);
		}
		else {
			path.append(url.getHost()).append(":").append(url.getPort()).append(File.pathSeparator);
		}
		
		dir = new File(dir,path.toString());
		
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		String name;
		
		try {
			name = MD5.md5String(url.getPath());
		} catch (Exception e) {
			name = url.getPath().replace("/", "_");
		} 
		
		int index = url.getPath().lastIndexOf(".");
		
		if(index >=0){
			name = name + url.getPath().substring(index);
		}
		
		return new File(dir,name);
	}
	
	public void reloadData(){
		
		cancel();
		
		URL url = getDocumentURL();
		
		if(url != null){
			
			if(getDocument() == null && _allowLoadFromCached){
				
				File file = getDocumentCacheFile(url);
				
				if(file.exists()){
					
					try {
						
						InputStream in = new FileInputStream(file);
						
						loadXMLContent(new InputStreamReader(in,"UTF-8"),url);
						
						in.close();
						
					} catch (Exception e) {
						Log.e(App.TAG, Log.getStackTraceString(e));
					}

					
				}
			}
			
			HttpGet http = new HttpGet(url.toString());
			
			_httpTask = new BaseHttpTask(http) {

				private HttpResponse _httpResonse;
					
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					_httpResonse = response;
					return super.handleResponse(response);
				}
				
				@Override
				public void onLoaded(String result) {
					
					Header h = null;
					
					if(_httpResonse != null 
							&& (h = _httpResonse.getLastHeader("VTDOMDocumentVersion")) != null
							&& DOM.Version.equals( h.getValue()) ){
					
						URL url = getDocumentURL();
						
						loadXMLContent(new StringReader(result),url);
						
						if(_allowLoadFromCached){
							
							File file = getDocumentCacheFile(url);
							
							try {
								
								OutputStream out = new FileOutputStream(file);
								
								OutputStreamWriter writer = new OutputStreamWriter(out,"UTF-8");
								
								writer.write(result);
								
								writer.flush();
								
								writer.close();
								
								out.close();
								
							} catch (Exception e) {
								Log.e(App.TAG, Log.getStackTraceString(e));
							}
						}
						
						if(_OnURLDocumentControllerListener != null){
							_OnURLDocumentControllerListener.onLoaded(URLDocumentController.this);
						}
						
					}
					else {
						onException(new Exception("Not Found VTDOMDocumentVersion: "+DOM.Version));
					}
				}

				@Override
				public void onException(Exception ex) {
					if(_OnURLDocumentControllerListener != null){
						_OnURLDocumentControllerListener.onException(URLDocumentController.this, ex);
					}
				}};
	
			try {
				
				getServiceContext().handle(IHttpResourceTask.class, _httpTask, 0);
				
			} catch (Exception e) {
				Log.e(App.TAG, Log.getStackTraceString(e));
			}
			
		}
		
	}

	
	public OnURLDocumentControllerListener<T> getOnURLDocumentControllerListener(){
		return _OnURLDocumentControllerListener;
	}
	
	public void setOnURLDocumentControllerListener(OnURLDocumentControllerListener<T> onURLDocumentControllerListener){
		_OnURLDocumentControllerListener = onURLDocumentControllerListener;
	}
	
	public boolean isAllowLoadFromCached(){
		return _allowLoadFromCached;
	}
	
	public void setAllowLoadFromCached(boolean allowLoadFromCached){
		_allowLoadFromCached = allowLoadFromCached;
	}
	
	public static interface OnURLDocumentControllerListener<T extends AppContext> {
		
		public void onException(URLDocumentController<T> documentController,Exception ex);
		
		public void onLoaded(URLDocumentController<T> documentController);
		
		public void onLoadedFormCache(URLDocumentController<T> documentController);

	}
	
}
