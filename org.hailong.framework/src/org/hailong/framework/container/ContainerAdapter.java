package org.hailong.framework.container;

import org.hailong.framework.datasource.DataSource;
import org.hailong.framework.views.ViewLayout;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ContainerAdapter extends BaseAdapter implements
		IContainerAdapter {

	public Object dataItem;
	private ViewLayout _itemViewLayout;
	
	private DataSource _dataSource;

	public DataSource getDataSource(){
		return _dataSource;
	}
	
	public void setDataSource(DataSource dataSource){
		_dataSource = dataSource;
		notifyDataSetChanged();
	}
	
	public ViewLayout getItemViewLayout(){
		return _itemViewLayout;
	}
	
	public void setItemViewLayout(ViewLayout viewLayout){
		_itemViewLayout = viewLayout;
	}
	
	public ViewLayout getItemViewLayout(int position){
		return _itemViewLayout;
	}
	
	public View getItemView(int position){
		
		ViewLayout viewLayout = getItemViewLayout(position);
		
		if(viewLayout != null){
			return viewLayout.getView();
		}
		
		return null;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			convertView = getItemView(position);
		}
		
		Container container = null;
		
		if(convertView instanceof IContainerView){
			
			IContainerView containerView = (IContainerView)convertView;
			
			container = containerView.getContainer();
			
		}
		else{
			container = new Container(convertView);
		}
		
		dataItem = getItem(position);
		
		container.setDataObject(this);
		
		return convertView;
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public String getReuseIdentifier(int position){
		return null;
	}

	public int getCount() {
		if(_dataSource != null){
			return _dataSource.size();
		}
		return 0;
	}

	public Object getItem(int position) {
		if(_dataSource != null){
			return _dataSource.getDataObject(position);
		}
		return null;
	}


}
