package org.hailong.framework.controllers;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hailong.framework.AbstractActivity;
import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.PLIST;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;


public abstract class AbstractViewControllerActivity<T extends IServiceContext>
		extends AbstractActivity<T> implements IViewControllerContext<T> {

	private Map<String, IViewControllerContainer<T>> _containers;
	private ViewController<T> _rootViewController;
	private Map<String,Object> _values;
	private Object _result;
	
	private void config(Document configDocument){
		NodeList nodes =  configDocument.getElementsByTagName("controller");
		int length = nodes.getLength();
		for(int i=0;i<length;i++){
			Node node = nodes.item(i);
			String className = PLIST.getNodeAttribute(node,"class");
			String viewLayout = PLIST.getNodeAttribute(node,"view");
			String alias = PLIST.getNodeAttribute(node, "alias");
			if(alias != null ){
				IViewControllerContainer<T> container = new ViewControllerContainer<T>();
				if(className != null){
					container.setClass(className);
				}
				if(viewLayout != null){
					container.setLayout(viewLayout);
				}
				NodeList configs = ((Element) node).getElementsByTagName("config");
				if(configs.getLength() >0){
					Node config = configs.item(0);
					NodeList datas = config.getChildNodes();
				
					for(int j=0;j<datas.getLength();j++){
						config = datas.item(j);
						if(config.getNodeType() == Node.ELEMENT_NODE){
							break;
						}
					}
					container.setConfig(PLIST.parseXmlObject(config));
				}
				String token = PLIST.getNodeAttribute(node, "token");
				if(token != null){
					container.setToken(token);
				}
				String title = PLIST.getNodeAttribute(node, "title");
				if(title != null){
					container.setTitle(title);
				}
				_containers.put(alias, container);
			}
			
		}
	}
	
	protected String getConfigFile(){
		return "config.xml";
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		_values = new HashMap<String,Object>();
		_containers = new HashMap<String,IViewControllerContainer<T>>();
		InputStream inputStream = this.getClass().getResourceAsStream(getConfigFile());
		if(inputStream != null){
			try {
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document  = documentBuilder.parse(inputStream);
				inputStream.close();
				config(document);
			} catch (Exception e) {
				Log.d(Framework.TAG, Log.getStackTraceString(e));
			}
		}
		
		_rootViewController = getInstance("ROOT", this);
		
		if(_rootViewController != null){
			_rootViewController.getView();
			_rootViewController.viewWillAppear(false);
			setContentView(_rootViewController.getView());
			_rootViewController.viewDidAppear(false);
		}
	}
	
	@Override
	protected void onServiceContextStart(){
		super.onServiceContextStart();
		
		if(_containers != null){
			for(IViewControllerContainer<T> container : _containers.values()){
				container.onServiceContextStart();
			}
		}
	}
	
	@Override
	protected void onServiceContextStop(){
		super.onServiceContextStop();
		
		if(_containers != null){
			for(IViewControllerContainer<T> container : _containers.values()){
				container.onServiceContextStop();
			}
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		if(_containers != null){
			for(IViewControllerContainer<T> container : _containers.values()){
				container.destroy();
			}
		}
		
		_containers = null;
		_rootViewController = null;
		_values = null;
	}
	
	public ViewController<T> getRootViewController(){
		return _rootViewController;
	}
	
	@Override
	public void onLowMemory(){
		super.onLowMemory();
		
		if(_containers != null){
			for(IViewControllerContainer<T> container : _containers.values()){
				container.onLowMemory();
			}
		}
	}
	
	public ViewController<T> getInstance(String alias,
			IViewControllerContext<T> controllerContext) {
		if(_containers.containsKey(alias)){
			IViewControllerContainer<T> container = _containers.get(alias);
			return container.getInstance(controllerContext);
		}
		return null;
	}
	
	public ViewController<T> getInstance(String alias) {
		if(_containers.containsKey(alias)){
			IViewControllerContainer<T> container = _containers.get(alias);
			return container.getInstance(this);
		}
		return null;
	}

	public IViewControllerContext<T> getRootContext() {
		return this;
	}

	public IViewControllerContext<T> getParentContext() {
		return null;
	}

	public boolean openUrl(String uri, boolean animated) {
		return false;
	}

	public Object getValue(String key) {
		return _values.containsKey(key) ? _values.get(key) : null;
	}

	public void setValue(String key, Object value) {
		_values.put(key, value);
	}

	public String setValue(Object value) {
		String key = "@" + String.valueOf(value.hashCode()) + "." + String.valueOf(Math.random());
		setValue(key,value);
		return key;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if(_rootViewController != null){
	    	if(! _rootViewController.onKeyDown(keyCode, event)){
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
			_rootViewController.onOrientationChanged(newConfig.orientation);
		}
	}
	
	public ViewController<T> getFocusViewController(){
		if(_rootViewController != null){
			return _rootViewController.getFocusViewController();
		}
		return _rootViewController;
	}
	
	public void onFocusViewControllerChanged() {
		if(_rootViewController != null){
			int orientation = _rootViewController.getControllerOrientation();
			if(orientation != getRequestedOrientation()){
				setRequestedOrientation(orientation);
			}
		}
	}

	private static class ViewControllerContainer<T extends IServiceContext> implements IViewControllerContainer<T>{

		private int _viewLayout;
		private Class<?> _viewClass;
		private String _token;
		private Object _config;
		private String _title;
		private List<ViewController<T>> _instances;
		private Constructor<?> _constructor;
		
		public ViewControllerContainer(){
			_viewLayout = 0;
			_viewClass = ViewController.class;
			_instances =  new ArrayList<ViewController<T>>();
			try {
				_constructor = _viewClass.getConstructor(IViewControllerContext.class,int.class);
			} catch (Exception e) {
				Log.d(Framework.TAG, Log.getStackTraceString(e));
			} 
		}
		
		public void setLayout(String viewLayout) {
			_viewLayout = 0;
			int index = viewLayout.lastIndexOf(".");
			if(index >0){
				try {
					Class<?> c = Class.forName(viewLayout.substring(0,index));
					Field field = c.getField(viewLayout.substring(index +1));
					_viewLayout = field.getInt(null);
				} catch (Exception e) {
					
					index = viewLayout.lastIndexOf(".layout");
					try {
						Class<?> c = Class.forName(viewLayout.substring(0,index));
						
						for(Class<?> sub : c.getClasses()){
							if("layout".equals(sub.getSimpleName())){
								Field field = sub.getField(viewLayout.substring(viewLayout.lastIndexOf(".") +1));
								_viewLayout = field.getInt(null);
								break;
							}
						}
					} catch (Exception ex) {
						Log.d(Framework.TAG, Log.getStackTraceString(ex));
					}

				}
			}
		}

		public void setClass(String className) {
			try {
				_viewClass = Class.forName(className);
				if(!ViewController.class.isAssignableFrom(_viewClass)){
					_viewClass = ViewController.class;
				}
				try {
					_constructor = _viewClass.getConstructor(IViewControllerContext.class,int.class);
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				} 
			} catch (Exception e) {
				Log.d(Framework.TAG, Log.getStackTraceString(e));
			} 
		}

		public void setToken(String token) {
			_token = token;
		}

		public void setConfig(Object config) {
			_config = config;
		}

		public void setTitle(String title) {
			_title = title;
		}

		public ViewController<T> getInstance(IViewControllerContext<T> context) {
			
			for(ViewController<T> viewController : _instances){
				if(viewController.isDisplaced()){
					return viewController;
				}
			}
			
			try {
				@SuppressWarnings("unchecked")
				ViewController<T> viewController = (ViewController<T>)_constructor.newInstance(context,_viewLayout);
				if(viewController != null){
					if(_config != null){
						viewController.setConfig(_config);
					}
					if(_token != null){
						viewController.setToken(_token);
					}
					if(_title != null){
						viewController.setTitle(_title);
					}
					_instances.add(viewController);
				}
				
				return viewController;
			} catch (Exception e) {
				Log.d(Framework.TAG, Log.getStackTraceString(e));
			} 
			
			return null;
		}

		public void onLowMemory() {
			for(int i=0;i<_instances.size();i++){
				ViewController<T> viewController = _instances.get(i);
				viewController.onLowMemory();
				if(viewController.isDisplaced()){
					_instances.remove(i --);
				}
			}
		}

		public int getInstaceCount() {
			return _instances.size();
		}

		public void destroy() {
			if(_instances !=null){
				for(ViewController<T> viewController : _instances){
					viewController.destroy();
				}
			}
			_instances = null;
		}
		
		public void onServiceContextStart() {
			if(_instances !=null){
				for(ViewController<T> viewController : _instances){
					viewController.onServiceContextStart();
				}
			}
		}
		
		public void onServiceContextStop() {
			if(_instances !=null){
				for(ViewController<T> viewController : _instances){
					viewController.onServiceContextStop();
				}
			}
		}
	}

	
	
}
