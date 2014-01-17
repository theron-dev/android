package org.hailong.framework.datasource;

import java.util.ArrayList;
import java.util.List;

import org.hailong.framework.IServiceContext;
import org.hailong.framework.tasks.IDownlinkTask;
import org.hailong.framework.value.Value;

public class DataSource implements IDownlinkTask{

	private boolean _loading;
	private boolean _loaded;
	private String _dataKey;
	private List<Object> _dataObjects;
	private boolean _cached = false;
	protected Listener _listener;
	private IServiceContext _context;
	
	public DataSource(IServiceContext context){
		_context = context;
	}
	
	public IServiceContext getContext(){
		return _context;
	}
	
	public boolean isLoading(){
		return _loading;
	}
	
	public boolean isLoaded(){
		return _loaded;
	}
	
	public boolean isEmpty(){
		return _dataObjects == null || _dataObjects.size() ==0;
	}
	
	public String dataKey(){
		return _dataKey;
	}
	
	public void setDataKey(String dataKey){
		_dataKey = dataKey;
	}
	
	public Object getDataObject(){
		if(_dataObjects != null && _dataObjects.size() > 0){
			return _dataObjects.get(0);
		}
		return null;
	}
	
	public Object getDataObject(int index){
		
		if(_dataObjects != null && index >=0 && index < _dataObjects.size()){
			return _dataObjects.get(index);
		}
		
		return null;
	}
	
	public List<Object> getDataObjects(){
		if(_dataObjects == null){
			_dataObjects = new ArrayList<Object>(4);
		}
		return _dataObjects;
	}
	
	public int size(){
		return _dataObjects != null ? _dataObjects.size() : 0;
	}
	
	public void loadResultsData(Object resultsData){
		if(resultsData != null){
			
			List<Object> dataObjects = getDataObjects();
			
			List<?> list = Value.listValue(resultsData);
			
			if(list != null){
				dataObjects.addAll(list);
			}
			else{
				dataObjects.add(resultsData);
			}
			
		}
	}
	
	public void reloadData(){
		
		if(_listener != null){
			_listener.onDataSourceWillLoading(this);
		}
		
	}
	
	public void reloadDataContent(){
		
		if(_listener != null){
			_listener.onDataSourceWillLoading(this);
		}
		
	}
	
	public void cancel(){
		
	}
	
	public static interface Listener {
		
		public void onDataSourceWillLoading(DataSource dataSource);
		
		public void onDataSourceDidLoadedFromCached(DataSource dataSource,long timestamp);
		
		public void onDataSourceDidLoaded(DataSource dataSource);
		
		public void onDataSourceDidContentChanged(DataSource dataSource);
		
		public void onDataSourceDidException(DataSource dataSource,Exception exception);
	}

	public boolean isCached() {
		return _cached;
	}
	
	public void setCached(boolean cached){
		_cached = cached;
	}
	
	public Listener getListener(){
		return _listener;
	}
	
	public void setListener(Listener listener){
		_listener = listener;
	}

	public void onDidLoadedFromCached(Class<?> taskType, Object resultsData,
			long timestamp) {
		
		loadResultsData(resultsData);
		
		if(_listener != null){
			_listener.onDataSourceDidLoadedFromCached(this, timestamp);
		}
	}

	public void onDidLoaded(Class<?> taskType, Object resultsData) {
		
		getDataObjects().clear();
		
		loadResultsData(resultsData);
		
		if(_listener != null){
			_listener.onDataSourceDidLoaded(this);
		}
		
	}

	public void onDidException(Class<?> taskType, Exception exception) {
		if(_listener != null){
			_listener.onDataSourceDidException(this, exception);
		}
	}
}
