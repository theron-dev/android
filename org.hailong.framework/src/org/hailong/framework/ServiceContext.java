package org.hailong.framework;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ServiceContext extends Service implements IServiceContext {
	
	private List<ServiceContainer<?>> _serviceContainers;
	
	public <T extends ITask> boolean handle(Class<T> taskType, T task,
			int priority)  throws Exception{
		for(ServiceContainer<?> serviceContainer : _serviceContainers){
			if(serviceContainer.hasTaskType(taskType)){
				if(false == serviceContainer.handle(taskType, task, priority)){
					return false;
				}
			}
		}
		return true;
	}

	public <T extends ITask> boolean cancelHandle(Class<T> taskType, T task)  throws Exception{
		for(ServiceContainer<?> serviceContainer : _serviceContainers){
			if(serviceContainer.hasTaskType(taskType)){
				if(false == serviceContainer.cancelHandle(taskType, task)){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder() ;
	}
	
	protected String getConfigFile(){
		return "config.xml";
	}
	
	@Override
	public void onCreate(){
		super.onCreate();

		_serviceContainers = new ArrayList<ServiceContainer<?>>();
		
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
	}

	@Override
	public void onDestroy(){
		for(ServiceContainer<?> container : _serviceContainers){
			container.destroy();
		}
		_serviceContainers = null;
		super.onDestroy();
	}
	
	public <T extends IService> IServiceContainer registerService(Class<T> serviceClass){
		ServiceContainer<T> serviceContainer = new ServiceContainer<T>(serviceClass,this);
		
		_serviceContainers.add(serviceContainer);
		
		return serviceContainer;
	}
	
	
	@SuppressWarnings("unchecked")
	private void config(Document configDocument){
		NodeList nodes =  configDocument.getElementsByTagName("service");
		int length = nodes.getLength();
		for(int i=0;i<length;i++){
			Node node = nodes.item(i);
			String className = PLIST.getNodeAttribute(node,"class");
			Class<?> clazz = null;
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(clazz != null && IService.class.isAssignableFrom(clazz)){
				IServiceContainer container = registerService((Class<IService>)clazz);
				NodeList tasks = ((Element) node).getElementsByTagName("task");
				int taskCount = tasks.getLength();
				for(int j=0;j<taskCount;j++){
					Node task = tasks.item(j);
					String taskTypeName = PLIST.getNodeContentString(task);
					Class<?> taskType = null;
					try {
						taskType = Class.forName(taskTypeName);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					if(taskType !=null && ITask.class.isAssignableFrom(taskType)){
						container.addTaskType((Class<ITask>)taskType);
					}
				}
				String allowDealloc = PLIST.getNodeAttribute(node,"allocDealloc");
				if("false".equals(allowDealloc)){
					container.setAllowDeallocInstance(false);
				}
				String createInstance = PLIST.getNodeAttribute(node,"createInstance");
				
				if("true".equals(createInstance)){
					try {
						container.createInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
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
			}
		}
	}
	
	
	@Override
	public void onLowMemory(){
		for(ServiceContainer<?> container : _serviceContainers){
			container.didReceiveMemoryWarning();
		}
	}
	
	class LocalBinder extends Binder{
		public ServiceContext getService()  {  
            return ServiceContext.this;  
        } 
	}
	
	private static class ServiceContainer<T extends IService> implements IServiceContainer{

		private Class<T> _serviceClass;
		private Set<Class<?>> _taskTypes;
		private IService _instance;
		private boolean _allowDeallocInstance;
		private IServiceContext _context;
		private Object _config;
		
		public ServiceContainer(Class<T> serviceClass,IServiceContext context){
			_serviceClass = serviceClass;
			_taskTypes = new HashSet<Class<?>>();
			_allowDeallocInstance = true;
			_context = context;
		}
		
		public <TTask extends ITask> void addTaskType(Class<TTask> taskType) {
			_taskTypes.add(taskType);
		}

		public <TTask extends ITask> void removeTaskType(Class<TTask> taskType) {
			_taskTypes.remove(taskType);
		}

		public boolean isInstance() {
			return _instance != null;
		}

		public boolean isAllowDeallocInstance() {
			return _allowDeallocInstance;
		}

		public void setAllowDeallocInstance(boolean allow) {
			_allowDeallocInstance = allow;
		}

		public void createInstance() throws Exception {
			if(_instance == null){
				_instance = _serviceClass.newInstance();
				_instance.setContext(_context);
				if(_config != null){
					_instance.setConfig(_config);
				}
			}
		}
		
		public <TTask extends ITask> boolean hasTaskType(Class<TTask> taskType){
			return _taskTypes.contains(taskType);
		}

		public <TTask extends ITask> boolean handle(
				Class<TTask> taskType, TTask task, int priority) throws Exception {
			if(_instance == null){
				_instance = _serviceClass.newInstance();
				_instance.setContext(_context);
				if(_config != null){
					_instance.setConfig(_config);
				}
			}
			return _instance.handle(taskType, task, priority);
		}

		public <TTask extends ITask> boolean cancelHandle(
				Class<TTask> taskType, TTask task) throws Exception {
			if(_instance != null){
				return _instance.cancelHandle(taskType, task);
			}
			return true;
		}

		public void didReceiveMemoryWarning() {
			if(_instance != null){
				_instance.didReceiveMemoryWarning();
		        if(_allowDeallocInstance && _instance.getServiceState() == ServiceState.None){
		        	_instance.destroy();
		        	_instance = null;
		        }
			}
		}

		public void setConfig(Object config){
			_config = config;
		}
		
		public void destroy(){
			if(_instance != null){
		        _instance.destroy();
		        _instance = null;
			}
		}
	}
}
