package org.hailong.framework.container;

import org.hailong.framework.IServiceContext;
import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.datasource.DataSource;
import org.hailong.framework.views.ViewLayout;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

public class ContainerDataContainer <T extends IServiceContext> extends DataContainer<T> implements android.widget.Adapter{

	public Object dataItem;
	private ContainerView _containerView;
	private ViewLayout _viewLayout;
	
	public ContainerDataContainer(IViewControllerContext<T> context) {
		super(context);

	}
	
	public ContainerView getContainerView(){
		return _containerView;
	}
	
	public void setContainerView(ContainerView containerView){
		_containerView = containerView;
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
		else{
			cancelDownloadImagesForView(convertView);
		}
		
		Container container = null;
		
		if(convertView instanceof ContainerItemView){
			
			ContainerItemView containerView = (ContainerItemView)convertView;
			
			container = containerView.getContainer();
			
		}
		else{
			container = new Container(convertView);
		}
		
		dataItem = getItem(position);
		container.setDataObject(this);
		
		downloadImagesForView(convertView);
		
		return convertView;
	}

	public int getViewTypeCount() {
		return 1;
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
		
		if(_containerView != null){
			_containerView.setAdapter(this);
		}
		
		
		super.onDataSourceDidLoadedFromCached(dataSource, timestamp);
	}

	public void onDataSourceDidLoaded(DataSource dataSource) {
		
		if(_containerView != null){
			_containerView.setAdapter(this);
		}
		
		
		super.onDataSourceDidLoaded(dataSource);
	}

	public void onDataSourceDidContentChanged(DataSource dataSource) {
		
		if(_containerView != null){
			_containerView.setAdapter(this);
		}
		
		super.onDataSourceDidContentChanged(dataSource);
	}

}
