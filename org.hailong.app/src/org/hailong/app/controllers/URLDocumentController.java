package org.hailong.app.controllers;

import java.io.File;
import org.hailong.app.App;
import org.hailong.app.AppContext;
import org.hailong.app.tasks.IDocumentTask;
import org.hailong.app.tasks.IDocumentTaskListener;
import org.hailong.app.tasks.impl.DocumentTask;
import org.hailong.core.MD5;
import org.hailong.core.URL;
import org.hailong.core.Value;
import org.hailong.dom.DOMDocument;
import org.hailong.dom.DOMDocumentView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class URLDocumentController<T extends AppContext> extends DocumentController<T>
	implements DOMDocumentView.OnElementVisableListener,DOMDocumentView.OnElementActionListener,IDocumentTaskListener{

	private OnURLDocumentControllerListener<T> _OnURLDocumentControllerListener;
	private boolean _allowLoadFromCached;
	private DocumentTask _documentTask;

	@Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  

		reloadData();
    } 

	public void cancel(){
		if(_documentTask != null){
			try {
				getServiceContext().cancelHandle(IDocumentTask.class, _documentTask);
			} catch (Exception e) {
				Log.e(App.TAG, Log.getStackTraceString(e));
			}
			_documentTask = null;
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
			return new URL(url,null,this.getURL().getQueryValues());
		}
		return null;
	}
	
	public void setConfig(Object config){
		super.setConfig(config);
		_allowLoadFromCached = Value.booleanValueForKey(config, "allowLoadFromCached");
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
			
			_documentTask = new DocumentTask();
			_documentTask.setSource(this);
			_documentTask.setListener(this);
			_documentTask.setDocumentURL(url);
			_documentTask.setBundle(getBundle());
			_documentTask.setAllowCached(_allowLoadFromCached);
			
			try {
				
				getServiceContext().handle(IDocumentTask.class, _documentTask, 0);
				
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

	@Override
	public void onDocumentTaskLoadedFromCached(IDocumentTask documentTask,
			DOMDocument document) {
		
		DOMDocument doc = getDocument();
		
		if(doc == null || !document.getSignature().equals( doc.getSignature())){
			
			document.setStyleSheet(getServiceContext().getStyleSheet());
			
			setDocument(document);
			
			DOMDocumentView documentView = getDocumentView();
			if(documentView != null){
				documentView.setElement(document.getRootElement());
				downloadImagesForView((View) documentView);
			}
		}
		
		if(_OnURLDocumentControllerListener != null){
			_OnURLDocumentControllerListener.onLoadedFormCache(this);
		}
	}

	@Override
	public void onDocumentTaskLoaded(IDocumentTask documentTask,
			DOMDocument document) {
	
		DOMDocument doc = getDocument();
		
		if(doc == null || !document.getSignature().equals( doc.getSignature())){
			
			document.setStyleSheet(getServiceContext().getStyleSheet());
			
			setDocument(document);
			
			DOMDocumentView documentView = getDocumentView();
			
			if(documentView != null){
				documentView.setElement(document.getRootElement());
				downloadImagesForView((View) documentView);
			}

		}
		
		if(_OnURLDocumentControllerListener != null){
			_OnURLDocumentControllerListener.onLoaded(this);
		}
		
	}

	@Override
	public void onDocumentTaskException(IDocumentTask documentTask,
			Exception exception) {
		
		if(_OnURLDocumentControllerListener != null){
			_OnURLDocumentControllerListener.onException(this, exception);
		}
		
	}
	
}
