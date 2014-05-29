package org.hailong.controller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hailong.core.JSON;
import org.hailong.core.PLIST;
import org.hailong.core.URL;
import org.hailong.core.Value;
import org.hailong.service.AbstractFragmentActivity;
import org.hailong.service.IServiceContext;
import org.w3c.dom.Document;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public abstract class AbstractControllerActivity<T extends IServiceContext> extends
	AbstractFragmentActivity<T> implements IControllerContext<T>{

	private Map<String,Object> _values;
	private Stack<IResultCallback> _resultsCallbacks;
	private Controller<T> _rootController;
	private Map<String,Class<?>> _controllerClasss;
	private Object _config;
	private boolean _idleTimerDisabled;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		if(getServiceContext() != null){
			loadRootController();
		}
		
	}
	
	protected void onServiceContextStart(){
		super.onServiceContextStart();
		
		if(_rootController == null){
			loadRootController();
		}
	}
	
	protected void loadRootController() {
		
		Object config = getConfig();
		
		String url = Value.stringValueForKey(config, "url");
		
		if(url != null){
			
			URL u = new URL(url);
			
			_rootController = getController(u, "/");
			
			if(_rootController != null){
				
				_rootController.loadURL(u, "/", false);
				
				getSupportFragmentManager().beginTransaction().replace(R.id.contentView, _rootController).commit();
				
			}
			
		}
		
	}

	
	@Override
	public void onDestroy(){
		
		_rootController = null;
		_values = null;
		
		super.onDestroy();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Controller<T> getController(URL url, String basePath) {
		
		String alias = url.firstPathComponent(basePath);
		
		Object cfg = Value.objectValueForKey(Value.objectValueForKey(getConfig(),"ui"),alias);

		if(cfg != null){
		   
			String className = Value.stringValueForKey(cfg, "class");
			
			Class<?> clazz = null;
			
			if(className != null){
				
				if(_controllerClasss != null){
					clazz = _controllerClasss.get(className);
				}
				
				if(clazz == null){
					try {
						clazz = Class.forName(className);
					} catch (ClassNotFoundException e) {
						Log.e(C.TAG, Log.getStackTraceString(e));
					}
				}
				
				if(clazz == null){
					Log.e(C.TAG, "not found controller class "+className);
				}
				else {
					if(_controllerClasss == null){
						_controllerClasss = new HashMap<String, Class<?>>(4);
					}
					_controllerClasss.put(className, clazz);
				}
			}
		
			if(clazz != null){
				
				Controller<T> controller= null;
				
				try {
					controller = (Controller<T>) clazz.newInstance();
				} catch (Exception e) {
					Log.e(C.TAG, Log.getStackTraceString(e));
				} 
				
				if(controller != null){
					
					controller.setControllerContext(this);
					
					String view = Value.stringValueForKey(cfg, "view");

					if(view != null){
						controller.setViewLayout(new ViewLayout(view));
					}
					
					controller.setAlias(alias);
					controller.setURL(url);
					controller.setBasePath(basePath);
					controller.setScheme(Value.stringValueForKey(cfg, "scheme"));
					controller.setConfig(cfg);

					return controller;
				}

			}
			else{
				Log.d(C.TAG, "not found viewController class" + className);
			}
		        
		}

		return null;
	}

	@Override
	public Object getValue(String key) {
		if(_values != null){
			return _values.get(key);
		}
		return null;
	}

	@Override
	public void setValue(String key, Object value) {
		if(value == null){
			if(_values != null){
				_values.remove(key);
			}
		}
		else {
			if(_values == null){
				_values = new HashMap<String, Object>(4);
			}
			_values.put(key, value);
		}
	}

	@Override
	public void setResult(Object result,Object sender) {
		if(_resultsCallbacks != null && ! _resultsCallbacks.isEmpty()){
			IResultCallback callback = _resultsCallbacks.pop();
			callback.onResult(result, sender);
		}
	}

	@Override
	public void setResultCallback(IResultCallback callback) {
		if(_resultsCallbacks == null){
			_resultsCallbacks = new Stack<IResultCallback>();
		}
		_resultsCallbacks.push(callback);
	}

	@Override
	public boolean hasResultCallback() {
		return _resultsCallbacks != null && ! _resultsCallbacks.isEmpty();
	}

	@Override
	public Controller<T> getRootController() {
		return _rootController;
	}

	
	protected String getConfigFile(){
		return "config.json";
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
							Log.d(C.TAG, Log.getStackTraceString(e));
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
							Log.d(C.TAG, Log.getStackTraceString(e));
						}
					}
				}
				
			}
		}
		
		return _config;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if(!_idleTimerDisabled ){
	    	if(_rootController != null &&  _rootController.getTopController().onKeyDown(keyCode, event)){
	    		return true;
	    	}
	    }
	    return false;
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


	public boolean isIdleTimerDisabled(){
		return _idleTimerDisabled;
	}
	
	public void setIdleTimerDisabled(boolean idleTimerDisabled){
		_idleTimerDisabled = idleTimerDisabled;
	}
}
