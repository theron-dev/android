package org.hailong.app.adapter;

import org.hailong.controller.ViewLayout;
import org.hailong.service.IServiceContext;
import org.hailong.service.tasks.IDownlinkPageTask;

public class DataContainerPageAdapter extends DataContainerAdapter implements IDownlinkPageTask {

	public DataContainerPageAdapter(ViewLayout viewLayout) {
		super(viewLayout);
	}

	private int _pageIndex=1;
	private int _pageSize=10;
	
	@Override
	public int getPageIndex() {
		return _pageIndex;
	}
	
	public void setPageIndex(int pageIndex){
		_pageIndex = pageIndex;
	}

	@Override
	public int getPageSize() {
		return _pageSize;
	}
	
	public void setPageSize(int pageSize){
		_pageSize = pageSize;
	}

	@Override
	public int getOffset() {
		return (_pageIndex - 1) * _pageSize;
	}

	public void loadMore(IServiceContext context){
		setPageIndex(getPageIndex() + 1);
	}
	
	public void reloadData(IServiceContext context){
		setPageIndex(1);
		super.reloadData(context);
	}
}
