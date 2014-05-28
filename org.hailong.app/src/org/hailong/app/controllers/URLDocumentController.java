package org.hailong.app.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import org.apache.http.client.methods.HttpGet;
import org.hailong.app.App;
import org.hailong.app.AppContext;
import org.hailong.app.R;
import org.hailong.controller.Controller;
import org.hailong.core.MD5;
import org.hailong.core.URL;
import org.hailong.core.Value;
import org.hailong.dom.DOMBundle;
import org.hailong.dom.DOMDocument;
import org.hailong.dom.DOMDocumentView;
import org.hailong.dom.DOMElement;
import org.hailong.dom.IDOMViewEntity;
import org.hailong.dom.parser.DOMParser;
import org.hailong.service.task.impl.BaseHttpTask;
import org.hailong.service.tasks.IHttpResourceTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class URLDocumentController<T extends AppContext> extends Controller<T>
	implements DOMDocumentView.OnElementVisableListener,DOMDocumentView.OnElementActionListener{

	private DOMDocumentView _documentView;
	private OnListener<T> _onListener;
	private boolean _allowLoadFromCached;
	private BaseHttpTask _httpTask;
	private DOMDocument _document;
	private DOMBundle _bundle;
	
	@Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  

        _documentView = (DOMDocumentView) getView().findViewById(R.id.documentView);
		_documentView.setOnElementVisableListener(this);
		_documentView.setOnElementActionListener(this);
		
		reloadData();
    } 
	
	public void loadXMLContent(String xmlContent){
		
		DOMBundle bundle = new DOMBundle(getActivity(),getClass());
		
		DOMDocument document = new DOMDocument(bundle);

		DOMParser parser = DOMParser.defaultParser();
		
		try {
			parser.parseHTML(new StringReader(xmlContent), document);
		} catch (Exception e) {
			Log.d(App.TAG, Log.getStackTraceString(e));
		}

		_documentView.setElement(document.getRootElement());
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
	
	public DOMDocument getDocument(){
		return _document;
	}
	
	public DOMBundle getBundle(){
		if(_bundle == null){
			_bundle = new DOMBundle(getActivity(), getClass());
		}
		return _bundle;
	}
	
	public void setBundle(DOMBundle bundle){
		_bundle = bundle;
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
	
	protected void loadDocument(URL documentURL,Reader reader){
		
		_document = new DOMDocument(getBundle());
		_document.setDocumentURL(documentURL);
		
		try {
			
			DOMParser.defaultParser().parseHTML(reader, _document);

		} catch (Exception e) {
			Log.e(App.TAG, Log.getStackTraceString(e));
		}
		
		_document.setStyleSheet(getServiceContext().getStyleSheet());
		
		_documentView.setElement(_document.getRootElement());
	
		downloadImagesForView((View) _documentView);
		
	}
	
	public void reloadData(){
		
		cancel();
		
		URL url = getDocumentURL();
		
		if(url != null){
			
			if(_document == null && _allowLoadFromCached){
				
				File file = getDocumentCacheFile(url);
				
				if(file.exists()){
					
					_document = new DOMDocument(getBundle());
					
					try {
						
						InputStream in = new FileInputStream(file);
						
						loadDocument(url,new InputStreamReader(in,"UTF-8"));
						
						in.close();
						
					} catch (Exception e) {
						Log.e(App.TAG, Log.getStackTraceString(e));
					}

					
				}
			}
			
			HttpGet http = new HttpGet(url.toString());
			
			_httpTask = new BaseHttpTask(http) {

				@Override
				public void onLoaded(String result) {
					
					URL url = getDocumentURL();
					
					loadDocument(url,new StringReader(result));
					
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
				}

				@Override
				public void onException(Exception ex) {
					if(_onListener != null){
						_onListener.onException(URLDocumentController.this, ex);
					}
				}};
	
			try {
				
				getServiceContext().handle(IHttpResourceTask.class, _httpTask, 0);
				
			} catch (Exception e) {
				Log.e(App.TAG, Log.getStackTraceString(e));
			}
			
		}
		
	}

	@Override
	public void onElementVisable(DOMDocumentView documentView,
			IDOMViewEntity viewEntity, DOMElement element) {
		
		if(viewEntity instanceof View){
			downloadImagesForView((View) viewEntity);
		}
	}

	@Override
	public void onElementAction(DOMDocumentView documentView,
			IDOMViewEntity viewEntity, DOMElement element) {
		if(_onListener != null){
			_onListener.onElementAction(this, documentView, viewEntity, element);
		}
	}
	
	public OnListener<T> getOnListener(){
		return _onListener;
	}
	
	public void setOnListener(OnListener<T> onListener){
		_onListener = onListener;
	}
	
	public boolean isAllowLoadFromCached(){
		return _allowLoadFromCached;
	}
	
	public void setAllowLoadFromCached(boolean allowLoadFromCached){
		_allowLoadFromCached = allowLoadFromCached;
	}
	
	public static interface OnListener<T extends AppContext> {
		
		public void onException(URLDocumentController<T> documentController,Exception ex);
		
		public void onLoaded(URLDocumentController<T> documentController);
		
		public void onLoadedFormCache(URLDocumentController<T> documentController);
		
		public void onElementAction(URLDocumentController<T> documentController,DOMDocumentView documentView,
			IDOMViewEntity viewEntity, DOMElement element);
		
	}
	
}
