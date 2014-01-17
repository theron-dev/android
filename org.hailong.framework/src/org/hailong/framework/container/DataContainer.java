package org.hailong.framework.container;

import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.datasource.DataSource;
import org.hailong.framework.tasks.IImageTask;
import org.hailong.framework.tasks.ILocalResourceTask;

import android.util.Log;
import android.view.View;

public class DataContainer <T extends IServiceContext> implements DataSource.Listener {

	private IViewControllerContext<T> _context;
	private DataSource _dataSource;
	protected Listener<T> _listener;
	
	public DataContainer(IViewControllerContext<T> context){
		_context = context;
	}
	
	public IViewControllerContext<T> getContext(){
		return _context;
	}
	
	public DataSource getDataSource(){
		return _dataSource;
	}
	
	public void setDataSource(DataSource dataSource){
		
		if(_dataSource != null){
			_dataSource.setListener(null);
		}
		
		_dataSource = dataSource;
		
		if(_dataSource != null){
			_dataSource.setListener(this);
		}
	}
	
	public Listener<T> getListener(){
		return _listener;
	}
	
	public void setListener(Listener<T> listener){
		_listener = listener;
	}
	
	public void reloadDataContent(){
		if(_dataSource != null){
			_dataSource.reloadDataContent();
		}
	}
	
	public void reloadData(){
		if(_dataSource != null){
			_dataSource.reloadData();
		}
	}
	
	public void cancel(){
		if(_dataSource != null){
			_dataSource.cancel();
		}
	}
	
	@Override
	protected void finalize() throws Throwable{
		if(_dataSource != null){
			_dataSource.setListener(this);
		}
		super.finalize();
	}

	public void onDataSourceWillLoading(DataSource dataSource) {
	
		if(_listener != null){
			_listener.onDataContainerWillLoading(this);
		}
	}

	public void onDataSourceDidLoadedFromCached(DataSource dataSource,
			long timestamp) {
		
		if(_listener != null){
			_listener.onDataContainerDidLoadedFromCached(this,timestamp);
		}
	}

	public void onDataSourceDidLoaded(DataSource dataSource) {
		
		if(_listener != null){
			_listener.onDataContainerDidLoaded(this);
		}
	}

	public void onDataSourceDidContentChanged(DataSource dataSource) {
		
		if(_listener != null){
			_listener.onDataContainerDidContentChanged(this);
		}
	}

	public void onDataSourceDidException(DataSource dataSource,
			Exception exception) {
		
		if(_listener != null){
			_listener.onDataContainerDidException(this,exception);
		}
	}
	
	public void downloadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isNeedDownload() && !imageTask.isLoading()){
				
				try {
					getContext().getServiceContext().handle(IImageTask.class, imageTask, 0);
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
				
			}
		}
		
	}

	public void loadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isNeedDownload() && !imageTask.isLoading()){
				
				try {
					
					getContext().getServiceContext().handle(ILocalResourceTask.class, imageTask, 0);
					
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
				
			}
		}

	}

	public void cancelDownloadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isLoading()){
				
				try {
					getContext().getServiceContext().cancelHandle(IImageTask.class, imageTask);
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
				
			}
		}

	}
	
	
	public static interface Listener<T extends IServiceContext> {
		
		public void onDataContainerWillLoading(DataContainer<T> dataContainer);
		public void onDataContainerDidLoadedFromCached(DataContainer<T> dataContainer,long timestamp);
		public void onDataContainerDidLoaded(DataContainer<T> dataContainer) ;
		public void onDataContainerDidContentChanged(DataContainer<T> dataContainer) ;
		public void onDataContainerDidException(DataContainer<T> dataContainer,Exception exception) ;
	}
	
}
