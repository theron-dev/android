package org.hailong.controller;

import org.hailong.controller.demo.R;
import org.hailong.framework.Edge;
import org.hailong.framework.container.ContainerColumnAdapter;
import org.hailong.framework.container.ContainerDataContainer;
import org.hailong.framework.container.ContainerView;
import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.datasource.URLDataSource;
import org.hailong.framework.views.ViewLayout;

public class DemoContainerViewController extends DemoBaseController {

	private ContainerDataContainer<DemoContext> _dataContainer;
	
	public DemoContainerViewController(
			IViewControllerContext<DemoContext> context, String viewLayout) {
		super(context, viewLayout);

	}
	
	@Override
	protected void didViewLoaded(){
		super.didViewLoaded();
		
		ContainerView containerView = (ContainerView) getView().findViewById(R.id.containerLayoutView);
		
		_dataContainer = new ContainerDataContainer<DemoContext>(getViewControllerContext());
		
		_dataContainer.setContainerView(containerView);
		
		
		
	}
	
	@Override
	protected void didViewUnLoaded(){
		
		if(_dataContainer != null){
			_dataContainer.cancel();
			_dataContainer = null;
		}
		
		super.didViewUnLoaded();
	}
	
	@Override
	protected void didViewLoadedContextStart(){
		super.didViewLoadedContextStart();

		if(_dataContainer != null){
		
			ContainerView containerView = _dataContainer.getContainerView();
			
			if(containerView != null && containerView.getAdapter() == null){
				
				ContainerColumnAdapter adpater = new ContainerColumnAdapter(getServiceContext()) {
					
					@Override
					public Edge getItemMargin(int position) {
						return new Edge();
					}
					
					@Override
					public int getItemHeight(int position, int width) {
						return 160;
					}
				};
				
				adpater.setItemViewLayout(new ViewLayout(getContext(),R.layout.item));

				containerView.setAdapter(adpater);
			}
			
			if(_dataContainer.getDataSource() == null){
				
				URLDataSource dataSource = new URLDataSource(getServiceContext());
				
				dataSource.setUrl("https://api.douban.com/v2/book/search?q=b");
				dataSource.setDataKey("books");
				
				_dataContainer.setDataSource(dataSource);
				
				_dataContainer.reloadData();
				
			}
			
		}
		
		
	}
	

}
