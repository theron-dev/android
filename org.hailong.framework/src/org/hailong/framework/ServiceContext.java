package org.hailong.framework;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hailong.framework.value.Value;
import org.w3c.dom.Document;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ServiceContext extends Service implements IServiceContext {
	
	private Object _config;
	private List<ServiceContainer<?>> _serviceContainers;
	
	public <T extends ITask> boolean handle(Class<T> taskType, T task,
			int priority)  throws Exception{
		for(ServiceContainer<?> serviceContainer : _serviceContainers){
			if(serviceContainer.hasTaskType(taskType)){
				if(serviceContainer.handle(taskType, task, priority)){
					return true;
				}
			}
		}
		return false;
	}

	public <T extends ITask> boolean cancelHandle(Class<T> taskType, T task)  throws Exception{
		for(ServiceContainer<?> serviceContainer : _serviceContainers){
			if(serviceContainer.hasTaskType(taskType)){
				if(serviceContainer.cancelHandle(taskType, task)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder() ;
	}
	
	protected String getConfigFile(){
		return "config.json";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(){
		super.onCreate();

		if(_serviceContainers == null){
			
			_serviceContainers = new ArrayList<ServiceContainer<?>>();
			
			Object config = getConfig();
			
			List<?> services = Value.listValueForKey(config, "services");
			
			if(services != null){
				
				for(Object cfg : services){
					
					String className = Value.stringValueForKey(cfg, "class");
					
					Class<?> clazz = null;
					
					try {
						clazz = Class.forName(className);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(clazz != null && IService.class.isAssignableFrom(clazz)){
						
						IServiceContainer container = registerService((Class<IService>)clazz);
						
						List<?> taskTypes = Value.listValueForKey(cfg, "taskTypes");
						
						if(taskTypes != null){
							
							for(Object taskType : taskTypes){
								
								Class<?> taskTypeClass = null;
								
								try {
									taskTypeClass = Class.forName(taskType.toString());
								} catch (Exception e) {
									
								}
								
								if(taskTypeClass != null && ITask.class.isAssignableFrom(taskTypeClass)){
									container.addTaskType((Class<ITask>)taskTypeClass);
								}
								else{
									Log.d(Framework.TAG, "not found taskType "+taskType);
								}
							}
							
						}
						
						if(Value.booleanValueForKey(cfg, "allocDealloc")){
							container.setAllowDeallocInstance(true);
						}
						
						if(Value.booleanValueForKey(cfg, "createInstance")){
							try {
								container.createInstance();
							} catch (Exception e) {
								Log.d(Framework.TAG, Log.getStackTraceString(e));
							}
						}
					
						container.setConfig(cfg);
						
					}
					else{
						Log.d(Framework.TAG, "not found service class "+className);
					}
					
				}
				
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
}
