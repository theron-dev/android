package org.hailong.controller;

import org.hailong.controller.demo.R;
import org.hailong.framework.controllers.HeapController;
import org.hailong.framework.controllers.IViewControllerContext;

import android.widget.TextView;

public class DemoNavigationController extends HeapController<DemoContext> {

	private TextView _titleView;
	
	public DemoNavigationController(
			IViewControllerContext<DemoContext> activity, int viewLayout) {
		super(activity, viewLayout);
		
	}

	@Override
	protected void didViewLoaded(){
		super.didViewLoaded();
		
		_titleView = (TextView) getView().findViewById(R.id.titleTextView);
	
	}
	
	@Override
	protected void didViewUnLoaded(){
		_titleView = null;
	}
	
	@Override
	protected void onTopControllerChanged(){
		super.onTopControllerChanged();
		
		if(_titleView !=null && getTopViewController() != null){
			_titleView.setText(getTopViewController().getTitle());
		}
	}
}
