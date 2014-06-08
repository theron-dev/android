package org.hailong.app.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hailong.app.R;
import org.hailong.controller.ViewLayout;
import org.hailong.core.Value;
import org.hailong.service.IServiceContext;
import org.hailong.service.tasks.IDownlinkTask;
import org.hailong.view.Container;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DataContainerAdapter extends BaseAdapter implements IDownlinkTask {

	private List<Object> _dataObjects; 
	private Object _source;
	private boolean _allowCached;
	private boolean _cached;
	private String _dataKey;
	private final ViewLayout _viewLayout;
	private LayoutInflater _layoutInflater;
	private boolean _loading;
	private boolean _loaded;
	
	public DataContainerAdapter(ViewLayout viewLayout){
		_viewLayout = viewLayout;
	}

	public ViewLayout getViewLayout(){
		return _viewLayout;
	}
	
	@Override
	public Object getSource() {
		return _source;
	}

	@Override
	public void setSource(Object source) {
		_source = source;
	}

	@Override
	public int getCount() {
		return _dataObjects == null ? 0 : _dataObjects.size();
	}

	@Override
	public Object getItem(int index) {
		return _dataObjects.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	public String getDataKey(){
		return _dataKey;
	}
	
	public void setDataKey(String dataKey){
		_dataKey = dataKey;
	}

	@Override
	public boolean isAllowCached() {
		return _allowCached;
	}
	
	public void setAllowCached(boolean allowCached){
		_allowCached = allowCached;
	}
	
	public boolean isCached(){
		return _cached;
	}
	
	public void setCached(boolean cached){
		_cached = cached;
	}
	
	protected void loadResultsData(Object resultsData){
		Object data = _dataKey == null ? resultsData : Value.objectValueForKeyPath(resultsData, _dataKey);
		if(data != null){
			
			if(_dataObjects == null){
				_dataObjects = new ArrayList<Object>(4);
			}
			
			if(data instanceof Collection){
				_dataObjects.addAll((Collection<?>)data);
			}
			else {
				_dataObjects.add(data);
			}
		}
	}

	@Override
	public void onDidLoadedFromCached(Class<?> taskType, Object resultsData,
			long timestamp) {
		
		setCached(true);
		
		loadResultsData(resultsData);
		
		notifyDataSetChanged();
		
	}

	@Override
	public void onDidLoaded(Class<?> taskType, Object resultsData) {

		setLoading(false);
		setLoaded(true);
		setCached(false);
		
		loadResultsData(resultsData);
		
		notifyDataSetChanged();
	}

	@Override
	public void onDidException(Class<?> taskType, Exception exception) {
		setLoading(false);
	}

	public ViewLayout getViewLayout(int index){
		return getViewLayout();
	}
	
	@Override
	public View getView(int index, View view, ViewGroup parent) {
		
		if(view == null){
			
			ViewLayout viewLayout = getViewLayout(index);
			
			if(viewLayout != null){
				
				if(_layoutInflater == null){
					_layoutInflater = LayoutInflater.from(parent.getContext());
				}
				
				view = viewLayout.getView(_layoutInflater, parent);
				
				Container container = new Container(view);
				
				view.setTag(R.id.container, container);
			}
		}
		
		Container container = (Container) view.getTag(R.id.container);
		
		container.setDataObject(getItem(index));
		
		return view;
	}

	public void reloadData(IServiceContext context){
		
	}
	
	public void cancel(IServiceContext context){
		
	}
	
	public boolean isLoading(){
		return _loading;
	}
	
	public void setLoading(boolean loading){
		_loading = loading;
	}
	
	public boolean isLoaded(){
		return _loaded;
	}
	
	public void setLoaded(boolean loaded){
		_loaded = loaded;
	}
}
