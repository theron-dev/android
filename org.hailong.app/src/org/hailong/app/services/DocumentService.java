package org.hailong.app.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.hailong.app.App;
import org.hailong.app.AppException;
import org.hailong.app.tasks.IDocumentTask;
import org.hailong.app.tasks.IDocumentTaskListener;
import org.hailong.core.MD5;
import org.hailong.core.URL;
import org.hailong.core.Value;
import org.hailong.dom.DOM;
import org.hailong.dom.DOMBundle;
import org.hailong.dom.DOMDocument;
import org.hailong.dom.parser.DOMParser;
import org.hailong.service.AbstractService;
import org.hailong.service.IServiceContext;
import org.hailong.service.ITask;
import org.hailong.service.task.impl.BaseHttpTask;
import org.hailong.service.tasks.IHttpResourceTask;
import android.os.Handler;
import android.util.Log;

public class DocumentService extends AbstractService {

	private List<DocumentHttpTask> _httpTasks;
	private ThreadPoolExecutor _poolExecutor;
	
	protected ThreadPoolExecutor getPoolExecutor(){
		
		if(_poolExecutor == null){
			
			int maxThreadCount = Value.intValueForKey(getConfig(), "maxThreadCount");
			
			if(maxThreadCount < 1){
				maxThreadCount = 1;
			}
			
			long keepAlive = Value.longValueForKey(getConfig(), "keepAlive");
			
			_poolExecutor = new ThreadPoolExecutor(0, maxThreadCount, keepAlive , TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
			
		}
		
		return _poolExecutor;
	}
	
	@Override
	public <T extends ITask> boolean handle(Class<T> taskType, T task,
			int priority) throws Exception {
		
		if(taskType == IDocumentTask.class){
			
			IDocumentTask documentTask = (IDocumentTask) task;
			
			DocumentLoadRunnable loadRunnable = null;
			
			if(documentTask.isAllowCached()){
				
				File file = getDocumentCacheFile(documentTask.getDocumentURL());
				
				if(file.exists()){
					
					loadRunnable = new DocumentLoadRunnable(documentTask,file);
					
					getPoolExecutor().execute(loadRunnable);
					
				}
			}
			
			if(! documentTask.isDisabledRemoteLoad()){
				
				HttpGet httpGet = new HttpGet(documentTask.getDocumentURL().toString());
				
				DocumentHttpTask httpTask = new DocumentHttpTask(httpGet, documentTask,loadRunnable);
				
				getContext().handle(IHttpResourceTask.class, httpTask, 0);
				
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	public <T extends ITask> boolean cancelHandle(Class<T> taskType, T task)
			throws Exception {
		
		if(taskType == IDocumentTask.class){
			
			ThreadPoolExecutor pool = getPoolExecutor();
			
			Object[] runnables = pool.getQueue().toArray();
			
			for(Object runnable : runnables){
				if(runnable instanceof DocumentLoadRunnable){
					DocumentLoadRunnable run = (DocumentLoadRunnable) runnable;
					if(task == null || run.getDocumentTask()== task){
						run.setCanceled(true);
						pool.remove(run);
					}
				}
			}
			
			if(_httpTasks != null && _httpTasks.size() > 0){
				
				IServiceContext ctx = getContext();
				List<DocumentHttpTask> httpTasks = new ArrayList<DocumentHttpTask>(4);
				
				for(DocumentHttpTask httpTask : _httpTasks){
					if(task == null || httpTask.getDocumentTask() == task){
						ctx.cancelHandle(IHttpResourceTask.class, httpTask);
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
		
		ThreadPoolExecutor pool = getPoolExecutor();
		
		Object[] runnables = pool.getQueue().toArray();
		
		for(Object runnable : runnables){
			if(runnable instanceof DocumentLoadRunnable){
				DocumentLoadRunnable run = (DocumentLoadRunnable) runnable;
				if(run.getDocumentTask().getSource() == source){
					run.setCanceled(true);
					pool.remove(run);
				}
			}
		}
		
		if(_httpTasks != null && _httpTasks.size() > 0){
			
			IServiceContext ctx = getContext();
			List<DocumentHttpTask> httpTasks = new ArrayList<DocumentHttpTask>(4);
			
			for(DocumentHttpTask httpTask : _httpTasks){
				if( httpTask.getDocumentTask().getSource() == source){
					ctx.cancelHandle(IHttpResourceTask.class, httpTask);
					httpTasks.add(httpTask);
				}
			}
			
			_httpTasks.removeAll(httpTasks);
		}
		
		return false;
	}
	
	public File getDocumentCacheFile(URL url){
		
		File dir = getContext().getDir("Documents", 0777);
		
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
			name = MD5.md5String(url.toString());
		} catch (Exception e) {
			name = url.getPath().replace("/", "_");
		} 
		
		int index = url.getPath().lastIndexOf(".");
		
		if(index >=0){
			name = name + url.getPath().substring(index);
		}
		
		return new File(dir,name);
	}
	
	public DOMDocument loadXMLContent(Reader reader,URL documentURL,DOMBundle bundle){
		
		DOMDocument document = new DOMDocument(bundle);

		document.setDocumentURL(documentURL);
		
		DOMParser parser = DOMParser.defaultParser();
		
		try {
			parser.parseHTML(reader, document);
		} catch (Exception e) {
			Log.d(App.TAG, Log.getStackTraceString(e));
		}
		
		return document;
	}
	
	private class DocumentLoadRunnable implements Runnable{

		private File _file;
		private IDocumentTask _documentTask;
		private Handler _handler;
		private boolean _canceled;
		private URL _documentURL;
		private DOMBundle _bundle;
		private DOMDocument _document;
		
		public DocumentLoadRunnable(IDocumentTask documentTask,File file){
			_documentTask = documentTask;
			_documentURL = documentTask.getDocumentURL();
			_bundle = documentTask.getBundle();
			_file = file;
			_handler = new Handler();
		}
		
		@Override
		public void run() {

			if(!_canceled){
				
				try {
					
					DocumentInputStream in = new DocumentInputStream(_file);
					
					InputStreamReader reader = new InputStreamReader(in, "UTF-8");
					
					_document = loadXMLContent(reader, _documentURL, _bundle);
					
					if(_document != null){
						_document.setSignature(in.getSignature());
					}
					
					reader.close();
					in.close();
					
				} catch (Exception e) {
					Log.e(App.TAG, Log.getStackTraceString(e));
				}
				
				if(_document != null){
					_handler.post(new Runnable(){

						@Override
						public void run() {
							
							IDocumentTaskListener listener = _documentTask.getListener();
							
							if(!_canceled && listener != null){
								listener.onDocumentTaskLoadedFromCached(_documentTask, _document);
							}
							
						}});
				}
				
			}
		}
		
		public IDocumentTask getDocumentTask(){
			return _documentTask;
		}
		
		public boolean isCanceled(){
			return _canceled;
		}
		
		public void setCanceled(boolean canceled){
			_canceled = canceled;
		}
	}
	
	private class DocumentInputStream extends FileInputStream {

		private  MessageDigest _md5;
		
		public DocumentInputStream(File file) throws FileNotFoundException, NoSuchAlgorithmException {
			super(file);
			_md5 = MessageDigest.getInstance("MD5");
		}

		@Override
		public int read(byte[] buffer,int offset,int count) throws IOException{
			int l = super.read(buffer, offset, count);
			if(l > 0){
				_md5.update(buffer, offset, l);
			}
			return l;
		}
		
		@Override
		public int read() throws IOException{
			int c = super.read();
			if(c != 0){
				_md5.update((byte)c);
			}
			return c;
		}
		
		public String getSignature(){
			
			byte[] digest = _md5.digest();
			 
			StringBuffer sb = new StringBuffer();
			 
			for(int i = 0; i < digest.length; i ++){
				String s = Integer.toHexString(digest[i]);
				if(s.length() == 1){
					sb.append('0');
				}
				else if(s.length() > 2){
					sb.append(s, 0, 2);
				}
				else{
					sb.append(s);
				}
			}
			 
			return sb.toString(); 
		}
	}

	private class DocumentHttpTask extends BaseHttpTask{

		private IDocumentTask _documentTask;
		private HttpResponse _httpResonse;
		private Exception _exception;
		private DOMDocument _document;
		private URL _documentURL;
		private DOMBundle _bundle;
		private boolean _allocCached;
		private DocumentLoadRunnable _loadRunnable;
		
		public DocumentHttpTask(HttpUriRequest httpRequest,IDocumentTask documentTask,DocumentLoadRunnable loadRunnable) {
			super(httpRequest);
			_documentTask = documentTask;
			_documentURL = documentTask.getDocumentURL();
			_bundle = documentTask.getBundle();
			_allocCached = documentTask.isAllowCached();
			_loadRunnable = loadRunnable;
		}

		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			_httpResonse = response;
			return super.handleResponse(response);
		}
		
		public IDocumentTask getDocumentTask(){
			return _documentTask;
		}

		@Override
		public void onBackgroundLoaded(String result){
			Header h = _httpResonse.getFirstHeader("VTDOMDocumentVersion");
			if(h != null && DOM.Version.equals(h.getValue())){
				
				_document = loadXMLContent(new StringReader(result),_documentURL,_bundle);
				
				if(_document != null){
					try {
						_document.setSignature(MD5.md5String(result));
					} catch (Exception e) {
						Log.e(App.TAG, Log.getStackTraceString(e));
					} 
				}
				
				if(_allocCached){
					
					File f = getDocumentCacheFile(_documentURL);
					
					try {
						
						OutputStream out = new FileOutputStream(f);
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
			else {
				_exception = new AppException("VTDOMDocumentVersion not is " + DOM.Version);
			}
		}
		
		@Override
		public void onLoaded(String result) {
		
			if(_loadRunnable != null && ! _loadRunnable.isCanceled()){
				_loadRunnable.setCanceled(true);
				getPoolExecutor().remove(_loadRunnable);
			}
			
			IDocumentTaskListener listener = _documentTask.getListener();
			
			if(_exception != null){
				if(listener != null){
					listener.onDocumentTaskException(_documentTask, _exception);
				}
			}
			else {
				if(listener != null){
					listener.onDocumentTaskLoaded(_documentTask, _document);
				}
			}
		}

		@Override
		public void onException(Exception ex) {

			IDocumentTaskListener listener = _documentTask.getListener();
			
			if(listener != null){
				listener.onDocumentTaskException(_documentTask, _exception);
			}
		}
		
	}
}
