package org.hailong.framework.datasource;

import org.hailong.framework.IServiceContext;


public class PageDataSource extends DataSource{

	public PageDataSource(IServiceContext context) {
		super(context);

	}

	protected int _pageIndex = 1;
	protected int _pageSize = 10;
	protected boolean _hasMoreData = false;
	
	public boolean isHasMoreData(){
		return _hasMoreData;
	}
	
	public void setHasMoreData(boolean hasMoreData){
		_hasMoreData = hasMoreData;
	}
	
	public int getPageIndex(){
		return _pageIndex;
	}
	
	public void setPageIndex(int pageIndex){
		_pageIndex = pageIndex;
	}
	
	public int getPageSize(){
		return _pageSize;
	}
	
	public void setPageSize(int pageSize){
		_pageSize = pageSize;
	}
	
	public int getOffset(){
		return (_pageIndex - 1) * _pageSize;
	}
	
	public void reloadData(){
		_pageIndex = 1;
		super.reloadData();
	}
	
	public void loadMoreData(){
		_pageIndex ++;
		
		if(_listener != null){
			_listener.onDataSourceWillLoading(this);
		}
	}
	
	@Override
	public void onDidLoadedFromCached(Class<?> taskType, Object resultsData,
			long timestamp) {
		
		if(_pageIndex == 1){
		
			loadResultsData(resultsData);
			
			if(_listener != null){
				_listener.onDataSourceDidLoadedFromCached(this, timestamp);
			}
			
		}
	}

	@Override
	public void onDidLoaded(Class<?> taskType, Object resultsData) {
		
		if(_pageIndex == 1){
			getDataObjects().clear();
		}
		
		int count = size();
		
		loadResultsData(resultsData);
		
		if(size() - count == 0){
			_hasMoreData = false;
		}
		
		if(_listener != null){
			_listener.onDataSourceDidLoaded(this);
		}
		
	}
	
}
