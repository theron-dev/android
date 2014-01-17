package org.hailong.controller;

import org.hailong.framework.container.Container;
import org.hailong.framework.controllers.HeapController;
import org.hailong.framework.controllers.IViewControllerContext;

public class DemoNavigationController extends HeapController<DemoContext> {

	private Container _container;

	public DemoNavigationController(
			IViewControllerContext<DemoContext> activity, String viewLayout) {
		super(activity, viewLayout);
		
	}
	
	@Override
	protected void didViewLoaded(){
		super.didViewLoaded();
		
		_container = new Container(getView());

		_container.setDataObject(this);
		
	}

	@Override
	protected void onTopControllerChanged(){
		super.onTopControllerChanged();
		
		if(_container != null){
			_container.setDataObject(this);
		}

	}
}
