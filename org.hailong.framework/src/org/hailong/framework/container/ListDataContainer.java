package org.hailong.framework.container;

import org.hailong.framework.IServiceContext;
import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.datasource.DataSource;
import org.hailong.framework.views.ViewLayout;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ListDataContainer <T extends IServiceContext> extends DataContainer<T> implements android.widget.ListAdapter{

	public Object dataItem;
	private ListView _listView;
	private ViewLayout _viewLayout;
	
	public ListDataContainer(IViewControllerContext<T> context) {
		super(context);

	}
	
	public ListView getListView(){
		return _listView;
	}
	
	public void setListView(ListView listView){
		_listView = listView;
	}
	
	
	public ViewLayout getViewLayout(){
		return _viewLayout;
	}
	
	public void setViewLayout(ViewLayout viewLayout){
		_viewLayout = viewLayout;
	}
	
	public ViewLayout getItemViewLayout(int position){
		return _viewLayout;
	}
	
	public View getItemView(int position){
		
		ViewLayout viewLayout = getItemViewLayout(position);
		
		if(viewLayout != null){
			return viewLayout.getView();
		}
		
		return null;
	}

	public int getCount() {
		DataSource dataSource = getDataSource();
		if(dataSource != null){
			return dataSource.size();
		}
		return 0;
	}

	public Object getItem(int position) {
		
		DataSource dataSource = getDataSource();
		if(dataSource != null){
			return dataSource.getDataObject(position);
		}
		
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			convertView = getItemView(position);
		}
		
		Container container = null;
		
		if(convertView instanceof ContainerView){
			
			ContainerView containerView = (ContainerView)convertView;
			
			container = containerView.getContainer();
			
		}
		else{
			container = new Container(convertView);
		}
		
		dataItem = getItem(position);
		container.setDataObject(this);
		
		return convertView;
	}

	public int getViewTypeCount() {
		return 0;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isEmpty() {
		
		DataSource dataSource = getDataSource();
		if(dataSource != null){
			return dataSource.isEmpty();
		}
		
		return true;
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		
	}

	public boolean areAllItemsEnabled() {
		return false;
	}

	public boolean isEnabled(int position) {
		return true;
	}


	
	public void onDataSourceDidLoadedFromCached(DataSource dataSource,
			long timestamp) {
		
		if(_listView != null){
			_listView.setAdapter(this);
		}
		
		
		super.onDataSourceDidLoadedFromCached(dataSource, timestamp);
	}

	public void onDataSourceDidLoaded(DataSource dataSource) {
		
		if(_listView != null){
			_listView.setAdapter(this);
		}
		
		
		super.onDataSourceDidLoaded(dataSource);
	}

	public void onDataSourceDidContentChanged(DataSource dataSource) {
		
		if(_listView != null){
			_listView.setAdapter(this);
		}
		
		super.onDataSourceDidContentChanged(dataSource);
	}

}
