package org.hailong.framework.container;

import org.hailong.framework.IServiceContext;
import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.datasource.DataSource;

public class ContainerDataContainer <T extends IServiceContext> extends DataContainer<T>{

	private ContainerView _containerView;
	
	public ContainerDataContainer(IViewControllerContext<T> context) {
		super(context);

	}
	
	public ContainerView getContainerView(){
		return _containerView;
	}
	
	public void setContainerView(ContainerView containerView){
		_containerView = containerView;
	}
	
	public void onDataSourceDidLoadedFromCached(DataSource dataSource,
			long timestamp) {
		
		reloadContainerView();
		
		super.onDataSourceDidLoadedFromCached(dataSource, timestamp);
	}
	
	public void reloadContainerView(){
		
		if(_containerView != null){
			IContainerAdapter adapter = _containerView.getAdapter();
			if(adapter != null){
				
				adapter.setDataSource(getDataSource());

			}
		}
		
	}
	
	public void onDataSourceDidLoaded(DataSource dataSource) {
		
		reloadContainerView();
		
		super.onDataSourceDidLoaded(dataSource);
	}

	public void onDataSourceDidContentChanged(DataSource dataSource) {
		
		reloadContainerView();
		
		super.onDataSourceDidContentChanged(dataSource);
	}

}
