package org.hailong.controller;

import org.hailong.controller.demo.R;
import org.hailong.framework.container.ListDataContainer;
import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.datasource.URLDataSource;
import org.hailong.framework.views.ViewLayout;

import android.content.pm.ActivityInfo;
import android.widget.ListView;

public class DemoUserViewController extends DemoBaseController {

	private ListDataContainer<DemoContext> _dataContainer;
	
	public DemoUserViewController(IViewControllerContext<DemoContext> context,
			String viewLayout) {
		super(context, viewLayout);
		
	}

	@Override
	public void viewWillAppear(boolean animated){
		super.viewWillAppear(animated);
	}
	
	@Override
	public int getControllerOrientation(){
		return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
	}
	
	@Override
	protected void didViewLoaded(){
		super.didViewLoaded();
		
		ListView listView = (ListView) getView().findViewById(R.id.listView);
		
		_dataContainer = new ListDataContainer<DemoContext>(getViewControllerContext());
		_dataContainer.setListView(listView);
		_dataContainer.setItemViewLayout(new ViewLayout(getContext(),R.layout.item));

	}
	
	@Override
	protected void didViewLoadedContextStart(){
		super.didViewLoadedContextStart();
		
		if(_dataContainer != null && _dataContainer.getDataSource() == null){
			
			URLDataSource dataSource = new URLDataSource(getServiceContext());
			
			dataSource.setUrl("https://api.douban.com/v2/book/search?q=b");
			dataSource.setDataKey("books");
			
			_dataContainer.setDataSource(dataSource);
			
			_dataContainer.reloadData();
			
		}
		
	}
	
	@Override
	protected void didViewUnLoaded(){
		
		if(_dataContainer != null){
			_dataContainer.cancel();
			_dataContainer.setListener(null);
			_dataContainer = null;
		}
		
		super.didViewUnLoaded();
	}
}
