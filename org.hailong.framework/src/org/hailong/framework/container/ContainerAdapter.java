package org.hailong.framework.container;

import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.datasource.DataSource;
import org.hailong.framework.tasks.IImageTask;
import org.hailong.framework.tasks.ILocalResourceTask;
import org.hailong.framework.views.ViewLayout;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ContainerAdapter extends BaseAdapter implements
		IContainerAdapter {

	public Object dataItem;
	private ViewLayout _itemViewLayout;
	private IServiceContext _context;
	private DataSource _dataSource;

	public ContainerAdapter(IServiceContext context){
		_context = context;
	}
	
	public IServiceContext getServiceContext(){
		return _context;
	}
	
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
		else{
			cancelDownloadImagesForView(convertView);
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
		
		downloadImagesForView(convertView);
		
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


	public void downloadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isNeedDownload() && !imageTask.isLoading()){
				
				try {
					getServiceContext().handle(IImageTask.class, imageTask, 0);
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
				
			}
		}
		
		if(view instanceof ViewGroup){
			
			ViewGroup viewGroup = (ViewGroup) view;
			
			int c = viewGroup.getChildCount();
			
			for(int i=0;i<c;i++){
				
				downloadImagesForView(viewGroup.getChildAt(i));
				
			}
			
		}
		
	}

	public void loadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isNeedDownload() && !imageTask.isLoading()){
				
				try {
					
					getServiceContext().handle(ILocalResourceTask.class, imageTask, 0);
					
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
				
			}
		}

		if(view instanceof ViewGroup){
			
			ViewGroup viewGroup = (ViewGroup) view;
			
			int c = viewGroup.getChildCount();
			
			for(int i=0;i<c;i++){
				
				loadImagesForView(viewGroup.getChildAt(i));
				
			}
			
		}
	}

	public void cancelDownloadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isLoading()){
				
				try {
					getServiceContext().cancelHandle(IImageTask.class, imageTask);
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
				
			}
		}

		if(view instanceof ViewGroup){
			
			ViewGroup viewGroup = (ViewGroup) view;
			
			int c = viewGroup.getChildCount();
			
			for(int i=0;i<c;i++){
				
				cancelDownloadImagesForView(viewGroup.getChildAt(i));
				
			}
			
		}
	}

}
