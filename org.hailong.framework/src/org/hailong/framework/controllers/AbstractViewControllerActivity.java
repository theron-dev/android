package org.hailong.framework.controllers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hailong.framework.AbstractActivity;
import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.JSON;
import org.hailong.framework.PLIST;
import org.hailong.framework.URL;
import org.hailong.framework.value.Value;
import org.w3c.dom.Document;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public abstract class AbstractViewControllerActivity<T extends IServiceContext>
		extends AbstractActivity<T> implements IViewControllerContext<T> {

	private List<IViewController<T>> _viewControllers;
	private IViewController<T> _rootViewController;
	private Map<String,Object> _values;
	private Object _result;
	private IResultCallback _resultCallback;
	private Object _config;
	private Map<String,ViewControllerClassDefine> _classDefines;
	private boolean _idleTimerDisabled;
	
	protected String getConfigFile(){
		return "config.json";
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Object config = getConfig();
		
		String url = Value.stringValueForKey(config, "url");
		
		if(url != null){
			
			URL u = new URL(url);
			
			_rootViewController = getViewController(u, "/");
			
			if(_rootViewController != null){
				
				_rootViewController.loadURL(u, "/", false);
				_rootViewController.getView();
				_rootViewController.viewWillAppear(false);
				setContentView(_rootViewController.getView());
				_rootViewController.viewDidAppear(false);
			}
			
		}
		
	}
	
	@Override
	protected void onServiceContextStart(){
		super.onServiceContextStart();
		
	}
	
	@Override
	protected void onServiceContextStop(){
		super.onServiceContextStop();

		lowMemory();
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		_viewControllers = null;
		_rootViewController = null;
		_values = null;
	}
	
	public IViewController<T> getRootViewController(){
		return _rootViewController;
	}
	
	protected void lowMemory(){
		
		if(_rootViewController != null){
			_rootViewController.onLowMemory();
		}
		
		if(_viewControllers != null){

			for(Object viewController : _viewControllers.toArray()){
				
				@SuppressWarnings("unchecked")
				
				IViewController<T> controller = (IViewController<T>)viewController;
			
				if(controller.isDisplaced()){
					_viewControllers.remove(controller);
				}
				
			}
		}
		
	}
	
	@Override
	public void onLowMemory(){
		super.onLowMemory();
		
		lowMemory();
	}
	
	public Object getValue(String key) {
		return _values != null && _values.containsKey(key) ? _values.get(key) : null;
	}

	public void setValue(String key, Object value) {
		
		if(_values == null){
			_values = new HashMap<String, Object>();
		}
		
		if(value == null){
			_values.remove(key);
		}
		else{
			_values.put(key, value);
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if(!_idleTimerDisabled && _rootViewController != null){
	    	if(! _rootViewController.getTopController().onKeyDown(keyCode, event)){
	    		return super.onKeyDown(keyCode, event);
	    	}
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void setResult(Object result){
		_result = result;
	}
	
	public Object getResult(){
		return _result;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(_rootViewController != null){
			_rootViewController.getTopController().onOrientationChanged(newConfig.orientation);
		}
	}


	public IViewController<T> getViewController(URL url, String basePath) {
	
		String alias = url.firstPathComponent(basePath);
		
		Object cfg = Value.objectValueForKey(Value.objectValueForKey(getConfig(),"ui"),alias);

		if(cfg != null){
		       
			boolean cached = Value.booleanValueForKey(cfg, "cached");
			
			if(cached && _viewControllers != null){
				
				for(IViewController<T> viewController : _viewControllers){
					if(alias.equals(viewController.getAlias()) && viewController.isDisplaced()){
						viewController.setBasePath(basePath);
						viewController.setURL(url);
						return viewController;
					}
				}
				
			}
		    
			String className = Value.stringValueForKey(cfg, "class");
			
			ViewControllerClassDefine define = getViewControllerClassDefine(className);
			
			if(define != null){
				
				String view = Value.stringValueForKey(cfg, "view");
				
				int viewLayout = 0;
				
				if(view != null){
					viewLayout = getViewLayout(view);
				}
				
				IViewController<T> viewController = null;
				
				try {
					viewController = define.newInstance(viewLayout);
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				} 
				
				if(viewController != null){
					
					viewController.setAlias(alias);
					viewController.setURL(url);
					viewController.setBasePath(basePath);
					viewController.setScheme(Value.stringValueForKey(cfg, "scheme"));
					viewController.setConfig(cfg);
					
					if(cached){
						if(_viewControllers == null){
							_viewControllers = new ArrayList<IViewController<T>>();
						}
						_viewControllers.add(viewController);
					}
					
					return viewController;
				}

			}
			else{
				Log.d(Framework.TAG, "not found viewController class" + className);
			}
		        
		}

		return null;
	}

	public void setResultCallback(IResultCallback callback) {
		_resultCallback = callback;
	}

	public boolean hasResultCallback() {
		return _resultCallback != null;
	}

	public boolean isIdleTimerDisabled(){
		return _idleTimerDisabled;
	}
	
	public void setIdleTimerDisabled(boolean idleTimerDisabled){
		_idleTimerDisabled = idleTimerDisabled;
	}
	
	public boolean dispatchTouchEvent(MotionEvent event){
		if(!_idleTimerDisabled){
			return super.dispatchTouchEvent(event);
		}
		return false;
	}
	
	public boolean dispatchKeyEvent(KeyEvent event){
		if(!_idleTimerDisabled){
			return super.dispatchKeyEvent(event);
		}
		return false;
	}

	public Object getConfig() {
		if(_config == null){

			String configFile = getConfigFile();
			
			if(configFile != null){
				
				if(configFile.endsWith(".json")){
					
					InputStream inputStream = this.getClass().getResourceAsStream(getConfigFile());
					
					if(inputStream != null){

						try {
							
							InputStreamReader reader = new InputStreamReader(inputStream,"utf-8");
							
							StringBuilder sb = new StringBuilder();
							
							char[] buf = new char[12400];
							
							int len;
							
							while((len = reader.read(buf)) >0){
								sb.append(buf,0,len);
							}
							
							_config = JSON.decodeString(sb.toString());
							
							reader.close();
							inputStream.close();
							
						} catch (Exception e) {
							Log.d(Framework.TAG, Log.getStackTraceString(e));
						}
					}
					
				}
				else if(configFile.startsWith(".xml")){

					InputStream inputStream = this.getClass().getResourceAsStream(getConfigFile());
					
					if(inputStream != null){

						try {
							
							DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
							Document document  = documentBuilder.parse(inputStream);
							inputStream.close();
							
							_config = PLIST.parseXmlObject(document.getDocumentElement());

							
						} catch (Exception e) {
							Log.d(Framework.TAG, Log.getStackTraceString(e));
						}
					}
				}
				
			}
		}
		
		return _config;
	}

	private ViewControllerClassDefine getViewControllerClassDefine(String className){
		
		ViewControllerClassDefine define = null;
		
		if(_classDefines != null){
			define = _classDefines.get(className);
		}
		
		if(define == null){
			try {
				define = new ViewControllerClassDefine(className);
			} catch (Exception e) {
				Log.d(Framework.TAG,Log.getStackTraceString(e));
			}
		}
		
		if(define != null){
			if(_classDefines == null){
				_classDefines = new HashMap<String,ViewControllerClassDefine>();
			}
			_classDefines.put(className, define);
		}
		
		return define;
	}
	
	private int getViewLayout(String viewLayout){
		
		int layout = 0;
		int index = viewLayout.lastIndexOf(".");
		
		if(index >0){
		
			try {
				Class<?> c = Class.forName(viewLayout.substring(0,index));
				Field field = c.getField(viewLayout.substring(index +1));
				layout = field.getInt(null);
			} catch (Exception e) {
				
				index = viewLayout.lastIndexOf(".layout");
			
				try {
					Class<?> c = Class.forName(viewLayout.substring(0,index));
					
					for(Class<?> sub : c.getClasses()){
						if("layout".equals(sub.getSimpleName())){
							Field field = sub.getField(viewLayout.substring(viewLayout.lastIndexOf(".") +1));
							layout = field.getInt(null);
							break;
						}
					}
				} catch (Exception ex) {
					Log.d(Framework.TAG, Log.getStackTraceString(ex));
				}

			}
		}
		return layout;
	}

	private class ViewControllerClassDefine {
		
		private Class<?> clazz;
		private Constructor<IViewController<T>> constructor;
		
		@SuppressWarnings("unchecked")
		public ViewControllerClassDefine(String className) throws ClassNotFoundException, NoSuchMethodException{
			
			this.clazz = Class.forName(className);
			
			if(!IViewController.class.isAssignableFrom(clazz)){
				throw new ClassNotFoundException("not found viewController class " +className);
			}
			
			try {
				this.constructor = (Constructor<IViewController<T>>) clazz.getConstructor(IViewControllerContext.class,int.class);
			} catch (NoSuchMethodException e) {
				this.constructor = (Constructor<IViewController<T>>) clazz.getConstructor(IViewControllerContext.class);
			}

		}
		
		public IViewController<T> newInstance(int layoutView) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			return this.constructor.newInstance(AbstractViewControllerActivity.this,layoutView);
		}
	}
}
