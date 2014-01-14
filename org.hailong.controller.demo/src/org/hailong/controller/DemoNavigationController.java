package org.hailong.controller;

import org.hailong.controller.demo.R;
import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.controllers.NavigationController;

import android.widget.TextView;

public class DemoNavigationController extends NavigationController<DemoContext> {

	private TextView _titleView;
	
	public DemoNavigationController(
			IViewControllerContext<DemoContext> activity, int viewLayout) {
		super(activity, viewLayout);
		
	}

	@Override
	protected void didViewLoaded(){
		super.didViewLoaded();
		
		_titleView = (TextView) getView().findViewById(R.id.titleTextView);
		
		if(_titleView !=null){
			_titleView.setText(getTopViewController().getTitle());
		}
	}
	
	@Override
	protected void didViewUnLoaded(){
		_titleView = null;
	}
	
	@Override
	protected void onTopControllerChanged(){
		super.onTopControllerChanged();
		
		if(_titleView !=null){
			_titleView.setText(getTopViewController().getTitle());
		}
	}
}
